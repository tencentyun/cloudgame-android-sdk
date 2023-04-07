- [中文文档](云渲染SDK接入指南.md)

# Prerequisites
Make sure that you understand the business logic and frontend-backend interaction process of the Real-Time Cloud Rendering service, have activated the service, and have set up the business backend program (for more information, see [here](../README_EN-US.md)).


# Steps for Integrating the Real-Time Cloud Rendering Android SDK

1. Integrate the SDK. Import the following content into `build.gradle` of the application module:

```java
implementation 'com.tencent.tcr:tcrsdk-full:3.0.0'
```

To integrate the lightweight SDK, import the following content:

```java
implementation 'com.tencent.tcr:tcrsdk-lite:3.0.0' 
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
TcrSessionConfig tcrSessionConfig = TcrSessionConfig.builder()
        .setObserver(mSessionEventObserver)
        .connectTimeout(25000)
        .idleThreshold(30)
        .build();
mTcrSession = TcrSdk.getInstance().createTcrSession(tcrSessionConfig);
// Create and add the rendering view.
mRenderView = TcrSdk.getInstance().createTcrRenderView(MainActivity.this, mTcrSession, TcrRenderViewType.SURFACE);
((FrameLayout) MainActivity.this.findViewById(R.id.main)).addView(mRenderView);
// Set the rendering view for the session.
mTcrSession.setRenderView(mRenderView);
```

5. After the session object is created, the object will be automatically initialized. After the initialization is successful, the event will be notified through the mSessionEventObserver object passed in step 4. The event that the Session initialization is completed is TcrSession.Event.EVENT_INITED, and you can get `clientSession` in event data to further request the business backend and call the TencentCloud API to start the specified application instance and return `serverSession`. The client can call the `start()` API of the session object to pass in the `serverSession` parameter so as to start a session and initiate a connection from the SDK to the cloud. After the async callback of `start()` succeeds, the client program will display the image of the cloud application. 

```java
private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
        @Override
        public void onEvent(TcrSession.Event event, Object eventData) {
            switch (event) {
                case STATE_INITED:
                    // Get the client session from the callback data and request ServerSession
                    String clientSession = (String) eventData;
                    requestServerSession(clientSession);
                    break;
                case STATE_CONNECTED:
                    // Set the operation mode after the connection is successful
                    // The interaction with the cloud needs to start calling the interface after this event callback
                    runOnUiThread(() -> setTouchHandler(mTcrSession, mRenderView, PC_GAME));
                    break;
                default:
            }
        }
   };    

```

Request a ServerSession and start the game:

```java
CloudRenderBiz.getInstance().startGame(clientSession, response -> {
            Log.i(TAG, "Request ServerSession success, response=" + response.toString());
            // Start a session using the server session obtained from the server
            StartGameResponse result = new Gson().fromJson(response.toString(), StartGameResponse.class);
            if (result.code == 0) {
                boolean res = mTcrSession.start(result.sessionDescribe.serverSession);
                if (!res) {
                    Log.e(TAG, "start session failed");
                    showToast("Connection failed, please check the log", Toast.LENGTH_SHORT);
                }
                showToast("Connection success", Toast.LENGTH_SHORT);
            } else {
                String showMessage = "";
                switch (result.code) {
                    case 10000:
                        showMessage = "sign verification error";
                        break;
                    case 10001:
                        showMessage = "missing required parameter";
                        break;
                    case 10200:
                        showMessage = "Failed to create session";
                        break;
                    case 10202:
                        showMessage = "Try lock concurrency failed, no resources";
                        break;
                    default:
                }
                showToast(showMessage + result.msg, Toast.LENGTH_LONG);
            }
        }, error -> Log.i(TAG, "Request ServerSession success, response=" + error.toString()));
```


The API for requesting the business backend is customized by yourself. In the [demo](../Demo), it is encapsulated in the `CloudRenderBiz` class.

6. In addition to displaying the cloud application image on the client, operations also need to be performed on the application, that is, client operations need to be synced to the cloud application. The SDK provides abstract objects such as `KeyBoard`, `Mouse`, and `GamePad`, whose APIs can be called by the client to interact with cloud input devices.  
In addition, the SDK for Android implements the default screen touch handlers: `MobileTouchListener ` and ` PcTouchListener `. The former is used to sync the local screen touch events to a cloud mobile app. The latter is used to convert local screen touch events to cloud cursor movement events as well as mouse click, long-press, and double-click events for cloud PC applications. You can also customize and implement your own screen touch handler and assign it to `TcrRenderView`.

```java
// A mobile app
renderView.setOnTouchListener(new MobileTouchListener());

// A PC application
renderView.setOnTouchListener(new PcTouchListener(session));
```

<br><p>
**Above are the core steps of integration. For the specific code, see [Demo](../Demo).
API usage reference[API_Documentation](API_Documentation.md). For more functional scenarios, please refer to [Scene Function](场景功能.md)**

# FAQs
1. **Which is the earliest Android version supported by the Real-Time Cloud Rendering SDK?**  
Android 4.1 (API level 16).

2. **How do I choose between the complete and lightweight SDKs?**  
To use the lightweight SDK, the client program needs to download the plugin file over the network and pass it in to the SDK during SDK initialization for dynamic loading. However, the two SDKs share the same APIs. If you have strict requirements for the application package size, you can choose the lightweight SDK; otherwise, we recommend you use the complete SDK.
