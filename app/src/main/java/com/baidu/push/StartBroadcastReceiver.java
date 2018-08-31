package com.baidu.push;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.util.UbiaUtil;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;

public class StartBroadcastReceiver extends BroadcastReceiver{
 

public void onReceive(Context context, Intent intent) {
                 {
                	   try {
                		   Log.e("StartBroadcastReceiver","自启动>>>>>>>>>>>StartBroadcastReceiver");
                         /*  Intent var2 = new Intent();
                           var2.setComponent(new ComponentName(context, "com.xiaomi.push.service.XMPushService")); 
                           context.startService(var2);*/

                           if (UbiaUtil.shouldInitPush(context) ) {
                               MiPushClient.registerPush(context, APP_ID, APP_KEY);
                               JPushInterface.init(context);
                           }

                       } catch (Exception var3) {
                    
                       }
                }
        }     
}