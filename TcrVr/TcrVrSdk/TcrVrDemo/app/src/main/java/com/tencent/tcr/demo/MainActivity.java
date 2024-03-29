package com.tencent.tcr.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import com.tencent.component.utils.LogUtils;
import com.tencent.demo.BuildConfig;
import com.tencent.demo.R;
import java.util.Locale;
import java.util.Objects;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 这个Activity是程序的主入口。<br>
 * 用户在界面上输入体验码之后点击"启动程序"会直接执行{@link MainActivity#connect(View)}方法。<br>
 * 该方法会把体验码作为参数传递给XrActivity。
 */
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
    }

    public void connect(View view) {
        if (TextUtils.isEmpty(PreferenceMgr.getExperienceCode())) {
            Toast toast = Toast.makeText(getApplicationContext(), "请输入体验码", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            return;
        }

        if ("oculus".equals(BuildConfig.FLAVOR)) {
            startForOculus();
        } else if ("pico".equals(BuildConfig.FLAVOR)) {
            startForPico();
        }
    }

    private void initPermissions() {
        String[] permissions = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "需要SD卡读写权限以处理渲染资源", 0, permissions);
        }
    }

    // 在Oculus中, 从2D的Activity跳转到3D Activity需要延迟1s跳转, 否则在Oculus中第二个Activity也会展示成一个窗口。
    private void startForOculus() {
        LogUtils.i(TAG, "play game for oculus");
        new Handler().postDelayed(() -> {
            Log.i(TAG, "run()");
            startActivity(new Intent(MainActivity.this, XrActivity.class));
        }, 1000);
        finish();
    }

    private void startForPico() {
        LogUtils.i(TAG, "play game for pico");
        startActivity(new Intent(MainActivity.this, XrActivity.class));
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private final SharedPreferences.OnSharedPreferenceChangeListener mFFRSwitchChangeListener = (sharedPreferences, key) -> {
            if (key.equals(getResources().getString(R.string.pref_key_enable_ffr)) ||
                    key.equals(getResources().getString(R.string.pref_key_enable_fovPlus))) {
                LogUtils.i(TAG, "change option " + key + " to " + sharedPreferences.getBoolean(key, false));
                CloudGameApi.getInstance().stopGame();
                Toast.makeText(getContext(), "配置修改成功, 请等待云端重启实例(1分钟左右)", Toast.LENGTH_LONG).show();
            }
        };

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(mFFRSwitchChangeListener);

            // 在Pico设备中退出应用时不会触发Activity的onDestory()回调，因此我们在切换分辨率的时候回收掉云端的实例。
            // 因为云端实例没有重启的情况下分辨率参数不会变化，如果我们在过程中切换过分辨率会导致客户端和服务端的分辨率不一致。
            SeekBarPreference resSeekBarPreference = findPreference(getResources().getString(R.string.pref_key_resolution));
            final String RES_SUMMARY_FORMAT = "分辨率(宽度): %d";
            Objects.requireNonNull(resSeekBarPreference).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    // 这其实是服务端的逻辑，为了让文案展示的分辨率和服务端一致，这里我们展示文案之前也做一个64对齐
                    int align32Value = (Integer)newValue / 64 * 64;
                    resSeekBarPreference.setSummary(String.format(Locale.CHINESE, RES_SUMMARY_FORMAT, align32Value));
                    new Handler().post(() -> resSeekBarPreference.setValue(align32Value));
                    CloudGameApi.getInstance().stopGame();
                    return true;
                }
            });
            resSeekBarPreference.setSummary(String.format(Locale.CHINESE, RES_SUMMARY_FORMAT, resSeekBarPreference.getValue()));

            SeekBarPreference srSeekBarPreference = findPreference(getResources().getString(R.string.pref_key_sr_option));
            final String SR_SUMMARY_FORMAT = "超分倍数: %1.1fx";
            Objects.requireNonNull(srSeekBarPreference).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    srSeekBarPreference.setSummary(String.format(Locale.CHINESE, SR_SUMMARY_FORMAT, (Integer)newValue/10.0f));
                    return true;
                }
            });
            srSeekBarPreference.setSummary(String.format(Locale.CHINESE, SR_SUMMARY_FORMAT, srSeekBarPreference.getValue()/10.0f));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mFFRSwitchChangeListener);
        }
    }
}
