package com.tencent.advancedemo.render;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLException;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;

/**
 * 创建和管理EglSurface和EglContext
 */
public class EGLManager {
    public static final Object lock = new Object();
    public static final int EGL_OPENGL_ES2_BIT = 4;
    public static final int EGL_OPENGL_ES3_BIT = 0x40;
    // Android-specific extension.
    public static final int EGL_RECORDABLE_ANDROID = 0x3142;
    public static final int[] CONFIG_PLAIN = configBuilder().createConfigAttributes();
    public static final int[] CONFIG_RGBA =
            configBuilder().setHasAlphaChannel(true).createConfigAttributes();
    public static final int[] CONFIG_PIXEL_BUFFER =
            configBuilder().setSupportsPixelBuffer(true).createConfigAttributes();
    public static final int[] CONFIG_PIXEL_RGBA_BUFFER = configBuilder()
            .setHasAlphaChannel(true)
            .setSupportsPixelBuffer(true)
            .createConfigAttributes();
    public static final int[] CONFIG_RECORDABLE =
            configBuilder().setIsRecordable(true).createConfigAttributes();
    private static final String TAG = "EGLManager";
    private EGLContext eglContext;
    @Nullable private EGLConfig eglConfig;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    public EGLManager(@Nullable EGLContext sharedContext) {
        eglDisplay = getEglDisplay();
        eglConfig = getEglConfig(eglDisplay, EGLManager.CONFIG_PLAIN);
        int openGlesVersion = getOpenGlesVersionFromConfig(EGLManager.CONFIG_PLAIN);
        Log.d(TAG, "Using OpenGL ES version " + openGlesVersion);
        eglContext = createEglContext(sharedContext, eglDisplay, eglConfig, openGlesVersion);
    }

    public static ConfigBuilder configBuilder() {
        return new ConfigBuilder();
    }

    static int getOpenGlesVersionFromConfig(int[] configAttributes) {
        for (int i = 0; i < configAttributes.length - 1; ++i)
            if (configAttributes[i] == EGL10.EGL_RENDERABLE_TYPE) switch (configAttributes[i + 1]) {
                case EGL_OPENGL_ES2_BIT:
                    return 2;
                case EGL_OPENGL_ES3_BIT:
                    return 3;
                default:
                    return 1;
            }
        // Default to V1 if no renderable type is specified.
        return 1;
    }

    // Return an EGLDisplay, or die trying.
    private static EGLDisplay getEglDisplay() {
        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) throw new GLException(EGL14.eglGetError(),
                "Unable to get EGL14 display: 0x" + Integer.toHexString(EGL14.eglGetError()));
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1))
            throw new GLException(EGL14.eglGetError(),
                    "Unable to initialize EGL14: 0x" + Integer.toHexString(EGL14.eglGetError()));
        return eglDisplay;
    }

    // Return an EGLConfig, or die trying.
    private static EGLConfig getEglConfig(EGLDisplay eglDisplay, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(
                eglDisplay, configAttributes, 0, configs, 0, configs.length, numConfigs, 0))
            throw new GLException(EGL14.eglGetError(),
                    "eglChooseConfig failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
        if (numConfigs[0] <= 0)
            throw new RuntimeException("Unable to find any matching EGL config");
        EGLConfig eglConfig = configs[0];
        if (eglConfig == null) throw new RuntimeException("eglChooseConfig returned null");
        return eglConfig;
    }

    // Return an EGLConfig, or die trying.
    private static EGLContext createEglContext(@Nullable EGLContext sharedContext,
                                               EGLDisplay eglDisplay, EGLConfig eglConfig, int openGlesVersion) {
        if (sharedContext != null && sharedContext == EGL14.EGL_NO_CONTEXT)
            throw new RuntimeException("Invalid sharedContext");
        int[] contextAttributes = {EGL14.EGL_CONTEXT_CLIENT_VERSION, openGlesVersion, EGL14.EGL_NONE};
        EGLContext rootContext = sharedContext == null ? EGL14.EGL_NO_CONTEXT : sharedContext;
        EGLContext eglContext;
        synchronized (lock) {
            eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes, 0);
        }
        if (eglContext == EGL14.EGL_NO_CONTEXT) throw new GLException(EGL14.eglGetError(),
                "Failed to create EGL context: 0x" + Integer.toHexString(EGL14.eglGetError()));
        return eglContext;
    }

    // Create EGLSurface from the Android Surface.
    public void createSurface(Surface surface) {
        createSurfaceInternal(surface);
    }

    // Create EGLSurface from the Android SurfaceTexture.
    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture);
    }

    // Create EGLSurface from either Surface or SurfaceTexture.
    private void createSurfaceInternal(Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture))
            throw new IllegalStateException("Input must be either a Surface or SurfaceTexture");
        checkIsNotReleased();
        if (eglSurface != EGL14.EGL_NO_SURFACE)
            throw new RuntimeException("Already has an EGLSurface");
        int[] surfaceAttribs = {EGL14.EGL_NONE};
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) throw new GLException(EGL14.eglGetError(),
                "Failed to create window surface: 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (eglSurface != EGL14.EGL_NO_SURFACE)
            throw new RuntimeException("Already has an EGLSurface");
        int[] surfaceAttribs = {EGL14.EGL_WIDTH, width, EGL14.EGL_HEIGHT, height, EGL14.EGL_NONE};
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) throw new GLException(EGL14.eglGetError(),
                "Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x"
                        + Integer.toHexString(EGL14.eglGetError()));
    }

    public boolean hasSurface() {
        return eglSurface != EGL14.EGL_NO_SURFACE;
    }

    public int surfaceWidth() {
        int[] widthArray = new int[1];
        EGL14.eglQuerySurface(eglDisplay, eglSurface, EGL14.EGL_WIDTH, widthArray, 0);
        return widthArray[0];
    }

    public int surfaceHeight() {
        int[] heightArray = new int[1];
        EGL14.eglQuerySurface(eglDisplay, eglSurface, EGL14.EGL_HEIGHT, heightArray, 0);
        return heightArray[0];
    }

    public void releaseSurface() {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface);
            eglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    private void checkIsNotReleased() {
        if (eglDisplay == EGL14.EGL_NO_DISPLAY || eglContext == EGL14.EGL_NO_CONTEXT
                || eglConfig == null) throw new RuntimeException("This object has been released");
    }

    public void release() {
        checkIsNotReleased();
        releaseSurface();
        detachCurrent();
        synchronized (EGLManager.lock) {
            EGL14.eglDestroyContext(eglDisplay, eglContext);
        }
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(eglDisplay);
        eglContext = EGL14.EGL_NO_CONTEXT;
        eglDisplay = EGL14.EGL_NO_DISPLAY;
        eglConfig = null;
    }

    public void makeCurrent() {
        checkIsNotReleased();
        if (eglSurface == EGL14.EGL_NO_SURFACE)
            throw new RuntimeException("No EGLSurface - can't make current");
        synchronized (EGLManager.lock) {
            if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext))
                throw new GLException(EGL14.eglGetError(),
                        "eglMakeCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
    }

    // Detach the current EGL context, so that it can be made current on another thread.
    public void detachCurrent() {
        synchronized (EGLManager.lock) {
            if (!EGL14.eglMakeCurrent(
                    eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT))
                throw new GLException(EGL14.eglGetError(),
                        "eglDetachCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
    }

    public void swapBuffers() {
        checkIsNotReleased();
        if (eglSurface == EGL14.EGL_NO_SURFACE)
            throw new RuntimeException("No EGLSurface - can't swap buffers");
        synchronized (EGLManager.lock) {
            EGL14.eglSwapBuffers(eglDisplay, eglSurface);
        }
    }

    public static class ConfigBuilder {
        private int openGlesVersion = 2;
        private boolean hasAlphaChannel;
        private boolean supportsPixelBuffer;
        private boolean isRecordable;

        public ConfigBuilder setOpenGlesVersion(int version) {
            if (version < 1 || version > 3)
                throw new IllegalArgumentException("OpenGL ES version " + version + " not supported");
            openGlesVersion = version;
            return this;
        }

        public ConfigBuilder setHasAlphaChannel(boolean hasAlphaChannel) {
            this.hasAlphaChannel = hasAlphaChannel;
            return this;
        }

        public ConfigBuilder setSupportsPixelBuffer(boolean supportsPixelBuffer) {
            this.supportsPixelBuffer = supportsPixelBuffer;
            return this;
        }

        public ConfigBuilder setIsRecordable(boolean isRecordable) {
            this.isRecordable = isRecordable;
            return this;
        }

        public int[] createConfigAttributes() {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(EGL10.EGL_RED_SIZE);
            list.add(8);
            list.add(EGL10.EGL_GREEN_SIZE);
            list.add(8);
            list.add(EGL10.EGL_BLUE_SIZE);
            list.add(8);
            if (hasAlphaChannel) {
                list.add(EGL10.EGL_ALPHA_SIZE);
                list.add(8);
            }
            if (openGlesVersion == 2 || openGlesVersion == 3) {
                list.add(EGL10.EGL_RENDERABLE_TYPE);
                list.add(openGlesVersion == 3 ? EGL_OPENGL_ES3_BIT : EGL_OPENGL_ES2_BIT);
            }
            if (supportsPixelBuffer) {
                list.add(EGL10.EGL_SURFACE_TYPE);
                list.add(EGL10.EGL_PBUFFER_BIT);
            }
            if (isRecordable) {
                list.add(EGL_RECORDABLE_ANDROID);
                list.add(1);
            }
            list.add(EGL10.EGL_NONE);

            int[] res = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) res[i] = list.get(i);
            return res;
        }
    }
}
