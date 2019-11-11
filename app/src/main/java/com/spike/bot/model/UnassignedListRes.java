package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class UnassignedListRes implements Serializable{

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

    public static class Data  implements Serializable{

        @SerializedName("module_id")
        @Expose
        private String moduleId;
        @SerializedName("module_identifier")
        @Expose
        private String moduleIdentifier;
        @SerializedName("module_type")
        @Expose
        private String moduleType;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("last_response_time")
        @Expose
        private String lastResponseTime;
        @SerializedName("is_configured")
        @Expose
        private String isConfigured;

        public String getModuleId() {
            return moduleId;
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public String getModuleIdentifier() {
            return moduleIdentifier;
        }

        public void setModuleIdentifier(String moduleIdentifier) {
            this.moduleIdentifier = moduleIdentifier;
        }

        public String getModuleType() {
            return moduleType;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getLastResponseTime() {
            return lastResponseTime;
        }

        public void setLastResponseTime(String lastResponseTime) {
            this.lastResponseTime = lastResponseTime;
        }

        public String getIsConfigured() {
            return isConfigured;
        }

        public void setIsConfigured(String isConfigured) {
            this.isConfigured = isConfigured;
        }


        public static class RoomdeviceList implements Serializable {

            @SerializedName("room_id")
            @Expose
            private String roomId;

            public String getRepeator_module_id() {
                return repeator_module_id;
            }

            public void setRepeator_module_id(String repeator_module_id) {
                this.repeator_module_id = repeator_module_id;
            }

            public String getRepeator_name() {
                return repeator_name;
            }

            public void setRepeator_name(String repeator_name) {
                this.repeator_name = repeator_name;
            }

            @SerializedName("repeator_module_id")
            @Expose
            private String repeator_module_id;

            @SerializedName("repeator_name")
            @Expose
            private String repeator_name;

            @SerializedName("module_id")
            @Expose
            private String moduleId;
            @SerializedName("module_name")
            @Expose
            private String moduleName;
            @SerializedName("is_module")
            @Expose
            private Integer isModule=0;
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

            public String getLock_id() {
                return lock_id;
            }

            public void setLock_id(String lock_id) {
                this.lock_id = lock_id;
            }

            public String getLock_data() {
                return lock_data;
            }

            public void setLock_data(String lock_data) {
                this.lock_data = lock_data;
            }

            @SerializedName("lock_id")
            private String lock_id;

            @SerializedName("lock_data")
            private String lock_data;

            public String getLock_subtype() {
                return lock_subtype;
            }

            public void setLock_subtype(String lock_subtype) {
                this.lock_subtype = lock_subtype;
            }

            @SerializedName("lock_subtype")
            private String lock_subtype;

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

            public class RoomList_  implements Serializable{

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

            public class DeviceList implements Serializable{

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


        public class RoomList implements Serializable {

            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("room_name")
            @Expose
            private String roomName;

            public boolean isDisable() {
                return isDisable;
            }

            public void setDisable(boolean disable) {
                isDisable = disable;
            }

            private boolean isDisable;

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
