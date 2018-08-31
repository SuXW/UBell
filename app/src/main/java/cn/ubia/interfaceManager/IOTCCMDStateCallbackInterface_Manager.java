package cn.ubia.interfaceManager;

 
public class IOTCCMDStateCallbackInterface_Manager implements IOTCResultCallBackInterface {

	public static boolean isLog = true;
	private static IOTCCMDStateCallbackInterface_Manager manager = null;
	private IOTCResultCallBackInterface mCallback = null;

	public IOTCResultCallBackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(IOTCResultCallBackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static IOTCCMDStateCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (IOTCCMDStateCallbackInterface_Manager.class) {
				manager = new IOTCCMDStateCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
 
	@Override
	public void IOTCCMDStatecallback(int IOTCCMD, int reuslt) {
		final IOTCResultCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.IOTCCMDStatecallback( IOTCCMD,reuslt); 
		}
	}
}
