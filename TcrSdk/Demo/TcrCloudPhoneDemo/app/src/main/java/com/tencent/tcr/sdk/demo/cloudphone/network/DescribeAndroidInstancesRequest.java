package com.tencent.tcr.sdk.demo.cloudphone.network;

import com.google.gson.reflect.TypeToken;
import com.tencent.tcr.sdk.api.AsyncCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *
 * 请求示例数据：{"InstanceIds":[],"Limit":3,"Offset":0,"Region":"","RequestId":"d2522c46-4711-47df-945d-92f5194ad975"}
 *
 * 响应示例数据（业务失败）：{"Error":{"Code":"AuthenticationFailed","Message":"Authentication failed."},"RequestId":""}
 * 响应示例数据（业务成功）：{"Response":{"TotalCount":203,"AndroidInstances":[{"AndroidInstanceId":"cai-1300056159-fe2dT8KV4RQ","AndroidInstanceRegion":"ap-hangzhou-ec","AndroidInstanceZone":"ap-hangzhou-ec-1","State":"NORMAL","AndroidInstanceType":"A4","AndroidInstanceImageId":"image-49va6apu","Width":720,"Height":1280,"HostSerialNumber":"RK8S31P1402437484","AndroidInstanceGroupId":"","AndroidInstanceLabels":[{"Key":"分组1","Value":""},{"Key":"分组2","Value":""}],"Name":"测试 1","UserId":"yili","PrivateIP":"10.112.242.243","CreateTime":"2025-03-03T13:41:43.586Z","HostServerSerialNumber":"S12B31M2V22402667"},{"AndroidInstanceId":"cai-1300056159-fe2dC9rRMET","AndroidInstanceRegion":"ap-hangzhou-ec","AndroidInstanceZone":"ap-hangzhou-ec-1","State":"NORMAL","AndroidInstanceType":"A4","AndroidInstanceImageId":"image-49va6apu","Width":720,"Height":1280,"HostSerialNumber":"RK8S31P1402437479","AndroidInstanceGroupId":"","AndroidInstanceLabels":[{"Key":"分组1","Value":""},{"Key":"分组2","Value":""}],"Name":"测试 2","UserId":"yili","PrivateIP":"10.112.243.130","CreateTime":"2025-03-03T13:41:43.628Z","HostServerSerialNumber":"S12B31M2V22402667"},{"AndroidInstanceId":"cai-1300056159-fe2dDcHH0Et","AndroidInstanceRegion":"ap-hangzhou-ec","AndroidInstanceZone":"ap-hangzhou-ec-1","State":"NORMAL","AndroidInstanceType":"A4","AndroidInstanceImageId":"image-49va6apu","Width":720,"Height":1280,"HostSerialNumber":"RK8S31P1402437466","AndroidInstanceGroupId":"","AndroidInstanceLabels":[{"Key":"分组1","Value":""},{"Key":"分组2","Value":""}],"Name":"rrrr","UserId":"yili","PrivateIP":"10.112.245.114","CreateTime":"2025-03-03T13:41:43.67Z","HostServerSerialNumber":"S12B31M2V22402667"}],"RequestId":"75c6776b-2a9b-4350-ae5e-03c1ea67a923"}}
 */
public class DescribeAndroidInstancesRequest extends ExpServerRequest<DescribeAndroidInstancesResponse> {

    public static final String TAG = "[TCR]Desc...Ins...Req";

    public DescribeAndroidInstancesRequest(
            AsyncCallback<ExpServerResponse<DescribeAndroidInstancesResponse>> callback) {
        super(callback);
    }

    @Override
    protected String getCmd() {
        return "DescribeAndroidInstances";
    }

    @Override
    protected JSONObject buildRequest() throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("InstanceIds", new JSONArray());
        jsonRequest.put("Limit", 10);
        jsonRequest.put("Offset", 0);
        //jsonRequest.put("AndroidInstanceZone", "ap-hangzhou-ec-1");//控制获取的Android实例的区域
        jsonRequest.put("RequestId", "d2522c46-4711-47df-945d-92f5194ad975");
        return jsonRequest;
    }

    @Override
    protected TypeToken<ExpServerResponse<DescribeAndroidInstancesResponse>> getResponseType() {
        return new TypeToken<ExpServerResponse<DescribeAndroidInstancesResponse>>() {
        };
    }
}
