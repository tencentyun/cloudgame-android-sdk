package com.tencent.tcr.sdk.demo.cloudphone.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.demo.cloudphone.util.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public abstract class BaseRequest<T> {
    protected static final String TAG = "[TCR]Request";

    // 错误码常量定义
    public static final int CODE_SUCCESS = 0;   // success
    public static final int CODE_ERROR_UNKNOWN = -100; // unknown error
    public static final int CODE_ERROR_INTERNAL = -101; // internal error
    public static final int CODE_ERROR_BUILD_REQUEST = -110;   // build request fail
    public static final int CODE_ERROR_NETWORK = -111;          // network error
    public static final int CODE_ERROR_PROCESS_RESPONSE = -112; // process response fail

    protected static final String BASE_URL = "https://test-xiaosuan-server.cloud-device.crtrcloud.com";
    protected final String mRequestID = UUID.randomUUID().toString();

    protected final AsyncCallback<T> callback;

    public BaseRequest(AsyncCallback<T> callback) {
        this.callback = callback;
    }

    public static void processVolleyError(@NonNull VolleyError error, @Nullable AsyncCallback<?> callback,
                                          @NonNull String logTag) {
        // 1. 获取详细的错误信息
        String errorMsg = "未知网络错误";
        if (error.networkResponse != null) {
            // 服务器返回了HTTP错误码
            int statusCode = error.networkResponse.statusCode;
            errorMsg = "HTTP错误: " + statusCode;
            try {
                String responseBody = new String(error.networkResponse.data,
                        HttpHeaderParser.parseCharset(error.networkResponse.headers));
                errorMsg += ", 响应: " + responseBody;
            } catch (UnsupportedEncodingException e) {
                Log.e(logTag, "解析网络响应错误: " + e.getMessage());
            }
        } else if (error instanceof TimeoutError) {
            errorMsg = "请求超时";
        } else if (error instanceof NoConnectionError) {
            errorMsg = "无网络连接";
        } else if (error instanceof AuthFailureError) {
            errorMsg = "认证失败";
        } else if (error.getMessage() != null) {
            errorMsg = error.getMessage();
        }

        // 2. 记录详细的错误日志
        Log.e(logTag, "网络请求失败: " + errorMsg);

        // 3. 回调错误信息
        if (callback != null) {
            callback.onFailure(CODE_ERROR_NETWORK, errorMsg);
        }
    }

    protected String getUrl() {
        return BASE_URL + "/" + getCmd();
    }

    protected abstract String getCmd();

    protected abstract JSONObject buildRequest() throws JSONException;

    protected abstract TypeToken<T> getResponseType();

    public void execute(RequestQueue queue) {
        String url = getUrl();
        if (url == null) {
            Log.e(TAG, "url is null");
            if (callback != null) {
                callback.onFailure(CODE_ERROR_INTERNAL, "url is null");
            }
            return;
        }

        JSONObject jsonRequest;
        try {
            jsonRequest = buildRequest();
            Log.d(TAG, "build request: " + jsonRequest);
        } catch (JSONException e) {
            Log.e(TAG, "build request fail. ex=" + e.getMessage());
            if (callback != null) {
                callback.onFailure(CODE_ERROR_BUILD_REQUEST, "build request fail: " + e.getMessage());
            }
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonRequest, response -> {
            try {
                T result = GsonUtils.getGson().fromJson(response.toString(), getResponseType().getType());
                Log.d(TAG, "get response for " + mRequestID + ": " + result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } catch (Exception e) {
                Log.e(TAG, "process response fail. ex=" + e.getMessage() + ". response=" + response);
                if (callback != null) {
                    callback.onFailure(CODE_ERROR_PROCESS_RESPONSE, "解析响应失败: " + e.getMessage());
                }
            }
        }, error -> processVolleyError(error, callback, TAG));
        queue.add(request);
    }
}
