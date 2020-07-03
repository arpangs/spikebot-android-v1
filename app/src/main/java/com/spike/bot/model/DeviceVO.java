package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by kaushal on 22/12/17.
 */

public class DeviceVO implements Serializable,Cloneable {
    private int mood_id;
    private String roomDeviceId;
    private int userId;
    private String homeControllerDeviceId;
    private String roomId;
    private String roomName;
    private String deviceId;
    private String deviceName;
    private String deviceImage;
    private String device_icon;
    private String moduleId;
    private int moduleOrder;
    private int deviceStatus;
    private String deviceSpecificValue;
    private String deviceOnOffTime;
    private String deviceOnTime;
    private String deviceOffTime;
    private String deviceTimeDifference;
    private String deviceSync;
    private String deviceType;
    private String enable_lock_unlock_from_app;
    private String meta_ir_blaster_id;

    public String getEnable_lock_unlock_from_app() {
        return enable_lock_unlock_from_app;
    }

    public void setEnable_lock_unlock_from_app(String enable_lock_unlock_from_app) {
        this.enable_lock_unlock_from_app = enable_lock_unlock_from_app;
    }

    public String getDevice_sub_status() {
        return device_sub_status;
    }

    public void setDevice_sub_status(String device_sub_status) {
        this.device_sub_status = device_sub_status;
    }

    private String device_sub_status;

    public String getDevice_identifier() {
        return device_identifier;
    }

    public void setDevice_identifier(String device_identifier) {
        this.device_identifier = device_identifier;
    }

    private String device_identifier="";
    private int isActive;
    private int isAlive;
    private String isLocked;
    private int shareId;
    private String createdBy;
    private String createdDate;
    private String modifiedBy;
    private String modifiedDate;
    private int auto_on_off_value;
    private int schedule_value;
    private String panel_name;
    private boolean selected;
    private String panel_id;
    private int is_original;
    private String original_room_device_id;
    private int oldStatus;

    public int getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(int is_locked) {
        this.is_locked = is_locked;
    }

    private int is_locked;

    private boolean isSensor;
    private String sensor_id;
    private String sensor_name;
    private String sensor_voltage;
    private String sensor_icon;
    private String door_sensor_status="";
    private String sensor_type="";
    private String is_unread;
    private String created_date;
    private String temp_in_c;
    private String temp_in_f;
    private String module_type;

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    private String humidity;
    private String is_in_c;
    private String ir_blaster_id;
    private String remote_status;
    private String remote_device_id;
    private String room_panel_id;

    public String getDevice_sub_type() {
        return device_sub_type;
    }

    public void setDevice_sub_type(String device_sub_type) {
        this.device_sub_type = device_sub_type;
    }

    private String device_sub_type;

    public String getPanel_device_id() {
        return panel_device_id;
    }

    public void setPanel_device_id(String panel_device_id) {
        this.panel_device_id = panel_device_id;
    }

    public String getMeta_ir_blaster_id() {
        return meta_ir_blaster_id;
    }

    public void setMeta_ir_blaster_id(String meta_ir_blaster_id) {
        this.meta_ir_blaster_id = meta_ir_blaster_id;
    }

    private String panel_device_id;

    public int getLock_id() {
        return lock_id;
    }

    public void setLock_id(int lock_id) {
        this.lock_id = lock_id;
    }

    private int lock_id;

    public int getDoor_lock_status() {
        return door_lock_status;
    }

    public void setDoor_lock_status(int door_lock_status) {
        this.door_lock_status = door_lock_status;
    }

    private int door_lock_status=1;

    public int getIs_door_active() {
        return is_door_active;
    }

    public void setIs_door_active(int is_door_active) {
        this.is_door_active = is_door_active;
    }

    public int getIs_lock_active() {
        return is_lock_active;
    }

    public void setIs_lock_active(int is_lock_active) {
        this.is_lock_active = is_lock_active;
    }

    private int is_door_active=0;
    private int is_lock_active=0;

    public int getDoor_subtype() {
        return door_subtype;
    }

    public void setDoor_subtype(int door_subtype) {
        this.door_subtype = door_subtype;
    }

    private int door_subtype;

    public String getDevice_nameTemp() {
        return device_nameTemp;
    }

    public void setDevice_nameTemp(String device_nameTemp) {
        this.device_nameTemp = device_nameTemp;
    }

    private String device_nameTemp;

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    private String power;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private String mode;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    private String temperature;

    public String getTo_use() {
        return to_use;
    }

    public void setTo_use(String to_use) {
        this.to_use = to_use;
    }

    private String to_use;

    private String speed;
    private String temprature;

    public String getTemprature() {
        return temprature;
    }

    public void setTemprature(String temprature) {
        this.temprature = temprature;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getRoom_panel_id() {
        return room_panel_id;
    }

    public void setRoom_panel_id(String room_panel_id) {
        this.room_panel_id = room_panel_id;
    }

    @Override
    public String toString() {
        return deviceName;
    }

    public String getRemote_device_id() {
        return remote_device_id;
    }

    public void setRemote_device_id(String remote_device_id) {
        this.remote_device_id = remote_device_id;
    }

    public String getRemote_status() {
        return remote_status;
    }

    public void setRemote_status(String remote_status) {
        this.remote_status = remote_status;
    }

    public String getIr_blaster_id() {
        return ir_blaster_id;
    }

    public void setIr_blaster_id(String ir_blaster_id) {
        this.ir_blaster_id = ir_blaster_id;
    }

    public String getIs_in_c() {
        return is_in_c;
    }

    public void setIs_in_c(String is_in_c) {
        this.is_in_c = is_in_c;
    }

    public String getIs_unread() {
        return is_unread;
    }

    public void setIs_unread(String is_unread) {
        this.is_unread = is_unread;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getTemp_in_c() {
        return temp_in_c;
    }

    public void setTemp_in_c(String temp_in_c) {
        this.temp_in_c = temp_in_c;
    }

    public String getTemp_in_f() {
        return temp_in_f;
    }

    public void setTemp_in_f(String temp_in_f) {
        this.temp_in_f = temp_in_f;
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setSensor(boolean sensor) {
        isSensor = sensor;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getSensor_voltage() {
        return sensor_voltage;
    }

    public void setSensor_voltage(String sensor_voltage) {
        this.sensor_voltage = sensor_voltage;
    }

    public String getSensor_icon() {
        return sensor_icon;
    }

    public void setSensor_icon(String sensor_icon) {
        this.sensor_icon = sensor_icon;
    }

    public String getDoor_sensor_status() {
        return door_sensor_status;
    }

    public void setDoor_sensor_status(String door_sensor_status) {
        this.door_sensor_status = door_sensor_status;
    }

    public String getSensor_type() {
        return sensor_type;
    }

    public void setSensor_type(String sensor_type) {
        this.sensor_type = sensor_type;
    }

    public int getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(int oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getOriginal_room_device_id() {
        return original_room_device_id;
    }

    public void setOriginal_room_device_id(String original_room_device_id) {
        this.original_room_device_id = original_room_device_id;
    }

    public int getIs_original() {
        return is_original;
    }

    public void setIs_original(int is_original) {
        this.is_original = is_original;
    }

    public String getPanel_id() {
        return panel_id;
    }

    public void setPanel_id(String panel_id) {
        this.panel_id = panel_id;
    }

    public int getMood_id() {
        return mood_id;
    }

    public void setMood_id(int mood_id) {
        this.mood_id = mood_id;
    }

    public DeviceVO(){

    }
    public DeviceVO(String deviceName,String deviceId){
        this.deviceName = deviceName;
        this.deviceId = deviceId;
    }
    public DeviceVO(String deviceName){
        this.deviceName = deviceName;
    }
    public String getRoomDeviceId() {
        return roomDeviceId;
    }

    public void setRoomDeviceId(String roomDeviceId) {
        this.roomDeviceId = roomDeviceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHomeControllerDeviceId() {
        return homeControllerDeviceId;
    }

    public void setHomeControllerDeviceId(String homeControllerDeviceId) {
        this.homeControllerDeviceId = homeControllerDeviceId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPanel_name() {
        return panel_name;
    }

    public void setPanel_name(String panel_name) {
        this.panel_name = panel_name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(String deviceImage) {
        this.deviceImage = deviceImage;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(int moduleOrder) {
        this.moduleOrder = moduleOrder;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceSpecificValue() {
        return deviceSpecificValue;
    }

    public void setDeviceSpecificValue(String deviceSpecificValue) {
        this.deviceSpecificValue = deviceSpecificValue;
    }

    public String getDeviceOnOffTime() {
        return deviceOnOffTime;
    }

    public void setDeviceOnOffTime(String deviceOnOffTime) {
        this.deviceOnOffTime = deviceOnOffTime;
    }

    public String getDeviceOnTime() {
        return deviceOnTime;
    }

    public void setDeviceOnTime(String deviceOnTime) {
        this.deviceOnTime = deviceOnTime;
    }

    public String getDeviceOffTime() {
        return deviceOffTime;
    }

    public void setDeviceOffTime(String deviceOffTime) {
        this.deviceOffTime = deviceOffTime;
    }

    public String getDeviceTimeDifference() {
        return deviceTimeDifference;
    }

    public void setDeviceTimeDifference(String deviceTimeDifference) {
        this.deviceTimeDifference = deviceTimeDifference;
    }

    public String getDeviceSync() {
        return deviceSync;
    }

    public void setDeviceSync(String deviceSync) {
        this.deviceSync = deviceSync;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getAuto_on_off_value() {
        return auto_on_off_value;
    }

    public void setAuto_on_off_value(int auto_on_off_value) {
        this.auto_on_off_value = auto_on_off_value;
    }

    public int getSchedule_value() {
        return schedule_value;
    }

    public void setSchedule_value(int schedule_value) {
        this.schedule_value = schedule_value;
    }

    public String getDevice_icon() {
        return device_icon;
    }

    public void setDevice_icon(String device_icon) {
        this.device_icon = device_icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getModule_type() {
        return module_type;
    }

    public void setModule_type(String module_type) {
        this.module_type = module_type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DeviceVO)) {
            return false;
        }
        final DeviceVO other = (DeviceVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.deviceId == other.deviceId && this.moduleId.equals(other.moduleId) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.moduleId != null ? this.moduleId.hashCode() : 0);
        hash = 53 * hash + Integer.parseInt(this.deviceId);
        return hash;
    }
    @Override public DeviceVO clone() {
        try {
            final DeviceVO result = (DeviceVO) super.clone();
            // copy fields that need to be copied here!
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

}