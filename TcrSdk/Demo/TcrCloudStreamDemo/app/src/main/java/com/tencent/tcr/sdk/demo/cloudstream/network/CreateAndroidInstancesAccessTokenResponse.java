package com.tencent.tcr.sdk.demo.cloudstream.network;

import androidx.annotation.NonNull;

public class CreateAndroidInstancesAccessTokenResponse {

    public String Token;
    public String AccessInfo;
    public String RequestId;

    @NonNull
    @Override
    public String toString() {
        return "CreateAndroidInstancesAccessTokenResponse{" + "Token='" + Token + ", AccessInfo='" + (
                AccessInfo != null ? AccessInfo.substring(0, Math.min(5, AccessInfo.length())) + "...base64="
                        : "null") + ", RequestId='" + RequestId + '}';
    }

}