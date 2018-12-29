package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 8/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RemoteAddScheduleReq {

    @SerializedName("remote_id")
    private String remoteId;
    @SerializedName("ir_schedule_name")
    private String irScheduleName;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("start_temperature")
    private String startTemperature;
    @SerializedName("end_temperature")
    private String endTemperature;
    @SerializedName("start_mode")
    private String startMode;
    @SerializedName("end_mode")
    private String endMode;
    @SerializedName("schedule_day")
    private String scheduleDay;
    @SerializedName("phone_id")
    private String phoneId;
    @SerializedName("phone_type")
    private String phoneType;

    private String schedule_id;

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public RemoteAddScheduleReq(String remoteId, String irScheduleName, String startTime, String endTime, String startTemperature, String endTemperature, String startMode, String endMode, String scheduleDay, String phoneId, String phoneType) {
        this.remoteId = remoteId;
        this.irScheduleName = irScheduleName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTemperature = startTemperature;
        this.endTemperature = endTemperature;
        this.startMode = startMode;
        this.endMode = endMode;
        this.scheduleDay = scheduleDay;
        this.phoneId = phoneId;
        this.phoneType = phoneType;
    }

    public RemoteAddScheduleReq() {

    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getIrScheduleName() {
        return irScheduleName;
    }

    public void setIrScheduleName(String irScheduleName) {
        this.irScheduleName = irScheduleName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTemperature() {
        return startTemperature;
    }

    public void setStartTemperature(String startTemperature) {
        this.startTemperature = startTemperature;
    }

    public String getEndTemperature() {
        return endTemperature;
    }

    public void setEndTemperature(String endTemperature) {
        this.endTemperature = endTemperature;
    }

    public String getStartMode() {
        return startMode;
    }

    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    public String getEndMode() {
        return endMode;
    }

    public void setEndMode(String endMode) {
        this.endMode = endMode;
    }

    public String getScheduleDay() {
        return scheduleDay;
    }

    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
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
