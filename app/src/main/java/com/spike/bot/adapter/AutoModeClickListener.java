package com.spike.bot.adapter;

import com.spike.bot.model.AutoModeVO;

/**
 * Created by kaushal on 10/1/18.
 */

public interface AutoModeClickListener {
    void itemClicked(AutoModeVO autoModeVO, String action);
}
