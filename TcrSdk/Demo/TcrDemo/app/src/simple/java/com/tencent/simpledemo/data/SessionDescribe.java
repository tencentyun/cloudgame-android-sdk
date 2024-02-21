package com.tencent.simpledemo.data;

import com.google.gson.annotations.SerializedName;

public class SessionDescribe {

    @SerializedName("ServerSession")
    public String serverSession;     // 云端下发的服务端会话serverSession
}
