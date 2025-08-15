package com.tencent.tcrdemo.gameplay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.gson.Gson;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.CustomDataChannel;
import com.tencent.tcr.sdk.api.TcrLogger;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.TcrSessionConfig;
import com.tencent.tcr.sdk.api.TcrTestEnv;
import com.tencent.tcr.sdk.api.data.CursorImageInfo;
import com.tencent.tcr.sdk.api.data.CursorState;
import com.tencent.tcr.sdk.api.data.MultiUser;
import com.tencent.tcr.sdk.api.data.MultiUser.Role;
import com.tencent.tcr.sdk.api.data.MultiUserSeatInfo;
import com.tencent.tcr.sdk.api.data.RoleApplyInfo;
import com.tencent.tcr.sdk.api.data.ScreenConfig;
import com.tencent.tcr.sdk.api.data.StatsInfo;
import com.tencent.tcr.sdk.api.data.VideoStreamConfig;
import com.tencent.tcr.sdk.api.view.MobileTouchListener;
import com.tencent.tcr.sdk.api.view.PcTouchListener;
import com.tencent.tcr.sdk.api.view.PcZoomHandler;
import com.tencent.tcr.sdk.api.view.TcrRenderView;
import com.tencent.tcr.sdk.api.view.TcrRenderView.TcrRenderViewType;
import com.tencent.tcr.sdk.api.view.TcrRenderView.VideoRotation;
import com.tencent.tcrdemo.BR;
import com.tencent.tcrdemo.R;
import com.tencent.tcrdemo.adapter.MultiPlayerAdapter;
import com.tencent.tcrdemo.adapter.SimplePagerAdapter;
import com.tencent.tcrdemo.bean.CameraStatus;
import com.tencent.tcrdemo.bean.HitInput;
import com.tencent.tcrdemo.bean.User;
import com.tencent.tcrdemo.databinding.FragmentGamePlayBinding;
import com.tencent.tcrdemo.utils.CustomAudioCapturer;
import com.tencent.tcrdemo.utils.DeviceUtils;
import com.tencent.tcrdemo.utils.TcrSdkWrapper;
import com.tencent.tcrgamepad.GamepadManager;
import com.tencent.tcrgui.keyboard.KeyboardView;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.twebrtc.VideoCapturer;
import pub.devrel.easypermissions.EasyPermissions;

public class GamePlayFragment extends Fragment implements Handler.Callback, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "GamePlayFragment";
    private static final String sApiTAG = "ApiTest";
    private static final int MOBILE_GAME = 1;
    private static final int PC_GAME = 2;
    /**
     * 初始化会话成功
     **/
    private static final int MSG_INIT_SESSION_SUCCESS = 0x001;
    /**
     * 启动会话成功
     **/
    private static final int MSG_START_SESSION_SUCCESS = 0x002;
    /**
     * 多人云游-席位更新
     **/
    private static final int MSG_SEAT_CHANGED = 0x003;
    // 测试入口
    private final TestApiHandler mTestApiHandler = new TestApiHandler();
    // 云游类型，PC or Android or unknown
    private final MultiPlayerAdapter<User> mMultiPlayViewerAdapter = new MultiPlayerAdapter<>(R.layout.multi_user_item,
            BR.multiUser);
    private final MultiPlayerAdapter<User> mMultiPlayPlayerAdapter = new MultiPlayerAdapter<>(R.layout.multi_user_item,
            BR.multiUser);
    private final DecimalFormat mDf = new DecimalFormat("#.##");
    private final Handler mUIThreadHandler = new Handler(this);
    private FragmentGamePlayBinding mViewDataBinding;
    private GamePlayViewModel mViewModel;
    // 会话
    private volatile TcrSession mSession;
    // 从SDK创建的渲染视图
    private TcrRenderView mRenderView;
    private PcTouchListener mPcTouchListener;
    // 虚拟手柄视图
    private GamepadManager mGamePadManager;
    // 键盘视图的父容器
    private RelativeLayout mKeyboardParent;
    // 虚拟键盘视图
    private KeyboardView mKeyboardView;
    // 测试环境
    private boolean mIsTestEnv;
    private boolean mIsIntlEnv;
    // 多人互动云游场景:房主id
    private String mHostUserId;
    // 多人互动云游场景:角色
    private String mRole;
    // 本机userId
    private String mHandlerUserId;
    // 在腾讯云官网生成的体验码
    private String mExperienceCode;
    // 自定义数据通道
    private CustomDataChannel mCustomDataChannel;
    // 视频流分辨率信息
    private VideoStreamConfig mVideoStreamConfig;
    // 云端横竖屏信息
    private ScreenConfig mScreenConfig;
    // 旋转视图的两个信号量
    private boolean mScreenConfigChanged = false;
    private boolean mVideoStreamConfigChanged = false;
    // 本地输入法Activity的启动器
    private ActivityResultLauncher<Intent> mInputActivityLauncher;
    private CameraStatus mCameraStatus;

    private void openCamera(CameraStatus cameraStatus) {
        Log.i(sApiTAG, "openCamera() status=" + cameraStatus.status + ", width=" + cameraStatus.width + ", height=" + cameraStatus.height);
        if (mSession == null) {
            Log.e(sApiTAG, "openCamera() mSession==null");
            return;
        }
        switch (cameraStatus.status) {
            case "open_front":
                mSession.setEnableLocalVideo(true);
                mSession.setLocalVideoProfile(cameraStatus.width, cameraStatus.height, 25, 512, 1024, true);
                break;
            case "open_back":
                mSession.setEnableLocalVideo(true);
                mSession.setLocalVideoProfile(cameraStatus.width, cameraStatus.height, 25, 512, 2048, false);
                break;
            case "close":
                mSession.setEnableLocalVideo(false);
                break;
            default:
        }
    }

    /**
     * session事件回调处理
     */
    private final TcrSession.Observer mSessionEventObserver = new TcrSession.Observer() {
        @Override
        public void onEvent(TcrSession.Event event, Object eventData) {
            if (TcrSession.Event.CLIENT_STATS != event && TcrSession.Event.REMOTE_DESKTOP_INFO != event
                    && TcrSession.Event.CURSOR_IMAGE_INFO != event) {
                // 不打印回调次数频繁的事件
                Log.i(sApiTAG, "event:" + event + " msg:" + eventData);
            }

            switch (event) {
                case STATE_INITED:
                    // 初始化成功，将回调的数据(clientSession)回调出去请求serverSessoin
                    mUIThreadHandler.obtainMessage(MSG_INIT_SESSION_SUCCESS, eventData).sendToTarget();
                    break;
                case STATE_CONNECTED:
                    // 链接建立完成，在这之后可进行和云端电脑的交互
                    mUIThreadHandler.obtainMessage(MSG_START_SESSION_SUCCESS).sendToTarget();
                    // 测试创建数据通道
                    mCustomDataChannel = mSession.createCustomDataChannel(6666, new CustomDataChannel.Observer() {
                        @Override
                        public void onConnected(int port) {
                            final String msg = "{\"connected\":true}";
                            mCustomDataChannel.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                            Log.i(sApiTAG, "onConnected() send data to port " + port + ": " + msg);
                        }

                        @Override
                        public void onError(int port, int code, String msg) {
                            Log.e(sApiTAG, "onError() " + port + " msg:" + msg);
                        }

                        @Override
                        public void onMessage(int port, ByteBuffer data) {
                            Log.i(sApiTAG, "onMessage() port=" + port + " data=" + StandardCharsets.UTF_8.decode(data));
                        }
                    });
                    Log.i(sApiTAG, "requestId = " + mSession.getRequestId());
                    break;
                case STATE_RECONNECTING:
                    // 内部发现链接断开，正在进行重连的回调通知
                    showToast(getContext(), "重连中...", Toast.LENGTH_LONG);
                    break;
                case STATE_CLOSED:
                    // 连接已断开，关闭activity
                    showToast(getContext(), "会话关闭：" + eventData, Toast.LENGTH_LONG);
                    getActivity().finish();
                    break;
                case SCREEN_CONFIG_CHANGE:
                    // 屏幕方向以及大小回调
                    mScreenConfig = (ScreenConfig) eventData;
                    if (mScreenConfig == null) {
                        Log.e(sApiTAG, "screenConfig parse error");
                        break;
                    }
                    mScreenConfigChanged = true;
                    updateRotation();
                    break;
                case VIDEO_STREAM_CONFIG_CHANGED:
                    // 流分辨率的回调
                    mVideoStreamConfig = (VideoStreamConfig) eventData;
                    mVideoStreamConfigChanged = true;
                    updateRotation();
                    break;
                case CAMERA_STATUS_CHANGED:
                    CameraStatus cameraStatus = new Gson().fromJson((String) eventData, CameraStatus.class);
                    if (cameraStatus == null) {
                        Log.e(TAG, "CAMERA_STATUS_CHANGED eventData: " + eventData);
                        break;
                    }
                    switch (cameraStatus.status) {
                        case "open_front":
                        case "open_back":
                            if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
                                openCamera(cameraStatus);
                            } else {
                                mCameraStatus = cameraStatus;
                                EasyPermissions.requestPermissions(
                                        GamePlayFragment.this,
                                        "需要摄像头权限才能开启摄像头",
                                        0,
                                        Manifest.permission.CAMERA
                                );
                            }
                            break;
                        case "close":
                            openCamera(cameraStatus);
                            break;
                        default:
                    }
                    break;
                case INPUT_STATE_CHANGE:
                    // 输入状态改变的回调，可以监听此回调做一些业务操作
                    // 此处示例代码为收到可输入时调起输入Activity，并将内容发送到云端输入框
                    HitInput hitinput = new Gson().fromJson((String) eventData, HitInput.class);
                    Log.i(sApiTAG, "INPUT_STATE_CHANGE() hitinput=" + hitinput);
                    if (hitinput == null) {
                        Log.e(sApiTAG, "onEvent() hitinput=null");
                        break;
                    }
                    if ("normal_input".equals(hitinput.field_type)) {
                        Intent intent = new Intent(getContext(), InputActivity.class);
                        intent.putExtra(InputActivity.INPUT_TEXT, hitinput.text);
                        mInputActivityLauncher.launch(intent);
                    }
                    break;

                case CLIENT_STATS:
                    // 性能状态信息数据的回调，这里拿到其中三个常用数据显示到右上角view上
                    StatsInfo statsInfo = (StatsInfo) eventData;
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mViewDataBinding.statsValue.setText(
                                        "   fps: " + statsInfo.fps + "   bitrate: "
                                                + mDf.format(statsInfo.bitrate / 1024.0 / 1024.0)
                                                + "Mb/s   rtt: " + statsInfo.rtt + "ms");
                            }
                        });
                    }
                    break;
                case CURSOR_STATE_CHANGE:
                    // 鼠标光标显示状态的回调
                    CursorState cursorState = (CursorState) eventData;
                    Log.i(sApiTAG, "cursor showing state changed, " + cursorState.toString());
                    break;
                case MULTI_USER_SEAT_INFO:
                    // 多人互动云游场景下:房间内有人员变动时收到的信息，包含房间所有人员的数据，此处拿到数据更新到UI上
                    // 有以下几种情况会回调该事件: 有人加入/退出房间，有人切换了角色/席位，个人调用了syncRoomInfo刷新房间信息接口
                    MultiUserSeatInfo multiUserSeatInfo = (MultiUserSeatInfo) eventData;
                    showToast(getContext(),
                            "seat changed player:" + Arrays.toString(multiUserSeatInfo.players.toArray())
                                    + "\n viewer:" + Arrays.toString(multiUserSeatInfo.viewers.toArray()),
                            Toast.LENGTH_SHORT);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", multiUserSeatInfo.userID);
                    bundle.putParcelableArrayList("players",
                            (ArrayList<? extends Parcelable>) multiUserSeatInfo.players);
                    bundle.putParcelableArrayList("viewers",
                            (ArrayList<? extends Parcelable>) multiUserSeatInfo.viewers);
                    mUIThreadHandler.obtainMessage(MSG_SEAT_CHANGED, bundle).sendToTarget();
                    break;
                case MULTI_USER_ROLE_APPLY:
                    // 多人互动云游场景下:房主收到房间内其他玩家的申请切换角色/坐席的请求，弹出确认框进行操作
                    RoleApplyInfo roleApplyInfo = (RoleApplyInfo) eventData;
                    String myMsg =
                            String.format(Locale.ENGLISH, "onRoleApplied userID:%s to:%s index:%s",
                                    roleApplyInfo.userID, roleApplyInfo.role, roleApplyInfo.seatIndex);
                    Log.i(sApiTAG, myMsg);
                    showToast(getContext(), myMsg, Toast.LENGTH_SHORT);
                    mUIThreadHandler.post(() -> showNormalDialog(roleApplyInfo));
                    break;
                case CURSOR_IMAGE_INFO:
                    // 鼠标图片信息的下发，一般为自行渲染视图时，可拿到鼠标图片渲染到自定义渲染的视图上，便于操作
                    CursorImageInfo cursorImageInfo = (CursorImageInfo) eventData;
//                    Log.i(sApiTAG, "cursor image info: bitmap=" + cursorImageInfo.cursorBitmap + " hotspotx="
//                            + cursorImageInfo.hotSpotX + " hotspoty=" + cursorImageInfo.hotSpotY);
                    break;
                case PROXY_RELAY_AVAILABLE:
                    // 代理中继服务器可用，可以通过此回调获取到代理服务器信息
                    ProxyService.initProxy(getContext(), (String) eventData);
                default:
                    // do nothing
                    break;
            }
        }
    };
    // 自定义采集麦克风数据
    private CustomAudioCapturer mCustomAudioCapturer;
    // 是否开启自定义采集麦克风数据
    private boolean mEnableCustomAudioCapture;

    public GamePlayFragment() {
        mTestApiHandler.setFragment(this);
    }

    public static GamePlayFragment newInstance() {
        return new GamePlayFragment();
    }

    public void setUserId(String userId) {
        mHandlerUserId = userId;
    }

    public void setHostUserId(String hostUserId) {
        mHostUserId = hostUserId;
    }

    public void setTestEnv(boolean isTestEnv) {
        mIsTestEnv = isTestEnv;
    }

    public void setIntlEnv(boolean isIntlEnv) {
        mIsIntlEnv = isIntlEnv;
    }

    public void setRole(boolean isPlayer) {
        mRole = isPlayer ? "Player" : "Viewer";
    }

    public void setExperienceCode(String experienceCode) {
        mExperienceCode = experienceCode;
    }

    public void setViewModel(GamePlayViewModel viewModel) {
        mViewModel = viewModel;
        mTestApiHandler.setViewModel(mViewModel);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mInputActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 收到输入Activity输入的内容，通过session复制接口将内容复制过去
                    if (mSession == null) {
                        Log.e(TAG, "onActivityResult mSession=null");
                        return;
                    }

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            Log.e(TAG, "onActivityResult data=null");
                            return;
                        }
                        String input = data.getStringExtra(InputActivity.INPUT_TEXT);
                        Log.d(TAG, "onActivityResult input:" + input);
                        mSession.pasteText(input);
                    }
                });

        AsyncCallback<Void> initCallback = new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.i(sApiTAG, "TcrSdk#init success.");
                if (getContext() != null && !isDetached()) {
                    // SDK初始化成功后创建Session
                    mSession = TcrSdk.getInstance().createTcrSession(createSessionConfig());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e(sApiTAG, "TcrSdk#init failed:" + code + " msg:" + msg);
                Context context = getContext();
                if (context != null) {
                    showToast(getContext(),
                            "初始化失败: code=" + code + " msg:" + msg,
                            Toast.LENGTH_LONG);
                }
            }
        };
        // 初始化SDK
        TcrSdkWrapper.getInstance().init(getContext(), initCallback);
        setLogger();
    }

    protected TcrSessionConfig createSessionConfig() {
        VideoCapturer videoCapture = null;
        // 演示自定义采集视频上行。打开以下注释后，openCamera() 的上行视频会被替换为自定义采集视频。注意这里只是为了演示功能，实际使用时请不要在主线程进行拷贝文件操作。
//        try {
//            String filePath = "/storage/emulated/0/Android/data/com.tencent.tcrdemo.full/files/VideoCapture.y4m";
//            File y4mFile = new File(filePath);
//            if (!y4mFile.exists()) {
//                InputStream inputStream = getContext().getAssets().open("VideoCapture.y4m");
//                OutputStream outputStream = new FileOutputStream(y4mFile);
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = inputStream.read(buffer)) > 0) {
//                    outputStream.write(buffer, 0, length);
//                }
//                inputStream.close();
//                outputStream.close();
//            }
//            videoCapture = new MyFileVideoCapturer(filePath);
//        } catch (IOException e) {
//            Log.e(TAG, "videoCapture=null, e=" + e.getMessage());
//        }
        Pair<Integer, Integer> screenSize = DeviceUtils.getScreenSize(requireActivity());
        TcrSessionConfig.Builder builder = TcrSessionConfig.builder()
                .observer(mSessionEventObserver)
                .enableCustomVideoCapture(videoCapture)
                .idleThreshold(30000)
                .lowFpsThreshold(31, 5)
                .remoteDesktopResolution(screenSize.first, screenSize.second)
                .enableLowLegacyRendering(true);
        if (mEnableCustomAudioCapture) {
            mCustomAudioCapturer = new CustomAudioCapturer();
            builder.enableCustomAudioCapture(true, mCustomAudioCapturer.getSampleRateInHz(),
                    mCustomAudioCapturer.getChannelNum() == 2);
        }
        return builder.build();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mCustomAudioCapturer != null) {
            mCustomAudioCapturer.stopRecording();
        }
        if (mPcTouchListener != null) {
            mPcTouchListener.release();
        }
        if (mRenderView != null) {
            mRenderView.release();
            mRenderView = null;
            Log.i(sApiTAG, "TcrRenderView#release");
        }
        if (mSession != null) {
            mSession.release();
            mSession = null;
            Log.i(sApiTAG, "TcrSession#release");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        mViewDataBinding.setViewModel(mViewModel);
        mViewDataBinding.setHandlers(mTestApiHandler);
        mViewDataBinding.setLifecycleOwner(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false);
        mViewDataBinding.setViewerAdapter(mMultiPlayViewerAdapter);
        mViewDataBinding.setPlayerAdapter(mMultiPlayPlayerAdapter);
        View rootView = mViewDataBinding.getRoot();
        ViewPager pager = rootView.findViewById(R.id.pager);
        SimplePagerAdapter adapter = new SimplePagerAdapter(pager);
        pager.setAdapter(adapter);
        addChildView();
        adapter.getView(0).findViewById(R.id.mouseLeft).setOnTouchListener((v, event) -> {
            boolean isSuccess = mTestApiHandler.onTouchMouseLeft(v, event);
            v.performClick();
            return isSuccess;
        });
        adapter.getView(0).findViewById(R.id.mouseRight).setOnTouchListener((v, event) -> {
            boolean isSuccess = mTestApiHandler.onTouchMouseRight(v, event);
            v.performClick();
            return isSuccess;
        });
        return rootView;
    }

    private void addChildView() {
        if (mViewDataBinding == null) {
            return;
        }

        if (mRenderView == null) {
            return;
        }

        FrameLayout parent = mViewDataBinding.getRoot().findViewById(R.id.render_view_parent);
        if (parent == null) {
            Log.w(TAG, "addChildView() parent=null");
            return;
        }
        // 添加云游视图
        parent.addView(mRenderView);

        // 添加虚拟键盘视图，初始化但不可见
        mKeyboardParent = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mKeyboardParent, lp);

        RelativeLayout.LayoutParams kbLp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        kbLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mKeyboardView.setLayoutParams(kbLp);
        mKeyboardParent.addView(mKeyboardView);
        mKeyboardParent.setVisibility(View.GONE);

        // 添加虚拟手柄视图，初始化但不可见
        ViewGroup.LayoutParams gpLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mGamePadManager, gpLp);
        if (mGamePadManager.getParent() != null) {
            ((ViewGroup) mGamePadManager.getParent()).removeView(mGamePadManager);
        }
        parent.addView(mGamePadManager, gpLp);
        mGamePadManager.setVisibility(View.GONE);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Log.d(TAG, "handleMessage() msg.what=" + msg.what);
        switch (msg.what) {
            case MSG_INIT_SESSION_SUCCESS:
                String clientSession = (String) msg.obj;
                Log.i(TAG, "init session success:" + clientSession);
                Context context = getContext();
                if (context == null) {
                    Log.w(TAG, "mInitSessionCallback() context=null");
                    break;
                }

                mRenderView =
                        TcrSdk.getInstance().createTcrRenderView(context, mSession, TcrRenderViewType.SURFACE);
                mRenderView.post(() -> {
                    Toast.makeText(context, "开始请求云API", Toast.LENGTH_SHORT).show();
                    TcrTestEnv.getInstance()
                            .startSession(context, mExperienceCode, clientSession, null, null, null, mIsTestEnv,
                                    mIsIntlEnv, response -> {
                                        boolean res = mSession.start(response);
                                        if (!res) {
                                            Log.e(sApiTAG, "TcrSession#start() fail.");
                                        }
                                    }, error -> {
                                        showToast(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG);
                                        Log.w(TAG, error);
                                    });

                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        mRenderView.requestPointerCapture();
                    }
                });
                mGamePadManager = new GamepadManager(context, mSession);
                mKeyboardView = new KeyboardView(context, mSession);
                // 把云游视图添加到View树上
                addChildView();
                break;
            case MSG_START_SESSION_SUCCESS:
                if (mSession == null) {
                    Log.w(TAG, "handleMessage() START_SESSION_SUCCESS mSession=null");
                    break;
                }

                // 针对不同的云端实例设置不同的处理器
                setTouchHandler(mRenderView, MOBILE_GAME);

                // 开始我们的测试
                mTestApiHandler.set(mSession, mRenderView, mPcTouchListener, mGamePadManager,
                        mKeyboardParent, mViewDataBinding.pinchPivotValue,
                        mViewDataBinding.pinchScaleValue, mCustomAudioCapturer);
                if (mHostUserId != null) {
                    mMultiPlayPlayerAdapter.setSession(mSession, mHostUserId, mHandlerUserId);
                    mMultiPlayViewerAdapter.setSession(mSession, mHostUserId, mHandlerUserId);
                }
                break;

            case MSG_SEAT_CHANGED:
                Bundle bundle = (Bundle) msg.obj;
                ArrayList<MultiUser> players = bundle.getParcelableArrayList("players");
                List<User> users = new ArrayList<>();
                for (MultiUser u : players) {
                    User user = new User();
                    user.role = Role.PLAYER;
                    user.userID = u.userID;
                    user.seatIndex = u.seatIndex;
                    user.muteChecked = u.micStatus == MultiUser.MIC_STATUS_ON;
                    users.add(user);
                }
                mMultiPlayPlayerAdapter.updateData(users);

                users = new ArrayList<>();
                ArrayList<MultiUser> viewers = bundle.getParcelableArrayList("viewers");
                for (MultiUser u : viewers) {
                    User user = new User();
                    user.role = Role.VIEWER;
                    user.userID = u.userID;
                    user.seatIndex = u.seatIndex;
                    user.muteChecked = u.micStatus == MultiUser.MIC_STATUS_ON;
                    users.add(user);
                }
                mMultiPlayViewerAdapter.updateData(users);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 点击设置Logger，然后将回调的日志TAG前加 sdk
     */
    private void setLogger() {
        final String SDKLogTAG = "TcrSdk";
        TcrSdk.getInstance().setLogger(new TcrLogger() {
            @Override
            public void v(String tag, String msg) {
                Log.v(SDKLogTAG + ":" + tag, msg);
            }

            @Override
            public void d(String tag, String msg) {
                Log.d(SDKLogTAG + ":" + tag, msg);
            }

            @Override
            public void i(String tag, String msg) {
                Log.i(SDKLogTAG + ":" + tag, msg);
            }

            @Override
            public void e(String tag, String msg) {
                Log.e(SDKLogTAG + ":" + tag, msg);
            }

            @Override
            public void w(String tag, String msg) {
                Log.w(SDKLogTAG + ":" + tag, msg);
            }
        });
    }

    /**
     * 旋转屏幕方向以及画面方向, 以便本地的屏幕方向和云端保持一致<br>
     * 注意: 请确保Manifest中的Activity有android:configChanges="orientation|screenSize"配置, 避免Activity因旋转而被销毁.<br>
     **/
    private void updateRotation() {
        if (!mScreenConfigChanged || !mVideoStreamConfigChanged) {
            Log.w(TAG, "updateRotation failed,mScreenConfigChanged=" + mScreenConfigChanged
                    + "  mVideoStreamConfigChanged=" + mScreenConfigChanged);
            return;
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.w(TAG, "updateOrientation() activity=null");
            return;
        }

        Log.w(TAG, "mVideoStreamConfig=" + mVideoStreamConfig);
        Log.w(TAG, "mScreenConfig.degree=" + mScreenConfig.degree);


        // 1. 根据云端Activity的方向（degree）和视频流的宽高，调整本地屏幕方向(使得本地屏幕方向和云端Activity方向保持一致)
        // 视频流	云端Activity		客户端处理
        // 横屏	      竖屏		    设置竖屏
        // 竖屏        竖屏           设置竖屏
        // 竖屏        横屏           设置横屏
        // 横屏        横屏           设置横屏
        boolean isLandscape = mScreenConfig.degree.equals("90_degree") || mScreenConfig.degree.equals("270_degree");
        boolean isPortrait = mScreenConfig.degree.equals("0_degree") || mScreenConfig.degree.equals("180_degree");
        if (mVideoStreamConfig.width > mVideoStreamConfig.height) {
            if (isLandscape) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (isPortrait) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        // 2. 根据云端屏幕方向，调整本地画面方向(云端画面为逆时针旋转, 本地视图setVideoRotation设置的是顺时针旋转)
        if (mScreenConfig.degree.equals("0_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_0);
        }
        if (mScreenConfig.degree.equals("90_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_270);
        }
        if (mScreenConfig.degree.equals("180_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_180);
        }
        if (mScreenConfig.degree.equals("270_degree")) {
            mRenderView.setVideoRotation(VideoRotation.ROTATION_90);
        }
    }


    /**
     * 设置不同的操作模式
     * PcTouchListener为将本地触摸转为鼠标操作
     * MobileTouchListener为将本地触摸转为云端触摸(windows应用支持触摸/手游平台)
     */
    private void setTouchHandler(TcrRenderView renderView, int type) {
        Log.d(TAG, "setTouchHandler() mGameType=" + type);
        switch (type) {
            case MOBILE_GAME:
                renderView.setOnTouchListener((OnTouchListener) new MobileTouchListener(mSession));
                break;
            case PC_GAME:
                mPcTouchListener = new PcTouchListener(mSession);
                mPcTouchListener.getZoomHandler().setZoomRatio(1f, 5f);
                renderView.setOnTouchListener((OnTouchListener) mPcTouchListener);
                mPcTouchListener.getZoomHandler().setZoomListener(new PcZoomHandler.ZoomListener() {
                    @Override
                    public void onPivot(float x, float y) {
                        mUIThreadHandler.post(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mViewDataBinding.pinchPivotValue.setText("onPivot x=" + x + ", y=" + y);
                            }
                        });
                    }

                    @Override
                    public void onZoom(float ratio) {
                        mUIThreadHandler.post(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mViewDataBinding.pinchScaleValue.setText("onZoom ratio=" + ratio);
                            }
                        });
                    }
                });
                break;
            default:
                Log.e(TAG, "UNKNOWN DeviceMode!!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsGranted requestCode:" + requestCode + " perms:" + perms);
        if (requestCode == 0 && perms.contains(Manifest.permission.CAMERA)) {
            openCamera(mCameraStatus);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied requestCode:" + requestCode + " perms:" + perms);
        if (requestCode == 0 && perms.contains(Manifest.permission.CAMERA)) {
            Toast.makeText(getContext(), "摄像头权限被拒绝，无法打开摄像头", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 房主收到房间内切换申请的弹出框
     */
    private void showNormalDialog(RoleApplyInfo roleApplyInfo) {
        String userID = roleApplyInfo.userID;
        Role role = roleApplyInfo.role;
        int seatIndex = roleApplyInfo.seatIndex;
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(requireActivity());
        normalDialog.setTitle("访客申请切换席位");
        normalDialog.setMessage(userID + "申请切换席位 role=" + role + "  seatIndex=" + seatIndex);
        normalDialog.setPositiveButton("同意",
                (dialog, which) -> {
                    //...To-do
                    mSession.changeSeat(userID, role, seatIndex,
                            new AsyncCallback<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    String msg = String.format(
                                            "MultiUserManager#changeSeat %s to:%s index:%s success", userID,
                                            role, seatIndex);
                                    Log.i(sApiTAG, msg);
                                    showToast(getContext(), msg,
                                            Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    String myMsg =
                                            String.format(Locale.ENGLISH,
                                                    "MultiUserManager#changeSeat %s to:%s index:%s failed:%d msg:%s",
                                                    userID,
                                                    role, seatIndex, i, s);
                                    Log.e(sApiTAG, myMsg);
                                    showToast(getContext(), myMsg,
                                            Toast.LENGTH_SHORT);
                                }
                            });
                });
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                    // do nothing
                });
        // 显示
        normalDialog.show();
    }

    void showToast(Context context, CharSequence text, int duration) {
        mUIThreadHandler.post(() -> {
            if (context == null) {
                Log.w(TAG, "toast() context=null text=" + text);
                return;
            }
            Toast.makeText(context, text, duration).show();
        });
    }
}
