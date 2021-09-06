# 云游戏 Demo

## 工程介绍

该工程提供了四个演示入口，包含端游-简单示例、端游-虚拟按键、端游-API调用、手游-示例。

1、[端游-简单示例](app/src/main/java/com/example/demop/activity/PcSimpleActivity.java)

主要展示了端游的启动流程。

2、[端游-虚拟按键](app/src/main/java/com/example/demop/activity/PcGamePadActivity.java)

主要展示了端游虚拟按键的使用方法。

3、[端游-API调用](app/src/main/java/com/example/demop/activity/PcApiActivity.java)

主要展示了SDK中常用API的使用方法。

4、[手游-示例](app/src/main/java/com/example/demop/activity/MobileSimpleActivity.java)

主要展示了手游的启动流程。

## 流程介绍

![](https://main.qcloudimg.com/raw/d7d82673b8e1ead4a31301ef2c7de3c9.png)

游戏的启动和结束主要涉及四个角色，分别是SDK、客户端、客户端后台、腾讯云游戏后台。

客户端首先初始化云游SDK，客户ClientSession信息由SDK生成，在SDK初始化成功后返回给客户端。

客户端将ClientSession信息传递给客户端后台，客户端后台最终返回ServerSession给客户端。

客户端将ServerSession信息传递给SDK，完成游戏的启动。

## 客户端后台介绍

为了便于演示，云游戏团队搭建了一个客户端后台，客户端通过[CloudGameAPi](app/src/main/java/com/example/demop/server/CloudGameApi.java)访问客户端后台。

CloudGameAPi中封装了启动游戏和停止游戏的请求。

启动游戏时向客户端后台传递gameCode、clientSession、UserId请求参数，启动成功后会返回ServerSession。

停止游戏时向客户端后台传递gameCode、UserId请求参数。

其中gameCode为游戏体验码，clientSession为SDK初始化成功生成的参数，UserId为标识请求的用户身份。

注：客户在接入时可以根据需求实现自己的客户端后台，客户端后台的实现请参考[后台API文档](https://cloud.tencent.com/document/product/1162/40729)。

### 体验码

体验码是体验码服务后台生成的一串字符，使用体验码可以直接运行该工程。

体验码的有效期是7天，当您发现体验码已过期，请联系云游团队获取。

获取到有效体验码之后，你可以直接修改[Constant](app/src/main/java/com/example/demop/Constant.java)里面的体验码。

端游的体验码是PC_GAME_CODE，手游的体验码是MOBILE_GAME_CODE。

## 如何运行

1、在Android Studio中导入该工程, 并且确保运行环境配置正常

2、修改local.properties, 确保sdk路径正确

3、填写有效的体验码

