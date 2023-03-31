- [English document](API_Documentation.md)

## TCRSDK

## TcrSdk

### SDK 基础接口

| API                                                          | 描述                  |
| ------------------------------------------------------------ | --------------------- |
| [setLogger]() | 设置SDK的日志回调级别 |
| [init]() | 初始化SDK             |
| [createTcrSession]() | 创建Tcr会话对象       |
| [createTcrRenderView]() | 创建Tcr渲染视图对象   |


## TcrSession

### 生命周期相关接口 

| API                                                          | 描述                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| [start]() | 启动会话，拿到云端返回的serverSession后发起SDK到云端的连接 |
| [release]() | 销毁会话，断开本地和云端的连接，释放资源                   |
| [setRenderView]() | 设置会话的渲染视图，SDK会将云端画面渲染到此视图上          |

### 音视频相关接口

| API                                                          | 描述                              |
| ------------------------------------------------------------ | --------------------------------- |
| [pauseStreaming]() | 暂停音视频传输                    |
| [resumeStreaming]() | 恢复音视频传输                    |
| [setRemoteVideoProfile]() | 设置串流视频的帧率和码率          |
| [setRemoteAudioProfile]() | 设置音量放大系数                  |
| [toggleLocalAudio]() | 启用禁用麦克风，默认值false不开启 |
| [toggleLocalVideo]() | 启用禁用本地视频上行，默认值false不开启 |
| [setLocalVideoProfile]() | 设置摄像头的传输帧率和码率   |
| [setLocalAudioProfile]() | 设置麦克风的音量   |


### 云端应用交互接口

| API                                                          | 描述                       |
| ------------------------------------------------------------ | -------------------------- |
| [restartCloudApp]() | 重启云端应用进程           |
| [pasteText]() | 粘贴文本到云端应用的输入框 |
| [setRemoteDesktopResolution]() | 设置云端桌面的分辨率   |

### 多人云游接口

| API                                                          | 描述               |
| ------------------------------------------------------------ | ------------------ |
| [changeSeat]() | 改变某个用户的坐席 |
| [requestChangeSeat]() | 申请切换席位 |
| [setMicMute]() | 改变某个用户的麦克风状态 |
| [setRemoteAudioProfile]() | 调整某个用户的音量 |
| [syncRoomInfo]() | 刷新房间信息 |

### 按键接口

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [getKeyBoard]() | 获取与云端键盘交互的对象 |
| [getMouse]() | 获取与云端鼠标交互的对象 |
| [getGamePad]() | 获取与云端手柄交互的对象 |

### 数据通道接口

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [createCustomDataChannel]() | 创建数据通道 |

### TcrSession.Observer
| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onEvent]() | 事件通知 |

### TcrSession.Event
| 定义                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [STATE_INITED]() | 初始化成功|
| [STATE_CONNECTED]() | 连接成功 |
| [STATE_RECONNECTING]() | 重连中 |
| [STATE_CLOSED]() | 会话关闭 |
| [CLIENT_IDLE]() | 用户无操作 |
| [CLIENT_LOW_FPS]() | 帧率低状态 |
| [GAME_START_COMPLETE]() | 远端游戏状态变化 |
| [ARCHIVE_LOAD_STATUS]() | 存档加载状态变化 |
| [ARCHIVE_SAVE_STATUS]() | 存档保存状态变化 |
| [INPUT_STATUS_CHANGED]() | 远端是否允许输入 |
| [SCREEN_CONFIG_CHANGE]() | 云端分辨率或横竖屏状态改变 |
| [CLIENT_STATS]() | 性能数据通知 |
| [REMOTE_DESKTOP_INFO]() | 远端桌面信息 |
| [CURSOR_STATE_CHANGE]() | 鼠标显示状态变换 |
| [MULTI_USER_SEAT_INFO]() | 多人云游房间信息刷新 |
| [MULTI_USER_ROLE_APPLY]() | 角色切换申请信息 |
| [CURSOR_IMAGE_INFO]() | 鼠标图片信息 |



## [TcrSessionConfig]()

[云游会话配置类-设置配置]()


| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [build]() | 构造出一个TcrSessionConfig |
| [observer​]() | 设置事件通知监听|
| [connectTimeout​]() | 设置连接超时时间 |
| [idleThreshold​​]() | 设置空闲检测阈值 |
| [lowFpsThreshold​]() | 设置低帧率通知阈值 |
| [videoFrameCallback​]() | 设置自定义视频数据回调 |
| [audioSampleCallback​]() | 设置WebRTC解码后的音频数据的消费者 |
| [enableLowLegacyRendering​]() | 开启低延时渲染 |



## AudioSampleCallback

音频数据回调接口

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onAudioData]() | 回调音频数据 |
| [onAudioFormat]() | 首次回调时返回音频采样格式 |

## VideoFrameBufferCallback
自定义视频数据帧回调，回调解码前的视频裸数据字节流。设置回调拿到数据后自行解码并渲染画面

| API                                                          | 描述         |
| ------------------------------------------------------------ | ------------ |
| [onVideoBufferCallback]() | 视频帧数据回调 |
| [onMediaCodecFormat]() | MediaFormat初始化参数 |

## Keyboard

云端键盘交互类

| API                                                          | 描述                     |
| ------------------------------------------------------------ | ------------------------ |
| [onKeyboard]() | 触发云端键盘按键事件     |
| [checkKeyboardCapsLock]() | 查询云端键盘的大小写状态 |
| [resetKeyboardCapsLock]() | 重置云端键盘的大小写状态 |
| [resetKeyboard]() | 重置云端键盘的按键状态   |



## Mouse

云端鼠标交互类，直接操作云端鼠标，不会修改本地TcrRenderView鼠标状态

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onMouseDeltaMove]() | 让云端鼠标相对移动距离 |
| [onMouseMoveTo]() | 让云端鼠标移动到坐标点 |
| [onMouseKey]() | 触发云端鼠标的点击事件 |
| [onMouseScroll]() | 让云端鼠标滚轮滚动     |
| [setMouseCursorStyle]() | 设置鼠标样式       |


## Gamepad

云端手柄交互类

| API                                                          | 描述                         |
| ------------------------------------------------------------ | ---------------------------- |
| [connectGamepad]() | 触发云端手柄插入事件         |
| [disconnectGamepad]() | 触发云端手柄断开事件         |
| [onGamepadStick]() | 触发云端手柄摇杆事件         |
| [onGamepadKey]() | 触发云端手柄按键事件         |
| [onGamepadTrigger]() | 触发云端手柄的L2R2触发键事件 |

                                    


## CustomDataChannel

数据通道相关接口

| API                                                          | 描述                 |
| ------------------------------------------------------------ | -------------------- |
| [send]() | 通过数据通道发送数据 |
| [close](h) | 关闭数据通道         |

## CustomDataChannel.Observer

数据通道监听器

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onConnected]() | 创建数据通道成功的回调 |
| [onError]() | 发生错误的回调         |
| [onMessage]() | 接收云端消息           |



## TcrRenderView

渲染视图相关接口

| API                                                          | 描述                                  |
| ------------------------------------------------------------ | ------------------------------------- |
| [setOnTouchListener]() | 设置视图点击事件的监听器              |
| [handleMotion]() | 触摸事件传递                          |
| [setVideoScaleType]() | 设置视频缩放类型                      |
| [setVideoRotation]() | 设置画面旋转角度                      |
| [enablePinch]() | 视频图层是否支持双指手势(放大、拖动)  |
| [setPinchOffset]()) | 设置videoView在父视图上拖动的边缘间距 |
| [setOnPinchListener]() | 设置视频图层在缩放时的回调事件        |
| [resetView]() | 重置视图的位置与大小为初始情况        |
| [showDebugView]() | 显示调试视图                          |
| [release]() | 释放当前视图的底层资源                |
| [setCursorImage]() | 设置鼠标图片                          |
| [setCursorVisibility]() | 设置鼠标可见性                         |
| [setCursorPos]() | 设置鼠标位置在手机上的绝对位置        |
| [enableSuperResolution]() | [开启/关闭视频流超分辨率能力](API使用示例.md#TcrRenderView)|
## 

## AsyncCallback<T>

通用的异步任务回调类

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [onSuccess]() | 异步执行成功的结果回调 |
| [onFailure]() | 异步执行出错的回调     |

## VideoCapabilityUtil

查询视频解码能力的工具类

| API                                                          | 描述                   |
| ------------------------------------------------------------ | ---------------------- |
| [getRecommendedResolution]() | 返回推荐的视频分辨率 |

