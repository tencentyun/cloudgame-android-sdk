package com.tencent.tcr.sdk.demo.cloudphone.network;

import androidx.annotation.NonNull;

public class ExpErrorResponse {

    public static final int ERROR_CODE_ErrorResponse = 193; // 临时定义的，代表后台返回ErrorResponse的错误码
    public String Code;// 错误码，应该像Operator一样，定义为 int 类型。但体验业务后台定义的错误码是字符串类型，暂时先这样处理，本地定义一个ERROR_CODE_ErrorResponse
    public String Message;

    @NonNull
    @Override
    public String toString() {
        return "ExpErrorResponse{" + "Code='" + Code + '\'' + ", Message='" + Message + '\'' + '}';
    }
}