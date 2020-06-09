package com.spike.bot.model;

import java.io.Serializable;

public class DeviceList implements Serializable
{

    private String devicename;
    private int thumbnail;

    public DeviceList() {
    }

    public DeviceList(String name, int thumbnail) {
        this.devicename = name;
        this.thumbnail = thumbnail;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
