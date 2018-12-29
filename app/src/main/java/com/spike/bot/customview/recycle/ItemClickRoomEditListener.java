package com.spike.bot.customview.recycle;

import android.view.View;

import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

/**
 * Created by lenovo on 2/23/2016.
 */
public interface ItemClickRoomEditListener {
    void itemClicked(RoomVO item, String action, View view);

    void itemClicked(PanelVO panelVO, String action, View view);

    void itemClicked(DeviceVO section, String action, View view);
}
