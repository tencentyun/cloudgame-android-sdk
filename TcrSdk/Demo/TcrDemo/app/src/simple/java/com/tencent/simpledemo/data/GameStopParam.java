package com.tencent.simpledemo.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.UUID;

/**
 * 该类封装了停止游戏所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40739
 */
public class GameStopParam {

    @SerializedName("ExperienceCode")
    public String gameCode= "MIL1V51K" ;
    @SerializedName("UserId")
    public String userId;
    @SerializedName("RequestId")
    private final String requestId = UUID.randomUUID().toString();
    /**
     * @param userId 用户id
     */
    public GameStopParam(String userId) {
        this.userId = userId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}