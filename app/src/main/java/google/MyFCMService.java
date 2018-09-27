package google;

/**
 * Created by PIXPO-MAC on 2017/12/20.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baidu.push.MessageDealReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cn.ubia.MainActivity;
import cn.ubia.UBell.R;
import cn.ubia.UbiaApplication;

public class MyFCMService extends FirebaseMessagingService {
    private static final String TAG = "MyFCMService";
    public MyFCMService() {
		Log.e("FCMService", "OnCreate");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification

        Log.e("FCM", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("FCM", "Message data payload: " + remoteMessage.getData());
            MessageDealReceiver mMessageDealReceiver = MessageDealReceiver.getInstance();
            mMessageDealReceiver.onReceivePassThroughMessage(UbiaApplication.getInstance().getApplicationContext()
                    ,remoteMessage.getData().get("uid")
                    ,remoteMessage.getData().get("event")
                    ,remoteMessage.getData().get("timestamp")
                    ,remoteMessage.getData().get("state")
                    ,remoteMessage.getData().get("valud")

            );

    //    }
        }
    }

    private void sendNotification(String messageBody) {
        Log.d(TAG, "From: " + messageBody);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
   
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}