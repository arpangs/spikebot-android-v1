package com.spike.bot.listener;

import android.support.v7.widget.SwitchCompat;

import com.spike.bot.model.SensorResModel;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public interface OnNotificationSensorContextMenu {

    void onEditOpetion(SensorResModel.DATA.TempList.TempNotificationList notification, int position, boolean isEdit);
    void onSwitchChanged(SensorResModel.DATA.TempList.TempNotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive);
}
