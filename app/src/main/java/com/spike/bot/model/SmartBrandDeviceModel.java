package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 6/8/19.
 * Gmail : vipul patel
 */
public class SmartBrandDeviceModel implements Serializable {

    String id;
    String on;
    String bri;
    String hue;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getBri() {
        return bri;
    }

    public void setBri(String bri) {
        this.bri = bri;
    }

    public String getHue() {
        return hue;
    }

    public void setHue(String hue) {
        this.hue = hue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    String uniqueid;
}
