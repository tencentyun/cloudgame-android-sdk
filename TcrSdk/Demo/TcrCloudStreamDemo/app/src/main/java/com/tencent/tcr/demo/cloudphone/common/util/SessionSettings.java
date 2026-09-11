package com.tencent.tcr.demo.cloudphone.common.util;

import com.tencent.tcr.sdk.api.TcrSessionConfig.VideoCodecType;

/**
 * 串流会话参数的内存存取工具（仅当次运行有效，应用重启后恢复默认，不做持久化）。<br>
 * 由 {@link com.tencent.tcr.demo.cloudphone.common.ui.SessionSettingsActivity}（可选进入）写入，
 * {@link com.tencent.tcr.demo.cloudphone.common.ui.PlayActivity} 创建会话时读取。
 */
public final class SessionSettings {

    private static VideoCodecType sPreferredCodec;

    private SessionSettings() {
    }

    // 读取首选视频编码，null 表示"不设置"（创建会话时不指定，由 SDK 使用默认编解码策略）
    public static VideoCodecType getPreferredCodec() {
        return sPreferredCodec;
    }

    // 保存首选视频编码，codec 传 null 表示"不设置"
    public static void savePreferredCodec(VideoCodecType codec) {
        sPreferredCodec = codec;
    }
}
