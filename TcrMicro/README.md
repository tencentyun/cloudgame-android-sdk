# 微端
微端是使用云游戏技术构建出来的某款游戏的专属云游戏客户端。它提供了云试玩、登录支付穿透、静默下载升级的功能，体验接近原生游戏客户端。

因为微端的体积小，所以很适合用来做游戏买量广告投放，快速获客。

<img width="947" alt="微端模式" 
src="https://user-images.githubusercontent.com/8381597/177968325-8cfa9b4b-6bc2-43a6-bc7a-7db9e6c3f2d3.png">

# 发布记录
请跳转到[最新版本](发布记录.md)  

# 目录说明

``` shell
.
├── Demo
│   ├── 云端APP示例工程
│   │   ├── DataChannelTest.zip
│   │   └── TcrMicroCloud.zip
│   ├── 微端APP示例工程
│   │   ├── TcrMicroAppForUnity2018Empty.zip
│   │   └── 微端体验.apk
│   └── 游戏APP示例工程
│       └── AndroidUnity2018Empty.zip
├── Doc
│   ├── images
│   │   ├── 微端整体流程.png
│   │   └── 微端各模块关系.png
│   └── 微端接入指南.md
├── License_tcrmicro.txt
├── README.md
├── Sdk
│   └── README.md
└── 发布记录.md
```

## Demo

[该目录](Demo)下有三个示例工程，分别是云端APP示例工程、微端APP示例工程和游戏APP示例工程。

* 游戏APP示例工程[AndroidUnity2018Empty.zip](Demo/游戏APP示例工程/AndroidUnity2018Empty.zip)是一个Demo游戏工程。

* 云端APP示例工程[TcrMicroCloud.zip](Demo/云端APP示例工程/TcrMicroCloud.zip)演示了如何将上面的游戏APP示例工程改造为云端APP工程，如何通过数据通道和微端APP进行通信。另外还提供了数据通道的测试APP[DataChannelTest.zip](Demo/云端APP示例工程/DataChannelTest.zip)，方便在本地对云端APP工程的数据通道功能进行测试。

* 微端APP示例工程[TcrMicroAppForUnity2018Empty.zip](Demo/微端APP示例工程/TcrMicroAppForUnity2018Empty.zip)演示了如何集成微端SDK，开发微端程序，使用云试玩、静默下载、热更新和登录/支付穿透的功能。另外也提供了[微端体验.apk](Demo/微端APP示例工程/微端体验.apk)，可以直接下载安装来体验微端。

## Doc

[该目录](Doc)下有[微端接入指南](Doc/微端接入指南.md)，介绍了如何接入并实现微端云试玩以及热更新的功能。

关于热更新的接入可以参考[微端接入操作演示视频](https://cg-sdk-1258344699.cos.ap-nanjing.myqcloud.com/micro/docs/%E5%BE%AE%E7%AB%AF%E6%8E%A5%E5%85%A5%E6%93%8D%E4%BD%9C%E6%BC%94%E7%A4%BA%E8%A7%86%E9%A2%91.mp4)。

## Sdk

[该目录](Sdk)下主要存放了微端的相关SDK及说明。

微端SDK实现了云试玩、静默下载和提供登录/支付穿透的能力。微端APP示例工程中已经集成了微端SDK，您可以直接在该工程的基础上进行二次开发，实现微端的自定义功能。

云端SDK提供了登录/支付穿透的能力。游戏工程集成云端SDK，实现登录/支付请求穿透到微端。参见[云端SDK的接口文档](https://tencentyun.github.io/cloudgame-android-sdk/microsdk/com/tencent/tcr/micro/cloudsdk/DataChannel.html)。
