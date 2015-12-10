package com.ysong.opengl_no_buffer.Object3D;

import android.opengl.GLES20;

public abstract class Object3D {
	protected static final int BYTE_PER_SHORT = 2;
	protected static final int BYTE_PER_FLOAT = 4;
	protected static final int POSITION_SIZE = 3;
	protected static final int NORMAL_SIZE = 3;

	protected static int mMVPMatrixHandle;
	protected static int mMVMatrixHandle;
	protected static int mPositionHandle;
	protected static int mNormalHandle;
	protected static int mColorHandle;

	public static void init(int programHandle) {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition");
		mNormalHandle = GLES20.glGetAttribLocation(programHandle, "aNormal");
		mColorHandle = GLES20.glGetUniformLocation(programHandle, "uColor");
	}

	public abstract void render(float[] mMVPMatrix, float[] mMVMatrix);

	public abstract void release();
}
