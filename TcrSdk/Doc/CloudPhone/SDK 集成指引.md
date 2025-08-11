## **运行环境**

iOS 12及以上系统版本。

## 快速接入

### 获取 SDK
<table>
<tr>
<td rowspan="1" colSpan="1" >SDK</td>

<td rowspan="1" colSpan="1" >下载地址</td>

<td rowspan="1" colSpan="1" >SDK 说明文档</td>

<td rowspan="1" colSpan="1" >版本发布日志</td>
</tr>

<tr>
<td rowspan="1" colSpan="1" >iOS SDK</td>

<td rowspan="1" colSpan="1" >[GitHub下载](https://github.com/tencentyun/cloudgame-ios-sdk/tree/master/SDK)</td>

<td rowspan="1" colSpan="1" >[接口说明]()</td>

<td rowspan="1" colSpan="1" >[Release notes](https://github.com/tencentyun/cloudgame-ios-sdk/blob/master/Doc/%E5%8E%86%E5%8F%B2%E7%89%88%E6%9C%AC.md)</td>
</tr>
</table>


### SDK 集成

在 Podfile 中添加：
``` bash
# 集成指定版本的SDK，x.x.x需替换为真实版本号
pod 'TCRSDK', :git => "https://github.com/tencentyun/cloudgame-ios-sdk.git", :tag => 'x.x.x'

# 虚拟按键可选库
# pod 'TCRVKey', :git => "https://github.com/tencentyun/cloudgame-ios-sdk.git", :tag => 'x.x.x'
```

### SDK 使用
``` objectivec
// 以下是伪代码，
// 完整代码参考 https://github.com/tencentyun/cloudgame-ios-sdk/blob/master/Demo/TCAIDemo/TCAIDemo/CAIDemoLoginVC.m#L222
#import <TCRSDK/TCRSDK.h>

- (void) quickStart {
    // 调用 云API 创建安卓实例访问Token
    // https://cloud.tencent.com/document/api/1162/119708
    NSDictionary *androidInstancesAccessToken = [self CreateAndroidInstancesAccessToken];
    NSString *token = androidInstancesAccessToken[@"Token"];
    NSString *accessInfo = androidInstancesAccessToken[@"AccessInfo"];
    
    // 使用Token和AceessInfo构建TcrConfig
    TcrConfig* tcrConfig = [[TcrConfig alloc] initWithToken:token accessInfo:accessInfo];
    
    // 给TCRSDK设置Token和AccessInfo
    [[TcrSdkInstance sharedInstance] setTcrConfig:tcrConfig error:nil];
    
    // 创建TcrSession
    NSMutableDictionary *tcrSessionConfig = [NSMutableDictionary dictionary];
    TcrSession *session = [[TcrSdkInstance sharedInstance] createSessionWithParams:tcrSessionConfig];
    [session setTcrSessionObserver:self];
    
    // 发起会话连接控制云端实例
    [session accessWithInstanceId:instanceIds[0]];
    
    // ........进行云手机操作..........//
    
    // 销毁TcrSession
    [[TcrSdkInstance sharedInstance] destroySession:session];
}

```

## 完整 Demo

完整接入代码可 [参考 Demo](https://github.com/tencentyun/cloudgame-ios-sdk/tree/master/Demo/TCAIDemo)。