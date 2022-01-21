package com.example.demop.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.model.GameViewModel;
import com.example.demop.server.CloudGameApi;
import com.example.demop.server.CloudGameApi.IServerResponseListener;
import com.example.demop.server.param.ResponseResult;
import com.example.demop.server.param.StartGameResponseResult;
import com.example.demop.utils.CommonUtil;
import com.example.demop.view.ControlView;
import com.example.demop.view.FloatingSettingBarView;
import com.example.demop.view.FloatingSettingBarView.SettingEventListener;
import com.tencent.tcggamepad.IGamepadTouchDelegate;
import com.tencent.tcgsdk.TLog;
import com.tencent.tcgsdk.api.CursorStyle;
import com.tencent.tcgsdk.api.CursorType;
import com.tencent.tcgsdk.api.IPcTcgSdk;
import com.tencent.tcgsdk.api.IStatsListener;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.PcSurfaceGameView;
import com.tencent.tcgsdk.api.PcTcgSdk;
import com.tencent.tcgsdk.api.PerfValue;
import com.tencent.tcgsdk.api.ScaleType;
import java.util.Locale;

/**
 * 端游-虚拟按键 示例演示
 * 演示如何启动端游以及使用自定义虚拟手柄以及虚拟键盘
 */
public class PcGamePadActivity extends AppCompatActivity {
    private final static String TAG = "PcGamePadActivity";

    // 我们把虚拟手柄、虚拟按键、自定义虚拟按键添加到这一层
    protected ControlView mControlView;
    // 云端游SDK调用接口
    protected IPcTcgSdk mSDK;
    // 显示端游的视图
    protected PcSurfaceGameView mGameView;
    protected GameViewModel mViewModel;

    // 悬浮菜单
    private FloatingSettingBarView mSettingBarView;
    private ImageView mLoadingView;
    private LinearLayout gamepadView;

    private AlertDialog gameStartErrorDialog;

    //是否显示调试信息
    private boolean isDebugMode;
    private String mUserId;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initWindow();
        initView();
        initSdk();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        if (mSDK != null) {
            mSDK.setVolume(1);
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
        if (mSDK != null) {
            mSDK.setVolume(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        CloudGameApi.getInstance().stopGame(null);
    }

    private void init() {
        Log.d(TAG, "init: ");
        mUserId = CommonUtil.getIdentity(this);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        CloudGameApi.getInstance().init(mRequestQueue, mUserId);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);

        mViewModel.getKeyboard().observe(this, result -> {
            mControlView.keyboard(result);
        });

        mViewModel.getCustomGamePad().observe(this, result -> {
            mControlView.editCustomGamePad(result);
        });
    }

    private void initWindow() {
        Log.d(TAG, "initWindow: ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 初始UI
    private void initView() {
        setContentView(R.layout.gamepad_sample_layout);
        mGameView = findViewById(R.id.game_view);
        mSettingBarView = new FloatingSettingBarView(findViewById(R.id.game_setting));
        mLoadingView = findViewById(R.id.loading);
        mControlView = findViewById(R.id.control_view);
        gamepadView = findViewById(R.id.gamepad);
        mSettingBarView.setEventListener(mSettingsListener);
        initStartGameErrorDialog();

        ((CheckBox) (findViewById(R.id.keyboard_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getKeyboard().setValue(result);
        });

        ((CheckBox) (findViewById(R.id.custom_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getCustomGamePad().setValue(result);
            // 云端接入手柄(需要确保在onConnectionSuccess回调之后调用)
            mSDK.sendGamePadConnected();
        });

        // 将触摸事件传递给GameView
        mControlView.setGamePadTouchDelegate(new IGamepadTouchDelegate() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGameView.handleMotion(motionEvent);
                return true;
            }
        });
    }
    private void initStartGameErrorDialog() {
        Log.i(TAG, "initStartGameFailedDialog: ");
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("游戏启动失败");
        builder.setCancelable(false);
        builder.setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "exit game");
                finish();
            }
        });
        gameStartErrorDialog = builder.create();
    }

    /**
     * 初始化SDK
     */
    private void initSdk() {
        Log.i(TAG, "initSdk");
        // 创建Builder
        PcTcgSdk.Builder builder = new PcTcgSdk.Builder(
                this,
                Constant.APP_ID,
                mTcgLifeCycleImpl, // 生命周期回调
                mGameView);

        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        mSDK.registerStatsListener(mStatsListener);
    }

    /**
     * 开始请求业务后台启动游戏，获取服务端server session
     *
     * 注意：客户在接入时需要请求自己的业务后台，获取ServerSession
     * 业务后台实现请参考API：https://cloud.tencent.com/document/product/1162/40740
     *
     * @param clientSession sdk初始化成功后返回的client session
     */
    protected void startGame(String clientSession) {
        Log.i(TAG, "start game");
        // 通过业务后台来启动游戏
        CloudGameApi.getInstance().startGame(Constant.PC_GAME_ID, clientSession, new IServerResponseListener() {
            @Override
            public void onFailed(String msg) {
                Log.e(TAG, msg);
                if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                    gameStartErrorDialog.setMessage("请求服务器失败，请稍后再试～");
                    gameStartErrorDialog.show();
                }
            }

            @Override
            public void onSuccess(ResponseResult response) {
                StartGameResponseResult result = (StartGameResponseResult) response;
                TLog.i(TAG, "Response Success: " + result.toString());
                if (result.sessionDescribe != null) {
                    mSDK.start(result.sessionDescribe.serverSession);
                }
            }
        });
    }


    private final SettingEventListener mSettingsListener = new SettingEventListener() {
        @Override
        public void onClickDebug() {
            Log.d(TAG, "onClickDebug: isDebugMode=" + isDebugMode);
            if (mGameView == null) {
                return;
            }
            isDebugMode = !isDebugMode;
            mGameView.enableDebugView(isDebugMode);
        }

        @Override
        public void onExit() {
            Log.d(TAG, "onExit: ");
            finish();
        }
    };

    private final IStatsListener mStatsListener = new IStatsListener() {
        @Override
        public void onStats(PerfValue perfValue, String s, String s1, String s2) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSettingBarView.setFps(perfValue.fps);
                    mSettingBarView.setNetworkInfo((int) perfValue.rtt);
                }
            });
        }
    };

    /**
     * TcgSdk生命周期回调
     */
    private final ITcgListener mTcgLifeCycleImpl = new ITcgListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
            Log.e(TAG, "onConnectionTimeout");
            if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                gameStartErrorDialog.setMessage("服务器连接超时，请稍后再试～");
                gameStartErrorDialog.show();
            }
        }

        @Override
        public void onInitSuccess(String clientSession) {
            // 初始化成功，在此处请求业务后台
            Log.d(TAG, "onInitSuccess: ");
            startGame(clientSession);
        }

        @Override
        public void onInitFailure(int errorCode) {
            // 初始化失败, 用户无法使用, 只能退出
            Log.e(TAG, String.format(Locale.ENGLISH, "onInitFailure:%d", errorCode));
            if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                gameStartErrorDialog.setMessage("初始化失败，请稍后再试～");
                gameStartErrorDialog.show();
            }
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            // 云游戏连接失败
            Log.e(TAG, String.format(Locale.ENGLISH, "onConnectionFailure:%d %s", errorCode, errorMsg));
            if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                gameStartErrorDialog.setMessage("连接服务器失败，请稍后再试～");
                gameStartErrorDialog.show();
            }
        }

        @Override
        public void onConnectionSuccess() {
            // 云游戏连接成功, 所有SDK的设置必须在这个回调之后进行

            // 云端相关设置需要在连接成功之后才生效
            // 设置鼠标样式为HUGE
            mSDK.setCursorStyle(CursorStyle.HUGE, null);

            // 设置鼠标模式, 具体请见API文档
            mGameView.setCursorType(CursorType.RELATIVE_TOUCH);
            mGameView.setTouchClickKey(CursorType.TouchClickKey.MOUSE_LEFT);

            // 填充画面
            mGameView.setScaleType(ScaleType.ASPECT_FILL);
        }

        @Override
        public void onDrawFirstFrame() {
            // 游戏画面首帧回调
            Log.d(TAG, "onDrawFirstFrame: ");
            mSettingBarView.setViewShow(true);
            mLoadingView.setVisibility(View.GONE);
            gamepadView.setVisibility(View.VISIBLE);
        }
    };
}
