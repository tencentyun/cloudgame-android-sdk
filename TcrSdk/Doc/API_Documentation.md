- [中文文档](API文档.md)

## TCRSDK

## TcrSdk

### Basic SDK APIs

| API                                                          | Description                  |
| ------------------------------------------------------------ | --------------------- |
| [setLogger]() | Set the log callback object and level |
| [init]() | Initialize the SDK             |
| [createTcrSession]() | Create a session object       |
| [createTcrRenderView]() | Create a rendering view   |


## TcrSession

### Lifecycle APIs

| API                                                          | Description                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [start]() | Starts the session. This method should only be called once |
| [release]() | Release the session                  |
| [setRenderView]() | Set the rendering view for this session, and thus the SDK will render the streaming content to the view         |

### Audio/Video APIs

| API                                                          | Description                              |
| ------------------------------------------------------------ | --------------------------------- |
| [pauseStreaming]() | Pause the media stream                   |
| [resumeStreaming]() | Resume the media stream                    |
| [setRemoteVideoProfile]() | Set the remote video profile          |
| [setRemoteAudioProfile]() | Set the remote audio profile                 |
| [toggleLocalAudio]() | Enable or disable the local audio track that is captured from the mic |
| [toggleLocalVideo]() | Enable or disable the local video track that is captured from the camera |
| [setLocalVideoProfile]() | Set the local video profile   |
| [setLocalAudioProfile]() | Set the local audio profile   |


### Cloud application interaction APIs

| API                                                          | Description                       |
| ------------------------------------------------------------ | -------------------------- |
| [restartCloudApp]() | Restart the cloud application process           |
| [pasteText]() | Copy the text to the input box in the cloud application |
| [setRemoteDesktopResolution]() | Set the resolution of Cloud Desktop  |

### Multiplayer interactive cloud game APIs

| API                                                          | Description               |
| ------------------------------------------------------------ | ------------------ |
| [changeSeat]() | Switch the role and seat of a user (`userID`) to `targetRole` and `targetPlayerIndex` respectively. |
| [requestChangeSeat]() | Apply to the room owner to switch the role and seat of a player (`userID`) to `targetRole` and `targetPlayerIndex` respectively |
| [setMicMute]() | Change the mic status of someone  |
| [setRemoteAudioProfile]() | Set the gain of the volume level of another user|
| [syncRoomInfo]() | Refresh the information of all seats |

### Key APIs

| API                                                          | Description                     |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard]() | Return the interface to interact with the cloud keyboard in this session |
| [getMouse]() | Return the interface to interact with the cloud Mouse in this session |
| [getGamePad]() | Return the interface to interact with the cloud Mouse in this session |

### Data channel APIs

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [createCustomDataChannel]() | Creates a custom data channel |

### TcrSession.Observer
| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onEvent]() | This will be called when some event happened in the session |

### TcrSession.Event
| event                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [EVENT_INITED]() | The session has been initialized|
| [EVENT_CONNECTED]() | The session is connected |
| [EVENT_RECONNECTING]() | The session is reconnecting |
| [EVENT_CLOSED]() | The session is closed |
| [EVENT_IDLE]() | The user was idle(the user didn't perform operations related to button events such as keyboard, controller, and mouse events) |
| [EVENT_LOW_FPS]() | The frame rate remains low for a while |
| [EVENT_GAME_START_COMPLETE]() | The status of the game process on the server has been changed |
| [EVENT_ARCHIVE_LOAD_STATUS]() | The status of the archive loading in the server |
| [EVENT_ARCHIVE_SAVE_STATUS]() | The status of the archive saving in the server |
| [EVENT_CLIENT_INPUT_STATUS_CHANGED]() | The input status of the server has changed |
| [EVENT_SCREEN_CONFIG_CHANGE]() | The configuration of the cloud screen configuration has been changed |
| [EVENT_STATS]() | The performance data is updated |
| [EVENT_REMOTE_DESKTOP_INFO]() | The remote desktop information is updated |
| [EVENT_CURSOR_STATE_CHANGE]() | The showing status of cloud cursor is changed |
| [EVENT_MULTI_USER_SEAT_INFO]() | The multi user seat info is updated |
| [EVENT_ROLE_APPLY]() | Some user request to change seat |
| [EVENT_CURSOR_IMAGE_INFO]() | The remote cursor image information is updated |



## [TcrSessionConfig]()
The cloud game session configuration class containing configuration of getting and setting different objects.  

[TcrSession config builer]()


| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [build]() | Construct a `TcrSessionConfig` |
| [setObserver​]() |  set the observer of the TcrSession|
| [connectTimeout​]() | Set the connection timeout period |
| [enableLocalAudio​]() | Whether to enable audio upstreaming for the session |
| [enableLocalVideo​]() | Enable/Disable local video upstreaming |
| [idleThreshold​​]() | set the idleness detection threshold |
| [lowFpsThreshold​]() | Set the threshold for a low frame rate notification |
| [setVideoFrameCallback​]() | Set custom video data callback |
| [setAudioSampleCallback​]() | et the consumer for WebRTC decoded audio data. If not set, the decoded audio data will be played directly |


## AudioSampleCallback

Audio sample data Callback Interface

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onAudioData]() | Called when a piece of audio data is ready |
| [onAudioFormat]() | Called when the audio sample format has changed. You may need these output parameters to build or update your `AudioFormat` |

## VideoFrameBufferCallback
Customize the video data frame callback to call back the raw video data byte stream before decoding. Set the callback to decode and render the screen by yourself after getting the data

| API                                                          | Description         |
| ------------------------------------------------------------ | ------------ |
| [onVideoBufferCallback]() | Called when a piece of video raw data is ready |
| [onMediaCodecFormat]() | Called when the video format has changed. You may need these output parameters to build or update your `link MediaFormat`. |

## Keyboard

The cloud keyboard interaction class.

| API                                                          | Description                     |
| ------------------------------------------------------------ | ------------------------ |
| [onKeyboard]() | Trigger a key event of the cloud keyboard     |
| [checkKeyboardCapsLock]() | Query the letter case of the cloud keyboard |
| [resetKeyboardCapsLock]() | Reset the letter case of the cloud keyboard to lowercase |
| [resetKeyboard]() | Reset the key status of the cloud keyboard   |



## Mouse

The cloud mouse interaction class, which directly manipulates the cloud mouse without changing the mouse status of the local TcrRenderView.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onMouseDeltaMove]() | Let the cloud mouse move relative to the distance |
| [onMouseMoveTo]() | Let the cloud mouse move to the coordinate point |
| [onMouseKey]() | Trigger a click event of the cloud mouse |
| [onMouseScroll]() | Rotate the scroll wheel of the cloud mouse     |
| [setMouseCursorStyle]() | Set the cursor style       |


## Gamepad

The cloud controller interaction class.

| API                                                          | Description                         |
| ------------------------------------------------------------ | ---------------------------- |
| [connectGamepad]() | Connect the virtual gamepad to the server. The Gamepad can work only after connection         |
| [disconnectGamepad]() | Disconnect the virtual gamepad from the server         |
| [onGamepadStick]() | Send a gamepad stick event         |
| [onGamepadKey]() | Send a gamepad button event         |
| [onGamepadTrigger]() | Send a gamepad trigger event |

                                    


## CustomDataChannel

Data channel APIs.

| API                                                          | Description                 |
| ------------------------------------------------------------ | -------------------- |
| [send]() | Send data to the cloud Application |
| [close]() | Close this data channel         |

## CustomDataChannel.Observer

The data channel observer.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onConnected]() | This method is called when the listened CustomDataChannelis connected successfully |
| [onError]() | This method is called whenever some error is happened in the listened CustomDataChannel.         |
| [onMessage]() | This method is called whenever the listened CustomDataChannel receives cloud message.          |



## TcrRenderView

Rendering view APIs.

| API                                                          | Description                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener]() | Set the listener for the touch events of the view              |
| [handleMotion]() | Pass in the touch event                         |
| [setVideoScaleType]() | Set the video scaling mode                      |
| [setVideoRotation]() | Set the image rotation angle                      |
| [enablePinch]() | Set whether to allow two-finger pinch zoom on the game video image  |
| [setPinchOffset]()) | Set the borders for dragging the zoomed image |
| [setOnPinchListener]() | The two-finger pinch zoom listener, which listens for the two-finger pinch zoom parameter of the view        |
| [resetView]() | Reset the zoomed and dragged view        |
| [showDebugView]() | Enable the debugging mode to display the debugging information                          |
| [release]() | Release the underlying resources of this view                |
| [setCursorImage]() | Set the cursor image                          |
| [setCursorVisibility]() | Set the visibility of the current cursor                          |
| [setCursorPos]() | Set the absolute position of the cursor on the mobile phone        |
| [enableSuperResolution]() | [Enable video super-resolution capability](API使用示例.md#TcrRenderView)|
## 

## AsyncCallback<T>

The general async task callback class.

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess]() | This is called when the async task is executed successfully |
| [onFailure]() | This is called when the async task is executed failed     |

## VideoCapabilityUtil

A tool class for querying video decoding capabilities

| API                                                          | Description                   |
| ------------------------------------------------------------ | ---------------------- |
| [getRecommendedResolution]() | Returns the recommended resolution that take into account the capability of hardware decoders |