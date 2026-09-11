package com.tencent.tcr.demo.cloudphone.common.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.common.util.SessionSettings;
import com.tencent.tcr.sdk.api.TcrSessionConfig.VideoCodecType;

/**
 * 会话参数设置页（可选进入）：配置串流会话使用的 TcrSessionConfig 参数。<br>
 * 当前支持的配置项：preferredCodec（视频编码 不设置/H264/H265），后续可按需扩展其它字段。<br>
 * "不设置"表示创建会话时不指定 preferredCodec，由 SDK 使用默认编解码策略；默认选中"不设置"。<br>
 * 参数仅记录在内存中（当次运行有效，不持久化），修改保存后立即生效，
 * {@link PlayActivity} 创建串流会话时读取。
 */
public class SessionSettingsActivity extends AppCompatActivity {

    private RadioGroup mCodecRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_settings);

        mCodecRadioGroup = findViewById(R.id.rg_video_codec);
        // 回显当前生效的编码，null 表示"不设置"
        VideoCodecType current = SessionSettings.getPreferredCodec();
        if (current == VideoCodecType.H264) {
            mCodecRadioGroup.check(R.id.rb_codec_h264);
        } else if (current == VideoCodecType.H265) {
            mCodecRadioGroup.check(R.id.rb_codec_h265);
        } else {
            mCodecRadioGroup.check(R.id.rb_codec_unset);
        }

        Button saveBtn = findViewById(R.id.btn_save_settings);
        saveBtn.setOnClickListener(v -> save());
    }

    // 保存参数并返回
    private void save() {
        // getSelectedCodec() 返回 null 表示"不设置"，同样作为合法值保存
        VideoCodecType codec = getSelectedCodec();
        SessionSettings.savePreferredCodec(codec);
        Toast.makeText(this, "已保存，串流时生效", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 获取当前选中的视频编码，"不设置"时返回 null
    private VideoCodecType getSelectedCodec() {
        int checkedId = mCodecRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_codec_h264) {
            return VideoCodecType.H264;
        } else if (checkedId == R.id.rb_codec_h265) {
            return VideoCodecType.H265;
        }
        return null;
    }
}
