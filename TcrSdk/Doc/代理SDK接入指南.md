# TcrProxy SDK接入说明

本文档介绍如何通过TcrProxy SDK，将本地设备变成一个云端实例的网络代理，实现访问客户端网络的能力。适用于访问局域网NAS等设备场景。

## 接入声明

> **重要提示：**  
> 当代理功能开启后，云端实例的所有网络访问流量将会经由本地客户端设备转发，即“云端应用请求 → 互联网 → 真机客户端 → 互联网 → 真机客户端 → 互联网 → 云端应用响应“
> 由于链路较长，并且受限于客户端设备的上行带宽，极有可能导致云端实例访问网页时出现卡顿、延迟甚至无法联网等问题。请务必根据实际网络环境评估代理能力，避免因带宽瓶颈影响业务体验。

## 目录

- [功能简介](#功能简介)
- [集成准备](#集成准备)
- [权限与Manifest配置](#权限与manifest配置)
- [代理服务接入流程](#代理服务接入流程)
- [代码示例](#代码示例)

---

## 功能简介

- **本地代理**：将本地设备变为云端实例的网络出口，所有云端流量通过本地设备转发。
- **前台服务**：用于保证代理服务在 Android 后台存活。
>需要注意的是，前台服务并非强制要求。如果你的业务场景下，代理的开启和关闭始终与界面（如 Activity 的生命周期）保持同步，例如用户进入某个页面时启动代理，离开页面时关闭代理，那么你完全可以直接在 Activity 中调用 Proxy 的相关方法，无需额外启动Service。
>但请务必确保在 Activity 销毁（如 onDestroy()）时，及时调用 Proxy.getInstance().stopProxy() 停止代理服务，释放相关资源，避免后台代理进程无故占用系统资源或引发安全风险。

---

## 集成准备

1. **添加依赖**

   在你的 `build.gradle` 中添加：

   ```groovy
   implementation 'com.tencent.tcr:proxy:1.1.4'
   ```

2. **引入 ProxyService**

   将 `ProxyService.java` 添加到你的工程中（或参考文档实现类似逻辑）。

---

## 权限与Manifest配置

1. **权限声明**

   在 `AndroidManifest.xml` 中添加：

   ```xml
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
   ```

2. **服务声明**

   ```xml
   <service  
    android:name=".gameplay.ProxyService"  
    android:exported="false" />
   ```

---

## 代理服务接入流程

### 1. 初始化代理

- **方法**：`ProxyService.initProxy(Context context, String relayInfoJson)`
- **参数说明**：
  - `relayInfoJson`：云端下发的中继信息，由TcrSdk的消息`PROXY_RELAY_AVAILABLE`提供。

### 2. 启动代理

- **方法**：`ProxyService.startProxy(Context context)`
- **说明**：初始化成功后，调用此方法开始转发流量。

### 3. 停止代理

- **方法**：`ProxyService.stopProxy(Context context)`
- **说明**：不再需要代理时调用，释放资源。

> **调用顺序必须为：初始化 → 启动 → 停止**

---

## 代码示例

```java
// 1. 初始化代理（异步）
String relayInfoJson = ...; // 从云端获取
ProxyService.initProxy(context, relayInfoJson);

// 2. 启动代理
ProxyService.startProxy(context);

// 3. 停止代理
ProxyService.stopProxy(context);
```

---

## 详细说明

### ProxyService

- **ProxyService** 是 Demo 工程中的源码实现，主要用于演示如何在 Android 项目中集成和管理代理服务的生命周期。实际业务中可根据自身需求参考或自定义实现。

### Proxy

- **Proxy** 是 TcrProxy SDK 提供的核心代理能力接口，采用单例模式，所有操作均通过 `Proxy.getInstance()` 获取实例。其主要接口和功能如下：
#### Proxy接口说明

| 方法签名                                                            | 说明                     | 参数                                                                               | 返回值            | 备注                                |
| --------------------------------------------------------------- | ---------------------- | -------------------------------------------------------------------------------- | -------------- | --------------------------------- |
| `public static Proxy getInstance()`                             | 获取全局唯一的 Proxy 实例       | 无                                                                                | Proxy 实例       | 单例模式                              |
| `public boolean init(String relayInfoString)`                   | 初始化代理服务                | `relayInfoString`：云端下发的代理中继信息                                                    | `true`/`false` | 必须在 `startProxy()` 前调用，初始化失败需检查日志 |
| `public boolean init(String bandwidth, String relayInfoString)` | 初始化代理服务，并指定本地设备的上行带宽限制 | `bandwidth`：上行带宽限制（如 `'1MB'`、`'500KB'`，最大`4MB`）<br>`relayInfoString`：云端下发的代理中继信息 | `true`/`false` | 带宽参数可选，建议根据实际网络情况设置               |
| `public void startProxy()`                                      | 启动代理服务，开始转发云端实例的网络请求   | 无                                                                                | 无              | 必须已成功调用 `init()`                  |
| `public void stopProxy()`                                       | 停止代理服务，终止网络转发          | 无                                                                                | 无              |                                   |
