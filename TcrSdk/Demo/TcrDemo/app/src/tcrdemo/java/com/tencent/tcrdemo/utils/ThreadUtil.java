package com.tencent.tcrdemo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * 线程工具类
 */
public class ThreadUtil {

    private static final Thread sMainThread = Looper.getMainLooper().getThread();
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 将任务抛进主线程执行
     *
     * @param runnable 传入需要执行的任务
     */
    public static void runOnUiThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            sMainHandler.post(runnable);
        }
    }

    /**
     * 判断当前线程是否为主线程
     *
     * @return true:主线程 false:非主线程
     */
    public static boolean isMainThread() {
        return sMainThread == Thread.currentThread();
    }
}
