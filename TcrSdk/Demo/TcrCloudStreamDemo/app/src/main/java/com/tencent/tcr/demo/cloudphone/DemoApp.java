package com.tencent.tcr.demo.cloudphone;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
        sQueue = Volley.newRequestQueue(sContext);
    }
}
