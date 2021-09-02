package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类为业务后台返回的参数
 * 返回参数是云游团队和业务后台协定
 * 客户需要自行实现业务后台，根据实际需求自定义和业务后台的返回参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40739
 */
public class ServerResponse {
    @SerializedName("Code")
    public int code;
    @SerializedName("Message")
    public String message;
    @SerializedName("ServerSession")
    public String serverSession;

    @Override
    public String toString() {
        return "ServerResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", serverSession='" + serverSession + '\'' +
                '}';
    }
}
