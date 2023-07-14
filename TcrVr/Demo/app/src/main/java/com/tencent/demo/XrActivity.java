package com.tencent.demo;

import android.util.Log;
import com.tencent.demo.CloudGameApi.IServerSessionListener;
import com.tencent.demo.data.GameStartParam;
import com.tencent.demo.data.ServerResponse;
import com.tencent.tcr.xr.TcrActivity;

public class XrActivity extends TcrActivity {
    private static final String TAG = "XrActivity";
    public static final String KEY_EXPERIENCE_CODE = "KEY_EXPERIENCE_CODE";

    @Override
    public void onInitSuccess(String clientSession) {
        new CloudGameApi(this).startGame(new GameStartParam(clientSession, getIntent().getStringExtra(KEY_EXPERIENCE_CODE)),
                new IServerSessionListener() {
            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "onFailed:" + msg);
                setLobbyText("请求服务端失败, 错误信息:" + msg);
            }

            @Override
            public void onSuccess(ServerResponse resp) {
                Log.e(TAG, "onSuccess:" + resp);
                if (resp.code == 0) {
                    setLobbyText("请求服务端成功，正在启动会话...");
                    start(resp.serverSession);
                } else {
                    setLobbyText("请求服务端失败, 错误信息:" + resp.message);
                }
            }
        });
    }

    @Override
    public void onInitFailure(int code, String msg) {
        Log.e(TAG, "onInitFailure:" + msg);
        setLobbyText("本地初始化失败:" + msg);
    }

    @Override
    public void onStartSuccess() {
        setLobbyText("启动会话成功，等待画面渲染...");
        // 开始发送位姿
        nativeStartTracking();
    }
}
