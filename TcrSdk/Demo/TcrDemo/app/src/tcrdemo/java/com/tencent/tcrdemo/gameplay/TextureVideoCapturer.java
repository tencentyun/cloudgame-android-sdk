package com.tencent.tcrdemo.gameplay;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import org.twebrtc.CapturerObserver;
import org.twebrtc.SurfaceTextureHelper;
import org.twebrtc.ThreadUtils;
import org.twebrtc.VideoCapturer;
import org.twebrtc.VideoFrame;
import org.twebrtc.VideoSink;

/**
 * 演示自定义视频采集器：解码本地的 mp4 文件，将获取的纹理帧回调给 TcrSdk。以实现自定义上行视频功能。
 */
public class TextureVideoCapturer implements VideoCapturer, VideoSink {

    private static final String TAG = "TextureVideoCapturer";

    private final String filePath;
    private MediaPlayer mediaPlayer;
    private SurfaceTextureHelper surfaceTextureHelper;
    private CapturerObserver capturerObserver;
    private volatile boolean isDisposed;
    private volatile boolean isCapturing;

    public TextureVideoCapturer(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver observer) {
        checkNotDisposed();
        Log.i(TAG, "initialize()");
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.capturerObserver = observer;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            Log.e(TAG, "initialize() filePath=" + filePath + " Ex=" + e.getMessage());
        }
    }

    @Override
    public void startCapture(int width, int height, int fps) {
        checkNotDisposed();
        if (isCapturing) {
            return;
        }
        isCapturing = true;
        Log.i(TAG, "startCapture() " + width + "x" + height + "@" + fps);

        surfaceTextureHelper.setTextureSize(width, height);
        mediaPlayer.setSurface(new Surface(surfaceTextureHelper.getSurfaceTexture()));
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(mp -> {
            Log.i(TAG, "OnPreparedListener");
            mediaPlayer.start();
        });

        capturerObserver.onCapturerStarted(true); // 必须调用
        // SurfaceTextureHelper 的内部机制已经处理了帧的创建和分发逻辑。开发者只需要监听这个，就可以在 onFrame() 中获取到组装好的 VideoFrame 纹理帧。
        surfaceTextureHelper.startListening(this);
    }

    @Override
    public void stopCapture() {
        checkNotDisposed();
        if (!isCapturing) {
            return;
        }
        Log.i(TAG, "stopCapture()");

        ThreadUtils.invokeAtFrontUninterruptibly(surfaceTextureHelper.getHandler(), () -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            surfaceTextureHelper.stopListening(); // 对称调用
            capturerObserver.onCapturerStopped(); // 对称调用

            isCapturing = false;
        });
    }

    @Override
    public void changeCaptureFormat(int width, int height, int fps) {
        Log.w(TAG, "changeCaptureFormat() not supported");
        // 不支持动态修改格式
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        Log.i(TAG, "dispose()");
        try {
            if (isCapturing) {
                stopCapture();
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "dispose() e=" + e.getMessage());
        } finally {
            isDisposed = true;
        }
    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    @Override
    public void onFrame(VideoFrame frame) {
        //Log.v(TAG, "onFrame()");
        capturerObserver.onFrameCaptured(frame);
    }

    private void checkNotDisposed() {
        if (isDisposed) {
            throw new RuntimeException("disposed");
        }
    }
}