package com.spike.bot.listener;

import com.spike.bot.model.DeviceVO;

/**
 * Created by Sagar on 8/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public interface TempClickListener {
    void itemClicked(DeviceVO section, String action , boolean isTemp,int position);
}
