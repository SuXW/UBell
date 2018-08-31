package com.timeline.listenview;

 
public class GetTimelineBitMapCallback_Manager implements GetTimelineBitMapbackInterface {

	public static boolean isLog = true;
	private static GetTimelineBitMapCallback_Manager manager = null;
	private GetTimelineBitMapbackInterface mCallback = null;

	public GetTimelineBitMapbackInterface getCallback() {
		if (mCallback != null) {
			return mCallback;
		}
		return null;

	}

	public void setmCallback(GetTimelineBitMapbackInterface mCallback) {
		this.mCallback = mCallback;
	}

	public synchronized static GetTimelineBitMapCallback_Manager getInstance() {
		if (null == manager) {
			synchronized (GetTimelineBitMapCallback_Manager.class) {
				manager = new GetTimelineBitMapCallback_Manager();
			}
		}
		return manager;
	}

	 
 

	@Override
	public void GetPushNoteStatecallback(NoteInfoData mNoteInfoData, boolean state) {
		// TODO Auto-generated method stub
		final GetTimelineBitMapbackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.GetPushNoteStatecallback(mNoteInfoData, state);
		}
	}

	@Override
	public void SetPushNoteStatecallback(boolean state) {
		// TODO Auto-generated method stub
		final GetTimelineBitMapbackInterface callback = getCallback();
		if (callback != null) {
		 
			callback.SetPushNoteStatecallback(  state);
		}
	}
}
