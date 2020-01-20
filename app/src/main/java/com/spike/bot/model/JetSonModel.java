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



        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("camera_id")
        @Expose
        private String cameraId;
        @SerializedName("camera_name")
        @Expose
        private String cameraName;
        @SerializedName("camera_ip")
        @Expose
        private String cameraIp;
        @SerializedName("camera_videopath")
        @Expose
        private String cameraVideopath;
        @SerializedName("camera_icon")
        @Expose
        private String cameraIcon;
        @SerializedName("camera_vpn_port")
        @Expose
        private String cameraVpnPort;
        @SerializedName("camera_url")
        @Expose
        private String cameraUrl;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("is_sync")
        @Expose
        private Integer isSync;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("home_controller_device_id")
        @Expose
        private String homeControllerDeviceId;
        @SerializedName("created_by")
        @Expose
        private String createdBy;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("modified_by")
        @Expose
        private Object modifiedBy;
        @SerializedName("modified_date")
        @Expose
        private Object modifiedDate;
        @SerializedName("jetson_device_id")
        @Expose
        private String jetsonDeviceId;



        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public String getCameraName() {
            return cameraName;
        }

        public void setCameraName(String cameraName) {
            this.cameraName = cameraName;
        }

        public String getCameraIp() {
            return cameraIp;
        }

        public void setCameraIp(String cameraIp) {
            this.cameraIp = cameraIp;
        }

        public String getCameraVideopath() {
            return cameraVideopath;
        }

        public void setCameraVideopath(String cameraVideopath) {
            this.cameraVideopath = cameraVideopath;
        }

        public String getCameraIcon() {
            return cameraIcon;
        }

        public void setCameraIcon(String cameraIcon) {
            this.cameraIcon = cameraIcon;
        }

        public String getCameraVpnPort() {
            return cameraVpnPort;
        }

        public void setCameraVpnPort(String cameraVpnPort) {
            this.cameraVpnPort = cameraVpnPort;
        }

        public String getCameraUrl() {
            return cameraUrl;
        }

        public void setCameraUrl(String cameraUrl) {
            this.cameraUrl = cameraUrl;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }


        public Integer getIsSync() {
            return isSync;
        }

        public void setIsSync(Integer isSync) {
            this.isSync = isSync;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getHomeControllerDeviceId() {
            return homeControllerDeviceId;
        }

        public void setHomeControllerDeviceId(String homeControllerDeviceId) {
            this.homeControllerDeviceId = homeControllerDeviceId;
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

        public Object getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(Object modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public Object getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(Object modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public String getJetsonDeviceId() {
            return jetsonDeviceId;
        }

        public void setJetsonDeviceId(String jetsonDeviceId) {
            this.jetsonDeviceId = jetsonDeviceId;
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

    }
}
