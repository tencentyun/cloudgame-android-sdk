## 环境要求

1. 建议使用 Android 5.0 （SDK API Level 21）及以上版本。
2. Android Studio 3.0 及以上版本。
3. App 要求 Android 5.0及以上设备。



## 轻量版和全量版SDK区别

1.轻量版SDK采用插件化设计，集成SDK的APP可以选择使用轻量版SDK(体积增量仅65KB)，运行时再从网络下载SDK插件(9MB)后进行动态加载。

2.二者在使用上并无区别，区别在于轻量版SDK需要下载插件并动态加载后成为完整的SDK包。

3.在使用选择方面：如果有对包体积大小有要求的APP，我们推荐集成轻量版SDK；如果对包体积大小没有限制，我们推荐使用完整版SDK，减少SDK加载的过程和提高集成速度。



## 操作步骤

腾讯云提供了云渲染终端程序 Android 入门  Demo  供参考，本章演示如何快速配置  Demo  运行简单端游及手游。Android 入门 Demo 提供了基础的云试玩能力，包含简单示例和一些常见 API 的使用，您可以在入门 Demo 的基础上运行您自己的游戏，详细使用步骤如下：

## 步骤1：下载 Demo[](id:step1)
单击 [下载](https://github.com/tencentyun/cloudgame-android-sdk/blob/master/TcrCloudGame/Demo.zip) Android  端入门 Demo 工程，将工程导入 AndroidStudio 工具。工程中有三个demo文件，分别对应端游Demo，手游Demo以及轻量版SdkDemo。在步骤2.3(#step2.3)中会介绍三个工程的不同点。

![](https://tva1.sinaimg.cn/large/e6c9d24egy1h32hp6ifd2j20jy06kwen.jpg)

## 步骤2：集成游戏[](id:step2)

1. 在接入云游戏前，需要完成接入前的[准备](https://cloud.tencent.com/document/product/1162/46135 )。

<img src="https://main.qcloudimg.com/raw/ef3f517af8dc21844bd4447db532ba52.svg" width="800px" />



2. 完成 [接入准备 - 步骤4](https://cloud.tencent.com/document/product/1162/46135#step4)的部署后,会在**云游戏控制台** > **游戏管理**中生成对应的 GAME_ID。您需将 GAME_ID 拷贝到工程中 Constant 类的手游或者端游的 GAME_ID下（**请注意不要填错位置**）。

```java
// demo工程下Constant目录下
public static final String PC_GAME_ID = "game-xxxxx";
public static final String MOBILE_GAME_ID = "game-xxxxx";
```

3. 将您在 [创建后台程序 - 导入云函数](https://cloud.tencent.com/document/product/1162/65429#upload) 中获取到的 SERVER 地址拷贝到工程中 Constant类的 SERVER 下。

```java
public static final String SERVER = "service-test-xxxxxxxxxx.gz.apigw.tencentcs.com/release";
```

4.完整版SDK和轻量版SDK在APP下的Build.gradle引用不同。

``` java
    implementation "com.tencent.tcr:tcrsdk-lite:2.0.0"  --轻量版
    implementation "com.tencent.tcr:tcrsdk-full:2.0.0"  --完整版

```

>! 云游戏的进阶使用，请参见 [云游戏接入指南](https://github.com/tencentyun/cloudgame-android-sdk)。

