package com.kamino.filemanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.vrtoolkit.cardboard.CardboardActivity;

@SuppressLint("NewApi")
public class MainActivity extends CardboardActivity {
	private FrameLayout mMainView;
	private MySurfaceView mGLSurfaceView;
	private boolean mIsGetPath;
	private StatMonitor mStatMonitor;

	@Override//¶Ì°´²Ëµ¥¼ü
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override//³¤°´²Ëµ¥¼ü
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (mGLSurfaceView != null) {
				mGLSurfaceView.resetHeadTracker();
			}
		}
		return super.onKeyLongPress(keyCode, event);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				onBackPressed();//·µ»Ø¼ü
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
					|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				mGLSurfaceView.click();//È·¶¨¼ü
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				mGLSurfaceView.goLeft();//×ó¼ü
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mGLSurfaceView.goRight();//ÓÒ¼ü
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
				mGLSurfaceView.goUp();//ÉÏ¼ü
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
				mGLSurfaceView.goDown();//ÏÂ¼ü
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	public final boolean isGetPath() {
		return mIsGetPath;
	}

	public void onBackPressed() {
		mGLSurfaceView.goReturn();
	}

	@SuppressLint("NewApi")
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.e("²âÊÔ","0");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (VERSION.SDK_INT >= 19) {
			int flags = getWindow().getDecorView().getSystemUiVisibility();
			getWindow().getDecorView().setSystemUiVisibility(
					flags | View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}

		mGLSurfaceView = new MySurfaceView(this);
		setContentView(R.layout.activity_main);
		mIsGetPath = getIntent().getBooleanExtra("getPath", false);
		mMainView = (FrameLayout) findViewById(R.id.main_view);
		mMainView.addView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
		mGLSurfaceView.setPreserveEGLContextOnPause(true);
		if (VERSION.SDK_INT >= 19) {
			Intent intent = new Intent(
					"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			sendBroadcast(intent);
		} else {
			sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		}
		Log.e("²âÊÔ","1");
		mStatMonitor = new StatMonitor(this);
		mStatMonitor.setListener(mGLSurfaceView);
		mStatMonitor.initMonitor();

		Log.e("²âÊÔ","2");
		// CardboardView config
		setConvertTapIntoTrigger(true);
		// setCardboardView(mGLSurfaceView);

		mGLSurfaceView.setDistortionCorrectionEnabled(false);
		mGLSurfaceView.setAlignmentMarkerEnabled(false);
		mGLSurfaceView.setSettingsButtonEnabled(false);

		// using 3D Mode as default
		mGLSurfaceView.setVRModeEnabled(true); // 3D Mode
	}

	protected void onDestroy() {
		if (mStatMonitor != null) {
			mStatMonitor.finishMonitor();
			mStatMonitor = null;
		}
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override//µã»÷È·¶¨¼ü
	public void onCardboardTrigger() {
		super.onCardboardTrigger();
		if (mGLSurfaceView != null) {
			mGLSurfaceView.click();
		}
	}
}
