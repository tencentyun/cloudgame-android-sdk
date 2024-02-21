package com.tencent.advancedemo;

import static com.tencent.advancedemo.CloudRenderBiz.EXPERIENCE_CODE;
import static com.tencent.advancedemo.CloudRenderBiz.USE_TCR_TEST_ENV;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tencent.advancedemo.data.StartGameResponse;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.AudioSink;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.TcrTestEnv;
import com.tencent.tcr.sdk.api.VideoFrameBufferCallback;
import com.tencent.tcr.sdk.api.view.PcTouchListener;
import com.tencent.tcrdemo.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * 该类演示了如何通过VideoFrameBufferCallback拿到解码前的视频帧数据并通过MediaCodec解码渲染到视图上。
 */
public class MediaCodecActivity extends Activity {
    private static final String TAG = "MediaCodecActivity";
    private final MediaCodec.BufferInfo mVideoBufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec mMediaCodec;
    private Surface mSurface;
    private volatile boolean mRunning = false;
    private Thread mOutputVideoThread;
    private final VideoFrameBufferCallback mVideoFrameBufferCallback = new VideoFrameBufferCallback() {
        @Override
        public void onVideoBufferCallback(final ByteBuffer videoFrameByteBuffer, final int width, final int height, final long captureTimeNs) {
            if (mMediaCodec == null) {
                Log.i(TAG, "decode uninitalized, codec: ");
                return;
            }

            if (videoFrameByteBuffer == null) {
                Log.i(TAG, "decode() - no input data");
                return;
            }

            final int size = videoFrameByteBuffer.remaining();
            if (size == 0) {
                Log.i(TAG, "decode() - input buffer empty");
                return;
            }

            final int index;
            try {
                index = mMediaCodec.dequeueInputBuffer(500000);
            } catch (final IllegalStateException e) {
                Log.e(TAG, "dequeueInputBuffer failed," + e.getMessage());
                return;
            }
            if (index < 0) {
                Log.e(TAG, "decode() - no HW buffers available; decoder falling behind");
                return;
            }

            final ByteBuffer mediaInputBuffer;
            try {
                mediaInputBuffer = mMediaCodec.getInputBuffer(index);
            } catch (final IllegalStateException e) {
                Log.e(TAG, "getInputBuffers failed," + e.getMessage());
                return;
            }

            if (mediaInputBuffer.capacity() < size) {
                Log.e(TAG, "decode() - HW buffer too small");
                return;
            }
            mediaInputBuffer.put(videoFrameByteBuffer);

            try {
                mMediaCodec.queueInputBuffer(index, 0, size, TimeUnit.NANOSECONDS.toMicros(captureTimeNs), 0);
            } catch (final IllegalStateException e) {
                Log.e(TAG, "queueInputBuffer failed," + e.getMessage());
            }
        }

        @Override
        public void onMediaCodecFormat(final String mimeType, final int width, final int height) {
            try {
                mMediaCodec = MediaCodec.createDecoderByType(mimeType);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            mMediaCodec.configure(MediaFormat.createVideoFormat(mimeType, width, height), mSurface, null, 0);
            mMediaCodec.start();
            mRunning = true;
            mOutputVideoThread = new Thread("outputVideoThread") {
                @Override
                public void run() {
                    int result = 0;
                    while (mRunning) {
                        try {
                            result = mMediaCodec.dequeueOutputBuffer(mVideoBufferInfo, 100000);
                        } catch (final Exception exception){
                            Log.e(TAG,"dequeueOutputBuffer" + exception);
                        }
                        switch (result) {
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                Log.i(TAG, "format changed");
                                break;
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                Log.i(TAG, "解码当前帧超时");
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                Log.i(TAG, "info output buffers changed");
                                break;
                            default:
                                try {
                                    mMediaCodec.releaseOutputBuffer(result, true);
                                } catch (final Exception exception){
                                    Log.e(TAG,"releaseOutputBuffer" + exception);
                                }
                                break;
                        }
                    }
                    try {
                        mMediaCodec.stop();
                        mMediaCodec.release();
                    } catch (final Exception exception){
                        Log.e(TAG,"stop codec " + exception);
                    }
                }
            };
            mOutputVideoThread.start();
        }
    };
    /**
     * 渲染视图,将视频回调帧渲染到该View上
     */
    private SurfaceView mSurfaceView;
    /**
     * 云渲染会话
     */
    private TcrSession mTcrSession;
    /**
     * 观察TcrSession通知出的各类事件，处理各类事件通知的消息和数据
     *
     * @see TcrSession.Event
     */
    private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
        @Override
        public void onEvent(final TcrSession.Event event, final Object eventData) {
            switch (event) {
                case STATE_INITED:
                    // 回调数据中拿到client session并请求ServerSession
                    final String clientSession = (String) eventData;
                    requestServerSession(clientSession);
                    break;
                case STATE_CONNECTED:
                    // 连接成功后设置操作模式
                    // 与云端的交互需在此事件回调后开始调用接口
                    runOnUiThread(() -> mSurfaceView.setOnTouchListener(new PcTouchListener(mTcrSession)));
                    break;
                case STATE_RECONNECTING:
                    showToast("重连中...", Toast.LENGTH_LONG);
                    break;
                case STATE_CLOSED:
                    showToast("会话关闭", Toast.LENGTH_SHORT);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * AudioTrack，用于播放从回调中拿到的数据
     */
    private AudioTrack mAudioTrack = null;
    /**
     * 音频数据回调，在持续回调中写入数据
     */
    private final AudioSink mAudioSink = new AudioSink() {
        @Override
        public void onAudioData(final byte[] data, final int offsetInBytes, final int sizeInBytes) {
            // 向audioTrack中写入数据
            mAudioTrack.write(data, offsetInBytes, sizeInBytes);
        }

        @Override
        public void onAudioFormat(final int audioFormat, final int channelConfiguration, final int sampleRate) {
            mAudioTrack = new AudioTrack(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
                    new AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfiguration)
                            .build(),
                    AudioTrack.getMinBufferSize(sampleRate, channelConfiguration, audioFormat), AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);
            // 播放声音
            mAudioTrack.play();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_advance);
        initTcr();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
        mOutputVideoThread = null;
        if (mAudioTrack != null) {
            mAudioTrack.release();
        }
        mTcrSession.release();
    }

    private void initWindow() {
        // 不显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏展示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 初始化云游相关逻辑，包括TcrSession和TcrRenderView
     */
    private void initTcr() {
        // 初始化TcrSdk，初始化成功后创建TcrSession
        TcrSdk.getInstance().init(this, null, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {
                Log.i(TAG, "init SDK success");
                showToast("sdk 初始化成功", Toast.LENGTH_SHORT);
                // 为TcrSession创建配置参数对象。参考https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html
                final TcrSessionConfig tcrSessionConfig = TcrSessionConfig.builder()
                        .observer(mSessionEventObserver)
                        .videoFrameCallback(mVideoFrameBufferCallback)
                        .build();
                // 创建会话对象
                mTcrSession = TcrSdk.getInstance().createTcrSession(tcrSessionConfig);
                mTcrSession.setAudioSink(mAudioSink);
                if (mTcrSession == null) {
                    Log.e(TAG, "mTcrSession = null");
                    showToast("创建TcrSession失败，请查看日志", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(final int code, final String msg) {
                final String errorMsg = "init SDK failed:" + code + " msg:" + msg;
                Log.e(TAG, errorMsg);
                showToast(errorMsg, Toast.LENGTH_LONG);
            }
        });

        mSurfaceView = new SurfaceView(this);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull final SurfaceHolder holder) {
                mSurface = holder.getSurface();
            }

            @Override
            public void surfaceChanged(@NonNull final SurfaceHolder holder, final int format, final int width, final int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull final SurfaceHolder holder) {
            }
        });

        // 将渲染视图添加到界面上
        ((FrameLayout) findViewById(R.id.main)).addView(mSurfaceView);
    }

    /**
     * 通过http请求业务后台并获取ServerSession，拿到ServerSession后启动会话<br>
     * 如果您需要启动云应用请调用CloudRenderBiz.getInstance().startProject
     */
    private void requestServerSession(final String clientSession) {
        Log.i(TAG, "init session success:" + clientSession);

        if (USE_TCR_TEST_ENV) {
            if (TextUtils.isEmpty(EXPERIENCE_CODE)) {
                throw new NullPointerException("请在控制台创建体验码，并填写到EXPERIENCE_CODE中!!");
            }
            TcrTestEnv.getInstance().startSession(this, EXPERIENCE_CODE, clientSession, serverSession -> {
                mTcrSession.start(serverSession);
            }, error -> {
                showToast("startSession failed, error=" + error, Toast.LENGTH_SHORT);
            });
        } else {
            CloudRenderBiz.getInstance().startGame(clientSession, response -> {
                Log.i(TAG, "Request ServerSession success, response=" + response.toString());
                // 用从服务端获取到的server session启动会话
                final StartGameResponse result = new Gson().fromJson(response.toString(), StartGameResponse.class);
                if (result.code == 0) {
                    final boolean res = mTcrSession.start(result.sessionDescribe.serverSession);
                    if (!res) {
                        Log.e(TAG, "start session failed");
                        showToast("连接失败，请查看日志", Toast.LENGTH_SHORT);
                    }
                    showToast("连接成功", Toast.LENGTH_SHORT);
                } else {
                    String showMessage = "";
                    switch (result.code) {
                        case 10000:
                            showMessage = "sign校验错误";
                            break;
                        case 10001:
                            showMessage = "缺少必要参数";
                            break;
                        case 10200:
                            showMessage = "创建会话失败";
                            break;
                        case 10202:
                            showMessage = "锁定并发失败，无资源";
                            break;
                        default:
                    }
                    showToast(showMessage + result.msg, Toast.LENGTH_LONG);
                }
            }, error -> Log.i(TAG, "Request ServerSession success, response=" + error.toString()));
        }
    }

    private void showToast(final String msg, final int duration) {
        runOnUiThread(() -> Toast.makeText(MediaCodecActivity.this, msg, duration).show());
    }

}
