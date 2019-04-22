package com.spike.bot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.DateHelper;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.CustomReportSender;
import com.spike.bot.model.User;
import com.spike.bot.receiver.ApplicationCrashHandler;
import com.spike.bot.receiver.ConnectivityReceiver;

import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

@ReportsCrashes(formKey = "",//1LZjNZVtIyc5O2-llj8VfHMsvDC2YdGkgNp3RiIGBL2I", // will not be used 1tgWQ58TdbvD0CGhW91Se2MwCFz_nlV1sezQrimsw6qw
//mailTo = "kaushalap@gochetak.com",
        customReportContent = { ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,ReportField.AVAILABLE_MEM_SIZE,
                ReportField.PRODUCT,ReportField.DISPLAY, ReportField.CUSTOM_DATA,
                ReportField.USER_APP_START_DATE ,ReportField.SHARED_PREFERENCES,ReportField.STACK_TRACE},
//forceCloseDialogAfterToast = true,
//formUri = "http://yourserver.com/yourscript",
//httpMethod = org.acra.sender.HttpSender.Method.POST,
//reportType = HttpSender.Type.FORM,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class ChatApplication extends Application {

    private Socket mSocket;
    public static String url="";
    public static boolean isRefreshDashBoard=false;
    public static boolean isRefreshSchedule=true;
    public static boolean isRefreshHome=false;
    public static boolean isRefreshMood=false;
    public static boolean isSignUp=false;

    public static boolean isMainFragmentNeedResume = false;
    public static boolean isScheduleNeedResume = true;
    public static boolean isMoodFragmentNeedResume = false;
    public static boolean isEditActivityNeedResume = false;
    public static boolean isLogResume = false;
    public static boolean isLocalFragmentResume = false;

    public static boolean isRefreshUserData = false;

    public static boolean isOpenDialog = false;
    public static String testIP = Constants.IP_END;

    public static int ADAPTER_POSITION = -1;
    public static String currentuserId = "";
    public static boolean isPushFound =true;

    /*{
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            Log.d("","ChatApplication ");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }*/


    public Socket openSocket(String url) {
        this.url = url;
        try {
            mSocket = IO.socket(url);


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return mSocket;
    }

    /**
     *
     */

    public void connectSocket(){
        if(mSocket!=null)
            mSocket.connect();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void closeSocket(String url) {
       // this.url = url;
        try {
            //IO.Options options = new IO.Options();
            // options.forceNew=true;
            //IO.Options opts = new IO.Options();
            //opts.forceNew = true;
//            opts.query = "auth_token=" + authToken;
            if(mSocket!=null) {
                mSocket.disconnect();
                mSocket.close();
                mSocket = null;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

       // return mSocket;
    }

    public Socket getSocket() {
        return mSocket;
    }


    private static ChatApplication mInstance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        context = this;
        ACRA.init(this);
        ACRA.getErrorReporter().removeAllReportSenders();
        ACRA.getErrorReporter().setReportSender(new CustomReportSender(this.getApplicationContext()));
// Install the application crash handler
        ApplicationCrashHandler.installHandler();
      //  Common.savePrefValue(getApplicationContext(),"","");
    }

    private static ChatApplication instance;
    public static ChatApplication getApp() {
        if (instance== null) {
            synchronized(ChatApplication.class) {
                if (instance == null)
                    instance = new ChatApplication();
            }
        }
        return instance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized ChatApplication getInstance() {
        return mInstance;
    }
    public static synchronized Context getContext(){
        return context;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static Integer checkActivity(String name){
        int value=-1;
        if(name.equals("mode") || name.equals("room")){
            value=1;
        }else if(name.equals("schedule") || name.equals("Timer")){
            value=0;
        }else if(name.equals("tempSensor") || name.equals("doorSensor")){
            value=0;
        }
        return value;
    }

    public static Integer checkSelection(String name, List<String> mListRoomMood){
        int value=0;
        if(name.equalsIgnoreCase("room")){
            name="Room";
        }else if(name.equalsIgnoreCase("mode")){
            name="Mood";
        }else if(name.equalsIgnoreCase("schedule")){
            name="Schedule";
        }else if(name.equalsIgnoreCase("Timer")){
            name="Timer";
        }
        for(int i=0;i<mListRoomMood.size(); i++){
            if(name.equals(""+mListRoomMood.get(i))){
                value=i;
            }
        }

        return value;
    }

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;

    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
//yyyy-MM-dd HH:mm:ss
        return currentDateandTime+" 00:01:00";

    }

    public static void showToast(Context context,String message){
        Toast.makeText(context,""+message,Toast.LENGTH_LONG).show();
    }

    public static void logDisplay(String message){
        if (BuildConfig.DEBUG) {
             Log.d("System out",""+message);
        }
    }

    public static String getUuid(){
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }


    public static List<User> getUserList(Context context) {
        List<User> userList = new ArrayList<>();
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(jsonText, type);
        }
        return userList;
    }
    public static void keyBoardHideForce(Context activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
           // imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }

   public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i) {
                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*").matcher(String.valueOf(source.charAt(i))).matches()) {
                    return "";
                }
            }
            return null;
        }
    };

}
