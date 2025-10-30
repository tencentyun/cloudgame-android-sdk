package com.tencent.tcr.demo.cloudphone.operator.network;

import androidx.annotation.NonNull;

public class LoginResponse {

    public String RequestId;
    public String UserType;

    @NonNull
    @Override
    public String toString() {
        return "LoginResponse{" + "RequestId='" + RequestId + '\'' + ", UserType='" + UserType + '\'' + '}';
    }
}