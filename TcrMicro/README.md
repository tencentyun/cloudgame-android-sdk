# 微端

## 目录介绍

``` shell
.
├── Demo
│   ├── 云端APP示例工程
│   ├── 微端APP示例工程
│   └── 热更APP示例工程
├── Doc
│   ├── 云端SDK使用文档.zip
│   ├── 微端快速接入文档.md
│   └── 微端功能增强版接入文档.md
├── License_tcrmicro.txt
├── README.md
├── Sdk
│   ├── changelog.md
│   ├── README.md
└── Tools
    ├── generate_patch.sh
    ├── hotUpdate
    ├── tinker-patch-cli-1.9.14.18.jar
    └── tinker_config.xml
```

该目录主要分为四个部分。

### [Demo](Demo)

该目录下有三个示例工程，分别是微端APP示例工程、热更APP示例工程和云端APP示例工程。

微端APP示例工程提供了静默下载、云试玩和登录/支付穿透的功能。

热更APP示例工程是在游戏工程的基础上进行了简单的修改。

云端APP示例工程中演示了如何通过数据通道和微端APP进行通信。

同时，您可以直接下载[微端体验.apk](Demo/微端APP示例工程/微端体验.apk)来体验整个热更新的流程。

### [Doc](Doc)

该目录下有两份使用说明文档，分别是[快速接入版使用说明](Doc/微端快速接入文档.md)和[功能增强版使用说明](Doc/微端功能增强版接入文档.md)。

前者是下载完整游戏包然后覆盖安装的方式实现微端包到完整包的转化，后者是下载补丁包然后热更新动态加载的方式实现转化。
前者接入简单，不需要生成补丁包，可以快速完成接入，后者则需要进行相应的适配，生成补丁。

### [Sdk](Sdk)

该目录下主要存放了微端相sdk说明，包含微端sdk、云端sdk和对应的changelog。

微端sdk提供静默下载、云试玩和登录/支付穿透的能力。

云端sdk提供登录/支付穿透的能力【可选】。

微端APP示例工程中已经集成了微端sdk，您直接可以在该工程的基础上进行二次开发即可实现微端的功能。

云端sdk需要集成到游戏工程中，使用说明请参考[云端SDK使用文档](Doc/云端SDK使用文档.zip)。

### [Tools](Tools)

该目录下提供了生成补丁包的工具，

其中

[generate_patch.sh](Tools/generate_patch.sh)是用于生成补丁的脚本。

[tinker_config.xml](Tools/tinker_config.xml)是生成补丁时的配置文件。

[tinker-patch-cli-xxxx.jar](Tools/tinker-patch-cli-1.9.14.18.jar)是生成补丁的工具。

[hotUpdate](Tools/hotUpdate)是存放生成补丁时需要的微端包和更新包。

详细使用方法在参考[微端功能增强版接入文档](Doc/微端功能增强版接入文档.md#15-生成补丁包)1.5节【生成补丁包】内容。

## 版本变更

工程中的sdk已经发布到Maven Center，引用方式请参考[README.md](Sdk/README.md)。

版本变更记录请参考[changelog](Sdk/changelog.md)。
