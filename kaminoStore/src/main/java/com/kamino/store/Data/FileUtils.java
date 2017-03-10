package com.kamino.store.Data;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileUtils {
	private Context mContext;
	
	public static boolean installAPK(Context context ,String path){
		//������ͨ��װʱ���͵�Intent ��Ϊ����ʾƽ��İ�װ���� �������鲻��
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		/*//�����Ǿ�Ĭ��װ��Intent ��Ϊ����ʾƽ��İ�װ���� ����������һ�� ����ֻ�������vrfarmer��Ŀ
		 //�Ĺ̼� ��Ϊ���͵�����㲥��������vrfarmerд�Ĵ��� 
		Intent intent = new Intent();
		//����һ��intent-filterΪcom.kamino.action.INSTALL_NODIPLAY�Ĺ㲥
		intent.setAction("com.kamino.action.INSTALL_NODIPLAY");
		intent.putExtra("packagename", path);
		context.sendBroadcast(intent);*/
		System.out.println(path+"��װ�ɹ�............");
		return true;
	}
}
