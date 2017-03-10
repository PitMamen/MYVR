package com.kamino.filemanager.device;

//�洢�豸��
public final class DiskInfo {
	//�洢�豸��·��
	public String path;
	public String name;
	//�洢�豸������
	public int type;
	//�洢�豸�����ÿռ�
	public String free;
	//�洢�豸�Ŀ��ÿռ�
	public String available;
	//�洢�豸���ܿռ�
	public String total;
	//�洢�豸�Ŀ��ðٷֱ�
	public float freeRatio;
	public int nameTextureId;
	public int sizeTextureId;
	//�洢�豸�Ƿ����
	public boolean mounted;

	public DiskInfo(String diskPath) {
		path = diskPath;
	}
}
