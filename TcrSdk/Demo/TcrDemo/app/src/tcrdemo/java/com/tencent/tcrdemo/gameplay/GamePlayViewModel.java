package com.tencent.tcrdemo.gameplay;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

/**
 * 操作界面viewModel
 */
public class GamePlayViewModel extends BaseObservable {
    private static final String TAG = "GamePlayModel";

    /**
     * 鼠标是否是相对移动
     */
    public final MutableLiveData<Boolean> pcRelativeMove = new MutableLiveData<>(false);
    /**
     * 是否显示调试视图
     */
    public final MutableLiveData<Boolean> enableDebugView = new MutableLiveData<>(false);
    /**
     * 是否启用本地输入法
     */
    public final MutableLiveData<Boolean> enableLocalIme = new MutableLiveData<>(false);
    /**
     * 在调试界面输入的用户ID
     */
    public final MutableLiveData<String> multiPlayerUserId = new MutableLiveData<>(null);

    /**
     * 在调试界面输入的席位
     */
    public final MutableLiveData<String> multiPlayerSeatIndex = new MutableLiveData<>(null);

    /**
     * 在调试界面输入上行最大码率
     */
    public final MutableLiveData<String> localVideoBitrate = new MutableLiveData<>(null);

    /**
     * 在调试界面输入要设置的分辨率的宽
     */
    public final MutableLiveData<String> videoWidth = new MutableLiveData<>(null);

    /**
     * 在调试界面输入要设置的分辨率的高
     */
    public final MutableLiveData<String> videoHeight = new MutableLiveData<>(null);

    /**
     * 是否显示虚拟键盘
     */
    public final MutableLiveData<Boolean> virtualKeyboard = new MutableLiveData<>(false);

    /**
     * 是否显示虚拟按键手柄
     */
    public final MutableLiveData<Boolean> virtualGamePad = new MutableLiveData<>(false);

    /**
     * 是否开启超分能力
     */
    public final MutableLiveData<Boolean> enableSR = new MutableLiveData<>(false);

    /**
     * 是否显示本地光标图片
     */
    public final MutableLiveData<Boolean> cursorViewVisibility = new MutableLiveData<>(true);

    /**
     * 在调试界面输入的需复制的文本
     */
    public final MutableLiveData<String> pasteText = new MutableLiveData<>(null);

    /**
     * 手柄连接
     */
    public final MutableLiveData<Boolean> connectGamePad = new MutableLiveData<>(false);

    /**
     * 数据通道端口号
     */
    public final MutableLiveData<String> dataChannelPort = new MutableLiveData<>(null);

    /**
     * 数据通道发送内容
     */
    public final MutableLiveData<String> dataChannelMsg = new MutableLiveData<>(null);

    /**
     * 是否允许双指拖动与缩放
     */
    public final MutableLiveData<Boolean> enablePinch = new MutableLiveData<>(true);

    /**
     * 在调试界面输入要设置的拖动边界左
     */
    public final MutableLiveData<String> pinchLeft = new MutableLiveData<>(null);
    /**
     * 在调试界面输入要设置的拖动边界上
     */
    public final MutableLiveData<String> pinchTop = new MutableLiveData<>(null);
    /**
     * 在调试界面输入要设置的拖动边界右
     */
    public final MutableLiveData<String> pinchRight = new MutableLiveData<>(null);
    /**
     * 在调试界面输入要设置的拖动边界下
     */
    public final MutableLiveData<String> pinchBottom = new MutableLiveData<>(null);

    /**
     * 是否打开双指拖动数据
     */
    public final MutableLiveData<Boolean> enablePinchDataView = new MutableLiveData<>(true);
    /**
     * 自动登录输入的账号
     */
    public final MutableLiveData<String> account = new MutableLiveData<>(null);
    /**
     * 自动登录输入的密码
     */
    public final MutableLiveData<String> password = new MutableLiveData<>(null);

    /**
     * 选择视图旋转角度
     */
    public final MutableLiveData<Integer> renderViewRotation = new MutableLiveData<>(null);
    /**
     * 选择视图缩放类型
     */
    public final MutableLiveData<Integer> renderViewScaleType = new MutableLiveData<>(null);
    /**
     * 选择鼠标显示样式
     */
    public final MutableLiveData<Integer> cursorStyle = new MutableLiveData<>(null);

    /**
     * 选择鼠标点击类型
     */
    public final MutableLiveData<Integer> mouseClickType = new MutableLiveData<>(null);



}
