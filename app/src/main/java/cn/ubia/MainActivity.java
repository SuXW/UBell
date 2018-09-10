package cn.ubia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.ActivityHelper;
import cn.ubia.util.UbiaUtil;

import com.baidu.push.MessageDealReceiver;
import com.baidu.push.NotificationsUtils;
import com.baidu.push.PhoneMessageActivity;
import com.baidu.push.StartBroadcastReceiver;
import com.daemon.Service1;
import com.decoder.util.H264Decoder;
import com.keeplife.GuardService;
import com.keeplife.JobWakeUpService;
import com.keeplife.MessageService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.http.HttpClient;
import com.xiaomi.mipush.sdk.MiPushClient;

public class MainActivity extends BaseActivity {

	 
	public static boolean isBackgroundRunning;
	private int registerPushCount = 0;
	private Fragment mContent;
	private ActivityHelper mHelper;
	StartBroadcastReceiver myBroadcast = new StartBroadcastReceiver();
	private MainCameraFragment mFrag;
	private static String TAG = "MainActivity";
	  private void openTwoService() {

	        if(Build.VERSION.SDK_INT < 26) { // 8.0以下
				try { // 某些型号手机会在省电模式时禁止服务启动，在服务启动的地方进行try catch防止崩溃
				startService(new Intent(this,MessageService.class));
				startService(new Intent(this,GuardService.class));
				}catch (Exception e){
					e.printStackTrace();
				}
				if(Build.VERSION.SDK_INT >= 23){  // 6.0以上
					scheduleJob(this);
				}
	           // startService(new Intent(this, JobWakeUpService.class));
			}else { //8.0以上
				scheduleJob(this);
			}
	    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		setTitle(R.string.app_name); 

		android.app.FragmentTransaction t = this.getFragmentManager()
				.beginTransaction();
		mFrag = new MainCameraFragment();
		t.replace(R.id.content_frame, mFrag);
		t.commit();
  
		MainCameraFragment.mCameraLoaded = false;

		mHelper = new ActivityHelper(this);
 
		IntentFilter intentFilter = new IntentFilter();  
		intentFilter.addAction("action.refreshFriend");
	    registerReceiver(mRefreshBroadcastReceiver, intentFilter); 
	    
		 IntentFilter intentFilter2 = new IntentFilter();  
		 intentFilter2.addAction("action.doLogout");  
	     registerReceiver(doLogoutReceiver, intentFilter2);  
	    
	    IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED"); 
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.phone.cancel");
        filter.addAction(Intent.ACTION_TIME_TICK);//系统时间，每分钟发送一次
		registerReceiver(mHomeKeyEventReceiver,filter);

	     registerReceiver(myBroadcast, filter);
	     openTwoService();
	     final Window win = getWindow();
		    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

	     
	/*	new Thread(new  Runnable() {
			@Override
			public void run() {
			UbiaApplication.registerPushScuess = false;
			while (registerPushCount<=5) {
				Log.e(TAG, "xiaomi  going to registerPush registerPushCount：  " +  registerPushCount + "   registerPushScuess：" + 	UbiaApplication.registerPushScuess);
				if ((shouldInit() || !	UbiaApplication.registerPushScuess) && registerPushCount < 5) {
					MiPushClient.registerPush(MainActivity.this, BuildConfig.MIPUSHID, BuildConfig.MIPUSHKEY);
				}
				if(	UbiaApplication.registerPushScuess){
					break;
				}
				*//*if (registerPushCount == 5 ) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
						Toast.makeText(	MainActivity.this, 	MainActivity.this.getString(R.string.register_fail), Toast.LENGTH_LONG).show();
						}
					});
				}*//*
				registerPushCount++;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			}
		}).start();
		startService(new Intent( this, Service1.class));*/
	}

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {  
        String SYSTEM_REASON = "reason";  
        String SYSTEM_HOME_KEY = "homekey";  
        String SYSTEM_HOME_KEY_LONG = "recentapps";  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();
        	MiPushClient.registerPush(MainActivity.this, UbiaApplication.APP_ID, UbiaApplication.APP_KEY);

            Log.e("","应用 action："+action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)&& !isBackgroundRunning) {  
                String reason = intent.getStringExtra(SYSTEM_REASON);  
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {  
                	isBackgroundRunning = true;
                	 
                	UbiaApplication.currentDeviceLive= "";
                	quit();
                	Log.e("","应用退到后台");
                
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){  
                } else if(TextUtils.equals(reason, "lock")){  
                	isBackgroundRunning = true;
                	UbiaApplication.currentDeviceLive= ""; 
                	quit();
                }  
            }  
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            	Log.e("","应用 开屏");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            	Log.e("","应用 MMMMMMm锁屏");
               	isBackgroundRunning = true;
            	UbiaApplication.currentDeviceLive= ""; 
            	quit();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            	Log.e("","应用 解锁");
            }  else if ("android.intent.phone.cancel".equals(action)) { // 解锁
            	Log.e("","应用 取消接听电话");
            	MainActivity.this.moveTaskToBack(true);
             	isBackgroundRunning = true;
             	UbiaApplication.currentDeviceLive= ""; 
            	quit();
            }    
        }  
    };
	   private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (action.equals("action.refreshFriend"))
	            {
	                Log.i("MainActivity","BroadcastReceiver 重新加载");
	                MainCameraFragment.mainCameraFragment.setAdapternotifyDataSetChanged();
	            }
	        }
	    };
	    
	 private BroadcastReceiver doLogoutReceiver = new BroadcastReceiver() {

		        @Override
		        public void onReceive(Context context, Intent intent) {
		            String action = intent.getAction();
		            if (action.equals("action.doLogout"))
		            {
		                Log.i("MainActivity","BroadcastReceiver 退出登录");
		                logoutback();
		            }
		        }
		    };
	private void renewToken() {
		// String tokenTime = mHelper.getConfig(Constants.TOKEN_TIME);
		// if (!TextUtils.isEmpty(tokenTime)) {
		// long time = Long.parseLong(tokenTime);
		// // 小于一天,不更新
		// if (System.currentTimeMillis() - time < 12 * 60 * 60 * 1000) {
		// return;
		// }
		// }

		String token = mHelper.getConfig(Constants.TOKEN);
		String tokenSecret = mHelper.getConfig(Constants.TOKEN_SECRET);
		String account = mHelper.getConfig(Constants.USER_NAME);
		HttpClient httpClient = new HttpClient(token, tokenSecret);
		httpClient.renewToken(account, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				Log.d("response", response.toString());
				String token = response.optString("Token");
				String tokenSecret = response.optString("Token_secret");
				mHelper.saveConfig(Constants.TOKEN, token);
				mHelper.saveConfig(Constants.TOKEN_SECRET, tokenSecret);
				mHelper.saveConfig(Constants.TOKEN_TIME,
						System.currentTimeMillis() + "");
			}

		});
	}

	public void switchContent(Fragment fragment, String tag) { 
		mContent = fragment; 
		 
	}

	// initCameraList
	private long mBackPressTime;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
 		this.setIntent(intent);
 
	}

	public void onResume() {
		super.onResume();
		isBackgroundRunning = false; 
		//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, this);
		Bundle var2 = this.getIntent().getExtras();
		Boolean fromReceiverNotify = false;
		if(var2!=null){
			fromReceiverNotify = getIntent().getExtras().getBoolean("NotificationManager");
		}
 		Log.i("IOTCamera", "MainActivity onResume  登陆成功！！！！fromReceiverNotify:"+ fromReceiverNotify);
		 
		MainCameraFragment.mCameraLoaded = false; 
		mHelper = new ActivityHelper(this);
		MyCamera.init();
	 
	    Log.i("IOTCamera", "onResume    登陆成功！！！！UbiaApplication.fromReceiver:"+UbiaApplication.fromReceiver);
	   if(UbiaApplication.fromReceiver){



		   PhoneMessageActivity phoneMessageActivity = UbiaUtil.phoneMessageActivity;
			//上次的要finish掉，否则上次的activity内的计时还在继续，重复收到推送时会出现画面没有了，铃声还在响的情况
		   if (phoneMessageActivity != null) {
		   		if(phoneMessageActivity.isFinishing()){
					phoneMessageActivity.isruning = false;
					phoneMessageActivity.finish();
				}
		   }


		   Intent activityIntent = new Intent(this, PhoneMessageActivity.class);
		   activityIntent.setClass(this, PhoneMessageActivity.class);
		   this.startActivity(activityIntent);

	 	}
		if(fromReceiverNotify){
			String mDevUID = var2.getString("dev_uid");
			Bundle var21 = new Bundle();
			Intent var3 = new Intent();
			var21.putString( "dev_uid",mDevUID);
			var21.putString( "dev_uuid",mDevUID);
			var21.putString( "dev_uuid_deal",mDevUID);
			var21.putBoolean( "NotificationManager",true);
			var3.putExtras(var21);
			var3.setClass( this, LiveViewGLviewActivity.class);
			startActivity(var3);


		}
		if((var2!=null)) {
			String mDevUID = var2.getString("dev_uid");
			Bundle var21 = new Bundle();
			Intent var3 = new Intent();
			var21.putString("dev_uid", mDevUID);
			var21.putString("dev_uuid", mDevUID);
			var3.putExtras(var21);
			this.setIntent(var3);
		}
	   UbiaApplication.fromReceiver = false; 
	}

	public void onPause() {
		super.onPause();
		 Log.i("IOTCamera", "MainActivity onPause   ！！！！");
	}

	private void logoutback(){
		
	 
		MainCameraFragment.DeviceList.clear(); 
		MainCameraFragment.mCameraLoaded = false; 
		new Thread() {
			public void run() {
				quit();
				CameraManagerment.getInstance().CameraList.clear();
				if (MainCameraFragment.mainCameraFragment != null) {
					MainCameraFragment.mainCameraFragment = null;
					Log.i("IOTCamera",
							"MainCameraFragment.mainCameraFragment is finish");
					// MainCameraFragment.mainCameraFragment.getActivity().finish();
				}
			  
			}

		}.start();
		MainActivity.this.finish();
 
		
	}
	
	private void quit() {
		Log.i("IOTCamera", "main>>>>>>>>>>>>>quit");
		 CameraManagerment.getInstance().Free();
		if (MainCameraFragment.mainCameraFragment != null) {
			  MainCameraFragment.mainCameraFragment.stopOnGoingNotification();
			NotificationManager notificationManager = (NotificationManager) this
					.getSystemService("notification");
			notificationManager.cancel(0);
			notificationManager.cancel(1);
			Iterator var1 = CameraManagerment.getInstance().CameraList.iterator();
			while (var1.hasNext()) {
				MyCamera var2 = (MyCamera) var1.next();
				Log.i("IOTCamera",var2.getmUID()+ "main>>>>>>>>>>>>>stop");
				CameraManagerment.getInstance().StopPPPP(var2.getmUID());
				var2.disconnect();
				var2.unregisterIOTCListener(MainCameraFragment.mainCameraFragment);
			}

			// System.out.println("kill process");
		  MyCamera.uninit();
		}
		for (DeviceInfo mDeviceInfo : MainCameraFragment.mainCameraFragment.DeviceList) {
			Resources res = getResources();
			String text = res
					.getString(R.string.fragment_liveviewactivity_mainactivity_state_disconnected);
			mDeviceInfo.Status = text; 
			mDeviceInfo.online = false;
			mDeviceInfo.offline = true;
			mDeviceInfo.lineing = false;
			mDeviceInfo.connect_count =0;
			mDeviceInfo.device_connect_state = 0;

		}
//		CameraManagerment.getInstance().CameraList.clear();

	}

	public boolean onKeyDown(int var1, KeyEvent var2) {
		Log.i("IOTCamera", "main>>>>>>>>>>>>>quit");
		Log.i("first", "===================================onResume var1:"+var1);
	 
		if (var1 == 4) {
			Builder var3 = new Builder(this);
			var3.setMessage(this.getText(R.string.page18_dialog_Exit));
			var3.setPositiveButton(this.getText(R.string.page18_btnExit),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();
							new Thread() {
								public void run() {
									quit();
  //merge by maxwell
									MyCamera.uninit();
								}

							}.start();
							// quit();
							finish();
						}
					});
			var3.setNeutralButton(this.getText(R.string.page26_MainActivity_btnRunInBackground),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									MainActivity.this.moveTaskToBack(true);
								}
							});
							if (CameraManagerment.getInstance().CameraList.size() > 0) {
								MainActivity.this.moveTaskToBack(true);
							}

						}
					});
			var3.setNegativeButton(this.getText(R.string.btnCancel),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();
						}
					});
			var3.create().show();
			return false;
		} else {
			return true;
		}
		}
	 
 
	protected void onDestroy() {
		Log.i("IOTCamera", "MAinacitivty>>>>>>>>>>>>>onDestroy");
		unregisterReceiver(mRefreshBroadcastReceiver);
		unregisterReceiver(doLogoutReceiver);
		unregisterReceiver(mHomeKeyEventReceiver );
		unregisterReceiver(myBroadcast );
		quit();
		MyCamera.uninit();
		super.onDestroy();
		finish();
		
		
		Process.killProcess(Process.myPid());
		System.exit(0);  
	};
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void scheduleJob(Context context) {
		ComponentName serviceComponent = new ComponentName(context, JobWakeUpService.class);
		JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);
		//	builder.setPeriodic(15 * 60 * 1000, 5 * 60 *1000);
		builder.setMinimumLatency ( 10 * 1000 ).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);;
		JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		int ret = jobScheduler.schedule(builder.build());
		if (ret == JobScheduler.RESULT_SUCCESS) {
			Log.d(TAG, "Job scheduled successfully");
		} else {
			Log.d(TAG, "Job scheduling failed");
		}
	}
}
