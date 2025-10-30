package com.tencent.tcr.demo.cloudphone.sfu.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.DemoApp;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.sfu.network.CreateAndroidInstancesAccessTokenBaseRequest;
import com.tencent.tcr.demo.cloudphone.sfu.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import java.util.Arrays;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class GetTokenActivity extends AppCompatActivity {

    private static final String TAG = "GetTokenActivity";

    private EditText etInstanceIds;
    private EditText etUserIp;
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String instanceIds = etInstanceIds.getText().toString().trim();
            String userIp = etUserIp.getText().toString().trim();

            if (validateInput(instanceIds)) {
                // 调用创建访问令牌的逻辑
                createAccessToken(instanceIds, userIp);
            }
        }
    };
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_token);
        initViews();
        initPermissions();
    }

    private void initPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "测试包需要录音、摄像头、SD卡读写权限", 0, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initViews() {
        etInstanceIds = findViewById(R.id.etInstanceIds);
        etUserIp = findViewById(R.id.etUserIp);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(mClickListener);
    }

    private boolean validateInput(String instanceIds) {
        if (instanceIds.isEmpty()) {
            showToast("请输入实例ID");
            return false;
        }
        return true;
    }

    private void initTcrSdk(CreateAndroidInstancesAccessTokenResponse serverResponse) {
        TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
        config.type = TcrSdk.SdkType.CloudStream;
        config.ctx = this;
        // 用于接收 SDK 日志的对象，App 需要将收到的 SDK 日志存储到文件和上报，以便分析定位 SDK 运行时的问题。
        // 如果App没有显示设置 logger，SDK 会将日志打印到系统 logcat 以及写入日志文件（/storage/emulated/0/Android/data/app package name/files/tcrlogs 目录下）。
        // 注意，当你反馈 SDK 问题时，你需要提供相应的 SDK 日志。
        // config.logger = ...
        config.callback = new AsyncCallback<>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    Log.i(TAG, "init TcrSdk success");
                    Intent intent = new Intent(GetTokenActivity.this, InstanceListActivity.class);
                    intent.putExtra("INSTANCE_IDS", etInstanceIds.getText().toString().trim());
                    intent.putExtra("IS_SFU", true);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(() -> {
                    String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
                    Toast.makeText(GetTokenActivity.this, "初始化TcrSdk失败: " + code + ", " + errorMsg,
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        };
        // 访问信息AceessInfo和鉴权Token，在初始化SDK时不是必须的。可以后续再调用 TcrSdk.getInstance().setAccessToken() 进行设置。
        config.accessInfo = serverResponse.AccessInfo;
        config.token = serverResponse.Token;
        TcrSdk.getInstance().init(config);
    }

    // 请求云手机实例的访问信息AceessInfo和鉴权Token
    private void createAccessToken(String instanceIds, String userIp) {
        AsyncCallback<CreateAndroidInstancesAccessTokenResponse> createAndroidInstancesAccessTokenCallback = new AsyncCallback<>() {
            @Override
            public void onSuccess(CreateAndroidInstancesAccessTokenResponse response) {
                // 初始化 TcrSdk
                initTcrSdk(response);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken failed: " + code + ", " + errorMsg);
                Toast.makeText(GetTokenActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        };

        List<String> instanceIdsList = Arrays.asList(instanceIds.split(","));
        new CreateAndroidInstancesAccessTokenBaseRequest(instanceIdsList, userIp, createAndroidInstancesAccessTokenCallback).execute(DemoApp.getQueue());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}