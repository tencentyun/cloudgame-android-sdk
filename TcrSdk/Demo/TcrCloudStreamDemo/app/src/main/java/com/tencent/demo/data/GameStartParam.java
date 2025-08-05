package com.tencent.demo.data;

import android.text.TextUtils;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 该类封装了开始游戏所需要的参数
 * 客户端需要根据实际需求自定义业务后台的请求参数
 * 业务后台API文档：https://cloud.tencent.com/document/product/1162/40740
 */
public class GameStartParam {

    @SerializedName("ExperienceCode")
    private String gameCode = "MIL1V51K";
    @SerializedName("ClientSession")
    private final String clientSession;
    @SerializedName("UserId")
    private String userId="xxhape";
    @SerializedName("RequestId")
    private final String requestId = UUID.randomUUID().toString();
    @SerializedName("GameId")
    private String gameId;               //可选，游戏ID
    @SerializedName("GameRegion")
    private String gameRegion;           //可选，游戏区域，ap-guangzhou、ap-shanghai、ap-beijing等，如果不为空，优先按照该区域进行调度分配机器
    @SerializedName("SetNo")
    private long setNo;                  //可选，setno
    @SerializedName("GroupId")
    private String groupId;              //可选，groupId
    @SerializedName("Resolution")
    private String resolution;           //可选，分辨率
    @SerializedName("MaxBitrate")
    private int maxBitrate;              //可选，单位Mbps，动态调整最大码率
    @SerializedName("MinBitrate")
    private int minBitrate;              //可选，单位Mbps，动态调整最小码率
    @SerializedName("Bitrate")
    private int bitrate;                 //可选，固定码率(后台在该字段设置后minBitrate/maxBitrate参数无效)
    @SerializedName("Fps")
    private int fps;                     //可选，Fps
    @SerializedName("Role")
    private String role;                 //可选，互动云游的用户角色
    @SerializedName("HostUserId")
    private String hostUserId ;           //可选，互动云游的房主ID
    @SerializedName("AppId")
    private String appId;                //可选，应用ID
    @SerializedName("GameParas")
    private String gameParas;


    private final List<String> skipFiled = new ArrayList<>(); // 不希望生成Json的字段
    /**
     * @param clientSession 客户端请求参数
     */
    public GameStartParam(String clientSession) {
        this.clientSession = clientSession;
        // 先忽略掉所有字段
        for (Field field : GameStartParam.class.getDeclaredFields()) {
            skipFiled.add(field.getName());
        }
        skipFiled.remove("clientSession");
        skipFiled.remove("requestId");
    }

    public GameStartParam setGameCode(String gameCode) {
        if (!TextUtils.isEmpty(gameCode)) {
            skipFiled.remove("gameCode");
        }
        this.gameCode = gameCode;
        return this;
    }

    public GameStartParam setUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            skipFiled.remove("userId");
        }
        this.userId = userId;
        return this;
    }

    public GameStartParam setGameId(String gameId) {
        if (!TextUtils.isEmpty(gameId)) {
            skipFiled.remove("gameId");
        }
        this.gameId = gameId;
        return this;
    }

    public GameStartParam setGameRegion(String gameRegion) {
        if (!TextUtils.isEmpty(gameRegion)) {
            skipFiled.remove("gameRegion");
        }
        this.gameRegion = gameRegion;
        return this;
    }

    public GameStartParam setSetNo(long setNo) {
        if (setNo > 1) {
            skipFiled.remove("setNo");
        }
        this.setNo = setNo;
        return this;
    }

    public GameStartParam setGroupId(String groupId) {
        if (!TextUtils.isEmpty(groupId)) {
            skipFiled.remove("groupId");
        }
        this.groupId = groupId;
        return this;
    }

    public GameStartParam setResolution(String resolution) {
        if (!TextUtils.isEmpty(resolution)) {
            skipFiled.remove("resolution");
        }
        this.resolution = resolution;
        return this;
    }

    public GameStartParam setMaxBitrate(int maxBitrate) {
        if (maxBitrate > 0) {
            skipFiled.remove("maxBitrate");
        }
        this.maxBitrate = maxBitrate;
        return this;
    }

    public GameStartParam setMinBitrate(int minBitrate) {
        if (minBitrate > 0) {
            skipFiled.remove("minBitrate");
        }
        this.minBitrate = minBitrate;
        return this;
    }

    public GameStartParam setBitrate(int bitrate) {
        if (bitrate > 1) {
            skipFiled.remove("bitrate");
        }
        this.bitrate = bitrate;
        return this;
    }

    public GameStartParam setFps(int fps) {
        if (fps > 1) {
            skipFiled.remove("fps");
        }
        this.fps = fps;
        return this;
    }

    public GameStartParam setRole(String role) {
        this.role = role;
        return this;
    }

    public GameStartParam setHostUserId(String hostUserId) {
        if (!TextUtils.isEmpty(hostUserId)) {
            // 填了房主ID才需要带上房主ID、role两个参数
            skipFiled.remove("hostUserId");
            skipFiled.remove("role");
        }
        this.hostUserId = hostUserId;
        return this;
    }

    public GameStartParam setAppId(String appId) {
        if (!TextUtils.isEmpty(appId)) {
            skipFiled.remove("appId");
        }
        this.appId = appId;
        return this;
    }

    public GameStartParam setGameParas(String gameParas) {
        if (!TextUtils.isEmpty(gameParas)) {
            skipFiled.remove("gameParas");
        }
        this.gameParas = gameParas;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public String toJson() {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategies()).create().toJson(this);
    }


    private class ExclusionStrategies implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass() == GameStartParam.class
                    && skipFiled.contains(f.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
