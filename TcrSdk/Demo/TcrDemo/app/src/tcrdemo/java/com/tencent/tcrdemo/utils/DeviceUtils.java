package com.tencent.tcrdemo.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class DeviceUtils {

    /**
     * 返回设备屏幕的宽高
     * @return Pair<宽,高>
     */
    public static Pair<Integer, Integer> getScreenSize(@NonNull Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        if (windowManager == null) {
            return null;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return new Pair<>(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }
}
