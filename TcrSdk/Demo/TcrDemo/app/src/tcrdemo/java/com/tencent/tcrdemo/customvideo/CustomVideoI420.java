package com.tencent.tcrdemo.customvideo;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.VideoFrame;
import com.tencent.tcr.sdk.api.utils.CustomVideoI420Helper;
import com.tencent.tcrdemo.TcrDemoApplication;
import com.tencent.tcrdemo.customvideo.gles.core.EglCore;
import java.io.File;
import java.nio.ByteBuffer;
import org.twebrtc.GlUtil;
import org.twebrtc.ThreadUtils;

/**
 * 此类演示了如何自定义上行视频（I420数据帧）。
 * <p>
 * 它首先使用MediaPlayer播放视频文件到SurfaceTexture，以获取视频纹理帧，将其转换为I420格式的YUV数据，作为数据源。</br>
 * 然后使用 CustomVideoI420Helper.buildFrame() 工具构造 VideoFrame 对象，通过 TcrSession.sendCustomVideo() 发送到TCR SDK。</p>
 */
public class CustomVideoI420 {

    private static final String TAG = "CustomVideoI420";
    private final TcrSession mSession;  // 会话对象
    private Handler mHandler;           // 处理EGL线程操作的Handler
    private MediaPlayer mediaPlayer;    // 媒体播放器实例
    private int videoWidth, videoHeight; // 视频原始宽高
    private EglCore mEglCore;           // EGL核心管理对象
    private EGLSurface mDummySurface;   // 离屏渲染表面
    private int mPreviewTexture = -1;   // OpenGL纹理ID
    private SurfaceTexture mPreviewSurfaceTexture; // 纹理表面
    private Surface mPreviewSurface;    // 供MediaPlayer输出的Surface

    /**
     * 构造函数：初始化视频播放和帧捕获
     *
     * @param session 会话对象，用于发送视频帧
     */
    public CustomVideoI420(TcrSession session) {
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

                // 获取视频实际尺寸并更新纹理缓冲区大小
                videoWidth = mediaPlayer.getVideoWidth();
                videoHeight = mediaPlayer.getVideoHeight();
                mHandler.post(() -> mPreviewSurfaceTexture.setDefaultBufferSize(videoWidth, videoHeight));

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
     * 1. 更新纹理数据
     * 2. 提取YUV数据
     * 3. 构造视频帧
     * 4. 发送到TcrSession会话
     */
    private void onFrameAvailable() {
        // 更新纹理内容（从MediaPlayer获取最新帧）
        mPreviewSurfaceTexture.updateTexImage();

        // 获取纹理变换矩阵（校正坐标映射）
        float[] transformMatrix = new float[16];
        mPreviewSurfaceTexture.getTransformMatrix(transformMatrix);

        // 从OpenGL纹理提取I420格式的YUV数据
        ByteBuffer[] i420Buffers = FrameDumper.getI420(mPreviewTexture, GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoWidth,
                videoHeight, transformMatrix);

        // 使用工具类构造视频帧对象。建议监听releaseCallback，复用传入的ByteBuffer，避免频繁的内存申请释放影响性能。
        VideoFrame videoFrame = CustomVideoI420Helper.buildFrame(i420Buffers[0], i420Buffers[1], i420Buffers[2],
                videoWidth, videoHeight, 0, System.nanoTime(), null);

        // 发送自定义视频帧到 TcrSession 会话
        mSession.sendCustomVideo(videoFrame);
        videoFrame.release();//必须调用，谁创建谁释放
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
                // 解除帧监听防止回调
                if (mPreviewSurfaceTexture != null) {
                    mPreviewSurfaceTexture.setOnFrameAvailableListener(null);
                }

                // 释放SurfaceTexture（自动删除关联纹理）
                if (mPreviewSurfaceTexture != null) {
                    mPreviewSurfaceTexture.release();
                    mPreviewSurfaceTexture = null;
                }

                // 释放EGL资源
                if (mEglCore != null) {
                    if (mDummySurface != null) {
                        mEglCore.releaseSurface(mDummySurface);
                        mDummySurface = null;
                    }
                    mEglCore.release();
                    mEglCore = null;
                }
            });

            // 4. 停止并清理EGL线程
            mHandler.getLooper().quitSafely();
            mHandler = null;
        }
    }
}