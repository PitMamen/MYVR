package com.kamino.launcher3d.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppManagerReceiver extends BroadcastReceiver {
	private static final String TAG = AppManagerReceiver.class.getSimpleName();
	private List<OnAppStateChangeListener> mAppStateListeners = new ArrayList<OnAppStateChangeListener>();

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String[] pkgs = intent.getData().toString().split("\\:");
		String pkgName = pkgs[-1 + pkgs.length];
		boolean installed;
		//广播：设备上新安装了一个应用程序包。
		if (TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
			installed = true;
		//成功的删除某个APK之后发出的广播
		} else if (TextUtils.equals(action,
				"android.intent.action.PACKAGE_REMOVED")) {
			installed = false;
		} else {
			//替换一个现有的安装包时发出的广播（不管现在安装的APP比之前的新还是旧，都会发出此广播？）
			boolean replacing = intent.getBooleanExtra(
					"android.intent.extra.REPLACING", false);
			if (replacing) {
				installed = true;
			} else {
				installed = false;
			}
		}

		//遍历mAppStateListeners集合的每一个元素，itertor即枚举器，枚举器是Java在后台自动排序好，比纯粹的for循环要快
		Iterator<OnAppStateChangeListener> iter = mAppStateListeners.iterator();

		while (iter.hasNext()) {
			iter.next().onAppStateChanged(pkgName, installed);
		}
	}

	public void registerAppStateChangeListener(OnAppStateChangeListener listener) {
		if (listener != null && !mAppStateListeners.contains(listener)) {
			mAppStateListeners.add(listener);
		}
	}

	public void unregisterAppStateChangeListener(
			OnAppStateChangeListener listener) {
		if (listener != null && mAppStateListeners.contains(listener)) {
			mAppStateListeners.remove(listener);
		}
	}
}
