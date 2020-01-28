package com.spike.bot.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.ttlock.bl.sdk.net.OkHttpRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kaushal on 28/12/17.
 */

public class Common {

    public static String USER_JSON = "user_pref_json";
    public static String camera_key = "camera_key"; //key : spike123

    /**
     * @param context
     * @return
     */
    public static boolean isConnectedToMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean NisConnected = activeNetwork != null && activeNetwork.isConnected();
        if (NisConnected) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * check if network is connected or not
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) ChatApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String TAG = "isReachableURL";

    public static int getDoorIcon(int status) {
        return status == 0 ? R.drawable.on_door : R.drawable.off_door;
    }

    /**
     * @param status
     * @param type
     * @return
     */
    public static int getIcon(int status, String type) {
        int resource = R.drawable.bulb_on;
        ChatApplication.logDisplay("type is "+type);
        switch (status) {

            case -1:
                switch (type) {
                    case "curtain":
                        resource = R.drawable.curtain_closed;
                        break;
                }
            case 0:
                switch (type) {
                    case "bulb":
                        resource = R.drawable.bulb_off;
                        break;
                    case "ac":
                        resource = R.drawable.ac_off;
                        break;
                    case "fan":
                        resource = R.drawable.fan_off;
                        break;
                    case "fridge":
                        resource = R.drawable.fridge_off;
                        break;
                    case "tv":
                        resource = R.drawable.tv_off;
                        break;
                    case "oven":
                        resource = R.drawable.microwave_oven_off;
                        break;
                    case "cfl":
                        resource = R.drawable.cfl_off;
                        break;
                    case "generic":
                        resource = R.drawable.genericelectricdevice_off;
                        break;
                    case "camera":
                        resource = R.drawable.camera_off;
                        break;
                    case "work":
                        resource = R.drawable.smily_gray;
                        break;
                    case "temp_sensor":
                        resource = R.drawable.off_temperature;
                        break;

                    case "tempsensor":
                        resource = R.drawable.off_temperature;
                        break;
                    case "door_sensor":
                        resource = R.drawable.on_door;
                        break;

                    case "doorsensor":
                        resource = R.drawable.on_door;
                        break;
                    case "unavailable":
                        resource = R.drawable.icn_dead_temp;
                        break;
                    case "remote":
                        resource = R.drawable.remote_ac_off;
                        break;
                    case "ir_blaster":
                        resource = R.drawable.remote_ac_off;
                        break;
                    case "Remote_AC":
                        resource = R.drawable.remote_ac_off;
                        break;
                    case "multisensor":
                        resource = R.drawable.icon_multi_sensor;
                        break;
                    case "heavy_load":
                        resource = R.drawable.off;
                        break;
                    case "heavyload":
                        resource = R.drawable.off;
                        break;
                    case "double_heavy_load":
                        resource = R.drawable.off;
                        break;

                    case "Smart Bulb":
                        resource = R.drawable.philips_hue_off;
                        break;

                    case "lockWithDoor":
                        resource = R.drawable.door_locked;
                        break;

                    case "lockOnly":
                        resource = R.drawable.lock_only;
                        break;

                    case "gas_sensor":
                        resource = R.drawable.fire_and_gas_gray;
                        break;

                    case "gassensor":
                        resource = R.drawable.fire_and_gas_gray;
                        break;

                    case "curtain":
                        resource = R.drawable.curtain_closed;
                        break;
                    case "repeater":
                        resource = R.drawable.gray_repeater;
                        break;
                    case "smart_remote":
                        resource = R.drawable.remote;
                        break;

                    default:
                        resource = R.drawable.bulb_off;
                        break;
                }
                break;
            case 1:
                switch (type) {
                    case "bulb":
                        resource = R.drawable.bulb_on;
                        break;
                    case "ac":
                        resource = R.drawable.ac_on;
                        break;
                    case "fan":
                        resource = R.drawable.fan_on;
                        break;
                    case "fridge":
                        resource = R.drawable.fridge_on;
                        break;
                    case "tv":
                        resource = R.drawable.tv_on;
                        break;
                    case "oven":
                        resource = R.drawable.microwave_oven_on;
                        break;
                    case "cfl":
                        resource = R.drawable.cfl_on;
                        break;
                    case "generic":
                        resource = R.drawable.genericelectricdevice_on;
                        break;
                    case "camera":
                        resource = R.drawable.camera_on;
                        break;
                    case "work":
                        resource = R.drawable.smily_yello;
                        break;
                    case "home":
                        resource = R.drawable.smily_yello;
                        break;
                    case "night":
                        resource = R.drawable.smily_yello;
                        break;
                    case "temp_sensor":
                        resource = R.drawable.on_temperature;
                        break;
                    case "tempsensor":
                        resource = R.drawable.on_temperature;
                        break;
                    case "door_sensor":
                        resource = R.drawable.off_door;
                        break;
                    case "doorsensor":
                        resource = R.drawable.off_door;
                        break;
                    case "unavailable":
                        resource = R.drawable.icn_dead_temp;
                        break;
                    case "remote":
                        resource = R.drawable.remote_ac;
                        break;
                    case "ir_blaster":
                        resource = R.drawable.remote_ac;
                        break;
                    case "Remote_AC":
                        resource = R.drawable.remote_ac;
                        break;

                    case "heavy_load":
                        resource = R.drawable.on;
                        break;

                    case "heavyload":
                        resource = R.drawable.on;
                        break;

                    case "Smart Bulb":
                        resource = R.drawable.philips_hue_on;
                        break;

                    case "lockWithDoor":
                        resource = R.drawable.door_unlocked;
                        break;

                    case "lockOnly":
                        resource = R.drawable.unlock_only;
                        break;
                    case "gas_sensor":
                        resource = R.drawable.fire_and_gas;
                        break;

                    case "gassensor":
                        resource = R.drawable.fire_and_gas;
                        break;
                    case "curtain":
                        resource = R.drawable.curtain_open;
                        break;
                    default:
                        resource = R.drawable.bulb_on;
                        break;
                }
                break;

//            case 3:
//                switch (type) {
//
//                }
//
//                break;

        }
        return resource;
    }


    public static int getIconForEditRoom(int status, String type) {
        int resource = R.drawable.bulb_on;
        switch (status) {
            case 0:
                switch (type) {
                    case "bulb":
                        resource = R.drawable.bulb_off;
                        break;
                    case "ac":
                        resource = R.drawable.ac_off;
                        break;
                    case "fan":
                        resource = R.drawable.fan_off;
                        break;
                    case "fridge":
                        resource = R.drawable.fridge_off;
                        break;
                    case "tv":
                        resource = R.drawable.tv_off;
                        break;
                    case "oven":
                        resource = R.drawable.microwave_oven_off;
                        break;
                    case "cfl":
                        resource = R.drawable.cfl_off;
                        break;
                    case "generic":
                        resource = R.drawable.genericelectricdevice_off;
                        break;
                    case "camera":
                        resource = R.drawable.camera_off;
                        break;
                    case "work":
                        resource = R.drawable.smily_gray;
                        break;
                    case "tempsensor":
                        resource = R.drawable.off_temperature;
                        break;
                        case "temp_sensor":
                        resource = R.drawable.off_temperature;
                        break;
                    case "doorsensor":
                        resource = R.drawable.off_door;
                        break;
                    case "unavailable":
                        resource = R.drawable.icn_dead_temp;
                        break;
                    case "remote":
                        resource = R.drawable.remote_ac_off;
                        break;
                    case "Remote_AC":
                        resource = R.drawable.remote_ac_off;
                        break;
                    case "multisensor":
                        resource = R.drawable.icon_multi_sensor;
                        break;
                    case "heavyload":
                        resource = R.drawable.off;
                        break;

                    case "Smart Bulb":
                        resource = R.drawable.philips_hue_off;
                        break;

                    case "lockWithDoor":
                        resource = R.drawable.door_locked;
                        break;

                    case "lockOnly":
                        resource = R.drawable.lock_only;
                        break;
                    case "curtain":
                        resource = R.drawable.curtain_closed;
                        break;

                    default:
                        resource = R.drawable.bulb_off;
                        break;
                }
                break;
        }
        return resource;
    }

    /**
     * get In Active Temp/Door/Remote Icon
     *
     * @param status     device status
     * @param sensorType device/sensor type
     * @return drawable icon
     */
    public static int getIconInActive(int status, String sensorType) {
        int resource = R.drawable.bulb_on;
        switch (sensorType) {
            case "remote":
                resource = R.drawable.ac_remote_off_inactive;
                break;
            case "Remote_AC":
                resource = R.drawable.ac_remote_off_inactive;
                break;
            case "ir_blaster":
                resource = R.drawable.ac_remote_off_inactive;
                break;
            case "gas_sensor":
                resource = R.drawable.fire_and_gas_gray_with_red_cross;
                break;
            case "camera":
                resource = R.drawable.camera_off_inactive;
                break;
            case "door_sensor":
                resource = R.drawable.door_off_inactive;
                break;
            case "doorsensor":
                resource = R.drawable.door_off_inactive;
                break;
            case "temp":
                resource = R.drawable.temp_off_inactive;
                break;
            case "tempsensor":
                resource = R.drawable.temp_off_inactive;
                break;
            case "temp_sensor":
                resource = R.drawable.temp_off_inactive;
                break;
            case "heavyload":
                resource = R.drawable.headload_inactive;
                break;
            case "heavy_load":
                resource = R.drawable.headload_inactive;
                break;

            case "multisensor":
                resource = R.drawable.heavuload_inactive;
                break;
        }
        return resource;
    }

    /**
     * @param mContext
     * @param textview
     */
    public static void setOnOffBackground(Context mContext, TextView textview) {
        Boolean flag = false;
        Log.d("", "setOnOffBackground tag = " + textview.getTag());
        if (textview.getTag() == null) {
            flag = false;
        } else {
            flag = Boolean.parseBoolean(textview.getTag().toString());
        }
        flag = !flag;

        setBackground(mContext, textview, flag);
        textview.setTag(flag);
    }

    /**
     * @param mContext
     * @param textview
     * @param flag
     */
    public static void setBackground(Context mContext, TextView textview, Boolean flag) {
        if (flag) {
            textview.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_blue_circle_fill));
            textview.setTextColor(mContext.getResources().getColor(R.color.automation_white));
        } else {
            textview.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_blue_circle_border));
            textview.setTextColor(mContext.getResources().getColor(R.color.sky_blue));
        }
        textview.setTag(flag);

    }

    /**
     * @param schedule_device_day
     * @return
     */
    public static String getDaysString(String schedule_device_day) {
        String deviceString = "";
        String start = "<font color=\"#FFBC38\"><b>";//FFBC38 008BE0
        String end = "</b></font>";
        if (schedule_device_day.contains("0")) {
            deviceString = deviceString + start + "S " + end;
        } else {
            deviceString = deviceString + "S ";
        }

        if (schedule_device_day.contains("1")) {
            deviceString = deviceString + start + "&nbsp;M " + end;
        } else {
            deviceString = deviceString + "&nbsp;M ";
        }

        if (schedule_device_day.contains("2")) {
            deviceString = deviceString + start + "&nbsp;T " + end;
        } else {
            deviceString = deviceString + "&nbsp;T ";
        }

        if (schedule_device_day.contains("3")) {
            deviceString = deviceString + start + "&nbsp;W " + end;
        } else {
            deviceString = deviceString + "&nbsp;W ";
        }

        if (schedule_device_day.contains("4")) {
            deviceString = deviceString + start + "&nbsp;T " + end;
        } else {
            deviceString = deviceString + "&nbsp;T ";
        }

        if (schedule_device_day.contains("5")) {
            deviceString = deviceString + start + "&nbsp;F " + end;
        } else {
            deviceString = deviceString + "&nbsp;F ";
        }

        if (schedule_device_day.contains("6")) {
            deviceString = deviceString + start + "&nbsp;S " + end;
        } else {
            deviceString = deviceString + "&nbsp;S ";
        }
        return deviceString;
    }

    /**
     * @param context
     * @param key
     * @param keyValue
     */
    public static void savePrefValue(Context context, String key, String keyValue) {
        //To save
        SharedPreferences settings = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, keyValue);
        editor.commit();
        editor.apply();
    }

    /**
     * getPref Value
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPrefValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        String value = settings.getString(key, ""); //0 is the default value
        return value;
    }


    /**
     * hideSoftKeyBoard
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(new View(activity).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * <p>Common method for get Calender instance with set custom date and minutes
     * {@link com.spike.bot.activity.ScheduleActivity} - onEditTextChangeListener)</p>
     *
     * @param currentDate
     * @param hours
     * @param minutes
     * @return
     */

    public static Calendar getCalendarHourMinute(Date currentDate, String hours, String minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.HOUR, Integer.parseInt(hours));
        calendar.add(Calendar.MINUTE, Integer.parseInt(minutes));
        return calendar;
    }

    /**
     * <p1>Convert date in string after add hour and minute in specific calender time</p>
     *
     * @param calendar
     * @return
     */

    public static String getConvertDateString(Calendar calendar) {
        String outputPattern2 = Constants.SIMPLE_DATE_FORMAT_1;
        SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
        return outputFormat2.format(calendar.getTime());
    }

    public static String getConvertDateForSchedule(String value) {
        //Nov 11, 2019 52: ,,  Nov 10, 2019 10:45
        SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = spf.format(newDate);
        return date;
    }

    public static String getHH(String value) {
        //Nov 11, 2019 52:52
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("HH:mm");
        String date = spf.format(newDate);
        return date;
    }


    public static String getTimeHH(String value) {
        //Nov 11, 2019 52:52
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("hh:mm a");
        String date = spf.format(newDate);
        return date;
    }

    public static String getTimeAM(String value) {
        //Nov 11, 2019 52:52
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("a");
        String date = spf.format(newDate);
        return date;
    }

    public static String getDateTime(String value) {
        //Nov 11, 2019 52:52
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("MMM dd, yyyy");
        String date = spf.format(newDate);
        return date;
    }


    public static String getConvertDateForScheduleHour(String value) {
        //Nov 11, 2019 52:52
        SimpleDateFormat spf=new SimpleDateFormat("HH:mm");
        Date newDate= null;
        try {
            newDate = spf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("hh:mm a");
        String date = spf.format(newDate);
        return date;
    }

    /**
     * convert Json String to POJO class using GSON Lib
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return Generics Objects
     * @throws JsonSyntaxException
     */
    public static <T> T jsonToPojo(String json, Class<T> classOfT) throws JsonSyntaxException {
        Gson gson = new Gson();
        Object object = gson.fromJson(json, classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    /**
     * get Battery icon using battery percentage %
     *
     * @param percentage battery percentage
     * @return battery drawable icon
     */
    public static int getBatteryIcon(String percentage) {

        int icon = R.drawable.battery_1;
        int btrPer = 0;

        if (TextUtils.isEmpty(percentage) && percentage.equalsIgnoreCase("null"))
            return icon;

        btrPer = (int) Double.parseDouble(percentage);

        if (btrPer >= 0 && btrPer <= 25)
            icon = R.drawable.battery_1;
        else if (btrPer >= 26 && btrPer <= 50)
            icon = R.drawable.battery_2;
        else if (btrPer >= 51 && btrPer <= 75)
            icon = R.drawable.battery_3;
        else if (btrPer >= 76 && btrPer <= 100)
            icon = R.drawable.battery_4;

        return icon;
    }

    /**
     * Convert String to ℃ Celsius
     *
     * @param celsius
     * @return
     */
    public static String parseCelsius(String celsius) {
        return String.format("%s ℃", (int) Double.parseDouble(celsius));
    }

    public static String getC() {
        return "℃";
    }

    public static String getF() {
        return "℉";
    }


    /**
     * Convert String to ℉ value
     *
     * @param fahrenheit
     * @return
     */
   /* public static String parseFahrenheit(String fahrenheit){
        return String.format("%s℉", String.format("%.2f", Double.parseDouble(fahrenheit)));
    }*/
    public static String parseFahrenheit(String fahrenheit) {
        return String.format("%s ℉", (int) Double.parseDouble(fahrenheit));
    }

    /**
     * set days format
     *
     * @param dayList
     */
    public static String htmlDaysFormat(String dayList) {

        String days[] = dayList.split(",");
        String htmlDays[] = {"S", "M", "T", "W", "T", "F", "S"};

        String daysInHtml = "";
        for (int i = 0; i < htmlDays.length; i++) {
            boolean isFoundDay = false;

            for (String string : days) {
                int dayIndex = Integer.parseInt(string);

                if (dayIndex == i) {
                    isFoundDay = true;
                    daysInHtml += "<b> " + htmlDays[i] + " </b>";
                }
            }
            if (!isFoundDay) {
                daysInHtml += "<font color=\"#B9B9B9\"> " + htmlDays[i] + " </font>";
            }
        }

        return daysInHtml;
    }

    /**
     * Convert dp to pixels (px)
     *
     * @param context Activity Context
     * @param dp      Screen dp
     * @return
     */

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private static Toast mToast;

    /**
     * showToast
     *
     * @param message : Toast display message
     */
    public static void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        int Y = ChatApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.abc_action_bar_height);
        mToast = Toast.makeText(ChatApplication.getContext(), message, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, Y);
        mToast.show();
    }

    /**
     * show Toast on center of screen
     *
     * @param message Toast display message
     */
    public static void showToastCenter(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ChatApplication.getContext(), message, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }



    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

    public static String isInitSuccess(String mac) {
        String url = Constants.locK_base_uri + "/v3/gateway/isInitSuccess";
        HashMap params = new HashMap();
        params.put("clientId", Constants.client_id);
        params.put("accessToken", Constants.access_token);
        params.put("gatewayNetMac", mac);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);

    }

    public static String getUserId() {
        String url = Constants.locK_base_uri + "v3/user/getUid";
        HashMap params = new HashMap();
        params.put("clientId", Constants.client_id);
        params.put("accessToken", Constants.access_token);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }
}
