package com.kamino.filemanager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import com.kamino.filemanager.util.DeviceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatMonitor {
	private static final String TAG = "StatMonitor";
	private BatteryChangedReceiver batteryChangedReceiver;
	private BluetoothStateReceiver bluetoothStateReceiver;
	private ConnectivityManager connectivityManager;
	private HeadsetReceiver headsetReceiver;
	private NetworkInfo info;
	private List<OnStatChangeListener> mListener = new ArrayList<OnStatChangeListener>();
	private Context mContext;
	private DeviceManager mDeviceManager;
	private NetworkStateReceiver netStateReceiver;
	private SoundReceiver soundReceiver;
	private TFStateReceiver tfBroadcastReceiver;

	public interface OnStatChangeListener {
		void onBatteryChanged(int percent);

		void onBatteryPower(boolean charge);

		void onBluetoothStateChanged(int state);

		void onHeadsetStateChanged(boolean plugin);

		void onNetworkStateChanged(int type, int state, int rssi);

		void onSoundStateChanged(boolean mute);

		void onTFStateChanged(boolean plugin);
	}

	public StatMonitor(Context context) {
		mContext = context;
		if (mDeviceManager == null) {
			mDeviceManager = new DeviceManager(mContext);
		}
	}

	@SuppressLint({ "NewApi" })
	private void handleBTState(int state) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (state < 0) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			  if (state == 12) {
					if (adapter.getProfileConnectionState(4) == BluetoothProfile.STATE_CONNECTED) {
						state = 2;
					} else if (adapter.getProfileConnectionState(BluetoothProfile.A2DP) == 2) {
						state = 2;
					} else if (adapter.getProfileConnectionState(7) == 2) {
						state = 2;
					} else if (adapter.getProfileConnectionState(1) == 2) {
						state = 2;
					} else if (adapter.getProfileConnectionState(3) == 2) {
						state = 2;
					}
				}
			}
		} else if (state == 1 || state == 0 || state == 3) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			}
		}

		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onBluetoothStateChanged(state);
			}
		}
	}
	private void handleNetworkState() {
		connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						iter.next().onNetworkStateChanged(info.getType(), 0,
								getRssi());
					}
				}
				return;
			}
		}

		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onNetworkStateChanged(0, 0, getRssi());
			}
		}
	}

	@SuppressLint({ "NewApi" })
	private void handleTFState() {
		ArrayList<String> list = mDeviceManager.getSdDevicesList();
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); ++i) {
				Log.e("测试SD卡路径",Environment.getStorageState(new File(list.get(i)))+"测试成功");
				if (Environment.getStorageState(new File(list.get(i))).equals(
						"mounted")) {
					if (mListener != null && mListener.size() > 0) {
						Iterator<OnStatChangeListener> iter = mListener
								.iterator();
						while (iter.hasNext()) {
							iter.next().onTFStateChanged(true);
						}
					}

					return;
				}
			}
		}
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onTFStateChanged(false);
			}
		}
	}

	public void finishMonitor() {
		if (netStateReceiver != null) {
			mContext.unregisterReceiver(netStateReceiver);
		}

		if (batteryChangedReceiver != null) {
			mContext.unregisterReceiver(batteryChangedReceiver);
		}

		if (bluetoothStateReceiver != null) {
			mContext.unregisterReceiver(bluetoothStateReceiver);
		}

		if (tfBroadcastReceiver != null) {
			mContext.unregisterReceiver(tfBroadcastReceiver);
		}

		if (soundReceiver != null) {
			mContext.unregisterReceiver(soundReceiver);
		}

		if (headsetReceiver != null) {
			mContext.unregisterReceiver(headsetReceiver);
		}

		mListener = null;
	}

	public int getAudioVolume(Context context) {
		return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
				.getStreamVolume(3);
	}

	public long getAvailableMemory() {
		MemoryInfo info = new MemoryInfo();
		((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryInfo(info);
		return info.availMem / 1048576L;
	}

	public List<OnStatChangeListener> getListener() {
		return mListener;
	}

	int getRssi() {
		WifiInfo info = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE))
				.getConnectionInfo();
		return info == null ? 0 : WifiManager.calculateSignalLevel(
				info.getRssi(), 4);
	}

	public void handleSoundState(Context context) {
		int volume = getAudioVolume(context);
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onSoundStateChanged(volume == 0);
			}
		}
	}

	public void initMonitor() {
		netStateReceiver = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction("android.net.wifi.RSSI_CHANGED");
		mContext.registerReceiver(netStateReceiver, filter);

		soundReceiver = new SoundReceiver();
		filter = new IntentFilter();
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		mContext.registerReceiver(soundReceiver, filter);

		headsetReceiver = new HeadsetReceiver();
		filter = new IntentFilter();
		filter.addAction("android.intent.action.HEADSET_PLUG");
		mContext.registerReceiver(headsetReceiver, filter);
		
		batteryChangedReceiver = new BatteryChangedReceiver();
		filter = new IntentFilter();
		filter.addAction("android.intent.action.BATTERY_CHANGED");
		filter.addAction("android.intent.action.BATTERY_LOW");
		filter.addAction("android.intent.action.BATTERY_OKAY");
		Intent intent = mContext.registerReceiver(batteryChangedReceiver,
				filter);

		boolean charge = (intent.getIntExtra("status", -1) == 2);
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onBatteryPower(charge);
			}
		}

		tfBroadcastReceiver = new TFStateReceiver();
		filter = new IntentFilter();
		filter.addAction("android.intent.action.MEDIA_MOUNTED");
		filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		filter.addDataScheme("file");
		mContext.registerReceiver(tfBroadcastReceiver, filter);

		bluetoothStateReceiver = new BluetoothStateReceiver();
		filter = new IntentFilter();
		filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		filter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
		mContext.registerReceiver(bluetoothStateReceiver, filter);

		handleTFState();
		handleNetworkState();
		handleBTState(-1);
	}

	public void setListener(OnStatChangeListener listener) {
		if (mListener == null) {
			mListener = new ArrayList<OnStatChangeListener>();
		}

		if (!mListener.contains(listener)) {
			mListener.add(listener);
		}
	}

	public class BatteryChangedReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ("android.intent.action.BATTERY_CHANGED".equals(intent
					.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				double percent = (double) (level * 100) / (double) scale;
				int status = intent.getIntExtra("status", -1);

				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						OnStatChangeListener listener = iter.next();
						listener.onBatteryPower(status == 2);
						listener.onBatteryChanged((int) percent);
					}
				}
			} else if (!"android.intent.action.BATTERY_OKAY".equals(intent
					.getAction())
					&& "android.intent.action.BATTERY_LOW".equals(intent
							.getAction())) {
				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						iter.next().onBatteryChanged(10);
					}
				}
			}
		}
	}

	public class BluetoothStateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent
					.getAction())) {
				int state = intent.getIntExtra(
						"android.bluetooth.adapter.extra.STATE", 10);
				handleBTState(state);
			} else if ("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED"
					.equals(intent.getAction())) {
				int state = intent.getIntExtra(
						"android.bluetooth.adapter.extra.CONNECTION_STATE", 0);
				handleBTState(state);
			}
		}
	}

	public class HeadsetReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
				int state = intent.getIntExtra("state", 0);
				if (state == 0 || state == 1) {
					if (mListener != null && mListener.size() > 0) {
						Iterator<OnStatChangeListener> iter = mListener
								.iterator();
						while (iter.hasNext()) {
							iter.next().onHeadsetStateChanged(state == 1);
						}
					}
				}

				handleSoundState(mContext);
			}
		}
	}

	public class NetworkStateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent
					.getAction())) {
				handleNetworkState();
			} else if ("android.net.wifi.RSSI_CHANGED".equals(intent
					.getAction())) {
				handleNetworkState();
			}
		}
	}

	public class SoundReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int volume = getAudioVolume(context);
				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						iter.next().onSoundStateChanged(volume == 0);
					}
				}
			}
		}
	}
	public class TFStateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == "android.intent.action.MEDIA_UNMOUNTED") {
				Log.e("测试接收到SD拔出的广播","拔出SD卡广播接收成功");
				handleTFState();
			} else if (intent.getAction() == "android.intent.action.MEDIA_MOUNTED") {
				Log.e("测试接收到SD插入的广播","插入SD卡广播接收成功");
				handleTFState();
			}
		}
	}
}
