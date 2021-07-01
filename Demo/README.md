# TcgSdk Demo
该工程演示了如何使用TcgSdk

### 接入
接入TcgSdk需要有自己的业务后台, TcgSdk无法单独运行．为了便于演示TcgSdk如何使用, 云游戏团队搭建了一个体验码服务后台.
正式接入时,接入方需要在业务后台完成云游戏的后台交互逻辑．
[调用流程](https://cloud.tencent.com/document/product/1162/52326)
[云API](https://cloud.tencent.com/document/product/1162/40729)


### 体验码
体验码是体验码服务后台生成的一串字符, 使用体验码可以直接运行该工程, 以便了解TcgSdk如何使用.
体验码需要从云游团队处获取, 最高有效期是７天, 您看到这段文字的时候工程中的体验码可能过期了, 需要重新获取.
当您获得一个有效的体验码之后, 您可以直接修改Constant里面的体验码, 端游的体验码是PC_EXPIRATION_CODE,
手游的体验码是MOBILE_EXPIRATION_CODE, 之后便可直接启动游戏.

### 如何运行
在Android Studio中导入该工程, 并且确保运行环境配置正常．
您可能需要修改local.properties和gradle.properties, 确保使用自己的sdk路径正确, 以及代理服务配置正确(如果您不需要设置代理,　请直接删除代理配置)
修改体验码后运行.

### TcgSdk API
该工程仅演示了简单的接口调用, 更多的API信息请查阅API docs.
