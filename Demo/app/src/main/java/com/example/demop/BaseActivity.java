package com.example.demop;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demop.expirtationcode.CloudGameApi;
import com.example.demop.expirtationcode.bean.ExperienceCodeResp;
import com.google.gson.Gson;

public abstract class BaseActivity extends AppCompatActivity {
    // 云游体验后台交互接口
    private CloudGameApi mCloudGameApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
    }

    // 窗口全屏
    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 获取到服务端server session的回调
    public abstract void onStartExperience(String serverSession);

    // 开始体验: 获取服务端server session
    protected void startExperience(String clientSession) {
        Log.i(Constant.TAG, "start experience");
        mCloudGameApi = new CloudGameApi(this);
        mCloudGameApi.startExperience(clientSession, new CloudGameApi.IServerSessionListener() {
            @Override
            public void onSuccess(String result) {
                Log.i(Constant.TAG, result);
                ExperienceCodeResp resp = new Gson().fromJson(result, ExperienceCodeResp.class);
                if (resp.Code == 0) {
                    onStartExperience(resp.ServerSession);
                } else {
                    Toast.makeText(BaseActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String msg) {
                Log.i(Constant.TAG, msg);
            }
        });
    }
}
