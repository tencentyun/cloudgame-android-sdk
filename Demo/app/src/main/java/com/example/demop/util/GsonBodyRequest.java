package com.example.demop.util;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.tencent.tcgsdk.TLog;

import java.io.UnsupportedEncodingException;

public class GsonBodyRequest extends GsonRequest{

    /** Default charset for JSON request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    @Nullable
    private final String mRequestBody;

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method get or post
     * @param url URL of the request to make
     */
    public GsonBodyRequest(int method, String url,
                           @Nullable String requestBody,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(method, url, listener, error -> {
            String errorMsg = (error.getMessage() != null)? error.getMessage() : "unknown";
            if (error.networkResponse != null && error.networkResponse.data != null) {
                errorMsg = errorMsg + "\nnetwork response: " + new String(error.networkResponse.data);
            }
            TLog.e("url: " + url + ", requestBody: " + requestBody + ", error: " + errorMsg);
            if (errorListener != null) {
                errorListener.onErrorResponse(error);
            }
        });
        mRequestBody = requestBody;
        TLog.i(TAG, "url: " + url + ", requestBody: " + (requestBody != null? requestBody : "null"));
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            TLog.d(TAG,
                    "Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

}