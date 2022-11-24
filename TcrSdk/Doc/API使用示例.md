在这篇文档中，我们将展示如何正确的使用一些常用的API。

# TcrRenderView
## 开启超分辨率能力 TcrRenderView#enableSuperResolution
调用时机: 
通过```TcrRenderView tcrRenderView = TcrSdk.getInstance().createTcrRenderView(context, TcrSession, TcrRenderViewType.SURFACE);```拿到TcrRenderView对象后，即可调用```tcrRenderView.enableSuperResolution(true)```开启超分能力。  
在TcrRenderView对象未被释放之前，可以随时开启和关闭超分。

使用说明:   
(1) 终端开启超分能力后，画质有增强。超分能力可以在相同的传输分辨率和码率下提升用户体验。  
(2) 开启超分能力后的720P视频在画质表现上和源1080P视频相差无几，而720P视频的传输码率大小仅为1080P的一半。终端超分能力可以在保证画质的情况下，降低视频码率，从而降低带宽传输成本。  
(3) 因超分的图像处理是在移动端完成，开启超分后会使用手机/平板的GPU资源、占用一部分GL内存以及耗电量会有少量的增加。  
(4) 超分接口最低支持Android 5.0 (API 级别 21)


