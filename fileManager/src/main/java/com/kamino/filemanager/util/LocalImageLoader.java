package com.kamino.filemanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.v4.util.LruCache;

import com.kamino.filemanager.file.Util;

import java.io.File;
import java.io.InputStream;

public final class LocalImageLoader {
	private static LocalImageLoader mInstance = new LocalImageLoader();
	private LruCache<String, Bitmap> mMemoryCache = null;

	private LocalImageLoader() {
		//覆盖  sizeOf(K, V) 以不同单位调整缓存大小。在这里，此缓存限制为Runtime.getRuntime().maxMemory() / 1024L) / 4的位图：
		//Runtime.getRuntime().maxMemory() java虚拟机能够从操作系统那里挖到的最大内存
		//一般没特殊定义就是64*1024*1024 也就是64M
		mMemoryCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime()
				.maxMemory() / 1024L) / 4) {
			protected int sizeOf(String key, Bitmap value) {
				//getByteCount() = getRowBytes() * getHeight()，
				//也就是说位图所占用的内存空间数等于位图的每一行所占用的空间数乘以位图的行数
				//getByteCount()要求的API版本比较高 就用了下面的这样的算法
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};
	}

	private static int computeScale(Options opt, int width, int height) {
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

	@SuppressWarnings("deprecation")
	public static Bitmap decodeThumbBitmapForFile(String path, int width,
			int height) {
		if (!(new File(path)).exists()) {
			return null;
		} else {
			Options opt = new Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opt);
			opt.inSampleSize = computeScale(opt, width, height);
			opt.inPreferredConfig = Config.ARGB_8888;
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
		/*Bitmap bmp = Bitmap.createBitmap(2,2,Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);

		canvas.drawRect(0,0,2,2,new Paint(Color.RED));

		return bmp;*/
	}

	public static LocalImageLoader getInstance() {
		return mInstance;
	}

	private void addBitmapToMemoryCache(String key, Bitmap bmp) {
		if (getBitmapFromMemCache(key) == null && bmp != null) {
			synchronized (mMemoryCache) {
				mMemoryCache.put(key, bmp);
			}
		}

	}

	@SuppressWarnings("deprecation")
	private static Bitmap loadBitmap(Context context, int resId) {
		InputStream in = context.getResources().openRawResource(resId);
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, (Rect) null, opt);
		opt.inSampleSize = computeScale((Options) opt, 2048, 1024);
		opt.inPreferredConfig = Config.ARGB_8888;
		opt.inJustDecodeBounds = false;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		try {
			Bitmap bmp = BitmapFactory.decodeStream(in, (Rect) null, opt);
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Bitmap getBitmapFromMemCache(final String key) {
		synchronized (mMemoryCache) {
			final Bitmap bitmap = (Bitmap) mMemoryCache.get(key);
			if (bitmap != null) {
				mMemoryCache.remove(key);
				mMemoryCache.put(key, bitmap);
				return bitmap;
			}
			return null;
		}
	}

	public final Bitmap getLocalBitmap(Context context, int key) {
		Bitmap bmp = getBitmapFromMemCache(String.valueOf(key));
		if (bmp == null) {
			bmp = loadBitmap(context, key);
			if (bmp != null) {
				addBitmapToMemoryCache(String.valueOf(key), bmp);
			}
		}

		return bmp;
	}

	public final Bitmap getAppIcon(Context context, String key) {
		Bitmap cacheBmp = getBitmapFromMemCache(key);
		if (cacheBmp == null) {
			Drawable drawable = Util.getAppIcon(context, key);
			if (drawable instanceof BitmapDrawable) {
				cacheBmp = ((BitmapDrawable) drawable).getBitmap();
			} else if (drawable instanceof NinePatchDrawable) {
				int width = drawable.getIntrinsicWidth();
				int height = drawable.getIntrinsicHeight();
				Config config;
				if (drawable.getOpacity() != PixelFormat.OPAQUE) {
					config = Config.ARGB_8888;
				} else {
					config = Config.RGB_565;
				}

				Bitmap bmp = Bitmap.createBitmap(width, height, config);
				Canvas canvas = new Canvas(bmp);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				drawable.draw(canvas);
				cacheBmp = bmp;
			} else {
				cacheBmp = null;
			}

			if (cacheBmp != null) {
				addBitmapToMemoryCache(key, cacheBmp);
			}
		}

		return cacheBmp;
	}

	public final Bitmap generateVideoThumbnail(String key) {
		Bitmap bmp = getBitmapFromMemCache(key);
		if (bmp == null) {
			bmp = ThumbnailUtils.extractThumbnail(
					ThumbnailUtils.createVideoThumbnail(key, 1), 256, 256, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			if (bmp != null) {
				addBitmapToMemoryCache(key, bmp);
			}
		}

		return bmp;
	}

	public final void loadThumbCache(final Context context) {
		synchronized (this) {
			String path;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable()) {
				path = context.getExternalCacheDir().getPath();
			} else {
				path = context.getCacheDir().getPath();
			}
			final File file = new File(String.valueOf(path) + File.separator
					+ "thumb");
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	public final Bitmap getLocalBitmap(String key) {
		Bitmap bmp = getBitmapFromMemCache(key);
		if (bmp == null) {
			bmp = decodeThumbBitmapForFile((String) key, 256, 256);
			if (bmp != null) {
				addBitmapToMemoryCache(key, bmp);
			}
		}

		return bmp;
	}
}
