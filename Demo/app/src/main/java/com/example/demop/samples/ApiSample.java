package com.example.demop.samples;

import static com.tencent.tcgsdk.api.BitrateUnit.KB;

import android.os.Build;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import com.example.demop.BaseActivity;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.model.GameViewModel;
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

public class ApiSample extends BaseActivity {
    private static final int LOW_QUALITY = 1;
    private static final int MEDIUM_QUALITY = 2;
    private static final int HIGH_QUALITY = 3;
    protected GameView mGameView;
    protected GameViewModel mViewModel;
    protected ITcgSdk mSDK;
    protected FrameLayout mContainer;
    private int mCurrentBitrate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initSdk();
        initApiView();
    }

    protected void init() {
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);

        mViewModel.getDebugView().observe(this, result -> {
            mGameView.enableDebugView(result);
        });
    }

    private void initView() {
        setContentView(R.layout.pc_sample_layout);
        mContainer = findViewById(R.id.container);

        //　创建游戏画面视图
        mGameView = new GameView(this);
        mGameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.addView(mGameView);
    }

    protected void initApiView() {
        View apiEntries = LayoutInflater.from(this).inflate(R.layout.api_sample_layout, null);
        ((CheckBox)(apiEntries.findViewById(R.id.debug_view))).setOnCheckedChangeListener((var1, result) -> {
            mViewModel.getDebugView().setValue(result);
        });

        mContainer.addView(apiEntries);
    }

    /**
     * 启动云游戏, 云端实例启动成功后会回调onStartExperience
     *
     * @param clientSession 用于云端初始化的client session
     * @see ApiSample#onStartExperience(String)
     */
    protected void start(String clientSession) {
        // 以下请求ServerSession的后端支持是云游团队的体验服务
        // 客户端接入时需要在自己的业务后台返回ServerSession
        // 业务后台的API请参考:
        // https://cloud.tencent.com/document/product/1162/40740
        super.startExperience(Constant.PC_EXPIRATION_CODE, clientSession);
    }

    @Override
    public void onStartExperience(String serverSession) {
        //　启动游戏
        mSDK.start(serverSession);
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

    private void initSdk() {
        // 创建SDK的接口
        TcgSdk2.Builder builder = new TcgSdk2.Builder(this.getApplicationContext(), Constant.APP_ID, mTcgLifeCycleImpl , mGameView.getSurfaceRenderer());
        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();

        // 给游戏视图设置SDK实例
        mGameView.setSDK(mSDK);
    }

    //　鼠标左键点击
    public void clickMouseLeft(View view) {
        mSDK.sendMouseLeft(true);
        mSDK.sendMouseLeft(false);
    }

    // 回车键键值
    // 其他按键值请通过该网址查询: https://keycode.info/
    final int ENTER_KEY_CODE = 13;

    // 发送回车键
    public void keyEnter(View view) {
        mSDK.sendKeyboardEvent(ENTER_KEY_CODE, true, null);
        mSDK.sendKeyboardEvent(ENTER_KEY_CODE, false, null);
    }

    // 停止云端画面传输
    public void pause(View view) {
        mSDK.pause(null);
    }

    // 恢复云端画面传输
    public void resume(View view) {
        mSDK.resume(null);
    }

    // 关闭当前游戏视图
    public void closeGameView(View view) {
        if (mGameView != null) {
            // 从View树上移除GameView
            mContainer.removeView(mGameView);

            // 释放Native层对象
            mGameView.release();
            mSDK.replaceRenderer(null);

            mGameView = null;
        }
    }

    // 重新创建游戏视图
    public void newGameView(View view) {
        if (mGameView == null) {
            mGameView = new GameView(this);
            mContainer.addView(mGameView, 0);
            mGameView.setSDK(mSDK);
            mSDK.replaceRenderer(mGameView);
        }
    }

    // 捕获鼠标
    // 参考 使用指针捕获： https://developer.android.com/training/gestures/movement?hl=zh-cn
    public void capturePointer(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 仅鼠标模式为TOUCH模式时支持捕获鼠标
            mGameView.setCursorType(CursorType.TOUCH);
            // 捕获鼠标
            if (mGameView != null) {
                mGameView.requestPointerCapture();
            }
        } else {
            Toast.makeText(this, "仅Android O以上版本支持鼠标操作", Toast.LENGTH_LONG).show();
        }
    }

    public void changeBitrate(View view) {
        int bitrateType = nextBitrate();
        switch (bitrateType) {
            case LOW_QUALITY:
                Toast.makeText(this, "码率2-3M", Toast.LENGTH_LONG).show();
                mSDK.setStreamProfile(60, 2000, 3000, KB, null);
                break;
            case MEDIUM_QUALITY:
                Toast.makeText(this, "码率4-7M", Toast.LENGTH_LONG).show();
                mSDK.setStreamProfile(60, 4000, 7000, KB, null);
                break;
            case HIGH_QUALITY:
                Toast.makeText(this, "码率8-12M", Toast.LENGTH_LONG).show();
                mSDK.setStreamProfile(60, 8000, 12000, KB, null);
                break;
            default:
        }
    }

    private int nextBitrate() {
        mCurrentBitrate++;
        if (mCurrentBitrate > HIGH_QUALITY) {
            mCurrentBitrate = LOW_QUALITY;
        }
        return mCurrentBitrate;
    }

    // 这部分代码演示如何在云端创建udp端口, 并收发数据
    // 由于用的是UDP, 需要客户端自行保证传输的可靠性
    // NOTE: 该能力需要云端应用能够支持
    private void testDataChannel() {
        final int REMOTE_UDP_PORT = 6666;

        // 云端创建udp端口6666
        mSDK.connect(REMOTE_UDP_PORT, new IStatusChangeListener() {
            @Override
            public void onFailed(String msg) {
                // 数据通道创建失败, 或者数据通道出现异常
                // 请注意: 创建成功回调之后并不意味着不会出现异常, 如运行过程中网络连接断开仍会回调该函数
                Log.d(Constant.TAG, "DataChannel failure:" + msg);
            }

            @Override
            public void onTimeout() {

            }

            @Override
            public void onSuccess() {
                // 云端端口创建成功
                // 发送数据到云端
                mSDK.send(REMOTE_UDP_PORT, "Data to be sent.".getBytes());
                TLog.d(Constant.TAG, "send data for " + REMOTE_UDP_PORT);
            }
        });

        // 监听云端返回数据
        mSDK.listen(REMOTE_UDP_PORT, (buffer) -> {
            Charset charset = Charset.forName("utf-8");
            String data2beRead = charset.decode(buffer.data).toString();
            TLog.d(Constant.TAG, "receive data:" + data2beRead);
        });
    }
}