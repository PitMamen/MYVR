package com.kamino.store.Data;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileUtils {
	private Context mContext;
	
	public static boolean installAPK(Context context ,String path){
		//这是普通安装时发送的Intent 因为会显示平面的安装界面 所以体验不好
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		/*//这里是静默安装的Intent 因为不显示平面的安装界面 所以体验会好一点 但是只针对用了vrfarmer项目
		 //的固件 因为发送的这个广播接收者是vrfarmer写的代码 
		Intent intent = new Intent();
		//发送一个intent-filter为com.kamino.action.INSTALL_NODIPLAY的广播
		intent.setAction("com.kamino.action.INSTALL_NODIPLAY");
		intent.putExtra("packagename", path);
		context.sendBroadcast(intent);*/
		System.out.println(path+"安装成功............");
		return true;
	}
}
