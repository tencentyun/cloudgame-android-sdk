package com.tencent.simpledemo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 该类封装了停止云端应用所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 */
public class StopGameRequest extends Request {

    @SerializedName("Sign")
    public String sign;

    public StopGameRequest(String userId) {
        super("StopGame",userId);
        this.sign = getSignature();
    }

    private String getSignature() {
        return getSHA256(requestId + timeStamp + userId + SALT);
    }
}