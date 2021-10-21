package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类为业务后台返回的参数，业务后台会从云游后台获取该参数
 * 需要和搭建的业务后台接收的参数保持一致
 * 如何搭建业务后台请参考：
 * https://docs.qq.com/doc/DRUZsV3VHbm1ERE5U
 */
public class ServerResponse {
    public int code;
    public ResponseData data;

    @Override
    public String toString() {
        return "ServerResponse{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public static class ResponseData {
        @SerializedName("RequestId")
        public String requestId;
        @SerializedName("ServerSession")
        public String serverSession;

        @Override
        public String toString() {
            return "ResponseData{" +
                    "requestId='" + requestId + '\'' +
                    ", serverSession='" + serverSession + '\'' +
                    '}';
        }
    }
 }
