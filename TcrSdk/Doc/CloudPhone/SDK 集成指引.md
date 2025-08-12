## **运行环境**

Android 4.4及以上系统版本。



## 快速接入
<table>
<tr>
<td rowspan="1" colSpan="1" >SDK</td>

<td rowspan="1" colSpan="1" >下载地址</td>

<td rowspan="1" colSpan="1" >SDK 说明文档</td>

<td rowspan="1" colSpan="1" >版本发布日志</td>
</tr>

<tr>
<td rowspan="1" colSpan="1" >Android SDK</td>

<td rowspan="1" colSpan="1" >通过 Maven 集成</td>

<td rowspan="1" colSpan="1" >[接口说明](接口说明.md)</td>

<td rowspan="1" colSpan="1" >[Release notes](https://github.com/tencentyun/cloudgame-android-sdk/blob/master/TcrSdk/%E5%8F%91%E5%B8%83%E8%AE%B0%E5%BD%95.md)</td>
</tr>
</table>


### SDK集成

在应用模块的'build.gradle'中引用
``` bash
implementation 'com.tencent.tcr:tcrsdk-full:3.26.1'
```

### SDK使用
``` objectivec
// 请求云手机实例的访问信息AceessInfo和鉴权Token
private void requestAccessToken() {
    AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>> createAndroidInstancesAccessTokenCallback = new AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>>() {
        @Override
        public void onSuccess(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
            if (expServerResponse.Error != null) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken expServerResponse.Error: " + expServerResponse.Error);
                return;
            }
            if (expServerResponse.Response == null) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken Response: null");
                return;
            }

            // 初始化 TcrSdk
            initTcrSdk(expServerResponse);
        }

        @Override
        public void onFailure(int code, String errorMsg) {
            Log.e(TAG, "CreateAndroidInstancesAccessToken failed: " + code + ", " + errorMsg);
            Toast.makeText(FunctionActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg, Toast.LENGTH_LONG).show();
            finish();
        }
    };
    ......
}

// 初始化TcrSdk
private void initTcrSdk(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
    TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
    config.type = SdkType.CloudPhone;
    config.ctx = this;
    config.logger = new TcrLogger() {
        // 记录日志
    };
    config.callback = new AsyncCallback<Void>() {
        @Override
        public void onSuccess(Void result) {
            Log.i(TAG, "init TcrSdk success");
        }

        @Override
        public void onFailure(int code, String msg) {
            String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
            Log.e(TAG, "init TcrSdk fail. errorMsg: " + errorMsg);
        }
    };
    config.accessInfo = expServerResponse.Response.AccessInfo;
    config.token = expServerResponse.Response.Token;
    TcrSdk.getInstance().init(config);
}

// 创建Tcr会话对象
private void initTcrSession() {
    TcrSessionConfig tcrSessionConfig = TcrSessionConfig.builder().observer(mSessionObserver).build();
    mTcrSession = TcrSdk.getInstance().createTcrSession(tcrSessionConfig);
    if (mTcrSession == null) {
        Log.e(TAG, "mTcrSession = null");
        showToast("创建会话失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
    }
}

// 创建渲染视图
private void initTcrRenderView() {
    if (mTcrSession == null) {
        showToast("创建渲染视图必须有关联的会话", Toast.LENGTH_SHORT);
        return;
    }
    // 创建渲染视图
    mRenderView = TcrSdk.getInstance().createTcrRenderView(this, mTcrSession, TcrRenderViewType.SURFACE);
    if (mRenderView == null) {
        Log.e(TAG, "mRenderView = null");
        showToast("创建渲染失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
        return;
    }
    // 将渲染视图添加到界面上
    ((FrameLayout) findViewById(R.id.render_view_parent)).addView(mRenderView);
}

// Tcr会话的观察者，处理各类事件通知的消息和数据
private final Observer mSessionObserver = new Observer() {
    @Override
    public void onEvent(TcrSession.Event event, Object eventData) {
        switch (event) {
            case STATE_INITED:// 本地会话对象初始化完成
                // 可以开始连接指定的云手机。
                boolean ret = mTcrSession.access(mGroupInstanceIds, mIsGroupControl);
                if (!ret) {
                    showToast("连接云手机失败，请重试", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            case STATE_CONNECTED:// 成功和指定的云手机建立连接
                // 设置群控云手机的主控、同步列表、请求主控视频流
                if (mIsGroupControl) {
                    String masterId = mGroupInstanceIds.get(0);
                    TcrSdk.getInstance().getAndroidInstance().setMaster(masterId);
                    TcrSdk.getInstance().getAndroidInstance().setSyncList(mGroupInstanceIds);
                    TcrSdk.getInstance().getAndroidInstance().requestStream(masterId, "open", "low");
                }
                break;
            case STATE_RECONNECTING:
                showToast("重连中...", Toast.LENGTH_LONG);
                break;
            case STATE_CLOSED:
                showToast("会话关闭", Toast.LENGTH_SHORT);
                finish();
                break;
            default:
                break;
        }
    }
};
```



### Demo

完整接入代码可参考 [TcrCloudPhoneDemo](https://github.com/tencentyun/cloudgame-android-sdk/tree/master/TcrSdk/Demo/TcrCloudPhoneDemo)



