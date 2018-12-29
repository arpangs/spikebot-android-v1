package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kaushal on 22/12/17.
 */

public class DevicePanelVO implements Serializable,Cloneable {
    private int mood_id;
    private String roomDeviceId;
    private int userId;
    private String homeControllerDeviceId;
    private String roomId;
    private String roomName;
    private int deviceId;
    private String deviceName;
    private String deviceImage;
    private String device_icon;
    private String moduleId;
    private int moduleOrder;
    private String deviceStatus;
    private String deviceSpecificValue;
    private String deviceOnOffTime;
    private String deviceOnTime;
    private String deviceOffTime;
    private String deviceTimeDifference;
    private String deviceSync;
    private String deviceType;
    private int isActive;
    private int isAlive;
    private String isLocked;
    private int shareId;
    private String createdBy;
    private String createdDate;
    private String modifiedBy;
    private String modifiedDate;
    private int auto_on_off_value;
    private int schedule_value;
    private String panel_name;
    private boolean selected;
    private String panel_id;

    private int is_original;
    private String original_room_device_id;

    public String getRoom_panel_id() {
        return room_panel_id;
    }

    public void setRoom_panel_id(String room_panel_id) {
        this.room_panel_id = room_panel_id;
    }

    private String room_panel_id;

    public String getOriginal_room_device_id() {
        return original_room_device_id;
    }

    public void setOriginal_room_device_id(String original_room_device_id) {
        this.original_room_device_id = original_room_device_id;
    }

    public int getIs_original() {
        return is_original;
    }

    public void setIs_original(int is_original) {
        this.is_original = is_original;
    }

    public String getPanel_id() {
        return panel_id;
    }

    public void setPanel_id(String panel_id) {
        this.panel_id = panel_id;
    }

    public int getMood_id() {
        return mood_id;
    }

    public void setMood_id(int mood_id) {
        this.mood_id = mood_id;
    }

    public DevicePanelVO(){
        deviceName = "";
        deviceType = "";
        deviceSpecificValue = "";
        deviceStatus = "";
        roomDeviceId = "";
    }
    public DevicePanelVO(String deviceName){
        this.deviceName = deviceName;
    }
    public String getRoomDeviceId() {
        return roomDeviceId;
    }

    public void setRoomDeviceId(String roomDeviceId) {
        this.roomDeviceId = roomDeviceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHomeControllerDeviceId() {
        return homeControllerDeviceId;
    }

    public void setHomeControllerDeviceId(String homeControllerDeviceId) {
        this.homeControllerDeviceId = homeControllerDeviceId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPanel_name() {
        return panel_name;
    }

    public void setPanel_name(String panel_name) {
        this.panel_name = panel_name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(String deviceImage) {
        this.deviceImage = deviceImage;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(int moduleOrder) {
        this.moduleOrder = moduleOrder;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceSpecificValue() {
        return deviceSpecificValue;
    }

    public void setDeviceSpecificValue(String deviceSpecificValue) {
        this.deviceSpecificValue = deviceSpecificValue;
    }

    public String getDeviceOnOffTime() {
        return deviceOnOffTime;
    }

    public void setDeviceOnOffTime(String deviceOnOffTime) {
        this.deviceOnOffTime = deviceOnOffTime;
    }

    public String getDeviceOnTime() {
        return deviceOnTime;
    }

    public void setDeviceOnTime(String deviceOnTime) {
        this.deviceOnTime = deviceOnTime;
    }

    public String getDeviceOffTime() {
        return deviceOffTime;
    }

    public void setDeviceOffTime(String deviceOffTime) {
        this.deviceOffTime = deviceOffTime;
    }

    public String getDeviceTimeDifference() {
        return deviceTimeDifference;
    }

    public void setDeviceTimeDifference(String deviceTimeDifference) {
        this.deviceTimeDifference = deviceTimeDifference;
    }

    public String getDeviceSync() {
        return deviceSync;
    }

    public void setDeviceSync(String deviceSync) {
        this.deviceSync = deviceSync;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
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

    public int getAuto_on_off_value() {
        return auto_on_off_value;
    }

    public void setAuto_on_off_value(int auto_on_off_value) {
        this.auto_on_off_value = auto_on_off_value;
    }

    public int getSchedule_value() {
        return schedule_value;
    }

    public void setSchedule_value(int schedule_value) {
        this.schedule_value = schedule_value;
    }

    public String getDevice_icon() {
        return device_icon;
    }

    public void setDevice_icon(String device_icon) {
        this.device_icon = device_icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DevicePanelVO)) {
            return false;
        }
        final DevicePanelVO other = (DevicePanelVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.deviceId == other.deviceId && this.moduleId.equals(other.moduleId) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.moduleId != null ? this.moduleId.hashCode() : 0);
        hash = 53 * hash + this.deviceId;
        return hash;
    }
    @Override public DevicePanelVO clone() {
        try {
            final DevicePanelVO result = (DevicePanelVO) super.clone();
            // copy fields that need to be copied here!
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}