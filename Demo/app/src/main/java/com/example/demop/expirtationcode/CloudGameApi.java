package com.example.demop.expirtationcode;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.demop.expirtationcode.bean.ExperienceCodeParam;
import com.example.demop.expirtationcode.bean.StopGameParam;
import com.example.demop.util.GsonBodyRequest;
import com.google.gson.Gson;
import com.tencent.tcgsdk.TLog;

public class CloudGameApi {
    private final static String TAG = "CloudGame";
    public static final String SERVER = "code.cloud-gaming.myqcloud.com";
    public static final String CREATE_EXPERIENCE_SESSION = "/CreateExperienceSession";
    public static final String STOP_EXPERIENCE_SESSION = "/StopExperienceSession";

    public interface IServerSessionListener {
        void onSuccess(String result);
        void onFailed(String msg);
    }

    private Context mContext;
    private RequestQueue mQueue;
    private Gson mGson = new Gson();

    private String address(String path) {
        return "https://" + SERVER + path;
    }

    public CloudGameApi(Context mContext) {
        this.mContext = mContext;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public void startExperience(ExperienceCodeParam param, IServerSessionListener listener) {
        String bodyString = mGson.toJson(param);

        String url = address(CREATE_EXPERIENCE_SESSION);
        TLog.d(TAG, "createSession: " + url);
        TLog.d(TAG, "createSession bodyString: " + bodyString);
        mQueue.add(new GsonBodyRequest(Request.Method.POST, url, bodyString, response -> {
            listener.onSuccess(response);
        }, error -> {
            TLog.d(TAG, "createSession error: " + error);
            listener.onFailed(error.getMessage());
        }));
    }

    public void stopExperience(StopGameParam param) {
        String bodyString = mGson.toJson(param);
        TLog.d(TAG, "stopGame bodyString: " + bodyString);
        mQueue.add(new GsonBodyRequest(Request.Method.POST, address(STOP_EXPERIENCE_SESSION), bodyString, response -> {
            TLog.d(TAG, "stop game result:" + response);
        }, null));
    }
}
