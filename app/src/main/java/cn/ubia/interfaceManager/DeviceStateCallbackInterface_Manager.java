package cn.ubia.interfaceManager;

import android.util.Log;
 
public class DeviceStateCallbackInterface_Manager implements DeviceStateCallbackInterface {

	public static boolean isLog = true;
	private static DeviceStateCallbackInterface_Manager manager = null;
	private DeviceStateCallbackInterface mCallback = null;

	public DeviceStateCallbackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(DeviceStateCallbackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static DeviceStateCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (DeviceStateCallbackInterface_Manager.class) {
				manager = new DeviceStateCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
  
	@Override
	public void DeviceStateCallbackInterface(String did, int type, int param) {
		// TODO Auto-generated method stub
		final DeviceStateCallbackInterface callback = getCallback();
		if (callback != null) {
 			callback.DeviceStateCallbackInterface(  did,   type,   param);
			callback.DeviceStateCallbackLiveInterface(  did,   type,   param);
		}
	}

	@Override
	public void DeviceStateCallbackLiveInterface(String did, int type, int param) {
		// TODO Auto-generated method stub
		
	}
}
