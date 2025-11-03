package com.tencent.tcr.demo.cloudphone.sfu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.DemoApp;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.common.ui.InstanceListActivity;
import com.tencent.tcr.demo.cloudphone.sfu.network.CreateAndroidInstancesAccessTokenBaseRequest;
import com.tencent.tcr.demo.cloudphone.sfu.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrSdk;
import java.util.Arrays;
import java.util.List;

public class GetTokenActivity extends AppCompatActivity {

    private static final String TAG = "GetTokenActivity";

    private EditText etInstanceIds;
    private EditText etUserIp;
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String instanceIds = etInstanceIds.getText().toString().trim();
            String userIp = etUserIp.getText().toString().trim();
            if (instanceIds.isEmpty()) {
                Toast.makeText(GetTokenActivity.this, "请输入实例ID", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccessToken(instanceIds, userIp);// 调用创建访问令牌的逻辑
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_token);
        etInstanceIds = findViewById(R.id.etInstanceIds);
        etUserIp = findViewById(R.id.etUserIp);
        findViewById(R.id.btnConfirm).setOnClickListener(mClickListener);
    }


    // 请求云手机实例的访问信息AceessInfo和鉴权Token
    private void createAccessToken(String instanceIds, String userIp) {
        AsyncCallback<CreateAndroidInstancesAccessTokenResponse> createAndroidInstancesAccessTokenCallback = new AsyncCallback<>() {
            @Override
            public void onSuccess(CreateAndroidInstancesAccessTokenResponse response) {
                // 给 TcrSdk 设置访问信息和令牌
                Log.e(TAG, "CreateAndroidInstancesAccessToken Response AccessInfo=" + response.AccessInfo + " Token="
                        + response.Token);
                TcrSdk.getInstance().setAccessToken(response.AccessInfo, response.Token);

                // 跳转
                Intent intent = new Intent(GetTokenActivity.this, InstanceListActivity.class);
                intent.putExtra("INSTANCE_IDS", etInstanceIds.getText().toString().trim());
                intent.putExtra("IS_SFU", true);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken failed: " + code + ", " + errorMsg);
                Toast.makeText(GetTokenActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg,
                        Toast.LENGTH_LONG).show();
            }
        };

        List<String> instanceIdsList = Arrays.asList(instanceIds.split(","));
        new CreateAndroidInstancesAccessTokenBaseRequest(instanceIdsList, userIp,
                createAndroidInstancesAccessTokenCallback).execute(DemoApp.getQueue());
    }
}