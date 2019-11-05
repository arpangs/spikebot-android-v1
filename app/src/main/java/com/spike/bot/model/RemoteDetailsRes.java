package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 18/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RemoteDetailsRes implements Serializable {


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


        @SerializedName("device")
        @Expose
        private Device device;
        @SerializedName("alerts")
        @Expose
        private List<Object> alerts = null;

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        public List<Object> getAlerts() {
            return alerts;
        }

        public void setAlerts(List<Object> alerts) {
            this.alerts = alerts;
        }

        public class Device implements Serializable {

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
            private DeviceMeta deviceMeta;
            @SerializedName("is_active")
            @Expose
            private String isActive;
            @SerializedName("module_type")
            @Expose
            private String moduleType;
            @SerializedName("module_identifier")
            @Expose
            private String moduleIdentifier;
            @SerializedName("module_meta")
            @Expose
            private String moduleMeta;

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

            public DeviceMeta getDeviceMeta() {
                return deviceMeta;
            }

            public void setDeviceMeta(DeviceMeta deviceMeta) {
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

            public String getModuleIdentifier() {
                return moduleIdentifier;
            }

            public void setModuleIdentifier(String moduleIdentifier) {
                this.moduleIdentifier = moduleIdentifier;
            }

            public String getModuleMeta() {
                return moduleMeta;
            }

            public void setModuleMeta(String moduleMeta) {
                this.moduleMeta = moduleMeta;
            }

        }

        public class DeviceMeta {

            @SerializedName("device_default_mode")
            @Expose
            private String deviceDefaultMode;
            @SerializedName("device_default_status")
            @Expose
            private String deviceDefaultStatus;
            @SerializedName("device_brand")
            @Expose
            private String deviceBrand;
            @SerializedName("device_model")
            @Expose
            private String deviceModel;
            @SerializedName("device_codeset_id")
            @Expose
            private String deviceCodesetId;
            @SerializedName("ir_blaster_id")
            @Expose
            private String irBlasterId;

            public String getDeviceDefaultMode() {
                return deviceDefaultMode;
            }

            public void setDeviceDefaultMode(String deviceDefaultMode) {
                this.deviceDefaultMode = deviceDefaultMode;
            }

            public String getDeviceDefaultStatus() {
                return deviceDefaultStatus;
            }

            public void setDeviceDefaultStatus(String deviceDefaultStatus) {
                this.deviceDefaultStatus = deviceDefaultStatus;
            }

            public String getDeviceBrand() {
                return deviceBrand;
            }

            public void setDeviceBrand(String deviceBrand) {
                this.deviceBrand = deviceBrand;
            }

            public String getDeviceModel() {
                return deviceModel;
            }

            public void setDeviceModel(String deviceModel) {
                this.deviceModel = deviceModel;
            }

            public String getDeviceCodesetId() {
                return deviceCodesetId;
            }

            public void setDeviceCodesetId(String deviceCodesetId) {
                this.deviceCodesetId = deviceCodesetId;
            }

            public String getIrBlasterId() {
                return irBlasterId;
            }

            public void setIrBlasterId(String irBlasterId) {
                this.irBlasterId = irBlasterId;
            }

        }

        public class RemoteCommandList implements Serializable{

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

        public class RemoteCurrentStatusDetails implements Serializable {

            public String getIr_code() {
                return ir_code;
            }

            public void setIr_code(String ir_code) {
                this.ir_code = ir_code;
            }

            @SerializedName("ir_code")
            @Expose
            private String ir_code;

            @SerializedName("brand_name")
            @Expose
            private String brand_name;

            public String getBrand_name() {
                return brand_name;
            }

            public void setBrand_name(String brand_name) {
                this.brand_name = brand_name;
            }

            public String getModel_number() {
                return model_number;
            }

            public void setModel_number(String model_number) {
                this.model_number = model_number;
            }

            @SerializedName("model_number")
            @Expose
            private String model_number;

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
