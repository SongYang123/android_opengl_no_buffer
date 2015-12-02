package com.ysong.opengl_no_buffer.Object3D;

public abstract class Object3D {
	protected static final int BYTE_PER_SHORT = 2;
	protected static final int BYTE_PER_FLOAT = 4;
	protected static final int COORD_PER_VERTEX = 3;

	protected static int mMVPMatrixHandle;
	protected static int mPositionHandle;
	protected static int mColorHandle;

	public static void init(int mvpMatrixHandle, int positionHandle, int colorHandle) {
		mMVPMatrixHandle = mvpMatrixHandle;
		mPositionHandle = positionHandle;
		mColorHandle = colorHandle;
	}

	public abstract void render(float[] mMVPMatrix);
}
