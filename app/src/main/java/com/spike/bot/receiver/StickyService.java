package com.spike.bot.receiver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.spike.bot.ChatApplication;

public class StickyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//       if(ChatApplication.mScanner!=null){
//           ChatApplication.logDisplay("on trim memory onTaskRemoved");
//           ChatApplication.mScanner.scanLeDevice(-1, false);
//       }
    }
}