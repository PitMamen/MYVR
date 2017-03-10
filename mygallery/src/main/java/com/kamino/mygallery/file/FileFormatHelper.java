package com.kamino.mygallery.file;

import java.util.HashMap;

import android.annotation.SuppressLint;

public final class FileFormatHelper {
	private static HashMap<String, FileMeta> fileMetaMap = new HashMap<String, FileMeta>();
	private static HashMap<String, Integer> fileIndexMap = new HashMap<String, Integer>();

	static {
		_addToList("MP3", 1, "audio/mpeg");
		_addToList("M4A", 2, "audio/mp4");
		_addToList("WAV", 3, "audio/x-wav");
		_addToList("AMR", 4, "audio/amr");
		_addToList("AWB", 5, "audio/amr-wb");
		_addToList("OGG", 7, "application/ogg");
		_addToList("OGA", 7, "application/ogg");
		_addToList("AAC", 8, "audio/aac");
		_addToList("AAC", 8, "audio/aac-adts");
		_addToList("MKA", 9, "audio/x-matroska");
		_addToList("MPEG", 21, "video/mpeg");
		_addToList("MPG", 21, "video/mpeg");
		_addToList("MP4", 21, "video/mp4");
		_addToList("M4V", 22, "video/mp4");
		_addToList("3GP", 23, "video/3gpp");
		_addToList("3GPP", 23, "video/3gpp");
		_addToList("3G2", 24, "video/3gpp2");
		_addToList("3GPP2", 24, "video/3gpp2");
		_addToList("MKV", 27, "video/x-matroska");
		_addToList("WEBM", 30, "video/webm");
		_addToList("TS", 28, "video/mp2ts");
		_addToList("AVI", 29, "video/avi");
		_addToList("WMV", 25, "video/wmv");
		_addToList("FLV", 31, "video/flv");
		_addToList("VOB", 32, "video/vob");
		_addToList("MOV", 33, "video/mov");
		_addToList("RMVB", 34, "video/rmvb");
		_addToList("JPG", 41, "image/jpeg");
		_addToList("JPEG", 41, "image/jpeg");
		_addToList("GIF", 42, "image/gif");
		_addToList("PNG", 43, "image/png");
		_addToList("BMP", 44, "image/x-ms-bmp");
		_addToList("WBMP", 45, "image/vnd.wap.wbmp");
		_addToList("WEBP", 46, "image/webp");
	}

	@SuppressLint("DefaultLocale")
	public static FileMeta getFileMeta(String file) {
		int index = file.lastIndexOf(".");
		return index < 0 ? null : (FileMeta) fileMetaMap.get(file.substring(
				index + 1).toUpperCase());
	}

	private static void _addToList(String ext, int type, String meta) {
		fileMetaMap.put(ext, new FileMeta(type, meta));
		fileIndexMap.put(meta, Integer.valueOf(type));
	}

	public static boolean isAudioFile(int type) {
		return type > 0 && type <= 10 || type >= 11 && type <= 13;
	}

	public static boolean isVideoFile(int type) {
		return type >= 21 && type <= 34;
	}

	public static boolean isPicFile(int type) {
		return type >= 41 && type <= 46;
	}
}
