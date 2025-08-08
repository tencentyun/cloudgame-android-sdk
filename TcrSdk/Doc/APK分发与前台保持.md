## 云手机场景：APK分发与前台保持

在云手机场景下，App需要将指定的APK分发到云端设备，并确保该应用始终在前台运行，给终端用户带来“即点即玩”的体验。
SDK提供了完整的接口和事件回调，帮助你实现如下流程：

1. **分发APK到云手机**
2. **实时接收分发状态，展示Loading界面**
3. **分发成功后自动进入游戏，保持应用前台运行**
4. **可选：只保留指定应用，清理云手机环境**

### 业务流程

1.**创建并初始化TcrSession对象**  
参考[接入文档](云渲染SDK接入指南.md)的标准流程，初始化SDK并创建TcrSession对象。

2.**分发APK到云手机**  
在云手机业务场景下，拿到会话连接成功（`STATE_CONNECTED`）后，调用如下接口分发APK：
```
mSession.distributeApp("com.example.gameapk");
```
- `com.example.gameapk`参数为你要分发的APK包名。
- 分发操作会自动在云手机上安装该APK。

3.**监听分发状态，展示Loading/进度界面**  
分发操作是异步的，分发状态会通过`TcrSession.Event.DISTRIBUTE_STATUS_CHANGED`事件回调通知。
你可以在收到该事件时，更新UI（如显示“正在分发，请稍候...”的遮罩或Loading动画）：
```
private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
    @Override
    public void onEvent(TcrSession.Event event, Object eventData) {
        if (event == TcrSession.Event.DISTRIBUTE_STATUS_CHANGED) {
            String state = ((JsonObject) eventData).get("state").getAsString();
            String pkgName = ((JsonObject) eventData).get("package_name").getAsString();
            // 根据state更新UI
            switch (state) {
                case "SUCCESS":
                    // 分发完成，去掉loading，进入游戏
                    break;
                case "BUSY":
                case "FAIL":
                case "UNSUPPORTED":
                    // 分发失败，提示用户并退出
                    break;
            }
        }
    }
};
```
- **SUCCESS**：分发完成，可以去掉loading，进入游戏。
- **BUSY/FAIL/UNSUPPORTED**：分发失败或不支持，建议提示用户并退出会话。

4.**分发成功后，保持应用前台并清理其他应用（可选）**  
分发成功后（`state == "SUCCESS"`），你可以进一步调用：
```
// 只保留目标应用，清理其他非系统应用
mSession.preserveApps(Arrays.asList(pkgName));

// 保持目标应用始终在前台
mSession.keepAppInForeground(pkgName);
```
这样可以保证云手机环境干净，且目标应用即使被关闭也会自动重启并保持在前台，提升用户体验。

5.**关闭前台保持（可选）**  
如果你希望恢复云手机的正常行为（不再强制任何应用前台），可调用：
```
mSession.disableForegroundApp();
```
### 推荐UI交互
- **分发期间**：建议在渲染视图上方覆盖一个loading遮罩，提示“正在分发/安装应用，请稍候...”
- **分发成功**：去掉loading，用户直接看到游戏启动后的画面
- **分发失败/不支持**：弹窗提示“分发失败，请重试或联系客服”，并关闭会话

### 参考代码片段
```
// 1. 连接成功后分发APK
mSession.distributeApp("com.example.gameapk");

// 2. 监听分发状态
private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
    @Override
    public void onEvent(TcrSession.Event event, Object eventData) {
        if (event == TcrSession.Event.DISTRIBUTE_STATUS_CHANGED) {
            String state = ((JsonObject) eventData).get("state").getAsString();
            String pkgName = ((JsonObject) eventData).get("package_name").getAsString();
            if ("SUCCESS".equals(state)) {
                // 去掉loading
                maskView.setVisibility(View.GONE);
                // 只保留目标应用并保持前台
                mSession.preserveApps(Arrays.asList(pkgName));
                mSession.keepAppInForeground(pkgName);
            } else if ("BUSY".equals(state) || "FAIL".equals(state) || "UNSUPPORTED".equals(state)) {
                // 分发失败，提示并退出
                showToast("分发失败：" + state, Toast.LENGTH_LONG);
                // 关闭会话
                mSession.release();
            } else {
                // 其他状态，更新loading文案
                maskView.setText("分发状态：" + state);
            }
        }
    }
};
```
### 注意事项
- 只有在服务端支持的云手机场景下，`distributeApp`等接口才有效，请先与云渲染团队服务端人员确认服务端支持应用分发才可使用该接口。
- 分发APK前建议先展示loading，避免用户看到云手机桌面或其他无关画面。
- 分发失败要有兜底提示，避免用户无感知卡住。