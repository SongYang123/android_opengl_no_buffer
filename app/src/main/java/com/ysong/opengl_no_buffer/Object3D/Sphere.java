package com.ysong.opengl_no_buffer.Object3D;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Sphere extends Object3D {

	private FloatBuffer vertexBuffer;
	private ShortBuffer[] indexBuffer = new ShortBuffer[3];
	private int n;

	public Sphere(int n, float radius, float[] color) {
		float[] vertex = genVertex(n, radius);
		vertexBuffer = ByteBuffer.allocateDirect(BYTE_PER_FLOAT * vertex.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(vertex);

		short[][] index = genIndex(n);
		for (int i = 0; i < 3; i++) {
			indexBuffer[i] = ByteBuffer.allocateDirect(BYTE_PER_SHORT * index[i].length).order(ByteOrder.nativeOrder()).asShortBuffer();
			indexBuffer[i].put(index[i]);
		}

		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		this.n = n;
	}

	@Override
	public void render(float[] mMVPMatrix, float[] mMVMatrix) {
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glEnableVertexAttribArray(mNormalHandle);

		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer);
		vertexBuffer.position(POSITION_SIZE);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_SIZE, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * (POSITION_SIZE + NORMAL_SIZE), vertexBuffer);

		for (int i = 0; i < 2; i++) {
			indexBuffer[i].position(0);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indexBuffer[i].capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer[i]);
		}

		for (int i = 0; i < indexBuffer[2].capacity(); i += (n * 2 + 1) * 2) {
			indexBuffer[2].position(i);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, (n * 2 + 1) * 2, GLES20.GL_UNSIGNED_SHORT, indexBuffer[2]);
		}
	}

	@Override
	public void release() {
		vertexBuffer.limit(0);
		vertexBuffer = null;
		for (int i = 0; i < 3; i++) {
			indexBuffer[i].limit(0);
			indexBuffer[i] = null;
		}
	}

	private float[] genVertex(int n, float radius) {
		float[] vertex = new float[n * n * 12 - n * 12 + 12];
		for (int i = 0; i < n - 1; i++) {
			double phi = Math.PI * (i + 1) / n;
			float r = (float) Math.sin(phi);
			float h = (float) Math.cos(phi);
			for (int j = 0; j < n * 2; j++) {
				double theta = Math.PI * j / n;
				float x = r * (float) Math.cos(theta);
				float y = r * (float) Math.sin(theta);
				int ij6 = (i * n * 2 + j) * 6;
				vertex[ij6] = radius * x;
				vertex[ij6 + 1] = radius * y;
				vertex[ij6 + 2] = radius * h;
				vertex[ij6 + 3] = x;
				vertex[ij6 + 4] = y;
				vertex[ij6 + 5] = h;
			}
			vertex[n * n * 12 - n * 12 + 2] = radius;
			vertex[n * n * 12 - n * 12 + 5] = 1.0f;
			vertex[n * n * 12 - n * 12 + 8] = -radius;
			vertex[n * n * 12 - n * 12 + 11] = -1.0f;
		}
		return vertex;
	}

	private short[][] genIndex(int n) {
		short[][] index = new short[3][];
		index[0] = new short[n * 2 + 2];
		index[1] = new short[n * 2 + 2];
		index[2] = new short[(n * 2 + 1) * (n - 2) * 2];
		/* top and bottom */
		index[0][0] = (short) (n * n * 2 - n * 2);
		index[1][0] = (short) (n * n * 2 - n * 2 + 1);
		for (int i = 0; i < n * 2 + 1; i++) {
			index[0][i + 1] = (short) (i % (n * 2));
			index[1][i + 1] = (short) (n * n * 2 - n * 2 - i % (n * 2) - 1);
		}
		/* side */
		for (int i = 0; i < n - 2; i++) {
			int in = i * (n * 2 + 1) * 2;
			for (int j = 0; j < n * 2 + 1; j++) {
				int j2 = j * 2;
				index[2][in + j2] = (short) (i * (n * 2) + j % (n * 2));
				index[2][in + j2 + 1] = (short) ((i + 1) * (n * 2) + j % (n * 2));
			}
		}
		return index;
	}
}
