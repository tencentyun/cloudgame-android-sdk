package com.example.demop.samples;

import android.os.Bundle;
import android.util.Log;

import com.example.demop.BaseActivity;
import com.example.demop.Constant;
import com.tencent.tcgsdk.api.GameView;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.ITcgSdk;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.TcgSdk2;

import java.util.Locale;

import static com.example.demop.Constant.APP_ID;

public class SimpleSample extends BaseActivity {
    private GameView mGameView;
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
        mGameView = new GameView(this);
        setContentView(mGameView);
    }

    /**
     * 初始化SDK
     */
    private void initSdk() {
        Log.i(Constant.TAG, "initSdk");
        // 创建Builder
        TcgSdk2.Builder builder = new TcgSdk2.Builder(
                this.getApplicationContext(),
                APP_ID,
                mTcgLifeCycleImpl,
                mGameView.getSurfaceRenderer());

        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);
    }


    /**
     * TcgSdk生命周期回调
     */
    private final ITcgListener mTcgLifeCycleImpl = new ITcgListener() {
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
        super.startExperience(clientSession);
    }

    @Override
    public void onStartExperience(String serverSession) {
        //　启动游戏
        mSDK.start(serverSession);
    }
}