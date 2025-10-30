package com.tencent.tcr.demo.cloudphone.operator.network;

import androidx.annotation.NonNull;
import java.util.List;

public class DescribeAndroidInstancesResponse {

    public String RequestId;
    public int TotalCount;
    public List<AndroidInstanceData> AndroidInstances;

    @NonNull
    @Override
    public String toString() {
        return "DescribeAndroidInstancesResponse{" + "TotalCount=" + TotalCount + ", AndroidInstances="
                + AndroidInstances + ", RequestId='" + RequestId + '\'' + '}';
    }

    public static class AndroidInstanceData {

        public String AndroidInstanceId;
        public String AndroidInstanceRegion;
        public String AndroidInstanceZone;
        public String State;
        public String AndroidInstanceType;
        public String Name;
        public String CreateTime;

        @Override
        public String toString() {
            return "AndroidInstanceData{" + "AndroidInstanceId='" + AndroidInstanceId + '\''
                    + ", AndroidInstanceRegion='"
                    + AndroidInstanceRegion + '\'' + ", AndroidInstanceZone='" + AndroidInstanceZone + '\''
                    + ", State='" + State + '\'' + ", AndroidInstanceType='" + AndroidInstanceType + '\'' + ", Name='"
                    + Name + '\'' + ", CreateTime='" + CreateTime + '\'' + '}';
        }
    }
}