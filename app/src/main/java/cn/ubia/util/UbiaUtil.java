package cn.ubia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.ubia.UbiaApplication;
import cn.ubia.fragment.MainCameraFragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.push.PhoneMessageActivity;

import cn.ubia.UBell.BuildConfig;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;

public class UbiaUtil {
	public static PhoneMessageActivity phoneMessageActivity;


	public  static boolean shouldInitPush(Context context) {
		//if(!OPENXIAOMI_PUSH) return false;
		ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = context.getPackageName();
		int myPid = Process.myPid();
		for (ActivityManager.RunningAppProcessInfo info : processInfos) {

			if (info.pid == myPid && mainProcessName.equals(info.processName)) {

				return false;
			}
		}
		return true;

		/*ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		String serviceName= "com.xiaomi.push.service.XMPushService";

		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				return false;
			}
		}
		return true;*/
	}

	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	public byte[] int2bytes2(int channel) {
		byte[] b = new byte[32];
		for (int i = 0; i < 32; i++) {
			b[i] = (byte) (channel >>> (24 - i * 8));
		}
		return b;
	}

	static public int bytes2int(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[3 - i] & mask;
			res |= temp;
		}
		return res;
	}

	static public short bytes2short(byte[] b) {
		short mask = 0xff;
		short temp = 0;
		short res = 0;
		for (int i = 0; i < 2; i++) {
			res <<= 8;
			temp = (short) (b[1 - i] & mask);
			res |= temp;
		}
		return res;
	}

	static public byte[] short2bytes(short data) {
		byte bytes[] = new byte[2];

		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[0] = (byte) (data & 0x00ff);
		return bytes;
	}

	/**
	 * @return
	 */
	public static String getCurrentTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		String strTz = tz.getDisplayName(false, TimeZone.SHORT);
		return strTz;

	}

	public static String getLocalVersionName(Context ctx) {
		String localVersion = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionName;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	public static String getVedioFileNameWithTime() {
		Calendar localCalendar = Calendar.getInstance();
		int i = localCalendar.get(1);
		int j = 1 + localCalendar.get(2);
		int k = localCalendar.get(5);
		int m = localCalendar.get(11);
		int n = localCalendar.get(12);
		int i1 = localCalendar.get(13);
		localCalendar.get(14);
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer.append("VEDIO_");
		localStringBuffer.append(i);
		if (j < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(j);
		if (k < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(k);
		localStringBuffer.append('_');
		if (m < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(m);
		if (n < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(n);
		if (i1 < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(i1);
		localStringBuffer.append(".mp4");
		return localStringBuffer.toString();
	}

	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}
	public static boolean[] validatepassword(String password){
	    boolean ret[] = {false,true,false,false};
	    int i;
	    boolean haslowercase= false;
	    boolean hasluppercase= false;
	    boolean hasnumber= false;
	    
	    if (password == null ||  password.length()  < 8) {
	          ret[1]= false;
	    }
	    if ( password.length()  > 64) {
	          ret[1]= false;
	    }
	    
	     byte   utf8str[] = password.getBytes();
	    
	    for (i = 0; i < password.length() ; i++) {
	        if (utf8str[i] >= 0x30 && utf8str[i]<=0x39) {
	            hasnumber = true;
	            ret[0]= true;
	        }
	        if (utf8str[i] >= 0x61 && utf8str[i]<=0x7a) {
	            haslowercase = true;
	            ret[3]= true;
	        }
	        if (utf8str[i] >= 0x41 && utf8str[i]<=0x5a) {
	            hasluppercase = true;
	            ret[2]= true;
	        }
	        if (utf8str[i] == 0x20) {
	            break;
	        }
	    }
		if(!BuildConfig.bAnumber){
			ret[0]= true;
		}
		if(!BuildConfig.b8characters){
			ret[1]= true;
		}
		if(!BuildConfig.bAnuppercaseletter){
			ret[2]= true;
		}
		if(!BuildConfig.bAlowercaseletter){
			ret[3]= true;
		}
	    if(!UbiaApplication.BUILD_CHECK_PWD)
	    {
	        ret[0]= true;
	        ret[1]= true;
	        ret[2]= true;
	        ret[3]= true;
	    	 
	    }
	    if(MainCameraFragment.build_for_factory_tool){
			ret[0]= true;
			ret[1]= true;
			ret[2]= true;
			ret[3]= true;
		}
	    
	    return ret;
	}


	private static void printResult(String type, byte[] bt, int len) {
		System.out.println(type + "长度为：" + len);
		String cont = type + "的内容为：";
		System.out.print(cont);
		for (int ix = 0; ix < len; ++ix) {
			System.out.printf("%02x ", bt[ix]);
		}
		System.out.println("\n----------");
	}

}
