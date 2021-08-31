package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 这个类封装了停止游戏时所需要的参数
 * 该参数是云游团队和后台体验服务协定，客户请参考业务后台API参数
 * 参数详情请参考：https://cloud.tencent.com/document/product/1162/40739
 */
public class GameStopParam {
    @SerializedName("ExperienceCode")
    public String gameCode;
    @SerializedName("UserId")
    public String userId;

    /**
     * @param gameCode 游戏体验码
     * @param userId 用户id
     */
    public GameStopParam(String gameCode, String userId) {
        this.gameCode = gameCode;
        this.userId = userId;
    }
}