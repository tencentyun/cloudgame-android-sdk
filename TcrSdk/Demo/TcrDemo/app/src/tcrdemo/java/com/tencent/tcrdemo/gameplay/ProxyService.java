package com.tencent.tcrdemo.gameplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
 * ### 典型使用流程：
 * 1. 初始化代理：调用 {@link #initProxy(Context, String)} 传入云端中继信息
 * 2. 启动代理：调用 {@link #startProxy(Context)} 开启网络流量转发
 * 3. 停止代理：调用 {@link #stopProxy(Context)} 终止代理服务
 *
 * ### 注意事项：
 * - 必须按顺序调用：先初始化 → 再启动 → 最后停止
 * - 初始化操作是异步执行
 * - 前台服务会显示持续运行的通知（Android 8.0+要求）
 * - 需要在AndroidManifest.xml声明服务：
 *   {@code <service android:name=".gameplay.ProxyService"/>}
 */
public class ProxyService extends Service {
    private static final String TAG = "ProxyService";
    public static final String ACTION_START_PROXY = "com.tencent.tcr.proxy.action.START_PROXY";
    public static final String ACTION_STOP_PROXY = "com.tencent.tcr.proxy.action.STOP_PROXY";
    public static final String ACTION_INIT_PROXY = "com.tencent.tcr.proxy.action.INIT_PROXY";
    public static final String EXTRA_RELAY_INFO_JSON = "relay_info_json";

    private static final String CHANNEL_ID = "proxy_service_channel";
    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate: initializing service");
        handlerThread = new HandlerThread("ProxyServiceThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        startForegroundService();
    }

    /**
     * 启动前台服务（Foreground Service）
     *
     * 为什么需要前台服务？
     * - Android 8.0 及以上系统对后台服务有严格限制，普通 Service 很容易被系统杀死, 前台服务系统不会轻易回收
     */
    private void startForegroundService() {
        LogUtils.i(TAG, "startForegroundService: creating notification channel");
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand received: " + (intent != null ? intent.getAction() : "null intent"));
        if (intent == null) return START_STICKY;

        String action = intent.getAction();
        if (ACTION_INIT_PROXY.equals(action)) {
            final String relayInfoJson = intent.getStringExtra(EXTRA_RELAY_INFO_JSON);

            handler.post(() -> {
                boolean result = Proxy.getInstance().init(relayInfoJson);
                LogUtils.i(TAG, "Proxy initialization result: " + result);
            });
        } else if (ACTION_START_PROXY.equals(action)) {
            handler.post(() -> {
                Proxy.getInstance().startProxy();
                LogUtils.i(TAG, "Proxy started successfully");
            });
        } else if (ACTION_STOP_PROXY.equals(action)) {
            handler.post(() -> {
                Proxy.getInstance().stopProxy();
                LogUtils.i(TAG, "Proxy stopped");
            });
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "onDestroy: stopping proxy and quitting thread");
        handler.post(() -> Proxy.getInstance().stopProxy());
        handlerThread.quitSafely();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // 不支持绑定
    }

    /**
     * 初始化代理服务
     *
     * @param context 上下文对象，用于启动服务
     * @param relayInfoJson 云端下发的代理中继信息，
     *        对应{@link com.tencent.tcr.Proxy#init(String)}参数要求
     *
     * @see com.tencent.tcr.Proxy#init(String)
     */
    public static void initProxy(Context context, String relayInfoJson) {
        LogUtils.i(TAG, "initProxy called with relay info");
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_INIT_PROXY);
        intent.putExtra(EXTRA_RELAY_INFO_JSON, relayInfoJson);
        context.startService(intent);
    }

    /**
     * 启动代理服务
     *
     * @param context 上下文对象，用于启动服务
     *
     * ### 功能说明：
     * - 启动后本地设备将转发云端实例的所有网络请求
     * - 注意：设备上行带宽限制可能导致云端网络延迟/丢包
     *
     * @see com.tencent.tcr.Proxy#startProxy()
     */
    public static void startProxy(Context context) {
        LogUtils.i(TAG, "startProxy called");
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_START_PROXY);
        context.startService(intent);
    }

    /**
     * 停止代理服务
     *
     * @param context 上下文对象，用于启动服务
     *
     * @see com.tencent.tcr.Proxy#stopProxy()
     */
    public static void stopProxy(Context context) {
        LogUtils.i(TAG, "stopProxy called");
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_STOP_PROXY);
        context.startService(intent);
    }
}