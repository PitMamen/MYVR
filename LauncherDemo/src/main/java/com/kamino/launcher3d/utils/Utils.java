package com.kamino.launcher3d.utils;

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
		return (int) (0.5F + dip
				* context.getResources().getDisplayMetrics().density);
	}

	public static Bitmap generateWLT(int percent, int width, int height,
			Bitmap bgBmp, boolean hasBg, String text) {
		if (percent > 100) {
			percent = 100;
		}

		String perText = percent + "%";
		Paint paint = new Paint();
		paint.setARGB(255, R, G, B);
		paint.setTextSize(textSize);
		paint.setTypeface(null);
		paint.setFlags(1);
		Bitmap dstBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		canvas.drawText(text, 10.0F, 36.0F, paint);
		canvas.drawRect(
				new Rect((int) Math.floor(82.399994F), (int) Math.floor(19.2F),
						(int) Math.floor(113.600006F), (int) Math
								.floor(34.800003F)), paint);
		canvas.drawRect(
				new Rect(
						(int) Math.floor(113.600006F),
						(int) Math.floor(19.2F + (34.800003F - 19.2F) / 4.0F),
						(int) Math
								.floor(113.600006F + (113.600006F - 82.399994F) / 10.0F),
						(int) Math
								.floor(19.2F + 3.0F * (34.800003F - 19.2F) / 4.0F)),
				paint);
		paint.setARGB(255, 0, 255, 0);
		canvas.drawRect(
				new Rect(
						2 + (int) Math.floor(82.399994F),
						2 + (int) Math.floor(19.2F),
						(int) (2.0F + 82.399994F + (113.600006F - 2.0F - (2.0F + 82.399994F))
								* (percent / 100.0F)), -2
								+ (int) Math.floor(34.800003F)), paint);
		paint.setARGB(255, R, G, B);
		if (hasBg) {
			canvas.drawBitmap(
					bgBmp,
					(Rect) null,
					new Rect((int) Math.floor(82.399994F)
							- ((int) Math.floor(19.2F) - (int) Math
									.floor(34.800003F)) / 2, (int) Math
							.floor(19.2F), (int) Math.floor(113.600006F)
							+ ((int) Math.floor(19.2F) - (int) Math
									.floor(34.800003F)) / 2, (int) Math
							.floor(34.800003F)), new Paint());
		}
		canvas.drawText(perText, 130.0F, 36.0F, paint);
		return dstBmp;
	}

	public static Bitmap generateWLT(String text, float size, int width,
			int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		TextPaint tp = new TextPaint();
		tp.setColor(-1);
		tp.setTextSize(size);
		tp.setAntiAlias(true);
		/*1.�ַ�������Դ
		2 .���ʶ���
		3.layout�Ŀ�ȣ��ַ����������ʱ�Զ����С�
		4.layout����ʽ����ALIGN_CENTER�� ALIGN_NORMAL�� ALIGN_OPPOSITE ���֡�
		5.����м�࣬��������С��1.5f��ʾ�м��Ϊ1.5��������߶ȡ�
		6.����м�࣬0��ʾ0�����ء�
		ʵ���м����������ߵĺ͡�
		7.����֪����ʲô��˼����������boolean includepad��
		��Ҫָ���������layout��Ĭ�ϻ���Canvas��(0,0)��ģ������Ҫ����λ��ֻ����draw֮ǰ��Canvas����ʼ����
		canvas.translate(x,y);*/
		StaticLayout layout = new StaticLayout(text, tp, width,
				Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
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
			canvas.drawBitmap(bmp, null, new RectF(2.0F, 2.0F, 254.0F, 254.0F),
					paint);
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
		canvas.drawRoundRect(new RectF(3.0F, 3.0F, 253.0F, 253.0F), 10.0F,
				10.0F, paint);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawRoundRect(new RectF(3.0F, 3.0F, 253.0F, 80.0F), 10.0F,
				10.0F, paint);
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
		Bitmap dstBmp = Bitmap.createBitmap(2 + bmp.getWidth(),
				2 + bmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstBmp);
		Paint paint = new Paint();
		if (bmp != null) {
			canvas.drawBitmap(
					bmp,
					null,
					new RectF(1.0F, 1.0F, (1 + bmp.getWidth()), (1 + bmp
							.getHeight())), paint);
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
			switch (activity.getWindowManager().getDefaultDisplay()
					.getRotation()) {
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

		return ThumbnailUtils.extractThumbnail(
				BitmapFactory.decodeFile(path, opt), width, height, 2);
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
	//TODO
	public static int initAppTexture(Bitmap bmp, String name) {
		if (name == null) {
			name = "";
		}

		Bitmap iconBmp = Bitmap.createBitmap(ICON_BG_WIDTH, ICON_BG_HEIGHT,
				Config.ARGB_8888);
		Bitmap nameBmp = generateWLT(subStringCN(name, 8), 15.0F, 80, 24);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		paint.setARGB(204, 19, 19, 19);
		//���������� ͼƬ APP����
		canvas.drawRect(new Rect(2, 2, 98, 114), paint);
		canvas.drawBitmap(bmp, 14.0F, 10.0F, null);
		canvas.drawBitmap(nameBmp, 10.0F, 82.0F, null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}
	//TODO
	public static int initTexture(Resources res, int resId) {
		
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
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
	//TODO
	public static int initTexture(Resources res, String title) {
		Bitmap bmp = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		Bitmap titleBmp = generateWLT(subStringCN(title, 8), 8, 100, 90);
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#88000000"));
		paint.setAntiAlias(true);
		//paint.setFilterBitmap(true);
		Canvas canvas = new Canvas(bmp);
		canvas.drawRect(0, 0, 100, 100, paint);
		canvas.drawBitmap(titleBmp,0,10,null);
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}
	//TODO
	public static int initTexture(Resources res, int resId,String up,String down,int width,int height,int color) {
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
		Bitmap iconBmp = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Bitmap UpBmp = generateWLT(subStringCN(up, 8), 30F, 150, 50);
		Bitmap DownBmp = generateWLT(subStringCN(down, 8), 30, 150, 50);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		if(color == Color.BLACK){
			paint.setColor(Color.parseColor("#88000000"));
		}
		if(color == Color.BLUE){
			paint.setColor(Color.parseColor("#880000FF"));
		}
		//paint.setARGB(204, 19, 19, 19);
		canvas.drawRect(new Rect(0, 0, width, height), paint);
		canvas.drawBitmap(UpBmp, (width-UpBmp.getWidth())/2.0F, ((height-bmp.getHeight())/2.0F-UpBmp.getHeight())/2.0F, null);
		canvas.drawBitmap(bmp, (width-bmp.getWidth())/2.0F, (height-bmp.getHeight())/2.0F, null);
		canvas.drawBitmap(DownBmp, (width-DownBmp.getWidth())/2.0F, height-(((height-bmp.getHeight())/2.0F-DownBmp.getHeight()+80)/2.0F), null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		iconBmp.recycle();
		return texture;
	}
	
	//TODO
	public static int initTexture(Resources res, int resId,String left,String right,int color) {
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
		Bitmap iconBmp = Bitmap.createBitmap(100, 24,
				Config.ARGB_8888);
		Bitmap leftBmp = generateWLT(subStringCN(left, 8), 7.0F, 20, 15);
		Bitmap rightBmp = generateWLT(right, 7.0F, 20, 15);
		Canvas canvas = new Canvas(iconBmp);
		Paint paint = new Paint();
		if(color == Color.BLACK){
			paint.setColor(Color.parseColor("#88000000"));
		}
		if(color == Color.BLUE){
			paint.setColor(Color.parseColor("#880000FF"));
		}
		//paint.setARGB(204, 19, 19, 19);
		canvas.drawRect(new Rect(2, 2, 98, 20), paint);
		canvas.drawBitmap(leftBmp, 0F,5.0F, null);
		canvas.drawBitmap(bmp,100F-bmp.getWidth()-10,5.0F, null);
		canvas.drawBitmap(rightBmp,100F-bmp.getWidth()-rightBmp.getWidth()-10,5.0F, null);
		canvas.save(31);
		canvas.restore();
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
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
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
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
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String key, String name, int count) {
		int[] textures = new int[1];
		Bitmap bmp = getAblumImage(
				LocalImageLoader.getInstance().loadImage(key, 256, 256), name,
				count);
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();
		return texture;
	}

	public static int initTexture(String path, boolean rebuild) {
		int[] textures = new int[1];
		Bitmap bmp;
		if (rebuild) {
			bmp = LocalImageLoader.getInstance().decodeThumbBitmapForFile(path,
					2048, 2048);
		} else {
			bmp = getBitmap(LocalImageLoader.getInstance().loadImage(path, 256,
					256));
		}

		if (bmp != null) {
			GLES20.glGenTextures(1, textures, 0);
			int texture = textures[0];
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
			return texture;
		} else {
			return 0;
		}
	}

	public static boolean isSdcardExisting() {
		return "mounted".equals(Environment.getExternalStorageState());
	}

	public static int px2dip(Context context, float px) {
		return (int) (0.5F + px
				/ context.getResources().getDisplayMetrics().density);
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

	//����APP
	public static void startApp(Context context, String name) {
		try {
			Intent intent = context
					.getPackageManager()
					.getLaunchIntentForPackage(
							context.getPackageManager().getPackageInfo(name, 0).applicationInfo.packageName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (NameNotFoundException ex) {
			Log.i("Utils", "Ӧ�ð����Ҳ���������");
			ex.printStackTrace();
		}
	}

	//��APP������������ ̫���������ʾ...
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

	//ж��APP
	public static void uninstallApk(Context context, String name) {
		try {
			String pkgName = context.getPackageManager()
					.getPackageInfo(name, 0).packageName;
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
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);

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

}
