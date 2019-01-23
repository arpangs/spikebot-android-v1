package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRDeviceDetailsRes {

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

        @SerializedName("ir_list")
        @Expose
        private List<IrList> irList = null;
        @SerializedName("room_list")
        @Expose
        private List<RoomList> roomList = null;
        @SerializedName("device_list")
        @Expose
        private List<Devicelist> devicelist = null;

        public List<IrList> getIrList() {
            return irList;
        }

        public void setIrList(List<IrList> irList) {
            this.irList = irList;
        }

        public List<RoomList> getRoomList() {
            return roomList;
        }

        public void setRoomList(List<RoomList> roomList) {
            this.roomList = roomList;
        }

        public List<Devicelist> getDevicelist() {
            return devicelist;
        }

        public void setDevicelist(List<Devicelist> devicelist) {
            this.devicelist = devicelist;
        }

        public static class IrList {

            @SerializedName("ir_blaster_name")
            @Expose
            private String irBlasterName;
            @SerializedName("ir_blaster_id")
            @Expose
            private String irBlasterId;
            @SerializedName("ir_blaster_module_id")
            @Expose
            private String irBlasterModuleId;
            @SerializedName("is_active")
            @Expose
            private Integer isActive;
            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("room_name")
            @Expose
            private String roomName;
            @SerializedName("panel_id")
            @Expose
            private String panelId;

            public IrList(){}

            public String getIrBlasterName() {
                return irBlasterName;
            }

            public void setIrBlasterName(String irBlasterName) {
                this.irBlasterName = irBlasterName;
            }

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

            public Integer getIsActive() {
                return isActive;
            }

            public void setIsActive(Integer isActive) {
                this.isActive = isActive;
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

            public String getPanelId() {
                return panelId;
            }

            public void setPanelId(String panelId) {
                this.panelId = panelId;
            }

            @Override
            public String toString() {
                return irBlasterName;
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
        public class Devicelist {

            @SerializedName("device_id")
            @Expose
            private Integer deviceId;
            @SerializedName("device_type")
            @Expose
            private String deviceType;

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

        }

    }
}
