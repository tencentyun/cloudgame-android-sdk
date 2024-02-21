package com.tencent.tcrdemo.gameplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.tencent.tcrdemo.R;

/**
 * 本地输入法界面:<br>
 * 这个Activity在底部展示一个输入框和发送按钮，其余其余显示透明内容.<br>
 * 任何一个Activity都可以通过{@link Activity#startActivityForResult(Intent, int)} 跳转过来，输入框可以显示Intent里带上的文本.<br>
 * 当点击'发送'按钮时会关闭该Activity, 并且通过{@link Activity#onActivityResult(int, int, Intent)}返回输入的文本内容.
 */
public class InputActivity extends Activity {
    private static final String TAG = "InputActivity";
    /** 传送和获取文本对应的Key **/
    public static final String INPUT_TEXT = "input_text";
    /** 输入文本的输入框 **/
    private EditText mInputEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "InputActivity.onCreate");
        setContentView(R.layout.activity_input);
        mInputEdt = findViewById(R.id.edit);
        setInputText();
    }

    private void setInputText() {
        String input = getIntent().getStringExtra(INPUT_TEXT);
        Log.i(TAG, "setInputText input:" + input);
        mInputEdt.setText(input);
    }

    public void finish(View v) {
        Log.i(TAG, "finish");
        finish();
    }

    public void send(View v) {
        String inputString = mInputEdt.getText().toString();
        Log.i(TAG, "send inputString:" + inputString);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(INPUT_TEXT, inputString);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "InputActivity.onNewIntent");
        setInputText();
    }
}
