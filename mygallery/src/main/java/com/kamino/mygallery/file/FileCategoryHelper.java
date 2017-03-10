package com.kamino.mygallery.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileCategoryHelper {
	public static HashMap<FileType, FilenameFilter> filterMap = new HashMap<FileType, FilenameFilter>();
	public static FileType[] fileTypeList;
	private static String apkExt = "apk";
	private static String mtzExt = "mtz";
	private static String[] zipExt = new String[] { "zip", "rar" };
	private FileType filterType;
	private Context mContext;
	private HashMap<FileType, FileTypeStat> fileTypeStatMap = new HashMap<FileType, FileTypeStat>();
	private static int[] fileTypeVal;
	private static int[] sortTypeVal;

	static {
		fileTypeList = new FileType[] { FileType.AUDIO, FileType.VIDEO,
				FileType.PICTURE, FileType.MTZ, FileType.OFFICE, FileType.ZIP,
				FileType.APK, FileType.DIR };
	}

	public final class FileTypeStat {
	   public long count;
	   public long size;
	}
	
	public FileCategoryHelper(Context context) {
		mContext = context;
		filterType = FileType.UNKNOWN;
	}

	public static FileType getFileType(String name) {
		FileMeta meta = FileFormatHelper.getFileMeta(name);
		if (meta != null) {
			if (FileFormatHelper.isAudioFile(meta.mType)) {
				return FileType.AUDIO;
			}

			if (FileFormatHelper.isVideoFile(meta.mType)) {
				return FileType.VIDEO;
			}

			if (FileFormatHelper.isPicFile(meta.mType)) {
				return FileType.PICTURE;
			}

			if (Util.officeFileSet.contains(meta.metadata)) {
				return FileType.OFFICE;
			}
		}

		int extOffset = name.lastIndexOf('.');
		if (extOffset < 0) {
			return FileType.DIR;
		} else {
			String ext = name.substring(extOffset + 1);
			if (ext.equalsIgnoreCase(apkExt)) {
				return FileType.APK;
			} else if (ext.equalsIgnoreCase(mtzExt)) {
				return FileType.MTZ;
			} else {
				int idx = 0;
				boolean isZip;

				while (true) {
					isZip = false;
					if (idx >= zipExt.length) {
						break;
					}

					if (zipExt[idx].equalsIgnoreCase(ext)) {
						isZip = true;
						break;
					}

					++idx;
				}

				return isZip ? FileType.ZIP : FileType.DIR;
			}
		}
	}

	//把类型加到集合中  视频、音频、APK...
	private void addFileTypeStat(FileType type, long count, long size) {
		FileTypeStat stat = (FileTypeStat) fileTypeStatMap.get(type);
		if (stat == null) {
			stat = new FileTypeStat();
			fileTypeStatMap.put(type, stat);
		}

		stat.count = count;
		stat.size = size;
	}

	//通过内容提供者查询各种类型文件的数量
	private boolean addFileTypeStat(FileType type, Uri uri) {
		String[] projection = new String[] { "COUNT(*)", "SUM(_size)" };
		Cursor cursor = mContext.getContentResolver().query(uri, projection,
				getQueryString(type), (String[]) null, (String) null);
		if (cursor == null) {
			Log.e("FileCategoryHelper", "fail to query uri:" + uri);
			return false;
		} else if (cursor.moveToNext()) {
			addFileTypeStat(type, cursor.getLong(0), cursor.getLong(1));
			Log.v("FileCategoryHelper", "Retrieved " + type.name()
					+ " info >>> count:" + cursor.getLong(0) + " size:"
					+ cursor.getLong(1));
			cursor.close();
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("NewApi")
	public static Uri getBaseUri(FileType type) {
		switch (getFileTypes()[type.ordinal()]) {
		case 2:
			return Media.getContentUri("external");
		case 3:
			return android.provider.MediaStore.Video.Media
					.getContentUri("external");
		case 4:
			return android.provider.MediaStore.Images.Media
					.getContentUri("external");
		case 5:
		case 6:
		case 7:
		case 8:
			return Files.getContentUri("external");
		default:
			return null;
		}
	}

	private static int[] getFileTypes() {
		int[] arrn;
		int[] arrn2 = fileTypeVal;
		if (arrn2 != null) {
			return arrn2;
		}
		arrn = new int[FileType.values().length];
		try {
			arrn[FileType.UNKNOWN.ordinal()] = 1;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.APK.ordinal()] = 8;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.CACHE.ordinal()] = 9;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.OFFICE.ordinal()] = 6;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.ASEC.ordinal()] = 11;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.AUDIO.ordinal()] = 2;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.DIR.ordinal()] = 10;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.PICTURE.ordinal()] = 4;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.MTZ.ordinal()] = 5;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.VIDEO.ordinal()] = 3;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[FileType.ZIP.ordinal()] = 7;
		} catch (NoSuchFieldError e) {
		}
		fileTypeVal = arrn;
		return arrn;
	}

	private static int[] getSortTypes() {
		int[] arrn;
		int[] arrn2 = sortTypeVal;
		if (arrn2 != null) {
			return arrn2;
		}
		arrn = new int[SortType.values().length];
		try {
			arrn[SortType.DATE.ordinal()] = 3;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[SortType.TITLE.ordinal()] = 1;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[SortType.SIZE.ordinal()] = 2;
		} catch (NoSuchFieldError e) {
		}
		try {
			arrn[SortType.MIME.ordinal()] = 4;
		} catch (NoSuchFieldError e) {
		}
		sortTypeVal = arrn;
		return arrn;
	}

	private static String getQueryString(FileType type) {
		switch (getFileTypes()[type.ordinal()]) {
		case 8:
			return "_data LIKE \'%.apk\'";
		default:
			return null;
		}
	}

	//传的type存在的话把值返回出去 没有new一个FileTypeStat返回出去
	public final FileTypeStat getFileTypeStat(FileType type) {
		if (fileTypeStatMap.containsKey(type)) {
			return (FileTypeStat) fileTypeStatMap.get(type);
		} else {
			FileTypeStat stat = new FileTypeStat();
			fileTypeStatMap.put(type, stat);
			return stat;
		}
	}

	public final FilenameFilter getFileFilter() {
		return (FilenameFilter) filterMap.get(filterType);
	}

	public final List<FileInfo> getFileList(FileType type, SortType sort) {
		Cursor cursor = null;
		ArrayList<FileInfo> list = new ArrayList<FileInfo>();
		//得到存储设备所有的视频、图片、apk分别的Uri
		Uri uri = FileCategoryHelper.getBaseUri(type);
		String selection = FileCategoryHelper.getQueryString(type);
		String sortOrder = null;

		switch (FileCategoryHelper.getSortTypes()[sort.ordinal()]) {
		default: {
			sortOrder = null;
			break;
		}
		case 1: {
			sortOrder = "title asc";
			break;
		}
		case 2: {
			sortOrder = "_size asc";
			break;
		}
		case 3: {
			sortOrder = "date_modified desc";
			break;
		}
		case 4: {
			sortOrder = "mime_type asc, title asc";
		}
		}

		if (uri == null) {
			Log.e((String) "FileCategoryHelper",
					(String) ("invalid uri, category:" + type.name()));
		} else {
			String[] projection = new String[] { "_id", "_data", "_size",
					"date_modified" };
			cursor = mContext.getContentResolver().query(uri, projection,
					selection, null, sortOrder);
		}

		try {
			if (cursor == null)
				return list;

			if (cursor.moveToFirst()) {
				do {
					FileInfo info = new FileInfo();
					//文件的ID
					info.id = cursor.getLong(0);
					//文件的路径
					info.path = cursor.getString(1);
					//文件的文件名
					info.name = Util.getLastFileName(info.path);
					//文件的大小
					info.size = cursor.getLong(2);
					//文件的最后一次修改时间
					info.lastModified = cursor.getLong(3);
					//文件的类型
					info.type = FileCategoryHelper.getFileType(info.path);
					list.add(info);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	@SuppressLint("NewApi")//初始化文件类型的状态
	public final void initFileTypeStat() {
		FileType[] list = fileTypeList;
		int len = list.length;

		for (int idx = 0; idx < len; ++idx) {
			addFileTypeStat(list[idx], 0L, 0L);
		}

		//通过内容提供者查询各种类型文件的数量
		Uri uri = Media.getContentUri("external");
		addFileTypeStat(FileType.AUDIO, uri);
		uri = Video.Media.getContentUri("external");
		addFileTypeStat(FileType.VIDEO, uri);
		uri = Images.Media.getContentUri("external");
		addFileTypeStat(FileType.PICTURE, uri);
		uri = Files.getContentUri("external");
		addFileTypeStat(FileType.MTZ, uri);
		addFileTypeStat(FileType.OFFICE, uri);
		addFileTypeStat(FileType.ZIP, uri);
		addFileTypeStat(FileType.APK, uri);
	}
}
