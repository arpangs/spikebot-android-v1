package com.spike.bot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.Constants;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static int counter=0;
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public static ConnectivityReceiverWifi connectivityReceiverWifi;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null
                && activeNetwork.isConnected();


        if (Constants.isWifiConnect) {
            if (Constants.isWifiConnectSave) {
                if (connectivityReceiverWifi != null) {
                    connectivityReceiverWifi.onNetworkConnectionChanged(isConnected);
                }
            }
        } else {
            //counter means that first double time calling api so
            if (connectivityReceiverListener != null && counter>0) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
            counter++;

        }
    }

    //for change network
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    //for ir blaster connection change
    public interface ConnectivityReceiverWifi {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}