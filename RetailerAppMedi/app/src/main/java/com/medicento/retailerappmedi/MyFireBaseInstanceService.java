package com.medicento.retailerappmedi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.medicento.retailerappmedi.activity.NotificationActivity;
import com.medicento.retailerappmedi.activity.UploadPurchaseActivity;

import java.util.Map;
import java.util.Random;

public class MyFireBaseInstanceService extends FirebaseMessagingService {

    private static final String TAG = "FIIDService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("message_recived", "message");
        if (remoteMessage != null && remoteMessage.getData() != null) {
            try {
                if (remoteMessage.getData().isEmpty())
                    showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                else
                    showNotification(remoteMessage.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification(Map<String, String> data) {

        String title = data.get("title").toString();
        String title_upper = data.get("title_upper").toString();

        Intent intent;

        if (data.containsKey("play_store")) {
            intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.medicento.retailerappmedi"
                    )
            );
        } else if (data.containsKey("type")) {
            String type = data.get("type").toString();
            if (type.equals("PO") || type.equals("PI") || type.equals("request")) {
                intent = new Intent(getApplicationContext(), UploadPurchaseActivity.class).putExtra("type", type);
            } else {
                intent = new Intent(getApplicationContext(), NotificationActivity.class);
            }
        } else {
            intent = new Intent(getApplicationContext(), NotificationActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "com.medicento.retailerappmedi";

        NotificationCompat.Builder notificationBuilder;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationBuilder = new NotificationCompat.Builder(this);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setColor(Color.RED)
                    .setContentTitle(title_upper)
                    .setContentText(title)
                    .setSmallIcon(R.drawable.mdl)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(title))
                    .setContentIntent(pendingIntent);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Medicento");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);


            assert notificationManager != null;
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        } else {

            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setColor(Color.RED)
                    .setChannelId("Medicento")
                    .setContentTitle(title_upper)
                    .setSmallIcon(R.drawable.mdl)
                    .setContentText(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(title))
                    .setContentIntent(pendingIntent);

            assert notificationManager != null;
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }

    }

    private void showNotification(String title, String body) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "com.medicento.retailerappmedi";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Medicento");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentText(title)
                .setContentText(body);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        try {
            String refreshedToken = s;
            Log.d(TAG, "onTokenRefresh Refreshed token : " + refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// dev.send_message(data={'title_upper': 'Update Needed', 'title': 'Please Update your app for better user experience', 'play_store': 'true'})
