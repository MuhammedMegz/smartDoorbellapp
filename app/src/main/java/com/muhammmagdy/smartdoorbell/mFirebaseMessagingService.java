package com.muhammmagdy.smartdoorbell;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class mFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_ID = "channel1";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, videoCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        notificationManager = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.doorbell)
                .setContentTitle("Smart Doorbell")
                .setContentText(remoteMessage.getNotification().getBody().toString())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(1, notification);
        createNotificationChannel();




//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle("Doorbell Notification");
//        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setSmallIcon(R.mipmap.doorbell);
//        notificationBuilder.setContentIntent(pendingIntent);
//
//
//
//        notificationManager.notify(0, notificationBuilder.build());

    }

    private void createNotificationChannel() {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        NotificationChannel channel_one = new NotificationChannel(CHANNEL_ID,
                "Smart Doorbell",
                NotificationManager.IMPORTANCE_HIGH);

    channel_one.enableVibration(true);
    channel_one.setDescription("Someone is Calling Outside..");

    NotificationManager manager = getSystemService(NotificationManager.class);
    manager.createNotificationChannel(channel_one);
    }

    }


}
