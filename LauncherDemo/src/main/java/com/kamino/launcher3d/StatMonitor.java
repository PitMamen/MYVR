package com.kamino.launcher3d;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.kamino.launcher3d.StatMonitor.OnStatChangeListener;
import com.kamino.launcher3d.utils.DeviceManager;

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

	//蓝牙连接状态
	@SuppressLint({ "NewApi" })
	private void handleBTState(int state) {
		//获得一个系统默认可用的蓝牙设备 没有蓝牙设备就返回null
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();		
		if (state < 0) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			  if (state == 12) {
					//查看是否蓝牙是否连接到以下几种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态  
					if (adapter.getProfileConnectionState(4) == BluetoothProfile.STATE_CONNECTED) {
						state = 2;
					 //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机  
					} else if (adapter.getProfileConnectionState(BluetoothProfile.A2DP) == 2) {
						state = 2;
					} else if (adapter.getProfileConnectionState(7) == 2) {
						state = 2;
						//蓝牙头戴式耳机，支持语音输入输出  
					} else if (adapter.getProfileConnectionState(1) == 2) {
						state = 2;
						 //蓝牙穿戴式设备  
					} else if (adapter.getProfileConnectionState(3) == 2) {
						state = 2;
					}
				}
			}
			//0 关闭 1 正在打开 2 开启 3 正在关闭
		} else if (state == 1 || state == 0 || state == 3) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			}
		}

		if (mListener != null && mListener.size() > 0) {
			//获得List对象的迭代器，然后通过迭代器来遍历List对象内保存的元素。
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onBluetoothStateChanged(state);
			}
		}
	}
	//保存wifi的状态
	private void handleNetworkState() {
		//ConnectivityManager主要管理和网络连接相关的操作
		connectivityManager = (ConnectivityManager) mContext
				.getSystemService("connectivity");
		// 获取代表联网状态的NetWorkInfo对象  
		info = connectivityManager.getActiveNetworkInfo();
		//info.isAvailable()为true表示当前的网络连接可用  为false表示不可用
		if (info != null && info.isAvailable()) {
			//判断连接的是wifi 
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
		//SD卡路径
		ArrayList<String> list = mDeviceManager.getSdDevicesList();
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); ++i) {
				if (Environment.getStorageState(new File(list.get(i))).equals(
						"mounted")) {
					//储存有SD卡状态
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
		//储存没有SD卡状态
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onTFStateChanged(false);
			}
		}
	}

	//unregisterReceiver注销广播是为了保证BroadcastReceiver对象资源被正确回收
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
		return ((AudioManager) context.getSystemService("audio"))
				.getStreamVolume(3);
		//取得当前手机的音量，最大值为7，最小值为0，当为0时，手机自动将模式调整为“震动模式”。
	}

	public long getAvailableMemory() {
		MemoryInfo info = new MemoryInfo();
		//获取系统可用内存信息，数据封装在info对象上
		((ActivityManager) mContext.getSystemService("activity"))
				.getMemoryInfo(info);
		//1024 * 1024   == 1048576
		//占用系统内存的百分比
		return info.availMem / 1048576L;
	}

	public List<OnStatChangeListener> getListener() {
		return mListener;
	}

	//通过WifiManager监听Wifi信号强弱
	int getRssi() {
		WifiInfo info = ((WifiManager) mContext.getSystemService("wifi"))
				.getConnectionInfo();
		//得到信号强度就靠wifiinfo.getRssi()；这个方法。得到的值是一个0到-100的区间值，
		//是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，
		//有可能连接不上或者掉线，一般Wifi已断则值为-200。
		//形参4表示wifi的信号一共分为几级。
		return info == null ? 0 : WifiManager.calculateSignalLevel(
				info.getRssi(), 4);
	}

	public void handleSoundState(Context context) {
		int volume = getAudioVolume(context);
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				//volume为0表示静音 其他表示有声音
				iter.next().onSoundStateChanged(volume == 0);
			}
		}
	}

	//注册各种广播
	public void initMonitor() {
		netStateReceiver = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter();
		//网络状态发生时发出的
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		//wifi强度发生变化发出
		filter.addAction("android.net.wifi.RSSI_CHANGED");
		mContext.registerReceiver(netStateReceiver, filter);

		soundReceiver = new SoundReceiver();
		filter = new IntentFilter();
		//注册当音量发生变化时接收的广播
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		mContext.registerReceiver(soundReceiver, filter);

		headsetReceiver = new HeadsetReceiver();
		filter = new IntentFilter();
		//在耳机口上插入耳机时发出的广播
		filter.addAction("android.intent.action.HEADSET_PLUG");
		mContext.registerReceiver(headsetReceiver, filter);
		
		batteryChangedReceiver = new BatteryChangedReceiver();
		filter = new IntentFilter();
		//充电状态，或者电池的电量发生变化
		filter.addAction("android.intent.action.BATTERY_CHANGED");
		//表示电池电量低
		filter.addAction("android.intent.action.BATTERY_LOW");
		//表示电池电量充足，即从电池电量低变化到饱满时会发出广播
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
		//插入SD卡并且已正确安装（识别）时发出的广播
		filter.addAction("android.intent.action.MEDIA_MOUNTED");
		// 拔出SD卡
		filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		filter.addDataScheme("file");
		mContext.registerReceiver(tfBroadcastReceiver, filter);

		bluetoothStateReceiver = new BluetoothStateReceiver();
		filter = new IntentFilter();
		//开关模式变化广播 
		filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		//搜索蓝牙
		filter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
		mContext.registerReceiver(bluetoothStateReceiver, filter);

		handleTFState();
		handleNetworkState();
		handleBTState(-1);
	}

	//判断listener是否属于mListener 不属于就加进来
	public void setListener(OnStatChangeListener listener) {
		if (mListener == null) {
			mListener = new ArrayList<OnStatChangeListener>();
		}

		if (!mListener.contains(listener)) {
			mListener.add(listener);
		}
	}

	//电量广播 接受Intent 在onReceive里面实现方法
	public class BatteryChangedReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//充电状态，或者电池的电量发生变化
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

	//蓝牙广播 接受Intent 在onReceive里面实现方法
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

	//耳机广播 接受Intent 在onReceive里面实现方法
	public class HeadsetReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//在耳机口上插入耳机时发出的广播
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

	//网络状态发出的广播 接受Intent 在onReceive里面实现方法
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

	//声音广播 接受Intent 在onReceive里面实现方法
	public class SoundReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int volume = getAudioVolume(context);
				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						//volume为0表示静音 其他表示有声音
						iter.next().onSoundStateChanged(volume == 0);
					}
				}
			}
		}
	}
	//TF卡的广播 接受Intent在onReceive里面实现方法
	public class TFStateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == "android.intent.action.MEDIA_UNMOUNTED") {
				handleTFState();
			} else if (intent.getAction() == "android.intent.action.MEDIA_MOUNTED") {
				handleTFState();
			}
		}
	}
}
