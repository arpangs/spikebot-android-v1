package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaushal on 22/12/17.
 */

public class RoomVO implements Serializable{
    private String room_id;

    private String created_by;

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getTotalDevice() {
        return totalDevice;
    }

    public void setTotalDevice(String totalDevice) {
        this.totalDevice = totalDevice;
    }

    private String totalDevice;

    public String getSensor_panel() {
        return sensor_panel;
    }

    public void setSensor_panel(String sensor_panel) {
        this.sensor_panel = sensor_panel;
    }

    private String sensor_panel;
    private String roomName;
    private String home_controller_device_id;
    private int room_status;
    public boolean isExpanded=false;
    private String type;
    private ArrayList<String> roomDeviceIdList;

    private String panel_id;
    private String panel_name;
    private String module_id;
    private String module_name;
    private int room_order;
    private boolean isDisable;
    private int is_original;
    private int old_room_status;
    private String is_unread;
    private String totalbeacons;
    private String temp_sensor_value;
    private String door_sensor_value;
    private boolean isSensorPanelOnly;
    private String dropSensorList;
    private boolean isRoomOnOff;
    private String device_count;
    private int pirDetectorList;

    public int getPirDetectorList() {
        return pirDetectorList;
    }

    public void setPirDetectorList(int pirDetectorList) {
        this.pirDetectorList = pirDetectorList;
    }

    public boolean isIsheavyload() {
        return isheavyload;
    }

    public void setIsheavyload(boolean isheavyload) {
        this.isheavyload = isheavyload;
    }

    private boolean isheavyload=false;

    public String getSmart_remote_number() {
        return smart_remote_number;
    }

    public void setSmart_remote_number(String smart_remote_number) {
        this.smart_remote_number = smart_remote_number;
    }

    private String smart_remote_number;

    public String getSelectDevice() {
        return selectDevice;
    }

    public void setSelectDevice(String selectDevice) {
        this.selectDevice = selectDevice;
    }

    private String selectDevice="0";

    public String getDevice_count() {
        return device_count;
    }

    public void setDevice_count(String device_count) {
        this.device_count = device_count;
    }

    public boolean isSensorPanelOnly() {
        return isSensorPanelOnly;
    }

    public void setSensorPanelOnly(boolean sensorPanelOnly) {
        isSensorPanelOnly = sensorPanelOnly;
    }

    public boolean isRoomOnOff() {
        return isRoomOnOff;
    }

    public void setRoomOnOff(boolean roomOnOff) {
        isRoomOnOff = roomOnOff;
    }

    public ArrayList<String> getRemoteDeviceList() {
        return remoteDeviceList;
    }

    public void setRemoteDeviceList(ArrayList<String> remoteDeviceList) {
        this.remoteDeviceList = remoteDeviceList;
    }

    private ArrayList<String> remoteDeviceList;



    public String getDropSensorList() {
        return dropSensorList;
    }

    public void setDropSensorList(String dropSensorList) {
        this.dropSensorList = dropSensorList;
    }

    public boolean isDevicePanel() {
        return isSensorPanelOnly;
    }

    public void setDevicePanel(boolean sensorPanelOnly) {
        isSensorPanelOnly = sensorPanelOnly;
    }

    public String getIs_unread() {
        return is_unread;
    }

    public void setIs_unread(String is_unread) {
        this.is_unread = is_unread;
    }

    public String getTemp_sensor_value() {
        return temp_sensor_value;
    }

    public void setTemp_sensor_value(String temp_sensor_value) {
        this.temp_sensor_value = temp_sensor_value;
    }

    public String getDoor_sensor_value() {
        return door_sensor_value;
    }

    public void setDoor_sensor_value(String door_sensor_value) {
        this.door_sensor_value = door_sensor_value;
    }

    public int getOld_room_status() {
        return old_room_status;
    }

    public void setOld_room_status(int old_room_status) {
        this.old_room_status = old_room_status;
    }

    private boolean isSelectAllDevices;

    public boolean isSelectAllDevices() {
        return isSelectAllDevices;
    }

    public void setSelectAllDevices(boolean selectAllDevices) {
        isSelectAllDevices = selectAllDevices;
    }

    public int getIs_original() {
        return is_original;
    }

    public void setIs_original(int is_original) {
        this.is_original = is_original;
    }

    public String getRoom_icon() {
        return room_icon;
    }

    public void setRoom_icon(String room_icon) {
        this.room_icon = room_icon;
    }

    private String room_icon;

    private int is_schedule;

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public int getIs_schedule() {
        return is_schedule;
    }

    public void setIs_schedule(int is_schedule) {
        this.is_schedule = is_schedule;
    }

    private ArrayList<PanelVO> panelList = new ArrayList<PanelVO>();

    private ArrayList<DevicePanelVO> devicePanelList = new ArrayList<DevicePanelVO>();
    private ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

    private ArrayList<RoomVO> roomList = new ArrayList<RoomVO>();

    private ArrayList<CameraVO> cameraList= new ArrayList<CameraVO>();

    public RoomVO(){
        room_id="";
        roomName="";
        home_controller_device_id="";
        type="";
        panel_id ="";
        panel_name = "";
        module_id = "";
        module_name = "";
        roomDeviceIdList = new ArrayList<>();
    }

    public String getMood_name_id() {
        return mood_name_id;
    }

    public void setMood_name_id(String mood_name_id) {
        this.mood_name_id = mood_name_id;
    }

    private String mood_name_id;

    public int getRoom_order() {
        return room_order;
    }

    public void setRoom_order(int room_order) {
        this.room_order = room_order;
    }

    public ArrayList<RoomVO> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<RoomVO> roomList) {
        this.roomList = roomList;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getPanel_id() {
        return panel_id;
    }

    public void setPanel_id(String panel_id) {
        this.panel_id = panel_id;
    }

    public String getPanel_name() {
        return panel_name;
    }

    public void setPanel_name(String panel_name) {
        this.panel_name = panel_name;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public ArrayList<String> getRoomDeviceIdList() {
        return roomDeviceIdList;
    }

    public void setRoomDeviceIdList(ArrayList<String> roomDeviceIdList) {
        this.roomDeviceIdList = roomDeviceIdList;
    }

    public ArrayList<DevicePanelVO> getDevicePanelList() {
        return devicePanelList;
    }

    public void setDevicePanelList(ArrayList<DevicePanelVO> devicePanelList) {
        this.devicePanelList = devicePanelList;
    }

    public ArrayList<DeviceVO> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<DeviceVO> deviceList) {
        this.deviceList = deviceList;
    }

    public ArrayList<CameraVO> getCameraList() {
        return cameraList;
    }

    public void setCameraList(ArrayList<CameraVO> cameraList) {
        this.cameraList = cameraList;
    }

    public ArrayList<String> getRoomDeviceId() {
        return roomDeviceIdList;
    }

    public void setRoomDeviceId(ArrayList<String> roomDeviceId) {
        this.roomDeviceIdList = roomDeviceId;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public RoomVO(String roomName){
        this.roomName =roomName;
    }

    public ArrayList<PanelVO> getPanelList() {
        return panelList;
    }

    public void setPanelList(ArrayList<PanelVO> panelList) {
        this.panelList = panelList;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public String getRoomId() {
        return room_id;
    }

    public void setRoomId(String roomId) {
        this.room_id = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHomeControllerDeviceId() {
        return home_controller_device_id;
    }

    public void setHomeControllerDeviceId(String homeControllerDeviceId) {
        this.home_controller_device_id = homeControllerDeviceId;
    }

    public String getTotalbeacons() {
        return totalbeacons;
    }

    public void setTotalbeacons(String totalbeacons) {
        this.totalbeacons = totalbeacons;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return roomName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RoomVO)) {
            return false;
        }
        final RoomVO other = (RoomVO) obj;
        if (this.room_id.equalsIgnoreCase(other.room_id )) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.room_id != null ? this.room_id.hashCode() : 0);
        return hash;
    }

}
