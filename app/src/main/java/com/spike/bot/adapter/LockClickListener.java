package com.spike.bot.adapter;

import com.spike.bot.model.ScheduleVO;
import com.spike.bot.model.YaleLockVO;

public interface LockClickListener {
    void itemClicked(YaleLockVO.Data yalelockVO, String action);
}
