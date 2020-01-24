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

        @SerializedName("jetson_id")
        @Expose
        private String jetsonId;
        @SerializedName("jetson_name")
        @Expose
        private String jetsonName;
        @SerializedName("jetson_ip")
        @Expose
        private String jetsonIp;

        public String getJetsonId() {
            return jetsonId;
        }

        public void setJetsonId(String jetsonId) {
            this.jetsonId = jetsonId;
        }

        public String getJetsonName() {
            return jetsonName;
        }

        public void setJetsonName(String jetsonName) {
            this.jetsonName = jetsonName;
        }

        public String getJetsonIp() {
            return jetsonIp;
        }

        public void setJetsonIp(String jetsonIp) {
            this.jetsonIp = jetsonIp;
        }


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




    }
}
