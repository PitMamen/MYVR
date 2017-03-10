package com.kamino.filemanager.file;

public final class FileInfo {
	//文件名
	public String name;
	//文件的路径 
	public String path;
	public long size;
	//是否是一个文件夹
	public boolean isDirectory;
	public int subCount;
	public long lastModified;
	public boolean canRead;
	public boolean canWrite;
	public boolean isHidden;
	public long id;
	//文件自己的图片
	public int imageTextureId = -1;
	//文件的名字
	public int nameTextureId = -1;
	//图片是否已经加载好了
	public boolean imageDecode;
	//打开文件的时候 文件放大的倍数
	public float imageRatio = 1.5F;
	//文件的类型
	public FileType type;
}
