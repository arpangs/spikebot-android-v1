package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Sagar on 14/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DoorSensorResModel {

    @SerializedName("code")
    private String mCode;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("data")
    private DATA mData;

    public static  class DATA{

        @SerializedName("doorList")
        private DoorList[] doorLists;

        public DATA.bridgeList[] getBridgeList() {
            return bridgeList;
        }

        public void setBridgeList(DATA.bridgeList[] bridgeList) {
            this.bridgeList = bridgeList;
        }

        @SerializedName("bridgeList")
        private bridgeList[] bridgeList;

        public DoorList[] getDoorLists() {
            return doorLists;
        }

        public static class bridgeList {

            //  "id": 2,
            //        "bridge_id": "7035",
            //        "bridge_name": "hk"

            public int getBridge_id() {
                return bridge_id;
            }

            public void setBridge_id(int bridge_id) {
                this.bridge_id = bridge_id;
            }

            public String getBridge_name() {
                return bridge_name;
            }

            public void setBridge_name(String bridge_name) {
                this.bridge_name = bridge_name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            @SerializedName("bridge_id")
            private int bridge_id;


            @SerializedName("bridge_name")
            private String bridge_name;

            @SerializedName("id")
            private int id;

        }

            public static class DoorList {
            public void setmDoorSensorId(String mDoorSensorId) {
                this.mDoorSensorId = mDoorSensorId;
            }

            public void setmDoorSensorMoudleId(String mDoorSensorMoudleId) {
                this.mDoorSensorMoudleId = mDoorSensorMoudleId;
            }

            public void setmDoorSensorName(String mDoorSensorName) {
                this.mDoorSensorName = mDoorSensorName;
            }

            public void setmDoorSensorVoltage(String mDoorSensorVoltage) {
                this.mDoorSensorVoltage = mDoorSensorVoltage;
            }

            public void setmDoorSensorPerc(String mDoorSensorPerc) {
                this.mDoorSensorPerc = mDoorSensorPerc;
            }

            public void setmIsUNread(String mIsUNread) {
                this.mIsUNread = mIsUNread;
            }

            public void setmIsPushEnable(String mIsPushEnable) {
                this.mIsPushEnable = mIsPushEnable;
            }

            public void setmDoorSensorStatus(String mDoorSensorStatus) {
                this.mDoorSensorStatus = mDoorSensorStatus;
            }

            public void setmRoomName(String mRoomName) {
                this.mRoomName = mRoomName;
            }

            public void setmCreatedDate(String mCreatedDate) {
                this.mCreatedDate = mCreatedDate;
            }

            public void setNotificationLists(NotificationList[] notificationLists) {
                this.notificationLists = notificationLists;
            }

            @SerializedName("door_sensor_id")
            private String mDoorSensorId;

            //    "is_lock_active": null,
                //        "is_door_active": 1,

                public String getIs_lock_active() {
                    return is_lock_active;
                }

                public void setIs_lock_active(String is_lock_active) {
                    this.is_lock_active = is_lock_active;
                }

                public String getIs_door_active() {
                    return is_door_active;
                }

                public void setIs_door_active(String is_door_active) {
                    this.is_door_active = is_door_active;
                }

                @SerializedName("is_lock_active")
                private String is_lock_active;

                @SerializedName("is_door_active")
                private String is_door_active;

            public int getIs_autolock_enable() {
                return is_autolock_enable;
            }

            public void setIs_autolock_enable(int is_autolock_enable) {
                this.is_autolock_enable = is_autolock_enable;
            }

            @SerializedName("is_autolock_enable")
            private int is_autolock_enable=1;

            public String getLock_data() {
                return lock_data;
            }

            public void setLock_data(String lock_data) {
                this.lock_data = lock_data;
            }

            @SerializedName("lock_data")
            private String lock_data;

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            @SerializedName("room_id")
            private String room_id;

            public String getDoor_subtype() {
                return door_subtype;
            }

            public void setDoor_subtype(String door_subtype) {
                this.door_subtype = door_subtype;
            }

            @SerializedName("door_subtype")
            private String door_subtype;

            public String getLock_id() {
                return lock_id;
            }

            public void setLock_id(String lock_id) {
                this.lock_id = lock_id;
            }

            @SerializedName("lock_id")
            private String lock_id;
            @SerializedName("door_sensor_module_id")
            private String mDoorSensorMoudleId;
            @SerializedName("door_sensor_name")
            private String mDoorSensorName;
            @SerializedName("door_sensor_voltage")
            private String mDoorSensorVoltage;
            @SerializedName("door_sensor_perc")
            private String mDoorSensorPerc;
            @SerializedName("is_unread")
            private String mIsUNread;
            @SerializedName("is_push_enable")
            private String mIsPushEnable;

            @SerializedName("door_sensor_status")
            private String mDoorSensorStatus;

            public String getDoor_lock_status() {
                return door_lock_status;
            }

            public void setDoor_lock_status(String door_lock_status) {
                this.door_lock_status = door_lock_status;
            }

            @SerializedName("door_lock_status")
            private String door_lock_status="";


            @SerializedName("room_name")
            private String mRoomName;
            @SerializedName("created_date")
            private String mCreatedDate;
            @SerializedName("notificationList")
            private NotificationList[] notificationLists;

            public UnreadLogs[] getUnreadLogs() {
                return unreadLogs;
            }

            public void setUnreadLogs(UnreadLogs[] unreadLogs) {
                this.unreadLogs = unreadLogs;
            }

            @SerializedName("unreadLogs")
            private UnreadLogs[] unreadLogs;

            public String getmDoorSensorId() {
                return mDoorSensorId;
            }

            public String getmDoorSensorMoudleId() {
                return mDoorSensorMoudleId;
            }

            public String getmDoorSensorName() {
                return mDoorSensorName;
            }

            public String getmDoorSensorVoltage() {
                return mDoorSensorVoltage;
            }

            public String getmDoorSensorPerc() {
                return mDoorSensorPerc;
            }

            public String getmIsUNread() {
                return mIsUNread;
            }

            public String getmIsPushEnable() {
                return mIsPushEnable;
            }

            public String getmDoorSensorStatus() {
                return mDoorSensorStatus;
            }

            public String getmRoomName() {
                return mRoomName;
            }

            public String getmCreatedDate() {
                return mCreatedDate;
            }

            public NotificationList[] getNotificationLists() {
                return notificationLists;
            }

            public static class NotificationList{

                @SerializedName("door_sensor_notification_id")
                private String mDoorSensorNotificationId;
                @SerializedName("start_datetime")
                private String mStartDateTime;
                @SerializedName("end_datetime")
                private String mEndDateTime;
                @SerializedName("is_active")
                private String mIsActive;

                public String getUser_id() {
                    return user_id;
                }

                public void setUser_id(String user_id) {
                    this.user_id = user_id;
                }

                @SerializedName("user_id")
                private String user_id;

                public String getmDoorSensorNotificationId() {
                    return mDoorSensorNotificationId;
                }

                public String getmStartDateTime() {
                    return mStartDateTime;
                }

                public String getmEndDateTime() {
                    return mEndDateTime;
                }

                public String getmIsActive() {
                    return mIsActive;
                }
            }

            public static class UnreadLogs implements Serializable{

                @SerializedName("id")
                @Expose
                private Integer id;
                @SerializedName("activity_action")
                @Expose
                private String activityAction;
                @SerializedName("activity_type")
                @Expose
                private String activityType;
                @SerializedName("activity_description")
                @Expose
                private String activityDescription;
                @SerializedName("activity_time")
                @Expose
                private String activityTime;
                @SerializedName("is_unread")
                @Expose
                private Integer isUnread;

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
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

                public String getActivityTime() {
                    return activityTime;
                }

                public void setActivityTime(String activityTime) {
                    this.activityTime = activityTime;
                }

                public Integer getIsUnread() {
                    return isUnread;
                }

                public void setIsUnread(Integer isUnread) {
                    this.isUnread = isUnread;
                }

            }
        }
    }

    public DATA getData() {
        return mData;
    }



    public int getCode() {
        return Integer.parseInt(mCode);
    }

    public String getMessage() {
        return mMessage;
    }
}
