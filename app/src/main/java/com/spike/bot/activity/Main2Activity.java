package com.spike.bot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.ack.SocketManager;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.Log;
import com.spike.bot.customview.CustomViewPager;
import com.spike.bot.fcm.MyFirebaseMessagingService;
import com.spike.bot.fragments.MainFragment;
import com.spike.bot.fragments.MoodFragment;
import com.spike.bot.fragments.ScheduleFragment;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.listener.RouterIssue;
import com.spike.bot.listener.RunServiceInterface;
import com.spike.bot.listener.SocketListener;
import com.spike.bot.model.User;
import com.spike.bot.receiver.ConnectivityReceiver;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import io.fabric.sdk.android.Fabric;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.ChatApplication.getContext;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener, CloudAdapter.CloudClickListener,
        MainFragment.OnHeadlineSelectedListener, RunServiceInterface, ResponseErrorCode, SocketListener, LoginPIEvent, RouterIssue {

    private static final String TAG = "Main2Activity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private WifiManager.MulticastLock lock;

    public static ImageView mToolBarSettings;
    public static TabLayout tabLayout;
    public static int lastTabPos = 0;
    public static TextView toolbarTitle;
    public static TextView txt_error_value;
    public static boolean flagPicheck = false, flagLogin = false;
    public boolean isFlagWifi = false, isLoginTimeCount = false;
    public ImageView deepsImage, toolbarImage, mImageCloud;
    TextView txt_connection, txt_add_acoount;
    LinearLayout linear_main, linear_retry, linear_progress, linear_login;
    FrameLayout linearCloud;
    EditText et_username, et_password;
    Button btn_login, button_retry, btn_cancel, btnSignUp, btn_SKIP;
    public Toolbar toolbar;
    List<User> userList;
    // PopupWindow popupWindow;
    private CloudAdapter cloudAdapter;
    private RecyclerView recyclerView;
    PowerManager.WakeLock wakeLock;
    private ProgressDialog progressBar;
    public MainFragment mainFragment1;

    //current

    /**
     * If Network is connected on cloud then display cloud icon otherwise display wifi icon
     */
    public void invalidateToolbarCloudImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(ChatApplication.url)) {
                    if (ChatApplication.url.startsWith("http://home.deepfoods")) {
                        mImageCloud.setImageResource(R.drawable.cloud);
                    } else {
                        mImageCloud.setImageResource(R.drawable.wifi);
                    }
                }
//                if (WifiBlasterActivity.setMobileDataEnabled(Main2Activity.this)) {
//                    mImageCloud.setImageResource(R.drawable.cloud);
//                } else {
//                    mImageCloud.setImageResource(R.drawable.wifi);
//                }

            }
        });

    }

    private int[] navIcons = {
            R.drawable.dasboard_drey,
            R.drawable.mood_grey,
            R.drawable.schedule_grey
    };
    private int[] navLabels = {
            R.string.menu_dashboard1,
            R.string.menu_mood1,
            R.string.menu_schedule1
    };
    // another resources array for active state for the icon
    private int[] navIconsActive = {
            R.drawable.dasboard_blue,
            R.drawable.mood_blue,
            R.drawable.schedule_blue
    };


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        ChatApplication.getInstance().setConnectivityListener(this);

        MyFirebaseMessagingService.badge = "0";
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userFname = Common.getPrefValue(ChatApplication.getInstance(), "first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(), "last_name");

        // setTitle(""+userFname + " " + userLname);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        token = FirebaseInstanceId.getInstance().getToken();

        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mImageCloud = (ImageView) findViewById(R.id.toolbar_cloud);
        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        toolbarImage.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        deepsImage = (ImageView) findViewById(R.id.deepsImage);
        txt_connection = (TextView) findViewById(R.id.txt_connection);
        linear_main = (LinearLayout) findViewById(R.id.linear_main);
        //retry
        linear_retry = (LinearLayout) findViewById(R.id.linear_retry);
        button_retry = (Button) findViewById(R.id.button_retry);
        linear_progress = (LinearLayout) findViewById(R.id.linear_progress);
        // login details
        linear_login = (LinearLayout) findViewById(R.id.linear_login);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        mToolBarSettings = (ImageView) findViewById(R.id.toolbar_setting);
        btn_SKIP = findViewById(R.id.btn_SKIP);
        linearCloud = (FrameLayout) findViewById(R.id.linear_progress_cloud);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        wakeLock.acquire();

        MainFragment.clearNotification(Main2Activity.this);
        //new view
        // ll_show_error = (LinearLayout) findViewById(R.id.ll_show_error);
        txt_error_value = (TextView) findViewById(R.id.txt_error_value);

        btn_login.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_SKIP.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        if (mSectionsPagerAdapter == null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);

            // loop through all navigation tabs
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                final LinearLayout tabTemp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
                TextView tab_label = (TextView) tabTemp.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabTemp.findViewById(R.id.nav_icon);

                String name = "<b>" + getResources().getString(navLabels[i]) + "</b> ";
                tab_label.setText(Html.fromHtml(name));

                if (i == 0) {
                    tab_label.setTextColor(getResources().getColor(R.color.sky_blue));
                    tab_icon.setImageResource(navIconsActive[i]);
                } else {
                    tab_label.setTextColor(getResources().getColor(R.color.txtPanal));
                    tab_icon.setImageResource(navIcons[i]);
                }
                tabLayout.getTabAt(i).setCustomView(tabTemp);
            }
        }
        tabClickItem();

        if (!Constants.checkLoginAccountCount(this)) {
            showLogin();
            return;
        }


        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnected()) {
                    hideAlertDialog();

                    //check cloud url
                    String isLogin = Common.getPrefValue(getApplicationContext(), Constants.PREF_CLOUDLOGIN);
                    String cloudIp = Common.getPrefValue(getApplicationContext(), Constants.PREF_IP);
                    if (userId.equalsIgnoreCase("0") && isLogin.equalsIgnoreCase("true") && cloudIp.equalsIgnoreCase(webUrl)) {
                        startSocketConnection();
                    } else {
                        runService();
                    }

                    //  runService();

                } else {
                    //deepsImage.setVisibility(View.VISIBLE);
                    linear_progress.setVisibility(View.VISIBLE);
                    hideAlertDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //deepsImage.setVisibility(View.GONE);
                            linear_progress.setVisibility(View.GONE);
                            showAlertDialog(ERROR_STRING);
                        }
                    }, 100);
                }
            }
        });
        isResumeConnect = true;


        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        userList = new ArrayList<User>();

        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {}.getType();
            userList = gson.fromJson(jsonText, type);
            // toolbarImage.setVisibility(View.VISIBLE);
            // getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //  toolbarTitle.setText("" + userFname + " " + userLname);
        toolbarTitle.setText(Constants.getUserName(this));
        toolbarTitle.setOnClickListener(this);
        mImageCloud.setOnClickListener(this);
        mToolBarSettings.setOnClickListener(this);


//        btn_login_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Common.isConnected()) {
//                    hideAlertDialog();
//                    loginDialog(false, false);
//                    runService();
//                }
//            }
//        });
    }

    private void getUUId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            } else {
                imei = ActivityHelper.getIMEI(this);
            }
        } else {
            imei = ActivityHelper.getIMEI(this);
        }

    }

    private void tabClickItem() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 1. get the custom View you've added
                View tabView = tab.getCustomView();
                // get inflated children Views the icon and the label by their id
                TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                // change the label color, by getting the color resource value
                tab_label.setTextColor(getResources().getColor(R.color.sky_blue));
                // change the image Resource
                // i defined all icons in an array ordered in order of tabs appearances
                // call tab.getPosition() to get active tab index.
                tab_icon.setImageResource(navIconsActive[tab.getPosition()]);
                //   reloadFragment2();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                // back to the black color
                tab_label.setTextColor(getResources().getColor(R.color.txtPanal));
                // and the icon resouce to the old black image
                // also via array that holds the icon resources in order
                // and get the one of this tab's position
                tab_icon.setImageResource(navIcons[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private String ERROR_STRING = "No Internet found.\n" +
            "Check your connection or try again.";
    private String SPIKE_BOT_DOWN = "Spike Bot Server is Down!!!";

    @Override
    public void onErrorCode(int errorResponseCode) {
        showAlertDialog(SPIKE_BOT_DOWN);
    }

    @Override
    public void executeService() {
        // toolbarTitle.setText("      ");
        runService2();

    }

    /**
     * @param name
     */

    @Override
    public void onArticleSelected(String name) {
        if (toolbarTitle != null)
            toolbarTitle.setText(Constants.getUserName(this));
//            toolbarTitle.setText(name);
        else {
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
//            toolbarTitle.setText(name);
            toolbarTitle.setText(Constants.getUserName(this));
            toolbarTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAccountDialog(toolbarTitle);
                }
            });
        }
    }

    public static boolean isClick = false;
    public static String CLOUD_MESSAGE = "Connecting to cloud...";
    public static String LOCAL_MESSAGE = "Connecting...";

    private void showConnectingCloudDialog(String message) {
        if (progressBar == null) {
            progressBar = new ProgressDialog(Main2Activity.this);
        }
        progressBar.setCancelable(false);
        progressBar.setMessage(message);
        progressBar.show();
        // linearCloud.setVisibility(View.VISIBLE);
        //mViewPager.setVisibility(View.GONE);
    }

    private void hideConnectingCloudDialog() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.setMessage("Connected...");
            progressBar.dismiss();
            progressBar = null;
        }
        // mViewPager.setVisibility(View.VISIBLE);
        // linearCloud.setVisibility(View.GONE);
    }


    @Override
    public void onLogout(boolean isLogout) {
        if (isLogout) {
            logoutCloudUser();
        }
    }

    @Override
    public void onLogout() {
        logoutCloudUser();
    }

    /**
     * click on switch account user {@link CloudAdapter#
     * CloudAdapter(List, CloudAdapter.CloudClickListener)}}
     *
     * @param user
     */


    @Override
    public void userSelectclick(User user) {
        if (dialogUser != null) {
            dialogUser.dismiss();
        }
        if (Constants.isWifiConnect) {
            return;
        }
        if (!TextUtils.isEmpty(user.getCloudIP())) {
            showConnectingCloudDialog(CLOUD_MESSAGE);

            linear_progress.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String jsonCurProduct = gson.toJson(userList);
            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

            String cloudIp = "", local_ip = "";
            String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
            if (!TextUtils.isEmpty(jsonText)) {
                Type type = new TypeToken<List<User>>() {
                }.getType();
                userList = gson.fromJson(jsonText, type);

                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getUser_id().equalsIgnoreCase(user.getUser_id())) {
                        userList.get(i).setIsActive(true);
                        cloudIp = userList.get(i).getCloudIP();
                        local_ip = userList.get(i).getLocal_ip();
                    } else {
                        userList.get(i).setIsActive(false);
                    }
                }

                String json = gson.toJson(userList);
                Common.savePrefValue(getApplicationContext(), Common.USER_JSON, json);
            }

            toolbarTitle.setText(user.getFirstname());

            if (cloudAdapter != null) {
                cloudAdapter.notifyDataSetChanged();
            }

            Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
            Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, user.getCloudIP());
            Common.savePrefValue(Main2Activity.this, Constants.USER_ID, user.getUser_id());
            Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, user.getPassword());

            Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, user.getAdmin());
            setUserTypeValue();
            if (Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                Constants.room_type = 0;
                Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 0);

            } else {
                Constants.room_type = 2;
                Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 2);
            }


            setLoginFlow(local_ip, cloudIp);

//            if (Common.isNetworkConnected(Main2Activity.this)) {
//                isWifiConnect = true;
//
//                InetAddress addr = getLocalIpAddress(Main2Activity.this);
//                String hostname = addr.toString().replace("/", "");
//                String[] array = hostname.split("\\.");
//
//                String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
//
//                if (ipAddressPI.equalsIgnoreCase(local_ip)) {
//                    webUrl = ipAddressPI;
//                    ChatApplication.url = webUrl;
//                    flagPicheck = false;
//                    listService("\nService resolved: ", webUrl, 80, true, false);
//                    loginDialog(false, false);
//                    reloadFragment();
//                } else {
//                    isNetwork = true;
//                    webUrl = cloudIp;
//                    ChatApplication.url = webUrl;
//                    isCloudConnected = true;
//                    flagPicheck = false;
//
//                    openSocket();
//                    isResumeConnect = false;
//                    flagLogin = true;
//                    ChatApplication.isRefreshHome = false;
//                }
//
//            } else {
//                isNetwork = true;
//                webUrl = cloudIp;
//                ChatApplication.url = webUrl;
//                isCloudConnected = true;
//                flagPicheck = false;
//
//                openSocket();
//                isResumeConnect = false;
//                flagLogin = true;
//                ChatApplication.isRefreshHome = false;
//            }
//
//            ChatApplication app = ChatApplication.getInstance();
//            app.closeSocket(webUrl);
//
//            if (mSocket != null) {
//                mSocket.disconnect();
//                mSocket = null;
//            }
//
//            startSocketConnection();
//            isFlagWifi = false;
//            isResumeConnect = false;
//            mainFragment1.RefreshAnotherFragment();
//            mViewPager.setCurrentItem(0);
//            mainFragment1.showDialog = 1;
        }

    }

    private void listServiceTemp(final String msg, String ip, final int port, final boolean isFound, boolean isShow) {

        final String openIp = (isFound) ? "http://" + ip + ":" + port : Constants.CLOUD_SERVER_URL;
        ChatApplication.logDisplay("openIp is " + openIp);
        webUrl = openIp;

//        if (!ip.equalsIgnoreCase("") && !webUrl.equalsIgnoreCase(openIp)) {
//            webUrl = openIp;
//            openSocket();
//        } else if (ip.equalsIgnoreCase("")) {
//            openSocket();
//        } else if (webUrl.equalsIgnoreCase(openIp)) {
//            if (!isFound) {
//                hideAlertDialog();
//                linear_progress.setVisibility(View.GONE);
//            }
//        }

    }

    private void setLoginFlow(String local_ip, String cloudIp) {
        ChatApplication app = ChatApplication.getInstance();
        app.closeSocket(webUrl);

        if (mSocket != null) {
            mSocket.disconnect();
            mSocket = null;
        }

        loginDialog(false, false);

        linear_retry.setVisibility(View.GONE);
        linear_main.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mToolBarSettings.setVisibility(View.VISIBLE);
        toolbarTitle.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        linear_login.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);

        if(mainFragment1==null){
//            mViewPager.setCurrentItem(0);
//            mainFragment1 = (MainFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
//            mViewPager.getAdapter().getItemPosition(0);
        }

        if (Common.isNetworkConnected(Main2Activity.this)) {
            isWifiConnect = true;

            InetAddress addr = getLocalIpAddress(Main2Activity.this);
            String hostname = addr.toString().replace("/", "");
            String[] array = hostname.split("\\.");

            String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;

            if (ipAddressPI.equalsIgnoreCase(local_ip)) {
                webUrl = ipAddressPI;
                ChatApplication.url = webUrl;
                flagPicheck = false;
                listServiceTemp("\nService resolved: ", webUrl, 80, true, false);
//                loginDialog(false, false);
//                reloadFragment();
            } else {
                isNetwork = true;
                webUrl = cloudIp;
                ChatApplication.url = webUrl;
                isCloudConnected = true;
                flagPicheck = false;
//                listServiceTemp("\nService resolved: ", webUrl, 80, false, false);
//                openSocket();
                isResumeConnect = false;
                flagLogin = true;
                ChatApplication.isRefreshHome = false;
            }

        } else {
            isNetwork = true;
            webUrl = cloudIp;
            ChatApplication.url = webUrl;
            isCloudConnected = true;
            flagPicheck = false;
//            listServiceTemp("\nService resolved: ", webUrl, 80, false, false);

//            openSocket();
            isResumeConnect = false;
            flagLogin = true;
            ChatApplication.isRefreshHome = false;
//            mainFragment1.getDeviceList(1);
        }

        startSocketConnectionNew();
        if(mainFragment1==null){
//            mViewPager.setCurrentItem(0);
//
//            mainFragment1 = (MainFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
//            mainFragment1.onResume();
//            mainFragment1.onRefresh();
        }else {
            if (mViewPager.getCurrentItem() != 0) {
                mViewPager.setCurrentItem(0);
            } else {
                mainFragment1.getDeviceList(1);
            }
        }
//        startSocketConnection();
        isFlagWifi = false;
        isResumeConnect = false;
//        mainFragment1.RefreshAnotherFragment();

        flagLogin = true;
        ChatApplication.isRefreshHome = false;
        invalidateToolbarCloudImage();
        getUserDialogClick(true);
        mainFragment1.showDialog = 1;
    }

    /**
     *
     */
    @Override
    public void startSession() {
    }


    /**
     * show Account Dialog with toolbar title
     *
     * @param toolbarTitle
     */

    public Dialog dialogUser;

    private void showAccountDialog(TextView toolbarTitle) {

        if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {

            return;
        }


        if (TextUtils.isEmpty(Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE))) {
            return;
        }
//        else {
//            if(Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")){
//                return;
//            }
//        }

        if (dialogUser == null) {
            dialogUser = new Dialog(this);
        }
//        dialogUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUser.setCanceledOnTouchOutside(false);
        dialogUser.setContentView(R.layout.popup_toolbar);
        dialogUser.getWindow().setGravity(Gravity.TOP);
        dialogUser.setCanceledOnTouchOutside(true);
        dialogUser.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogUser.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_add_acoount = (TextView) dialogUser.findViewById(R.id.txt_add_account);
        recyclerView = (RecyclerView) dialogUser.findViewById(R.id.userList);
        TextView txtOutside = dialogUser.findViewById(R.id.txtOutside);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        if (userList == null) {
            userList = new ArrayList<>();
        }
        userList.clear();

        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(jsonText, type);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        if (userList != null) {
            cloudAdapter = new CloudAdapter(Main2Activity.this, userList, this);
            recyclerView.setAdapter(cloudAdapter);
        }

        txtOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUser.dismiss();
            }
        });

        txt_add_acoount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUser.dismiss();
                hideAlertDialog();
                loginDialog(true, true);
            }
        });

        if (!dialogUser.isShowing()) {
            dialogUser.show();
        }
    }

    private void showIpInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input IP");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChatApplication.testIP = input.getText().toString();
                runService();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private ConnectivityReceiver connectivityReceiver;

    @Override
    public void onResume() {
        super.onResume();

        if (!Constants.checkLoginAccountCount(this)) {
            getUUId();
            showLogin();
            return;
        }

        //update user fname and lname after updaing the profile activity
        String userFname = Common.getPrefValue(ChatApplication.getInstance(), "first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(), "last_name");
        if (ChatApplication.isRefreshUserData) {
            if (!TextUtils.isEmpty(userFname) && !TextUtils.isEmpty(userLname)) {
                if (toolbarTitle != null) {
                    //toolbarTitle.setText(userFname + " " + userLname);
                    toolbarTitle.setText(Constants.getUserName(this));
                }
            }
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setOnClickListener(this);
        ChatApplication.getInstance().setConnectivityListener(this);
        tabClickItem();
        if (Common.isConnected() && isResumeConnect) {
            hideAlertDialog();
            runService();
            isResumeConnect = false;
        } else if (Common.isConnected() && ChatApplication.isRefreshHome) {
            hideAlertDialog();
            loginDialog(false, false);
            linear_progress.setVisibility(View.GONE);
            ChatApplication.isRefreshHome = false;
        } else if (!Common.isConnected()) {
            hideAlertDialog();
            showAlertDialog(ERROR_STRING);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ChatApplication.isPushFound) {
            MainFragment.getBadgeClear(Main2Activity.this);
        }
        MainFragment.isRefredCheck = true;
        ChatApplication.isMainFragmentNeedResume = true;

    }

    private Emitter.Listener deleteChildUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (Main2Activity.this == null) {
                return;
            }
            Main2Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String message = object.optString("message");
                            String user_id = object.optString("user_id");
                            if (Common.getPrefValue(Main2Activity.this, Constants.USER_ID).equalsIgnoreCase(user_id)) {

                                if (Common.isNetworkConnected(Main2Activity.this)) {
                                    isWifiConnect = true;
                                    wifiIpAddress = "";
                                    asyncTask = new Main2Activity.ServiceEventAsync(Constants.getuserIp(Main2Activity.this)).execute();
                                }

                                childRemoveSQLite();
                                ChatApplication.showToast(Main2Activity.this, message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatApplication.logDisplay("onterminal is call onDestroy ");

        if (mSocket != null) {
            mSocket.emit(TAG, "android == disconnect " + mSocket.id());
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on("deleteChildUser", deleteChildUser);
            mSocket = null;
        }
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
        try {

            if (lock != null) {
                if (lock.isHeld()) {
                    lock.release();
                }
            }


            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       /* getMenuInflater().inflate(R.menu.menu_main2, menu);
        MenuItem menuItem = menu.findItem(R.id.showIpDialog);
        menuItem.setVisible(Constants.isTesting);
        MenuItem settingIcon = menu.findItem(R.id.action_settings);
        if(isCloudConnected){
            settingIcon.setIcon(R.drawable.icn_setting_cloud);
        }else{
            settingIcon.setIcon(R.drawable.settings_white);
        }*/
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettingPopup(toolbar);
            return true;
        }
        if (id == R.id.showIpDialog) {
            showIpInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSettingPopup(final View v) {//,final ICallBackAction actionCallBack
        PopupMenu popup = null;
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_room_setting_popup, popup.getMenu());

        if (Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            MenuItem menuItemReset = popup.getMenu().findItem(R.id.action_profile);
            menuItemReset.setVisible(false);
        } else {
            MenuItem menuItemReset = popup.getMenu().findItem(R.id.action_profile);
            menuItemReset.setVisible(true);
        }


        String isLogin = Common.getPrefValue(this, Constants.PREF_CLOUDLOGIN);
        String cloudIp = Common.getPrefValue(this, Constants.PREF_IP);
        if (userId.equalsIgnoreCase("0") && isLogin.equalsIgnoreCase("true") && cloudIp.equalsIgnoreCase(webUrl)) {
            isCloudConnected = true;
        } else {
            isCloudConnected = false;
        }
        if (!isCloudConnected) {
//            MenuItem action_logout = popup.getMenu().findItem(R.id.action_logout);
//            action_logout.setVisible(false);
        }
        //  invalidateToolbarCloudImage();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(activity, " moveAction - "  ,Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_device_log:
                        // getconfigureData();
                        Intent intent = new Intent(Main2Activity.this, DeviceLogActivity.class);
                        intent.putExtra("isCheckActivity", "AllType");
                        startActivity(intent);
                        break;
                    case R.id.action_sensor_log:
                        Intent intent1 = new Intent(Main2Activity.this, DeviceLogActivity.class);
                        intent1.putExtra("IS_SENSOR", true);
                        startActivity(intent1);
//                        Intent logIntent = new Intent(Main2Activity.this,SensorDoorLogActivity.class);
//                        logIntent.putExtra("is_global",true);
//                        startActivity(logIntent);
                        break;
                    case R.id.action_notification_settings:
                        Intent intentNotification = new Intent(Main2Activity.this, NotificationSetting.class);
                        startActivity(intentNotification);
                        break;
                    case R.id.action_profile:
                        Intent intentProfile = new Intent(Main2Activity.this, ProfileActivity.class);
                        startActivity(intentProfile);
                        break;
                    case R.id.action_logout:
                        //paster here
                        logoutCloudUser();
//                        childRemoveSQLite();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void childRemoveSQLite() {

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        //clear pref and open login screen
        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "false");
        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, "");

        ///start//

        final Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        String USER_ID = Common.getPrefValue(getApplicationContext(), Constants.USER_ID);

        List<User> uList = new ArrayList<User>();
        final List<User> tempList = new ArrayList<User>();

        if (!TextUtils.isEmpty(jsonText)) {

            Type type = new TypeToken<List<User>>() {
            }.getType();
            uList = gson.fromJson(jsonText, type);

            boolean isFoundUser = false;
            User logOutUser = new User();

            for (User user : uList) {
                if (!user.isActive()) {
                    tempList.add(user);
                } else {
                    isFoundUser = true;
                    logOutUser = user;
                    // break;
                }
            }

            if (isFoundUser) {

//
//                new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        ActivityHelper.dismissProgressDialog();
                int code = 0;


                String jsonCurProduct = gson.toJson(tempList);
                ChatApplication.logDisplay("user name logout: " + tempList.size());

                if (tempList.size() == 0) {
                    Common.savePrefValue(getApplicationContext(), Common.USER_JSON, "");
                } else {

                    tempList.get(0).setIsActive(true);
                    jsonCurProduct = gson.toJson(tempList);
                    Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);
                }

                if (cloudAdapter != null) {
                    cloudAdapter.notifyDataSetChanged();
                }

                if (tempList.size() > 0) {
                    try {
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, tempList.get(0).getCloudIP());
                        Common.savePrefValue(Main2Activity.this, Constants.USER_ID, tempList.get(0).getUser_id());
                        Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, tempList.get(0).getPassword());
                        Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, tempList.get(0).getAdmin());

                        if (Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                            Constants.room_type = 0;
                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 0);
                        } else {
                            Constants.room_type = 2;
                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 2);
                        }

                        webUrl = tempList.get(0).getCloudIP();
                        isCloudConnected = true;
                        invalidateToolbarCloudImage();

                        toolbarTitle.setText(tempList.get(0).getFirstname());

                        ChatApplication app = ChatApplication.getInstance();
                        app.closeSocket(webUrl);

                        if (mSocket != null) {
                            mSocket.disconnect();
                            mSocket = null;
                        }
                        ActivityHelper.dismissProgressDialog();

                        startSocketConnection();
                        mainFragment1.RefreshAnotherFragment();
                        mViewPager.setCurrentItem(0);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ActivityHelper.dismissProgressDialog();
                    Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "false");
                    Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, "");
                    Common.savePrefValue(Main2Activity.this, Constants.USER_ID, "");
                    Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, "");
                    Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, "");

                    webUrl = "";
                    ChatApplication app = ChatApplication.getInstance();
                    app.closeSocket(webUrl);

                    flagPicheck = true;
                    loginDialog(true, true);
//                                    mViewPager.setCurrentItem(0);
//                                    runService();
                }


            }


        } else {
            ChatApplication app = ChatApplication.getInstance();
            //  mSocket = app.closeSocket(webUrl);
            app.closeSocket(webUrl);
            loginDialog(true, false);
        }


    }

    public void logoutCloudUser() {

        //clear pref and open login screen
        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "false");
        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, "");

        ///start//

        final Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        String USER_ID = Common.getPrefValue(getApplicationContext(), Constants.USER_ID);

        List<User> uList = new ArrayList<User>();
        final List<User> tempList = new ArrayList<User>();

        if (!TextUtils.isEmpty(jsonText)) {

            Type type = new TypeToken<List<User>>() {
            }.getType();
            uList = gson.fromJson(jsonText, type);

            boolean isFoundUser = false;
            User logOutUser = new User();

            for (User user : uList) {
                if (!user.isActive()) {
                    tempList.add(user);
                } else {
                    isFoundUser = true;
                    logOutUser = user;
                    // break;
                }
            }

            if (isFoundUser) {
                //logoutUser();
                //TODO code for logout api call
                String url = Constants.CLOUD_SERVER_URL + Constants.APP_LOGOUT;

                JSONObject object = new JSONObject();
                try {

                    String device_push_token = Common.getPrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN);

                    object.put("user_id", "" + logOutUser.getUser_id());
                    object.put("device_push_token", "" + device_push_token);
                    object.put("device_id", APIConst.PHONE_ID_VALUE);
                    object.put("device_type", APIConst.PHONE_TYPE_VALUE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatApplication.logDisplay("logout is " + object.toString());
                ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

                new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        ActivityHelper.dismissProgressDialog();
                        int code = 0;
                        try {
                            code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                String jsonCurProduct = gson.toJson(tempList);
                                ChatApplication.logDisplay("user name logout: " + tempList.size());

                                if (tempList.size() == 0) {
                                    Common.savePrefValue(getApplicationContext(), Common.USER_JSON, "");
                                } else {
                                    ChatApplication.logDisplay("user name logout: " + tempList.get(0).getFirstname() + " " + tempList.get(0).getLastname());

                                    tempList.get(0).setIsActive(true);
                                    jsonCurProduct = gson.toJson(tempList);
                                    Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);
                                }

                                if (cloudAdapter != null) {
                                    cloudAdapter.notifyDataSetChanged();
                                }

                                if (tempList.size() > 0) {
                                    try {
                                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, tempList.get(0).getCloudIP());
                                        Common.savePrefValue(Main2Activity.this, Constants.USER_ID, tempList.get(0).getUser_id());
                                        Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, tempList.get(0).getPassword());
                                        Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, tempList.get(0).getAdmin());

                                        if (Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                                            Constants.room_type = 0;
                                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 0);
                                        } else {
                                            Constants.room_type = 2;
                                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 2);
                                        }

                                        setLoginFlow(tempList.get(0).getLocal_ip(),tempList.get(0).getCloudIP());

//                                        if (Common.isNetworkConnected(Main2Activity.this)) {
//                                            isWifiConnect = true;
//
//                                            InetAddress addr = getLocalIpAddress(Main2Activity.this);
//                                            String hostname = addr.toString().replace("/", "");
//                                            String[] array = hostname.split("\\.");
//
//                                            String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
//
//                                            if (ipAddressPI.equalsIgnoreCase(tempList.get(0).getLocal_ip())) {
//                                                webUrl = ipAddressPI;
//                                                ChatApplication.url = webUrl;
//                                                flagPicheck = false;
//                                                listService("\nService resolved: ", webUrl, 80, true, false);
//                                                loginDialog(false, false);
//                                                reloadFragment();
//                                            } else {
//                                                isNetwork = true;
//                                                webUrl = tempList.get(0).getCloudIP();
//                                                ChatApplication.url = webUrl;
//                                                isCloudConnected = true;
//                                                flagPicheck = false;
//
//                                                openSocket();
//                                                isResumeConnect = false;
//                                                flagLogin = true;
//                                                ChatApplication.isRefreshHome = false;
//                                            }
//
//                                        } else {
//                                            isNetwork = true;
//                                            webUrl = tempList.get(0).getCloudIP();
//                                            ;
//                                            ChatApplication.url = webUrl;
//                                            isCloudConnected = true;
//                                            flagPicheck = false;
//
//                                            openSocket();
//                                            isResumeConnect = false;
//                                            flagLogin = true;
//                                            ChatApplication.isRefreshHome = false;
//                                        }
//
//                                        ChatApplication app = ChatApplication.getInstance();
//                                        app.closeSocket(webUrl);
//
//                                        if (mSocket != null) {
//                                            mSocket.disconnect();
//                                            mSocket = null;
//
//                                        }
//                                        startSocketConnection();
//                                        isFlagWifi = false;
//                                        isResumeConnect = false;
//                                        mainFragment1.RefreshAnotherFragment();
//                                        mViewPager.setCurrentItem(0);
//                                        mainFragment1.showDialog = 1;


//                                        if (Common.isNetworkConnected(Main2Activity.this)) {
//                                            isWifiConnect = true;
//
//                                            InetAddress addr = getLocalIpAddress(Main2Activity.this);
//                                            String hostname = addr.toString().replace("/", "");
//                                            String[] array = hostname.split("\\.");
//
//                                            String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
//
//                                            if (ipAddressPI.equalsIgnoreCase(tempList.get(0).getLocal_ip())) {
//                                                webUrl = ipAddressPI;
//                                                ChatApplication.url = webUrl;
//                                                flagPicheck = false;
//                                                listService("\nService resolved: ", webUrl, 80, true, false);
//                                                loginDialog(false, false);
////                                                reloadFragment();
//                                            } else {
//                                                isNetwork = true;
//                                                webUrl = tempList.get(0).getCloudIP();
//                                                ChatApplication.url = webUrl;
//                                                isCloudConnected = true;
//                                                flagPicheck = false;
//
//                                                openSocket();
//                                                isResumeConnect = false;
//                                                flagLogin = true;
//                                                ChatApplication.isRefreshHome = false;
//                                            }
//
//                                        } else {
//                                            isNetwork = true;
//                                            webUrl = tempList.get(0).getCloudIP();
//                                            ChatApplication.url = webUrl;
//                                            isCloudConnected = true;
//                                            flagPicheck = false;
//
//                                            openSocket();
//                                            isResumeConnect = false;
//                                            flagLogin = true;
//                                            ChatApplication.isRefreshHome = false;
//                                        }
//
//
//                                        toolbarTitle.setText(tempList.get(0).getFirstname());
//                                        ChatApplication app = ChatApplication.getInstance();
//                                        app.closeSocket(webUrl);
//
//                                        if (mSocket != null) {
//                                            mSocket.disconnect();
//                                            mSocket = null;
//                                        }
//                                        MainFragment.showDialog = 1;
//                                        startSocketConnection();
//                                        mainFragment1.RefreshAnotherFragment();
//                                        mViewPager.setCurrentItem(0);


                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "false");
                                    Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, "");
                                    Common.savePrefValue(Main2Activity.this, Constants.USER_ID, "");
                                    Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, "");
                                    Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, "");

                                    webUrl = "";
                                    ChatApplication app = ChatApplication.getInstance();
                                    app.closeSocket(webUrl);

                                    flagPicheck = true;
                                    loginDialog(true, true);
//                                    mViewPager.setCurrentItem(0);
//                                    runService();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                        ActivityHelper.dismissProgressDialog();
                    }
                }).execute();

            }


        } else {
            ChatApplication app = ChatApplication.getInstance();
            //  mSocket = app.closeSocket(webUrl);
            app.closeSocket(webUrl);
            loginDialog(true, false);
        }


        //end///

    }

    @Override
    public void onNetworkConnectionChanged(final boolean isConnected) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        if (ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
            mToolBarSettings.setVisibility(View.VISIBLE);
            ChatApplication.logDisplay("onNetworkConnectionChanged isConnected : " + isConnected);
            ChatApplication.logDisplay("onNetworkConnectionChanged isConnected isResumeConnect : " + isResumeConnect);
//            invalidateToolbarCloudImage();
            if (isConnected) {
                ChatApplication.logDisplay("onNetworkConnectionChanged isConnected : " + isConnected);

//                userSelectclick(Constants.getuser(this));

                isResumeConnect = false;
                MainFragment.showDialog = 1;
                ChatApplication app = ChatApplication.getInstance();
                app.closeSocket(webUrl);

                if (Common.isNetworkConnected(Main2Activity.this)) {
//                    mbn
//                    wifiIpAddress = "";
//                    asyncTask = new Main2Activity.ServiceEventAsync(Constants.getuserIp(Main2Activity.this)).execute();

                    isWifiConnect = true;

                    InetAddress addr = getLocalIpAddress(Main2Activity.this);
                    String hostname = addr.toString().replace("/", "");
                    String[] array = hostname.split("\\.");

                    String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;

                    if (ipAddressPI.equalsIgnoreCase(Constants.getuserIp(this))) {
                        webUrl = ipAddressPI;
                        ChatApplication.url = webUrl;
                        flagPicheck = false;
                        listService("\nService resolved: ", webUrl, 80, true, false);
                        loginDialog(false, false);
                        reloadFragment();
                    } else {
                        isNetwork = true;
                        webUrl = Constants.getCouldIp(this);
                        ChatApplication.url = webUrl;
                        isCloudConnected = true;
                        flagPicheck = false;

                        openSocket();
                        isResumeConnect = false;
                        flagLogin = true;
                        ChatApplication.isRefreshHome = false;
                    }

                } else {
                    invalidateToolbarCloudImage();
                    webUrl = Constants.getuserCloudIP(this);
                    ChatApplication.url = webUrl;
                    isCloudConnected = true;
                    flagPicheck = false;

                    openSocket();
                    isResumeConnect = false;
                    flagLogin = true;
                    ChatApplication.isRefreshHome = false;
                }

            } else {
                // webView.clearView();
                //webView.loadUrl("about:blank");
                //webView.setVisibility(View.GONE);
                linear_progress.setVisibility(View.GONE);

//            deepsImage.setVisibility(View.GONE);
//            txt_connection.setVisibility(View.GONE);
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
                isResumeConnect = true;
                hideAlertDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(ERROR_STRING);
                    }
                }, 100);
            }
        } else {

            final Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(6000);
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
                                    linear_progress.setVisibility(View.VISIBLE);
                                    mToolBarSettings.setVisibility(View.INVISIBLE);
                                    hideAlertDialog();
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
                                    //deepsImage.setVisibility(View.GONE);
                                    linear_progress.setVisibility(View.GONE);
                                    showAlertDialog(ERROR_STRING);
//                                        }
//                                    }, 100);
                                }

                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread1.start();


        }
    }

    @Override
    public void showLogin() {
//        if (piDetailsModel != null) {
        flagPicheck = true;
        loginDialog(true, true);
//        }
    }


    @Override
    public void wifiConnectionIssue(final boolean isflag) {
//        ChatApplication.logDisplay("is issue found");
//
//        new Thread() {
//            public void run() {
//                Main2Activity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        ProgressDialog progressDialog=new ProgressDialog(Main2Activity.this);
//                        progressDialog.setMessage("Please wait... \ncheck your gateway connection");
//                        progressDialog.setCancelable(false);
//                        if(isflag){
//                            progressDialog.dismiss();
//                        }else {
//                            progressDialog.show();
//                        }
//                    }
//                });
//            }
//        }.start();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        ArrayList<String> strList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentList.add(MainFragment.newInstance());
            fragmentList.add(MoodFragment.newInstance());
            fragmentList.add(ScheduleFragment.newInstance());
            //  fragmentList.add(MainFragment2.newInstance());
            //    fragmentList.add(DashBoardFragment.newInstance());
            strList.add("Dashboard");
            strList.add("Mood");
            strList.add("Schedule");
            //    strList.add("Dashboard2");
            //  strList.add("Room");
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strList.get(position);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(Main2Activity.this).inflate(R.layout.nav_tab, null);
            TextView tv = (TextView) v.findViewById(R.id.nav_label);
            tv.setText("Test");
            ImageView img = (ImageView) v.findViewById(R.id.nav_icon);
            img.setImageResource(navIconsActive[position]);
            return v;
        }

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mSocket != null) {
                        if (!isSocketConnected) {

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                isTimerStart = false;
                            }

                            hideAlertDialog();
                            isSocketConnected = true;
                            ChatApplication.isScheduleNeedResume = true;
                            //added by sagar
                            reloadFragment();

                            // end
                        }
                        mSocket.emit("socketconnection", "android == startconnect  " + mSocket.id());

                        //linear_login
                    }
                    mViewPager.setVisibility(View.VISIBLE);
                }
            });
        }
    };
    public Boolean isSocketConnected = true;
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null) {
                    }
                    //hideConnectingCloudDialog();
                    isSocketConnected = false;
                    //if swtich user account could't display error dialog
                    if (!isClick) {
                        //todo code here for open error dialog
                        //openErrorDialog(ERROR_STRING);
                    }
                }
            });
        }
    };

    private boolean isTimerEnd = false;
    private boolean isTimerStart = false;

    private CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            isTimerStart = true;
        }

        @Override
        public void onFinish() {
            hideConnectingCloudDialog();
            isTimerEnd = true;
        }
    };


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // hideConnectingCloudDialog();
                    if (mSocket != null) {
                    }
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT).show();

                    if (!isTimerStart)
                        countDownTimer.start();

                    //todo code here for open error dialog
                    if (isTimerEnd) {

                        //     openErrorDialog(ERROR_STRING);
                    }
                }
            });
        }
    };

    String imei = "";
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            imei = ActivityHelper.getIMEI(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {

            if (TextUtils.isEmpty(imei)) {
                getUUId();
            }
            if (TextUtils.isEmpty(et_username.getText().toString())) {
                et_username.requestFocus();
                et_username.setError("Enter User Name");
            } else if (TextUtils.isEmpty(et_password.getText().toString())) {
                et_password.requestFocus();
                et_password.setError("Enter Password");
            } else {


                // Muset login with cloud
//            if(flagPicheck){
//                loginWifiCloud();
//            }else {
                loginCloud(et_username.getText().toString(), et_password.getText().toString());
//            }
            }
        } else if (view.getId() == R.id.btn_cancel) {
            loginDialog(false, false);
        } else if (view.getId() == R.id.toolbar_cloud) {
            showAccountDialog(toolbarTitle);
        } else if (view.getId() == R.id.toolbarImage) {
            showAccountDialog(toolbarTitle);
        } else if (view.getId() == R.id.toolbarTitle) {
            showAccountDialog(toolbarTitle);
        } else if (view.getId() == R.id.btn_SKIP) {
            loginDialog(false, false);
        } else if (view.getId() == R.id.btn_SKIP) {
            loginDialog(false, false);
        } else if (view.getId() == R.id.btnSignUp) {
            Intent intent = new Intent(this, SignUp.class);
            startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
        } else if (view.getId() == R.id.toolbar_setting) {

            if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
                ChatApplication.showToast(Main2Activity.this, getResources().getString(R.string.disconnect));
            } else {
                openSettingPopup(toolbar);
            }
        }

    }

    public void getUserDialogClick(boolean isClick) {
        if (isClick) {
            toolbarImage.setClickable(true);
            toolbarTitle.setClickable(true);
            mToolBarSettings.setClickable(true);
            toolbarImage.setOnClickListener(this);
            toolbarTitle.setOnClickListener(this);
            mToolBarSettings.setOnClickListener(this);
        } else {
            toolbarImage.setClickable(false);
            toolbarTitle.setClickable(false);
        }
    }

    public String webUrl = "", wifiIpAddress = "";
    public static boolean isCloudConnected = false;
    public String token = "";
    boolean isWifiConnect = false, isNetwork = false;
    ;

    public void loginCloud(String username, String password) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(imei)) {
            getUUId();
        }


        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_name", et_username.getText().toString().trim());
            obj.put("user_password", et_password.getText().toString().trim());
            obj.put("device_id", imei);
            obj.put("device_type", "android");
            obj.put("device_push_token", token);
            obj.put("uuid", ChatApplication.getUuid());
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoginTimeCount = true;
        //callTheadCount();
        String url = Constants.CLOUD_SERVER_URL + Constants.APP_LOGIN;

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        ChatApplication.logDisplay("login is " + url + " " + obj.toString());
        //http://34.212.76.50/applogin
        new GetJsonTask(Main2Activity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    isLoginTimeCount = false;
                    if (code == 200) {
                        if (mainFragment1 != null && mainFragment1.sectionedExpandableLayoutHelper != null) {
                            mainFragment1.sectionedExpandableLayoutHelper.clearData();
                        }

                        ActivityHelper.hideKeyboard(Main2Activity.this);
//                        ChatApplication app = ChatApplication.getInstance();
//                        app.closeSocket(webUrl);
//
//                        if (mSocket != null) {
//                            mSocket.disconnect();
//                            mSocket = null;
//                        }

                        /*{"code":200,"message":"success","data":{"ip":"http:\/\/home.deepfoods.net:11113",
                        "user_id":"1528702842286_HyGYPsieQ",
                        "first_name":"spike","last_name":"bot","user_password":"c1bc06"}}
                        * */

                        JSONObject data = result.getJSONObject("data");
                        String cloudIp = data.getString("ip");
                        String local_ip = data.getString("local_ip");
                        ChatApplication.logDisplay("login response is " + data.toString());

                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, cloudIp);

                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        String user_id = data.getString("user_id");
                        String admin = data.getString("admin");
                        String mac_address = data.getString("mac_address");
                        Constants.adminType = Integer.parseInt(admin);

                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, cloudIp);
                        Common.savePrefValue(Main2Activity.this, Constants.USER_ID, user_id);
                        Common.savePrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE, admin);
                        Common.savePrefValue(Main2Activity.this, Constants.USER_TYPE, user_id);

                        if (Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                            Constants.room_type = 0;
                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 0);
                        } else {
                            Constants.room_type = 2;
                            Common.savePrefValue(Main2Activity.this, Constants.USER_ROOM_TYPE, "" + 2);
                        }
                        String user_password = "";
                        if (data.has("user_password")) {
                            user_password = data.getString("user_password");
                        }
                        Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, user_password);

                        if (toolbarTitle != null) {
                            toolbarTitle.setText(first_name);
                        }

                        User user = new User(user_id, first_name, last_name, cloudIp, false, user_password, admin, local_ip, mac_address);
                        invalidateToolbarCloudImage();
                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
                        ChatApplication.logDisplay("jsonText text is " + jsonText);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonText) && !jsonText.equals("[]") && !jsonText.equals("null")) {
                            Type type = new TypeToken<List<User>>() {
                            }.getType();
                            userList = gson.fromJson(jsonText, type);

                            if (userList != null && userList.size() != 0) {
                                boolean isFound = false;
                                for (User user1 : userList) {
                                    if (user1.getUser_id().equalsIgnoreCase(user.getUser_id())) {
                                        isFound = true;
                                        user1.setIsActive(true);
                                    } else {
                                        user1.setIsActive(false);
                                    }
                                }
                                if (!isFound) {
                                    user.setIsActive(true);
                                    userList.add(user);
                                }
                            }

                            String jsonCurProduct = gson.toJson(userList);
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

                        } else {

                            user.setIsActive(true);
                            userList.add(user);

                            String jsonCurProduct = gson.toJson(userList);
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);
                        }

                        if (cloudAdapter != null) {
                            cloudAdapter.notifyDataSetChanged();
                        }
                        if ((userList != null ? userList.size() : 0) == 0) {
                            if (recyclerView != null) {
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            if (recyclerView != null) {
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }

//                        setLoginFlow(local_ip, cloudIp);
                        if (Common.isNetworkConnected(Main2Activity.this)) {
                            isWifiConnect = true;

                            InetAddress addr = getLocalIpAddress(Main2Activity.this);
                            String hostname = addr.toString().replace("/", "");
                            String[] array = hostname.split("\\.");

                            String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;

                            if (ipAddressPI.equalsIgnoreCase(local_ip)) {
                                webUrl = ipAddressPI;
                                ChatApplication.url = webUrl;
                                flagPicheck = false;
                                listService("\nService resolved: ", webUrl, 80, true, false);
                                loginDialog(false, false);
                                reloadFragment();
                            } else {
                                isNetwork = true;
                                webUrl = cloudIp;
                                ChatApplication.url = webUrl;
                                isCloudConnected = true;
                                flagPicheck = false;

                                openSocket();
                                isResumeConnect = false;
                                flagLogin = true;
                                ChatApplication.isRefreshHome = false;
                            }

                        } else {
                            isNetwork = true;
                            webUrl = cloudIp;
                            ChatApplication.url = webUrl;
                            isCloudConnected = true;
                            flagPicheck = false;

                            openSocket();
                            isResumeConnect = false;
                            flagLogin = true;
                            ChatApplication.isRefreshHome = false;
                        }

                        ChatApplication app = ChatApplication.getInstance();
                        app.closeSocket(webUrl);

                        if (mSocket != null) {
                            mSocket.disconnect();
                            mSocket = null;
                        }

                        startSocketConnectionNew();

                        Toast.makeText(Main2Activity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        // openSocket();

                        hideAlertDialog();

                        linear_main.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.VISIBLE);
                        mToolBarSettings.setVisibility(View.VISIBLE);
                        toolbarTitle.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.VISIBLE);
                        linear_login.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        flagLogin = true;
                        ChatApplication.isRefreshHome = false;
                        invalidateToolbarCloudImage();
                        if (Common.isNetworkConnected(Main2Activity.this) && isNetwork == true) {
                            mainFragment1.RefreshAnotherFragment();
                        }

                        mViewPager.setCurrentItem(0);
                        getUserDialogClick(true);

                        mainFragment1.showDialog = 1;
                        ActivityHelper.dismissProgressDialog();

                    } else {
                        Toast.makeText(Main2Activity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                isLoginTimeCount = false;
                Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void callTheadCount() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    if (isLoginTimeCount == true) {
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                isLoginTimeCount = false;
                                ActivityHelper.dismissProgressDialog();
                                ChatApplication.showToast(Main2Activity.this, "Something went wrong....");
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    public void loginWifiCloud() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(et_username.getText().toString())) {
            et_username.requestFocus();
            et_username.setError("Enter User Name");
            return;
        }
        if (TextUtils.isEmpty(et_password.getText().toString())) {
            et_password.requestFocus();
            et_password.setError("Enter Password");
            return;
        }

        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        }
        ChatApplication.logDisplay("token : " + token);

        JSONObject obj = new JSONObject();
        try {
            //"wifi_user_name":"admin","wifi_user_password": "123"
            obj.put("wifi_user_name", et_username.getText().toString());
            obj.put("wifi_user_password", et_password.getText().toString());

        } catch (Exception e) {
            ChatApplication.logDisplay("Exception loginCloud " + e.getMessage());
        }
        //'192.168.175.119/wifilogin'
        String url = ChatApplication.url + Constants.wifilogin;

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        new GetJsonTask(Main2Activity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ActivityHelper.hideKeyboard(Main2Activity.this);

                        Gson gson = new Gson();
                        String jsonCurProduct = gson.toJson(MainFragment.piDetailsModelArrayList);
                        Common.savePrefValue(Main2Activity.this, Common.USER_PIDETAIL, jsonCurProduct);
                        flagPicheck = false;
                        loginDialog(false, false);
                        reloadFragment();
                    } else {
                        Toast.makeText(Main2Activity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    String userId = "0";

    public void openSocket() {

        if (webUrl.equalsIgnoreCase("")) {// webUrl.equalsIgnoreCase(Constants.CLOUD_SERVER_URL) ||

            String isLogin = Common.getPrefValue(this, Constants.PREF_CLOUDLOGIN);
            String cloudIp = Common.getPrefValue(this, Constants.PREF_IP);

            if (userId.equalsIgnoreCase("0") && isLogin.equalsIgnoreCase("true")) {

                webUrl = cloudIp;//Constants.CLOUD_SERVER_URL;
                ChatApplication.isRefreshHome = true;
                hideAlertDialog();
                startSocketConnection();
                isCloudConnected = true;
                invalidateToolbarCloudImage();
            } else {
                hideAlertDialog();
                isCloudConnected = true;
                invalidateToolbarCloudImage();
                webUrl = Constants.CLOUD_SERVER_URL;
                loginDialog(true, false);
            }
        } else {
            hideAlertDialog();
            linear_login.setVisibility(View.GONE);
            //showAlertDialog();
            ChatApplication.isRefreshHome = true;
            startSocketConnection();
        }


    }

    private Socket mSocket;
    private int SIGN_IP_REQUEST_CODE = 204;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == SIGN_IP_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            getDelegate();//TODO for signup code
            doLoginCloud();
        }
    }

    private void doLoginCloud() {

        final Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        String USER_ID = Common.getPrefValue(getApplicationContext(), Constants.USER_ID);

        final List<User> tempList = new ArrayList<User>();

        String jsonCurProduct = gson.toJson(tempList);
        if (tempList.size() == 0) {
            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, "");
        } else {
            tempList.get(0).setIsActive(true);
            jsonCurProduct = gson.toJson(tempList);
            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

        }

        if (cloudAdapter != null) {
            cloudAdapter.notifyDataSetChanged();
        }

        if (tempList.size() > 0) {
            try {

                Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, tempList.get(0).getCloudIP());
                Common.savePrefValue(Main2Activity.this, Constants.USER_ID, tempList.get(0).getUser_id());
                Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, tempList.get(0).getPassword());

                webUrl = tempList.get(0).getCloudIP();
                isCloudConnected = true;
                invalidateToolbarCloudImage();

                ChatApplication app = ChatApplication.getInstance();
                app.closeSocket(webUrl);

                if (mSocket != null) {
                    mSocket.disconnect();
                    mSocket = null;
                }

                startSocketConnection();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            runService();
        }
    }

    public void startSocketConnectionNew() {

//        if (Constants.isWifiConnect) {
//            return;
//        }
        ChatApplication app = ChatApplication.getInstance();
//
//        if (mSocket != null && mSocket.connected() && app.url.equalsIgnoreCase(webUrl)) {
//        }
//        //using @SerializedName we can customize the model name maping.when we are dealing with API and
//        //we need to convert JSON response to direct Model class then make sure your api key filed name and model method name both are same.
//
//        //
//        else {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket = null;
        }

        mSocket = app.openSocket(webUrl);
        ChatApplication.logDisplay("socket url11 " + webUrl);
        ChatApplication.url = webUrl;

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on("deleteChildUser", deleteChildUser);
        mSocket.connect();
        ChatApplication.logDisplay("socket connect " + mSocket.connected());
        SocketManager socketManager = SocketManager.getInstance();

//        }

        linear_progress.setVisibility(View.GONE);
    }

    public void startSocketConnection() {

        if (Constants.isWifiConnect) {
            return;
        }
        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected() && app.url.equalsIgnoreCase(webUrl)) {
        }
        //using @SerializedName we can customize the model name maping.when we are dealing with API and
        //we need to convert JSON response to direct Model class then make sure your api key filed name and model method name both are same.

        //
        else {

            mSocket = app.openSocket(webUrl);
            ChatApplication.logDisplay("socket url " + webUrl);
            ChatApplication.url = webUrl;

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on("deleteChildUser", deleteChildUser);


            mSocket.connect();

            //SocketManager.getInstance().connectSocket(ChatApplication.url, "80");
            SocketManager socketManager = SocketManager.getInstance();
            /*socketManager.connectSocket(ChatApplication.url);
            socketManager.setSocketConnectionListener(new OnSocketConnectionListener() {
                @Override
                public void onSocketEventFailed() {
                    Log.d("SocketManager","onSocketEventFailed...");
                }

                @Override
                public void onSocketConnectionStateChange(int socketState) {
                    if(socketState == SocketManager.STATE_CONNECTED){

                    }else if(socketState == SocketManager.STATE_DISCONNECTED){

                    }
                    Log.d("SocketManager","onSocketConnectionStateChange : " + socketState);
                }

                @Override
                public void onInternetConnectionStateChange(int socketState) {
                    Log.d("SocketManager","onInternetConnectionStateChange : " + socketState);
                }
            });*/

        }

        //added for solved getActivity()== null issue on MainFragment
        if (!mSocket.connected()) {
            ChatApplication.isMainFragmentNeedResume = true;
        }
        mViewPager.setVisibility(View.VISIBLE);

        reloadFragment();
        loginDialog(false, false);
        linear_progress.setVisibility(View.GONE);
    }


    public void reloadFragment() {

        //  Log.d("SocketIP","fragment...");


        if (mViewPager.getCurrentItem() == 0) {
            //  isResumeConnect = true;
            mainFragment1 = (MainFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
            mainFragment1.onLoadFragment();
        } else if (mViewPager.getCurrentItem() == 1) {
            MoodFragment mainFragment2 = (MoodFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
            mainFragment2.onLoadFragment();
        } else if (mViewPager.getCurrentItem() == 2) {
            ScheduleFragment scheduleFragment = (ScheduleFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
            scheduleFragment.onLoadFragment(8);
        }


       /* else if(mViewPager.getCurrentItem()==3){
            MainFragment2 scheduleFragment = (MainFragment2)mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
            scheduleFragment.onLoadFragment();
        }*/
    }

    //
    public void reloadFragment2() {

        if (mViewPager.getCurrentItem() == 0) {
            lastTabPos = 0;
            if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.fragmentList != null) {
                // MainFragment mainFragment1 = (MainFragment)mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
                // mainFragment1.onLoadFragment();
            }
        } else if (mViewPager.getCurrentItem() == 1) {
            lastTabPos = 1;
            if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.fragmentList != null) {
                //  MoodFragment mainFragment2 = (MoodFragment)mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
                //  mainFragment2.onLoadFragment();
            }
        } else if (mViewPager.getCurrentItem() == 2) {
            lastTabPos = 2;
            if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.fragmentList != null) {
                ScheduleFragment scheduleFragment = (ScheduleFragment) mSectionsPagerAdapter.fragmentList.get(mViewPager.getCurrentItem());
                scheduleFragment.onLoadFragment(9);
            }
        }
    }

    @Override
    public void onProgress() {
        showConnectingCloudDialog(LOCAL_MESSAGE);
    }

    @Override
    public void onSuccess() {
        hideConnectingCloudDialog();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public AsyncTask asyncTask;

    private void runService() {
        if (Common.isConnectedToMobile(this)) {
            linear_progress.setVisibility(View.VISIBLE);
            txt_connection.setText(R.string.connect_cloud);
            webUrl = ""; //added by sagar
            openSocket();

        } else {
            linear_progress.setVisibility(View.VISIBLE);
            txt_connection.setText(R.string.search_home_controller);

            if (Common.isNetworkConnected(this)) {
                asyncTask = new Main2Activity.ServiceEventAsync(Constants.getuserIp(Main2Activity.this)).execute();

            }
        }
    }

    /*
     * swap cloud to local connect
     * */
    private void runService2() {
        if (Common.isConnectedToMobile(this)) {
            linear_progress.setVisibility(View.VISIBLE);
            txt_connection.setText(R.string.connect_cloud);

            webUrl = ""; //added by sagar
            openSocket();

        } else {
            // linear_progress.setVisibility(View.VISIBLE);
            // txt_connection.setText(R.string.search_home_controller);

            if (Common.isNetworkConnected(this)) {

                if (mSocket != null && mSocket.connected()) {
                    ChatApplication app = ChatApplication.getInstance();
                    app.closeSocket(webUrl);
                }
                asyncTask = new ServiceEventAsync(Constants.getuserIp(Main2Activity.this)).execute();
            }
        }
    }


    //call service event
    public class ServiceEventAsync extends AsyncTask {

        boolean isRegisterListener = false;
        String ipAddressPI = "";
        String ipLocal = "";

        public ServiceEventAsync(String localIp) {
            showConnectingCloudDialog(LOCAL_MESSAGE);
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            lock = wifi.createMulticastLock("lock");
            lock.setReferenceCounted(true);
            lock.acquire();
            this.ipLocal = localIp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                if (lock != null) {
                    if (lock.isHeld()) {
                        lock.release();
                        lock = null;
                    }
                }
                InetAddress addr = getLocalIpAddress(Main2Activity.this);
                String hostname = addr.toString().replace("/", "");

                String[] array = hostname.split("\\.");
                if (Constants.isTesting) {
                    ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + ChatApplication.testIP;
                } else {
                    ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
                }
//                ipAddressPI="192.168.175.119";
//                boolean isReachable = Common.isPortReachable(ipAddressPI, 80, Main2Activity.this);
//                boolean isReachable = true;

                if (Constants.isWifiConnect) {

                } else {
                    ChatApplication.logDisplay("ip address is found " + ipAddressPI + " " + Constants.getGateway(Main2Activity.this));
                    if (ipAddressPI.equalsIgnoreCase("" + Constants.getGateway(Main2Activity.this))) {
                        isFlagWifi = true;
                        isCloudConnected = false; //added by sagar
                        invalidateToolbarCloudImage();
                        webUrl = ipAddressPI;
                        wifiIpAddress = ipAddressPI;
                        ChatApplication.url = webUrl;
                        isClick = false;
                    } else {
                        ipAddressPI = "";
                        webUrl = ipAddressPI;
                    }

//                    if (!TextUtils.isEmpty(ipLocal)) {
////                        if (isReachable && ipLocal.equalsIgnoreCase(ipAddressPI) && Common.getMacAddress(Main2Activity.this,ipAddressPI).equalsIgnoreCase(Constants.getMacAddress(Main2Activity.this))) {
////                        if (isReachable && ipLocal.equalsIgnoreCase(ipAddressPI)) {
//                            isFlagWifi = true;
//                            isCloudConnected = false; //added by sagar
//                            invalidateToolbarCloudImage();
//                            webUrl = ipAddressPI;
//                            wifiIpAddress = ipAddressPI;
//                            ChatApplication.url = webUrl;
//                            isClick = false;
////                        } else {
////                            ipAddressPI = "";
////                            webUrl = ipAddressPI;
////                        }
//                    } else {
////                        if (isReachable && Common.getMacAddress(Main2Activity.this,ipAddressPI).equalsIgnoreCase(Constants.getMacAddress(Main2Activity.this))) {
////                        if (isReachable) {
//                            isFlagWifi = true;
//                            isCloudConnected = false; //added by sagar
//                            invalidateToolbarCloudImage();
//                            webUrl = ipAddressPI;
//                            wifiIpAddress = ipAddressPI;
//                            ChatApplication.url = webUrl;
//                            isClick = false;
//
////                        } else {
////                            ipAddressPI = "";
////                            webUrl = ipAddressPI;
////                        }
//                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(Object o) {
            hideConnectingCloudDialog();
            if (Constants.isWifiConnect) {
            } else if (isWifiConnect) {

            } else if (isFlagWifi) {
                isFlagWifi = false;
                listService("\nService resolved: ", ipAddressPI, 80, true, false);
            } else {
                listService("\nService resolved: ", ipAddressPI, 80, true, false);
            }

            super.onPostExecute(o);
        }
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

    public static boolean isResumeConnect = false;

    private void listService(final String msg, String ip, final int port, final boolean isFound, boolean isShow) {

        final String openIp = (isFound) ? "http://" + ip + ":" + port : Constants.CLOUD_SERVER_URL;
        ChatApplication.logDisplay("openIp is " + openIp);
//        final String openIp = "http://" + ip + ":" + port : Constants.CLOUD_SERVER_URL;

        if (!ip.equalsIgnoreCase("") && !webUrl.equalsIgnoreCase(openIp)) {
            webUrl = openIp;
            openSocket();
        } else if (ip.equalsIgnoreCase("")) {
            openSocket();
        } else if (webUrl.equalsIgnoreCase(openIp)) {
            if (!isFound) {
                hideAlertDialog();
                linear_progress.setVisibility(View.GONE);
            }
        }


        //openWebView(openIp);
        //  }, 10);
    }

    public void openErrorDialog(String errrString) {
        hideAlertDialog();
        showAlertDialog(errrString);
    }

    private void showAlertDialog(String erroString) {
        hideConnectingCloudDialog();
        txt_error_value.setText(erroString);
        if (linear_retry.getVisibility() == View.GONE) {
            mViewPager.setVisibility(View.GONE);
            linear_retry.setVisibility(View.VISIBLE);
        }

    }

    private void hideAlertDialog() {
        linear_main.setVisibility(View.VISIBLE);
        linear_retry.setVisibility(View.GONE);
    }

    public void loginDialog(final boolean value, final boolean isCancelButtonVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActivityHelper.dismissProgressDialog();
                et_username.setText("");
                et_password.setText("");

                if (TextUtils.isEmpty(Common.getPrefValue(Main2Activity.this, Constants.USER_ID))) {
                    hideAlertDialog();
                    mViewPager.setVisibility(View.GONE);
                    linear_progress.setVisibility(View.GONE);
                    mToolBarSettings.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    linear_login.setVisibility(View.VISIBLE);

                    if (ChatApplication.getUserList(Main2Activity.this).size() > 0) {
                        btn_SKIP.setVisibility(View.VISIBLE);
                    } else {
                        btn_SKIP.setVisibility(View.GONE);
                    }
                } else {

                    if (flagPicheck && !value) {
                        hideAlertDialog();
                        mViewPager.setVisibility(View.VISIBLE);
                        linear_progress.setVisibility(View.GONE);
                        mToolBarSettings.setVisibility(View.INVISIBLE);
                        toolbarTitle.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.GONE);
                        linear_login.setVisibility(View.GONE);
                        linear_retry.setVisibility(View.VISIBLE);
                    } else {

                        if (value) {
                            //hideAlertDialog();
                            hideAlertDialog();
                            //linear_retry.setVisibility(View.GONE);
                            //    linear_main.setVisibility(View.GONE);
                            mViewPager.setVisibility(View.GONE);
                            linear_progress.setVisibility(View.GONE);
                            mToolBarSettings.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            linear_login.setVisibility(View.VISIBLE);

                            if (ChatApplication.getUserList(Main2Activity.this).size() > 0) {
                                btn_SKIP.setVisibility(View.VISIBLE);
                            } else {
                                btn_SKIP.setVisibility(View.GONE);
                            }
                        } else {
                            hideAlertDialog();
                            linear_main.setVisibility(View.VISIBLE);
                            mViewPager.setVisibility(View.VISIBLE);
                            mToolBarSettings.setVisibility(View.VISIBLE);
                            toolbarTitle.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.VISIBLE);
                            linear_login.setVisibility(View.GONE);
                        }

                        if (isCancelButtonVisible || flagPicheck) {
                            // btn_cancel.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.GONE);
                        } else {
                            btn_cancel.setVisibility(View.GONE);
                        }
                    }
                }

//                final Thread thread=new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2500);
//                            ActivityHelper.dismissProgressDialog();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });thread.start();


            }
        });
    }

    public void setUserTypeValue() {
        if (!TextUtils.isEmpty(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE))) {
            if (Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                toolbarImage.setVisibility(View.VISIBLE);
                toolbarTitle.setClickable(true);
                toolbarImage.setClickable(true);

                toolbarTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                toolbarTitle.setClickable(true);
                toolbarImage.setClickable(true);
                toolbarImage.setVisibility(View.VISIBLE);
                toolbarTitle.setTextColor(getResources().getColor(R.color.sky_blue));
            }
        }
    }

    public void setCouldMethod() {
        // mainFragment1.RefreshAnotherFragment();
    }

    public static void changestatus() {
        tabLayout.setVisibility(View.VISIBLE);
        mToolBarSettings.setVisibility(View.VISIBLE);
    }


}
