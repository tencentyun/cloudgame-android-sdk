package com.tencent.tcrdemo.gameplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.Mouse.CursorStyle;
import com.tencent.tcr.sdk.api.Mouse.KeyType;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrTestEnv;
import com.tencent.tcr.sdk.api.data.MultiUser;
import com.tencent.tcr.sdk.api.view.MobileTouchListener;
import com.tencent.tcr.sdk.api.view.PcClickListener;
import com.tencent.tcr.sdk.api.view.PcTouchListener;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.ScaleType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import com.tencent.tcrdemo.R;
import com.tencent.tcrdemo.bean.User;
import com.tencent.tcrdemo.utils.CustomAudioCapturer;
import com.tencent.tcrgamepad.GamepadManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 测试Demo界面上的测试入口统一都会调到这个类的函数中
 */
public class TestApiHandler {

    private static final String TAG = "TestApiHandler";
    private static final String sApiTAG = "ApiTest";
    private GamePlayFragment mFragment;
    private GamePlayViewModel mModel;
    // session会话
    private TcrSession mSession;
    // 从SDK创建的渲染视图
    private TcrRenderView mRenderView;
    private PcTouchListener mPcTouchListener;
    private GamepadManager mGamePadManager;
    private RelativeLayout mKeyboardParent;
    /**
     * 虚拟按键配置json文件，从文件中读取
     */
    private String mCustomGamePadCfg;
    private CustomDataChannel mDataChannel;

    /**
     * MouseConfig
     */
    private boolean mMouseRelativeMove = false;
    private float mMoveSensitivity = 1.0f;
    private boolean mMouseVisibility = true;

    private TextView mPivotTextView;
    private TextView mScaleTextView;

    private PcClickListener mPcClickListener;
    private CustomAudioCapturer mCustomAudioCapturer;

    private static final VideoRotation[] ROTATIONS = {
            VideoRotation.ROTATION_0,
            VideoRotation.ROTATION_90,
            VideoRotation.ROTATION_180,
            VideoRotation.ROTATION_270
    };

    private static final ScaleType[] SCALETYPES = {
            ScaleType.SCALE_ASPECT_FIT,
            ScaleType.SCALE_ASPECT_FILL,
            ScaleType.SCALE_ASPECT_CROP
    };

    private static final KeyType[] KEYTYPES = {
            KeyType.LEFT,
            KeyType.MIDDLE,
            KeyType.RIGHT
    };

    private final Map<Integer, Integer> mIDMap = new HashMap<Integer, Integer>() {{
        put(R.id.paste_layout_button, R.id.paste_layout);
        put(R.id.set_res_layout_button, R.id.set_res_layout);
        put(R.id.data_channel_layout_button, R.id.data_channel_layout);
        put(R.id.auto_login_layout_button, R.id.auto_login_layout);
        put(R.id.media_up_layout_button, R.id.media_up_layout);
        put(R.id.media_down_layout_button, R.id.media_down_layout);
        put(R.id.set_view_pinch_layout_button,R.id.set_view_pinch_layout);
        put(R.id.multiply_host_layout_button,R.id.multiply_host_layout);
        put(R.id.multiply_guest_layout_button,R.id.multiply_guest_layout);
        put(R.id.pc_input_layout_button,R.id.pc_input_layout);
        put(R.id.mobile_input_layout_button,R.id.mobile_input_layout);
    }};

    public void setFragment(GamePlayFragment fragment) {
        mFragment = fragment;
    }

    public void set(TcrSession session, TcrRenderView renderView) {
        mSession = session;
        mRenderView = renderView;
    }

    public void set(TcrSession session, TcrRenderView renderView,
                    PcTouchListener pcTouchListener, GamepadManager gamepadManager, RelativeLayout keyboardParent, TextView pivotTextView, TextView scaleTextView,
                    CustomAudioCapturer customAudioCapturer) {
        mSession = session;
        mPcTouchListener = pcTouchListener;
        mGamePadManager = gamepadManager;
        mKeyboardParent = keyboardParent;
        mPivotTextView = pivotTextView;
        mScaleTextView = scaleTextView;
        mPcClickListener = new PcClickListener(mSession);
        mCustomAudioCapturer = customAudioCapturer;
        set(session, renderView);
        initGamePadCfg();
    }

    private void initGamePadCfg() {
        mCustomGamePadCfg = readConfigFile(mRenderView.getContext(), "lol_5v5.cfg");
        mGamePadManager.showGamepad(mCustomGamePadCfg);
        mGamePadManager.setEditListener((isChanged, newCfg) -> {
            if (isChanged) {
                mCustomGamePadCfg = newCfg;
            }
            mGamePadManager.showGamepad(mCustomGamePadCfg);
        });
    }

    /**
     * 设置viewModel
     *
     * @param viewModel
     */
    public void setViewModel(GamePlayViewModel viewModel) {
        mModel = viewModel;

        // 设置显示调试数据视图
        viewModel.enableDebugView.observe(mFragment, aBoolean -> {
            if (mRenderView != null) {
                mRenderView.setDisplayDebugView(aBoolean);
            }
        });

        // 调整鼠标数据
        viewModel.pcRelativeMove.observe(mFragment, aBoolean -> {
            if (mPcTouchListener != null && mRenderView != null) {
                mMouseRelativeMove = aBoolean;
                mPcTouchListener.setMouseConfig(mMouseRelativeMove, mMoveSensitivity, mMouseVisibility);
            }
        });

        // 使用虚拟手柄视图
        viewModel.virtualGamePad.observe(mFragment, aBoolean -> {
            if (mGamePadManager == null) {
                return;
            }
            if (aBoolean) {
                mGamePadManager.setVisibility(View.VISIBLE);
                mGamePadManager.editGamepad(mCustomGamePadCfg);
            } else {
                mGamePadManager.setVisibility(View.GONE);
            }
        });

        // 使用虚拟键盘
        viewModel.virtualKeyboard.observe(mFragment, aBoolean -> {
            if (mKeyboardParent == null) {
                return;
            }
            mKeyboardParent.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
        });

        // 开关视图超分辨率
        viewModel.enableSR.observe(mFragment, aBoolean -> {
            if (mRenderView == null) {
                return;
            }
            mRenderView.setEnableSuperResolution(aBoolean);
        });

        // 开关鼠标是否可见
        viewModel.cursorViewVisibility.observe(mFragment, aBoolean -> {
            if (mPcTouchListener != null && mRenderView != null) {
                mMouseVisibility = aBoolean;
                mPcTouchListener.setMouseConfig(mMouseRelativeMove, mMoveSensitivity, mMouseVisibility);
            }
        });

        // 链接手柄
        viewModel.connectGamePad.observe(mFragment, aBoolean -> {
            if (mSession == null) {
                return;
            }
            if (aBoolean) {
                mSession.getGamepad().connectGamepad();
            } else {
                mSession.getGamepad().disconnectGamepad();
            }
        });

        // 开关缩放拖动回调数据
        viewModel.enablePinchDataView.observe(mFragment, aBoolean -> {
            if (mScaleTextView != null) {
                mScaleTextView.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
            if (mPivotTextView != null) {
                mPivotTextView.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }

        });
        viewModel.enableLocalIme.observe(mFragment, aBoolean -> {
            if (mSession == null) {
                return;
            }
            mSession.setDisableCloudInput(aBoolean);
        });

        viewModel.renderViewRotation.observe(mFragment, integer -> {
            if (mRenderView == null || integer == null) {
                return;
            }
            mRenderView.setVideoRotation(ROTATIONS[integer]);
        });

        viewModel.cursorStyle.observe(mFragment,integer -> {
            if (mSession == null || integer == null) {
                return;
            }
            if (integer == 0) {
                mSession.getMouse().setMouseCursorStyle(CursorStyle.NORMAL);
            } else {
                mSession.getMouse().setMouseCursorStyle(CursorStyle.HUGE);
            }
        });

        viewModel.renderViewScaleType.observe(mFragment,integer -> {
            if (mRenderView == null || integer == null) {
                return;
            }
            mRenderView.setVideoScaleType(SCALETYPES[integer]);
        });

        viewModel.mouseClickType.observe(mFragment,integer -> {
            if (mRenderView == null || mPcTouchListener == null) {
                return;
            }

            mPcClickListener.setMouseKey(KEYTYPES[integer]);
        });

    }


    /**
     * View layout面板的展开与收起
     */
    public void onClickExpandLayoutShow(View view) {
        int buttonId = view.getId();
        LinearLayout expandLayout = mFragment.getView().findViewById(mIDMap.get(buttonId));
        if (expandLayout.getVisibility() == View.VISIBLE) {
            expandLayout.setVisibility(View.GONE);
        } else {
            expandLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 点击暂停推流
     */
    public void onClickPause(View view) {
        if (mSession != null) {
            mSession.pauseStreaming();
        }
    }

    /**
     * 点击恢复推流
     */
    public void onClickResume(View view) {
        if (mSession != null) {
            mSession.resumeStreaming();
        }
    }

    private void stopGame(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String experienceCode =
                pref.getString(context.getResources().getString(R.string.key_experience_code),
                        "");
        TcrTestEnv.getInstance().stopSession(experienceCode);
    }

    /**
     * 点击关闭云端游戏
     */
    public void onClickCloseRemoteGame(View view) {
        stopGame(view.getContext());
    }

    /**
     * 点击复制文本
     */
    public void onClickPasteText(View view) {
        String pasteText = mModel.pasteText.getValue();
        if (pasteText == null || TextUtils.isEmpty(pasteText)) {
            mFragment.showToast(mRenderView.getContext(), "请输入需要复制的文本", Toast.LENGTH_SHORT);
            return;
        }
        if (mSession != null) {
            mSession.pasteText(pasteText);
        }
    }

    /**
     * 点击退出游戏
     */
    public void onClickExit(View view) {
        if (mDataChannel != null) {
            mDataChannel.close();
        }
        if (mSession != null) {
            mSession.release();
            mSession = null;

            stopGame(view.getContext());
        }

        if (mFragment != null) {
            Activity activity = mFragment.getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 返回键KeyCode
     */
    static final int KEY_BACK = 158;

    /**
     * 菜单键KeyCode
     */
    static final int KEY_MENU = 139;

    /**
     * Home键KeyCode
     */
    static final int KEY_HOME = 172;

    public void onClickMenu(View view) {
        if (mSession != null) {
            mSession.getKeyboard().onKeyboard(KEY_MENU, true);
            mSession.getKeyboard().onKeyboard(KEY_MENU, false);
        }
    }

    public void onClickHome(View view) {
        if (mSession != null) {
            mSession.getKeyboard().onKeyboard(KEY_HOME, true);
            mSession.getKeyboard().onKeyboard(KEY_HOME, false);
        }
    }

    public void onClickBack(View view) {
        if (mSession != null) {
            mSession.getKeyboard().onKeyboard(KEY_BACK, true);
            mSession.getKeyboard().onKeyboard(KEY_BACK, false);
        }
    }

    /**
     * 点击重启云端App
     */
    public void onClickRestartCloudApp(View view) {
        if (mSession != null) {
            mSession.restartCloudApp();
        }
    }

    /**
     * 点击鼠标左键
     */
    public boolean onTouchMouseLeft(View v, MotionEvent event) {
        if (mSession == null) {
            return false;
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mSession.getMouse().onMouseKey(KeyType.LEFT, true);
                break;
            case MotionEvent.ACTION_UP:
                mSession.getMouse().onMouseKey(KeyType.LEFT, false);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 点击鼠标右键
     */
    public boolean onTouchMouseRight(View v, MotionEvent event) {
        if (mSession == null) {
            return false;
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mSession.getMouse().onMouseKey(KeyType.RIGHT, true);
                break;
            case MotionEvent.ACTION_UP:
                mSession.getMouse().onMouseKey(KeyType.RIGHT, false);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 点击查询键盘大小写状态
     */
    public void onClickCheckKeyBoardCapsLock(View view) {
        if (mSession == null) {
            return;
        }
        mSession.getKeyboard().checkKeyboardCapsLock(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (mFragment != null) {
                    mFragment.showToast(view.getContext(), "KeyboardCapsLock = " + result, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                String myMsg = "checkKeyboardCapsLock failed, code=" + code + " msg=" + msg;
                Log.e(sApiTAG, myMsg);
            }
        });

    }

    public void onClickResetKeyBoardCapsLock(View view) {
        if (mSession == null) {
            return;
        }
        mSession.getKeyboard().resetKeyboardCapsLock();
    }

    /**
     * 调整鼠标灵敏度
     */
    public void onMouseMoveSensitivityChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPcTouchListener == null || mRenderView == null) {
            return;
        }
        if (progress >= 0 && progress <= 100) {
            mMoveSensitivity = (float) (progress / 10.0);
            mPcTouchListener.setMouseConfig(mMouseRelativeMove, mMoveSensitivity, mMouseVisibility);
        }
    }

    public void switchTouchMode(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.radio_btn_pc_input) {
            switchPCOnTouch();
        } else {
            switchMobileOnTouch();
        }
    }

    /**
     * 切换为端游操作模式
     */
    public void switchPCOnTouch() {
        if (mSession != null && mPcTouchListener!=null) {
            mPcTouchListener = new PcTouchListener(mSession);
            mPcTouchListener.getZoomHandler().setZoomRatio(1, 5);
            mPcTouchListener.setMouseConfig(mMouseRelativeMove, mMoveSensitivity, mMouseVisibility);
        }
        if (mRenderView!=null) {
            mRenderView.setOnTouchListener(mPcTouchListener);
        }
    }
    /**
     * 切换为手游操作模式
     */
    public void switchMobileOnTouch() {
        if (mRenderView != null && mSession != null) {
            mRenderView.setOnTouchListener(new MobileTouchListener(mSession));
        }
    }

    /**
     * 重置画面，将缩放和拖动的视图进行重置
     */
    public void onClickSetPinchOffset(View view) {
        if (mRenderView == null || mPcTouchListener == null) {
            return;
        }
        if (mModel == null) {
            return;
        }
        String pinchLeftValue = mModel.pinchLeft.getValue();
        String pinchTopValue = mModel.pinchTop.getValue();
        String pinchRightValue = mModel.pinchRight.getValue();
        String pinchBottomValue = mModel.pinchBottom.getValue();
        if (pinchLeftValue == null || pinchBottomValue == null || pinchTopValue == null || pinchRightValue == null) {
            return;
        }
        Rect rect = new Rect(Integer.parseInt(pinchLeftValue), Integer.parseInt(pinchTopValue),
                Integer.parseInt(pinchRightValue), Integer.parseInt(pinchBottomValue));
        mPcTouchListener.getZoomHandler().setZoomOffset(rect);
    }

    /**
     * 重置画面，将缩放和拖动的视图进行重置
     */
    public void onClickResetView(View view) {
        if (mPcTouchListener != null) {
            mPcTouchListener.getZoomHandler().resetZoom();
        }
    }

    /**
     * 传递motion event事件
     * 这里构造了一个按下坐标(200,200)的位置
     */
    public void onClickMotionEvent(View view) {
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 200, 200, 0);

        if (mRenderView != null) {
            mRenderView.handleMotion(event);
        }
    }

    /**
     * 更改下行音量
     */
    public void onRemoteVolumeChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mSession == null) {
            return;
        }
        // 我们接口对外定义值为[0-10],默认1，超过1可能会声音失真
        if (progress >= 0 && progress <= 100) {
            mSession.setRemoteAudioPlayProfile((float) (progress / 10.0));
        }
    }
//    /**
//     * 更改上行音量
//     */
//    public void onLocalVolumeChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (mSession == null) {
//            return;
//        }
//        if (progress >= 0 && progress <= 100) {
//            // 我们接口对外定义值为[0-10],默认1，超过1可能会声音失真
//            mSession.setLocalAudioProfile((float) (progress / 10.0));
//        }
//    }

    ///////////////////////////////////////////// 互动云游 begin ////////////////////////////////////////////

    /**
     * 点击'同步全部用户信息'
     */
    public void onClickSyncRoomInfo(View view) {
        if (mSession != null) {
            mSession.syncRoomInfo();
        }
    }

    /**
     * 点击'切换为玩家'
     */
    public void onClickChangeToPlayer(View view) {
        changeToRole(view.getContext(), MultiUser.Role.PLAYER);
    }

    /**
     * 点击'切换为观察者'
     */
    public void onClickChangeToViewer(View view) {
        changeToRole(view.getContext(), MultiUser.Role.VIEWER);
    }

    /**
     * 点击'申请为玩家'
     */
    public void onClickApplyToPlayer(View view) {
        applyToRole(view.getContext(), MultiUser.Role.PLAYER);
    }

    /**
     * 点击'申请为观察者'
     */
    public void onClickApplyToViewer(View view) {
        applyToRole(view.getContext(), MultiUser.Role.VIEWER);
    }

    private void changeToRole(Context context, MultiUser.Role role) {
        if (mSession == null) {
            return;
        }

        if (mModel == null) {
            return;
        }

        String userID = mModel.multiPlayerUserId.getValue();
        if (TextUtils.isEmpty(userID)) {
            mFragment.showToast(context, "请输入用户ID", Toast.LENGTH_SHORT);
            return;
        }

        String seatIndex = mModel.multiPlayerSeatIndex.getValue();
        if (TextUtils.isEmpty(seatIndex)) {
            mFragment.showToast(context, "请输入席位", Toast.LENGTH_SHORT);
            return;
        }

        assert seatIndex != null;
        mSession.changeSeat(userID, role, Integer.parseInt(seatIndex),
                new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        String msg = String.format("MultiUserManager#changeSeat %s to:%s index:%s success", userID,
                                role, seatIndex);
                        logAndToast(msg);
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        String myMsg =
                                String.format(Locale.ENGLISH,
                                        "MultiUserManager#changeSeat %s to:%s index:%s failed:%d msg:%s",
                                        userID,
                                        role, seatIndex, code, msg);
                        logAndToast(myMsg);

                    }
                });
    }

    private void applyToRole(Context context, MultiUser.Role role) {
        if (mSession == null) {
            return;
        }

        if (mModel == null) {
            return;
        }

        String userID = mModel.multiPlayerUserId.getValue();
        if (TextUtils.isEmpty(userID)) {
            mFragment.showToast(context, "请输入用户ID", Toast.LENGTH_SHORT);
            return;
        }

        String seatIndex = mModel.multiPlayerSeatIndex.getValue();
        if (TextUtils.isEmpty(seatIndex)) {
            mFragment.showToast(context, "请输入席位", Toast.LENGTH_SHORT);
            return;
        }

        assert seatIndex != null;
        mSession.requestChangeSeat(userID, role, Integer.parseInt(seatIndex),
                new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        String msg = String.format("MultiUserManager#applyChangeSeat %s to:%s index:%s success", userID,
                                role, seatIndex);
                        logAndToast(msg);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        String myMsg =
                                String.format(Locale.ENGLISH,
                                        "MultiUserManager#applyChangeSeat %s to:%s index:%s failed:%d msg:%s",
                                        userID,
                                        role, seatIndex, code, msg);
                        logAndToast(myMsg);
                    }
                });
    }

    /**
     * 静音userId麦克风
     */
    public void onClickMuteUser(CompoundButton compoundButton, boolean checked, User user) {
        if (mSession == null) {
            return;
        }
        mSession.setMicMute(user.userID, checked ? MultiUser.MIC_STATUS_OFF : MultiUser.MIC_STATUS_ON,
                new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        String myMsg = "设置" + user.userID + "静音成功";
                        logAndToast(myMsg);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        String myMsg = String.format(Locale.ENGLISH,
                                "设置 %s 静音失败，error code=%d  error msg= %s", user.userID, code, msg);
                        logAndToast(myMsg);
                    }
                });
    }
    ///////////////////////////////////////////// 互动云游 end ////////////////////////////////////////////

    ///////////////////////////////////////////// 音视频上行 begin ////////////////////////////////////////////

    /**
     * 开关麦克风上行
     */
    public void enableLocalAudio(RadioGroup group, int checkedId) {
        boolean enable = checkedId == R.id.radio_btn_enable_local_audio;
        if (mSession != null) {
            mSession.setEnableLocalAudio(enable);
            if (mCustomAudioCapturer != null) {
                mCustomAudioCapturer.startRecording(mSession);
            }
        }
    }

    /**
     * 开关摄像头上行
     */
    public void enableLocalVideo(RadioGroup group, int checkedId) {
        boolean enable = checkedId == R.id.radio_btn_enable_local_video;
        if (mSession != null) {
            mSession.setEnableLocalVideo(enable);
        }
    }

    /**
     * 设置摄像头上行码率
     */
    public void setLocalVideoBitrate(View view) {
        if (mSession == null) {
            return;
        }
        if (mModel == null) {
            return;
        }
        String localVideoBitrate = mModel.localVideoBitrate.getValue();
        if (localVideoBitrate != null) {
            int videoBitrate = Integer.parseInt(localVideoBitrate);
            mSession.setLocalVideoProfile(480, 640, 30, videoBitrate, videoBitrate, true);
        }
    }


    /**
     * 设置桌面分辨率
     */
    public void setRemoteDesktopResolution(View view) {
        if (mSession == null) {
            return;
        }
        if (mModel == null) {
            return;
        }
        String videoWidth = mModel.videoWidth.getValue();
        String videoHeight = mModel.videoHeight.getValue();
        if (videoWidth != null && videoHeight != null) {
            int width = Integer.parseInt(videoWidth);
            int height = Integer.parseInt(videoHeight);
            mSession.setRemoteDesktopResolution(width, height);
        }
    }

    /**
     * 创建数据通道
     */
    public void createDataChannel(View view) {
        if (mSession == null) {
            return;
        }
        if (mModel == null) {
            return;
        }
        String port = mModel.dataChannelPort.getValue();
        if (port != null) {
            mDataChannel = mSession.createCustomDataChannel(Integer.parseInt(port), new CustomDataChannel.Observer() {
                @Override
                public void onConnected(int port) {
                    String msg = "CustomDataChannel.Observer: Create dataChannel success,port=" + port;
                    logAndToast(msg);
                    boolean status = mDataChannel.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                    Log.i(sApiTAG, "DataChannel#send: send buffer status=" + status);
                }

                @Override
                public void onError(int port, int code, String msg) {
                    String message =
                            "CustomDataChannel.Observer: dataChannel error:port=" + port + "  errorCode=" + code
                                    + "  errorMsg=" + msg;
                    logAndToast(message);
                }

                @Override
                public void onMessage(int port, ByteBuffer data) {
                    String msg = "onMessage() port=" + port + " data=" + StandardCharsets.UTF_8.decode(data);
                    logAndToast(msg);
                }
            });
        }
    }

    /**
     * 点击通过数据通道发送数据
     */
    public void dataChannelSend(View view) {
        if (mDataChannel == null) {
            return;
        }
        if (mModel == null) {
            return;
        }
        String msg = mModel.dataChannelMsg.getValue();
        String res = null;
        if (msg != null) {
            res = mDataChannel.send(StandardCharsets.UTF_8.encode(msg)) ? "数据发送成功" : "数据发送失败";
        }
        logAndToast(res);
    }

    private void logAndToast(String msg) {
        Log.i(sApiTAG, msg);
        if (mFragment != null) {
            mFragment.showToast(mFragment.getContext(), msg, Toast.LENGTH_SHORT);
        }
    }


    public void startAutoLogin(View view) {
        if (mSession == null) {
            Log.e(sApiTAG, "startAutoLogin() mSession=null");
            return;
        }
        // 判空
        String account = mModel.account.getValue();
        String password = mModel.password.getValue();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            logAndToast("empty account=" + account + " or password=" + password);
            return;
        }

//        mSession.getLoginHelper().checkAutoLoginSupport(new AsyncCallback<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                mSession.getLoginHelper().startAutoLogin(account, password,null);
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                logAndToast("自动登录检查失败:"+s);
//            }
//        });
    }

    ///////////////////////////////////////////// 视频上行 end ////////////////////////////////////////////

    /**
     * 读取配置文件
     *
     * @param fileName 文件名
     * @return 返回String类型JsonConfig内容
     */
    private static String readConfigFile(Context context, String fileName) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            char[] input = new char[is.available()];
            isr.read(input);
            isr.close();
            is.close();
            return new String(input);
        } catch (Exception e) {
            Log.e(TAG, "readConfigFile failed:" + e);
        }
        return null;
    }

}
