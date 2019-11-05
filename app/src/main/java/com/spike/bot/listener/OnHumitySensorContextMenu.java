package com.spike.bot.listener;

import android.support.v7.widget.SwitchCompat;

import com.spike.bot.model.RemoteDetailsRes;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public interface OnHumitySensorContextMenu {
    void onEditHumityOpetion(RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit);
    void onSwitchHumityChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive);

}
