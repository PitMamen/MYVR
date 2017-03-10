package com.kamino.store.Data;

import java.util.ArrayList;

import com.kamino.store.entity.APKEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
	public static String GAME_TABLENAME = "GameApks";
	private static DataHelper Mydatahelper;
	private static Context mContext;
	private SQLiteDatabase mReadData, mWriteData;
	private final String APK_ID = "id";
	private final String APK_NAME = "apkname";
	private final String APK_SIZE = "apksize";
	private final String APK_BESUPPORTED = "apkbesupported";
	private final String APK_INTRODUCTION = "apkintroduction";
	private final String APK_DOWNLOADURL = "apkdownloadurl";
	private final String APK_PACKAGENAME = "apkpackagename";
	private final String APK_ICONURL = "apkiconurl";
	private final String APK_THUMBNAIL1URL = "apkthumbnail1url";
	private final String APK_THUMBNAIL2URL = "apkthumbnail2url";
	private final String APK_THUMBNAIL3URL = "apkthumbnail3url";
	private final String APK_THUMBNAIL4URL = "apkthumbnail4url";

	private String CREATE_TABLE_GAME = "CREATE TABLE IF NOT EXISTS" + GAME_TABLENAME + "(" + APK_ID
			+ "integer NOT NULL PRIMARY KEY AUTOINCREMENT," + APK_NAME + " varchar(100)," + APK_SIZE + " varchar(100),"
			+ APK_BESUPPORTED + " varchar(100)," + APK_INTRODUCTION + " varchar(100)," + APK_DOWNLOADURL
			+ " varchar(100)," + APK_PACKAGENAME + " varchar(100)," + APK_ICONURL + " varchar(100)," + APK_THUMBNAIL1URL
			+ " varchar(100)," + APK_THUMBNAIL2URL + " varchar(100)," + APK_THUMBNAIL3URL + " varchar(100),"
			+ APK_THUMBNAIL4URL + " varchar(100))";

	public DataHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, "kanimoStore.db", factory, 1);
	}

	public DataHelper(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, "kanimoStore.db", factory, 1, errorHandler);

	}

	public DataHelper(Context context) {
		super(context, "kanimoStore.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_GAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + GAME_TABLENAME);
		onCreate(db);
	}

	public static DataHelper getInstance(Context context) {
		if (null == Mydatahelper) {
			Mydatahelper = new DataHelper(context);
		}
		if (null == mContext) {
			mContext = context;
		}
		return Mydatahelper;
	}

	public SQLiteDatabase getReadDatabase() {
		if (null == mReadData) {
			mReadData = Mydatahelper.getReadableDatabase();
		}
		return mReadData;
	}

	public SQLiteDatabase getWriteDatabase() {
		if (null == mWriteData) {
			mWriteData = Mydatahelper.getWritableDatabase();
		}
		return mWriteData;
	}

	public long insert(String apkname, String apksize, String apkbesupported, String apkintroduction,
			String apkdownloadurl, String apkpackagename, String apkiconurl, String apkthumbnail1url,
			String apkthumbnail2url, String apkthumbnail3url, String apkthumbnail4url, String where) {
		SQLiteDatabase db = getWriteDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(APK_NAME, apkname);
		contentValues.put(APK_SIZE, apksize);
		contentValues.put(APK_BESUPPORTED, apkbesupported);
		contentValues.put(APK_INTRODUCTION, apkintroduction);
		contentValues.put(APK_DOWNLOADURL, apkdownloadurl);
		contentValues.put(APK_PACKAGENAME, apkpackagename);
		contentValues.put(APK_ICONURL, apkiconurl);
		contentValues.put(APK_THUMBNAIL1URL, apkthumbnail1url);
		contentValues.put(APK_THUMBNAIL2URL, apkthumbnail2url);
		contentValues.put(APK_THUMBNAIL3URL, apkthumbnail3url);
		contentValues.put(APK_THUMBNAIL4URL, apkthumbnail4url);
		return db.insert(GAME_TABLENAME, where, contentValues);
	}

	public void saveGAMEData(ArrayList<APKEntity> apkdataList) {
		SQLiteDatabase db = getWriteDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + GAME_TABLENAME);
		onCreate(db);
		for (int i = 0; i < apkdataList.size(); i++) {
			APKEntity apkdata = apkdataList.get(i);
			insert(apkdata.getName(), apkdata.getSize(), apkdata.getBesupported(), apkdata.getIntroduction(),
					apkdata.getDownloadurl(), apkdata.getPackagename(), apkdata.getIconurl(), apkdata.getThumbnail1url(),
					apkdata.getThumbnail2url(),apkdata.getThumbnail3url(),apkdata.getThumbnail4url(), null);
		}
	}

}
