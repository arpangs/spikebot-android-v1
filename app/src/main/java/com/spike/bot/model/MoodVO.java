package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaushal on 22/12/17.
 */

public class MoodVO implements Serializable{
    private int id;
    private String mood_id;
    private String mood_name;
    private String mood_icon;
    private String mood_device_id;
    private String room_device_id;
    private int mood_status;
    public boolean isExpanded;
    private int is_schedule;

    private ArrayList<PanelVO> panelList = new ArrayList<PanelVO>();

    public MoodVO(){
        id = 0;
        mood_id="";
        mood_name="";
        mood_icon="";
        mood_device_id="";
        room_device_id="";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMood_id() {
        return mood_id;
    }

    public void setMood_id(String mood_id) {
        this.mood_id = mood_id;
    }

    public String getMood_name() {
        return mood_name;
    }

    public void setMood_name(String mood_name) {
        this.mood_name = mood_name;
    }

    public String getMood_icon() {
        return mood_icon;
    }

    public void setMood_icon(String mood_icon) {
        this.mood_icon = mood_icon;
    }

    public int getMood_status() {
        return mood_status;
    }

    public void setMood_status(int mood_status) {
        this.mood_status = mood_status;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getMood_device_id() {
        return mood_device_id;
    }

    public void setMood_device_id(String mood_device_id) {
        this.mood_device_id = mood_device_id;
    }

    public String getRoom_device_id() {
        return room_device_id;
    }

    public void setRoom_device_id(String room_device_id) {
        this.room_device_id = room_device_id;
    }

    public ArrayList<PanelVO> getPanelList() {
        return panelList;
    }

    public void setPanelList(ArrayList<PanelVO> panelList) {
        this.panelList = panelList;
    }

    public int getIs_schedule() {
        return is_schedule;
    }

    public void setIs_schedule(int is_schedule) {
        this.is_schedule = is_schedule;
    }

    @Override
    public String toString() {
        return mood_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MoodVO)) {
            return false;
        }
        final MoodVO other = (MoodVO) obj;
        if (this.mood_id.equalsIgnoreCase(other.mood_id )) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.mood_id != null ? this.mood_id.hashCode() : 0);
        return hash;
    }

}
