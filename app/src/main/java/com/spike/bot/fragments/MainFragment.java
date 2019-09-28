package com.spike.bot.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.GetJsonTaskMacAddress;
import com.kp.core.GetJsonTaskRemote;
import com.kp.core.GetJsonTaslLocal;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.ack.AckWithTimeOut;
import com.spike.bot.activity.AddUnassignedPanel;
import com.spike.bot.activity.CameraDeviceLogActivity;
import com.spike.bot.activity.CameraEdit;
import com.spike.bot.activity.CameraGridActivity;
import com.spike.bot.activity.CameraNotificationActivity;
import com.spike.bot.activity.CameraPlayBack;
import com.spike.bot.activity.Curtain.CurtainActivity;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.DeviceLogRoomActivity;
import com.spike.bot.activity.DoorSensorInfoActivity;
import com.spike.bot.activity.GasSensorActivity;
import com.spike.bot.activity.HeavyLoadDetailActivity;
import com.spike.bot.activity.MultiSensorActivity;
import com.spike.bot.activity.RepeaterActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.NotificationSetting;
import com.spike.bot.activity.ProfileActivity;
import com.spike.bot.activity.RoomEditActivity_v2;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.SensorDoorLogActivity;
import com.spike.bot.activity.SensorUnassignedActivity;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.activity.SmartDevice.BrandListActivity;
import com.spike.bot.activity.SmartRemoteActivity;
import com.spike.bot.activity.TTLock.GateWayListActivity;
import com.spike.bot.activity.TTLock.LockBrandActivity;
import com.spike.bot.activity.TempSensorInfoActivity;
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
import com.spike.bot.core.Log;
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
import com.spike.bot.model.GatewayObj;
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
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.spike.bot.core.Common.showToast;

/**
 * DashBoard fragment / Room
 * {@link Main2Activity}
 *
 * @see Fragment
 */

public class MainFragment extends Fragment implements View.OnClickListener, ItemClickListener, SectionedExpandableGridAdapter.CameraClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnSmoothScrollList, TempClickListener {

    public static int showDialog = 1;
    private Boolean isSocketConnected = true, flagisCould = false, flagHeavyload = false,isCheckFlow=false;
    public static Boolean isRefredCheck = true;
    private String userId = "0", token_id = "";
    private RecyclerView mMessagesView;
    public ProgressDialog progressDialog;
    private Socket mSocket;
    private ArrayList<RoomVO> roomList = new ArrayList<>();
    public SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;

    // auto service find
    public View view;
    public RelativeLayout relativeMainfragment;
    private LinearLayout linear_retry, linear_progress, linear_login;
    private Button button_retry, btn_login;
    EditText et_username, et_password;
    private LinearLayout txt_empty_schedule;
    public static Activity activity;
    private TextView txt_empty_text, tv_header, txt_connection;
    private ImageView empty_add_image, deepsImage;
    public CloudAdapter.CloudClickListener cloudClickListener;
    public Handler handlerHeavyLoad = null;
    public Runnable runnableHeavyLoad = null;
    public Thread threadHeavyLoad = null;
    public CountDownTimer countDownTimerSocket = null;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.

    public static OnHeadlineSelectedListener mCallback;
    RunServiceInterface runServiceInterface;
    ResponseErrorCode responseErrorCode;
    LoginPIEvent loginPIEvent;
    SocketListener socketListener;


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
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
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
        ChatApplication.isCallDeviceList = true;
        swipeRefreshLayout.setRefreshing(true);
//        if (Main2Activity.isCloudConnected) {
//            runServiceInterface.executeService();
//        } else {
            getDeviceList(1);
//        }

        sectionedExpandableLayoutHelper.setClickable(false);


    }

    public void RefreshAnotherFragment() {
        isRefredCheck = true;
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(true);
        }

//        if (Main2Activity.isCloudConnected) {
//            runServiceInterface.executeService();
//        } else {
            getDeviceList(12);
//        }

        sectionedExpandableLayoutHelper.setClickable(false);

    }

    private NestedScrollView main_scroll;
    private FloatingActionButton mFab;
    private CardView mFabMenuLayout;
    private TextView fab_menu1, fab_menu2, fab_menu3, fab_menu4, fab_menu5, fab_menu6, fab_menu7, fabZigbeeRemote, fabSmartDevice,fabLock,fabrepeater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //     Common.savePrefValue(getActivity(), Constants.USER_ID,"1552398595478_S1ibYErwV");

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

        relativeMainfragment = view.findViewById(R.id.relativeMainfragment);
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
//        register();
        mFabMenuLayout = (CardView) view.findViewById(R.id.fabLayout1);
        fab_menu1 = (TextView) view.findViewById(R.id.fab_menu1);
        fab_menu2 = (TextView) view.findViewById(R.id.fab_menu2);
        fab_menu3 = (TextView) view.findViewById(R.id.fab_menu3);
        fab_menu4 = (TextView) view.findViewById(R.id.fab_menu4);
        fab_menu5 = (TextView) view.findViewById(R.id.fab_menu5);
        fab_menu6 = (TextView) view.findViewById(R.id.fab_menu6);
        fab_menu7 = (TextView) view.findViewById(R.id.fab_menu7);
        fabSmartDevice = (TextView) view.findViewById(R.id.fabSmartDevice);
        fabZigbeeRemote = view.findViewById(R.id.fabZigbeeRemote);
        fabLock = view.findViewById(R.id.fabLock);
        fabrepeater = view.findViewById(R.id.fabrepeater);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);

        fabSmartDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                // Intent intent=new Intent(getActivity(), SearchSmartDeviceActivity.class);
                Intent intent = new Intent(getActivity(), BrandListActivity.class);
                startActivity(intent);

            }
        });

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

        fabZigbeeRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(getActivity(), SmartRemoteActivity.class));
            }
        });

        fabLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(getActivity(), LockBrandActivity.class));
            }
        });

        fabrepeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(getActivity(), RepeaterActivity.class));
//                startActivity(new Intent(getActivity(), GasSensorActivity.class));
            }
        });


        relativeMainfragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();

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
        // mFab.setVisibility(View.VISIBLE);
        handlerHeavyLoad = new Handler();
        empty_add_image.setVisibility(View.VISIBLE);
        txt_empty_text.setText("Add Room");

        setPrograbar();
        if (ChatApplication.isLocalFragmentResume) {
            ChatApplication.isLocalFragmentResume = false;
            getDeviceList(2);

        } else if (ChatApplication.isMainFragmentNeedResume) {
            ChatApplication.isMainFragmentNeedResume = false;
            onLoadFragment();
        }
    }

    private void setPrograbar() {
        if(getActivity()!=null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait");
//            progressDialog.setp
        }
    }

    @Override
    public void onClick(View view) {
        // ChatApplication.logDisplay( "onClick ");
        if (view.getId() == R.id.btn_login) {
            //loginCloud();
        }
    }

    /**
     * {@link SectionedExpandableGridAdapter#}
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
//        webUrl = ChatApplication.url;

        if (mSocket != null && mSocket.connected()) {
        } else {

            if(!webUrl.startsWith("http")){
                webUrl="http://"+webUrl;
            }
            mSocket = app.getSocket();

            if(mSocket!=null){
                ChatApplication.logDisplay("chat app is null "+mSocket.connected());
                if(mSocket.connected()==false){
                    mSocket=null;
                    ChatApplication.logDisplay("chat app is null");
                }
            }

            if(mSocket==null){
                ChatApplication.logDisplay("chat app is call "+ChatApplication.url);
                mSocket = app.openSocket(webUrl);
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.connect();
//                ChatApplication.url=webUrl;
//                ((Main2Activity)getActivity()).callSocket();
            }
            try {
                mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);  // ac on off
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
                mSocket.on("updateChildUser", updateChildUser);
//                mSocket.on("heavyLoadValue", heavyLoadValue);
//                mSocket.on("updateChildUser", updateChildUser);
//                mSocket.on("deleteChildUser", deleteChildUser);
                //   mSocket.on("configureIRBlaster",configureIRBlaster);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

//                mSocket.on(Socket.EVENT_CONNECT,onConnect);
//                mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
//                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        //  mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
//            mSocket.emit("socketconnection", "android == disconnecti " + mSocket.id());
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
            mSocket.off("updateChildUser", updateChildUser);

//            mSocket.off("deleteChildUser", deleteChildUser);
            //   mSocket.off("configureIRBlaster",configureIRBlaster);

        }
    }

    @Override
    public void onPause() {
        flagHeavyload=false;
        if(countDownTimerSocket!=null){
            countDownTimerSocket.cancel();
            countDownTimerSocket.onFinish();
            countDownTimerSocket=null;
        }
        if(!ChatApplication.url.startsWith("http")){
            ChatApplication.url="http://"+ChatApplication.url;
        }
//        handlerHeavyLoad.removeCallbacksAndMessages(null);
        super.onPause();
    }

    public void onLoadFragment() {

        if (activity != null) {
            Main2Activity activity = (Main2Activity) getActivity();
            if (activity != null) {
                isSocketConnected = activity.isSocketConnected;
            }
            isResumeConnect = activity.isResumeConnect;
            isCloudConnected = activity.isCloudConnected;
            if (socketListener != null) {
                socketListener.startSession();
            }
        }

        if (Common.isConnected() && isResumeConnect) {
            //hideAlertDialog();
            //runService();
            isResumeConnect = false;
            //openSocketandGetList();
            //  startSocketConnection();
            getDeviceList(3);
        } else if (Common.isConnected() && ChatApplication.isRefreshDashBoard) {
            ChatApplication.logDisplay("onResume isResumeConnect 22222  " + ChatApplication.isRefreshDashBoard);
            getDeviceList(4);
            //  startSocketConnection();
            ChatApplication.isRefreshDashBoard = true; //false

        } else if (!Common.isConnected()) {
            ChatApplication.logDisplay("onResume isResumeConnect 33333  ");
            //hideAlertDialog();
            // showAlertDialog();
        } else {
            ChatApplication.logDisplay("else Co..........");

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

//        if(!ChatApplication.url.startsWith("http")){
//            ChatApplication.url="http://"+ChatApplication.url;
//        }
        String url = ChatApplication.url + Constants.GET_ALL_UNASSIGNED_DEVICES;

        new GetJsonTask(getContext(), url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
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
    public static int SENSOR_REPEATAR = 10;

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
                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(), SensorUnassignedRes.class);

                if (sensorUnassignedRes.getCode() == 200) {

                    if (sensorUnassignedRes.getData() != null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0) {
                        Intent intent = new Intent(getActivity(), SensorUnassignedActivity.class);
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
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("camera_name", camera_name);
            obj.put("camera_ip", camera_ip);
            obj.put("video_path", video_path);
            obj.put("user_name", user_name);
            obj.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.ADD_CAMERA;

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("onSuccess " + result.toString());
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
                ChatApplication.logDisplay("onFailure " + error);
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
                        intent.putExtra("isCheckActivity", "AllType");
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
                    if (!isSocketConnected) {
                        //  hideAlertDialog();
                        //if(null!=mUsername)
                        //mSocket.emit("add user", mUsername);
                        //Toast.makeText(getActivity().getApplicationContext(), R.string.connect, Toast.LENGTH_SHORT).show();
                        isSocketConnected = true;
                    }
                    if(!ChatApplication.url.startsWith("http")){
                        ChatApplication.url="http://"+ChatApplication.url;
                    }
                    ChatApplication.logDisplay("chat app is connnect"+ChatApplication.url);
                    mSocket.emit("socketconnection", "android == startconnect  " + mSocket.id());

                    mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);  // ac on off
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
                    mSocket.on("updateChildUser", updateChildUser);
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
                    isSocketConnected = false;
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                    // openErrorDialog();

//                    ChatApplication app = ChatApplication.getInstance();
//                    webUrl = app.url;
//                    if (!webUrl.startsWith("http")) {
//                            webUrl = "http://" + webUrl;
//                        }
//                        mSocket = app.getSocket();
//                            ChatApplication.logDisplay("chat app is call11" + ChatApplication.url);
//                            mSocket = app.openSocket(webUrl);
//                            mSocket.on(Socket.EVENT_CONNECT, onConnect);
//                            mSocket.connect();

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
                    //Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect, Toast.LENGTH_SHORT).show();
                    //   openErrorDialog();
                }
            });
        }
    };
    ////////isSensorClick
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

                            //{"module_id":"SpikeBot10355128","device_id":"1563952394430_ANUROHD9-h","device_status":1,"is_locked":0}
                            JSONObject object = new JSONObject(args[0].toString());
                            String module_id = object.getString("module_id");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int is_locked = object.optInt("is_locked");

                            sectionedExpandableLayoutHelper.updateItem(module_id, device_id, device_status, is_locked);
                            //   sectionedExpandableLayoutHelper.notifyDataSetChanged();

                            ChatApplication.logDisplay("panel status reloadDeviceStatusApp " + object.toString());
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
                            JSONObject object = new JSONObject(args[0].toString());

                            ChatApplication.logDisplay("panel status panelStatus " + object.toString());
                            String panel_id = object.optString("panel_id");
                            String panelstatusValue = object.getString("panel_status");
//                            String user_id = object.optString("user_id");

                            ChatApplication.logDisplay("panel status panelStatus update " + object.toString());
                            sectionedExpandableLayoutHelper.updatePanel(panel_id, panelstatusValue, "");


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


                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String roomupdateValue = object.getString("room_status");

                            sectionedExpandableLayoutHelper.updateRoom(room_id, roomupdateValue);
                            ChatApplication.logDisplay("panel status roomStatus update " + object.toString());

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
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        //{"message":"Temperature Sensor already configured. Add from Unconfigured Sensor devices","temp_module_id":"","room_list":""}
                        JSONObject object = new JSONObject(args[0].toString());

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
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("door is "+object);
                            String room_order = object.optString("room_order");
                            String door_sensor_id = object.optString("door_sensor_id");
                            String door_sensor_status = object.optString("door_sensor_status");
                            String door_lock_status = object.optString("door_lock_status");

                            sectionedExpandableLayoutHelper.updateDoorStatus(room_order, door_sensor_id, door_sensor_status,door_lock_status);

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
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String sensor_type = object.getString("sensor_type");
                            String sensor_unread = object.getString("sensor_unread");
                            String module_id = object.getString("module_id");
                            String room_id = object.getString("room_id");
                            String room_unread = object.getString("room_unread");
                            String user_id = object.getString("user_id");

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(getActivity(), Constants.USER_ID))) {
                                sectionedExpandableLayoutHelper.updateBadgeCount(sensor_type, sensor_unread, module_id, room_id, room_unread);
                            }

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
                        try {

                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("temp is "+object);
                            String room_id = object.getString("room_id");
                            String room_order = object.getString("room_order");
                            String temp_sensor_id = object.getString("temp_sensor_id");
                            String temp_celsius = object.getString("temp_celsius");
                            String temp_fahrenheit = object.getString("temp_fahrenheit");
                            String is_in_C = object.getString("is_in_C");
                            String humidity = object.getString("humidity");

                            sectionedExpandableLayoutHelper.updateTempSensor(room_id, room_order, temp_sensor_id, temp_celsius, temp_fahrenheit, is_in_C,humidity);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    };

//    private Emitter.Listener deleteChildUser = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            if (getActivity() == null) {
//                return;
//            }
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (args != null) {
//
//                        try {
//                            JSONObject object = new JSONObject(args[0].toString());
//                            String message=object.optString("message");
//                            String user_id=object.optString("user_id");
//                            if(Common.getPrefValue(getActivity(), Constants.USER_ID).equalsIgnoreCase(user_id)){
//                                ((Main2Activity)getActivity()).logoutCloudUser();
//                                ChatApplication.showToast(getActivity(),message);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//        }
//    };

    private Emitter.Listener updateChildUser = new Emitter.Listener() {
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
                            JSONObject object = new JSONObject(args[0].toString());

                            String message = object.optString("message");
                            String user_id = object.optString("user_id");
                            ChatApplication.logDisplay("update socket is " + object.toString());
                            if (Common.getPrefValue(getActivity(), Constants.USER_ID).equalsIgnoreCase(user_id)) {
                                showDialog = 1;
                                getDeviceList(showDialog);
//                                ChatApplication.showToast(getActivity(),message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

//    private Emitter.Listener heavyLoadValue = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            if (getActivity() == null) {
//                return;
//            }
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (args != null) {
//
//                        try {
//                            JSONObject object = new JSONObject(args[0].toString());
//
//                       //
//                            ChatApplication.logDisplay("object socket is found heavyLoadValue " + object);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//        }
//    };

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
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("sensorStatus is "+jsonObject.toString());
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
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        ChatApplication.logDisplay("configureNewRoom configureGatewayDevice");
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
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

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

        String url = "";
        if (isTempSensorRequest) {
            url = ChatApplication.url + Constants.ADD_TEMP_SENSOR;
        } else {
            url = ChatApplication.url + Constants.ADD_DOOR_SENSOR;
        }

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay(isTempSensorRequest + " configureNewSensor onSuccess " + result.toString());
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
                ChatApplication.logDisplay(isTempSensorRequest + " configureNewSensor onFailure " + error);
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


    private void deviceOnOff(final DeviceVO deviceVO, final int position) {

        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

            if(deviceVO.getDeviceType().equalsIgnoreCase("3")){
                obj.put("status",deviceVO.getDeviceStatus());
                obj.put("bright","");
                obj.put("is_rgb","0");//1;
                obj.put("rgb_array","");
                obj.put("room_device_id",deviceVO.getRoomDeviceId());
            }else {
                obj.put("room_device_id", deviceVO.getRoomDeviceId());
                obj.put("module_id", deviceVO.getModuleId());
                obj.put("device_id", deviceVO.getDeviceId());
                obj.put("device_status", deviceVO.getOldStatus());
                obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
                //   obj.put("is_change","0");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket != null && mSocket.connected()) {
            mSocket.emit("socketChangeDeviceAck", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
                @Override
                public void call(Object... args) {

                    if (args != null) {

                        if (args[0].toString().equalsIgnoreCase("No Ack")) {

                            updateDeviceOfflineMode(deviceVO);

                        } else if (args[0].toString().equalsIgnoreCase("true")) {
                            cancelTimer();
                        }
                    }
                }
            });

        } else {

            callDeviceOnOffApi(deviceVO, obj);

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
                        String.valueOf(deviceVO.getDeviceId()), String.valueOf(deviceVO.getOldStatus()), deviceVO.getIs_locked());
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
                sectionedExpandableLayoutHelper.updatePanel(panelVO.getPanelId(), String.valueOf(panelVO.getOldStatus()), panelVO.getRoom_panel_id());
            }
        });
    }

    private void callDeviceOnOffApi(final DeviceVO deviceVO, JSONObject obj) {

        String url="";
        if(deviceVO.getDeviceType().equalsIgnoreCase("3")){
            url = ChatApplication.url + Constants.changeHueLightState;
        }else {
            url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;
        }

        ChatApplication.logDisplay("Device roomPanelOnOff obj " + obj.toString());
        //  ChatApplication.logDisplay( "roomPanelOnOff url " + url );

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
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
                    ChatApplication.logDisplay("Device roomPanelOnOff finally ");
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("roomPanelOnOff onFailure " + error);
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
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("room_id", roomId);
            obj.put("panel_id", panelId);
            obj.put("device_status", panel_status);
            obj.put("operationtype", type);
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket != null && mSocket.connected()) {

            ChatApplication.logDisplay("roomPanelOnOff");

            mSocket.emit("changeRoomPanelMoodStatusAck", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
                @Override
                public void call(Object... args) {

                    if (args != null) {

                        ChatApplication.logDisplay("Panel AckWithTimeOut log : " + args[0].toString());
                        if (args[0].toString().equalsIgnoreCase("No Ack")) {

                            ChatApplication.logDisplay("Panel AckWithTimeOut1 : " + args[0].toString());

                            //  callPanelOnOffApi(panelVO,obj);

                            if (panelVO != null) {
                                updatePanelDeviceOfflineMode(panelVO);
                                ChatApplication.logDisplay("update Panel Device OffLine");
                            } else if (roomVO != null) {
                                updateRoomOfflineMode(roomVO);
                                ChatApplication.logDisplay("update Room Device OffLine");
                            }

                        } else if (args[0].toString().equalsIgnoreCase("true")) {
                            cancelTimer();
                            ChatApplication.logDisplay("Panel AckWithTimeOut2 : " + args[0].toString());
                        }
                    }
                }
            });

        } else {
            callPanelOnOffApi(obj);

        }
    }

    private void callPanelOnOffApi(JSONObject obj) {

        String url = ChatApplication.url + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW;

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
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
                    ChatApplication.logDisplay("roomPanelOnOff finally ");
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("roomPanelOnOff onFailure " + error);
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
        if (action.equalsIgnoreCase("heavyloadSocketon")) {

//            heavyloadSocket(roomVO, action);

        } else if (action.equalsIgnoreCase("heavyloadSocketoff")) {

//            heavyloadSocket(roomVO, action);

        } else if (action.equalsIgnoreCase("showGridCamera")) {
            Intent intent = new Intent(getActivity(), CameraGridActivity.class);
            intent.putExtra("cameraList", cameraList);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("refreshCamera")) {
            getDeviceList(1);

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
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("isEditable", false);
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
            Intent intent = new Intent(getActivity(), DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", roomVO.getRoomId());
            intent.putExtra("activity_type", roomVO.getType());
            intent.putExtra("isCheckActivity", "room");
            intent.putExtra("isRoomName", "" + roomVO.getRoomName());
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
            intent.putExtra("isRoomMainFm", "room");
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
            object.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());

        //  ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);

        String url = webUrl + Constants.DELETE_ROOM;


        new GetJsonTask(getActivity(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
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
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
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
        }else if (action.equalsIgnoreCase("philipsClick")) {
            deviceOnOff(item, position);
            //  item.setDeviceStatus(item.getDeviceStatus()==0?1:0);
        }else if (action.equalsIgnoreCase("curtain")) {
            Intent intent = new Intent(getActivity(), CurtainActivity.class);
            intent.putExtra("curtain_id", item.getDeviceId());
            startActivity(intent);

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

            if (item.getSensor_type().equalsIgnoreCase("tempsensor") && item.getIsActive()==1) {

//                Intent intent = new Intent(getActivity(), TempSensorInfoActivity.class);
                Intent intent = new Intent(getActivity(), MultiSensorActivity.class);
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getModuleId());
                startActivity(intent);

            }else if (item.getSensor_type().equalsIgnoreCase("gassensor")) {

//                Intent intent = new Intent(getActivity(), TempSensorInfoActivity.class);
                Intent intent = new Intent(getActivity(), GasSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("module_id", item.getModuleId());
                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("multisensor")) {
                if(item.getIsActive()== -1){
                    return;
                }

                Intent intent = new Intent(getActivity(), MultiSensorActivity.class);
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getModuleId());
                startActivity(intent);
            } else if (item.getSensor_type().equalsIgnoreCase("door")) {
                ChatApplication.logDisplay("door call is intent "+mSocket.connected());
                Intent intent = new Intent(getActivity(), DoorSensorInfoActivity.class);
                intent.putExtra("door_sensor_id", item.getSensor_id());
                intent.putExtra("door_room_name", item.getRoomName());
                intent.putExtra("door_room_id", item.getRoomId());
                intent.putExtra("door_unread_count", item.getIs_unread());
                intent.putExtra("door_module_id", item.getModuleId());
                startActivity(intent);
            }
        }else if(action.equalsIgnoreCase("heavyloadlongClick")){
            if(countDownTimerSocket!=null){
                countDownTimerSocket.cancel();
                countDownTimerSocket.onFinish();
                countDownTimerSocket=null;
            }
            Intent intent=new Intent(getActivity(), HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomDeviceId",item.getRoomDeviceId());
            intent.putExtra("getRoomName",item.getRoomName());
            intent.putExtra("getModuleId",item.getModuleId());
            intent.putExtra("device_id",item.getDeviceId());
            startActivity(intent);

        }else if(action.equalsIgnoreCase("philipslongClick")){
            //on-off remote
            if(item.getDeviceStatus()==1){
                Intent intent=new Intent(getActivity(), SmartColorPickerActivity.class);
                intent.putExtra("roomDeviceId",item.getRoomDeviceId());
                intent.putExtra("getOriginal_room_device_id",item.getOriginal_room_device_id());
                startActivity(intent);
            }else {
                ChatApplication.showToast(getActivity(),"Please device on");
            }


        } else if (action.equalsIgnoreCase("isIRSensorClick")) {
            //on-off remote
            sendRemoteCommand(item,"isIRSensorClick");
        }else if (action.equalsIgnoreCase("doorOpenClose") && item.getDoor_subtype()!=1) {
            callDoorRemotly(item);
        } else if (action.equalsIgnoreCase("isIRSensorLongClick")) {

            Intent intent = new Intent(getActivity(), IRBlasterRemote.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("REMOTE_IS_ACTIVE", item.getDeviceStatus());
            bundle.putSerializable("REMOTE_ID", item.getSensor_id());
            bundle.putSerializable("IR_BLASTER_ID", item.getSensor_id());
            // intent.putExtra("IR_BLASTER_ID",item.getIr_blaster_id());
            intent.putExtra("IR_BLASTER_ID", item.getSensor_id());
            intent.putExtra("ROOM_DEVICE_ID", item.getRoomDeviceId());
            intent.putExtras(bundle);
            startActivity(intent);
        }else if (action.equalsIgnoreCase("doorLongClick")) {
            Intent intent = new Intent(getActivity(), DoorSensorInfoActivity.class);
            intent.putExtra("door_sensor_id", item.getSensor_id());
            intent.putExtra("door_room_name", item.getRoomName());
            intent.putExtra("door_room_id", item.getRoomId());
            intent.putExtra("door_unread_count", item.getIs_unread());
            intent.putExtra("door_module_id", item.getModuleId());
            startActivity(intent);
        }
    }

    private void doorOpenCLose(DeviceVO item) {

        if(mSocket!=null){
            // "lock_id":"",
            // "user_id":"",
            // "door_sensor_status":"",
            // "lock_status":""

            JSONObject object=new JSONObject();
            try {
                object.put("lock_id",item.getLock_id());
                object.put("user_id",Common.getPrefValue(getActivity(), Constants.USER_ID));
                object.put("door_sensor_status",item.getDoor_sensor_status().equals("1") ? 0:1 );
                object.put("lock_status",item.getDoor_sensor_status().equals("1") ? 0:1 );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatApplication.logDisplay("door json "+object);
            mSocket.emit("changeLockStatus",object);
        }

    }

    private void callDoorRemotly(DeviceVO item) {
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();

        Call<ResponseBody> call;
        if(item.getDoor_sensor_status().equalsIgnoreCase("1")){
           call = apiService.unlockGatewayUse(Constants.client_id, Constants.access_token, item.getLock_id(),System.currentTimeMillis());
        }else {
            call = apiService.lockGatewayUse(Constants.client_id, Constants.access_token, item.getLock_id(),System.currentTimeMillis());
        }
        ChatApplication.logDisplay("door json call");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ActivityHelper.dismissProgressDialog();
                if(response.code()==200){
                    doorOpenCLose(item);
                    ChatApplication.logDisplay("lock is "+response.body().toString());

                }else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });
    }

    private void sendRemoteCommand(final DeviceVO item, String philipslongClick) {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        String mRemoteCommandReq="";
        String url ="";
//        if(philipslongClick.equalsIgnoreCase("philipslongClick")){
//            url = ChatApplication.url + Constants.changeHueLightState;
//
//            JSONObject jsonObject=new JSONObject();
//            try {
//                jsonObject.put("status",item.getDeviceStatus()==1 ? 0:1);
//                jsonObject.put("bright","");
//                jsonObject.put("is_rgb","0");//1;
//                jsonObject.put("rgb_array","");
//                jsonObject.put("room_device_id",item.getRoomDeviceId());
//                mRemoteCommandReq=jsonObject.toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }else {
            url = ChatApplication.url + Constants.SEND_REMOTE_COMMAND;
            SendRemoteCommandReq sendRemoteCommandReq = new SendRemoteCommandReq();
            sendRemoteCommandReq.setRemoteid(item.getSensor_id());

            sendRemoteCommandReq.setPower(item.getRemote_status().equalsIgnoreCase("OFF") ? "ON" : "OFF");
            sendRemoteCommandReq.setSpeed(item.getSpeed());
            sendRemoteCommandReq.setTemperature(item.getTemprature());
            sendRemoteCommandReq.setRoomDeviceId(item.getRoomDeviceId());
            sendRemoteCommandReq.setPhoneId(APIConst.PHONE_ID_VALUE);
            sendRemoteCommandReq.setPhoneType(APIConst.PHONE_TYPE_VALUE);

            Gson gson = new Gson();
            mRemoteCommandReq = gson.toJson(sendRemoteCommandReq);
//        }


        com.spike.bot.core.Log.d("sendRemoteCommand", "" + mRemoteCommandReq);


        new GetJsonTaskRemote(getContext(), url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                com.spike.bot.core.Log.d("SendRemote", "onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("result is ir "+result);
                    if (code == 200) {
                        //update remote UI
                        ChatApplication.isMoodFragmentNeedResume = true;
                        SendRemoteCommandRes tmpIrBlasterCurrentStatusList = Common.jsonToPojo(result.toString(),
                                SendRemoteCommandRes.class);
                        // getDeviceList();


                    } else {
                        ChatApplication.showToast(getActivity(),item.getSensor_name()+" "+getActivity().getString(R.string.ir_error));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result is ir error "+error.toString());
                ChatApplication.showToast(getActivity(),item.getSensor_name()+" "+getActivity().getString(R.string.ir_error));
            }
        }).execute();
    }


    /**
     * @param item
     * @param action
     */

    @Override
    public void itemClicked(CameraVO item, String action) {
//        String url = item.getCamera_ip();
//        url = url + item.getCamera_videopath();
//        String camera_name = item.getCamera_name();

        if (action.equalsIgnoreCase("editcamera")) {
            Intent intent = new Intent(getActivity(), CameraEdit.class);
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("cameraSelcet", item);
            intent.putExtra("isEditable", false);
            startActivity(intent);
        }else if(action.equalsIgnoreCase("cameraLog")){
            Intent intent=new Intent(getActivity(),CameraDeviceLogActivity.class);
            intent.putExtra("cameraId",""+item.getCamera_id());
            startActivity(intent);
        }else {
            callCameraToken(item, action);
        }

//        ChatApplication.logDisplay("isCloudConnect : " + Main2Activity.isCloudConnected + " url : " + url + " : item url >> " + item.getCamera_url());

        //TODO code here for cloud connected or not...
        // rtmp://LOCAL_IP/live/livestream1528897402049_SJftJoAe7
//        if (Main2Activity.isCloudConnected) {
//            url = Constants.CAMERA_DEEP + ":" + item.getCamera_vpn_port() + "" + item.getCamera_url();
//        } else {
//            //String tmpurl = ChatApplication.url + "/"+item.getCamera_url();
//            String tmpurl = ChatApplication.url + "" + item.getCamera_url();
//            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
//        }


//        //start camera rtsp player
//        Intent intent = new Intent(getActivity(), CameraPlayer.class);
//        intent.putExtra("videoUrl", url);
//        intent.putExtra("name", camera_name);
//        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
//        startActivity(intent);
    }

    private void callCameraToken(final CameraVO item, final String action) {
        ActivityHelper.showProgressDialog(getActivity(), " Please Wait...", false);
        String url = ChatApplication.url + Constants.getCameraToken + item.getCamera_id();

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");

                    if (code == 200) {
                        ActivityHelper.dismissProgressDialog();
                        JSONObject dataObject = result.optJSONObject("data");

                        String camera_vpn_port = dataObject.optString("camera_vpn_port");
                        String camera_url = dataObject.optString("camera_url");

                        String url = item.getCamera_ip();
                        url = url + item.getCamera_videopath();
                        String camera_name = item.getCamera_name();

                        ChatApplication.logDisplay("isCloudConnect : " + dataObject);

                        //TODO code here for cloud connected or not...
                        // rtmp://LOCAL_IP/live/livestream1528897402049_SJftJoAe7
//                        if (Main2Activity.isCloudConnected) {
//                            url = Constants.CAMERA_DEEP + ":" + item.getCamera_vpn_port() + "" + item.getCamera_url();
//                        } else {
//                            //String tmpurl = ChatApplication.url + "/"+item.getCamera_url();
//                            String tmpurl = ChatApplication.url + "" + item.getCamera_url();
//                            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
//                        }

                        if (Main2Activity.isCloudConnected) {
                            url = Constants.CAMERA_DEEP + ":" + camera_vpn_port + "" + camera_url;
                        } else {
                            String tmpurl = ChatApplication.url + "" + camera_url;
                            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
                        }


                        //start camera rtsp player
                        Intent intent = new Intent(getActivity(), CameraPlayer.class);
                        intent.putExtra("videoUrl", url);
                        intent.putExtra("name", camera_name);
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
                        ActivityHelper.dismissProgressDialog();
                    }
                } catch (JSONException e) {
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
        }

        public void onFinish() {
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
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
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
            object.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());
        ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

        String url = webUrl + Constants.ADD_CUSTOME_ROOM;

        new GetJsonTask(getActivity(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
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
                } finally {
                    {
                        ActivityHelper.dismissProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    public void getconfigureData() {
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

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /**
     *
     */
    public boolean addTempSensor = false;

    private void getTempConfigData() {
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
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    //get config IR Blaster
    public boolean addIRBlasterSensor = false;

    private void getIRBlasterConfigData() {
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
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
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
//                            responseErrorCode.onLogout();
                            ((Main2Activity) getActivity()).logoutCloudUser();

                        }
                    }
                }).show();
    }

    public static int SING_UP_REQUEST = 500;

    public static void saveCurrentId(Context context, String userId, String gatewayIp) {
        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(context, Common.USER_JSON);
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> userList = gson.fromJson(jsonText, type);

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUser_id().equals(userId)) {
                userList.get(i).setIsActive(true);
                userList.get(i).setGateway_ip(gatewayIp);
                mCallback.onArticleSelected("" + userList.get(i).getFirstname());
            } else {
                userList.get(i).setIsActive(false);
            }
        }
        if (userList.size() > 0) {
            String jsonCurProduct = gson.toJson(userList);
            Common.savePrefValue(context, Common.USER_JSON, jsonCurProduct);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    int countFlow=0;

    public  void getMacAddress(String locaIp) {
        countFlow=0;

//        progressDialog.show();

        String url = ChatApplication.http+locaIp + Constants.getLocalMacAddress;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTaskMacAddress(getActivity(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                countFlow=1;
                countDownTimer.cancel();
                // {"code":200,"message":"Success","data":{"mac_address":"b8:27:eb:f7:45:b3"}}

                ChatApplication.logDisplay("reponse is mac "+result.toString());
//                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject object=new JSONObject(result.toString());
                    JSONObject jsonObject=object.optJSONObject("data");

                    if(jsonObject!=null && countFlow!=2){
                        if(jsonObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(getActivity()))){
                            callColud(true);
                        }else {
                            callColud(false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
//                ActivityHelper.dismissProgressDialog();
                countDownTimer.cancel();
                ChatApplication.logDisplay("reponse is mac error "+error.toString());
                if(countFlow!=2){
                    countFlow=1;
                    callColud(false);
                }


            }
        }).execute();

         CountDownTimer countDownTimer=  new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                if(countFlow==0){
                    countFlow=2;
                    ChatApplication app = ChatApplication.getInstance();
                    app.closeSocket(webUrl);

                    if (mSocket != null) {
                        mSocket.disconnect();
                        mSocket = null;
                    }
                    ChatApplication.logDisplay("show is calling time ");
                    callColud(false);
                }

            }

        };
        countDownTimer.start();


    }

    private void callColud(boolean isflag) {
        ChatApplication.logDisplay("show is call "+isflag);
        if(isflag){
            //local
            ((Main2Activity)getActivity()).webUrl=ChatApplication.http+Constants.getuserIp(getActivity())+":80";
            ChatApplication.url=ChatApplication.http+Constants.getuserIp(getActivity());
            if(!ChatApplication.url.startsWith("http")){
                ChatApplication.url="http://"+ChatApplication.url;
            }
            ((Main2Activity)getActivity()).callSocket();
            ((Main2Activity) getActivity()).invalidateToolbarCloudImage();

            getDeviceLocal(0);
        }else {
            String colud=Constants.getuserCloudIP(getActivity());
            ChatApplication.url=colud;
            if(!ChatApplication.url.startsWith("http")){
                ChatApplication.url="http://"+ChatApplication.url;
            }
            ((Main2Activity)getActivity()).webUrl=colud;
            ((Main2Activity)getActivity()).callSocket();
            ((Main2Activity) getActivity()).invalidateToolbarCloudImage();

            getDeviceCloud(0);
        }

    }

    public void getDeviceCloud(int isShow) {

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if(isShow==0){
//            if(ChatApplication.url.startsWith("http://home.")){
            if(ChatApplication.url.startsWith(Constants.startUrl)){
                if(ActivityHelper.m_progressDialog==null){
                    ActivityHelper.showProgressDialog(getActivity(), "Connecting to cloud...", false);
                }
                ActivityHelper.m_progressDialog.setMessage("Connecting to cloud...");
            }else {
                if(ActivityHelper.m_progressDialog==null){
                    ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
                }
            }
        }


        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        }
        mMessagesView.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

//        if(!ChatApplication.url.startsWith("http://")){
//            ChatApplication.url="http://"+ChatApplication.url;
//        }


        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

            if (TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", 1);
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE)));
            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard cloud " + url + "   " + jsonObject.toString());
        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.isCallDeviceList=false;
                ((Main2Activity) getActivity()).wifiConnectionIssue(true);
                if (ChatApplication.isPushFound) {
                    getBadgeClear(getActivity());
                }

                mMessagesView.setClickable(true);
                responseErrorCode.onSuccess();

//                //connect socket
//                startSocketConnection();

                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    isCheckFlow=false;
                    webUrl=ChatApplication.url;
                    startSocketConnection();
                    ActivityHelper.dismissProgressDialog();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Constants.socketType=1;
                        Constants.socketIp=ChatApplication.url;
                        hideAdapter(true);
                        JSONObject dataObject = result.getJSONObject("data");
                        ((Main2Activity)getActivity()).getUserDialogClick(true);

//                        if(!webUrl.startsWith("http://home.deepfoods") && !dataObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(getActivity()))){
//                            ChatApplication.logDisplay("mac is = "+dataObject.optString("mac_address")+" "+Constants.getMacAddress(getActivity()));
//                            flagisCould = false;
//                            showDialog = 1;
//                            ChatApplication.url = Constants.getuserCloudIP(getActivity());
//                            ((Main2Activity) getActivity()).webUrl= ChatApplication.url;
//                            isCloudConnected = true;
//                            ((Main2Activity)getActivity()).startSocketConnectionNew();
//                            isResumeConnect = false;
//                            ChatApplication.isRefreshHome = false;
//                            ((Main2Activity) getActivity()).getUserDialogClick(true);
//                            ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                            getDeviceCould(12);
//                            return;
//                        }

                        roomList = new ArrayList<>();

                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {

                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            // mCallback.onArticleSelected("Spike Bot");

                            if (!TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ID))) {
                                ((Main2Activity) getActivity()).logoutCloudUser();
                            } else {
//                                Intent intent = new Intent(getActivity(), SignUp.class);
//                                startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
                            }
                            return;
                        }


                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String is_active = userObject.optString("is_active");
                        String gateway_ip = userObject.optString("gateway_ip");

                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) getActivity()).logoutCloudUser();
                            return;
                        }

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
                            Type type = new TypeToken<List<PiDetailsModel>>() {}.getType();
                            piUserList = gson.fromJson(jsonText, type);
                        }

                        String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonTextTemp) && !jsonTextTemp.equals("null")) {
                            Type type = new TypeToken<List<User>>() {}.getType();
                            userList = gson.fromJson(jsonTextTemp, type);
                        }

//                        if (userList != null && userList.size() > 0) {
//                            boolean flagIsLogin = false;
//                            for (User user : userList) {
//                                if (user.getUser_id().equalsIgnoreCase(userId) && user.getIsActive()) {
//                                    flagIsLogin = true;
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    break;
//                                }
//                            }
//
//                            if (flagIsLogin == false) {
//                                if (checkLoginId(userId)) {
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    ChatApplication.logDisplay("pie details is isSignUp ");
//                                } else if (flagisCould) {
//                                    flagisCould = false;
//                                    showDialog = 1;
//                                    ((Main2Activity) getActivity()).getUserDialogClick(true);
//                                    ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                                    getDeviceCould(12);
//                                    return;
//                                } else {
//                                    showDialog = 1;
//                                    loginPIEvent.showLogin();
//                                    return;
//                                }
//                            } else {
//                                ((Main2Activity) getActivity()).loginDialog(false, false);
//                            }
//                        } else {
//                            showDialog = 1;
//                            loginPIEvent.showLogin();
//                            return;
//                        }

                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
//                        if (Main2Activity.isCloudConnected && !TextUtils.isEmpty(userPassword)) {
                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {}.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type);
                            }

                            for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        showLogoutAlert();
                                        ChatApplication.showToast(getActivity(), "Password has been changed!");
                                        ((Main2Activity) getActivity()).logoutCloudUser();
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


                        // getActivity().setTitle("" + userFirstName + " " + userLastName);
                        /**
                         * <h1>for set ToolBar title in MainActivity2</h1>
                         * {@link Main2Activity#toolbarTitle}
                         * @see Main2Activity#toolbar
                         */
                        mCallback.onArticleSelected("" + userFirstName);

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);

//                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
//                    exp_list.setAdapter(listAdapter);


                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {
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
                            section.setIs_unread(cameraList.get(0).getTotal_unread());

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
                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();

                        //JSONArray userList = dataObject.getJSONArray("userList");
                        Main2Activity.changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }

                        setUserTypeValue();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    showDialog = 0;
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
                //        ActivityHelper.dismissProgressDialog();
                //set custom homer controller not found error message on dashboard
                if (reCode == 503 || reCode == 404) {
                    responseErrorCode.onErrorCode(reCode);
                }
                ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
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

//                if(!ChatApplication.url.startsWith("http://home.")){
//                   callColud(false);
//                }else {
                ChatApplication.showToast(getActivity(),"Please try again");
//                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void getDeviceLocal(int isShow) {

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if(isShow==0){
//            if(ChatApplication.url.startsWith("http://home.")){
//                if(ActivityHelper.m_progressDialog==null){
//                    ActivityHelper.showProgressDialog(getActivity(), "Connecting to cloud...", false);
//                }
//                ActivityHelper.m_progressDialog.setMessage("Connecting to cloud...");
//            }else {
//            progressDialog.show();
                if(ActivityHelper.m_progressDialog==null){
                    ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
                }
//            }
        }


        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        }

        mMessagesView.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }


//        if(!ChatApplication.url.startsWith("http://")){
//            ChatApplication.url="http://"+ChatApplication.url;
//        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

            if (TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", 1);
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE)));
            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard local " + url + "   " + jsonObject.toString());
        new GetJsonTaslLocal(activity, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.isCallDeviceList=false;
                ((Main2Activity) getActivity()).wifiConnectionIssue(true);
                if (ChatApplication.isPushFound) {
                    getBadgeClear(getActivity());
                }
                mMessagesView.setClickable(true);
                responseErrorCode.onSuccess();

//                //connect socket
//                startSocketConnection();

                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay("getDeviceList onSuccess local " + result.toString());

                try {
                    webUrl=ChatApplication.url;
                    startSocketConnection();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Constants.socketType=0;
                        Constants.socketIp=ChatApplication.url;
                        isCheckFlow=true;
                        hideAdapter(true);
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                        JSONObject dataObject = result.getJSONObject("data");
                        ((Main2Activity)getActivity()).getUserDialogClick(true);

//                        if(!webUrl.startsWith("http://home.deepfoods") && !dataObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(getActivity()))){
//                            ChatApplication.logDisplay("mac is = "+dataObject.optString("mac_address")+" "+Constants.getMacAddress(getActivity()));
//                            flagisCould = false;
//                            showDialog = 1;
//                            ChatApplication.url = Constants.getuserCloudIP(getActivity());
//                            ((Main2Activity) getActivity()).webUrl= ChatApplication.url;
//                            isCloudConnected = true;
//                            ((Main2Activity)getActivity()).startSocketConnectionNew();
//                            isResumeConnect = false;
//                            ChatApplication.isRefreshHome = false;
//                            ((Main2Activity) getActivity()).getUserDialogClick(true);
//                            ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                            getDeviceCould(12);
//                            return;
//                        }

                        roomList = new ArrayList<>();
                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {

                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            // mCallback.onArticleSelected("Spike Bot");

                            if (!TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ID))) {
                                ((Main2Activity) getActivity()).logoutCloudUser();
                            } else {
//                                Intent intent = new Intent(getActivity(), SignUp.class);
//                                startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
                            }
                            return;
                        }


                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String is_active = userObject.optString("is_active");
                        String gateway_ip = userObject.optString("gateway_ip");

                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) getActivity()).logoutCloudUser();
                            return;
                        }

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
                            Type type = new TypeToken<List<PiDetailsModel>>() {}.getType();
                            piUserList = gson.fromJson(jsonText, type);
                        }

                        String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonTextTemp) && !jsonTextTemp.equals("null")) {
                            Type type = new TypeToken<List<User>>() {}.getType();
                            userList = gson.fromJson(jsonTextTemp, type);
                        }

//                        if (userList != null && userList.size() > 0) {
//                            boolean flagIsLogin = false;
//                            for (User user : userList) {
//                                if (user.getUser_id().equalsIgnoreCase(userId) && user.getIsActive()) {
//                                    flagIsLogin = true;
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    break;
//                                }
//                            }
//
//                            if (flagIsLogin == false) {
//                                if (checkLoginId(userId)) {
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    ChatApplication.logDisplay("pie details is isSignUp ");
//                                } else if (flagisCould) {
//                                    flagisCould = false;
//                                    showDialog = 1;
//                                    ((Main2Activity) getActivity()).getUserDialogClick(true);
//                                    ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                                    getDeviceCould(12);
//                                    return;
//                                } else {
//                                    showDialog = 1;
//                                    loginPIEvent.showLogin();
//                                    return;
//                                }
//                            } else {
//                                ((Main2Activity) getActivity()).loginDialog(false, false);
//                            }
//                        } else {
//                            showDialog = 1;
//                            loginPIEvent.showLogin();
//                            return;
//                        }

                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
//                        if (Main2Activity.isCloudConnected && !TextUtils.isEmpty(userPassword)) {
                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {}.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type);
                            }

                            for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        showLogoutAlert();
                                        ChatApplication.showToast(getActivity(), "Password has been changed!");
                                        ((Main2Activity) getActivity()).logoutCloudUser();
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


                        // getActivity().setTitle("" + userFirstName + " " + userLastName);
                        /**
                         * <h1>for set ToolBar title in MainActivity2</h1>
                         * {@link Main2Activity#toolbarTitle}
                         * @see Main2Activity#toolbar
                         */
                        mCallback.onArticleSelected("" + userFirstName);

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);

//                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
//                    exp_list.setAdapter(listAdapter);


                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {
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
                            section.setIs_unread(cameraList.get(0).getTotal_unread());

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
                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();

                        //JSONArray userList = dataObject.getJSONArray("userList");
                        Main2Activity.changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }

                        setUserTypeValue();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    showDialog = 0;
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
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                swipeRefreshLayout.setRefreshing(false);

                ChatApplication.logDisplay("show is call local error"+error);
                if(!TextUtils.isEmpty(error)){
                    ChatApplication.logDisplay("show is call local error22 "+error);
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
                    if(mSocket!=null){
                        mSocket.disconnect();
                    }
                    isCheckFlow=false;
                    callColud(false);
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void hideAdapter(boolean isflag){

        mMessagesView.setVisibility(isflag==true ? View.VISIBLE : View.INVISIBLE);
    }

    boolean isCallingFlag=false;
    /// all webservice call below.
    public void getDeviceList(final int checkmessgae) {
        //showProgress();

//        Common.savePrefValue(ChatApplication.getInstance(),  Constants.USER_ID, "1561786612083_ZdO_DUSnu");
//        Common.savePrefValue(ChatApplication.getInstance(),  Constants.USER_ADMIN_TYPE, "1");
        if (getActivity() == null) {
            return;
        }


        if(!Constants.checkLoginAccountCount(getActivity())){
            ((Main2Activity) getActivity()).showLogin();
            return;
        }

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (showDialog == 1 || checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 10) {
            ActivityHelper.showProgressDialog(getActivity(), " Please Wait...", false);
        }
        ChatApplication.logDisplay("show progress is " + showDialog);

        roomList.clear();
        // mMessagesView.removeAllViews();
        if (mMessagesView == null) {
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        }
        mMessagesView.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        if(!ChatApplication.url.startsWith("http://")){
            ChatApplication.url="http://"+ChatApplication.url;
        }

//        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        if(ChatApplication.isCallDeviceList) {
            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
            }
            hideAdapter(false);
            sectionedExpandableLayoutHelper.clearData();
            if (Common.isNetworkConnected(getActivity())) {
                getMacAddress(Constants.getuserIp(getActivity()));
            } else {
                callColud(false);
            }
        }else {
//            if(ChatApplication.url.startsWith("http://home.")){
            if(ChatApplication.url.startsWith(Constants.startUrl)){
//                getDeviceCould(1);
                getDeviceCloud(1);
            }else {
                getDeviceLocal(1);
            }
//getDeviceCloud
        }



//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            //admin =0
//            //child=2
//
//
//            jsonObject.put("room_type", 0);
//            jsonObject.put("is_sensor_panel", 1);
//            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
//
//            if (TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))) {
//                jsonObject.put("admin", 1);
//            } else {
//                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE)));
//            }
//            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
//            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ChatApplication.logDisplay("jsonObject is dashboard " + url + "   " + jsonObject.toString());
//        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
//            @Override
//            public void onSuccess(JSONObject result) {
//
//
//                ((Main2Activity) getActivity()).wifiConnectionIssue(true);
//                if (ChatApplication.isPushFound) {
//                    getBadgeClear(getActivity());
//                }
//
//                mMessagesView.setClickable(true);
//                responseErrorCode.onSuccess();
//
//                //connect socket
//                startSocketConnection();
//
//                swipeRefreshLayout.setRefreshing(false);
//                sectionedExpandableLayoutHelper.setClickable(true);
//                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());
//
//                try {
//
//                    int code = result.getInt("code");
//                    String message = result.getString("message");
//                    if (code == 200) {
//                        JSONObject dataObject = result.getJSONObject("data");
//                        ((Main2Activity)getActivity()).getUserDialogClick(true);
//                        if(!webUrl.startsWith("http://home.deepfoods") && !dataObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(getActivity()))){
//                            ChatApplication.logDisplay("mac is = "+dataObject.optString("mac_address")+" "+Constants.getMacAddress(getActivity()));
//                            flagisCould = false;
//                            showDialog = 1;
//                            ChatApplication.url = Constants.getuserCloudIP(getActivity());
//                            ((Main2Activity) getActivity()).webUrl= ChatApplication.url;
//                            isCloudConnected = true;
//                            ((Main2Activity)getActivity()).startSocketConnectionNew();
//                            isResumeConnect = false;
//                            ChatApplication.isRefreshHome = false;
//                            ((Main2Activity) getActivity()).getUserDialogClick(true);
//                            ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                            getDeviceCould(12);
//                            return;
//                        }
//
//                        roomList = new ArrayList<>();
//
//                        JSONArray userListArray = dataObject.getJSONArray("userList");
//
//                        if (userListArray.length() == 0) {
//
//                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
//                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");
//
//                            if (!TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ID))) {
//                                ((Main2Activity) getActivity()).logoutCloudUser();
//                            }
//                            return;
//                        }
//
//
//                        JSONObject userObject = userListArray.getJSONObject(0);
//                        String userId = userObject.getString("user_id");
//                        String userFirstName = userObject.getString("first_name");
//                        String userLastName = userObject.getString("last_name");
//                        String camera_key = userObject.optString("camera_key");
//                        String is_active = userObject.optString("is_active");
//                        String gateway_ip = userObject.optString("gateway_ip");
//
//                        if (is_active.equalsIgnoreCase("0")) {
//
//                            ((Main2Activity) getActivity()).logoutCloudUser();
//                            return;
//                        }
//
//                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
//                        ChatApplication.currentuserId = userId;
//                        String userPassword = "";
//                        if (userObject.has("user_password")) {
//                            userPassword = userObject.getString("user_password");
//                        }
//
//
//                        Gson gson = new Gson();
//                        String jsonText = Common.getPrefValue(getContext(), Common.USER_PIDETAIL);
//                        List<PiDetailsModel> piUserList = new ArrayList<PiDetailsModel>();
//                        if (!TextUtils.isEmpty(jsonText)) {
//                            Type type = new TypeToken<List<PiDetailsModel>>() {}.getType();
//                            piUserList = gson.fromJson(jsonText, type);
//                        }
//
//                        String jsonTextTemp = Common.getPrefValue(getContext(), Common.USER_JSON);
//                        List<User> userList = new ArrayList<User>();
//                        if (!TextUtils.isEmpty(jsonTextTemp) && !jsonTextTemp.equals("null")) {
//                            Type type = new TypeToken<List<User>>() {}.getType();
//                            userList = gson.fromJson(jsonTextTemp, type);
//                        }
//
//                        if (userList != null && userList.size() > 0) {
//                            boolean flagIsLogin = false;
//                            for (User user : userList) {
//                                if (user.getUser_id().equalsIgnoreCase(userId) && user.getIsActive()) {
//                                    flagIsLogin = true;
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    break;
//                                }
//                            }
//
//                            if (flagIsLogin == false) {
//                                if (checkLoginId(userId)) {
//                                    saveCurrentId(getActivity(), userId,gateway_ip);
//                                    mCallback.onArticleSelected("" + userFirstName);
//                                    ChatApplication.logDisplay("pie details is isSignUp ");
//                                } else if (flagisCould) {
//                                    flagisCould = false;
//                                    showDialog = 1;
//                                    ((Main2Activity) getActivity()).getUserDialogClick(true);
//                                    ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                                    getDeviceCould(12);
//                                    return;
//                                } else {
//                                    showDialog = 1;
//                                    loginPIEvent.showLogin();
//                                    return;
//                                }
//                            } else {
//                                ((Main2Activity) getActivity()).loginDialog(false, false);
//                            }
//                        } else {
//                            showDialog = 1;
//                            loginPIEvent.showLogin();
//                            return;
//                        }
//
//                        if (!TextUtils.isEmpty(userPassword)) {
//                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
//                            List<User> userList1 = new ArrayList<User>();
//                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
//                                Type type = new TypeToken<List<User>>() {}.getType();
//                                userList1 = gson.fromJson(jsonTextTemp1, type);
//                            }
//
//                            for (User user : userList1) {
//                                if (user.isActive()) {
//                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        ChatApplication.showToast(getActivity(), "Password has been changed!");
//                                        ((Main2Activity) getActivity()).logoutCloudUser();
//                                    }
//
//                                }
//                            }
//                        }
//
//                        try {
//                            if (userObject.has("is_logout")) {
//                                int isLogout = userObject.getInt("is_logout");
//                                if (isLogout == 1 && Main2Activity.isCloudConnected) {
//                                    mCallback.onLogout(true);
//                                }
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//
//                        Common.savePrefValue(ChatApplication.getInstance(), "first_name", userFirstName);
//                        Common.savePrefValue(ChatApplication.getInstance(), "last_name", userLastName);
//                        Common.savePrefValue(getContext(), Constants.USER_PASSWORD, userPassword);
//
//
//                        // getActivity().setTitle("" + userFirstName + " " + userLastName);
//                        /**
//                         * <h1>for set ToolBar title in MainActivity2</h1>
//                         * {@link Main2Activity#toolbarTitle}
//                         * @see Main2Activity#toolbar
//                         */
//                        mCallback.onArticleSelected("" + userFirstName);
//
//                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
//                        roomList = JsonHelper.parseRoomArray(roomArray, false);
//                        sectionedExpandableLayoutHelper.addSectionList(roomList);
//
////                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
////                    exp_list.setAdapter(listAdapter);
//
//
//                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
//                        if (cameraArray.length() > 0) {
//                            cameraList = JsonHelper.parseCameraArray(cameraArray);
//
//                            RoomVO section = new RoomVO();
//                            section.setRoomName("Camera");
//                            section.setRoomId("Camera");
//
//                            ArrayList<PanelVO> panelList = new ArrayList<>();
//                            PanelVO panel = new PanelVO();
//                            panel.setPanelName("Devices");
//                            panel.setType("camera");
//                            panel.setCameraList(cameraList);
//
//                            panelList.add(panel);
//
//                            section.setPanelList(panelList);
//                            section.setIs_unread(cameraList.get(0).getTotal_unread());
//
//                            roomList.add(section);
//
//                            /*camera device counting*/
//                            for (int i = 0; i < roomList.size(); i++) {
//                                if (roomList.get(i).getRoomId().equalsIgnoreCase("Camera")) {
//                                    roomList.get(i).setDevice_count("" + roomList.get(i).getPanelList().get(0).getCameraList().size());
//                                }
//                            }
//
//                            //comment if roomList empty could not display cameraList for remove this conditions
//                            // sectionedExpandableLayoutHelper.addCameraList(JsonHelper.parseCameraArray(cameraArray));
//                            sectionedExpandableLayoutHelper.addSectionList(roomList);
//                        }
//                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
//                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//
//                        //JSONArray userList = dataObject.getJSONArray("userList");
//                        Main2Activity.changestatus();
//                        if (roomArray.length() == 0) {
//                            mMessagesView.setVisibility(View.GONE);
//                            txt_empty_schedule.setVisibility(View.VISIBLE);
//                        } else {
//                            mMessagesView.setVisibility(View.VISIBLE);
//                            txt_empty_schedule.setVisibility(View.GONE);
//                        }
//
//                        setUserTypeValue();
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                    showDialog = 0;
//                    if (roomList != null) {
//                        CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
//                    }
//                    mMessagesView.setClickable(true);
//                    mFab.setClickable(true);
//                    swipeRefreshLayout.setRefreshing(false);
////                    ((Main2Activity)getActivity()).toolbarTitle.setClickable(true);
////                    ((Main2Activity)getActivity()).toolbarImage.setClickable(true);
//                    if (roomList.size() == 0) {
//                        mMessagesView.setVisibility(View.GONE);
//                        txt_empty_schedule.setVisibility(View.VISIBLE);
//                    } else {
//                        mMessagesView.setVisibility(View.VISIBLE);
//                        txt_empty_schedule.setVisibility(View.GONE);
//                    }
//                    ActivityHelper.dismissProgressDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String error, int reCode) {
//                swipeRefreshLayout.setRefreshing(false);
//                //        ActivityHelper.dismissProgressDialog();
//                //set custom homer controller not found error message on dashboard
//                if (reCode == 503 || reCode == 404) {
//                    responseErrorCode.onErrorCode(reCode);
//                }
//                ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
//                //  Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
//
//                //for clear adapter and view after resume the screen
//                sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
//                sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);
//                sectionedExpandableLayoutHelper.setClickable(true);
//                sectionedExpandableLayoutHelper.notifyDataSetChanged();
//
//                if (roomList.size() == 0) {
//                    mMessagesView.setVisibility(View.GONE);
//                    txt_empty_schedule.setVisibility(View.VISIBLE);
//                } else {
//                    mMessagesView.setVisibility(View.VISIBLE);
//                    txt_empty_schedule.setVisibility(View.GONE);
//                }
//            }
//        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setUserTypeValue() {
        if (!TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))) {
            if (Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                mFab.setVisibility(View.GONE);
                ((Main2Activity) getActivity()).toolbarImage.setVisibility(View.VISIBLE);
                ((Main2Activity) getActivity()).toolbarTitle.setClickable(true);
                ((Main2Activity) getActivity()).toolbarImage.setClickable(true);

                ((Main2Activity) getActivity()).toolbarTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                mFab.setVisibility(View.VISIBLE);
                ((Main2Activity) getActivity()).toolbarTitle.setClickable(true);
                ((Main2Activity) getActivity()).toolbarImage.setClickable(true);
                ((Main2Activity) getActivity()).toolbarImage.setVisibility(View.VISIBLE);
                ((Main2Activity) getActivity()).toolbarTitle.setTextColor(getResources().getColor(R.color.sky_blue));
            }
        }
    }

    /// all webservice call below.
    public void getDeviceCould(final int checkmessgae) {
        //showProgress();
        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
        if (getActivity() == null) {
            return;
        }

        if (((Main2Activity) getActivity()).dialogUser != null) {
            if (((Main2Activity) getActivity()).dialogUser.isShowing()) {
                ((Main2Activity) getActivity()).dialogUser.dismiss();
            }
        }

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (checkmessgae != 1) {
            ActivityHelper.showProgressDialog(getActivity(), "Connectiong to cloud", false);
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

        // String url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
        if(!ChatApplication.url.startsWith("http://")){
            ChatApplication.url="http://"+ChatApplication.url;
        }
        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        if (!token_id.equalsIgnoreCase("")) {
            url = url + "/" + token_id;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ROOM_TYPE)));
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            jsonObject.put("admin", Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE));

            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard could " + url + "   " + jsonObject.toString());

        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                showDialog = 0;
                isRefredCheck = false;
                mMessagesView.setClickable(true);
                mFab.setClickable(true);
                mFab.setClickable(true);
//                ActivityHelper.dismissProgressDialog();
                ((Main2Activity) getActivity()).toolbarTitle.setClickable(true);
                ((Main2Activity) getActivity()).toolbarImage.setClickable(true);

                responseErrorCode.onSuccess();

                //connect socket
//                startSocketConnection();

                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
//                    startSocketConnection();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
                        roomList = new ArrayList<>();

                        JSONObject dataObject = result.getJSONObject("data");

                        JSONArray userListArray = dataObject.optJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {

                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            // mCallback.onArticleSelected("Spike Bot");

//                            Intent intent = new Intent(getActivity(), SignUp.class);
//                            startActivityForResult(intent, SIGN_IP_REQUEST_CODE);
                            return;
                        }


                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String ip = userObject.optString("ip");
                        String is_active = userObject.optString("is_active");
                        String gateway_ip = userObject.optString("gateway_ip");

                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) getActivity()).logoutCloudUser();
                            return;
                        }

                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        ChatApplication.currentuserId = userId;
                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
                        }


                        Common.savePrefValue(getActivity(), Constants.USER_PASSWORD, userPassword);
                        Common.savePrefValue(getActivity(), Constants.PREF_IP, ip);

                        // User user = new User(userId,userFirstName,userLastName,ip,false,userPassword);

                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getActivity(), Common.USER_JSON);
                        Type type = new TypeToken<List<User>>() {}.getType();
                        List<User> userList = gson.fromJson(jsonText, type);
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUser_id().equalsIgnoreCase(userId)) {
                                userList.get(i).setIsActive(true);
                                ((Main2Activity) getActivity()).isCloudConnected = true;
                                // ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
                                webUrl = userList.get(i).getCloudIP();
                                if(!webUrl.startsWith("http")){
                                    webUrl="http://"+webUrl;
                                }
                                ChatApplication.url = webUrl;
                            } else {
                                userList.get(i).setIsActive(false);
                            }
                        }

//                        userList.get(0).setIsActive(true);
//                        ((Main2Activity) getActivity()).isCloudConnected = true;
//                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
//                        webUrl = userList.get(0).getCloudIP();
//                        ChatApplication.url = webUrl;
                        String jsonCurProduct = gson.toJson(userList);
                        mCallback.onArticleSelected("" + userFirstName);
                        Common.savePrefValue(getActivity(), Common.USER_JSON, jsonCurProduct);

                        saveCurrentId(getActivity(), userId, gateway_ip);

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

                        mCallback.onArticleSelected("" + userFirstName);

                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type1 = new TypeToken<List<User>>() {}.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type1);
                            }

                            for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        showLogoutAlert();
                                        ChatApplication.showToast(getActivity(), "Password has been changed!");
                                        ((Main2Activity) getActivity()).logoutCloudUser();
                                    }

                                }
                            }
                        }

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);
                        setUserTypeValue();
//                    MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(),roomList);
//                    exp_list.setAdapter(listAdapter);


                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {
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
                            section.setIs_unread(cameraList.get(0).getTotal_unread());

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
                        ((Main2Activity) getActivity()).invalidateToolbarCloudImage();
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


                        /*JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        CameraAdapter cameraAdapter = new CameraAdapter(getActivity(), cameraArray, webUrl);
                        camera_list.setAdapter(cameraAdapter);*/

//                        ActivityHelper.dismissProgressDialog();
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
                ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
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

    //getMoodList
    public static void getBadgeClear(final Context context) {
        if(!Constants.checkLoginAccountCount(context)){
            return;
        }
        String url = ChatApplication.url + Constants.updateBadgeCount;

        JSONObject jsonObject = new JSONObject();
        try {
            if (activity == null) {
                jsonObject.put("user_id", "");
            } else {
                jsonObject.put("user_id", "" + Common.getPrefValue(activity, Constants.USER_ID));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(context, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.isPushFound = false;

                clearNotification(context);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {

            }
        }).execute();
    }

    public static void clearNotification(Context context) {
        // Clear all notification
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    private void register() {
        //{"username":"ttlock_7020424747"}
        //@Field("clientId") String clientId, @Field("clientSecret") String accessToken, @Field("username") String gatewayNetMac, @Field("password") long date
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.registerUser(Constants.client_id, Constants.client_secret, "7020424747","123456789",System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                ActivityHelper.dismissProgressDialog();
                String json = response.body();
                ChatApplication.logDisplay("response tt is "+json);
                if (!TextUtils.isEmpty(json)) {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();

                ChatApplication.logDisplay("response tt is error");
            }
        });
    }
}
