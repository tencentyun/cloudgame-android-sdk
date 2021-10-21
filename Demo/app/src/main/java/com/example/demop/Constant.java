package com.example.demop;

public class Constant {

    /**
     * 默认在我们的业务后台部署的端游，可以联系接口人部署自己的游戏
     * 目前可替换的有：
     *      1、game-nf771d1e（DNF）
     *      2、game-kvcwhp2s（剑网三）
     *      3、game-zmuayrmi (英雄联盟)
     * 体验时请注意，由于测试demo下的云游戏并发比较低，频繁打开游戏可能访问出错。
     */
    public static final String PC_GAME_ID = "game-kvcwhp2s";
    // 默认在我们的业务后台部署的手游，可以联系我们的接口人部署自己的游戏
    public static final String MOBILE_GAME_ID = "game-fvokabtq";
    // 应用的AppID
    public static final long APP_ID = 123456789;
    // 日志标签
    public static String TAG = "TcgSample";
}
