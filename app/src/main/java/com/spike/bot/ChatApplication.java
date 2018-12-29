package com.spike.bot;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.spike.bot.core.Constants;
import com.spike.bot.core.CustomReportSender;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

@ReportsCrashes(formKey = "",//1LZjNZVtIyc5O2-llj8VfHMsvDC2YdGkgNp3RiIGBL2I", // will not be used 1tgWQ58TdbvD0CGhW91Se2MwCFz_nlV1sezQrimsw6qw
//mailTo = "kaushalap@gochetak.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
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
        Log.e("Socket", "openSocket openSocket = " + url );
        try {
          //  IO.Options options = new IO.Options();
         //   options.forceNew = true;
            //IO.Options opts = new IO.Options();
            //opts.forceNew = true;
//            opts.query = "auth_token=" + authToken;

            mSocket = IO.socket(url);


            Log.d("","ChatApplication ");
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
        Log.d("onTerminate","onTemrmiate...");
    }

    public void closeSocket(String url) {
       // this.url = url;
        Log.e("Socket", "openSocket closeSocket = " + url );
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
            Log.d("","ChatApplication closeSocket");
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
        mInstance = this;
        context = this;
        ACRA.init(this);
        ACRA.getErrorReporter().removeAllReportSenders();
        ACRA.getErrorReporter().setReportSender(new CustomReportSender(this.getApplicationContext()));

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
}
