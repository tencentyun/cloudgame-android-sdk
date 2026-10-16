# 构建变体与工程说明

## 构建变体（app/src 下四个源集）

| 源集        | 打出的 Demo     | 场景                                                                                                                                     |
| --------- | ------------ | -------------------------------------------------------------------------------------------------------------------------------------- |
| `tcrdemo` | 测试 Demo（最常用） | 基本涵盖所有 SDK 接口调用测试：`ExperiencePage` 为入口（输入体验码），`GamePlayFragment` 为连接主入口，`TestApiHandler` 收归接口调用；`res/layout` 下 `test_api_*` 为 API 调用界面 |
| `simple`  | 简单 Demo      | 仅包含必要的对象创建、连接创建与交互建立                                                                                                                   |
| `scenes`  | 场景 Demo      | 自定义渲染（`CustomRenderActivity`）、自定义解码（`MediaCodecActivity`）                                                                              |
| `main`    | 公共代码         | 各变体共享                                                                                                                                  |

通过 Android Studio 的 Build Variants 或 `./gradlew assemble<Variant>` 切换。

## 构建

```bash
./gradlew assembleDebug    # 默认变体
```

工程无自动化测试，构建成功即可安装体验。

## 连接参数填写位置

- 体验码启动：`app/src/main/simple(scenes)/CloudRenderBiz.java` 中 `USE_TCR_TEST_ENV=true` + `EXPERIENCE_CODE`。
- 自建后台启动：同文件中 `USE_TCR_TEST_ENV=false` + `PROJECT_ID`、`SERVER_URL`。
- 上级导航：[Demo/README.md](../README.md)、[TcrSdk/README.md](../../README.md)
