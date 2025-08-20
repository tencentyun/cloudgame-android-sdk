package com.tencent.tcr.sdk.demo.cloudphone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.demo.cloudphone.DemoApp;
import com.tencent.tcr.sdk.demo.cloudphone.R;
import com.tencent.tcr.sdk.demo.cloudphone.network.ExpServerResponse;
import com.tencent.tcr.sdk.demo.cloudphone.network.LoginRequest;
import com.tencent.tcr.sdk.demo.cloudphone.network.LoginResponse;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                // 执行登录请求
                AsyncCallback<ExpServerResponse<LoginResponse>> callback = new AsyncCallback<>() {
                    @Override
                    public void onSuccess(ExpServerResponse<LoginResponse> expServerResponse) {
                        if (expServerResponse.Error != null) {
                            showToast("login fail：" + expServerResponse.Error.Message);
                        } else {
                            showToast("login success");
                            startActivity(new Intent(LoginActivity.this, FunctionActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        showToast("login fail：" + errorMsg);
                    }
                };
                new LoginRequest(username, password, callback).execute(DemoApp.getQueue());
            }
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

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showToast("please input name");
            return false;
        }

        if (password.isEmpty()) {
            showToast("please input password");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}