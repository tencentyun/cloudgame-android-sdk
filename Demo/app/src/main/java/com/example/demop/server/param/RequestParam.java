package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class RequestParam {

    @SerializedName("RequestId")
    public String requestId;
    @SerializedName("UserId")
    public String userId;
    @SerializedName("TimeStamp")
    public long timeStamp;

    public RequestParam(String userId) {
        this.userId = userId;
        this.requestId = getRequestId();
        this.timeStamp = getCurrentTimeStamp();
    }

    private static String getRequestId() {
        return UUID.randomUUID().toString();
    }

    private static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "RequestParam{" +
                "requestId='" + requestId + '\'' +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
