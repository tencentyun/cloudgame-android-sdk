package com.example.demop.server.param;

import com.example.demop.Constant;
import com.example.demop.utils.CommonUtil;
import com.google.gson.annotations.SerializedName;

public class StopGameRequestParam extends RequestParam {

    @SerializedName("Sign")
    public String sign;

    public StopGameRequestParam(String userId) {
        super(userId);
        this.sign = getSignature();
    }

    private String getSignature() {
        return CommonUtil.getSHA256(requestId + timeStamp + userId + Constant.SOLT);
    }

    @Override
    public String toString() {
        return "StopGameRequestParam{" +
                "sign='" + sign + '\'' +
                ", requestId='" + requestId + '\'' +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
