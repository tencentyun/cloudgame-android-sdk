## TCRSDK

## TcrSdk

### SDK 基础接口

| API                                                          | 描述                  |
| ------------------------------------------------------------ | --------------------- |
| [setLogger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#setLogger(ILogger,LogLevel)) | 设置SDK的日志回调级别 |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#init(Context,java.lang.String,com.tencent.tcr.sdk.api.AsyncCallback)) | 初始化SDK             |
| [createTcrSession](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrSession(com.tencent.tcr.sdk.api.config.TcrSessionConfig)) | 创建Tcr会话对象       |
| [createTcrRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSdk.html#createTcrRenderView(Context,com.tencent.tcr.sdk.api.TcrSession,com.tencent.tcr.sdk.api.TcrRenderViewType)) | 创建Tcr渲染视图对象   |



## TcrSession

### 生命周期相关接口 

| API                                                          | 描述                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [setListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setListener(com.tencent.tcr.sdk.api.TcrSessionListener)) | 设置会话监听器                                             |
| [start](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#start(java.lang.String,com.tencent.tcr.sdk.api.AsyncCallback)) | 启动会话，拿到云端返回的serverSession后发起SDK到云端的连接 |
| [init](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#init(com.tencent.tcr.sdk.api.AsyncCallback)) | 初始化会话，创建本地ClientSession                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#release()) | 销毁会话，断开本地和云端的连接，释放资源                   |
| [setRenderView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setRenderView(com.tencent.tcr.sdk.api.TcrRenderView)) | 设置会话的渲染视图，SDK会将云端画面渲染到此视图上          |

### 音视频相关接口

| API                                                          | 描述                              |
| ------------------------------------------------------------ | --------------------------------- |
| [pauseStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#pauseStreaming()) | 暂停音视频传输                    |
| [resumeStreaming](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#resumeStreaming()) | 恢复音视频传输                    |
| [setStreamProfile](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setStreamProfile(int,int,int,com.tencent.tcr.sdk.api.AsyncCallback)) | 设置串流视频的帧率和码率          |
| [setVolume](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#setVolume(float)) | 设置音量放大系数                  |
| [enableMic](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#enableMic(boolean)) | 启用禁用麦克风，默认值false不开启 |

### 云端应用交互接口

| API                                                          | 描述                                   |
| ------------------------------------------------------------ | -------------------------------------- |
| [restartCloudApp](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#restartCloudApp()) | 重启云端应用进程                       |
| [pasteText](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#pasteText(java.lang.String)) | 粘贴文本到云端应用的输入框             |
| [checkAutoLogin](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#checkAutoLogin(com.tencent.tcr.sdk.api.AsyncCallback)) | 查询云端应用的当前窗口是否支持自动登录 |
| [startAutoLogin](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#startAutoLogin(java.lang.String,java.lang.String,com.tencent.tcr.sdk.api.AsyncCallback)) | 进行自动登录                           |
| [cancelAutoLogin](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#cancelAutoLogin()) | 取消自动登录                           |

### 多人云游接口

| API                                                          | 描述               |
| ------------------------------------------------------------ | ------------------ |
| [getMultiUserManager](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getMultiUserManager()) | 获取多人云游管理器 |

### 按键接口

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getKeyBoard()) | 获取与云端键盘交互的对象 |
| [getMouse](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getMouse()) | 获取与云端鼠标交互的对象 |
| [getGamePad](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#getGamePad()) | 获取与云端手柄交互的对象 |

### 数据通道接口

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [createDataChannel](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSession.html#createDataChannel(int,com.tencent.tcr.sdk.api.datachannel.DataChannelListener)) | 创建数据通道 |



## [TcrSessionConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/config/TcrSessionConfig.html)

云游会话配置类，获取配置和设置配置

## TcrSessionListener 

会话监听器

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onEvent](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSessionListener.html#onEvent(java.lang.String,java.lang.String)) | 会话事件通知 |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrSessionListener.html#onError(int,java.lang.String)) | 会话出错回调 |



## Keyboard

云端键盘交互类

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#onKey(int,boolean)) | 触发云端键盘按键事件     |
| [checkCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#checkCapsLock(com.tencent.tcr.sdk.api.AsyncCallback)) | 查询云端键盘的大小写状态 |
| [resetCapsLock](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#resetCapsLock()) | 重置云端键盘的大小写状态 |
| [reset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Keyboard.html#reset()) | 重置云端键盘的按键状态   |



## Mouse

云端鼠标交互类，直接操作云端鼠标，不会修改本地TcrRenderView鼠标状态

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onDeltaMove](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onDeltaMove(float,float)) | 让云端鼠标相对移动距离 |
| [onMoveTo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onMoveTo(float,float)) | 让云端鼠标移动到坐标点 |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onKey(com.tencent.tcr.sdk.api.config.MouseKey,boolean)) | 触发云端鼠标的点击事件 |
| [onScroll](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#onScroll(boolean)) | 让云端鼠标滚轮滚动     |
| [setMouseConfig](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/Mouse.html#setMouseConfig(com.tencent.tcr.sdk.api.MouseConfig)) | 设置鼠标参数           |



## Gamepad

云端手柄交互类

| API                                                          | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| [connect](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#connect()) | 触发云端手柄插入事件         |
| [disconnect](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#disconnect()) | 触发云端手柄断开事件         |
| [onAxis](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onAxis(com.tencent.tcr.sdk.api.config.GamePadKey,int,int)) | 触发云端手柄摇杆事件         |
| [onKey](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onKey(int,boolean)) | 触发云端手柄按键事件         |
| [onTrigger](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/GamePad.html#onTrigger(com.tencent.tcr.sdk.api.config.GamePadKey,int,boolean)) | 触发云端手柄的L2R2触发键事件 |



## MultiUserManager

多人互动云游相关接口

| API                                                          | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [applyChangeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#applyChangeSeat(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int,com.tencent.tcr.sdk.api.AsyncCallback)) | 申请切换玩家的角色和席位                                     |
| [changeSeat](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#changeSeat(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int,com.tencent.tcr.sdk.api.AsyncCallback)) | 切换用户的角色或席位, 设置userID用户的角色为targetRole，席位为targetPlayerIndex |
| [setMicMute](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setMicMute(java.lang.String,boolean,com.tencent.tcr.sdk.api.AsyncCallback)) | 静音本地麦克风                                               |
| [setVolume](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setVolume(java.lang.String,float)) | 设置其他用户的音量增益                                       |
| [syncRoomInfo](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#syncRoomInfo()) | 刷新所有席位信息                                             |
| [setMultiUserListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/MultiUserManager.html#setMultiUserListener(com.tencent.tcr.sdk.api.multiplayer.SeatsListener)) | 注册多人云游监听器                                           |

## SeatsListener

多人互动云游监听器

| API                                                          | 描述                                         |
| ------------------------------------------------------------ | -------------------------------------------- |
| [onRoleApplied](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/SeatsListener.html#onRoleApplied(java.lang.String,com.tencent.tcr.sdk.api.multiplayer.MultiUserManager.Role,int)) | 当有用户申请席位变更时, 房主会收到该回调     |
| [onSeatChanged](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/multiplayer/SeatsListener.html#onSeatChanged(java.lang.String,java.util.List,java.util.List,int,int)) | 当席位信息出现变更时，所有的用户会收到该回调 |



## DataChannel

数据通道相关接口

| API                                                          | 描述                 |
| ------------------------------------------------------------ | -------------------- |
| [send](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannel.html#send(java.nio.ByteBuffer)) | 通过数据通道发送数据 |
| [close](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannel.html#close()) | 关闭数据通道         |

## DataChannelListener

数据通道监听器

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onCreateSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onCreateSuccess(int)) | 创建数据通道成功的回调 |
| [onError](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onError(int,com.tencent.tcr.sdk.api.datachannel.DataChannelListener.State,java.lang.String)) | 发生错误的回调         |
| [onMessage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/datachannel/DataChannelListener.html#onMessage(int,java.nio.ByteBuffer)) | 接收云端消息           |



## TcrRenderView

渲染视图相关接口

| API                                                          | 描述                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setOnTouchListener(View.OnTouchListener)) | 设置视图点击事件的监听器              |
| [handleMotion](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#handleMotion(MotionEvent)) | 触摸事件传递                          |
| [setVideoScaleType](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setVideoScaleType(com.tencent.tcr.sdk.api.ScaleType)) | 设置视频缩放类型                      |
| [setVideoRotation](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setVideoRotation(com.tencent.tcr.sdk.api.VideoRotation)) | 设置画面旋转角度                      |
| [enablePinch](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#enablePinch(boolean)) | 视频图层是否支持双指手势(放大、拖动)  |
| [setPinchOffset](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setPinchOffset(Rect)) | 设置videoView在父视图上拖动的边缘间距 |
| [setOnPinchListener](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setOnPinchListener(com.tencent.tcr.sdk.api.PinchListener)) | 设置视频图层在缩放时的回调事件        |
| [resetView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#resetView()) | 重置视图的位置与大小为初始情况        |
| [showDebugView](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#showDebugView(boolean)) | 显示调试视图                          |
| [release](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#release()) | 释放当前视图的底层资源                |
| [setCursorImage](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setCursorImage(Bitmap,int)) | 设置鼠标图片                          |
| [setCursorPos](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/TcrRenderView.html#setCursorPos(int,int)) | 设置鼠标位置在手机上的绝对位置        |

## 

## AsyncCallback<T>

通用的异步任务回调类

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AsyncCallback.html#onSuccess(T)) | 异步执行成功的结果回调 |
| [onFailure](https://tencentyun.github.io/cloudgame-android-sdk/tcrsdk/com/tencent/tcr/sdk/api/AsyncCallback.html#onFailure(int,java.lang.String)) | 异步执行出错的回调     |