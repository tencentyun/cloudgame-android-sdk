package com.tencent.tcr.demo.cloudphone.common.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

/**
 * 对话框工具类
 */
public class DialogUtils {

    /**
     * 双输入框对话框的回调接口
     */
    public interface OnDoubleInputConfirmListener {
        /**
         * 确认按钮点击回调
         * @param firstValue 第一个输入框的值
         * @param secondValue 第二个输入框的值
         */
        void onConfirm(int firstValue, int secondValue);
    }

    /**
     * 显示双输入框对话框
     * @param context 上下文
     * @param title 对话框标题
     * @param message 对话框提示信息
     * @param firstHint 第一个输入框的提示文本
     * @param secondHint 第二个输入框的提示文本
     * @param firstDefaultValue 第一个输入框的默认值
     * @param secondDefaultValue 第二个输入框的默认值
     * @param listener 确认按钮点击监听器
     */
    public static void showDoubleInputDialog(Context context,
                                            String title,
                                            String message,
                                            String firstHint,
                                            String secondHint,
                                            String firstDefaultValue,
                                            String secondDefaultValue,
                                            OnDoubleInputConfirmListener listener) {
        // 创建对话框布局
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // 创建第一个输入框
        final EditText firstInput = new EditText(context);
        firstInput.setHint(firstHint);
        firstInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        firstInput.setText(firstDefaultValue);
        firstInput.setGravity(Gravity.CENTER);
        layout.addView(firstInput);

        // 创建第二个输入框
        final EditText secondInput = new EditText(context);
        secondInput.setHint(secondHint);
        secondInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        secondInput.setText(secondDefaultValue);
        secondInput.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        secondInput.setLayoutParams(params);
        layout.addView(secondInput);

        // 创建对话框
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(layout)
                .setPositiveButton("确认", (dialog, which) -> {
                    String firstStr = firstInput.getText().toString().trim();
                    String secondStr = secondInput.getText().toString().trim();

                    // 验证输入是否为空
                    if (TextUtils.isEmpty(firstStr) || TextUtils.isEmpty(secondStr)) {
                        Toast.makeText(context, "输入不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int firstValue = Integer.parseInt(firstStr);
                        int secondValue = Integer.parseInt(secondStr);

                        // 验证输入是否为正数
                        if (firstValue <= 0 || secondValue <= 0) {
                            Toast.makeText(context, "输入必须大于0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 回调监听器
                        if (listener != null) {
                            listener.onConfirm(firstValue, secondValue);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "请输入有效的数字", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}