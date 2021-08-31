package com.example.demop.server;

import static com.example.demop.Constant.PC_GAME_CODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.demop.Constant;
import com.example.demop.server.param.GameStartParam;
import com.example.demop.server.param.GameStopParam;
import com.google.gson.Gson;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 这个类用于请求云游团队体验后台, 客户端接入SDK时无法使用
 * 客户端的业务后台需要支持客户端调用接口, 以便通过client session获取到server session.
 * 业务后台的API请参考:
 * https://cloud.tencent.com/document/product/1162/40740
 */
public class CloudGameApi {

    public static final String SERVER = "code.cloud-gaming.myqcloud.com";
    public static final String CREATE_GAME_SESSION = "/CreateExperienceSession";
    public static final String STOP_GAME_SESSION = "/StopExperienceSession";

    public interface IServerSessionListener {
        void onFailed(String msg);
        void onSuccess(JSONObject result);
    }

    private final RequestQueue mQueue;
    private final Gson mGson = new Gson();
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
     * 开始游戏
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     */
    public void startGame(String gameCode, String clientSession, IServerSessionListener listener) {
        String bodyString = mGson.toJson(new GameStartParam(gameCode, clientSession, mUserID));
        String url = address(CREATE_GAME_SESSION);
        Log.d(Constant.TAG, "createSession url: " + url);
        JSONObject request = null;
        try {
            request = new JSONObject(bodyString);
            Log.d(Constant.TAG, "createSession clientSession: " + request.getString("ClientSession"));
            Log.d(Constant.TAG, "createSession UserID: " + request.getString("UserId"));
            Log.d(Constant.TAG, "createSession ExperienceCode: " + request.getString("ExperienceCode"));
        } catch (JSONException e) {
            Log.e(Constant.TAG, e.getMessage());
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, url, request, listener::onSuccess, error -> {
            Log.d(Constant.TAG, "createSession error: " + error);
            listener.onFailed(error.getMessage());
        }));
    }

    /**
     * 停止游戏(释放云端实例)
     */
    public void stopGame() {
        String bodyString = mGson.toJson(new GameStopParam(PC_GAME_CODE, mUserID));
        Log.d(Constant.TAG, "stopGame bodyString: " + bodyString);
        JSONObject request = null;
        try {
            request = new JSONObject(bodyString);
            Log.d(Constant.TAG, "stopGame: request: " + request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, address(STOP_GAME_SESSION), request, response -> {
            Log.d(Constant.TAG, "stopGame result:" + response);
        }, null));
    }
}
