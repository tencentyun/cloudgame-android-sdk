# Demo（示例工程目录）

> 腾讯云渲染 Android 的示例工程集合：两个 Gradle 示例工程、虚拟按键编辑工具与体验 App。  
> 上级导航：[TcrSdk/README.md](../README.md)

## 内容清单

| 条目                                                    | 说明                                                                       |
| ----------------------------------------------------- | ------------------------------------------------------------------------ |
| [`TcrDemo/`](TcrDemo/README.md)                       | **综合 Demo**：云游戏 / 云应用接入演示，含 tcrdemo / scenes / simple 三个构建变体，支持体验码连接云端   |
| [`TcrCloudStreamDemo/`](TcrCloudStreamDemo/README.md) | **云手机 Demo**：云手机串流（PaaS + SFU 两种场景）、截图、触控/按键、实例批量操作，README 内含完整 SDK 集成指引 |
| `VKtoolDemo.zip`                                      | 自定义虚拟按键编辑工具 Demo（解压后是独立工程）                                               |
| `TCRExperience.apk`                                   | 云应用体验 App 安装包：腾讯云控制台生成体验码后输入即可体验                                         |

## 快速上手

- 两个 Gradle 工程均为标准 Android 工程：Android Studio 打开 → 同步 → 运行 `app` 模块；或 `./gradlew assembleDebug`。
- 运行 Demo 需要腾讯云控制台生成的**体验码**（或自建业务后台获取会话参数），详见各工程 README。
- 真机要求 arm64（工程 `abiFilters` 仅 `arm64-v8a`），模拟器无法运行。
