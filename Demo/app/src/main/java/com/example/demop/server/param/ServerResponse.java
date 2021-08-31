package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类为业务后台返回的参数
 * 返回参数是云游团队和后台体验服务协定，客户请参考业务后台API参数
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
