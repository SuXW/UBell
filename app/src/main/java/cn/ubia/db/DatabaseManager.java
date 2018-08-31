package cn.ubia.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DatabaseManager {

	public static final String TABLE_DEVICE = "device";
	public static final String TABLE_REMOVE_LIST = "remove_list";
	public static final String TABLE_SEARCH_HISTORY = "search_history";
	public static final String TABLE_SNAPSHOT = "snapshot";
	public static int n_mainActivity_Status = 0;
	public static String s_GCM_IMEI = "";
	public static final String s_GCM_PHP_URL = "http://push.iotcplatform.com/apns/apns.php";
	public static final String s_GCM_SYNC_PHP_URL = "http://push.iotcplatform.com/apns/sync.php";
	public static final String s_GCM_sender = "935793047540";
	public static String s_GCM_token;
	public static final String s_Package_name = "com.tutk.p2pcamlive.2(Android)";
	public static final  int DEFAULT_STREAM_INDEX = 0;//Ĭ�ϳ�ʼ������ //maxwell 1612191035, 0:substream, 1: mainstream
	private DatabaseHelper mDbHelper;

	public DatabaseManager(Context var1) {
		this.mDbHelper = new DatabaseHelper(var1);
	}

	public static Bitmap getBitmapFromByteArray(byte[] var0) {
		return BitmapFactory.decodeStream(new ByteArrayInputStream(var0),
				(Rect) null, getBitmapOptions(2));
	}

	public static Options getBitmapOptions(int var0) {
		Options var1 = new Options();
		var1.inPurgeable = true;
		var1.inInputShareable = true;
		var1.inSampleSize = var0;

		try {
			Options.class.getField("inNativeAlloc").setBoolean(var1, true);
			return var1;
		} catch (IllegalArgumentException var6) {
			var6.printStackTrace();
			return var1;
		} catch (SecurityException var7) {
			var7.printStackTrace();
			return var1;
		} catch (IllegalAccessException var8) {
			var8.printStackTrace();
			return var1;
		} catch (NoSuchFieldException var9) {
			var9.printStackTrace();
			return var1;
		}
	}

	public static byte[] getByteArrayFromBitmap(Bitmap var0) {
		if (var0 != null && !var0.isRecycled()) {
			ByteArrayOutputStream var1 = new ByteArrayOutputStream();
			var0.compress(CompressFormat.PNG, 0, var1);
			return var1.toByteArray();
		} else {
			return null;
		}
	}

	public long addDevice(String var1, String var2, String var3, String var4,
						  String var5, String var6, int var7, int var8,int installmode,int pkg) {
		SQLiteDatabase var9 = this.mDbHelper.getWritableDatabase();
		ContentValues var10 = new ContentValues();
		var10.put("dev_nickname", var1);
		var10.put("dev_uid", var2);
		var10.put("dev_name", var3);
		var10.put("dev_pwd", var4);
		var10.put("view_acc", var5);
		var10.put("view_pwd", var6);
		var10.put("event_notification", Integer.valueOf(var7));
		var10.put("installmode", Integer.valueOf(installmode));
		var10.put("hardware_pkg", Integer.valueOf(pkg));
		var10.put("camera_channel",DatabaseManager.DEFAULT_STREAM_INDEX);
		long var11 = var9.insertOrThrow("device", (String) null, var10);
		var9.close();
		return var11;
	}

	public long addSearchHistory(String var1, int var2, long var3, long var5) {
		SQLiteDatabase var7 = this.mDbHelper.getWritableDatabase();
		ContentValues var8 = new ContentValues();
		var8.put("dev_uid", var1);
		var8.put("search_event_type", Integer.valueOf(var2));
		var8.put("search_start_time", Long.valueOf(var3));
		var8.put("search_stop_time", Long.valueOf(var5));
		long var9 = var7.insertOrThrow("search_history", (String) null, var8);
		var7.close();
		return var9;
	}

	public long addSnapshot(String var1, String var2, long var3) {
		SQLiteDatabase var5 = this.mDbHelper.getWritableDatabase();
		ContentValues var6 = new ContentValues();
		var6.put("dev_uid", var1);
		var6.put("file_path", var2);
		var6.put("time", Long.valueOf(var3));
		long var7 = var5.insertOrThrow("snapshot", (String) null, var6);
		var5.close();
		return var7;
	}

	public long add_remove_list(String var1) {
		SQLiteDatabase var2 = this.mDbHelper.getWritableDatabase();
		ContentValues var3 = new ContentValues();
		var3.put("uid", var1);
		long var4 = var2.insertOrThrow("remove_list", (String) null, var3);
		var2.close();
		return var4;
	}

	public void delete_remove_list(String var1) {
		SQLiteDatabase var2 = this.mDbHelper.getWritableDatabase();
		var2.delete("remove_list", "uid = \'" + var1 + "\'", (String[]) null);
		var2.close();
	}

	public SQLiteDatabase getReadableDatabase() {
		return this.mDbHelper.getReadableDatabase();
	}

	public void removeDeviceByUID(String var1) {
		SQLiteDatabase var2 = this.mDbHelper.getWritableDatabase();
		var2.delete("device", "dev_uid = \'" + var1 + "\'", (String[]) null);
		var2.close();
	}

	public void removeSnapshotByUID(String var1) {
		SQLiteDatabase var2 = this.mDbHelper.getWritableDatabase();
		var2.delete("snapshot", "dev_uid = \'" + var1 + "\'", (String[]) null);
		var2.close();
	}

	public void updateDeviceAskFormatSDCardByUID(String var1, boolean var2) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		byte var5;
		if (var2) {
			var5 = 1;
		} else {
			var5 = 0;
		}

		var4.put("ask_format_sdcard", Integer.valueOf(var5));
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);
		var3.close();
	}

	public void updateDeviceChannelByUID(String var1, int channelIndex) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("camera_channel", Integer.valueOf(channelIndex));
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);

		Log.v("","updateChannel camera_channel------->>>>"+channelIndex);
		var3.close();
	}
	public void updateDeviceInstallmodeByUID(String var1, int installmode) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("installmode", Integer.valueOf(installmode));
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);

		Log.v("","updatePutmode putmode------->>>>"+installmode);
		var3.close();
	}

	public void updateDeviceViewPwdByUID(String var1, String viewPwd) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("view_pwd", viewPwd);
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);

		Log.v("","update viewPwd------->>>>"+viewPwd);
		var3.close();
	}

	public void updateDeviceHardware_pkgByUID(String var1, int hardware_pkg) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("hardware_pkg", Integer.valueOf(hardware_pkg));
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);

		Log.v("","updatePutmode putmode------->>>>"+hardware_pkg);
		var3.close();
	}

	public void updateDeviceInfoByUID(long dbid,String uid, String nickName,
									  String devName, String devPwd, String viewAcc, String viewPwd, int event,
									  int channel, boolean ispublic) {
		SQLiteDatabase var11 = this.mDbHelper.getWritableDatabase();
		ContentValues var12 = new ContentValues();
		var12.put("dev_uid", uid);
		var12.put("dev_nickname", nickName);
		var12.put("dev_name", devName);
		var12.put("dev_pwd", devPwd);
		var12.put("view_acc", viewAcc);
		var12.put("view_pwd", viewPwd);
		var12.put("event_notification", Integer.valueOf(event));
		var12.put("camera_channel", Integer.valueOf(channel));
		var12.put("camera_public", ispublic ? 1 : 0);
		int  v  = var11.update("device", var12, "dev_uid = \'" + uid + "\'", (String[]) null);
		Log.e(getClass().getName(),"修改名称结果："+v +",uid="+uid);
		var11.close();
	}
	public long getDBIDbyUID(String uid){
		SQLiteDatabase sqLiteDatabase = this.mDbHelper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.query("device", new String[] { "_id"},  "dev_uid = \'" + uid + "\'",
				null,  null,  null, "_id LIMIT 50");
		while (cursor.moveToNext()) {
			long dbid = cursor.getLong(0);
			sqLiteDatabase.close();
			cursor.close();
			return dbid;
		}
		sqLiteDatabase.close();
		cursor.close();
		return 0;
	}
	public void updateDeviceInfoByDBID(long dbid, String uid, String nickName,
									   String devName, String devPwd, String viewAcc, String viewPwd, int event,
									   int channel, boolean ispublic) {
		SQLiteDatabase var11 = this.mDbHelper.getWritableDatabase();
		ContentValues var12 = new ContentValues();
		var12.put("dev_uid", uid);
		var12.put("dev_nickname", nickName);
		var12.put("dev_name", devName);
		var12.put("dev_pwd", devPwd);
		var12.put("view_acc", viewAcc);
		var12.put("view_pwd", viewPwd);
		var12.put("event_notification", Integer.valueOf(event));
		var12.put("camera_channel", Integer.valueOf(channel));
		var12.put("camera_public", ispublic ? 1 : 0);
		int  v  = var11.update("device", var12, "_id = \'" + dbid + "\'", (String[]) null);
		Log.e(getClass().getName(),"guo..修改名称结果："+v +",dbid="+dbid);
		var11.close();
	}

	public void updateDeviceSnapshotByUID(String var1, Bitmap var2) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("snapshot", getByteArrayFromBitmap(var2));
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);
		var3.close();
	}

	public void updateDeviceSnapshotByUID(String var1, byte[] var2) {
		SQLiteDatabase var3 = this.mDbHelper.getWritableDatabase();
		ContentValues var4 = new ContentValues();
		var4.put("snapshot", var2);
		var3.update("device", var4, "dev_uid = \'" + var1 + "\'",
				(String[]) null);
		var3.close();
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DB_FILE = "UBIAcamViewer.db";
		private static final int DB_VERSION = 9;
		private static final String SQLCMD_CREATE_TABLE_DEVICE = "CREATE TABLE device(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dev_nickname\t\t\tNVARCHAR(30) NULL, dev_uid\t\t\t\tVARCHAR(20) NULL, dev_name\t\t\t\tVARCHAR(30) NULL, dev_pwd\t\t\t\tVARCHAR(30) NULL, view_acc\t\t\t\tVARCHAR(30) NULL, view_pwd\t\t\t\tVARCHAR(30) NULL, event_notification \tINTEGER, ask_format_sdcard\t\tINTEGER,camera_channel\t\t\tINTEGER, snapshot\t\t\t\tBLOB,camera_public\t\t\tINTEGER,installmode\t\t\tINTEGER,hardware_pkg\t\t\tINTEGER);";
		private static final String SQLCMD_CREATE_TABLE_REMOVE_LIST = "CREATE TABLE remove_list(uid VARCHAR(20) NOT NULL PRIMARY KEY )";
		private static final String SQLCMD_CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE search_history(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dev_uid\t\t\tVARCHAR(20) NULL, search_event_type\tINTEGER, search_start_time\tINTEGER, search_stop_time\tINTEGER);";
		private static final String SQLCMD_CREATE_TABLE_SNAPSHOT = "CREATE TABLE snapshot(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dev_uid\t\t\tVARCHAR(20) NULL, file_path\t\t\tVARCHAR(80), time\t\t\t\tINTEGER);";
		private static final String SQLCMD_DROP_TABLE_DEVICE = "drop table if exists device;";
		private static final String SQLCMD_DROP_TABLE_REMOVE_LIST = "drop table if exists remove_list;";
		private static final String SQLCMD_DROP_TABLE_SEARCH_HISTORY = "drop table if exists search_history;";
		private static final String SQLCMD_DROP_TABLE_SNAPSHOT = "drop table if exists snapshot;";

		public DatabaseHelper(Context var2) {
			super(var2, DB_FILE, (CursorFactory) null, DB_VERSION);
		}

		public void onCreate(SQLiteDatabase var1) {
			var1.execSQL(SQLCMD_CREATE_TABLE_DEVICE);
			var1.execSQL(SQLCMD_CREATE_TABLE_SEARCH_HISTORY);
			var1.execSQL(SQLCMD_CREATE_TABLE_SNAPSHOT);
			var1.execSQL(SQLCMD_CREATE_TABLE_REMOVE_LIST);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			if (newVersion == 6) {
				db.execSQL("CREATE TABLE remove_list(uid VARCHAR(20) NOT NULL PRIMARY KEY )");
			}
			if (newVersion == 8) {
				db.execSQL("ALTER TABLE device ADD installmode INTEGER");
			}
			if (newVersion == 9) {
				db.execSQL("ALTER TABLE device ADD hardware_pkg INTEGER");
			}

		}
	}
	public void updateChannel(String uID, int channelIndex) {
		SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
		try {
			ContentValues var4 = new ContentValues();
			var4.put("camera_channel", channelIndex);
			db.update("device", var4, "dev_uid = \'" + uID + "\'",
					(String[]) null);

			Log.v("","updateChannel camera_channel------->>>>"+channelIndex);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
}
