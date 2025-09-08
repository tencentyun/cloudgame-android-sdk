package com.tencent.tcrdemo.customvideo;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.util.Log;
import com.tencent.tcrdemo.TcrDemoApplication;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.twebrtc.VideoFrame;

public class FrameDumper {

    private int frameCount = 0;

    public void increaseFrameCount() {
        frameCount++;
    }

    public void dumpTexture(int textureId, int textureType, int width, int height) {
        // 创建一个FBO，并将纹理附加到颜色附件上
        int[] framebuffers = new int[1];
        GLES30.glGenFramebuffers(1, framebuffers, 0);
        int fboId = framebuffers[0];
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, textureType, textureId, 0);

        // 从FBO的颜色附件中读取像素数据
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer);

        // 将像素数据转换为Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(pixelBuffer);

        // 将Bitmap对象保存为JPG文件
        String filePath =
                TcrDemoApplication.getContext().getExternalFilesDir("dumpTexture") + "/" + frameCount + ".jpg";
        saveBitmap(bitmap, new File(filePath));

        // 释放资源
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glDeleteFramebuffers(1, framebuffers, 0);
        bitmap.recycle();
    }

    public static ByteBuffer[] getI420(int textureId, int textureType, int width, int height) {
        // 创建一个FBO，并将纹理附加到颜色附件上
        int[] framebuffers = new int[1];
        GLES30.glGenFramebuffers(1, framebuffers, 0);
        int fboId = framebuffers[0];
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, textureType, textureId, 0);

        // 从FBO的颜色附件中读取像素数据
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer);

        ByteBuffer yBuffer = ByteBuffer.allocateDirect(width * height);
        ByteBuffer uBuffer = ByteBuffer.allocateDirect((width * height) / 4);
        ByteBuffer vBuffer = ByteBuffer.allocateDirect((width * height) / 4);

        // 将RGBA数据转换为I420格式
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width; j += 2) {
                // 计算翻转后的行索引（OpenGL坐标系 → 标准坐标系）
                int flippedRow1 = height - 1 - i;      // 当前行（在OpenGL中是底部行）
                int flippedRow2 = height - 1 - i - 1;  // 下一行（在OpenGL中是底部上一行）

                // 四个像素的偏移量（RGBA格式，每个像素4字节）
                int offset1 = (flippedRow1 * width + j) * 4;     // 左上
                int offset2 = (flippedRow1 * width + j + 1) * 4; // 右上
                int offset3 = (flippedRow2 * width + j) * 4;     // 左下
                int offset4 = (flippedRow2 * width + j + 1) * 4; // 右下

                // 处理2x2像素块
                int sumU = 0, sumV = 0;

                // 左上像素
                int[] yuv1 = getYUV(pixelBuffer, offset1);
                yBuffer.put(i * width + j, (byte) yuv1[0]);
                sumU += yuv1[1];
                sumV += yuv1[2];

                // 右上像素
                int[] yuv2 = getYUV(pixelBuffer, offset2);
                yBuffer.put(i * width + j + 1, (byte) yuv2[0]);
                sumU += yuv2[1];
                sumV += yuv2[2];

                // 左下像素
                int[] yuv3 = getYUV(pixelBuffer, offset3);
                yBuffer.put((i + 1) * width + j, (byte) yuv3[0]);
                sumU += yuv3[1];
                sumV += yuv3[2];

                // 右下像素
                int[] yuv4 = getYUV(pixelBuffer, offset4);
                yBuffer.put((i + 1) * width + j + 1, (byte) yuv4[0]);
                sumU += yuv4[1];
                sumV += yuv4[2];

                // 计算U/V平均值（2x2块共享一个UV值）
                int avgU = sumU / 4;
                int avgV = sumV / 4;
                int uvIndex = (i / 2) * (width / 2) + (j / 2);
                uBuffer.put(uvIndex, (byte) avgU);
                vBuffer.put(uvIndex, (byte) avgV);
            }
        }

        // 释放资源
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glDeleteFramebuffers(1, framebuffers, 0);

        return new ByteBuffer[]{yBuffer, uBuffer, vBuffer};
    }

    // RGB转YUV（使用整数运算优化）
    private static int[] getYUV(ByteBuffer pixelBuffer, int offset) {
        // 提取RGB分量（忽略Alpha）
        int r = pixelBuffer.get(offset) & 0xFF;
        int g = pixelBuffer.get(offset + 1) & 0xFF;
        int b = pixelBuffer.get(offset + 2) & 0xFF;

        // 使用整数运算计算YUV（ITU-R BT.601标准）
        int y = (77 * r + 150 * g + 29 * b) >> 8;
        int u = ((-43 * r - 85 * g + 128 * b) >> 8) + 128;
        int v = ((128 * r - 107 * g - 21 * b) >> 8) + 128;

        // 钳制值到[0,255]范围
        y = Math.max(0, Math.min(y, 255));
        u = Math.max(0, Math.min(u, 255));
        v = Math.max(0, Math.min(v, 255));

        return new int[]{y, u, v};
    }

    public static ByteBuffer[] getI420(int textureId, int textureType, int width, int height, float[] transformMatrix) {
        // 创建FBO并绑定纹理
        int[] framebuffers = new int[1];
        GLES30.glGenFramebuffers(1, framebuffers, 0);
        int fboId = framebuffers[0];
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, textureType, textureId, 0);

        // 读取像素数据
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        GLES30.glReadPixels(0, 0, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer);

        // 解析变换矩阵以确定旋转和镜像
        boolean flipHorizontal = (transformMatrix[0] < 0); // 水平镜像
        boolean flipVertical = (transformMatrix[5] < 0);   // 垂直镜像
        int rotation = getRotationFromMatrix(transformMatrix); // 获取旋转角度

        // 关键修复：创建行映射数组，考虑变换矩阵的垂直翻转
        int[] rowMap = new int[height];
        for (int i = 0; i < height; i++) {
            // 根据垂直翻转状态决定映射方式
            if (flipVertical) {
                // 如果变换矩阵已经垂直翻转，则使用正向映射
                rowMap[i] = i;
            } else {
                // 否则需要翻转行顺序
                rowMap[i] = height - 1 - i;
            }
        }

        // 根据旋转调整宽高
        boolean isRotated = (rotation == 90 || rotation == 270);
        int outputWidth = isRotated ? height : width;
        int outputHeight = isRotated ? width : height;

        // 创建YUV缓冲区
        ByteBuffer yBuffer = ByteBuffer.allocateDirect(width * height);
        ByteBuffer uBuffer = ByteBuffer.allocateDirect((width * height) / 4);
        ByteBuffer vBuffer = ByteBuffer.allocateDirect((width * height) / 4);

        // 处理像素块
        for (int i = 0; i < height; i += 2) {
            for (int j = 0; j < width; j += 2) {
                // 使用行映射获取正确物理行
                int physicalRow1 = rowMap[i];
                int physicalRow2 = rowMap[i + 1];

                int offset1 = (physicalRow1 * width + j) * 4;     // 左上
                int offset2 = (physicalRow1 * width + j + 1) * 4; // 右上
                int offset3 = (physicalRow2 * width + j) * 4;     // 左下
                int offset4 = (physicalRow2 * width + j + 1) * 4; // 右下

                // 处理像素块
                int sumU = 0, sumV = 0;

                int[] yuv1 = getYUV(pixelBuffer, offset1);
                yBuffer.put(i * width + j, (byte) yuv1[0]);
                sumU += yuv1[1];
                sumV += yuv1[2];

                int[] yuv2 = getYUV(pixelBuffer, offset2);
                yBuffer.put(i * width + j + 1, (byte) yuv2[0]);
                sumU += yuv2[1];
                sumV += yuv2[2];

                int[] yuv3 = getYUV(pixelBuffer, offset3);
                yBuffer.put((i + 1) * width + j, (byte) yuv3[0]);
                sumU += yuv3[1];
                sumV += yuv3[2];

                int[] yuv4 = getYUV(pixelBuffer, offset4);
                yBuffer.put((i + 1) * width + j + 1, (byte) yuv4[0]);
                sumU += yuv4[1];
                sumV += yuv4[2];

                int avgU = sumU / 4;
                int avgV = sumV / 4;
                int uvIndex = (i / 2) * (width / 2) + (j / 2);
                uBuffer.put(uvIndex, (byte) avgU);
                vBuffer.put(uvIndex, (byte) avgV);
            }
        }

        // 释放资源
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glDeleteFramebuffers(1, framebuffers, 0);

        return new ByteBuffer[]{yBuffer, uBuffer, vBuffer};
    }

    // 从变换矩阵中提取旋转角度
    private static int getRotationFromMatrix(float[] matrix) {
        // 检查矩阵元素确定旋转
        if (matrix[0] == 0 && matrix[1] == -1 && matrix[4] == 1 && matrix[5] == 0) {
            return 90;
        } else if (matrix[0] == -1 && matrix[1] == 0 && matrix[4] == 0 && matrix[5] == -1) {
            return 180;
        } else if (matrix[0] == 0 && matrix[1] == 1 && matrix[4] == -1 && matrix[5] == 0) {
            return 270;
        }
        return 0; // 无旋转
    }

    /// ////////////////////////////////////////////////////////////////////////////

    public void dumpFrame(VideoFrame frame) {
        // 将 VideoFrame 转换为 Bitmap
        Bitmap bitmap = convertVideoFrameToBitmap(frame);

        // 保存为图片文件
        String filePath = TcrDemoApplication.getContext().getExternalFilesDir("dumpFrame") + "/" + frameCount + ".jpg";
        saveBitmap(bitmap, new File(filePath));
    }

    private static Bitmap convertVideoFrameToBitmap(VideoFrame frame) {
        VideoFrame.I420Buffer i420Buffer = frame.getBuffer().toI420();
        int width = i420Buffer.getWidth();
        int height = i420Buffer.getHeight();

        // 创建 ARGB_8888 Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] argb = new int[width * height];

        // 获取 YUV 数据
        ByteBuffer yBuffer = i420Buffer.getDataY();
        ByteBuffer uBuffer = i420Buffer.getDataU();
        ByteBuffer vBuffer = i420Buffer.getDataV();

        int yStride = i420Buffer.getStrideY();
        int uStride = i420Buffer.getStrideU();
        int vStride = i420Buffer.getStrideV();

        // 手动转换 YUV420P 到 ARGB
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取 YUV 值
                int yValue = yBuffer.get(y * yStride + x) & 0xFF;
                int uValue = uBuffer.get((y / 2) * uStride + (x / 2)) & 0xFF;
                int vValue = vBuffer.get((y / 2) * vStride + (x / 2)) & 0xFF;

                // YUV 到 RGB 转换公式
                int r = clamp((int) (yValue + 1.402 * (vValue - 128)));
                int g = clamp((int) (yValue - 0.344136 * (uValue - 128) - 0.714136 * (vValue - 128)));
                int b = clamp((int) (yValue + 1.772 * (uValue - 128)));

                // ARGB 格式 (0xAARRGGBB)
                argb[y * width + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        bitmap.setPixels(argb, 0, width, 0, 0, width, height);
        i420Buffer.release();
        return bitmap;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static void saveBitmap(Bitmap bitmap, File file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Log.d("FrameDumper", "Saved frame: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("FrameDumper", "Error saving frame", e);
        }
    }

    public void dumpFrame2(VideoFrame frame) {
        // 获取旋转角度
        int rotation = frame.getRotation();
        // 获取缓冲区并转换为I420
        VideoFrame.I420Buffer i420Buffer = frame.getBuffer().toI420();
        int width = i420Buffer.getWidth();
        int height = i420Buffer.getHeight();

        // 根据旋转调整宽高
        if (rotation == 90 || rotation == 270) {
            int temp = width;
            width = height;
            height = temp;
        }

        // 创建Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] argb = new int[width * height];

        // 获取YUV数据
        ByteBuffer yBuffer = i420Buffer.getDataY();
        ByteBuffer uBuffer = i420Buffer.getDataU();
        ByteBuffer vBuffer = i420Buffer.getDataV();

        int yStride = i420Buffer.getStrideY();
        int uStride = i420Buffer.getStrideU();
        int vStride = i420Buffer.getStrideV();

        // 使用WebRTC官方转换公式
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 根据旋转计算原始坐标
                int origX = x, origY = y;
                switch (rotation) {
                    case 90:
                        origX = y;
                        origY = height - 1 - x;
                        break;
                    case 180:
                        origX = width - 1 - x;
                        origY = height - 1 - y;
                        break;
                    case 270:
                        origX = width - 1 - y;
                        origY = x;
                        break;
                }

                // 获取Y值
                int yIdx = origY * yStride + origX;
                int yValue = (yBuffer.get(yIdx) & 0xFF);

                // 获取UV值（420采样)
                int uvX = origX / 2;
                int uvY = origY / 2;
                int uIdx = uvY * uStride + uvX;
                int vIdx = uvY * vStride + uvX;
                int uValue = (uBuffer.get(uIdx) & 0xFF) - 128;
                int vValue = (vBuffer.get(vIdx) & 0xFF) - 128;

                // WebRTC官方转换公式
                int r = (int) (yValue + 1.403 * vValue);
                int g = (int) (yValue - 0.344 * uValue - 0.714 * vValue);
                int b = (int) (yValue + 1.770 * uValue);

                // 限幅并组合ARGB
                r = clamp(r);
                g = clamp(g);
                b = clamp(b);
                argb[y * width + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        bitmap.setPixels(argb, 0, width, 0, 0, width, height);
        i420Buffer.release();

        // 保存图片
        String filePath = TcrDemoApplication.getContext().getExternalFilesDir("dumpFrame") + "/" + frameCount + ".jpg";
        saveBitmap(bitmap, new File(filePath));
        bitmap.recycle();
    }
}