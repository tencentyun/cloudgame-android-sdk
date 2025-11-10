package com.tencent.tcrdemo.gameplay;


import android.content.Context;
import android.content.res.Resources;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.tencent.tcrdemo.R;

public class ExperiencePageView implements View.OnClickListener,  RadioGroup.OnCheckedChangeListener {

    private final RadioGroup mTestEnvRadioGroup;
    private final RadioGroup mIntlEnvRadioGroup;
    private final RadioGroup mMultiUserRoleRadioGroup;
    private final Group mAdvanceGroup;

    private final View mExperienceLayout;
    private final ConstraintLayout mDialogContainer;
    private final EditText mExperienceCodeEdt;

    private final EditText mUserIdEdit;
    private final EditText mHostUserIdEdit;
    private final Context mContext;
    private Listener mListener;
    private final Button mStartBtn;

    /**
     * 启动游戏，回调用户输入的启动参数
     *
     * @param parent 存在输入UI的父视图
     */
    public ExperiencePageView(View parent) {
        mContext = parent.getContext();
        mExperienceLayout = parent.findViewById(R.id.experience_code_layout);
        mExperienceCodeEdt = parent.findViewById(R.id.experience_code);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mExperienceCodeEdt.getWindowToken(), 0);
        mUserIdEdit = parent.findViewById(R.id.user_id);
        mHostUserIdEdit = parent.findViewById(R.id.host_user_id);
        mDialogContainer = parent.findViewById(R.id.login_dialog_container);
        mAdvanceGroup = parent.findViewById(R.id.advance_layout);
        mAdvanceGroup.setVisibility(View.GONE);
        mTestEnvRadioGroup = parent.findViewById(R.id.test_env_radio_group);
        mIntlEnvRadioGroup = parent.findViewById(R.id.intl_env_radio_group);
        mMultiUserRoleRadioGroup = parent.findViewById(R.id.role_radio_group);
        RadioGroup mTabRadioGroup = parent.findViewById(R.id.tab_radio_group);
        mTabRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mDialogContainer.getLayoutParams();
            TransitionManager.beginDelayedTransition(mDialogContainer);
            if (checkedId == R.id.easy) {
                mExperienceLayout.setVisibility(View.VISIBLE);
                mAdvanceGroup.setVisibility(View.GONE);
                lp.dimensionRatio = "H,550:432";
                lp.topMargin = (int) dp2px(parent.getContext(), 60);
            } else if (checkedId == R.id.advanced) {
                mExperienceLayout.setVisibility(View.VISIBLE);
                mAdvanceGroup.setVisibility(View.VISIBLE);
                lp.dimensionRatio = "H,550:880";
                lp.topMargin = (int) dp2px(parent.getContext(), 5);
            }
            mDialogContainer.setLayoutParams(lp);
        });
        mStartBtn = parent.findViewById(R.id.start);
        mStartBtn.setOnClickListener(this);
    }


    public void setExperienceCode(String experienceCode) {
        mExperienceCodeEdt.setText(experienceCode);
    }

    public boolean isTestEnv() {
        return mTestEnvRadioGroup.getCheckedRadioButtonId() == R.id.is_test_env;
    }

    public boolean isIntlEnv() {
        return mIntlEnvRadioGroup.getCheckedRadioButtonId() == R.id.is_intl_env;
    }


    public boolean isRolePlayer() {
        return mMultiUserRoleRadioGroup.getCheckedRadioButtonId() == R.id.role_player;
    }

    public void setEnableRolePlayer(boolean enable) {
        mMultiUserRoleRadioGroup.check(enable ? R.id.role_player : R.id.role_viewer);
    }

    public void setUserID(String userID) {
        mUserIdEdit.setText(userID);
    }

    public void setHostUserId(String hostUserId) {
        mHostUserIdEdit.setText(hostUserId);
    }

    public int getTestEnvIndex() {
        return mTestEnvRadioGroup.getCheckedRadioButtonId() == R.id.is_not_test_env
                ? 0 : 1;
    }

    public void setTestEnvIndex(int index) {
        mTestEnvRadioGroup.check(index == 0 ? R.id.is_not_test_env : R.id.is_test_env);
    }

    public int getIntlEnvIndex() {
        return mIntlEnvRadioGroup.getCheckedRadioButtonId() == R.id.is_not_test_env
                ? 0 : 1;
    }

    public void setIntlEnvIndex(int index) {
        mIntlEnvRadioGroup.check(index == 0 ? R.id.is_not_test_env : R.id.is_test_env);
    }

    private float dp2px(Context context, float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    private void doStart() {
        String experienceCode = mExperienceCodeEdt.getText().toString().trim();
        String userID = mUserIdEdit.getText().toString().trim();
        String hostUserID = mHostUserIdEdit.getText().toString().trim();
        if (mListener != null) {
            mListener.startGame(experienceCode,
                    userID,
                    hostUserID);
        }
    }

    public void setListener(Listener litener) {
        mListener = litener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            doStart();
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

    }

    public interface Listener {

        void startGame(@Nullable String experienceCode,
                       @Nullable String userId,
                       @Nullable String hostUserId
        );
    }

    public void enableStartBtn(boolean enable){
        mStartBtn.setEnabled(enable);
    }
}