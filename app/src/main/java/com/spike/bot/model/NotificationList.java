package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationList implements Serializable{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("image_url")
    @Expose
    private Object imageUrl;
    @SerializedName("log_type")
    @Expose
    private String logType;
    @SerializedName("activity_action")
    @Expose
    private String activityAction;
    @SerializedName("activity_type")
    @Expose
    private String activityType;
    @SerializedName("activity_description")
    @Expose
    private String activityDescription;
    @SerializedName("activity_time")
    @Expose
    private String activityTime;
    @SerializedName("user_name")
    @Expose
    private String userName;

    public String getSeen_by() {
        return seen_by;
    }

    public void setSeen_by(String seen_by) {
        this.seen_by = seen_by;
    }

    @SerializedName("seen_by")
    @Expose
    private String seen_by;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}