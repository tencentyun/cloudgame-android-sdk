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

## 业务后台介绍

为了便于演示，云游戏团队搭建了一个业务后台，客户端通过[CloudGameAPi](app/src/main/java/com/example/demop/server/CloudGameApi.java)访问客户端后台。

CloudGameAPi中封装了启动游戏和停止游戏的请求, 和业务后台的参数相对应。

启动游戏时向客户端后台传递gameId、clientSession、UserId请求参数，启动成功后会返回ServerSession。

停止游戏时向客户端后台传递UserId请求参数。

其中gameId为游戏ID，clientSession为SDK初始化成功生成的参数，UserId为标识请求的用户身份。

注：客户在接入时可以根据需求实现自己的客户端后台，

如何搭建业务后台请参考：[业务后台创建实例](https://docs.qq.com/doc/DRUZsV3VHbm1ERE5U)

客户端后台的实现请参考[后台API文档](https://cloud.tencent.com/document/product/1162/40729)。

### 游戏ID

游戏ID为部署在您腾讯云账号上的游戏，如果您使用的自己的业务后台，你可以联系我们部署您自己的游戏。

本工程中提供了部分用于体验的游戏，你可以根据需要在Constant中进行修改。

## 如何运行

1、在Android Studio中导入该工程, 并且确保运行环境配置正常

2、修改local.properties, 确保sdk路径正确

