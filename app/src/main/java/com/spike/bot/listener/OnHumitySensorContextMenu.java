package com.spike.bot.listener;

import android.support.v7.widget.SwitchCompat;

import com.spike.bot.model.SensorResModel;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public interface OnHumitySensorContextMenu {
    void onEditHumityOpetion(SensorResModel.DATA.TempList.HumidityNotificationList notification, int position, boolean isEdit);
    void onSwitchHumityChanged(SensorResModel.DATA.TempList.HumidityNotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive);

}
