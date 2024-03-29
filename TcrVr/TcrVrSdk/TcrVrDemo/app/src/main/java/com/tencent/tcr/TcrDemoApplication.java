package com.tencent.tcr;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class TcrDemoApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static volatile Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

    }

    public static Context getContext() {
        return sContext;
    }
}