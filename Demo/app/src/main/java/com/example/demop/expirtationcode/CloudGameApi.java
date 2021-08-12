package com.example.demop.expirtationcode;

import android.content.Context;
import android.os.Build;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.demop.Constant;
import com.example.demop.expirtationcode.bean.ExperienceCodeParam;
import com.example.demop.expirtationcode.bean.ExperienceCodeResp;
import com.example.demop.expirtationcode.bean.StopGameParam;
import com.example.demop.util.GsonBodyRequest;
import com.google.gson.Gson;
import com.tencent.tcgsdk.TLog;
import static com.example.demop.Constant.PC_EXPIRATION_CODE;
import static com.example.demop.Constant.TAG;

/**
 * 这个类用于请求云游团队体验后台, 客户端接入SDK时无法使用
 * 客户端的业务后台需要支持客户端调用接口, 以便通过client session获取到server session.
 * 业务后台的API请参考:
 * https://cloud.tencent.com/document/product/1162/40740
 */
public class CloudGameApi {
    public static final String SERVER = "code.cloud-gaming.myqcloud.com";
    public static final String CREATE_EXPERIENCE_SESSION = "/CreateExperienceSession";
    public static final String STOP_EXPERIENCE_SESSION = "/StopExperienceSession";

    public interface IServerSessionListener {
        void onSuccess(ExperienceCodeResp result);
        void onFailed(String msg);
    }

    private final RequestQueue mQueue;
    private final Gson mGson = new Gson();
    private final String mUserID;

    private String address(String path) {
        return "https://" + SERVER + path;
    }

    public CloudGameApi(Context mContext) {
        mQueue = Volley.newRequestQueue(mContext);
        mUserID = Build.FINGERPRINT;
    }

    /**
     * 开始体验
     * 该接口调用成功后, 云端会锁定机器实例, 并返回相应的server session
     */
    public void startExperience(String experienceCode, String clientSession, IServerSessionListener listener) {
        String bodyString = mGson.toJson(new ExperienceCodeParam(experienceCode, clientSession, mUserID));

        String url = address(CREATE_EXPERIENCE_SESSION);
        TLog.d(TAG, "createSession: " + url);
        TLog.d(TAG, "createSession bodyString: " + bodyString);

        mQueue.add(new GsonBodyRequest(Request.Method.POST, url, bodyString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
                listener.onSuccess(new Gson().fromJson(response, ExperienceCodeResp.class));
            }
        }, error -> {
            TLog.d(TAG, "createSession error: " + error);
            listener.onFailed(error.getMessage());
        }));
    }

    /**
     * 停止体验(释放云端实例)
     */
    public void stopExperience() {
        String bodyString = mGson.toJson(new StopGameParam(PC_EXPIRATION_CODE, mUserID));
        TLog.d(TAG, "stopGame bodyString: " + bodyString);
        mQueue.add(new GsonBodyRequest(Request.Method.POST, address(STOP_EXPERIENCE_SESSION), bodyString, response -> {
            TLog.d(TAG, "stop game result:" + response);
        }, null));
    }
}
