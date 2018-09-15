package com.ubia.IOTC;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;

import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.MainActivity;
import cn.ubia.UBell.R;
import cn.ubia.UbiaApplication;
import cn.ubia.bean.*;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;

/**
 * Created by Guo on 2018/9/13.
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private int refCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        refCount++;

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(final Activity activity) {
        refCount--;

        if(refCount == 0){
                new Thread() {
                    public void run() {
                        if(activity instanceof LiveViewGLviewActivity){
                            LiveViewGLviewActivity liveViewGLviewActivity = (LiveViewGLviewActivity)activity;
                            Log.i("ActivityListener","live quit");
                            StopAllSpeak(liveViewGLviewActivity);
                            CameraManagerment.getInstance().userIPCStopAlStream( UbiaApplication.currentDeviceLive);
                            CameraManagerment.getInstance().StopPPPP( UbiaApplication.currentDeviceLive);

                        }

                        if(activity instanceof MainActivity){
                            MainActivity mainActivity = (MainActivity)activity;
                            mainActivity.isBackgroundRunning = true;
                        }

                        Log.i("ActivityListener", "main quit:"+ activity.toString());

                        Iterator iterator = CameraManagerment.getInstance().CameraList.iterator();
                        while (iterator.hasNext()) {
                            cn.ubia.bean.MyCamera myCamera = (cn.ubia.bean.MyCamera) iterator.next();
                            Log.i("ActivityListener", myCamera.getmUID() + " stop");
                            CameraManagerment.getInstance().StopPPPP(myCamera.getmUID());

                        }
                        cn.ubia.bean.MyCamera.uninit();

                        UbiaApplication.currentDeviceLive= "";


                        for (DeviceInfo mDeviceInfo : MainCameraFragment.mainCameraFragment.DeviceList) {
                            mDeviceInfo.online = false;
                            mDeviceInfo.offline = true;
                            mDeviceInfo.lineing = false;
                            mDeviceInfo.connect_count = 0;
                            mDeviceInfo.device_connect_state = 0;

                        }
                    }
                }.start();

        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    protected void StopAllSpeak(LiveViewGLviewActivity activity) {
        // TODO Auto-generated method stub
        try {
            CameraManagerment.getInstance().userIPCstopListen( UbiaApplication.currentDeviceLive);
            CameraManagerment.getInstance().userIPCstopSpeak( UbiaApplication.currentDeviceLive);

            activity.mIsSpeaking = false;
            activity.mIsListening = false;
            Log.i("onTouch", "button_say.StopAllSpeak");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

