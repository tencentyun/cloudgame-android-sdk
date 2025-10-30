package com.tencent.tcr.demo.cloudphone.operator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.DemoApp;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.operator.network.CreateAndroidInstancesAccessTokenRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesResponse.AndroidInstanceData;
import com.tencent.tcr.demo.cloudphone.operator.network.ExpServerResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.LoginRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.LoginResponse;
import com.tencent.tcr.demo.cloudphone.sfu.ui.InstanceListActivity;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSdk.SdkType;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ArrayList<String> mInstanceIds;
    private EditText etUsername;
    private EditText etPassword;
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (username.isEmpty()) {
                showToast("please input name");
                return;
            }
            if (password.isEmpty()) {
                showToast("please input password");
                return;
            }

            // 执行登录请求
            AsyncCallback<ExpServerResponse<LoginResponse>> callback = new AsyncCallback<>() {
                @Override
                public void onSuccess(ExpServerResponse<LoginResponse> expServerResponse) {
                    if (expServerResponse.Error != null) {
                        showToast("login fail：" + expServerResponse.Error.Message);
                    } else {
                        //showToast("login success");
                        requestAccessToken();
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    showToast("login fail：" + errorMsg);
                }
            };
            new LoginRequest(username, password, callback).execute(DemoApp.getQueue());
        }

        private void showToast(String message) {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(mClickListener);
    }

    // 请求云手机实例的访问信息AceessInfo和鉴权Token
    private void requestAccessToken() {
        AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>> createAndroidInstancesAccessTokenCallback = new AsyncCallback<>() {
            @Override
            public void onSuccess(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
                if (expServerResponse.Error != null) {
                    Log.e(TAG, "CreateAndroidInstancesAccessToken expServerResponse.Error: " + expServerResponse.Error);
                    return;
                }
                if (expServerResponse.Response == null) {
                    Log.e(TAG, "CreateAndroidInstancesAccessToken Response: null");
                    return;
                }

                // 初始化 TcrSdk
                initTcrSdk(expServerResponse);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken failed: " + code + ", " + errorMsg);
                Toast.makeText(LoginActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg, Toast.LENGTH_LONG)
                        .show();
            }
        };
        AsyncCallback<ExpServerResponse<DescribeAndroidInstancesResponse>> describeAndroidInstancesCallback = new AsyncCallback<>() {
            @Override
            public void onSuccess(ExpServerResponse<DescribeAndroidInstancesResponse> expServerResponse) {
                if (expServerResponse.Error != null) {
                    Log.e(TAG, "DescribeAndroidInstances expServerResponse.Error: " + expServerResponse.Error);
                    return;
                }

                if (expServerResponse.Response == null || expServerResponse.Response.AndroidInstances == null) {
                    Log.e(TAG, "DescribeAndroidInstances Response or AndroidInstances is null");
                    return;
                }

                // 业务处理
                mInstanceIds = new ArrayList<>();
                for (AndroidInstanceData instance : expServerResponse.Response.AndroidInstances) {
                    //Log.v(TAG, instance.toString());
                    mInstanceIds.add(instance.AndroidInstanceId);
                }
                new CreateAndroidInstancesAccessTokenRequest(mInstanceIds,
                        createAndroidInstancesAccessTokenCallback).execute(DemoApp.getQueue());
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "DescribeAndroidInstances failed: " + code + ", " + errorMsg);
                Toast.makeText(LoginActivity.this, "获取实例列表失败: " + code + ", " + errorMsg, Toast.LENGTH_LONG)
                        .show();
            }
        };
        new DescribeAndroidInstancesRequest(describeAndroidInstancesCallback).execute(DemoApp.getQueue());
    }

    // 初始化TcrSdk
    private void initTcrSdk(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
        TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
        config.type = SdkType.CloudPhone;
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
                    Intent intent = new Intent(LoginActivity.this, InstanceListActivity.class);
                    intent.putExtra("INSTANCE_IDS", TextUtils.join(",", mInstanceIds));
                    intent.putExtra("IS_SFU", false);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(() -> {
                    String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
                    Toast.makeText(LoginActivity.this, "初始化TcrSdk失败: " + code + ", " + errorMsg, Toast.LENGTH_LONG)
                            .show();
                });
            }
        };
        config.accessInfo = expServerResponse.Response.AccessInfo;
        config.token = expServerResponse.Response.Token;
        TcrSdk.getInstance().init(config);
    }


}