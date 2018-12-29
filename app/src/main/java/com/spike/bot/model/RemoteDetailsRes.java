package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 18/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RemoteDetailsRes {


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


    public class Data {

        @SerializedName("remote_command_list")
        @Expose
        private List<RemoteCommandList> remoteCommandList = null;
        @SerializedName("remote_currentStatus_details")
        @Expose
        private RemoteCurrentStatusDetails remoteCurrentStatusDetails;

        public List<RemoteCommandList> getRemoteCommandList() {
            return remoteCommandList;
        }

        public void setRemoteCommandList(List<RemoteCommandList> remoteCommandList) {
            this.remoteCommandList = remoteCommandList;
        }

        public RemoteCurrentStatusDetails getRemoteCurrentStatusDetails() {
            return remoteCurrentStatusDetails;
        }

        public void setRemoteCurrentStatusDetails(RemoteCurrentStatusDetails remoteCurrentStatusDetails) {
            this.remoteCurrentStatusDetails = remoteCurrentStatusDetails;
        }

        public class RemoteCommandList {

            @SerializedName("POWER")
            @Expose
            private List<String> pOWER = null;
            @SerializedName("MODE")
            @Expose
            private List<String> mode = null;
            @SerializedName("TEMPERATURE")
            @Expose
            private List<Integer> tEMPERATURE = null;

            public List<String> getPOWER() {
                return pOWER;
            }

            public void setPOWER(List<String> pOWER) {
                this.pOWER = pOWER;
            }

            public List<String> getMODE() {
                return mode;
            }

            public void setModeList(List<String> modeList) {
                this.mode = modeList;
            }

            public List<Integer> getTEMPERATURE() {
                return tEMPERATURE;
            }

            public void setTEMPERATURE(List<Integer> tEMPERATURE) {
                this.tEMPERATURE = tEMPERATURE;
            }

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
            private String mode;
            @SerializedName("temperature")
            @Expose
            private Integer temperature;
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
            @SerializedName("schedule_mode")
            @Expose
            private String scheduleMode;
            @SerializedName("schedule_temperature")
            @Expose
            private Integer scheduleTemperature;
            @SerializedName("room_name")
            @Expose
            private String roomName;
            @SerializedName("ir_blaster_name")
            @Expose
            private Object irBlasterName;

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

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public Integer getTemperature() {
                return temperature;
            }

            public void setTemperature(Integer temperature) {
                this.temperature = temperature;
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

            public String getScheduleMode() {
                return scheduleMode;
            }

            public void setScheduleMode(String scheduleMode) {
                this.scheduleMode = scheduleMode;
            }

            public Integer getScheduleTemperature() {
                return scheduleTemperature;
            }

            public void setScheduleTemperature(Integer scheduleTemperature) {
                this.scheduleTemperature = scheduleTemperature;
            }

            public String getRoomName() {
                return roomName;
            }

            public void setRoomName(String roomName) {
                this.roomName = roomName;
            }

            public Object getIrBlasterName() {
                return irBlasterName;
            }

            public void setIrBlasterName(Object irBlasterName) {
                this.irBlasterName = irBlasterName;
            }

        }
    }

}
