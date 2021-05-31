package com.example.demop.samples;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import androidx.lifecycle.ViewModelProvider;
import com.example.demop.BaseActivity;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.model.GameViewModel;
import com.example.demop.view.ControlView;
import com.tencent.tcgsdk.api.CursorStyle;
import com.tencent.tcgsdk.api.CursorType;
import com.tencent.tcgsdk.api.GameView;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.ITcgSdk;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.TcgSdk2;

import java.util.Locale;

/**
 * 这个界面演示如何使用tcgui中的虚拟手柄、虚拟按键, 以及tcgui-gamepad中的自定义虚拟手柄
 */
public class UISample extends BaseActivity {
    // 我们把虚拟手柄、虚拟按键、自定义虚拟按键添加到这一层
    protected ControlView mControlView;
    // 云游交互的主要入口
    protected ITcgSdk mSDK;
    protected GameView mGameView;
    protected GameViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constant.TAG, "onCreate");
        init();
        initView();
        initSdk();
    }

    protected void init() {
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);
        mViewModel.getGamePad().observe(this, result -> {
            mControlView.joyStick(result);
        });

        mViewModel.getKeyboard().observe(this, result -> {
            mControlView.keyboard(result);
        });

        mViewModel.getCustomGamePad().observe(this, result -> {
            mControlView.editCustomGamePad(result);
        });
    }

    // 初始化SDK
    private void initSdk() {
        Log.i(Constant.TAG, "initSdk");
        // 创建SDK的接口
        TcgSdk2.Builder builder = new TcgSdk2.Builder(this.getApplicationContext(), Constant.APP_ID, mTcgLifeCycleImpl , mGameView.getSurfaceRenderer());
        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);
        mControlView.setSDK(mSDK);
    }

    // 初始UI
    private void initView() {
        setContentView(R.layout.ui_sample_layout);
        mGameView = findViewById(R.id.game_view);

        ((CheckBox)(findViewById(R.id.gamepad_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getGamePad().setValue(result);
        });
        ((CheckBox)(findViewById(R.id.keyboard_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getKeyboard().setValue(result);
        });
        ((CheckBox)(findViewById(R.id.custom_checkbx))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getCustomGamePad().setValue(result);
        });

        mControlView = findViewById(R.id.control_view);
    }

    // TcgSdk生命周期回调
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


    @Override
    public void onStartExperience(String serverSession) {
        mSDK.start(serverSession);
    }

    /**
     * 启动云游戏, 云端实例启动成功后会回调onStartExperience
     *
     * @param clientSession 用于云端初始化的client session
     * @see ApiSample#onStartExperience(String)
     */
    protected void start(String clientSession) {
        Log.i(Constant.TAG, "start client Session");
        // 以下请求ServerSession的后端支持是云游团队的体验服务
        // 客户端接入时需要在自己的业务后台返回ServerSession
        // 业务后台的API请参考:
        // https://cloud.tencent.com/document/product/1162/40740
        super.startExperience(clientSession);
    }
}
