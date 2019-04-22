package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by kaushal on 10/1/18.
 */

public class ScheduleVO implements Serializable{
    int id;
    String schedule_id;
    String schedule_name;
    int schedule_type;
    String room_id;
    String mood_id;
    String schedule_device_on_time;
    String schedule_device_off_time;
    String schedule_device_day;
    int schedule_status;
    int is_active;
    String room_device_id;
    String room_name;
    boolean updated;

    private int is_timer;
    private String timer_on_after;
    private String timer_on_date;
    private String timer_off_after;
    private String timer_off_date;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String user_id;

    /* "schedule_id": "1517387409895_Sk5qRgkIf",
             "schedule_name": "Test",
             "schedule_type": 1,
             "room_id": "1516177147438_rJmZwFnVz",
             "mood_id": "",
             "schedule_device_on_time": "11:00",
             "schedule_device_off_time": "15:00",
             "schedule_device_day": "1,2,3",
             "schedule_active": 1,
             "roomDeviceList": [
    {
        "room_device_id": "1516177148079_SJ4ZvFnEf, 1516177148096_BJgNWvYnVf, 1516177148098_HkZVbPYhVf"
    }
      ]*/


    public ScheduleVO(){
        schedule_id="";
        schedule_name="";
        schedule_device_day="";
        room_name = "";
        room_id ="";
        mood_id = "";
        room_device_id= "";
        schedule_status = 0;

        is_timer = 0;
        timer_on_after = "";
        timer_on_date = "";
        timer_off_after = "";
        timer_off_date = "";

        schedule_device_on_time ="";
        schedule_device_off_time = "";

    }

    public int getIs_timer() {
        return is_timer;
    }

    public void setIs_timer(int is_timer) {
        this.is_timer = is_timer;
    }

    public String getTimer_on_after() {
        return timer_on_after;
    }

    public void setTimer_on_after(String timer_on_after) {
        this.timer_on_after = timer_on_after;
    }

    public String getTimer_on_date() {
        return timer_on_date;
    }

    public void setTimer_on_date(String timer_on_date) {
        this.timer_on_date = timer_on_date;
    }

    public String getTimer_off_after() {
        return timer_off_after;
    }

    public void setTimer_off_after(String timer_off_after) {
        this.timer_off_after = timer_off_after;
    }

    public String getTimer_off_date() {
        return timer_off_date;
    }

    public void setTimer_off_date(String timer_off_date) {
        this.timer_off_date = timer_off_date;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getSchedule_name() {
        return schedule_name;
    }

    public void setSchedule_name(String schedule_name) {
        this.schedule_name = schedule_name;
    }

    public String getSchedule_device_day() {
        return schedule_device_day;
    }

    public void setSchedule_device_day(String schedule_device_day) {
        this.schedule_device_day = schedule_device_day;
    }

    public int getSchedule_type() {
        return schedule_type;
    }

    public void setSchedule_type(int schedule_type) {
        this.schedule_type = schedule_type;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getMood_id() {
        return mood_id;
    }

    public void setMood_id(String mood_id) {
        this.mood_id = mood_id;
    }

    public String getSchedule_device_on_time() {
        return schedule_device_on_time;
    }

    public void setSchedule_device_on_time(String schedule_device_on_time) {
        this.schedule_device_on_time = schedule_device_on_time;
    }

    public String getSchedule_device_off_time() {
        return schedule_device_off_time;
    }

    public void setSchedule_device_off_time(String schedule_device_off_time) {
        this.schedule_device_off_time = schedule_device_off_time;
    }

    public int getSchedule_status() {
        return schedule_status;
    }

    public void setSchedule_status(int schedule_status) {
        this.schedule_status = schedule_status;
    }

    public String getRoom_device_id() {
        return room_device_id;
    }

    public void setRoom_device_id(String room_device_id) {
        this.room_device_id = room_device_id;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ScheduleVO)) {
            return false;
        }
        final ScheduleVO other = (ScheduleVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.schedule_id.equals(other.schedule_id)
                && this.schedule_device_day.equals(other.schedule_device_day) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.schedule_id != null ? this.schedule_id.hashCode() : 0);
        //hash = 53 * hash + this.schedule_device_time.hashCode();
        hash = 53 * hash + this.schedule_device_day.hashCode();
        return hash;
    }

}
