package com.kamino.mygallery.file;

public enum SortType {
	TITLE, SIZE, DATE, MIME;

	static {
		@SuppressWarnings("unused")
		SortType[] types = new SortType[] { TITLE, SIZE, DATE, MIME };
	}
}
