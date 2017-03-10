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
		APKEntity storeapk = new APKEntity("���ֲ���", R.drawable.headset, "31.1M", "֧������:VR�۾�", 
				"���:��Western VR���ǰ�׿ƽ̨��һ��������ʵFPS��Ϸ,��������������ʵ���������һ������ţ��,"
						+ "�������˫ǹ��ǰ����ս�Ļ�����һ������!ţ��,�ϰ�!����Ϸ��ʹ��СG�ٿ�!", 
						"http://baidu.com", R.drawable.ic_default, R.drawable.ic_default, 
						R.drawable.ic_default, R.drawable.ic_default, "com.kamino.store");
		if(apklists.size() == 0){
			for (int i = 0; i <6; i++) {
				apklists.add(storeapk);
			}
		}
		
		packageManager = context.getPackageManager();
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
			//���ݰ�����ȡӦ��ͼ�� ��������Lrucache��������
			Bitmap bmp = getBitmapFromPackageName(pkgName);
			if (bmp != null) {
				bitmap.put(pkgName, bmp);
			}

			return bmp;
		}
	}
	
	//�Ƴ�����������Ǳ���Ҫ�����£���ΪͼƬ���洦�����ͻᱨ�ڴ����������һ��Ҫ����ע�⡣
	//�������
	public void clear() {
		if (bitmap != null) {
			bitmap.evictAll();
		}
	}
}
