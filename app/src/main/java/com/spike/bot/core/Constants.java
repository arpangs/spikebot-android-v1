package com.spike.bot.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.model.User;
import com.ttlock.bl.sdk.util.DigitUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Constants {

    //device type =3 - philip
    // device type = 2 = Ac

    //    public static  String CLOUD_SERVER_URL = "http://52.24.23.7:8079"; //222
//    public static  String CLOUD_SERVER_URL = "http://api.spikebot.io"; //222
    public static String CLOUD_SERVER_URL = "http://34.212.76.50:8079"; //wifi / 123
//    public static  String CLOUD_SERVER_URL = "http://52.24.23.7:8079"; //222
//    public static  String CLOUD_SERVER_URL = "http://api.spikebot.io"; //222

// *-   public static  String CLOUD_SERVER_URL = "http://52.201.70.116:8079"; // unuser
//    public static  String CLOUD_SERVER_URL = "http://54.201.70.116:8079"; // unuser
//    public static  String CLOUD_SERVER_URL = ""; //117 testing
//    public static  String IP_END = "111";  // vipul/123
//    public static  String  IP_END = "117"; // jhanvi / 123
//    public static  String  IP_END = "118"; // bhumi / 123

    public static String IP_END = "222";
    public static final String CAMERA_DEEP = "rtmp://home.deepfoods.net";
    public static final String CAMERA_PATH = "/static/storage/volume/pi/";

    public static String startUrl = "http://home.d";

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
    public static final String lock_exe = "lock_exe";
    public static final String lock_token = "lock_token";
    public static int lockDate = 0;
    public static String socketIp = "";
    public static int socketType = 0;
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
    public static final String GET_DEVICES_LIST = "/device/list";
    public static final String getMoodName = "/mood/names/list";
    public static final String getChildUsers = "/getChildUsers";
    public static final String DeleteChildUser = "/DeleteChildUser";
    public static final String getRoomCameraList = "/getRoomCameraList";
    public static final String AddChildUser = "/AddChildUser";
    public static final String updateChildUser = "/updateChildUser";
    public static final String ADD_CUSTOME_ROOM = "/rooms/add";
 //   public static final String SAVE_ROOM_AND_PANEL_NAME = "/saveRoomAndPanelName";
    //    public static final String CONFIGURE_NEWROOM = "/configureNewRoom";
 //   public static final String ADD_CUSTOME_ROOM = "/addCustomRoom";
    public static final String SAVE_ROOM_AND_PANEL_NAME = "/rooms/edit";
//    public static final String CONFIGURE_NEWROOM = "/configureNewRoom";
    public static final String GET_EDIT_ROOM_INFO = "/getEditRoomInfo";
    public static final String CONFIGURE_DEVICE_REQUEST = "/configureDeviceRequest";
    public static final String configuresmartRemoteRequest = "/configuresmartRemoteRequest";
    public static final String getSmartRemoteList = "/getSmartRemoteList";
    public static final String assignNumberToMood = "/assignNumberToMood";
    public static final String deleteSmartRemote = "/deleteSmartRemote";
    public static final String DELETE_ROOM = "/rooms/delete";
    public static final String DELETE_ROOM_PANEL = "/panel/delete";
    public static final String GET_MOOD_DETAILS = "/getMoodDetails";
    public static final String validatecamerakey = "/validatecamerakey";
    public static final String addHueLight = "/addHueLight";
    public static final String getHueRouterDetails = "/getHueRouterDetails";
    public static final String HueLightsList = "/PhilipsHueLightsList";
    public static final String HueLightState = "/HueLightState";
    public static final String createHueUSer = "/createHueUSer";
    public static final String addHueBridge = "/addHueBridge";
    public static final String searchBridges = "/searchBridges";

    //added : 3-10-2018
    public static final String GET_ALL_UNASSIGNED_DEVICES = "/getAllUnassignedDevices";
    public static final String ADD_UN_CONFIGURED_DEVICE = "/addUnconfiguredDevice";

    //door sensor
    public static final String CONFIGURE_DOOR_SENSOR_REQUEST = "/configureDoorSensorRequest";
    public static final String ADD_DOOR_SENSOR = "/addDoorSensor"; // is_new=1 means new panel add & is_new=0 means exting panel add
    public static final String addMultiSensor = "/addMultiSensor";
    public static final String addGasSensor = "/addGasSensor";
    public static final String addRepeator = "/addRepeator";
    public static final String updateGasSensor = "/updateGasSensor";
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
    public static final String getSmartDeviceType = "/getSmartDeviceType";
    public static final String getHeavyLoadDetails = "/getHeavyLoadDetails";
    public static final String filterHeavyLoadData = "/filterHeavyLoadData";
    public static final String getHueBridgeList = "/getHueBridgeList";
    public static final String getSpikebotHueLightList = "/getSpikebotHueLightList";
    public static final String changeLockSensorAutoLockStatus = "/changeLockSensorAutoLockStatus";
    public static final String deleteDoorLock = "/deleteDoorLock";
    public static final String addLockBridge = "/addLockBridge";
    public static final String deleteTTLockBridge = "/deleteTTLockBridge";
    public static final String updateTTLockActiveStatus = "/updateTTLockActiveStatus";
    public static final String curtainadd = "device/add";
    public static final String deviceadd = "/device/add";
    public static final String curtainupdate = "/curtain/update";
    public static final String curtaindelete = "/curtain/delete";
    public static final String curtainupdatestatus = "/curtain/update-status";

    public static final String SENSOR_ROOM_DETAILS = "/sensorRoomDetails";
    public static final String SENSOR_NOTIFICATION = "/sensorNotification";
    public static final String GET_UNASSIGNED_SENSORS = "/getUnassignedSensors";
    public static final String SAVE_UNCONFIGURED_SENSOR = "/saveUnconfiguredSensor";
    public static final String UPDATE_UNREAD_LOGS = "/updateUnReadLogs";
    public static final String SAVE_EDIT_SWITCH = "/saveEditSwitch";
    public static final String configureGasSensorRequest = "/configureGasSensorRequest";
    public static final String getRepeatorLists = "/getRepeatorLists";
    public static final String configureRepeatorRequest = "/configureRepeatorRequest";
    public static final String getGasSensorInfo = "/getGasSensorInfo";
    public static final String deleteGasSensor = "/deleteGasSensor";
    public static final String updateRepeator = "/updateRepeator";
    public static final String deleteRepeator = "/deleteRepeator";
    public static final String reassignRepeater = "/reassignRepeater";
    public static final String getUnassignedRepeaterList = "/getUnassignedRepeaterList";
    public static final String curtainconfigure = "/curtain/configure";
    public static final String deviceconfigure = "/device/configure/";


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
    public static final String reportFalseImage = "/reportFalseImage";
    public static final String changeTempSensorNotificationStatus = "/changeTempSensorNotificationStatus";

    //IR Blaster
    public static final String CONFIGURE_IR_BLASTER_REQUEST = "/configureIRBlasterRequest";
    public static final String ADD_IR_BLASTER = "/AddIRBlaster";
    public static final String GET_IR_BLASTER_INFO = "/getIRBlasterInfo";
    public static final String getIRDeviceTypeBrands = "/getIRDeviceTypeBrands";
    public static final String SEND_REMOTE_COMMAND = "/sendRemoteCommand";
    public static final String GET_REMOTE_SCHEDULE_LIST = "/getRemoteScheduleList";
    public static final String ADD_REMOTE_SCHEDULE = "/addRemoteSchedule";
    public static final String UPDATE_REMOTE_SCHEDULE = "/updateRemoteSchedule";
    public static final String DELETE_REMOTE_SCHEDULE = "/deleteRemoteSchedule";
    public static final String CHANGE_REMOTE_SCHEDULE_STATUS = "/changeRemoteScheduleStatus";
    public static final String getRoomList = "/getRoomList";
    public static final String getDeviceBrandRemoteList = "/getDeviceBrandRemoteList";
    public static final String changeHueLightState = "/changeHueLightState";
    public static final String getPhilipsHueParams = "/getPhilipsHueParams";


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
    public static final String deletePhilipsHue = "/deletePhilipsHue";

    //panel
    public static final String ADD_CUSTOM_PANEL = "/addCustomPanel";
    public static final String CHANGE_ROOM_PANELMOOD_STATUS_NEW = "/changeRoomPanelMoodStatus";
    public static final String CONFIGURE_NEW_PANEL = "/configureNewPanel";

    //devices
    public static final String CHANGE_DEVICE_STATUS = "/changeDeviceStatus";
    public static final String ADD_CUSTOME_DEVICE = "/addCustomDevice";
    public static final String CHANGE_FAN_SPEED = "/changeFanSpeed";
    public static final String DELETE_INDIVIDUAL_DEVICE = "/deleteIndividualDevice";
    public static final String GET_FAN_SPEED = "/getFanSpeed";
    public static final String CHECK_INDIVIDUAL_SWITCH_DETAILS = "/checkIndividualSwitchDetails";

    //mood
    public static final String ADD_NEW_MOOD_NEW = "/mood/add";
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

    //camera
    public static final String ADD_CAMERA = "/addCamera";
    public static final String SAVE_EDIT_CAMERA = "/updateCamera";
    public static final String GET_CAMERA_INFO = "/editCamera";
    public static final String DELETE_CAMERA = "/deleteCamera";
    public static final String GET_CAMERA_RECORDING_BY_DATE = "/getCameraRecordingByDate";

    //others
    public static final String getScheduleNotification = "/getScheduleNotification";
    public static final String GET_FILTER_NOTIFICATION_INFO = "/filterNotificationInfo";
    public static final String GET_NOTIFICATION_LIST = "/getNotificationList";
    public static final String SAVE_NOTIFICATION_LIST = "/saveNotificationList";
    public static final String roomSensorUnreadLogs = "/roomSensorUnreadLogs";
    public static final String roomLogs = "/roomLogs";
    public static final String updateBadgeCount = "/updateBadgeCount";
    public static final String getCameraToken = "/getCameraToken/";
    public static final String getAllCameraToken = "/getAllCameraToken";
    public static final String getLocalMacAddress = "/getLocalMacAddress";
    public static final String addTTLock = "/addTTLock";
    public static final String getLockLists = "/getLockLists";
    public static final String deleteTTLock = "/deleteTTLock";

    /*----------------------------------------------------------------------*/

    public static final String DEVICE_TOKEN = "sTZka4A72j";
    public static final String ANDROID = "android";

    //lock
    public static final String client_id = "439063e312444f1f85050a52efcecd2e";
    public static final String client_secret = "0ef1c49b70c02ae6314bde603d4e9b05";
    //    public static  String access_token = "fac1734b6209dd5b3ea602c9cc7a15ae";
    public static String access_token = "a74549ab15d07ecd988e26f50985aee7";
    public static final String refresh_token = "5ca1a4bc670b16b571b1488a631e57fc";
    public static final String lock_user_id_vip = "1769341";
    public static final String lock_open_vip = "1930389027";
    public static final String locK_base_uri = "http://open.ttlock.com.cn";
    public static final String locK_add = "/v3/lock/initialize";
    public static final String lockUserName = "ttchatcrash@gmail.com";
    public static final String lockPassword = "ttchat$123";
    public static final String startUrlhttp = "http:";
    public static final int lockUserId = 1941573;


    /*-----------ununsed api-----------*/
    public static boolean isWifiConnect = false;
    public static boolean isWifiConnectSave = false;

    public static Activity activityWifi;

    public static Activity activityPhilipsHueBridgeDeviceListActivity;
    public static Activity activityAddDeviceConfirmActivity;
    public static Activity activitySearchHueBridgeActivity;

    public static String getUserName(Context context) {
        String name = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        name = userList.get(i).getFirstname();
                        break;
                    }
                }
            }
        }

        return name;
    }

    public static String getuserIp(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getLocal_ip();
//                        getuserIp="192.168.175.222";
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }

    public static User getuser(Context context) {
        User user = new User();
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        user = userList.get(i);
                        break;
                    }
                }
            }
        }

        return user;
    }

    public static String getUserId(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getUser_id();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }

    public static String getuserCloudIP(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getCloudIP();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }

    public static String getGateway(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getGateway_ip();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }


    public static String getCouldIp(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getCloudIP();
                        break;
                    }
                }
            }
        }

        return getuserIp;
    }


    public static String getMacAddress(Context context) {
        String getuserIp = "";
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getIsActive()) {
                        getuserIp = userList.get(i).getMac_address();
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

    public static boolean checkLoginAccountCount(Context context) {
        boolean isFlag = false;
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);
            if (userList.size() > 0) {
                isFlag = true;
            } else {
                isFlag = false;
            }
        } else {
            isFlag = false;
        }

        return isFlag;
    }


    public static Bitmap takescreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View view, RecyclerView recyclerView) {

//        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
//        view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
//
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache(true);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//        view.setDrawingCacheEnabled(false);

        // create bitmap screen capture
        View v1 = view;
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//        v1.setDrawingCacheEnabled(false);


        return bitmap;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        month = getMonthOfDay(month, year);
        return month;
    }

    public static ArrayList<String> getMonthList() {
        ArrayList<String> xAxis = new ArrayList<>();
//        xAxis.add("JAN");
//        xAxis.add("FEB");
//        xAxis.add("MAR");
//        xAxis.add("APR");
//        xAxis.add("MAY");
//        xAxis.add("JUN");

        xAxis.add("All");
        xAxis.add("Jan");
        xAxis.add("Feb");
        xAxis.add("Mar");
        xAxis.add("Apr");
        xAxis.add("May");
        xAxis.add("Jun");
        xAxis.add("Jul");
        xAxis.add("Aug");
        xAxis.add("Sep");
        xAxis.add("Oct");
        xAxis.add("Nov");
        xAxis.add("Dec");
        return xAxis;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getMonthOfDay(int month, int year) {
        //January - 31 days 1
        //February - 28 days 2
        //March - 31 days 3
        //April - 30 days 4
        //May - 31 days 5
        //June - 30 days 6
        //July - 31 days 7
        //August - 31 days 8
        //September - 30 days 9
        //October - 31 days 10
        //November - 30 days 11
        //December - 31 days 12

        Calendar calendar = Calendar.getInstance();
        int date = 1;
        calendar.set(year, month, date);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return days;
    }

    public static String getMillsTimeFormat(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getpasswordLock() {
        return DigitUtil.getMD5("vg99092vg");
    }

    public static void startUrlset() {
        if (!ChatApplication.url.startsWith("http")) {
            ChatApplication.url = "http://" + ChatApplication.url;
        }

    }

    public static int twoDateDiff(String enddate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
//        String dateBeforeString = "31 01 2014";
//        String dateAfterString = "02 02 2014";
        int daysBetween = 0;
        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String startdate = df.format(c);

            Date dateBefore = myFormat.parse(startdate);
            Date dateAfter = myFormat.parse(enddate);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (int) (difference / (1000 * 60 * 60 * 24));
            /* You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println("Number of Days between dates: " + daysBetween);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysBetween;
    }

    public static InetAddress getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        InetAddress address = null;
        try {
            address = InetAddress.getByName(String.format(Locale.ENGLISH,
                    "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }


}
