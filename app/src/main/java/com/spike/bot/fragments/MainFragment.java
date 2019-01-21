package com.spike.bot.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.MainActivity;
import com.spike.bot.R;
import com.spike.bot.ack.AckWithTimeOut;
import com.spike.bot.activity.AddUnassignedPanel;
import com.spike.bot.activity.CameraDeviceLogActivity;
import com.spike.bot.activity.CameraEdit;
import com.spike.bot.activity.CameraNotificationActivity;
import com.spike.bot.activity.CameraPlayBack;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.DeviceLogRoomActivity;
import com.spike.bot.activity.DoorSensorInfoActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.NotificationSetting;
import com.spike.bot.activity.ProfileActivity;
import com.spike.bot.activity.RoomEditActivity_v2;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.SensorDoorLogActivity;
import com.spike.bot.activity.SensorUnassignedActivity;
import com.spike.bot.activity.TempSensorInfoActivity;
import com.spike.bot.activity.SignUp;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.adapter.SectionedExpandableGridAdapter;
import com.spike.bot.adapter.SectionedExpandableLayoutHelper;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.listener.RunServiceInterface;
import com.spike.bot.listener.SocketListener;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.PiDetailsModel;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.SendRemoteCommandReq;
import com.spike.bot.model.SendRemoteCommandRes;
import com.spike.bot.model.SensorUnassignedRes;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * DashBoard fragment / Room
 * {@link Main2Activity}
 *
 * @see Fragment
 */

public class MainFragment extends Fragment implements View.OnClickListener, ItemClickListener, SectionedExpandableGridAdapter.CameraClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnSmoothScrollList, TempClickListener {

    private static final String TAG = "MainFragment";
    public static int showDialog=1;
    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private Boolean isSocketConnected = true,flagisCould=false;
    public static Boolean isRefredCheck = true;
    private RecyclerView mMessagesView;
    private Socket mSocket;
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;

    // auto service find
    private static final String SERVICE_TYPE = "_vatsal._tcp.local.";
    private ImageView deepsImage;
    private TextView txt_connection;
    private LinearLayout linear_retry, linear_progress, linear_login;
    private Button button_retry, btn_login;
    EditText et_username, et_password;
    TextView tv_header;
    private ArrayList<RoomVO> roomList = new ArrayList<>();
    //private ConnectivityReceiver connectivityReceiver;
    private String userId = "0";
    private String token_id = "";
    private LinearLayout txt_empty_schedule;

    private Activity activity;
    private TextView txt_empty_text;
    private ImageView empty_add_image;
    public CloudAdapter.CloudClickListener cloudClickListener;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.


    public static   OnHeadlineSelectedListener mCallback;
    RunServiceInterface runServiceInterface;
    ResponseErrorCode responseErrorCode;
    LoginPIEvent loginPIEvent;
    SocketListener socketListener;

    public View view;
    public boolean isNull = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            //this.listener = (MainActivity) context;
            this.activity = (Activity) context;
        }
        try {
            runServiceInterface = (RunServiceInterface) activity;
            mCallback = (OnHeadlineSelectedListener) activity;
            responseErrorCode = (ResponseErrorCode) activity;
            loginPIEvent = (LoginPIEvent) activity;
            socketListener = (SocketListener) activity;
            cloudClickListener = (CloudAdapter.CloudClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }


    /**
     * Display Alert dialog when found door or temp sensor config already configured
     *
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // startSocketConnection();
      /*  connectivityReceiver = new ConnectivityReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);*/

    }

    // com.deep.automation.customview.ExpandableGridView exp_list;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onRefresh() {
        isRefredCheck = true;
        if (Main2Activity.isCloudConnected) {
            runServiceInterface.executeService();
        } else {
            getDeviceList(1);
        }
        swipeRefreshLayout.setRefreshing(true);
        sectionedExpandableLayoutHelper.setClickable(false);

    }

    public void RefreshAnotherFragment(){
        isRefredCheck = true;
        if (Main2Activity.isCloudConnected) {
            runServiceInterface.executeService();
        } else {
            getDeviceList(12);
        }
        swipeRefreshLayout.setRefreshing(true);
        sectionedExpandableLayoutHelper.setClickable(false);
    }

    private NestedScrollView main_scroll;
    private FloatingActionButton mFab;
    private CardView mFabMenuLayout;
    private TextView fab_menu1, fab_menu2, fab_menu3, fab_menu4, fab_menu5, fab_menu6, fab_menu7;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        main_scroll = (NestedScrollView) view.findViewById(R.id.main_scroll);

        deepsImage = (ImageView) view.findViewById(R.id.deepsImage);
        txt_connection = (TextView) view.findViewById(R.id.txt_connection);
        //retry
        linear_retry = (LinearLayout) view.findViewById(R.id.linear_retry);
        button_retry = (Button) view.findViewById(R.id.button_retry);
        linear_progress = (LinearLayout) view.findViewById(R.id.linear_progress);
        // login details
        linear_login = (LinearLayout) view.findViewById(R.id.linear_login);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        et_username = (EditText) view.findViewById(R.id.et_username);
        et_password = (EditText) view.findViewById(R.id.et_password);
        tv_header = (TextView) view.findViewById(R.id.tv_header);

        txt_empty_text = (TextView) view.findViewById(R.id.txt_empty_text);

        txt_empty_schedule = (LinearLayout) view.findViewById(R.id.txt_empty_schedule);
        empty_add_image = (ImageView) view.findViewById(R.id.empty_add_image);

        txt_empty_schedule.setVisibility(View.GONE);
        btn_login.setOnClickListener(this);

        ChatApplication.isRefreshDashBoard = true;

        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
        sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);

        if (!isCallVisibleHint) {
            //Log.d("SocketIP","1 !isCallVisibleHint...");
            onLoadFragment();
            //  startSocketConnection();
        }

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomRoom();
            }
        });

        mFabMenuLayout = (CardView) view.findViewById(R.id.fabLayout1);
        fab_menu1 = (TextView) view.findViewById(R.id.fab_menu1);
        fab_menu2 = (TextView) view.findViewById(R.id.fab_menu2);
        fab_menu3 = (TextView) view.findViewById(R.id.fab_menu3);
        fab_menu4 = (TextView) view.findViewById(R.id.fab_menu4);
        fab_menu5 = (TextView) view.findViewById(R.id.fab_menu5);
        fab_menu6 = (TextView) view.findViewById(R.id.fab_menu6);
        fab_menu7 = (TextView) view.findViewById(R.id.fab_menu7);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }

            }
        });

        fab_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                ChatApplication.isOpenDialog = false;
                addCustomRoom();
            }
        });

        fab_menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                if (Common.getPrefValue(getContext(), Common.camera_key).equalsIgnoreCase("0")) {
                    addKeyCamera();
                } else {
                    addCamera();
                }
            }
        });

        fab_menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_TYPE_DOOR);
            }
        });
        fab_menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_TYPE_TEMP);
            }
        });

        fab_menu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(getActivity(), IRRemoteAdd.class));
            }
        });

        fab_menu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(getActivity(), IRBlasterAddActivity.class));
            }
        });

        fab_menu7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                getUnAssignedList();
            }
        });


        return view;
    }

    /**
     * Display Fab Menu with subFab Button
     */
    boolean isFABOpen = false;

    private void showFABMenu() {

        isFABOpen = true;
        mFabMenuLayout.setVisibility(View.VISIBLE);
        //mFab.animate().rotationBy(180);
        //mFabMenuLayout.animate().translationY(-getResources().getDimension(R.dimen.appbar_padding_top));
    }

    public void closeFABMenu() {
        isFABOpen = false;
        mFabMenuLayout.setVisibility(View.GONE);
        //mFab.animate().rotationBy(-180);
        // mFabMenuLayout.animate().translationY(0);
        if (!isFABOpen) {
            mFabMenuLayout.setVisibility(View.GONE);
        }
    }

    private boolean isCallVisibleHint = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCallVisibleHint = true;
        //   Log.d("SocketIP","2 isCallVisibleHint...");
        if (isVisibleToUser) {

            onLoadFragment();
            // startSocketConnection();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("System out", "flag login is " + Main2Activity.flagLogin);
//        if(Main2Activity.flagLogin){
//            Main2Activity.flagLogin=false;
//            mFab.setVisibility(View.VISIBLE);
//            empty_add_image.setVisibility(View.VISIBLE);
//            txt_empty_text.setText("Add Room");
//            linear_retry.setVisibility(View.GONE);
//        }else {
//            if (Main2Activity.flagPicheck) {
//                mFab.setVisibility(View.GONE);
//                empty_add_image.setVisibility(View.GONE);
//                txt_empty_text.setText("Login First.");
//            } else {
        mFab.setVisibility(View.VISIBLE);
        empty_add_image.setVisibility(View.VISIBLE);
        txt_empty_text.setText("Add Room");
//            }
//        }
        if (ChatApplication.isLocalFragmentResume) {
            Log.d("RefreshList", "resumeLocal side");
            ChatApplication.isLocalFragmentResume = false;
            getDeviceList(2);

        } else if (ChatApplication.isMainFragmentNeedResume) {
            Log.d("RefreshList", "resumeLocal side11111");
            ChatApplication.isMainFragmentNeedResume = false;
            onLoadFragment();
        }
    }

    @Override
    public void onClick(View view) {
        // Log.d(TAG, "onClick ");
        if (view.getId() == R.id.btn_login) {
            //loginCloud();
        }
    }

    /**
     * {@link SectionedExpandableGridAdapter#onSmoothScrollList}
     *
     * @param position
     */
    @Override
    public void onPoisitionClick(final int position) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                closeFABMenu();

                LinearLayoutManager layoutManager = ((LinearLayoutManager) mMessagesView.getLayoutManager());

                main_scroll.getParent().requestChildFocus(mMessagesView, mMessagesView);

                //View view = mMessagesView.getChildAt(position);

                // main_scroll.scrollTo(30, (int) view.getY());

                //main_scroll.fullScroll(ScrollView.FOCUS_DOWN);

                //int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
                //int lastItemPosition  = layoutManager.findLastVisibleItemPosition();


                main_scroll.smoothScrollBy(50, 100);
            }
        });
    }

    public void startSocketConnection() { //◉

        ChatApplication app = ChatApplication.getInstance();
        webUrl = app.url;
        Log.d("ChatSocket", "MainFragment startSocketConnection  webUrl " + webUrl);

        if (mSocket != null && mSocket.connected()) {
            Log.d("ChatSocket", "MainFragment if mSocket.connected  mScoket id :" + mSocket.id());
        } else {

            mSocket = app.getSocket();

            try {
                Log.d("ChatSocket", "MainFragment else startSocketConnection  webUrl " + webUrl);

                mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);
                mSocket.on("roomStatus", roomStatus);
                mSocket.on("panelStatus", panelStatus);
                //    mSocket.on("configureGatewayDevice", configureGatewayDevice);
                mSocket.on("saveEditSwitchSocket", saveEditSwitchSocket);
             //   mSocket.on("configureDoorSensor", configureGatewayDoorSensor);
                //   mSocket.on("configureTempSensor",configureTempSensor);
                mSocket.on("changeDoorSensorStatus", changeDoorSensorStatus);
                mSocket.on("changeTempSensorValue", changeTempSensorValue);
                mSocket.on("unReadCount", unReadCount);
                mSocket.on("sensorStatus", sensorStatus);
                //   mSocket.on("configureIRBlaster",configureIRBlaster);


                //  mSocket.connect();
                Log.d("ChatSocket", "MainFragment mSocket.connect()= " + mSocket.id());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        //        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        //        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        //  mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.emit("socketconnection", "android == disconnecti " + mSocket.id());
           /* mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);*/
            //   mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("ReloadDeviceStatusApp", reloadDeviceStatusApp);
            mSocket.off("roomStatus", roomStatus);
            mSocket.off("panelStatus", panelStatus);
            mSocket.off("saveEditSwitchSocket", saveEditSwitchSocket);
            //  mSocket.off("configureGatewayDevice", configureGatewayDevice);
    //        mSocket.off("configureGatewayDoorSensor", configureGatewayDoorSensor);
            // mSocket.off("configureTempSensor",configureTempSensor);
            mSocket.off("changeDoorSensorStatus", changeDoorSensorStatus);
            mSocket.off("changeTempSensorValue", changeTempSensorValue);
            mSocket.off("unReadCount", unReadCount);
            mSocket.off("sensorStatus", sensorStatus);
            //   mSocket.off("configureIRBlaster",configureIRBlaster);

            Log.d("", "mSocket onDestroy disconnect();");
        }
    }

    @Override
    public void onPause(){
//        if(ChatApplication.isPushFound){
//            getBadgeClear(getActivity());
//        }
        super.onPause();
    }

    public void onLoadFragment() {

        //  Log.d("SocketIP","on Resume MainFragment...");

        /*ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            Log.d("ChatSocket", "MainFragment if onLoadFragment  mSocket ID : " + mSocket.id());
        } else {
            Log.d("ChatSocket", "MainFragment else mSocket open url : " + app.url);
            mSocket = app.getSocket();
        }
        webUrl = app.url;*/

        // mSocket = app.openSocket(webUrl);
        if (activity != null) {
            Log.d("SocketIP", "activity!=null");
            Main2Activity activity = (Main2Activity) getActivity();
            if (activity != null) {
                isSocketConnected = activity.isSocketConnected;
            }
            isResumeConnect = activity.isResumeConnect;
            isCloudConnected = activity.isCloudConnected;
            socketListener.startSession();
        }

        //webView.loadUrl("about:blank");//&& isSocketConnected
        // Log.d("SocketIP","Common connected : " + Common.isConnected() + "  isResumeConnect : " + isResumeConnect + " isRefreshDashboard : " + ChatApplication.isRefreshDashBoard);
        if (Common.isConnected() && isResumeConnect) {
            Log.d(TAG, "onResume isResumeConnect 1111  " + isResumeConnect);
            //hideAlertDialog();
            //runService();
            isResumeConnect = false;
            //openSocketandGetList();
            //  startSocketConnection();
            getDeviceList(3);
        } else if (Common.isConnected() && ChatApplication.isRefreshDashBoard) {
            Log.d(TAG, "onResume isResumeConnect 22222  " + ChatApplication.isRefreshDashBoard);
            getDeviceList(4);
            //  startSocketConnection();
            ChatApplication.isRefreshDashBoard = true; //false

        } else if (!Common.isConnected()) {
            Log.d(TAG, "onResume isResumeConnect 33333  ");
            //hideAlertDialog();
            // showAlertDialog();
        } else {
            Log.d(TAG, "else Co..........");

            getDeviceList(5);

        }
        //getDeviceList();
        // startSocketConnection();


    }


    Menu menu;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
    }

    public void hideShowMenu(boolean flag) {
        if (menu != null) {

            try {
                menu.findItem(R.id.action_add).setVisible(flag);
                menu.findItem(R.id.action_setting).setVisible(flag);
            } catch (Exception e) {
                Log.d("", "Exception " + e.getMessage());
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            openAddPopup(toolbar);//tv_header
            return true;
        } else if (id == R.id.action_setting) {
            openSettingPopup(toolbar);//tv_header
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openAddPopup(final View v) {//,final ICallBackAction actionCallBack
        PopupMenu popup = new PopupMenu(getActivity(), v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_room_add_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(activity, " moveAction - "  ,Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_add_room:
                        /*AddRoomDialog addRoomDialog = new AddRoomDialog(getActivity());
                        addRoomDialog.show();
                        boolean addRoom = false;*/
                        ChatApplication.isOpenDialog = false;
                        // getconfigureData();
                        addCustomRoom();
                        //
                        break;
                    case R.id.action_add_camera:
                        addCamera();
                        break;

                    case R.id.action_add_door_sensor:
                        showOptionDialog(SENSOR_TYPE_DOOR);

                        break;
                    case R.id.action_add_temp:
                        showOptionDialog(SENSOR_TYPE_TEMP);
                        break;
                    case R.id.action_ir_blaster:
                        startActivity(new Intent(getActivity(), IRBlasterAddActivity.class));
                        //old flow code
                        //showOptionDialog(SENSOR_TYPE_IR);
                        //addIRBlaster();
                        break;
                    case R.id.action_add_remote:
                        startActivity(new Intent(getActivity(), IRRemoteAdd.class));
                        break;
                    case R.id.action_unassigned_panel:
                        getUnAssignedList();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private List<UnassignedListRes.Data.RoomList> roomListUn;

    private void getUnAssignedList() {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            showToast("" + R.string.disconnect);
            return;
        }
        //  ActivityHelper.showProgressDialog(getContext(),"Loading...",false);

        String url = ChatApplication.url + Constants.GET_ALL_UNASSIGNED_DEVICES;

        new GetJsonTask(getContext(), url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.d("ResultList", "onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        UnassignedListRes unassignedListRes = Common.jsonToPojo(result.toString(), UnassignedListRes.class);
                        roomListUn = unassignedListRes.getData().getRoomList();
                        if (roomListUn.size() > 0) {
                            startActivity(new Intent(getActivity(), AddUnassignedPanel.class));
                        } else {
                            Toast.makeText(getContext(), "No Unassigned Module", Toast.LENGTH_SHORT).show();
                            //Common.showToast("No Unassigned Module");
                        }

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
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

    public static int SENSOR_TYPE_DOOR = 1;
    public static int SENSOR_TYPE_TEMP = 2;
    public static int SENSOR_TYPE_IR = 3;

    private void showOptionDialog(final int sensor_type) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Sensor Type");

        Button btn_sync = (Button) dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button) dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (sensor_type == SENSOR_TYPE_DOOR) {
                    getconfigureData();
                } else if (sensor_type == SENSOR_TYPE_TEMP) {
                    getTempConfigData();
                } else if (sensor_type == SENSOR_TYPE_IR) {
                    getIRBlasterConfigData();
                }
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                isUnassignedDoorSensor(sensor_type);

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void isUnassignedDoorSensor(final int isDoorSensor) {

        String url = "";
        if (isDoorSensor == SENSOR_TYPE_DOOR) {
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/0"; //0 door - 1 door
        } else if (isDoorSensor == SENSOR_TYPE_TEMP) {
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/1"; //0 door - 1 temp
        } else if (isDoorSensor == SENSOR_TYPE_IR) {
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir
        }

        new GetJsonTask(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                //   ActivityHelper.dismissProgressDialog();
                Log.d("doorSensorUnAsigned", "result : " + result.toString());

                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(), SensorUnassignedRes.class);

                if (sensorUnassignedRes.getCode() == 200) {

                    if (sensorUnassignedRes.getData() != null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0) {
                        Intent intent = new Intent(getActivity(), SensorUnassignedActivity.class);
                        Log.i("SensorType", "Type isDoorSensor : " + isDoorSensor);
                        intent.putExtra("isDoorSensor", isDoorSensor);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), sensorUnassignedRes.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();


    }

    private void addCamera() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_camera);
        dialog.setCanceledOnTouchOutside(false);

        final TextInputEditText camera_name = (TextInputEditText) dialog.findViewById(R.id.txt_camera_name);
        final TextInputEditText camera_ip = (TextInputEditText) dialog.findViewById(R.id.txt_camera_ip);
        final TextInputEditText video_path = (TextInputEditText) dialog.findViewById(R.id.txt_video_path);
        final TextInputEditText user_name = (TextInputEditText) dialog.findViewById(R.id.txt_user_name);
        final TextInputEditText password = (TextInputEditText) dialog.findViewById(R.id.txt_password);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(camera_name.getText().toString())) {
                    camera_name.requestFocus();
                    camera_name.setError("Enter Camera name");
                    //  Toast.makeText(getContext(),"Please select Camera name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(camera_ip.getText().toString())) {
                    camera_ip.requestFocus();
                    camera_ip.setError("Enter Camera IP");
                    //Toast.makeText(getContext(),"Please select Camera IP",Toast.LENGTH_SHORT).show();
                    return;
                }
               /* if(!ActivityHelper.isValidUrl(camera_ip.getText().toString())){
                    camera_ip.requestFocus();
                    camera_ip.setError("Invallid Camera IP");
                    //Toast.makeText(getContext(),"Invalid Camera IP Address",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (TextUtils.isEmpty(video_path.getText().toString())) {
                    video_path.requestFocus();
                    video_path.setError("Enter Video Path");
                    //  Toast.makeText(getContext(),"Please select Video Path",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(user_name.getText().toString())) {
                    user_name.requestFocus();
                    user_name.setError("Enter User Name");
                    //Toast.makeText(getContext(),"Please select User Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.requestFocus();
                    password.setError("Enter Password");
                    //Toast.makeText(getContext(),"Please select Password",Toast.LENGTH_SHORT).show();
                    return;
                }

                addCameraCall(camera_name.getText().toString(), camera_ip.getText().toString(), video_path.getText().toString(), user_name.getText().toString(),
                        password.getText().toString(), dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /**
     * @param camera_name : c1
     * @param camera_ip   : 192.168.75.113
     * @param video_path  : /live/streaming
     * @param user_name   : abcd
     * @param password    : 123...
     * @param dialog      : if(response code == 200) dismiss dialog
     */
    private void addCameraCall(String camera_name, String camera_ip, String video_path, String user_name, String password, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

            obj.put("camera_name", camera_name);
            obj.put("camera_ip", camera_ip);
            obj.put("video_path", video_path);
            obj.put("user_name", user_name);
            obj.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("cameraObj", "obj : " + obj.toString());

        String url = ChatApplication.url + Constants.ADD_CAMERA;

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getDeviceList(6);
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    /*
     * Camera IP address validation
     * @return true if validate
     *
     * */
    private boolean isValidateIP(String ipAddress) {
        return Patterns.IP_ADDRESS.matcher(ipAddress).matches();
    }


    //not used
    public void openSettingPopup(final View v) {//,final ICallBackAction actionCallBack
        PopupMenu popup = new PopupMenu(getActivity(), v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1, new String[]{});
        arrayAdapter.notifyDataSetChanged();
        popup.getMenuInflater().inflate(R.menu.menu_room_setting_popup, popup.getMenu());

        final Main2Activity activity = (Main2Activity) getActivity();
        if (!activity.isCloudConnected) {
            popup.getMenu().findItem(R.id.action_logout).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(activity, " moveAction - "  ,Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_device_log:
                        // getconfigureData();
                        Intent intent = new Intent(activity, DeviceLogActivity.class);
                        intent.putExtra("isCheckActivity","AllType");
                        startActivity(intent);
                        break;
                    case R.id.action_sensor_log:
                        Intent logIntent = new Intent(getActivity(), SensorDoorLogActivity.class);
                        startActivity(logIntent);
                        break;
                    case R.id.action_notification_settings:
                        Intent intentNotification = new Intent(activity, NotificationSetting.class);
                        startActivity(intentNotification);
                        break;
                    case R.id.action_profile:
                        Intent intentProfile = new Intent(activity, ProfileActivity.class);
                        startActivity(intentProfile);
                        break;
                    case R.id.action_logout:
                        //clear pref and open login screen
                        Common.savePrefValue(getActivity(), Constants.PREF_CLOUDLOGIN, "false");
                        Common.savePrefValue(getActivity(), Constants.PREF_IP, "");
                        ChatApplication app = ChatApplication.getInstance();
                        //webUrl = "";
                        // isCloudConnected = true;
                        //  mSocket = app.closeSocket(webUrl);
                        app.closeSocket(webUrl);
                        // loginDialog(true);
                        Main2Activity activity = (Main2Activity) getActivity();
                        activity.loginDialog(true, false);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    //// not used
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("", mSocket.id() + " ===== Listener onConnect== " + webUrl);
                    if (!isSocketConnected) {
                        //  hideAlertDialog();
                        //if(null!=mUsername)
                        //mSocket.emit("add user", mUsername);
                        //Toast.makeText(getActivity().getApplicationContext(), R.string.connect, Toast.LENGTH_SHORT).show();
                        isSocketConnected = true;
                    }
                    mSocket.emit("socketconnection", "android == startconnect  " + mSocket.id());
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, mSocket.id() + " === Listener diconnected === " + webUrl);
                    isSocketConnected = false;
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                    // openErrorDialog();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, mSocket.id() + " Listener onConnectError = " + webUrl);
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT).show();
                    //   openErrorDialog();
                }
            });
        }
    };
    ////////
    private Emitter.Listener reloadDeviceStatusApp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            Log.d("sensorV3", "reloadDeviceStatusApp : " + args.length + " toString : " + args[0]);

                            JSONObject object = new JSONObject(args[0].toString());
                            String module_id = object.getString("module_id");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int is_locked = object.optInt("is_locked");

                            sectionedExpandableLayoutHelper.updateItem(module_id, device_id, device_status,is_locked);
                            //   sectionedExpandableLayoutHelper.notifyDataSetChanged();


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    /**
     * Update panel status
     */
    private Emitter.Listener panelStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {

                            Log.d("sensorV3", "panelStatus : " + args.length + " toString : " + args.toString());

                            JSONObject object = new JSONObject(args[0].toString());
                            // String room_order = object.getString("room_order");
                            // String panel_order = object.getString("panel_order");
                            String panel_id = object.getString("panel_id");
                            String panelstatusValue = object.getString("panel_status");

                            sectionedExpandableLayoutHelper.updatePanel(panel_id, panelstatusValue);
                            // sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    /**
     * Update Room status
     */
    private Emitter.Listener roomStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {

                            Log.d("sensorV3", "roomstatus : " + args.length + " toString : " + args[0]);

                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String roomupdateValue = object.getString("room_status");

                            sectionedExpandableLayoutHelper.updateRoom(room_id, roomupdateValue);
                            // sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    /**
     * Save Edit Switch Socket
     */
    private Emitter.Listener saveEditSwitchSocket = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            //  Log.d("saveEditSwitchSocket " + args.length, mSocket.id() + " saveEditSwitchSocket moduleid  1 " + args[0].toString());
                            //   Log.d("saveEditSwitchSocket ", " roomStatus deviceid 2 " + args[1]);
                            //   String id = args[0].toString();
                            //  String status = args[1].toString();
                            //update UI from other changes
//                            sectionedExpandableLayoutHelper.updatePanel(id, status);
//                            sectionedExpandableLayoutHelper.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    public AddRoomDialog addRoomDialog;

    /**
     * Configure Gateway Door Sensor
     */
    private Emitter.Listener configureGatewayDoorSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //  Toast.makeText(getContext(),"Found Door Sensor : "   + args[0],Toast.LENGTH_LONG).show();
                    //{"door_sensor_module_id":"D2D3C612004B1200"}

                    try {

                        Log.d("configGatewayDoorSensor", "Found sensor id : " + args[0]);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        JSONObject object = new JSONObject(args[0].toString());
                        String door_sensor_module_id = object.getString("door_sensor_module_id");

                        String message = object.getString("message");

                        if (TextUtils.isEmpty(message)) {

                            JSONArray jsonArray = object.getJSONArray("room_list");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }
                        }


                        ActivityHelper.dismissProgressDialog();

                        if (TextUtils.isEmpty(message)) {
                            showAddSensorDialog(door_sensor_module_id, false);
                        } else {
                            showConfigAlert(message);
                            //Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                        }

                        //  addRoom = false;
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };


    private Emitter.Listener configureTempSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        Log.d("configureTempSensor", "Found sensor id : " + args[0]);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        //{"message":"Temperature Sensor already configured. Add from Unconfigured Sensor devices","temp_module_id":"","room_list":""}
                        JSONObject object = new JSONObject(args[0].toString());

                        Log.d("configureTempSensor", "Object : " + object.toString());
                        String message = object.getString("message");

                        String temp_sensor_module_id = object.getString("temp_sensor_module_id");

                        if (TextUtils.isEmpty(message)) {
                           /* String roomList = object.getString("room_list");

                            Object json = new JSONTokener(roomList).nextValue();*/

                            JSONArray jsonArray = object.getJSONArray("room_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }

                           /* if (json instanceof JSONObject){

                            }else if (json instanceof JSONArray){

                            }*/
                        }

                        ActivityHelper.dismissProgressDialog();

                        if (TextUtils.isEmpty(message)) {
                            showAddSensorDialog(temp_sensor_module_id, true);
                        } else {
                            showConfigAlert(message);
                            //Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        }


                        //  addRoom = false;
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener changeDoorSensorStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        Log.d("mSocketEmitter", "changeDoorSensorStatus >>>> " + args[0].toString());

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_order = object.getString("room_order");
                            String door_sensor_id = object.getString("door_sensor_id");
                            String door_sensor_status = object.getString("door_sensor_status");

                            sectionedExpandableLayoutHelper.updateDoorStatus(room_order, door_sensor_id, door_sensor_status);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    private Emitter.Listener unReadCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        Log.d("mSocketEmitter", "unReadCount >>>> " + args[0].toString());
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String sensor_type = object.getString("sensor_type");
                            String sensor_unread = object.getString("sensor_unread");
                            String module_id = object.getString("module_id");
                            String room_id = object.getString("room_id");
                            String room_unread = object.getString("room_unread");

                            sectionedExpandableLayoutHelper.updateBadgeCount(sensor_type, sensor_unread, module_id, room_id, room_unread);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener changeTempSensorValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {

                        Log.d("mSocketEmitter", "changeTempSensorValue >>>> " + args[0].toString());

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String room_order = object.getString("room_order");
                            String temp_sensor_id = object.getString("temp_sensor_id");
                            String temp_celsius = object.getString("temp_celsius");
                            String temp_fahrenheit = object.getString("temp_fahrenheit");
                            String is_in_C = object.getString("is_in_C");

                            sectionedExpandableLayoutHelper.updateTempSensor(room_id, room_order, temp_sensor_id, temp_celsius, temp_fahrenheit, is_in_C);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    };

    private Emitter.Listener sensorStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        Log.d("mSocketEmitter", "sensorStatus : " + args[0].toString());

                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            String is_active = jsonObject.getString("is_active");
                            String sensor_id = "";
                            if (jsonObject.has("sensor_id")) {
                                sensor_id = jsonObject.getString("sensor_id");
                            } else if (jsonObject.has("door_sensor_id")) {
                                sensor_id = jsonObject.getString("door_sensor_id");
                            }

                            //{"door_sensor_id":"1527913226555_Bk7fo51gQ","is_active":1}

                            sectionedExpandableLayoutHelper.updateDeadSensor(sensor_id, is_active);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
        }
    };

    private Emitter.Listener configureIRBlaster = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (activity == null) return;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {

                            Log.d("IRBlaster", "Found sensor id : " + args[0]);

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }

                            roomIdList.clear();
                            roomNameList.clear();

                            JSONObject object = new JSONObject(args[0].toString());

                            String message = object.getString("message");

                            String ir_module_id = object.getString("ir_blaster_module_id");

                            if (TextUtils.isEmpty(message)) {

                                JSONArray jsonArray = object.getJSONArray("room_list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objectRoom = jsonArray.getJSONObject(i);
                                    String room_id = objectRoom.getString("room_id");
                                    String room_name = objectRoom.getString("room_name");

                                    roomIdList.add(room_id);
                                    roomNameList.add(room_name);
                                }

                            }

                            ActivityHelper.dismissProgressDialog();

                            if (TextUtils.isEmpty(message)) {
                                showIRSensorDialog(ir_module_id);
                            } else {
                                showConfigAlert(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };


    ArrayList<String> roomIdList = new ArrayList<>();

    ArrayList<String> roomNameList = new ArrayList<>();
    private boolean isShowSensor = false;

    private Emitter.Listener changeRoomPanelMoodStatus = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {

        }
    };

    Dialog irDialog;

    private void showIRSensorDialog(String ir_module_id) {

        if (irDialog == null) {
            irDialog = new Dialog(getActivity());
            irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            irDialog.setContentView(R.layout.dialog_add_sensordoor);
            irDialog.setCanceledOnTouchOutside(false);
        }

        final EditText edt_door_name = (EditText) irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) irDialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = (TextView) irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) irDialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add IR Blaster");
        txt_sensor_name.setText("IR Name");

        edt_door_module_id.setText(ir_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        int spinner_position = sp_room_list.getSelectedItemPosition();

        sp_room_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_cancel = (Button) irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = (Button) irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = (ImageView) irDialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                irDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                irDialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                saveIRBlaster(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(),
                        sp_room_list);
            }
        });

        if (!irDialog.isShowing() && !isShowSensor) {
            isShowSensor = true;
            irDialog.show();
        }

    }

    /**
     * showAddSensorDialog
     *
     * @param door_module_id
     * @param isTempSensorRequest
     */
    private void showAddSensorDialog(String door_module_id, final boolean isTempSensorRequest) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = (EditText) dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) dialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) dialog.findViewById(R.id.txt_sensor_name);

        if (isTempSensorRequest) {
            dialogTitle.setText("Add Temp Sensor");
            txt_sensor_name.setText("Temp Name");
        } else {
            dialogTitle.setText("Add Door Sensor");
            txt_sensor_name.setText("Door Name");
        }
        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        int spinner_position = sp_room_list.getSelectedItemPosition();

        sp_room_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                saveSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                dialog.dismiss();
            }
        });

        if (!dialog.isShowing() && !isShowSensor) {
            isShowSensor = true;
            dialog.show();
        }

    }

    /**
     * save IR Blaster
     *
     * @param dialog
     * @param textInputEditText
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     */
    private void saveIRBlaster(final Dialog dialog, EditText textInputEditText, String door_name,
                               String door_module_id, Spinner sp_room_list) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.requestFocus();
            textInputEditText.setError("Enter IR Name");
            return;
        }

        ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("ir_blaster_name", door_name);
            obj.put("ir_blaster_module_id", door_module_id);

            obj.put("room_id", roomIdList.get(room_pos));
            obj.put("room_name", roomNameList.get(room_pos));

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomOBJ", "obj : " + obj.toString());
        String url = "";

        url = ChatApplication.url + Constants.ADD_IR_BLASTER;

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * @param dialog              - for dismiss
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     * @param isTempSensorRequest
     */

    private void saveSensor(final Dialog dialog, EditText textInputEditText, String door_name,
                            String door_module_id, Spinner sp_room_list, final boolean isTempSensorRequest) {

        Log.d(TAG, "configureNewRoom configureGatewayDevice");
        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.setError(isTempSensorRequest ? "Enter Temp Name" : "Enter Door Name");
            textInputEditText.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("room_id", roomIdList.get(room_pos));
            obj.put("room_name", roomNameList.get(room_pos));

            if (isTempSensorRequest) {
                obj.put("temp_sensor_name", door_name);
                obj.put("temp_sensor_module_id", door_module_id);
            } else {
                obj.put("door_sensor_name", door_name);
                obj.put("door_sensor_module_id", door_module_id);
            }
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomOBJ", "obj : " + obj.toString());
        String url = "";
        if (isTempSensorRequest) {
            url = ChatApplication.url + Constants.ADD_TEMP_SENSOR;
        } else {
            url = ChatApplication.url + Constants.ADD_DOOR_SENSOR;
        }

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, isTempSensorRequest + " configureNewSensor onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, isTempSensorRequest + " configureNewSensor onFailure " + error);
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private Emitter.Listener configureGatewayDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        Log.d("configureGatewayDevice " + args.length, mSocket.id() + " configureGatewayDevice moduleid  1 " + args[0]);
                        //Log.d("configureGatewayDevice ", " configureGatewayDevice deviceid 2 " + args[1]);

                        String module_id = "";//args[0].toString();
                        String device_id = "";//args[1].toString();
                        try {
                            JSONObject obj = new JSONObject(args[0].toString());
                            module_id = obj.getString("module_id");
                            device_id = obj.getString("device_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            // to do remove.
                            //if(addRoom ){ //|| true

                            ActivityHelper.dismissProgressDialog();

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }

                            if (addRoomDialog != null) {
                                if (addRoomDialog.isShowing()) {
                                    addRoomDialog.dismiss();
                                }
                            }

                            addRoomDialog = new AddRoomDialog(getActivity(), "", "", module_id, device_id, "", new ICallback() {
                                @Override
                                public void onSuccess(String str) {
                                    Log.d("", "str " + str);
                                    if (str.equalsIgnoreCase("yes")) {
                                        getDeviceList(7);
                                    }
                                }
                            });
                            if (!addRoomDialog.isShowing()) {
                                addRoomDialog.show();
                            }
                            addRoom = false;
                            // }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };


    //panelStatus
    //roomStatus
    private void deviceOnOff(final DeviceVO deviceVO, final int position) {

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

            obj.put("room_device_id", deviceVO.getRoomDeviceId());
            obj.put("module_id", deviceVO.getModuleId());
            obj.put("device_id", deviceVO.getDeviceId());
            obj.put("device_status", deviceVO.getOldStatus());
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
            //   obj.put("is_change","0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket != null && !mSocket.connected()) {

            Log.d("DeviceOnOff", "Api...");
            callDeviceOnOffApi(deviceVO, obj);

        } else {

            Log.d("DeviceOnOff", "Socket...");

            //   Log.d("sotID", mSocket.id() + " socketChangeDevice deviceOnOff " + obj.toString());

            mSocket.emit("socketChangeDeviceAck", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
                @Override
                public void call(Object... args) {

                    if (args != null) {

                        if (args[0].toString().equalsIgnoreCase("No Ack")) {

                            Log.d("ACK_SOCKET", "AckWithTimeOut : " + args[0].toString());
                            updateDeviceOfflineMode(deviceVO);

                        } else if (args[0].toString().equalsIgnoreCase("true")) {
                            cancelTimer();
                            Log.d("ACK_SOCKET", "AckWithTimeOut : " + args[0].toString());
                        }
                    }
                }
            });

            /*mSocket.emit("socketChangeDevice", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    if(args!=null){
                        Ack ack = (Ack) args[args.length - 1];
                        ack.call();
                        Log.d("ACK_SOCKET","isAck : "+ ack);
                    }
                }
            });*/
        }


        //  sectionedExpandableLayoutHelper.notifyDataSetChanged();
    }

    /**
     * offline device status updated
     *
     * @param deviceVO
     */
    private void updateDeviceOfflineMode(final DeviceVO deviceVO) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper.updateItem(deviceVO.getModuleId(),
                        String.valueOf(deviceVO.getDeviceId()), String.valueOf(deviceVO.getOldStatus()),deviceVO.getIs_locked());
            }
        });
    }

    private void updateRoomOfflineMode(final RoomVO roomVO) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper.updateRoom(roomVO.getRoomId(), String.valueOf(roomVO.getOld_room_status()));
            }
        });
    }

    /**
     * offline panel device status updated
     *
     * @param panelVO
     */
    private void updatePanelDeviceOfflineMode(final PanelVO panelVO) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper.updatePanel(panelVO.getPanelId(), String.valueOf(panelVO.getOldStatus()));
            }
        });
    }

    private void callDeviceOnOffApi(final DeviceVO deviceVO, JSONObject obj) {

        String url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;

        Log.d("DeviceStatus", "Device roomPanelOnOff obj " + obj.toString());
        //  Log.d(TAG, "roomPanelOnOff url " + url );

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("DeviceStatus", "roomPanelOnOff onSuccess " + result.toString());
                try {
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code == 200) {
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Log.d("DeviceStatus", "Device roomPanelOnOff finally ");
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d("DeviceStatus", "roomPanelOnOff onFailure " + error);
                updateDeviceOfflineMode(deviceVO);
                //Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void roomPanelOnOff(final PanelVO panelVO, final RoomVO roomVO, String roomId, String panelId, int panel_status, int type) {

        final JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

            obj.put("room_id", roomId);
            obj.put("panel_id", panelId);
            obj.put("device_status", panel_status);
            obj.put("operationtype", type);
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket != null && !mSocket.connected()) {

            callPanelOnOffApi(obj);

        } else {

            Log.d(TAG, "roomPanelOnOff");

            //  if(panelVO!=null){

            mSocket.emit("changeRoomPanelMoodStatusAck", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
                @Override
                public void call(Object... args) {

                    if (args != null) {

                        if (args[0].toString().equalsIgnoreCase("No Ack")) {

                            Log.d("ACK_SOCKET_2", "Panel AckWithTimeOut : " + args[0].toString());

                            //  callPanelOnOffApi(panelVO,obj);

                            if (panelVO != null) {
                                updatePanelDeviceOfflineMode(panelVO);
                                Log.d("ACK_SOCKET_2", "update Panel Device OffLine");
                            } else if (roomVO != null) {
                                updateRoomOfflineMode(roomVO);
                                Log.d("ACK_SOCKET_2", "update Room Device OffLine");
                            }


                        } else if (args[0].toString().equalsIgnoreCase("true")) {
                            cancelTimer();
                            Log.d("ACK_SOCKET_2", "Panel AckWithTimeOut : " + args[0].toString());
                        }
                    }
                }
            });


            // mSocket.emit("changeRoomPanelMoodStatus", obj);

        }


        //  sectionedExpandableLayoutHelper.notifyDataSetChanged();

    }

    private void callPanelOnOffApi(JSONObject obj) {

        String url = ChatApplication.url + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW;

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "roomPanelOnOff onSuccess " + result.toString());
                try {
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code == 200) {
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "roomPanelOnOff finally ");
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "roomPanelOnOff onFailure " + error);
                //Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                //updatePanelDeviceOfflineMode(panelVO);
            }
        }).execute();

    }

    // Room buttons click
    @Override
    public void itemClicked(final RoomVO roomVO, String action) {
        //   Log.d("itemClicked", action + " itemClicked itemClicked RoomVO " + roomVO.getRoomName() + " ");
        closeFABMenu();
        if (action.equalsIgnoreCase("expandclick")) {

        } else if (action.equalsIgnoreCase("onoffclick")) {
            //on off room
            if (action.equalsIgnoreCase("onOffclick")) {

                roomPanelOnOff(null, roomVO, roomVO.getRoomId(), "", roomVO.getOld_room_status(), 1);
                //    Log.d("", action + " itemClicked itemClicked RoomVO ===/*/--*/ " + roomVO.getRoomName());
                //  roomVO.setRoom_status(roomVO.getRoom_status()==0?1:0);

                //int index = roomList.indexOf(roomVO);
                //Log.d("",action + " itemClicked itemClicked RoomVO " + index );
                //if(index!=-1){
                //roomList.get(index).setRoom_status(roomVO.getRoom_status());
                //}
            }
        } else if (action.equalsIgnoreCase("editclick_false")) {
            // Log.d("cameraopen","cameraopen editclick_false : " + cameraList.size());
            //edit
            Intent intent = new Intent(getActivity(), RoomEditActivity_v2.class);
            intent.putExtra("room", roomVO);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("cameraopen")) {
            //   Log.d("cameraopen","cameraopen : " + cameraList.size());
            //open camera
            Intent intent = new Intent(getActivity(), CameraPlayBack.class);
            if (roomVO != null) {
                intent.putExtra("room", roomVO);
            } else {
                intent.putExtra("room", new RoomVO());
            }
            intent.putExtra("cameraList", cameraList);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("editclick_true")) {

            //   Log.d("cameraopen","cameraopen editclick_true : " + cameraList.size());

            Intent intent = new Intent(getActivity(), CameraEdit.class);
            intent.putExtra("room", roomVO);
            intent.putExtra("cameraList", cameraList);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("deleteRoom")) {
            //   Log.d("DeleteAPI","action : " + action);

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteRoom(roomVO);
                }

                @Override
                public void onConfirmDialogNoClick() {
//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
                }
            });
            newFragment.show(getActivity().getFragmentManager(), "dialog");

        } else if (action.equalsIgnoreCase("icnLog")) {
            Intent intent = new Intent(getActivity(),DeviceLogActivity.class);
            intent.putExtra("ROOM_ID",roomVO.getRoomId());
            intent.putExtra("activity_type",roomVO.getType());
            intent.putExtra("isCheckActivity","room");
            intent.putExtra("isRoomName",""+roomVO.getRoomName());
            startActivity(intent);
//            Intent intent = new Intent(getActivity(), DeviceLogRoomActivity.class);
//            intent.putExtra("ROOM_ID", roomVO.getRoomId());
//            intent.putExtra("IS_SENSOR", "" + false);
//            intent.putExtra("room_name", "" + roomVO.getRoomName());
//            intent.putExtra("isNotification", "roomLogs");
//            startActivity(intent);

        } else if (action.equalsIgnoreCase("icnSch")) {
            Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
            intent.putExtra("moodId3", roomVO.getRoomId());
            intent.putExtra("roomId", roomVO.getRoomId());
            intent.putExtra("roomName", roomVO.getRoomName());
            intent.putExtra("selection", 1);
            intent.putExtra("isMoodAdapter", true);
            intent.putExtra("isActivityType", "1");
            startActivity(intent);
        } else if (action.equalsIgnoreCase("icnSensorLog")) {
//            Intent intent = new Intent(getActivity(),DeviceLogActivity.class);
//            intent.putExtra("ROOM_ID",roomVO.getRoomId());
//            intent.putExtra("IS_SENSOR",true);
//            intent.putExtra("isCheckActivity","room");
//            intent.putExtra("isRoomName",""+roomVO.getRoomName());
//            startActivity(intent);
            Intent intent = new Intent(getActivity(), DeviceLogRoomActivity.class);
            intent.putExtra("ROOM_ID", roomVO.getRoomId());
            intent.putExtra("IS_SENSOR", "" + true);
            intent.putExtra("room_name", "" + roomVO.getRoomName());
            intent.putExtra("isNotification", "roomSensorUnreadLogs");
            startActivity(intent);
        } else if (action.equalsIgnoreCase("cameraDevice")) {

            Intent intent = new Intent(getActivity(), CameraDeviceLogActivity.class);
            // intent.putExtra("getCameraList",roomList.get(roomList.size()-1).getPanelList().get(0).getCameraList());
            startActivity(intent);


        } else if (action.equalsIgnoreCase("cameraNotification")) {
            Intent intent = new Intent(getActivity(), CameraNotificationActivity.class);
            //   intent.putExtra("getCameraList",roomList.get(roomList.size()-1).getPanelList().get(0).getCameraList());
            startActivity(intent);
        }

        // roomOnOff(roomVO);
        // roomOnOff(roomVO);
    }

    /**
     * Delete Room
     *
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO) {


        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject object = new JSONObject();
        try {
            object.put("room_id", roomVO.getRoomId());
            object.put("room_name", roomVO.getRoomName());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomObj", "ob : " + object.toString());

        //  ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);

        String url = webUrl + Constants.DELETE_ROOM;


        new GetJsonTask(getActivity(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getDeviceList(8);
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }


    // Panel buttons click
    @Override
    public void itemClicked(PanelVO panelVO, String action) {
        closeFABMenu();
        if (action.equalsIgnoreCase("onOffclick")) {
            roomPanelOnOff(panelVO, null, "", panelVO.getPanelId(), panelVO.getOldStatus(), 2);
        }
    }

    /**
     * @param item
     * @param action
     */
    @Override
    public void itemClicked(final DeviceVO item, String action, int position) {

        closeFABMenu();
        if (action.equalsIgnoreCase("itemclick")) {
            deviceOnOff(item, position);
            //  item.setDeviceStatus(item.getDeviceStatus()==0?1:0);
        } else if (action.equalsIgnoreCase("scheduleclick")) {
            //   Log.d("", action + " itemClicked itemClicked DeviceVO " + item.getDeviceName());
            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
            intent.putExtra("item", item);
            intent.putExtra("schedule", true);
            startActivity(intent);


        }

        if (action.equalsIgnoreCase("longclick")) {
            int getDeviceSpecificValue = 0;
            if (!TextUtils.isEmpty(item.getDeviceSpecificValue())) {
                getDeviceSpecificValue = Integer.parseInt(item.getDeviceSpecificValue());
            }
            FanDialog fanDialog = new FanDialog(getActivity(), item.getRoomDeviceId(), getDeviceSpecificValue, new ICallback() {
                @Override
                public void onSuccess(String str) {
                    if (str.contains("yes")) {
                        //String array[]=str.split("-");
                        //Integer.parseInt(array[1]);
                        //item.setde
                    }
                }
            });
            fanDialog.show();
        }

    }

    public static DeviceVO tmpDeviceV0;
    public static int tmpPosition = -1;

    /**
     * Temperature Device Click listener handle
     *
     * @param item
     * @param action
     * @param isTemp
     */
    @Override
    public void itemClicked(DeviceVO item, String action, boolean isTemp, int position) {

        if (action.equalsIgnoreCase("isSensorClick") && isTemp) { //start sensor details activity

            tmpDeviceV0 = item;
            tmpPosition = position;

            if (item.getSensor_type().equalsIgnoreCase("temp")) {

                Intent intent = new Intent(getActivity(), TempSensorInfoActivity.class);
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getModuleId());
                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("door")) {

                Intent intent = new Intent(getActivity(), DoorSensorInfoActivity.class);
                intent.putExtra("door_sensor_id", item.getSensor_id());
                intent.putExtra("door_room_name", item.getRoomName());
                intent.putExtra("door_room_id", item.getRoomId());
                intent.putExtra("door_unread_count", item.getIs_unread());
                intent.putExtra("door_module_id", item.getModuleId());
                startActivity(intent);
            }
        } else if (action.equalsIgnoreCase("isIRSensorClick")) {

            //on-off remote
            sendRemoteCommand(item);


        } else if (action.equalsIgnoreCase("isIRSensorLongClick")) {


            Intent intent = new Intent(getActivity(), IRBlasterRemote.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("REMOTE_IS_ACTIVE", item.getIsActive());
            bundle.putSerializable("REMOTE_ID", item.getSensor_id());
            bundle.putSerializable("IR_BLASTER_ID", item.getSensor_id());
            // intent.putExtra("IR_BLASTER_ID",item.getIr_blaster_id());
            intent.putExtra("IR_BLASTER_ID", item.getSensor_id());
            intent.putExtra("ROOM_DEVICE_ID", item.getRoomDeviceId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void sendRemoteCommand(DeviceVO item) {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        SendRemoteCommandReq sendRemoteCommandReq = new SendRemoteCommandReq();
        sendRemoteCommandReq.setRemoteid(item.getSensor_id());


        // sendRemoteCommandReq.setCodesetid(String.valueOf(irBlasterCurrentStatusList.getCodesetId()));
        //   sendRemoteCommandReq.setIrblasterid(mIrBlasterId);
        //   sendRemoteCommandReq.setIrblasterModuleid(mIrBlasterModuleId);


        sendRemoteCommandReq.setPower(item.getRemote_status().equalsIgnoreCase("OFF") ? "ON" : "OFF");
        sendRemoteCommandReq.setSpeed(item.getSpeed());
        sendRemoteCommandReq.setTemperature(item.getTemprature());
        sendRemoteCommandReq.setRoomDeviceId(item.getRoomDeviceId());
        sendRemoteCommandReq.setPhoneId(APIConst.PHONE_ID_VALUE);
        sendRemoteCommandReq.setPhoneType(APIConst.PHONE_TYPE_VALUE);

        Gson gson = new Gson();
        String mRemoteCommandReq = gson.toJson(sendRemoteCommandReq);

        com.spike.bot.core.Log.d("sendRemoteCommand", "" + mRemoteCommandReq);

        String url = ChatApplication.url + Constants.SEND_REMOTE_COMMAND;
        new GetJsonTask(getContext(), url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                com.spike.bot.core.Log.d("SendRemote", "onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        //update remote UI
                        ChatApplication.isMoodFragmentNeedResume = true;
                        SendRemoteCommandRes tmpIrBlasterCurrentStatusList = Common.jsonToPojo(result.toString(),
                                SendRemoteCommandRes.class);
                        // getDeviceList();


                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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


    /**
     * @param item
     * @param action
     */

    @Override
    public void itemClicked(CameraVO item, String action) {
        String url = item.getCamera_ip();
        url = url + item.getCamera_videopath();
        String camera_name = item.getCamera_name();

        Log.d("isCloudConnect", "isCloudConnect : " + Main2Activity.isCloudConnected + " url : " + url + " : item url >> " + item.getCamera_url());

        //TODO code here for cloud connected or not...
        // rtmp://LOCAL_IP/live/livestream1528897402049_SJftJoAe7
        if (Main2Activity.isCloudConnected) {
            url = Constants.CAMERA_DEEP + ":" + item.getCamera_vpn_port() + "" + item.getCamera_url();
        } else {
            //String tmpurl = ChatApplication.url + "/"+item.getCamera_url();
            String tmpurl = ChatApplication.url + "" + item.getCamera_url();
            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
        }

        Log.e("cameraURL", "live url : " + url);
        //start camera rtsp player
        Intent intent = new Intent(getActivity(), CameraPlayer.class);
        intent.putExtra("videoUrl", url);
        intent.putExtra("name", camera_name);
        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
        startActivity(intent);
    }
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStop() {
        super.onStop();
    }

    String webUrl = "";
    boolean isCloudConnected = false;
    public static boolean isResumeConnect = false;
    //startTimer();
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {

        public void onTick(long millisUntilFinished) {
            // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            //here you can have your logic to set text to edittext
            Log.d("", "getconfigureData configureGatewayDevice countDownTimer onTick ");
        }

        public void onFinish() {
            Log.d("", "getconfigureData configureGatewayDevice countDownTimer onFinish ");
            /*- SOCKET Response:
            - 'configureGatewayDevice'
                    - parameter: module_id, device_id
                    - Open Popup and Fill below data: module_id, device_id*/
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!", Toast.LENGTH_SHORT).show();
            //showAddSensorDialog("212121");
        }

    };

    public void startTimer() {
        //  timerObj = new Timer();
        try {
            countDownTimer.start();
        } catch (Exception e) {
            Log.d("", "TimerTask configureGatewayDevice Exception " + e.getMessage());
        }
    }

    /*dialog enter key camera*/
    public void addKeyCamera() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_camera_key);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCameraKey(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        //ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

    }


    /*AddRoomDialog addRoomDialog = new AddRoomDialog(getActivity());
                        addRoomDialog.show();*/
    boolean addRoom = false;

    /**
     * Show custom room dialog
     */
    public void addCustomRoom() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomRoom(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        //ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

    }

    /*
     * camera key check is valid or not..
     * */

    private void saveCameraKey(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter key name");
            // Toast.makeText(getContext(),"Enter Room name",Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("key", roomName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

        String url = webUrl + Constants.validatecamerakey;

        new GetJsonTask(getActivity(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, "1");
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        addCamera();
                    } else if (code == 301) {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /**
     * Call api for save custom room
     *
     * @param roomName mRoomName
     * @param dialog   mDialog instance
     */
    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            // Toast.makeText(getContext(),"Enter Room name",Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("room_name", roomName.getText().toString());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomName", "ob : " + object.toString());
        ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

        String url = webUrl + Constants.ADD_CUSTOME_ROOM;

        new GetJsonTask(getActivity(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getDeviceList(9);
                    } else if (code == 301) {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    public void getconfigureData() {
        Log.d("configGatewayDoorSensor", "getconfigureData configureGatewayDevice");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);
        startTimer();
        addRoom = true;
        String url = webUrl + Constants.CONFIGURE_DOOR_SENSOR_REQUEST;
        new GetJsonTask(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d("configGatewayDoorSensor", "getconfigureData onSuccess " + result.toString());
                try {
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d("configGatewayDoorSensor", "getconfigureData onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /**
     *
     */
    public boolean addTempSensor = false;

    private void getTempConfigData() {
        Log.d("configTempSensor", "configTempSensor");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);
        startTimer();
        addTempSensor = true;
        String url = webUrl + Constants.CONFIGURE_TEMP_SENSOR_REQUEST;
        new GetJsonTask(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d("configTempSensor", "getconfigureData onSuccess " + result.toString());
                try {
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d("configTempSensor", "getconfigureData onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    //get config IR Blaster
    public boolean addIRBlasterSensor = false;

    private void getIRBlasterConfigData() {
        Log.d("IRBlaster", "configIRBlaster");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(getActivity(), "Searching IR Blaster attached ", false);
        startTimer();
        addIRBlasterSensor = true;
        String url = webUrl + Constants.CONFIGURE_IR_BLASTER_REQUEST;
        new GetJsonTask(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d("IRBlaster", "configIRBlaster onSuccess " + result.toString());
                try {
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d("IRBlaster", "configIRBlaster onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    private void showProgress() {
        if (mMessagesView != null) {
            mMessagesView.setVisibility(View.GONE);
        }
        linear_progress.setVisibility(View.VISIBLE);
        txt_connection.setVisibility(View.GONE);
    }

    private void hideProgress() {
        if (mMessagesView != null) {
            mMessagesView.setVisibility(View.VISIBLE);
        }
        linear_progress.setVisibility(View.GONE);
        txt_connection.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String name);

        void onLogout(boolean isLogout);
    }

    ArrayList<CameraVO> cameraList = new ArrayList<CameraVO>();
    public static ArrayList<PiDetailsModel> piDetailsModelArrayList = new ArrayList<PiDetailsModel>();

    private int SIGN_IP_REQUEST_CODE = 204;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("SignUpCode", "MainFragment Code : " + data);
            getDeviceList(10);
        }
    }

    /**
     * show alert dialog if password has beed changed
     */
    AlertDialog.Builder alertDialog = null;

    private void showLogoutAlert() {

        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(getContext());
        }
        alertDialog.setMessage("Password has been changed!")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (responseErrorCode != null) {
                            responseErrorCode.onLogout();
                        }
                    }
                }).show();
    }

    public static int SING_UP_REQUEST = 500;

    public static void saveCurrentId(Context context,String userId){
        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(context,Common.USER_JSON);
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> userList = gson.fromJson(jsonText, type);

        for(int i=0; i<userList.size(); i++){
            if(userList.get(i).getUser_id().equals(userId)){
                userList.get(i).setIsActive(true);
                mCallback.onArticleSelected("" + userList.get(i).getFirstname() + " " + userList.get(i).getLastname());
            }else {
                userList.get(i).setIsActive(false);
            }
        }
        if(userList.size()>0){
            String jsonCurProduct = gson.toJson(userList);
            Common.savePrefValue(context,Common.USER_JSON,jsonCurProduct);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /// all webservice call below.
    public  void getDeviceList(final int checkmessgae) {
        //showProgress();

        Log.d("CallAPIMainFragment", "getDeviceList getDeviceList webUrl " + checkmessgae);
        if (getActivity() == null) {
            return;
        }

        if (showDialog == 1 ||checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 9 || checkmessgae == 10) {
            ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        }

        roomList.clear();
        // mMessagesView.removeAllViews();
        if(mMessagesView==null){
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        }
        mMessagesView.setClickable(false);
//        if (mFab == null) {
//            mFab = (FloatingActionButton) view.findViewById(R.id.fab);
//        }
//        mFab.setClickable(false);
//        ((Main2Activity)getActivity()).toolbarTitle.setClickable(false);
//        ((Main2Activity)getActivity()).toolbarImage.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
        if (!token_id.equalsIgnoreCase("")) {
            url = url + "/" + token_id;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //responseErrorCode.onProgress();
        Log.d("CouldFoundIP", "call api");
        new GetJsonTask2(activity, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                showDialog=0;
                ActivityHelper.dismissProgressDialog();
                if(ChatApplication.isPushFound){
                    getBadgeClear(getActivity());
                }

                mMessagesView.setClickable(true);
//                mFab.setClickable(true);
//                ((Main2Activity)getActivity()).toolbarTitle.setClickable(true);
//                ((Main2Activity)getActivity()).toolbarImage.setClickable(true);

                responseErrorCode.onSuccess();

                //connect socket
                startSocketConnection();

                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
                Log.d("RefreshList", "call api success");

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        roomList = new ArrayList<>();

                        JSONObject dataObject = result.getJSONObject("data");

                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {

                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            // mCallback.onArticleSelected("Spike Bot");

                            Intent intent = new Intent(getActivity(), SignUp.class);
                            startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
                            return;
                        }


                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        ChatApplication.currentuserId = userId;
                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
                        }



                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getContext(), Common.USER_PIDETAIL);
                        List<PiDetailsModel> piUserList = new ArrayList<PiDetailsModel>();
                        if (!TextUtils.isEmpty(jsonText)) {
                            Type type = new TypeToken<List<PiDetailsModel>>() {
                            }.getType();
                            piUserList = gson.fromJson(jsonText, type);
                        }

                        String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
                        Log.d("UserPass", "Json String  : " + jsonTextTemp);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonTextTemp) && !jsonTextTemp.equals("null")) {
                            Type type = new TypeToken<List<User>>() {
                            }.getType();
                            userList = gson.fromJson(jsonTextTemp, type);
                        }

                        if(userList!=null && userList.size()>0){
                            boolean flagIsLogin = false;
                            for (User user : userList) {
                                if (user.getUser_id().equalsIgnoreCase(userId)) {
                                    flagIsLogin = true;
                                    saveCurrentId(getActivity(),userId);
                                    mCallback.onArticleSelected("" + userFirstName + " " + userLastName);
                                    break;
                                }
                            }

                            if (flagIsLogin == false) {
                                if (checkLoginId(userId, checkmessgae)) {
                                    saveCurrentId(getActivity(),userId);
                                    mCallback.onArticleSelected("" + userFirstName + " " + userLastName);
                                    Log.d("System out", "pie details is isSignUp ");
                                }else if(flagisCould){
                                    flagisCould=false;
                                    getDeviceCould(12);
                                    return;
                                } else {
//                                    showDialog=1;
//                                    loginPIEvent.showLogin();
//                                    return;
                                }

                            }
                        }else {
//                            showDialog=1;
//                            loginPIEvent.showLogin();
                        }

                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
                        if (Main2Activity.isCloudConnected && !TextUtils.isEmpty(userPassword)) {
                            Log.d("UserPass", "ID : " + userId);
                            Log.d("UserPass", "Pass : " + userPassword);
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            Log.d("UserPass", "Json String  : " + jsonTextTemp1);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {
                                }.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type);
                            }

                            for (User user : userList1) {
                              /*  if(user.getUser_id().equalsIgnoreCase(userId)
                                        && !userPassword.equalsIgnoreCase(user.getPassword()) && user.isActive()){
                                    Log.d("UserPass","User Found   : " +user.getFirstname()  + " User pass : " + user.getPassword());
                                    showLogoutAlert();
                                }*/

                                if (user.isActive()) {
                                    String USER_ID = Common.getPrefValue(getContext(), Constants.USER_ID);
                                    String USER_PASSWORD = Common.getPrefValue(getContext(), Constants.USER_PASSWORD);

                                    if (!USER_PASSWORD.equalsIgnoreCase(userPassword)) {
                                        Log.d("UserPass", "FOUND Pass  :" + USER_PASSWORD + " ID : " + USER_ID);
                                        showLogoutAlert();
                                    }

                                }
                            }
                        }
                        //End user pass checking


                        try {
                            if (userObject.has("is_logout")) {
                                int isLogout = userObject.getInt("is_logout");
                                if (isLogout == 1 && Main2Activity.isCloudConnected) {
                                    mCallback.onLogout(true);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        Common.savePrefValue(ChatApplication.getInstance(), "first_name", userFirstName);
                        Common.savePrefValue(ChatApplication.getInstance(), "last_name", userLastName);
                        Common.savePrefValue(getContext(), Constants.USER_PASSWORD, userPassword);

                        Log.d("userPassword", "textFound : " + userFirstName);

                        // getActivity().setTitle("" + userFirstName + " " + userLastName);
                        /**
                         * <h1>for set ToolBar title in MainActivity2</h1>
                         * {@link Main2Activity#toolbarTitle}
                         * @see Main2Activity#toolbar
                         */
                        mCallback.onArticleSelected("" + userFirstName + " " + userLastName);

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);

//                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
//                    exp_list.setAdapter(listAdapter);


                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {
                            Log.d("cameradeviceListSize", "size : " + cameraArray.length());
                            cameraList = JsonHelper.parseCameraArray(cameraArray);

                            RoomVO section = new RoomVO();
                            section.setRoomName("Camera");
                            section.setRoomId("Camera");

                            ArrayList<PanelVO> panelList = new ArrayList<>();
                            PanelVO panel = new PanelVO();
                            panel.setPanelName("Devices");
                            panel.setType("camera");
                            panel.setCameraList(cameraList);

                            panelList.add(panel);

                            section.setPanelList(panelList);
                            section.setIs_unread(cameraList.get(0).getIs_unread());

                            roomList.add(section);

                            /*camera device counting*/
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomId().equalsIgnoreCase("Camera")) {
                                    roomList.get(i).setDevice_count("" + roomList.get(i).getPanelList().get(0).getCameraList().size());
                                }
                            }

                            //comment if roomList empty could not display cameraList for remove this conditions
                            // sectionedExpandableLayoutHelper.addCameraList(JsonHelper.parseCameraArray(cameraArray));
                            sectionedExpandableLayoutHelper.addSectionList(roomList);
                        }
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        //JSONArray userList = dataObject.getJSONArray("userList");
                        Main2Activity.changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }
                        Log.d("RefreshList", "call api");


                        /*JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        CameraAdapter cameraAdapter = new CameraAdapter(getActivity(), cameraArray, webUrl);
                        camera_list.setAdapter(cameraAdapter);*/

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (roomList != null) {
                        CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
                    }
                    mMessagesView.setClickable(true);
                    mFab.setClickable(true);
                    swipeRefreshLayout.setRefreshing(false);
//                    ((Main2Activity)getActivity()).toolbarTitle.setClickable(true);
//                    ((Main2Activity)getActivity()).toolbarImage.setClickable(true);
                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                //set custom homer controller not found error message on dashboard
                if (reCode == 503 || reCode == 404) {
                    responseErrorCode.onErrorCode(reCode);
                }
                Log.d(TAG, "reCode getDeviceList onFailure " + reCode);
                //  Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();

                //for clear adapter and view after resume the screen
                sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
                sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);
                sectionedExpandableLayoutHelper.setClickable(true);
                sectionedExpandableLayoutHelper.notifyDataSetChanged();

                if (roomList.size() == 0) {
                    mMessagesView.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                } else {
                    mMessagesView.setVisibility(View.VISIBLE);
                    txt_empty_schedule.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /// all webservice call below.
    public void getDeviceCould(final int checkmessgae) {
        //showProgress();

        Log.d("CallAPIMainFragment", "getDeviceList getDeviceList webUrl " + checkmessgae);
        if (getActivity() == null) {
            return;
        }

        if (showDialog==1||checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 9 || checkmessgae == 10) {
            ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        }

        roomList.clear();
        // mMessagesView.removeAllViews();
        mMessagesView.setClickable(false);
        if (mFab == null) {
            mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        }
//        mFab.setClickable(false);
//        ((Main2Activity)getActivity()).toolbarTitle.setClickable(false);
//        ((Main2Activity)getActivity()).toolbarImage.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
        if (!token_id.equalsIgnoreCase("")) {
            url = url + "/" + token_id;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //responseErrorCode.onProgress();
        Log.d("CouldFoundIP", "call api");
        new GetJsonTask2(activity, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                showDialog=0;
                isRefredCheck = false;
                mMessagesView.setClickable(true);
                mFab.setClickable(true);
                mFab.setClickable(true);
                ActivityHelper.dismissProgressDialog();
                ((Main2Activity)getActivity()).toolbarTitle.setClickable(true);
                ((Main2Activity)getActivity()).toolbarImage.setClickable(true);

                responseErrorCode.onSuccess();

                //connect socket
                startSocketConnection();

                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
                Log.d("RefreshList", "call api success");

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        roomList = new ArrayList<>();

                        JSONObject dataObject = result.getJSONObject("data");

                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {

                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            // mCallback.onArticleSelected("Spike Bot");

                            Intent intent = new Intent(getActivity(), SignUp.class);
                            startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
                            return;
                        }


                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String ip=userObject.optString("ip");
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        ChatApplication.currentuserId = userId;
                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
                        }

                        Common.savePrefValue(getActivity(),Constants.USER_PASSWORD,userPassword);
                        Common.savePrefValue(getActivity(),Constants.PREF_IP,ip);

                       // User user = new User(userId,userFirstName,userLastName,ip,false,userPassword);

                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getActivity(),Common.USER_JSON);
                        Type type = new TypeToken<List<User>>() {}.getType();
                        List<User> userList = gson.fromJson(jsonText, type);
                        userList.get(0).setIsActive(true);
                        ((Main2Activity)getActivity()).isCloudConnected=true;
                        ((Main2Activity)getActivity()).invalidateToolbarCloudImage();
                        webUrl = userList.get(0).getCloudIP();
                        ChatApplication.url = webUrl;
                        String jsonCurProduct = gson.toJson(userList);
                        mCallback.onArticleSelected("" + userFirstName + " " + userLastName);
                        Common.savePrefValue(getActivity(),Common.USER_JSON,jsonCurProduct);

                        saveCurrentId(getActivity(),userId);
//                        JSONArray piDetailsArray = dataObject.optJSONArray("piDetails");
//                        if (piDetailsArray != null) {
//                            String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
//                            Log.d("UserPass", "Json String  : " + jsonTextTemp);
//                            List<User> userList = new ArrayList<User>();
//                            if (!TextUtils.isEmpty(jsonTextTemp) && !jsonTextTemp.equals("null")) {
//                                Type type = new TypeToken<List<User>>() {
//                                }.getType();
//                                userList = gson.fromJson(jsonTextTemp, type);
//                            }
//
//                            boolean flagIsLogin = false;
//                            for (User user : userList) {
//                                if (user.getUser_id().equalsIgnoreCase(userId)) {
//                                    flagIsLogin = true;
//                                    break;
//                                }
//                            }
//
//                            for (int j = 0; j < piDetailsArray.length(); j++) {
//                                try {
//                                    JSONObject deviceObj = piDetailsArray.getJSONObject(j);
//
//                                    String wifi_user_name = deviceObj.optString("wifi_user_name");
//                                    String wifi_user_password = deviceObj.optString("wifi_user_password");
//                                    String home_controller_device_id = deviceObj.optString("mac_address");
//
//                                    PiDetailsModel piDetailsModel = new PiDetailsModel();
//                                    piDetailsModel.setWifi_user_name(wifi_user_name);
//                                    piDetailsModel.setWifi_user_password(wifi_user_password);
//                                    piDetailsModel.setHome_controller_device_id(home_controller_device_id);
//
//                                    piDetailsModelArrayList.add(piDetailsModel);
//
//                                    Log.d("System out", "pie details is ");
//                                    if (piUserList.size() == 0) {
////                                        String jsonCurProduct = gson.toJson(piDetailsModelArrayList);
////                                        Common.savePrefValue(getActivity(), Common.USER_PIDETAIL, jsonCurProduct);
////                                       if(!ChatApplication.isSignUp){
//
//                                        if (flagIsLogin == false) {
//                                            if (checkLoginId(userId, checkmessgae)) {
//                                                Log.d("System out", "pie details is isSignUp ");
//                                            }else if(flagisCould){
//                                                flagisCould=false;
//                                                return;
//                                            } else {
//                                                loginPIEvent.showLogin();
//                                                return;
//                                            }
//
//                                        }
//
////                                       }
//                                    } else {
//                                        for (int i = 0; i < piUserList.size(); i++) {
//                                            if (!piUserList.get(i).getHome_controller_device_id().equalsIgnoreCase(home_controller_device_id)
//                                                    && piUserList.get(i).getWifi_user_password().equalsIgnoreCase(wifi_user_password)) {
////                                                String jsonCurProduct = gson.toJson(piDetailsModelArrayList);
////                                                Common.savePrefValue(getActivity(), Common.USER_PIDETAIL, jsonCurProduct);
//                                                if (flagIsLogin == false) {
//                                                    Log.d("System out", "pie details is isSignUp equal ");
//                                                    if (checkLoginId(userId, checkmessgae)) {
//
//                                                    } else {
//                                                        loginPIEvent.showLogin();
//                                                        return;
//                                                    }
//
//                                                }
//
//                                            }
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }


                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
//                        if (Main2Activity.isCloudConnected && !TextUtils.isEmpty(userPassword)) {
//                            Log.d("UserPass", "ID : " + userId);
//                            Log.d("UserPass", "Pass : " + userPassword);
//                            String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
//                            Log.d("UserPass", "Json String  : " + jsonTextTemp);
//                            List<User> userList = new ArrayList<User>();
//                            if (!TextUtils.isEmpty(jsonTextTemp)) {
//                                Type type = new TypeToken<List<User>>() {
//                                }.getType();
//                                userList = gson.fromJson(jsonTextTemp, type);
//                            }
//
//                            for (User user : userList) {
//                              /*  if(user.getUser_id().equalsIgnoreCase(userId)
//                                        && !userPassword.equalsIgnoreCase(user.getPassword()) && user.isActive()){
//                                    Log.d("UserPass","User Found   : " +user.getFirstname()  + " User pass : " + user.getPassword());
//                                    showLogoutAlert();
//                                }*/
//
//                                if (user.isActive()) {
//                                    String USER_ID = Common.getPrefValue(getContext(), Constants.USER_ID);
//                                    String USER_PASSWORD = Common.getPrefValue(getContext(), Constants.USER_PASSWORD);
//
//                                    if (!USER_PASSWORD.equalsIgnoreCase(userPassword)) {
//                                        Log.d("UserPass", "FOUND Pass  :" + USER_PASSWORD + " ID : " + USER_ID);
//                                        showLogoutAlert();
//                                    }
//
//                                }
//                            }
//                        }
                        //End user pass checking


                        try {
                            if (userObject.has("is_logout")) {
                                int isLogout = userObject.getInt("is_logout");
                                if (isLogout == 1 && Main2Activity.isCloudConnected) {
                                    mCallback.onLogout(true);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        Common.savePrefValue(ChatApplication.getInstance(), "first_name", userFirstName);
                        Common.savePrefValue(ChatApplication.getInstance(), "last_name", userLastName);
                        Common.savePrefValue(getContext(), Constants.USER_PASSWORD, userPassword);

                        Log.d("userPassword", "textFound : " + userFirstName);

                        // getActivity().setTitle("" + userFirstName + " " + userLastName);
                        /**
                         * <h1>for set ToolBar title in MainActivity2</h1>
                         * {@link Main2Activity#toolbarTitle}
                         * @see Main2Activity#toolbar
                         */
                        mCallback.onArticleSelected("" + userFirstName + " " + userLastName);

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);

//                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
//                    exp_list.setAdapter(listAdapter);


                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {
                            Log.d("cameradeviceListSize", "size : " + cameraArray.length());
                            cameraList = JsonHelper.parseCameraArray(cameraArray);

                            RoomVO section = new RoomVO();
                            section.setRoomName("Camera");
                            section.setRoomId("Camera");

                            ArrayList<PanelVO> panelList = new ArrayList<>();
                            PanelVO panel = new PanelVO();
                            panel.setPanelName("Devices");
                            panel.setType("camera");
                            panel.setCameraList(cameraList);

                            panelList.add(panel);

                            section.setPanelList(panelList);
                            section.setIs_unread(cameraList.get(0).getIs_unread());

                            roomList.add(section);

                            /*camera device counting*/
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomId().equalsIgnoreCase("Camera")) {
                                    roomList.get(i).setDevice_count("" + roomList.get(i).getPanelList().get(0).getCameraList().size());
                                }
                            }

                            //comment if roomList empty could not display cameraList for remove this conditions
                            // sectionedExpandableLayoutHelper.addCameraList(JsonHelper.parseCameraArray(cameraArray));
                            sectionedExpandableLayoutHelper.addSectionList(roomList);
                        }
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        //JSONArray userList = dataObject.getJSONArray("userList");
                        Main2Activity.changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }
                        Log.d("RefreshList", "call api");


                        /*JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        CameraAdapter cameraAdapter = new CameraAdapter(getActivity(), cameraArray, webUrl);
                        camera_list.setAdapter(cameraAdapter);*/

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (roomList != null) {
                        CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
                    }
                    mMessagesView.setClickable(true);
                    mFab.setClickable(true);
                    swipeRefreshLayout.setRefreshing(false);
//                    ((Main2Activity)getActivity()).toolbarTitle.setClickable(true);
//                    ((Main2Activity)getActivity()).toolbarImage.setClickable(true);
                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                //set custom homer controller not found error message on dashboard
                if (reCode == 503 || reCode == 404) {
                    responseErrorCode.onErrorCode(reCode);
                }
                Log.d(TAG, "reCode getDeviceList onFailure " + reCode);
                //  Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();

                //for clear adapter and view after resume the screen
                sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
                sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);
                sectionedExpandableLayoutHelper.setClickable(true);
                sectionedExpandableLayoutHelper.notifyDataSetChanged();

                if (roomList.size() == 0) {
                    mMessagesView.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                } else {
                    mMessagesView.setVisibility(View.VISIBLE);
                    txt_empty_schedule.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean checkLoginId(String userIdCheck, int checkmessgae) {
        boolean isFlag = false;
        String jsonText = Common.getPrefValue(getActivity(), Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonText, type);

            Log.d("System out", "user list is " + jsonText);
            if (userList != null) {
                if (userList.size() > 0) {
                    if (isRefredCheck) {

                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUser_id().equals(userIdCheck)) {
                                isFlag = true;
                                break;
                            }
                        }

                        if(!isFlag){
                            flagisCould=true;
                            webUrl = userList.get(0).getCloudIP();
                            ChatApplication.url = webUrl;
                            isCloudConnected = true;
                          //  invalidateToolbarCloudImage();
                            Main2Activity.isClick = true;
                       //     getDeviceCould(12);
                           // cloudClickListener.click(userList.get(0));
                            return false;
                        }
                    } else {
                        isFlag = true;
                    }
                    return isFlag;
                }
            }


        }
        return isFlag;
    }

    //getMoodList
    public static void getBadgeClear(final Context context) {
        String url = ChatApplication.url + Constants.updateBadgeCount;

        ChatApplication.logDisplay("url is "+url);
        new GetJsonTask(context, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.isPushFound=false;

                clearNotification(context);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {

            }
        }).execute();


    }

    public static void clearNotification(Context context) {
        // Clear all notification
        NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
