package com.hisilicon.hisilinkapi;

public class HisiLibApi {


	static {
		System.loadLibrary("HisiLink");
	}

	//将network信息设置给hisi_lib_api
	static public native int setNetworkInfo(int security, int port, int onlineProtocal, int ipAdress, String ssid, String password, String deviceName);
	//获取发送报文
	static public native byte []getMessageToSend();
	//获取设备AP密码
	static public native String getPassword(String ssid);
	//组播模式报文发送入口
	static public native int startMulticast();
	//停止组播发送
	static public native int stopMulticast();
}