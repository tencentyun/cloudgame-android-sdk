package com.tencent.tcrdemo.customvideo;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.VideoFrame;
import com.tencent.tcr.sdk.api.VideoSink;
import com.tencent.tcr.sdk.api.utils.CustomVideoTextureHelper;
import com.tencent.tcrdemo.TcrDemoApplication;
import com.tencent.tcrdemo.customvideo.gles.ProgramTextureOES;
import com.tencent.tcrdemo.customvideo.gles.core.EglCore;
import java.io.File;
import org.twebrtc.GlUtil;
import org.twebrtc.ThreadUtils;

/**
 * 此类演示了如何自定义上行视频（纹理帧）。
 * <p>
 * 它首先使用 MediaPlayer 播放视频文件到 SurfaceTexture，以获取自定义视频纹理帧，作为数据源。</br>
 * 然后将源纹理帧渲染到 CustomVideoTextureHelper 工具的 getSurfaceTexture() 上。</br>
 * CustomVideoTextureHelper 内部在捕获到新纹理帧后，会将其封装成 VideoFrame 对象，并回调给构造函数传入的 VideoSink 对象。</br>
 * 将获取到 VideoFrame 对象通过 TcrSession.sendCustomVideo() 接口发送给 TCR SDK 进行编码上行。</br>
 */
public class CustomVideoTexture implements VideoSink {

    private static final String TAG = "CustomVideoTexture";
    private final TcrSession mSession;  // 会话对象
    private final boolean DUMP_FRAME = false; // 调试用帧捕获开关
    private final FrameDumper frameDumper = new FrameDumper(); // 帧捕获工具
    private final float[] mMVPMatrix = new float[16]; // OpenGL模型视图投影矩阵
    private Handler mHandler;           // EGL线程处理器
    private MediaPlayer mediaPlayer;    // 媒体播放器实例
    private int videoWidth, videoHeight; // 视频原始宽高
    private EglCore mEglCore;           // EGL核心管理对象
    private EGLSurface mDummySurface;   // 离屏渲染表面
    private int mPreviewTexture = -1;   // 预览纹理ID
    private SurfaceTexture mPreviewSurfaceTexture; // 预览纹理表面
    private Surface mPreviewSurface;    // 供MediaPlayer输出的Surface
    private CustomVideoTextureHelper mCustomVideoTextureHelper; // 纹理帧辅助工具
    private EGLSurface mDrawSurface;    // 目标渲染表面
    private ProgramTextureOES mDrawProgram; // OpenGL渲染程序

    /**
     * 构造函数：初始化视频纹理帧处理管道
     *
     * @param session 会话对象，用于发送视频帧
     */
    public CustomVideoTexture(TcrSession session) {
        this.mSession = session;

        // 从Assets复制视频文件到应用存储目录
        File videoFile = AssetsFileCopier.copyAssetToExternalFilesDir(TcrDemoApplication.getContext(),
                "VideoCapture.mp4");

        // 创建专用的EGL渲染线程
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mHandler = new Handler(thread.getLooper());

        // 在EGL线程中初始化OpenGL环境
        ThreadUtils.invokeAtFrontUninterruptibly(mHandler, () -> {
            // 创建EGL上下文和显示
            mEglCore = new EglCore();
            // 创建1x1像素的离屏表面（用于保持EGL上下文）
            mDummySurface = mEglCore.createOffscreenSurface(1, 1);
            mEglCore.makeCurrent(mDummySurface);

            // 创建OES外部纹理（供MediaPlayer输出）
            mPreviewTexture = GlUtil.generateTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
            // 将纹理绑定到SurfaceTexture
            mPreviewSurfaceTexture = new SurfaceTexture(mPreviewTexture);
            // 设置帧可用监听器（当新帧到来时触发）
            mPreviewSurfaceTexture.setOnFrameAvailableListener(st -> onFrameAvailable(), mHandler);

            // 创建纹理帧辅助工具（将当前对象作为VideoSink回调）
            mCustomVideoTextureHelper = new CustomVideoTextureHelper(this);
            // 从辅助工具获取目标SurfaceTexture并创建EGL渲染表面
            mDrawSurface = mEglCore.createWindowSurface(new Surface(mCustomVideoTextureHelper.getSurfaceTexture()));
            // 初始化OES纹理渲染程序
            mDrawProgram = new ProgramTextureOES();
            // 初始化单位矩阵
            Matrix.setIdentityM(mMVPMatrix, 0);
        });

        // 初始化媒体播放器
        try {
            // 创建Surface供MediaPlayer渲染
            mPreviewSurface = new Surface(mPreviewSurfaceTexture);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(videoFile.getAbsolutePath());
            // 将播放器输出绑定到OpenGL纹理表面
            mediaPlayer.setSurface(mPreviewSurface);
            mediaPlayer.setLooping(true);  // 设置循环播放

            // 准备完成回调
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "MediaPlayer prepared, starting playback");

                // 获取视频实际尺寸
                videoWidth = mediaPlayer.getVideoWidth();
                videoHeight = mediaPlayer.getVideoHeight();

                // 在EGL线程更新纹理尺寸
                mHandler.post(() -> {
                    // 设置预览纹理缓冲区大小
                    mPreviewSurfaceTexture.setDefaultBufferSize(videoWidth, videoHeight);
                    // 设置目标纹理尺寸（通知辅助工具）
                    mCustomVideoTextureHelper.setTextureSize(videoWidth, videoHeight);
                });

                mediaPlayer.start();  // 开始播放
            });

            // 错误监听
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                return false;
            });

            mediaPlayer.prepareAsync();  // 异步准备播放器
        } catch (Exception e) {
            Log.e(TAG, "MediaPlayer initialization failed", e);
        }
    }

    /**
     * 当新视频帧可用时的回调处理
     * 1. 确保EGL上下文正确绑定
     * 2. 更新纹理数据
     * 3. 获取纹理变换矩阵
     * 4. (可选)调试帧捕获
     * 5. 渲染到目标表面
     */
    private void onFrameAvailable() {
        // 确保当前绑定的是目标渲染表面
        if (!mEglCore.isCurrent(mDrawSurface)) {
            mEglCore.makeCurrent(mDrawSurface);
            Log.i(TAG, "onFrameAvailable makeCurrent mDrawSurface");
        }

        // 更新纹理内容（从MediaPlayer获取最新帧）
        mPreviewSurfaceTexture.updateTexImage();

        // 获取纹理变换矩阵（校正坐标映射）
        float[] transformMatrix = new float[16];
        mPreviewSurfaceTexture.getTransformMatrix(transformMatrix);

        // 调试用帧捕获（仅在DUMP_FRAME开启时生效）
        if (DUMP_FRAME) {
            frameDumper.increaseFrameCount();
            frameDumper.dumpTexture(mPreviewTexture, GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoWidth, videoHeight);
        }

        // 设置视口尺寸
        GLES20.glViewport(0, 0, videoWidth, videoHeight);
        // 使用OES纹理程序渲染帧
        mDrawProgram.drawFrame(mPreviewTexture, transformMatrix, mMVPMatrix);
        // 交换缓冲区（触发帧回调）
        mEglCore.swapBuffers(mDrawSurface);
    }

    /**
     * 释放所有资源（线程安全的资源清理）
     */
    public void release() {
        // 1. 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // 2. 释放Surface（非OpenGL资源）
        if (mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }

        // 3. 在EGL线程中安全释放OpenGL资源
        if (mHandler != null) {
            ThreadUtils.invokeAtFrontUninterruptibly(mHandler, () -> {
                // 3.1 解除帧监听防止回调
                if (mPreviewSurfaceTexture != null) {
                    mPreviewSurfaceTexture.setOnFrameAvailableListener(null);
                }

                // 3.2 释放OpenGL渲染程序
                if (mDrawProgram != null) {
                    mDrawProgram.release();
                    mDrawProgram = null;
                }

                // 3.3 释放纹理帧辅助工具（停止帧回调）
                if (mCustomVideoTextureHelper != null) {
                    mCustomVideoTextureHelper.dispose();
                    mCustomVideoTextureHelper = null;
                }

                // 3.4 释放SurfaceTexture（自动删除关联纹理）
                if (mPreviewSurfaceTexture != null) {
                    mPreviewSurfaceTexture.release();
                    mPreviewSurfaceTexture = null;
                }

                // 3.5 释放EGL资源
                if (mEglCore != null) {
                    // 释放目标渲染表面
                    if (mDrawSurface != null) {
                        mEglCore.releaseSurface(mDrawSurface);
                        mDrawSurface = null;
                    }
                    // 释放离屏表面
                    if (mDummySurface != null) {
                        mEglCore.releaseSurface(mDummySurface);
                        mDummySurface = null;
                    }
                    // 释放EGL核心
                    mEglCore.release();
                    mEglCore = null;
                }
            });

            // 4. 停止并清理EGL线程
            mHandler.getLooper().quitSafely();
            mHandler = null;
        }
    }

    /**
     * VideoSink接口实现：接收处理后的视频帧
     *
     * 当CustomVideoTextureHelper完成帧处理后，会通过此回调将封装好的
     * VideoFrame对象传递给当前对象，最终通过TcrSession发送给SDK
     *
     * @param videoFrame 封装好的视频帧对象
     */
    @Override
    public void onFrame(VideoFrame videoFrame) {
        // 将处理后的视频帧提交给会话
        mSession.sendCustomVideo(videoFrame);
    }
}