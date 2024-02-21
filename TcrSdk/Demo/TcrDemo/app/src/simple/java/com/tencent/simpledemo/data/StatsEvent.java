package com.tencent.simpledemo.data;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class StatsEvent {

    /**
     * 远端设备cpu使用情况
     */
    @SerializedName("cpu")
    public String cpu;

    /**
     * 远端设备gpu使用情况
     */
    @SerializedName("gpu")
    public String gpu;

    /**
     * 解码帧数
     */
    @SerializedName("fps")
    public long fps;

    /**
     * 到STUN的往返时间
     */
    @SerializedName("rtt")
    public long rtt;

    /**
     * 通过网络接收到的字节计算的码率
     */
    @SerializedName("bitrate")
    public long bitrate;

    /**
     * 丢弃的包的个数
     */
    @SerializedName("packetLost")
    public int packetLost;

    public StatsEvent(String cpu, String gpu, long fps, long rtt, long bitrate, int packetLost) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.fps = fps;
        this.rtt = rtt;
        this.bitrate = bitrate;
        this.packetLost = packetLost;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(StatsEvent.this);
    }
}
