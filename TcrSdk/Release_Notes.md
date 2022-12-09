- [中文文档](发布记录.md)

# [Version 2.4.0](https://github.com/tencentyun/cloudgame-android-sdk/tree/tcrsdk/2.4.0/TcrSdk) (2022-12-09)
Features
- TcrSession newly added method setRemoteDesktopResolution(int, int) for setting the resolution of the cloud desktop

Bug Fixes
- Fix and optimize some known issues
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
