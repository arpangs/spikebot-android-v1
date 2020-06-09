package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class YaleLockVO implements Serializable {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("panel_device_id")
        @Expose
        private String panelDeviceId;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
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
        private Object deviceSubStatus;
        @SerializedName("meta_unit")
        @Expose
        private Object metaUnit;
        @SerializedName("meta_device_default_status")
        @Expose
        private Object metaDeviceDefaultStatus;
        @SerializedName("meta_device_brand")
        @Expose
        private Object metaDeviceBrand;
        @SerializedName("meta_device_model")
        @Expose
        private Object metaDeviceModel;
        @SerializedName("meta_device_codeset_id")
        @Expose
        private Object metaDeviceCodesetId;
        @SerializedName("meta_ir_blaster_id")
        @Expose
        private Object metaIrBlasterId;
        @SerializedName("meta_battery_level")
        @Expose
        private Object metaBatteryLevel;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_sub_type")
        @Expose
        private String deviceSubType;
        @SerializedName("device_identifier")
        @Expose
        private Integer deviceIdentifier;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("module_type")
        @Expose
        private String moduleType;

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

        public Object getDeviceSubStatus() {
            return deviceSubStatus;
        }

        public void setDeviceSubStatus(Object deviceSubStatus) {
            this.deviceSubStatus = deviceSubStatus;
        }

        public Object getMetaUnit() {
            return metaUnit;
        }

        public void setMetaUnit(Object metaUnit) {
            this.metaUnit = metaUnit;
        }

        public Object getMetaDeviceDefaultStatus() {
            return metaDeviceDefaultStatus;
        }

        public void setMetaDeviceDefaultStatus(Object metaDeviceDefaultStatus) {
            this.metaDeviceDefaultStatus = metaDeviceDefaultStatus;
        }

        public Object getMetaDeviceBrand() {
            return metaDeviceBrand;
        }

        public void setMetaDeviceBrand(Object metaDeviceBrand) {
            this.metaDeviceBrand = metaDeviceBrand;
        }

        public Object getMetaDeviceModel() {
            return metaDeviceModel;
        }

        public void setMetaDeviceModel(Object metaDeviceModel) {
            this.metaDeviceModel = metaDeviceModel;
        }

        public Object getMetaDeviceCodesetId() {
            return metaDeviceCodesetId;
        }

        public void setMetaDeviceCodesetId(Object metaDeviceCodesetId) {
            this.metaDeviceCodesetId = metaDeviceCodesetId;
        }

        public Object getMetaIrBlasterId() {
            return metaIrBlasterId;
        }

        public void setMetaIrBlasterId(Object metaIrBlasterId) {
            this.metaIrBlasterId = metaIrBlasterId;
        }

        public Object getMetaBatteryLevel() {
            return metaBatteryLevel;
        }

        public void setMetaBatteryLevel(Object metaBatteryLevel) {
            this.metaBatteryLevel = metaBatteryLevel;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceSubType() {
            return deviceSubType;
        }

        public void setDeviceSubType(String deviceSubType) {
            this.deviceSubType = deviceSubType;
        }

        public Integer getDeviceIdentifier() {
            return deviceIdentifier;
        }

        public void setDeviceIdentifier(Integer deviceIdentifier) {
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
    }
}
