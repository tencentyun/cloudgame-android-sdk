package com.tencent.demo;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tencent.component.utils.LogUtils;
import com.tencent.demo.data.GameStartParam;
import com.tencent.demo.data.ServerResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 该类用于请求业务后台
 * 客户端请求业务后台，传入client session获取到server session
 * 客户可以根据实际需求实现自己的业务后台
 * 业务后台的API请参考:
 * https://cloud.tencent.com/document/product/1162/40740
 */
public class CloudGameApi {
    private static final String TAG = "CloudGameApi";
    private RequestQueue mQueue;

    public CloudGameApi(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * 开始请求业务后台，获取云端游戏实例
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     *
     * @param listener 服务端返回结果
     */
    public void startGame(GameStartParam param, IServerSessionListener listener) {
        LogUtils.i(TAG, "startGame");
        String bodyString = new Gson().toJson(param);
        String url = "https://code.cloud-gaming.myqcloud.com/CreateExperienceSession";

        printDebugLogs(TAG, "createSession url: " + url);
        JSONObject request = null;
        printDebugLogs(TAG, "bodyString: " + bodyString);
        try {
            // 构造JSONObject类型的请求对象
            request = new JSONObject(bodyString);
        } catch (JSONException e) {
            LogUtils.e(TAG, "startGame failed:" + e.getMessage());
        }

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url, request,
                success -> {
                    ServerResponse resp;
                    printDebugLogs(TAG, "createSession success: " + success);
                    resp = new Gson().fromJson(success.toString(), ServerResponse.class);
                    listener.onSuccess(resp);
                },
                error -> {
                    printDebugLogs(TAG, "createSession error: " + error);
                    listener.onFailed(error.getMessage() + "");
                });

        final int MAX_RETRIES = 3;
        request2.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(request2);
    }

    private static void printDebugLogs(String tag, String content) {
        if (content == null) {
            LogUtils.e(TAG, "content=null " + new Throwable());
            return;
        }

        final int MAX_LEN = 4000;
        if (content.length() > MAX_LEN) {
            LogUtils.i(tag, content.substring(0, MAX_LEN));
            printDebugLogs(tag, content.substring(MAX_LEN));
        } else {
            LogUtils.i(tag, content);
        }
    }


    // 业务后台返回结果监听
    public interface IServerSessionListener {

        void onFailed(String msg);

        void onSuccess(ServerResponse resp);
    }
}
