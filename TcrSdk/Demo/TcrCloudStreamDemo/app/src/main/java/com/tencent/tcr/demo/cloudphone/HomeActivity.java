package com.tencent.tcr.demo.cloudphone;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.operator.ui.LoginActivity;
import com.tencent.tcr.demo.cloudphone.sfu.ui.GetTokenActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "com.tencent.tcr.demo.HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.sfuDemo).setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, GetTokenActivity.class)));
        findViewById(R.id.operatorDemo).setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, LoginActivity.class)));

    }

}