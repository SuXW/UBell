package com.keeplife;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.util.UbiaUtil;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;


public class GuardService extends Service {
    private Context mContext;
    private int ID=0X00021;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
             
//            Notification.Builder builder = new Notification.Builder(mContext);
//            Notification notification = builder
//                    .setContentText("GuardService")
//                    .setSmallIcon(R.drawable.ubell_icon) 
//                    .build();
//
//            startForeground(ID,notification);

            bindService(new Intent(GuardService.this,MessageService.class),mServiceConnection,BIND_WAIVE_PRIORITY);

            return START_STICKY;
    }

//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ProcessConnect.Stub(){

        };
    }

    public ServiceConnection  mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("GuardService", "GuardService====>onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {


            try {
     		   Log.e("","自启动>>>>>>>>>>>GuardService");
                startService(new Intent(GuardService.this,MessageService.class));
                bindService(new Intent(GuardService.this,MessageService.class),mServiceConnection,BIND_WAIVE_PRIORITY);
                if (UbiaUtil.shouldInitPush(mContext) ) {
                    MiPushClient.registerPush(mContext, APP_ID, APP_KEY);
                    JPushInterface.init(mContext);
                }

              /*  Intent var2 = new Intent();
                var2.setComponent(new ComponentName(GuardService.this, "com.xiaomi.push.service.XMPushService")); 
                startService(var2);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
