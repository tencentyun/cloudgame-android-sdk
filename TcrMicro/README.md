# 微端

## 目录介绍

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
│   ├── 微端快速接入文档.md
│   └── 微端功能增强版接入文档.md
├── License_tcrmicro.txt
├── README.md
└── Sdk
    ├── README.md
    └── changelog.md
```

该目录主要分为三个部分。

### [Demo](Demo)

该目录下有三个示例工程，分别是云端APP示例工程、微端APP示例工程和游戏APP示例工程。

* 游戏APP示例工程[AndroidUnity2018Empty.zip](Demo/游戏APP示例工程/AndroidUnity2018Empty.zip)是一个Demo游戏工程。

* 云端APP示例工程[TcrMicroCloud.zip](Demo/云端APP示例工程/TcrMicroCloud.zip)演示了如何将游戏APP工程改造为云端APP工程，如何通过数据通道和微端APP进行通信。另外还提供了数据通道的测试APP[DataChannelTest.zip](Demo/云端APP示例工程/DataChannelTest.zip)，方便在本地对云端APP工程的数据通道功能进行测试。

* 微端APP示例工程[TcrMicroAppForUnity2018Empty.zip](Demo/微端APP示例工程/TcrMicroAppForUnity2018Empty.zip)演示了如何集成微端SDK，开发微端程序，使用云试玩、静默下载、热更新和登录/支付穿透的功能。另外也提供了[微端体验.apk](Demo/微端APP示例工程/微端体验.apk)，可以直接下载安装来体验微端。

### [Doc](Doc)

该目录下有两份使用说明文档，分别是[快速接入版使用说明](Doc/微端快速接入文档.md)和[功能增强版使用说明](Doc/微端功能增强版接入文档.md)。

前者是下载完整游戏包然后覆盖安装的方式实现微端包到完整包的转化，后者是下载补丁包然后热更新动态加载的方式实现转化。

前者接入简单，不需要生成补丁包，可以快速完成接入，后者则需要进行相应的适配，生成补丁。

### [Sdk](Sdk)

该目录下主要存放了微端相sdk说明，包含微端sdk、云端sdk和对应的changelog。

微端sdk提供静默下载、云试玩和登录/支付穿透的能力。

云端sdk提供登录/支付穿透的能力【可选】。

微端APP示例工程中已经集成了微端sdk，您直接可以在该工程的基础上进行二次开发即可实现微端的功能。

云端sdk需要集成到游戏工程中，使用说明请参考[云端SDK的API文档](https://tencentyun.github.io/cloudgame-android-sdk/microsdk/com/tencent/tcr/micro/cloudsdk/DataChannel.html)。

## 版本变更

工程中的sdk已经发布到Maven Center，引用方式请参考[README.md](Sdk/README.md)。

版本变更记录请参考[changelog](Sdk/changelog.md)。
