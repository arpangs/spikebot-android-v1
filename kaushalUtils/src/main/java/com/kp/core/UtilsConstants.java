package com.kp.core;

import android.util.Log;

import com.kaushalprajapti.util.BuildConfig;

public class UtilsConstants {

    public static void logDisplay(String message){
        if (BuildConfig.DEBUG) {
            Log.d("System out",""+message);
        }

    }
}
