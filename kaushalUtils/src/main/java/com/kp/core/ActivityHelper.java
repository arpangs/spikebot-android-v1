package com.kp.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kaushalprajapti.util.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author kaushal Prajapati (kaushal2406@gmail.com)
 */
public class ActivityHelper {
    public static String TAG = "ActivityHelper";

    // Dialog Action
    public static final int NO_ACTION = 1;
    public static final int CLOSE_ACTIVITY = 2;

    public static void hideKeyboard(final Activity p_context) {
        try {
            InputMethodManager imm = (InputMethodManager) p_context.getSystemService(Context.INPUT_METHOD_SERVICE);
            // imm.hideSoftInputFromWindow(((EditText) p_view).getWindowToken(),
            // 0);

            imm.hideSoftInputFromWindow(p_context.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    // Progress dialog
    public static ProgressDialog m_progressDialog;

    public static void showProgressDialog(final Context p_context, final String p_loadingMessage, final boolean p_isCancelable) {
        // m_progressDialog = null;
        if (m_progressDialog != null && m_progressDialog.isShowing()) {
            // m_progressDialog.isShowing();
            return;
        }
            Log.d("System out","is showing dialog ss ");
            m_progressDialog = new ProgressDialog(p_context);
        m_progressDialog.setMessage(p_loadingMessage);
//        m_progressDialog.setCancelable(p_isCancelable);
        m_progressDialog.setCanceledOnTouchOutside(p_isCancelable);
        // m_progressDialog.findViewById(android.R.id)
//        if (!((Activity) p_context).isFinishing()) {
            m_progressDialog.show();
//        }
    }

    public static void dismissProgressDialog() {
        try {
            if (m_progressDialog != null) {
                m_progressDialog.cancel();
                m_progressDialog.dismiss();
            }
        } catch (Throwable e) {
        }
    }

    public static void showDialog(final Context p_context, String title, String p_message, final int p_action) {

        AlertDialog.Builder m_builder = new AlertDialog.Builder(p_context);

        //m_builder.setTitle(title);
        m_builder.setMessage(p_message);
        m_builder.setCancelable(false);
        m_builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface p_dialog, int p_id) {

                if (p_action == NO_ACTION) {

                } else if (p_action == CLOSE_ACTIVITY) {
                    if (p_context instanceof Activity) {
                        ((Activity) p_context).finish();
                    }
                }

            }
        });

        AlertDialog m_alertDialog = m_builder.create();
        m_alertDialog.show();
    }


    public static boolean isConnectingToInternet(final Context context) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        try {


            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
//				NetworkInfo[] info = connectivity.getAllNetworkInfo();
//				if (info != null)
//					for (int i = 0; i < info.length; i++)
//						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//							return true;
//						}

//				getActiveNetworkInfo
                NetworkInfo[] netInfo = connectivity.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
//				    	ni.getState()
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnected())
                            haveConnectedWifi = true;
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnected())
                            haveConnectedMobile = true;
                }
                return haveConnectedWifi || haveConnectedMobile;
            }

        } catch (Exception e) {
            // Toast.makeText(context, " isConnectingToInternet = "
            // +e.getMessage() , Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String number = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            number = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return number;
    }

    public static String CallJSONService(String url, String json, String method) throws Exception {

        String result = "";
        String url1 = url;

        URL obj = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setUseCaches(false);
        if (method.equalsIgnoreCase("POST")) {
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        int responseCode = con.getResponseCode();
        UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        result = response.toString();

//        con.disconnect();
        return result;
    }

    public static String CallJSONServiceVideo(String url, String json, String method) throws Exception {

        String result = "";
        String url1 = url;

        URL obj = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setConnectTimeout(20000);
        con.setReadTimeout(20000);
        con.setUseCaches(false);
        if (method.equalsIgnoreCase("POST")) {
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        int responseCode = con.getResponseCode();
        UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        result = response.toString();

//        con.disconnect();
        return result;
    }

    public static String CallJSONService1(String url, String json, String method) throws Exception {

        String result = "";
        String url1 = url;

        UtilsConstants.logDisplay("CallXMLService  url  " + url1);
        URL obj = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setUseCaches(false);
        if (method.equalsIgnoreCase("POST")) {
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        int responseCode = con.getResponseCode();
        UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        result = response.toString();
//        con.disconnect();
        return result;
    }


    public static String CallJSONService2(String url, String json, String method) throws Exception {

        String result = "";
        String url1 = url;

        UtilsConstants.logDisplay("CallXMLService  url  " + url1);
        URL obj = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        con.setUseCaches(false);
        if (method.equalsIgnoreCase("POST")) {
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        int responseCode = con.getResponseCode();
        UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        result = response.toString();
//        con.disconnect();

        return result;


    }

    public static String CallJSONService3(String url, String json, String method) throws Exception {

        String result = "";
        String url1 = url;

        UtilsConstants.logDisplay("CallXMLService  url  " + url1);
        URL obj = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "UTF-8");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setUseCaches(false);
        if (method.equalsIgnoreCase("POST")) {
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        int responseCode = con.getResponseCode();
        UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        result = response.toString();

//		con.disconnect();
        return result;


    }

    public static Date parseTimeSimple(String date, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatter.parse(date));
        calendar.setTimeZone(TimeZone.getDefault());

        return calendar.getTime();
    }

    /* hmZero make double digit hour and minute value
     *  @htMnt : 1
     *  return : 01
     */
    public static String hmZero(int hrMnt) {
        return (hrMnt >= 10) ? Integer.toString(hrMnt) : String.format("0%s", Integer.toString(hrMnt));
    }
    /*
     * make double format hour:minute string
     * @hour : 1
     * @minute : 2
     * return : 01:02
     * */

    public static String hourMinuteZero(int hour, int mnts) {
        String hourZero = (hour >= 10) ? Integer.toString(hour) : String.format("0%s", Integer.toString(hour));
        String minuteZero = (mnts >= 10) ? Integer.toString(mnts) : String.format("0%s", Integer.toString(mnts));
        return hourZero + ":" + minuteZero;
    }

    private String putTimeInXX(String inputDescription, String pTime) {
        return inputDescription.replace("XX", pTime);
    }
}
