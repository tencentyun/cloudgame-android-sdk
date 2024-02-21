package com.tencent.advancedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tencent.tcrdemo.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_main);
        Button RenderButton = findViewById(R.id.button2);
        RenderButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomRenderActivity.class);
            startActivity(intent);
        });
        Button DecodeButton = findViewById(R.id.button);
        DecodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MediaCodecActivity.class);
            startActivity(intent);
        });
    }

    private void initWindow() {
        // 全屏展示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
