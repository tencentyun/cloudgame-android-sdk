package com.tencent.advancedemo;

import android.app.Application;

/**
 * 应用程序
 */
public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化和业务后台交互的类
        CloudRenderBiz.getInstance().init(this);


    }
}
