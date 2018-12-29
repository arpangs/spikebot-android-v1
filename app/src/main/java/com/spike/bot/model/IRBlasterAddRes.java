package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddRes {
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
        @SerializedName("roomList")
        @Expose
        private List<RoomList> roomList = null;

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
            @SerializedName("remoteList")
            @Expose
            private List<RemoteList> remoteList = null;

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

            public List<RemoteList> getRemoteList() {
                return remoteList;
            }

            public void setRemoteList(List<RemoteList> remoteList) {
                this.remoteList = remoteList;
            }

            @Override
            public String toString() {
                return irBlasterName;
            }

            public class RemoteList {

                @SerializedName("remote_id")
                @Expose
                private String remoteId;
                @SerializedName("remote_name")
                @Expose
                private String remoteName;
                @SerializedName("ir_blaster_id")
                @Expose
                private String irBlasterId;
                @SerializedName("remote_icon")
                @Expose
                private String remoteIcon;
                @SerializedName("room_id")
                @Expose
                private String roomId;
                @SerializedName("panel_id")
                @Expose
                private String panelId;
                @SerializedName("room_name")
                @Expose
                private String roomName;
                @SerializedName("is_active")
                @Expose
                private Integer isActive;

                public String getRemoteId() {
                    return remoteId;
                }

                public void setRemoteId(String remoteId) {
                    this.remoteId = remoteId;
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

                public String getRemoteIcon() {
                    return remoteIcon;
                }

                public void setRemoteIcon(String remoteIcon) {
                    this.remoteIcon = remoteIcon;
                }

                public String getRoomId() {
                    return roomId;
                }

                public void setRoomId(String roomId) {
                    this.roomId = roomId;
                }

                public String getPanelId() {
                    return panelId;
                }

                public void setPanelId(String panelId) {
                    this.panelId = panelId;
                }

                public String getRoomName() {
                    return roomName;
                }

                public void setRoomName(String roomName) {
                    this.roomName = roomName;
                }

                public Integer getIsActive() {
                    return isActive;
                }

                public void setIsActive(Integer isActive) {
                    this.isActive = isActive;
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
                return this.roomName;            // What to display in the Spinner list.
            }

        }


    }
}
