package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

public class ResponseResult {

    @SerializedName("Code")
    public int code;
    @SerializedName("Msg")
    public String msg;
    @SerializedName("RequestId")
    public String requestId;

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
