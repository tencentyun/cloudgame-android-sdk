package com.example.demop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.demop.expirtationcode.CloudGameApi;
import com.example.demop.expirtationcode.bean.ExperienceCodeParam;
import com.example.demop.expirtationcode.bean.ExperienceCodeResp;
import com.example.demop.expirtationcode.bean.StopGameParam;
import com.example.demop.view.ControlView;
import com.google.gson.Gson;
import com.tencent.tcgsdk.TLog;
import com.tencent.tcgsdk.api.CursorStyle;
import com.tencent.tcgsdk.api.CursorType;
import com.tencent.tcgsdk.api.GameView;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.ITcgSdk;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.TcgSdk2;
import com.tencent.tcgsdk.api.datachannel.IStatusChangeListener;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static long APP_ID = 1300679218;
    private GameView mGameView;
    private GameViewModel mViewModel;
    private ITcgSdk mSDK;
    private FrameLayout mContainer;
    private ControlView mControlView;

    // 云游戏团队提供的体验码, 有效期7天
    private static final String EXPIRATION_CODE = "D0JKLYRM";

    private String mUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initWindow();
        initView();
        initSdk();
        initControlView();
        initAPITestView();
    }

    private void init() {
        mUserID = Build.FINGERPRINT;
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);

        mViewModel.getDebugView().observe(this, result -> {
            mGameView.enableDebugView(result);
        });

        mViewModel.getGamePad().observe(this, result -> {
            mControlView.joyStick(result);
        });

        mViewModel.getKeyboard().observe(this, result -> {
            mControlView.keyboard(result);
        });
    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mContainer = findViewById(R.id.container);
    }

    private void initAPITestView() {
        View apiEntries = LayoutInflater.from(this).inflate(R.layout.api_entries, null);
        ((CheckBox)(apiEntries.findViewById(R.id.debug_view))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getDebugView().setValue(result);
        });
        ((CheckBox)(apiEntries.findViewById(R.id.gamepad))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getGamePad().setValue(result);
        });
        ((CheckBox)(apiEntries.findViewById(R.id.keyboard))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getKeyboard().setValue(result);
        });

        mContainer.addView(apiEntries);
    }

    private void initControlView() {
        mControlView = new ControlView(this);
        mControlView.setSDK(mSDK);
        mControlView.setOnVirtualControlListener(new ControlView.IVirtualControlListener() {
            @Override
            public void onKeyboard(boolean show) {
            }

            @Override
            public void onJoystick(boolean show) {
            }
        });
        mContainer.addView(mControlView);
    }

    private GameView createGameView() {
        GameView gameView = new GameView(this);
        gameView.setTouchClickKey(CursorType.TouchClickKey.MOUSE_LEFT);
        return gameView;
    }

    private void start(String clientSession) {
        // 对接TcgSdk的客户端请注意
        // 以下请求ServerSession的后端服务是云游团队的体验服务, 客户端接入时需要在自己的业务后台返回ServerSession
        // 业务后台的API请参考:
        // https://cloud.tencent.com/document/product/1162/40740
        CloudGameApi cloudApi = new CloudGameApi(this);
        cloudApi.startExperience(new ExperienceCodeParam(EXPIRATION_CODE, clientSession, mUserID), new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(String result) {
                ExperienceCodeResp resp = new Gson().fromJson(result, ExperienceCodeResp.class);
                if (resp.Code == 0) {
                    //　启动游戏
                    mSDK.start(resp.ServerSession);
                } else {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String msg) {
            }
        });
    }

    private void stop() {
        CloudGameApi cloudApi = new CloudGameApi(this);
        cloudApi.stopExperience(new StopGameParam(EXPIRATION_CODE, mUserID));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    // TcgSdk生命周期回调
    private final ITcgListener mTcgLifeCycleImpl = new ITcgListener() {
        @Override
        public void onConnectionTimeout() {
            // 云游戏连接超时, 用户无法使用, 只能退出
        }

        @Override
        public void onInitSuccess(String clientSession) {
            // 初始化成功
            start(clientSession);
        }

        @Override
        public void onInitFailure(int errorCode) {
            // 初始化失败, 用户无法使用, 只能退出
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            // 云游戏连接失败
        }

        @Override
        public void onConnectionSuccess() {
            // 云游戏连接成功, 所有SDK的设置必须在这个回调之后进行

            // 设置鼠标样式为HUGE
            mSDK.setCursorStyle(CursorStyle.HUGE, null);
            // 设置鼠标模式, 具体请见API文档
            mGameView.setCursorType(CursorType.RELATIVE_TOUCH);
            // 填充画面
            mGameView.setScaleType(ScaleType.ASPECT_FILL);
        }

        @Override
        public void onDrawFirstFrame() {
            // 游戏画面首帧回调
        }
    };

    private void initSdk() {
        //　创建游戏画面视图
        mGameView = createGameView();
        mGameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.addView(mGameView);

        // 创建SDK的接口
        TcgSdk2.Builder builder = new TcgSdk2.Builder(this.getApplicationContext(), APP_ID, mTcgLifeCycleImpl , mGameView.getSurfaceRenderer());
        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);
    }

    //　鼠标左键点击
    public void clickMouseLeft(View view) {
//        mSDK.sendMouseLeft(true);
//        mSDK.sendMouseLeft(false);
        testDataChannel();
    }

    // 停止云端画面传输
    public void pause(View view) {
        mSDK.pause(null);
    }

    // 恢复云端画面传输
    public void resume(View view) {
        mSDK.resume(null);
    }

    // 重新创建游戏视图
    public void newGameView(View view) {
        int index = mContainer.indexOfChild(mGameView);
        mContainer.removeView(mGameView);
        mSDK.replaceRenderer(null);

        mContainer.postDelayed(() -> {
            mGameView = createGameView();
            mContainer.addView(mGameView, index);
            mGameView.setSDK(mSDK);
            mSDK.replaceRenderer(mGameView);
        }, 5000);
    }

    // 这部分代码演示如何在云端创建udp端口, 并收发数据
    // 由于用的是UDP, 需要客户端自行保证传输的可靠性
    private void testDataChannel() {
        final int REMOTE_UDP_PORT = 6666;

        // 云端创建udp端口6666
        mSDK.connect(REMOTE_UDP_PORT, new IStatusChangeListener() {
            @Override
            public void onFailed(String msg) {
                // 数据通道创建失败, 或者数据通道出现异常
                // 请注意: 创建成功回调之后并不意味着不会出现异常, 如运行过程中网络连接断开仍会回调该函数
                Log.d(TAG, "DataChannel failure:" + msg);
            }

            @Override
            public void onTimeout() {

            }

            @Override
            public void onSuccess() {
                // 云端端口创建成功
                // 发送数据到云端
                mSDK.send(REMOTE_UDP_PORT, "Data to be sent.".getBytes());
                TLog.d(TAG, "send data for " + REMOTE_UDP_PORT);
            }
        });

        // 监听云端返回数据
        mSDK.listen(REMOTE_UDP_PORT, (buffer) -> {
            Charset charset = Charset.forName("utf-8");
            String data2beRead = charset.decode(buffer.data).toString();
            TLog.d(TAG, "receive data:" + data2beRead);
        });
    }
}