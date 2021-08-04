package com.example.demop.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.tcggamepad.GamepadManager;
import com.tencent.tcggamepad.IGamepadTouchDelegate;
import com.tencent.tcgsdk.TLog;
import com.tencent.tcgsdk.api.ITcgSdk;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ControlView extends RelativeLayout {
    private static final String TAG = "ControlView";

    // 云游戏SDK调用接口
    protected ITcgSdk mSDK;
    // 自定义虚拟按键
    private GamepadManager mCustomGamePad;
    // 自定义虚拟按键布局配置
    private String mCustomGamePadCfg;

    public ControlView(Context context) {
        this(context, null);
    }

    public ControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setGamePadTouchDelegate(IGamepadTouchDelegate delegate) {
        mCustomGamePad.setGamePadTouchDelegate(delegate);
    }

    public void editCustomGamePad(boolean enable) {
        if (enable) {
            mCustomGamePad.setVisibility(View.VISIBLE);
            mCustomGamePad.editGamepad(mCustomGamePadCfg);
        } else {
            mCustomGamePad.setVisibility(View.GONE);
        }
    }

    public void showCustomGamePad(boolean enable, String conf) {
        if (enable) {
            if (!TextUtils.isEmpty(conf)) {
                mCustomGamePadCfg = conf;
            }
            mCustomGamePad.setVisibility(View.VISIBLE);
            mCustomGamePad.showGamepad(mCustomGamePadCfg);
        } else {
            mCustomGamePad.setVisibility(View.GONE);
        }
    }


    private void initCustomGamePad() {
        mCustomGamePad = new GamepadManager(this.getContext());
        mCustomGamePad.setInstructionListener(instruction -> {
            if (mSDK != null) {
                mSDK.sendKmEvents(instruction);
            }
        });
        mCustomGamePad.setEditListener((isChanged, jsonCfg) -> {
            if (isChanged) {
                mCustomGamePadCfg = jsonCfg;
            }
            mCustomGamePad.showGamepad(mCustomGamePadCfg);
        });
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mCustomGamePad, lp);
        mCustomGamePadCfg = readConfigFile("lol_5v5_default.cfg");
        mCustomGamePad.setVisibility(GONE);
    }

    private String readConfigFile(String fileName) {
        try {
            AssetManager am = this.getContext().getAssets();
            InputStream is = am.open(fileName);
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            char input[] = new char[is.available()];
            isr.read(input);
            isr.close();
            is.close();
            return new String(input);
        } catch (Exception e) {
            TLog.e(TAG, "readConfigFile failed:" + e);
        }
        return null;
    }

    private void init(Context context) {
        initCustomGamePad();
    }

    public void setSDK(ITcgSdk sdk) {
        mSDK = sdk;
    }
}
