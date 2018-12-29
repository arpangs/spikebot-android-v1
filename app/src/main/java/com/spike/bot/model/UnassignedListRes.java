package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class UnassignedListRes {

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

        @SerializedName("room_list")
        @Expose
        private List<RoomList> roomList = null;
        @SerializedName("roomdeviceList")
        @Expose
        private List<RoomdeviceList> roomdeviceList = null;

        public List<RoomList> getRoomList() {
            return roomList;
        }

        public void setRoomList(List<RoomList> roomList) {
            this.roomList = roomList;
        }

        public List<RoomdeviceList> getRoomdeviceList() {
            return roomdeviceList;
        }

        public void setRoomdeviceList(List<RoomdeviceList> roomdeviceList) {
            this.roomdeviceList = roomdeviceList;
        }

        public static class RoomdeviceList {

            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("module_id")
            @Expose
            private String moduleId;
            @SerializedName("module_name")
            @Expose
            private String moduleName;
            @SerializedName("is_module")
            @Expose
            private Integer isModule;
            @SerializedName("roomList")
            @Expose
            private RoomList_ roomList;
            @SerializedName("deviceList")
            @Expose
            private List<DeviceList> deviceList = null;
            @SerializedName("sensor_id")
            @Expose
            private String sensorId;
            @SerializedName("sensor_name")
            @Expose
            private String sensorName;
            @SerializedName("sensor_icon")
            @Expose
            private String sensorIcon;
            @SerializedName("sensor_type")
            private String sensorType;

            public String getSensorType() {
                return sensorType;
            }

            public void setSensorType(String sensorType) {
                this.sensorType = sensorType;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            public String getModuleId() {
                return moduleId;
            }

            public void setModuleId(String moduleId) {
                this.moduleId = moduleId;
            }

            public String getModuleName() {
                return moduleName;
            }

            public void setModuleName(String moduleName) {
                this.moduleName = moduleName;
            }

            public Integer getIsModule() {
                return isModule;
            }

            public void setIsModule(Integer isModule) {
                this.isModule = isModule;
            }

            public RoomList_ getRoomList() {
                return roomList;
            }

            public void setRoomList(RoomList_ roomList) {
                this.roomList = roomList;
            }

            public List<DeviceList> getDeviceList() {
                return deviceList;
            }

            public void setDeviceList(List<DeviceList> deviceList) {
                this.deviceList = deviceList;
            }

            public String getSensorId() {
                return sensorId;
            }

            public void setSensorId(String sensorId) {
                this.sensorId = sensorId;
            }

            public String getSensorName() {
                return sensorName;
            }

            public void setSensorName(String sensorName) {
                this.sensorName = sensorName;
            }

            public String getSensorIcon() {
                return sensorIcon;
            }

            public void setSensorIcon(String sensorIcon) {
                this.sensorIcon = sensorIcon;
            }

            public class RoomList_ {

                @SerializedName("room_name")
                @Expose
                private String roomName;
                @SerializedName("panel_name")
                @Expose
                private String panelName;

                public String getRoomName() {
                    return roomName;
                }

                public void setRoomName(String roomName) {
                    this.roomName = roomName;
                }

                public String getPanelName() {
                    return panelName;
                }

                public void setPanelName(String panelName) {
                    this.panelName = panelName;
                }

            }

            public class DeviceList {

                @SerializedName("module_id")
                @Expose
                private String moduleId;

                public String getRoom_panel_id() {
                    return room_panel_id;
                }

                public void setRoom_panel_id(String room_panel_id) {
                    this.room_panel_id = room_panel_id;
                }

                @SerializedName("room_panel_id")
                @Expose
                private String room_panel_id;
                @SerializedName("device_id")
                @Expose
                private Integer deviceId;
                @SerializedName("device_icon")
                @Expose
                private String deviceIcon;
                @SerializedName("device_name")
                @Expose
                private String deviceName;
                @SerializedName("module_name")
                @Expose
                private String moduleName;
                @SerializedName("original_room_device_id")
                @Expose
                private String originalRoomDeviceId;
                @SerializedName("room_device_id")
                @Expose
                private String roomDeviceId;
                @SerializedName("panel_id")
                @Expose
                private String panelId;
                @SerializedName("device_status")
                @Expose
                private String deviceStatus;
                @SerializedName("device_specific_value")
                @Expose
                private String deviceSpecificValue;
                @SerializedName("device_type")
                @Expose
                private String deviceType;

                public String getModuleId() {
                    return moduleId;
                }

                public void setModuleId(String moduleId) {
                    this.moduleId = moduleId;
                }

                public Integer getDeviceId() {
                    return deviceId;
                }

                public void setDeviceId(Integer deviceId) {
                    this.deviceId = deviceId;
                }

                public String getDeviceIcon() {
                    return deviceIcon;
                }

                public void setDeviceIcon(String deviceIcon) {
                    this.deviceIcon = deviceIcon;
                }

                public String getDeviceName() {
                    return deviceName;
                }

                public void setDeviceName(String deviceName) {
                    this.deviceName = deviceName;
                }

                public String getModuleName() {
                    return moduleName;
                }

                public void setModuleName(String moduleName) {
                    this.moduleName = moduleName;
                }

                public String getOriginalRoomDeviceId() {
                    return originalRoomDeviceId;
                }

                public void setOriginalRoomDeviceId(String originalRoomDeviceId) {
                    this.originalRoomDeviceId = originalRoomDeviceId;
                }

                public String getRoomDeviceId() {
                    return roomDeviceId;
                }

                public void setRoomDeviceId(String roomDeviceId) {
                    this.roomDeviceId = roomDeviceId;
                }

                public String getPanelId() {
                    return panelId;
                }

                public void setPanelId(String panelId) {
                    this.panelId = panelId;
                }

                public String getDeviceStatus() {
                    return deviceStatus;
                }

                public void setDeviceStatus(String deviceStatus) {
                    this.deviceStatus = deviceStatus;
                }

                public String getDeviceSpecificValue() {
                    return deviceSpecificValue;
                }

                public void setDeviceSpecificValue(String deviceSpecificValue) {
                    this.deviceSpecificValue = deviceSpecificValue;
                }

                public String getDeviceType() {
                    return deviceType;
                }

                public void setDeviceType(String deviceType) {
                    this.deviceType = deviceType;
                }

            }

        }


        public class RoomList {

            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("room_name")
            @Expose
            private String roomName;

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

            @Override
            public String toString() {
                return roomName;
            }
        }

    }


}
