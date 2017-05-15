package com.devsideas.leapchat.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.devsideas.leapchat.R;
import com.devsideas.leapchat.ui.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Gustavo Forer on 14/05/2017.
 */

public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        displayNotification(remoteMessage.getNotification());

    }

    private void displayNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String body = notification.getBody();
        String[] result = body.split(",");
        if (!notification.getBody().contains("aceptado")) {
            intent.putExtra("phone", notification.getTitle());
            intent.putExtra("picture", result[1]);
            intent.putExtra("body", result[0]);
        } else {
            intent.putExtra("chat", notification.getTitle());
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(result[0])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
