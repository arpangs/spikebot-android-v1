package com.spike.bot.model;

import android.view.ViewDebug;

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
        private List<Alert> alerts = null;

        public List<UnseenLog> getUnseenLogs() {
            return unseenLogs;
        }

        public void setUnseenLogs(List<UnseenLog> unseenLogs) {
            this.unseenLogs = unseenLogs;
        }

        @SerializedName("unseen_logs")
        @Expose
        private List<UnseenLog> unseenLogs = null;

        @SerializedName("unread_count")
        @Expose
        private String unread_count;

        public String getUnread_count() {
            return unread_count;
        }

        public void setUnread_count(String unread_count) {
            this.unread_count = unread_count;
        }


        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        public List<Alert> getAlerts() {
            return alerts;
        }

        public void setAlerts(List<Alert> alerts) {
            this.alerts = alerts;
        }


        public class UnseenLog implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("log_type")
            @Expose
            private String logType;
            @SerializedName("seen_by")
            @Expose
            private Object seenBy;
            @SerializedName("activity_action")
            @Expose
            private String activityAction;
            @SerializedName("activity_type")
            @Expose
            private String activityType;
            @SerializedName("activity_description")
            @Expose
            private String activityDescription;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("message")
            @Expose
            private String message;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLogType() {
                return logType;
            }

            public void setLogType(String logType) {
                this.logType = logType;
            }

            public Object getSeenBy() {
                return seenBy;
            }

            public void setSeenBy(Object seenBy) {
                this.seenBy = seenBy;
            }

            public String getActivityAction() {
                return activityAction;
            }

            public void setActivityAction(String activityAction) {
                this.activityAction = activityAction;
            }

            public String getActivityType() {
                return activityType;
            }

            public void setActivityType(String activityType) {
                this.activityType = activityType;
            }

            public String getActivityDescription() {
                return activityDescription;
            }

            public void setActivityDescription(String activityDescription) {
                this.activityDescription = activityDescription;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }

        public class Alert implements Serializable{

            @SerializedName("alert_id")
            @Expose
            private String alertId;
            @SerializedName("device_id")
            @Expose
            private String deviceId;
            @SerializedName("alert_type")
            @Expose
            private String alertType;
            @SerializedName("user_id")
            @Expose
            private String userId;
            @SerializedName("days")
            @Expose
            private String days;
            @SerializedName("start_time")
            @Expose
            private String startTime;
            @SerializedName("end_time")
            @Expose
            private String endTime;
            @SerializedName("meta")
            @Expose
            private String meta;
            @SerializedName("is_active")
            @Expose
            private String isActive;
            @SerializedName("min_humidity")
            @Expose
            private String minHumidity;
            @SerializedName("max_humidity")
            @Expose
            private String maxHumidity;

            public String getMax_temp() {
                return max_temp;
            }

            public void setMax_temp(String max_temp) {
                this.max_temp = max_temp;
            }

            public String getMin_temp() {
                return min_temp;
            }

            public void setMin_temp(String min_temp) {
                this.min_temp = min_temp;
            }

            @SerializedName("max_temp")
            @Expose
            private String max_temp;

            @SerializedName("min_temp")
            @Expose
            private String min_temp;

            public boolean isCFActive() {
                return isCFActive;
            }

            public void setCFActive(boolean CFActive) {
                isCFActive = CFActive;
            }

            private boolean isCFActive;

            public String getAlertId() {
                return alertId;
            }

            public void setAlertId(String alertId) {
                this.alertId = alertId;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public String getAlertType() {
                return alertType;
            }

            public void setAlertType(String alertType) {
                this.alertType = alertType;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getDays() {
                return days;
            }

            public void setDays(String days) {
                this.days = days;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getMeta() {
                return meta;
            }

            public void setMeta(String meta) {
                this.meta = meta;
            }

            public String getIsActive() {
                return isActive;
            }

            public void setIsActive(String isActive) {
                this.isActive = isActive;
            }

            public String getMinHumidity() {
                return minHumidity;
            }

            public void setMinHumidity(String minHumidity) {
                this.minHumidity = minHumidity;
            }

            public String getMaxHumidity() {
                return maxHumidity;
            }

            public void setMaxHumidity(String maxHumidity) {
                this.maxHumidity = maxHumidity;
            }

        }

        public class irBlaster{
            public String getDevice_name() {
                return device_name;
            }

            public void setDevice_name(String device_name) {
                this.device_name = device_name;
            }

            @SerializedName("device_name")
            @Expose
            private String device_name;

            @SerializedName("device_id")
            @Expose
            private String device_id;

            public String getDevice_id() {
                return device_id;
            }

            public void setDevice_id(String device_id) {
                this.device_id = device_id;
            }

            @SerializedName("module_id\n")
            @Expose
            private String module_id;

            public String getModule_id() {
                return module_id;
            }

            public void setModule_id(String module_id) {
                this.module_id = module_id;
            }
        }

        public class Device implements Serializable {


            public String getDevice_id() {
                return device_id;
            }

            public void setDevice_id(String device_id) {
                this.device_id = device_id;
            }

            @SerializedName("device_id")
            @Expose
            private String device_id;

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
            @SerializedName("meta_unit")
            @Expose
            private String meta_unit;
            @SerializedName("meta_enable_lock_unlock_from_app")
            @Expose
            private String meta_enable_lock_unlock_from_app;

            public String getMeta_enable_lock_unlock_from_app() {
                return meta_enable_lock_unlock_from_app;
            }

            public void setMeta_enable_lock_unlock_from_app(String meta_enable_lock_unlock_from_app) {
                this.meta_enable_lock_unlock_from_app = meta_enable_lock_unlock_from_app;
            }


            @SerializedName("humidity_counter")
            @Expose
            private int humidity_counter ;


            @SerializedName("temp_counter")
            @Expose
            private int temp_counter ;

            public int getHumidity_counter() {
                return humidity_counter;
            }

            public void setHumidity_counter(int humidity_counter) {
                this.humidity_counter = humidity_counter;
            }

            public int getTemp_counter() {
                return temp_counter;
            }

            public void setTemp_counter(int temp_counter) {
                this.temp_counter = temp_counter;
            }

            public irBlaster getIr_blaster() {
                return ir_blaster;
            }

            public void setIr_blaster(irBlaster ir_blaster) {
                this.ir_blaster = ir_blaster;
            }

            @SerializedName("ir_blaster")
            @Expose
            private irBlaster ir_blaster;

            public String getMeta_device_brand() {
                return meta_device_brand;
            }

            public void setMeta_device_brand(String meta_device_brand) {
                this.meta_device_brand = meta_device_brand;
            }

            @SerializedName("meta_device_brand")
            @Expose
            private String meta_device_brand;

            public String getMeta_battery_level() {
                return meta_battery_level;
            }

            public void setMeta_battery_level(String meta_battery_level) {
                this.meta_battery_level = meta_battery_level;
            }

            @SerializedName("meta_battery_level")
            @Expose
            private String meta_battery_level;

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

            public Room getRoom() {
                return room;
            }

            public void setRoom(Room room) {
                this.room = room;
            }

            @SerializedName("room")
            @Expose
            public Room room;

            public String getMeta_device_default_status() {
                return meta_device_default_status;
            }

            public void setMeta_device_default_status(String meta_device_default_status) {
                this.meta_device_default_status = meta_device_default_status;
            }

            @SerializedName("meta_pass_code")
            @Expose
            private String meta_pass_code;
            @SerializedName("meta_onetime_code")
            @Expose
            private String meta_onetime_code;

            public String getMeta_pass_code() {
                return meta_pass_code;
            }

            public void setMeta_pass_code(String meta_pass_code) {
                this.meta_pass_code = meta_pass_code;
            }

            public String getMeta_onetime_code() {
                return meta_onetime_code;
            }

            public void setMeta_onetime_code(String meta_onetime_code) {
                this.meta_onetime_code = meta_onetime_code;
            }

            public String getMeta_unit() {
                return meta_unit;
            }

            public void setMeta_unit(String meta_unit) {
                this.meta_unit = meta_unit;
            }

            @SerializedName("meta_device_default_status")
            @Expose
            private String meta_device_default_status;

            public String getMeta_device_model() {
                return meta_device_model;
            }

            public void setMeta_device_model(String meta_device_model) {
                this.meta_device_model = meta_device_model;
            }

            @SerializedName("meta_device_model")
            @Expose
            private String meta_device_model;

            public String getFahrenheitvalue() {
                return fahrenheitvalue;
            }

            public void setFahrenheitvalue(String fahrenheitvalue) {
                this.fahrenheitvalue = fahrenheitvalue;
            }


            public String fahrenheitvalue;

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

            public String getmeta_unit() {
                return meta_unit;
            }

            public void setmeta_unit(String deviceMeta) {
                this.meta_unit = meta_unit;
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

        public class Room{

            public String getRoom_name() {
                return room_name;
            }

            public void setRoom_name(String room_name) {
                this.room_name = room_name;
            }

            @SerializedName("room_name")
            @Expose
            private String room_name;


            @SerializedName("room_id")
            @Expose
            private String room_id;

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }
        }

        public class DeviceMeta {

            @SerializedName("unit")
            @Expose
            private String unit;
            @SerializedName("sensor_battery_level")
            @Expose
            private Integer sensorBatteryLevel;

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public Integer getSensorBatteryLevel() {
                return sensorBatteryLevel;
            }

            public void setSensorBatteryLevel(Integer sensorBatteryLevel) {
                this.sensorBatteryLevel = sensorBatteryLevel;
            }



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
