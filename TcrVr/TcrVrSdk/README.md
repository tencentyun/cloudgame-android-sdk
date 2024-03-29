# SDK说明

## 一、SDK
相关SDK已发布到mavenCentral()，推荐通过Maven方式引用。

- Pico平台SDK：  implementation "com.tencent.tcr:vr-sdk-pico:1.0.10"
- Oculus平台SDK：implementation "com.tencent.tcr:vr-sdk-oculus:1.0.10"

最新版本请参考[发布记录](发布记录.md)


## 二、前后端交互流程

腾讯云渲染VR SDK是基于云渲染SDK封装的一个组件，其内部直接调用了云渲染SDK接口。前后端交互流程和接入云渲染SDK的交互流程一致。
<br><br>
<img src="云渲染VR前后端交互逻辑_CN.png" width="700px">
<br><br>

## 三、SDK接入流程

VR SDK暴露了一个XrBaseActivity供客户端应用程序接入.

### **步骤1：继承XrBaseActivity**

新建一个Activity并继承XrBaseActivity, 例如YourActivity。

### **步骤2：修改AndroidManifest.xml文件**

在AndroidManifest.xml文件中声明新建的Activity, 并包含以下内容:

```
    <activity
            android:name="YourActivity"
            android:configChanges="screenSize|screenLayout|orientation|keyboardHidden|keyboard|navigation|uiMode|density"
            android:excludeFromRecents="false"
            android:launchMode="singleTask"
            android:screenOrientation="landscape"
            tools:ignore="NonResizeableActivity"
            android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen"
            >
        <!-- Pico配置 -->
        <meta-data android:name="pvr.app.type" android:value="vr" />

        <!-- Oculus配置 -->
        <meta-data android:name="com.samsung.android.vr.application.mode" android:value="vr_only"/>

        <!-- 告诉YourActivity应该加载哪个.so文件 -->
        <meta-data
                android:name="android.app.lib_name"
                android:value="tcr_xr" />

        <intent-filter >
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>

        <intent-filter>
            <category android:name="com.oculus.intent.category.VR" />
            <category android:name="org.khronos.openxr.intent.category.IMMERSIVE_HMD" />
        </intent-filter>
    </activity>
```

### **步骤3：处理并调用TcrActivity相关接口**

作为接入方，你需要关注以下几个函数
 - 初始化成功: {@link XrBaseActivity#onInitSuccess(String)}, 当程序初始化成功时会回调该函数，你需要拿到回调的clientSession请求服务端获取ServerSession。
 - 初始化失败: {@link XrBaseActivity#onInitFailed()}}, 当程序初始化失败时回调该函数，通常你只能够退出，或提示用户当前出错让用户自行退出。
 - 启动会话: {@link XrBaseActivity#start(String)}, 当你请求服务端拿到ServerSession时可以调用该函数启动会话建立连接。
 - 启动会话成功: {@link XrBaseActivity#onConnected()}, 告知你当前会话与云端建立连接，接下来等待视频流渲染。如果客户端需要创建数据通道，也应该在这个时机进行创建。
 - 设置大厅文本: {@link XrBaseActivity#setLobbyText(String)}, 在视频流渲染之前或渲染失败后你可以通过该函数设置在大厅显示的文本。
 - 功能配置: {@link XrBaseActivity#onUpdateConfigBuilder(Builder)}, 重写该方法通过传入的Builder对云渲染进行设置。
 - 获取手柄事件: {@link XrBaseActivity#onUpdateCtrlEvents(List)}, 通过重写该方法能够获取到所有的手柄事件状态。
 - 创建数据通道: {@link XrBaseActivity#createCustomDataChannel(int, Observer)}, 通过调用该方法可以创建和云端进行交互的数据通道。