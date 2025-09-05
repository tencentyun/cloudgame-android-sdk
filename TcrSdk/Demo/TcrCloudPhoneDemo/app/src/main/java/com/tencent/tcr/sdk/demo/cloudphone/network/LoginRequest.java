package com.tencent.tcr.sdk.demo.cloudphone.network;

import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * 请求示例数据：{"Password" : "","RequestId" : "ece5c983-6f0d-4f94-8633-495de9153b27","UserId" : "handley"}
 *
 * 响应示例数据（业务失败）：{"Error":{"Code":"AuthenticationFailed","Message":"Authentication failed."},"RequestId":""}
 * 响应示例数据（业务成功）：{"Response":{"RequestId":"ece5c983-6f0d-4f94-8633-495de9153b27","UserType":"Admin"}}
 * 响应头中有个重要字段 Set-Cookie: authorization=ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SlZjMlZ5U1VRaU9pSmplWGxmWVdSdGFXNGlMQ0pUWlhOemFXOXVTVVFpT2lKU1ZrNW5NM2R6ZFVaekluMC5rSmJuTmdXVkZoa01XWjRaOXpFTWpMT0pIaDdvTXNIazJRSmJaMHNxWGNF; Expires=Thu, 03 Jul 2025 04:09:47 GMT
 */
public class LoginRequest extends ExpServerRequest<LoginResponse> {

    private final String username;
    private final String password;

    public LoginRequest(String username, String password,
            AsyncCallback<ExpServerResponse<LoginResponse>> callback) {
        super(callback);
        this.username = username;
        this.password = password;
    }

    @Override
    protected String getCmd() {
        return "Login";
    }

    @Override
    protected JSONObject buildRequest() throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("RequestId", mRequestID);
        jsonRequest.put("UserId", username);
        jsonRequest.put("Password", password);
        return jsonRequest;
    }

    @Override
    protected TypeToken<ExpServerResponse<LoginResponse>> getResponseType() {
        return new TypeToken<>() {
        };
    }
}
