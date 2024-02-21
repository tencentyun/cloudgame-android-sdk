# 云渲染Android demo工程

## 目录介绍

app/src下可打包3个demo，分别是测试demo(tcrDemo)和场景demo(scenes)以及最轻量的simple demo(simple)。
通过build variants打包不同的demo apk。
其中测试demo有两类构建方式，分别为full和lite构建，其区别在于full使用完整版sdk，lite使用轻量版sdk。lite版需要在运行时下载并加载插件。
三个Demo都需要您通过腾讯云控制台生成体验码才可正常体验。
其中测试demo可以在运行后在主界面输入体验码进行体验，simple demo和场景demo需要在CloudRenderBiz.java文件中填写体验码到变量EXPERIENCE_CODE中。

### simple demo
最简单的接入demo，仅包含必要的对象创建、连接创建以及交互的建立。
可先查阅该demo完成最基本的云渲染场景创建

### 场景demo
包含几类场景的demo工程演示，目前有自定义渲染和自定义解码两个场景的构建。可查看scenes下的CustomRenderActivity以及
MediaCodecActivity了解如何使用。

### 测试demo
基本涵盖所有sdk接口测试调用的demo工程，其中ExperiencePage为入口，输入在腾讯云控制台得到的体验码即可连接并体验。
GamePlayFragment为连接的主入口，TestApiHandler为接口调用的收归类。细节请阅读代码。
res/layout下以test_api命名开始的为api调用的UI，分类为场景下的使用，可根据需要查看接口在项目中集成。