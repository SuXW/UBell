package cn.ubia.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.db.DatabaseManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.xiaomi.mipush.sdk.MiPushClient;

public class NotificationTagManager {

	private Context mContext;
	private static NotificationTagManager mManager;

	public static NotificationTagManager getInstance() {
		if (mManager == null) {
			mManager = new NotificationTagManager();
		}
		return mManager;
	}

	public void init(Context context) {
		if (mContext == null) {
			mContext = context;
		}
		findDeviceUID();
	}

	private List<String> baiduPushTags = Collections
			.synchronizedList(new ArrayList<String>());
	private List<String> xiaomiPushTags = Collections
			.synchronizedList(new ArrayList<String>());
	private List<String> baiduRemovePushTags = Collections
			.synchronizedList(new ArrayList<String>());
	private List<String> dbPushTags = Collections
			.synchronizedList(new ArrayList<String>());
	public void addTag(String tag) {
		if (!baiduPushTags.contains(tag)) {
			baiduPushTags.add(tag);
			Log.v("xiaomi", "PushManager.setTags="+tag);
		}
	}
	public void addxiaomiTag(String tag) {
		if (!xiaomiPushTags.contains(tag)) {
			MiPushClient.setUserAccount(mContext, tag, null) ;
			FirebaseMessaging.getInstance().subscribeToTopic(tag);
			Log.e("FirebaseMessaging", "FirebaseMessaging.subscribeToTopic  UID="+tag);

		}
	}

	public void addJiguanTag(Set<String> tag) {
		JPushInterface.addTags(mContext,1,tag);
	}


	public void listTags() {
		xiaomiPushTags = MiPushClient.getAllUserAccount(mContext);
		for (String dbuid : xiaomiPushTags) {

		}
	}
	public void unbind() {
		Log.v("deviceinfo","deviceinfo = stopWork");
		MiPushClient.unregisterPush(mContext);
	}
	private void findDeviceUID( ) { 
		dbPushTags.clear();
		DatabaseManager var1 = new DatabaseManager(mContext);
		SQLiteDatabase var2 = var1.getReadableDatabase();
		Cursor var3 = var2.query("device", new String[] { "_id",
				"dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
				"view_pwd", "event_notification", "camera_channel", "snapshot",
				"ask_format_sdcard", "camera_public" }, (String) null,
				(String[]) null, (String) null, (String) null, "_id LIMIT 50");
		var3.getCount();
		while (var3.moveToNext()) {
			long var4 = var3.getLong(0);
			String var6 = var3.getString(1);
			String var7 = var3.getString(2);
			String var8 = var3.getString(5);
			String var9 = var3.getString(6);
			int var10 = var3.getInt(7);
			int var11 = var3.getInt(8);
			byte[] var12 = var3.getBlob(9);
			int var13 = var3.getInt(10);
			int ispublic = var3.getInt(11);
			dbPushTags.add(var7);
		}
		var3.close();
		var2.close(); 
	}

	public void removeTag(String tag) {

		try {
			Log.v("FirebaseMessaging", "FirebaseMessaging.unsubscribeFromTopic  UID=" + tag);
			FirebaseMessaging.getInstance().unsubscribeFromTopic(tag);
			Log.v("xiaomi", "UID=" + tag);
			MiPushClient.unsetUserAccount(mContext, tag, null);

			Set<String> tags = new LinkedHashSet<>();
			tags.add(tag);
			JPushInterface.deleteTags(mContext, 0x6, tags);
		}catch (Exception e){
			e.printStackTrace();
		}

	  }


}
