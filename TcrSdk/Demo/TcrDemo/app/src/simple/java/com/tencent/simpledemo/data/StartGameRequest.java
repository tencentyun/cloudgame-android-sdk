package com.tencent.simpledemo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 该类封装了开始游戏所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 */
public class StartGameRequest extends Request {

    @SerializedName("GameId")
    public String gameId;                // 游戏ID，在您的腾讯云控制台查询

    @SerializedName("ClientSession")
    public String clientSession;        // 客户端会话

    @SerializedName("Sign")
    public String sign;

    public StartGameRequest(String userId, String gameId, String clientSession) {
        super("StartGame", userId);
        this.clientSession = clientSession;
        this.gameId = gameId;
        this.sign = getSHA256(clientSession + gameId + requestId + timeStamp + userId + SALT);
    }

}
