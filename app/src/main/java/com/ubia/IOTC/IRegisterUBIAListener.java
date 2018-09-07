package com.ubia.IOTC;

import android.os.Bundle;

public interface IRegisterUBIAListener {



	// Channel连接时回调
	abstract void CallbackNetconfigStatus(int Success, String uid, int pkg);
	// wifi配置好并添加设备成功回调
	//abstract void CallWifiConfigToAddDevice(int Success,String uid);

	abstract void CallWifiConfigToAddDevice(int success, Bundle date);

}
