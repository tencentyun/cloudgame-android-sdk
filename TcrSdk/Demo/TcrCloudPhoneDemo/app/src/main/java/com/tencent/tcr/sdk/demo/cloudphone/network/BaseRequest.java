package com.tencent.tcr.sdk.demo.cloudphone.network;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.demo.cloudphone.util.GsonUtils;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseRequest<T> {

    // 错误码常量定义
    public static final int CODE_SUCCESS = 0;   // success
    public static final int CODE_ERROR_UNKNOWN = -100; // unknown error
    public static final int CODE_ERROR_INTERNAL = -101; // internal error
    public static final int CODE_ERROR_BUILD_REQUEST = -110;   // build request fail
    public static final int CODE_ERROR_NETWORK = -111;          // network error
    public static final int CODE_ERROR_PROCESS_RESPONSE = -112; // process response fail


    protected static final String TAG = "BaseRequest";
    protected final AsyncCallback<T> callback;
    protected final String mRequestID = UUID.randomUUID().toString();

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

    protected abstract String getUrl();

    protected abstract JSONObject buildRequest() throws JSONException;

    protected abstract TypeToken<T> getResponseType();

    public void execute(RequestQueue queue) {
        String url = getUrl();
        if (url == null) {
            Log.e(TAG, "rid=" + mRequestID + " url=null");
            if (callback != null) {
                callback.onFailure(CODE_ERROR_INTERNAL, "url is null");
            }
            return;
        }

        JSONObject jsonRequest;
        try {
            jsonRequest = buildRequest();
        } catch (JSONException e) {
            Log.e(TAG, "rid=" + mRequestID + " buildRequest ex=" + e.getMessage());
            if (callback != null) {
                callback.onFailure(CODE_ERROR_BUILD_REQUEST, "build request fail: " + e.getMessage());
            }
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Method.POST, url, jsonRequest, response -> {
            T result = null;
            try {
                result = GsonUtils.getGson().fromJson(response.toString(), getResponseType().getType());
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "rid=" + mRequestID + " response JsonSyntaxException=" + e.getMessage() + ". response="
                        + response);
            }
            if (callback != null) {
                if (result == null) {
                    callback.onFailure(CODE_ERROR_PROCESS_RESPONSE, "解析响应失败");
                } else {
                    callback.onSuccess(result);
                }
            }
        }, error -> processVolleyError(error, callback, TAG));

        Log.d(TAG, "rid=" + mRequestID + " url=" + url + " request=" + jsonRequest);
        queue.add(request);
    }
}
