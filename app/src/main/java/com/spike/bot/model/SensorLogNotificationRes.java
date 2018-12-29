package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 16/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorLogNotificationRes {

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("notificationList")
        private List<NotificationList> notificationList = null;

        public List<NotificationList> getNotificationList() {
            return notificationList;
        }

        public void setNotificationList(List<NotificationList> notificationList) {
            this.notificationList = notificationList;
        }

        public static class NotificationList {

            @SerializedName("id")
            private Integer id;
            @SerializedName("activity_action")
            private String activityAction;
            @SerializedName("activity_type")
            private String activityType;
            @SerializedName("activity_description")
            private String activityDescription;
            @SerializedName("activity_time")
            private String activityTime;
            @SerializedName("is_unread")
            private Integer isUnread;

            public NotificationList(){

            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getActivityAction() {
                return activityAction;
            }

            public void setActivityAction(String activityAction) {
                this.activityAction = activityAction;
            }

            public String getActivityType() {
                return activityType;
            }

            public void setActivityType(String activityType) {
                this.activityType = activityType;
            }

            public String getActivityDescription() {
                return activityDescription;
            }

            public void setActivityDescription(String activityDescription) {
                this.activityDescription = activityDescription;
            }

            public String getActivityTime() {
                return activityTime;
            }

            public void setActivityTime(String activityTime) {
                this.activityTime = activityTime;
            }

            public Integer getIsUnread() {
                return isUnread;
            }

            public void setIsUnread(Integer isUnread) {
                this.isUnread = isUnread;
            }

        }

    }


}
