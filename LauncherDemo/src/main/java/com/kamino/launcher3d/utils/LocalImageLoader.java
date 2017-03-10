package com.kamino.launcher3d.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.support.v4.util.LruCache;

public class LocalImageLoader {
	private static LocalImageLoader mInstance = new LocalImageLoader();
	private LruCache<String, Bitmap> mMemoryCache = null;

	private LocalImageLoader() {
		mMemoryCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime()
				.maxMemory() / 1024L) / 4) {
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};
	}

	private void addBitmapToMemoryCache(String s, Bitmap bitmap) {
		if (getBitmapFromMemCache(s) == null && bitmap != null) {
			synchronized (mMemoryCache) {
				mMemoryCache.put(s, bitmap);
			}
		}
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; ++i) {
			String numStr = Integer.toHexString(255 & bytes[i]);
			if (numStr.length() == 1) {
				sb.append('0');
			}

			sb.append(numStr);
		}

		return sb.toString();
	}

	private int computeScale(Options opt, int width, int height) {
		int oh = opt.outHeight;
		int ow = opt.outWidth;
		int scale = 1;
		if (oh > height || ow > width) {
			scale = Math.round((float) oh / (float) height);
			int newScale = Math.round((float) ow / (float) width);
			if (scale >= newScale) {
				scale = newScale;
			}

			float size = (float) (ow * oh);

			for (float doubleSize = (float) (2 * width * height); size
					/ (float) (scale * scale) > doubleSize; ++scale) {
				;
			}
		}

		return scale;
	}

	private Bitmap getBitmapFromMemCache(String s) {
		synchronized (mMemoryCache) {
			Bitmap bmp = mMemoryCache.get(s);
			if (bmp != null) {
				mMemoryCache.remove(s);
				mMemoryCache.put(s, bmp);
				return bmp;
			}
			return null;
		}
	}

	public static LocalImageLoader getInstance() {
		return mInstance;
	}

	@SuppressWarnings("deprecation")
	public Bitmap decodeThumbBitmapForFile(String path, int width, int height) {
		if (!(new File(path)).exists()) {
			return null;
		} else {
			Options opt = new Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opt);
			opt.inSampleSize = computeScale(opt, width, height);
			opt.inPreferredConfig = Config.RGB_565;
			opt.inJustDecodeBounds = false;
			opt.inPurgeable = true;
			opt.inInputShareable = true;

			try {
				Bitmap bmp = BitmapFactory.decodeFile(path, opt);
				return bmp;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public int getAppVersion(Context context) {
		try {
			int version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 1;
		}
	}

	@SuppressLint("NewApi")
	public File getDiskCacheDir(Context context, String dir) {
		String path;
		if (!"mounted".equals(Environment.getExternalStorageState())
				&& Environment.isExternalStorageRemovable()) {
			path = context.getCacheDir().getPath();
		} else {
			path = context.getExternalCacheDir().getPath();
		}

		return new File(path + File.separator + dir);
	}

	public Bitmap getLocalBitmap(String key, int width, int height) {
		Bitmap bmp = decodeThumbBitmapForFile(key, width, height);
		if (bmp != null) {
			addBitmapToMemoryCache(key, bmp);
		}

		return bmp;
	}

	public String hashKeyForDisk(String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			String hash = bytesToHexString(md.digest());
			return hash;
		} catch (NoSuchAlgorithmException e) {
			return String.valueOf(key.hashCode());
		}
	}

	public void init(Context context) {
		File dir = getDiskCacheDir(context, "thumb");
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public Bitmap loadImage(String key, int width, int height) {
		Bitmap bmp = getBitmapFromMemCache(key);
		if (bmp == null) {
			bmp = getLocalBitmap(key, width, height);
		}

		return bmp;
	}
}
