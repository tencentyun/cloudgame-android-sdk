package com.tencent.tcr.sdk.demo.cloudstream.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.demo.cloudstream.DemoApp;
import com.tencent.tcr.sdk.demo.cloudstream.R;
import com.tencent.tcr.sdk.demo.cloudstream.network.CreateAndroidInstancesAccessTokenBaseRequest;
import com.tencent.tcr.sdk.demo.cloudstream.network.CreateAndroidInstancesAccessTokenResponse;

import java.util.Arrays;
import java.util.List;

public class InstanceTokenActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

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
        setContentView(R.layout.activity_instance_token);
        initViews();
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
                    Intent intent = new Intent(InstanceTokenActivity.this, InstanceScreenshotActivity.class);
                    intent.putExtra("INSTANCE_IDS", etInstanceIds.getText().toString().trim());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(() -> {
                    String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
                    Toast.makeText(InstanceTokenActivity.this, "初始化TcrSdk失败: " + code + ", " + errorMsg,
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        };
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
                Toast.makeText(InstanceTokenActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg,
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