package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 19/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class PiDetailsModel implements Serializable {


    public String getWifi_user_name() {
        return wifi_user_name;
    }

    public void setWifi_user_name(String wifi_user_name) {
        this.wifi_user_name = wifi_user_name;
    }

    public String getWifi_user_password() {
        return wifi_user_password;
    }

    public void setWifi_user_password(String wifi_user_password) {
        this.wifi_user_password = wifi_user_password;
    }

    public String getHome_controller_device_id() {
        return home_controller_device_id;
    }

    public void setHome_controller_device_id(String home_controller_device_id) {
        this.home_controller_device_id = home_controller_device_id;
    }

    String wifi_user_name,wifi_user_password,home_controller_device_id;

}
