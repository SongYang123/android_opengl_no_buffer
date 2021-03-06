package com.ysong.opengl_no_buffer.Object3D;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Prism extends Object3D {

	private FloatBuffer[] vertexBuffer = new FloatBuffer[3];
	private ShortBuffer[] indexBuffer = new ShortBuffer[3];
	private float[] color;

	public Prism(int n, float radius, float height, float[] color) {
		float[][] vertex = genVertex(n, radius, height);
		for (int i = 0; i < 3; i++) {
			vertexBuffer[i] = ByteBuffer.allocateDirect(BYTE_PER_FLOAT * vertex[i].length).order(ByteOrder.nativeOrder()).asFloatBuffer();
			vertexBuffer[i].put(vertex[i]);
		}

		short[][] index = genIndex(n);
		for (int i = 0; i < 3; i++) {
			indexBuffer[i] = ByteBuffer.allocateDirect(BYTE_PER_SHORT * index[i].length).order(ByteOrder.nativeOrder()).asShortBuffer();
			indexBuffer[i].put(index[i]);
		}

		this.color = color;
	}

	@Override
	public void render(float[] mMVPMatrix, float[] mMVMatrix) {
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glEnableVertexAttribArray(mNormalHandle);

		for (int i = 0; i < 2; i++) {
			vertexBuffer[i].position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer[i]);
			vertexBuffer[i].position(POSITION_SIZE);
			GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer[i]);
			indexBuffer[i].position(0);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indexBuffer[i].capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer[i]);
		}

		vertexBuffer[2].position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer[2]);
		vertexBuffer[2].position(POSITION_SIZE);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer[2]);
		for (int i = 0; i < indexBuffer[2].capacity(); i += 4) {
			indexBuffer[2].position(i);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 4, GLES20.GL_UNSIGNED_SHORT, indexBuffer[2]);
		}
	}

	@Override
	public void release() {
		for (int i = 0; i < 3; i++) {
			vertexBuffer[i].limit(0);
			vertexBuffer[i] = null;
			indexBuffer[i].limit(0);
			indexBuffer[i] = null;
		}
	}

	private float[][] genVertex(int n, float radius, float height) {
		float[][] vertex = new float[3][];
		vertex[0] = new float[n * 6 + 6];
		vertex[1] = new float[n * 6 + 6];
		vertex[2] = new float[n * 24];
		height /= 2;
		/* top and bottom */
		for (int i = 0; i < n; i++) {
			double angle = Math.PI * 2 * i / n;
			float x = radius * (float) Math.cos(angle);
			float y = radius * (float) Math.sin(angle);
			int i6 = i * 6;
			vertex[0][i6] = vertex[1][i6] = x;
			vertex[0][i6 + 1] = y;
			vertex[1][i6 + 1] = -y;

		}
		for (int i = 0; i < n + 1; i++) {
			int i6 = i * 6;
			vertex[0][i6 + 2] = height;
			vertex[0][i6 + 5] = 1.0f;
			vertex[1][i6 + 2] = -height;
			vertex[1][i6 + 5] = -1.0f;
		}
		/* side */
		for (int i = 0; i < n; i++) {
			double angle = Math.PI * 2 * i / n;
			float x = radius * (float) Math.cos(angle);
			float y = radius * (float) Math.sin(angle);
			int prevTopR6 = ((i * 2 + n * 2 - 1) % (n * 2)) * 6;
			int curTopL6 = i * 2 * 6;
			int prevBtmR6 = prevTopR6 + n * 2 * 6;
			int curBtmL6 = curTopL6 + n * 2 * 6;
			vertex[2][prevTopR6] = vertex[2][curTopL6] = vertex[2][prevBtmR6] = vertex[2][curBtmL6] = x;
			vertex[2][prevTopR6 + 1] = vertex[2][curTopL6 + 1] = vertex[2][prevBtmR6 + 1] = vertex[2][curBtmL6 + 1] = y;
			vertex[2][prevTopR6 + 2] = vertex[2][curTopL6 + 2] = height;
			vertex[2][prevBtmR6 + 2] = vertex[2][curBtmL6 + 2] = -height;

		}
		for (int i = 0; i < n; i++) {
			double angle = Math.PI * (i * 2 + 1) / n;
			float x = (float) Math.cos(angle);
			float y = (float) Math.sin(angle);
			int topL6 = i * 2 * 6;
			int topR6 = (i * 2 + 1) * 6;
			int btmL6 = topL6 + n * 2 * 6;
			int btmR6 = topR6 + n * 2 * 6;
			vertex[2][topL6 + 3] = vertex[2][topR6 + 3] = vertex[2][btmL6 + 3] = vertex[2][btmR6 + 3] = x;
			vertex[2][topL6 + 4] = vertex[2][topR6 + 4] = vertex[2][btmL6 + 4] = vertex[2][btmR6 + 4] = y;
		}
		return vertex;
	}

	private short[][] genIndex(int n) {
		short[][] index = new short[3][];
		index[0] = new short[n + 2];
		index[1] = new short[n + 2];
		index[2] = new short[n * 4];
		/* top and bottom */
		for (int i = 0; i < n + 1; i++) {
			short idx = (short) ((i + n) % (n + 1));
			index[0][i] = index[1][i] = idx;
		}
		/* side */
		for (int i = 0;i < n; i++) {
			int i4 = i * 4;
			index[2][i4] = (short) (i * 2);
			index[2][i4 + 1] = (short) (i * 2 + n * 2);
			index[2][i4 + 2] = (short) (i * 2 + 1);
			index[2][i4 + 3] = (short) (i * 2 + n * 2 + 1);
		}
		return index;
	}
}
