package com.tencent.tcrdemo.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcrdemo.BuildConfig;

import java.io.File;

public class TcrSdkWrapper {
    private static final String TAG = "TcrSdkWrapper";

    private static class Holder {
        private static final TcrSdkWrapper sINSTANCE = new TcrSdkWrapper();
    }

    private TcrSdkWrapper() {}

    private AsyncCallback<Void> mInitCallback;
    private boolean mInitSdkSuccess = false;

    private final AsyncCallback<Void> mInitSdkCallback = new AsyncCallback<Void>() {

        @Override
        public void onSuccess(Void result) {
            Log.i(TAG, "sdk init success.");
            mInitSdkSuccess = true;
            ThreadUtil.runOnUiThread(() -> {
                if (mInitCallback != null) {
                    mInitCallback.onSuccess(null);
                }
            });
        }

        @Override
        public void onFailure(int code, String msg) {
            Log.i(TAG, "sdk init failed:" + code + " msg:" + msg);
            ThreadUtil.runOnUiThread(() -> {
                if (mInitCallback != null) {
                    mInitCallback.onFailure(0, "init sdk failed:" + msg);
                }
            });
        }
    };

    /**
     * 下载插件并初始化TcrSdk。<br
     * 该接口可以重复调用，当sdk已初始化成功的时候再次调用该接口会直接回调成功。<br>
     * 更关键的是，这个函数会保证callback在UI线程回调
     * @param callback 这个接口会在UI线程回调, 初始化成功时回调{@link AsyncCallback#onSuccess(Object)}, 下载插件失败或初始化Sdk失败时会回调{@link AsyncCallback#onFailure(int, String)}
     */
    public void init(Context context, @Nullable AsyncCallback<Void> callback) {
        Log.i(TAG, "init()");
        if (mInitSdkSuccess && callback != null) {
            Log.i(TAG, "init() mInitSdkSuccess=true");
            callback.onSuccess(null);
            return;
        }

        mInitCallback = callback;

        if (BuildConfig.useLightSdk) {
            String pluginUrl = TcrSdk.getPluginUrl();
            assert(pluginUrl != null);
            String pluginName = pluginUrl.substring(TcrSdk.getPluginUrl().lastIndexOf("/") + 1);
            File plugin = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), pluginName);

            Log.i(TAG, "plugin path:" + plugin.getAbsolutePath());
            if (plugin.exists()) {
                Log.d(TAG, "load plugin from local:" + plugin.getAbsolutePath());
                // 插件存在直接加载(有可能之前下载过, 也有可能是我们本地测试push进去的)
                TcrSdk.getInstance().init(context,
                        plugin.getAbsolutePath(),
                        mInitSdkCallback);
            } else {
                // 插件不存在需要先下载
                Toast.makeText(context, "下载插件中", Toast.LENGTH_SHORT).show();
                DownloadManagerHelper.getInstance().download("下载插件",
                        "使用轻量版SDK",
                        TcrSdk.getPluginUrl(),
                        plugin,
                        new DownloadManagerHelper.Listener() {
                            @Override
                            public void onStatusChange(int status) {
                                Log.d(TAG, "onStatusChange:" + status);
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    ThreadUtil.runOnUiThread(() -> {
                                        Toast.makeText(context, "插件下载成功，初始化sdk", Toast.LENGTH_SHORT).show();
                                        TcrSdk.getInstance().init(context,
                                                plugin.getAbsolutePath(),
                                                mInitSdkCallback);
                                    });
                                }
                            }

                            @Override
                            public void onFailed(String msg) {
                                Log.e(TAG, "download plugin failed:" + msg);
                                Toast.makeText(context, "插件下载失败,reason:" + msg, Toast.LENGTH_SHORT).show();
                                ThreadUtil.runOnUiThread(() -> {
                                    if (mInitCallback != null) {
                                        mInitCallback.onFailure(0, "download plugin failed:" + msg);
                                    }
                                });
                            }
                        });
            }
        } else {
            TcrSdk.getInstance().init(context, null, mInitSdkCallback);
        }
    }

    public void destroy() {
        mInitCallback = null;
        mInitSdkSuccess = false;
    }

    public static TcrSdkWrapper getInstance() {
        return Holder.sINSTANCE;
    }
}
