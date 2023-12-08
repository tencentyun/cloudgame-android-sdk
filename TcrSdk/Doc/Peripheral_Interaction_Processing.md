- [中文文档](外设交互处理.md)

# Introduction
The client App can send peripheral events such as keyboard, mouse, gamepad, and touch events to the cloud system through the SDK. The interaction process between the front and back ends can be found in this [link](https://cloud.tencent.com/document/product/1547/102297).

# Basic Usage

## Keyboard Events
The client can obtain the `Keyboard` object through `TcrSession#getKeyboard()` and send any key press and release events of the keyboard to the cloud. The KeyCode specification for the Windows keyboard can be found in this [link](https://www.toptal.com/developers/keycode), and the internal definition in the SDK can be found in the `WindowsKeyEvent` class.

## Mouse Events
The client can obtain the `Mouse` object through `TcrSession#getMouse()` and send mouse left and right button press, release, and movement events, as well as mouse middle button wheel events to the cloud.

## Gamepad Events
If the cloud application supports gamepad operation (e.g., gamepad games), the client can obtain the `Gamepad` object through `TcrSession#getGamepad()` and send any gamepad key press and release events to the cloud. To trigger the connection and disconnection events of the gamepad on the cloud side, you can call the `Gamepad#connectGamepad()` and `Gamepad#disconnectGamepad()` methods.

## Touch Events
Both mobile applications (Android system) and desktop applications (Windows system) in the cloud can support touch screen operation. When the cloud application supports touch screen operation, the client can obtain the `TouchScreen` object through `TcrSession#getTouchScreen()` and send touch events to the cloud.

# Client Touch Events to Cloud Peripheral Events
If you use the SDK-provided `TcrRenderView` view for rendering, we also provide some default implementation classes for the touch events on this view, which can easily convert client touch events into cloud peripheral events and send them to the cloud, such as mapping the click events on the client touch screen to the click events of the cloud mouse. This is suitable for most customers to use without complex development.

## MobileTouchListener
This class implements the automatic conversion of touch events on the `TcrRenderView` view to touch events on the cloud touch screen and sends them to the cloud. The touch coordinate point values will be calculated based on the ScaleType of the view.

### Usage
Create an instance of this class and set it to the rendering view through `TcrRenderView#setOnTouchListener()`, for example:

```
TcrRenderView.setOnTouchListener(new MobileTouchListener(mSession));
```

## PcTouchListener
This class implements the automatic conversion of touch events on the `TcrRenderView` view to cloud mouse events and sends them to the cloud. The default touch behavior implemented by `PcTouchListener` is as follows:

- Single-finger short press: When the user presses down on the `TcrRenderView`, the left mouse button press event is sent to the cloud, and when the user releases, the left mouse button release event is sent to the cloud.
- Single-finger long press: When the user presses down on the `TcrRenderView`, the left mouse button press event is sent to the cloud, and when the user releases, the left mouse button release event is sent to the cloud.
- Single-finger movement: When the user moves on the `TcrRenderView`, the mouse movement event is sent to the cloud, and the mouse position on the `TcrRenderView` view is moved. The client can modify the mouse movement mode (e.g., relative movement, absolute movement), movement speed, and whether the mouse is hidden through the `PcTouchListener#setMouseConfig()` function.
- Single-finger double-click: No action.
- Two-finger operation: When the user pinches with two fingers on the `TcrRenderView`, the view can be scaled; when the user translates with two fingers, the view can be dragged, and this operation will not send any events to the cloud.

### Usage
Create an instance of this class and set it to the rendering view through `TcrRenderView#setOnTouchListener()`, for example:

```
TcrRenderView.setOnTouchListener(new PcTouchListener(mSession));
```

### Advanced Usage
If the default touch behavior implemented by `PcTouchListener` does not meet your needs, you can customize and modify some of the default behaviors.

- Single-finger short press: You can implement the `OnClickListener` interface and customize the behavior of single-finger short press events through `PcTouchListener#setShortClickListener()`. If not customized, `PcTouchListener` will use the `PcClickListener` object to respond to single-finger short press events by default. You can also customize the type of mouse button events to be sent to