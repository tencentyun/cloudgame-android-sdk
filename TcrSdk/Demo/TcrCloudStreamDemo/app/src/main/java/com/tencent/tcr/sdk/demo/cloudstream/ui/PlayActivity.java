package com.tencent.tcr.sdk.demo.cloudstream.ui;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSession.Observer;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.view.MobileTouchListener;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import com.tencent.tcr.sdk.demo.cloudstream.R;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    boolean mIsGroupControl;//是否是群控云手机
    private ArrayList<String> mGroupInstanceIds;//云手机的实例ID列表。如果是非群控（单机模式），则只取第一个实例ID。
    private ArrayList<String> mPendingJoinInstanceIds;//待加入的云手机的实例ID列表。用于测试 join 操作
    private TcrRenderView mRenderView;// 渲染视图
    private TcrSession mTcrSession;// 云渲染会话
    private ScreenConfig mScreenConfig;// 云端屏幕信息
    private CustomDataChannel mCustomDataChannel_23331, mCustomDataChannel_23332;//自定义数据通道
    private boolean mIsConnected_23331, mIsConnected_23332;//标记自定义数据通道的状态
    private final Observer mSessionObserver = new Observer() {// Tcr会话的观察者，处理各类事件通知的消息和数据
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
                        TcrSdk.getInstance().getAndroidInstance().requestStream(masterId, "open", "normal");
                    }
                    mRenderView.postDelayed(() -> createCustomDataChannel(), 1000);
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
        mPendingJoinInstanceIds = getIntent().getStringArrayListExtra("pending_join_instanceIds");
        findViewById(R.id.back).setOnClickListener(v -> onBackKeyPressed());
        findViewById(R.id.menu).setOnClickListener(v -> onMenuKeyPressed());
        findViewById(R.id.home).setOnClickListener(v -> onHomeKeyPressed());

        // 创建Tcr会话对象
        initTcrSession();
        // 创建渲染视图
        initTcrRenderView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_setRemoteVideoProfile) {
            mTcrSession.setRemoteVideoProfile(10, 100, 100, 960, 960, null);
            return true;
        } else if (id == R.id.menu_pauseStreaming) {
            mTcrSession.pauseStreaming("video");
            return true;
        } else if (id == R.id.menu_resumeStreaming) {
            mTcrSession.resumeStreaming("video");
            return true;
        } else if (id == R.id.menu_joinGroup) {
            if (mIsGroupControl) {
                TcrSdk.getInstance().getAndroidInstance().joinGroupControl(mPendingJoinInstanceIds);
                Toast.makeText(this, "joinGroup:" + TextUtils.join(",", mPendingJoinInstanceIds), Toast.LENGTH_SHORT)
                        .show();
                mGroupInstanceIds.addAll(mPendingJoinInstanceIds);
                mPendingJoinInstanceIds.clear();
                // 延迟一会后，同步操作列表
                mRenderView.postDelayed(() -> TcrSdk.getInstance().getAndroidInstance().setSyncList(mGroupInstanceIds),
                        1000);
            } else {
                Log.e(TAG, "not in group control");
                Toast.makeText(this, "not in group control", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.menu_sendCustomMsg) {
            if (mCustomDataChannel_23331 == null || !mIsConnected_23331) {
                Toast.makeText(this, "mCustomDataChannel_23331 不可用", Toast.LENGTH_SHORT).show();
            } else {
                mCustomDataChannel_23331.send(ByteBuffer.wrap("hello world 1".getBytes()));
            }
            if (mCustomDataChannel_23332 == null || !mIsConnected_23332) {
                Toast.makeText(this, "mCustomDataChannel2 不可用", Toast.LENGTH_SHORT).show();
            } else {
                mCustomDataChannel_23332.send(ByteBuffer.wrap("hello world 2".getBytes()));
            }
        }
        return super.onOptionsItemSelected(item);
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
        mRenderView.setOnTouchListener(new MobileTouchListener(mTcrSession));
        //mRenderView.setDisplayDebugView(true);
    }

    /**
     * 旋转屏幕方向以及画面方向, 以便本地的屏幕方向和云端保持一致<br>
     * 注意: 请确保Manifest中的Activity有android:configChanges="orientation|screenSize"配置, 避免Activity因旋转而被销毁.<br>
     **/
    private void updateRotation() {
        Activity activity = this;
        if (activity == null) {
            Log.w(TAG, "updateOrientation() activity=null");
            return;
        }

        if (mScreenConfig.screenHeight > mScreenConfig.screenWidth) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (mScreenConfig.degree.equals("0_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
        }
        if (mScreenConfig.degree.equals("90_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_90);
        }
        if (mScreenConfig.degree.equals("180_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
        }
        if (mScreenConfig.degree.equals("270_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_270);
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
        int KEY_HOME = 172; // Home键KeyCode
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, false);
    }

    // 按下back键
    private void onBackKeyPressed() {
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            return;
        }
        final int KEY_BACK = 158; // 返回键KeyCode
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, false);
    }

    // 按下menu键
    private void onMenuKeyPressed() {
        if (mTcrSession == null) {
            Log.e(TAG, "mTcrSession = null");
            return;
        }
        int KEY_MENU = 139;// 菜单键KeyCode
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, false);
    }

    private void createCustomDataChannel() {
        CustomDataChannel.Observer observer = new CustomDataChannel.Observer() {
            @Override
            public void onConnected(int port) {
                Log.d(TAG, "CustomDataChannel onConnected: " + port);
                if (port == 23331) {
                    mIsConnected_23331 = true;
                } else if (port == 23332) {
                    mIsConnected_23332 = true;
                }
            }

            @Override
            public void onError(int port, int code, String msg) {
                Log.d(TAG, "CustomDataChannel onError: port=" + port + ", code=" + code + ", msg=" + msg);
                if (port == 23331) {
                    mIsConnected_23331 = false;
                } else if (port == 23332) {
                    mIsConnected_23332 = false;
                }
            }

            @Override
            public void onMessage(int port, ByteBuffer buffer) {
                Log.d(TAG, "CustomDataChannel onMessage: port=" + port);
            }
        };
        mCustomDataChannel_23331 = mTcrSession.createCustomDataChannel(23331, "android", observer);
        mCustomDataChannel_23332 = mTcrSession.createCustomDataChannel(23332, "android_broadcast", observer);
    }
}