package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.fcm.MyFirebaseMessagingService;
import com.spike.bot.fragments.DashBoardFragment;
import com.spike.bot.fragments.MoodFragment;
import com.spike.bot.fragments.ScheduleFragment;
import com.spike.bot.listener.DeviceListRefreshView;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.ResponseErrorCode;
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
import java.util.ArrayList;
import java.util.List;


import io.fabric.sdk.android.Fabric;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener, CloudAdapter.CloudClickListener,
        DashBoardFragment.OnHeadlineSelectedListener, ResponseErrorCode, SocketListener, LoginPIEvent ,BottomNavigationView.OnNavigationItemSelectedListener{

    private String ERROR_STRING = "No Internet found.\n" + "Check your connection or try again.";
    public static boolean flagPicheck = false, flagLogin = false,isResumeConnect=false,isCloudConnected=false;

    public Toolbar toolbar;
    public BottomNavigationView tabLayout;
    public ImageView mToolBarSettings,deepsImage, toolbarImage, mImageCloud;
    public TextView toolbarTitle,txt_connection, txt_add_acoount;
    public LinearLayout linear_main, linear_progress,linearTab;
    public FrameLayout linearCloud;
    private RecyclerView recyclerView;

    //refresh view like change network
    DeviceListRefreshView deviceListRefreshView;
    // PopupWindow popupWindow;
    private CloudAdapter cloudAdapter;

    PowerManager.WakeLock wakeLock;
    public DashBoardFragment dashBoardFragment1;
    public MoodFragment moodFragment;
    public ScheduleFragment scheduleFragment;
    private FrameLayout mViewPager;
    private Socket mSocket;

    public int tabCount=0;
    public String userId="0",webUrl = "",token="";
    public boolean  isSocketConnected = true,isTimerStart=false,isTimerEnd=false,doubleBackToExitPressedOnce=false,isWifiConnect = false, isNetwork = false;

    Gson gson;
    private ConnectivityReceiver connectivityReceiver;
    public Dialog dialogUser;

    List<User> userList = new ArrayList<User>();

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);

        //network change broadcast registretion..
        gson= new Gson();
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        ChatApplication.getInstance().setConnectivityListener(this);

        //application menu show badge count
        MyFirebaseMessagingService.badge = "0";

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get push notification token
        token = FirebaseInstanceId.getInstance().getToken();
        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        setViewId();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "");
        wakeLock.acquire();

        DashBoardFragment.clearNotification(Main2Activity.this);

        //first fragment load
        loadFragment(new DashBoardFragment());
        tabLayout.setOnNavigationItemSelectedListener(this);

        // check login or not
        if (!Constants.checkLoginAccountCount(this)) {
            showLogin();
            return;
        }

        isResumeConnect = true;

        setUserName();
    }


    @Override
    public void onResume() {
        super.onResume();

        setViewId();

        ChatApplication.getInstance().setConnectivityListener(this);
        if (Common.isConnected() && isResumeConnect) {
            hideAlertDialog();
            isResumeConnect = false;
        } else if (Common.isConnected() && ChatApplication.isRefreshHome) {
            hideAlertDialog();
            linear_progress.setVisibility(View.GONE);
            ChatApplication.isRefreshHome = false;
        } else if (!Common.isConnected()) {
            hideAlertDialog();
            showAlertDialog(ERROR_STRING);
        }
    }

    private void setViewId() {
        mViewPager = findViewById(R.id.container);
        tabLayout =  findViewById(R.id.tabs);
        mImageCloud =  findViewById(R.id.toolbar_cloud);
        toolbarImage =  findViewById(R.id.toolbarImage);
        deepsImage =  findViewById(R.id.deepsImage);
        txt_connection =  findViewById(R.id.txt_connection);
        linear_main =  findViewById(R.id.linear_main);
        linear_progress =  findViewById(R.id.linear_progress);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        mToolBarSettings =  findViewById(R.id.toolbar_setting);
        linearCloud =  findViewById(R.id.linear_progress_cloud);
        linearTab = findViewById(R.id.linearTab);

        tabLayout.setOnNavigationItemSelectedListener(this);
        toolbarImage.setOnClickListener(this);
        toolbarTitle.setOnClickListener(this);
        mImageCloud.setOnClickListener(this);
        mToolBarSettings.setOnClickListener(this);

        //update user fname and lname after updaing the profile activity
        setUserName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ChatApplication.isPushFound) {
            DashBoardFragment.getBadgeClear(Main2Activity.this);
        }
        ChatApplication.isCallDeviceList = false;
        DashBoardFragment.isRefredCheck = true;
        ChatApplication.isMainFragmentNeedResume = true;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("deleteChildUser", deleteChildUser);
        }
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
        try {
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
    public void onClick(View view) {
        if (view.getId() == R.id.toolbar_cloud) {
            showAccountDialog(toolbarTitle);
        } else if (view.getId() == R.id.toolbarImage) {
            showAccountDialog(toolbarTitle);
        } else if (view.getId() == R.id.toolbarTitle) {
            showAccountDialog(toolbarTitle);
        }else if (view.getId() == R.id.toolbar_setting) {
            if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
                ChatApplication.showToast(Main2Activity.this, getResources().getString(R.string.disconnect));
            } else {
                    openSettingPopup(toolbar);
            }
        }

    }

    Fragment fragment=null;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigationDashboard:
                tabCount=0;
//                if(dashBoardFragment1 ==null){
                    dashBoardFragment1 = new DashBoardFragment();
                    fragment= dashBoardFragment1;
//                }else {
//                    fragment= dashBoardFragment1;
//                }

                break;

            case R.id.navigationMood:
                tabCount=1;
                if(moodFragment==null){
                    moodFragment = new MoodFragment();
                    fragment=moodFragment;
                }else {
                    fragment=moodFragment;
                }
                break;

            case R.id.navigationSchedule:
                tabCount=2;
                if(scheduleFragment==null){
                    scheduleFragment = new ScheduleFragment();
                    fragment=scheduleFragment;
                }else {
                    fragment=scheduleFragment;
                }
                break;
        }

        return loadFragment(fragment);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            openSettingPopup(toolbar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onErrorCode(int errorResponseCode) {
    }

    /**
     * @param name
     */

    @Override
    public void onArticleSelected(String name) {
        if (toolbarTitle != null)
          setUserName();
        else {
            toolbarTitle =  findViewById(R.id.toolbarTitle);
            setUserName();
            toolbarTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAccountDialog(toolbarTitle);
                }
            });
        }
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

    /*user selection set value & refresh*/
    @Override
    public void userSelectclick(User user) {
        if (dialogUser != null) {
            dialogUser.dismiss();
        }
        if (Constants.isWifiConnect) {
            return;
        }
        if (!TextUtils.isEmpty(user.getCloudIP())) {
            linear_progress.setVisibility(View.VISIBLE);

            String jsonCurProduct = gson.toJson(userList);
            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

            String cloudIp = "", local_ip = "",mac_address="";
            String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
            if (!TextUtils.isEmpty(jsonText)) {
                Type type = new TypeToken<List<User>>() {}.getType();
                userList = gson.fromJson(jsonText, type);

                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getUser_id().equalsIgnoreCase(user.getUser_id())) {
                        userList.get(i).setIsActive(true);
                        cloudIp = userList.get(i).getCloudIP();
                        local_ip = userList.get(i).getLocal_ip();
                        mac_address = userList.get(i).getLocal_ip();
                        ChatApplication.currentuserId=userList.get(i).getUser_id();
                    } else {
                        userList.get(i).setIsActive(false);
                    }
                }

                String json = gson.toJson(userList);
                Common.savePrefValue(getApplicationContext(), Common.USER_JSON, json);
            }

             setUserName();

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

            ChatApplication.isCallDeviceList=true;
            setWifiLocalflow(local_ip,cloudIp,mac_address,0);
        }

    }

    /**
     *
     */
    @Override
    public void startSession() { }

    /*network change interface call like switch network*/
    @Override
    public void onNetworkConnectionChanged(final boolean isConnected) {
        /*waiting for same time connct network timeing issue faceing  */
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
            if (isConnected) {
                if(tabCount!=0){
                    tabLayout.setSelectedItemId(R.id.navigationDashboard);
                }else {
                    ChatApplication.isCallDeviceList = true;
                    deviceListRefreshView.deviceRefreshView(1);
                }

                ChatApplication.logDisplay("device refreshing main "+tabCount);

            } else {
                linear_progress.setVisibility(View.GONE);
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
                                    deviceListRefreshView.deviceRefreshView(0);
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
        flagPicheck = true;
    }

    @Override
    public void onProgress() {
    }

    @Override
    public void onSuccess() {
    }


    //load fragment
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commitAllowingStateLoss();
            return true;
        }
        return false;
    }


    public void setUserName(){
        toolbarTitle.setText(Constants.getUserName(this));
    }

    /*tab hide & show*/
    public void tabShow(boolean b){
        linearTab.setVisibility(b==true ? View.VISIBLE :View.GONE);
//        mToolBarSettings.setClickable(b);
    }


    /**
     * If Network is connected on cloud then display cloud icon otherwise display wifi icon
     * local : same network like pi & our device network
     * cloud : mobile data Or diff network
     */
    public void invalidateToolbarCloudImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(ChatApplication.url)) {
                    if (ChatApplication.url.startsWith(Constants.startUrl)) {
                        mImageCloud.setImageResource(R.drawable.cloud);
                    } else {
                        mImageCloud.setImageResource(R.drawable.wifi);
                    }
                }
            }
        });

    }

    /* port & url set */
    private void listServiceTemp(final String msg, String ip, final int port, final boolean isFound, boolean isShow) {
        String openIp="";
        if(ip.startsWith(Constants.startUrlhttp)){
            openIp   = (isFound) ? ip + ":" + port : Constants.CLOUD_SERVER_URL;
        }else {
            openIp  = (isFound) ?  "http://" +ip + ":" + port : Constants.CLOUD_SERVER_URL;
        }

        ChatApplication.logDisplay("openIp is connect " + openIp);
        webUrl = openIp;
    }

    /*user type set like admin & child user fun*/
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

    public  void changestatus() {
        linearTab.setVisibility(View.VISIBLE);
        mToolBarSettings.setVisibility(View.VISIBLE);
    }


    /**
     * show Account Dialog with toolbar title
     * add new user & set another user dialog
     *
     * @param toolbarTitle
     */
    private void showAccountDialog(TextView toolbarTitle) {

        if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
            return;
        }

        if (TextUtils.isEmpty(Common.getPrefValue(Main2Activity.this, Constants.USER_ADMIN_TYPE))) {
            return;
        }

        if (dialogUser == null) {
            dialogUser = new Dialog(this);
        }
        dialogUser.setCanceledOnTouchOutside(false);
        dialogUser.setContentView(R.layout.popup_toolbar);
        dialogUser.getWindow().setGravity(Gravity.TOP);
        dialogUser.setCanceledOnTouchOutside(true);
        dialogUser.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogUser.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txt_add_acoount =  dialogUser.findViewById(R.id.txt_add_account);
        recyclerView =  dialogUser.findViewById(R.id.userList);
        TextView txtOutside = dialogUser.findViewById(R.id.txtOutside);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        if (userList == null) {
            userList = new ArrayList<>();
        }
        userList.clear();

        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {}.getType();
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

                Intent intent=new Intent(Main2Activity.this,LoginActivity.class);
                intent.putExtra("isFlag","true");
                startActivity(intent);
            }
        });

        if (!dialogUser.isShowing()) {
            dialogUser.show();
        }
    }

    /*setting menu dialog*/
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

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_device_log:
                        if(linearTab.getVisibility()==View.VISIBLE){
                            Intent intent = new Intent(Main2Activity.this, DeviceLogNewActivity.class);
                            intent.putExtra("isCheckActivity", "AllType");
                            startActivity(intent);
                        }else {
                            ChatApplication.showToast(Main2Activity.this,getResources().getString(R.string.something_wrong1));
                        }

                        break;
                    case R.id.action_notification_settings:
                        if(linearTab.getVisibility()==View.VISIBLE){
                            Intent intentNotification = new Intent(Main2Activity.this, NotificationSetting.class);
                            startActivity(intentNotification);
                        }else {
                            ChatApplication.showToast(Main2Activity.this,getResources().getString(R.string.something_wrong1));
                        }

                        break;
                    case R.id.action_profile:
                        if(linearTab.getVisibility()==View.VISIBLE){
                            Intent intentProfile = new Intent(Main2Activity.this, ProfileActivity.class);
                            startActivity(intentProfile);
                        }else {
                            ChatApplication.showToast(Main2Activity.this,getResources().getString(R.string.something_wrong1));
                        }

                        break;
                    case R.id.action_logout:
                        if(linearTab.getVisibility()==View.VISIBLE) {
                            logoutCloudUser();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /* child user remove from local DB*/
    public void childRemoveSQLite() {

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        //clear pref and open login screen
        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "false");
        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, "");

        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);

        List<User> uList = new ArrayList<User>();
        final List<User> tempList = new ArrayList<User>();

        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {}.getType();
            uList = gson.fromJson(jsonText, type);
            boolean isFoundUser = false;

            for (User user : uList) {
                if (!user.isActive()) {
                    tempList.add(user);
                } else {
                    isFoundUser = true;
                }
            }

            if (isFoundUser) {
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

                        setWifiLocalflow(tempList.get(0).getLocal_ip(),tempList.get(0).getCloudIP(),tempList.get(0).getMac_address(),0);

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
                    loginIntent();
                }
            }
        } else {
            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);
        }


    }

    /* logout user than back user showing another wise login screen move to*/
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

            Type type = new TypeToken<List<User>>() {}.getType();
            uList = gson.fromJson(jsonText, type);

            boolean isFoundUser = false;
            User logOutUser = new User();

            for (User user : uList) {
                if (!user.isActive()) {
                    tempList.add(user);
                } else {
                    isFoundUser = true;
                    logOutUser = user;
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
                                ChatApplication.showToast(Main2Activity.this, message);

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

                                        ChatApplication.isCallDeviceList = true;
                                        ChatApplication.currentuserId=tempList.get(0).getUser_id();
                                        setWifiLocalflow(tempList.get(0).getLocal_ip(),tempList.get(0).getCloudIP(),tempList.get(0).getMac_address(),0);

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
                                    loginIntent();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        ActivityHelper.dismissProgressDialog();
                    }
                }).execute();
            }
        } else {
            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);
        }
    }

    /*login screen */
    private void loginIntent() {
        Intent intent=new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }
    /* call back set when dashboard screen refresh*/
    public void setCallBack(DeviceListRefreshView callBack){
        this.deviceListRefreshView=callBack;
    }

    /* click lister*/
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

    /* most impoer method work like
    * > user switch
    * > nertwork change
    * > user logout
    *
    *  */
    public void setWifiLocalflow(String localIp, String cloudIp, String mac_address,int isCount) {

        ChatApplication.logDisplay("count is ss "+isCount);
        if (Common.isNetworkConnected(Main2Activity.this)) {
            isWifiConnect = true;
            webUrl = localIp;
            if(!webUrl.startsWith("http")){
                webUrl="http://"+webUrl;
            }
                ChatApplication.url = webUrl;
                flagPicheck = false;
                listServiceTemp("\nService resolved: ", webUrl, 80, true, false);

        } else {
            isNetwork = true;
            webUrl = cloudIp;
            if(!webUrl.startsWith("http")){
                webUrl="http://"+webUrl;
            }
            ChatApplication.url = webUrl;
            isCloudConnected = true;
            flagPicheck = false;

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

        hideAlertDialog();

        linear_main.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mToolBarSettings.setVisibility(View.VISIBLE);
        toolbarTitle.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        flagLogin = true;
        ChatApplication.isRefreshHome = false;
        invalidateToolbarCloudImage();

        dashBoardFragment1.isResumeConnect=true;
        dashBoardFragment1.showDialog = 1;
        getUserDialogClick(true);
        /* user switch to first tab*/
        tabLayout.setSelectedItemId(R.id.navigationDashboard);
        setUserName();
        deviceListRefreshView.deviceRefreshView(1);
    }

    public void callSocket() {
        if (Constants.isWifiConnect) {
            return;
        }
        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected() && app.url.equalsIgnoreCase(webUrl)) {
            mSocket.on("deleteChildUser", deleteChildUser);
        }

        mViewPager.setVisibility(View.VISIBLE);
        linear_progress.setVisibility(View.GONE);
    }


    public void showAlertDialog(String erroString) {
        deviceListRefreshView.deviceRefreshView(0);
    }

    public void hideAlertDialog() {
        linear_main.setVisibility(View.VISIBLE);
    }


    //Socket
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        ChatApplication.showToast(Main2Activity.this, "Please click BACK again to exit.");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
