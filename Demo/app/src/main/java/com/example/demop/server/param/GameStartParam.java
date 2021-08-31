package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 这个类封装了启动游戏所需要的参数
 * 该参数是云游团队和后台体验服务协定，客户请参考业务后台API参数
 * 参数详情：https://cloud.tencent.com/document/product/1162/40740
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
