package com.kamino.filemanager.file;

public enum SortType {
	TITLE, SIZE, DATE, MIME;

	static {
		@SuppressWarnings("unused")
		SortType[] types = new SortType[] { TITLE, SIZE, DATE, MIME };
	}
}
