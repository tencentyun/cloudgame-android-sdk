# 引言

   云渲染 TCR SDK （以下简称“ SDK  产品”）由深圳市腾讯计算机系统有限公司（以下简称“我们”）开发，公司注册地为深圳市南山区粤海街道麻岭社区科技中一路腾讯大厦 35 层。其中，我们基于原生端云渲染 TCR SDK （Android/iOS）设计了云渲染虚拟按键 TCR Vkey SDK。云渲染虚拟按键 TCR Vkey SDK隐私保护规则与云渲染 TCR SDK 隐私保护规则一致，具体可以参见本规则中适用于云渲染 TCR SDK 的内容。  
  《云渲染 TCR  SDK 个人信息保护规则》（以下简称“本规则”）主要向开发者及其终端用户（“终端用户”）说明，为了实现 SDK 产品的相关功能，SDK  产品将如何处理终端用户的个人信息，“处理”包括收集、存储、使用、加工、传输、提供、公开个人信息等行为。
  请开发者及终端用户务必认真阅读本规则。如您是开发者，请您确认充分了解并同意本规则后再集成 SDK 产品，如果您不同意本规则的任何内容，应立即停止接入及使用 SDK 产品。同时，您应仅在获得终端用户的同意后集成 SDK 产品并处理终端用户的个人信息。

# 特别说明

  如您是开发者，您应当：
1. 遵守法律、法规收集、使用和处理终端用户的个人信息，包括但不限于制定和公布有关个人信息保护的隐私政策等。
2. 在集成 SDK 产品前，告知终端用户 SDK 产品处理终端用户个人信息的情况，并依法获得终端用户同意。
3. 在获得终端用户的同意前，除非法律法规另有规定，不应收集终端用户的个人信息。
4. 向终端用户提供易于操作且满足法律法规要求的用户权利实现机制，并告知终端用户如何查阅、复制、修改、删除个人信息，撤回同意，以及限制个人信息处理、转移个人信息、获取个人信息副本和注销账号。
5. 遵守本规则的所有要求。
  如开发者和终端用户对本规则内容有任何疑问、意见或建议, 可随时通过本规则第七条提供的方式与我们联系。

# 一、我们收集的信息及我们如何使用信息

## (一) 为实现 SDK 产品功能所需收集的个人信息
  SDK 目前不涉及提供其他扩展业务功能，为实现 SDK 产品的相应功能所必需，我们将向终端用户或开发者收集终端用户在使用与 SDK 产品相关的功能时产生的如下个人信息：  
其中相机和录音为可选
| 个人信息名称 | 处理目的 | 使用场景 | 处理方式 | 操作系统 |
| --- | --- | --- | --- | --- |
| 应用程序包名称 | Android/iOS 的兼容问题、崩溃问题进行适配和分析 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| 网络类型 | 针对网络类型进行网络优化 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| 系统属性 | 针对 Android 兼容性问题进行适配 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| 设备型号 | Android/iOS 的兼容问题、崩溃问题进行适配和分析 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| 操作系统 | Android/iOS的兼容问题、崩溃问题进行适配和分析 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| IP 地址 | 就近调度云渲染实例 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| **相机** | 视频上行时采集视频画面 | 在视频上行过程中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| **录音** | 音频上行时采集声音 | 在语音上行过程中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| CPU 信息 | Android/iOS兼容问题、崩溃问题进行适配和分析 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android、iOS |
| 运营商信息 | 链接中的网络问题分析 | 在音视频传输场景中 | 去标识化、加密传输的安全处理方式 | Android |

## (二) 为实现 SDK 产品功能所需的权限
  SDK 目前不涉及提供其他扩展业务功能，为实现 SDK 产品的相应功能所必需，我们可能通过开发者的应用申请开启终端用户设备操作系统的特定权限，如下的权限均为必选，无可选权限：
  
| 操作系统 | 权限名称 | 使用目的 | 必选/可选 |
| --- | --- | --- | --- |
| Android | android.permission.ACCESS_NETWORK_STATE | 音视频传输网络需要 | 必选 |
|  | android.permission.INTERNET | 音视频传输网络需要 | 必选 |
|  | android.permission.CAMERA | 使用视频上行功能，需要开启摄像头 | 可选 |
|  | android.permission.RECORD_AUDIO | 使用音频上行功能，需要开启麦克风 | 可选 |
|  | android.permission.WRITE_EXTERNAL_STORAGE | 存储 SDK 配置文件和日志文件 | 必选 |
| iOS | 网络权限 | 音视频传输网络需要 | 必选 |
|  | NSCameraUsageDescription | 使用视频上行功能，需要开启摄像头 | 可选 |
|  | NSMicrophoneUsageDescription | 使用音频上行功能，需要开启麦克风 | 可选 |  


注: 请注意，在不同设备和系统中，权限显示方式及关闭方式可能有所不同，请终端用户参考其使用的设备及操作系统开发方的说明或指引。当终端用户关闭权限即代表其取消了相应的授权，我们和开发者将无法继续收集和使用对应的个人信息，也无法为终端用户提供上述与该等授权所对应的功能。

## (三) 根据法律法规的规定，以下是征得用户同意的例外情形
1. 为订立、履行与终端用户的合同所必需。
2. 为履行我们的法定义务所必需。
3. 为应对突发公共卫生事件，或者紧急情况下为保护终端用户的生命健康和财产安全所必需。
4. 为公共利益实施新闻报道、舆论监督等行为，在合理的范围内处理终端用户的个人信息。
5. 依照本法规定在合理的范围内处理终端用户自行公开或者其他已经合法公开的个人信息。
6. 法律行政法规规定的其他情形。
特别提示: 如我们收集的信息无法单独或结合其他信息识别到终端用户的个人身份，其不属于法律意义上的个人信息。

# 二、第三方数据处理及信息的公开披露

我们不会与我们的关联公司、合作伙伴及第三方共享（“接收方”）终端用户的个人信息。  
我们与第三方合作过程中，将遵守法律规定，按照最小必要原则，安全审慎地处理相关数据。  
我们将按照法律法规的规定，对数据处理涉及的第三方进行严格的限制，要求其严格遵守我们关于个人信息保护的措施与要求。    
我们不会将终端用户的个人信息转移给任何公司、组织和个人，但以下情况除外：  
事先告知终端用户转移个人信息的种类、目的、方式和范围，并获得终端用户的单独同意。  
如涉及合并、分立、解散、被宣告破产等原因需要转移个人信息的，我们会向终端用户告知接收方的名称或者姓名和联系方式，并要求接收方继续履行个人信息处理者的义务。接收方变更原先的处理目的、处理方式的，我们会要求接收方重新取得终端用户的同意。  
我们不会公开披露终端用户的个人信息，但以下情况除外：
告知终端用户公开披露的个人信息的种类、目的、方式和范围并获得终端用户的单独同意后。
在法律法规、法律程序、诉讼或政府主管部门强制要求的情况下。  

# 三、终端用户如何管理自己的信息
我们非常重视终端用户对其个人信息管理的权利，并尽全力帮助终端用户管理其个人信息，包括个人信息查阅、复制、修改、删除、撤回同意、限制个人信息处理、获取个人信息副本、注销账号以及设置隐私功能等，以使终端用户有能力保障自身的隐私和信息安全。  
如您是开发者，您应当为终端用户提供并明确其查阅、复制、修改、删除个人信息、撤回同意、转移个人信息、限制个人信息处理、获取个人信息副本和注销账号的方式。  
如您是终端用户，由于您不是我们的直接用户，与我们无直接的交互对话界面，为保障您的权利实现，我们已要求开发者提供便于操作的用户权利实现方式。您也可通过本规则 第七条 中的方式与我们取得联系。请您理解，特定的业务功能和服务将需要您的信息才能得以完成，当您撤回同意或授权后，我们无法继续为您提供对应的功能和服务，也不再处理您相应的个人信息。但您撤回同意或授权的决定，不会影响我们此前基于您的授权而开展的个人信息处理。

# 四、信息的存储

(一) 存储信息的地点  
我们遵守法律法规的规定，将在中华人民共和国境内收集和产生的个人信息存储在境内。  
(二) 存储信息的期限  
一般而言，我们仅在为实现目的所必需的最短时间内保留终端用户的个人信息，但下列情况除外：  
为遵守适用的法律法规等有关规定。  
为遵守法院判决、裁定或其他法律程序的规定。  
为遵守相关政府机关执法的要求。  

# 五、信息安全

我们为终端用户的个人信息提供相应的安全保障，以防止信息的丢失、不当使用、未经授权访问或披露。
我们严格遵守法律法规保护终端用户的个人信息。  
我们将在合理的安全水平内使用各种安全保护措施以保障信息的安全。 例如，我们使用加密技术、匿名化处理等手段来保护终端用户的个人信息。  
我们建立专门的管理制度、流程和组织确保信息安全。 例如，我们严格限制访问信息的人员范围，要求他们遵守保密义务，并进行审查。  
若发生个人信息泄露等安全事件，我们会启动应急预案，阻止安全事件扩大，并以推送通知、公告等形式告知开发者。

# 六、变更

我们可能适时修订本规则的内容。  
如该等变更会导致终端用户在本规则项下权利的实质减损，我们将在变更生效前，通过网站公告等方式进行提示。如您是开发者，当更新后的本规则对处理终端用户的个人信息情况有重大变化的，您应当适时更新隐私政策，并以弹框形式通知终端用户并且获得其同意，如果终端用户不同意接受本规则，请停止集成 SDK 产品。

# 七、联系我们

我们设立了专门的个人信息保护团队和个人信息保护负责人，如果开发者和/或终端用户对本规则或个人信息保护相关事宜有任何疑问或投诉、建议时，可以通过以下方式与我们联系：  
通过 [客服](https://kf.qq.com/) 与我们联系。  
将问题发送至 Dataprivacy@tencent.com。  
邮寄信件至：中国广东省深圳市南山区海天二路33号腾讯滨海大厦 数据隐私保护部（收）邮编：518054。
我们将尽快审核所涉问题，并在15个工作日或法律法规规定的期限内予以反馈。

