package com.tencent.tcr.demo;

import android.util.Log;
import androidx.annotation.NonNull;
import com.tencent.tcr.demo.CloudGameApi.IServerSessionListener;
import com.tencent.tcr.demo.data.ServerResponse;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.CustomDataChannel.Observer;
import com.tencent.tcr.vr.XrBaseActivity;
import com.tencent.tcr.xr.api.TcrXrConfig.Builder;
import com.tencent.tcr.xr.api.TcrXrSdk;
import com.tencent.tcr.xr.api.bean.BaseInput;
import com.tencent.tcr.xr.api.bean.BoolInput;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XrActivity extends XrBaseActivity {
    private static final String TAG = "XrActivity";

    // 和云端进行交互的数据通道
    private CustomDataChannel mCustomDataChannel;

    @Override
    public void onInitSuccess(@NonNull String clientSession) {
        setLobbyText("初始化成功, 请求体验平台获取ServerSession...");
        CloudGameApi.getInstance().startGame(clientSession,
                new IServerSessionListener() {
                    @Override
                    public void onFailed(String msg) {
                        Log.e(TAG, "onFailed:" + msg);
                        setLobbyText("ERROR:" + msg);
                    }

                    @Override
                    public void onSuccess(ServerResponse resp) {
                        Log.e(TAG, "onSuccess:" + resp);
                        if (resp.code == 0) {
                            setLobbyText("获得ServerSession，启动会话...");
                            start(resp.serverSession);
                        } else {
                            setLobbyText("FAILED:" + resp.message);
                        }
                    }
                });
    }

    @Override
    public void onInitFailed() {
        setLobbyText("初始化失败");
    }

    @Override
    public void onUpdateConfigBuilder(@NonNull Builder configBuilder) {
        configBuilder
                .enableWideFov(PreferenceMgr.isEnableWideFov())
                .enableFFR(PreferenceMgr.isEnableWideFFR())
                .srRatio(PreferenceMgr.getSrRatio())
                .targetResolution(PreferenceMgr.getTargetResolution());
    }

    @Override
    public void onConnected() {
        Log.e(TAG, "onConnected()");
        // 连接成功: 创建一个数据通道, 用于与云端进行交互
        if (mCustomDataChannel == null) {
            mCustomDataChannel = createCustomDataChannel(6666, new Observer() {

                // 数据通道创建成功, 此后可以用该通道与云端进行交互
                @Override
                public void onConnected(int port) {
                    Log.e(TAG, "createCustomDataChannel.onConnected() port:" + port);
                    String msg = "{\"connected\":true}";
                    mCustomDataChannel.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                }

                // 数据通道创建失败
                @Override
                public void onError(int port, int code, String msg) {
                    Log.e(TAG, "createCustomDataChannel.onError() port:" + port + "code:" + code + " msg:" + msg);
                }

                // 数据通道收到云端应用下发的消息
                @Override
                public void onMessage(int port, ByteBuffer buffer) {
                    String message = StandardCharsets.UTF_8.decode(buffer).toString();
                    Log.i(TAG, "onMessage() port=" + port + " data=" + message);
                }
            });
        }
    }

    // 发送一个消息到云端
    private void sendBtnClick(boolean value) {
        CustomDataChannel dataChannel = mCustomDataChannel;
        if (dataChannel == null) {
            Log.e(TAG, "sendBtnClick() dataChannel=null");
            return;
        }

        String msg = "{\"btn_click\":" + Boolean.toString(value) + "}";
        mCustomDataChannel.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void onUpdateCtrlEvents(@NonNull List<BaseInput> events) {
        for (BaseInput input : events) {
            // 按下右手柄b键启用超分效果
            if ("/hand/right".equals(input.part) && "/b/click".equals(input.path)) {
                BoolInput value = (BoolInput)input;
                sendBtnClick(value.bool_value);
            }
        }
    }
}
