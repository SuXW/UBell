package com.baidu.push;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.http.HttpClient;
import com.ubia.util.DateUtil;
import com.xiaomi.mipush.sdk.MiPushMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.MainActivity;
import cn.ubia.UBell.R;
import cn.ubia.UbiaApplication;
import cn.ubia.base.Constants;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.PreferenceUtil;


public class MessageDealReceiver extends BroadcastReceiver{

    String TAG = "MessageDealReceiver";

    private static MessageDealReceiver messageDealReceiver;

    public void onReceive(Context context, Intent intent) {

    }


    public static MessageDealReceiver getInstance() {
        if (messageDealReceiver == null) {
            messageDealReceiver = new MessageDealReceiver();
        }

        return messageDealReceiver;
    }



    public boolean callphoneInfoCallBack(Context context, String uid,
                                         String time, String event,String state) {

        if (UbiaApplication.currentDeviceLive.equals("") && (!MainCameraFragment.getRunningActivityName(LiveViewGLviewActivity.class.getSimpleName()) || LiveViewGLviewActivity.isBackgroundRunning)) {//&&  ( System.currentTimeMillis()- timeLast>delaycalltime)) {//no live view

            UbiaApplication.fromReceiver = true;
            UbiaApplication.messageUID = uid;
            UbiaApplication.messageTime = time;
            UbiaApplication.messageEvent = event; //这里要保存，不能直接传到下个activity，否则会接收到null,原因暂不明
            UbiaApplication.messageState = state;

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            return false;
        }
        //   if(!this.time.equals(time) &&  ( System.currentTimeMillis()- timeLast>delaycalltime)){
        Intent intent = new Intent("action.newDeviceCallBroadcastReceiver");
//			uid = "LFLDI6LJ2G3VXXU3FU5Q";
        intent.putExtra("alarmMessageuid", uid);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        //Log.d(TAG, "alarmInfoCallBack ,newDeviceCallBroadcastReceiver ,time =" + time);
        // this.time = time;

        //   }
        return true;
    }

    long lastTime = 0;
    String lastEvent = "";

    static long timeLast = 0;
    static long timeLastnotify = 0;
    int delaycalltime = 3000;

    Map<String, String> pushDeviceMap = new HashMap();

    public void onReceivePassThroughMessage(Context context, String messuid, String event, String timestamp,String state) {


        if (pushDeviceMap.get(messuid) != null) {
            lastEvent = pushDeviceMap.get(messuid).split("-")[0];
            lastTime = Long.valueOf(pushDeviceMap.get(messuid).split("-")[1]);
            if (lastEvent.equals(event)) {
                if (Long.valueOf(timestamp) <= lastTime) {
                    return;
                }
            }
        }



        if (UbiaApplication.currentDeviceLive.equals(messuid)) {
            Log.v("deviceinfo", "Same UID   UbiaApplication.messageUID =" + UbiaApplication.messageUID + "   messuid：" + messuid + "  UbiaApplication.currentDeviceLive:" + UbiaApplication.currentDeviceLive);
            return;
        }

        startPush(context, messuid, event, timestamp,state);

    }


    private DeviceInfo getDeviceInfo(String uid,Context context) {



        long _id;
        String dev_nickName;
        String dev_uid;

        String view_pwd;
        int event_notification;
        int camera_channel;


        int install_mode;
        int hardware_pkg;

        DeviceInfo deviceInfo = null;
        DatabaseManager databaseManager = new DatabaseManager(context);
        SQLiteDatabase database = databaseManager.getReadableDatabase();
        Cursor cursor = database.query("device", new String[]{"_id",
                        "dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
                        "view_pwd", "event_notification", "camera_channel", "snapshot",
                        "ask_format_sdcard", "camera_public", "installmode", "hardware_pkg"},
                null, null, null, null, "_id LIMIT 50");

        Log.i("guo..","getDeviceInfo:..........."+cursor.getCount());

        while (cursor.moveToNext()) {

                dev_uid = cursor.getString(2);

                Log.i("guo..","dev_uid:"+dev_uid+"...uid="+uid);

                if(dev_uid.equals(uid)){
                    _id = cursor.getLong(0);
                    dev_nickName = cursor.getString(1);


                    view_pwd = cursor.getString(6);
                    event_notification = cursor.getInt(7);
                    camera_channel = cursor.getInt(8);
                    install_mode = cursor.getInt(12);
                    hardware_pkg = cursor.getInt(13);


                    deviceInfo = new DeviceInfo(_id, dev_uid, dev_nickName,
                            dev_uid, dev_uid, view_pwd, "", event_notification, camera_channel, null);
                    Log.i("guo..","dev_uid:"+dev_uid+ " uid:"+uid+" nickName:" + deviceInfo.nickName);
                    deviceInfo.installmode = install_mode;
                    deviceInfo.connect_count = 0;
                    deviceInfo.hardware_pkg = hardware_pkg;

                }

        }

        if(database!=null){
            database.close();
        }

        if(cursor!=null){
            cursor.close();
        }

        return deviceInfo;
    }
    private void startPush(Context context, String messuid, String event, String timestamp,String state) {

        DeviceInfo deviceInfo = getDeviceInfo(messuid,context);

        if (deviceInfo == null) {
            Log.e("guo..startPush", "没有找到设备，移除tag:"+messuid);
            NotificationTagManager.getInstance().removeTag(messuid);
            return;
        }

        pushDeviceMap.put(messuid, event + "-" + timestamp);

        boolean showActivityCall = true;
        int pushType = !UbiaApplication.currentDeviceLive.equals("")? 1:PreferenceUtil.getInstance().getInt(Constants.MESSAGETYPE_CHECK + messuid, UbiaApplication.DefaultReceiverType);
        if (pushType == 0) {
            return;
        } else if (pushType > 1) {// 来电呼叫

            if (!TextUtils.isEmpty(messuid)) {
                callphoneInfoCallBack(UbiaApplication.getInstance()
                                .getApplicationContext(),
                        messuid,
                        DateUtil.formatNormalTimeStyle(System.currentTimeMillis()), event,state);

            }
        }else{
            NotificationManager NoManager = (NotificationManager) UbiaApplication
                    .getInstance().getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);


            UbiaApplication.messageUID = messuid;
            Log.v("deviceinfo", "UbiaApplication.messageUID =" + UbiaApplication.messageUID + "   messuid：" + messuid+" nickName="+deviceInfo.nickName);
            Bundle bundle = new Bundle();
            bundle.putString("dev_uid", messuid);
            bundle.putString("dev_uuid", messuid);
            bundle.putString("dev_uuid_deal", messuid);
            bundle.putBoolean("NotificationManager", true);
            Intent IntentLiveViewGLviewActivity = new Intent();
            IntentLiveViewGLviewActivity.putExtras(bundle);
            IntentLiveViewGLviewActivity.setClass(context, MainActivity.class);

            PendingIntent Pintent = PendingIntent.getActivity(UbiaApplication.getInstance().getApplicationContext(), 110, IntentLiveViewGLviewActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            Resources res = context.getResources();
            Bitmap nty_alert = BitmapFactory.decodeResource(res, R.drawable.nty_alert);
            String title;//context.getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_frombell);
            if (event.equals("push")) {
                title = UbiaApplication.getInstance().getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_frombell);
            } else if (event.equals("plug")) {
                title = UbiaApplication.getInstance().getString(R.string.page26_page34_MyPushMessageReceiver_alarm_plug_frombell);
            } else {
                title = UbiaApplication.getInstance().getString(R.string.page26_page34_MyPushMessageReceiver_alarm_pir_frombell);
            }
            //获取时间系统时间


            if (Build.VERSION.SDK_INT >= 26) {

                String channelId = "channel_1";
                String channelName = "push";

                NotificationChannel channel = new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_DEFAULT);

                NoManager.createNotificationChannel(channel);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId); //与channelId对应
                builder.setSmallIcon(R.drawable.nty_alert)
                        .setLargeIcon(nty_alert)
                        .setContentIntent(Pintent)
                        .setTicker(" " + title)
                        .setContentText("" + title)
                        .setContentTitle(" " + deviceInfo.nickName).setAutoCancel(true);

                NoManager.notify(0, builder.build());

                Log.d(TAG, "Receive onReceive message...8.0");

            } else {


                Notification.Builder builder = new Notification.Builder(UbiaApplication
                        .getInstance().getApplicationContext())
                        .setSmallIcon(R.drawable.nty_alert)
                        .setLargeIcon(nty_alert)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
//     	 .setFullScreenIntent(Pintent, true)
                        .setContentIntent(Pintent)
                        .setTicker(" " + title)
                        .setContentText("" + title)
                        .setContentTitle(" " + deviceInfo.nickName).setAutoCancel(true);
                NoManager.notify(0, builder.build());

                Log.d(TAG, "Receive onReceive message");
            }
        }


    }
}