# 一、接入流程

## 云渲染技术说明

云渲染服务指您的应用客户端（包括UE、Unity 等VR或非VR应用）运行在云端机器上，用户通过视频流的方式访问云上应用。

在阅读快速入门前希望您已经了解云渲染的 [基本技术概念](https://cloud.tencent.com/document/product/1547/75988)

## 注册腾讯云账号并申请应用云渲染
1. 若您未注册腾讯云账号，请前往 [注册腾讯云账号](https://cloud.tencent.com/register?s_url=https%3A%2F%2Fcloud.tencent.com%2F)。

2. [申请应用云渲染服务](https://console.cloud.tencent.com/car) 后即可进入控制台。
   

   > **说明：**
   > 

   > 建议您将腾讯云账号进行 [企业实名认证](https://cloud.tencent.com/document/product/378/10496)，以便我们更好的为您提供服务。
   > 

## 应用云渲染接入

接入前，希望您先理解**应用、项目和并发**三个基础概念的关系。
- **应用：** 即您通过控制台上传的应用。每个应用有唯一的应用ID（ApplicationId），可以在控制台找到。

- **项目：** 可以被理解为分组，您可以通过项目将并发包分组，并指定可以使用这些并发包的应用。每个项目有唯一的项目ID（ProjectId），可以在控制台找到。

- **并发：** 即应用云渲染唯一计费项，一个并发即一台虚拟化的云端实例（包括CPU、带宽、磁盘、GPU 等虚拟计算资源），一个并发同时仅支持一个用户访问使用。详细请查看[计费说明](https://write.woa.com/document/93900056324304896)。
   

   在控制台中，建议您按照 **上传应用、新建项目、购买并发** 的顺序操作，得到一个关联某款应用，并且分配有可用并发的项目。


### **步骤1：新建应用**
1. **上传应用：**

  1. 在上传至云渲染平台前，**您需要先接入SteamVR插件, 并确保在SteamVR平台上能够成功运行**便于后续使用VR串流功能。

  2. 将应用压缩打包为 ZIP / RAR 文件，在 **应用管理** 处上传压缩包，等待应用创建完成。


      ![](https://qcloudimg.tencent-cloud.cn/image/document/d43cbbc8bda9b4036a69c7153fc9c481.png)

2. **配置应用启动参数，** 具体请参见 [应用启动参数配置](https://cloud.tencent.com/document/product/1547/72369)。

  - 为避免填写错误导致应用无法正常启动，建议您等待应用创建完成后直接单击 **选择** 获取启动路径。


### **步骤2：新建项目并在项目下购买并发**

> **默认测试项目** **（Test Project）** **说明：**
> 

> 应用云渲染新用户在正式购买并发之前，可以通过默认测试项目（Test Project）**免费测试您上传的应用**。免费资源有限，因此 **每次测试限时2分钟。** 更多说明请查看[默认测试项目与新手免费并发包](https://write.woa.com/document/107789508743409664)。
> 


如您期望购买并发并正式上线使用，请新建一个项目。
1. **新建项目** ，关联上传的应用并指定 **并发规格** 。

  项目支持两种类型：单应用独享类型、多应用共享类型。
  单应用独享类型可以确保项目下的并发包仅被单个应用使用，而多应用共享类型下的并发包则可以作为“资源池”被多个应用共享使用。（单击查看[ 如何通过多应用共享项目实现并发资源共享](https://write.woa.com/document/100439631848730624#36a60fdc-fac4-46c6-9889-3357aef6e040)）。

  您可以新建多个项目，指定不同规格的并发，从而测试不同规格并发的运行效果。建议您从 L / XL型开始测试，避免因并发性能不满足应用运行需求导致卡顿等问题。

   > **应用类型** : 优先选择 **云XR**, 若该选项未开放权限可选择 **云3D**
   > 

2. **在项目下购买并发包**，等待并发包创建完成后，可以进入下一步。


   ![](https://write-document-release-1258344699.cos.ap-guangzhou.tencentcos.cn/100027236949/cd796c98b8de11ed9e14525400088f3a.png?q-sign-algorithm=sha1&q-ak=AKIDInciVM47EO_xl2hcGhpzS46FdfvXFA1AP1tNTQ3BOwTi5H3icJ-7BZLVA00T93Y0&q-sign-time=1689585450;1689589050&q-key-time=1689585450;1689589050&q-header-list=&q-url-param-list=&q-signature=4ac9c930d45b0e22f14ecd3ae7d284e1f8baf1e4&x-cos-security-token=hUzQzBdeXocjAGvl92CNElsZy2hjo8Ua8a36f97eb94047752d7304a5838394f2-XlVb_4jYfpu-eK65aoUamt23lXNqhxo7wYIbDX7TBn0ReMSX3AS7ZU8Xz-euatkNsRoyZrxYNCsw2i-2G0GoKBNMePmAA6cJfCNS8Ru8FVVPZs7i15fdtuI6hpVQM1cLpilj3ncU0R3ETwqhXbUhXZiJsCS8lkC4QvzZxCr23REAv-v4l71UXzIFMVl58cG7KZ_H-1dlezwIpniZ4ts2YJDWFzjvOLWQEDk8hneaTB4FK4EQ22BMAOT4a-URBhVn6LdlGmOsvVe6Q6a7Qhc9Ml1R_qCbAGmUg450D4lgOhvWVI218m4-1sNt4LpUUMlrDlbGlX7ZUm6Yum7A0c0SLu88pHmVc6c8SfP6rejO-0DyaB88gHLeRNtr9ggNDah)


### **步骤3：联系云渲染工作人员调整配置**

项目建立完成，并关联所上传应用、购买并发后, 提供 **项目ID** 给云渲染工作人员进行配置。


### **步骤4：效果测试**

单击 **效果测试** 生成体验链接和体验码。体验链接供非VR应用使用，体验码可用于VR应用使用。

  1. **下载VR体验客户端：**

	[Pico VR体验客户端](https://tcrsdk.tencent-cloud.com/tcrvr/%E8%85%BE%E8%AE%AF%E4%BA%91XR-pico.apk)

	[Oculus VR体验客户端](https://tcrsdk.tencent-cloud.com/tcrvr/%E8%85%BE%E8%AE%AF%E4%BA%91XR-oculus.apk)

  2. **安装VR体验客户端：**

	安装VR体验客户端后, 输入体验码, 点击"启动程序", 就能体验云端部署的程序。


## 二、云渲染VR SDK接入

参考 [SDK接入流程](SDK/README.md)。