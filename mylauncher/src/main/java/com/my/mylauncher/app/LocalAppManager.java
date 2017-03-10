package com.my.mylauncher.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.util.LruCache;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LocalAppManager {
	public static final Comparator<AppData> ALPHA_COMPARATOR = new Comparator<AppData>() {
		private final Collator sCollator = Collator.getInstance();
		public int compare(AppData app1, AppData app2) {
			return sCollator.compare(app1.getLabel(), app2.getLabel());
		}
	};
	private static final int ICON_SIZE = 72;
	// private static final String TAG = "LocalAppManager";
	private static List<String> packageList = new ArrayList<String>();
	private static LocalAppManager self;
	private LruCache<String, Bitmap> bitmap;
	private List<AppData> datalist;
	@SuppressWarnings("unused")
	private Context mContext;
	private PackageManager packageManager;
	private String[] packageNames;
	private List<AppData> preloadList;
	private boolean mState;
	private List<AppData> unPreloadList;

	private LocalAppManager(Context context) {
		datalist = new ArrayList<AppData>();
		preloadList = new ArrayList<AppData>();
		unPreloadList = new ArrayList<AppData>();
		packageNames = new String[] { "com.kamino.player", "com.kamino.pptv",
				"com.kamino.gallery", "com.kamino.gallery360",
				"com.kamino.settings", "com.kamino.filemanager" };
		mContext = context;
		packageManager = context.getPackageManager();

		for (int i = 0; i < packageNames.length; ++i) {
			packageList.add(packageNames[i]);
		}

		bitmap = new LruCache<String, Bitmap>((int) (Runtime.getRuntime()
				.maxMemory() / 1024L) / 64) { 
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

			@SuppressLint({ "NewApi" })
			protected int sizeOf(String key, Bitmap value) {
				return VERSION.SDK_INT >= 12 ? value.getByteCount() / 1024
						: value.getRowBytes() / 1024;
			}
		};
	}

	private AppData getAppInfo(String pkgName) {
		AppData appData = new AppData();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
			String label = (String) packageInfo.applicationInfo
					.loadLabel(packageManager);
			if ((0x1 & packageInfo.applicationInfo.flags) > 0) {
				appData.setSystemApp(true);
			} else {
				appData.setSystemApp(false);
			}
			getAppBitmap(pkgName);
			appData.setLabel(label);
			appData.setPackageName(pkgName);
			return appData;
		} catch (NameNotFoundException ex) {
			ex.printStackTrace();
			return appData;
		}
	}

	private Bitmap getBitmapFromPackageName(String pkgName) {
		if (pkgName == null) {
			return null;
		} else {
			try {
				Bitmap bmp = drawableToBitmap(packageManager.getPackageInfo(
						pkgName, 0).applicationInfo.loadIcon(packageManager));
				return bmp;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static LocalAppManager getInstance(Context context) {
		if (self == null) {
			self = new LocalAppManager(context);
		} else {
			self.setContext(context);
		}

		return self;
	}

	public void clear() {
		if (bitmap != null) {
			bitmap.evictAll();
		}
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		Config config;
		if (drawable.getOpacity() != PixelFormat.OPAQUE) {
			config = Config.ARGB_8888;
		} else {
			config = Config.RGB_565;
		}

		Bitmap bmp = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, config);
		Canvas canvas = new Canvas(bmp);
		drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
		drawable.draw(canvas);
		return bmp;
	}

	@SuppressWarnings("null")
	@SuppressLint({ "NewApi" })
	public List<AppData> getAllLocalApp() {
		ArrayList<AppData> list = null;
		ArrayList<AppData> list_SystemApp = new ArrayList<AppData>();
		ArrayList<AppData> list_Userapp = new ArrayList<AppData>();
		Intent intent;
		List<ResolveInfo> resolveList;
		AppData appData = null;
		String packageName = null;
		String label;

		list = new ArrayList<AppData>();
		intent = new Intent("android.intent.action.MAIN", null);
		intent.addCategory("android.intent.category.LAUNCHER");
		resolveList = packageManager.queryIntentActivities(intent, 0);
		Collections.sort(resolveList,
				new LocalAppManager.DisplayTimeComparator(packageManager));
		for (ResolveInfo info : resolveList) {
			appData = new AppData();
			packageName = info.activityInfo.packageName;
			label = (String) info.loadLabel(packageManager);
			appData.setLabel(label);
			appData.setPackageName(packageName);
			getAppBitmap(packageName);
			if ((ApplicationInfo.FLAG_SYSTEM & info.activityInfo.applicationInfo.flags) <= 0) {
				appData.setSystemApp(false);
				list_Userapp.add(appData);
			} else {
				appData.setSystemApp(true);
				list_SystemApp.add(appData);
			}
			
			
		}
		list.addAll(list_Userapp);
		list.addAll(list_SystemApp);
		synchronized (this) {
			if (datalist != null) {
				datalist.clear();
			}
			if (preloadList != null) {
				preloadList.clear();
			}
			if (unPreloadList != null) {
				unPreloadList.clear();
			}
			datalist.addAll(list);
		}

		return datalist;
	}

	public Bitmap getAppBitmap(String pkgName) {
		if (bitmap.get(pkgName) != null) {
			return (Bitmap) bitmap.get(pkgName);
		} else {
			Bitmap bmp = getBitmapFromPackageName(pkgName);
			if (bmp != null) {
				bitmap.put(pkgName, bmp);
			}

			return bmp;
		}
	}

	public List<AppData> getAppList() {
		return datalist;
	}

	public boolean isPreloadApp(String pkgName) {
		return pkgName != null && !pkgName.equals("") ? packageList
				.contains(pkgName) : false;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public void setState(boolean state) {
		mState = state;
	}

	public void updateData(String pkgName, boolean isInstall) {
		if (mState) {
			if (!isInstall) {
				ArrayList<AppData> list = new ArrayList<AppData>();
				Iterator<AppData> iter = datalist.iterator();

				while (iter.hasNext()) {
					AppData app = (AppData) iter.next();
					if (app.getPackageName().equals(pkgName)) {
						list.add(app);
					}
				}

				if (isPreloadApp(pkgName)) {
					preloadList.removeAll(list);
				} else {
					unPreloadList.removeAll(list);
				}
				datalist.removeAll(list);
			} else {
				AppData app = getAppInfo(pkgName);
				if (!datalist.contains(app)) {
					if (isPreloadApp(pkgName)) {
						preloadList.add(app);
					} else {
						unPreloadList.add(app);
					}
				}

				//datalist.clear();
				datalist.addAll(0,preloadList);
				datalist.addAll(0,unPreloadList);
			}
		}
	}

	public static class DisplayTimeComparator implements
			Comparator<ResolveInfo> {
		private PackageManager mPM;

		public DisplayTimeComparator(PackageManager pm) {
			mPM = pm;
		}

		@SuppressLint({ "NewApi" })
		public final int compare(ResolveInfo info1, ResolveInfo info2) {
			long time1 = 0L;
			long time2 = 0L;

			try {
				time1 = mPM.getPackageInfo(info1.activityInfo.packageName, 0).firstInstallTime;
				time2 = mPM.getPackageInfo(info2.activityInfo.packageName, 0).firstInstallTime;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return time1 == time2 ? 0 : (time1 > time2 ? -1 : 1);
		}
	}
}
