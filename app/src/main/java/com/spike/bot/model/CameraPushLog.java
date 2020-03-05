package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CameraPushLog implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
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
    @SerializedName("is_unread")
    @Expose
    private Integer isUnread;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("message")
    @Expose
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}