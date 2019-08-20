package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 31/7/19.
 * Gmail : vipul patel
 */
public class SmartBrandModel implements Serializable {

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmart_device_brand_name() {
        return smart_device_brand_name;
    }

    public void setSmart_device_brand_name(String smart_device_brand_name) {
        this.smart_device_brand_name = smart_device_brand_name;
    }

    String smart_device_brand_name;

    public String getIcon_image() {
        return icon_image;
    }

    public void setIcon_image(String icon_image) {
        this.icon_image = icon_image;
    }

    String icon_image;

    public String getHost_ip() {
        return host_ip;
    }

    public void setHost_ip(String host_ip) {
        this.host_ip = host_ip;
    }

    public String getBridge_id() {
        return bridge_id;
    }

    public void setBridge_id(String bridge_id) {
        this.bridge_id = bridge_id;
    }

    public String getBridge_type() {
        return bridge_type;
    }

    public void setBridge_type(String bridge_type) {
        this.bridge_type = bridge_type;
    }

    public String getBridge_name() {
        return bridge_name;
    }

    public void setBridge_name(String bridge_name) {
        this.bridge_name = bridge_name;
    }

    String host_ip;
    String bridge_id;
    String bridge_type;
    String bridge_name;

    //"id": 1,
    //        "host_ip": "192.168.175.74",
    //        "bridge_id": "TTfzwGKNNCTzoyqnIe99x0tYfAGIBwRoGW-7uQrd",
    //        "bridge_type": "Philips Hue",
    //        "bridge_name": "test"
    //      }
}
