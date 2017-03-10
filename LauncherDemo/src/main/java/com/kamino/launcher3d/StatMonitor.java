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

	//��������״̬
	@SuppressLint({ "NewApi" })
	private void handleBTState(int state) {
		//���һ��ϵͳĬ�Ͽ��õ������豸 û�������豸�ͷ���null
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();		
		if (state < 0) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			  if (state == 12) {
					//�鿴�Ƿ������Ƿ����ӵ����¼����豸��һ�֣��Դ����ж��Ƿ�������״̬���Ǵ򿪲�û�����ӵ�״̬  
					if (adapter.getProfileConnectionState(4) == BluetoothProfile.STATE_CONNECTED) {
						state = 2;
					 //�ɲٿ������豸�����������ͣ���ܵ���������  
					} else if (adapter.getProfileConnectionState(BluetoothProfile.A2DP) == 2) {
						state = 2;
					} else if (adapter.getProfileConnectionState(7) == 2) {
						state = 2;
						//����ͷ��ʽ������֧�������������  
					} else if (adapter.getProfileConnectionState(1) == 2) {
						state = 2;
						 //��������ʽ�豸  
					} else if (adapter.getProfileConnectionState(3) == 2) {
						state = 2;
					}
				}
			}
			//0 �ر� 1 ���ڴ� 2 ���� 3 ���ڹر�
		} else if (state == 1 || state == 0 || state == 3) {
			if (adapter == null) {
				state = 10;
			} else {
				state = adapter.getState();
			}
		}

		if (mListener != null && mListener.size() > 0) {
			//���List����ĵ�������Ȼ��ͨ��������������List�����ڱ����Ԫ�ء�
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onBluetoothStateChanged(state);
			}
		}
	}
	//����wifi��״̬
	private void handleNetworkState() {
		//ConnectivityManager��Ҫ���������������صĲ���
		connectivityManager = (ConnectivityManager) mContext
				.getSystemService("connectivity");
		// ��ȡ��������״̬��NetWorkInfo����  
		info = connectivityManager.getActiveNetworkInfo();
		//info.isAvailable()Ϊtrue��ʾ��ǰ���������ӿ���  Ϊfalse��ʾ������
		if (info != null && info.isAvailable()) {
			//�ж����ӵ���wifi 
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
		//SD��·��
		ArrayList<String> list = mDeviceManager.getSdDevicesList();
		if (list != null && list.size() != 0) {
			for (int i = 0; i < list.size(); ++i) {
				if (Environment.getStorageState(new File(list.get(i))).equals(
						"mounted")) {
					//������SD��״̬
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
		//����û��SD��״̬
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				iter.next().onTFStateChanged(false);
			}
		}
	}

	//unregisterReceiverע���㲥��Ϊ�˱�֤BroadcastReceiver������Դ����ȷ����
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
		//ȡ�õ�ǰ�ֻ������������ֵΪ7����СֵΪ0����Ϊ0ʱ���ֻ��Զ���ģʽ����Ϊ����ģʽ����
	}

	public long getAvailableMemory() {
		MemoryInfo info = new MemoryInfo();
		//��ȡϵͳ�����ڴ���Ϣ�����ݷ�װ��info������
		((ActivityManager) mContext.getSystemService("activity"))
				.getMemoryInfo(info);
		//1024 * 1024   == 1048576
		//ռ��ϵͳ�ڴ�İٷֱ�
		return info.availMem / 1048576L;
	}

	public List<OnStatChangeListener> getListener() {
		return mListener;
	}

	//ͨ��WifiManager����Wifi�ź�ǿ��
	int getRssi() {
		WifiInfo info = ((WifiManager) mContext.getSystemService("wifi"))
				.getConnectionInfo();
		//�õ��ź�ǿ�ȾͿ�wifiinfo.getRssi()������������õ���ֵ��һ��0��-100������ֵ��
		//��һ��int�����ݣ�����0��-50��ʾ�ź���ã�-50��-70��ʾ�ź�ƫ�С��-70��ʾ��
		//�п������Ӳ��ϻ��ߵ��ߣ�һ��Wifi�Ѷ���ֵΪ-200��
		//�β�4��ʾwifi���ź�һ����Ϊ������
		return info == null ? 0 : WifiManager.calculateSignalLevel(
				info.getRssi(), 4);
	}

	public void handleSoundState(Context context) {
		int volume = getAudioVolume(context);
		if (mListener != null && mListener.size() > 0) {
			Iterator<OnStatChangeListener> iter = mListener.iterator();
			while (iter.hasNext()) {
				//volumeΪ0��ʾ���� ������ʾ������
				iter.next().onSoundStateChanged(volume == 0);
			}
		}
	}

	//ע����ֹ㲥
	public void initMonitor() {
		netStateReceiver = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter();
		//����״̬����ʱ������
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		//wifiǿ�ȷ����仯����
		filter.addAction("android.net.wifi.RSSI_CHANGED");
		mContext.registerReceiver(netStateReceiver, filter);

		soundReceiver = new SoundReceiver();
		filter = new IntentFilter();
		//ע�ᵱ���������仯ʱ���յĹ㲥
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		mContext.registerReceiver(soundReceiver, filter);

		headsetReceiver = new HeadsetReceiver();
		filter = new IntentFilter();
		//�ڶ������ϲ������ʱ�����Ĺ㲥
		filter.addAction("android.intent.action.HEADSET_PLUG");
		mContext.registerReceiver(headsetReceiver, filter);
		
		batteryChangedReceiver = new BatteryChangedReceiver();
		filter = new IntentFilter();
		//���״̬�����ߵ�صĵ��������仯
		filter.addAction("android.intent.action.BATTERY_CHANGED");
		//��ʾ��ص�����
		filter.addAction("android.intent.action.BATTERY_LOW");
		//��ʾ��ص������㣬���ӵ�ص����ͱ仯������ʱ�ᷢ���㲥
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
		//����SD����������ȷ��װ��ʶ��ʱ�����Ĺ㲥
		filter.addAction("android.intent.action.MEDIA_MOUNTED");
		// �γ�SD��
		filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		filter.addDataScheme("file");
		mContext.registerReceiver(tfBroadcastReceiver, filter);

		bluetoothStateReceiver = new BluetoothStateReceiver();
		filter = new IntentFilter();
		//����ģʽ�仯�㲥 
		filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		//��������
		filter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
		mContext.registerReceiver(bluetoothStateReceiver, filter);

		handleTFState();
		handleNetworkState();
		handleBTState(-1);
	}

	//�ж�listener�Ƿ�����mListener �����ھͼӽ���
	public void setListener(OnStatChangeListener listener) {
		if (mListener == null) {
			mListener = new ArrayList<OnStatChangeListener>();
		}

		if (!mListener.contains(listener)) {
			mListener.add(listener);
		}
	}

	//�����㲥 ����Intent ��onReceive����ʵ�ַ���
	public class BatteryChangedReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//���״̬�����ߵ�صĵ��������仯
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

	//�����㲥 ����Intent ��onReceive����ʵ�ַ���
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

	//�����㲥 ����Intent ��onReceive����ʵ�ַ���
	public class HeadsetReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//�ڶ������ϲ������ʱ�����Ĺ㲥
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

	//����״̬�����Ĺ㲥 ����Intent ��onReceive����ʵ�ַ���
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

	//�����㲥 ����Intent ��onReceive����ʵ�ַ���
	public class SoundReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int volume = getAudioVolume(context);
				if (mListener != null && mListener.size() > 0) {
					Iterator<OnStatChangeListener> iter = mListener.iterator();
					while (iter.hasNext()) {
						//volumeΪ0��ʾ���� ������ʾ������
						iter.next().onSoundStateChanged(volume == 0);
					}
				}
			}
		}
	}
	//TF���Ĺ㲥 ����Intent��onReceive����ʵ�ַ���
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
