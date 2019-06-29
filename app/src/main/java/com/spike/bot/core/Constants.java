package com.spike.bot.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class Constants {

//    public static  String CLOUD_SERVER_URL = "http://52.24.23.7:8079"; //222
    public static  String CLOUD_SERVER_URL = "http://34.212.76.50:8079"; //119 testing
//    public static  String CLOUD_SERVER_URL = "http://18.237.74.22:8079"; // unuser
//    public static  String CLOUD_SERVER_URL = ""; //117 testing
//http://52.24.23.7:8079
//http://52.24.23.7:7
//    public static  String IP_END = "111"; //101 //117 //222 node11 / 123
    public static  String  IP_END = "119"; //101 //117 //222
//
//    public  static  String  IP_END = "222"; //101 //117 //222
//    public static  String  IP_END = ""; //101 //117 //222
//    public static final String  IP_END = "117"; //101 //117 //222 vip/123
    public static final String CAMERA_DEEP = "rtmp://home.deepfoods.net";
    public static final String CAMERA_PATH = "/static/storage/volume/";


    public static final int ACK_TIME_OUT = 5000;
    public static final int REMOTE_REQUEST_CODE = 10;

    public static final float CAMERA_NUMBER = (float) 4.5;
    public static final int SWITCH_NUMBER = 4;
    public static final int SWITCH_NUMBER_EXIST_PANEL = 5;
    public static final String PREF_CLOUDLOGIN = "cloudLogin";
    public static final String PREF_IP = "ip";
    public static final String USER_ID = "user_id";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_TYPE = "user_type";
    public static final String USER_ADMIN_TYPE = "user_admin_type";
    public static final String USER_ROOM_TYPE = "user_room_type";
    public static final String couldIp = "";
    public static final String startIp = "";
    public static int adminType = 1;
    public static int room_type = 0;

    public static final String DEVICE_PUSH_TOKEN = "device_push_token";

    public static final boolean isTesting = false;

    public static final String SIMPLE_DATE_FORMAT_1 = "MMM dd, yyyy";
    public static final String SIMPLE_DATE_FORMAT_2 = "MMM dd, yyyy hh:mm";
    public static final String SIMPLE_DATE_FORMAT_3 = "MMM dd, yyyy HH:mm a";
    public static final String SIMPLE_DATE_FORMAT_4 = "MMM dd, yyyy hh:mm aa";  //MMM dd, yyyy hh:mm aa
    public static final String LOG_DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String LOG_DATE_FORMAT_2 = "dd-MMM yyyy h:mm a";

    /*---------------------------Add new api v2-----------------------------*/

    public static final String APP_LOGIN = "/applogin";
    public static final String APP_LOGOUT = "/applogout";
    public static final String SIGNUP_API = "/signupdetails";
    public static final String wifilogin = "/wifilogin";

    //room
    public static final String GET_DEVICES_LIST = "/getDevicesList";  //r
    public static final String getChildUsers = "/getChildUsers";  //r
    public static final String DeleteChildUser = "/DeleteChildUser";  //r
    public static final String getRoomCameraList = "/getRoomCameraList";  //r
    public static final String AddChildUser = "/AddChildUser";  //r
    public static final String updateChildUser = "/updateChildUser";  //r
    public static final String ADD_CUSTOME_ROOM = "/addCustomRoom";
    public static final String SAVE_ROOM_AND_PANEL_NAME = "/saveRoomAndPanelName";
//    public static final String CONFIGURE_NEWROOM = "/configureNewRoom";
    public static final String GET_EDIT_ROOM_INFO = "/getEditRoomInfo";
    public static final String CONFIGURE_DEVICE_REQUEST = "/configureDeviceRequest";
    public static final String configuresmartRemoteRequest = "/configuresmartRemoteRequest";
    public static final String getSmartRemoteList = "/getSmartRemoteList";
    public static final String assignNumberToMood = "/assignNumberToMood";
    public static final String deleteSmartRemote = "/deleteSmartRemote";
    public static final String DELETE_ROOM = "/deleteRoom";
    public static final String DELETE_ROOM_PANEL = "/deletePanel";
    public static final String GET_MOOD_DETAILS = "/getMoodDetails";
    public static final String validatecamerakey = "/validatecamerakey";
    public static final String addHueLight = "/addHueLight";
    public static final String getHueRouterDetails = "/getHueRouterDetails";
    public static final String HueLightsList = "/HueLightsList";
    public static final String HueLightState = "/HueLightState";
    public static final String createHueUSer = "/createHueUSer";

    //added : 3-10-2018
    public static final String GET_ALL_UNASSIGNED_DEVICES = "/getAllUnassignedDevices";
    public static final String GET_ALL_GETORIGINALDEVICES = "/getOriginalDevices/0";
    public static final String ADD_UN_CONFIGURED_DEVICE = "/addUnconfiguredDevice";

    //door sensor
    public static final String CONFIGURE_DOOR_SENSOR_REQUEST = "/configureDoorSensorRequest";
    public static final String ADD_DOOR_SENSOR = "/addDoorSensor";
    public static final String addMultiSensor = "/addMultiSensor";
    public static final String addSmartRemote = "/addSmartRemote";
    public static final String GET_DOOR_SENSOR_INFO = "/getDoorSensorInfo";
    public static final String ADD_DOOR_SENSOR_NOTIFICATION = "/addDoorSensorNotification";
    public static final String UPDATE_DOOR_SENSOR_NOTIFICATION = "/updateDoorSensorNotification";
    public static final String DELETE_DOOR_SENSOR_NOTIFICATION = "/deleteDoorSensorNotification";
    public static final String CHANGE_DOOR_SENSOR_NOTIFICATION_STATUS = "/changeDoorSensorNotificationStatus";
    public static final String CHANGE_DOOR_SENSOR_STATUS = "/changeDoorSensorStatus";
    public static final String DELETE_DOOR_SENSOR = "/deleteDoorSensor";
    public static final String UPDATE_DOOR_SENSOR = "/updateDoorSensor";
    public static final String getCameraNotificationAlertList = "/getCameraNotificationAlertList";
    public static final String updateUnReadCameraLogs = "/updateUnReadCameraLogs";
    public static final String getSmartDeviceBrands = "/getSmartDeviceBrands";

    public static final String SENSOR_ROOM_DETAILS = "/sensorRoomDetails";
    public static final String SENSOR_NOTIFICATION = "/sensorNotification";
    public static final String GET_UNASSIGNED_SENSORS = "/getUnassignedSensors";
    public static final String SAVE_UNCONFIGURED_SENSOR = "/saveUnconfiguredSensor";
    public static final String UPDATE_UNREAD_LOGS = "/updateUnReadLogs";

    //temp sensor
    public static final String GET_TEMP_SENSOR_INFO = "/getTempSensorInfo";
    public static final String getMultiSensorInfo = "/getMultiSensorInfo";
    public static final String CONFIGURE_TEMP_SENSOR_REQUEST = "/configureTempSensorRequest";
    public static final String configureMultiSensorRequest = "/configureMultiSensorRequest";
    public static final String ADD_TEMP_SENSOR = "/addTempSensor";
    public static final String ADD_TEMP_SENSOR_NOTIFICATION = "/addTempSensorNotification";
    public static final String addMultiSensorNotification = "/addMultiSensorNotification";
    public static final String UPDATE_TEMP_SENSOR_NOTIFICATION = "/updateTempSensorNotification";
    public static final String updateMultiSensorNotification = "/updateMultiSensorNotification";
    public static final String DELETE_TEMP_SENSOR_NOTIFICATION = "/deleteTempSensorNotification";
    public static final String deleteMultiSensorNotification = "/deleteMultiSensorNotification";
    public static final String CHANGE_TEMP_SENSOR_STATUS = "/changeTempSensorStatus";
    public static final String changeMultiSensorStatus = "/changeMultiSensorStatus";
    public static final String CHANGE_TEMP_SENSOR_NOTIFICATION_STATUS = "/changeTempSensorNotificationStatus";
    public static final String changeMultiSensorNotificationStatus = "/changeMultiSensorNotificationStatus";
    public static final String UPDATE_TEMP_SENSOR = "/updateTempSensor";
    public static final String updateMultiSensor = "/updateMultiSensor";
    public static final String DELETE_TEMP_SENSOR = "/deleteTempSensor";
    public static final String deleteMultiSensor = "/deleteMultiSensor";
    public static final String getCameraLogs = "/getCameraLogs";

    //IR Blaster
    public static final String CONFIGURE_IR_BLASTER_REQUEST = "/configureIRBlasterRequest";
    public static final String ADD_IR_BLASTER = "/AddIRBlaster";
    public static final String GET_IR_BLASTER_INFO = "/getIRBlasterInfo";
    public static final String GET_IR_DEVICE_DETAILS = "/getIRDeviceDetails";
    public static final String getIRDeviceTypeBrands = "/getIRDeviceTypeBrands";
    public static final String SEND_REMOTE_COMMAND = "/sendRemoteCommand";
    public static final String GET_REMOTE_SCHEDULE_LIST = "/getRemoteScheduleList";
    public static final String ADD_REMOTE_SCHEDULE = "/addRemoteSchedule";
    public static final String UPDATE_REMOTE_SCHEDULE = "/updateRemoteSchedule";
    public static final String DELETE_REMOTE_SCHEDULE = "/deleteRemoteSchedule";
    public static final String CHANGE_REMOTE_SCHEDULE_STATUS = "/changeRemoteScheduleStatus";
    public static final String getRoomList = "/getRoomList";
    public static final String getDeviceBrandRemoteList = "/getDeviceBrandRemoteList";


    //new api for IR Blaster
    public static final String GET_REMOTE_INFO = "/getRemoteInfo";
    public static final String GET_IR_BLASTER_LIST = "/getIRBlasterList";
    public static final String UPDATE_IR_BLASTER = "/UpdateIRBlaster";
    public static final String DELETE_IR_BLASTER = "/deleteIRBlaster";
    public static final String SEND_ON_OFF_COMMAND = "/sendOnOffCommand";
    public static final String ADD_REMOTE = "/AddRemote";
    public static final String GET_IR_DEVICE_TYPE_LIST = "/getIRDeviceTypeList";
    public static final String UPDATE_REMOTE_DETAILS = "/updateRemoteDetails";
    public static final String DELETE_REMOTE = "/deleteRemote";

    //panel
    public static final String ADD_CUSTOM_PANEL = "/addCustomPanel";
    public static final String CHANGE_ROOM_PANELMOOD_STATUS_NEW = "/changeRoomPanelMoodStatus";
    public static final String CONFIGURE_NEW_PANEL = "/configureNewPanel";
    // public static final String CHANGE_ROOM_PANEL_STATUS = "/changeRoomPanelStatus";

    //devices
    public static final String CHANGE_DEVICE_STATUS = "/changeDeviceStatus";
    public static final String ADD_CUSTOME_DEVICE = "/addCustomDevice";
    public static final String CHANGE_FAN_SPEED = "/changeFanSpeed";
    public static final String DELETE_INDIVIDUAL_DEVICE = "/deleteIndividualDevice";
    public static final String SAVE_EDIT_SWITCH = "/saveEditSwitch";
    public static final String GET_FAN_SPEED = "/getFanSpeed";
    public static final String CHECK_INDIVIDUAL_SWITCH_DETAILS = "/checkIndividualSwitchDetails";

    //mood
    public static final String ADD_NEW_MOOD_NEW = "/addMood";
    public static final String SAVEEDITMOOD = "/updateMood";  //saveEditMood
    public static final String DELETE_MOOD = "/deleteMood";
    public static final String GET_MOOD_DEVICE_DETAILS = "/getMoodDeviceDetails";

    //schedule
    public static final String ADD_NEW_SCHEDULE = "/addSchedule";
    public static final String UPDATE_SCHEDULE = "/updateSchedule";
    public static final String CHANGE_SCHEDULE_STATUS = "/changeScheduleStatus";
    public static final String DELETE_SCHEDULE = "/deleteSchedule";
    public static final String GET_SCHEDULE_LIST = "/getScheduleList";
    public static final String GET_SCHEDULE_LIST_LOG = "/getScheduleListForLogs";
    public static final String GET_SCHEDULE_ON_ROOM = "/getScheduleOnRoom";
    public static final String GET_ORIGINAL_DEVICES = "/getOriginalDevices";
    public static final String addCameraNotification = "/addCameraNotification";
    public static final String deleteCameraNotification = "/deleteCameraNotification";
    public static final String updateCameraNotification = "/updateCameraNotification";
    public static final String changeCameraAlertStatus = "/changeCameraAlertStatus";

    //profile
    public static final String GET_USER_PROFILE_INFO = "/getuserProfileInfo";
    public static final String SAVE_USER_PROFILE_DETAILS = "/saveUserProfileDetails";
    //public static final String SIGN_UP_DETAILS = "/signupdetails";

    //camera
    public static final String ADD_CAMERA = "/addCamera";
    public static final String SAVE_EDIT_CAMERA = "/updateCamera";
    public static final String GET_CAMERA_INFO = "/editCamera";
    public static final String DELETE_CAMERA = "/deleteCamera";
    public static final String GET_CAMERA_RECORDING_BY_DATE = "/getCameraRecordingByDate";

    //others

    //  public static final String GET_CAMERA_RECORD_BY_DATE = "/getCameraRecordingByDate";
    //  public static final String SHOW_CAMERA_RECORDING = "/showCameraRecording";

    //notifications
   // public static final String GET_NOTIFICATION_INFO = "/getNotificationInfo";
    public static final String getScheduleNotification = "/getScheduleNotification";
    public static final String GET_FILTER_NOTIFICATION_INFO = "/filterNotificationInfo";
    public static final String GET_NOTIFICATION_LIST = "/getNotificationList";
    public static final String SAVE_NOTIFICATION_LIST = "/saveNotificationList";
    public static final String roomSensorUnreadLogs = "/roomSensorUnreadLogs";
    public static final String roomLogs = "/roomLogs";
    public static final String updateBadgeCount = "/updateBadgeCount";
    public static final String getCameraToken = "/getCameraToken/";
    public static final String getAllCameraToken = "/getAllCameraToken";

    /*----------------------------------------------------------------------*/

    public static final String DEVICE_TOKEN = "sTZka4A72j";
    public static final String ANDROID = "android";

    /*-----------ununsed api-----------*/
    public static boolean isWifiConnect = false;
    public static boolean isWifiConnectSave = false;
    public static Activity activityWifi;



    public static String getUserName(Context context){
        String name="";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if(userList.size()>0){
                for(int i=0; i<userList.size(); i++){
                    if(userList.get(i).getIsActive()){
                        name=userList.get(i).getFirstname();
                        break;
                    }
                }
            }
        }

        return name;
    }

    public static String getuserIp(Context context){
        String getuserIp="";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if(userList.size()>0){
                for(int i=0; i<userList.size(); i++){
                    if(userList.get(i).getIsActive()){
                        getuserIp=userList.get(i).getLocal_ip();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }


    public static String getuserCloudIP(Context context){
        String getuserIp="";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if(userList.size()>0){
                for(int i=0; i<userList.size(); i++){
                    if(userList.get(i).getIsActive()){
                        getuserIp=userList.get(i).getCloudIP();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }


    // ignore enter First space on edittext
    public static InputFilter ignoreFirstWhiteSpace() {
        return new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        if (dstart == 0)
                            return "";
                    }
                }
                return null;
            }
        };
    }

    public static Bitmap takescreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View view, RecyclerView recyclerView) {

        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);


        return bitmap;

    }
}
