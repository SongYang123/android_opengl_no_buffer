package com.ysong.opengl_no_buffer;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	private GLSurfaceView mGLSurfaceView;
	private MyGLRender mGLRender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLRender = new MyGLRender(this);
		mGLSurfaceView.setRenderer(mGLRender);
		setContentView(mGLSurfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	@Override
	protected void onDestroy() {
		mGLRender.release();
		super.onDestroy();
	}
}
