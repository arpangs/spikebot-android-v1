package com.spike.bot.model;

/**
 * Created by Sagar on 19/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DeviceLog {

    public String activity_action;
    public String activity_type;
    public String activity_description;
    public String activity_time;
    public String activity_state;

    public DeviceLog(){
        activity_action = "";
        activity_type = "";
        activity_description = "";
        activity_time = "";
        activity_state = "";
    }

    public DeviceLog(String activity_action, String activity_type, String activity_description, String activity_time, String activity_state) {
        this.activity_action = activity_action;
        this.activity_type = activity_type;
        this.activity_description = activity_description;
        this.activity_time = activity_time;
        this.activity_state = activity_state;
    }

    public String getActivity_action() {
        return activity_action;
    }

    public void setActivity_action(String activity_action) {
        this.activity_action = activity_action;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getActivity_description() {
        return activity_description;
    }

    public void setActivity_description(String activity_description) {
        this.activity_description = activity_description;
    }

    public String getActivity_time() {
        return activity_time;
    }

    public void setActivity_time(String activity_time) {
        this.activity_time = activity_time;
    }

    public String getActivity_state() {
        return activity_state;
    }

    public void setActivity_state(String activity_state) {
        this.activity_state = activity_state;
    }
}
