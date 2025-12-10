package com.tencent.tcrdemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.tencent.bugly.crashreport.CrashReport;

public class TcrDemoApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        CrashReport.initCrashReport(sContext, "b21f18effe", false);
    }
}