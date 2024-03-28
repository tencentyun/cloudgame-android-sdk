package com.tencent.advancedemo;

import static com.tencent.advancedemo.CloudRenderBiz.EXPERIENCE_CODE;
import static com.tencent.advancedemo.CloudRenderBiz.USE_TCR_TEST_ENV;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tencent.advancedemo.data.StartGameResponse;
import com.tencent.advancedemo.render.GLDrawer;
import com.tencent.advancedemo.render.GLRenderer;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.AudioSink;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.TcrTestEnv;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcrdemo.R;


/**
 * 该类演示了如何通过VideoSink和AudioSink拿到待渲染和待播放的音视频数据，通过回调的音视频数据自行做渲染和播放
 */
public class CustomRenderActivity extends Activity {

    private static final String TAG = "MainActivity";
    private final GLRenderer mRenderer = new GLRenderer("GLRender");
    /**
     * 渲染视图
     */
    private TcrRenderView mRenderView;
    /**
     * 云渲染会话
     */
    private TcrSession mTcrSession;
    /**
     * 观察TcrSession通知出的各类事件，处理各类事件通知的消息和数据
     *
     * @see TcrSession.Event
     */
    private final TcrSession.Observer mSessionEventObserver = (event, eventData) -> {
        switch (event) {
            case STATE_INITED:
                // 回调数据中拿到client session并请求ServerSession
                String clientSession = (String) eventData;
                requestServerSession(clientSession);
                break;
            case STATE_CONNECTED:
                // 连接成功后设置操作模式
                // 与云端的交互需在此事件回调后开始调用接口
                mRenderer.init(TcrSdk.getInstance().getEGLContext(), new GLDrawer("void main() {\n"
                        + "  gl_FragColor = sample(tc);\n"
                        + "}\n"));
                break;
            case STATE_RECONNECTING:
                showToast("重连中...", Toast.LENGTH_LONG);
                break;
            case STATE_CLOSED:
                showToast("会话关闭", Toast.LENGTH_SHORT);
                finish();
                break;
            case SCREEN_CONFIG_CHANGE:
                ScreenConfig screenConfig = (ScreenConfig) eventData;
                if (screenConfig == null) {
                    Log.e(TAG, "screenConfig parse error");
                    break;
                }
                updateOrientation(screenConfig);
                break;
            default:
                break;
        }
    };
    private SurfaceView mCustomView;
    private Surface mSurface;
    /**
     * AudioTrack，用于播放从回调中拿到的数据
     */
    private AudioTrack mAudioTrack = null;
    /**
     * 音频数据回调，在持续回调中写入数据
     */
    private final AudioSink mAudioSink = new AudioSink() {
        @Override
        public void onAudioData(byte[] data, int offsetInBytes, int sizeInBytes) {
            // 向audioTrack中写入数据
            mAudioTrack.write(data, offsetInBytes, sizeInBytes);
        }

        @Override
        public void onAudioFormat(int audioFormat, int channelConfiguration, int sampleRate) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_advance);
        mCustomView = new SurfaceView(getApplicationContext());
        mCustomView.getHolder().addCallback(new Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                mSurface = surfaceHolder.getSurface();
                mRenderer.createEglSurface(mSurface);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        // 初始化TcrSdk，初始化成功后创建TcrSession
        TcrSdk.getInstance().init(this, null, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.i(TAG, "init SDK success");
                showToast("sdk 初始化成功", Toast.LENGTH_SHORT);
                // 为TcrSession创建配置参数对象。参考https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html
                TcrSessionConfig tcrSessionConfig = TcrSessionConfig.builder()
                        .observer(mSessionEventObserver)
                        .idleThreshold(30000)
                        .build();
                // 创建会话对象
                mTcrSession = TcrSdk.getInstance().createTcrSession(tcrSessionConfig);
                mTcrSession.setAudioSink(mAudioSink);
                if (mTcrSession == null) {
                    Log.e(TAG, "mTcrSession = null");
                    showToast("创建TcrSession失败，请查看日志", Toast.LENGTH_SHORT);
                    return;
                }
                // 创建和初始化渲染视图
                initTcrRenderView();
            }

            @Override
            public void onFailure(int code, String msg) {
                String errorMsg = "init SDK failed:" + code + " msg:" + msg;
                Log.e(TAG, errorMsg);
                showToast(errorMsg, Toast.LENGTH_LONG);
            }
        });
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
     * 初始化渲染视图
     */
    private void initTcrRenderView() {
        // 为会话设置渲染视图
        mTcrSession.setVideoSink(mRenderer);
        // 将渲染视图添加到界面上
        ((FrameLayout) findViewById(R.id.render_view_parent)).addView(mCustomView);
    }

    /**
     * 通过http请求业务后台并获取ServerSession，拿到ServerSession后启动会话<br>
     * 如果您需要启动云应用请调用CloudRenderBiz.getInstance().startProject
     */
    private void requestServerSession(String clientSession) {
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
                StartGameResponse result = new Gson().fromJson(response.toString(), StartGameResponse.class);
                if (result.code == 0) {
                    boolean res = mTcrSession.start(result.sessionDescribe.serverSession);
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

    /**
     * 旋转屏幕方向, 以便本地的屏幕方向和云端保持一致<br>
     * 注意: 请确保Manifest中的Activity有android:configChanges="orientation|screenSize"配置, 避免Activity因旋转而被销毁.<br>
     *
     * @param config 云端的屏幕配置
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void updateOrientation(ScreenConfig config) {
        Log.i(TAG, "updateOrientation:" + config.orientation);
        if (config.orientation.equals("portrait")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (config.orientation.equals("landscape")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void showToast(String msg, int duration) {
        runOnUiThread(() -> Toast.makeText(CustomRenderActivity.this, msg, duration).show());
    }

    @Override
    protected void onDestroy() {
        mRenderer.release();
        if (mTcrSession != null) {
            mTcrSession.release();
        }
        if (mRenderView != null) {
            mRenderView.release();
        }

        super.onDestroy();
    }


}