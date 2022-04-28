package com.example.demop.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * 该类用于存储和GameView相关的数据
 */
public class GameViewModel extends ViewModel {
    // 虚拟键盘开关
    private final MutableLiveData<Boolean> mKeyboard = new MutableLiveData<>();
    // 自定义虚拟手柄开关
    private final MutableLiveData<Boolean> mCustomGamePad = new MutableLiveData<>();
    public GameViewModel() {
        mKeyboard.setValue(false);
        mCustomGamePad.setValue(false);
    }

    public MutableLiveData<Boolean> getKeyboard() {
        return mKeyboard;
    }

    public MutableLiveData<Boolean> getCustomGamePad() {
        return mCustomGamePad;
    }
}
