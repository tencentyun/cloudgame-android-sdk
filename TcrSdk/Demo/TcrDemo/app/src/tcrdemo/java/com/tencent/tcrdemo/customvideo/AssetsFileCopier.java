package com.tencent.tcrdemo.customvideo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsFileCopier {

    private static final String TAG = "AssetsFileCopier";

    /**
     * 将 Assets 中的文件拷贝到应用外部存储目录
     *
     * @param context 上下文对象
     * @param assetFileName Assets 中的文件名（如 "test.h264"）
     * @return 拷贝后的文件对象，如果失败返回 null
     */
    public static File copyAssetToExternalFilesDir(Context context, String assetFileName) {
        // 1. 检查外部存储是否可用
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "External storage not available");
            return null;
        }

        // 2. 获取目标目录
        File targetDir = context.getExternalFilesDir(null);
        if (targetDir == null) {
            Log.e(TAG, "Failed to get external files directory");
            return null;
        }

        // 3. 创建目标文件
        File targetFile = new File(targetDir, assetFileName);

        // 4. 如果文件已存在，检查是否需要覆盖
        if (targetFile.exists()) {
            // 这里可以根据需要决定是否覆盖
            Log.w(TAG, "File already exists: " + targetFile.getAbsolutePath());
            return targetFile;
        }

        // 5. 执行拷贝
        try (InputStream is = context.getAssets().open(assetFileName);
                OutputStream os = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            Log.i(TAG, "File copied successfully: " + targetFile.getAbsolutePath());
            return targetFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + assetFileName, e);
            // 删除可能已创建的部分文件
            if (targetFile.exists()) {
                targetFile.delete();
            }
            return null;
        }
    }

    /**
     * 检查外部存储是否可写
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 检查文件是否存在于外部存储
     */
    public static boolean isFileExistsInExternalFilesDir(Context context, String fileName) {
        File targetDir = context.getExternalFilesDir(null);
        if (targetDir == null) {
            return false;
        }

        File targetFile = new File(targetDir, fileName);
        return targetFile.exists();
    }
}