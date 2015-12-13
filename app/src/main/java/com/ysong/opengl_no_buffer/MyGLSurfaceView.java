package com.ysong.opengl_no_buffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private MyGLRender mGLRender;
	private float mPrevX;
	private float mPrevY;

	public MyGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mGLRender = new MyGLRender(context);
		setRenderer(mGLRender);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setPreserveEGLContextOnPause(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float w = getWidth();

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float ax = -(float) 180 * (y - mPrevY) / w;
			float ay = -(float) 180 * (x - mPrevX) / w;
			mGLRender.camMove(ax, ay);
			requestRender();
		}

		mPrevX = x;
		mPrevY = y;
		return true;
	}

	public void release() {
		mGLRender.release();
	}
}
