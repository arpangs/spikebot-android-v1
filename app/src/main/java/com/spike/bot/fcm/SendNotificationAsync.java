package com.spike.bot.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.CameraImagePush;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SendNotificationAsync extends AsyncTask<String, Void, Bitmap> {

        private Context ctx;
        private String message,camera_url,camera_body,badge="";

        SendNotificationAsync(Context context, String badge) {
            super();
            this.ctx = context;
            this.badge = badge;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            /*for(String prm : params){
                Log.d("FCMNotification","params : " + prm);
            }*/
            try {
                InputStream in;
                message = params[0] + params[1];
                camera_body = message;

                if(params[2]!=null){
                    URL url = new URL(params[2]);
                    camera_url = params[2];
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    in = connection.getInputStream();
                    return BitmapFactory.decodeStream(in);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
                if(TextUtils.isEmpty(badge)){
                    badge="0";
                }
                ChatApplication.isPushFound=true;
                Intent intent = new Intent(ctx, CameraImagePush.class); //{@link Main2Activity}
                intent.putExtra("camera_url",camera_url);
                intent.putExtra("camera_body",camera_body);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 , intent,PendingIntent.FLAG_UPDATE_CURRENT);

                String channelId = "1";
                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(ctx, channelId)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                                .setContentTitle("Spike Bot")
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent)
                                .setNumber(Integer.parseInt(badge))
                                .setLargeIcon(result);

                notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

                NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, "Spike Bot", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setShowBadge(true);
                    notificationManager.createNotificationChannel(channel);
                }
                id ++;
                ShortcutBadger.applyCount(ctx, Integer.parseInt(badge));
                notificationManager.notify(id /* ID of notification */, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static int id = 0;
    }