package com.spike.bot.adapter;

import com.spike.bot.model.ScheduleVO;

/**
 * Created by kaushal on 10/1/18.
 */

public interface ScheduleClickListener {
    void itemClicked(ScheduleVO scheduleVO, String action);
    void itemClicked(ScheduleVO scheduleVO, String action,boolean isMood);
}
