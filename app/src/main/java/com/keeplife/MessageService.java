package com.keeplife;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.UbiaApplication;
import cn.ubia.util.UbiaUtil;

import com.xiaomi.mipush.sdk.MiPushClient;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;


public class MessageService extends Service {

    private String TAG = "MessageService";

    private int ID=0X00022;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //Log.e(TAG, "MessageService====>print");

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
     
//        Notification.Builder builder = new Notification.Builder(mContext);
//        Notification notification = builder
//                .setContentText("messageservice")
//                .setSmallIcon(R.drawable.ubell_icon) 
//                .build();
//
//        startForeground(ID,notification);


        bindService(new Intent(MessageService.this,GuardService.class),mServiceConnection,BIND_WAIVE_PRIORITY);

        return START_STICKY;
    }
    public ServiceConnection  mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "MessageService====>onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {


     	   try {
               startService(new Intent(MessageService.this,GuardService.class));
               bindService(new Intent(MessageService.this,GuardService.class),mServiceConnection,BIND_WAIVE_PRIORITY);
    		   Log.e("","自启动>>>>>>>>>>>MessageService");
               if (UbiaUtil.shouldInitPush(mContext) ) {
                   MiPushClient.registerPush(mContext, APP_ID, APP_KEY);
                   JPushInterface.init(mContext);
               }


              /* Intent var2 = new Intent();
               var2.setComponent(new ComponentName(MessageService.this, "com.xiaomi.push.service.XMPushService")); 
               startService(var2);*/
           } catch (Exception e) {
     	       e.printStackTrace();
           }

        }
    }; 
//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ProcessConnect.Stub() {
        };
    }
}
