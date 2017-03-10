package com.kamino.filemanager.file;

public final class FileMeta {
	public final int mType;
	public final String metadata;

	FileMeta(int type, String meta) {
		mType = type;
		metadata = meta;
	}
}
