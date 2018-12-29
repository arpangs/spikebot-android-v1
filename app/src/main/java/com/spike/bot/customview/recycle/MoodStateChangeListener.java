package com.spike.bot.customview.recycle;

/**
 * Created by bpncool on 2/24/2016.
 */

import com.spike.bot.model.RoomVO;

/**
 * interface to listen changes in state of sections
 */
public interface MoodStateChangeListener {
    void onSectionStateChanged(RoomVO moodVO, boolean isOpen);
}