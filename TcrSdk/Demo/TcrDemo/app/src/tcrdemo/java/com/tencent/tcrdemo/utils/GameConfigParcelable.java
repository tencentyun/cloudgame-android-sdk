package com.tencent.tcrdemo.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 体验页跳转到游戏页面传递的参数
 */
public class GameConfigParcelable implements Parcelable {

    public static final String GAME_CONFIG_KEY = "game_config";
    public static final Creator<GameConfigParcelable> CREATOR = new Creator<GameConfigParcelable>() {
        @Override
        public GameConfigParcelable createFromParcel(Parcel in) {
            return new GameConfigParcelable(in);
        }

        @Override
        public GameConfigParcelable[] newArray(int size) {
            return new GameConfigParcelable[size];
        }
    };
    public String experienceCode;        //必选，体验码
    public boolean isTestEnv;
    public boolean isIntlEnv;
    public boolean isEnablePlayer;
    public String userId;
    public String hostUserId;

    /**
     * 创建一个GameConfigParcelable
     */
    public GameConfigParcelable(String experienceCode,
                                boolean isTest, String userID, String hostUserId, boolean isPlayer,boolean isIntlEnv) {
        this.experienceCode = experienceCode;
        isTestEnv = isTest;
        this.isIntlEnv = isIntlEnv;
        userId = userID;
        this.hostUserId = hostUserId;
        isEnablePlayer = isPlayer;
    }

    protected GameConfigParcelable(Parcel in) {
        experienceCode = in.readString();
        isTestEnv = in.readInt() == 1;
        isIntlEnv = in.readInt() == 1;
        userId = in.readString();
        hostUserId = in.readString();
        isEnablePlayer = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(experienceCode);
        dest.writeInt(isTestEnv ? 1 : 0);
        dest.writeInt(isIntlEnv ? 1 : 0);
        dest.writeString(userId);
        dest.writeString(hostUserId);
        dest.writeInt(isEnablePlayer ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Override
    public String toString() {
        return "GameConfigParcelable{" +
                "ExperienceCode='" + experienceCode + '\'' +
                ", IsTestEnv=" + isTestEnv +
                '}';
    }
}
