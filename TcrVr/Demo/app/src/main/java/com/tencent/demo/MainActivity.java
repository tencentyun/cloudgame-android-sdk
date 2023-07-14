package com.tencent.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initPermissions();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        Button connectToCloudXRButton = findViewById(R.id.connect_to_cloudxr_button);
        connectToCloudXRButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext());
                String experienceCode = sharedPreferences.getString(
                        getResources().getString(R.string.pref_key_experienceCode), null);
                if (experienceCode == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "请输入体验码", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                } else {
                    start(experienceCode);
                }
            }
        });
    }

    private void initPermissions() {
        String[] permissions = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "需要SD卡读写权限以处理渲染资源", 0, permissions);
        }
    }

    private void start(String experienceCode) {
        if (TextUtils.isEmpty(experienceCode)) {
            Toast.makeText(this, "请输入体验码", Toast.LENGTH_LONG).show();
            return;
        }

        if ("oculus".equals(BuildConfig.FLAVOR)) {
            startForOculus(experienceCode);
        } else if ("pico".equals(BuildConfig.FLAVOR)) {
            startForPico(experienceCode);
        }
    }

    private void startForOculus(String experienceCode) {
        new Handler().postDelayed(() -> {
            Log.i(TAG, "run()");
            Intent intent = new Intent(MainActivity.this, XrActivity.class);
            intent.putExtra(XrActivity.KEY_EXPERIENCE_CODE, experienceCode);
            startActivity(intent);

        }, 1000);
        finish();
    }

    private void startForPico(String experienceCode) {
        Intent intent = new Intent(this, XrActivity.class);
        intent.putExtra(XrActivity.KEY_EXPERIENCE_CODE, experienceCode);
        startActivity(intent);
        finish();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}
