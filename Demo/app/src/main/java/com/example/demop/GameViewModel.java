package com.example.demop;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mDebugView = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mGamePad = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mKeyboard = new MutableLiveData<>();

    public GameViewModel() {
        mDebugView.setValue(true);
        mGamePad.setValue(false);
        mKeyboard.setValue(false);
    }

    public MutableLiveData<Boolean> getDebugView() {
        return mDebugView;
    }

    public MutableLiveData<Boolean> getGamePad() {
        return mGamePad;
    }

    public MutableLiveData<Boolean> getKeyboard() {
        return mKeyboard;
    }
}
