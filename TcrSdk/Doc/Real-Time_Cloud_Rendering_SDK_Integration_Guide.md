- [中文文档](云渲染SDK接入指南.md)

# Prerequisites
Make sure that you understand the business logic and frontend-backend interaction process of the Real-Time Cloud Rendering service, have activated the service, and have set up the business backend program (for more information, see [here](../README_EN-US.md)).


# Steps for Integrating the Real-Time Cloud Rendering SDK

1. Integrate the SDK. Import the following content into `build.gradle` of the application module:

```java
implementation 'com.tencent.tcr:tcrsdk-full:2.1.0'
```

To integrate the lightweight SDK, import the following content:

```java
implementation 'com.tencent.tcr:tcrsdk-lite:2.1.0' 
```

2. Configure the network permissions in `AndroidManifest`:

```java
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

3. Initialize the SDK.

```java
// Initialize the SDK.
TcrSdk.getInstance().init(this, null, mInitSdkCallback);
```

To import the lightweight SDK, the application needs to download the SDK plugin first and pass in the plugin file path to the second parameter when calling the `init()` function. You can get the SDK plugin download URL through `TcrSdk.getPluginUrl()`.

4. Initialize the SDK. After the async callback succeeds, you can create and initialize the session object and rendering view.

```java
// Create and initialize the session object.
mTcrSession = TcrSdk.getInstance().createTcrSession(createSessionConfig());
mTcrSession.init(mInitSessionCallback);
// Create and add the rendering view.
mRenderView = TcrSdk.getInstance().createTcrRenderView(PCGameActivity.this, mTcrSession, TcrRenderViewType.SURFACE);
((FrameLayout) PCGameActivity.this.findViewById(R.id.main)).addView(mRenderView);
// Set the rendering view for the session.
mTcrSession.setRenderView(mRenderView);
```

5. Initialize the session object. After the async callback succeeds, you can get `clientSession` to further request the business backend and call the TencentCloud API to start the specified application instance and return `serverSession`. The client can call the `start()` API of the session object to pass in the `serverSession` parameter so as to start a session and initiate a connection from the SDK to the cloud. After the async callback of `start()` succeeds, the client program will display the image of the cloud application. If you use CAR, call `startCAR` to start the cloud application in the demo.

```java
private final AsyncCallback<String> mInitSessionCallback = new AsyncCallback<String>() {
    @Override
    public void onSuccess(String clientSession) {
        Log.i(TAG, "init session success, clientSession:" + clientSession);

        // `clientSession` will be returned after the session initialization succeeds. Then, request the backend to start the game or application and get `ServerSession`.
        // To start a cloud application, call `CloudRenderBiz.getInstance().startCAR`.
        CloudRenderBiz.getInstance().startGame(clientSession, response -> {
            Log.i(TAG, "start Cloud Application Render reponse: " + response);

            // Use the server session obtained from the server to start a session.
            GameStartResponse result = new Gson().fromJson(response.toString(), GameStartResponse.class);
            if (result.code == 0) {
                mTcrSession.start(result.sessionDescribe.serverSession, mStartSessionCallback);
            }
        }, error -> Log.e(TAG, "Cloud Application Render:" + error));
    }

    @Override
    public void onFailure(int code, String msg) {
        Log.e(TAG, "onFailure code:" + code + " msg:" + msg);
    }
};
```

The API for requesting the business backend is customized by yourself. In the [demo](../Demo), it is encapsulated in the `CloudRenderBiz` class.

6. In addition to displaying the cloud application image on the client, operations also need to be performed on the application, that is, client operations need to be synced to the cloud application. The SDK provides abstract objects such as `KeyBoard`, `Mouse`, and `GamePad`, whose APIs can be called by the client to interact with cloud input devices.  
In addition, the SDK for Android implements the default screen touch handlers: `MobileTouchHandler` and ` PcTouchHandler`. The former is used to sync the local screen touch events to a cloud mobile app. The latter is used to convert local screen touch events to cloud cursor movement events as well as mouse click, long-press, and double-click events for cloud PC applications. You can also customize and implement your own screen touch handler and assign it to `TcrRenderView`.

```java
// A mobile app
MobileTouchHandler mobileTouchHandler = new MobileTouchHandler();
mRenderView.setOnTouchListener(mobileTouchHandler);

// A PC application
PcTouchHandler pcTouchHandler = new PcTouchHandler();
mRenderView.setOnTouchListener(pcTouchHandler);
```

<br><p>
**Above are the core steps of integration. For the specific code, see [Demo](../Demo).**

# FAQs
1. **Which is the earliest Android version supported by the Real-Time Cloud Rendering SDK?**  
Android 4.1 (API level 16).

2. **How do I choose between the complete and lightweight SDKs?**  
To use the lightweight SDK, the client program needs to download the plugin file over the network and pass it in to the SDK during SDK initialization for dynamic loading. However, the two SDKs share the same APIs. If you have strict requirements for the application package size, you can choose the lightweight SDK; otherwise, we recommend you use the complete SDK.
