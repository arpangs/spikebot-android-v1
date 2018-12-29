package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 16/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RemoteMoodModel implements Serializable {



    String moodName;

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    boolean isSelect;
}
