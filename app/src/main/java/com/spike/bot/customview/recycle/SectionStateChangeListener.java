package com.spike.bot.customview.recycle;

/**
 * Created by bpncool on 2/24/2016.
 */

import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

/**
 * interface to listen changes in state of sections
 */
public interface SectionStateChangeListener {
    void onSectionStateChanged(RoomVO section, boolean isOpen);
    void onSectionStateChanged(PanelVO section, boolean isOpen);
}