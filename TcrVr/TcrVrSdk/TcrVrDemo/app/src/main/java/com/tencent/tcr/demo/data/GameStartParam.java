package com.tencent.tcr.demo.data;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

/**
 * 该类封装了开始云渲染所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40740
 */
public class GameStartParam {

    @SerializedName("ExperienceCode")
    private final String experienceCode;

    @SerializedName("ClientSession")
    private final String clientSession;

    @SerializedName("UserId")
    private String userId = "test";

    @SerializedName("RequestId")
    private final String requestId = UUID.randomUUID().toString();

    /**
     * @param clientSession 客户端请求参数
     */
    public GameStartParam(String clientSession, String experienceCode) {
        this.clientSession = clientSession;
        this.experienceCode = experienceCode;
    }
}
