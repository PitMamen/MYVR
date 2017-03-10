package com.kamino.filemanager.device;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("unused")
public final class DeviceManager {
	private static String TAG = "DeviceManager";
	private ArrayList<String> localDevicesList;
	private ArrayList<String> sdDevicesList;
	private ArrayList<String> usbDevicesList;
	private ArrayList<String> sataDevicesList;
	private ArrayList<String> internalDevicesList;
	private Context mContext;
	private StorageManager mStorageManager;

	public DeviceManager(Context context) {
		int idx = 0;
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
		//得到的是所有的存储路径
		String[] volumePaths;
		try {
			volumePaths = (String[]) getVolPathMethod.invoke(mStorageManager,
					new Object[0]);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			volumePaths = null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			volumePaths = null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			volumePaths = null;
		}

		//获取的是sdcard的路径  内置和外置
		for (int i = 0; i < volumePaths.length; ++i) {
			localDevicesList.add(volumePaths[i]);

		}

		internalDevicesList = new ArrayList<String>();
		//有外置SD卡，获取的就是外置的SD卡 没有就是获取的内置的SD卡
		internalDevicesList.add(Environment.getExternalStorageDirectory()
				.getPath());
		sdDevicesList = new ArrayList<String>();
		usbDevicesList = new ArrayList<String>();

		for (sataDevicesList = new ArrayList<String>(); idx < localDevicesList
				.size(); ++idx) {
			String device = (String) localDevicesList.get(idx);
			Log.e("测试外设路径",""+device);
			if (!device.equals(Environment.getExternalStorageDirectory()
					.getPath())) {
				if(device.contains("usb") || device.contains("uhost")){
					//存储USB路径
					Log.e("测试外设路径USB",""+device);
					usbDevicesList.add(device);
				}else if(device.contains("sata")) {
					//存储硬盘的路径
					Log.e("测试外设路径SATA",""+device);
					sataDevicesList.add(device);
				}else{
					if(sdDevicesList.size()==0){
						//存储SD卡路径
						Log.e("测试外设路径SD",""+device);
						sdDevicesList.add(device);
					}else{
						//存储USB路径
						Log.e("测试外设路径USB",""+device);
						usbDevicesList.add(device);
					}
				}


			}
		}

	}

	@SuppressWarnings("unchecked")
	public final ArrayList<String> getSdDevicesList() {
		return (ArrayList<String>) sdDevicesList.clone();
	}

	@SuppressWarnings("unchecked")
	public final ArrayList<String> getUsbDevicesList() {
		return (ArrayList<String>) usbDevicesList.clone();
	}

	@SuppressWarnings("unchecked")
	public final ArrayList<String> getInternalDevicesList() {
		return (ArrayList<String>) internalDevicesList.clone();
	}
}
