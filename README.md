# 腾讯云云游戏 Android SDK
- [腾讯云云游戏](https://cloud.tencent.com/solution/gs)：稳定低延时的音视频能力，配合腾讯云丰富的边缘计算节点和灵活的 GPU 虚拟化技术，为云游戏开发者提供一站式端游+手游 PaaS 解决方案。

## 一、各文件夹说明

* **Demo**: 可以直接导入Android Studio运行的Demo, 包含端游、手游的调用示例
* **Doc**：相关文档
  - APIDocs.md: 云游戏SDK的API文档
  - 历史版本.md: SDK发布历史, 包含各版本的变更信息
  - 手游接入文档.md: 描述如何接入手游SDK
  - 端游接入文档.md: 描述如何接入端游SDK
  - 自定义虚拟按键.md: 自定义虚拟按键接入文档
* **SDK**：包含客户端需要下载的SDK
* **TOOLS**：SDK的配套工具


## 二、SDK 下载
您可以在以下链接中获取最新SDK：

| 所属平台 | Zip下载 | Demo运行说明 | 端游集成指引| 手游集成指引 | API 列表 |
|:---------:| :--------:|:--------:| :--------:| :--------:|:--------:|
| Android | [下载](https://recorder-10018504.cos.ap-shanghai.myqcloud.com/tcgsdk-android/tcgsdk_latest.zip)| [DOC](Demo/README.md)| [端游集成指引](Doc/端游接入文档.md) | [手游集成指引](Doc/手游接入文档.md) | [API](Doc/APIDocs.md) |

### 三、Version 1.2.3 (2021-08-09)
Features
- 支持[自定义虚拟按键](Doc/自定义虚拟按键.md)
- 支持云手游
- 低延迟优化
- 重连优化

Bug Fixes
- 修复网络超时情况下的偶现崩溃问题



