package com.spike.bot.customview.recycle;

import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

/**
 * Created by lenovo on 2/23/2016.
 */
public interface ItemClickMoodListener {
    void itemClicked(RoomVO item, String action);
    void itemClicked(PanelVO panelVO, String action);
    void itemClicked(DeviceVO deviceVO, String action);
}
