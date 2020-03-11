package com.spike.bot.activity.TTLock;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.spike.bot.R;
import com.spike.bot.adapter.DoorSensorInfoAdapter;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.receiver.ConnectivityReceiver;

public class TTlockinfoActivity extends AppCompatActivity implements View.OnClickListener, DoorSensorInfoAdapter.OnNotificationContextMenu, ConnectivityReceiver.ConnectivityReceiverListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_tt_lock_info);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onEditOpetion(RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {

    }

    @Override
    public void onSwitchChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
