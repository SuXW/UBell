package cn.ubia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.UbiaUtil;

import com.baidu.push.MessageDealReceiver;
import com.daemon.Receiver1;
import com.daemon.Receiver2;
import com.daemon.Service1;
import com.daemon.Service2;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import com.ubia.IOTC.LoadLibConfig;
import com.ubia.util.AntsImageDownloader;
import com.xiaomi.mipush.sdk.MiPushClient;

//import com.tutk.IOTC.MyCamera;

public class UbiaApplication extends DaemonApplication {

	public static List<DeviceInfo> myDeviceList = Collections
			.synchronizedList(new ArrayList<DeviceInfo>());
	public static List<MyCamera> CameraList = new ArrayList<MyCamera>();
	public static Boolean DEBUG = true;
	public  static Boolean ISWEB= false;
	public  static Boolean ISCLIENTB= false;
	
	public  static Boolean SHOWLASTSNAPSHOT= false;//show last snasp
	
	public  static Boolean DEFAULT_NO_PUTMODE = false;//show putmode choose
	
	public  static Boolean BUILD_CHECK_PWD = true;//check pwd detail
	public  static Boolean BUILD_FOR_WIFICAM = false;//
	
	public  static Boolean BUILD_CHECK_PLAYVOICE = false;//check playVoice; detail default close
	//public static Bus bus = new Bus();
	public static boolean add_mycamera = false;
	public static Context context;
	private static UbiaApplication instance;
	public static Boolean OPENBAIDU_PUSH = true;
	public static Boolean OPENXIAOMI_PUSH = true;
	public static final String APP_ID = BuildConfig.MIPUSHID;
	public static final String APP_KEY = BuildConfig.MIPUSHKEY;
	public static final String TAG = BuildConfig.APPLICATION_ID;

	public static   String currentDeviceLive = "";
	public static   String messageUID = "";
	public static   String messageTime = "";
	public static   String messageEvent = "";
	public static   String messageState = "";
	public static Boolean fromReceiver = false;
	public static int DefaultReceiverType = 2;
	public static String myCountryCode = "--";
	public static Boolean registerPushScuess = false;
	public static final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
	public static final String endpoint_us = "http://oss-us-west-1.aliyuncs.com";
	//public static final String bucket = "ubell";

	@Override
	public void onCreate() {
		super.onCreate(); 
		PreferenceUtil.getInstance().init(getApplicationContext());
		NotificationTagManager.getInstance().init(getApplicationContext());
		context = getApplicationContext();
		initImageLoader(getApplicationContext());
		LoadLibConfig ldlib =  LoadLibConfig.getInstance();
	    instance = this;
		if (UbiaUtil.shouldInitPush(context) ) {
			Log.e("UbiaApplication","Do RegisterPush APP_ID:"+APP_ID+" APP_KEY:"+APP_KEY);

			MiPushClient.registerPush(this, APP_ID, APP_KEY);

			JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
			JPushInterface.init(this);
		}else{
			Log.e("UbiaApplication","Skip RegisterPush APP_ID:"+APP_ID+" APP_KEY:"+APP_KEY);

		}
	}

    public static UbiaApplication getInstance() {
        return instance;
    }
    public static String getDeviceToken(){
    	return MiPushClient.getRegId(UbiaApplication.getInstance());
    }

    
	public static void initImageLoader(Context context) {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
															// limit
		} else {
			memoryCacheSize = 3 * 1024 * 1024;
		}

		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.denyCacheImageMultipleSizesInMemory()
				.imageDownloader(new AntsImageDownloader(context))
				// .enableLogging()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	/**
	 * you can override this method instead of {@link Application attachBaseContext}
	 * @param base
	 */
	@Override
	public void attachBaseContextByDaemon(Context base) {
		super.attachBaseContextByDaemon(base);
	}


	/**
	 * give the configuration to lib in this callback
	 * @return
	 */
	@Override
	protected DaemonConfigurations getDaemonConfigurations() {
		DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
				BuildConfig.APPLICATION_ID+ ":process1",
				Service1.class.getCanonicalName(),
				Receiver1.class.getCanonicalName());

		DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
				BuildConfig.APPLICATION_ID+ ":process2",
				Service2.class.getCanonicalName(),
				Receiver2.class.getCanonicalName());

		DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
		//return new DaemonConfigurations(configuration1, configuration2);//listener can be null
		return new DaemonConfigurations(configuration1, configuration2, listener);
	}


	class MyDaemonListener implements DaemonConfigurations.DaemonListener{
		@Override
		public void onPersistentStart(Context context) {
		}

		@Override
		public void onDaemonAssistantStart(Context context) {
		}

		@Override
		public void onWatchDaemonDaed() {
		}
	}
}
