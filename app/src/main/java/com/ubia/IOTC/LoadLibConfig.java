package com.ubia.IOTC;

import android.util.Log;

public class LoadLibConfig {

	private static LoadLibConfig manager = null;
	public synchronized static LoadLibConfig getInstance() {
		if (null == manager) {
			synchronized (LoadLibConfig.class) {
				manager = new LoadLibConfig();
			}
		}
		return manager;
	}

	static {
		{
			try {
				Log.i("loadlib",
						"VRConfig..................................start");
				System.loadLibrary("VRConfig");
				Log.i("loadlib",
						"VRConfig..................................end");
			} catch (UnsatisfiedLinkError ule) {
				System.out.println("loadLibrary(VRConfig)," + ule.getMessage());
			}
		}
		try {
			Log.i("loadlib",
					"IOTCAPIs_ubia..................................start");
			System.loadLibrary("IOTCAPIs_ubia");
			Log.i("loadlib",
					"IOTCAPIs_ubia..................................end");
		} catch (UnsatisfiedLinkError var1) {
			System.out.println("loadLibrary(IOTCAPIs)," + var1.getMessage());
		}

		{
			try {
				Log.i("loadlib",
						"AVAPIs_ubia..................................start");
				System.loadLibrary("AVAPIs_ubia");
				Log.i("loadlib",
						"AVAPIs_ubia..................................end");
			} catch (UnsatisfiedLinkError var1) {
				System.out.println("loadLibrary(AVAPIs)," + var1.getMessage());
			}
		}
		{
			try {
				System.loadLibrary("FdkAACCodec");
			} catch (UnsatisfiedLinkError ule) {
				System.out.println("loadLibrary(FdkAACCodec),"
						+ ule.getMessage());
			}
		}

		{
			try {
				System.loadLibrary("WiFiDirectConfig");
			} catch (UnsatisfiedLinkError ule) {
				System.out.println("loadLibrary(WiFiDirectConfig),"
						+ ule.getMessage());
			}
		}
	}
}
