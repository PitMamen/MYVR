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
		//�㲥���豸���°�װ��һ��Ӧ�ó������
		if (TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
			installed = true;
		//�ɹ���ɾ��ĳ��APK֮�󷢳��Ĺ㲥
		} else if (TextUtils.equals(action,
				"android.intent.action.PACKAGE_REMOVED")) {
			installed = false;
		} else {
			//�滻һ�����еİ�װ��ʱ�����Ĺ㲥���������ڰ�װ��APP��֮ǰ���»��Ǿɣ����ᷢ���˹㲥����
			boolean replacing = intent.getBooleanExtra(
					"android.intent.extra.REPLACING", false);
			if (replacing) {
				installed = true;
			} else {
				installed = false;
			}
		}

		//����mAppStateListeners���ϵ�ÿһ��Ԫ�أ�itertor��ö������ö������Java�ں�̨�Զ�����ã��ȴ����forѭ��Ҫ��
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
