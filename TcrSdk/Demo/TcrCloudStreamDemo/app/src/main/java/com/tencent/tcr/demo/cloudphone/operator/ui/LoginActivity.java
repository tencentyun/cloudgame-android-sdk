package com.tencent.tcr.demo.cloudphone.operator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.BuildConfig;
import com.tencent.tcr.demo.cloudphone.DemoApp;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.common.ui.InstanceListActivity;
import com.tencent.tcr.demo.cloudphone.operator.network.CreateAndroidInstancesAccessTokenRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.DescribeAndroidInstancesResponse.AndroidInstanceData;
import com.tencent.tcr.demo.cloudphone.operator.network.ExpServerResponse;
import com.tencent.tcr.demo.cloudphone.operator.network.LoginRequest;
import com.tencent.tcr.demo.cloudphone.operator.network.LoginResponse;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ArrayList<String> mInstanceIds;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etToken;
    private EditText etAccessInfo;
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (username.isEmpty()) {
                showToast("请输入用户名");
                return;
            }
            if (password.isEmpty()) {
                showToast("请输入密码");
                return;
            }

            // 执行登录请求
            AsyncCallback<ExpServerResponse<LoginResponse>> callback = new AsyncCallback<>() {
                @Override
                public void onSuccess(ExpServerResponse<LoginResponse> expServerResponse) {
                    if (expServerResponse.Error != null) {
                        showToast("登录失败：" + expServerResponse.Error.Message);
                    } else {
                        requestAccessToken();
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    showToast("登录失败：" + errorMsg);
                }
            };
            new LoginRequest(username, password, callback).execute(DemoApp.getQueue());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etUsername.setText(BuildConfig.paas_login_name);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setText(BuildConfig.paas_login_password);
        etToken = findViewById(R.id.etToken);
        etAccessInfo = findViewById(R.id.etAccessInfo);
        findViewById(R.id.btnLogin).setOnClickListener(mClickListener);
        findViewById(R.id.btnTokenLogin).setOnClickListener(v -> handleTokenLogin());
    }

    private void handleTokenLogin() {
        String token = etToken.getText().toString().trim();
        String accessInfo = etAccessInfo.getText().toString().trim();
        if (token.isEmpty()) {
            showToast("请输入 Token");
            return;
        }
        if (accessInfo.isEmpty()) {
            showToast("请输入 AccessInfo");
            return;
        }
        ArrayList<String> instanceIds = parseInstanceIdsFromAccessInfo(accessInfo);
        if (instanceIds.isEmpty()) {
            showToast("无法从 AccessInfo 中解析实例 ID");
            return;
        }
        TcrSdk.getInstance().setAccessToken(accessInfo, token);
        mInstanceIds = instanceIds;
        startInstanceListActivity(instanceIds);
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startInstanceListActivity(ArrayList<String> instanceIds) {
        if (instanceIds == null || instanceIds.isEmpty()) {
            showToast("暂无可用实例");
            return;
        }
        Intent intent = new Intent(LoginActivity.this, InstanceListActivity.class);
        intent.putExtra("INSTANCE_IDS", TextUtils.join(",", instanceIds));
        intent.putExtra("IS_SFU", false);
        startActivity(intent);
        finish();
    }

    private ArrayList<String> parseInstanceIdsFromAccessInfo(String accessInfo) {
        ArrayList<String> instanceIds = new ArrayList<>();
        if (TextUtils.isEmpty(accessInfo)) {
            return instanceIds;
        }
        String jsonPayload = accessInfo.trim();
        try {
            if (!jsonPayload.startsWith("{")) {
                byte[] decoded = Base64.decode(accessInfo, Base64.DEFAULT);
                jsonPayload = new String(decoded, StandardCharsets.UTF_8);
            }
            JSONObject root = new JSONObject(jsonPayload);
            JSONArray accessArray = root.optJSONArray("AccessInfo");
            if (accessArray == null) {
                return instanceIds;
            }
            for (int i = 0; i < accessArray.length(); i++) {
                JSONObject zoneInfo = accessArray.optJSONObject(i);
                if (zoneInfo == null) {
                    continue;
                }
                JSONArray idsArray = zoneInfo.optJSONArray("InstanceIds");
                if (idsArray == null) {
                    continue;
                }
                for (int j = 0; j < idsArray.length(); j++) {
                    String id = idsArray.optString(j);
                    if (!TextUtils.isEmpty(id) && !instanceIds.contains(id)) {
                        instanceIds.add(id);
                    }
                }
            }
        } catch (IllegalArgumentException | JSONException e) {
            Log.e(TAG, "解析 AccessInfo 失败: " + e.getMessage());
        }
        return instanceIds;
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

                // 给 TcrSdk 设置访问信息和令牌
                Log.d(TAG, "CreateAndroidInstancesAccessToken Response AccessInfo="
                        + expServerResponse.Response.AccessInfo + " Token=" + expServerResponse.Response.Token);
                TcrSdk.getInstance()
                        .setAccessToken(expServerResponse.Response.AccessInfo, expServerResponse.Response.Token);

                // 跳转
                startInstanceListActivity(mInstanceIds);
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
}