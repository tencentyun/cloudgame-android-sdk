package com.tencent.tcr.sdk.demo.cloudphone.ui;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSession.Observer;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.data.VideoStreamConfig;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import com.tencent.tcr.sdk.demo.cloudphone.R;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlayActivity extends Activity {

    private static final String TAG = "PlayActivity";

    // 返回键KeyCode
    private static final int KEY_BACK = 158;

    // 菜单键KeyCode
    private static final int KEY_MENU = 139;

    // Home键KeyCode
    private static final int KEY_HOME = 172;
    private final DecimalFormat mDf = new DecimalFormat("#.##");
    boolean mIsGroupControl;//是否是群控云手机
    private ArrayList<String> mGroupInstanceIds;//云手机的实例ID列表。如果是非群控（单机模式），则只取第一个实例ID。
    // 渲染视图
    private TcrRenderView mRenderView;
    // 云渲染会话
    private TcrSession mTcrSession;
    // 云端横竖屏信息
    private ScreenConfig mScreenConfig;
    // 视频流分辨率信息
    private VideoStreamConfig mVideoStreamConfig;
    // 记录云端屏幕配置是否发生变化
    private boolean mScreenConfigChanged = false;
    // 记录视频分辨率是否发生变化
    private boolean mVideoStreamConfigChanged = false;
    // Tcr会话的观察者，处理各类事件通知的消息和数据
    private final Observer mSessionObserver = new Observer() {
        @Override
        public void onEvent(TcrSession.Event event, Object eventData) {
            switch (event) {
                case STATE_INITED:// 本地会话对象初始化完成
                    // 可以开始连接指定的云手机。
                    boolean ret = mTcrSession.access(mGroupInstanceIds, mIsGroupControl);
                    if (!ret) {
                        showToast("连接云手机失败，请重试", Toast.LENGTH_SHORT);
                        finish();
                    }
                    break;
                case STATE_CONNECTED:// 成功和指定的云手机建立连接
                    // 设置群控云手机的主控、同步列表、请求主控视频流
                    if (mIsGroupControl) {
                        String masterId = mGroupInstanceIds.get(0);
                        TcrSdk.getInstance().getAndroidInstance().setMaster(masterId);
                        TcrSdk.getInstance().getAndroidInstance().setSyncList(mGroupInstanceIds);
                        TcrSdk.getInstance().getAndroidInstance().requestStream(masterId, "open", "low");
                    }
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
                case CAI_TRANS_MESSAGE:
                case CAI_SYSTEM_USAGE:
                case CAI_CLIPBOARD:
                case CAI_NOTIFICATION:
                case CAI_SYSTEM_STATUS:
                    Log.d(TAG, "android instance event:" + event.name() + " msg: " + eventData.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // 获取传入的参数
        mIsGroupControl = getIntent().getBooleanExtra("isGroupControl", false);
        mGroupInstanceIds = getIntent().getStringArrayListExtra("instanceIds");
        findViewById(R.id.back).setOnClickListener(v -> onBackKeyPressed());
        findViewById(R.id.menu).setOnClickListener(v -> onMenuKeyPressed());
        findViewById(R.id.home).setOnClickListener(v -> onHomeKeyPressed());

        // 创建Tcr会话对象
        initTcrSession();
        // 创建渲染视图
        initTcrRenderView();
    }

    // 创建Tcr会话对象
    private void initTcrSession() {
        TcrSessionConfig tcrSessionConfig = TcrSessionConfig.builder().observer(mSessionObserver).build();
        mTcrSession = TcrSdk.getInstance().createTcrSession(tcrSessionConfig);
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            showToast("创建会话失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
        }
    }

    // 创建渲染视图
    private void initTcrRenderView() {
        if (mTcrSession == null) {
            showToast("创建渲染视图必须有关联的会话", Toast.LENGTH_SHORT);
            return;
        }
        // 创建渲染视图
        mRenderView = TcrSdk.getInstance().createTcrRenderView(this, mTcrSession, TcrRenderViewType.SURFACE);
        if (mRenderView == null) {
            Log.e(TAG, "mRenderView = null");
            showToast("创建渲染失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
            return;
        }
        // 将渲染视图添加到界面上
        ((FrameLayout) findViewById(R.id.render_view_parent)).addView(mRenderView);
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

    private void showToast(String msg, int duration) {
        runOnUiThread(() -> Toast.makeText(PlayActivity.this, msg, duration).show());
    }

    @Override
    protected void onDestroy() {
        if (mTcrSession != null) {
            mTcrSession.release();
        }
        if (mRenderView != null) {
            mRenderView.release();
        }
        super.onDestroy();
    }

    // 按下home键
    private void onHomeKeyPressed() {
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            return;
        }
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, false);
    }

    // 按下back键
    private void onBackKeyPressed() {
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            return;
        }
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, false);
    }

    // 按下menu键
    private void onMenuKeyPressed() {
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            return;
        }
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, false);
    }
}