package com.example.demop.server;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.demop.Constant;
import com.example.demop.server.param.ReportActiveRequestParam;
import com.example.demop.server.param.RequestParam;
import com.example.demop.server.param.ResponseResult;
import com.example.demop.server.param.StartGameRequestParam;
import com.example.demop.server.param.StartGameResponseResult;
import com.example.demop.server.param.StopGameRequestParam;
import com.google.gson.Gson;
import com.tencent.tcgsdk.TLog;
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

    private final static String TAG = Constant.TAG + "CloudGameApi";

    // 业务后台请求参数
    public static final String START_GAME = "/StartGame";
    public static final String STOP_GAME = "/StopGame";
    public static final String REPORT_ACTIVE = "/ReportActive";
    public static final int REQUEST_SUCCESS = 0;
    private static final int TIMEOUT = 50000;
    private static final int RETRY_TIMES = 0;

    private static final Gson mGson = new Gson();

    private static CloudGameApi sCloudGameApi;

    private RequestQueue mQueue;
    // 标识请求来自哪个用户
    private String mUserID;


    private CloudGameApi() {
    }

    private static String address(String path) {
        return "https://" + Constant.SERVER + path;
    }

    public static CloudGameApi getInstance() {
        if (sCloudGameApi == null) {
            synchronized (CloudGameApi.class) {
                if (sCloudGameApi == null) {
                    sCloudGameApi = new CloudGameApi();
                }
            }
        }
        return sCloudGameApi;
    }

    private static JsonObjectRequest createRequest(String requestAddress, RequestParam param,
            Listener<JSONObject> listener, ErrorListener errorListener) {
        TLog.i(TAG, "createRequest url " + address(requestAddress));
        TLog.i(TAG, "createRequest " + param);
        JSONObject requestJson = null;
        try {
            requestJson = new JSONObject(mGson.toJson(param));
        } catch (JSONException e) {
            TLog.e(TAG, "Create request error " + e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                address(requestAddress), requestJson, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return request;
    }

    public void init(RequestQueue queue, String userID) {
        mQueue = queue;
        mUserID = userID;
    }

    /**
     * 开始请求业务后台，获取云端游戏实例
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     *
     * @param gameId 游戏体验码
     * @param clientSession sdk初始化成功后返回的client session
     * @param listener 服务端返回结果
     */
    public void startGame(String gameId, String clientSession, final IServerResponseListener listener) {
        RequestParam param = new StartGameRequestParam(mUserID, gameId, clientSession, 30, 1, 5);
        JsonObjectRequest request = createRequest(START_GAME, param, response -> {
            ResponseResult result = mGson.fromJson(response.toString(), StartGameResponseResult.class);
            onResultCallback(result, listener);
        }, error -> {
            TLog.e(TAG, "request failed, reason is " + (error == null ? "msg is null" : error.getMessage()));
            listener.onFailed((error == null ? "msg is null" : error.getMessage()));
        });
        mQueue.add(request);
    }

    /**
     * 请求业务后台，停止游戏(释放云端实例)
     */
    public void stopGame(final IServerResponseListener listener) {
        RequestParam param = new StopGameRequestParam(mUserID);
        JsonObjectRequest request = createRequest(STOP_GAME, param, response -> {
            ResponseResult result = new Gson().fromJson(response.toString(),
                    ResponseResult.class);
            onResultCallback(result, listener);
        }, volleyError -> {
            TLog.e(TAG, "error: " + volleyError.getMessage());
        });
        mQueue.add(request);
    }

    public void reportActive(String gameId) {
        RequestParam param = new ReportActiveRequestParam(mUserID, gameId);
        JsonObjectRequest request = createRequest(REPORT_ACTIVE, param, response -> {
            ResponseResult result = new Gson().fromJson(response.toString(),
                    ResponseResult.class);
            onResultCallback(result, new IServerResponseListener() {
                @Override
                public void onFailed(String errorMsg) {
                    TLog.e(TAG, "onFailed: " + errorMsg);
                }

                @Override
                public void onSuccess(ResponseResult resp) {
                    TLog.i(TAG, "onSuccess: " + resp);
                }
            });
        }, volleyError -> {
            TLog.e(TAG, "error: " + volleyError.getMessage());
        });
        mQueue.add(request);
    }

    private void onResultCallback(ResponseResult result, IServerResponseListener listener) {
        if (result.code == REQUEST_SUCCESS) {
            TLog.i(TAG, "Response success, result is " + result.toString());
            if (listener != null) {
                listener.onSuccess(result);
            }
        } else {
            TLog.e(TAG, "Response error, reason is " + result.toString());
            if (listener != null) {
                listener.onFailed(result.msg);
            }
        }
    }

    // 业务后台返回结果监听
    public interface IServerResponseListener {

        void onFailed(String msg);

        void onSuccess(ResponseResult resp);
    }
}
