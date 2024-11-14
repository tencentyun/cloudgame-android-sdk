- [中文文档](API文档.md)

## TCRSDK

## TcrSdk

### Basic SDK APIs

| API                                                                                                                                                                                                                                                      | Description                           |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------|
| [setLogger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#setLogger-com.tencent.tcr.sdk.api.TcrLogger-)                                                                                           | Set the log callback object and level |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#init-Context-java.lang.String-com.tencent.tcr.sdk.api.AsyncCallback-)                                                                        | Initialize the SDK                    |
| [createTcrSession](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrSession-com.tencent.tcr.sdk.api.TcrSessionConfig-)                                                                      | Create a session object               |
| [createTcrRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrRenderView-Context-com.tencent.tcr.sdk.api.TcrSession-com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType-) | Create a rendering view               |
| [getEGLContext](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#getEGLContext--)                                                                                                                    | Get the OpenGL EGLContext             |
| [setEGLContext](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSdk.html#setEGLContext--)                                                                                                                    | Set the OpenGL EGLContext             |


## TcrSession

### Lifecycle APIs

| API                                                          | Description                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [start](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#start-java.lang.String-) | Starts the session. This method should only be called once |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#release--) | Release the session                  |

### Audio/Video APIs

| API                                                          | Description                                                              |
| ------------------------------------------------------------ |--------------------------------------------------------------------------|
| [pauseStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#pauseStreaming--) | Pause the media stream                                                   |
| [resumeStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#resumeStreaming--) | Resume the media stream                                                  |
| [setRemoteAudioPlayProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setRemoteAudioPlayProfile-float-) | Set the remote audio profile                                             |
| [setEnableLocalAudio](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableLocalAudio-boolean-) | Enable or disable the local audio track that is captured from the mic    |
| [setEnableLocalVideo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableLocalVideo-boolean-) | Enable or disable the local video track that is captured from the camera |
| [setLocalVideoProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setLocalVideoProfile-int-int-int-int-int-boolean-) | Set the local video profile                                              |
| [setVideoSink](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setVideoSink-com.tencent.tcr.sdk.api.VideoSink-) | Sets a video sink for this session.                                      |
| [setAudioSink](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setAudioSink-com.tencent.tcr.sdk.api.AudioSink-) | Sets a audio sink for this session.                                      |
| [setEnableAudioPlaying](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableAudioPlaying-boolean-) | Control session audio playback switch                                    |
| [sendCustomAudioData](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#sendCustomAudioData-java.nio.ByteBuffer-long-) | Send custom captured audio data.                                                      |

### Cloud application interaction APIs

| API                                                          | Description                       |
| ------------------------------------------------------------ | -------------------------- |
| [restartCloudApp](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#restartCloudApp--) | Restart the cloud application process           |
| [pasteText](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#pasteText-java.lang.String-) | Copy the text to the input box in the cloud application |
| [setRemoteDesktopResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setRemoteDesktopResolution-int-int-) | Set the resolution of Cloud Desktop  |

### Multiplayer interactive cloud game APIs

| API                                                          | Description               |
| ------------------------------------------------------------ | ------------------ |
| [changeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#changeSeat-java.lang.String-com.tencent.tcr.sdk.api.data.MultiUser.Role-int-com.tencent.tcr.sdk.api.AsyncCallback-) | Switch the role and seat of a user (`userID`) to `targetRole` and `targetPlayerIndex` respectively. |
| [requestChangeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#requestChangeSeat-java.lang.String-com.tencent.tcr.sdk.api.data.MultiUser.Role-int-com.tencent.tcr.sdk.api.AsyncCallback-) | Apply to the room owner to switch the role and seat of a player (`userID`) to `targetRole` and `targetPlayerIndex` respectively |
| [setMicMute](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#setMicMute-java.lang.String-int-com.tencent.tcr.sdk.api.AsyncCallback-) | Change the mic status of someone  |
| [syncRoomInfo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#syncRoomInfo--) | Refresh the information of all seats |

### Key APIs

| API                                                          | Description                     |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#getKeyboard--) | Return the interface to interact with the cloud keyboard in this session |
| [getMouse](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#getMouse--) | Return the interface to interact with the cloud Mouse in this session |
| [getGamePad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#getGamepad--) | Return the interface to interact with the cloud Mouse in this session |

### Data channel APIs

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [createCustomDataChannel](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.html#createCustomDataChannel-int-com.tencent.tcr.sdk.api.CustomDataChannel.Observer-) | Creates a custom data channel |

### TcrSession.Observer
| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onEvent](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Observer.html#onEvent-com.tencent.tcr.sdk.api.TcrSession.Event-java.lang.Object-) | This will be called when some event happened in the session |

### TcrSession.Event
| event                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [STATE_INITED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_INITED) | The session has been initialized|
| [STATE_CONNECTED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_CONNECTED) | The session is connected |
| [STATE_RECONNECTING](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_RECONNECTING) | The session is reconnecting |
| [STATE_CLOSED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_CLOSED) | The session is closed |
| [CLIENT_IDLE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_IDLE) | The user was idle(the user didn't perform operations related to button events such as keyboard, controller, and mouse events) |
| [CLIENT_LOW_FPS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_LOW_FPS) | The frame rate remains low for a while |
| [GAME_START_COMPLETE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#GAME_START_COMPLETE) | The status of the game process on the server has been changed |
| [ARCHIVE_LOAD_STATUS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#ARCHIVE_LOAD_STATUS) | The status of the archive loading in the server |
| [ARCHIVE_SAVE_STATUS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#ARCHIVE_SAVE_STATUS) | The status of the archive saving in the server |
| [INPUT_STATUS_CHANGED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#INPUT_STATUS_CHANGED) | The input status of the server has changed |
| [SCREEN_CONFIG_CHANGE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#SCREEN_CONFIG_CHANGE) | The configuration of the cloud screen configuration has been changed |
| [CLIENT_STATS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_STATS) | The performance data is updated |
| [REMOTE_DESKTOP_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#REMOTE_DESKTOP_INFO) | The remote desktop information is updated |
| [CURSOR_STATE_CHANGE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CURSOR_STATE_CHANGE) | The showing status of cloud cursor is changed |
| [MULTI_USER_SEAT_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#MULTI_USER_SEAT_INFO) | The multi user seat info is updated |
| [MULTI_USER_ROLE_APPLY](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#MULTI_USER_ROLE_APPLY) | Some user request to change seat |
| [CURSOR_IMAGE_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CURSOR_IMAGE_INFO) | The remote cursor image information is updated |



## [TcrSessionConfig]()
The cloud game session configuration class containing configuration of getting and setting different objects.  

[TcrSession config builer]()


| API                                                          | Description                                         |
| ------------------------------------------------------------ |-----------------------------------------------------|
| [build](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.Builder.html#build--) | Construct a `TcrSessionConfig`                      |
| [observer​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#observer) | Set the observer of the TcrSession                  |
| [idleThreshold​​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#idleThreshold) | Set the idleness detection threshold                |
| [lowFpsThreshold​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#lowFpsThresholdCount) | Set the threshold for a low frame rate notification |
| [videoFrameCallback​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#videoFrameBufferCallback) | Set custom video data callback                      |
| [enableLowLegacyRendering​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#enableLowLegacyRendering) | Enable low legacy rendering                         |
| [preferredCodec​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#preferredCodec)                     | Set perferred codec                                 |
| [enableCustomAudioCapture​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TcrSessionConfig.Builder.html#enableCustomAudioCapture-boolean-int-boolean-)                     | Enable custom audio capture                         |

## AudioSink

Audio sample data Callback Interface

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onAudioData](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/AudioSink.html#onAudioData-byte:A-int-int-) | Called when a piece of audio data is ready |
| [onAudioFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/AudioSink.html#onAudioFormat-int-int-int-) | Called when the audio sample format has changed. You may need these output parameters to build or update your `AudioFormat` |

## VideoFrameBufferCallback
Customize the video data frame callback to call back the raw video data byte stream before decoding. Set the callback to decode and render the screen by yourself after getting the data

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onVideoBufferCallback](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onVideoBufferCallback-java.nio.ByteBuffer-int-int-long-) | Called when a piece of video raw data is ready |
| [onMediaCodecFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onMediaCodecFormat-java.lang.String-int-int-) | Called when the video format has changed. You may need these output parameters to build or update your `link MediaFormat`. |

## VideoSink
This interface represents a video sink.
| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onFrame](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/VideoSink.html#onVideoFrame-com.tencent.tcr.sdk.api.VideoFrame-) | Called when a video frame is received. |
## Keyboard

The cloud keyboard interaction class.

| API                                                          | Description                     |
| ------------------------------------------------------------ | ------------------------ |
| [onKeyboard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Keyboard.html#onKeyboard-int-boolean-) | Trigger a key event of the cloud keyboard     |
| [checkKeyboardCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Keyboard.html#checkKeyboardCapsLock-com.tencent.tcr.sdk.api.AsyncCallback-) | Query the letter case of the cloud keyboard |
| [resetKeyboardCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Keyboard.html#resetKeyboardCapsLock--) | Reset the letter case of the cloud keyboard to lowercase |
| [resetKeyboard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Keyboard.html#resetKeyboard--) | Reset the key status of the cloud keyboard   |



## Mouse

The cloud mouse interaction class, which directly manipulates the cloud mouse without changing the mouse status of the local TcrRenderView.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onMouseDeltaMove](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseDeltaMove-int-int-) | Let the cloud mouse move relative to the distance |
| [onMouseMoveTo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseMoveTo-int-int-) | Let the cloud mouse move to the coordinate point |
| [onMouseKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseKey-com.tencent.tcr.sdk.api.Mouse.KeyType-boolean-) | Trigger a click event of the cloud mouse |
| [onMouseScroll](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseScroll-boolean-) | Rotate the scroll wheel of the cloud mouse     |
| [setMouseCursorStyle](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Mouse.html#setMouseCursorStyle-com.tencent.tcr.sdk.api.Mouse.CursorStyle-) | Set the cursor style       |


## Gamepad

The cloud controller interaction class.

| API                                                          | Description                         |
| ------------------------------------------------------------ | ---------------------------- |
| [connectGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Gamepad.html#connectGamepad--) | Connect the virtual gamepad to the server. The Gamepad can work only after connection         |
| [disconnectGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Gamepad.html#disconnectGamepad--) | Disconnect the virtual gamepad from the server         |
| [onGamepadStick](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadStick-com.tencent.tcr.sdk.api.Gamepad.KeyType-int-int-) | Send a gamepad stick event         |
| [onGamepadKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadKey-int-boolean-) | Send a gamepad button event         |
| [onGamepadTrigger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadTrigger-com.tencent.tcr.sdk.api.Gamepad.KeyType-int-boolean-) | Send a gamepad trigger event |

                                    
## TouchScreen

The cloud touch screen interaction class.

| API                                                          | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| [touch](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/TouchScreen.html#touch-float-float-int-int-int-int-long-) | Trigger the touch event of the cloud touch screen.         |


## CustomDataChannel

Data channel APIs.

| API                                                          | Description                 |
| ------------------------------------------------------------ | -------------------- |
| [send](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/CustomDataChannel.html#send-java.nio.ByteBuffer-) | Send data to the cloud Application |
| [close](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/CustomDataChannel.html#close--) | Close this data channel         |

## CustomDataChannel.Observer

The data channel observer.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onConnected](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onConnected-int-) | This method is called when the listened CustomDataChannelis connected successfully |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onError-int-int-java.lang.String-) | This method is called whenever some error is happened in the listened CustomDataChannel.         |
| [onMessage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onMessage-int-java.nio.ByteBuffer-) | This method is called whenever the listened CustomDataChannel receives cloud message.          |



## TcrRenderView

Rendering view APIs.

| API                                                          | Description                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setOnTouchListener-View.OnTouchListener-) | Set the listener for the touch events of the view              |
| [handleMotion](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#handleMotion-MotionEvent-) | Pass in the touch event                         |
| [setVideoScaleType](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setVideoScaleType-com.tencent.tcr.sdk.api.view.TcrRenderView.ScaleType-) | Set the video scaling mode                      |
| [setVideoRotation](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setVideoRotation-com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation-) | Set the image rotation angle                      |
| [setDisplayDebugView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setDisplayDebugView-boolean-) | Enable the debugging mode to display the debugging information                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#release--) | Release the underlying resources of this view                |
| [setEnableSuperResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableSuperResolution-boolean-) | [Enable video super-resolution capability](API使用示例.md#TcrRenderView)|
| [setEnableInputDeviceHandle](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableInputDeviceHandle-boolean-) | Set whether to enable TcrRenderView to process the peripheral input events|
| [getKeyCodeMapping](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#getKeyCodeMapping--) | Returns a mapping table, based on which TcrRenderView maps local peripheral input events to cloud input events|
| [setEnableFrameCallback](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableFrameCallback-float-) | Turn on and off the rendering video frame callback|
## PcTouchListener

This class handles touch event logic

| API                                                          | Description                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setZoomHandler](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setZoomHandler-com.tencent.tcr.sdk.api.view.PcZoomHandler-) | set zoom handler              |
| [setMouseConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setMouseConfig-boolean-float-boolean-) | set the mouse parameters                          |
| [	getZoomHandler](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#getZoomHandler--) | get zoom handler                     |
| [setShortClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setShortClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnClickListener-) | set mouse single click listener                      |
| [	setLongClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setLongClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnClickListener-) | set mouse long press listener                      |
| [setDoubleClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setDoubleClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnDoubleClickListener-) | set mouse double click listener                      |

## PcZoomHandler

This class handles the two-finger pinch zoom logic

| API                                                          | Description                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setZoomRatio](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomRatio-float-float-) | Set zoom ratio              |
| [resetZoom](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#resetZoom--) | Reset the zoom of the render view                         |
| [setZoomOffset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomOffset-Rect-) | Set the offsets of the video image frame during zoom                      |
| [setZoomListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomListener-com.tencent.tcr.sdk.api.view.PcZoomHandler.ZoomListener-) | Set the two-finger pinch zoom listener                      |

## AsyncCallback<T>

The general async task callback class.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/AsyncCallback.html#onSuccess-T-) | This is called when the async task is executed successfully |
| [onFailure](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/AsyncCallback.html#onFailure-int-java.lang.String-) | This is called when the async task is executed failed     |

## VideoCapabilityUtil

A tool class for querying video decoding capabilities

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [getRecommendedResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/utils/VideoCapabilityUtil.html#getRecommendedResolution-int-int-) | Returns the recommended resolution that take into account the capability of hardware decoders |

## CustomAudioBufferUtil
A utility class for obtaining the size of the ByteBuffer for custom audio capture.

| API                                                          | 描述                     |
| ------------------------------------------------------------ |------------------------|
| [getCustomAudioCaptureDataBufferSize](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.21.0/com/tencent/tcr/sdk/api/utils/CustomAudioBufferUtil.html#getCustomAudioCaptureDataBufferSize-int-boolean-) | Return the size of the ByteBuffer for custom captured audio. |