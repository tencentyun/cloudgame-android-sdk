- [中文文档](发布记录.md)

# [Version 3.5.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.5.0/TcrSdk) (2023-07-03)
**Features**

- Optimize idle detection.
- Audio and video sender maintains resolution under weak network conditions.
- Optimize the connection establishment time, saving about 56% in SDP creation and parsing time.


**Bug Fixes**
- Fixed creating a profile per codec when negotiating available codecs. The fixed behavior is to only create the preferred codecs and the rest only when needed.
- Fixed some known bugs.
# [Version 3.4.1](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.4.1/TcrSdk) (2023-06-19)
**Bug Fixes**

- Fixed some bugs.

# [Version 3.4.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.4.0/TcrSdk) (2023-06-09)
**Features** 

- TcrSession adds 'getTouchScreen' method to get TouchScreen object
- Added TouchScreen interface for interacting with the cloud touch screen

# [Version 3.3.3](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.3.3/TcrSdk) (2023-06-07)
**Bug Fixes**

- Fixed some bugs.

# [Version 3.3.2](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.3.2/TcrSdk) (2023-05-25)
**Bug Fixes**

- Fixed some bugs.

# [Version 3.3.1](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.3.1/TcrSdk) (2023-05-09)
**Bug Fixes**

- Fixed some bugs.

# [Version 3.3.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.3.0/TcrSdk) (2023-05-08)
**Features** 

- TcrRenderView added Observer Call back the first frame rendering event

**Bug Fixes**

- Fixed some bugs.


# [Version 3.2.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.2.0/TcrSdk) (2023-04-21)
**Features** 

- TcrRenderView added the function of automatically processing peripheral input events
- TcrRenderView new interface setEnableInputDeviceHandle&getKeyCodeMapping 

# [Version 3.0.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/3.0.0/TcrSdk) (2023-04-07)

**Features**

- Refactored SDK interface: adjusted package structure, interface/method definition and documentation.
- Optimized the implementation of the SDK.

**Bug Fixes**

- Fixed some bugs.

# [Version 2.6.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.6.0/TcrSdk) (2023-05-26)
**Bug Fixes**

- Fixed some bugs.

# [Version 2.5.7](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.7/TcrSdk) (2023-04-20)
Bug Fixes
- Fix the problem of crashing on some models

# [Version 2.5.5](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.5/TcrSdk) (2023-03-30)
Bug Fixes
- Fix the problem that the lite version of sdk fails after the app dependency is turned on and mixed
- Fix and optimize some known issues

# [Version 2.5.4](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.4/TcrSdk) (2023-03-29)
Bug Fixes
- Fixed the bug that when using H265, calling the setRemoteDesktopResolution(int, int) interface on the terminal may cause hardware decoding to fall back to software decoding.
# [Version 2.5.3](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.3/TcrSdk) (2023-03-14)
Bug Fixes
- Fix and optimize some known issues.
# [Version 2.5.2](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.2/TcrSdk) (2023-02-28)
Features
- TcrSessionConfig newly added method enableLowLegacyRendering(boolean) for reduce local rendering delay.  
Bug Fixes
- Fix and optimize some known issues.
# [Version 2.5.1](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.1/TcrSdk) (2023-02-13)
Bug Fixes
- Fix and optimize some known issues.

# [Version 2.5.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.5.0/TcrSdk) (2023-01-04)
Features
- Tcr-GamePad supports distinguishing left and right shift/ctrl/alt keys.

# [Version 2.4.3](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.4.3/TcrSdk) (2022-12-22)
Bug Fixes
- Fix and optimize some known issues.

# [Version 2.4.2](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.4.2/TcrSdk) (2022-12-19)
Bug Fixes
- Fix the compatibility issue with hot update plugin tinker.

# [Version 2.4.1](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.4.1/TcrSdk) (2022-12-16)
Bug Fixes
- Fix and optimize some known issues.

# [Version 2.4.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.4.0/TcrSdk) (2022-12-09)
Features
- TcrSession newly added method setRemoteDesktopResolution(int, int) for setting the resolution of cloud desktop. 

Bug Fixes
- Fix and optimize some known issues.
# [Version 2.3.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.3.0/TcrSdk) (2022-11-14)
Features
- TcrRenderView newly added method(enableSuperResolution) to enable and disable super-resolution capabilities
- Added VideoCapabilityUtil, a tool class for querying video resolution decode capabilities, to obtain appropriate video resolutions
# [Version 2.2.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.2.0/TcrSdk) (2022-09-29)
Features
- TcrEvent adds remote desktop information event notification.
- Added interface MouseInfoListener to call back remote mouse data
- The interface javadoc is updated from Chinese to English
- Mouse#setMouseConfig(MouseConfig) is modified to setCursorStyle(CursorStyle)
- Mouse#onMoveTo and onDeltaMove interfaces change the parameter type from float to int

Bug Fixes
- Fix and optimize some known issues
# [Version 2.1.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.1.0/TcrSdk) (2022-09-14)
Features
- Support dynamic opening and closing of local audio and video(mic and camera).
- Support choose callback media stream raw data.
# [Version 2.0.3](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.0.3/TcrSdk) (2022-08-18)
Features
- The video stream adopts hardware decoding by default. If hardware decoding fails, software decoding will fall back.

# [Version 2.0.2]() (2022-08-17)
Bug Fixes
-  Fixed the issue where the mouse show/hidden status different between local and cloud.

# [Version 2.0.1]() (2022-07-13)
Features
- Designed Real-Time Cloud Rendering as a plugin and provided complete and lightweight SDKs, which share the same APIs.
- Redesigned APIs and abstracted sessions and input APIs for peripheral devices such as mouse, keyboard, and controller.
- Simplified APIs. Deleted the redundant listener registration API and changed it to a message notification.
- Abstracted and simplified the rendering view `TcrRenderView` and supported features such as view zoom as well as image zoom and rotation.
- Changed the mouse mode setting to more flexible touch processing classes `PcTouchHandler` and `MobileTouchHandler` to make it easy for you to extend operation modes on your own.
- Added support for dynamic opening of local video upstreaming (resolution, frame rate, and front/rear camera switch).

# [Version 1.5.9](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/1.5.9/TcrCloudGame) (2022-01-25)
Bug fixes
- Fixed the issue where reconnection failed and the game stopped after network disconnection.

# Version 1.5.6 (2022-01-13)
Bug fixes
- Fixed the issue where garbled text was returned by the custom data channel.

# Version 1.5.4 (2021-12-02)
Bug fixes
- Fixed the issue where the `Delete` or `BackSlash` key of the external keyboard was mapped to an incorrect key.

# Version 1.5.1 (2021-11-24)
Bug fixes
- Fixed the issue where the cursor position deviated.

# Version 1.5.0 (2021-11-05)
Features
- Cloud PC game: Supported the external physical keyboard.
- API change: Supported the `(IMobileTcgSdk.gameRestart())` API (cloud game restart API) for cloud mobile games.
- Removed the old virtual controller module `tcgui`.

# Version 1.4.1 (2021-10-15)
Bug fixes
- Virtual controller: Fixed the issue where D-pad key events failed to be sent.

# Version 1.4.0 (2021-10-15)
Bug fixes
- Fixed the issue where `Window` could not take effect after it was removed from `GameView` and added to the view tree again (the cursor could not be displayed, and events could not be processed).
Features
- API change: Changed the constructor parameter `IViewRenderer` of `Builder` to `GameView`.
- API change: Eliminated the need to call the `GameView.setSdk` API by the client.
- Behavior change: Required that attributes be set separately during `GameView` creation (the SDK on earlier versions will copy the attributes set for the first `GameView` to subsequently created `GameView` objects in APIs such as `setCursorType` and `setMoveSensitivity`).

# Version 1.3.1 (2021-10-04)
Features
- Changed the `ITcgSdk.replace` API to make it no longer release the game view. You need to release the game view that is no longer needed through `SimpleGameView.release`. If a released game view (`SimpleGameView`) is passed in, the `IllegalStateException` exception will be reported.
- Moved `KeyboardView` from `tcgui` to `tcgui-gamepad` (if a virtual keyboard is used, the dependent component needs to be changed to `tcgui-gamepad`).
- Added support for H.265 decoding.
- Mobile game API change: Deleted the automatic rotation logic of the game view and required that `SimpleGameView.setVideoRotation` be manually called to rotate the game image during `Activity` rotation (`ROTATION_0` for portrait mode and `ROTATION_270` for landscape mode).

# Version 1.2.6 (2021-09-03)
Bug fixes
- Fixed the issue where the stick didn't reset when the finger moved out of the screen in a mobile game operation. 

# Version 1.2.5 (2021-08-13)
Bug fixes
- Fixed the issue where the image was blurry after the game view was zoomed.
- Fixed the issue where the touch didn't take effect if the view port calculation was incorrect.

# Version 1.2.1 (2021-08-07)
Features
- Added support for cloud mobile games.
- Reduced the latency.
- Optimized automatic reconnection.

Bug fixes
- Fixed occasional crashes during network connection timeout.

# Version 1.1.8 (2021-06-21)
- Added support for the multiplayer interactive cloud game feature.
- Added support for speech upload.
