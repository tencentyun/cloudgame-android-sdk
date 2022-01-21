package com.example.demop.server.param;

import com.example.demop.Constant;
import com.example.demop.utils.CommonUtil;
import com.google.gson.annotations.SerializedName;

public class StartGameRequestParam extends RequestParam {

    @SerializedName("GameId")
    public String gameId;
    @SerializedName("ClientSession")
    public String clientSession;
    @SerializedName("Sign")
    public String sign;

    @SerializedName("MaxBitrate")
    public int maxBitrate;              //可选，单位Mbps，动态调整最大码率
    @SerializedName("MinBitrate")
    public int minBitrate;             //可选，单位Mbps，动态调整最小码率
    @SerializedName("Fps")
    public int fps;

    public StartGameRequestParam(String userId, String gameId, String clientSession, int fps, int minBitrate, int maxBitrate) {
        super(userId);
        this.clientSession = clientSession;
        this.gameId = gameId;
        this.fps = fps;
        this.minBitrate = minBitrate;
        this.maxBitrate = maxBitrate;
        this.sign = getSignature();
    }

    private String getSignature() {
        return CommonUtil.getSHA256(clientSession + fps + gameId + maxBitrate + minBitrate
                + requestId + timeStamp + userId + Constant.SOLT);
    }

    @Override
    public String toString() {
        return "StartGameRequestParam{" +
                "gameId='" + gameId + '\'' +
                ", sign='" + sign + '\'' +
                ", maxBitrate=" + maxBitrate +
                ", minBitrate=" + minBitrate +
                ", fps=" + fps +
                ", requestId='" + requestId + '\'' +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
