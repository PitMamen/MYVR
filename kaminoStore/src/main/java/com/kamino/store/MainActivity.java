package com.kamino.store;

import com.google.vrtoolkit.cardboard.CardboardActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends CardboardActivity {
	private final String TAG = "MainActivity";
	private Context mContext;
	private MainSurfaceView mWorld;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Save Context
		mContext = this;

		setConvertTapIntoTrigger(true);
		handleFullScreenMode();

		setContentView(R.layout.activity_main);
		mWorld = (MainSurfaceView) findViewById(R.id.vr_view);
		setCardboardView(mWorld);

		// Disable Distortion && hide marker and button 
		mWorld.setDistortionCorrectionEnabled(false);
		mWorld.setAlignmentMarkerEnabled(false);
		mWorld.setSettingsButtonEnabled(false);
		mWorld.setVRModeEnabled(true);

	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	public void handleFullScreenMode() {
		int flags = getWindow().getDecorView().getSystemUiVisibility();
		getWindow().getDecorView().setSystemUiVisibility(
				flags | View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Resume CardboardView
		mWorld.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Pause CardboardView
		mWorld.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCardboardTrigger() {
		super.onCardboardTrigger();
		// Trigger When press OK or click
		Log.d(TAG, "Cardboard Trigger!");
		if (mWorld != null) {
			mWorld.onConfirm();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mWorld != null) {
				mWorld.resetHeadTracker();
			}
		}
		return super.onKeyLongPress(keyCode, event);
	}
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		if (action == 0) {
			//È·¶¨¼ü
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
					//»Ø³µ¼ü
					|| keyCode == KeyEvent.KEYCODE_ENTER) {
				if (mWorld != null) {
					mWorld.onConfirm();
					return true;
				}
				//ÓÒ¼ü
			}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (mWorld != null && mWorld.isDrawGame()) {
					mWorld.goNextPage();
					return true;
				}
				//×ó¼ü
			}else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (mWorld != null && mWorld.isDrawGame()) {
					mWorld.goPreviousPage();
					return true;
				}
			}
			//ÅÄÕÕ¼ü  F7¼ü  ²åÈë¼ü
			if (keyCode == KeyEvent.KEYCODE_CAMERA
					|| keyCode == KeyEvent.KEYCODE_F7
					|| keyCode == KeyEvent.KEYCODE_INSERT) {
				try {
					Intent intent = new Intent();
					//packageName, className Æô¶¯Ïà»ú
					intent.setComponent(new ComponentName(
							"com.android.camera2",
							"com.android.camera.CameraActivity"));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception e) {
					;
				}
			}
		}

		return super.dispatchKeyEvent(event);
	}

	public void onBackPressed() {
		if (mWorld != null) {
			mWorld.onBackPressed();
		}
	}
	
}
