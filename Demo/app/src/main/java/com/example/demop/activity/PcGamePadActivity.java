package com.example.demop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.model.GameViewModel;
import com.example.demop.server.CloudGameApi;
import com.example.demop.server.param.ServerResponse;
import com.example.demop.view.ControlView;
import com.google.gson.Gson;
import com.tencent.tcgsdk.api.CursorStyle;
import com.tencent.tcgsdk.api.CursorType;
import com.tencent.tcgsdk.api.IPcTcgSdk;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.PcSurfaceGameView;
import com.tencent.tcgsdk.api.PcTcgSdk;
import com.tencent.tcgsdk.api.ScaleType;
import java.util.Locale;
import org.json.JSONObject;

/**
 * 端游-虚拟按键 示例演示
 * 演示如何启动端游以及使用自定义虚拟手柄以及虚拟键盘
 */
public class PcGamePadActivity extends AppCompatActivity {
    private final static String TAG = "PcGamePadActivity";

    // 业务后台API
    private CloudGameApi mCloudGameApi;
    // 我们把虚拟手柄、虚拟按键、自定义虚拟按键添加到这一层
    protected ControlView mControlView;
    // 云端游SDK调用接口
    protected IPcTcgSdk mSDK;
    // 显示端游的视图
    protected PcSurfaceGameView mGameView;
    protected GameViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        initWindow();
        init();
        initView();
        initSdk();
    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void init() {
        mCloudGameApi = new CloudGameApi(this);

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);

        mViewModel.getKeyboard().observe(this, result -> {
            mControlView.keyboard(result);
        });

        mViewModel.getCustomGamePad().observe(this, result -> {
            mControlView.editCustomGamePad(result);
        });
    }

    // 初始化SDK
    private void initSdk() {
        Log.i(TAG, "initSdk");
        // 创建SDK的接口
        PcTcgSdk.Builder builder = new PcTcgSdk.Builder(
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

        // 给控制视图设置SDK实例
        mControlView.setSDK(mSDK);
    }

    // 初始UI
    private void initView() {
        setContentView(R.layout.ui_sample_layout);
        mGameView = findViewById(R.id.game_view);

        ((CheckBox) (findViewById(R.id.keyboard_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getKeyboard().setValue(result);
        });
        ((CheckBox) (findViewById(R.id.custom_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getCustomGamePad().setValue(result);
        });

        mControlView = findViewById(R.id.control_view);
    }

    // TcgSdk生命周期回调
    private final ITcgListener mTcgLifeCycleImpl = new ITcgListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
            Log.e(TAG, "onConnectionTimeout");
        }

        @Override
        public void onInitSuccess(String clientSession) {
            // 初始化成功
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
        }
    };

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
        mCloudGameApi.startGame(Constant.PC_GAME_CODE, clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(ServerResponse resp) {
                Log.d(TAG, "onSuccess: " + resp.toString());
                if (resp.code == 0) {
                    // 请求成功，从服务端获取到server session，启动游戏
                    mSDK.start(resp.serverSession);
                } else {
                    Toast.makeText(PcGamePadActivity.this, resp.toString(), Toast.LENGTH_LONG).show();
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
