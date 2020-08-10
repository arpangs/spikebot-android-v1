package com.spike.bot.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akhilpatoliya.floating_text_button.FloatingTextButton;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.Beacon.BeaconDetailActivity;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.AddDevice.AddDeviceTypeListActivity;
import com.spike.bot.activity.Camera.CameraDeviceLogActivity;
import com.spike.bot.activity.Camera.CameraEdit;
import com.spike.bot.activity.Camera.CameraGridActivity;
import com.spike.bot.activity.Camera.CameraNotificationActivity;
import com.spike.bot.activity.Camera.CameraPlayBack;
import com.spike.bot.activity.Curtain.CurtainActivity;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.DeviceLogRoomActivity;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.RoomDetailActivity;
import com.spike.bot.activity.RoomEditActivity_v2;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.Sensor.DoorSensorInfoActivity;
import com.spike.bot.activity.Sensor.GasSensorActivity;
import com.spike.bot.activity.Sensor.MultiSensorActivity;
import com.spike.bot.activity.Sensor.WaterSensorActivity;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.activity.TTLock.YaleLockInfoActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.adapter.SectionedExpandableGridAdapter;
import com.spike.bot.adapter.SectionedExpandableLayoutHelper;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.onCallback;
import com.spike.bot.listener.DeviceListRefreshView;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.listener.SocketListener;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraCounterModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.User;

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

public class DashBoardFragment extends Fragment implements ItemClickListener, SectionedExpandableGridAdapter.CameraClickListener,
        SectionedExpandableGridAdapter.JetsonClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnSmoothScrollList, TempClickListener, DeviceListRefreshView {

    public static int showDialog = 1;
    public static Boolean isRefredCheck = true, isResumeConnect = false;
    public static DeviceVO tmpDeviceV0;
    public static int tmpPosition = -1;
    public static Activity activity;
    public static int SENSOR_TYPE_DOOR = 1, SENSOR_TYPE_TEMP = 2, SENSOR_TYPE_IR = 3, SENSOR_REPEATAR = 10, Curtain = 6;
    public static OnHeadlineSelectedListener mCallback;
    public static FloatingTextButton addDeviceFab;
    public View view;
    public LinearLayout linear_retry;
    public RelativeLayout txt_empty_schedule;
    public CloudAdapter.CloudClickListener cloudClickListener;
    public SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    public boolean isCloudConnected = false;
    ProgressDialog m_progressDialog;
    Button button_retry;
    Toolbar toolbar;
    ResponseErrorCode responseErrorCode;
    LoginPIEvent loginPIEvent;
    SocketListener socketListener;
    Gson gsonType;
    ArrayList<CameraVO> cameraList = new ArrayList<CameraVO>();
    ArrayList<CameraVO> jetsonCameraList = new ArrayList<CameraVO>();
    ArrayList<CameraVO> jetsonlist = new ArrayList<>();
    List<CameraCounterModel.Data.CameraCounterList> cameracounterlist;
    CameraCounterModel.Data totalcount = new CameraCounterModel.Data();
    Dialog mPIRModedialog;
    boolean isPIREdit;
    String pir_device_on_off_timer = "30";
    private Boolean isSocketConnected = true;
    private RecyclerView mMessagesView;
    private ImageView empty_add_image;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Socket mSocket, cloudsocket;
    private NestedScrollView main_scroll;
    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private String userId = "0", webUrl = "", cloudurl = "", camera_id = "", homecontrollerid = "", jetson_device_id = "";
    private int SIGN_IP_REQUEST_CODE = 204, countFlow = 0, all_notification_count = 0;
    /*for panel update*/
    private TextInputEditText edSensorName;
    private Spinner mDeviceTurnOnOffTimer;
    private Button btnPIRSave;
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isSocketConnected = false;
                }
            });
        }
    };
    /*change device status socket getting
    like devic eon /  off , value change
    * */
   /* private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
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
                            //{"device_id":"1571671238168_3FH8phZUCh","device_type":"switch","device_status":0,"device_sub_status":0}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("status update panel device " + object.toString());
                            String device_type = object.getString("device_type");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            String device_sub_status = object.getString("device_sub_status");

                            sectionedExpandableLayoutHelper.updateDeviceItem(device_type, device_id, device_status, device_sub_status);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            });
        }
    };*/
    /**
     * Update panel status
     * on, off
     */
    /*private Emitter.Listener panelStatus = new Emitter.Listener() {
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

                            ChatApplication.logDisplay("status update panel " + object.toString());

                            String panel_id = object.optString("panel_id");
                            String panelstatusValue = object.getString("panel_status");

                            sectionedExpandableLayoutHelper.updatePanel(panel_id, panelstatusValue, "");

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    };*/
    /**
     * Update Room status
     * on , off
     */
    private Emitter.Listener roomStatus = new Emitter.Listener() {
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
                            String room_id = object.getString("room_id");
                            String roomupdateValue = object.getString("room_status");

                            sectionedExpandableLayoutHelper.updateRoom(room_id, roomupdateValue);

                            ChatApplication.logDisplay("status update room " + object.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    /* door sensor open / close
     * sensor temp , door , ir remote */
   /* private Emitter.Listener changeModuleStatus = new Emitter.Listener() {
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
                            //{"module_id":"1573121550836_Prw60ijgq","is_active":"y"}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("changeModuleStatus is " + object);
                            String module_id = object.optString("module_id");
                            String is_active = object.optString("is_active");

                            sectionedExpandableLayoutHelper.updateModuleActiveItem(module_id, is_active);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };*/

    /* uprecount like notification icon upper showing count*/
   /* private Emitter.Listener unReadCount = new Emitter.Listener() {
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
                            // user_id, device_id, counter
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("unReadCount is " + object);
                            String device_id = object.getString("device_id");
                            String counter = object.getString("counter");
                            String user_id = object.getString("user_id");

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(activity, Constants.USER_ID))) {
                                sectionedExpandableLayoutHelper.updateBadgeCount(device_id, counter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };*/
    /*update alert socket to display badgecount */
    private Emitter.Listener updateRoomAlertCounter = new Emitter.Listener() {
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

                            String unseen_alert_counter = object.optString("unseen_alert_counter");
                            String room_id = object.optString("room_id");
                            String user_id = object.optString("user_id");

                            if (Common.getPrefValue(activity, Constants.USER_ID).equalsIgnoreCase(user_id)) {
                                sectionedExpandableLayoutHelper.updateBadgeCountnew(room_id, unseen_alert_counter);
                                ChatApplication.logDisplay("update room alert socket is " + object.toString());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };
    private Emitter.Listener updateCameraCounter = new Emitter.Listener() {
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
                            String user_id = object.getString("user_id");
                            String camera_id = object.getString("user_id");
                            int unseen_log = object.getInt("unseen_log");
                            ChatApplication.logDisplay("camera counter socket" + object.toString());
                            getCameraBadgeCount();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };
    private Emitter.Listener beaconInRoomCountUpdate = new Emitter.Listener() {
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
                            //  String user_id = object.getString("user_id");
                            String room_id = object.getString("room_id");
                            String device_count = object.getString("device_count");
                            ChatApplication.logDisplay("radar counter socket" + object.toString());
                            //if(!device_count.equals("0")) {
                            sectionedExpandableLayoutHelper.updateBeaconBadgeCount(room_id, device_count);
                            //  }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };
    /*private Emitter.Listener changeIrBlasterTemperature = new Emitter.Listener() {
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
                            //  String user_id = object.getString("user_id");
                            String ir_blaster_id = object.getString("ir_blaster_id");
                            String temperature = object.getString("temperature");
                            ChatApplication.logDisplay("ir remote temp socket" + object.toString());
                            sectionedExpandableLayoutHelper.updateTempCount(ir_blaster_id, temperature);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };*/
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isSocketConnected) {
                            isSocketConnected = true;
                        }
                        Constants.startUrlset();
                        mSocket.emit("socketconnection", "android == startconnect  " + mSocket.id());

                        socketOn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    /*update child socket like rmeove , add Or delete room */
    private Emitter.Listener updateChildUser = new Emitter.Listener() {
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

                            String user_id = object.optString("user_id");
                            ChatApplication.logDisplay("update socket is " + object.toString());
                            if (Common.getPrefValue(activity, Constants.USER_ID).equalsIgnoreCase(user_id)) {
                                showDialog = 1;
                                getDeviceList(showDialog);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    public DashBoardFragment() {
        super();
    }

    public static DashBoardFragment newInstance() {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //clear badge count
    public static void getBadgeClear(final Context context) {


        if (!Constants.checkLoginAccountCount(context)) {
            return;
        }
        String url = ChatApplication.url + Constants.updateBadgeCount;

        /*dev arpan update on 24 june 2020*/

       /* JSONObject jsonObject = new JSONObject();
        try {
            if (activity == null) {
                jsonObject.put("user_id", "");
            } else {
                jsonObject.put("user_id", "" + Common.getPrefValue(activity, Constants.USER_ID));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        ChatApplication.logDisplay("url is " + url);
       /* new GetJsonTask(context, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.isPushFound = false;

                clearNotification(context);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {

            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetBadgeClear(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ChatApplication.isPushFound = false;

                clearNotification(activity); // change arpan on 26 june 2020
            }

            @Override
            public void onData_FailureResponse() {

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
    }

    /*clear notidication count*/
    public static void clearNotification(Context context) {
        // Clear all notification
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
            responseErrorCode = (ResponseErrorCode) activity;
            loginPIEvent = (LoginPIEvent) activity;
            socketListener = (SocketListener) activity;
            cloudClickListener = (CloudAdapter.CloudClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }

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

    @Override
    public void onRefresh() {
        isRefredCheck = true;
        ChatApplication.isCallDeviceList = true;
        swipeRefreshLayout.setRefreshing(true);
        getDeviceList(1);
        sectionedExpandableLayoutHelper.setClickable(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(activity, new Crashlytics());
        view = inflater.inflate(R.layout.fragment_main, container, false);


        //for callback network change
        ((Main2Activity) activity).setCallBack(this);

        toolbar = activity.findViewById(R.id.toolbar);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        mMessagesView = view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(activity));
        main_scroll = view.findViewById(R.id.main_scroll);
        txt_empty_schedule = view.findViewById(R.id.txt_empty_schedule);
        empty_add_image = view.findViewById(R.id.empty_add_image);
        button_retry = view.findViewById(R.id.button_retry);
        linear_retry = view.findViewById(R.id.linear_retry);
        addDeviceFab = view.findViewById(R.id.action_fab);

        if (Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            addDeviceFab.setVisibility(View.GONE);
        } else {
            addDeviceFab.setVisibility(View.VISIBLE);
        }

        txt_empty_schedule.setVisibility(View.GONE);
        ChatApplication.isRefreshDashBoard = true;

        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, DashBoardFragment.this, DashBoardFragment.this, DashBoardFragment.this, Constants.SWITCH_NUMBER);
        sectionedExpandableLayoutHelper.setCameraClick(DashBoardFragment.this);
        sectionedExpandableLayoutHelper.setjetsonClick(DashBoardFragment.this);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.LIVE + Common.getPrefValue(activity, Constants.USER_ID));
        ChatApplication.logDisplay(Constants.LIVE + Common.getPrefValue(activity, Constants.USER_ID));

        clickListerFolatingBtn();
        return view;
    }

    private void clickListerFolatingBtn() {

        if (!Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE).equals("1")) {
            addDeviceFab.setVisibility(View.GONE);
        }
        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomRoom();
            }
        });

        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAllView();
            }
        });

        addDeviceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDeviceTypeListActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();

        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }

        ((Main2Activity) activity).setCallBack(this);
        if (ChatApplication.isLocalFragmentResume) {
            ChatApplication.isLocalFragmentResume = false;
            getDeviceList(2);

        } else if (ChatApplication.isMainFragmentNeedResume) {
            ChatApplication.isMainFragmentNeedResume = false;
            onLoadFragment();
        } else if (!ChatApplication.isCallDeviceList && roomList.size() > 0) {
            if (sectionedExpandableLayoutHelper != null) {
                sectionedExpandableLayoutHelper.addSectionList(roomList);
                sectionedExpandableLayoutHelper.notifyDataSetChanged();
            }
            getDeviceList(15);
        } else {
            getDeviceList(15);
        }
    }

    /**
     * {@link SectionedExpandableGridAdapter#}
     *
     * @param position
     */
    @Override
    public void onPoisitionClick(final int position) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = ((LinearLayoutManager) mMessagesView.getLayoutManager());
                main_scroll.getParent().requestChildFocus(mMessagesView, mMessagesView);
                main_scroll.smoothScrollBy(50, 100);
            }
        });
    }

    @Override
    public void onPause() {
        Constants.startUrlset();
        if (mSocket != null) {
           // mSocket.off("changeDeviceStatus", changeDeviceStatus);
            mSocket.off("changeRoomStatus", roomStatus);
         //   mSocket.off("changePanelStatus", panelStatus);
          //  mSocket.off("changeModuleStatus", changeModuleStatus);
          //  mSocket.off("updateDeviceBadgeCounter", unReadCount);
            mSocket.off("updateChildUser", updateChildUser);
            mSocket.off("updateRoomAlertCounter", updateRoomAlertCounter);
            mSocket.off("beaconInRoomCountUpdate", beaconInRoomCountUpdate);
         //   mSocket.off("changeIrBlasterTemperature", changeIrBlasterTemperature);

        }

        if (cloudsocket != null) {
            cloudsocket.off("camera-" + Common.getPrefValue(activity, Constants.USER_ID), updateCameraCounter);
        }
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onLoadFragment();
        }
    }

    // Room buttons click
    @Override
    public void itemClicked(final RoomVO roomVO, String action) {
        if (action.equalsIgnoreCase("showGridCamera")) {
            Intent intent = new Intent(activity, CameraGridActivity.class);
            intent.putExtra("isshowGridCamera", true);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("refreshCamera")) {
            getDeviceList(1);

        } else if (action.equalsIgnoreCase("onoffclick")) {
            //on off room
            if (action.equalsIgnoreCase("onOffclick")) {
                roomPanelOnOff(null, roomVO, roomVO.getRoomId(), "", roomVO.getRoom_status(), 1);
            }
        } else if (action.equalsIgnoreCase("editclick_false")) {
          //  showBottomSheetDialog(roomVO);

        } else if (action.equalsIgnoreCase("cameraopen")) {

            Intent intent = new Intent(activity, CameraPlayBack.class);
            if (roomVO != null) {
                intent.putExtra("room", roomVO);
            } else {
                intent.putExtra("room", new RoomVO());
            }
            intent.putExtra("cameraList", cameraList);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("editclick_true")) {
            Intent intent = new Intent(activity, CameraEdit.class);
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("isEditable", false);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("deleteRoom")) {
            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    //deleteRoom(roomVO);
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(activity.getFragmentManager(), "dialog");

        } else if (action.equalsIgnoreCase("icnLog")) {
            Intent intent = new Intent(activity, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", roomVO.getRoomId());
            intent.putExtra("activity_type", roomVO.getType());
            intent.putExtra("isCheckActivity", "room");
            intent.putExtra("isRoomName", "" + roomVO.getRoomName());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("icnSch")) {
            Intent intent = new Intent(activity, ScheduleListActivity.class);
            intent.putExtra("moodId3", roomVO.getRoomId());
            intent.putExtra("roomId", roomVO.getRoomId());
            intent.putExtra("roomName", roomVO.getRoomName());
            intent.putExtra("selection", 1);
            intent.putExtra("isMoodAdapter", true);
            intent.putExtra("isActivityType", "1");
            intent.putExtra("isRoomMainFm", "room");
            startActivity(intent);
        } else if (action.equalsIgnoreCase("icnSensorLog")) {
            Intent intent = new Intent(activity, DeviceLogRoomActivity.class);
            intent.putExtra("ROOM_ID", roomVO.getRoomId());
            intent.putExtra("IS_SENSOR", "" + true);
            intent.putExtra("room_name", "" + roomVO.getRoomName());
            intent.putExtra("isNotification", "roomSensorUnreadLogs");
            startActivity(intent);
        } else if (action.equalsIgnoreCase("cameraDevice")) {
            Intent intent = new Intent(activity, CameraDeviceLogActivity.class);
            //     ChatApplication.logDisplay("Camera Id" + " "+ camera_id);
            //   intent.putExtra("cameraId", "" + camera_id);
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("cameraNotification")) {
            Intent intent = new Intent(activity, CameraNotificationActivity.class);
            intent.putExtra("Cameraunseenlog", "Cameraunseenlog");
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("icnBeacon")) {
            Intent intent = new Intent(activity, BeaconDetailActivity.class);
            intent.putExtra("room_id", roomVO.getRoomId());
            startActivity(intent);
        }  else if (action.equalsIgnoreCase("camera_click")) {
            Intent intent = new Intent(activity, RoomDetailActivity.class);
            intent.putExtra("camera_id", roomVO.getRoomId());
            intent.putExtra("room_name", roomVO.getRoomName());
            startActivity(intent);

        }  else if (action.equalsIgnoreCase("jetson_click")) {
            Intent intent = new Intent(activity, RoomDetailActivity.class);
            intent.putExtra("jetson_id", roomVO.getRoomId());
            intent.putExtra("room_name", roomVO.getRoomName());
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);

        } else if(action.equalsIgnoreCase("room_click")){
            Intent intent = new Intent(activity, RoomDetailActivity.class);
            intent.putExtra("room_id", roomVO.getRoomId());
            intent.putExtra("room_name", roomVO.getRoomName());
            intent.putExtra("room", roomVO);
            ChatApplication.logDisplay("room id is callingclick fire !! " + roomVO.getRoomId());
            startActivity(intent);
        }
    }

    // Panel buttons click
    @Override
    public void itemClicked(PanelVO panelVO, String action) {
        if (action.equalsIgnoreCase("onOffclick")) {
            roomPanelOnOff(panelVO, null, "", panelVO.getPanelId(), panelVO.getPanel_status(), 2);
        }
    }

    /**
     * @param item
     * @param action
     */
    @Override
    public void itemClicked(final DeviceVO item, String action, int position) {
        if (action.equalsIgnoreCase("itemclick")) {
            if (item.getIsActive() != -1) {
                deviceOnOff(item, position);
            }

        } else if (action.equalsIgnoreCase("philipsClick")) {
            deviceOnOff(item, position);
        } else if (action.equalsIgnoreCase("curtain")) {
            if (item.getIsActive() == -1) {
                return;
            }
            Intent intent = new Intent(activity, CurtainActivity.class);
            intent.putExtra("curtain_id", item.getDeviceId());
            intent.putExtra("module_id", item.getModuleId());
            intent.putExtra("curtain_name", item.getDeviceName());
            intent.putExtra("panel_id", item.getPanel_id());
            intent.putExtra("curtain_status", "" + item.getDeviceStatus());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("scheduleclick")) {
            Intent intent = new Intent(activity, ScheduleActivity.class);
            intent.putExtra("item", item);
            intent.putExtra("schedule", true);
            startActivity(intent);
        }
        if (action.equalsIgnoreCase("longclick")) {
            try {
                int getDeviceSpecificValue = 0;
                ChatApplication.logDisplay("fan speed is" + item.getDevice_sub_status());
                if (!TextUtils.isEmpty(item.getDevice_sub_status())) {
                    getDeviceSpecificValue = Integer.parseInt(item.getDevice_sub_status());
                }
                FanDialog fanDialog = new FanDialog(activity, item.getDeviceId(), item.getDeviceName(), getDeviceSpecificValue, new onCallback() {
                    @Override
                    public void onSuccess(int status) {
                        sectionedExpandableLayoutHelper.updateFanDevice(item.getDeviceId(), status);
                    }
                });
                fanDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

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

            if (item.getSensor_type().equalsIgnoreCase("temp_sensor")) {
                Intent intent = new Intent(activity, MultiSensorActivity.class);
                intent.putExtra("temp_sensor_name", item.getSensor_name());
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getDeviceId());

                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("gas_sensor")) {

                Intent intent = new Intent(activity, GasSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("device_id", item.getDeviceId());
                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("door_sensor")) {
                ChatApplication.logDisplay("door call is intent " + mSocket.connected());
                Intent intent = new Intent(activity, DoorSensorInfoActivity.class);
                intent.putExtra("door_sensor_id", item.getDeviceId());
                intent.putExtra("door_room_name", item.getRoomName());
                intent.putExtra("door_room_id", item.getRoomId());
                intent.putExtra("door_unread_count", item.getIs_unread());
                intent.putExtra("door_module_id", item.getModuleId());
                intent.putExtra("door_subtype", "" + item.getDoor_subtype());
                startActivity(intent);
            } else if (item.getSensor_type().equalsIgnoreCase("lock")) {
                if (item.getIsActive() == -1) {
                    return;
                } else {
                    ChatApplication.logDisplay("yale lock call is intent " + mSocket.connected());
                    deviceOnOff(item, position);
                }
            } else if (item.getSensor_type().equalsIgnoreCase("water_detector")) {
                ChatApplication.logDisplay("water is intent " + mSocket.connected());
                Intent intent = new Intent(activity, WaterSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("device_id", item.getDeviceId());
                startActivity(intent);
            }
        } else if (action.equalsIgnoreCase("heavyloadlongClick")) {
            Intent intent = new Intent(activity, HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomName", item.getRoomName());
            intent.putExtra("getModuleId", item.getModuleId());
            intent.putExtra("device_id", item.getDeviceId());
            startActivity(intent);

        } else if (action.equalsIgnoreCase("philipslongClick")) {
            //on-off remote
            if (item.getDeviceStatus() == 1) {
                Intent intent = new Intent(activity, SmartColorPickerActivity.class);
                intent.putExtra("roomDeviceId", item.getRoomDeviceId());
                intent.putExtra("getOriginal_room_device_id", item.getOriginal_room_device_id());
                startActivity(intent);
            } else {
                ChatApplication.showToast(activity, "Please device on");
            }

        } else if (action.equalsIgnoreCase("isIRSensorClick")) {
            //on-off remote
            sendRemoteCommand(item, "isIRSensorClick");
        } else if (action.equalsIgnoreCase("doorOpenClose") && item.getDoor_subtype() != 1) {
           // callDoorRemotly(item);
        } else if (action.equalsIgnoreCase("isIRSensorLongClick")) {

            Intent intent = new Intent(activity, IRBlasterRemote.class);
            intent.putExtra("IR_MODULE_ID", item.getModuleId());
            intent.putExtra("IR_BLASTER_ID", item.getDeviceId());
            intent.putExtra("ROOM_DEVICE_ID", item.getRoomDeviceId());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("isLockLongClick")) {

            ChatApplication.logDisplay("yale door call is intent " + mSocket.connected());
            Intent intent = new Intent(activity, YaleLockInfoActivity.class);
            intent.putExtra("door_sensor_id", item.getDeviceId());
            intent.putExtra("door_room_name", item.getRoomName());
            intent.putExtra("door_room_id", item.getRoomId());
            intent.putExtra("door_unread_count", item.getIs_unread());
            intent.putExtra("door_module_id", item.getModuleId());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("isPIRLongClick")) {
           /* if (item.getIsActive() != -1)
                dialogNewPassword(item);*/
        } else if (action.equalsIgnoreCase("pir")) {
            deviceOnOff(item, position);
        }

    }

    /**
     * @param item
     * @param action
     */

    @Override
    public void itemClicked(CameraVO item, String action) {
        if (action.equalsIgnoreCase("editcamera")) {
           /* Intent intent = new Intent(activity, CameraEdit.class);
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("cameraSelcet", item);
            intent.putExtra("isEditable", false);
            startActivity(intent);*/

            showBottomSheetDialogCamera(item);
        } else if (action.equalsIgnoreCase("cameraLog")) {
            Intent intent = new Intent(activity, CameraDeviceLogActivity.class);
            intent.putExtra("cameralog", "cameralog");
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("cameraId", "" + item.getCamera_id());
            intent.putExtra("homecontrollerId", item.getHomeControllerDeviceId());
            startActivity(intent);
        } else {
            callCameraToken(item, action);
        }
    }

    @Override
    public void jetsonClicked(PanelVO panelVO, String action, String jetsonid, String camera_id) {
        if (action.equalsIgnoreCase("showGridJetsonCamera")) {
            Intent intent = new Intent(activity, CameraGridActivity.class);
            intent.putExtra("jetson_device_id", jetsonid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("jetsonlog")) {
            Intent intent = new Intent(activity, CameraDeviceLogActivity.class);
            intent.putExtra("isshowJestonCameraLog", true);
            intent.putExtra("jetson_device_id", jetsonid);
            intent.putExtra("cameraList", panelVO.getCameraList());
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("showjetsoncameraLog")) {
            Intent intent = new Intent(activity, CameraDeviceLogActivity.class);
            intent.putExtra("jetsoncameralog", "jetsoncameralog");
            intent.putExtra("jetson_device_id", jetsonid);
            intent.putExtra("cameraId", camera_id);
            //  intent.putExtra("cameraList", panelVO.getCameraList());
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("jetsoncameraNotification")) {
            Intent intent = new Intent(activity, CameraNotificationActivity.class);
            intent.putExtra("jetsoncameraNotification", true);
            intent.putExtra("jetson_device_id", jetsonid);
            intent.putExtra("cameraList", panelVO.getCameraList());
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("jetsoncameraopen")) {
            Intent intent = new Intent(activity, CameraPlayBack.class);
            intent.putExtra("cameraList", panelVO.getCameraList());
            intent.putExtra("jetson_device_id", jetsonid);
            ChatApplication.logDisplay("JETSON RECORDING OPEN" + " " + panelVO.getCameraList().size());
            startActivity(intent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getDeviceList(10);
        }
    }

    //network change
    @Override
    public void deviceRefreshView(int count) {
        if (count == 0) {
            errorViewClick();
        } else {
            getDeviceList(1);
        }
        ChatApplication.logDisplay("device refreshing mainfragment");
    }

    // view refresh
    @SuppressLint("RestrictedApi")
    private void refreshAllView() {
        if (!Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE).equals("1")) {
            addDeviceFab.setVisibility(View.GONE);
        } else {
            addDeviceFab.setVisibility(View.VISIBLE);
        }
        linear_retry.setVisibility(View.GONE);
        addDeviceFab.setVisibility(View.GONE);
        ((Main2Activity) activity).tabShow(true);
        ChatApplication.isCallDeviceList = true;
        getDeviceList(1);
    }

    /*getting error than */
    @SuppressLint("RestrictedApi")
    public void errorViewClick() {
        addDeviceFab.setVisibility(View.GONE);
        txt_empty_schedule.setVisibility(View.GONE);
        linear_retry.setVisibility(View.VISIBLE);
        ((Main2Activity) activity).tabShow(false);

    }

    public void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {


            if (!webUrl.startsWith("http")) {
                webUrl = "http://" + webUrl;
            } else {

            }

            ChatApplication.logDisplay("SOCKET WEB URL" + " " + webUrl);
            mSocket = app.getSocket();

            if (mSocket != null) {
                if (mSocket.connected() == false) {
                    mSocket = null;
                }
            }

            if (mSocket == null) {
                mSocket = app.openSocket(webUrl);
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.connect();
            }
            try {
                socketOn();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void startLiveSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (cloudsocket != null && cloudsocket.connected()) {
        } else {

            cloudurl = "https://live.spikebot.io:8443";
            cloudsocket = app.getCloudSocket();

            if (cloudsocket != null) {
                if (cloudsocket.connected() == false) {
                    cloudsocket = null;
                }
            }

            if (cloudsocket == null) {
                cloudsocket = app.openCloudSocket(cloudurl);
                cloudsocket.on(Socket.EVENT_CONNECT, onConnect);
                cloudsocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                cloudsocket.connect();
            }
            try {
                cloudsocket();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void socketOn() {
       // mSocket.on("changeDeviceStatus", changeDeviceStatus);  // ac on off
        mSocket.on("changeRoomStatus", roomStatus);
       // mSocket.on("changePanelStatus", panelStatus);
        //mSocket.on("changeModuleStatus", changeModuleStatus);
       // mSocket.on("updateDeviceBadgeCounter", unReadCount);
        mSocket.on("updateChildUser", updateChildUser);
        mSocket.on("updateRoomAlertCounter", updateRoomAlertCounter);
        mSocket.on("beaconInRoomCountUpdate", beaconInRoomCountUpdate);
       // mSocket.on("changeIrBlasterTemperature", changeIrBlasterTemperature);
    }

    private void cloudsocket() {
        cloudsocket.on("camera-" + Common.getPrefValue(activity, Constants.USER_ID), updateCameraCounter);
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
            isResumeConnect = false;
            getDeviceList(3);
        } else if (Common.isConnected() && ChatApplication.isRefreshDashBoard) {
            getDeviceList(4);
            ChatApplication.isRefreshDashBoard = true; //false
        } else {
            getDeviceList(5);
        }
    }

    /*device on off */
    private void deviceOnOff(final DeviceVO deviceVO, final int position) {
        callDeviceOnOffApi(deviceVO);

    }

    /**
     * offline device status updated
     *
     * @param deviceVO
     */
    private void updateDeviceOfflineMode(final DeviceVO deviceVO) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper.updateDeviceItem(deviceVO.getDeviceType(), String.valueOf(deviceVO.getDeviceId()), String.valueOf(deviceVO.getOldStatus()), deviceVO.getDevice_sub_status());
            }
        });
    }

    private void callDeviceOnOffApi(final DeviceVO deviceVO) {

        String url = "";
        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            url = ChatApplication.url + Constants.changeHueLightState;
        } else {
            url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;
        }


        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        ChatApplication.logDisplay("Device roomPanelOnOff obj " + url + " "); //+ obj.toString());

        SpikeBotApi.getInstance().CallDeviceOnOffApi(deviceVO, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("Device roomPanelOnOff obj result" + result.toString());

                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code != 200) {
                        updateDeviceOfflineMode(deviceVO);
                        ChatApplication.showToast(activity, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ChatApplication.logDisplay("Device roomPanelOnOff finally ");
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.logDisplay("Device roomPanelOnOff error ");
                updateDeviceOfflineMode(deviceVO);
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("Device roomPanelOnOff error " + error);
                updateDeviceOfflineMode(deviceVO);
            }
        });
    }

    /*panel on off */
    private void roomPanelOnOff(final PanelVO panelVO, final RoomVO roomVO, String roomId, String panelId, int panel_status, int type) {
        ChatApplication.logDisplay("roomPanelOnOff ");
        callPanelOnOffApi(roomId, panelId, panel_status, type);

    }

    //    private void callPanelOnOffApi(JSONObject obj, String roomId, String panelId, int panel_status,int type) {
    private void callPanelOnOffApi(String roomId, String panelId, int panel_status, int type) {


        String url = "";
        if (type == 1) {
            url = ChatApplication.url + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW;
        } else {
            url = ChatApplication.url + Constants.CHANGE_PANELSTATUS;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        ChatApplication.logDisplay("room is " + url + " ");

        SpikeBotApi.getInstance().CallPanelOnOffApi(roomId, panelId, panel_status, type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code != 200) {
                        ChatApplication.showToast(activity, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ChatApplication.logDisplay("roomPanelOnOff finally ");
                }
            }

            @Override
            public void onData_FailureResponse() {

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

    /**
     * Delete Room
     *
     * @param //roomVO
     */

  /*  private void deleteRoom(RoomVO roomVO) {


        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }
       *//* JSONObject object = new JSONObject();
        try {
            object.put("room_id", roomVO.getRoomId());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }*//*

//        ChatApplication.logDisplay("ob : " + object.toString());
        String url = webUrl + Constants.DELETE_ROOM;
        *//*new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(activity, message);
                        getDeviceList(8);
                    } else {
                        ChatApplication.showToast(activity, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }
        }).execute();*//*

        SpikeBotApi.getInstance().DeleteRoom(roomVO.getRoomId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    dismissProgressDialog();
                    ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(activity, message);
                        getDeviceList(8);
                    } else {
                        ChatApplication.showToast(activity, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure ");
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure ");
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }
        });

    }*/

   /* private void doorOpenCLose(DeviceVO item) {
        if (mSocket != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("lock_id", item.getLock_id());
                object.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
                object.put("door_sensor_status", item.getDoor_sensor_status().equals("1") ? 0 : 1);
                object.put("lock_status", item.getDoor_sensor_status().equals("1") ? 0 : 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatApplication.logDisplay("door json " + object);
            mSocket.emit("changeLockStatus", object);
        }
    }

    private void callDoorRemotly(DeviceVO item) {
        showProgressDialog(activity, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();

        Call<ResponseBody> call;
        if (item.getDoor_sensor_status().equalsIgnoreCase("1")) {
            call = apiService.unlockGatewayUse(Constants.client_id, Constants.access_token, item.getLock_id(), System.currentTimeMillis());
        } else {
            call = apiService.lockGatewayUse(Constants.client_id, Constants.access_token, item.getLock_id(), System.currentTimeMillis());
        }
        ChatApplication.logDisplay("door json call");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                if (response.code() == 200) {
                    doorOpenCLose(item);
                    ChatApplication.logDisplay("lock is " + response.body().toString());

                } else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });
    }*/

    /*send ir remote command */

    private void sendRemoteCommand(final DeviceVO item, String philipslongClick) {
        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().SendRemoteCommand(item, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("result is ir " + result);
                    if (code == 200) {
                        ChatApplication.isMoodFragmentNeedResume = true;
                    } else {
                        sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                ChatApplication.showToast(activity, item.getSensor_name() + " " + activity.getString(R.string.ir_error));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                ChatApplication.showToast(activity, item.getSensor_name() + " " + activity.getString(R.string.ir_error));
            }
        });

    }

    /*camera token */
    private void callCameraToken(final CameraVO item, final String action) {
        showProgressDialog(activity, " Please Wait...", false);
        String url = ChatApplication.url + Constants.getCameraToken + item.getCamera_id();

        ChatApplication.logDisplay("url is " + url);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().CallCameraToken(item.getCamera_id(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);

                    int code = 0;

                    code = result.getInt("code");

                    if (code == 200) {
                        dismissProgressDialog();
                        JSONObject dataObject = result.optJSONObject("data");

                        String camera_vpn_port = dataObject.optString("camera_vpn_port");
                        String camera_url = dataObject.optString("camera_url");

                        String url = "";

                        String camera_name = item.getCamera_name();


                        if (Main2Activity.isCloudConnected) {
                            url = Constants.CAMERA_DEEP + ":" + camera_vpn_port + "" + camera_url;
                        } else {
                            String tmpurl = ChatApplication.url + "" + camera_url;
                            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
                        }
                        ChatApplication.logDisplay("isCloudConnect : " + camera_vpn_port + " " + url);
                        Intent intent = new Intent(activity, CameraPlayer.class);
                        intent.putExtra("videoUrl", url);
                        intent.putExtra("name", camera_name);
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
                        dismissProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
            }
        });

    }

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

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
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
    }

    /**
     * Call api for save custom room
     *
     * @param roomName mRoomName
     * @param dialog   mDialog instance
     */
    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            return;
        }

        /*dev arpan update on 24 june 2020*/

        showProgressDialog(activity, "Searching Device attached ", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().SaveCustomRoom(roomName.getText().toString(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject();

                    dismissProgressDialog();
                    ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        ChatApplication.showToast(activity, message);
                        getDeviceList(9);
                    } else if (code == 301) {
                        ChatApplication.showToast(activity, message);
                    } else {
                        ChatApplication.showToast(activity, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    {
                        dismissProgressDialog();
                    }
                }

            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure ");
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }
        });

    }

    /*first check is wifi / mobile network
     * > is wifi > get ip address than call mac address api
     * > check reponse is getting reponse than check mac_address is same or not . is same means local
     * > another reponse 3  sec more is colud  */
    public void getMacAddress(String locaIp) {

        /*dev arpan change on 26 june 2020*/
        countFlow = 0;
        String url = ChatApplication.http + locaIp + Constants.getLocalMacAddress;

        ChatApplication.logDisplay("url is " + url);
        if (locaIp.contains("http://"))
            locaIp = locaIp.replace("http://", "");

        SpikeBotApi.getInstance().GetMacAddress(locaIp, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                countFlow = 1;

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("reponse is mac " + result.toString());
                    JSONObject object = new JSONObject(result.toString());
                    JSONObject jsonObject = object.optJSONObject("data");

                    if (jsonObject != null && countFlow != 2) {
                        if (jsonObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(activity))) {
                            callCloud(true);
                        } else {
                            callCloud(false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                if (countFlow != 2) {
                    countFlow = 1;
                    callCloud(false);
                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                if (countFlow != 2) {
                    countFlow = 1;
                    callCloud(false);
                }
            }
        });

        /*if geting not any reponse in 3 sec than cloud api call*/
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (countFlow == 0) {
                    countFlow = 2;
                    ChatApplication app = ChatApplication.getInstance();
                    app.closeSocket(webUrl);
                    app.closecloudSocket(cloudurl);

                    if (mSocket != null) {
                        mSocket.disconnect();
                        mSocket = null;
                    }

                    if (cloudsocket != null) {
                        cloudsocket.disconnect();
                        cloudsocket = null;
                    }
                    callCloud(false);
                }

            }

        };
        countDownTimer.start();


    }

    /* isflag is true means connect to local
     *   another cloud connect*/
    private void callCloud(boolean isflag) {
        ChatApplication.logDisplay("show is call 11  " + activity);
        if (isflag) {
            //local
            ((Main2Activity) activity).webUrl = ChatApplication.http + Constants.getuserIp(activity) + ":80";
//            ChatApplication.url = ChatApplication.http + Constants.getuserIp(activity);
            ChatApplication.url = Constants.getuserIp(activity);
            Constants.startUrlset();
            ((Main2Activity) activity).callSocket();
            ((Main2Activity) activity).invalidateToolbarCloudImage();

            if (ChatApplication.url.contains("http://"))
                ChatApplication.url = ChatApplication.url.replace("http://", "");


            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getDeviceLocal(0);
                }
            });


        } else {
            ChatApplication.logDisplay("get fragment " + activity);
            String cloud = Constants.getuserCloudIP(activity);
            ChatApplication.url = cloud;
            Constants.startUrlset();
            ((Main2Activity) activity).webUrl = cloud;
            ((Main2Activity) activity).callSocket();
            ((Main2Activity) activity).invalidateToolbarCloudImage();

            if (ChatApplication.url.contains("http://"))
                ChatApplication.url = ChatApplication.url.replace("http://", "");


            getDeviceCloud(0);
        }
    }

    /*cloud connect*/
    public void getDeviceCloud(int isShow) {


        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (isShow == 0) {
            if (ChatApplication.url.startsWith(Constants.startUrl)) {
                dismissProgressDialog();
                if (m_progressDialog != null && m_progressDialog.isShowing()) {
                    m_progressDialog.setMessage("Connecting to cloud...");
                } else {
                    showProgressDialog(activity, "Connecting to cloud...", true);
                }
            } else {
                if (!m_progressDialog.isShowing()) {
                    dismissProgressDialog();
                    showProgressDialog(activity, "Please Wait...", true);
                }
            }
        }


        roomList.clear();
        mMessagesView.setClickable(false);
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }
        SpikeBotApi.getInstance().GetDeviceClould(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);

                    dismissProgressDialog();
                    ChatApplication.isCallDeviceList = false;
                   /* if (ChatApplication.isPushFound) {
                        getBadgeClear(activity);
                    }*/
                    addDeviceFab.setVisibility(View.VISIBLE);
                    mMessagesView.setClickable(true);
                    responseErrorCode.onSuccess();
                    swipeRefreshLayout.setRefreshing(false);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());


                    ((Main2Activity) activity).tabShow(true);
                    Main2Activity.isCloudConnected = true;
                    webUrl = ChatApplication.url;
                    startSocketConnection();
                    startLiveSocketConnection();
                    dismissProgressDialog();
                    if (result.getInt("code") == 200) {
                        Constants.socketIp = ChatApplication.url;
                        if (!Constants.socketIp.startsWith("http")) {  // dev arpan add this due to logout time exception issue or 443 add before url
                            Constants.socketIp = "http://" + Constants.socketIp;
                        }
                        hideAdapter(true);
                        JSONObject dataObject = result.getJSONObject("data");
                        ((Main2Activity) activity).getUserDialogClick(true);

                        all_notification_count = dataObject.optInt("total_unread_count");
                        ((Main2Activity) activity).setAllNotificationCount(all_notification_count);

                        if (gsonType == null) {
                            gsonType = new Gson();
                        }

                        JSONArray userListArray = dataObject.optJSONArray("userList");

                        /*userListArray =0 found than logout user  */
                        if (userListArray.length() == 0) {
                            if (!TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ID))) {
                                ((Main2Activity) activity).logoutCloudUser();
                            }

                            return;
                        }

                        JSONObject userObject = userListArray.optJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String is_active = userObject.optString("is_active");
                        homecontrollerid = userObject.optString("mac_address");

                        /*is_active ==0 than user logout means delete this user*/
                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) activity).logoutCloudUser();
                            return;
                        }


                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
                        }


                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {
                                }.getType();
                                userList1 = gsonType.fromJson(jsonTextTemp1, type);
                            }

                           /* for (User user : userList1) {
                                if (user.isActive()) {
                                    *//*if user password changed than logout *//*
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
                                        ChatApplication.showToast(activity, "Password has been changed!");
                                        ((Main2Activity) activity).logoutCloudUser();
                                    }

                                }
                            }*/
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

                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        /* user name*/
                        mCallback.onArticleSelected("" + userFirstName);


                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);

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
                        ((Main2Activity) activity).invalidateToolbarCloudImage();

                        JSONArray jetsonList = dataObject.optJSONArray("jetsonList");

                        if (jetsonList != null && jetsonList.length() > 0) {

                            for (int i = 0; i < jetsonList.length(); i++) {

                                JSONObject object = jetsonList.optJSONObject(i);
                                jetsonlist = JsonHelper.parseCameraArray(object.optJSONArray("cameraList"));

                                RoomVO section1 = new RoomVO();
                                section1.setRoomName(object.optString("jetson_name"));
                                section1.setRoomId(object.optString("jetson_id"));


                                ArrayList<PanelVO> panelList1 = new ArrayList<>();
                                PanelVO panel1 = new PanelVO();
                                panel1.setType("JETSON-");
                                panel1.setPanelName("Devices");
                                panel1.setCameraList(jetsonlist);

                                panelList1.add(panel1);

                                section1.setPanelList(panelList1);
                                roomList.add(section1);
                            }

                        }

                        sectionedExpandableLayoutHelper.addSectionList(roomList);
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                        ((Main2Activity) activity).changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    showDialog = 0;
                    if (roomList != null && roomList.size() > 0) {
                        CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
                    }
                    mMessagesView.setClickable(true);
                    swipeRefreshLayout.setRefreshing(false);
                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }

                    getCameraBadgeCount();
                    setUserTypeValue();
                    dismissProgressDialog();

                }

            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
//set custom homer controller not found error message on dashboard
//                if (reCode == 503 || reCode == 404) {
//                    responseErrorCode.onErrorCode(reCode);
//                }
//                ChatApplication.logDisplay("show is call could error" + error + " " + reCode);
//                if (!TextUtils.isEmpty(error)) {
                ((Main2Activity) activity).tabShow(false);
//                    ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
                sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, DashBoardFragment.this, DashBoardFragment.this, DashBoardFragment.this, Constants.SWITCH_NUMBER);
                sectionedExpandableLayoutHelper.setCameraClick(DashBoardFragment.this);
                sectionedExpandableLayoutHelper.setjetsonClick(DashBoardFragment.this);
                sectionedExpandableLayoutHelper.setClickable(true);
                sectionedExpandableLayoutHelper.notifyDataSetChanged();

                ChatApplication.isCallDeviceList = true;
                errorViewClick();
//                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

                swipeRefreshLayout.setRefreshing(false);
                addDeviceFab.setVisibility(View.VISIBLE);
                dismissProgressDialog();
                //set custom homer controller not found error message on dashboard
//                if (reCode == 503 || reCode == 404) {
//                    responseErrorCode.onErrorCode(reCode);
//                }
//                ChatApplication.logDisplay("show is call could error" + error + " " + reCode);
                if (!TextUtils.isEmpty(error)) {
                    ((Main2Activity) activity).tabShow(false);
//                    ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
                    sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, DashBoardFragment.this, DashBoardFragment.this, DashBoardFragment.this, Constants.SWITCH_NUMBER);
                    sectionedExpandableLayoutHelper.setCameraClick(DashBoardFragment.this);
                    sectionedExpandableLayoutHelper.setjetsonClick(DashBoardFragment.this);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    sectionedExpandableLayoutHelper.notifyDataSetChanged();

                    ChatApplication.isCallDeviceList = true;
                    errorViewClick();
                }
            }
        });


    }

    /*local connect*/
    public void getDeviceLocal(int isShow) {


        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (isShow == 0) {
            if (m_progressDialog != null && !m_progressDialog.isShowing()) {
                dismissProgressDialog();
                showProgressDialog(activity, "Please Wait...", true);
            }
        }

        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView = view.findViewById(R.id.messages);
        }

        mMessagesView.setClickable(false);
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().GetDeviceLocal(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);

                    dismissProgressDialog();
                    ChatApplication.isCallDeviceList = false;
                  /*  if (ChatApplication.isPushFound) {
                        getBadgeClear(activity);
                    }*/
                    addDeviceFab.setVisibility(View.VISIBLE);
                    mMessagesView.setClickable(true);
                    responseErrorCode.onSuccess();
                    swipeRefreshLayout.setRefreshing(false);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    ChatApplication.logDisplay("getDeviceList onSuccess local " + result.toString());


                    Main2Activity.isCloudConnected = false;
                    ((Main2Activity) activity).tabShow(true);
                    webUrl = ChatApplication.url;
                    startSocketConnection();
                    startLiveSocketConnection();
                    if (result.getInt("code") == 200) {
                        if (gsonType == null) {
                            gsonType = new Gson();
                        }
                        ((Main2Activity) activity).tabShow(true);
                        Constants.socketIp = ChatApplication.url;
                        if (!Constants.socketIp.startsWith("http")) {  // dev arpan add this due to logout time exception issue or 443 add before url
                            Constants.socketIp = "http://" + Constants.socketIp;
                        }
                        hideAdapter(true);
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);

                        sectionedExpandableLayoutHelper.clearData();

                        JSONObject dataObject = result.optJSONObject("data");
                        ((Main2Activity) activity).getUserDialogClick(true);

                        all_notification_count = dataObject.optInt("total_unread_count");
                        ((Main2Activity) activity).setAllNotificationCount(all_notification_count);

                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {
                            if (!TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ID))) {
                                ((Main2Activity) activity).logoutCloudUser();
                            }
                            return;
                        }

                        JSONObject userObject = userListArray.optJSONObject(0);
                        String userId = userObject.optString("user_id");
                        String userFirstName = userObject.optString("first_name");
                        String camera_key = userObject.optString("camera_key");
                        String is_active = userObject.optString("is_active");
                        homecontrollerid = userObject.optString("mac_address");


                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) activity).logoutCloudUser();
                            return;
                        }

                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.optString("user_password");
                        }

                        /**
                         * If Network connected on cloud then check user password is changed or not
                         * if changed then logout user
                         */
                        Gson gson = new Gson();
                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = "";
                            try {
                                jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {
                                }.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type);
                            }

                            /*for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
                                        ChatApplication.showToast(activity, "Password has been changed!");
                                        ((Main2Activity) activity).logoutCloudUser();
                                    }

                                }
                            }*/

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

                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        Common.savePrefValue(ChatApplication.getInstance(), Constants.USER_PASSWORD, userPassword);


                        /**
                         * <h1>for set ToolBar title in MainActivity2</h1>
                         * {@link Main2Activity#toolbarTitle}
                         * @see Main2Activity#toolbar
                         */
                        mCallback.onArticleSelected("" + userFirstName);

                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);
                        sectionedExpandableLayoutHelper.addSectionList(roomList);


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
                            section.setIs_unread(cameraList.get(0).getTotal_unread());

                            roomList.add(section);


                            /*camera device counting*/
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomId().equalsIgnoreCase("Camera")) {
                                    roomList.get(i).setDevice_count("" + roomList.get(i).getPanelList().get(0).getCameraList().size());

                                }
                            }
                        }

                        //for jetson
                        JSONArray jetsonList = dataObject.optJSONArray("jetsonList");
                        JSONObject object = new JSONObject();
                        if (jetsonList != null && jetsonList.length() > 0) {

                            for (int i = 0; i < jetsonList.length(); i++) {

                                object = jetsonList.optJSONObject(i);

                                ArrayList<CameraVO> jetsonlist1 = JsonHelper.parseCameraArray(object.optJSONArray("cameraList"));


                                RoomVO section1 = new RoomVO();
                                section1.setRoomName(object.optString("jetson_name"));
                                section1.setRoomId(object.optString("jetson_id"));


                                ArrayList<PanelVO> panelList1 = new ArrayList<>();
                                PanelVO panel1 = new PanelVO();
                                panel1.setType("JETSON-");
                                panel1.setPanelName("Devices");
                                panel1.setCameraList(jetsonlist1);
                                panelList1.add(panel1);

                                section1.setPanelList(panelList1);
                                section1.setIs_unread("0");
                                roomList.add(section1);
                            }

                            /*camera device counting*/
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomId().startsWith("JETSON-")) {
                                    roomList.get(i).setDevice_count("" + roomList.get(i).getPanelList().get(0).getCameraList().size());
                                }
                            }


                        }
                        sectionedExpandableLayoutHelper.addSectionList(roomList);
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                        ((Main2Activity) activity).invalidateToolbarCloudImage();

                        ((Main2Activity) activity).changestatus();
                        if (roomArray.length() == 0) {
                            mMessagesView.setVisibility(View.GONE);
                            txt_empty_schedule.setVisibility(View.VISIBLE);
                        } else {
                            mMessagesView.setVisibility(View.VISIBLE);
                            txt_empty_schedule.setVisibility(View.GONE);
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay("json Exception==========================" + e.getMessage());
                } finally {
                    dismissProgressDialog();
                    showDialog = 0;
                    if (roomList != null && roomList.size() > 0) {  // dev arpan add  roomList.size() > 0 condition on 26 june 2020 due to exception
                        if (roomList.get(roomList.size() - 1).getPanelList() != null)  // dev arpan add this condition on 26 june 2020 due to exception
                            if (roomList.get(roomList.size() - 1).getPanelList() != null && roomList.get(roomList.size() - 1).getPanelList().size() > 0) {
                                CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
                            } else {
                                ChatApplication.logDisplay("Room or panel list not found");
                            }
                    }
                    mMessagesView.setClickable(true);
                    swipeRefreshLayout.setRefreshing(false);
                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                    getCameraBadgeCount();
                    setUserTypeValue();
                }


            }

            @Override
            public void onData_FailureResponse() {

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

                swipeRefreshLayout.setRefreshing(false);
                ChatApplication.logDisplay("show is call local error" + error);
                ChatApplication.logDisplay("On Failure==========================" + " " + error);
                addDeviceFab.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(error)) {
                    ChatApplication.logDisplay("show is call local error22 " + error);
                    sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, DashBoardFragment.this, DashBoardFragment.this, DashBoardFragment.this, Constants.SWITCH_NUMBER);
                    sectionedExpandableLayoutHelper.setCameraClick(DashBoardFragment.this);
                    sectionedExpandableLayoutHelper.setjetsonClick(DashBoardFragment.this);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    sectionedExpandableLayoutHelper.notifyDataSetChanged();

                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                    if (mSocket != null) {
                        mSocket.disconnect();
                    }
                    ((Main2Activity) activity).tabShow(false);
                    callCloud(false);
                }
            }
        });

    }

    /**
     * Get camera badge count
     */
    private void getCameraBadgeCount() {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        SpikeBotApi.getInstance().GetCameraBadgeCount(homecontrollerid, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    ActivityHelper.dismissProgressDialog();

                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("Camera Badge onSuccess " + result.toString());

                        JSONObject object = result.optJSONObject("data");
                        JSONArray jsonArray = object.optJSONArray("camera_counter_list");

                        CameraCounterModel counterres = Common.jsonToPojo(result.toString(), CameraCounterModel.class);
                        cameracounterlist = counterres.getData().getCameraCounterList();

                        sectionedExpandableLayoutHelper.setCounterlist(cameracounterlist);
                        sectionedExpandableLayoutHelper.setCounterres(counterres.getData());
                        //sectionedExpandableLayoutHelper.getCameracounter();
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                        ChatApplication.logDisplay("total_camera_list " + cameracounterlist.size());
                        ChatApplication.logDisplay("total_camera_notification " + counterres.getData().getTotalCameraNotification());
                        dismissProgressDialog();
                    }
                } catch (Exception e) {
                    ChatApplication.logDisplay("total_camera_list Exception " + e.getMessage());
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    public void hideAdapter(boolean isflag) {
        mMessagesView.setVisibility(isflag == true ? View.VISIBLE : View.INVISIBLE);
    }

    /// all webservice call below. check local & cloud
    public void getDeviceList(final int checkmessgae) {
        if (activity == null) {
            return;
        }

        if (!Constants.checkLoginAccountCount(activity)) {
            ((Main2Activity) activity).showLogin();
            return;
        }

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (linear_retry.getVisibility() == View.VISIBLE) {
            linear_retry.setVisibility(View.GONE);
            ((Main2Activity) activity).tabShow(true);
            ChatApplication.isCallDeviceList = true;
        }

        if (showDialog == 1 || checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 10) {
            showProgressDialog(activity, " Please Wait...", true);
        }
        ChatApplication.logDisplay("show progress is " + showDialog);

//        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView = view.findViewById(R.id.messages);
        }
        mMessagesView.setClickable(false);
//        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }
        Constants.startUrlset();

        /*check is wifi or mobile network*/
        if (ChatApplication.isCallDeviceList) {
            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);
            app.closecloudSocket(cloudurl);
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
            }

            if (cloudsocket != null) {
                cloudsocket.disconnect();
                cloudsocket = null;
            }
            ((Main2Activity) activity).tabShow(false);
            hideAdapter(false);

            sectionedExpandableLayoutHelper.clearData();

            if (Common.isNetworkConnected(activity)) {
                ((Main2Activity) activity).tabShow(false);
                getMacAddress(Constants.getuserIp(activity));
            } else {
                if (ChatApplication.url.contains("http://"))
                    ChatApplication.url = ChatApplication.url.replace("http://", "");
                callCloud(false);
            }
        } else {
            ((Main2Activity) activity).invalidateToolbarCloudImage();
            if (ChatApplication.url.startsWith(Constants.startUrl)) {
                if (ChatApplication.url.contains("http://"))      // dev arpan add this condition on 27 june
                    ChatApplication.url = ChatApplication.url.replace("http://", "");
                getDeviceCloud(1);
            } else {
                if (ChatApplication.url.contains("http://")) // dev arpan add this condition on 27 june
                    ChatApplication.url = ChatApplication.url.replace("http://", "");

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceLocal(1);
                    }
                });
            }
        }
    }

    /*  user type admin ||  child */
    public void setUserTypeValue() {

        if (activity == null) {
            activity = DashBoardFragment.this.getActivity();  // added by dev arpan on 27 june 2020
        }
        try {

            if (!TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE))) {
                if (Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                    //  ((Main2Activity) activity).toolbarImage.setVisibility(View.VISIBLE);
                    ((Main2Activity) activity).toolbarTitle.setClickable(true);
                    /// ((Main2Activity) activity).toolbarImage.setClickable(true);
                    addDeviceFab.setVisibility(View.GONE);
                    ((Main2Activity) activity).toolbarTitle.setTextColor(getResources().getColor(R.color.username_green));
                } else {
                    ((Main2Activity) activity).toolbarTitle.setClickable(true);
                    // ((Main2Activity) activity).toolbarImage.setClickable(true);
                    //  ((Main2Activity) activity).toolbarImage.setVisibility(View.VISIBLE);
                    addDeviceFab.setVisibility(View.VISIBLE);
                    ((Main2Activity) activity).toolbarTitle.setTextColor(getResources().getColor(R.color.signupblack));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBottomSheetDialogCamera(CameraVO item) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + item.getCamera_name() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (item.getJetson_device_id().startsWith("JETSON-")) {
                    Intent intent = new Intent(activity, CameraEdit.class);
                    intent.putExtra("cameraList", cameraList);
                    intent.putExtra("cameraSelcet", item);
                    intent.putExtra("JETSON_ID", item.getJetson_device_id());
                    intent.putExtra("isjetsonedit", true);
                    intent.putExtra("isEditable", false);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, CameraEdit.class);
                    intent.putExtra("cameraList", cameraList);
                    intent.putExtra("cameraSelcet", item);
                    intent.putExtra("isEditable", false);
                    startActivity(intent);
                }
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteCamera(item.getCamera_id());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(activity.getFragmentManager(), "dialog");
            }
        });
    }

    /*public void showBottomSheetDialog(RoomVO roomVO) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + roomVO.getRoomName() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, RoomEditActivity_v2.class);
                intent.putExtra("room", roomVO);
                startActivity(intent);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteRoomAction(roomVO);
            }
        });
    }*/

    public void showProgressDialog(Context context, String message, boolean iscancle) {
        m_progressDialog = new ProgressDialog(activity);
        m_progressDialog.setMessage(message);
        m_progressDialog.setCanceledOnTouchOutside(true);
        m_progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (m_progressDialog != null) {
            ChatApplication.logDisplay("m_progressDialog is null not");
            m_progressDialog.cancel();
            m_progressDialog.dismiss();
        }
    }

    /**
     * DeleteCamera
     */

    private void deleteCamera(String camera_id) {


        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);

        SpikeBotApi.getInstance().DeleteCamera(camera_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

 /*   private void deleteRoomAction(final RoomVO room) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ? \nNote: All the devices and sensors associated with this room will be deleted", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteRoom(room);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(activity.getFragmentManager(), "dialog");
    }*/

   /* private void dialogNewPassword(DeviceVO item) {

        isPIREdit = false;

        mPIRModedialog = new Dialog(getActivity());
        mPIRModedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPIRModedialog.setCanceledOnTouchOutside(false);
        mPIRModedialog.setContentView(R.layout.dialog_add_custome_room);


        final TextInputEditText newpassword = mPIRModedialog.findViewById(R.id.edt_room_name);
        final TextInputLayout mTextInput = mPIRModedialog.findViewById(R.id.txtInputSensor_pir);
        final TextInputLayout mTextInputextra = mPIRModedialog.findViewById(R.id.inputRoom);
        edSensorName = mPIRModedialog.findViewById(R.id.edSensorName_pir);
        RelativeLayout relative_guestpasscode = mPIRModedialog.findViewById(R.id.relative_guestpasscode);
        ImageView mImgPIRMode = mPIRModedialog.findViewById(R.id.img_pir_mode);
        LinearLayout mDeviceTurnOnOff = mPIRModedialog.findViewById(R.id.lineardeviceturnofftime);
        mPIRModedialog.findViewById(R.id.rel_pir_mode).setVisibility(View.VISIBLE);
        RelativeLayout rel_pir = mPIRModedialog.findViewById(R.id.rel_pir_layout);
        ImageView mDialogEdit = mPIRModedialog.findViewById(R.id.iv_edit);

        mDeviceTurnOnOffTimer = mPIRModedialog.findViewById(R.id.sp_device_trun_onoff);
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.pir_turn_on_off_timer));
        mDeviceTurnOnOffTimer.setAdapter(spinnerCountShoesArrayAdapter);


        if (item.getMeta_pir_timer() != null) {
            switch (item.getMeta_pir_timer()) {

                case "30":
                    mDeviceTurnOnOffTimer.setSelection(0);
                    break;
                case "60":
                    mDeviceTurnOnOffTimer.setSelection(1);
                    break;
                case "120":
                    mDeviceTurnOnOffTimer.setSelection(2);
                    break;
                case "180":
                    mDeviceTurnOnOffTimer.setSelection(3);
                    break;
                case "240":
                    mDeviceTurnOnOffTimer.setSelection(4);
                    break;
                default:
                    mDeviceTurnOnOffTimer.setSelection(0);
                    break;


            }
        }


        mDeviceTurnOnOffTimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();

                switch (text) {

                    case "30 Seconds":
                        pir_device_on_off_timer = "30";
                        break;
                    case "1 minute":
                        pir_device_on_off_timer = "60";
                        break;
                    case "2 minutes":
                        pir_device_on_off_timer = "120";
                        break;
                    case "3 minutes":
                        pir_device_on_off_timer = "180";
                        break;
                    case "4 minutes":
                        pir_device_on_off_timer = "240";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        newpassword.setVisibility(View.GONE);
        relative_guestpasscode.setVisibility(View.GONE);
        mTextInputextra.setVisibility(View.GONE);
        edSensorName.setVisibility(View.VISIBLE);
        mTextInput.setVisibility(View.VISIBLE);
        mDeviceTurnOnOff.setVisibility(View.VISIBLE);
        rel_pir.setVisibility(View.VISIBLE);
        mDialogEdit.setVisibility(View.VISIBLE);


        mTextInput.setHint("Motion Detector name");
        edSensorName.setText(item.getDeviceName());

        edSensorName.setEnabled(false);
        mDeviceTurnOnOffTimer.setEnabled(false);

        if (item.getDevice_sub_type().equalsIgnoreCase("pir")) {
            mImgPIRMode.setImageResource(R.drawable.switch_enable);
            mDeviceTurnOnOffTimer.setEnabled(false);
        } else {
            mImgPIRMode.setImageResource((R.drawable.switch_disable));
        }

        mDialogEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog_PIR(item);
            }
        });

        mImgPIRMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPIREdit) {

                    if (item.getDevice_sub_type().equalsIgnoreCase("pir")) {
                        mImgPIRMode.setImageResource(R.drawable.switch_disable);
                        item.setDevice_sub_type("normal");
                        mDeviceTurnOnOffTimer.setEnabled(false);
                    } else {
                        mImgPIRMode.setImageResource(R.drawable.switch_enable);
                        item.setDevice_sub_type("pir");
                        mDeviceTurnOnOffTimer.setEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please tap on edit icon for update the device.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        btnPIRSave = mPIRModedialog.findViewById(R.id.btn_save);

        btnPIRSave.setAlpha(0.5f);


        ImageView iv_close = mPIRModedialog.findViewById(R.id.iv_close);
        TextView tv_title = mPIRModedialog.findViewById(R.id.tv_title);

        tv_title.setText(item.getDeviceName());


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPIRModedialog.dismiss();
            }
        });


        btnPIRSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPIREdit) {
                    item.setDeviceName(edSensorName.getText().toString());
                    UpdatePIRDevice(item);
                } else {
                    Toast.makeText(getActivity(), "Please edit before save the device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPIRModedialog.show();
    }*/

   /* private void UpdatePIRDevice(DeviceVO item) {

        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            showToast("" + R.string.disconnect);
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().updatePIRDevice(item, pir_device_on_off_timer, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        //    showToast(message);
                    }
                    if (code == 200) {

                        Intent intent = new Intent(ChatApplication.getContext(), Main2Activity.class);
                        getActivity().startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });


    }*/

    /*public void showBottomSheetDialog_PIR(DeviceVO item) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + item.getDeviceName() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!isPIREdit) {
                    isPIREdit = true;
                    edSensorName.setEnabled(true);
                    btnPIRSave.setAlpha(1.0f);
                    edSensorName.requestFocus();

                    if (edSensorName.getText() != null)
                        edSensorName.setSelection(edSensorName.getText().toString().length());

                }
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                showDelete(item);
            }
        });
    }*/

    /*private void showDelete(DeviceVO item) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure want to delete?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deletePIRDetector(item);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(activity.getFragmentManager(), "dialog");
    }*/

    /*private void deletePIRDetector(DeviceVO item) {
        ActivityHelper.showProgressDialog(getActivity(), "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(item.getDeviceId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getActivity(), message);
                        Intent intent = new Intent(ChatApplication.getContext(), Main2Activity.class);
                        getActivity().startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }*/

    public interface OnHeadlineSelectedListener {
        void onArticleSelected(String name);

        void onLogout(boolean isLogout);
    }

}
