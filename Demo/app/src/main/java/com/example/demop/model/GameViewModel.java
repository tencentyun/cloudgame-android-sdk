package com.example.demop.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mDebugView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCustomGamePad = new MutableLiveData<>();
    public GameViewModel() {
        mDebugView.setValue(true);
        mCustomGamePad.setValue(false);
    }

    public MutableLiveData<Boolean> getDebugView() {
        return mDebugView;
    }

    public MutableLiveData<Boolean> getCustomGamePad() {
        return mCustomGamePad;
    }
}
