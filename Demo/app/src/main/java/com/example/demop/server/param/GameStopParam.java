package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

/**
 * 该类封装了停止游戏所需要的参数
 * 需要和搭建的业务后台接收的参数保持一致
 * 如何搭建业务后台请参考：
 * https://docs.qq.com/doc/DRUZsV3VHbm1ERE5U
 */
public class GameStopParam {
    @SerializedName("UserId")
    public String userId;

    /**
     * @param userId 用户id
     */
    public GameStopParam(String userId) {
        this.userId = userId;
    }
}