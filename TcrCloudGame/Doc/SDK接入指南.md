## 环境要求

1. 建议使用 Android 4.1 （SDK API Level 16）及以上版本。

## 接入前准备

在接入云游戏前，需要完成接入前的[准备](https://cloud.tencent.com/document/product/1162/46135 )。

<img src="https://main.qcloudimg.com/raw/ef3f517af8dc21844bd4447db532ba52.svg" width="800px" />



## 操作步骤

腾讯云提供了云渲染终端程序 Android 入门  Demo  供参考，本章演示如何快速配置  Demo  运行简单端游及手游。Android 入门 Demo 提供了基础的云试玩能力，包含简单示例和一些常见 API 的使用，您可以在入门 Demo 的基础上运行您自己的游戏，详细使用步骤如下：

## 步骤1：下载 Demo[](id:step1)
单击 [下载](https://github.com/tencentyun/cloudgame-android-sdk/blob/master/TcrCloudGame/Demo.zip) Android  端入门 Demo 工程，将工程导入 AndroidStudio 工具。工程中有三个demo文件，分别对应端游Demo，手游Demo以及轻量版SdkDemo。在步骤2.3(#step2.3)中会介绍三个工程的不同点。

![](https://tva1.sinaimg.cn/large/e6c9d24egy1h32hp6ifd2j20jy06kwen.jpg)

## 步骤2：集成游戏[](id:step2)

1. 完成 [接入准备 - 步骤4](https://cloud.tencent.com/document/product/1162/46135#step4)的部署后,会在**云游戏控制台** > **游戏管理**中生成对应的 GAME_ID。您需将 GAME_ID 拷贝到工程中 server/CloudGameApi类的 GAME_ID下（**请注意不要填错位置**）。

```java
/**
* 云游后台游戏ID，该ID对应云游后台部署的游戏。
* 可以联系云游团队接口人部署自己的游戏。
*/
 public static final String GAME_ID = "xxxx";
```

2. 将您在 [创建后台程序 - 导入云函数](https://cloud.tencent.com/document/product/1162/65429#upload) 中获取到的 SERVER 地址替换 server/CloudGameApi类中启动和停止游戏的url下。

```java
final String START_GAME_URL = "https://xxx.myqcloud.com/StartGame";
final String STOP_GAME_URL = "https://xxx.myqcloud.com/StopGame";
```

3. 根据您的游戏类型和所选择接入的SDK类型打开对应demo工程，将上述步骤的内容进行修改和替换。手游和端游对触摸事件的处理方式不同。

```java
    /**
     * 为不同的云端实例设置处理器
     * 对于端游要使用{@link com.tencent.tcr.sdk.api.PcTouchHandler}
     * 对于手游要使用{@link com.tencent.tcr.sdk.api.MobileTouchHandler}
     */
    private void setTouchHandler(TcrSession session, TcrRenderView renderView) {
        switch (session.getRemoteDeviceMode()) {
            case ANDROID:
                renderView.setOnTouchListener(new MobileTouchHandler());
                break;
            case PC:
                PcTouchHandler pcTouchHandler = new PcTouchHandler();
                pcTouchHandler.enableScaling(1, 5);
                renderView.setOnTouchListener(pcTouchHandler);
                break;
            default:
                Log.e(TAG,"UNKNOWN DeviceMode!!");
        }
    }
```

>! 云游戏的进阶使用，请参见 [云游戏接入指南](https://github.com/tencentyun/cloudgame-android-sdk)。

## 轻量版云游SDK

1.轻量版SDK采用插件化设计，集成SDK的APP可以选择使用轻量版SDK(体积增量仅65KB)，运行时再从网络下载SDK插件(9MB)后进行动态加载。

2.二者在使用上并无区别，区别在于轻量版SDK需要下载插件并动态加载后成为完整的SDK包。

3.在使用选择方面：如果有对包体积大小有要求的APP，我们推荐集成轻量版SDK；如果对包体积大小没有限制，我们推荐使用完整版SDK，减少SDK加载的过程和提高集成速度。
