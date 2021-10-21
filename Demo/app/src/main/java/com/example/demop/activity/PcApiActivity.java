package com.example.demop.activity;

import static com.tencent.tcgsdk.api.BitrateUnit.KB;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.demop.Constant;
import com.example.demop.R;
import com.example.demop.model.GameViewModel;
import com.example.demop.server.CloudGameApi;
import com.example.demop.server.param.ServerResponse;
import com.tencent.tcgsdk.api.CursorStyle;
import com.tencent.tcgsdk.api.CursorType;
import com.tencent.tcgsdk.api.IPcTcgSdk;
import com.tencent.tcgsdk.api.ITcgListener;
import com.tencent.tcgsdk.api.LogLevel;
import com.tencent.tcgsdk.api.PcSurfaceGameView;
import com.tencent.tcgsdk.api.PcTcgSdk;
import com.tencent.tcgsdk.api.ScaleType;
import com.tencent.tcgsdk.api.datachannel.IDataChannel;
import com.tencent.tcgsdk.api.datachannel.IDataChannelListener;
import com.tencent.tcgsdk.api.datachannel.IStatusChangeListener;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 端游-API调用
 * 演示如何调用端游SDK接口, 调用接口的示例包括
 * 1.初始化SDK
 * 2.鼠标左键点击: clickMouseLeft
 * 3.发送按键(示例是回车键): keyEnter
 * 4.暂停服务端推流: pause
 * 5.恢复服务端推流: resume
 * 6.关闭游戏视图(可能在退出界面时会用到): closeGameView
 * 7.恢复游戏视图: newGameView
 * 8.外界鼠标操作(捕获鼠标): capturePointer
 * 9.切换码率: changeBitrate
 * 10.自定义数据通道: testDataChannel
 */
public class PcApiActivity extends AppCompatActivity {

    private final static String TAG = "PcApiActivity";

    // 码率质量
    private static final int LOW_QUALITY = 1;
    private static final int MEDIUM_QUALITY = 2;
    private static final int HIGH_QUALITY = 3;

    // 业务后台交互的API
    private CloudGameApi mCloudGameApi;
    // 端游游戏视图
    protected PcSurfaceGameView mGameView;
    // 云端游SDK调用接口
    protected IPcTcgSdk mSDK;
    protected GameViewModel mViewModel;
    protected FrameLayout mContainer;

    // 当前码率质量，初始值为0
    // 可选质量 LOW_QUALITY（1），MEDIUM_QUALITY（2），HIGH_QUALITY（3）
    private int mCurrentBitrate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        init();
        initView();
        initSdk();
        initApiView();
    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void init() {
        mCloudGameApi = new CloudGameApi(this);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GameViewModel.class);

        mViewModel.getDebugView().observe(this, result -> mGameView.enableDebugView(result));
    }

    private void initView() {
        setContentView(R.layout.pc_sample_layout);
        mContainer = findViewById(R.id.container);

        //　创建游戏画面视图
        mGameView = new PcSurfaceGameView(this);
        mGameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.addView(mGameView);
    }

    protected void initApiView() {
        View apiEntries = LayoutInflater.from(this).inflate(R.layout.api_sample_layout, null);
        ((CheckBox) (apiEntries.findViewById(R.id.debug_view))).
                setOnCheckedChangeListener((var1, result) -> mViewModel.getDebugView().setValue(result));

        mContainer.addView(apiEntries);
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
        // 请求业务后台来启动游戏
        mCloudGameApi.startGame(Constant.PC_GAME_ID, clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(ServerResponse resp) {
                Log.d(TAG, "onSuccess: " + resp.toString());
                if (resp.code == 0) {
                    //　请求成功，获取服务端的server session，启动游戏
                    mSDK.start(resp.data.serverSession);
                } else {
                    Toast.makeText(PcApiActivity.this, resp.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.i(TAG, msg);
            }
        });
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
            startGame(clientSession);
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
        PcTcgSdk.Builder builder = new PcTcgSdk.Builder(
                this,
                Constant.APP_ID,
                mTcgLifeCycleImpl, // 生命周期回调
                mGameView);
        // 设置日志级别
        builder.logLevel(LogLevel.VERBOSE);

        // 通过Builder创建SDK接口实例
        mSDK = builder.build();
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
            mGameView = new PcSurfaceGameView(this);
            mContainer.addView(mGameView, 0);
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

    // 切换码率
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

    // 数据通道
    private IDataChannel mDataChannel;

    // 这部分代码演示如何在云端创建udp端口, 并收发数据
    // 由于用的是UDP, 需要客户端自行保证传输的可靠性
    // NOTE: 该能力需要云端应用能够支持
    private void testDataChannel() {
        final int REMOTE_UDP_PORT = 6666;

        // 云端创建udp端口6666
        mDataChannel = mSDK.createDataChannel(REMOTE_UDP_PORT, new IDataChannelListener() {
            @Override
            public void onMessage(int i, ByteBuffer byteBuffer) {
                Charset charset = Charset.forName("utf-8");
                String data2beRead = charset.decode(byteBuffer).toString();
                Log.d(TAG, "receive data:" + data2beRead);
            }

            @Override
            public void onConnectFailed(int i, String msg) {
                // 数据通道创建失败, 或者数据通道出现异常
                Log.d(TAG, "DataChannel failure:" + msg);
            }

            @Override
            public void onConnectSuccess(int i) {
                // 云端端口创建成功
                // 发送数据到云端
                sendData("Data to be sent.");
            }
        });
    }

    private void sendData(String data) {
        if (mDataChannel != null) {
            mDataChannel.send(ByteBuffer.wrap(data.getBytes()));
            Log.d(TAG, "send data:" + data);
        }
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
}