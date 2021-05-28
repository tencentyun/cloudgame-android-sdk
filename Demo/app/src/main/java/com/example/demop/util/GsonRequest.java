package com.example.demop.util;

import androidx.annotation.GuardedBy;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.tencent.tcgsdk.TLog;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest extends Request<String> {
    public final static String TAG = "GsonRequest";
    /** Lock to guard mListener as it is cleared on cancel() and read on delivery. */
    private final Object mLock = new Object();

    private Map<String, String> mHeaders;
    private Map<String, String> mParameters;
    @GuardedBy("mLock")
    private Response.Listener<String> mListener;


    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method get or post
     * @param url URL of the request to make
     */
    public GsonRequest(int method, String url,
                       Response.Listener<String> listener,
                       Response.ErrorListener errorListener) {
        this(method, url, null, null, listener, errorListener);
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method get or post
     * @param url URL of the request to make
     * @param parameters Map of request parameters
     */
    public GsonRequest(int method, String url,
                       Map<String, String> parameters,
                       Response.Listener<String> listener,
                       Response.ErrorListener errorListener) {
        this(method, url, null, parameters, listener, errorListener);
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method get or post
     * @param url URL of the request to make
     * @param headers Map of request headers
     * @param parameters Map of request parameters
     */
    public GsonRequest(int method, String url,
                       Map<String, String> headers,
                       Map<String, String> parameters,
                       Response.Listener<String> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mHeaders = headers;
        this.mParameters = parameters;
        this.mListener = listener;
        TLog.i(TAG, "url: " + url + ", parameters: " + (parameters != null? parameters.toString() : "null"));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParameters != null ? mParameters : super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public void cancel() {
        super.cancel();
        synchronized (mLock) {
            mListener = null;
        }
    }

    @Override
    protected void deliverResponse(String response) {
        Response.Listener<String> listener;
        synchronized (mLock) {
            listener = mListener;
        }
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            TLog.d(TAG, "request response: " + json);
            return Response.success(json,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
