package com.kamino.launcher3d.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.storage.StorageManager;

public class DeviceManager {
	//private static String TAG = "DeviceManager";
	private ArrayList<String> internalDevicesList;
	private ArrayList<String> localDevicesList;
	private Context mContext;
	private StorageManager mStorageManager;
	private ArrayList<String> sataDevicesList;
	private ArrayList<String> sdDevicesList;
	private ArrayList<String> usbDevicesList;

	public DeviceManager(Context context) {
		mContext = context;
		localDevicesList = new ArrayList<String>();
		//�õ������豸������
		mStorageManager = (StorageManager) context.getSystemService("storage");

		Method getVolPathMethod;
		try {
			//���淽����ͨ�����䣬����StorageManager�����ؽӿ�getVolumePaths()��ʵ�ֻ�ȡ�洢���б�
			getVolPathMethod = mStorageManager.getClass().getMethod(
					"getVolumePaths", new Class[0]);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			getVolPathMethod = null;
		}

		String[] volumePaths;
		try {
			//ͨ�����䣬��ȡ�洢���б�
			volumePaths = (String[]) getVolPathMethod.invoke(mStorageManager,
					new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			volumePaths = null;
		}

		for (int i = 0; i < volumePaths.length; ++i) {
			localDevicesList.add(volumePaths[i]);
		}

		internalDevicesList = new ArrayList<String>();
		//��ȡSD��·��
		internalDevicesList.add(Environment.getExternalStorageDirectory()
				.getPath());
		sdDevicesList = new ArrayList<String>();
		usbDevicesList = new ArrayList<String>();
		sataDevicesList = new ArrayList<String>();

		for (int i = 0; i < localDevicesList.size(); ++i) {
			String device = (String) localDevicesList.get(i);
			if (!device.equals(Environment.getExternalStorageDirectory()
					.getPath())) {
				//sd�ӿ�
				if (device.contains("sd")) {
					sdDevicesList.add(device);
					//usb�ӿ�
				} else if (device.contains("usb")) {
					usbDevicesList.add(device);
					//Sata���нӿڻ����нṹ�򵥡�֧���Ȳ�ε��ŵ㡣
				} else if (device.contains("sata")) {
					sataDevicesList.add(device);
				}
			}
		}

	}

	public void delNetDevice(String key) {
		if (key != null) {
			ArrayList<String> list = getNetDeviceList();
			list.remove(key);

			String devices = null;

			for (int i = 0; i < list.size(); ++i) {
				if (devices == null) {
					devices = (String) list.get(i);
				} else {
					devices = devices + "," + (String) list.get(i);
				}
			}

			Editor editor = mContext.getSharedPreferences("Device", 0).edit();
			editor.putString("Net", devices);
			editor.commit();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getInternalDevicesList() {
		//�����������internalDevicesList��һ������
		return (ArrayList<String>) internalDevicesList.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getLocalDevicesList() {
		//�����������localDevicesList��һ������
		return (ArrayList<String>) localDevicesList.clone();
	}

	public ArrayList<String> getNetDeviceList() {
		String devices = mContext.getSharedPreferences("Device", 0).getString(
				"Net", (String) null);
		ArrayList<String> list = null;
		if (devices != null) {
			String[] strs = devices.split(",");
			list = null;
			if (strs != null) {
				list = new ArrayList<String>();

				for (int i = 0; i < strs.length; ++i) {
					list.add(strs[i]);
				}
			}
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSataDevicesList() {
		return (ArrayList<String>) sataDevicesList.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSdDevicesList() {
		return (ArrayList<String>) sdDevicesList.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getUsbDevicesList() {
		return (ArrayList<String>) usbDevicesList.clone();
	}

	public boolean isInterStoragePath(String path) {
		return internalDevicesList.contains(path);
	}

	public boolean isLocalDevicesRootPath(String path) {
		for (int i = 0; i < localDevicesList.size(); ++i) {
			if (path.equals(localDevicesList.get(i))) {
				return true;
			}
		}

		return false;
	}

	public boolean isSataStoragePath(String path) {
		return sataDevicesList.contains(path);
	}

	public boolean isSdStoragePath(String path) {
		return sdDevicesList.contains(path);
	}

	public boolean isUsbStoragePath(String path) {
		return usbDevicesList.contains(path);
	}

	//
	public void saveNetDevice(String device) {
		if (device != null) {
			SharedPreferences sp = mContext.getSharedPreferences("Device", 0);
			Editor editor = sp.edit();
			String devices = sp.getString("Net", (String) null);
			if (devices == null) {
				editor.putString("Net", device);
			} else {
				editor.putString("Net", devices + "," + device);
			}

			editor.commit();
		}
	}
}
