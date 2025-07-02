package com.tencent.tcr.sdk.demo.cloudphone.network;

import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * 请求示例数据：{"AndroidInstanceIds":["cai-1300056159-fe2dT8KV4RQ","cai-1300056159-fe2dC9rRMET"],"ExpirationDuration":"12h","RequestId":"ae1847d1-83f1-4ed6-a04e-756e84269e1a"}
 *
 * 响应示例数据（业务失败）：{"Error":{"Code":"AuthenticationFailed","Message":"Authentication failed."},"RequestId":""}
 * 响应示例数据（业务成功）：{"Response":{"Token":"1300056159_NusYqgpWrcGNd0GMAp67OlJQXIlz3TvXXoxq0ZEPgZgN9xzD6pVqW","AccessInfo":"base64=","RequestId":"e20ae2c9-7e68-454e-ba46-1b2323ee2d60"}}
 * 其中 AccessInfo 进行 base64 解析后为: {"AccessInfo":[{"Zone":"ap-hangzhou-ec-1","InstanceIds":["cai-1300056159-fe2dT8KV4RQ","cai-1300056159-fe2dC9rRMET"],"OperatorAddress":"https://test-ap-hangzhou-ec-1-operator.cai.crtrcloud.com:10088","WebSocketAddress":"ws://test-ap-hangzhou-ec-1-mediaserver.cai.crtrcloud.com:20080","WebSocketSecureAddress":"wss://test-ap-hangzhou-ec-1-mediaserver.cai.crtrcloud.com:20443","WebRTCAddress":"https://test-ap-hangzhou-ec-1-webrtcserver.cai.crtrcloud.com:21443"}]}
 */
public class CreateAndroidInstancesAccessTokenRequest extends
        ExpServerRequest<CreateAndroidInstancesAccessTokenResponse> {

    public static final String TAG = "[TCR]Create...AccessTokenReq";

    private final List<String> instanceIds;

    public CreateAndroidInstancesAccessTokenRequest(List<String> instanceIds,
            AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>> createAndroidInstancesAccessTokenCallback) {
        super(createAndroidInstancesAccessTokenCallback);
        this.instanceIds = instanceIds;
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
        return jsonRequest;
    }

    @Override
    protected TypeToken<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>> getResponseType() {
        return new TypeToken<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>>() {
        };
    }
}
