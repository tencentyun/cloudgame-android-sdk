package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类封装了启动游戏所需要的参数
 * 启动游戏参数是云游团队和业务后台协定
 * 客户需要自行实现业务后台，根据实际需求自定义和业务后台的请求参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40740
 */
public class GameStartParam {
    @SerializedName("ExperienceCode")
    public String gameCode;
    @SerializedName("ClientSession")
    public String clientSession;
    @SerializedName("UserId")
    public String userId;

    /**
     * @param gameCode 游戏代码
     * @param clientSession 客户端请求参数
     * @param userId 用户id
     */
    public GameStartParam(String gameCode, String clientSession, String userId) {
        this.gameCode = gameCode;
        this.clientSession = clientSession;
        this.userId = userId;
    }
}
