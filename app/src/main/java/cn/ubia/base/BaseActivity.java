package cn.ubia.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;

import cn.ubia.util.ActivityHelper;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;

import static cn.ubia.UbiaApplication.isSupportPad;

public class BaseActivity extends  Activity implements
		IRegisterIOTCListener {

	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		if(!isSupportPad){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

	}

	public ActivityHelper getHelper() {
		if (mHelper == null) {
			mHelper = new ActivityHelper(this);
		}
		return mHelper;
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	public void receiveChannelInfo(Camera camera, int channel, int code) {

	}

//	@Override
//	public void receiveErrorState(Camera camera, int channel, int ret) {
//
//	}

	@Override
	public void receiveIOCtrlData(Camera camera, int channel, int type,
			byte[] data) {

	}

	@Override
	public void receiveSessionInfo(Camera camera, int code) {

	}

//	@Override
//	public void receiveFrameData(Camera camera, int channel, Bitmap bitmap,
//			long time) {
//	}

	@Override
	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame  , int var8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub
		
	}

}
