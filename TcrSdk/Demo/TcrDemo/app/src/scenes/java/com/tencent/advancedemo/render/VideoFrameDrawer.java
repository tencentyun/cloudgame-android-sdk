
package com.tencent.advancedemo.render;


import android.graphics.Matrix;
import android.graphics.Point;
import android.opengl.GLES20;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tencent.tcr.sdk.api.VideoFrame;

import java.nio.ByteBuffer;

public class VideoFrameDrawer {

    public static final String TAG = "VideoFrameDrawer";
    // These points are used to calculate the size of the part of the frame we are rendering.
    final static float[] srcPoints =
            new float[]{0f /* x0 */, 0f /* y0 */, 1f /* x1 */, 0f /* y1 */, 0f /* x2 */, 1f /* y2 */};
    private final float[] dstPoints = new float[6];
    private final Point renderSize = new Point();
    private final YuvUploader yuvUploader = new YuvUploader();
    private final Matrix renderMatrix = new Matrix();
    private int renderWidth;
    private int renderHeight;
    // This variable will only be used for checking reference equality and is used for caching I420
    // textures.
    @Nullable
    private VideoFrame lastI420Frame;

    public static void drawTexture(GLDrawer drawer, VideoFrame.TextureBuffer buffer,
                                   Matrix renderMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY,
                                   int viewportWidth, int viewportHeight) {
        Matrix finalMatrix = new Matrix(buffer.getTransformMatrix());
        finalMatrix.preConcat(renderMatrix);
        float[] finalGlMatrix = convertMatrixFromAndroidGraphicsMatrix(finalMatrix);
        drawer.drawTexture(buffer.getTextureID(), finalGlMatrix, frameWidth, frameHeight, viewportX,
                viewportY, viewportWidth, viewportHeight);
    }

    private static int distance(float x0, float y0, float x1, float y1) {
        return (int) Math.round(Math.hypot(x1 - x0, y1 - y0));
    }

    public static float[] convertMatrixFromAndroidGraphicsMatrix(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);

        // The android.graphics.Matrix looks like this:
        // [x1 y1 w1]
        // [x2 y2 w2]
        // [x3 y3 w3]
        // We want to contruct a matrix that looks like this:
        // [x1 y1  0 w1]
        // [x2 y2  0 w2]
        // [ 0  0  1  0]
        // [x3 y3  0 w3]
        // Since it is stored in column-major order, it looks like this:
        // [x1 x2 0 x3
        //  y1 y2 0 y3
        //   0  0 1  0
        //  w1 w2 0 w3]
        // clang-format off
        float[] matrix4x4 = {
                values[0 * 3 + 0], values[1 * 3 + 0], 0, values[2 * 3 + 0],
                values[0 * 3 + 1], values[1 * 3 + 1], 0, values[2 * 3 + 1],
                0, 0, 1, 0,
                values[0 * 3 + 2], values[1 * 3 + 2], 0, values[2 * 3 + 2],
        };
        // clang-format on
        return matrix4x4;
    }

    public static VideoFrame.Buffer prepareBufferForViewportSize(
            VideoFrame.Buffer buffer, int width, int height) {
        buffer.retain();
        return buffer;
    }

    // Calculate the frame size after `renderMatrix` is applied. Stores the output in member variables
    // `renderWidth` and `renderHeight` to avoid allocations since this function is called for every
    // frame.
    private void calculateTransformedRenderSize(
            int frameWidth, int frameHeight, @Nullable Matrix renderMatrix) {
        if (renderMatrix == null) {
            renderWidth = frameWidth;
            renderHeight = frameHeight;
            return;
        }
        // Transform the texture coordinates (in the range [0, 1]) according to `renderMatrix`.
        renderMatrix.mapPoints(dstPoints, srcPoints);

        // Multiply with the width and height to get the positions in terms of pixels.
        for (int i = 0; i < 3; ++i) {
            dstPoints[i * 2 + 0] *= frameWidth;
            dstPoints[i * 2 + 1] *= frameHeight;
        }

        // Get the length of the sides of the transformed rectangle in terms of pixels.
        renderWidth = distance(dstPoints[0], dstPoints[1], dstPoints[2], dstPoints[3]);
        renderHeight = distance(dstPoints[0], dstPoints[1], dstPoints[4], dstPoints[5]);
    }

    public void drawFrame(VideoFrame frame, GLDrawer drawer) {
        drawFrame(frame, drawer, null /* additionalRenderMatrix */);
    }

    public void drawFrame(
            VideoFrame frame, GLDrawer drawer, Matrix additionalRenderMatrix) {
        drawFrame(frame, drawer, additionalRenderMatrix, 0 /* viewportX */, 0 /* viewportY */,
                frame.getWidth(), frame.getHeight());
    }

    public void drawFrame(VideoFrame frame, GLDrawer drawer,
                          @Nullable Matrix additionalRenderMatrix, int viewportX, int viewportY, int viewportWidth,
                          int viewportHeight) {
        int width = frame.getWidth();
        int height = frame.getHeight();
        calculateTransformedRenderSize(width, height, additionalRenderMatrix);
        if (renderWidth <= 0 || renderHeight <= 0) {
            Log.w(TAG, "Illegal frame size: " + renderWidth + "x" + renderHeight);
            return;
        }

        boolean isTextureFrame = frame.getBuffer() instanceof VideoFrame.TextureBuffer;
        renderMatrix.reset();
        renderMatrix.preTranslate(0.5f, 0.5f);
        if (!isTextureFrame) {
            renderMatrix.preScale(1f, -1f); // I420-frames are upside down
        }
        renderMatrix.preRotate(frame.getRotation());
        renderMatrix.preTranslate(-0.5f, -0.5f);
        if (additionalRenderMatrix != null) {
            renderMatrix.preConcat(additionalRenderMatrix);
        }

        if (isTextureFrame) {
            lastI420Frame = null;
            drawTexture(drawer, (VideoFrame.TextureBuffer) frame.getBuffer(), renderMatrix, renderWidth,
                    renderHeight, viewportX, viewportY, viewportWidth, viewportHeight);
        } else {
            if (frame != lastI420Frame) {
                yuvUploader.uploadFromBuffer(frame);
            }
            drawer.drawYuv(yuvUploader.getYuvTextures(), convertMatrixFromAndroidGraphicsMatrix(renderMatrix),
                    renderWidth,
                    renderHeight, viewportX, viewportY, viewportWidth, viewportHeight);
        }
    }

    public void release() {
        yuvUploader.release();
        lastI420Frame = null;
    }

    /**
     * Helper class for uploading YUV bytebuffer frames to textures that handles stride > width. This
     * class keeps an internal ByteBuffer to avoid unnecessary allocations for intermediate copies.
     */
    private static class YuvUploader {

        // Intermediate copy buffer for uploading yuv frames that are not packed, i.e. stride > width.
        // that handles stride and compare performance with intermediate copy.
        @Nullable
        private ByteBuffer copyBuffer;
        @Nullable
        private int[] yuvTextures;

        /**
         * Upload `planes` into OpenGL textures, taking stride into consideration.
         *
         * @return Array of three texture indices corresponding to Y-, U-, and V-plane respectively.
         */
        @Nullable
        public int[] uploadYuvData(int width, int height, int[] strides, ByteBuffer[] planes) {
            int[] planeWidths = new int[]{width, width / 2, width / 2};
            int[] planeHeights = new int[]{height, height / 2, height / 2};
            // Make a first pass to see if we need a temporary copy buffer.
            int copyCapacityNeeded = 0;
            for (int i = 0; i < 3; ++i) {
                if (strides[i] > planeWidths[i]) {
                    copyCapacityNeeded = Math.max(copyCapacityNeeded, planeWidths[i] * planeHeights[i]);
                }
            }
            // Allocate copy buffer if necessary.
            if (copyCapacityNeeded > 0
                    && (copyBuffer == null || copyBuffer.capacity() < copyCapacityNeeded)) {
                copyBuffer = ByteBuffer.allocateDirect(copyCapacityNeeded);
            }
            // Make sure YUV textures are allocated.
            if (yuvTextures == null) {
                yuvTextures = new int[3];
                for (int i = 0; i < 3; i++) {
                    yuvTextures[i] = GlUtils.generateTexture(GLES20.GL_TEXTURE_2D);
                }
            }
            // Upload each plane.
            for (int i = 0; i < 3; ++i) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[i]);
                // GLES only accepts packed data, i.e. stride == planeWidth.
                ByteBuffer packedByteBuffer;
                // Input is packed already.
                packedByteBuffer = planes[i];
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, planeWidths[i],
                        planeHeights[i], 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, packedByteBuffer);
            }
            return yuvTextures;
        }

        @Nullable
        public int[] uploadFromBuffer(VideoFrame frame) {
            VideoFrame.I420Buffer buffer = frame.getBuffer().toI420();
            int[] strides = {buffer.getStrideY(), buffer.getStrideU(), buffer.getStrideV()};
            ByteBuffer[] planes = {buffer.getDataY(), buffer.getDataU(), buffer.getDataV()};
            return uploadYuvData(frame.getWidth(), frame.getHeight(), strides, planes);
        }

        @Nullable
        public int[] getYuvTextures() {
            return yuvTextures;
        }

        /**
         * Releases cached resources. Uploader can still be used and the resources will be reallocated
         * on first use.
         */
        public void release() {
            copyBuffer = null;
            if (yuvTextures != null) {
                GLES20.glDeleteTextures(3, yuvTextures, 0);
                yuvTextures = null;
            }
        }
    }
}
