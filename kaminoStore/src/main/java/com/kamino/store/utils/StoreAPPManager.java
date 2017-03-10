package com.kamino.store.utils;

import java.util.ArrayList;

import com.kamino.store.R;
import com.kamino.store.entity.APKEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.util.LruCache;

public class StoreAPPManager {
	private static StoreAPPManager self;
	private ArrayList<APKEntity> apklists = new ArrayList<APKEntity>();
	private Context mcontext;
	private PackageManager packageManager;
	private LruCache<String, Bitmap> bitmap;
	private static final int ICON_SIZE = 72;
	public StoreAPPManager(Context context){
		mcontext = context;
		APKEntity storeapk = new APKEntity("特种部队", R.drawable.headset, "31.1M", "支持外设:VR眼镜", 
				"简介:《Western VR》是安卓平台的一款虚拟现实FPS游戏,它让你在虚拟现实世界里变身一名西部牛仔,"
						+ "举起你的双枪和前来挑战的坏人们一决高下!牛仔,上吧!此游戏可使用小G操控!", 
						"http://baidu.com", R.drawable.ic_default, R.drawable.ic_default, 
						R.drawable.ic_default, R.drawable.ic_default, "com.kamino.store");
		if(apklists.size() == 0){
			for (int i = 0; i <6; i++) {
				apklists.add(storeapk);
			}
		}
		
		packageManager = context.getPackageManager();
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
	public ArrayList<APKEntity> getStoreAPKs(){
		return apklists;
	}
	
	public static StoreAPPManager getInstance(Context context){
		if(self == null){
			self = new StoreAPPManager(context);
		}else{
			self.setContext(context);
		}
		
		return self;
	}
	
	public void setContext(Context context){
		this.mcontext = context;
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
	
	//移除和清除缓存是必须要做的事，因为图片缓存处理不当就会报内存溢出，所以一定要引起注意。
	//清除缓冲
	public void clear() {
		if (bitmap != null) {
			bitmap.evictAll();
		}
	}
}
