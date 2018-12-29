package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 8/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorResModel {

    @SerializedName("code")
    private String mCode;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("data")
    private DATA mDate;

    public static  class DATA{

        @SerializedName("tempList")
        private TempList[] tempLists;


        public static class TempList{
            @SerializedName("temp_sensor_id")
            private String mTempSensorId;
            @SerializedName("temp_sensor_module_id")
            private String mTempSensorMoudleId;
            @SerializedName("temp_sensor_name")
            private String mTempSensorName;
            @SerializedName("temp_sensor_voltage")
            private String mTempSensorVoltage;
            @SerializedName("temp_sensor_perc")
            private String mTempSensorPerc;
            @SerializedName("is_in_C")
            private String mIsInC;
            @SerializedName("is_push_enable")
            private String mIsPushEnable;
            @SerializedName("room_id")
            private String mRoomId;
            @SerializedName("temp_in_C")
            private String mTempInC;
            @SerializedName("temp_in_F")
            private String mTempInF;
            @SerializedName("room_name")
            private String mRoomName;
            @SerializedName("created_date")
            private String mCreatedDate;
            @SerializedName("is_unread")
            private String mIsUnread;
            @SerializedName("notificationList")
            private NotificationList[] notificationLists;
            private Integer unreadcount;
            @SerializedName("unreadLogs")
            @Expose
            private List<UnreadLog> unreadLogs = null;
            public List<UnreadLog> getUnreadLogs() {
                return unreadLogs;
            }

            public void setUnreadLogs(List<UnreadLog> unreadLogs) {
                this.unreadLogs = unreadLogs;
            }

            public String getTempSensorPerc() {
                return mTempSensorPerc;
            }

            public String getTempSensorId() {
                return mTempSensorId;
            }

            public String getTempSensorMoudleId() {
                return mTempSensorMoudleId;
            }

            public String getTempSensorName() {
                return mTempSensorName;
            }

            public String getTempSensorVoltage() {
                return mTempSensorVoltage;
            }

            public String getIsInC() {
                return mIsInC;
            }

            public String getIsPushEnable() {
                return mIsPushEnable;
            }

            public String getRoomId() {
                return mRoomId;
            }

            public String getTempInC() {
                return mTempInC;
            }

            public String getTempInF() {
                return mTempInF;
            }

            public String getRoomName() {
                return mRoomName;
            }

            public String getCreatedDate() {
                return mCreatedDate;
            }

            public String getIsUnread() {
                return mIsUnread;
            }
            public void setInUnread(String unread){
                this.mIsUnread = unread;
            }

            public NotificationList[] getNotificationLists() {
                return notificationLists;
            }

            public static class NotificationList{

                @SerializedName("temp_sensor_notification_id")
                private String mTempSensorNotificationId;
                @SerializedName("min_value_C")
                private String mMinValueC;
                @SerializedName("max_value_C")
                private String mMaxValueC;
                @SerializedName("min_value_F")
                private String mMinValueF;
                @SerializedName("max_value_F")
                private String mMaxValueF;
                @SerializedName("days")
                private String mDays;
                @SerializedName("is_active")
                private String mIsActive;
                private boolean isCFActive;

                public boolean isCFActive() {
                    return isCFActive;
                }

                public void setCFActive(boolean CFActive) {
                    isCFActive = CFActive;
                }

                public String getTempSensorNotificationId() {
                    return mTempSensorNotificationId;
                }

                public String getMinValueC() {
                    return mMinValueC;
                }

                public String getMaxValueC() {
                    return mMaxValueC;
                }

                public String getMinValueF() {
                    return mMinValueF;
                }

                public String getMaxValueF() {
                    return mMaxValueF;
                }

                public String getDays() {
                    return mDays;
                }

                public String getIsActive() {
                    return mIsActive;
                }

                public void setmTempSensorNotificationId(String mTempSensorNotificationId) {
                    this.mTempSensorNotificationId = mTempSensorNotificationId;
                }

                public void setmMinValueC(String mMinValueC) {
                    this.mMinValueC = mMinValueC;
                }

                public void setmMaxValueC(String mMaxValueC) {
                    this.mMaxValueC = mMaxValueC;
                }

                public void setmMinValueF(String mMinValueF) {
                    this.mMinValueF = mMinValueF;
                }

                public void setmMaxValueF(String mMaxValueF) {
                    this.mMaxValueF = mMaxValueF;
                }

                public void setmDays(String mDays) {
                    this.mDays = mDays;
                }

                public void setmIsActive(String mIsActive) {
                    this.mIsActive = mIsActive;
                }
            }


            public class UnreadLog {

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
        public TempList[] getTempLists() {
            return tempLists;
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
