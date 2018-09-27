package com.baidu.push;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle; 
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.MainActivity;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.Constants;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.util.PreferenceUtil;

import com.ubia.util.DateUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="cn.ubia.UBell.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */


/**
 * @param context
 *            Context
 * @param intent
 *            接收的intent
 */

public class DemoMessageReceiver extends PushMessageReceiver {
	 
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private String TAG  = "xiaomi";
    private boolean isRunningForeground (Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(UbiaApplication.getInstance()
				.getApplicationContext().getPackageName()))
        {
            return true ;
        }
     
        return false ;
    }
    
    public void alarmInfoCallBack(Context context, String title, String uid,
    		String time) {
    	 
		AlarmMessage alarmMessage = new AlarmMessage(title, uid, time);
    	Intent activityIntent = new Intent(context, MainActivity.class) .putExtra("alarmMessage", alarmMessage);
    	activityIntent.setClass(context, MainActivity.class);
    	activityIntent.putExtra("alarmMessage", alarmMessage);
    	activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(activityIntent); 
    	
    	
 
    	Log.d(TAG, "alarmInfoCallBack  ,context ="+context);
    }




  /* static long timeLast   = 0;
   static long   timeLastnotify = 0;*/
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
    //	  Log.e(TAG,"onReceivePassThroughMessage is called. >>>>>" + message.toString()+" lasttime="+lastTime);

		//if(  System.currentTimeMillis()- timeLastnotify >delaycalltime) {
				Log.v(TAG,"onReceivePassThroughMessage is called. deal");
				String messuid = message.getUserAccount();
				JSONObject jo;
				String event="";
				String timestamp="";
				String state="";
				String value="";
			try {
				jo = new JSONObject(message.getContent());
				event = jo.optString("event");
				timestamp= jo.optString("timestamp");
				state= jo.optString("state");
				value = jo.optString("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			MessageDealReceiver mMessageDealReceiver = MessageDealReceiver.getInstance();
			mMessageDealReceiver.onReceivePassThroughMessage(UbiaApplication.getInstance().getApplicationContext(),messuid,event,timestamp,state,value);

         
    }
   
    
	 MediaPlayer mMediaPlayer;
	private void startAlarm() {  
		Uri mediaUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
        mMediaPlayer = MediaPlayer.create(UbiaApplication.getInstance().getApplicationContext(),    mediaUri);        
        mMediaPlayer.setLooping(false);  
        mMediaPlayer.start();  
   } 
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(TAG,
                "xiaomiaaaa onNotificationMessageClicked is called. " + message.toString());
      DeviceInfo deviceInfo =  MainCameraFragment. getLoaclDevice(message.getUserAccount());
      if(deviceInfo==null){
    	  Log.e("error","  未在本地找到设备，通知点击无效");
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
		var7.setClass(context, LiveViewGLviewActivity.class); 
		var7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
    	context.startActivity(var7); 
        
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(TAG,
                "xiaomiaaaa onNotificationMessageArrived is called. " + message.toString());
        
        {
        	JSONObject jo;
			try {
				jo = new JSONObject(message.getContent());
				String title = jo.optString("title");
				String description = jo.optString("description");
				String time = jo.optString("time");
				String uid = jo.optString("uid");
				String type = jo.optString("type");
				Log.v("deviceinfo","deviceinfo = "+title+"   "+description+"   "+time+"   "+type+"   "+uid);
			} catch (JSONException e) {
				e.printStackTrace();
			}

        }
    	  boolean hasfoundUID = false ;
    	 Log.v("deviceinfo", "onMessage(Context context, String message  String customContentString) message ="+message  );
    	 if(isRunningForeground(UbiaApplication
    				.getInstance().getApplicationContext()))
    	 {
    	  	 Log.v("deviceinfo", "onMessage(Context context, String message  String customContentString)   App在前端，接受P2p消息"  );
    		 return;	
    	 }
 
    	 try { 
    			 
    			String title = message.getTitle();
    			String description = message.getDescription(); 
    			String uid = message.getUserAccount(); 
    			Log.v("deviceinfo","deviceinfo title = "+title+"   description="+description+"   " +"  uid= "+uid);
    		
    			DatabaseManager var1 = new DatabaseManager( UbiaApplication
    					.getInstance().getApplicationContext());
    			SQLiteDatabase var2 = var1.getReadableDatabase();
    			Cursor var3 = var2.query("device", new String[] { "_id",
    					"dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
    					"view_pwd", "event_notification", "camera_channel", "snapshot",
    					"ask_format_sdcard", "camera_public" , "installmode","hardware_pkg"},
    					(String) null, (String[]) null, (String) null, (String) null,
    					"_id LIMIT 50");
    			String devname = "null" ;
    			long _id  = 0  ;
    			String dev_nickName = "" ;
    			String dev_uid = "" ;
    			String view_acc = "" ;
    			String view_pwd  = "";
    			int event_notification  = 0  ;
    			int camera_channel  = 0 ;
    			byte[] var12  ;
    			int var13  = 0  ;
    			int ispublic  = 0  ;
    			while (var3.moveToNext()) {
    				_id = var3.getLong(0);
    				dev_nickName = var3.getString(1);
    				dev_uid = var3.getString(2);
    				view_acc = var3.getString(5);
    				view_pwd = var3.getString(6);
    				event_notification = var3.getInt(7);
    				camera_channel = var3.getInt(8);
    				 var12 = var3.getBlob(9);
    				 var13 = var3.getInt(10);

    					int installmode = var3.getInt(12);
    					int hardware_pkg = var3.getInt(13);
    				  ispublic = var3.getInt(11);  
    				if(dev_uid.equals(uid))
    				{
    					devname =dev_nickName;
    					hasfoundUID= true;
    					break;
    				}
    			}
    		 if(!hasfoundUID)
    		 {
    			 Log.v("deviceinfo", "百度推送未能在本地找到相关设备 message=" + message );
    			 return;
    		 }
    		String messageString = "透传消息 message=\"" + message
    				+ "\" customContentString=" + message.getContent();
    		NotificationManager NoManager = (NotificationManager) UbiaApplication
    				.getInstance().getApplicationContext()
    				.getSystemService("notification");
    		Log.v("deviceinfo", "messageString =" + messageString);

    		Bundle var21 = new Bundle();
    		var21.putString("dev_uid", uid);
    		var21.putString("dev_nickName",devname);
    		var21.putString("view_acc",view_acc);
    		var21.putString("view_pwd",view_pwd);
    		var21.putInt("camera_channel",camera_channel);
    		Intent intent = new Intent(UbiaApplication.getInstance()
    				.getApplicationContext(), MainActivity.class);
    		intent.putExtras(var21);
    		PendingIntent Pintent = PendingIntent.getActivity(UbiaApplication
    				.getInstance().getApplicationContext(), 0, intent, 134217728);
    		//获取时间系统时间
    		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    		Log.v("deviceinfo", "date =" + date);
    		 
    		
    		
    		SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");     
    		String    eventdate    =    sDateFormat.format(new    Date());
    		 
    		
    		boolean  showactivity = false; //true default   
    		if (  !TextUtils.isEmpty(uid)&& showactivity) {
    			alarmInfoCallBack(UbiaApplication
    					.getInstance().getApplicationContext(), title, uid, DateUtil.formatNormalTimeStyle(System.currentTimeMillis()));
    		}
    		
    		
    		title =UbiaApplication.getInstance(). getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_from) +" " + devname;
    		//获取时间系统时间
    		Notification var16 = new Notification(
    				 R.drawable.nty_alert, title, (System.currentTimeMillis()));
    		var16.flags |= Notification.FLAG_AUTO_CANCEL;

    		} catch (Exception e) {
    			Log.d(TAG, "?JSONException  Receive onReceive message");
    			e.printStackTrace();
//    		}
    		}
    	
    	 
       
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e("MIPUSH",
                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
 				UbiaApplication.registerPushScuess = true;
            } else {
                log = context.getString(R.string.register_fail);
				UbiaApplication.registerPushScuess = false;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.page34_set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.page34_set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.page34_unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.page34_unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.page34_set_account_success, mAccount);
            } else {
                log = context.getString(R.string.page34_set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.page34_unset_account_success, mAccount);
            } else {
                log = context.getString(R.string.page34_unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.page34_subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.page34_subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.page34_unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.page34_unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.page34_set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.page34_set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }
        Log.v(TAG,
                "onCommandResult is called. log：" + log);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e("MIPUSH",
                "onReceiveRegisterResult is called. " + message.toString());
       
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

}
