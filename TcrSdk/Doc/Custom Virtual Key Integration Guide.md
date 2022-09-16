- [中文文档](自定义虚拟按键接入指南.md)

# Custom Virtual Key


The custom virtual key is an SDK component enabling you to add virtual keys to your application. After integrating the Real-Time Cloud Rendering SDK, you can integrate the custom virtual key SDK into your project so as to add appropriate key features to your application.

The custom virtual key SDK provides easy-to-use editing features. After integrating the SDK, you can dynamically generate custom virtual keys. Editing features include adding a key (such as mouse or controller button) and adjusting the key position and size.


## Feature Overview
### Supported keys

Three types of keys are supported: mouse and arrow keys, keyboard, and controller.

#### Mouse and arrow keys

1. Mouse buttons: Left button, middle button, right button, scroll-up, and scroll-down

2. Arrow keys: Up, down, left and right/W, A, S, and D
    Four common keys W, A, S, and D form arrow keys just like a D-pad on a controller. If the up direction is pressed, a message of the `W` key will be triggered. If a diagonal direction is pressed, its two adjacent keys will be triggered.
    Messages of the up, down, left, and right arrow keys are triggered in the same way.

#### General keyboard keys

1. 78 commonly used keys on a physical keyboard

#### Controller

1. Left and right sticks 

2. D-pad

3. General pressable buttons: A, B, X, Y, △, ☐, ×, ○, Select, Start, LB, RB, L3, and R3

4. Triggers: LT and RT (*If a trigger is pressed, its message will be sent multiple times in a row instantly, and the message strength decreases gradually over time.*)

### Supported editing features

- Dynamically adding/deleting a key.
- Dynamically adjusting the position and size of a key.
- Positioning a key by using auxiliary lines.
- Renaming a pressable key (the name can contain up to six characters).

Note: Resources such as spliced UI images cannot be set dynamically.

## API overview
### GamepadManager

| API | Description |
| ------------------------------------------------------------ | ------------------------ |
| [setEditListener](https://tencentyun.github.io/cloudgame-android-sdk/tcr-gamepad/com/tencent/tcrgamepad/GamepadManager.html#setEditListener(com.tencent.tcrgamepad.GamepadManager.OnEditListener))     | Sets the listener for virtual key editing events.  |
| [setGamePadTouchDelegate](https://tencentyun.github.io/cloudgame-android-sdk/tcr-gamepad/com/tencent/tcrgamepad/GamepadManager.html#setGamePadTouchDelegate(IGamepadTouchDelegate))                        | Sets the delegate of touch events. |
| [showGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcr-gamepad/com/tencent/tcrgamepad/GamepadManager.html#showGamepad(java.lang.String))                 | Displays the virtual controller.             |
| [editGamepad](https://tencentyun.github.io/cloudgame-android-sdk/tcr-gamepad/com/tencent/tcrgamepad/GamepadManager.html#editGamepad(java.lang.String)) | Edits the virtual controller.             |
| [needConnected](https://tencentyun.github.io/cloudgame-android-sdk/tcr-gamepad/com/tencent/tcrgamepad/GamepadManager.html#needConnected())                             | Determines whether the key is a controller button.       |


## Integrating the custom virtual key SDK

1. The custom virtual key component depends on the Real-Time Cloud Rendering SDK. Therefore, integrate the [Real-Time Cloud Rendering SDK](Getting-Started.md) into your application first.

2. Integrate the SDK. Add the following content to the `build.gradle` file of your application module:

```groovy
dependencies {
	 ......
    implementation 'com.tencent.tcr:tcr-gamepad:2.0.1'
}
```

3. Initialize the virtual key view and read the configuration file during initialization.

``` java
mGamePadManager = new GamepadManager(this);
mCustomGamePadCfg = readConfigFile("lol_5v5.cfg");  // You can implement the reading method on your own.
```

4. Initialize virtual keys.

```java
mKeyboardView = new KeyboardView(this);

```
5. Add the custom editing listener.

```java
mGamePadManager.setEditListener((isChanged, newCfg) -> {
            if (isChanged){
                mCustomGamePadCfg = newCfg;
            }
            mGamePadManager.showGamepad(mCustomGamePadCfg); // Update the key view after editing.
        });
```

6. Open the virtual key view.

```java
mGamePadManager.showGamepad(mCustomGamePadCfg); // Open the virtual key view.
```
7. Open and close the editing view.

```java
mGamePadManager.editGamepad(mCustomGamePadCfg); // Open the custom editing view.
```

**Above are the core steps of integration. For the specific code, see [Demo](../Demo).**



## Configuration file generation
### Generating a layout configuration file
A layout configuration file is a JSON configuration file. The SDK can load it to generate a layout of virtual keys. The demo project provides two common layout configuration files, which can be viewed in the `asserts` directory of the project. In addition, you can use the following configuration file generation tool to generate virtual key layouts suitable for various applications and save the configuration files for your applications to load and call them.
### Configuration file generation tool
A [custom virtual key configuration tool](../Tools/vktool-release.apk) is provided to generate key layout configuration files for the SDK. In addition, you can save `jsonCfg`, which is the edited configuration file content, in the `OnEditListener.onFinishEdit(boolean isChanged, String jsonCfg)` callback to your local storage in your code.

Note: You only need to save and import the generated configuration file for your application to use it with no need of caring about the content and generation rules in the file.

## FAQs
1. **Which is the earliest Android version supported by the virtual key SDK?**  
Android 5.0 (API level 21).
