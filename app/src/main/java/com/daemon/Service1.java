package com.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.util.UbiaUtil;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        if (UbiaUtil.shouldInitPush(this) ) {
            Log.e("daemon service","Do RegisterPush APP_ID:"+APP_ID+" APP_KEY:"+APP_KEY);

            MiPushClient.registerPush(this, APP_ID, APP_KEY);
            JPushInterface.init(this);
        }else{
            Log.e("daemon service","Skip RegisterPush APP_ID:"+APP_ID+" APP_KEY:"+APP_KEY);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
