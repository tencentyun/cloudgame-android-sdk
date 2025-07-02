package com.tencent.tcr.sdk.demo.cloudphone.network;

import android.text.TextUtils;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.demo.cloudphone.util.GsonUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ExpServerRequest<T> extends BaseRequest<ExpServerResponse<T>> {

    protected static final String TAG = "[TCR]ExpServerRequest";
    protected static final String BASE_URL = "https://test-cai-server.cloud-device.crtrcloud.com";
    protected static String sCookie;
    protected final String mRequestID = UUID.randomUUID().toString();

    public ExpServerRequest(AsyncCallback<ExpServerResponse<T>> callback) {
        super(callback);
    }

    @Override
    protected String getUrl() {
        return BASE_URL + "/" + getCmd();
    }

    protected abstract String getCmd();

    @Override
    protected abstract JSONObject buildRequest() throws JSONException;

    @Override
    protected abstract TypeToken<ExpServerResponse<T>> getResponseType();

    @Override
    public void execute(RequestQueue queue) {
        String url = getUrl();
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

        JsonObjectRequest request = new JsonObjectRequest(Method.POST, url, jsonRequest, response -> {
            ExpServerResponse<T> result;
            try {
                result = GsonUtils.getGson().fromJson(response.toString(), getResponseType().getType());
                Log.d(TAG, "get response for " + mRequestID + ": " + result);
            } catch (Exception e) {
                Log.e(TAG, "process response fail. ex=" + e.getMessage() + ". response=" + response);
                if (callback != null) {
                    callback.onFailure(CODE_ERROR_PROCESS_RESPONSE, "process response fail: " + e.getMessage());
                }
                return;
            }
            if (result.Error != null) {
                if (callback != null) {
                    callback.onFailure(ExpErrorResponse.ERROR_CODE_ErrorResponse, result.Error.Message);
                }
            }
            if (result.Response == null) {
                return;
            }
            if (callback != null) {
                callback.onSuccess(result);
            }
        }, error -> processVolleyError(error, callback, TAG)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                if (sCookie != null) {
                    headers.put("Cookie", sCookie);
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> headers = response.headers;
                if (headers != null) {
                    String newCookie = headers.get("Set-Cookie");
                    if (!TextUtils.isEmpty(newCookie)) {
                        Log.i(TAG, "Received cookie: " + newCookie);
                        sCookie = newCookie;
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(request);
    }
}