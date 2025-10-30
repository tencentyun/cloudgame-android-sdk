package com.tencent.tcrdemo.gameplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcrdemo.R;
import com.tencent.tcrdemo.utils.GameConfigParcelable;

public class ExperiencePageActivity extends AppCompatActivity implements ExperiencePageView.Listener {

    private ExperiencePageView mExperiencePageView;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_experience);
        mExperiencePageView = new ExperiencePageView(findViewById(R.id.login_main_page));
        mExperiencePageView.setListener(this);
        setupDefaultId();
    }

    @Override
    public void startGame(@Nullable String experienceCode, @Nullable String userId, @Nullable String hostUserId) {
        if (TextUtils.isEmpty(experienceCode)) {
            Toast.makeText(this, "请输入体验码", Toast.LENGTH_SHORT).show();
            return;
        }
        saveConfig(experienceCode, userId, hostUserId);
        Intent intent = new Intent(ExperiencePageActivity.this, GamePlayActivity.class);
        Parcelable gameParcelable = new GameConfigParcelable(experienceCode,
                mExperiencePageView.isTestEnv(), userId, hostUserId,
                mExperiencePageView.isRolePlayer(), mExperiencePageView.isIntlEnv());
        intent.putExtra(GameConfigParcelable.GAME_CONFIG_KEY, gameParcelable);
        startActivityForResult(intent, 1);
    }

    private void saveConfig(String experienceCode, String userId, String hostUserId) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (!TextUtils.isEmpty(experienceCode)) {
            editor.putString(getResources().getString(R.string.key_experience_code), experienceCode);
        }
        if (!TextUtils.isEmpty(userId)) {
            editor.putString(getResources().getString(R.string.key_set_userid), userId);
        }
        if (!TextUtils.isEmpty(hostUserId)) {
            editor.putString(getResources().getString(R.string.key_set_host_userid), hostUserId);
        }
        editor.putInt(getResources().getString(R.string.key_index_of_test_env_opt), mExperiencePageView.getTestEnvIndex());
        editor.putInt(getResources().getString(R.string.key_index_of_intl_env_opt), mExperiencePageView.getIntlEnvIndex());
        editor.putInt(getResources().getString(R.string.key_index_of_role_opt), mExperiencePageView.isRolePlayer() ? 1 : 0);
        editor.apply();
    }

    private void setupDefaultId() {
        String experienceCode = mPrefs.getString(getResources().getString(R.string.key_experience_code), "");
        if (experienceCode.isEmpty()) {
            experienceCode = "WR2416S6";
        }
        String userID = mPrefs.getString(getResources().getString(R.string.key_set_userid), "");
        String hostUserId = mPrefs.getString(getResources().getString(R.string.key_set_host_userid), "");
        mExperiencePageView.setExperienceCode(experienceCode);
        mExperiencePageView.setTestEnvIndex(mPrefs.getInt(getResources().getString(R.string.key_index_of_test_env_opt), 0));
        mExperiencePageView.setIntlEnvIndex(mPrefs.getInt(getResources().getString(R.string.key_index_of_intl_env_opt), 0));
        mExperiencePageView.setUserID(userID);
        mExperiencePageView.setHostUserId(hostUserId);
        mExperiencePageView
                .setEnableRolePlayer(mPrefs.getInt(getResources().getString(R.string.key_index_of_role_opt), 1) == 1);
    }

}
