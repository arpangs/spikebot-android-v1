package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vipul on 13/1/20.
 * Gmail : vipul patel
 */
public class JetSonModel implements Serializable {

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

    public class Datum {

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

    }
}
