package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

/**
 * 该类封装了开始游戏所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 * 需要和搭建的业务后台接收的参数保持一致
 * 如何搭建业务后台请参考：
 * https://docs.qq.com/doc/DRUZsV3VHbm1ERE5U
 */
public class GameStartParam {
    @SerializedName("GameId")
    public String gameId;
    @SerializedName("ClientSession")
    public String clientSession;
    @SerializedName("UserId")
    public String userId;
    /**
     * @param gameId 游戏代码
     * @param clientSession 客户端请求参数
     * @param userId 用户id
     */
    public GameStartParam(String gameId, String clientSession, String userId) {
        this.gameId = gameId;
        this.clientSession = clientSession;
        this.userId = userId;
    }
}
