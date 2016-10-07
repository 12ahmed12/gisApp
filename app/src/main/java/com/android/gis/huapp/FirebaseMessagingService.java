package com.android.gis.huapp;

/**
 * Created by Ahmed on 6/27/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message=remoteMessage.getData().get("message");
        String Title=remoteMessage.getData().get("Title");
        showNotification(message,Title);
    }

    private void showNotification(String message,String Title) {

        Intent i = new Intent(this, News.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(Title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo3)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }
}


