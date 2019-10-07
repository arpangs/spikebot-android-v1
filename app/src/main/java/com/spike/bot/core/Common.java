package com.spike.bot.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.spike.bot.listener.RouterIssue;
import com.ttlock.bl.sdk.net.OkHttpRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
//        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        return cm.getActiveNetworkInfo() != null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) ChatApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * check network is reachable or not using process execute command
     */
    public static void isReachableProcess() {
        Process p1 = null;
        try {
            p1 = Runtime.getRuntime().exec("ping www.google.com");
            Log.i("CmdName", "using cmd :" + p1.waitFor());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param addr
     * @param openPort
     * @param timeOutMillis
     * @return
     */
    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * check ip or port is reachable or not using socket
     *
     * @param inHost
     * @param inPort
     * @return
     */
    public static boolean isPortReachable(String inHost, int inPort, RouterIssue routerIssue) {

        Socket socket = null;
        boolean retVal = false;

        try {
            socket = new Socket(inHost, inPort);
            socket.setSoTimeout(1000);
            retVal = true;
        } catch (IOException e) {
            routerIssue.wifiConnectionIssue(false);
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return retVal;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    public static String getMacAddress(Context context, String ipaddress) {
        if (ipaddress == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ipaddress.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        ChatApplication.logDisplay("mac address is " + mac);
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String TAG = "isReachableURL";

    /**
     * Check is given IP is reachable or not
     *
     * @param context
     * @param urlAddress
     * @return
     */
    public static boolean IsReachable(Context context, String urlAddress) {
        // First, check we have any sort of connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        boolean isReachable = false;

        //String osName = System.getProperty("os.name");
        // String cmd = executeCmd("ls -l", false);

        if (netInfo != null && netInfo.isConnected()) {
            // Some sort of connection is open, check if server is reachable
            try {
                //URL url = new URL("http://10.0.2.2");
                URL url = new URL(urlAddress);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "Android Application");
                urlc.setRequestProperty("Connection", "close");
                //urlc.setConnectTimeout( 2500);
                urlc.setConnectTimeout(1000);
                urlc.setReadTimeout(1000);
                urlc.connect();
                isReachable = (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isReachable;
    }


    public static String executeCmd(String cmd, boolean sudo) {
        try {

            Process p;
            if (!sudo)
                p = Runtime.getRuntime().exec(cmd);
            else {
                p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }
            p.destroy();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * @param nping
     * @param wping
     * @param ipping
     * @return
     * @throws
     */
    public static boolean isReachable(int nping, int wping, String ipping) throws Exception {

        int nReceived = 0;
        int nLost = 0;

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("ping -n " + nping + " -w " + wping + " " + ipping);
        Scanner scanner = new Scanner(process.getInputStream());
        process.waitFor();
        ArrayList<String> strings = new ArrayList<>();
        String data = "";
        //
        while (scanner.hasNextLine()) {
            String string = scanner.nextLine();
            data = data + string + "\n";
            strings.add(string);
        }

        if (data.contains("IP address must be specified.")
                || (data.contains("Ping request could not find host " + ipping + ".")
                || data.contains("Please check the name and try again."))) {
            throw new Exception(data);
        } else if (nping > strings.size()) {
            throw new Exception(data);
        }

        int index = 2;

        for (int i = index; i < nping + index; i++) {
            String string = strings.get(i);
            if (string.contains("Destination host unreachable.")) {
                nLost++;
            } else if (string.contains("Request timed out.")) {
                nLost++;
            } else if (string.contains("bytes") && string.contains("time") && string.contains("TTL")) {
                nReceived++;
            } else {
            }
        }

        return nReceived > 0;
    }

    /**
     * @param status
     * @param type
     * @return
     */
    public static int getIcon(int status, String type) {
        int resource = R.drawable.bulb_on;
        switch (status) {

            case -1:
                switch (type) {
                    case "curtain":
                        resource = R.drawable.curtain_closed;
                        break;
                }
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
                    case "tempsensor":
                        resource = R.drawable.on_temperature;
                        break;
                    case "temp":
                        resource = R.drawable.on_temperature;
                        break;
                    case "doorsensor":
                        resource = R.drawable.on_door;
                        break;
                    case "unavailable":
                        resource = R.drawable.icn_dead_temp;
                        break;
                    case "irblaster":
                        resource = R.drawable.ic_ir_blaster;
                        break;
                    case "Remote_AC":
                        resource = R.drawable.remote_ac;
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
                    case "temp":
                        resource = R.drawable.off_temperature;
                        break;
                    case "doorsensor":
                        resource = R.drawable.off_door;
                        break;
                    case "unavailable":
                        resource = R.drawable.icn_dead_temp;
                        break;
                    case "irblaster":
                        resource = R.drawable.ic_ir_blaster;
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

                    case "gassensor":
                        resource = R.drawable.fire_and_gas_gray;
                        break;

                    case "curtain":
                        resource = R.drawable.curtain_closed;
                        break;

                    default:
                        resource = R.drawable.bulb_off;
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
            case "camera":
                resource = R.drawable.camera_off_inactive;
                break;
            case "door":
                resource = R.drawable.door_off_inactive;
                break;
            case "temp":
                resource = R.drawable.temp_off_inactive;
                break;
            case "tempsensor":
                resource = R.drawable.temp_off_inactive;
                break;
            case "heavyload":
                resource = R.drawable.heavuload_inactive;
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
        // textview.setTag("test");
        Boolean flag = false;
        Log.d("", "setOnOffBackground tag = " + textview.getTag());
        if (textview.getTag() == null) {
            flag = false;
        } else {
            flag = Boolean.parseBoolean(textview.getTag().toString());
        }
        flag = !flag;

        Log.d("", "setOnOffBackground = " + flag);
        //flag = Boolean.parseBoolean(textview.getTag(0).toString());
        setBackground(mContext, textview, flag);
        textview.setTag(flag);
    }

    /**
     * @param mContext
     * @param textview
     * @param flag
     */
    public static void setBackground(Context mContext, TextView textview, Boolean flag) {
        Log.d("", "setOnOffBackground = " + flag);
        //flag = Boolean.parseBoolean(textview.getTag(0).toString());
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

    public static String getDaysStringGray(String schedule_device_day) {
        String deviceString = "";
        String start = "<font color=\"#808080\"><b>";//FFBC38 008BE0
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

        //To retrieve
        SharedPreferences settings = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        String value = settings.getString(key, ""); //0 is the default value

        return value;
    }

    public static String USER_JSON = "user_pref_json";
    public static String USER_PIDETAIL = "user_pi_details";
    public static String camera_key = "camera_key";

    /**
     * hideSoftKeyBoard
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(
                new View(activity).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
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


    /**
     * @param context
     * @return
     */
    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isConnected();
    }

    /**
     * @param context
     * @return
     */
    public static boolean isInternetAccessible(Context context) {
        if (isWifiAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(200);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
        return false;
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
