package com.tencent.tcr.sdk.demo.cloudstream.network;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateAndroidInstancesAccessTokenBaseRequest extends
        BaseRequest<CreateAndroidInstancesAccessTokenResponse> {

    public static final String TAG = "[TCR]Create...AccessTokenReq";

    private final List<String> instanceIds;
    private final String userIp;

    public CreateAndroidInstancesAccessTokenBaseRequest(List<String> instanceIds, String userIp,
                                                        AsyncCallback<CreateAndroidInstancesAccessTokenResponse> createAndroidInstancesAccessTokenCallback) {
        super(createAndroidInstancesAccessTokenCallback);
        this.instanceIds = instanceIds;
        this.userIp = userIp;
    }

    @Override
    protected String getCmd() {
        return "CreateAndroidInstancesAccessToken";
    }

    @Override
    protected JSONObject buildRequest() throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String id : instanceIds) {
            jsonArray.put(id);
        }
        jsonRequest.put("RequestId", mRequestID);
        jsonRequest.put("AndroidInstanceIds", jsonArray);
        jsonRequest.put("ExpirationDuration", "12h");
        if (!TextUtils.isEmpty(userIp)) {
            jsonRequest.put("UserIp", userIp);
        }
        return jsonRequest;
    }

    @Override
    protected TypeToken<CreateAndroidInstancesAccessTokenResponse> getResponseType() {
        return new TypeToken<>() {
        };
    }
}
