package com.my.mylauncher.app;

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
		if (TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
			installed = true;
		} else if (TextUtils.equals(action,
				"android.intent.action.PACKAGE_REMOVED")) {
			installed = false;
		} else {
			boolean replacing = intent.getBooleanExtra(
					"android.intent.extra.REPLACING", false);
			if (replacing) {
				installed = true;
			} else {
				installed = false;
			}
		}

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
