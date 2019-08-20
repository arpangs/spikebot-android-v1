package com.spike.bot.model;

import java.io.Serializable;

public class SmartDeviceModel implements Serializable {

    public String getOnfff() {
        return onfff;
    }

    public void setOnfff(String onfff) {
        this.onfff = onfff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String onfff;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

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

    String host_ip;
    String bridge_id;
}
