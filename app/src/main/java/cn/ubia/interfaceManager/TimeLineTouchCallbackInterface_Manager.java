package cn.ubia.interfaceManager;

 
public class TimeLineTouchCallbackInterface_Manager implements TimeLineTouchCallBackInterface {

	public static boolean isLog = true;
	private static TimeLineTouchCallbackInterface_Manager manager = null;
	private TimeLineTouchCallBackInterface mCallback = null;

	public TimeLineTouchCallBackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(TimeLineTouchCallBackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static TimeLineTouchCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (TimeLineTouchCallbackInterface_Manager.class) {
				manager = new TimeLineTouchCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
  
 
	@Override
	public void TimeLineTouchStatecallback(int mTouchstate) {
		// TODO Auto-generated method stub
		final TimeLineTouchCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.TimeLineTouchStatecallback( mTouchstate); 
		}
	}
}
