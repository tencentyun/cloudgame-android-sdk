package com.tencent.tcr.demo.cloudphone;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.operator.ui.LoginActivity;
import com.tencent.tcr.demo.cloudphone.sfu.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.demo.cloudphone.sfu.ui.GetTokenActivity;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;

public class HomeActivity extends AppCompatActivity implements PermissionCallbacks {

    private static final String TAG = "com.tencent.tcr.demo.HomeActivity";
    private Button sfuDemo, operatorDemo;
    private TextView textView;
    private volatile boolean initTcrSdkSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sfuDemo = findViewById(R.id.sfuDemo);
        operatorDemo = findViewById(R.id.operatorDemo);
        textView = findViewById(R.id.textView);
        sfuDemo.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, GetTokenActivity.class)));
        operatorDemo.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, LoginActivity.class)));

        requestPermissions();// 请求权限

        initTcrSdk(null);// 初始化 TcrSdk
    }

    private void requestPermissions() {
        String[] permissions = {android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "Demo 需要录音、摄像头权限", 0, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this); // 转发权限结果
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "onPermissionsGranted requestCode:" + requestCode + " perms:" + perms);
        tryEnableDemo();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied requestCode:" + requestCode + " perms:" + perms);
        textView.setText("Demo 需要录音、摄像头权限");
    }

    private void tryEnableDemo() {
        String[] permissions = {android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, permissions) && initTcrSdkSuccess) {
            sfuDemo.setEnabled(true);
            operatorDemo.setEnabled(true);
            textView.setText("");
        }
    }

    private void initTcrSdk(CreateAndroidInstancesAccessTokenResponse serverResponse) {
        TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
        config.type = TcrSdk.SdkType.CloudPhone;
        config.ctx = this;
        // 用于接收 SDK 日志的对象，App 需要将收到的 SDK 日志存储到文件和上报，以便分析定位 SDK 运行时的问题。
        // 如果App没有显示设置 logger，SDK 会将日志打印到系统 logcat 以及写入日志文件（/storage/emulated/0/Android/data/app package name/files/tcrlogs 目录下）。
        // 注意，当你反馈 SDK 问题时，你需要提供相应的 SDK 日志。
        // config.logger = ...
        config.callback = new AsyncCallback<>() {
            @Override
            public void onSuccess(Void result) {
                Log.i(TAG, "init TcrSdk success");
                runOnUiThread(() -> {
                    initTcrSdkSuccess = true;
                    tryEnableDemo();
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(() -> {
                    String errorMsg = "初始化TcrSdk失败：" + code + " msg:" + msg;
                    Log.e(TAG, errorMsg);
                    textView.setText(errorMsg);
                });
            }
        };
        // 访问信息AceessInfo和鉴权Token，在初始化SDK时不是必须的。可以后续再调用 TcrSdk.getInstance().setAccessToken() 进行设置。
        if (serverResponse != null) {
            config.accessInfo = serverResponse.AccessInfo;
            config.token = serverResponse.Token;
        }
        TcrSdk.getInstance().init(config);
    }
}