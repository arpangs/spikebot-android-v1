package com.spike.bot.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.model.User;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Constants {

    //device type =3 - philip
    // device type = 2 = Ac


    public static final String LIVE = "LIVE-";     // live for notification subscribe/unsubscribe topic
    public static final String LOCAL = "LOCAL-";   // local for notification subscribe/unsubscribe topic
    public static final String CAMERA_DEEP = "rtmp://home.deepfoods.net";
    public static final String CAMERA_PATH = "/static/storage/volume/pi/";

    public static final int REMOTE_REQUEST_CODE = 10;
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
    public static final String home_controller_id = "home_controller_id";
    public static final String AUTHORIZATION_TOKEN = "Authorization_TOKEN"; // dev arp add on 22 june 2020
    public static final String DEVICE_PUSH_TOKEN = "device_push_token";

    public static final String FORGETPASSWORD_MOBILENUMBER = "mobile_number";
    public static final String CURRENT_OPT = "currnet_otp";


    public static final String SIMPLE_DATE_FORMAT_1 = "MMM dd, yyyy";
    public static final String LOG_DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_D_MMM_YY_H_MM_AMPM = "dd-MMM-yy hh:mm a";
    public static final String LOG_DATE_FORMAT_2 = "dd-MMM yyyy h:mm a";

    public static final String APP_LOGIN = "/auth/login"; // dev arp change on 22 june 2020
    public static final String APP_LOGOUT = "/auth/logout"; // dev arp change on 23 june 2020
    public static final String SIGNUP_API = "/signupdetails";

    //room
    public static final String GET_DEVICES_LIST = "/device/list";
    public static final String GET_DEVICES_LIST_NEW = "/rooms/get-all-room-details";
    public static final String moodList = "/mood/list";
    public static final String roomsget = "/rooms/get";
    public static final String roomslist = "/rooms/list";
    public static final String getMoodName = "/mood/names/list";

    /*---------------------------Add new api v2-----------------------------*/
    public static final String getChildUsers = "/admin/child-user/list";
    public static final String deviceunassigned = "/device/unassigned";
    public static final String DeleteChildUser = "/admin/child-user/delete";
    public static final String getRoomCameraList = "/admin/room-camera-list/";
    public static final String AddChildUser = "/admin/child-user/add";
    public static final String updateChildUser = "/admin/child-user/update";
    public static final String ADD_CUSTOME_ROOM = "/rooms/add";
    public static final String SAVE_ROOM_AND_PANEL_NAME = "/rooms/edit";
    public static final String DELETE_ROOM = "/rooms/delete";
    public static final String mooddelete = "/mood/delete";
    public static final String validatecamerakey = "/camera/validate-key";
    public static final String addHueLight = "/addHueLight";
    public static final String HueLightsList = "/PhilipsHueLightsList";
    public static final String addHueBridge = "/addHueBridge";
    public static final String searchBridges = "/searchBridges";
    public static final String moodsmartremote = "/mood/smart-remote";

    //door sensor
    public static final String ADD_DOOR_SENSOR = "/addDoorSensor"; // is_new=1 means new panel add & is_new=0 means exting panel add
    public static final String CHANGE_DOOR_SENSOR_STATUS = "/changeDoorSensorStatus";
    public static final String getCameraNotificationAlertList = "/camera/notification/all";
    public static final String getSmartDeviceBrands = "/getSmartDeviceBrands";
    public static final String getSmartDeviceType = "/getSmartDeviceType";
    public static final String getHeavyLoadDetails = "/device/heavy-load/usage";
    public static final String getHueBridgeList = "/getHueBridgeList";
    public static final String getSpikebotHueLightList = "/getSpikebotHueLightList";
    public static final String changeLockSensorAutoLockStatus = "/changeLockSensorAutoLockStatus";
    public static final String deleteDoorLock = "/deleteDoorLock";
    public static final String deleteTTLockBridge = "/deleteTTLockBridge";
    public static final String deviceadd = "/device/add";
    public static final String jetsonadd = "/jetson/add";

    public static final String deviceheavyloadping = "/device/heavy-load/ping";

    public static final String GET_UNASSIGNED_SENSORS = "/getUnassignedSensors";
    public static final String SAVE_UNCONFIGURED_SENSOR = "/saveUnconfiguredSensor";
    public static final String UPDATE_UNREAD_LOGS = "/updateUnReadLogs";
    public static final String MARK_SEEN = "/logs/seen";
    public static final String SAVE_EDIT_SWITCH = "/device/edit";
    public static final String devicefind = "/device/find";
    public static final String jetsonlist = "/jetson/list";
    public static final String jetsonupdate = "/jetson/update";
    public static final String jetsondelete = "/jetson/delete";
    public static final String camerarecording = "/camera/video";
    public static final String reassignRepeater = "/reassignRepeater";
    public static final String getUnassignedRepeaterList = "/getUnassignedRepeaterList";
    public static final String deviceconfigure = "/device/configure/";
    public static final String deviceinfo = "/device/info";
    public static final String cameralistbyjetson = "/camera/list-by-jetson";
    public static final String devicemoduledelete = "/device/module/delete";
    public static final String beaconscannerscan = "/device/beacon-scanner/scan";
    public static final String GET_BEACON_LOCATION = "/device/beacon/locations";
    public static final String GET_ROOM_DETAILS = "/rooms/get-room-details";
    public static final String GET_CAMERA_DETAILS = "/camera/list";

    //temp sensor
    public static final String ADD_TEMP_SENSOR_NOTIFICATION = "/alert/add";
    public static final String UPDATE_TEMP_SENSOR_NOTIFICATION = "/alert/edit";
    public static final String DELETE_TEMP_SENSOR_NOTIFICATION = "/alert/delete";
    public static final String DELETE_MODULE = "/device/delete";
    public static final String panelDelete = "/panel/delete";
    public static final String reportFalseImage = "/false_image";

    //IR Blaster
    public static final String ADD_IR_BLASTER = "/AddIRBlaster";
    public static final String GET_IR_BLASTER_INFO = "/getIRBlasterInfo";
    public static final String getIRDeviceTypeBrands = "/getIRDeviceTypeBrands/";
    public static final String getDeviceBrandRemoteList = "/getDeviceBrandRemoteList/";
    public static final String GET_REMOTE_SCHEDULE_LIST = "/getRemoteScheduleList";
    public static final String ADD_REMOTE_SCHEDULE = "/addRemoteSchedule";
    public static final String UPDATE_REMOTE_SCHEDULE = "/updateRemoteSchedule";
    public static final String DELETE_REMOTE_SCHEDULE = "/deleteRemoteSchedule";
    public static final String CHANGE_REMOTE_SCHEDULE_STATUS = "/changeRemoteScheduleStatus";
    public static final String getRoomList = "/getRoomList";
    public static final String changeHueLightState = "/changeHueLightState";
    public static final String getPhilipsHueParams = "/getPhilipsHueParams";


    //new api for IR Blaster
    public static final String DELETE_IR_BLASTER = "/deleteIRBlaster";
    public static final String SEND_ON_OFF_COMMAND = "/sendOnOffCommand";
    public static final String UPDATE_REMOTE_DETAILS = "/updateRemoteDetails";
    public static final String DELETE_REMOTE = "/deleteRemote";
    public static final String deletePhilipsHue = "/deletePhilipsHue";

    //panel
    public static final String ADD_CUSTOM_PANEL = "/panel/add-existing";
    public static final String CHANGE_ROOM_PANELMOOD_STATUS_NEW = "/room/status";
    public static final String mood_status = "/mood/status";
    public static final String CHANGE_PANELSTATUS = "/panel/status";

    //devices
    public static final String CHANGE_DEVICE_STATUS = "/device/status";
    public static final String ADD_CUSTOME_DEVICE = "/panel/update/devices";
    public static final String CHECK_INDIVIDUAL_SWITCH_DETAILS = "/device/icon/list";

    //mood
    public static final String ADD_NEW_MOOD_NEW = "/mood/add";
    public static final String EDITMOOD = "/mood/edit";  //saveEditMood
    public static final String GET_MOOD_DEVICE_DETAILS = "/getMoodDeviceDetails";

    //schedule
    public static final String ADD_NEW_SCHEDULE = "/schedule/add";
    public static final String scheduleedit = "/schedule/edit";
    public static final String schedulestatus = "/schedule/status";
    public static final String DELETE_SCHEDULE = "/schedule/delete";
    public static final String GET_SCHEDULE_LIST = "/schedule/list";
    public static final String GET_ORIGINAL_DEVICES = "/device/custom-panel-details";
    public static final String addCameraNotification = "/camera/notification/add";
    public static final String deleteCameraNotification = "/camera/notification/delete";
    public static final String updateCameraNotification = "/camera/notification/update";
    public static final String changeCameraAlertStatus = "/camera/notification/status";

    //profile
    public static final String GET_USER_PROFILE_INFO = "/getuserProfileInfo";
    public static final String SAVE_USER_PROFILE_DETAILS = "/saveUserProfileDetails";
    public static final String CHANGE_PASSWORD = "/user/change-password";

    //camera
    public static final String ADD_CAMERA = "/camera/add";
    public static final String SAVE_EDIT_CAMERA = "/camera/update";
    public static final String DELETE_CAMERA = "/camera/delete";
    public static final String GET_CAMERA_RECORDING_BY_DATE = "/camera/recording-by-date";
    public static final String GET_CAMERA_NOTIFICATION_COUNTER = "/getNotificationCounter";
    public static final String getCameraLogs = "/getCameraLogs";
    public static final String getUnseenCameraLog = "/getUnseenCameraLog";

    //others
    public static final String GET_FILTER_NOTIFICATION_INFO = "/logs/categories";
    public static final String logsfind = "/logs/find";
    public static final String GET_NOTIFICATION_LIST = "/user/notification-settings";
    public static final String SAVE_NOTIFICATION_LIST = "/user/notification-settings/update";
    public static final String updateBadgeCount = "/updateBadgeCount";
    public static final String getCameraToken = "/camera/token/";
    public static final String getAllCameraToken = "/camera/token/all";
    //    public static final String getLocalMacAddress = "/system/mac-address";
    public static final String getLocalMacAddress = ":8077/sysconfig.json";
    public static final String addTTLock = "/addTTLock";
    public static final String getLockLists = "/getLockLists";
    public static final String deleteTTLock = "/deleteTTLock";


    /*forget password*/

    public static final String FORGET_PASSWORD = "/auth/forget-password";
    public static final String OTP_VERIFY = "/auth/forget-password/verify-otp";
    public static final String RETRY_OTP = "/auth/forget-password/resend-otp";
    public static final String SET_NEW_PASSWORD = "/auth/forget-password/reset-password";



    /*----------------------------------------------------------------------*/

    public static final String ANDROID = "android";

    //lock
    public static final String client_id = "439063e312444f1f85050a52efcecd2e";
    public static final String client_secret = "0ef1c49b70c02ae6314bde603d4e9b05";
    public static final String locK_base_uri = "http://open.ttlock.com.cn";
    public static final String lockUserName = "ttchatcrash@gmail.com";
    public static final String lockPassword = "ttchat$123";
    public static final String startUrlhttp = "http:";
    public static final int lockUserId = 1941573;
    public static String access_token = "a74549ab15d07ecd988e26f50985aee7";
    /*----------------------------------------------------------------------*/
    public static String CLOUD_SERVER_URL = "https://live.spikebot.io:8443"; //222 live
    public static String IP_END = "222";
    public static String startUrl = "http://home.d";
    public static String ContainsUrl = "home.deepfoods";
    public static int lockDate = 0;
    public static String socketIp = "";
    public static int adminType = 1;
    public static int room_type = 0;
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

    public static void startUrlset() {
        if (!ChatApplication.url.startsWith("http")) {
            ChatApplication.url = "http://" + ChatApplication.url;
        }

    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
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

    public static Bitmap takescreenshotOfRootView(View view, RecyclerView recyclerView) {
        // create bitmap screen capture
        View v1 = view;
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

        return bitmap;
    }

    public static String getFTemp(String value) {
        double a = Double.parseDouble(value);
        double b = a * 9 / 5 + 32;
        int c = (int) b;
        String r = String.valueOf(c);
        return r;
    }

    public static String getCTemp(String value) {
        double a = Double.parseDouble(value);
        double b = a - 32;
        double c = b * 5 / 9;
        int c1 = (int) c;
        String r = String.valueOf(c1);
        ChatApplication.logDisplay("ff is " + value + "  " + r);
        return r;
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String logConverterDate(long activity_time) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        Date currenTimeZone = new Date((long) activity_time);
        return sdf.format(currenTimeZone);
    }

    public static String logConverterDateImagezoom(long activity_time) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy h:mm a", Locale.getDefault());
        Date currenTimeZone = new Date((long) activity_time);
        return sdf.format(currenTimeZone);
    }

    public static String formatcurrentdate(String date) throws ParseException {
     /*   Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = df.format(c);

        SimpleDateFormat newformat = new SimpleDateFormat("dd MMMM yyyy");
        date = newformat.format(formattedDate);
*/

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = fmt.parse(date);

        SimpleDateFormat fmtOut = new SimpleDateFormat("d MMM yyyy");
        return fmtOut.format(date1);
    }

    public static int getCurentMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MM");
        int row_index = Integer.parseInt(month_date.format(cal.getTime())) - 1;
        return row_index;
    }

    public static int getCurentYear() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int row_index = year;
        return row_index;
    }

    public static int getCurentMonthheavyload() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MM");
        int row_index = Integer.parseInt(month_date.format(cal.getTime()));
        return row_index;
    }

    public static String getNextDate(String curDate) {
        String nextDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_1);
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = format.format(today.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }

}
