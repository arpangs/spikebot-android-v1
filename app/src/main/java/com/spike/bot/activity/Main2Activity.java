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
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.ack.SocketManager;
import com.spike.bot.activity.ir.blaster.WifiBlasterActivity;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.adapter.ToolbarSpinnerAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomViewPager;
import com.spike.bot.fragments.MainFragment;
import com.spike.bot.fragments.MoodFragment;
import com.spike.bot.fragments.ScheduleFragment;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.listener.RunServiceInterface;
import com.spike.bot.listener.SocketListener;
import com.spike.bot.model.PiDetailsModel;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;


import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.leolin.shortcutbadger.ShortcutBadger;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener, CloudAdapter.CloudClickListener,
        MainFragment.OnHeadlineSelectedListener, RunServiceInterface, ResponseErrorCode, SocketListener, LoginPIEvent {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;

    private static final String TAG = "Main2Activity";

    private WifiManager.MulticastLock lock;
    private Handler handler = new Handler();

    ImageView deepsImage;
    public static ImageView mToolBarSettings;
    TextView txt_connection;
    LinearLayout linear_main, linear_retry, linear_progress, linear_login;
    FrameLayout linearCloud;

    EditText et_username, et_password;
    Button btn_login, button_retry;
    private Button btn_cancel;

    public static TabLayout tabLayout;
    Toolbar toolbar;
    public static int lastTabPos = 0;

    public ImageView toolbarImage;
    private Button btn_login_search;

    List<User> userList;
    public static TextView toolbarTitle;
    private static ToolbarSpinnerAdapter customAdapter;
    private TextView txt_add_acoount;
    PopupWindow popupWindow;

    private CloudAdapter cloudAdapter;
    private RecyclerView recyclerView;

    //new view
    private LinearLayout ll_show_error;
    public static TextView txt_error_value;
    public int positionTab = 0;
    public static boolean flagPicheck = false, flagLogin = false;

    PowerManager.WakeLock wakeLock;

    private ProgressDialog progressBar;
    private FloatingActionButton mFab;

    private CardView mFabMenuLayout;
    private TextView mFabAddRoom;
    private TextView mFabAddPanel;
    private TextView mFabAddDoor;
    private TextView mFabAddTempSensor;
    private TextView mFabAddRemote;
    private TextView mFabAddCamera;
    private TextView mFabAddUnPanel;
    MainFragment mainFragment1;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         *  connectivityManager.getAllNetworks() -> Apply filter (NetworkInfo type == "WIFI" or "MOBILE")
         *  connectivityManager.getActiveNetworkInfo() -> Only get Active Network Info
         *
         */

        Network[] allNetworks = connectivityManager.getAllNetworks();
       /* ...
          ...
          ...*/
        return true;
    }

    /**
     * If Network is connected on cloud then display cloud icon otherwise display wifi icon
     */
    public void invalidateToolbarCloudImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isCloudConnected) {
                    mImageCloud.setImageResource(R.drawable.cloud);
                } else {
                    mImageCloud.setImageResource(R.drawable.wifi);
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

    private ImageView mImageCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        connectivityReceiver = new ConnectivityReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        ChatApplication.getInstance().setConnectivityListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userFname = Common.getPrefValue(ChatApplication.getInstance(), "first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(), "last_name");

        // setTitle(""+userFname + " " + userLname);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        token = FirebaseInstanceId.getInstance().getToken();

        Log.d("Token", "token : " + token);

        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //  mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        // mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mImageCloud = (ImageView) findViewById(R.id.toolbar_cloud);

       /* if(!Common.isInternetAccessible(getApplicationContext())){
            Toast.makeText(getApplicationContext(),"Host is Not Reachable...",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Host is Reachable...",Toast.LENGTH_SHORT).show();
        }*/


     /*   mViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return false;
            }
        });
        mViewPager.beginFakeDrag();
        mViewPager.requestDisallowInterceptTouchEvent(true);*/


        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        toolbarImage.setOnClickListener(this);
        // toolbarImage.setVisibility(View.GONE);

        // tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        btn_login_search = (Button) findViewById(R.id.btn_login_search);
        btn_login_search.setVisibility(View.GONE);
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

        linearCloud = (FrameLayout) findViewById(R.id.linear_progress_cloud);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wakeLock.acquire();

        MainFragment.clearNotification(Main2Activity.this);
        //new view
        // ll_show_error = (LinearLayout) findViewById(R.id.ll_show_error);
        txt_error_value = (TextView) findViewById(R.id.txt_error_value);

        btn_login.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        if (mSectionsPagerAdapter == null) {

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            // Set up the ViewPager with the sections adapter.
            //mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            /*mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    positionTab = position;

                    //tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).setIcon(navIconsActive[tabLayout.getSelectedTabPosition()]);


//                    for (int i = 0; i < tabLayout.getTabCount(); i++) {
//
//                        View view = tabLayout.getTabAt(i).getCustomView();
//
//                        TextView tab_label = (TextView) view.findViewById(R.id.nav_label);
//                        ImageView tab_icon = (ImageView) view.findViewById(R.id.nav_icon);
//
//
//                        if(i != position) {
//                            tab_label.setText("Not");
//
//                            tab_label.setTextColor(getResources().getColor(R.color.sky_blue));
//                          //  tab_icon.setImageResource(navIcons[i]);
//                        }else {
//                            tab_label.setText("Yes");
//
//                            //   tab_icon.setImageResource(navIconsActive[i]);
//                        }
//                        tabLayout.getTabAt(position).setCustomView(view);
//
//                    }

                    reloadFragment2();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

*/

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            } else {
                imei = ActivityHelper.getIMEI(this);
            }
        }


        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);

        if (TextUtils.isEmpty(userFname) && TextUtils.isEmpty(userLname)) {
            //   toolbarImage.setVisibility(View.GONE);
        }

        userList = new ArrayList<User>();

        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(jsonText, type);

            // toolbarImage.setVisibility(View.VISIBLE);
            // getSupportActionBar().setDisplayShowTitleEnabled(false);

        } else {

        }


        toolbarTitle.setText("" + userFname + " " + userLname);
        toolbarTitle.setOnClickListener(this);


        btn_login_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnected()) {
                    hideAlertDialog();
                    loginDialog(false, false);
                    runService();
                }
            }
        });


        mToolBarSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingPopup(toolbar);
            }
        });

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

                reloadFragment2();

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
        Log.d("onArticleSelected", "nm : " + name);
        if (toolbarTitle != null)
            toolbarTitle.setText(name);
        else {
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(name);

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
    public void click(User user) {

        //    Log.d("isActive","event : " + user.isActive());

        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (Constants.isWifiConnect) {
            return;
        }
        if (!TextUtils.isEmpty(user.getCloudIP())) {

            Log.d("ProgressCloud", "connecting...");
            showConnectingCloudDialog(CLOUD_MESSAGE);

            linear_progress.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String jsonCurProduct = gson.toJson(userList);
            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

            String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
            if (!TextUtils.isEmpty(jsonText)) {
                Type type = new TypeToken<List<User>>() {
                }.getType();
                userList = gson.fromJson(jsonText, type);
            }

            toolbarTitle.setText(user.getFirstname() + " " + user.getLastname());

            if(cloudAdapter!=null){
                cloudAdapter.notifyDataSetChanged();
            }


            Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
            Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, user.getCloudIP());
            Common.savePrefValue(Main2Activity.this, Constants.USER_ID, user.getUser_id());
            Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, user.getPassword());

            webUrl = user.getCloudIP();
            ChatApplication.url = webUrl;
            isCloudConnected = true;
            invalidateToolbarCloudImage();
            isClick = true;

            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);

            if (mSocket != null) {

                //  Log.d("ChatSocket","Main2 click switch user webUrl : " + webUrl);

                mSocket.disconnect();
                mSocket = null;

               /* ChatApplication app = ChatApplication.getInstance();
                app.closeSocket(webUrl);
                mSocket.disconnect();
                mSocket.close();
                mSocket = null;

                isResumeConnect = true;
                ChatApplication.isMainFragmentNeedResume = true;
                ChatApplication.isMoodFragmentNeedResume = true;*/
                // ChatApplication.isScheduleNeedResume = true;
            }
            //  reloadFragment();

            startSocketConnection();
        }

    }

    /**
     *
     */
    @Override
    public void startSession() {
        //startSocketSession();
    }

    private Emitter.Listener reConnect = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            Log.d("ReconnectSocket", "reConnect : " + objects);
        }
    };
    private Emitter.Listener reConnectFailed = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            Log.d("ReconnectSocket", "reConnectFailed : " + objects);

        }
    };


    /**
     * show Account Dialog with toolbar title
     *
     * @param toolbarTitle
     */

    private void showAccountDialog(TextView toolbarTitle) {

        if (!ActivityHelper.isConnectingToInternet(Main2Activity.this)) {

            return;
        }
        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_toolbar, null);
        popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        txt_add_acoount = (TextView) popupView.findViewById(R.id.txt_add_account);

        recyclerView = (RecyclerView) popupView.findViewById(R.id.userList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);

        if (userList == null) {
            userList = new ArrayList<>();
        }

        userList.clear();

        if (!TextUtils.isEmpty(jsonText)) {
            //   spinner.setVisibility(View.VISIBLE);
            Type type = new TypeToken<List<User>>() {}.getType();
            userList = gson.fromJson(jsonText, type);

            recyclerView.setVisibility(View.VISIBLE);

            // getSupportActionBar().setDisplayShowTitleEnabled(false);

        } else {
            // getSupportActionBar().setDisplayShowTitleEnabled(true);

            //   userList.add(user);

            recyclerView.setVisibility(View.GONE);
        }

        if(userList!=null){
            cloudAdapter = new CloudAdapter(Main2Activity.this, userList, this);
            recyclerView.setAdapter(cloudAdapter);
        }


        txt_add_acoount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add new cloud account with login

                popupWindow.dismiss();

                hideAlertDialog();
                //  isCloudConnected = true;
                // webUrl = Constants.CLOUD_SERVER_URL;
                loginDialog(true, true);
            }
        });

        if (toolbarTitle != null) {
            popupWindow.showAsDropDown(toolbarTitle, 0, 0);
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
                //input.getText().toString();
                Log.d("InputIP", "input getText : " + input.getText().toString());
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

        //update user fname and lname after updaing the profile activity
        String userFname = Common.getPrefValue(ChatApplication.getInstance(), "first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(), "last_name");
        if (ChatApplication.isRefreshUserData) {
            if (!TextUtils.isEmpty(userFname) && !TextUtils.isEmpty(userLname)) {
                if (toolbarTitle != null) {
                    toolbarTitle.setText(userFname + " " + userLname);
                }
            }
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setOnClickListener(this);
        Log.d(TAG, "onResume");
        ChatApplication.getInstance().setConnectivityListener(this);
        Log.d(TAG, "onResume isRefreshHome     " + ChatApplication.isRefreshHome);
        Log.d(TAG, "onResume isSocketConnected " + isSocketConnected);
        Log.d(TAG, "onResume isResumeConnect   " + isResumeConnect);
        tabClickItem();
        //webView.loadUrl("about:blank");//&& isSocketConnected
        if (Common.isConnected() && isResumeConnect) {
            Log.d(TAG, "onResume isResumeConnect 1111  " + isResumeConnect);
            hideAlertDialog();
            runService();
            isResumeConnect = false;
            //openSocketandGetList();
        } else if (Common.isConnected() && ChatApplication.isRefreshHome) {
            Log.d(TAG, "onResume isResumeConnect 2222  " + isResumeConnect);
            hideAlertDialog();
            loginDialog(false, false);
            linear_progress.setVisibility(View.GONE);

            ChatApplication.isRefreshHome = false;

        } else if (!Common.isConnected()) {
            Log.d(TAG, "onResume isResumeConnect 3333  " + isResumeConnect);
            hideAlertDialog();
            // new Handler().postDelayed(() -> {
            showAlertDialog(ERROR_STRING);
            // },100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ChatApplication.isPushFound){
            MainFragment.getBadgeClear(Main2Activity.this);
        }
        MainFragment.isRefredCheck=true;
        ChatApplication.isMainFragmentNeedResume = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onTerminate", "android ==onDestroy  onDestroy ");
        if (mSocket != null) {
            mSocket.emit(TAG, "android == disconnect " + mSocket.id());
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);

            // mSocket.off(Socket.EVENT_RECONNECT, onReconnect);
            //   mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            Log.d("ChatSocket", "mSocket onDestroy disconnect();");

            //ChatApplication chatApplication = ChatApplication.getInstance();
            //chatApplication.closeSocket(webUrl);

         /*   mSocket.disconnect();
            mSocket.close();*/
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
                        intent.putExtra("isCheckActivity","AllType");
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
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void logoutCloudUser() {


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
                Log.d("System out", "user name : " + logOutUser.getFirstname() + " " + logOutUser.getLastname() + " " + logOutUser.getUser_id());

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
                ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

                new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        ActivityHelper.dismissProgressDialog();
                        Log.d("onSuccess", "onSuccess onSuccess " + result.toString());

                        int code = 0;
                        try {
                            code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                String jsonCurProduct = gson.toJson(tempList);
                                Log.d("System out", "user name logout: "+tempList.size());

                                if (tempList.size() == 0) {
                                    Common.savePrefValue(getApplicationContext(), Common.USER_JSON, "");
                                } else {
                                    Log.d("System out", "user name logout: "+tempList.get(0).getFirstname()+" "+tempList.get(0).getLastname());

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

                                        toolbarTitle.setText(tempList.get(0).getFirstname()+" "+tempList.get(0).getLastname());

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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (ActivityHelper.isConnectingToInternet(Main2Activity.this)) {
            mToolBarSettings.setVisibility(View.VISIBLE);
            Log.d("System out", "onNetworkConnectionChanged isConnected : " + isConnected);
            Log.d("System out", "onNetworkConnectionChanged isConnected isResumeConnect : " + isResumeConnect);
//            invalidateToolbarCloudImage();
            if (isConnected) {
                // hideAlertDialog();
                if (isResumeConnect) {
                    isResumeConnect = false;

                    //check cloud url
              /*  String isLogin = Common.getPrefValue(getApplicationContext(),Constants.PREF_CLOUDLOGIN);
                String cloudIp= Common.getPrefValue(getApplicationContext(),Constants.PREF_IP);
                if(userId.equalsIgnoreCase("0") && isLogin.equalsIgnoreCase("true") && cloudIp.equalsIgnoreCase(webUrl)){
                    startSocketConnection();
                } else{
                    runService();
                }*/

                    runService();

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
//            linear_progress.setVisibility(View.VISIBLE);
//            mToolBarSettings.setVisibility(View.INVISIBLE);
//            hideAlertDialog();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //deepsImage.setVisibility(View.GONE);
//                    linear_progress.setVisibility(View.GONE);
//                    showAlertDialog(ERROR_STRING);
//                }
//            }, 100);
        }
    }

    @Override
    public void showLogin() {
//        if (piDetailsModel != null) {
            flagPicheck = true;
            loginDialog(true, true);
//        }
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

    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            Log.d("onReconnect", "Reconnect....." + objects);
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("ChatSocket", "Main2 onConnect....");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mSocket != null) {
                        Log.d("SocketIP", "onConnect mSocket.connect()= " + mSocket.id());
                        Log.d(TAG, mSocket.id() + " ===== Listener onConnect== " + webUrl);

                        if (!isSocketConnected) {

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                isTimerStart = false;
                            }
                            Log.d("ChatSocket", "Main2 startSocketConnection mSocket.connect()= " + mSocket.id());

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

            Log.d("ChatSocket", "Main2 onDisconnect....");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null) {
                        Log.i(TAG, mSocket.id() + " ===socketconnection Listener diconnected === " + webUrl);
                    }
                    //hideConnectingCloudDialog();
                    isSocketConnected = false;
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();

                    //if swtich user account could't display error dialog
                    if (!isClick) {
                        //todo code here for open error dialog
                        //openErrorDialog(ERROR_STRING);
                    }
                }
            });
        }
    };

    Timer timer = new Timer();
    final int FPS = 40;

    private boolean isTimerEnd = false;
    private boolean isTimerStart = false;

    private CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            isTimerStart = true;
            Log.d("TimerStart", "Mill : " + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            hideConnectingCloudDialog();
            isTimerEnd = true;
            Log.d("TimerStart", "onFinish...");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("ChatSocket", "Main2 onConnectError....");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // hideConnectingCloudDialog();
                    if (mSocket != null) {
                        Log.e(TAG, mSocket.id() + " socketconnection Listener onConnectError = " + webUrl);
                    }
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT).show();

                    if (!isTimerStart)
                        countDownTimer.start();

                    //todo code here for open error dialog
                    if (isTimerEnd) {

                        //  openErrorDialog(ERROR_STRING);
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
            imei = ActivityHelper.getIMEI(this);
            //onResume();
        }
    }

    @Override
    public void onClick(View view) {
        Log.d("", "onClick ");
        if (view.getId() == R.id.btn_login) {

//            if(flagPicheck){
//                loginWifiCloud();
//            }else {
            loginCloud();
//            }
        } else if (view.getId() == R.id.btn_cancel) {
            loginDialog(false, false);
        } else if (view.getId() == R.id.toolbarImage) {
            showAccountDialog(toolbarTitle);
        }else if (view.getId() == R.id.toolbarTitle) {
            showAccountDialog(toolbarTitle);
        }

    }

    public String webUrl = "";
    public static boolean isCloudConnected = false;
    public String token = "";

    public void loginCloud() {
        Log.d(TAG, "loginCloud");

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(et_username.getText().toString())) {
            et_username.requestFocus();
            et_username.setError("Enter User Name");
            // Toast.makeText(Main2Activity.this.getApplicationContext(), "Enter User Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_password.getText().toString())) {
            et_password.requestFocus();
            et_password.setError("Enter Password");
            //  Toast.makeText(Main2Activity.this.getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        }
        Log.d("FCMToken", "token : " + token);

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_name", et_username.getText().toString().trim());
            obj.put("user_password", et_password.getText().toString().trim());
            obj.put("device_id", imei);
            obj.put("device_type", "android");
            obj.put("device_push_token", token);

        } catch (Exception e) {
            Log.d(TAG, "Exception loginCloud " + e.getMessage());
        }
        //  String url = webUrl + Constants.APP_LOGIN;

        String url = Constants.CLOUD_SERVER_URL + Constants.APP_LOGIN;

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        Log.d(TAG, "obj loginCloud obj " + obj.toString());
        Log.d(TAG, "obj loginCloud url " + url);
        //http://34.212.76.50/applogin
        new GetJsonTask(Main2Activity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "loginCloud onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ActivityHelper.hideKeyboard(Main2Activity.this);

                        ChatApplication app = ChatApplication.getInstance();
                        app.closeSocket(webUrl);

                        if (mSocket != null) {
                            mSocket.disconnect();
                            mSocket = null;
                        }

                        /*{"code":200,"message":"success","data":{"ip":"http:\/\/home.deepfoods.net:11113",
                        "user_id":"1528702842286_HyGYPsieQ",
                        "first_name":"spike","last_name":"bot","user_password":"c1bc06"}}
                        * */

                        JSONObject data = result.getJSONObject("data");
                        String cloudIp = data.getString("ip");

                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, cloudIp);

                        webUrl = cloudIp;
                        ChatApplication.url = webUrl;
                        isCloudConnected = true;
                        flagPicheck = false;
                        invalidateToolbarCloudImage();
                        ////////////////////

                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        String user_id = data.getString("user_id");

                        Common.savePrefValue(Main2Activity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(Main2Activity.this, Constants.PREF_IP, cloudIp);
                        Common.savePrefValue(Main2Activity.this, Constants.USER_ID, user_id);


                        String user_password = "";
                        if (data.has("user_password")) {
                            user_password = data.getString("user_password");
                        }
                        Common.savePrefValue(Main2Activity.this, Constants.USER_PASSWORD, user_password);

                        Log.d("JsonList", "u_id : " + user_id);

                        if (toolbarTitle != null) {
                            toolbarTitle.setText(first_name + " " + last_name);
                        }

                        User user = new User(user_id, first_name, last_name, cloudIp, false, user_password);

                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
                        Log.d("System out", "jsonText text is " + jsonText);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonText) && !jsonText.equals("[]")&& !jsonText.equals("null")) {
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

                        //   String jsonCurProduct = gson.toJson(userList);
                        //  Common.savePrefValue(getApplicationContext(),Common.USER_JSON,jsonCurProduct);

                        ///////////////////

                        // Toast.makeText(Main2Activity.this, TextUtils.isEmpty(message) ? "Login Successfully" : message , Toast.LENGTH_SHORT).show();
                        Toast.makeText(Main2Activity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        openSocket();

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
                        mToolBarSettings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openSettingPopup(toolbar);
                            }
                        });

                    } else {
                        Toast.makeText(Main2Activity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "loginCloud finally ");
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "loginCloud onFailure " + error);
                Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
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
        Log.d("FCMToken", "token : " + token);

        JSONObject obj = new JSONObject();
        try {
            //"wifi_user_name":"admin","wifi_user_password": "123"
            obj.put("wifi_user_name", et_username.getText().toString());
            obj.put("wifi_user_password", et_password.getText().toString());

        } catch (Exception e) {
            Log.d(TAG, "Exception loginCloud " + e.getMessage());
        }
        //'192.168.175.119/wifilogin'
        String url = ChatApplication.url + Constants.wifilogin;

        ActivityHelper.showProgressDialog(Main2Activity.this, "Please wait...", false);

        Log.d(TAG, "obj loginCloud obj " + obj.toString());
        Log.d(TAG, "obj loginCloud url " + url);
        new GetJsonTask(Main2Activity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "wifi onSuccess " + result.toString());
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
                    Log.d(TAG, "loginCloud finally ");
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "loginCloud onFailure " + error);
                Toast.makeText(Main2Activity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    String userId = "0";

    public void openSocket() {

        Log.d("FoundIP", "1 openSocket openSocket====== " + webUrl);
        // webUrl = Constants.CHAT_SERVER_URL;
        // to do remove.
        if (webUrl.equalsIgnoreCase("")) {// webUrl.equalsIgnoreCase(Constants.CLOUD_SERVER_URL) ||
            //Log.d("FoundIP","== openSocket  Service: if......... " + userId );

            String isLogin = Common.getPrefValue(this, Constants.PREF_CLOUDLOGIN);
            String cloudIp = Common.getPrefValue(this, Constants.PREF_IP);
            //  Log.d("FoundIP","openSocketandGetList isLogin " + isLogin );
            Log.d("FoundIP", "2 openSocketandGetList cloudIp " + cloudIp);

            if (userId.equalsIgnoreCase("0") && isLogin.equalsIgnoreCase("true")) {
                Log.d("FoundIP", "3 openSocket and GetList cloudIp " + cloudIp);

                webUrl = cloudIp;//Constants.CLOUD_SERVER_URL;
                ChatApplication.isRefreshHome = true;
                hideAlertDialog();
                startSocketConnection();
                isCloudConnected = true;
                invalidateToolbarCloudImage();
            } else {
                Log.d("FoundIP", "4 openSocketandGetList cloudIp " + cloudIp);

                hideAlertDialog();
                isCloudConnected = true;
                invalidateToolbarCloudImage();
                webUrl = Constants.CLOUD_SERVER_URL;
                loginDialog(true, false);
            }
        } else {
            Log.d("FoundIP", "5 openSocketandGetList cloudIp ");

            hideAlertDialog();
            // isCloudConnected = false;
            Log.d("FoundIP", "== openSocket  Service: else........ ");

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
            Log.d("SignUpCode", "Main2Activity Code : " + data);

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


    public void startSocketConnection() {
        Log.d("ChatSocket", "Main2 startSocketConnection  webUrl " + webUrl);

    /*    if(mSocket!=null && mSocket.connected()){
            return;
        }*/

        if (Constants.isWifiConnect) {
            return;
        }
        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected() && app.url.equalsIgnoreCase(webUrl)) {
            Log.d("ChatSocket", "Main2 if startSocketConnection mSocket id : " + mSocket.id());
        }
        //using @SerializedName we can customize the model name maping.when we are dealing with API and
        //we need to convert JSON response to direct Model class then make sure your api key filed name and model method name both are same.


        //
        else {
            Log.d("ChatSocket", "Main2 else startSocketConnection  webUrl " + webUrl);
            // mSocket = app.openSocket(webUrl);
            mSocket = app.openSocket(webUrl);

            ChatApplication.url = webUrl;

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

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
            scheduleFragment.onLoadFragment();
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
                scheduleFragment.onLoadFragment();
            }
        }
    }

    @Override
    public void onProgress() {
        Log.i("onProgress", "onProgress...");
        showConnectingCloudDialog(LOCAL_MESSAGE);
    }

    @Override
    public void onSuccess() {
        hideConnectingCloudDialog();
    }

    @Override
    public void onBackPressed() {

        /*if(lastTabPos !=0 ){
            if(lastTabPos == 2){
                tabLayout.getTabAt(1).select();
            }else if(lastTabPos == 1){
                tabLayout.getTabAt(0).select();
            }
        }else{
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }

    public AsyncTask asyncTask;

    private void runService() {
        Log.d("FoundIP", "runService runService runService ==-==-=-=-=" + Common.isConnectedToMobile(this));
        if (Common.isConnectedToMobile(this)) {
//            deepsImage.setVisibility(View.VISIBLE);
//            txt_connection.setVisibility(View.VISIBLE);
            linear_progress.setVisibility(View.VISIBLE);
            txt_connection.setText(R.string.connect_cloud);

            Log.d("FoundIP", "Mobile Date connection :: " + webUrl);
            //openWebView(CLOUD_URL);
            //ChatApplication app = (ChatApplication) getActivity().getApplication();
            //mSocket = app.openSocket(Constants.CLOUD_SERVER_URL);
            //webUrl = Constants.CLOUD_SERVER_URL;
            webUrl = ""; //added by sagar
            openSocket();

        } else {
//            deepsImage.setVisibility(View.VISIBLE);
//            txt_connection.setVisibility(View.VISIBLE);
            linear_progress.setVisibility(View.VISIBLE);
            txt_connection.setText(R.string.search_home_controller);

            if (Common.isNetworkConnected(this)) {
                asyncTask = new Main2Activity.ServiceEventAsync().execute();

            }
        }
    }

    /*
     * swap cloud to local connect
     * */
    private void runService2() {
        Log.d("FoundIP", "runService runService runService ==-==-=-=-=" + Common.isConnectedToMobile(this));
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
                asyncTask = new ServiceEventAsync().execute();
            }
        }
    }


    //call service event
    class ServiceEventAsync extends AsyncTask {

        boolean isRegisterListener = false;
        String ipAddressPI = "";

        public ServiceEventAsync() {
            Log.i("ServiceEventLog", "constructor call...");
            showConnectingCloudDialog(LOCAL_MESSAGE);
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            lock = wifi.createMulticastLock("lock");
            lock.setReferenceCounted(true);
            lock.acquire();
        }

        @Override
        protected void onPreExecute() {
            Log.i("ServiceEventLog", "onPreExecute...");
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            Log.i("ServiceEventLog", "doInBackground...");
            try {
                if (lock != null) {
                    if (lock.isHeld()) {
                        lock.release();
                        lock = null;
                    }
                }
                InetAddress addr = getLocalIpAddress();
                //   String hostname = addr.getHostName();
                String hostname = addr.toString().replace("/", "");
                //  jmdns = JmDNS.create(addr,hostname);
                //android-5a169bff9d4a3652
                //192.168.175.130

                String[] array = hostname.split("\\.");
                if (Constants.isTesting) {
                    ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + ChatApplication.testIP;
                } else {
                    ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
                }

                Log.i("CouldFoundIP", "IP Found 1 : " + ipAddressPI);

                //  boolean isReachable = InetAddress.getByName(ipAddressPI).isReachable(5000);
                //  boolean isReachable = Common.IsReachable(Main2Activity.this,"http://"+ipAddressPI);
                boolean isReachable = Common.isPortReachable(ipAddressPI, 80);
                //  boolean isReachable = Common.isReachable(ipAddressPI,80);
                //  boolean isReachable = Common.isReachable(5, 2000, "ipAddressPI");
                // Log.i("isConnectCloud","IP Found 2 : " + isReachable);
                Log.i("CouldFoundIP", "IP Found 3 : " + isReachable);

                if (Constants.isWifiConnect) {

                } else {
                    if (isReachable) {
                        isCloudConnected = false; //added by sagar
                        invalidateToolbarCloudImage();
                        webUrl = ipAddressPI;
                        ChatApplication.url = webUrl;
                        isClick = false;

                    } else {
                        //webUrl = "";
                        ipAddressPI = "";
                        webUrl = ipAddressPI;
                    }
                }
                //  }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i("ServiceEventLog", "onPostExecute...");
            hideConnectingCloudDialog();
            if (Constants.isWifiConnect) {
            } else {
                listService("\nService resolved: ", ipAddressPI, 80, true);
            }

            super.onPostExecute(o);
        }
    }

    public InetAddress getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    private void listService(final String msg, final String ip, final int port, final boolean isFound) {
        Log.d(TAG, "listService() isFound " + isFound);

        final String openIp = (isFound) ? "http://" + ip + ":" + port : Constants.CLOUD_SERVER_URL;

        //  handler.postDelayed(() -> {
        //    final String openIp = (isFound) ? "http://"+ip+":"+port : "http://192.168.75.120:3030";
        Log.d(TAG, "== openIP : " + openIp);
        if (!ip.equalsIgnoreCase("") && !webUrl.equalsIgnoreCase(openIp)) {
            Log.d(TAG, "== openIP Service : 111");
            webUrl = openIp;
            openSocket();
        } else if (ip.equalsIgnoreCase("")) {
            Log.d(TAG, "== openIP  Service: 222");
            // webUrl = openIp ;
            openSocket();
        } else if (webUrl.equalsIgnoreCase(openIp)) {
            Log.d(TAG, "== openIP  Service: 333");
            hideAlertDialog();
            linear_progress.setVisibility(View.GONE);
        }


        //openWebView(openIp);
        //  }, 10);
    }

    public void openErrorDialog(String errrString) {
        Log.d(TAG, " openErrorDialog ");
        hideAlertDialog();
        showAlertDialog(errrString);
    }

    private void showAlertDialog(String erroString) {
        //   Log.d("","showAlertDialog "  );
        hideConnectingCloudDialog();
        txt_error_value.setText(erroString);
        if (linear_retry.getVisibility() == View.GONE) {
            mViewPager.setVisibility(View.GONE);
            linear_retry.setVisibility(View.VISIBLE);
        }

    }

    private void hideAlertDialog() {

        Log.d("", "hideAlertDialog ");
        linear_main.setVisibility(View.VISIBLE);
        // mViewPager.setVisibility(View.VISIBLE);
        linear_retry.setVisibility(View.GONE);
    }

    public void loginDialog(final boolean value, final boolean isCancelButtonVisible) {
        // Log.d("","loginDialog loginDialog " + value );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActivityHelper.dismissProgressDialog();
                et_username.setText("");
                et_password.setText("");

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
        });
    }

    public void setCouldMethod(){
        mainFragment1.RefreshAnotherFragment();
    }
    public static void changestatus(){
        tabLayout.setVisibility(View.VISIBLE);
        mToolBarSettings.setVisibility(View.VISIBLE);
    }

}
