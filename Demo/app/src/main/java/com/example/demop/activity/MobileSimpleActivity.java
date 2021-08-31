package com.example.demop.activity;

import static com.example.demop.Constant.APP_ID;

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
import com.google.gson.Gson;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.mobile.Configuration;
import com.tencent.tcgsdk.api.mobile.ITcgMobileListener;
import com.tencent.tcgsdk.api.mobile.ITcgSdk;
import com.tencent.tcgsdk.api.mobile.MobileSurfaceView;
import com.tencent.tcgsdk.api.mobile.MobileTcgSdk;
import java.util.Locale;
import org.json.JSONObject;

/**
 * 端游示例演示: 如何简单地启动手游
 */
public class MobileSimpleActivity extends AppCompatActivity {
    private MobileSurfaceView mGameView;
    private ITcgSdk mSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            startGame(clientSession);
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
     * 开始游戏: 获取服务端server session
     *
     * 请注意: 请求的后台服务是云游团队的体验服务
     * 客户端接入时需要在自己的业务后台返回ServerSession
     *
     * 业务后台的API请参考:
     * https://cloud.tencent.com/document/product/1162/40740
     *
     * @param clientSession sdk初始化成功后返回的client session
     */
    protected void startGame(String clientSession) {
        Log.i(Constant.TAG, "start game");
        CloudGameApi cloudGameApi = new CloudGameApi(this);
        cloudGameApi.startGame(Constant.MOBILE_GAME_CODE, clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(Constant.TAG, "onSuccess: " + result.toString());
                ServerResponse resp = new Gson().fromJson(result.toString(), ServerResponse.class);
                if (resp.code == 0) {
                    //　启动游戏
                    mSDK.start(resp.serverSession);
                } else {
                    Toast.makeText(MobileSimpleActivity.this, resp.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.i(Constant.TAG, msg);
            }
        });
    }
}
