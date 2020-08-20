package com.spike.bot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.DateHelper;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.TypefaceUtil;
import com.spike.bot.model.LockObj;
import com.spike.bot.model.User;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@ReportsCrashes(formKey = "",//1LZjNZVtIyc5O2-llj8VfHMsvDC2YdGkgNp3RiIGBL2I", // will not be used 1tgWQ58TdbvD0CGhW91Se2MwCFz_nlV1sezQrimsw6qw
//mailTo = "kaushalap@gochetak.com",
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.AVAILABLE_MEM_SIZE,
                ReportField.PRODUCT, ReportField.DISPLAY, ReportField.CUSTOM_DATA,
                ReportField.USER_APP_START_DATE, ReportField.SHARED_PREFERENCES, ReportField.STACK_TRACE},
//forceCloseDialogAfterToast = true,
//formUri = "http://yourserver.com/yourscript",
//httpMethod = org.acra.sender.HttpSender.Method.POST,
//reportType = HttpSender.Type.FORM,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class ChatApplication extends Application {

    public static final String RestrofitBase = "http://";

    public static String url = "", CloudUrl = ""; // dev arpan change here
    public static String http = "http://";
    public static boolean isRefreshDashBoard = false;
    public static boolean isRefreshSchedule = true;
    public static boolean isRefreshHome = false;
    public static boolean isRefreshMood = false;
    public static boolean isSignUp = false;
    public static int CurrnetFragment = R.id.navigationDashboard;  // dev arpan on 15 june 2020
    public static boolean isMainFragmentNeedResume = false;
    public static boolean isScheduleNeedResume = true;
    public static boolean isShowProgress = false;
    public static boolean isMoodFragmentNeedResume = false;
    public static boolean isEditActivityNeedResume = false;
    public static boolean isLogResume = false;
    public static boolean isLocalFragmentResume = true;
    public static boolean isCallDeviceList = true;
    public static boolean isRefreshUserData = false;
    public static boolean isOpenDialog = false;
    public static String testIP = Constants.IP_END;
    public static int ADAPTER_POSITION = -1;
    public static String currentuserId = "";
    public static boolean isPushFound = true;
    public static LockObj mTestLockObj;
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
    private static ChatApplication mInstance;
    private static Context context;
    private static ChatApplication instance;


    /*{
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            Log.d("","ChatApplication ");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }*/
    private Socket mSocket, cloudsocket;
    //beacon
    private long lastSyncTimeStamp = 0;
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChatApplication.logDisplay("chat onDisconnect call");
            if (Constants.socketIp.length() > 0) {
                ChatApplication.logDisplay("chat onDisconnect callll " + url);

                if (!url.startsWith("http")) {  // dev arpan add this due to logout time exception issue or 443 add before url
                    openSocket("http://" + url);
                } else {
                    openSocket(url);
                }
            }

            if (Constants.socketIp.length() > 0) {
                ChatApplication.logDisplay("chat onDisconnect callll " + url);
                if (!url.startsWith("http")) {  // dev arpan add this due to logout time exception issue or 443 add before url
                    openCloudSocket("http://" + url);
                } else {
                    openCloudSocket(CloudUrl);
                }
            }

        }
    };

    public static ChatApplication getApp() {
        if (instance == null) {
            synchronized (ChatApplication.class) {
                if (instance == null)
                    instance = new ChatApplication();
            }
        }
        return instance;
    }

    public static synchronized ChatApplication getInstance() {
        return mInstance;
    }

    public static synchronized Context getContext() {
        return context;
    }

    public static Integer checkActivity(String name) {
        int value = -1;
        if (name.equals("mode") || name.equals("room")) {
            value = 1;
        } else if (name.equals("schedule") || name.equals("Timer")) {
            value = 0;
        } else if (name.equals("tempSensor") || name.equals("doorSensor")) {
            value = 0;
        }
        return value;
    }

    public static Integer checkSelection(String name, List<String> mListRoomMood) {
        int value = 0;
        if (name.equalsIgnoreCase("room")) {
            name = "Room";
        } else if (name.equalsIgnoreCase("mode")) {
            name = "Mood";
        } else if (name.equalsIgnoreCase("schedule")) {
            name = "Schedule";
        } else if (name.equalsIgnoreCase("Timer")) {
            name = "Timer";
        }
        for (int i = 0; i < mListRoomMood.size(); i++) {
            if (name.equals("" + mListRoomMood.get(i))) {
                value = i;
            }
        }

        return value;
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateHelper.DATE_D_MMM_YY_H_MM_AMPM);
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;

    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateHelper.DATE_D_MMM_YY_H_MM_AMPM);
        String currentDateandTime = sdf.format(new Date());
//yyyy-MM-dd HH:mm:ss
        return currentDateandTime;

    }

    public static String getCurrentDateTimeFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;

    }

    public static String getCurrentDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
//yyyy-MM-dd HH:mm:ss
        return currentDateandTime + " 00:01:00";

    }


    public static String getCurrentDateOnly(boolean isflag, String onTime, String offTime) {

        String currentDateandTime = "";
        Calendar calendar = Calendar.getInstance();

        if (isflag) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat sdfCurrent = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date currentDate = calendar.getTime();

            String currentStr = sdfCurrent.format(currentDate);

            if (offTime.length() > 1) {
                currentStr = currentStr + " " + offTime;
            } else {
                currentStr = currentStr + " " + onTime;
            }

            try {
                SimpleDateFormat sdfFormat = new SimpleDateFormat("EEE dd, MMM ");

                Date date1 = sdf.parse(currentStr);

                String str1 = sdf.format(new Date());
                Date date2 = sdf.parse(str1);

//                Date date2 = sdf.parse(calendar.getTime().toString());

//                if (new Date().after(date2)) {
                if (date1.after(date2)) {
                    currentDateandTime = sdfFormat.format(date1);
                } else {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrow = calendar1.getTime();

                    currentDateandTime = sdfFormat.format(tomorrow);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


//            Date tomorrow = calendar.getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd EEEE, MMM ");
//            currentDateandTime= sdf.format(tomorrow);
        } else {
            Date tomorrow = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM ");
            currentDateandTime = sdf.format(tomorrow);
        }

//yyyy-MM-dd HH:mm:ss
        return currentDateandTime;

    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
    }

    public static void logDisplay(String message) {
        if (BuildConfig.DEBUG) {
//            createFileLog(message);
            Log.d("System out", "" + message);
        }
    }

    public static String getUuid() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }


    public static List<User> getUserList(Context context) {
        List<User> userList = new ArrayList<>();
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText) && jsonText.length() > 2) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(jsonText, type);
        }
        return userList;
    }

    public static void keyBoardHideForce(Context activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }

    public static void keyBoardShow(Context activity, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void saveChoosedLock(LockObj lockObj) {
        this.mTestLockObj = lockObj;
    }

    public LockObj getChoosedLock() {
        return this.mTestLockObj;
    }

    public Socket openSocket(String url) {
        this.url = url;
        try {

            String newURL = url;
            if (url.startsWith("4")) {
                newURL = url.substring(3, url.length());      // dev arpan add due to add 443 before the string
            }

            String urltoken = newURL + "?" + "token=" + Common.getPrefValue(this, Constants.USER_ID);
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
//            opts.reconnectionDelay=200;


//            mSocket = IO.socket(newURL, opts);
            mSocket = IO.socket(urltoken, opts);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            ChatApplication.logDisplay("chat onDisconnect callll connect done ");
        } catch (URISyntaxException e) {
            ChatApplication.logDisplay("erro is " + e.toString());
            throw new RuntimeException(e);
        }
        return mSocket;
    }

    public Socket openCloudSocket(String url) {
        this.CloudUrl = url;
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
//            opts.reconnectionDelay=200;

            cloudsocket = IO.socket(url, opts);
            cloudsocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            ChatApplication.logDisplay("chat onDisconnect callll connect done ");
        } catch (URISyntaxException e) {
            ChatApplication.logDisplay("erro is " + e.toString());
            throw new RuntimeException(e);
        }
        return cloudsocket;
    }

    /**
     *
     */

    public void connectSocket() {
        if (mSocket != null)
            mSocket.connect();
    }

    @Override
    public void onTerminate() {
        ChatApplication.logDisplay("onterminal is call");
        if (mSocket != null) {
            mSocket.disconnect();
        }

        if (cloudsocket != null) {
            cloudsocket.disconnect();
        }
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
            if (mSocket != null) {
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

    public void closecloudSocket(String url) {
        // this.url = url;
        try {
            //IO.Options options = new IO.Options();
            // options.forceNew=true;
            //IO.Options opts = new IO.Options();
            //opts.forceNew = true;
//            opts.query = "auth_token=" + authToken;
            if (cloudsocket != null) {
                cloudsocket.disconnect();
                cloudsocket.close();
                cloudsocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return mSocket;
    }

    public Socket getCloudSocket() {
        return cloudsocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ChatApplication.logDisplay("on trim memory create");
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        context = this;
        //  ACRA.init(this);
        // ACRA.getErrorReporter().removeAllReportSenders();
        //ACRA.getErrorReporter().setReportSender(new CustomReportSender(this.getApplicationContext()));
// Install the application crash handler
        // ApplicationCrashHandler.installHandler();

        //beacon
        /*mDeviceStore = new BluetoothLeDeviceStore();
        BluetoothUtils mBluetoothUtils = new BluetoothUtils(getContext());
        BluetoothLeScanner mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);

        final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (isBluetoothOn && isBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);
        }*/

        TypefaceUtil.overrideFont(getApplicationContext(), "axiforma-Regular", "fonts/Axiforma-Regular.otf");


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    //beacon

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
