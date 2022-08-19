# 腾讯云云游戏 Android

## 工程介绍

```shell
.
├── Demo.zip
├── Doc
│   ├── APIDocs.md
│   ├── 历史版本.md
│   ├── 手游接入文档.md
│   ├── 端游接入文档.md
│   └── 自定义虚拟按键.md
├── LICENSE
├── README.md
├── SDK
│   ├── README.md
│   ├── tcgsdk-1.6.4.77_20220818_2108_release.aar
│   └── tcgui-gamepad-1.6.4.77_20220818_2108_release.aar
└── TOOLS
    ├── README.md
    └── vktool-release.apk

```

### [Demo.zip](Demo.zip)

云游戏的示例代码和说明，可以把代码导入Android Studio编译并体验云游戏。

### [Doc](Doc)

该文件夹下主要存放[手游接入文档](Doc/手游接入文档.md)、[端游接入文档](Doc/端游接入文档.md)以及[自定义虚拟按键](Doc/自定义虚拟按键.md)的使用文档。

### [SDK](SDK)

该文件夹下主要存放云游戏相关的SDK，包含[云游戏SDK](SDK/tcgsdk-1.1.7.67_20220310_1459_release.aar)和[虚拟按键SDK](SDK/tcgui-gamepad-1.1.7.67_20220310_1459_release.aar)。

### [TOOLS](TOOLS)

该文件夹下存放[虚拟按键配置文件生成工具](TOOLS/vktool-release.apk)。

## 快速入门

1、快速体验云游戏

将工程目录下的Demo文件夹导入到Android Studio直接运行，您运行此Demo可以快速体验云游示例，如果有疑问请参考[Demo运行说明](Demo/README.md)。

2、端游快速接入

如果您需要在您的应用中集成云端游的功能，请参考[端游接入文档](Doc/端游接入文档.md)。

3、手游快速接入

如果您需要在您的应用中集成云手游的功能，请参考[手游接入文档](Doc/手游接入文档.md)。

## 使用进阶

1、自定义虚拟按键

如果您需要为您的游戏添加对应的虚拟按键，您可以参考[自定义虚拟按键接入文档](Doc/自定义虚拟按键.md)。

2、虚拟按键配置

我们还提供了[自定义虚拟按键的配置工具](TOOLS/vktool-release.apk)，您可以使用该工具生成的按键布局配置供SDK使用。

3、进阶API

如果您需要了解更多API的相关内容，可以查阅[APIDocs](Doc/APIDocs.md)。

## 版本变更

我们会定期更新SDK的功能，您可以在本工程SDK目录下获取最新的版本。

你还可以查看SDK的[发布历史](Doc/历史版本.md)，了解各版本的变更信息。