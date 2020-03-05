package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CameraCounterModel implements Serializable
{
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


    public static class Data implements Serializable {

        @SerializedName("camera_counter_list")
        @Expose
        private List<CameraCounterList> cameraCounterList = null;

        @SerializedName("total_camera_notification")
        @Expose
        private Integer totalCameraNotification;

        @SerializedName("total_counter_list")
        @Expose
        private List<TotalCounterList> totalCounterList = null;

        public List<CameraCounterList> getCameraCounterList() {
            return cameraCounterList;
        }

        public void setCameraCounterList(List<CameraCounterList> cameraCounterList) {
            this.cameraCounterList = cameraCounterList;
        }

        public Integer getTotalCameraNotification() {
            return totalCameraNotification;
        }

        public void setTotalCameraNotification(Integer totalCameraNotification) {
            this.totalCameraNotification = totalCameraNotification;
        }

        public List<TotalCounterList> getTotalCounterList() {
            return totalCounterList;
        }

        public void setTotalCounterList(List<TotalCounterList> totalCounterList) {
            this.totalCounterList = totalCounterList;
        }


        public class CameraCounterList implements Serializable {

            @SerializedName("camera_id")
            @Expose
            private String cameraId;
            @SerializedName("jetson_device_id")
            @Expose
            private String jetsonDeviceId;
            @SerializedName("total_unread")
            @Expose
            private Integer totalUnread;

            public String getCameraId() {
                return cameraId;
            }

            public void setCameraId(String cameraId) {
                this.cameraId = cameraId;
            }

            public String getJetsonDeviceId() {
                return jetsonDeviceId;
            }

            public void setJetsonDeviceId(String jetsonDeviceId) {
                this.jetsonDeviceId = jetsonDeviceId;
            }

            public Integer getTotalUnread() {
                return totalUnread;
            }

            public void setTotalUnread(Integer totalUnread) {
                this.totalUnread = totalUnread;
            }

        }

        public class TotalCounterList implements Serializable {

            @SerializedName("jetson_device_id")
            @Expose
            private String jetsonDeviceId;
            @SerializedName("total_unread")
            @Expose
            private Integer totalUnread;

            public String getJetsonDeviceId() {
                return jetsonDeviceId;
            }

            public void setJetsonDeviceId(String jetsonDeviceId) {
                this.jetsonDeviceId = jetsonDeviceId;
            }

            public Integer getTotalUnread() {
                return totalUnread;
            }

            public void setTotalUnread(Integer totalUnread) {
                this.totalUnread = totalUnread;
            }

        }
    }
}
