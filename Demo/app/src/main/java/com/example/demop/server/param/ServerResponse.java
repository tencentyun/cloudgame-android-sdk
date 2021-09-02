package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类为业务后台返回的参数，业务后台会从云游后台获取该参数，客户需要自行实现业务后台。
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
