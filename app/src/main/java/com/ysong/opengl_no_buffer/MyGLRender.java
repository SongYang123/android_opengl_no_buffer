package com.ysong.opengl_no_buffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.ysong.opengl_no_buffer.Object3D.Cylinder;
import com.ysong.opengl_no_buffer.Object3D.Object3D;
import com.ysong.opengl_no_buffer.Object3D.Prism;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRender implements GLSurfaceView.Renderer {

	private Context context;

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];

	private Prism mPrism;
	private Cylinder mCylinder;

	public MyGLRender(Context context) {
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vertexShaderHandle, loadShader(R.raw.vertex_shader));
		GLES20.glCompileShader(vertexShaderHandle);
		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fragmentShaderHandle, loadShader(R.raw.fragment_shader));
		GLES20.glCompileShader(fragmentShaderHandle);
		int programHandle = GLES20.glCreateProgram();
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		GLES20.glLinkProgram(programHandle);
		GLES20.glUseProgram(programHandle);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);

//		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		float[] lightPos = {0.0f, 0.0f, 0.0f};
		float[] color = {1.0f, 0.0f, 1.0f, 1.0f};
		int mLightPosHandle = GLES20.glGetUniformLocation(programHandle, "uLightPos");
		GLES20.glUniform3fv(mLightPosHandle, 1, lightPos, 0);
		Object3D.init(programHandle);
		mPrism = new Prism(8, 0.5f, 1.0f, color);
		mCylinder = new Cylinder(32, 0.25f, 2.0f, color);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		long time = SystemClock.uptimeMillis() % 8000L;
		float angle = 0.045f * ((int) time);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, angle, 1.0f, 1.0f, 1.0f);
		Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
		mPrism.render(mMVPMatrix, mMVMatrix);
		mCylinder.render(mMVPMatrix, mMVMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		Matrix.frustumM(mProjectionMatrix, 0, -1.0f, 1.0f, -1 / ratio, 1 / ratio, 4.0f, 10.0f);
	}

	public void release() {
		mPrism.release();
		mCylinder.release();
	}

	private String loadShader(int resourceId) {
		InputStream inputStream = context.getResources().openRawResource(resourceId);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder body = new StringBuilder();
		try {
			String nextLine;
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (Exception e) {
			return null;
		}
		return body.toString();
	}
}
