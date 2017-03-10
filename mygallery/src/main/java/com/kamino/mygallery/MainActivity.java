package com.kamino.mygallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.kamino.mygallery.view.CardboardOverlayView;

@SuppressLint("NewApi")
public class MainActivity extends CardboardActivity {
	private FrameLayout mMainView;
	private MySurfaceView mGLSurfaceView;
	private boolean mIsGetPath;
	private StatMonitor mStatMonitor;
	private CardboardOverlayView toast;
	@Override//短按菜单键
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override//长按菜单键
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
				onBackPressed();//返回键
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
					|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				mGLSurfaceView.click();//确定键
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				mGLSurfaceView.goPreviousPage();//左键
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mGLSurfaceView.goNextPage();//右键
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
				mGLSurfaceView.goUp();//上键
				return true;
			}

			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
				mGLSurfaceView.goDown();//下键
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}



	public void onBackPressed() {
		mGLSurfaceView.goReturn();
	}

	@SuppressLint("NewApi")
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
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

		mMainView = (FrameLayout) findViewById(R.id.main_view);
		mMainView.addView(mGLSurfaceView);

		toast = (CardboardOverlayView) findViewById(R.id.toast);
		//改变view的z轴，使其处在他父view的顶端
		toast.bringToFront();

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
		mStatMonitor = new StatMonitor(this);
		mStatMonitor.setListener(mGLSurfaceView);
		mStatMonitor.initMonitor();

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

	@Override//点击确定键
	public void onCardboardTrigger() {
		super.onCardboardTrigger();
		if (mGLSurfaceView != null) {
			mGLSurfaceView.click();
		}
	}

	public void showToast(String text) {
		toast.show3DToast(text);
	}
}
