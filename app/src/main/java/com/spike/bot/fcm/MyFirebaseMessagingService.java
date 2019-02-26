package com.spike.bot.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMNotification";
    String badge="0";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            ChatApplication.logDisplay("Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob("");
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

            String message = remoteMessage.getData().get("default");
            badge = remoteMessage.getData().get("badge");

            ChatApplication.logDisplay("Message data payload: badge " + badge);

            if(!TextUtils.isEmpty(message)){
                sendNotification(message);
            }else{

                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String attachment = remoteMessage.getData().get("attachment");

                scheduleJob(title,body,attachment);
            }

            String dataPayloadMessage = remoteMessage.getData().get("message");
            if(!TextUtils.isEmpty(dataPayloadMessage)){
                sendNotification(dataPayloadMessage);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            ChatApplication.logDisplay("Message Notification Body: " + remoteMessage.getNotification());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     * @param title
     * @param body
     * @param attachment
     */
    private void scheduleJob(String title, String body, String attachment) {
        // [START dispatch_job]
      /*  FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);*/
        // [END dispatch_job]
        new SendNotificationAsync(getApplicationContext(),badge).execute(body, "", attachment);
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        ChatApplication.logDisplay("Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static int id = 0;
    private void sendNotification(String messageBody) {

        ChatApplication.isPushFound=true;
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = "1";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_push)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentTitle("Spike Bot")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                        .setNumber(Integer.parseInt(badge))
                .setContentIntent(pendingIntent);


//        int color = getResources().getColor(R.color.automation_red);
//        notificationBuilder.setColor(color);


        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Spike Bot",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
        id ++;
        ChatApplication.logDisplay("fcm id is "+id);
        ShortcutBadger.applyCount(MyFirebaseMessagingService.this, Integer.parseInt(badge));
       notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}