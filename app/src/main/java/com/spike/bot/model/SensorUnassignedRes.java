package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 17/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorUnassignedRes {

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
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

        @SerializedName("unassigend_sensor_list")
        private List<UnassigendSensorList> unassigendSensorList = null;
        @SerializedName("room_list")
        private List<RoomList> roomList = null;

        public List<UnassigendSensorList> getUnassigendSensorList() {
            return unassigendSensorList;
        }

        public void setUnassigendSensorList(List<UnassigendSensorList> unassigendSensorList) {
            this.unassigendSensorList = unassigendSensorList;
        }

        public List<RoomList> getRoomList() {
            return roomList;
        }

        public void setRoomList(List<RoomList> roomList) {
            this.roomList = roomList;
        }

        public class UnassigendSensorList {

            @SerializedName("sensor_id")
            private String sensorId;
            @SerializedName("module_id")
            private String moduleId;
            @SerializedName("sensor_name")
            private String sensorName;
            @SerializedName("sensor_icon")
            private String sensorIcon;

            public String getLock_id() {
                return lock_id;
            }

            public void setLock_id(String lock_id) {
                this.lock_id = lock_id;
            }

            @SerializedName("lock_id")
            private String lock_id;

            public String getLock_data() {
                return lock_data;
            }

            public void setLock_data(String lock_data) {
                this.lock_data = lock_data;
            }

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

            public boolean isChecked;

            public boolean isChecked() {
                return isChecked;
            }

            public void setChecked(boolean checked) {
                isChecked = checked;
            }

            public String getSensorId() {
                return sensorId;
            }

            public void setSensorId(String sensorId) {
                this.sensorId = sensorId;
            }

            public String getModuleId() {
                return moduleId;
            }

            public void setModuleId(String moduleId) {
                this.moduleId = moduleId;
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

        }

        public class RoomList {

            @SerializedName("room_id")
            private String roomId;
            @SerializedName("room_name")
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

        }

    }

}
