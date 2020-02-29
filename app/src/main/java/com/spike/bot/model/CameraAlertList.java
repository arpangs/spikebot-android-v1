package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CameraAlertList implements Serializable {

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("camera_ids")
    @Expose
    private String cameraIds;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("camera_notification_id")
    @Expose
    private String cameraNotificationId;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_date")

    @Expose
    private String createdDate;
    @SerializedName("modified_by")
    @Expose
    private String modifiedBy;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;

    public String getAlert_interval() {
        return alert_interval;
    }

    public void setAlert_interval(String alert_interval) {
        this.alert_interval = alert_interval;
    }

    @SerializedName("alert_interval")
    @Expose
    private String alert_interval;

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    @SerializedName("isOpen")
    @Expose
    private boolean isOpen = false;

    @SerializedName("activity_action")
    @Expose
    private String activity_action;

    @SerializedName("log_type")
    @Expose
    private String log_type;

    @SerializedName("user_name")
    @Expose
    private String user_name;

    @SerializedName("activity_description")
    @Expose
    private String activity_description;

    @SerializedName("activity_type")
    @Expose
    private String activity_type;

    @SerializedName("activity_time")
    @Expose
    private String activity_time;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("message")
    @Expose
    private String message;



    public ArrayList<CameraViewModel> getCameraViewModels() {
        return cameraViewModels;
    }

    public void setCameraViewModels(ArrayList<CameraViewModel> cameraViewModels) {
        this.cameraViewModels = cameraViewModels;
    }

    public ArrayList<CameraViewModel> cameraViewModels;

    public String getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCameraNotificationId() {
        return cameraNotificationId;
    }

    public void setCameraNotificationId(String cameraNotificationId) {
        this.cameraNotificationId = cameraNotificationId;
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getActivity_action() {
        return activity_action;
    }

    public void setActivity_action(String activity_action) {
        this.activity_action = activity_action;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getActivity_description() {
        return activity_description;
    }

    public void setActivity_description(String activity_description) {
        this.activity_description = activity_description;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getActivity_time() {
        return activity_time;
    }

    public void setActivity_time(String activity_time) {
        this.activity_time = activity_time;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}