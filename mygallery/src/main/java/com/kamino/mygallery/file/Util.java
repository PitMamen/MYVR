package com.kamino.mygallery.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

public final class Util {
	public static HashSet<String> officeFileSet = new OfficeFileSet();
	// private static String apkMeta = "application/zip";
	private static String asecPath = Environment.getExternalStorageDirectory()
			.getPath() + "/.android_secure";
	private static String[] cachePathList = new String[] { "miren_browser/imagecaches" };

	@SuppressWarnings("serial")
	final static class OfficeFileSet extends HashSet<String> {
		OfficeFileSet() {
			add("text/plain");
			add("text/plain");
			add("application/pdf");
			add("application/msword");
			add("application/vnd.ms-excel");
			add("application/vnd.ms-excel");
		}
	}

	public static Bitmap getBitmap(Context context, ContentResolver res,
			String path) {
		ContentResolver resolver = context.getContentResolver();
		String[] projection = new String[] { "_data", "_id" };
		String selection = "_data = \'" + path + "\'";
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, projection,
				selection, (String[]) null, (String) null);
		if (cursor != null && cursor.getCount() != 0) {
			int imageId;
			if (cursor.moveToFirst()) {
				int id = cursor.getColumnIndex("_id");
				int data = cursor.getColumnIndex("_data");

				do {
					imageId = cursor.getInt(id);
					cursor.getString(data);
				} while (cursor.moveToNext());
			} else {
				imageId = 0;
			}

			cursor.close();
			Options opt = new Options();
			opt.inDither = false;
			opt.inPreferredConfig = Config.ARGB_8888;
			return Thumbnails.getThumbnail(resolver, (long) imageId, 1, opt);
		} else {
			return null;
		}
	}

	public static Drawable getAppIcon(Context context, String path) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path, 1);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = path;
			appInfo.publicSourceDir = path;

			try {
				Drawable drawable = appInfo.loadIcon(pm);
				return drawable;
			} catch (OutOfMemoryError e) {
				Log.e("Util", e.toString());
			}
		}

		return null;
	}

	public static FileInfo getFileInfo(File path, FilenameFilter filter,
			boolean isShowAll) {
		int count = 0;
		FileInfo info = new FileInfo();
		String name = path.getPath();
		File file = new File(name);
		info.canRead = file.canRead();
		info.canWrite = file.canWrite();
		info.isHidden = file.isHidden();
		info.name = path.getName();
		info.lastModified = file.lastModified();
		info.isDirectory = file.isDirectory();
		info.path = name;
		info.type = FileCategoryHelper.getFileType(name);
		if (info.isDirectory) {
			File[] list = file.listFiles(filter);
			if (list == null) {
				return null;
			}

			int len = list.length;

			for (int idx = 0; idx < len; ++idx) {
				File f = list[idx];
				if ((!f.isHidden() || isShowAll)
						&& isNotAsecFile(f.getAbsolutePath())) {
					++count;
				}
			}

			info.subCount = count;
		} else {
			info.size = file.length();
		}

		return info;
	}

	public static final void deleteFileInDatabase(ContentResolver resolver,
			FileInfo info) {
		String[] selection = new String[] { info.path };

		try {
			resolver.delete(FileCategoryHelper.getBaseUri(info.type),
					"_data = ?", selection);
			Log.d("Util", "DeleteCR >>> " + info.path);
		} catch (Exception e) {
			;
		}
	}

	//播放指定路径的文件
	public static void playFile(Context context, FileInfo info) {
		Intent intent = new Intent("android.intent.action.VIEW");
		File file = new File(info.path);
		intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()),
				"video/*");

		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			;
		}
	}

	//删除指定路径的文件
	public static void deleteFile(FileInfo info) {
		if (info == null) {
			Log.e("Util", "DeleteFile: null parameter");
		} else {
			File file = new File(info.path);
			if (file.exists()) {
				file.delete();
			}

			Log.v("Util", "DeleteFile >>> " + info.path);
		}
	}

	public static boolean isKaminoRom() {
		return Build.BRAND != null
				//ʹ��ָ�������Ի�������ʽ�ַ����Ͳ�������һ����ʽ���ַ���
				&& Build.BRAND.toLowerCase(Locale.US).contains("kamino");
	}

	private static boolean isNormalFile(File file) {
		//是否是隐藏文件
		if (file.isHidden()) {
			return false;
		} else if (file.getName().startsWith(".")) {
			return false;
		} else {
			String root = Environment.getExternalStorageDirectory().getPath();
			String[] list = cachePathList;
			int len = list.length;
  
			for (int idx = 0; idx < len; ++idx) {
				String name = list[idx];
				String path = file.getPath();
				String full;
				//文件分隔符
				if (root.endsWith(File.separator)) {
					full = root + name;
				} else {
					full = root + File.separator + name;
				}

				if (path.startsWith(full)) {
					return false;
				}
			}

			return true;
		}
	}

	public static boolean isNotAsecFile(String path) {
		return !path.equals(asecPath);
	}

	//�õ��ļ��ĺ�׺��
	public static String getFileExtName(String path) {
		//����ָ���ַ��ڴ��ַ��������һ�γ��ִ���������
		int index = path.lastIndexOf('.');
		//�����ַ�����ָ���� beginIndex ����ʼ��ֱ������ endIndex - 1 �����ַ�
		return index != -1 ? path.substring(index + 1, path.length()) : "";
	}

	public static String getFileBaseName(String path) {
		int index = path.lastIndexOf('.');
		return index != -1 ? path.substring(0, index) : "";
	}

	//�õ��ļ���
	public static String getLastFileName(String path) {
		int index = path.lastIndexOf('/');
		return index != -1 ? path.substring(index + 1) : "";
	}

	public static boolean isNormalFile(String path) {
		return isNormalFile(new File(path));
	}
}
