package com.kamino.mygallery.file;

import java.util.Comparator;
import java.util.HashMap;

public final class FileComparatorHelper {
	private SortType type;
	boolean isDirectory;
	private HashMap<SortType, BaseComparator> comparatorMap = new HashMap<SortType, BaseComparator>();
	private BaseComparator title = new TitleComparator(this);
	private BaseComparator size = new SizeComparator(this);
	private BaseComparator data = new DateComparator(this);
	private BaseComparator mime = new MimeComparator(this);

	public FileComparatorHelper() {
		type = SortType.TITLE;
		comparatorMap.put(SortType.TITLE, title);
		comparatorMap.put(SortType.SIZE, size);
		comparatorMap.put(SortType.DATE, data);
		comparatorMap.put(SortType.MIME, mime);
	}

	static int compare(final long n) {
		if (n > 0L) {
			return 1;
		}
		if (n < 0L) {
			return -1;
		}
		return 0;
	}

	static boolean isDirectory(final FileComparatorHelper h) {
		return h.isDirectory;
	}

	public final Comparator<Object> getComparator() {
		return  comparatorMap.get(type);
	}

	abstract class BaseComparator implements Comparator<Object> {

		final FileComparatorHelper helper;

		BaseComparator(final FileComparatorHelper h) {
			helper = h;
		}

		protected abstract int compareFile(FileInfo p0, FileInfo p1);

		@Override
		public int compare(Object obj1, Object obj2) {
			int result = -1;
			final FileInfo info1 = (FileInfo) obj1;
			final FileInfo info2 = (FileInfo) obj2;
			if (info1.isDirectory != info2.isDirectory) {
				if (helper.isDirectory) {
					if (!info1.isDirectory) {
						return result;
					}
				} else if (info1.isDirectory) {
					return result;
				}
				return 1;
			}
			result = compareFile(info1, info2);
			return result;
		}
	}

	final class TitleComparator extends BaseComparator {

		TitleComparator(FileComparatorHelper h) {
			super(h);
		}

		public final int compareFile(FileInfo info1, FileInfo info2) {
			return info1.name.compareToIgnoreCase(info2.name);
		}
	}

	final class SizeComparator extends BaseComparator {

		SizeComparator(FileComparatorHelper h) {
			super(h);
		}

		public final int compareFile(FileInfo info1, FileInfo info2) {
			return FileComparatorHelper.compare(info1.size - info2.size);
		}
	}

	final class DateComparator extends BaseComparator {

		DateComparator(FileComparatorHelper h) {
			super(h);
		}

		public final int compareFile(FileInfo info1, FileInfo info2) {
			return FileComparatorHelper.compare(info2.lastModified
					- info1.lastModified);
		}
	}

	final class MimeComparator extends BaseComparator {

		MimeComparator(FileComparatorHelper h) {
			super(h);
		}

		public final int compareFile(FileInfo info1, FileInfo info2) {
			int result = Util.getFileExtName(info1.name).compareToIgnoreCase(
					Util.getFileExtName(info2.name));
			return result != 0 ? result : Util.getFileBaseName(info1.name)
					.compareToIgnoreCase(Util.getFileBaseName(info2.name));
		}
	}
}
