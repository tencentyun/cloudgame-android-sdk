package com.tencent.tcr.demo.cloudphone.common.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.bean.HitInput;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSession.Observer;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    boolean mIsGroupControl;//是否是群控云手机
    private ArrayList<String> mGroupInstanceIds;//云手机的实例ID列表。如果是非群控（单机模式），则只取第一个实例ID。
    private ArrayList<String> mJoinLeaveInstanceIds;//待加入或退出的群控云手机的实例ID列表
    private TcrRenderView mRenderView;// 渲染视图
    private TcrSession mTcrSession;// 云渲染会话
    private ScreenConfig mScreenConfig;// 云端屏幕信息
    private CustomDataChannel mCustomDataChannel_23331, mCustomDataChannel_23332;//自定义数据通道
    private boolean mIsConnected_23331, mIsConnected_23332;//标记自定义数据通道的状态
    private final Observer mSessionObserver = new Observer() {// Tcr会话的观察者，处理各类事件通知的消息和数据
        @Override
        public void onEvent(TcrSession.Event event, Object eventData) {
            if (isFinishing() || isDestroyed()) {
                Log.w(TAG, "onEvent() activity is finishing or destroyed, return");
                return;
            }
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
                    showToast("云手机连接成功", Toast.LENGTH_SHORT);
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
                case INPUT_STATE_CHANGE:
                    HitInput hitinput = new Gson().fromJson((String) eventData, HitInput.class);
                    showToast("INPUT_STATE_CHANGE() hitinput=" + hitinput, Toast.LENGTH_SHORT);
                    if (hitinput == null) {
                        showToast("onEvent() hitinput=null" + hitinput, Toast.LENGTH_SHORT);
                        break;
                    }
                    if ("normal_input".equals(hitinput.field_type)) {
                        Intent intent = new Intent(PlayActivity.this, InputActivity.class);
                        intent.putExtra(InputActivity.INPUT_TEXT, hitinput.text);
                        mInputActivityLauncher.launch(intent);
                    }
                    break;
                default:
                    Log.v(TAG, "onEvent() " + event.name() + " msg: " + eventData.toString());
                    break;
            }
        }
    };
    private boolean isLocalIME = true; // 默认使用local输入法
    private boolean menu_switchDebugView_state = false;// 调试视图，true显示，false不显示
    private boolean menu_switchJoinGroup_state = false;// true 加入群控，false 退出群控
    private boolean menu_switchPauseResumeStreaming_state = false;// true 暂停推流，false 恢复群控
    private boolean menu_switchCamera_state = false;// true 打开摄像头，false 关闭摄像头
    private boolean menu_switchMic_state = false;// true 打开麦克风，false 关闭麦克风

    // 本地输入法Activity的启动器
    private ActivityResultLauncher<Intent> mInputActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sfu_play);

        // 获取传入的参数
        mIsGroupControl = getIntent().getBooleanExtra("isGroupControl", false);
        mGroupInstanceIds = getIntent().getStringArrayListExtra("instanceIds");
        mJoinLeaveInstanceIds = getIntent().getStringArrayListExtra("joinLeaveInstanceIds");
        findViewById(R.id.back).setOnClickListener(v -> onBackKeyPressed());
        findViewById(R.id.menu).setOnClickListener(v -> onMenuKeyPressed());
        findViewById(R.id.home).setOnClickListener(v -> onHomeKeyPressed());

        // 创建Tcr会话对象
        initTcrSession();
        // 创建渲染视图
        initTcrRenderView();

        mInputActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (mTcrSession == null) {
                        Log.e(TAG, "onActivityResult mSession=null");
                        return;
                    }

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            Log.e(TAG, "onActivityResult data=null");
                            return;
                        }
                        String input = data.getStringExtra(InputActivity.INPUT_TEXT);
                        if (input == null) {
                            Log.e(TAG, "onActivityResult input=null");
                            return;
                        }

                        Log.d(TAG, "onActivityResult input:" + input);
                        mTcrSession.inputText(input, true, input.length());
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);

        if (!mIsGroupControl) {
            menu.findItem(R.id.menu_switchJoinLeaveGroup).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_setRemoteVideoProfile) {
            mTcrSession.setRemoteVideoProfile(30, 1000, 8000, 1080, 1920, null);
            return true;
        } else if (id == R.id.menu_switchPauseResumeStreaming) {
            menu_switchPauseResumeStreaming_state = !menu_switchPauseResumeStreaming_state;
            if (menu_switchPauseResumeStreaming_state) {
                mTcrSession.pauseStreaming("video");
                Toast.makeText(this, "暂停推流", Toast.LENGTH_SHORT).show();
            } else {
                mTcrSession.resumeStreaming("video");
                Toast.makeText(this, "恢复推流", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.menu_switchJoinLeaveGroup) {
            menu_switchJoinGroup_state = !menu_switchJoinGroup_state;
            if (menu_switchJoinGroup_state) {// 加入群控
                TcrSdk.getInstance().getAndroidInstance().joinGroupControl(mJoinLeaveInstanceIds);
                // 同步操作列表(延迟一会)
                ArrayList<String> list3 = new ArrayList<>(mGroupInstanceIds);
                list3.addAll(mJoinLeaveInstanceIds);
                mRenderView.postDelayed(() -> TcrSdk.getInstance().getAndroidInstance().setSyncList(list3), 1000);
                // ui 交互
                Toast.makeText(this, "加入群控:" + TextUtils.join(",", mJoinLeaveInstanceIds), Toast.LENGTH_LONG)
                        .show();
            } else {// 退出群控
                TcrSdk.getInstance().getAndroidInstance().setSyncList(mGroupInstanceIds);
                // ui 交互
                Toast.makeText(this, "退出群控:" + TextUtils.join(",", mJoinLeaveInstanceIds), Toast.LENGTH_LONG)
                        .show();
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
        } else if (id == R.id.menu_switchDebugView) {
            menu_switchDebugView_state = !menu_switchDebugView_state;
            mRenderView.setDisplayDebugView(menu_switchDebugView_state);
            return true;
        } else if (id == R.id.menu_switchCamera) {
            menu_switchCamera_state = !menu_switchCamera_state;
            if (menu_switchCamera_state) {
                Toast.makeText(this, "打开摄像头", Toast.LENGTH_SHORT).show();
                mTcrSession.setEnableLocalVideo(true);
                mTcrSession.setLocalVideoProfile(720, 1280, 25, 1000, 3000, false);//可选，true使用前置摄像头
            } else {
                mTcrSession.setEnableLocalVideo(false);
                Toast.makeText(this, "关闭摄像头", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.menu_switchMic) {
            menu_switchMic_state = !menu_switchMic_state;
            if (menu_switchMic_state) {
                mTcrSession.setEnableLocalAudio(true);
                Toast.makeText(this, "打开麦克风", Toast.LENGTH_SHORT).show();
            } else {
                mTcrSession.setEnableLocalAudio(false);
                Toast.makeText(this, "关闭麦克风", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.menu_transMessage) {
            TcrSdk.getInstance().getAndroidInstance().transMessage("com.example.myapplication", "trans_message");
            return true;
        } else if (id == R.id.menu_switchIME) {
            String imeType = isLocalIME ? "cloud" : "local";
            TcrSdk.getInstance().getAndroidInstance().switchIME(imeType);
            Toast.makeText(PlayActivity.this, "输入法已切换为" + imeType, Toast.LENGTH_SHORT).show();
            isLocalIME = !isLocalIME; // 切换状态
            return true;
        } else if (id == R.id.menu_setRemoteDesktopResolution) {
            showResolutionInputDialog();
            return true;
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
        //mRenderView.setDisplayDebugView(true);
    }

    /**
     * 旋转屏幕方向以及画面方向, 以便本地的显示和云端一致<br>
     * 注意: 请确保Manifest中的Activity有android:configChanges="orientation|screenSize"配置, 避免Activity因旋转而被销毁.<br>
     **/
    private void updateRotation() {
        // 旋转屏幕方向。逻辑：如果屏幕高度大于宽度，则认为竖屏，否则认为横屏。
        if (mScreenConfig.screenHeight > mScreenConfig.screenWidth) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // 旋转画面方向。逻辑：反方向旋转degree，如果为负数则+360
        switch (mScreenConfig.degree) {
            case "0_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
                break;
            case "90_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_270);
                break;
            case "180_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
                break;
            case "270_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_90);
                break;
            default:
                Log.w(TAG, "updateRotation() unknown degree=" + mScreenConfig.degree);
                break;
        }
    }

    private void updateRotation_mode2() {
        // 屏幕方向不旋转。画面方向 180 和 0 的显示一样，270 和 90 的显示一样。
        switch (mScreenConfig.degree) {
            case "0_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
                break;
            case "90_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
                break;
            case "180_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
                break;
            case "270_degree":
                mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
                break;
            default:
                Log.w(TAG, "updateRotation() unknown degree=" + mScreenConfig.degree);
                break;
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
                Log.i(TAG, "CustomDataChannel onConnected: " + port);
                //showToast("自定义数据通道连接成功 port="+port, Toast.LENGTH_SHORT);
                if (port == 23331) {
                    mIsConnected_23331 = true;
                } else if (port == 23332) {
                    mIsConnected_23332 = true;
                }
            }

            @Override
            public void onError(int port, int code, String msg) {
                Log.e(TAG, "CustomDataChannel onError: port=" + port + ", code=" + code + ", msg=" + msg);
                showToast("自定义数据通道连接失败 port=" + port + ", code=" + code + ", msg=" + msg,
                        Toast.LENGTH_SHORT);
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
        mCustomDataChannel_23331 = mTcrSession.createCustomDataChannel(23331, "android_broadcast", observer);
        mCustomDataChannel_23332 = mTcrSession.createCustomDataChannel(23332, "android", observer);
    }

    /**
     * 显示分辨率输入对话框
     */
    private void showResolutionInputDialog() {
        DialogUtils.showDoubleInputDialog(
                this,
                "设置云手机物理分辨率",
                "请输入需要修改的云手机物理分辨率",
                "宽度（例如：1280）",
                "高度（例如：720）",
                "1280",
                "720",
                (width, height) -> {
                    if (mTcrSession != null) {
                        mTcrSession.setRemoteDesktopResolution(width, height);
                        Toast.makeText(this, "已设置分辨率为 " + width + "x" + height, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "会话未初始化", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}