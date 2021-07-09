package com.example.demop.samples;

import static com.example.demop.Constant.APP_ID;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import com.example.demop.BaseActivity;
import com.example.demop.Constant;
import com.example.demop.R;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.mobile.Configuration;
import com.tencent.tcgsdk.api.mobile.ITcgMobileListener;
import com.tencent.tcgsdk.api.mobile.ITcgSdk;
import com.tencent.tcgsdk.api.mobile.MobileSurfaceView;
import com.tencent.tcgsdk.api.mobile.MobileTcgSdk;
import java.util.Locale;

public class MobileSample extends BaseActivity {
    private MobileSurfaceView mGameView;
    private ITcgSdk mSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initSdk();
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
        Log.i(Constant.TAG, "initSdk");
        // 创建Builder
        MobileTcgSdk.Builder builder = new MobileTcgSdk.Builder(
                this.getApplicationContext(),
                APP_ID,
                mTcgLifeCycleImpl,
                mGameView.getViewRenderer());

        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);

        mGameView.setScaleType(ScaleType.ASPECT_FILL);
    }


    /**
     * TcgSdk生命周期回调
     */
    private final ITcgMobileListener mTcgLifeCycleImpl = new ITcgMobileListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
            Log.e(Constant.TAG, "onConnectionTimeout");
        }

        @Override
        public void onInitSuccess(String clientSession) {
            // 初始化成功
            start(clientSession);
        }

        @Override
        public void onInitFailure(int errorCode) {
            // 初始化失败, 用户无法使用, 只能退出
            Log.e(Constant.TAG, String.format(Locale.ENGLISH, "onInitFailure:%d", errorCode));
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            // 云游戏连接失败
            Log.e(Constant.TAG, String.format(Locale.ENGLISH, "onConnectionFailure:%d %s", errorCode, errorMsg));
        }

        @Override
        public void onConnectionSuccess() {
            // 云游戏连接成功, 所有SDK的设置必须在这个回调之后进行
        }

        @Override
        public void onDrawFirstFrame() {
            // 游戏画面首帧回调
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            // 云端屏幕旋转时, 客户端需要同步旋转屏幕并固定下来
            Log.e(Constant.TAG, "onConfigurationChanged:" + newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    };

    /**
     * 启动云游戏, 云端实例启动成功后会回调onStartExperience
     *
     * @param clientSession 用于云端初始化的client session
     * @see SimpleSample#onStartExperience(String)
     */
    protected void start(String clientSession) {
        Log.i(Constant.TAG, "start client Session");
        // 以下请求ServerSession的后端支持是云游团队的体验服务
        // 客户端接入时需要在自己的业务后台返回ServerSession
        // 业务后台的API请参考:
        // https://cloud.tencent.com/document/product/1162/40740
        super.startExperience(Constant.MOBILE_EXPIRATION_CODE, clientSession);
    }

    @Override
    public void onStartExperience(String serverSession) {
        //　启动游戏
        mSDK.start(serverSession);
    }
}
