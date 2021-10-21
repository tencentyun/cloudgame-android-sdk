package com.example.demop.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.demop.server.param.GameStartParam;
import com.example.demop.server.param.GameStopParam;
import com.example.demop.server.param.ServerResponse;
import com.google.gson.Gson;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 该类用于请求业务后台
 * 客户端请求业务后台，传入client session获取到server session
 * 客户可以根据实际需求实现自己的业务后台
 * 如何搭建业务后台请参考：
 * https://docs.qq.com/doc/DRUZsV3VHbm1ERE5U
 * 业务后台的API请参考:
 * https://cloud.tencent.com/document/product/1162/40740
 */
public class CloudGameApi {
    private final static String TAG = "CloudGameApi";

    // 搭建完成自己的业务后台后可以修改为自己的server地址
    public static final String SERVER = "service-dn0r2sec-1304469412.gz.apigw.tencentcs.com/release";
    public static final String CREATE_GAME = "/StartCloudGame";
    public static final String STOP_GAME = "/StopCloudGame";

    // 业务后台返回结果监听
    public interface IServerSessionListener {
        void onFailed(String msg);
        void onSuccess(ServerResponse resp);
    }

    private final RequestQueue mQueue;
    private final Gson mGson = new Gson();
    // 标识请求来自哪个用户
    private final String mUserID;

    private String address(String path) {
        return "https://" + SERVER + path;
    }

    public CloudGameApi(Context context) {
        mQueue = Volley.newRequestQueue(context);
        mUserID = getIdentity(context);
    }

    /**
     * 通过自定义全局唯一 ID (GUID) 对应用实例进行唯一标识
     * 参考Google唯一标识符最佳做法：https://developer.android.com/training/articles/user-data-ids?hl=zh-cn
     * 卸载之后UserId会发生更改
     */
    public String getIdentity(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = preference.getString("identity", null);
        if (identity == null) {
            identity = UUID.randomUUID().toString();
            Editor editor = preference.edit();
            editor.putString("identity", identity);
            editor.apply();
        }
        return identity;
    }

    /**
     * 开始请求业务后台，获取云端游戏实例
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     *
     * @param gameId 游戏ID
     * @param clientSession sdk初始化成功后返回的client session
     * @param listener 服务端返回结果
     */
    public void startGame(String gameId, String clientSession, IServerSessionListener listener) {
        String bodyString = mGson.toJson(new GameStartParam(gameId, clientSession, mUserID));
        String url = address(CREATE_GAME);
        Log.d(TAG, "startGame request url: " + url);
        JSONObject request = null;
        try {
            // 构造JSONObject类型的请求对象
            request = new JSONObject(bodyString);
            Log.d(TAG, "createSession ClientSession: " + request.getString("ClientSession"));
            Log.d(TAG, "createSession UserID: " + request.getString("UserId"));
            Log.d(TAG, "createSession GameID: " + request.getString("GameID"));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, url, request,
                success -> {
                    Log.d(TAG, "startGame success: " + success);
                    ServerResponse resp = new Gson().fromJson(success.toString(), ServerResponse.class);
                    listener.onSuccess(resp);
                },
                error -> {
                    Log.d(TAG, "createSession error: " + error);
                    listener.onFailed(error.getMessage());
                }));
    }

    /**
     * 请求业务后台，停止游戏(释放云端实例)
     */
    public void stopGame() {
        String url = address(STOP_GAME);
        String bodyString = mGson.toJson(new GameStopParam(mUserID));
        Log.d(TAG, "stopGame request url: " + url);
        JSONObject request = null;
        try {
            request = new JSONObject(bodyString);
            Log.d(TAG, "createSession UserID: " + request.getString("UserId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, url, request, response -> {
            Log.d(TAG, "stopGame result:" + response);
        }, null));
    }
}
