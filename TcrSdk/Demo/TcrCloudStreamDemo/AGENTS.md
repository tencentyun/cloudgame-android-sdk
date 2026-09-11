# AGENTS.md

适用于本仓库（TcrCloudStreamDemo，腾讯云手机串流 Android Demo）。

## 构建与验证

- 构建命令：`./gradlew assembleDebug`（Gradle 8.11.1 / AGP 8.9.3 / Java 11 / compileSdk 35 / minSdk 24）。
- 本仓库没有任何测试和 CI，提交前的验证就是 `./gradlew assembleDebug` 成功。

## local.properties 驱动的构建开关

`settings.gradle` 会读取 `local.properties`（已 gitignore）中的以下键：

- `use_local_module_TcrSdkFull` / `use_local_module_TcrSdk` / `use_local_module_TcrGui` /
  `use_local_module_webrtc` / `use_local_module_proxy`：设为 `true` 时，settings.gradle 会
  include 位于 `../../../../tcr-android-oa-master/TcrSdkProject/` 下的**仓库外**本地模块替换
  maven 依赖（`app/build.gradle` 据此切换 `project(':TcrSdkFull')` 与
  `com.tencent.tcr:tcrsdk-full`）。这些路径在本仓库中不存在，若开关为 true 且路径无效，构建直接失败。
  修改 SDK 相关依赖时不要假设依赖一定来自 maven。
- `paas_login_name` / `paas_login_password`：会被注入 `BuildConfig`（见 `app/build.gradle` 的
  `buildConfigField`）。不要把这两个值硬编码进代码或提交到仓库；只通过 local.properties 提供。

## 版本与参数的唯一来源

- SDK 版本号定义在 `gradle/config.gradle` 的 `tcrsdkVersion`，`app/build.gradle` 通过
  `$rootProject.tcrsdkVersion` 引用。改 SDK 版本只改这一处（README 中的版本号是文档示例，可能滞后）。
- `buildTime` / `buildVersion` / `buildNumber` / `debugLevel` 等构建参数均可用 `-P` 覆盖
  （如 `./gradlew assembleDebug -PdebugLevel=0`），默认值也在 `gradle/config.gradle`。

## 其他约定

- `app` 的 `abiFilters` 仅 `arm64-v8a`；模拟器为 x86 时无法运行，属预期行为。
- 