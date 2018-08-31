package cn.ubia.interfaceManager;

 
public class DoorStateCallbackInterface_Manager implements DoorCallBackInterface {

	public static boolean isLog = true;
	private static DoorStateCallbackInterface_Manager manager = null;
	private DoorCallBackInterface mCallback = null;

	public DoorCallBackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(DoorCallBackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static DoorStateCallbackInterface_Manager getInstance() {
		if (null == manager) {
			synchronized (DoorStateCallbackInterface_Manager.class) {
				manager = new DoorStateCallbackInterface_Manager();
			}
		}
		return manager;
	}

	 
  
 
	@Override
	public void DoorStatecallback(int doorstate) {
		// TODO Auto-generated method stub
		final DoorCallBackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.DoorStatecallback( doorstate); 
		}
	}
}
