package com.example.demop.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mDebugView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mKeyboard = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCustomGamePad = new MutableLiveData<>();
    public GameViewModel() {
        mDebugView.setValue(true);
        mKeyboard.setValue(false);
        mCustomGamePad.setValue(false);
    }

    public MutableLiveData<Boolean> getDebugView() {
        return mDebugView;
    }

    public MutableLiveData<Boolean> getKeyboard() {
        return mKeyboard;
    }

    public MutableLiveData<Boolean> getCustomGamePad() {
        return mCustomGamePad;
    }
}
