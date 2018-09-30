package com.baidu.push;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ubia.CloudSaveVideoListActivity;
import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.FileInfo;
import cn.ubia.customprovider.OssService;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.UbiaUtil;

public class PhoneMessageActivity extends BaseActivity {
	private static final String TAG = "PhoneMessageActivity";
	public int onTime = 300;
	private ImageView alarmimage ;
	public boolean isruning;
	 MediaPlayer mMediaPlayer;


	private void startAlarm() {  
		Uri mediaUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);  
         mMediaPlayer = MediaPlayer.create(this,    mediaUri);    
         if(mMediaPlayer!=null){
			 mMediaPlayer.setLooping(true);
			 mMediaPlayer.start();
         }
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonemessage);
		this.getActionBar().hide(); 
	    final Window win = getWindow();
	    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
	            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		if(getResources().getConfiguration().orientation==1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		wakeUpAndUnlock();
		isruning = true;
		AlarmMessage alarmMessage = new AlarmMessage("",UbiaApplication.messageUID, UbiaApplication.messageTime,UbiaApplication.messageEvent);

		//Log.v("deviceinfo","deviceinfo = "+alarmMessage.getUid()+" , "+alarmMessage.getAlarminfo() +","+alarmMessage.getAlarmTime()+","+alarmMessage.getEvent());

		UbiaUtil.phoneMessageActivity = this;

		if(alarmMessage==null){
			finish();
			return;
		}

		final DeviceInfo deviceInfo = findDeviceInfo(alarmMessage.getUid());

		if (deviceInfo == null) {
			Log.d(TAG, "deviceInfo not found");
			finish();
			return;
		}

		startAlarm();
		alarmimage = (ImageView) findViewById(R.id.thumbnail_img);
		if(!UbiaApplication.messageEvent.equals("lock")){
			if(!UbiaApplication.messageState.equals("1")){
				getImage();
			}
		}


		TextView camera_name_tv = (TextView) findViewById(R.id.camera_name_tv);
		if(alarmMessage.getEvent().equals("plug")){
			camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.page26_page34_MyPushMessageReceiver_alarm_plug_frombell));

		}else if(alarmMessage.getEvent().equals("push")){
			camera_name_tv.setText(deviceInfo.nickName+" "+getString(R.string.calling));


		} else if (alarmMessage.getEvent().equals("battery")) {
			try {
				int battery = Integer.valueOf(UbiaApplication.messageState);
				if (battery > 0 && battery < 20) {
					//设备电量不足，远程监控功 能将停止使用
					camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.push_lowerpower_level_1));
				} else if (battery >= 20 && battery < 28) {
					//“设备电量不足，请及时充电 ”
					camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.push_lowerpower_level_2));
				} else {
					return ;
				}
			}catch (Exception e){
				camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.page26_page34_MyPushMessageReceiver_alarm_plug_frombell));
			}

		} else if(alarmMessage.getEvent().equals("lock")){
			//有人按门铃，请求开锁
			camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.push_lock));
		}else{
			camera_name_tv.setText(deviceInfo.nickName+"\n"+getString(R.string.page26_page34_MyPushMessageReceiver_alarm_pir_frombell));
		}




		Button seeJBButton = (Button) this.findViewById(R.id.seeJB);
		seeJBButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(mMediaPlayer!=null){
				    mMediaPlayer.stop(); 
					mMediaPlayer.release(); 
					mMediaPlayer = null;
				}
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
				var21.putInt("isdoolbeel",100);
				var3.putExtras(var21);
				var3.setClass(PhoneMessageActivity.this, LiveViewGLviewActivity.class);
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
		
		ImageButton seeLVButton = (ImageButton) this.findViewById(R.id.seeLV);
		seeLVButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(mMediaPlayer!=null){
				    mMediaPlayer.stop(); 
					mMediaPlayer.release(); 
					mMediaPlayer = null;
				}
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
				var7.setClass(PhoneMessageActivity.this , LiveViewGLviewActivity .class);
				startActivity(var7);
			
				isruning = false;
				PhoneMessageActivity.this.finish();

			}
		});

		ImageButton caneJBButton = (ImageButton) this.findViewById(R.id.caneJB);
		caneJBButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mMediaPlayer!=null){
				    mMediaPlayer.stop(); 
					mMediaPlayer.release(); 
					mMediaPlayer = null;
				}
				isruning = false;
				diconnect();
				Intent intent = new Intent();
				intent.setAction("android.intent.phone.cancel");
				LocalBroadcastManager.getInstance(PhoneMessageActivity.this).sendBroadcast(intent);
				PhoneMessageActivity.this.finish();
			}
		});
//		
//

		new Thread() {
			public void run() {
				int count = 0;
				while (isruning) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
					if (count > 300) {
						if (mMediaPlayer != null) {
							mMediaPlayer.stop();
							mMediaPlayer.release();
							mMediaPlayer = null;
						}
						isruning = false;
						Intent intent = new Intent();
						intent.setAction("android.intent.phone.cancel");
						LocalBroadcastManager.getInstance(PhoneMessageActivity.this).sendBroadcast(intent);
						PhoneMessageActivity.this.finish();
						return;
					}
				}
			}
		}.start();



	}
 



	private DeviceInfo findDeviceInfo(String auid) {
		DeviceInfo var16 = null;
		DatabaseManager var1 = new DatabaseManager(this);
		SQLiteDatabase var2 = var1.getReadableDatabase();
		Cursor var3 = var2.query("device", new String[] { "_id",
				"dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
				"view_pwd", "event_notification", "camera_channel", "snapshot",
				"ask_format_sdcard", "camera_public" , "installmode","hardware_pkg"}, (String) null,
				(String[]) null, (String) null, (String) null, "_id LIMIT 50");
		var3.getCount();
		while (var3.moveToNext()) {
			long var4 = var3.getLong(0);
			String var6 = var3.getString(1);
			final String var7 = var3.getString(2);
			final String var8 = var3.getString(5);
			final String var9 = var3.getString(6);
			int var10 = var3.getInt(7);
			int var11 = var3.getInt(8);
			byte[] var12 = var3.getBlob(9);
			int var13 = var3.getInt(10);
			int ispublic = var3.getInt(11); 
			int installmode = var3.getInt(12);
			int hardware_pkg = var3.getInt(13);
			
			 var16 = new DeviceInfo(var4, var7, var6,
					var7, var8, var9, "", var10, var11, null);
			var16.installmode = installmode; 
			var16.hardware_pkg = hardware_pkg; 
			if(var16.UID.equals(auid)){
				break;
			}
		}
		var3.close();
		var2.close(); 
		return var16;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	 /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) UbiaApplication.getInstance().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) UbiaApplication.getInstance().getApplicationContext() 
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

	public void getImage() {

		OssService ossService;
		if(!UbiaApplication.messageUID.equals("")){
			int country = PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+ UbiaApplication.messageUID.toUpperCase() );
			if (StringUtils.getCurrentLocaltionISOCountryCodeString(country).equals("CN")) {
				ossService = getHelper().initOSS(UbiaApplication.endpoint, BuildConfig.OSSBUCKET);
			} else {
				ossService = getHelper().initOSS(UbiaApplication.endpoint_us, BuildConfig.OSSBUCKET + "-us");
			}


			ossService.setHandler(aliHandler);
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFileImgCloudPath(UbiaApplication.messageState);
			ossService.asyncGetImg(fileInfo);
		}


	}

	private Handler aliHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case OssService.GETIMGSUCCESS:
					FileInfo fileInfo = (FileInfo) msg.obj;
					alarmimage.setImageBitmap(fileInfo.getThumbnailImg());
					break;
				case OssService.GETIMGERROR:


					break;
			}
		}
	};

    @Override
    protected void onPause() { 
    	super.onPause();

    
    }
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(mMediaPlayer!=null){
		     mMediaPlayer.stop(); 
			mMediaPlayer.release(); 
			mMediaPlayer = null;
		}
	}

	private void diconnect(){
		Iterator var1 = CameraManagerment.getInstance().CameraList.iterator();
		while (var1.hasNext()) {
			cn.ubia.bean.MyCamera myCamera = (cn.ubia.bean.MyCamera) var1.next();
			Log.i("PhoneMessage", myCamera.getmUID() + " stop");
			CameraManagerment.getInstance().StopPPPP(myCamera.getmUID());

		}
		cn.ubia.bean.MyCamera.uninit();

		UbiaApplication.currentDeviceLive= "";
	}
}
