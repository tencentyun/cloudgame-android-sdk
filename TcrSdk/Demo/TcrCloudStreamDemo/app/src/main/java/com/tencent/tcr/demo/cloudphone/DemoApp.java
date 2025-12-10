package com.tencent.tcr.demo.cloudphone;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tencent.bugly.crashreport.CrashReport;

public class DemoApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private static RequestQueue sQueue;

    public static Context getContext() {
        return sContext;
    }

    public static RequestQueue getQueue() {
        return sQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        CrashReport.initCrashReport(sContext, "b21f18effe", false);
        sQueue = Volley.newRequestQueue(sContext);
    }
}
