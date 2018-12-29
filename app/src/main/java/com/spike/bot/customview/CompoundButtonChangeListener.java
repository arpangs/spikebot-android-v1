package com.spike.bot.customview;

import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.spike.bot.model.SensorResModel;

/**
 * Created by Sagar on 23/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public interface CompoundButtonChangeListener extends CompoundButton.OnCheckedChangeListener {
    @Override
    void onCheckedChanged(CompoundButton buttonView, boolean isChecked);

    /**
     * custome SwitchCompact change listener for get custome value in adapter
     * @param buttonView
     * @param isChecked
     * @param switchCompat
     * @param notification
     * @param posiion
     */
    void onCompoundChanged(CompoundButton buttonView, boolean isChecked, SwitchCompat switchCompat, SensorResModel.DATA.TempList.NotificationList notification,int posiion);
}
