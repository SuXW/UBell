package cn.ubia.interfaceManager;

 
public class LiveViewTimeStateCallbackInterface_Manager implements LiveViewTimeCallBackInterface {

	public static boolean isLog = true;
	private static LiveViewTimeStateCallbackInterface_Manager manager = null;
	private LiveViewTimeCallBackInterface mCallback = null;

	public LiveViewTimeCallBackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(LiveViewTimeCallBackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static LiveViewTimeStateCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (LiveViewTimeStateCallbackInterface_Manager.class) {
				manager = new LiveViewTimeStateCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
  
 
	@Override
	public void TimeUTCStatecallback(long utcTime) {
		final LiveViewTimeCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.TimeUTCStatecallback( utcTime); 
		}
	}

	@Override
	public void saveTimeMsSeccallback(long saveTime) {
		final LiveViewTimeCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.saveTimeMsSeccallback( saveTime); 
		}
	}

}
