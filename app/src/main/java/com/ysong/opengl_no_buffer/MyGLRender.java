package com.ysong.opengl_no_buffer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.ysong.opengl_no_buffer.Object3D.Object3D;
import com.ysong.opengl_no_buffer.Object3D.Prism;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRender implements GLSurfaceView.Renderer {

	private float[] color = {1, 0.0f, 1.0f, 0.3f};

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private Prism mPrism;

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		final String vertexShaderCode =
				"uniform mat4 uMVPMatrix;"
						+ "attribute vec4 aPosition;"
						+ "void main() {"
						+ "gl_Position = uMVPMatrix * aPosition;"
						+ "}";

		final String fragmentShaderCode =
				"precision mediump float;"
						+ "uniform vec4 uColor;"
						+ "void main() {"
						+ "gl_FragColor = uColor;"
						+ "}";
		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
		GLES20.glCompileShader(vertexShaderHandle);
		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
		GLES20.glCompileShader(fragmentShaderHandle);
		int programHandle = GLES20.glCreateProgram();
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		GLES20.glLinkProgram(programHandle);
		GLES20.glUseProgram(programHandle);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		int mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
		int mPositionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition");
		int mColorHandle = GLES20.glGetUniformLocation(programHandle, "uColor");

		Object3D.init(mMVPMatrixHandle, mPositionHandle, mColorHandle);
		mPrism = new Prism(6, 0.5f, 1.0f, color);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		long time = SystemClock.uptimeMillis() % 8000L;
		float angle = 0.045f * ((int) time);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
		Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		mPrism.render(mMVPMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		Matrix.frustumM(mProjectionMatrix, 0, -1.0f, 1.0f, -1/ratio, 1/ratio, 4.0f, 10.0f);
	}
}
