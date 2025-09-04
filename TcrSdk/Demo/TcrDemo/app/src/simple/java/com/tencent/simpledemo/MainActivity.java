package com.tencent.simpledemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.tencent.simpledemo.data.StartGameResponse;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSession.Observer;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.TcrTestEnv;
import com.tencent.tcr.sdk.api.data.CursorState;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.data.StatsInfo;
import com.tencent.tcr.sdk.api.data.VideoStreamConfig;
import com.tencent.tcr.sdk.api.view.MobileTouchListener;
import com.tencent.tcr.sdk.api.view.PcClickListener;
import com.tencent.tcr.sdk.api.view.PcTouchListener;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import com.tencent.tcrdemo.R;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

/**
 * 该类演示了如何初始化TcrSdk，创建会话、请求远端会话、启动云应用，并将云应用画面展示到界面上的基础流程。<br>
 *
 * <p>
 * 要使用TcrSdk，你需要先调用{@link TcrSdk#init(Context, String, AsyncCallback)}接口初始化TcrSdk,<br>
 * 在{@link AsyncCallback#onSuccess(Object)} 回调以后才能做进一步操作，例如创建{@link TcrSession}以及{@link TcrRenderView}。
 * </p>
 *
 * 在启动会话{@link TcrSession#start(String)}后才可以与云端实例进行交互。<br>
 * 具体的流程如下:
 *
 * <pre>
 * {@code
 *     ┌──────────────┐  ┌────────────┐ ┌────────────┐  ┌───────────┐
 *     │ MainActivity │  │ TcrSession │ │ App Server │  │ Cloud Api │
 *     └──────┬───────┘  └──────┬─────┘ └──────┬─────┘  └─────┬─────┘
 *            │ setObserver()   │              │              │
 *            ├────────────────►│              │              │
 *            │                 ├─────┐        │              │
 *            │                 │     │        │              │
 *            │   Event.INITED  │◄────┘        │              │
 *            │◄────────────────┤              │              │
 *            │                 │              │              │
 *            │    startGame(clientSession)    │              │
 *            ├─────────────────┬─────────────►│              │
 *            │                 │              │   tryLock    │
 *            │                 │              ├─────────────►│
 *            │                 │              │              │
 *            │                 │              │createSession │
 *            │                 │              ├─────────────►│
 *            │                 │              │              │
 *            │    onSuccess(serverSession)    │              │
 *            │◄────────────────┬──────────────┤              │
 *            │                 │              │              │
 *            │      start()    │              │              │
 *            ├────────────────►│              │              │
 *            │                 │              │              │
 *                SDK调用流程                       后台交互流程
 * }
 * </pre>
 *
 * <p>
 * 1.调用{@link TcrSdk#init(Context, String, AsyncCallback)}初始化SDK，初始化成功后创建TcrSession,
 * 并通过{@link TcrSessionConfig.Builder#observer(Observer)}将Observer设置好，通过Observer的回调拿到TcrSession对外的通知<br>
 * 2.通过Observer的回调得到TcrSession初始化成功{@link TcrSession.Event#STATE_INITED}的事件后，将事件传递的数据解析为clientSession<br>
 * 3.调用业务后台接口, 将{@code clientSession}传递给云端实例，并获取{@code serverSession}。<br>
 * 4.拿到{@code serverSession}之后，调用{@link TcrSession#start(String)}启动会话。<br>
 * 5.当会话启动成功之后用户便可以和云端实例进行交互。<br>
 * </p>
 *
 *
 * <p>
 * 详细的TcrSdk接口如何使用，请参考文档<br>
 * 业务后台的搭建，请参考链接<br>
 *
 * @see <a href="https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/index.html">TcrSdK API</a>
 * @see <a href="https://github.com/tencentyun/gs-server-demo">搭建业务后台</a></p>
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private final int MOBILE_GAME = 1;
    private final int PC_GAME = 2;
    private final DecimalFormat mDf = new DecimalFormat("#.##");
    /**
     * 渲染视图
     */
    private TcrRenderView mRenderView;
    /**
     * 云渲染会话
     */
    private TcrSession mTcrSession;
    /**
     * 性能数据视图
     */
    private TextView mStatsValueTextView;
    /**
     * 创建的数据通道
     */
    private CustomDataChannel mCustomDataChannel;
    /**
     * 云端横竖屏信息
     */
    private ScreenConfig mScreenConfig;
    /**
     * 视频流分辨率信息
     */
    private VideoStreamConfig mVideoStreamConfig;
    /**
     * 记录云端屏幕配置是否发生变化
     */
    private boolean mScreenConfigChanged = false;
    /**
     * 记录视频分辨率是否发生变化
     */
    private boolean mVideoStreamConfigChanged = false;
    private PcTouchListener mPcTouchListener;
    /**
     * 观察TcrSession通知出的各类事件，处理各类事件通知的消息和数据
     *
     * @see TcrSession.Event
     */
    private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
        @Override
        public void onEvent(TcrSession.Event event, Object eventData) {
            switch (event) {
                case STATE_INITED:
                    // 回调数据中拿到client session并请求ServerSession
                    String clientSession = (String) eventData;
                    requestServerSession(clientSession);
                    break;
                case STATE_CONNECTED:
                    // 连接成功后设置操作模式
                    // 与云端的交互需在此事件回调后开始调用接口
                    runOnUiThread(() -> setTouchHandler(mTcrSession, mRenderView, PC_GAME));
                    createCustomDataChannel();
                    break;
                case STATE_RECONNECTING:
                    showToast("重连中...", Toast.LENGTH_LONG);
                    break;
                case STATE_CLOSED:
                    showToast("会话关闭", Toast.LENGTH_SHORT);
                    finish();
                    break;
                case SCREEN_CONFIG_CHANGE:
                    mScreenConfig = (ScreenConfig) eventData;
                    updateRotation();
                    mScreenConfigChanged = true;
                    updateRotation();
                    break;
                case VIDEO_STREAM_CONFIG_CHANGED:
                    mVideoStreamConfig = (VideoStreamConfig) eventData;
                    mVideoStreamConfigChanged = true;
                    updateRotation();
                    break;
                case CLIENT_STATS:
                    StatsInfo statsInfo = (StatsInfo) eventData;
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            mStatsValueTextView.setText(
                                    "   fps: " + statsInfo.fps + "   bitrate: " + mDf.format(
                                            statsInfo.bitrate / 1024.0 / 1024.0)
                                            + "Mb/s   rtt: " + statsInfo.rtt + "ms");
                        }
                    });
                    break;
                case CURSOR_STATE_CHANGE:
                    CursorState cursorState = (CursorState) eventData;
                    if (cursorState != null) {
                        Log.i(TAG, "CURSOR_STATE_CHANGE cursorShowState=" + cursorState.cursorShowState);
                        runOnUiThread(() -> {
                            if (mPcTouchListener != null) {
                                mPcTouchListener.setMouseConfig(false, 1.0f, cursorState.cursorShowState);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_main);
        mStatsValueTextView = findViewById(R.id.stats_value);
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
                if (mTcrSession == null) {
                    Log.e(TAG, "mTcrSession = null");
                    showToast("创建TcrSession失败，请查看日志", Toast.LENGTH_SHORT);
                    return;
                }
                // 创建和初始化渲染视图
                runOnUiThread(() -> initTcrRenderView());
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
        // 创建渲染视图
        mRenderView = TcrSdk.getInstance()
                .createTcrRenderView(MainActivity.this, mTcrSession, TcrRenderViewType.SURFACE);
        if (mRenderView == null) {
            Log.e(TAG, "mRenderView = null");
            showToast("创建TcrRenderView失败,请查看日志", Toast.LENGTH_SHORT);
            return;
        }
        // 将渲染视图添加到界面上
        ((FrameLayout) findViewById(R.id.render_view_parent)).addView(mRenderView);

    }

    /**
     * 通过http请求业务后台并获取ServerSession，拿到ServerSession后启动会话<br>
     * 如果您需要启动云应用请调用CloudRenderBiz.getInstance().startProject()<br>
     * 如果您需要启动云游戏请调用CloudRenderBiz.getInstance().startGame()<br>
     * <br>
     * 无论启动的是云应用还是云游戏，都需要接入方准备相应的业务后台环境。<br>
     * 云渲染团队提供了一个测试环境，可通过{@link TcrTestEnv}工具进行调用，方便您做客户端SDK接入测试。<br>
     */
    private void requestServerSession(String clientSession) {
        Log.i(TAG, "init session success:" + clientSession);
        if (!TextUtils.isEmpty(CloudRenderBiz.EXPERIENCE_CODE)) {
            TcrTestEnv.getInstance()
                    .startSession(this, CloudRenderBiz.EXPERIENCE_CODE, clientSession, serverSession -> {
                        mTcrSession.start(serverSession);
                    }, error -> {
                        showToast("startSession failed, error=" + error, Toast.LENGTH_SHORT);
                    });
        } else if (!TextUtils.isEmpty(CloudRenderBiz.SERVER_URL)) {
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
        } else {
            throw new RuntimeException("请在CloudRenderBiz类中填写体验码或者业务后台地址");
        }
    }

    /**
     * 旋转屏幕方向以及画面方向, 以便本地的屏幕方向和云端保持一致<br>
     * 注意: 请确保Manifest中的Activity有android:configChanges="orientation|screenSize"配置, 避免Activity因旋转而被销毁.<br>
     **/
    private void updateRotation() {
        if (!mScreenConfigChanged || !mVideoStreamConfigChanged) {
            Log.w(TAG, "updateRotation failed,mScreenConfigChanged=" + mScreenConfigChanged
                    + "  mVideoStreamConfigChanged=" + mScreenConfigChanged);
            return;
        }
        Activity activity = this;
        if (activity == null) {
            Log.w(TAG, "updateOrientation() activity=null");
            return;
        }

        Log.w(TAG, "mVideoStreamConfig=" + mVideoStreamConfig);
        Log.w(TAG, "mScreenConfig.degree=" + mScreenConfig.degree);

        // 1. 根据云端Activity的方向（degree）和视频流的宽高，调整本地屏幕方向(使得本地屏幕方向和云端Activity方向保持一致)
        // 视频流	云端Activity		客户端处理
        // 横屏	      竖屏		    设置竖屏
        // 竖屏        竖屏           设置竖屏
        // 竖屏        横屏           设置横屏
        // 横屏        横屏           设置横屏
        boolean isLandscape = mScreenConfig.degree.equals("90_degree") || mScreenConfig.degree.equals("270_degree");
        boolean isPortrait = mScreenConfig.degree.equals("0_degree") || mScreenConfig.degree.equals("180_degree");
        if (mVideoStreamConfig.width > mVideoStreamConfig.height) {
            if (isLandscape) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (isPortrait) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        // 2. 根据云端屏幕方向，调整本地画面方向(云端画面为逆时针旋转, 本地视图setVideoRotation设置的是顺时针旋转)
        if (mScreenConfig.degree.equals("0_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
        }
        if (mScreenConfig.degree.equals("90_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_270);
        }
        if (mScreenConfig.degree.equals("180_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
        }
        if (mScreenConfig.degree.equals("270_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_90);
        }
    }

    /**
     * 为不同的云端实例设置处理器
     * <p>
     * 对于云端PC应用可以使用{@link PcTouchListener}
     * (云端PC应用在支持windows多点触摸时可使用{@link MobileTouchListener})
     * </p>
     * 对于手游要使用{@link MobileTouchListener}
     */
    private void setTouchHandler(TcrSession session, TcrRenderView renderView, int gameType) {
        switch (gameType) {
            case MOBILE_GAME:
                renderView.setOnTouchListener(new MobileTouchListener(session));
                break;
            case PC_GAME:
                mPcTouchListener = new PcTouchListener(session);
                renderView.setOnTouchListener(mPcTouchListener);
                mPcTouchListener.getZoomHandler().setZoomRatio(1f, 5f);
                mPcTouchListener.setShortClickListener(new PcClickListener(session));
                break;
            default:
                Log.e(TAG, "UNKNOWN DeviceMode!!");
        }
    }

    private void showToast(String msg, int duration) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, duration).show());
    }

    /**
     * 创建自定义数据通道
     */
    private void createCustomDataChannel() {
        if (mTcrSession == null) {
            return;
        }
        // 10000为数据通道端口，请替换为你们自己业务的端口
        mCustomDataChannel = mTcrSession.createCustomDataChannel(10000, new CustomDataChannel.Observer() {
            @Override
            public void onConnected(int port) {
                final String msg = "Your message";
                mCustomDataChannel.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                Log.i(TAG, "onConnected() send data to port " + port + ": " + msg);
            }

            @Override
            public void onError(int port, int code, String msg) {
                Log.e(TAG, "onError() " + port + " msg:" + msg);
            }

            @Override
            public void onMessage(int port, ByteBuffer data) {
                Log.i(TAG, "onMessage() port=" + port + " data=" + StandardCharsets.UTF_8.decode(data));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mTcrSession != null) {
            mTcrSession.release();
        }
        if (mRenderView != null) {
            mRenderView.release();
        }
        if (mPcTouchListener != null) {
            mPcTouchListener.release();
        }
        super.onDestroy();
    }
}