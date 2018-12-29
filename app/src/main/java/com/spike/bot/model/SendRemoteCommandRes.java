package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 6/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class SendRemoteCommandRes {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("remote_currentStatus_details")
        @Expose
        private RemoteCurrentStatusDetails remoteCurrentStatusDetails;

        public RemoteCurrentStatusDetails getRemoteCurrentStatusDetails() {
            return remoteCurrentStatusDetails;
        }

        public void setRemoteCurrentStatusDetails(RemoteCurrentStatusDetails remoteCurrentStatusDetails) {
            this.remoteCurrentStatusDetails = remoteCurrentStatusDetails;
        }

        public class RemoteCurrentStatusDetails {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("remote_id")
            @Expose
            private String remoteId;
            @SerializedName("power")
            @Expose
            private String power;
            @SerializedName("mode")
            @Expose
            private String speed;
            @SerializedName("temperature")
            @Expose
            private Integer temperature;
            @SerializedName("direction")
            @Expose
            private Integer direction;
            @SerializedName("swing")
            @Expose
            private Integer swing;
            @SerializedName("device_id")
            @Expose
            private Integer deviceId;
            @SerializedName("device_type")
            @Expose
            private String deviceType;
            @SerializedName("brand_id")
            @Expose
            private Integer brandId;
            @SerializedName("brand_type")
            @Expose
            private String brandType;
            @SerializedName("codeset_id")
            @Expose
            private Integer codesetId;
            @SerializedName("remote_name")
            @Expose
            private String remoteName;
            @SerializedName("ir_blaster_id")
            @Expose
            private String irBlasterId;
            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("is_active")
            @Expose
            private Integer isActive;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getRemoteId() {
                return remoteId;
            }

            public void setRemoteId(String remoteId) {
                this.remoteId = remoteId;
            }

            public String getPower() {
                return power;
            }

            public void setPower(String power) {
                this.power = power;
            }

            public String getSpeed() {
                return speed;
            }

            public void setSpeed(String speed) {
                this.speed = speed;
            }

            public Integer getTemperature() {
                return temperature;
            }

            public void setTemperature(Integer temperature) {
                this.temperature = temperature;
            }

            public Integer getDirection() {
                return direction;
            }

            public void setDirection(Integer direction) {
                this.direction = direction;
            }

            public Integer getSwing() {
                return swing;
            }

            public void setSwing(Integer swing) {
                this.swing = swing;
            }

            public Integer getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(Integer deviceId) {
                this.deviceId = deviceId;
            }

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }

            public Integer getBrandId() {
                return brandId;
            }

            public void setBrandId(Integer brandId) {
                this.brandId = brandId;
            }

            public String getBrandType() {
                return brandType;
            }

            public void setBrandType(String brandType) {
                this.brandType = brandType;
            }

            public Integer getCodesetId() {
                return codesetId;
            }

            public void setCodesetId(Integer codesetId) {
                this.codesetId = codesetId;
            }

            public String getRemoteName() {
                return remoteName;
            }

            public void setRemoteName(String remoteName) {
                this.remoteName = remoteName;
            }

            public String getIrBlasterId() {
                return irBlasterId;
            }

            public void setIrBlasterId(String irBlasterId) {
                this.irBlasterId = irBlasterId;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            public Integer getIsActive() {
                return isActive;
            }

            public void setIsActive(Integer isActive) {
                this.isActive = isActive;
            }

        }

    }
}
