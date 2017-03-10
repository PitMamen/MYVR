package com.kamino.store.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Environment;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {
	static int B = 255;
	static int G = 255;
	static int R = 255;
	// 设置的是本地APP里面所有APP的宽和高
	public static final int ICON_BG_HEIGHT = 116;
	public static final int ICON_BG_WIDTH = 100;
	public static final float leftDis = 10.0F;
	public static final float textSize = 24.0F;

	public static boolean checkApkExist(Context context, String name) {
		if (name != null && !"".equals(name)) {
			try {
				context.getPackageManager().getApplicationInfo(name, 8192);
				return true;
			} catch (NameNotFoundException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static int dip2px(Context context, float dip) {
		return (int) (0.5F + dip * context.getResources().getDisplayMetrics().density);
	}

	public static Bitmap getRectBitmap(int color) {
		Bitmap bmp = Bitmap.createBitmap(2, 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(new RectF(0.0F, 0.0F, 2.0F, 2.0F), paint);
		return bmp;
	}

	public static Bitmap getAPKBGBitmap() {
		Bitmap bmp = Bitmap.createBitmap(550, 350, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setARGB(200, 0, 0, 0);
		paint.setAntiAlias(true);
		canvas.drawRect(new RectF(0.0F, 0.0F, 550.0F, 350.0F), paint);
		return bmp;
	}

	public static Bitmap generateWLT(int percent, int width, int height, Bitmap bgBmp, boolean hasBg, String text) {
		if (percent > 100) {
			percent = 100;
		}

		String perText = percent + "%";
		Paint paint = new Paint();
		// 白色
		paint.setARGB(255, R, G, B);
		paint.setTextSize(textSize);
		paint.setTypeface(null);
		paint.setFlags(1);
		Bitmap dstBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		// 10.0F表示往右边移动 36.0F表示往下边移动 画的是时间
		canvas.drawText(text, 10.0F, 36.0F, paint);
		// 画的电池的左边的大矩形
		canvas.drawRect(
				// 左 上 右 下
				new Rect((int) Math.floor(82.399994F), (int) Math.floor(19.2F), (int) Math.floor(113.600006F),
						(int) Math.floor(34.800003F)),
				paint);
		// 画的电池的右边的小矩形
		canvas.drawRect(new Rect((int) Math.floor(113.600006F), (int) Math.floor(19.2F + (34.800003F - 19.2F) / 4.0F),
				(int) Math.floor(113.600006F + (113.600006F - 82.399994F) / 10.0F),
				(int) Math.floor(19.2F + 3.0F * (34.800003F - 19.2F) / 4.0F)), paint);
		// 绿色 画的是电池的电量的矩形
		paint.setARGB(255, 0, 255, 0);
		canvas.drawRect(new Rect(2 + (int) Math.floor(82.399994F), 2 + (int) Math.floor(19.2F),
				(int) (2.0F + 82.399994F + (113.600006F - 2.0F - (2.0F + 82.399994F)) * (percent / 100.0F)),
				-2 + (int) Math.floor(34.800003F)), paint);
		// 白色
		paint.setARGB(255, R, G, B);
		if (hasBg) {
			canvas.drawBitmap(bgBmp, (Rect) null,
					new Rect(
							(int) Math.floor(82.399994F) - ((int) Math.floor(19.2F) - (int) Math.floor(34.800003F)) / 2,
							(int) Math.floor(19.2F),
							(int) Math.floor(113.600006F)
									+ ((int) Math.floor(19.2F) - (int) Math.floor(34.800003F)) / 2,
					(int) Math.floor(34.800003F)), new Paint());
		}
		// 画的是电量的百分比数字
		canvas.drawText(perText, 130.0F, 36.0F, paint);
		return dstBmp;
	}

	// 把字写在中间，字的颜色指定为color
	public static Bitmap generateWLT(String text, float size, int color, int height, Alignment align) {
		Bitmap bmp = Bitmap.createBitmap(256, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		TextPaint paint = new TextPaint();
		paint.setColor(color);
		paint.setTextSize(size);
		// paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setFlags(1);// 消除锯齿
		/*
		 * CharSequence source, 需要分行的字符串 int bufstart, 分行的字符串从第几的位置开始 int
		 * bufend, 分行的字符串从第几的位置结束 TextPaint paint, int outerwidth,
		 * 宽度，字符串超出宽度时自动换行 Alignment align, 有ALIGN_CENTER， ALIGN_NORMAL，
		 * ALIGN_OPPOSITE 三种 float spacingmult, 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度
		 * float spacingadd,. 在基础行距上添加多少实际行间距等于这两者的和。 boolean includepad,
		 * TextUtils.TruncateAt ellipsize, 从什么位置开始省略 int ellipsizedWidth
		 * 超过多少开始省略需要指出的是这layout是默认画在Canvas的(0,0)点的，
		 * 如果需要调整位置只能在draw前移Canvas的起始坐标canvas.translate(x,y);
		 */
		StaticLayout layout = new StaticLayout(text, paint, 256, align, 1.0F, 0.0F, false);
		canvas.translate(0.0F, (float) (height - layout.getHeight()) / 2.0F);
		canvas.save();
		layout.draw(canvas);
		canvas.restore();
		return bmp;
	}

	public static Bitmap generateWLT(String text, float size, int width, int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		TextPaint tp = new TextPaint();
		tp.setColor(-1);
		tp.setTextSize(size);
		// 1)Paint.setAntiAlias(true); 2)Paint.setBitmapFilter(true)。
		// 第一个函数是用来防止边缘的锯齿，让图像看的清楚一些，第二个函数是用来对位图进行滤波处理
		tp.setAntiAlias(true);
		/*
		 * 1.字符串子资源 2 .画笔对象 3.layout的宽度，字符串超出宽度时自动换行。 4.layout的样式，有ALIGN_CENTER，
		 * ALIGN_NORMAL， ALIGN_OPPOSITE 三种。 5.相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
		 * 6.相对行间距，0表示0个像素。 实际行间距等于这两者的和。 7.还不知道是什么意思，参数名是boolean includepad。
		 * 需要指出的是这个layout是默认画在Canvas的(0,0)点的，如果需要调整位置只能在draw之前移Canvas的起始坐标
		 * canvas.translate(x,y);
		 */
		StaticLayout layout = new StaticLayout(text, tp, width, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
		// canvas.save();和canvas.restore();是两个相互匹配出现的，作用是用来保存画布的状态和取出保存的状态的。
		canvas.save();
		layout.draw(canvas);
		canvas.restore();
		return bmp;
	}

	public static Bitmap generateWLT(Alignment align, String text, float size, int width, int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		TextPaint tp = new TextPaint();
		tp.setColor(-1);
		tp.setTextSize(size);
		// 1)Paint.setAntiAlias(true); 2)Paint.setBitmapFilter(true)。
		// 第一个函数是用来防止边缘的锯齿，让图像看的清楚一些，第二个函数是用来对位图进行滤波处理
		tp.setAntiAlias(true);
		/*
		 * 1.字符串子资源 2 .画笔对象 3.layout的宽度，字符串超出宽度时自动换行。 4.layout的样式，有ALIGN_CENTER，
		 * ALIGN_NORMAL， ALIGN_OPPOSITE 三种。 5.相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
		 * 6.相对行间距，0表示0个像素。 实际行间距等于这两者的和。 7.还不知道是什么意思，参数名是boolean includepad。
		 * 需要指出的是这个layout是默认画在Canvas的(0,0)点的，如果需要调整位置只能在draw之前移Canvas的起始坐标
		 * canvas.translate(x,y);
		 */
		StaticLayout layout = new StaticLayout(text, tp, width, align, 1.0F, 0.0F, true);
		// canvas.save();和canvas.restore();是两个相互匹配出现的，作用是用来保存画布的状态和取出保存的状态的。
		canvas.save();
		layout.draw(canvas);
		canvas.restore();
		return bmp;
	}

	public static Bitmap getAblumImage(Bitmap bmp, String name, int count) {
		Bitmap dstBmp = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		Paint paint = new Paint();
		if (bmp != null) {
			canvas.drawBitmap(bmp, null, new RectF(2.0F, 2.0F, 254.0F, 254.0F), paint);
		}

		paint.setARGB(220, 55, 71, 79);
		canvas.drawRect(2.0F, 164.0F, 254.0F, 254.0F, paint);
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(30.0F);
		paint.setTypeface(null);
		paint.setFlags(1);
		paint.setTextAlign(Align.LEFT);
		canvas.drawText(subStringCN(name, 12), 10.0F, 224.0F, paint);
		paint.setTextAlign(Align.RIGHT);
		canvas.drawText(String.valueOf(count), 244.0F, 224.0F, paint);

		return dstBmp;
	}

	public static Bitmap getAblumImage(String name, int count) {
		Bitmap bmp = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setARGB(255, 221, 221, 221);
		canvas.drawRoundRect(new RectF(3.0F, 3.0F, 253.0F, 253.0F), 10.0F, 10.0F, paint);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawRoundRect(new RectF(3.0F, 3.0F, 253.0F, 80.0F), 10.0F, 10.0F, paint);
		paint.setTextSize(30.0F);
		paint.setTypeface(null);
		paint.setFlags(1);
		paint.setTextAlign(Align.CENTER);
		paint.setARGB(255, 68, 68, 68);
		canvas.drawText(subStringCN(name, 16), 128.0F, 50.0F, paint);
		paint.setARGB(255, 78, 78, 78);
		paint.setTextAlign(Align.RIGHT);
		canvas.drawText("+" + count, 236.0F, 236.0F, paint);
		return bmp;
	}

	public static Bitmap getBitmap(Bitmap bmp) {
		Bitmap dstBmp = Bitmap.createBitmap(2 + bmp.getWidth(), 2 + bmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		Paint paint = new Paint();
		if (bmp != null) {
			canvas.drawBitmap(bmp, null, new RectF(1.0F, 1.0F, (1 + bmp.getWidth()), (1 + bmp.getHeight())), paint);
		}

		return dstBmp;
	}

	public static Bitmap getBitmapRect(int width, int height, int color) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(new RectF(0.0F, 0.0F, width, height), paint);
		return bmp;
	}

	public static Bitmap getBitmapRectWithEdge(int width, int height, int color) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(new RectF(2.0F, 2.0F, (width - 2), (height - 2)), paint);
		return bmp;
	}

	public static Point getDeviceSize(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		Point p = new Point();
		p.x = dm.widthPixels;
		p.y = dm.heightPixels;
		return p;
	}

	public static int getDisplayRotation(Activity activity) {
		if (activity != null) {
			switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
			case 0:
				break;
			case 1:
				return 90;
			case 2:
				return 180;
			case 3:
				return 270;
			default:
				return 0;
			}
		}

		return 0;
	}

	public static Bitmap getImageThumbnail(String path, int width, int height) {
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opt);
		opt.inJustDecodeBounds = false;
		int wr = opt.outWidth / width;
		int hr = opt.outHeight / height;
		if (wr < hr) {
			opt.inSampleSize = wr;
		} else {
			opt.inSampleSize = hr;
		}

		if (opt.inSampleSize <= 0) {
			opt.inSampleSize = 1;
		}

		return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path, opt), width, height, 2);
	}

	public static String getTime(long dateTaken) {
		Date date = new Date(dateTaken);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(1) + "\u5e74" + (1 + calendar.get(2)) + "\u6708";
	}

	public static String[] getTimeInfo(long dateTaken) {
		String[] info = new String[3];
		Date date = new Date(dateTaken);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		info[0] = String.valueOf(calendar.get(1));
		info[1] = String.valueOf(1 + calendar.get(2));
		info[2] = String.valueOf(calendar.get(5));
		return info;
	}

	// TODO
	public static int initAppTexture(Bitmap bmp, String name) {
		if (name == null) {
			name = "";
		}

		Bitmap iconBmp = Bitmap.createBitmap(ICON_BG_WIDTH, ICON_BG_HEIGHT, Config.ARGB_8888);
		Bitmap nameBmp = generateWLT(subStringCN(name, 8), 15.0F, 80, 24);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		paint.setARGB(204, 19, 19, 19);
		// 画背景矩形 图片 APP名字
		canvas.drawRect(new Rect(2, 2, 98, 114), paint);
		canvas.drawBitmap(bmp, 14.0F, 10.0F, null);
		canvas.drawBitmap(nameBmp, 10.0F, 82.0F, null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Resources res, int resId) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		InputStream is = res.openRawResource(resId);
		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String text, int color) {
		int[] textures = new int[1];
		Bitmap bmp = generateWLT(text, 32.0F, color, 40, Alignment.ALIGN_CENTER);
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

		// For 900C
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// For H8
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		// For H8
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		bmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Resources res, String title) {
		Bitmap bmp = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		Bitmap titleBmp = generateWLT(subStringCN(title, 8), 8, 100, 90);
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#88000000"));
		paint.setAntiAlias(true);
		// paint.setFilterBitmap(true);
		Canvas canvas = new Canvas(bmp);
		canvas.drawRect(0, 0, 100, 100, paint);
		canvas.drawBitmap(titleBmp, 0, 10, null);
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Resources res, int resId, String up, String down, int width, int height, int color) {
		InputStream is = res.openRawResource(resId);
		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Bitmap iconBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Bitmap UpBmp = generateWLT(subStringCN(up, 8), 30F, 150, 50);
		Bitmap DownBmp = generateWLT(subStringCN(down, 8), 30, 150, 50);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		if (color == Color.BLACK) {
			paint.setColor(Color.parseColor("#88000000"));
		}
		if (color == Color.BLUE) {
			paint.setColor(Color.parseColor("#880000FF"));
		}
		// paint.setARGB(204, 19, 19, 19);
		canvas.drawRect(new Rect(0, 0, width, height), paint);
		canvas.drawBitmap(UpBmp, (width - UpBmp.getWidth()) / 2.0F,
				((height - bmp.getHeight()) / 2.0F - UpBmp.getHeight()) / 2.0F, null);
		canvas.drawBitmap(bmp, (width - bmp.getWidth()) / 2.0F, (height - bmp.getHeight()) / 2.0F, null);
		canvas.drawBitmap(DownBmp, (width - DownBmp.getWidth()) / 2.0F,
				height - (((height - bmp.getHeight()) / 2.0F - DownBmp.getHeight() + 80) / 2.0F), null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Resources res, int resId, String left, String right, int color) {
		InputStream is = res.openRawResource(resId);
		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Bitmap iconBmp = Bitmap.createBitmap(100, 24, Config.ARGB_8888);
		Bitmap leftBmp = generateWLT(subStringCN(left, 8), 7.0F, 20, 15);
		Bitmap rightBmp = generateWLT(right, 7.0F, 20, 15);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		if (color == Color.BLACK) {
			paint.setColor(Color.parseColor("#88000000"));
		}
		if (color == Color.BLUE) {
			paint.setColor(Color.parseColor("#880000FF"));
		}
		// paint.setARGB(204, 19, 19, 19);
		canvas.drawRect(new Rect(2, 2, 98, 20), paint);
		canvas.drawBitmap(leftBmp, 0F, 5.0F, null);
		canvas.drawBitmap(bmp, 100F - bmp.getWidth() - 10, 5.0F, null);
		canvas.drawBitmap(rightBmp, 100F - bmp.getWidth() - rightBmp.getWidth() - 10, 5.0F, null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Context context,String down,int progress, boolean focused) {
		Bitmap iconBmp = Bitmap.createBitmap(200, 50, Config.ARGB_8888);
		//Bitmap downBmp = generateWLT(subStringCN(down, 8), 10.0F, 78, 16);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (focused) {
			paint.setColor(Color.WHITE);
			canvas.drawRect(0, 0, 200, 50, paint);
			paint.setColor(Color.BLACK);
			canvas.drawRect(1, 1, 199, 49, paint);
			paint.setARGB(255, 0, 0, 255);
			canvas.drawRect(1, 1, 2*progress-1, 49, paint);
		} else {
			paint.setARGB(255, 0, 0, 255);
			canvas.drawRect(0, 0, 2*progress, 50, paint);
		}
		//canvas.drawBitmap(downBmp, null, new Rect(1, 1, 79, 17), null);
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(22.0F);
		//paint.setTypeface(null);
		//paint.setFlags(1);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText(down, 100, 32, paint);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		//downBmp.recycle();
		return texture;
	}

	// TODO
	public static int initTexture(Resources res, int resId, String down) {
		InputStream is = res.openRawResource(resId);
		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Bitmap iconBmp = Bitmap.createBitmap(225, 190, Config.ARGB_8888);
		Bitmap downBmp = generateWLT(subStringCN(down, 8), 25.0F, 160, 80);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		// paint.setARGB(204, 19, 19, 19);
		paint.setColor(Color.parseColor("#880000FF"));
		canvas.drawBitmap(bmp, 0, 0, null);
		canvas.drawBitmap(downBmp, 35.0F, 115.0F, null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}

	public static int initTexture(Bitmap bmp) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String text, float size, int width, int height) {
		int[] textures = new int[1];
		Bitmap bmp = generateWLT(text, size, width, height);
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String key, String name, int count) {
		int[] textures = new int[1];
		Bitmap bmp = getAblumImage(LocalImageLoader.getInstance().loadImage(key, 256, 256), name, count);
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String path, boolean rebuild) {
		int[] textures = new int[1];
		Bitmap bmp;
		if (rebuild) {
			bmp = LocalImageLoader.getInstance().decodeThumbBitmapForFile(path, 2048, 2048);
		} else {
			bmp = getBitmap(LocalImageLoader.getInstance().loadImage(path, 256, 256));
		}

		if (bmp != null) {
			GLES20.glGenTextures(1, textures, 0);
			int texture = textures[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
			return texture;
		} else {
			return 0;
		}
	}

	public static Bitmap getdownlAPKBitmap(int width, int height, String title, String size, String besupport,
			String introduction, Bitmap background, Bitmap icon, Bitmap thumbnail1, Bitmap thumbnail2,
			Bitmap thumbnail3, Bitmap thumbnail4, boolean isFocused) {
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(27.0F);
		paint.setTypeface(null);
		paint.setFlags(1);
		Bitmap dstBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		paint.setARGB(205, 19, 19, 19);
		canvas.drawRect(0, 0, width, height, paint);
		//canvas.drawBitmap(background, null, new Rect(0, 0, width, height), null);
		paint.setTextAlign(Align.LEFT);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawText(title, 185, 40, paint);
		canvas.drawBitmap(icon, (Rect) null, new Rect(50, 30, 140, 120), null);
		paint.setTextSize(15.0F);
		if (!isFocused) {
			paint.setARGB(255, 139, 139, 139);
		}
		canvas.drawText(size, 185, 70, paint);
		canvas.drawText(besupport, 185, 90, paint);
		Bitmap introdbmp = generateWLT(Alignment.ALIGN_NORMAL, introduction, 17.0F, width - 40, 200);
		canvas.drawBitmap(introdbmp, 20, 160, null);
		canvas.drawBitmap(thumbnail1, (Rect) null, new Rect(20, 250, 146, 360), null);
		canvas.drawBitmap(thumbnail2, (Rect) null, new Rect(158, 250, 284, 360), null);
		canvas.drawBitmap(thumbnail3, (Rect) null, new Rect(296, 250, 422, 360), null);
		canvas.drawBitmap(thumbnail4, (Rect) null, new Rect(434, 250, 560, 360), null);
		icon.recycle();
		background.recycle();
		introdbmp.recycle();
		thumbnail1.recycle();
		thumbnail2.recycle();
		thumbnail3.recycle();
		thumbnail4.recycle();
		System.gc();
		return dstBmp;
	}

	public static Bitmap getdownBtBitmap(Bitmap bgbmp, String text) {
		Bitmap createBitmap = Bitmap.createBitmap(100, 50, Config.ARGB_8888);
		Canvas canvas = new Canvas(createBitmap);
		canvas.drawBitmap(bgbmp, null, new Rect(0, 0, 100, 50), null);
		Bitmap textbmp = generateWLT(text, 13.0F, 80, 50);
		canvas.drawBitmap(textbmp, 10, 0, null);
		bgbmp.recycle();
		textbmp.recycle();
		return createBitmap;
	}

	public static boolean isSdcardExisting() {
		return "mounted".equals(Environment.getExternalStorageState());
	}

	public static int px2dip(Context context, float px) {
		return (int) (0.5F + px / context.getResources().getDisplayMetrics().density);
	}

	public static String readRawTextFile(Context context, int resId) {
		InputStream is = context.getResources().openRawResource(resId);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				sb.append(line).append("\n");
			}
			br.close();
			return sb.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean startAction(Context context, String action) {
		try {
			Intent intent = new Intent(action);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.getApplicationContext().startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	// 开启APP

	public static void startApp(Context context, String name) {
		try {
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(
					// 得到apk的功能清单文件:为了防止出错直接使用getPackageName()方法获得包名
					// packageManager.getPackageInfo("com.xuliugen.mobilesafe",
					// 0);
					context.getPackageManager().getPackageInfo(name, 0).applicationInfo.packageName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (NameNotFoundException ex) {
			Log.i("Utils", "应用包名找不到。。。");
			ex.printStackTrace();
		}
	}

	// 对APP的名字做处理 太长后面会显示...
	public static String subStringCN(String text, int len) {
		if (text != null) {
			int dotLen = "...".length();
			StringBuffer sb = new StringBuffer();
			char[] chs = text.trim().toCharArray();
			int offset = 0;

			for (int i = 0; i < chs.length; ++i) {
				if (chs[i] >= 161) {
					offset += 2;
				} else {
					++offset;
				}
			}

			if (offset > len) {
				offset = 0;

				for (int j = 0; j < chs.length; ++j) {
					if (chs[j] >= 161) {
						offset += 2;
						if (offset + dotLen > len) {
							break;
						}

						sb.append(chs[j]);
					} else {
						++offset;
						if (offset + dotLen > len) {
							break;
						}

						sb.append(chs[j]);
					}
				}

				sb.append("...");
				return sb.toString();
			}
		}

		return text;
	}

	// 卸载APP
	public static void uninstallApk(Context context, String name) {
		try {
			String pkgName = context.getPackageManager().getPackageInfo(name, 0).packageName;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DELETE");
			intent.setData(Uri.parse("package:" + pkgName));
			context.startActivity(intent);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static int initTexture(Context context, int resId) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		InputStream is = context.getResources().openRawResource(resId);

		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		bmp.recycle();
		return texture;
	}
	
	public static Bitmap getIconBitamp(Context context , int resouce){
		Bitmap  iconBitmap= BitmapFactory.decodeResource(context.getResources(), resouce);
		Bitmap createBitmap = Bitmap.createBitmap(72, 72, iconBitmap.getConfig());
		Canvas canvas = new Canvas(createBitmap);
		canvas.drawBitmap(iconBitmap, null, new Rect(0,0,72,72),null);
		return createBitmap;
	}
	
	public static Bitmap changeBitampsize(Bitmap firstbmp){
		Matrix matrix = new Matrix();
		int oldwidth = firstbmp.getWidth();
		int oldheight = firstbmp.getHeight();
		float widthscale = (float)72/oldwidth;
		float heightscale = (float)72/oldheight;
		matrix.postScale(widthscale, heightscale);
		return Bitmap.createBitmap(firstbmp, 0, 0, oldwidth, oldheight, matrix, true);
		
		
	}

}
