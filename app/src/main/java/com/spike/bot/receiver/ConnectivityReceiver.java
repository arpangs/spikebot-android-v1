package com.spike.bot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.spike.bot.core.Constants;

public class ConnectivityReceiver extends BroadcastReceiver {
 
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public static ConnectivityReceiverWifi connectivityReceiverWifi;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        Log.d("System out", "onNetworkConnectionChanged isConnected111 : " + isConnected);

        if(Constants.isWifiConnect){
            if(Constants.isWifiConnectSave){
                if(connectivityReceiverWifi!=null){
                    connectivityReceiverWifi. onNetworkConnectionChanged(isConnected);
                }

            }
        }else {
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
        }


    }
 
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public interface ConnectivityReceiverWifi {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}