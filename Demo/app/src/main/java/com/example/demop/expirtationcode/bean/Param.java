package com.example.demop.expirtationcode.bean;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Param {
    public String RequestId = requestId();

    private static String expiredTime() {
        Date date = new Date(System.currentTimeMillis() + 6 * 24 * 60 * 60 * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        return format.format(date);
    }

    private static String requestId() {
        return (Build.MANUFACTURER + Build.DEVICE + Build.BRAND + Build.MODEL) + expiredTime();
    }

}
