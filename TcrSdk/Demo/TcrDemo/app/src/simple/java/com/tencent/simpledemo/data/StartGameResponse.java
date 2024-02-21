package com.tencent.simpledemo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 请求开始游戏后台响应的参数
 */
public class StartGameResponse extends Response {

    @SerializedName("SessionDescribe")
    public SessionDescribe sessionDescribe;
}
