package com.example.demop.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.server.CloudGameApi;
import com.example.demop.server.param.ServerResponse;
import com.example.demop.view.FloatingSettingBarView;
import com.example.demop.view.FloatingSettingBarView.SettingEventListener;
import com.tencent.tcgsdk.TLog;
import com.tencent.tcgsdk.api.IStatsListener;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.PerfValue;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.VideoRotation;
import com.tencent.tcgsdk.api.mobile.Configuration;
import com.tencent.tcgsdk.api.mobile.IMobileTcgSdk;
import com.tencent.tcgsdk.api.mobile.ITcgMobileListener;
import com.tencent.tcgsdk.api.mobile.MobileSurfaceView;
import com.tencent.tcgsdk.api.mobile.MobileTcgSdk;
import java.util.Locale;

/**
 * 手游-简单示例
 * 展示内容：如何快速启动手游
 */
public class MobileSimpleActivity extends AppCompatActivity {
    private final static String TAG = "MobileSimpleActivity";
    // 手游视图
    private MobileSurfaceView mGameView;
    // 云手游SDK调用接口
    private IMobileTcgSdk mSDK;
    // 业务后台交互的API
    private CloudGameApi mCloudGameApi;
    // 悬浮菜单
    private FloatingSettingBarView mSettingBarView;
    private ImageView mLoadingView;

    private AlertDialog gameStartErrorDialog;

    //是否显示调试信息
    private boolean isDebugMode;

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
        mCloudGameApi.stopGame();
    }

    @Override
    public void onBackPressed() {
        TLog.i(TAG, "onBackPressed: ");
        if (mSDK == null) {
            super.onBackPressed();
        } else {
            mSDK.sendKey(IMobileTcgSdk.KEY_BACK, true);
            mSDK.sendKey(IMobileTcgSdk.KEY_BACK, false);
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mGameView == null) {
            return;
        }

        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            mGameView.setVideoRotation(VideoRotation.ROTATION_0);
        } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            mGameView.setVideoRotation(VideoRotation.ROTATION_270);
        } else {
            Log.w(TAG, "There is no rotation for unexpected orientation:" + newConfig.orientation);
        }
    }

    private void init() {
        Log.d(TAG, "init: ");
        mCloudGameApi = new CloudGameApi(this);
    }

    private void initWindow() {
        Log.d(TAG, "initWindow: ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 创建游戏画面视图
     */
    private void initView() {
        Log.d(TAG, "initView: ");
        setContentView(R.layout.mobile_sample_layout);
        mGameView = findViewById(R.id.game_view);
        mSettingBarView = new FloatingSettingBarView(findViewById(R.id.game_setting));
        mLoadingView = findViewById(R.id.loading);
        mSettingBarView.setEventListener(mSettingsListener);
        initStartGameErrorDialog();
    }

    private void initStartGameErrorDialog() {
        TLog.i(TAG, "initStartGameFailedDialog: ");
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
        MobileTcgSdk.Builder builder = new MobileTcgSdk.Builder(
                this,
                Constant.APP_ID,
                mTcgLifeCycleImpl, // 生命周期回调
                mGameView);

        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        mSDK.registerStatsListener(mStatsListener);

        // 让画面和视图一样大,画面可能被拉伸
        mGameView.setScaleType(ScaleType.ASPECT_FILL);
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
        mCloudGameApi.startGame(Constant.MOBILE_GAME_ID, clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(ServerResponse resp) {
                if (resp.code == 0) {
                    Log.d(TAG, "Response Success: " + resp.toString());
                    //　请求成功，从服务端获取到server session，启动游戏
                    mSDK.start(resp.data.serverSession);
                } else {
                    Log.e(TAG, "Response Failed: " + resp.toString());
                    if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                        gameStartErrorDialog.setMessage("云端无空闲实例，请稍后再试～");
                        gameStartErrorDialog.show();
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, msg);
                if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                    gameStartErrorDialog.setMessage("请求服务器失败,请稍后再试～" + msg);
                    gameStartErrorDialog.show();
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
    private final ITcgMobileListener mTcgLifeCycleImpl = new ITcgMobileListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
            Log.e(TAG, "onConnectionTimeout");
            if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                gameStartErrorDialog.setMessage("服务器连接超时,请稍后再试～");
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
                gameStartErrorDialog.setMessage("初始化失败,请稍后再试～");
                gameStartErrorDialog.show();
            }
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            // 云游戏连接失败
            Log.e(TAG, String.format(Locale.ENGLISH, "onConnectionFailure:%d %s", errorCode, errorMsg));
            if (!gameStartErrorDialog.isShowing() && !isFinishing()) {
                gameStartErrorDialog.setMessage("连接服务器失败,请稍后再试～");
                gameStartErrorDialog.show();
            }
        }

        @Override
        public void onConnectionSuccess() {
            // 云游戏连接成功, 所有SDK的设置必须在这个回调之后进行
            Log.d(TAG, "onConnectionSuccess: ");
        }

        @Override
        public void onDrawFirstFrame() {
            // 游戏画面首帧回调
            Log.d(TAG, "onDrawFirstFrame: ");
            mSettingBarView.setViewShow(true);
            mLoadingView.setVisibility(View.GONE);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            // 云端屏幕旋转时, 客户端需要同步旋转屏幕并固定下来
            Log.d(TAG, "onConfigurationChanged:" + newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    };
}
