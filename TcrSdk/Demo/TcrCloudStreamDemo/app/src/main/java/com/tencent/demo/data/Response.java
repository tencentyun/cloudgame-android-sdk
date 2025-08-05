package com.tencent.demo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 业务后台响应的公共参数
 */
public class Response {

    @SerializedName("Code")
    public int code;

    @SerializedName("Msg")
    public String msg;

    @SerializedName("RequestId")
    public String requestId;
    
}
