package cn.ubia.interfaceManager;

import cn.ubia.bean.ZigbeeInfo;

 
public class ZigbeeInfoCallbackInterface_Manager implements ZigbeeInfoCallBackInterface {

	public static boolean isLog = true;
	private static ZigbeeInfoCallbackInterface_Manager manager = null;
	private ZigbeeInfoCallBackInterface mCallback = null;

	public ZigbeeInfoCallBackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(ZigbeeInfoCallBackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static ZigbeeInfoCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (ZigbeeInfoCallbackInterface_Manager.class) {
				manager = new ZigbeeInfoCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
  
 
	@Override
	public void ZigbeeInfocallback(ZigbeeInfo mZigbeeInfo) {
		// TODO Auto-generated method stub
		final ZigbeeInfoCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.ZigbeeInfocallback( mZigbeeInfo); 
		}
	}
}
