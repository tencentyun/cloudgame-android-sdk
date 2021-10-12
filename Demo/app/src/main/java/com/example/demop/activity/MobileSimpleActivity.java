package com.example.demop.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.server.CloudGameApi;
import com.example.demop.server.param.ServerResponse;
import com.tencent.tcgsdk.api.LogLevel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCloudGameApi = new CloudGameApi(this);
        initWindow();
        initView();
        initSdk();
    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 创建游戏画面视图
     */
    private void initView() {
        setContentView(R.layout.mobile_sample_layout);
        mGameView = findViewById(R.id.game_view);
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
                mGameView.getViewRenderer());

        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);

        // 让画面和视图一样大,画面可能被拉伸
        mGameView.setScaleType(ScaleType.ASPECT_FILL);
    }


    /**
     * TcgSdk生命周期回调
     */
    private final ITcgMobileListener mTcgLifeCycleImpl = new ITcgMobileListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
            Log.e(TAG, "onConnectionTimeout");
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
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            // 云游戏连接失败
            Log.e(TAG, String.format(Locale.ENGLISH, "onConnectionFailure:%d %s", errorCode, errorMsg));
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
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            // 云端屏幕旋转时, 客户端需要同步旋转屏幕并固定下来
            Log.e(TAG, "onConfigurationChanged:" + newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    };

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
        mCloudGameApi.startGame(Constant.MOBILE_GAME_CODE, clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(ServerResponse resp) {
                Log.d(TAG, "onSuccess: " + resp.toString());
                if (resp.code == 0) {
                    //　请求成功，从服务端获取到server session，启动游戏
                    mSDK.start(resp.serverSession);
                } else {
                    Toast.makeText(MobileSimpleActivity.this, resp.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.i(TAG, msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mCloudGameApi.stopGame();
    }
}
