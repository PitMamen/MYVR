package com.my.mylauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.lang.reflect.Method;
import java.util.ArrayList;

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
		mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

		Method getVolPathMethod;
		try {
			getVolPathMethod = mStorageManager.getClass().getMethod(
					"getVolumePaths", new Class[0]);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			getVolPathMethod = null;
		}

		String[] volumePaths;
		try {
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
		internalDevicesList.add(Environment.getExternalStorageDirectory()
				.getPath());
		sdDevicesList = new ArrayList<String>();
		usbDevicesList = new ArrayList<String>();
		sataDevicesList = new ArrayList<String>();

		for (int i = 0; i < localDevicesList.size(); ++i) {
			String device = (String) localDevicesList.get(i);
			if (!device.equals(Environment.getExternalStorageDirectory()
					.getPath())) {
				if (device.contains("sd")) {
					sdDevicesList.add(device);
				} else if (device.contains("usb")) {
					usbDevicesList.add(device);
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

	public ArrayList<String> getInternalDevicesList() {
		return (ArrayList<String>) internalDevicesList.clone();
	}

	public ArrayList<String> getLocalDevicesList() {
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

	public ArrayList<String> getSataDevicesList() {
		return (ArrayList<String>) sataDevicesList.clone();
	}

	public ArrayList<String> getSdDevicesList() {
		return (ArrayList<String>) sdDevicesList.clone();
	}

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
