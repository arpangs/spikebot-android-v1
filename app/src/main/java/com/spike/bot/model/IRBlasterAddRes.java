package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddRes {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum implements Serializable {

        @SerializedName("panel_device_id")
        @Expose
        private String panelDeviceId;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("panel_device_meta")
        @Expose
        private String panelDeviceMeta;
        @SerializedName("module_id")
        @Expose
        private String moduleId;
        @SerializedName("device_name")
        @Expose
        private String deviceName;
        @SerializedName("device_icon")
        @Expose
        private String deviceIcon;
        @SerializedName("device_status")
        @Expose
        private String deviceStatus;
        @SerializedName("device_sub_status")
        @Expose
        private String deviceSubStatus;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_sub_type")
        @Expose
        private Object deviceSubType;
        @SerializedName("device_identifier")
        @Expose
        private String deviceIdentifier;
        @SerializedName("device_meta")
        @Expose
        private String deviceMeta;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("module_type")
        @Expose
        private String moduleType;
        @SerializedName("module_meta")
        @Expose
        private String moduleMeta;
        @SerializedName("room")
        @Expose
        private Room room;
        @SerializedName("remote_list")
        @Expose
        private List<RemoteList> remoteList = null;
        @SerializedName("related_devices")
        @Expose
        private List<String> relatedDevices = null;
        @SerializedName("range")
        @Expose
        private int range;
        @SerializedName("on_time")
        @Expose
        private String on_time;
        @SerializedName("off_time")
        @Expose
        private String off_time;
        @SerializedName("module_identifier")
        @Expose
        private String module_identifier;

        public String getModule_identifier() {
            return module_identifier;
        }

        public void setModule_identifier(String module_identifier) {
            this.module_identifier = module_identifier;
        }

        public String getPanelDeviceId() {
            return panelDeviceId;
        }

        public void setPanelDeviceId(String panelDeviceId) {
            this.panelDeviceId = panelDeviceId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPanelDeviceMeta() {
            return panelDeviceMeta;
        }

        public void setPanelDeviceMeta(String panelDeviceMeta) {
            this.panelDeviceMeta = panelDeviceMeta;
        }

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceIcon() {
            return deviceIcon;
        }

        public void setDeviceIcon(String deviceIcon) {
            this.deviceIcon = deviceIcon;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getDeviceSubStatus() {
            return deviceSubStatus;
        }

        public void setDeviceSubStatus(String deviceSubStatus) {
            this.deviceSubStatus = deviceSubStatus;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Object getDeviceSubType() {
            return deviceSubType;
        }

        public void setDeviceSubType(Object deviceSubType) {
            this.deviceSubType = deviceSubType;
        }

        public String getDeviceIdentifier() {
            return deviceIdentifier;
        }

        public void setDeviceIdentifier(String deviceIdentifier) {
            this.deviceIdentifier = deviceIdentifier;
        }

        public String getDeviceMeta() {
            return deviceMeta;
        }

        public void setDeviceMeta(String deviceMeta) {
            this.deviceMeta = deviceMeta;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getModuleType() {
            return moduleType;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        public String getModuleMeta() {
            return moduleMeta;
        }

        public void setModuleMeta(String moduleMeta) {
            this.moduleMeta = moduleMeta;
        }

        public Room getRoom() {
            return room;
        }

        public void setRoom(Room room) {
            this.room = room;
        }

        public List<RemoteList> getRemoteList() {
            return remoteList;
        }

        public void setRemoteList(List<RemoteList> remoteList) {
            this.remoteList = remoteList;
        }

        public List<String> getRelatedDevices() {
            return relatedDevices;
        }

        public void setRelatedDevices(List<String> relatedDevices) {
            this.relatedDevices = relatedDevices;
        }

        public int getRange() {
            return range;
        }

        public void setRange(int range) {
            this.range = range;
        }

        public String getOn_time() {
            return on_time;
        }

        public void setOn_time(String on_time) {
            this.on_time = on_time;
        }

        public String getOff_time() {
            return off_time;
        }

        public void setOff_time(String off_time) {
            this.off_time = off_time;
        }
    }

    public class RemoteList {

        @SerializedName("panel_device_id")
        @Expose
        private String panelDeviceId;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("panel_device_meta")
        @Expose
        private String panelDeviceMeta;
        @SerializedName("module_id")
        @Expose
        private String moduleId;
        @SerializedName("device_name")
        @Expose
        private String deviceName;
        @SerializedName("device_icon")
        @Expose
        private String deviceIcon;
        @SerializedName("device_status")
        @Expose
        private String deviceStatus;
        @SerializedName("device_sub_status")
        @Expose
        private String deviceSubStatus;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_sub_type")
        @Expose
        private Object deviceSubType;
        @SerializedName("device_identifier")
        @Expose
        private String deviceIdentifier;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("module_type")
        @Expose
        private String moduleType;
        @SerializedName("module_meta")
        @Expose
        private String moduleMeta;

        public String getPanelDeviceId() {
            return panelDeviceId;
        }

        public void setPanelDeviceId(String panelDeviceId) {
            this.panelDeviceId = panelDeviceId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPanelDeviceMeta() {
            return panelDeviceMeta;
        }

        public void setPanelDeviceMeta(String panelDeviceMeta) {
            this.panelDeviceMeta = panelDeviceMeta;
        }

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceIcon() {
            return deviceIcon;
        }

        public void setDeviceIcon(String deviceIcon) {
            this.deviceIcon = deviceIcon;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getDeviceSubStatus() {
            return deviceSubStatus;
        }

        public void setDeviceSubStatus(String deviceSubStatus) {
            this.deviceSubStatus = deviceSubStatus;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Object getDeviceSubType() {
            return deviceSubType;
        }

        public void setDeviceSubType(Object deviceSubType) {
            this.deviceSubType = deviceSubType;
        }

        public String getDeviceIdentifier() {
            return deviceIdentifier;
        }

        public void setDeviceIdentifier(String deviceIdentifier) {
            this.deviceIdentifier = deviceIdentifier;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getModuleType() {
            return moduleType;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        public String getModuleMeta() {
            return moduleMeta;
        }

        public void setModuleMeta(String moduleMeta) {
            this.moduleMeta = moduleMeta;
        }

    }

    public class Room {

        @SerializedName("room_id")
        @Expose
        private String roomId;
        @SerializedName("room_name")
        @Expose
        private String roomName;
        @SerializedName("room_type")
        @Expose
        private String roomType;
        @SerializedName("room_users")
        @Expose
        private String roomUsers;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("mood_name_id")
        @Expose
        private Integer moodNameId;
        @SerializedName("meta")
        @Expose
        private String meta;

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

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public String getRoomUsers() {
            return roomUsers;
        }

        public void setRoomUsers(String roomUsers) {
            this.roomUsers = roomUsers;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Integer getMoodNameId() {
            return moodNameId;
        }

        public void setMoodNameId(Integer moodNameId) {
            this.moodNameId = moodNameId;
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

    }
}
