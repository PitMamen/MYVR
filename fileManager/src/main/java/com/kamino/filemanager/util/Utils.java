package com.kamino.filemanager.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

	static int B = 255;
	static int G = 255;
	static int R = 255;
	public static final float textSize = 24.0F;
	public static final String TAG = Utils.class.getSimpleName();

	public static int initTexture(Resources resurces, int resId) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

		// For 900C
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// For H8
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		InputStream in = resurces.openRawResource(resId);
		Bitmap bmp;
		try {
			bmp = BitmapFactory.decodeStream(in);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		// For H8
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		bmp.recycle();

		return texture;
	}

	public static int initTexture(Bitmap bmp) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		if (bmp != null) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

			// For 900C
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			// For H8
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_LINEAR_MIPMAP_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_LINEAR_MIPMAP_LINEAR);

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		}

		// For H8
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		return texture;
	}

	public static int initTexture(String text, int color,float textsize,int width) {
		int[] textures = new int[1];
		Bitmap bmp = generateWLT(text, textsize, color, width,40, Alignment.ALIGN_CENTER);
		GLES20.glGenTextures(1, textures, 0);
		int texture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

		// For 900C
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		// GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// For H8
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		// For H8
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		bmp.recycle();
		return texture;
	}

	public static Bitmap getRectBitmap(int color) {
		Bitmap bmp = Bitmap.createBitmap(2, 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(new RectF(0.0F, 0.0F, 2.0F, 2.0F), paint);
		return bmp;
	}



	//把字写在中间，字的颜色指定为color
	public static Bitmap generateWLT(String text, float size, int color,int width,
			int height, Alignment align) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);

		TextPaint paint = new TextPaint();
		//paint.setColor(Color.RED);
		//canvas.drawRect(0,0,width,height,paint);
		paint.setColor(color);
		paint.setTextSize(size);
		//paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setFlags(1);//消除锯齿
	/*	CharSequence source, 需要分行的字符串 
		int bufstart, 分行的字符串从第几的位置开始 
		int bufend, 分行的字符串从第几的位置结束 
		TextPaint paint, 
		int outerwidth, 宽度，字符串超出宽度时自动换行 
		Alignment align, 有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种 
		float spacingmult, 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度 
		float spacingadd,. 在基础行距上添加多少实际行间距等于这两者的和。 
		boolean includepad, 
		TextUtils.TruncateAt ellipsize, 从什么位置开始省略 
		int ellipsizedWidth 超过多少开始省略需要指出的是这layout是默认画在Canvas的(0,0)点的，
		如果需要调整位置只能在draw前移Canvas的起始坐标canvas.translate(x,y);*/
		StaticLayout layout = new StaticLayout(text, paint, width, align, 1.0F,
				0.0F, false);
		canvas.translate(0.0F, (float) (height - layout.getHeight()) / 2.0F);
		canvas.save();
		layout.draw(canvas);
		canvas.restore();

		return bmp;
	}

	public static String formatString(String text) {
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

			if (offset > 18) {
				offset = 0;
				for (int j = 0; j < chs.length; ++j) {
					if (chs[j] >= 161) {
						offset += 2;
						if (offset + dotLen > 18) {
							break;
						}

						sb.append(chs[j]);
					} else {
						++offset;
						if (offset + dotLen > 18) {
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
	//画一个半径为128的指定颜色为color的圆
	public static Bitmap getCircleBitmap(int color) {
		Bitmap bmp = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(color);
		//以点（128，128）为圆心 画一个半径128的圆
		canvas.drawCircle(128.0F, 128.0F, 128.0F, paint);
		return bmp;
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
		canvas.drawText(text, 0.0F, 36.0F, paint);
		canvas.drawRect(
				new Rect((int) Math.floor(82.399994F)-10, (int) Math.floor(19.2F),
						(int) Math.floor(113.600006F)-10, (int) Math
						.floor(34.800003F)), paint);
		canvas.drawRect(
				new Rect(
						(int) Math.floor(113.600006F)-10,
						(int) Math.floor(19.2F + (34.800003F - 19.2F) / 4.0F),
						(int) Math
								.floor(113.600006F + (113.600006F - 82.399994F) / 10.0F)-10,
						(int) Math
								.floor(19.2F + 3.0F * (34.800003F - 19.2F) / 4.0F)),
				paint);
		paint.setARGB(255, 0, 255, 0);
		canvas.drawRect(
				new Rect(
						2 + (int) Math.floor(82.399994F)-10,
						2 + (int) Math.floor(19.2F),
						(int) (2.0F + 82.399994F + (113.600006F - 2.0F - (2.0F + 82.399994F))
								* (percent / 100.0F))-10, -2
						+ (int) Math.floor(34.800003F)), paint);
		paint.setARGB(255, R, G, B);
		if (hasBg) {
			canvas.drawBitmap(
					bgBmp,
					(Rect) null,
					new Rect((int) Math.floor(82.399994F)
							- ((int) Math.floor(19.2F) - (int) Math
							.floor(34.800003F)) / 2-8, (int) Math
							.floor(19.2F), (int) Math.floor(113.600006F)
							+ ((int) Math.floor(19.2F) - (int) Math
							.floor(34.800003F)) / 2-8, (int) Math
							.floor(34.800003F)), new Paint());
		}
		canvas.drawText(perText, 114.0F, 36.0F, paint);


		return dstBmp;
	}


}
