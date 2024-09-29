package com.tencent.tcrdemo.gameplay;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcrdemo.R;
import com.tencent.tcrdemo.ViewModelHolder;
import com.tencent.tcrdemo.utils.ActivityUtils;
import com.tencent.tcrdemo.utils.DownloadManagerHelper;
import com.tencent.tcrdemo.utils.GameConfigParcelable;


public class GamePlayActivity extends AppCompatActivity {
    private static final String TAG = "GamePlayActivity";
    private static final String VIEW_MODEL_TAG = "GAME_PLAY_VIEW_MODEL";
    GamePlayViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        initWindow();
        setContentView(R.layout.activity_game_play);
        DownloadManagerHelper.getInstance().setContext(this);
        mViewModel = findOrCreateViewModel();
        GamePlayFragment gamePlayFragment = findOrCreateViewFragment();
        gamePlayFragment.setViewModel(mViewModel);

        // 获取体验页传入的参数并丢给fragment
        GameConfigParcelable parcelable = getIntent().getParcelableExtra(GameConfigParcelable.GAME_CONFIG_KEY);
        if (TextUtils.isEmpty(parcelable.userId)) {
            gamePlayFragment.setUserId(parcelable.userId);
        }
        gamePlayFragment.setHostUserId(parcelable.hostUserId);
        gamePlayFragment.setTestEnv(parcelable.isTestEnv);
        gamePlayFragment.setRole(parcelable.isEnablePlayer);
        gamePlayFragment.setExperienceCode(parcelable.experienceCode);
    }

    private void initWindow() {
        // 全屏展示
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private GamePlayFragment findOrCreateViewFragment() {
        GamePlayFragment gamePlayFragment =
                (GamePlayFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (gamePlayFragment == null) {
            gamePlayFragment = GamePlayFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), gamePlayFragment, R.id.content);
        }
        return gamePlayFragment;
    }

    private GamePlayViewModel findOrCreateViewModel() {
        @SuppressWarnings("unchecked") ViewModelHolder<GamePlayViewModel> viewModelHolder =
                (ViewModelHolder<GamePlayViewModel>) getSupportFragmentManager().findFragmentByTag(VIEW_MODEL_TAG);

        if (viewModelHolder != null && viewModelHolder.getViewModel() != null) {
            return viewModelHolder.getViewModel();
        } else {
            GamePlayViewModel gamePlayViewModel = new GamePlayViewModel();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    ViewModelHolder.createContainer(gamePlayViewModel), VIEW_MODEL_TAG);
            return gamePlayViewModel;
        }
    }

    private long mBackFirst;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mBackFirst < 500) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "连按Back键退出", Toast.LENGTH_SHORT).show();
            mBackFirst = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}