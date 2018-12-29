package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CameraAlertList implements Serializable{

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
    private boolean isOpen=false;


    public ArrayList<CameraViewModel> getCameraViewModels() {
        return cameraViewModels;
    }

    public void setCameraViewModels( ArrayList<CameraViewModel> cameraViewModels) {
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

}