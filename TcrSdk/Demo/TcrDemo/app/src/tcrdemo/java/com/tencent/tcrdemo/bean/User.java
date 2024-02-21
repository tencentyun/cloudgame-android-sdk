package com.tencent.tcrdemo.bean;

import com.tencent.tcr.sdk.api.data.MultiUser.Role;

public class User {
    // 用户角色
    public Role role;
    // 用户在角色中的席位
    public int seatIndex;
    // 用户ID
    public String userID;
    // 用户是否禁音
    public boolean muteChecked;
}
