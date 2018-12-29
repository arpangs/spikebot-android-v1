package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 6/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class SendRemoteCommandReq {
    @SerializedName("remote_id")
    private String remoteid;
    @SerializedName("power")
    private String power;
    @SerializedName("mode")
    private String speed;
    @SerializedName("temperature")
    private int temperature;
    @SerializedName("room_device_id")
    private String roomDeviceId;
    @SerializedName("phone_id")
    private String phoneId;
    @SerializedName("phone_type")
    private String phoneType;

    public SendRemoteCommandReq(){}

    public SendRemoteCommandReq(String remoteid, String power, String speed, int temperature, String roomDeviceId, String phoneId, String phoneType) {
        this.remoteid = remoteid;
        this.power = power;
        this.speed = speed;
        this.temperature = temperature;
        this.roomDeviceId = roomDeviceId;
        this.phoneId = phoneId;
        this.phoneType = phoneType;
    }

    public String getRoomDeviceId() {
        return roomDeviceId;
    }

    public void setRoomDeviceId(String roomDeviceId) {
        this.roomDeviceId = roomDeviceId;
    }

    public String getRemoteid() {
        return remoteid;
    }

    public void setRemoteid(String remoteid) {
        this.remoteid = remoteid;
    }



    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
}
