package com.spike.bot.fcm;

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

import com.spike.bot.R;
import com.spike.bot.activity.CameraImagePush;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendNotificationAsync extends AsyncTask<String, Void, Bitmap> {

        private Context ctx;
        private String message;
        private String camera_url;
        private String camera_body;

        SendNotificationAsync(Context context) {
            super();
            this.ctx = context;
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

                URL url = new URL(params[2]);
                camera_url = params[2];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {

                Intent intent = new Intent(ctx, CameraImagePush.class); //{@link Main2Activity}
                intent.putExtra("camera_url",camera_url);
                intent.putExtra("camera_body",camera_body);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 , intent,PendingIntent.FLAG_UPDATE_CURRENT);

                String channelId = "1";
                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(ctx, channelId)
                                .setSmallIcon(R.drawable.ic_push)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                                .setContentTitle("Spike Bot")
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent)
                                .setLargeIcon(result);

                NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "Spike Bot",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                id ++;
                notificationManager.notify(id /* ID of notification */, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static int id = 0;
    }