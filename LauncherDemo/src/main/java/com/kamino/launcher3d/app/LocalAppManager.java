package com.kamino.launcher3d.app;

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

		//这里(Runtime.getRuntime().maxMemory() / 1024L) / 64是指当前LruCache缓存数据最大量
		bitmap = new LruCache<String, Bitmap>((int) (Runtime.getRuntime()
				.maxMemory() / 1024L) / 64) { 
			//如果你想要知道你的LruCache在悄悄删除数据的时候能够告知你的话,你就需要重写这个方法
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

			//当前缓存的是BitmapDrawable类型的图片数据,则返回的就是这个图片的内存大小,否则返回1.
			//当sizeOf方法返回1的时候,就表示当前的LruCache缓存的数据数量是指键值对数量.
			@SuppressLint({ "NewApi" })
			protected int sizeOf(String key, Bitmap value) {
				return VERSION.SDK_INT >= 12 ? value.getByteCount() / 1024
						: value.getRowBytes() / 1024;
			}
		};
	}

	//根据包名获取APPData对象
	private AppData getAppInfo(String pkgName) {
		AppData appData = new AppData();
		try {
			//拿到packageInfo对象
			PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
			// 获得应用程序的Label  
			String label = (String) packageInfo.applicationInfo
					.loadLabel(packageManager);
			//是系统的APP
			if ((0x1 & packageInfo.applicationInfo.flags) > 0) {
				appData.setSystemApp(true);
			//是用户下载的APP
			} else {
				appData.setSystemApp(false);
			}
			getAppBitmap(pkgName);
			appData.setLabel(label);
			appData.setPackageName(pkgName);
			return appData;
		} catch (PackageManager.NameNotFoundException ex) {
			ex.printStackTrace();
			return appData;
		}
	}

	//根据包名获取应用图标
	private Bitmap getBitmapFromPackageName(String pkgName) {
		if (pkgName == null) {
			return null;
		} else {
			try {
				//根据包名获取应用图标
				Bitmap bmp = drawableToBitmap(packageManager.getPackageInfo(
						pkgName, 0).applicationInfo.loadIcon(packageManager));
				return bmp;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	//初始化LocalAppManager
	public static LocalAppManager getInstance(Context context) {
		if (self == null) {
			self = new LocalAppManager(context);
		} else {
			self.setContext(context);
		}

		return self;
	}

	//移除和清除缓存是必须要做的事，因为图片缓存处理不当就会报内存溢出，所以一定要引起注意。
	//清除缓冲
	public void clear() {
		if (bitmap != null) {
			bitmap.evictAll();
		}
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		Config config;
		if (drawable.getOpacity() != -1) {
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

	//得到所有可以启动的APP数据并封装到AppData集合里面
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
		//得到所有可以启动的APP程序
		intent = new Intent("android.intent.action.MAIN", null);
		intent.addCategory("android.intent.category.LAUNCHER");
		resolveList = packageManager.queryIntentActivities(intent, 0);
		//利用比较器对resolveList集合重新排序
		Collections.sort(resolveList,
				new LocalAppManager.DisplayTimeComparator(packageManager));
		for (ResolveInfo info : resolveList) {
			appData = new AppData();
			packageName = info.activityInfo.packageName;
			label = (String) info.loadLabel(packageManager);
			//设置标签
			appData.setLabel(label);
			//设置包名
			appData.setPackageName(packageName);
			//将应用图标放在LruCache<String, Bitmap> bitmap里面
			getAppBitmap(packageName);
			//判断是否是系统的APP
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
			//将list集合里面所有的内容加到datalist里面 datalist.size()=list.size()
			datalist.addAll(list);
		}

		return datalist;
	}

	public Bitmap getAppBitmap(String pkgName) {
		if (bitmap.get(pkgName) != null) {
			return (Bitmap) bitmap.get(pkgName);
		} else {
			//根据包名获取应用图标 并保存在Lrucache集合里面
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

	//看包名是不是为null 不为就是好的
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
			//isInstall表示包名是否还存在 false表示被卸载 true表示存在
			//被卸载
			if (!isInstall) {
				ArrayList<AppData> list = new ArrayList<AppData>();
				//itertor即枚举器，枚举器是Java在后台自动排序好，比纯粹的for循环要快，
				//因为如果用 for循环，也需要将List中的对象依次取出，这样对内存的调用是随机的，不如Iterator。
				//iterator遍历datalist集合里面的内容，将其放到iter集合中
				Iterator<AppData> iter = datalist.iterator();

				while (iter.hasNext()) {
					AppData app = (AppData) iter.next();
					//判断卸载的程序是否是所有的程序中间的一个，是的话就加到一个集合里面
					if (app.getPackageName().equals(pkgName)) {
						list.add(app);
					}
				}

				//如果是自己定义的APP则在其里面移除list的所有内容
				if (isPreloadApp(pkgName)) {
					preloadList.removeAll(list);
				} else {
				//如果是自己定义的APP则在其里面移除list的所有内容
					unPreloadList.removeAll(list);
				}
				//最后datalist也要移除list的所有内容
				datalist.removeAll(list);
				//还在的应用程序
			} else {
				//根据包名拿到的应用程序的包装类AppData
				AppData app = getAppInfo(pkgName);
				//如果APP不在可以启动的集合里
				if (!datalist.contains(app)) {
					//是有效的包名
					if (isPreloadApp(pkgName)) {
						preloadList.add(app);
					//不是有效的包名
					} else {
						unPreloadList.add(app);
					}
				}

				//就是说把所有类型的APP加到datalist集合里面
				//datalist.clear();
				datalist.addAll(0,preloadList);
				datalist.addAll(0,unPreloadList);
			}
		}
	}

	//比较器
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
				//应用第一次安装的时间 
				time1 = mPM.getPackageInfo(info1.activityInfo.packageName, 0).firstInstallTime;
				time2 = mPM.getPackageInfo(info2.activityInfo.packageName, 0).firstInstallTime;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			//比较两个应用的安装时间应用1先安装返回1  应用1后安装返回-1
			return time1 == time2 ? 0 : (time1 > time2 ? -1 : 1);
		}
	}
}
