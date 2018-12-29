package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 31/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterInfoRes implements Serializable{

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

    public class Data implements Serializable{

        @SerializedName("irBlasterList")
        @Expose
        private List<IrBlasterList> irBlasterList = null;

        public List<IrBlasterList> getIrBlasterList() {
            return irBlasterList;
        }

        public void setIrBlasterList(List<IrBlasterList> irBlasterList) {
            this.irBlasterList = irBlasterList;
        }

        public class IrBlasterList implements Serializable{

            @SerializedName("ir_blaster_id")
            @Expose
            private String irBlasterId;
            @SerializedName("ir_blaster_module_id")
            @Expose
            private String irBlasterModuleId;
            @SerializedName("ir_blaster_name")
            @Expose
            private String irBlasterName;
            @SerializedName("ir_blaster_icon")
            @Expose
            private String irBlasterIcon;
            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("is_active")
            @Expose
            private Integer isActive;
            @SerializedName("remoteList")
            @Expose
            private List<RemoteList> remoteList = null;

            public String getIrBlasterId() {
                return irBlasterId;
            }

            public void setIrBlasterId(String irBlasterId) {
                this.irBlasterId = irBlasterId;
            }

            public String getIrBlasterModuleId() {
                return irBlasterModuleId;
            }

            public void setIrBlasterModuleId(String irBlasterModuleId) {
                this.irBlasterModuleId = irBlasterModuleId;
            }

            public String getIrBlasterName() {
                return irBlasterName;
            }

            public void setIrBlasterName(String irBlasterName) {
                this.irBlasterName = irBlasterName;
            }

            public String getIrBlasterIcon() {
                return irBlasterIcon;
            }

            public void setIrBlasterIcon(String irBlasterIcon) {
                this.irBlasterIcon = irBlasterIcon;
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

            public List<RemoteList> getRemoteList() {
                return remoteList;
            }

            public void setRemoteList(List<RemoteList> remoteList) {
                this.remoteList = remoteList;
            }

            public class RemoteList implements Serializable{

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

                public class RemoteCommandList implements Serializable{

                    @SerializedName("POWER")
                    @Expose
                    private List<String> pOWER = null;
                    @SerializedName("SPEED")
                    @Expose
                    private List<String> sPEED = null;
                    @SerializedName("TEMPERATURE")
                    @Expose
                    private List<Integer> tEMPERATURE = null;

                    public List<String> getPOWER() {
                        return pOWER;
                    }

                    public void setPOWER(List<String> pOWER) {
                        this.pOWER = pOWER;
                    }

                    public List<String> getSPEED() {
                        return sPEED;
                    }

                    public void setSPEED(List<String> sPEED) {
                        this.sPEED = sPEED;
                    }

                    public List<Integer> getTEMPERATURE() {
                        return tEMPERATURE;
                    }

                    public void setTEMPERATURE(List<Integer> tEMPERATURE) {
                        this.tEMPERATURE = tEMPERATURE;
                    }

                }

                public class RemoteCurrentStatusDetails implements Serializable{

                    @SerializedName("id")
                    @Expose
                    private Integer id;
                    @SerializedName("remote_id")
                    @Expose
                    private String remoteId;
                    @SerializedName("power")
                    @Expose
                    private String power;
                    @SerializedName("speed")
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


    }

}
