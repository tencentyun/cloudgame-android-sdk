package com.tencent.advancedemo.data;

import com.google.gson.annotations.SerializedName;

/**
 * 开始云端应用后台响应的参数
 */
public class StartProjectResponse extends Response {

    @SerializedName("SessionDescribe")
    public SessionDescribe sessionDescribe;

}
