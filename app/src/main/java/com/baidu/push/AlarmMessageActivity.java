package com.baidu.push;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.UBell.R;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;

public class AlarmMessageActivity extends Activity {
	private static final String TAG = "AlarmMessageActivity";
	MediaPlayer alarmAudio;
	public static boolean showMessage = false;
	private ImageView alarmimage ;
	protected boolean isruning;
//	public static   boolean Pushmute=PreferenceUtil.getInstance().getBoolean(Constants.IS_PUSHMUTE_CHECKED) ;
//	public static   int soundid=PreferenceUtil.getInstance().getInt(Constants.ALARMSOUND) ;
	// private static TextView alarmNum;

	// private static Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// int what = msg.what;
	// switch (what) {
	// case 0:// ���
	// alarmNum.setText(alarList.size() + "");
	// break;
	// }
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		 
		 //setTitle(R.string.text_alarm);
		 
		isruning = true;
		showMessage = true;
		AlarmMessage alarmMessage = (AlarmMessage) getIntent()
				.getSerializableExtra("alarmMessage");
		Log.v("deviceinfo","deviceinfo = "+alarmMessage.getUid()+"   "+alarmMessage.getAlarminfo() +"   "+alarmMessage.getAlarmTime());

		final DeviceInfo deviceInfo = MainCameraFragment.getexistDevice(alarmMessage.getUid());
		
		if (deviceInfo == null) {
			Log.d(TAG, "deviceInfo not found");
			finish();
			return;
		}
//		Pushmute=PreferenceUtil.getInstance().getBoolean(Constants.IS_PUSHMUTE_CHECKED) ;
//		int soundsrcid = R.raw.dee;
//		soundid=PreferenceUtil.getInstance().getInt(Constants.ALARMSOUND) ;
//			
//		if(!Pushmute)
		{
//			if(soundid == 0)
				//alarmAudio = MediaPlayer.create(this, R.raw.dee);
//			else
//				alarmAudio = MediaPlayer.create(this, R.raw.alarm);	
//		// alarmAudio.setVolume(1, 1);
			//alarmAudio.setLooping(true);
			//alarmAudio.start();
		}
//		final long utcTime = DateUtil.parseToUTCTime(alarmMessage
//				.getAlarmTime());
//
//		String alarmInfo = deviceInfo.nickName + "\n"
//				+ DateUtil.formatToNormalStyle(utcTime);
//
//		
		TextView camera_name_tv = (TextView) findViewById(R.id.camera_name_tv);
		camera_name_tv.setText(deviceInfo.nickName);
		/*final TextView alarmName = (TextView) this.findViewById(R.id.alarmName);
		//this.getText(R.string.device_name)
		alarmName.setText(deviceInfo.nickName);*/
//		
//		alarmimage=(ImageView) this.findViewById(R.id.alarmimage);
//		
//		final TextView alarmType = (TextView) this.findViewById(R.id.alarmType);
//		alarmType.setText( this.getText(R.string.txtEventType)+" "+this.getText(R.string.evttype_motion_detection));
		//final TextView JBInfoText = (TextView) this.findViewById(R.id.JBInfo2);
		// alarmNum = (TextView) this.findViewById(R.id.alarmnum);
		//JBInfoText.setText(alarmInfo);
		// alarmNum.setText(alarList.size() + "");
		
		Button seeJBButton = (Button) this.findViewById(R.id.seeJB);
		seeJBButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle var21 = new Bundle();
				Intent var3 = new Intent();
				var21.putString("dev_uuid", deviceInfo.UID);
				var21.putString("dev_uid", deviceInfo.UID);
				var21.putString("dev_nickName",
						deviceInfo.nickName);
				var21.putString("view_acc",
						deviceInfo.viewAccount);
				var21.putString("view_pwd",
						deviceInfo.viewPassword);
				var21.putInt("camera_channel",
						deviceInfo.getChannelIndex());
				var3.putExtras(var21);
				var3.setClass(AlarmMessageActivity.this, LiveViewGLviewActivity.class);
				startActivity(var3);

				/*if (null != alarmAudio && !Pushmute) {
					alarmAudio.release();
					alarmAudio = null;
				}
				// vibrator.cancel();
				isruning = false;
				AlarmMessageActivity.this.finish();*/
				
			}
		});
		
		Button seeLVButton = (Button) this.findViewById(R.id.seeLV);
		seeLVButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle var6 = new Bundle();
				var6.putString(
						"dev_uid",deviceInfo.UID);
				var6.putString(
						"dev_uuid",deviceInfo.UID);
				var6.putString(
						"dev_nickName",deviceInfo.nickName);
				var6.putString(
						"conn_status",deviceInfo.Status);
				var6.putString(
						"view_acc",deviceInfo.viewAccount);
				var6.putString(
						"view_pwd",deviceInfo.viewPassword);
				var6.putInt(
						"camera_channel",deviceInfo.getChannelIndex());
				Intent var7 = new Intent();
				var7.putExtras(var6);
				var7.setClass(AlarmMessageActivity.this ,
						LiveViewGLviewActivity .class); 
				startActivity(var7);
//				if (null != alarmAudio) {
//					alarmAudio.release();
//					alarmAudio = null;
//				}
				// vibrator.cancel();
				isruning = false;
				AlarmMessageActivity.this.finish();
			}
		});

		Button caneJBButton = (Button) this.findViewById(R.id.caneJB);
		caneJBButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (null != alarmAudio) {
//					alarmAudio.release();
//					alarmAudio = null;
//				}
//				isruning = false;
				AlarmMessageActivity.this.finish();
			}
		});
//		
//		
		
 
        
        
//		new Thread() {
//			public void run() {
//				int count =0;
//				while(isruning){ 
//					
//					Message var4 =  new Message();
//					var4.what = count++; 
//					 handler1.sendMessage(var4);
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch
//						// block
//						e.printStackTrace();
//					}
//				}
// 
//			};
//		}.start();
 	}
//	private Handler handler1 = new Handler() {
//		public void handleMessage(Message var1) {
//			// Bundle var13 = var1.getData();
//			
//			try {
////				Log.v("deviceinfo","what ="+var1.what);
////			 if(var1.what%2==0)
////				 alarmimage.setImageResource(R.drawable.adddev);
////			 else
////				 alarmimage.setImageResource(R.drawable.adddev_off);
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//	};

	public String getLocalTime(long utcTime) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(pattern);
		return localSimpleDateFormat.format(utcTime);
	}

	private DeviceInfo findDeviceInfo(String auid) {
		DeviceInfo deviceInfo = null;
		DatabaseManager dbManager = new DatabaseManager(this);
		SQLiteDatabase db = dbManager.getReadableDatabase();
		Cursor cursor = db.query("device", new String[] { "_id",
				"dev_nickname", "dev_uid", "view_acc", "view_pwd",
				"camera_channel", "public", "private", "share", "location",
				"alarm", "my", "share_user", "share_msg" }, "dev_uid=?",
				new String[] { auid }, null, null, "_id");
		if (cursor.moveToNext()) {
			long id = cursor.getLong(0);
			String nickname = cursor.getString(1);
			String uid = cursor.getString(2);
			String acc = cursor.getString(3);
			String pwd = cursor.getString(4);
			int channel = cursor.getInt(5);
			boolean isPublic = cursor.getInt(6) == 1;
			boolean isPrivate = cursor.getInt(7) == 1;
			boolean isShare = cursor.getInt(8) == 1;
			String location = cursor.getString(9);
			boolean isAlarm = cursor.getInt(10) == 1;
			boolean isMy = cursor.getInt(11) == 1;
			String shareUser = cursor.getString(12);
			String shareMessage = cursor.getString(13);
			Log.i(TAG, "nickname:" + nickname + " ,dev_uid:" + uid + " ,acc:"
					+ acc);
			deviceInfo = new DeviceInfo(id, null, nickname, uid, location, acc,
					pwd, channel, isPrivate, isPublic, isShare, isAlarm, isMy,
					shareUser, shareMessage);
		}
		cursor.close();
		db.close(); 
		return deviceInfo;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// private void Time() {
	// Calendar c = Calendar.getInstance();
	// int year = c.get(Calendar.YEAR);
	// int month = c.get(Calendar.MONTH);
	// int day = c.get(Calendar.DAY_OF_MONTH);
	// System.out.println(year);
	// System.out.println(month);
	// System.out.println(day);
	// month = (month + 1);
	// String m_month = month < 10 ? ("0" + month) : month + "";
	// String m_day = day < 10 ? ("0" + day) : day + "";
	// String time = year + m_month + m_day;
	// SharedPreferences timesettings = this.getSharedPreferences("shareFile",
	// Context.MODE_APPEND);
	// Editor editor = timesettings.edit();
	// GlobalUtil.date = timesettings.getString("time", "");
	// if (GlobalUtil.date.equals("")) {
	// editor.putString("time", time);
	// GlobalUtil.date = time;
	// editor.commit();
	// }
	//
	// }

	// private void getLocalSetting() {
	// SharedPreferences mPrefs = getSharedPreferences("sharefile",
	// MODE_PRIVATE);
	// GlobalUtil.PTZstep = mPrefs.getInt(EditorKey.CONFIPTZ, 4);
	// GlobalUtil.viewNumber = mPrefs.getInt(EditorKey.CONFIVIEWNAMBER, 1);
	// GlobalUtil.videoSize = mPrefs.getInt(EditorKey.CONFIVIDEOSIZE, 100);
	// GlobalUtil.videoTime = mPrefs.getInt(EditorKey.CONFIVIDEOTIME, 3);
	// }

	// private void initInfo() {
	// // SqlHelper sqlHelper = new SqlHelper();
	// // String[] recordList = sqlHelper.getList();
	// // if (recordList != null)
	// // {
	// // for (int i = 0; i < recordList.length; i++) {
	// // Cursor cursor = sqlHelper.getCurosr(String.format(
	// // "recordname=\"%s\"", recordList[i]));
	// // if (cursor.moveToFirst()) {
	// // saveInfo(cursor);
	// // cursor.close();
	// // }
	// // }
	// // }
	// // sqlHelper.close();
	// EyeDeviceManager deviceMgr = EyeDeviceManager.getInstance();
	// assert (deviceMgr.isLoaded());
	// deviceMgr.loadStoreAll();
	// }

	// private void saveInfo_(Cursor cursor) {
	// EyeDeviceManager deviceMgr = EyeDeviceManager.getInstance();
	// assert (deviceMgr.isLoaded());
	// EyeDeviceInfo info = deviceMgr.getDeviceInfo(cursor.getString(0));
	// if (null == info) {
	// Log.e("RecordDialog", "addInfo");
	// info = deviceMgr.createDeviceInfo(cursor.getString(0));
	// info.setUser(cursor.getString(1));
	// info.setPassword(cursor.getString(2));
	// info.setHost(cursor.getString(3));
	// info.setPort(cursor.getInt(4));
	// info.setChanDefault((byte) cursor.getInt(5));
	// info.setChanTotal((byte) cursor.getInt(6));
	// info.setAlarmOpen(cursor.getInt(7));
	// info.setAlarmCount(cursor.getInt(8));
	// info.setDeviceInfo(cursor.getString(9));
	// info.setVeri(cursor.getInt(10));
	// info.setVersion(cursor.getInt(11));
	// deviceMgr.saveStore(cursor.getString(0), null);
	// }
	// }
	//
	// private void addAlarmToLocal(String uid) {
	// EyeDeviceManager deviceMgr = EyeDeviceManager.getInstance();
	// assert (deviceMgr.isLoaded());
	// EyeDeviceInfo info = deviceMgr.getDeviceInfo(uid);
	// if (info == null) {
	// info = deviceMgr.createDeviceInfo(uid);
	// info.setUser("");
	// info.setPassword("");
	// info.setHost("");
	// info.setPort(0);
	// info.setChanTotal((byte) 1);
	// info.setUid(uid);
	// info.setVeri(0);
	// info.setVersion(1);
	// info.setPulbic(1);
	// deviceMgr.saveStore(uid, null);
	// } else {
	// info.setUser("");
	// info.setPassword("");
	// info.setHost("");
	// info.setPort(0);
	// info.setChanTotal((byte) 1);
	// info.setUid(uid);
	// info.setVeri(0);
	// info.setVersion(1);
	// info.setPulbic(1);
	// deviceMgr.saveStore(uid, null);
	// }
	//
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (alarmAudio != null) {
			alarmAudio.release();
			alarmAudio = null;
		}
		showMessage = false;
	}
}
