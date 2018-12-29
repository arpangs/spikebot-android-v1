package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 5/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class UnassignedPanelRes {


    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName("panel_name")
    @Expose
    private String panelName;
    @SerializedName("is_module")
    @Expose
    private Integer isModule;
    @SerializedName("deviceList")
    @Expose
    private List<DeviceList> deviceList = null;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public Integer getIsModule() {
        return isModule;
    }

    public void setIsModule(Integer isModule) {
        this.isModule = isModule;
    }

    public List<DeviceList> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceList> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DeviceList {

        @SerializedName("module_id")
        @Expose
        private String moduleId;
        @SerializedName("device_id")
        @Expose
        private Integer deviceId;
        @SerializedName("device_icon")
        @Expose
        private String deviceIcon;
        @SerializedName("device_name")
        @Expose
        private String deviceName;
        @SerializedName("module_name")
        @Expose
        private String moduleName;
        @SerializedName("original_room_device_id")
        @Expose
        private String originalRoomDeviceId;
        @SerializedName("room_device_id")
        @Expose
        private String roomDeviceId;
        @SerializedName("panel_id")
        @Expose
        private String panelId;
        @SerializedName("device_status")
        @Expose
        private String deviceStatus;
        @SerializedName("device_specific_value")
        @Expose
        private String deviceSpecificValue;
        @SerializedName("device_type")
        @Expose
        private String deviceType;

        public String getRoom_panel_id() {
            return room_panel_id;
        }

        public void setRoom_panel_id(String room_panel_id) {
            this.room_panel_id = room_panel_id;
        }

        @SerializedName("room_panel_id")
        @Expose
        private String room_panel_id;

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public Integer getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Integer deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceIcon() {
            return deviceIcon;
        }

        public void setDeviceIcon(String deviceIcon) {
            this.deviceIcon = deviceIcon;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getOriginalRoomDeviceId() {
            return originalRoomDeviceId;
        }

        public void setOriginalRoomDeviceId(String originalRoomDeviceId) {
            this.originalRoomDeviceId = originalRoomDeviceId;
        }

        public String getRoomDeviceId() {
            return roomDeviceId;
        }

        public void setRoomDeviceId(String roomDeviceId) {
            this.roomDeviceId = roomDeviceId;
        }

        public String getPanelId() {
            return panelId;
        }

        public void setPanelId(String panelId) {
            this.panelId = panelId;
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

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

    }
}
