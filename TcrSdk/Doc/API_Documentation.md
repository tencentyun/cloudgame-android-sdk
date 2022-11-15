- [中文文档](API文档.md)

## TCRSDK

## TcrSdk

### Basic SDK APIs

| API | Description |
| ------------------------------------------------------------ | --------------------- |
| [setLogger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#setLogger(ILogger,LogLevel)) | Sets the log callback level of the SDK. |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#init(Context,java.lang.String,com.tencent.tcr.sdk.api.AsyncCallback)) | Initializes the SDK.             |
| [createTcrSession](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrSession(com.tencent.tcr.sdk.api.config.TcrSessionConfig)) | Creates a TcrSession object.       |
| [createTcrRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrRenderView(Context,com.tencent.tcr.sdk.api.TcrSession,com.tencent.tcr.sdk.api.TcrRenderViewType)) | Creates a TcrRenderView object.   |



## TcrSession

### Lifecycle APIs 

| API | Description |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [setListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setListener(com.tencent.tcr.sdk.api.TcrSessionListener)) | Sets the session listener.                                             |
| [start](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#start(java.lang.String,com.tencent.tcr.sdk.api.AsyncCallback)) | Starts a session, gets the `serverSession` returned from the cloud, and initiates a connection from the SDK to the cloud. |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#init(com.tencent.tcr.sdk.api.AsyncCallback)) | Initializes the session to create a local `ClientSession`.                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#release()) | Terminates the session to close the connection between the local device and the cloud and release the resources.                   |
| [setRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setRenderView(com.tencent.tcr.sdk.api.TcrRenderView)) | Sets the rendering view of the session. The SDK will render the cloud image to the view.          |

### Audio/Video APIs

| API | Description |
| ------------------------------------------------------------ | --------------------------------- |
| [pauseStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#pauseStreaming()) | Pauses the audio/video transfer.                    |
| [resumeStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#resumeStreaming()) | Resumes the audio/video transfer.                    |
| [setStreamProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setStreamProfile(int,int,int,com.tencent.tcr.sdk.api.AsyncCallback)) | Sets the frame rate and bitrates of the streamed video.          |
| [setVolume](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setVolume(float)) | Sets the amplification factor of the volume level.                  |
| [enableMic](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#enableMic(boolean)) | Enables/Disables the mic. Default value: `false`, which indicates to disable the mic. |
| [enableLocalVideo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#enableLocalVideo(boolean)) | Enables/Disables the camera，Default value: `false`, which indicates to disable the camera. |
| [setLocalVideoBitrate](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setLocalVideoBitrate(int)) |Sets LocalVideo upstream bitrate. Default value:1500kbps|
| [setLocalVideoFps](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setLocalVideoFps(int)) | Sets LocalVideo upstream fps. Default value:30 |

### Cloud application interaction APIs

| API | Description |
| ------------------------------------------------------------ | -------------------------- |
| [restartCloudApp](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#restartCloudApp()) | Restarts the cloud application process.           |
| [pasteText](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#pasteText(java.lang.String)) | Copies the text to the input box in the cloud application. |

### Multiplayer interactive cloud game APIs

| API | Description |
| ------------------------------------------------------------ | ------------------ |
| [getMultiUserManager](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getMultiUserManager()) | Gets the multiplayer interactive cloud game manager. |

### Key APIs

| API | Description |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getKeyBoard()) | Gets the object interacting with the cloud keyboard. |
| [getMouse](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getMouse()) | Gets the object interacting with the cloud mouse. |
| [getGamePad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getGamePad()) | Gets the object interacting with the cloud controller. |

### Data channel APIs

| API | Description |
| ------------------------------------------------------------ | ------------ |
| [createDataChannel](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#createDataChannel(int,com.tencent.tcr.sdk.api.datachannel.DataChannelListener)) | Creates a data channel. |



## [TcrSessionConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.html)


## [TcrSessionConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/package-summary.html)
The cloud game session configuration class containing configuration of getting and setting different objects.

[云游会话配置类-设置配置](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html)


| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [build](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#build()) | Build a SessionConfig |
| [connectTimeout​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#connectTimeout(long)) | Sets connect Timeout |
| [enableAudioTrack​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#enableAudioTrack(boolean)) | Enable/Disable the local mic |
| [enableLocalVideo​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#enableLocalVideo(boolean,int,int,int,boolean)) | Enable/Disable the local camera |
| [idleThreshold​​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#idleThreshold(long)) | Sets idle time threshold |
| [lowFpsThreshold​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#lowFpsThreshold(int,int)) | Sets the low fps threshold |
| [setVideoFrameCallback​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#setVideoFrameCallback(com.tencent.tcr.sdk.api.VideoFrameBufferCallback)) | Sets video frame callback |
| [setAudioSampleCallback​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.Builder.html#setAudioSampleCallback(com.tencent.tcr.sdk.api.AudioSampleCallback)) | Sets Audio data consumer |

## TcrSessionListener 

The session listener.

| API | Description |
| ------------------------------------------------------------ | ------------ |
| [onEvent](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSessionListener.html#onEvent(java.lang.String,java.lang.String)) | A session event occurred. |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSessionListener.html#onError(int,java.lang.String)) | A session error occurred. |

## AudioSampleCallback

Audio sample data Callback Interface

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onAudioData](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AudioSampleCallback.html#onAudioData(byte%5B%5D,int,int)) | call back audio data |
| [onAudioFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AudioSampleCallback.html#onAudioFormat(int,int,int)) | call back audio sample data format |

## VideoFrameBufferCallback

Customize the video data frame callback to call back the raw video data byte stream before decoding. Set the callback to decode and render the screen by yourself after getting the data

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onVideoBufferCallback](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onVideoBufferCallback(java.nio.ByteBuffer,int,int,long)) | 视频帧数据回调 |
| [onMediaCodecFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onMediaCodecFormat(java.lang.String,int,int)) | MediaFormat初始化参数 |



## Keyboard

The cloud keyboard interaction class.

| API | Description |
| ------------------------------------------------------------ | ------------------------ |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#onKey(int,boolean)) | Triggers a key event of the cloud keyboard.     |
| [checkCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#checkCapsLock(com.tencent.tcr.sdk.api.AsyncCallback)) | Queries the letter case of the cloud keyboard. |
| [resetCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#resetCapsLock()) | Resets the letter case of the cloud keyboard. |
| [reset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#reset()) | Resets the key status of the cloud keyboard.   |



## Mouse

The cloud mouse interaction class, which directly manipulates the cloud mouse without changing the mouse status of the local `TcrRenderView`.

| API | Description |
| ------------------------------------------------------------ | ---------------------- |
| [onDeltaMove](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onDeltaMove(float,float)) | Moves the cloud cursor by the specified delta. |
| [onMoveTo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onMoveTo(float,float)) | Moves the cloud cursor to the specified coordinates. |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onKey(com.tencent.tcr.sdk.api.config.MouseKey,boolean)) | Triggers a click event of the cloud mouse. |
| [onScroll](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onScroll(boolean)) | Rotates the scroll wheel of the cloud mouse.     |
| [setMouseConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#setCursorStyle(com.tencent.tcr.sdk.api.config.CursorStyle)) | Set the cursor style.       |
|[setMouseInfoListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#setMouseInfoListener(com.tencent.tcr.sdk.api.MouseInfoListener))|Set up a listener to get information about the mouse.|   



## Gamepad

The cloud controller interaction class.

| API | Description |
| ------------------------------------------------------------ | ---------------------------- |
| [connect](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#connect()) | Connects the cloud controller.         |
| [disconnect](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#disconnect()) | Disconnects the cloud controller.         |
| [onAxis](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onAxis(com.tencent.tcr.sdk.api.config.GamePadKey,int,int)) | Triggers a stick event of the cloud controller.         |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onKey(int,boolean)) | Triggers a button event of the cloud controller.         |
| [onTrigger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onTrigger(com.tencent.tcr.sdk.api.config.GamePadKey,int,boolean)) | Triggers an L2 or R2 trigger event of the cloud controller. |



## MultiUserManager

Multiplayer interactive cloud game APIs.

| API | Description |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [applyChangeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#applyChangeSeat(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int,com.tencent.tcr.sdk.api.AsyncCallback)) | Applies to switch the role and seat of a player.                                     |
| [changeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#changeSeat(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int,com.tencent.tcr.sdk.api.AsyncCallback)) | Switches the role and seat of a user (`userID`) to `targetRole` and `targetPlayerIndex` respectively. |
| [setMicMute](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setMicMute(java.lang.String,boolean,com.tencent.tcr.sdk.api.AsyncCallback)) | Mutes the local mic.                                               |
| [setVolume](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setVolume(java.lang.String,float)) | Sets the gain of the volume level of another user.                                       |
| [syncRoomInfo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#syncRoomInfo()) | Refreshes the information of all seats.                                             |
| [setMultiUserListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setMultiUserListener(com.tencent.tcr.sdk.api.multiplayer.SeatsListener)) | Registers a multiplayer interactive cloud game listener.                                           |

## SeatsListener

The multiplayer interactive cloud game listener.

| API | Description |
| ------------------------------------------------------------ | -------------------------------------------- |
| [onRoleApplied](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/SeatsListener.html#onRoleApplied(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int)) | When a user applies for a seat change, the room owner will receive this callback.      |
| [onSeatChanged](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/SeatsListener.html#onSeatChanged(java.lang.String,java.util.List,java.util.List,int,int)) | When the seat information changes, all users will receive this callback. |



## DataChannel

Data channel APIs.

| API | Description |
| ------------------------------------------------------------ | -------------------- |
| [send](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannel.html#send(java.nio.ByteBuffer)) | Sends the data through the data channel. |
| [close](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannel.html#close()) | Closes the data channel.         |

## DataChannelListener

The data channel listener.

| API | Description |
| ------------------------------------------------------------ | ---------------------- |
| [onCreateSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onCreateSuccess(int)) | The data channel was created successfully. |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onError(int,com.tencent.tcr.sdk.api.datachannel.DataChannelListener.State,java.lang.String)) | An error occurred.         |
| [onMessage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onMessage(int,java.nio.ByteBuffer)) | A cloud message was received.           |



## TcrRenderView

Rendering view APIs.

| API | Description |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setOnTouchListener(View.OnTouchListener)) | Sets the listener for view click events.              |
| [handleMotion](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#handleMotion(MotionEvent)) | Passes in a touch event.                          |
| [setVideoScaleType](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setVideoScaleType(com.tencent.tcr.sdk.api.ScaleType)) | Sets the video scaling mode.                      |
| [setVideoRotation](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setVideoRotation(com.tencent.tcr.sdk.api.VideoRotation)) | Sets the image rotation angle.                      |
| [enablePinch](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#enablePinch(boolean)) | Sets whether to enable two-finger gestures (such as zoom-in and drag) on the video layer.  |
| [setPinchOffset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setPinchOffset(Rect)) | Sets the margins between the sides of the area within which `videoView` can be dragged and the sides of the parent view. |
| [setOnPinchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setOnPinchListener(com.tencent.tcr.sdk.api.PinchListener)) | Sets the listener for the callback event triggered while zooming the video layer.        |
| [resetView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#resetView()) | Resets the view position and size to the initial status.        |
| [showDebugView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#showDebugView(boolean)) | Displays the debugging view.                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#release()) | Releases the underlying resources of the current view.                |
| [setCursorImage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setCursorImage(Bitmap,int)) | Sets the cursor image.                          |
| [setCursorPos](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setCursorPos(int,int)) | Sets the absolute position of the cursor on the mobile phone.        |
| [enableSuperResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#enableSuperResolution(boolean)) | Enable video super-resolution capability.|

## 

## AsyncCallback<T>

The general async task callback class.

| API | Description |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AsyncCallback.html#onSuccess(T)) | The async execution succeeded. |
| [onFailure](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AsyncCallback.html#onFailure(int,java.lang.String)) | An error occurred during the async execution.     |

## VideoCapabilityUtil

The util to query video resolution

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [getRecommendedResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/VideoCapabilityUtil.html#getRecommendedResolution(int,int)) | Returns the recommended resolution that take into account the capability of hardware decoders. |