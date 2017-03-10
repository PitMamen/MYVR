package com.kamino.filemanager.device;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

@SuppressLint("NewApi")
public final class FileUtils {
	//视频文件的文件后缀名集合
	public static final String[] videoExtList = new String[] { "3gp", "mkv",
			"mp4", "mov", "avi", "wmv", "rmvb", "MPEG", "flv", "mpg", "vob",
			"webm", "f4v", "ts", "asf", "dv" };
	//图片的文件后缀名集合
	public static final String[] picExtList = new String[] { "png", "jpg",
			"bmp", "gif" };
	//音乐文件的文件后缀名集合
	public static final String[] mp3ExtList = new String[] { "mp3" };
	//APK文件的文件后缀名集合
	public static final String[] apkExtList = new String[] { "apk" };
	public static final String[] txtExtList = new String[] { "txt" };
	public static final String[] xmlExtList = new String[] { "xml" };
	//压缩文件的文件名集合
	public static final String[] compressExtList = new String[] { "zip", "rar",
			"rars", "jar", "tar", "gz" };

	//inputstream.available()方法返回的值是该inputstream在不被阻塞的情况下一次可以读取到的数据长度
	public static long getFileSize(File file) {
		long size = 0L;
		if (file.exists()) {
			try {
				FileInputStream in = new FileInputStream(file);
				size = (long) in.available();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return size;
			}
		}

		return size;
	}

	//表示SD卡存在并可以读写，返回的是总的空间大小
	@SuppressWarnings("deprecation")
	public static String getStorageTotal(String path) {
		if (Environment.getStorageState(new File(path)).equals("mounted")) {
			long total = getTotalSize(path);
			if (total > 0L) {
				float totalG = (float) total / 1024.0F / 1024.0F;
				DecimalFormat df = new DecimalFormat("@@@");
				return totalG > 500.0F ? df.format((double) (totalG / 1024.0F))
						+ "G" : df.format((double) totalG) + "M";
			} else {
				return "0.0";
			}
		} else {
			return "0.0";
		}
	}
	//表示SD卡存在big可以读写，返回的是已用的大小
	@SuppressWarnings("deprecation")
	public static String getStorageFree(String path) {
		if (Environment.getStorageState(new File(path)).equals("mounted")) {
			long free = getFreeSize(path);
			if (free > 0L) {
				float megaFree = (float) free / 1024.0F / 1024.0F;
				DecimalFormat f = new DecimalFormat("@@@");
				return megaFree > 500.0F ? f
						.format((double) (megaFree / 1024.0F)) + "G" : f
						.format((double) megaFree) + "M";
			}else {
				return "0.0";
			}
		} else {
			return "0.0";
		}
	}

	//表示SD卡存在big可以读写，返回的是可用的大小
	@SuppressWarnings("deprecation")
	public static String getStorageAvailable(String path) {
		if (Environment.getStorageState(new File(path)).equals("mounted")) {
			long Available = getAvailableSize(path);
			if (Available > 0L) {
				float megaFree = (float) Available / 1024.0F / 1024.0F;
				DecimalFormat f = new DecimalFormat("@@@");
				return megaFree > 500.0F ? f
						.format((double) (megaFree / 1024.0F)) + "G" : f
						.format((double) megaFree) + "M";
			}else {
				return "0.0";
			}
		} else {
			return "0.0";
		}
	}

	//可用大小
	@SuppressWarnings("deprecation")
	public static long getAvailableSize(String path) {
		StatFs stat = new StatFs(path);
		return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
	}
	//总大小
	@SuppressWarnings("deprecation")
	public static long getTotalSize(String path) {
		StatFs stat = new StatFs(path);
		return (long) stat.getBlockSize() * (long) stat.getBlockCount();
	}
	//已用大小
	@SuppressWarnings("deprecation")
	public static long getFreeSize(String path) {
		StatFs stat = new StatFs(path);
		return (long) stat.getBlockSize()
				* (long) (stat.getBlockCount() - stat.getAvailableBlocks());
	}
}
