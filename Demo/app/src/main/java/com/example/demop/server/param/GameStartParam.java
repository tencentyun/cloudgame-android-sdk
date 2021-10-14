package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

/**
 * 该类封装了开始游戏所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40740
 */
public class GameStartParam {
    @SerializedName("ExperienceCode")
    public String gameCode;
    @SerializedName("ClientSession")
    public String clientSession;
    @SerializedName("UserId")
    public String userId;
    @SerializedName("RequestId")
    public String requestId = UUID.randomUUID().toString();
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
