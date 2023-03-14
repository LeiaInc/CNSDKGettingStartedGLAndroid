package com.leia.cnsdkgettingstartedgl;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
    // Shader program used to render the triangle.
    private int mProgram = 0;

    // Number of vertices per triangle.
    private final int vertexCount = 3;

    // Size of a vertex in bytes (xyz * 4 bytes)
    private final int vertexStride = 3 * 4;

    // Vertex buffer to store our triangle data.
    private FloatBuffer vertexBuffer = null;

    // Vertex shader for triangle.
    private final String vertexShaderCode =
        "attribute vec4 vPosition;" +
        "void main() {" +
        "    gl_Position = vPosition;" +
        "}";

    // Fragment shader for triangle.
    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "    gl_FragColor = vColor;" +
        "}";

    // Triangle vertices in counterclockwise order
    static float triangleCoords[] = {
        0.0f,  0.5f, 0.0f, // Top
       -0.5f, -0.5f, 0.0f, // Bottom-left
        0.5f, -0.5f, 0.0f  // Bottom-right
    };

    MainRenderer(Activity activity) {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Fill vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // Create shaders.
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Create program.
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        // Delete shaders that are no longer needed.
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear background with a solid value.
        GLES20.glClearColor(0,0.2f,0.4f,0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Get animated value for the triangle color.
        float green = (float)((System.currentTimeMillis() / 1000.0) % 1.0);
        float triangleColor[] = { 0.5f, green, 0.5f, 1.0f };

        // Render triangle.
        GLES20.glUseProgram(mProgram);
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, triangleColor, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    // Helper function to create a shader.
    private static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}