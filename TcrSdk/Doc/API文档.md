- [English document](API_Documentation.md)

## TCRSDK

## TcrSdk

### SDK 基础接口

| API                                                          | 描述                  |
| ------------------------------------------------------------ |---------------------|
| [setLogger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#setLogger-com.tencent.tcr.sdk.api.TcrLogger-) | 设置SDK的日志回调级别        |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#init-Context-java.lang.String-com.tencent.tcr.sdk.api.AsyncCallback-) | 初始化SDK              |
| [createTcrSession](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrSession-com.tencent.tcr.sdk.api.TcrSessionConfig-) | 创建Tcr会话对象           |
| [createTcrRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrRenderView-Context-com.tencent.tcr.sdk.api.TcrSession-com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType-) | 创建Tcr渲染视图对象         |
| [getEGLContext](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#getEGLContext--) | 获取OpenGL EGLContext |
| [setEGLContext](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSdk.html#setEGLContext--)                                                                                                                    | 设置OpenGL EGLContext |

## TcrSession

### 生命周期相关接口 

| API                                                          | 描述                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [start](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#start-java.lang.String-) | 启动会话，拿到云端返回的serverSession后发起SDK到云端的连接 |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#release--) | 销毁会话，断开本地和云端的连接，释放资源                   |

### 音视频相关接口

| API                                                          | 描述                     |
| ------------------------------------------------------------ |------------------------|
| [pauseStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#pauseStreaming--) | 暂停音视频传输                |
| [resumeStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#resumeStreaming--) | 恢复音视频传输                |
| [setRemoteAudioPlayProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setRemoteAudioPlayProfile-float-) | 设置音量放大系数               |
| [setEnableLocalAudio](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableLocalAudio-boolean-) | 启用禁用麦克风，默认值false不开启    |
| [setEnableLocalVideo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableLocalVideo-boolean-) | 启用禁用本地视频上行，默认值false不开启 |
| [setLocalVideoProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setLocalVideoProfile-int-int-int-int-int-boolean-) | 设置摄像头的传输帧率和码率          |
| [setVideoSink](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setVideoSink-com.tencent.tcr.sdk.api.VideoSink-) | 设置会话的视频流回调接口           |
| [setAudioSink](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setAudioSink-com.tencent.tcr.sdk.api.AudioSink-) | 设置会话的音频流回调接口           |
| [setEnableAudioPlaying](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setEnableAudioPlaying-boolean-) | 控制会话的音频播放开关            |
| [sendCustomAudioData](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#sendCustomAudioData-java.nio.ByteBuffer-long-) | 发送自定义采集音频数据            |

### 云端应用交互接口

| API                                                          | 描述                       |
| ------------------------------------------------------------ | -------------------------- |
| [restartCloudApp](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#restartCloudApp--) | 重启云端应用进程           |
| [pasteText](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#pasteText-java.lang.String-) | 粘贴文本到云端应用的输入框 |
| [setRemoteDesktopResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setRemoteDesktopResolution-int-int-) | 设置云端桌面的分辨率   |
| [setDisableCloudInput](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setDisableCloudInput-boolean-) | 关闭云端输入法          |

### 多人云游接口

| API                                                          | 描述               |
| ------------------------------------------------------------ | ------------------ |
| [changeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#changeSeat-java.lang.String-com.tencent.tcr.sdk.api.data.MultiUser.Role-int-com.tencent.tcr.sdk.api.AsyncCallback-) | 改变某个用户的坐席 |
| [requestChangeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#requestChangeSeat-java.lang.String-com.tencent.tcr.sdk.api.data.MultiUser.Role-int-com.tencent.tcr.sdk.api.AsyncCallback-) | 申请切换席位 |
| [setMicMute](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#setMicMute-java.lang.String-int-com.tencent.tcr.sdk.api.AsyncCallback-) | 改变某个用户的麦克风状态 |
| [syncRoomInfo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#syncRoomInfo--) | 刷新房间信息 |

### 按键接口

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#getKeyboard--) | 获取与云端键盘交互的对象 |
| [getMouse](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#getMouse--) | 获取与云端鼠标交互的对象 |
| [getGamePad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#getGamepad--) | 获取与云端手柄交互的对象 |

### 数据通道接口

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [createCustomDataChannel](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.html#createCustomDataChannel-int-com.tencent.tcr.sdk.api.CustomDataChannel.Observer-) | 创建数据通道 |

### TcrSession.Observer
| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onEvent](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Observer.html#onEvent-com.tencent.tcr.sdk.api.TcrSession.Event-java.lang.Object-) | 事件通知 |

### TcrSession.Event
| 定义                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [STATE_INITED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_INITED) | 初始化成功|
| [STATE_CONNECTED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_CONNECTED) | 连接成功 |
| [STATE_RECONNECTING](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_RECONNECTING) | 重连中 |
| [STATE_CLOSED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#STATE_CLOSED) | 会话关闭 |
| [CLIENT_IDLE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_IDLE) | 用户无操作 |
| [CLIENT_LOW_FPS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_LOW_FPS) | 帧率低状态 |
| [GAME_START_COMPLETE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#GAME_START_COMPLETE) | 远端游戏状态变化 |
| [ARCHIVE_LOAD_STATUS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#ARCHIVE_LOAD_STATUS) | 存档加载状态变化 |
| [ARCHIVE_SAVE_STATUS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#ARCHIVE_SAVE_STATUS) | 存档保存状态变化 |
| [INPUT_STATUS_CHANGED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#INPUT_STATUS_CHANGED) | 远端是否允许输入 |
| [SCREEN_CONFIG_CHANGE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#SCREEN_CONFIG_CHANGE) | 云端分辨率或横竖屏状态改变 |
| [CLIENT_STATS](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CLIENT_STATS) | 性能数据通知 |
| [REMOTE_DESKTOP_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#REMOTE_DESKTOP_INFO) | 远端桌面信息 |
| [CURSOR_STATE_CHANGE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CURSOR_STATE_CHANGE) | 鼠标显示状态变换 |
| [MULTI_USER_SEAT_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#MULTI_USER_SEAT_INFO) | 多人云游房间信息刷新 |
| [MULTI_USER_ROLE_APPLY](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#MULTI_USER_ROLE_APPLY) | 角色切换申请信息 |
| [CURSOR_IMAGE_INFO](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#CURSOR_IMAGE_INFO) | 鼠标图片信息 |
| [VIDEO_STREAM_CONFIG_CHANGED](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#VIDEO_STREAM_CONFIG_CHANGED) | 视频流分辨率变化 |
| [INPUT_STATE_CHANGE](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSession.Event.html#INPUT_STATE_CHANGE) | 输入框点击状态变化 |




## [TcrSessionConfig]()

[云游会话配置类-设置配置](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.Builder.html)


| API                                                                                                                                                                  | 描述                    |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| [build](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.Builder.html#build--)                              | 构造出一个TcrSessionConfig |
| [observer​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#observer)                                 | 设置事件通知监听              |
| [idleThreshold​​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#idleThreshold)                      | 设置空闲检测阈值              |
| [lowFpsThreshold​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#lowFpsThresholdCount)              | 设置低帧率通知阈值             |
| [videoFrameCallback​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#videoFrameBufferCallback)       | 设置自定义视频数据回调           |
| [enableLowLegacyRendering​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#enableLowLegacyRendering) | 开启低延时渲染               |
| [preferredCodec​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.html#preferredCodec)                     | 设置首选编解码器              |
| [enableCustomAudioCapture​](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TcrSessionConfig.Builder.html#enableCustomAudioCapture-boolean-int-boolean-)                     | 开启自定义音频采集             |



## AudioSink

音频数据回调接口

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onAudioData](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/AudioSink.html#onAudioData-byte:A-int-int-) | 回调音频数据 |
| [onAudioFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/AudioSink.html#onAudioFormat-int-int-int-) | 首次回调时返回音频采样格式 |

## VideoFrameBufferCallback
自定义视频数据帧回调，回调解码前的视频裸数据字节流。设置回调拿到数据后自行解码并渲染画面

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onVideoBufferCallback](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onVideoBufferCallback-java.nio.ByteBuffer-int-int-long-) | 视频帧数据回调 |
| [onMediaCodecFormat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/VideoFrameBufferCallback.html#onMediaCodecFormat-java.lang.String-int-int-) | MediaFormat初始化参数 |

## VideoSink
自定义视频数据帧回调，回调解码后的视频帧。
| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onFrame](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/VideoSink.html#onFrame-com.tencent.tcr.sdk.api.VideoFrame-) | 视频帧数据回调 |

## Keyboard

云端键盘交互类

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [onKeyboard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Keyboard.html#onKeyboard-int-boolean-) | 触发云端键盘按键事件     |
| [checkKeyboardCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Keyboard.html#checkKeyboardCapsLock-com.tencent.tcr.sdk.api.AsyncCallback-) | 查询云端键盘的大小写状态 |
| [resetKeyboardCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Keyboard.html#resetKeyboardCapsLock--) | 重置云端键盘的大小写状态 |
| [resetKeyboard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Keyboard.html#resetKeyboard--) | 重置云端键盘的按键状态   |



## Mouse

云端鼠标交互类，直接操作云端鼠标，不会修改本地TcrRenderView鼠标状态

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onMouseDeltaMove](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseDeltaMove-int-int-) | 让云端鼠标相对移动距离 |
| [onMouseMoveTo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseMoveTo-int-int-) | 让云端鼠标移动到坐标点 |
| [onMouseKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseKey-com.tencent.tcr.sdk.api.Mouse.KeyType-boolean-) | 触发云端鼠标的点击事件 |
| [onMouseScroll](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Mouse.html#onMouseScroll-boolean-) | 让云端鼠标滚轮滚动     |
| [setMouseCursorStyle](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Mouse.html#setMouseCursorStyle-com.tencent.tcr.sdk.api.Mouse.CursorStyle-) | 设置鼠标样式       |


## Gamepad

云端手柄交互类

| API                                                          | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| [connectGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Gamepad.html#connectGamepad--) | 触发云端手柄插入事件         |
| [disconnectGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Gamepad.html#disconnectGamepad--) | 触发云端手柄断开事件         |
| [onGamepadStick](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadStick-com.tencent.tcr.sdk.api.Gamepad.KeyType-int-int-) | 触发云端手柄摇杆事件         |
| [onGamepadKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadKey-int-boolean-) | 触发云端手柄按键事件         |
| [onGamepadTrigger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/Gamepad.html#onGamepadTrigger-com.tencent.tcr.sdk.api.Gamepad.KeyType-int-boolean-) | 触发云端手柄的L2R2触发键事件 |


## TouchScreen

云端触摸屏交互类

| API                                                          | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| [touch](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/TouchScreen.html#touch-float-float-int-int-int-int-long-) | 触发云端触摸屏的触摸事件。         |



## CustomDataChannel

数据通道相关接口

| API                                                          | 描述                 |
| ------------------------------------------------------------ | -------------------- |
| [send](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/CustomDataChannel.html#send-java.nio.ByteBuffer-) | 通过数据通道发送数据 |
| [close](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/CustomDataChannel.html#close--) | 关闭数据通道         |

## CustomDataChannel.Observer

数据通道监听器

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onConnected](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onConnected-int-) | 创建数据通道成功的回调 |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onError-int-int-java.lang.String-) | 发生错误的回调         |
| [onMessage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/CustomDataChannel.Observer.html#onMessage-int-java.nio.ByteBuffer-) | 接收云端消息           |



## TcrRenderView

渲染视图相关接口

| API                                                          | 描述                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setOnTouchListener-View.OnTouchListener-) | 设置视图点击事件的监听器              |
| [handleMotion](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#handleMotion-MotionEvent-) | 触摸事件传递                          |
| [setVideoScaleType](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setVideoScaleType-com.tencent.tcr.sdk.api.view.TcrRenderView.ScaleType-) | 设置视频缩放类型                      |
| [setVideoRotation](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setVideoRotation-com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation-) | 设置画面旋转角度                      |
| [setDisplayDebugView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setDisplayDebugView-boolean-) | 显示调试视图                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#release--) | 释放当前视图的底层资源                |
| [setEnableSuperResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableSuperResolution-boolean-) | [开启/关闭视频流超分辨率能力](API使用示例.md#TcrRenderView)|
| [setEnableInputDeviceHandle](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableInputDeviceHandle-boolean-) | 开启/关闭处理外设输入能力|
| [getKeyCodeMapping](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#getKeyCodeMapping--) | 获取android与windows按键码映射表|
| [setEnableFrameCallback](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/TcrRenderView.html#setEnableFrameCallback-float-) | 开启关闭渲染视频帧回调|

## PcZoomHandler

缩放、拖动视图相关接口

| API                                                          | 描述                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setZoomRatio](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomRatio-float-float-) | 设置放大缩小比例              |
| [resetZoom](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#resetZoom--) | 重置拖动和缩放                          |
| [setZoomOffset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomOffset-Rect-) | 设置拖动视图的边界偏移                      |
| [setZoomListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcZoomHandler.html#setZoomListener-com.tencent.tcr.sdk.api.view.PcZoomHandler.ZoomListener-) | 设置缩放、拖动数据监听器                      |

## PcTouchListener

触摸事件处理类

| API                                                          | 描述                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setZoomHandler](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setZoomHandler-com.tencent.tcr.sdk.api.view.PcZoomHandler-) | 设置缩放处理类              |
| [setMouseConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setMouseConfig-boolean-float-boolean-) | 设置鼠标参数                          |
| [	getZoomHandler](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#getZoomHandler--) | 返回缩放处理类                      |
| [setShortClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setShortClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnClickListener-) | 设置鼠标单击处理器                      |
| [	setLongClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setLongClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnClickListener-) | 设置鼠标长按处理器                      |
| [setDoubleClickListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/view/PcTouchListener.html#setDoubleClickListener-com.tencent.tcr.sdk.api.view.PcTouchListener.OnDoubleClickListener-) | 设置鼠标双击处理器                      |


## AsyncCallback<T>

通用的异步任务回调类

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/AsyncCallback.html#onSuccess-T-) | 异步执行成功的结果回调 |
| [onFailure](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/AsyncCallback.html#onFailure-int-java.lang.String-) | 异步执行出错的回调     |

## VideoCapabilityUtil

查询视频解码能力的工具类

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [getRecommendedResolution](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/utils/VideoCapabilityUtil.html#getRecommendedResolution-int-int-) | 返回推荐的视频分辨率 |

## CustomAudioBufferUtil
用于自定义采集音频，获取每个ByteBuffer的大小。

| API                                                          | 描述                     |
| ------------------------------------------------------------ |------------------------|
| [getCustomAudioCaptureDataBufferSize](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/3.18.0/com/tencent/tcr/sdk/api/utils/CustomAudioBufferUtil.html#getCustomAudioCaptureDataBufferSize-int-boolean-) | 返回自定义采集音频ByteBuffer的大小 |


