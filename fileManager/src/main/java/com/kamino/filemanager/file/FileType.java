package com.kamino.filemanager.file;

//ö���� �ļ�����
public enum FileType {
	UNKNOWN, AUDIO, VIDEO, PICTURE, MTZ, OFFICE, ZIP, APK, CACHE, DIR, ASEC;

	static {
		@SuppressWarnings("unused")
		FileType[] types = new FileType[] { UNKNOWN, AUDIO, VIDEO, PICTURE, MTZ,
				OFFICE, ZIP, APK, CACHE, DIR, ASEC };
	}
}
