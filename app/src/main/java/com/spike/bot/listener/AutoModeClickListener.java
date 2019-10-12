package com.spike.bot.listener;

import com.spike.bot.model.AutoModeVO;

public interface AutoModeClickListener {
    void itemClicked(AutoModeVO autoModeVO,String action);
}
