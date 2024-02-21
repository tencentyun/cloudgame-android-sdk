package com.tencent.simpledemo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 该类封装了开始云端应用所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 */
public class StartProjectRequest extends Request {

    @SerializedName("ProjectId")
    public String projectID;     // 项目ID，在地址https://console.cloud.tencent.com/car/project查询

    @SerializedName("ClientSession")
    public String clientSession;        // 客户端会话

    @SerializedName("Sign")
    public String sign;

    public StartProjectRequest(String userId, String projectID, String clientSession) {
        super("StartProject", userId);
        this.clientSession = clientSession;
        this.projectID = projectID;
        this.sign = getSHA256(clientSession + projectID + requestId + timeStamp + userId + SALT);
    }
}
