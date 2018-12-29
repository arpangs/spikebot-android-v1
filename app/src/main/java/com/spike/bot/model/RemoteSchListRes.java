package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 8/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RemoteSchListRes {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("remoteScheduleList")
        @Expose
        private List<RemoteScheduleList> remoteScheduleList = null;

        public List<RemoteScheduleList> getRemoteScheduleList() {
            return remoteScheduleList;
        }

        public void setRemoteScheduleList(List<RemoteScheduleList> remoteScheduleList) {
            this.remoteScheduleList = remoteScheduleList;
        }

        public class RemoteScheduleList {

            @SerializedName("schedule_id")
            @Expose
            private String scheduleId;
            @SerializedName("remote_id")
            @Expose
            private String remoteId;
            @SerializedName("ir_schedule_name")
            @Expose
            private String irScheduleName;
            @SerializedName("start_time")
            @Expose
            private String startTime;
            @SerializedName("end_time")
            @Expose
            private String endTime;
            @SerializedName("schedule_day")
            @Expose
            private String scheduleDay;
            @SerializedName("start_mode")
            @Expose
            private String startMode;
            @SerializedName("end_mode")
            @Expose
            private String endMode;
            @SerializedName("start_temperature")
            @Expose
            private Integer startTemperature;
            @SerializedName("end_temperature")
            @Expose
            private Integer endTemperature;
            @SerializedName("is_active")
            @Expose
            private Integer isActive;

            public String getScheduleId() {
                return scheduleId;
            }

            public void setScheduleId(String scheduleId) {
                this.scheduleId = scheduleId;
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

            public String getScheduleDay() {
                return scheduleDay;
            }

            public void setScheduleDay(String scheduleDay) {
                this.scheduleDay = scheduleDay;
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

            public Integer getStartTemperature() {
                return startTemperature;
            }

            public void setStartTemperature(Integer startTemperature) {
                this.startTemperature = startTemperature;
            }

            public Integer getEndTemperature() {
                return endTemperature;
            }

            public void setEndTemperature(Integer endTemperature) {
                this.endTemperature = endTemperature;
            }

            public Integer getIsActive() {
                return isActive;
            }

            public void setIsActive(Integer isActive) {
                this.isActive = isActive;
            }

        }

    }

}
