package com.spike.bot.listener;

import com.spike.bot.adapter.MoodRemoteAdapter;
import com.spike.bot.model.RemoteMoodModel;

/**
 * Created by Sagar on 16/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public interface MoodEvent {

    public void selectMood(RemoteMoodModel remoteMoodModel);
}
