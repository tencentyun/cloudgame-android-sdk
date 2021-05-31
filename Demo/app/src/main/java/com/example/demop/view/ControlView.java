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
import com.tencent.tcgsdk.util.StringUtil;
import com.tencent.tcgui.controller.VirtualGameController;
import com.tencent.tcgui.keyboard.IKeyboardListener;
import com.tencent.tcgui.keyboard.KeyboardView;
import com.tencent.tcgui.listener.PackEventListener;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ControlView extends RelativeLayout implements PackEventListener, IKeyboardListener {
    private static final String TAG = "ControlView";
    // 手柄视图的父容器
    private RelativeLayout mJoyStickParent;
    // 键盘视图的父容器
    private RelativeLayout mKeyboardParent;
    // 键盘视图
    private KeyboardView mKeyboardView;
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

    private void createTcgJoystick(Context context) {
        VirtualGameController ctrl = new VirtualGameController(context, mJoyStickParent);
        ctrl.createViews();
        ctrl.showViews(true);
        ctrl.setOuterPackEventListener(this);
    }

    @Override
    public void onPackEventData(String event) {
        if (mSDK == null) {
            Log.e(TAG, "To call method setSDK is needed!!!");
        } else {
            Log.d(TAG, "" + event);
            mSDK.sendRawEvent(event);
        }
    }

    private void initJoystick(Context context) {
        mJoyStickParent = new RelativeLayout(context);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mJoyStickParent.setLayoutParams(lp);
        addView(mJoyStickParent);

        createTcgJoystick(context);
        joyStick(false);
    }

    public interface IVirtualControlListener {
        void onKeyboard(boolean show);
        void onJoystick(boolean show);
    }

    public void setOnVirtualControlListener(IVirtualControlListener listener) {
        mKeyboardShowListener = listener;
    }

    private IVirtualControlListener mKeyboardShowListener;
    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener mKeyboardObserver = (v, event) -> {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            keyboard(false);
        }
        return true;
    };

    @SuppressLint("ClickableViewAccessibility")
    private void initKeyboard(Context context) {
        mKeyboardParent = new RelativeLayout(context);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mKeyboardParent.setLayoutParams(lp);
        addView(mKeyboardParent);
        mKeyboardParent.setOnTouchListener(mKeyboardObserver);

        mKeyboardView = new KeyboardView(context);
        mKeyboardView.setOnKeyboardListener(this);
        LayoutParams kbLp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        kbLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mKeyboardView.setLayoutParams(kbLp);
        mKeyboardParent.addView(mKeyboardView);
        keyboard(false);
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
        initJoystick(context);
        initKeyboard(context);
        initCustomGamePad();
    }

    public void joyStick(boolean enable) {
        mJoyStickParent.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (mSDK != null) {
            if (enable) {
                mSDK.sendGamePadConnected();
            } else {
                mSDK.sendGamePadDisconnected();
            }
        }

        if (mKeyboardShowListener != null) {
            mKeyboardShowListener.onJoystick(enable);
        }
    }

    public void keyboard(boolean enable) {
        mKeyboardParent.setVisibility(enable ? View.VISIBLE: View.GONE);

        // 展开输入法时重置云端大小写
        if (enable) {
            mKeyboardView.resetKeyboard();
            if (mSDK != null) {
                mSDK.resetRemoteCapsLock(null);
            }
        }

        if (mKeyboardShowListener != null) {
            mKeyboardShowListener.onKeyboard(enable);
        }
    }

    // 虚拟键盘按下按下事件回调
    @Override
    public void onPress(int primaryCode, boolean isShift) {
        if (mSDK == null) {
            Log.e(TAG, "To call method setSDK is needed!!!");
        } else {
            Log.d(TAG, "press " + primaryCode + (isShift ? "shifted" : ""));
            if (isShift) {
                mSDK.sendShiftKey(true);
            }
            mSDK.sendKeyboardEvent(primaryCode, true, null);
        }

        if (primaryCode == KeyboardView.KEYCODE_GAME) {
            keyboard(false);
            joyStick(true);
        }
    }

    // 虚拟键盘按下抬起事件回调
    @Override
    public void onRelease(int primaryCode, boolean isShift) {
        if (mSDK == null) {
            Log.e(TAG, "To call method setSDK is needed!!!");
        } else {
            Log.d(TAG, "release " + primaryCode + (isShift ? " shifted" : ""));
            mSDK.sendKeyboardEvent(primaryCode, false, null);
            if (isShift) {
                mSDK.sendShiftKey(false);
            }
        }
    }

    @Override
    public void onKey(int primaryCode, boolean isShift) {
    }

    public void setSDK(ITcgSdk sdk) {
        mSDK = sdk;
    }
}
