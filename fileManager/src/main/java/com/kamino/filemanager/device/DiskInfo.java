package com.kamino.filemanager.device;

//存储设备类
public final class DiskInfo {
	//存储设备的路径
	public String path;
	public String name;
	//存储设备的类型
	public int type;
	//存储设备的已用空间
	public String free;
	//存储设备的可用空间
	public String available;
	//存储设备的总空间
	public String total;
	//存储设备的可用百分比
	public float freeRatio;
	public int nameTextureId;
	public int sizeTextureId;
	//存储设备是否存在
	public boolean mounted;

	public DiskInfo(String diskPath) {
		path = diskPath;
	}
}
