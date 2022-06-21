## 接入步骤

请参照[SDK接入指南](../SDK接入指南.md)修改后台server地址以及GameID

1. 在应用模块中的'build.gradle'中加入:

```xml
implementation 'com.tencent.tcr:tcrsdk-lite:2.0.0-SNAPSHOT'
```

2. AndroidManifest 配置权限：

``` java
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

## 调用示例

轻量版与全量版在使用方面的唯一区别为在初始化SDK前需要下载和加载插件。

加载plugin和初始化SDK代码示例

``` java
private void initSDKPlugin() {
        File dest = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "plugin");
        TLog.d(TAG, "plugin path:" + dest.getAbsolutePath());
        DownloadManagerHelper.getInstance().download("下载插件",
                "使用轻量版SDK",
                TcrSdk.getPluginUrl(),
                dest,
                new DownloadManagerHelper.Listener() {
                    @Override
                    public void onStatusChange(int status) {
                        TLog.d(TAG, "onStatusChange:" + status);
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            ThreadUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    TcrSdk.getInstance().init(mContext,
                                            dest.getAbsolutePath(),
                                            mInitSdkCallback);
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailed(String msg) {
                       TLog.d(TAG,"download plugin failed : "+msg);
                    }
                });
    }
```



## 流程

- 客户端获取的 clientSession 作为参数传递给 App 后台（传递方式业务方可以自行实现），业务后台请求云 API 锁定机器并创建会话，拿到 serverSession。
- 客户端通过 serverSession 启动游戏。其中，客户端传递 clientSession，App 后台返回 serverSession，传递方式 App 可自行实现。
- 首先下载SDK插件，下载成功后再初始化SDK
- 客户端通过TcrSdk完成对SDK的初始化以及渲染画面的创建。

![](https://tva1.sinaimg.cn/large/e6c9d24egy1h30vhw31r2j207t0kljrx.jpg)

