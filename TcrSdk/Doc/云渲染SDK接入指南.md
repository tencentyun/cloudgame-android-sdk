# 准备工作
请先理解腾讯云渲染的业务逻辑和前后端交互流程，申请好腾讯云渲染服务和搭建好业务后台程序。（参考[链接](../README.md)）

# 调用云渲染Android SDK的主要流程
<br>
<img src="images/云渲染sdk调用流程.jpeg" height="500px">
<br>

# 接入云渲染SDK的步骤

1. 集成SDK。在应用模块的'build.gradle'中引用

```java
implementation 'com.tencent.tcr:tcrsdk-full:2.0.0'
```

如果选择集成轻量版SDK，则引用

```java
implementation 'com.tencent.tcr:tcrsdk-lite:2.0.0' 
```

2. AndroidManifest 配置网络权限：

```java
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

3. 初始化SDK

```java
// 初始化SDK
TcrSdk.getInstance().init(this, null, mInitSdkCallback);
```

如果引用的是轻量版SDK，则需要应用先下载SDK插件，再调用init()函数时通过第二个参数传入插件文件的路径。下载SDK插件的URL可以通过 TcrSdk.getPluginUrl() 获取。

4. 初始化SDK，异步回调成功后，可以创建和初始化会话对象、渲染视图。

```java
// 创建和初始化会话对象
mTcrSession = TcrSdk.getInstance().createTcrSession(createSessionConfig());
mTcrSession.init(mInitSessionCallback);
// 创建和添加渲染视图
mRenderView = TcrSdk.getInstance().createTcrRenderView(PCGameActivity.this, mTcrSession, TcrRenderViewType.SURFACE);
((FrameLayout) PCGameActivity.this.findViewById(R.id.main)).addView(mRenderView);
// 为会话设置渲染视图
mTcrSession.setRenderView(mRenderView);
```

5. 初始化会话对象，异步回调成功后，可以获得clientSession，用于进一步请求业务后台，再调用云API，启动指定游戏实例并返回serverSession。客户端调用会话对象的start()接口传入serverSession参数，就可以启动会话，发起SDK到云端的连接。启动会话异步回调成功后，客户端程序就会显示出云端应用的画面。

```java
private final AsyncCallback<String> mInitSessionCallback = new AsyncCallback<String>() {
    @Override
    public void onSuccess(String clientSession) {
        Log.i(TAG, "init session success, clientSession:" + clientSession);

        // 会话初始化成功后拿到clientSession， 请求后台启动游戏并获取ServerSession
        CloudGameApi.getInstance().startGame(clientSession, response -> {
            Log.i(TAG, "start game success: " + response);
            // 用从服务端获取到的server session启动会话
            GameStartResponse result = new Gson().fromJson(response.toString(), GameStartResponse.class);
            if (result.code == 0) {
                mTcrSession.start(result.sessionDescribe.serverSession, mStartSessionCallback);
            }
        }, error -> Log.e(TAG, "start game failed:" + error));
    }

    @Override
    public void onFailure(int code, String msg) {
        Log.e(TAG, "onFailure code:" + code + " msg:" + msg);
    }
};
```

请求业务后台的接口由业务自定义，在[Demo](../Demo)例子中，封装在CloudGameApi类里。

6. 除了能在客户端上看到云端应用的画面，我们通常还需要能操作应用，即把客户端的操作同步给云端。SDK提供了KeyBoard、Mouse、GamePad等抽象对象，客户端可以调用这些对象的接口，实现与云端输入设备的交互。  
同时，Android SDK还实现了默认的屏幕触摸处理器：MobileTouchHandler 和 PcTouchHandler。MobileTouchHandler针对云端为手机应用的场景，将本地屏幕触摸事件同步给云端；PcTouchHandler针对云端为PC应用的场景，将本地屏幕触摸事件转化为云端的鼠标移动、单击、长按、双击事件。您也可以自定义实现自己的屏幕触摸处理器。将屏幕触摸处理器设置给TcrRenderView即可使用。

```java
// 手机应用
MobileTouchHandler mobileTouchHandler = new MobileTouchHandler();
mRenderView.setOnTouchListener(mobileTouchHandler);

// PC应用
PcTouchHandler pcTouchHandler = new PcTouchHandler();
mRenderView.setOnTouchListener(pcTouchHandler);
```

<br><p>
**以上就是接入的核心步骤，具体代码可以参考[Demo](../Demo)。**

# FAQ
1. **云渲染SDK支持的最低Android系统版本。**  
Android 4.1（API 级别 16）。

2. **完整版SDK 和 轻量版SDK，该如何选择。**  
轻量版SDK需要客户端程序先从网络上下载插件文件，在初始化SDK时传入给SDK进行动态加载。除此之外，二者在使用接口上并无区别。如果您对APP包体积大小有严格要求，可以选择集成轻量版SDK；否则，推荐使用完整版SDK。
