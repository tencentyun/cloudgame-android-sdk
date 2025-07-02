package com.tencent.tcr.sdk.demo.cloudphone.network;

import androidx.annotation.NonNull;

public class ExpServerResponse<T> {

    public T Response;
    public ExpErrorResponse Error;

    @NonNull
    @Override
    public String toString() {
        return "ExpServerResponse{" + "Response=" + Response + ", Error=" + Error + '}';
    }
}