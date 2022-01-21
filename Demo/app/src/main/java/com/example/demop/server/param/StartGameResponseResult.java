package com.example.demop.server.param;

import com.google.gson.annotations.SerializedName;

public class StartGameResponseResult extends ResponseResult {

    @SerializedName("SessionDescribe")
    public SessionDescribe sessionDescribe;

    @Override
    public String toString() {
        return "StartGameResponseResult{" +
                "sessionDescribe=" + sessionDescribe +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    public static class SessionDescribe {

        @SerializedName("ServerSession")
        public String serverSession;
        @SerializedName("Role")
        public String role;
        @SerializedName("RoleNumber")
        public int roleNumber;
        @SerializedName("PlayersMax")
        public int playersMax;

        @Override
        public String toString() {
            return "SessionDescribe{" +
                    ", role='" + role + '\'' +
                    ", roleNumber=" + roleNumber +
                    ", playersMax=" + playersMax +
                    '}';
        }
    }
}
