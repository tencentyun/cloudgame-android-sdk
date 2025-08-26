package com.tencent.tcrdemo.gameplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.tencent.component.utils.LogUtils;
import com.tencent.tcr.Proxy;

/**
 * ProxyService
 * <p>
 * 本服务提供代理的初始化、启动和停止功能，采用前台服务模式保证在Android系统下的后台存活能力。
 *
 * ### 功能概述：
 * - 初始化代理：通过`initProxy`方法传入云端中继信息。
 * - 启动代理：通过`startProxy`方法开启网络流量转发。
 * - 停止代理：通过`stopProxy`方法终止代理服务。
 *
 * ### 典型使用流程：
 * 1. 初始化代理：调用`initProxy(Context context, String relayInfoJson)`。
 * 2. 启动代理：调用`startProxy(Context context)`。
 * 3. 停止代理：调用`stopProxy(Context context)`。
 *
 * ### 注意事项：
 * - 必须按顺序调用：先初始化 → 再启动 → 最后停止。
 * - 初始化操作是异步执行，中继信息会持久化存储到SharedPreferences中。
 * - 前台服务会显示持续运行的通知（Android 8.0+要求）。
 * - 需要在AndroidManifest.xml中声明服务：
 *   {@code <service android:name=".gameplay.ProxyService"/>}
 *
 * @see com.tencent.tcr.Proxy
 */
public class ProxyService extends Service {
    // 常量定义
    private static final String TAG = "ProxyService";
    private static final String CHANNEL_ID = "proxy_service_channel";
    private static final String PREF_NAME = "ProxyServicePrefs";
    private static final String KEY_RELAY_INFO = "relay_info_json";
    private static boolean isInitialized = false;

    // Intent Actions
    public static final String ACTION_START_PROXY = "com.tencent.tcr.proxy.action.START_PROXY";
    public static final String ACTION_STOP_PROXY = "com.tencent.tcr.proxy.action.STOP_PROXY";

    // 成员变量
    private HandlerThread handlerThread;
    private Handler handler;

    // 生命周期方法
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate: initializing service");
        handlerThread = new HandlerThread("ProxyServiceThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand received: " + (intent != null ? intent.getAction() : "null intent"));
        if (intent == null) return START_STICKY;

        String action = intent.getAction();
        if (ACTION_START_PROXY.equals(action)) {
            handleStartProxy();
        } else if (ACTION_STOP_PROXY.equals(action)) {
            handleStopProxy();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "Service is being destroyed. Stopping proxy and quitting handler thread.");
        handler.post(() -> Proxy.getInstance().stopProxy());
        handlerThread.quitSafely();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // 不支持绑定
    }

    private void startForegroundService() {
        LogUtils.i(TAG, "Creating notification channel for foreground service");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Proxy Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Proxy Running")
                .setContentText("Proxy service is running")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .build();
        startForeground(1, notification);
        LogUtils.i(TAG, "Foreground service started with notification");
    }

    private String readRelayInfoFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_RELAY_INFO, null);
    }

    private void initProxyIfNeeded() {
        String relayInfoJson = readRelayInfoFromPrefs();
        if (relayInfoJson != null && !isInitialized) {
            Proxy.getInstance().init(relayInfoJson);
            isInitialized = true;
        }
    }

    private void handleStartProxy() {
        handler.post(() -> {
            initProxyIfNeeded();
            if (isInitialized) {
                Proxy.getInstance().startProxy();
                LogUtils.i(TAG, "Proxy started successfully");
            } else {
                LogUtils.e(TAG, "No relay info found in SharedPreferences");
            }
        });
    }

    private void handleStopProxy() {
        handler.post(() -> {
            initProxyIfNeeded();
            if (isInitialized) {
                Proxy.getInstance().stopProxy();
                LogUtils.i(TAG, "Proxy stopped");
            } else {
                LogUtils.e(TAG, "No relay info found in SharedPreferences");
            }
        });
    }

    // 静态方法
    public static void initProxy(Context context, String relayInfoJson) {
        LogUtils.i(TAG, "Initializing proxy with relay info: " + (relayInfoJson != null ? "[REDACTED]" : "null"));
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_RELAY_INFO, relayInfoJson);
        editor.apply();
    }

    public static void startProxy(Context context) {
        LogUtils.i(TAG, "Starting proxy service");
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_START_PROXY);
        context.startService(intent);
    }

    public static void stopProxy(Context context) {
        LogUtils.i(TAG, "Stopping proxy service");
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_STOP_PROXY);
        context.startService(intent);
    }
}