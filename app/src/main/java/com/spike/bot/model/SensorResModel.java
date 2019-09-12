package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

        public void setTempLists(TempList[] tempLists) {
            this.tempLists = tempLists;
        }

        @SerializedName("tempSensorList")
        private TempList[] tempLists;

        public MultiSensorList[] getMultiSensorList() {
            return multiSensorList;
        }

        public void setMultiSensorList(MultiSensorList[] multiSensorList) {
            this.multiSensorList = multiSensorList;
        }

        @SerializedName("multiSensorList")
        private MultiSensorList[] multiSensorList;


        public static class MultiSensorList {

            @SerializedName("multi_sensor_id")
            @Expose
            private String multiSensorId;
            @SerializedName("multi_sensor_module_id")
            @Expose
            private String multiSensorModuleId;
            @SerializedName("multi_sensor_name")
            @Expose
            private String multiSensorName;
            @SerializedName("is_in_C")
            @Expose
            private Integer isInC;
            @SerializedName("is_push_enable")
            @Expose
            private Integer isPushEnable;
            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("temp_in_C")
            @Expose
            private String tempInC;
            @SerializedName("temp_in_F")
            @Expose
            private String tempInF;

            public int getGas_value() {
                return gas_value;
            }

            public void setGas_value(int gas_value) {
                this.gas_value = gas_value;
            }

            @SerializedName("gas_value")
            @Expose
            private int gas_value;

            public int getGas_threshold_value() {
                return gas_threshold_value;
            }

            public void setGas_threshold_value(int gas_threshold_value) {
                this.gas_threshold_value = gas_threshold_value;
            }

            @SerializedName("gas_threshold_value")
            @Expose
            private int gas_threshold_value;

            public String getHumidity() {
                return humidity;
            }

            public void setHumidity(String humidity) {
                this.humidity = humidity;
            }

            @SerializedName("humidity")
            @Expose
            private String humidity;
            @SerializedName("room_name")
            @Expose
            private String roomName;
            @SerializedName("created_date")
            @Expose
            private String createdDate;
            @SerializedName("tempNotificationList")
            @Expose
            private ArrayList<TempNotificationList> tempNotificationList = null;
            @SerializedName("tempAlertcount")
            @Expose
            private Integer tempAlertcount;
            @SerializedName("humidityNotificationList")
            @Expose
            private ArrayList<HumidityNotificationList> humidityNotificationList = null;
            @SerializedName("humidityAlertcount")
            @Expose
            private Integer humidityAlertcount;
            @SerializedName("unreadcount")
            @Expose
            private Integer unreadcount;
            @SerializedName("unreadLogs")
            @Expose
            private List<UnreadLog> unreadLogs = null;


            @SerializedName("NotificationSettingList")
            @Expose
            private List<NotificationSettingList> notificationSettingList = null;


            public List<NotificationSettingList> getNotificationSettingList() {
                return notificationSettingList;
            }

            public void setNotificationSettingList(List<NotificationSettingList> notificationSettingList) {
                this.notificationSettingList = notificationSettingList;
            }

            public String getMultiSensorId() {
                return multiSensorId;
            }

            public void setMultiSensorId(String multiSensorId) {
                this.multiSensorId = multiSensorId;
            }

            public String getMultiSensorModuleId() {
                return multiSensorModuleId;
            }

            public void setMultiSensorModuleId(String multiSensorModuleId) {
                this.multiSensorModuleId = multiSensorModuleId;
            }

            public String getMultiSensorName() {
                return multiSensorName;
            }

            public void setMultiSensorName(String multiSensorName) {
                this.multiSensorName = multiSensorName;
            }

            public Integer getIsInC() {
                return isInC;
            }

            public void setIsInC(Integer isInC) {
                this.isInC = isInC;
            }

            public Integer getIsPushEnable() {
                return isPushEnable;
            }

            public void setIsPushEnable(Integer isPushEnable) {
                this.isPushEnable = isPushEnable;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            public String getTempInC() {
                return tempInC;
            }

            public void setTempInC(String tempInC) {
                this.tempInC = tempInC;
            }

            public String getTempInF() {
                return tempInF;
            }

            public void setTempInF(String tempInF) {
                this.tempInF = tempInF;
            }


            public String getRoomName() {
                return roomName;
            }

            public void setRoomName(String roomName) {
                this.roomName = roomName;
            }

            public String getCreatedDate() {
                return createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public ArrayList<TempNotificationList> getTempNotificationList() {
                return tempNotificationList;
            }

            public void setTempNotificationList(ArrayList<TempNotificationList> tempNotificationList) {
                this.tempNotificationList = tempNotificationList;
            }

            public Integer getTempAlertcount() {
                return tempAlertcount;
            }

            public void setTempAlertcount(Integer tempAlertcount) {
                this.tempAlertcount = tempAlertcount;
            }

            public ArrayList<HumidityNotificationList> getHumidityNotificationList() {
                return humidityNotificationList;
            }

            public void setHumidityNotificationList(ArrayList<HumidityNotificationList> humidityNotificationList) {
                this.humidityNotificationList = humidityNotificationList;
            }

            public Integer getHumidityAlertcount() {
                return humidityAlertcount;
            }

            public void setHumidityAlertcount(Integer humidityAlertcount) {
                this.humidityAlertcount = humidityAlertcount;
            }

            public Integer getUnreadcount() {
                return unreadcount;
            }

            public void setUnreadcount(Integer unreadcount) {
                this.unreadcount = unreadcount;
            }

            public List<UnreadLog> getUnreadLogs() {
                return unreadLogs;
            }

            public void setUnreadLogs(List<UnreadLog> unreadLogs) {
                this.unreadLogs = unreadLogs;
            }



            public static class UnreadLog {

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

            public static class HumidityNotificationList {

                @SerializedName("temp_sensor_notification_id")
                @Expose
                private String multiSensorNotificationId;
                @SerializedName("min_humidity")
                @Expose
                private Integer minHumidity;
                @SerializedName("max_humidity")
                @Expose
                private Integer maxHumidity;
                @SerializedName("days")
                @Expose
                private String days;
                @SerializedName("is_active")
                @Expose
                private Integer isActive;
                @SerializedName("user_id")
                @Expose
                private String userId;

                public boolean isCFActive() {
                    return isCFActive;
                }

                public void setCFActive(boolean CFActive) {
                    isCFActive = CFActive;
                }

                private boolean isCFActive;

                public String getMultiSensorNotificationId() {
                    return multiSensorNotificationId;
                }

                public void setMultiSensorNotificationId(String multiSensorNotificationId) {
                    this.multiSensorNotificationId = multiSensorNotificationId;
                }

                public Integer getMinHumidity() {
                    return minHumidity;
                }

                public void setMinHumidity(Integer minHumidity) {
                    this.minHumidity = minHumidity;
                }

                public Integer getMaxHumidity() {
                    return maxHumidity;
                }

                public void setMaxHumidity(Integer maxHumidity) {
                    this.maxHumidity = maxHumidity;
                }

                public String getDays() {
                    return days;
                }

                public void setDays(String days) {
                    this.days = days;
                }

                public Integer getIsActive() {
                    return isActive;
                }

                public void setIsActive(Integer isActive) {
                    this.isActive = isActive;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

            }

            public static class TempNotificationList {

                @SerializedName("temp_sensor_notification_id")
                @Expose
                private String multiSensorNotificationId;
                @SerializedName("min_value_C")
                @Expose
                private String minValueC;
                @SerializedName("max_value_C")
                @Expose
                private String maxValueC;
                @SerializedName("min_value_F")
                @Expose
                private String minValueF;
                @SerializedName("max_value_F")
                @Expose
                private String maxValueF;
                @SerializedName("days")
                @Expose
                private String days;
                @SerializedName("is_active")
                @Expose
                private String isActive;
                @SerializedName("user_id")
                @Expose
                private String userId;

                public boolean isCFActive() {
                    return isCFActive;
                }

                public void setCFActive(boolean CFActive) {
                    isCFActive = CFActive;
                }

                private boolean isCFActive;

                public String getMultiSensorNotificationId() {
                    return multiSensorNotificationId;
                }

                public void setMultiSensorNotificationId(String multiSensorNotificationId) {
                    this.multiSensorNotificationId = multiSensorNotificationId;
                }

                public String getMinValueC() {
                    return minValueC;
                }

                public void setMinValueC(String minValueC) {
                    this.minValueC = minValueC;
                }

                public String getMaxValueC() {
                    return maxValueC;
                }

                public void setMaxValueC(String maxValueC) {
                    this.maxValueC = maxValueC;
                }

                public String getMinValueF() {
                    return minValueF;
                }

                public void setMinValueF(String minValueF) {
                    this.minValueF = minValueF;
                }

                public String getMaxValueF() {
                    return maxValueF;
                }

                public void setMaxValueF(String maxValueF) {
                    this.maxValueF = maxValueF;
                }

                public String getDays() {
                    return days;
                }

                public void setDays(String days) {
                    this.days = days;
                }

                public String getIsActive() {
                    return isActive;
                }

                public void setIsActive(String isActive) {
                    this.isActive = isActive;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }

            }
        }



            public static class TempList{

                public ArrayList<TempNotificationList> getTempNotificationList() {
                    return tempNotificationList;
                }

                public void setTempNotificationList(ArrayList<TempNotificationList> tempNotificationList) {
                    this.tempNotificationList = tempNotificationList;
                }

                @SerializedName("tempNotificationList")
                @Expose
                private ArrayList<TempNotificationList> tempNotificationList = null;


                public ArrayList<HumidityNotificationList> getHumidityNotificationList() {
                    return humidityNotificationList;
                }

                public void setHumidityNotificationList(ArrayList<HumidityNotificationList> humidityNotificationList) {
                    this.humidityNotificationList = humidityNotificationList;
                }

                @SerializedName("humidityNotificationList")
                @Expose
                public ArrayList<HumidityNotificationList> humidityNotificationList = null;

            @SerializedName("temp_sensor_id")
            private String mTempSensorId;

                public String getHumidity() {
                    return humidity;
                }

                public void setHumidity(String humidity) {
                    this.humidity = humidity;
                }

                @SerializedName("humidity")
            private String humidity;
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




                public static class TempNotificationList {

                    @SerializedName("temp_sensor_notification_id")
                    @Expose
                    private String multiSensorNotificationId;
                    @SerializedName("min_value_C")
                    @Expose
                    private String minValueC;
                    @SerializedName("max_value_C")
                    @Expose
                    private String maxValueC;
                    @SerializedName("min_value_F")
                    @Expose
                    private String minValueF;
                    @SerializedName("max_value_F")
                    @Expose
                    private String maxValueF;
                    @SerializedName("days")
                    @Expose
                    private String days;
                    @SerializedName("is_active")
                    @Expose
                    private String isActive;
                    @SerializedName("user_id")
                    @Expose
                    private String userId;

                    public boolean isCFActive() {
                        return isCFActive;
                    }

                    public void setCFActive(boolean CFActive) {
                        isCFActive = CFActive;
                    }

                    private boolean isCFActive;

                    public String getMultiSensorNotificationId() {
                        return multiSensorNotificationId;
                    }

                    public void setMultiSensorNotificationId(String multiSensorNotificationId) {
                        this.multiSensorNotificationId = multiSensorNotificationId;
                    }

                    public String getMinValueC() {
                        return minValueC;
                    }

                    public void setMinValueC(String minValueC) {
                        this.minValueC = minValueC;
                    }

                    public String getMaxValueC() {
                        return maxValueC;
                    }

                    public void setMaxValueC(String maxValueC) {
                        this.maxValueC = maxValueC;
                    }

                    public String getMinValueF() {
                        return minValueF;
                    }

                    public void setMinValueF(String minValueF) {
                        this.minValueF = minValueF;
                    }

                    public String getMaxValueF() {
                        return maxValueF;
                    }

                    public void setMaxValueF(String maxValueF) {
                        this.maxValueF = maxValueF;
                    }

                    public String getDays() {
                        return days;
                    }

                    public void setDays(String days) {
                        this.days = days;
                    }

                    public String getIsActive() {
                        return isActive;
                    }

                    public void setIsActive(String isActive) {
                        this.isActive = isActive;
                    }

                    public String getUserId() {
                        return userId;
                    }

                    public void setUserId(String userId) {
                        this.userId = userId;
                    }

                }


                public static class HumidityNotificationList {

                    @SerializedName("temp_sensor_notification_id")
                    @Expose
                    private String multiSensorNotificationId;
                    @SerializedName("min_humidity")
                    @Expose
                    private Integer minHumidity;
                    @SerializedName("max_humidity")
                    @Expose
                    private Integer maxHumidity;
                    @SerializedName("days")
                    @Expose
                    private String days;
                    @SerializedName("is_active")
                    @Expose
                    private Integer isActive;
                    @SerializedName("user_id")
                    @Expose
                    private String userId;

                    public boolean isCFActive() {
                        return isCFActive;
                    }

                    public void setCFActive(boolean CFActive) {
                        isCFActive = CFActive;
                    }

                    private boolean isCFActive;

                    public String getMultiSensorNotificationId() {
                        return multiSensorNotificationId;
                    }

                    public void setMultiSensorNotificationId(String multiSensorNotificationId) {
                        this.multiSensorNotificationId = multiSensorNotificationId;
                    }

                    public Integer getMinHumidity() {
                        return minHumidity;
                    }

                    public void setMinHumidity(Integer minHumidity) {
                        this.minHumidity = minHumidity;
                    }

                    public Integer getMaxHumidity() {
                        return maxHumidity;
                    }

                    public void setMaxHumidity(Integer maxHumidity) {
                        this.maxHumidity = maxHumidity;
                    }

                    public String getDays() {
                        return days;
                    }

                    public void setDays(String days) {
                        this.days = days;
                    }

                    public Integer getIsActive() {
                        return isActive;
                    }

                    public void setIsActive(Integer isActive) {
                        this.isActive = isActive;
                    }

                    public String getUserId() {
                        return userId;
                    }

                    public void setUserId(String userId) {
                        this.userId = userId;
                    }

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

                public String getUser_id() {
                    return user_id;
                }

                public void setUser_id(String user_id) {
                    this.user_id = user_id;
                }

                @SerializedName("user_id")
                private String user_id;
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
