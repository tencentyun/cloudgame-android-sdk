package com.tencent.tcrdemo.utils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载sdk插件的工具类，下载完成后广播出下载状态
 */
public class DownloadManagerHelper {

    private static final String TAG = "DownloadManagerHelper";
    private final HashMap<Long, WeakReference<Listener>> mListeners = new HashMap<>();
    private DownloadManager mDownloadManager;
    //广播监听下载的各个状态
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 遍历监听器把下载状态通知出去
            for (Map.Entry<Long, WeakReference<Listener>> entry : mListeners.entrySet()) {
                DownloadManager.Query query = new DownloadManager.Query();
                // download Id
                query.setFilterById(entry.getKey());
                Cursor cursor = mDownloadManager.query(query);
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    WeakReference<Listener> listenerWeakReference = entry.getValue();
                    Listener listener;
                    if (listenerWeakReference != null && (listener = listenerWeakReference.get()) != null) {
                        listener.onStatusChange(status);
                    }

                    if (status == DownloadManager.STATUS_FAILED) {
                        int index = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                        Log.e(TAG, "Reason: " + cursor.getString(index));
                        return;
                    }
                    Log.e(TAG, "onStatusChange: " + status);
                }
            }
        }
    };
    private WeakReference<Context> mContextRef;

    private DownloadManagerHelper() {
    }

    public static DownloadManagerHelper getInstance() {
        return Holder.sINSTANCE;
    }

    public void setContext(Context context) {
        mContextRef = new WeakReference<>(context.getApplicationContext());

        if (VERSION.SDK_INT > VERSION_CODES.TIRAMISU) {
            mContextRef.get().registerReceiver(mReceiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else {
            mContextRef.get().registerReceiver(mReceiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    /**
     * 开始下载
     *
     * @param title 需要在标题栏显示的标题, 如果填空，那么标题栏不显示下载任务
     * @param description 标题栏任务描述
     * @param url 需要下载的url
     * @param destination 下载目标路径
     * @param listener 下载状态回调
     */
    public void download(String title, String description, String url, File destination, Listener listener) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "download() url empty");
            listener.onFailed("url is empty.");
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        if (TextUtils.isEmpty(title)) {
            request.setVisibleInDownloadsUi(false);
        } else {
            request.setTitle(title);
            request.setDescription(description);
            request.setVisibleInDownloadsUi(true);
        }

        if (destination == null) {
            Log.e(TAG, "download() destination=null");
            listener.onFailed("not destination");
            return;
        }

        request.setDestinationUri(Uri.fromFile(destination));

        Context context;
        if (mContextRef == null || (context = mContextRef.get()) == null) {
            Log.e(TAG, "download() context=null");
            listener.onFailed("Android Context is freed.");
            return;
        }

        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (mDownloadManager == null) {
            Log.e(TAG, "download() mDownloadManager=null");
            listener.onFailed("cannot get DownloadManger");
            return;
        }

        Log.i(TAG, "download:" + url);
        mListeners.put(mDownloadManager.enqueue(request), new WeakReference<>(listener));
    }

    public void stop() {
        mListeners.clear();
        Context context;
        if (mContextRef == null || (context = mContextRef.get()) == null) {
            return;
        }
        context.unregisterReceiver(mReceiver);
        mContextRef = null;
    }

    public interface Listener {

        /**
         * 下载状态回调
         *
         * @param status {@link DownloadManager#STATUS_PENDING}
         *         {@link DownloadManager#STATUS_RUNNING}
         *         {@link DownloadManager#STATUS_PAUSED}
         *         {@link DownloadManager#STATUS_SUCCESSFUL}
         *         {@link DownloadManager#STATUS_FAILED}
         */
        void onStatusChange(int status);

        /**
         * 下载失败, 压根没开始下载
         */
        void onFailed(String msg);
    }

    private static class Holder {

        private static final DownloadManagerHelper sINSTANCE = new DownloadManagerHelper();
    }

}
