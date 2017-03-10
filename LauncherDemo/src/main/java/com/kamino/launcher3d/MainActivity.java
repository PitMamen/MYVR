package com.kamino.launcher3d;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.kamino.launcher3d.app.AppManagerReceiver;
import com.kamino.launcher3d.app.LocalAppManager;
import com.kamino.launcher3d.app.OnAppStateChangeListener;

public class MainActivity extends CardboardActivity implements
		OnAppStateChangeListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private SensorManager mSensorManager;
	private LocalAppManager mAppManager;
	private AppManagerReceiver mAppReceiver;
	private StatMonitor mStatMonitor;
	private MainSurfaceView mWorld;

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
			//确定键
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
					//回车键
					|| keyCode == KeyEvent.KEYCODE_ENTER) {
				if (mWorld != null) {
					mWorld.onConfirm();
					return true;
				}
				//右键
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (mWorld != null && mWorld.isDrawLocal()) {
					mWorld.goNextPage();
					return true;
				}
				//左键
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (mWorld != null && mWorld.isDrawLocal()) {
					mWorld.goPreviousPage();
					return true;
				}
			}
			//拍照键  F7键  插入键
			if (keyCode == KeyEvent.KEYCODE_CAMERA
					|| keyCode == KeyEvent.KEYCODE_F7
					|| keyCode == KeyEvent.KEYCODE_INSERT) {
				try {
					Intent intent = new Intent();
					//packageName, className 启动相机
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

	public void onBackPressed() {
		if (mWorld != null) {
			mWorld.onBackPressed();
		}
	}

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setConvertTapIntoTrigger(true);
		handleFullScreenMode();

		setContentView(R.layout.activity_main);
		mWorld = (MainSurfaceView) findViewById(R.id.vr_view);
		setCardboardView(mWorld);

		mAppManager = LocalAppManager.getInstance(this);
		mAppReceiver = new AppManagerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_ADDED");
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		registerReceiver(mAppReceiver, filter);
		mAppReceiver.registerAppStateChangeListener(this);

		mStatMonitor = new StatMonitor(this);
		mStatMonitor.setListener(mWorld);
		mStatMonitor.initMonitor();

		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				mAppManager.getAllLocalApp();
				mAppManager.setState(true);
			}
		}, 500L);

		mWorld.setDistortionCorrectionEnabled(false);
		mWorld.setAlignmentMarkerEnabled(false);
		mWorld.setSettingsButtonEnabled(false);

		// using 3D Mode as default
		mWorld.setVRModeEnabled(true); // 3D Mode

		// enable or disable sensor mode
		//不用的时候关掉传感器，因为手机黑屏是不会自动关掉传感器的
		//传感器管理者
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//陀螺仪传感器
		Sensor sensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
		if (sensor == null) {
			mWorld.enableSensorMode(false); // disable sensor mode 禁用传感器模式
			mWorld.setVRModeEnabled(false); // 2D Mode 禁用2D模式
			setConvertTapIntoTrigger(false); // disable tap trigger 禁用
		}
		Log.d(TAG, "SensorMode=" + mWorld.isSensorMode());
		Log.d(TAG, "VRMode=" + mWorld.getVRMode());
	}

	protected void onDestroy() {
		super.onDestroy();
		//注销所有广播
		if (mStatMonitor != null) {
			mStatMonitor.finishMonitor();
			mStatMonitor = null;
		}

		if (mAppReceiver != null) {
			mAppReceiver.unregisterAppStateChangeListener(this);
			unregisterReceiver(mAppReceiver);
		}
	}

	protected void onPause() {
		super.onPause();
		if (mWorld != null) {
			mWorld.onPause();
		}
	}

	protected void onResume() {
		super.onResume();
		if (mWorld != null) {
			mWorld.onResume();
		}
	}

	@Override
	public void onCardboardTrigger() {
		super.onCardboardTrigger();
		if (mWorld != null) {
			mWorld.onConfirm();
		}
	}

	@Override
	public void onAppStateChanged(String pkgName, boolean isInstall) {
		mAppManager.updateData(pkgName, isInstall);
	}
}
