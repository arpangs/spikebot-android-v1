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
    private DATA mDate;

    public static  class DATA{

        @SerializedName("doorList")
        private DoorList[] doorLists;

        public DoorList[] getDoorLists() {
            return doorLists;
        }

        public static class DoorList {
            @SerializedName("door_sensor_id")
            private String mDoorSensorId;

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            @SerializedName("room_id")
            private String room_id;
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

    public DATA getDate() {
        return mDate;
    }



    public int getCode() {
        return Integer.parseInt(mCode);
    }

    public String getMessage() {
        return mMessage;
    }
}
