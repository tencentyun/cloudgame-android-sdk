# SDK集成指引

## 1. 架构与接入流程

整体架构分为三部分：

- **业务后台**：负责调用云 API 创建 AccessToken，并将 AccessToken 和 AccessInfo 转发给业务客户端。
- **云 API**：提供 HTTP/RESTful 接口，供业务后台创建并获取云手机实例的访问权限信息。
- **客户端 SDK**：业务客户端集成 SDK，实现云手机实例的截图、视频流、交互事件监听等功能。

![arch](https://cg-sdk-1258344699.cos.ap-nanjing.myqcloud.com/CloudDeviceWinSDK/docs/images/cloud_stream_arch.png)

**接入流程**

1. 业务后台对接云 API，通过 API 获取云手机的 `AccessToken` 和 `AccessInfo`。
2. 业务客户端集成 SDK，实现云手机的实例展示、串流和交互，包括截图、视频流、触控和按键事件等。

---

## 2. SDK集成

在应用模块（`app/build.gradle`）中添加依赖：

```groovy
implementation 'com.tencent.tcr:tcrsdk-full:3.31.0'
```

---

## 3. SDK初始化

业务后台获取到 `CreateAndroidInstancesAccessTokenResponse` 后，客户端调用 SDK 初始化接口。

### 示例代码

```java
private void initTcrSdk(CreateAndroidInstancesAccessTokenResponse serverResponse) {
    TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
    config.type = TcrSdk.SdkType.CloudStream;
    config.ctx = this;
    // 可选：设置SDK日志回调，便于定位及上报问题
    // config.logger = ...;
    config.callback = new AsyncCallback<>() {
        @Override
        public void onSuccess(Void result) {
            runOnUiThread(() -> {
                Log.i(TAG, "init TcrSdk success");
                Intent intent = new Intent(InstanceTokenActivity.this, InstanceScreenshotActivity.class);
                intent.putExtra("INSTANCE_IDS", etInstanceIds.getText().toString().trim());
                startActivity(intent);
                finish();
            });
        }
        @Override
        public void onFailure(int code, String msg) {
            runOnUiThread(() -> {
                String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
                Toast.makeText(InstanceTokenActivity.this, "初始化TcrSdk失败: " + code + ", " + errorMsg,
                        Toast.LENGTH_LONG).show();
                finish();
            });
        }
    };
    config.accessInfo = serverResponse.AccessInfo;
    config.token = serverResponse.Token;
    TcrSdk.getInstance().init(config);
}
```

**说明**：
- `serverResponse` 由业务后台返回，包括 `Token` 和 `AccessInfo` 字段。
- 日志回调建议接入并存储文件，便于 SDK 问题排查。

---

## 4. 云手机截图

通过 SDK 接口，可以获取云手机实例的截图 url，实现预览或展示。

### 示例代码

```java
private void updateScreenshots() {
    for (ScreenshotItem item : screenshotItems) {
        Map<String, Object> params = new HashMap<>();
        params.put("instance_id", item.getInstanceId());
        AndroidInstance instance = TcrSdk.getInstance().getAndroidInstance();
        if (instance == null) {
            Log.e(TAG, "instance is null");
            return;
        }
        String imageUrl = instance.getInstanceImageAddress(params);
        if (TextUtils.isEmpty(imageUrl)) {
            Log.e(TAG, "imageUrl is empty");
            return;
        }
        item.setImageUrl(imageUrl);
    }
}
```

---

## 5. 云实例串流与交互

### 5.1 会话连接

- 创建 TcrSession 对象并注册事件观察者 Observer。
- Observer 负责处理会话初始化、连接、重连、关闭及云端屏幕/视频流变化等。

```java
// 初始化会话对象
private void initTcrSession() {
    TcrSessionConfig sessionConfig = TcrSessionConfig.builder().observer(mSessionObserver).build();
    mTcrSession = TcrSdk.getInstance().createTcrSession(sessionConfig);
    if (mTcrSession == null) {
        Log.e(TAG, "mTcrSession = null");
        showToast("创建会话失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
    }
}
```

**核心回调示例**（建议按实际开发场景扩展处理逻辑）:

```java
private final Observer mSessionObserver = new Observer() {
    @Override
    public void onEvent(TcrSession.Event event, Object eventData) {
        switch (event) {
            case STATE_INITED:              // 本地会话对象初始化完成
                boolean ret = mTcrSession.access(mGroupInstanceIds, mIsGroupControl);
                if (!ret) {
                    showToast("连接云手机失败，请重试", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            case STATE_CONNECTED:           // 成功建立连接
                // 群控模式下可设置主控（从哪个实例串流）、同步列表(群控哪些实例)、请求流(清晰度、暂停/恢复推流)
                if (mIsGroupControl) {
                    String masterId = mGroupInstanceIds.get(0);
                    TcrSdk.getInstance().getAndroidInstance().setMaster(masterId);
                    TcrSdk.getInstance().getAndroidInstance().setSyncList(mGroupInstanceIds);
                    TcrSdk.getInstance().getAndroidInstance().requestStream(masterId, "open", "low");
                }
                break;
            case STATE_RECONNECTING:        // 会话重连中
                showToast("重连中...", Toast.LENGTH_LONG);
                break;
            case STATE_CLOSED:              // 会话关闭(连接断开或连接失败)
                showToast("会话关闭", Toast.LENGTH_SHORT);
                finish();
                break;
            case SCREEN_CONFIG_CHANGE:      // 屏幕参数变化
                mScreenConfig = (ScreenConfig) eventData;
                mScreenConfigChanged = true;
                updateRotation();
                break;
            case VIDEO_STREAM_CONFIG_CHANGED:// 视频参数变化
                mVideoStreamConfig = (VideoStreamConfig) eventData;
                mVideoStreamConfigChanged = true;
                updateRotation();
                break;
            // 其它事件
            default:
                Log.d(TAG, "Event:" + event.name() + " Data: " + eventData);
                break;
        }
    }
};
```

---

### 5.2 渲染云端画面

- 创建 TcrRenderView，并添加至页面渲染区域。

```java
private void initTcrRenderView() {
    if (mTcrSession == null) {
        showToast("创建渲染视图必须有关联会话", Toast.LENGTH_SHORT);
        return;
    }
    mRenderView = TcrSdk.getInstance().createTcrRenderView(this, mTcrSession, TcrRenderViewType.SURFACE);
    if (mRenderView == null) {
        Log.e(TAG, "mRenderView = null");
        showToast("创建渲染失败，请检查TcrSdk是否初始化成功", Toast.LENGTH_SHORT);
        return;
    }
    ((FrameLayout) findViewById(R.id.render_view_parent)).addView(mRenderView);
}
```
**说明**：  
`TcrRenderView`除了负责渲染云端画面以外，还负责把用户的触摸事件转发到云端。


---

### 5.3 云端屏幕及画面方向同步

**说明**：  
云端设备横竖屏或视频分辨率发生变化时，本地应调整屏幕和画面方向，使两端保持一致。

```java
private void updateRotation() {
    // 屏幕和视频流的配置已变化时进行同步
    if (!mScreenConfigChanged || !mVideoStreamConfigChanged) return;

    boolean isLandscape = mScreenConfig.degree.equals("90_degree") || mScreenConfig.degree.equals("270_degree");
    boolean isPortrait = mScreenConfig.degree.equals("0_degree") || mScreenConfig.degree.equals("180_degree");

    if (mVideoStreamConfig.width > mVideoStreamConfig.height) {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    } else {
        if (isPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    // 旋转画面
    switch (mScreenConfig.degree) {
        case "0_degree":   mRenderView.setVideoRotation(VideoRotation.ROTATION_0); break;
        case "90_degree":  mRenderView.setVideoRotation(VideoRotation.ROTATION_270); break;
        case "180_degree": mRenderView.setVideoRotation(VideoRotation.ROTATION_180); break;
        case "270_degree": mRenderView.setVideoRotation(VideoRotation.ROTATION_90); break;
    }
}
```

**注意：**  
请确保 `Manifest` 里 Activity 配置了  
`android:configChanges="orientation|screenSize"`  
避免旋转时 Activity 重建，影响云手机体验。

---

### 5.4 键盘事件交互

支持通过 SDK 发送 Home/Back/Menu 等按键，实现远程控制云手机。

```java
private static final int KEY_BACK = 158;
private static final int KEY_MENU = 139;
private static final int KEY_HOME = 172;

private void onHomeKeyPressed() {
    if (mTcrSession != null) {
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_HOME, false);
    }
}

private void onBackKeyPressed() {
    if (mTcrSession != null) {
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_BACK, false);
    }
}

private void onMenuKeyPressed() {
    if (mTcrSession != null) {
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, true);
        mTcrSession.getKeyboard().onKeyboard(KEY_MENU, false);
    }
}
```

---

### 5.5 会话与资源释放

建议在 Activity/Fragment 生命周期结束时，及时释放云会话及渲染视图资源，避免内存泄漏。

```java
@Override
protected void onDestroy() {
    if (mTcrSession != null) {
        mTcrSession.release();
    }
    if (mRenderView != null) {
        mRenderView.release();
    }
    super.onDestroy();
}
```

---

## 6. 常见问题及建议

1. **SDK日志采集**：正式发布前需要将 SDK 日志写入文件或上传，便于异常情况反馈问题。
2. **权限要求**：需申请存储、网络等基本运行权限。
3. **后台服务接口**：可联系开发团队获取后台源码。