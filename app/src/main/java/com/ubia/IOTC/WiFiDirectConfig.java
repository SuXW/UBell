/**
 * 
 */
/**
 * @author apple
 *
 */
package com.ubia.IOTC;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import cn.ubia.adddevice.LoginAddDeviceActivity;
import cn.ubia.adddevice.WIfiAddDeviceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;

//import com.tutk.IOTC.UnsatisfiedLinkError;

public class WiFiDirectConfig {
	private String UID;
	private String Ssid;
	private String Key;
	public static WiFiDirectConfig wiFiDirectConfig = null;

	public static WiFiDirectConfig getInstance() {
		if (wiFiDirectConfig == null) {
			wiFiDirectConfig = new WiFiDirectConfig();
		}
		return wiFiDirectConfig;
	}

	private static List<IRegisterUBIAListener> mIOTCListeners = Collections
			.synchronizedList(new Vector());

	public native static int StartConfig(String UID, String Ssid, String Key,
										 int Tmout, int isBell);


	public native static int StopConfig();

	public native static int CheckStatus();
	public native static int GetSocketSrcIPAddr();
	public native static short GetSocketSrcPort();


	public static void StartnetConfig(String UID, String Ssid, String Key,
									  int Tmout, int isBell) {
		// CallbackStatus(1);
		StartConfig(UID, Ssid, Key, Tmout, isBell);
	}

	public static void CallbackStatus(int Success,String uid, String Macaddr, int pkg) {
		Log.i("IOTCamera", "uid:" + uid +" MACAddr:" + Macaddr + "DEVPKG:" + pkg);
		for (int i = 0; i < mIOTCListeners.size(); i++) {
			mIOTCListeners.get(i).CallbackNetconfigStatus(Success,uid,pkg);
		}
		// CallbackNetconfigStatus(Success);
	}

	
	 public static void WifiConfigToAddDevice(int Success,Bundle date) {
	
	 for (int i = 0; i < mIOTCListeners.size(); i++) {
			mIOTCListeners.get(i).CallWifiConfigToAddDevice(Success,date);
	 }
	 // CallbackNetconfigStatus(Success);
	 }

	public static boolean unregisterUBICListener(IRegisterUBIAListener var1) {
		boolean var2 = mIOTCListeners.contains(var1);
		boolean var3 = false;
		if (var2) {
			Log.i("IOTCamera", "unregister IOTC listener");
			mIOTCListeners.remove(var1);
			var3 = true;
		}

		return var3;
	}

	public static boolean registerUBICListener(IRegisterUBIAListener var1) {
		boolean var2 = mIOTCListeners.contains(var1);
		boolean var3 = false;
		if (!var2) {
			Log.i("IOTCamera", "register IOTC listener");
			mIOTCListeners.add(var1);
			var3 = true;
		}
		return var3;
	}



	/*
	 * public static interface IRegisterUBIAListener {
	 * 
	 * void CallbackNetconfigStatus(int Success);
	 * 
	 * }
	 */
}
