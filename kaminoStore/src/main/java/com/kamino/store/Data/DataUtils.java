package com.kamino.store.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.kamino.store.entity.APKEntity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Xml;

public class DataUtils {
	private static String GAMEapkurl = "http://qnfile.orangelive.tv/ali_default.xml";
	private static ArrayList<APKEntity> GAMEapks;
	private static Context mContext;  
	private static APKEntity gameapk;
	public static String GAMEAPKPATH = Environment.getExternalStorageDirectory()+File.separator+"gameapks.xml";
	public static String GAMEIMG_FOLDERPATH = Environment.getExternalStorageDirectory()+File.separator+"Store";


	public static boolean saveGAMEXML(String filename){
		File file = new File(filename);
		//创建HttpClient对象
		HttpClient client = new DefaultHttpClient();
		//创建HttpGet对象
		HttpGet httpget =  new HttpGet(GAMEapkurl);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			//关联HttpClient对象和HttpGet对象
			HttpResponse execute = client.execute(httpget);
			//得到服务器转发给我们的数据
			HttpEntity entity = execute.getEntity();
			if(entity!=null){
				InputStream is = entity.getContent();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int temp = 0;
				while((temp=is.read(buffer))!=-1){
					fos.write(buffer, 0, temp);
				}
				fos.close();
				is.close();
				return true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static ArrayList<APKEntity> parseXML(String XMLfilepath){
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			FileInputStream fis = new FileInputStream(XMLfilepath);
			xmlPullParser.setInput(fis, "UTF-8");
			int eventType = xmlPullParser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT){
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					GAMEapks =new ArrayList<APKEntity>();
					break;
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					if(tag.equalsIgnoreCase("app")){
						gameapk = new APKEntity();
					}else if(tag.equalsIgnoreCase("appinfo")){
						gameapk.setBelong("GAME");
						gameapk.setId(Integer.parseInt(xmlPullParser.getAttributeValue(null, "id")));
						gameapk.setName(xmlPullParser.getAttributeValue(null, "APK_NAME"));
						gameapk.setSize(xmlPullParser.getAttributeValue(null, "APK_SIZE"));
						gameapk.setBesupported(xmlPullParser.getAttributeValue(null, "APK_BESUPPORTED"));
						gameapk.setIntroduction(xmlPullParser.getAttributeValue(null, "APK_INTRODUCTION"));
						gameapk.setPackagename(xmlPullParser.getAttributeValue(null, "APK_PACKAGENAME"));
						gameapk.setIconurl(xmlPullParser.getAttributeValue(null, "APK_ICONURL"));
						gameapk.setThumbnail1url(xmlPullParser.getAttributeValue(null, "APK_THUMBNAIL1URL"));
						gameapk.setThumbnail2url(xmlPullParser.getAttributeValue(null, "APK_THUMBNAIL2URL"));
						gameapk.setThumbnail3url(xmlPullParser.getAttributeValue(null, "APK_THUMBNAIL3URL"));
						gameapk.setThumbnail4url(xmlPullParser.getAttributeValue(null, "APK_THUMBNAIL4URL"));
					}else if(tag.equalsIgnoreCase("Down_URL")){
						gameapk.setDownloadurl(xmlPullParser.getAttributeValue(null, "app_url"));
					}
					break;
				case XmlPullParser.END_TAG:
					if(xmlPullParser.getName().equalsIgnoreCase("app")){
						GAMEapks.add(gameapk);						
					}
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
			return GAMEapks;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean checkAPKExit(Context context,String packagename){
		try {
			ApplicationInfo applicationInfo2 = context.getPackageManager().getApplicationInfo(packagename, PackageManager.GET_ACTIVITIES);;
//			ApplicationInfo applicationInfo2 = getPackageManager().getApplicationInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);;
			return true;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
	}
	
	public static void openAPK(Context context,String packagename){
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packagename);
		context.startActivity(intent);
	}
	
}
