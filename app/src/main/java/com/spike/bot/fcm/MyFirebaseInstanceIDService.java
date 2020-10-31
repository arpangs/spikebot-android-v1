package com.spike.bot.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    // FirebaseInstanceId.getInstance().getToken();
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        if (!token.isEmpty()) {
            Log.e("NEW_TOKEN", token);
        }

    }

}
