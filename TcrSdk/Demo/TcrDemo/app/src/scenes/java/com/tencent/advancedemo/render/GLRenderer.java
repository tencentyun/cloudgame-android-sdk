package com.tencent.advancedemo.render;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.Nullable;

import com.tencent.tcr.sdk.api.VideoFrame;
import com.tencent.tcr.sdk.api.VideoSink;

import org.twebrtc.ThreadUtils;

import java.util.concurrent.CountDownLatch;

/**
 * VideoSink实现类，拿到视频帧后抛入渲染线程执行视频帧渲染
 */
public class GLRenderer implements VideoSink {
    protected final String name;
    private final VideoFrameDrawer frameDrawer;
    private final Matrix drawMatrix = new Matrix();
    // viewpoint x, y, w, h
    private final int[] viewPoints = {0, 0, 0, 0};
    private final Object handlerLock = new Object();
    private final Object frameLock = new Object();
    private final float[] glClearColorValue = {0, 0, 0, 0};
    private final EglSurfaceCreation mEglSurfaceCreationRunnable = new EglSurfaceCreation();
    @Nullable
    private EGLManager mEGLManager;
    @Nullable
    private GLDrawer drawer;
    @Nullable
    private Handler renderThreadHandler;
    @Nullable
    private VideoFrame pendingFrame;

    public GLRenderer(String name) {
        this(name, new VideoFrameDrawer());
    }

    public GLRenderer(String name, VideoFrameDrawer videoFrameDrawer) {
        this.name = name;
        frameDrawer = videoFrameDrawer;
    }

    @Override
    public void onFrame(VideoFrame videoFrame) {
        boolean dropOldFrame;
        synchronized (handlerLock) {
            if (renderThreadHandler == null) {
                return;
            }
            synchronized (frameLock) {
                dropOldFrame = (pendingFrame != null);
                if (dropOldFrame) {
                    pendingFrame.release();
                }
                pendingFrame = videoFrame;
                pendingFrame.retain();
                renderThreadHandler.post(GLRenderer.this::renderFrameOnRenderThread);
            }
        }
    }

    private void renderFrameOnRenderThread() {
        VideoFrame frame;
        synchronized (frameLock) {
            if (pendingFrame == null) {
                return;
            }
            frame = pendingFrame;
            pendingFrame = null;
        }
        if (mEGLManager == null || !mEGLManager.hasSurface()) {
            frame.release();
            return;
        }
        setupScaleAndViewpoint();
        try {
            GLES20.glClearColor(glClearColorValue[0] /* red */, glClearColorValue[1] /* green */, glClearColorValue[2] /* blue */, glClearColorValue[3] /* alpha */);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            frameDrawer.drawFrame(frame, drawer, drawMatrix,
                    viewPoints[0], viewPoints[1], viewPoints[2], viewPoints[3]);
            mEGLManager.swapBuffers();
        } catch (GlUtils.GlOutOfMemoryException e) {
            if (drawer !=null) {
                drawer.release();
            }
            frameDrawer.release();
        } finally {
            frame.release();
        }
    }

    private void setupScaleAndViewpoint() {
        viewPoints[0] = 0;
        viewPoints[1] = 0;
        viewPoints[2] = mEGLManager.surfaceWidth();
        viewPoints[3] = mEGLManager.surfaceHeight();

        drawMatrix.reset();
        drawMatrix.preTranslate(0.5f, 0.5f);
        drawMatrix.preScale(1f, 1f);
        drawMatrix.preTranslate(-0.5f, -0.5f);
    }

    /**
     * Initialize this class, sharing resources with `sharedContext`. The custom `drawer` will be used
     * for drawing frames on the EGLSurface. This class is responsible for calling release() on
     * `drawer`. It is allowed to call init() to reinitialize the renderer after a previous
     * init()/release() cycle. If usePresentationTimeStamp is true, eglPresentationTimeANDROID will be
     * set with the frame timestamps, which specifies desired presentation time and might be useful
     * for e.g. syncing audio and video.
     */
    public void init(@Nullable EGLContext context,
                     GLDrawer drawer) {
        synchronized (handlerLock) {
            if (renderThreadHandler != null) {
                throw new IllegalStateException(name + "Already initialized");
            }
            this.drawer = drawer;

            HandlerThread renderThread = new HandlerThread(name + "EglRenderer");
            renderThread.start();
            renderThreadHandler =
                    new HandlerWithExceptionCallback(renderThread.getLooper(), () -> {
                        synchronized (handlerLock) {
                            renderThreadHandler = null;
                        }
                    });
            ThreadUtils.invokeAtFrontUninterruptibly(renderThreadHandler, () -> {
                // If sharedContext is null, then texture frames are disabled. This is typically for old
                // devices that might not be fully spec compliant, so force EGL 1.0 since EGL 1.4 has
                // caused trouble on some weird devices.
                mEGLManager = new EGLManager(context);
            });
            renderThreadHandler.post(mEglSurfaceCreationRunnable);
        }
    }

    public void release() {
        CountDownLatch eglCleanupBarrier = new CountDownLatch(1);
        synchronized (handlerLock) {
            if (renderThreadHandler == null) {
                return;
            }
            // Release EGL and GL resources on render thread.
            renderThreadHandler.postAtFrontOfQueue(() -> {
                // Detach current shader program.
                synchronized (EGLManager.lock) {
                    GLES20.glUseProgram(/* program= */ 0);
                }

                if (drawer != null) {
                    drawer.release();
                    drawer = null;
                }
                frameDrawer.release();

                if (mEGLManager != null) {
                    mEGLManager.detachCurrent();
                    mEGLManager.release();
                    mEGLManager = null;
                }
                eglCleanupBarrier.countDown();
            });
            Looper renderLooper = renderThreadHandler.getLooper();
            renderThreadHandler.post(renderLooper::quit);
            // Don't accept any more frames or messages to the render thread.
            renderThreadHandler = null;
        }
        ThreadUtils.awaitUninterruptibly(eglCleanupBarrier);
        synchronized (frameLock) {
            if (pendingFrame != null) {
                pendingFrame.release();
                pendingFrame = null;
            }
        }
    }

    public void createEglSurface(Surface surface) {
        createEglSurfaceInternal(surface);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture);
    }

    private void createEglSurfaceInternal(Object surface) {
        mEglSurfaceCreationRunnable.setSurface(surface);
        postToRenderThread(mEglSurfaceCreationRunnable);
    }

    private void postToRenderThread(Runnable runnable) {
        synchronized (handlerLock) {
            if (renderThreadHandler != null) {
                renderThreadHandler.post(runnable);
            }
        }
    }

    private static class HandlerWithExceptionCallback extends Handler {
        private final Runnable exceptionCallback;

        public HandlerWithExceptionCallback(Looper looper, Runnable exceptionCallback) {
            super(looper);
            this.exceptionCallback = exceptionCallback;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                exceptionCallback.run();
                throw e;
            }
        }
    }

    private class EglSurfaceCreation implements Runnable {
        private Object surface;

        public synchronized void setSurface(Object surface) {
            this.surface = surface;
        }

        @Override
        public synchronized void run() {
            if (surface != null && mEGLManager != null && !mEGLManager.hasSurface()) {
                if (surface instanceof Surface) {
                    mEGLManager.createSurface((Surface) surface);
                } else if (surface instanceof SurfaceTexture) {
                    mEGLManager.createSurface((SurfaceTexture) surface);
                } else {
                    throw new IllegalStateException("Invalid surface: " + surface);
                }
                mEGLManager.makeCurrent();
                // Necessary for YUV frames with odd width.
                GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            }
        }
    }
}
