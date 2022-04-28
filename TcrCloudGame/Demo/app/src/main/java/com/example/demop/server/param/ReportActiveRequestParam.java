package com.example.demop.server.param;

import com.example.demop.Constant;
import com.example.demop.utils.CommonUtil;
import com.google.gson.annotations.SerializedName;

public class ReportActiveRequestParam extends RequestParam {

    @SerializedName("GameId")
    public String gameId;
    @SerializedName("Sign")
    public String sign;

    public ReportActiveRequestParam(String userId, String gameId) {
        super(userId);
        this.gameId = gameId;
        this.sign = getSignature();
    }

    private String getSignature() {
        return CommonUtil.getSHA256(gameId + requestId + timeStamp + userId + Constant.SOLT);
    }

    @Override
    public String toString() {
        return "ReportActiveRequestParam{" +
                "gameId='" + gameId + '\'' +
                ", sign='" + sign + '\'' +
                ", requestId='" + requestId + '\'' +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
