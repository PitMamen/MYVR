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

		//����(Runtime.getRuntime().maxMemory() / 1024L) / 64��ָ��ǰLruCache�������������
		bitmap = new LruCache<String, Bitmap>((int) (Runtime.getRuntime()
				.maxMemory() / 1024L) / 64) { 
			//�������Ҫ֪�����LruCache������ɾ�����ݵ�ʱ���ܹ���֪��Ļ�,�����Ҫ��д�������
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

			//��ǰ�������BitmapDrawable���͵�ͼƬ����,�򷵻صľ������ͼƬ���ڴ��С,���򷵻�1.
			//��sizeOf��������1��ʱ��,�ͱ�ʾ��ǰ��LruCache���������������ָ��ֵ������.
			@SuppressLint({ "NewApi" })
			protected int sizeOf(String key, Bitmap value) {
				return VERSION.SDK_INT >= 12 ? value.getByteCount() / 1024
						: value.getRowBytes() / 1024;
			}
		};
	}

	//���ݰ�����ȡAPPData����
	private AppData getAppInfo(String pkgName) {
		AppData appData = new AppData();
		try {
			//�õ�packageInfo����
			PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
			// ���Ӧ�ó����Label  
			String label = (String) packageInfo.applicationInfo
					.loadLabel(packageManager);
			//��ϵͳ��APP
			if ((0x1 & packageInfo.applicationInfo.flags) > 0) {
				appData.setSystemApp(true);
			//���û����ص�APP
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

	//���ݰ�����ȡӦ��ͼ��
	private Bitmap getBitmapFromPackageName(String pkgName) {
		if (pkgName == null) {
			return null;
		} else {
			try {
				//���ݰ�����ȡӦ��ͼ��
				Bitmap bmp = drawableToBitmap(packageManager.getPackageInfo(
						pkgName, 0).applicationInfo.loadIcon(packageManager));
				return bmp;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	//��ʼ��LocalAppManager
	public static LocalAppManager getInstance(Context context) {
		if (self == null) {
			self = new LocalAppManager(context);
		} else {
			self.setContext(context);
		}

		return self;
	}

	//�Ƴ�����������Ǳ���Ҫ�����£���ΪͼƬ���洦�����ͻᱨ�ڴ����������һ��Ҫ����ע�⡣
	//�������
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

	//�õ����п���������APP���ݲ���װ��AppData��������
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
		//�õ����п���������APP����
		intent = new Intent("android.intent.action.MAIN", null);
		intent.addCategory("android.intent.category.LAUNCHER");
		resolveList = packageManager.queryIntentActivities(intent, 0);
		//���ñȽ�����resolveList������������
		Collections.sort(resolveList,
				new LocalAppManager.DisplayTimeComparator(packageManager));
		for (ResolveInfo info : resolveList) {
			appData = new AppData();
			packageName = info.activityInfo.packageName;
			label = (String) info.loadLabel(packageManager);
			//���ñ�ǩ
			appData.setLabel(label);
			//���ð���
			appData.setPackageName(packageName);
			//��Ӧ��ͼ�����LruCache<String, Bitmap> bitmap����
			getAppBitmap(packageName);
			//�ж��Ƿ���ϵͳ��APP
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
			//��list�����������е����ݼӵ�datalist���� datalist.size()=list.size()
			datalist.addAll(list);
		}

		return datalist;
	}

	public Bitmap getAppBitmap(String pkgName) {
		if (bitmap.get(pkgName) != null) {
			return (Bitmap) bitmap.get(pkgName);
		} else {
			//���ݰ�����ȡӦ��ͼ�� ��������Lrucache��������
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

	//�������ǲ���Ϊnull ��Ϊ���Ǻõ�
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
			//isInstall��ʾ�����Ƿ񻹴��� false��ʾ��ж�� true��ʾ����
			//��ж��
			if (!isInstall) {
				ArrayList<AppData> list = new ArrayList<AppData>();
				//itertor��ö������ö������Java�ں�̨�Զ�����ã��ȴ����forѭ��Ҫ�죬
				//��Ϊ����� forѭ����Ҳ��Ҫ��List�еĶ�������ȡ�����������ڴ�ĵ���������ģ�����Iterator��
				//iterator����datalist������������ݣ�����ŵ�iter������
				Iterator<AppData> iter = datalist.iterator();

				while (iter.hasNext()) {
					AppData app = (AppData) iter.next();
					//�ж�ж�صĳ����Ƿ������еĳ����м��һ�����ǵĻ��ͼӵ�һ����������
					if (app.getPackageName().equals(pkgName)) {
						list.add(app);
					}
				}

				//������Լ������APP�����������Ƴ�list����������
				if (isPreloadApp(pkgName)) {
					preloadList.removeAll(list);
				} else {
				//������Լ������APP�����������Ƴ�list����������
					unPreloadList.removeAll(list);
				}
				//���datalistҲҪ�Ƴ�list����������
				datalist.removeAll(list);
				//���ڵ�Ӧ�ó���
			} else {
				//���ݰ����õ���Ӧ�ó���İ�װ��AppData
				AppData app = getAppInfo(pkgName);
				//���APP���ڿ��������ļ�����
				if (!datalist.contains(app)) {
					//����Ч�İ���
					if (isPreloadApp(pkgName)) {
						preloadList.add(app);
					//������Ч�İ���
					} else {
						unPreloadList.add(app);
					}
				}

				//����˵���������͵�APP�ӵ�datalist��������
				//datalist.clear();
				datalist.addAll(0,preloadList);
				datalist.addAll(0,unPreloadList);
			}
		}
	}

	//�Ƚ���
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
				//Ӧ�õ�һ�ΰ�װ��ʱ�� 
				time1 = mPM.getPackageInfo(info1.activityInfo.packageName, 0).firstInstallTime;
				time2 = mPM.getPackageInfo(info2.activityInfo.packageName, 0).firstInstallTime;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			//�Ƚ�����Ӧ�õİ�װʱ��Ӧ��1�Ȱ�װ����1  Ӧ��1��װ����-1
			return time1 == time2 ? 0 : (time1 > time2 ? -1 : 1);
		}
	}
}
