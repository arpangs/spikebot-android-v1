package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 1/6/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class NotificationSettingReq {
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

    public static class Data {

        @SerializedName("notificationList")
        @Expose
        private List<NotificationList> notificationList = null;

        public List<NotificationList> getNotificationList() {
            return notificationList;
        }

        public void setNotificationList(List<NotificationList> notificationList) {
            this.notificationList = notificationList;
        }

        public static class NotificationList {

            @SerializedName("is_homecontroller_enable")
            @Expose
            private Integer isHomecontrollerEnable;
            @SerializedName("is_doorsensor_enable")
            @Expose
            private Integer isDoorsensorEnable;
            @SerializedName("is_tempsensor_enable")
            @Expose
            private Integer isTempsensorEnable;

            public Integer getIsHomecontrollerEnable() {
                return isHomecontrollerEnable;
            }

            public void setIsHomecontrollerEnable(Integer isHomecontrollerEnable) {
                this.isHomecontrollerEnable = isHomecontrollerEnable;
            }

            public Integer getIsDoorsensorEnable() {
                return isDoorsensorEnable;
            }

            public void setIsDoorsensorEnable(Integer isDoorsensorEnable) {
                this.isDoorsensorEnable = isDoorsensorEnable;
            }

            public Integer getIsTempsensorEnable() {
                return isTempsensorEnable;
            }

            public void setIsTempsensorEnable(Integer isTempsensorEnable) {
                this.isTempsensorEnable = isTempsensorEnable;
            }

        }

    }
}
