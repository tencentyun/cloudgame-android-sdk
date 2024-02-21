package com.tencent.advancedemo.render;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import androidx.annotation.Nullable;

import java.nio.FloatBuffer;

public class GLDrawer {
    private static final String INPUT_VERTEX_COORDINATE_NAME = "in_pos";
    private static final String INPUT_TEXTURE_COORDINATE_NAME = "in_tc";
    private static final String TEXTURE_MATRIX_NAME = "tex_mat";
    private static final String DEFAULT_VERTEX_SHADER_STRING = "varying vec2 tc;\n"
            + "attribute vec4 in_pos;\n"
            + "attribute vec4 in_tc;\n"
            + "uniform mat4 tex_mat;\n"
            + "void main() {\n"
            + "  gl_Position = in_pos;\n"
            + "  tc = (tex_mat * in_tc).xy;\n"
            + "}\n";
    // Vertex coordinates in Normalized Device Coordinates, i.e. (-1, -1) is bottom-left and (1, 1)
    // is top-right.
    private static final FloatBuffer FULL_RECTANGLE_BUFFER = GlUtils.createFloatBuffer(new float[] {
            -1.0f, -1.0f, // Bottom left.
            1.0f, -1.0f, // Bottom right.
            -1.0f, 1.0f, // Top left.
            1.0f, 1.0f, // Top right.
    });
    // Texture coordinates - (0, 0) is bottom-left and (1, 1) is top-right.
    private static final FloatBuffer FULL_RECTANGLE_TEXTURE_BUFFER =
            GlUtils.createFloatBuffer(new float[] {
                    0.0f, 0.0f, // Bottom left.
                    1.0f, 0.0f, // Bottom right.
                    0.0f, 1.0f, // Top left.
                    1.0f, 1.0f, // Top right.
            });
    private final String genericFragmentSource;
    private final String vertexShader;
    @Nullable
    private ShaderType currentShaderType;
    @Nullable private GLShader currentShader;
    private int inPosLocation;
    private int inTcLocation;
    private int texMatrixLocation;
    public GLDrawer(String genericFragmentSource) {
        this(DEFAULT_VERTEX_SHADER_STRING, genericFragmentSource);
    }
    public GLDrawer(
            String vertexShader, String genericFragmentSource) {
        this.vertexShader = vertexShader;
        this.genericFragmentSource = genericFragmentSource;
    }

    static String createFragmentShaderString(String genericFragmentSource, ShaderType shaderType) {
        StringBuilder stringBuilder = new StringBuilder();
        if (shaderType == ShaderType.OES) {
            stringBuilder.append("#extension GL_OES_EGL_image_external : require\n");
        }
        stringBuilder.append("precision mediump float;\n");
        stringBuilder.append("varying vec2 tc;\n");

        if (shaderType == ShaderType.YUV) {
            stringBuilder.append("uniform sampler2D y_tex;\n");
            stringBuilder.append("uniform sampler2D u_tex;\n");
            stringBuilder.append("uniform sampler2D v_tex;\n");

            // Add separate function for sampling texture.
            // yuv_to_rgb_mat is inverse of the matrix defined in YuvConverter.
            stringBuilder.append("vec4 sample(vec2 p) {\n");
            stringBuilder.append("  float y = texture2D(y_tex, p).r * 1.16438;\n");
            stringBuilder.append("  float u = texture2D(u_tex, p).r;\n");
            stringBuilder.append("  float v = texture2D(v_tex, p).r;\n");
            stringBuilder.append("  return vec4(y + 1.59603 * v - 0.874202,\n");
            stringBuilder.append("    y - 0.391762 * u - 0.812968 * v + 0.531668,\n");
            stringBuilder.append("    y + 2.01723 * u - 1.08563, 1);\n");
            stringBuilder.append("}\n");
            stringBuilder.append(genericFragmentSource);
        } else {
            String samplerName = shaderType == ShaderType.OES ? "samplerExternalOES" : "sampler2D";
            stringBuilder.append("uniform ").append(samplerName).append(" tex;\n");

            // Update the sampling function in-place.
            stringBuilder.append(genericFragmentSource.replace("sample(", "texture2D(tex, "));
        }

        return stringBuilder.toString();
    }

    // Visible for testing.
    GLShader createShader(ShaderType shaderType) {
        return new GLShader(
                vertexShader, createFragmentShaderString(genericFragmentSource, shaderType));
    }

    /**
     * Draw an OES texture frame with specified texture transformation matrix. Required resources are
     * allocated at the first call to this function.
     */
    public void drawTexture(int oesTextureId, float[] texMatrix, int frameWidth, int frameHeight,
                            int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        prepareShader(
                ShaderType.OES, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
        // Bind the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId);
        // Draw the texture.
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        // Unbind the texture as a precaution.
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    /**
     * Draw a YUV frame with specified texture transformation matrix. Required resources are allocated
     * at the first call to this function.
     */
    public void drawYuv(int[] yuvTextures, float[] texMatrix, int frameWidth, int frameHeight,
                        int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        prepareShader(
                ShaderType.YUV, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
        // Bind the textures.
        for (int i = 0; i < 3; ++i) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[i]);
        }
        // Draw the textures.
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        // Unbind the textures as a precaution.
        for (int i = 0; i < 3; ++i) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    private void prepareShader(ShaderType shaderType, float[] texMatrix, int frameWidth,
                               int frameHeight, int viewportWidth, int viewportHeight) {
        GLShader shader;
        // Same shader type as before, reuse exising shader.
        if (shaderType.equals(currentShaderType)) {
            shader = currentShader;
        } else {
            // Allocate new shader.
            currentShaderType = null;
            if (currentShader != null) {
                currentShader.release();
                currentShader = null;
            }

            shader = createShader(shaderType);
            currentShaderType = shaderType;
            currentShader = shader;

            shader.useProgram();
            // Set input texture units.
            if (shaderType == ShaderType.YUV) {
                GLES20.glUniform1i(shader.getUniformLocation("y_tex"), 0);
                GLES20.glUniform1i(shader.getUniformLocation("u_tex"), 1);
                GLES20.glUniform1i(shader.getUniformLocation("v_tex"), 2);
            } else {
                GLES20.glUniform1i(shader.getUniformLocation("tex"), 0);
            }

            GlUtils.checkNoGLES2Error("Create shader");
            texMatrixLocation = shader.getUniformLocation(TEXTURE_MATRIX_NAME);
            inPosLocation = shader.getAttribLocation(INPUT_VERTEX_COORDINATE_NAME);
            inTcLocation = shader.getAttribLocation(INPUT_TEXTURE_COORDINATE_NAME);
        }

        shader.useProgram();

        // Upload the vertex coordinates.
        GLES20.glEnableVertexAttribArray(inPosLocation);
        GLES20.glVertexAttribPointer(inPosLocation, /* size= */ 2,
                /* type= */ GLES20.GL_FLOAT, /* normalized= */ false, /* stride= */ 0,
                FULL_RECTANGLE_BUFFER);

        // Upload the texture coordinates.
        GLES20.glEnableVertexAttribArray(inTcLocation);
        GLES20.glVertexAttribPointer(inTcLocation, /* size= */ 2,
                /* type= */ GLES20.GL_FLOAT, /* normalized= */ false, /* stride= */ 0,
                FULL_RECTANGLE_TEXTURE_BUFFER);

        // Upload the texture transformation matrix.
        GLES20.glUniformMatrix4fv(
                texMatrixLocation, 1 /* count= */, false /* transpose= */, texMatrix, 0 /* offset= */);

        // Do custom per-frame shader preparation.
        GlUtils.checkNoGLES2Error("Prepare shader");
    }

    /**
     * Release all GLES resources. This needs to be done manually, otherwise the resources are leaked.
     */
    public void release() {
        if (currentShader != null) {
            currentShader.release();
            currentShader = null;
            currentShaderType = null;
        }
    }

    /**
     * The different shader types representing different input sources. YUV here represents three
     * separate Y, U, V textures.
     */
    public static enum ShaderType { OES, YUV }
}
