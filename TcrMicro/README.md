# 微端
微端是使用云游戏技术构建出来的某款游戏的专属云游戏客户端。它提供了云试玩、登录支付穿透、静默下载升级的功能，体验接近原生游戏客户端。

因为微端的体积小，所以很适合用来做游戏买量广告投放，快速获客。

# 仓库目录说明

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

## [Demo](Demo)

该目录下有三个示例工程，分别是云端APP示例工程、微端APP示例工程和游戏APP示例工程。

* 游戏APP示例工程[AndroidUnity2018Empty.zip](Demo/游戏APP示例工程/AndroidUnity2018Empty.zip)是一个Demo游戏工程。

* 云端APP示例工程[TcrMicroCloud.zip](Demo/云端APP示例工程/TcrMicroCloud.zip)演示了如何将上面的游戏APP示例工程改造为云端APP工程，如何通过数据通道和微端APP进行通信。另外还提供了数据通道的测试APP[DataChannelTest.zip](Demo/云端APP示例工程/DataChannelTest.zip)，方便在本地对云端APP工程的数据通道功能进行测试。

* 微端APP示例工程[TcrMicroAppForUnity2018Empty.zip](Demo/微端APP示例工程/TcrMicroAppForUnity2018Empty.zip)演示了如何集成微端SDK，开发微端程序，使用云试玩、静默下载、热更新和登录/支付穿透的功能。另外也提供了[微端体验.apk](Demo/微端APP示例工程/微端体验.apk)，可以直接下载安装来体验微端。

## [Doc](Doc)

该目录下有微端的两种接入方式的说明文档，分别是[快速接入版](Doc/快速接入版.md)和[功能增强版](Doc/功能增强版.md)。

前者是下载完整游戏包然后覆盖安装的方式实现微端包到完整包的转化，后者是下载补丁包然后热更新动态加载的方式实现转化。

前者接入简单，不需要生成补丁包，可以快速完成接入，后者则需要进行相应的适配，生成补丁。

## [Sdk](Sdk)

该目录下主要存放了微端的相关SDK及说明。

微端SDK实现了云试玩、静默下载和提供登录/支付穿透的能力。微端APP示例工程中已经集成了微端SDK，您可以直接在该工程的基础上进行二次开发，实现微端的自定义功能。

云端SDK提供了登录/支付穿透的能力。游戏工程集成云端SDK，实现登录/支付请求穿透到微端。参见[云端SDK的接口文档](https://tencentyun.github.io/cloudgame-android-sdk/microsdk/com/tencent/tcr/micro/cloudsdk/DataChannel.html)。

# 仓库版本记录
