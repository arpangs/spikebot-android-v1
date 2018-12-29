package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 15/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorLogRes {

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

    public Integer getCode() {
       return code;
    }
    public String getMessage() {
       return message;
    }

    public Data getData() {
       return data;
    }

    public class Data {

        @SerializedName("doorSensor")
        private List<DoorSensor> doorSensor = null;
        @SerializedName("tempSensor")
        private List<TempSensor> tempSensor = null;

        public List<DoorSensor> getDoorSensor() {
            return doorSensor;
        }

        public List<TempSensor> getTempSensor() {
            return tempSensor;
        }

        public class DoorSensor {
            @SerializedName("door_sensor_name")
            private String doorSensorName;
            @SerializedName("room_name")
            private String roomName;
            @SerializedName("module_id")
            private String moduleId;
            @SerializedName("room_id")
            private String roomId;
            @SerializedName("sensor_name")
            private String sensorName;

            public String getDoorSensorName() {
                return doorSensorName;
            }

            public String getRoomName() {
                return roomName;
            }

            public String getModuleId() {
                return moduleId;
            }

            public String getRoomId() {
                return roomId;
            }

            public String getSensorName() {
                return sensorName;
            }

        }

        public class TempSensor {

            @SerializedName("temp_sensor_name")
            private String tempSensorName;
            @SerializedName("room_name")
            private String roomName;
            @SerializedName("module_id")
            private String moduleId;
            @SerializedName("room_id")
            private String roomId;
            @SerializedName("sensor_name")
            private String sensorName;

            public String getTempSensorName() {
                return tempSensorName;
            }

            public String getRoomName() {
                return roomName;
            }

            public String getModuleId() {
                return moduleId;
            }

            public String getRoomId() {
                return roomId;
            }

            public String getSensorName() {
                return sensorName;
            }
        }

    }

}
