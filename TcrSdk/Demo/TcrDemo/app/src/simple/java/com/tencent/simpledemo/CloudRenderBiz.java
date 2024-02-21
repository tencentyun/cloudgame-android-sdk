package com.tencent.simpledemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tencent.simpledemo.data.Request;
import com.tencent.simpledemo.data.StartGameRequest;
import com.tencent.simpledemo.data.StartProjectRequest;
import com.tencent.simpledemo.data.StopGameRequest;
import com.tencent.simpledemo.data.StopProjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * 该类用于请求业务后台接口。<br>
 *
 * 其中使用到的参数仅为演示，客户可以根据实际需求实现自己的业务后台。
 */
public class CloudRenderBiz {

    private static final String TAG = "CloudRenderBiz";

    /**
     * 是否使用云渲染团队提供的测试环境运行SDK。
     */
    public static final boolean USE_TCR_TEST_ENV = true;
    /**
     * 使用云渲染测试环境的体验码。<br>
     * 您需要自行到控制台中创建体验码，并且填到该变量中。
     */
    public static final String EXPERIENCE_CODE = "";

    /**
     * 云游后台游戏ID，该ID对应云游后台部署的游戏。<br>
     * 可以联系云游团队接口人部署自己的游戏。部署后即可生成game_id。
     */
    private static final String GAME_ID = "";

    /**
     * 应用云渲染项目ID，该ID对应云应用后台部署的应用
     * 可以联系云应用团队接口人部署自己的应用。部署后即可生成project_id.
     */
    private static final String PROJECT_ID = "";

    /**
     * 在部署业务后台时生成的请求地址。请在部署业务后台之后将地址填写在SERVER_URL中。
     * 文档说明：https://github.com/tencentyun/car-server-demo#3-%E5%90%AF%E5%8A%A8%E6%9C%8D%E5%8A%A1
     */
    private static final String SERVER_URL = "";

    /**
     * 网络请求库Volley提供的{@link RequestQueue}，我们通过它发起网络请求
     */
    private RequestQueue mQueue;

    /**
     * 用户ID。请求业务后台的参数之一。
     */
    private String mUserID;

    private CloudRenderBiz() {
    }

    private static final class Holder {

        private static final CloudRenderBiz INSTANCE = new CloudRenderBiz();
    }

    public static CloudRenderBiz getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 类初始化
     *
     * @param context Application Context
     */
    public void init(Context context) {
        mQueue = Volley.newRequestQueue(context);
        mUserID = getIdentity(context);
    }

    /**
     * 通过自定义全局唯一 ID (GUID) 对应用实例进行唯一标识
     * 参考Google唯一标识符最佳做法：https://developer.android.com/training/articles/user-data-ids?hl=zh-cn
     * 卸载之后UserId会发生更改
     */
    private static String getIdentity(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = preference.getString("identity", null);
        if (identity == null) {
            identity = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("identity", identity);
            editor.apply();
        }
        return identity;
    }

    /**
     * 云游戏
     * 开始请求业务后台，获取游戏实例
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     *
     * @param clientSession sdk初始化成功后返回的client session
     * @param listener 服务端返回结果
     */
    public void startGame(String clientSession, Listener<JSONObject> listener, ErrorListener errorListener) {
        if (SERVER_URL.equals("") || GAME_ID.equals("")) {
            throw new NullPointerException("请先部署业务后台，并将后台地址、游戏ID填到SERVER_URL和GAME_ID中，"
                    + "如何部署请参考: https://github.com/tencentyun/car-server-demo");
        }
        Request param = new StartGameRequest(mUserID, GAME_ID, clientSession);
        JsonObjectRequest request = createRequest(SERVER_URL + param.cmd, param, listener, errorListener);
        mQueue.add(request);
    }

    /**
     * 请求业务后台，停止游戏(释放云端实例)
     */
    public void stopGame(Listener<JSONObject> listener, ErrorListener errorListener) {
        Request param = new StopGameRequest(mUserID);
        JsonObjectRequest request = createRequest(SERVER_URL + param.cmd, param, listener, errorListener);
        mQueue.add(request);
    }

    /**
     * 应用云渲染
     * 开始请求业务后台，获取云端应用实例
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     *
     * @param clientSession sdk初始化成功后返回的client session
     * @param listener 服务端返回结果
     */
    public void startProject(String clientSession, Listener<JSONObject> listener, ErrorListener errorListener) {
        if (SERVER_URL.equals("") || PROJECT_ID.equals("")) {
            throw new NullPointerException("请先部署业务后台，并将后台地址、项目ID填到SERVER_URL和PROJECT_ID中，"
                    + "如何部署请参考: https://github.com/tencentyun/car-server-demo");
        }
        Request param = new StartProjectRequest(mUserID, PROJECT_ID, clientSession);
        JsonObjectRequest request = createRequest(SERVER_URL + param.cmd, param, listener, errorListener);
        mQueue.add(request);
    }

    /**
     * 请求业务后台，停止云端应用(释放云端实例)
     */
    public void stopProject(Listener<JSONObject> listener, ErrorListener errorListener) {
        Request param = new StopProjectRequest(mUserID);
        JsonObjectRequest request = createRequest(SERVER_URL + param.cmd, param, listener, errorListener);
        mQueue.add(request);
    }


    private JsonObjectRequest createRequest(String requestAddress, Request param, Listener<JSONObject> listener,
                                            ErrorListener errorListener) {
        Log.i(TAG, "createRequest() " + param);
        JSONObject requestJson = null;
        try {
            requestJson = new JSONObject(new Gson().toJson(param));
        } catch (JSONException e) {
            Log.e(TAG, "createRequest() e=" + e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, requestAddress,
                requestJson, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return request;
    }

}