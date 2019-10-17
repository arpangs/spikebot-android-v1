package com.spike.bot.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.GetJsonTaskMacAddress;
import com.kp.core.GetJsonTaskRemote;
import com.kp.core.GetJsonTaslLocal;
import com.spike.bot.Beacon.BeaconActivity;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.ack.AckWithTimeOut;
import com.spike.bot.activity.AddDevice.AddDeviceTypeListActivity;
import com.spike.bot.activity.AddDevice.AddUnassignedPanel;
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
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.MultiSensorActivity;
import com.spike.bot.activity.Repeatar.RepeaterActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.RoomEditActivity_v2;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.activity.SmartDevice.BrandListActivity;
import com.spike.bot.activity.SmartRemoteActivity;
import com.spike.bot.activity.TTLock.LockBrandActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.adapter.SectionedExpandableGridAdapter;
import com.spike.bot.adapter.SectionedExpandableLayoutHelper;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.listener.DeviceListRefreshView;
import com.spike.bot.listener.LoginPIEvent;
import com.spike.bot.listener.ResponseErrorCode;
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

public class MainFragment extends Fragment implements ItemClickListener, SectionedExpandableGridAdapter.CameraClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnSmoothScrollList, TempClickListener , DeviceListRefreshView {

    public static int showDialog = 1;
    public static Boolean isRefredCheck = true, isItemClickView=false,isResumeConnect=false;
    public static DeviceVO tmpDeviceV0;
    public static int tmpPosition = -1;
    public static Activity activity;
    public static int SENSOR_TYPE_DOOR = 1,SENSOR_TYPE_TEMP = 2,SENSOR_TYPE_IR = 3,SENSOR_REPEATAR = 10,Curtain = 6;
    private Boolean isSocketConnected = true;
    public static OnHeadlineSelectedListener mCallback;

    public View view;
    private RecyclerView mMessagesView;
    public RelativeLayout relativeMainfragment;
    private LinearLayout txt_empty_schedule,linear_retry;
    private ImageView empty_add_image;
    Button button_retry;
    Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public boolean isFABOpen=false,isCloudConnected=false;
    private NestedScrollView main_scroll;
    private FloatingActionButton mFab;
    private CardView mFabMenuLayout;
    private TextView txt_empty_text,fab_menu1, fab_menu2, fab_menu6, fab_menu7, fabZigbeeRemote, fabSmartDevice, fabLock, fabrepeater,fab_beacon;


    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private List<UnassignedListRes.Data.RoomList> roomListUn;
    ArrayList<CameraVO> cameraList = new ArrayList<CameraVO>();
    public static ArrayList<PiDetailsModel> piDetailsModelArrayList = new ArrayList<PiDetailsModel>();

    private int SIGN_IP_REQUEST_CODE = 204,countFlow = 0;

    public SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;

    private String userId = "0",webUrl="";
    private Socket mSocket;
    // auto service find
    public CloudAdapter.CloudClickListener cloudClickListener;
    Gson gsonType;
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.

    ResponseErrorCode responseErrorCode;
    LoginPIEvent loginPIEvent;
    SocketListener socketListener;


    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(activity, new Crashlytics());
        view = inflater.inflate(R.layout.fragment_main, container, false);

        //for callback network change
        ((Main2Activity)activity).setCallBack(this);

        toolbar =  activity.findViewById(R.id.toolbar);
        swipeRefreshLayout =  view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        mMessagesView =  view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(activity));
        main_scroll =  view.findViewById(R.id.main_scroll);
        relativeMainfragment = view.findViewById(R.id.relativeMainfragment);
        txt_empty_text =  view.findViewById(R.id.txt_empty_text);
        txt_empty_schedule =  view.findViewById(R.id.txt_empty_schedule);
        empty_add_image =  view.findViewById(R.id.empty_add_image);
        mFabMenuLayout =  view.findViewById(R.id.fabLayout1);
        fab_menu1 = view.findViewById(R.id.fab_menu1);
        fab_menu2 = view.findViewById(R.id.fab_menu2);
        fab_menu6 = view.findViewById(R.id.fab_menu6);
        fab_menu7 = view.findViewById(R.id.fab_menu7);
        fabSmartDevice = view.findViewById(R.id.fabSmartDevice);
        fabZigbeeRemote = view.findViewById(R.id.fabZigbeeRemote);
        fabLock = view.findViewById(R.id.fabLock);
        fabrepeater = view.findViewById(R.id.fabrepeater);
        fab_beacon = view.findViewById(R.id.fab_beacon);
        button_retry = view.findViewById(R.id.button_retry);
        linear_retry = view.findViewById(R.id.linear_retry);
        mFab =  view.findViewById(R.id.fab);

        txt_empty_schedule.setVisibility(View.GONE);
        ChatApplication.isRefreshDashBoard = true;

        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
        sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);

        clickListerFolatingBtn();

        return view;
    }

    private void clickListerFolatingBtn() {
        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomRoom();
            }
        });

        fabSmartDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent = new Intent(activity, BrandListActivity.class);
                startActivity(intent);

            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isItemClickView){
//                    if (!isFABOpen) {
//                        showFABMenu();
//                    } else {
//                        closeFABMenu();
//                    }
//                }

                Intent intent=new Intent(activity, AddDeviceTypeListActivity.class);
                startActivity(intent);


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


        fab_menu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(activity, IRBlasterAddActivity.class));
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
                startActivity(new Intent(activity, SmartRemoteActivity.class));
            }
        });

        fabLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(activity, LockBrandActivity.class));
            }
        });

        fabrepeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(activity, RepeaterActivity.class));
            }
        });


        fab_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivity(new Intent(activity, BeaconActivity.class));
            }
        });


        relativeMainfragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });


        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAllView();
            }
        });


    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        empty_add_image.setVisibility(View.VISIBLE);
        txt_empty_text.setText("Add Room");

        ChatApplication.logDisplay("array is "+roomList.size());
        ((Main2Activity)activity).setCallBack(this);
        if (ChatApplication.isLocalFragmentResume) {
            ChatApplication.isLocalFragmentResume = false;
            getDeviceList(2);

        } else if (ChatApplication.isMainFragmentNeedResume) {
            ChatApplication.isMainFragmentNeedResume = false;
            onLoadFragment();
        }else if(!ChatApplication.isCallDeviceList && roomList.size()>0){
            sectionedExpandableLayoutHelper.addSectionList(roomList);
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
            getDeviceList(15);
        }else{
            getDeviceList(15);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
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
                closeFABMenu();
                LinearLayoutManager layoutManager = ((LinearLayoutManager) mMessagesView.getLayoutManager());
                main_scroll.getParent().requestChildFocus(mMessagesView, mMessagesView);
                main_scroll.smoothScrollBy(50, 100);
            }
        });
    }
    @Override
    public void onPause() {
        Constants.startUrlset();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("ReloadDeviceStatusApp", reloadDeviceStatusApp);
            mSocket.off("roomStatus", roomStatus);
            mSocket.off("panelStatus", panelStatus);
            mSocket.off("changeDoorSensorStatus", changeDoorSensorStatus);
            mSocket.off("changeTempSensorValue", changeTempSensorValue);
            mSocket.off("unReadCount", unReadCount);
            mSocket.off("sensorStatus", sensorStatus);
            mSocket.off("updateChildUser", updateChildUser);
        }
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
        closeFABMenu();
        if (action.equalsIgnoreCase("heavyloadSocketon")) {

        } else if (action.equalsIgnoreCase("heavyloadSocketoff")) {

        } else if (action.equalsIgnoreCase("showGridCamera")) {
            Intent intent = new Intent(activity, CameraGridActivity.class);
            intent.putExtra("cameraList", cameraList);
            startActivity(intent);

        } else if (action.equalsIgnoreCase("refreshCamera")) {
            getDeviceList(1);

        } else if (action.equalsIgnoreCase("onoffclick")) {
            //on off room
            if (action.equalsIgnoreCase("onOffclick")) {
                roomPanelOnOff(null, roomVO, roomVO.getRoomId(), "", roomVO.getOld_room_status(), 1);
            }
        } else if (action.equalsIgnoreCase("editclick_false")) {
            Intent intent = new Intent(activity, RoomEditActivity_v2.class);
            intent.putExtra("room", roomVO);
            startActivity(intent);

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
                    deleteRoom(roomVO);
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
            startActivity(intent);
        } else if (action.equalsIgnoreCase("cameraNotification")) {
            Intent intent = new Intent(activity, CameraNotificationActivity.class);
            startActivity(intent);
        }
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
        } else if (action.equalsIgnoreCase("philipsClick")) {
            deviceOnOff(item, position);
        } else if (action.equalsIgnoreCase("curtain")) {
            Intent intent = new Intent(activity, CurtainActivity.class);
            intent.putExtra("curtain_id", item.getDeviceId());
            intent.putExtra("module_id", item.getModuleId());
            intent.putExtra("curtain_name", item.getDeviceName());
            intent.putExtra("curtain_status", ""+item.getDeviceStatus());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("scheduleclick")) {
            Intent intent = new Intent(activity, ScheduleActivity.class);
            intent.putExtra("item", item);
            intent.putExtra("schedule", true);
            startActivity(intent);
        }
        if (action.equalsIgnoreCase("longclick")) {
            int getDeviceSpecificValue = 0;
            if (!TextUtils.isEmpty(item.getDeviceSpecificValue())) {
                getDeviceSpecificValue = Integer.parseInt(item.getDeviceSpecificValue());
            }
            FanDialog fanDialog = new FanDialog(activity, item.getRoomDeviceId(), getDeviceSpecificValue, new ICallback() {
                @Override
                public void onSuccess(String str) {
                    if (str.contains("yes")) {
                    }
                }
            });
            fanDialog.show();
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

            if (item.getSensor_type().equalsIgnoreCase("tempsensor") && item.getIsActive() == 1) {
                Intent intent = new Intent(activity, MultiSensorActivity.class);
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getModuleId());
                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("gassensor")) {
                if (item.getIsActive() == -1) {
                    return;
                }
                Intent intent = new Intent(activity, GasSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("module_id", item.getModuleId());
                startActivity(intent);

            } else if (item.getSensor_type().equalsIgnoreCase("multisensor")) {
                if (item.getIsActive() == -1) {
                    return;
                }

                Intent intent = new Intent(activity, MultiSensorActivity.class);
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getModuleId());
                startActivity(intent);
            } else if (item.getSensor_type().equalsIgnoreCase("door")) {
                ChatApplication.logDisplay("door call is intent " + mSocket.connected());
                Intent intent = new Intent(activity, DoorSensorInfoActivity.class);
                intent.putExtra("door_sensor_id", item.getSensor_id());
                intent.putExtra("door_room_name", item.getRoomName());
                intent.putExtra("door_room_id", item.getRoomId());
                intent.putExtra("door_unread_count", item.getIs_unread());
                intent.putExtra("door_module_id", item.getModuleId());
                intent.putExtra("door_subtype", "" + item.getDoor_subtype());
                startActivity(intent);
            }
        } else if (action.equalsIgnoreCase("heavyloadlongClick")) {
            Intent intent = new Intent(activity, HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomDeviceId", item.getRoomDeviceId());
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
            callDoorRemotly(item);
        } else if (action.equalsIgnoreCase("isIRSensorLongClick")) {

            Intent intent = new Intent(activity, IRBlasterRemote.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("REMOTE_IS_ACTIVE", item.getDeviceStatus());
            bundle.putSerializable("REMOTE_ID", item.getSensor_id());
            bundle.putSerializable("IR_BLASTER_ID", item.getSensor_id());
            // intent.putExtra("IR_BLASTER_ID",item.getIr_blaster_id());
            intent.putExtra("IR_BLASTER_ID", item.getSensor_id());
            intent.putExtra("ROOM_DEVICE_ID", item.getRoomDeviceId());
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("doorLongClick")) {
            Intent intent = new Intent(activity, DoorSensorInfoActivity.class);
            intent.putExtra("door_sensor_id", item.getSensor_id());
            intent.putExtra("door_room_name", item.getRoomName());
            intent.putExtra("door_room_id", item.getRoomId());
            intent.putExtra("door_unread_count", item.getIs_unread());
            intent.putExtra("door_module_id", item.getModuleId());
            startActivity(intent);
        }
    }

    /**
     * @param item
     * @param action
     */

    @Override
    public void itemClicked(CameraVO item, String action) {
        if (action.equalsIgnoreCase("editcamera")) {
            Intent intent = new Intent(activity, CameraEdit.class);
            intent.putExtra("cameraList", cameraList);
            intent.putExtra("cameraSelcet", item);
            intent.putExtra("isEditable", false);
            startActivity(intent);
        } else if (action.equalsIgnoreCase("cameraLog")) {
            Intent intent = new Intent(activity, CameraDeviceLogActivity.class);
            intent.putExtra("cameraId", "" + item.getCamera_id());
            startActivity(intent);
        } else {
            callCameraToken(item, action);
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
        if(count==0){
            errorViewClick();
        }else {
            getDeviceList(1);
        }
        ChatApplication.logDisplay("device refreshing mainfragment");
    }

    // view refresh
    private void refreshAllView() {
        mFab.setVisibility(View.VISIBLE);
        linear_retry.setVisibility(View.GONE);
        ((Main2Activity)activity).mToolBarSettings.setClickable(true);
        ((Main2Activity)activity).tabShow(true);
        ChatApplication.isCallDeviceList=true;
        getDeviceList(1);
    }

    public void errorViewClick(){
        mFab.setVisibility(View.GONE);
        txt_empty_schedule.setVisibility(View.GONE);
        linear_retry.setVisibility(View.VISIBLE);
        ((Main2Activity)activity).mToolBarSettings.setClickable(false);
        ((Main2Activity)activity).tabShow(false);

    }

    /**
     * Display Fab Menu with subFab Button
     */

    private void showFABMenu() {
        isFABOpen = true;
        mFabMenuLayout.setVisibility(View.VISIBLE);
    }

    public void closeFABMenu() {
        isFABOpen = false;
        mFabMenuLayout.setVisibility(View.GONE);
        if (!isFABOpen) {
            mFabMenuLayout.setVisibility(View.GONE);
        }
    }

    public void startSocketConnection() { //◉

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {

            if (!webUrl.startsWith("http")) {
                webUrl = "http://" + webUrl;
            }
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
                mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);  // ac on off
                mSocket.on("roomStatus", roomStatus);
                mSocket.on("panelStatus", panelStatus);
                mSocket.on("changeDoorSensorStatus", changeDoorSensorStatus);
                mSocket.on("changeTempSensorValue", changeTempSensorValue);
                mSocket.on("unReadCount", unReadCount);
                mSocket.on("sensorStatus", sensorStatus);
                mSocket.on("updateChildUser", updateChildUser);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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

        } else if (!Common.isConnected()) {
        } else {
            getDeviceList(5);
        }
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
                    return;
                }
                if (TextUtils.isEmpty(camera_ip.getText().toString())) {
                    camera_ip.requestFocus();
                    camera_ip.setError("Enter Camera IP");
                    return;
                }
                if (TextUtils.isEmpty(video_path.getText().toString())) {
                    video_path.requestFocus();
                    video_path.setError("Enter Video Path");
                    return;
                }
                if (TextUtils.isEmpty(user_name.getText().toString())) {
                    user_name.requestFocus();
                    user_name.setError("Enter User Name");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.requestFocus();
                    password.setError("Enter Password");
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

    private void getUnAssignedList() {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            showToast("" + R.string.disconnect);
            return;
        }

        String url = ChatApplication.url + Constants.GET_ALL_UNASSIGNED_DEVICES;

        new GetJsonTask(getContext(), url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        UnassignedListRes unassignedListRes = Common.jsonToPojo(result.toString(), UnassignedListRes.class);
                        roomListUn = unassignedListRes.getData().getRoomList();
                        if (roomListUn.size() > 0) {
                            startActivity(new Intent(activity, AddUnassignedPanel.class));
                        } else {
                            ChatApplication.showToast(activity, "No Unassigned Module");
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
                dismissProgressDialog();
            }
        }).execute();

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

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
            obj.put("camera_name", camera_name);
            obj.put("camera_ip", camera_ip);
            obj.put("video_path", video_path);
            obj.put("user_name", user_name);
            obj.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.ADD_CAMERA;

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (code == 200) {
                        dialog.dismiss();
                        ChatApplication.showToast(activity, message);
                        getDeviceList(6);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("onFailure " + error);
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }


    private void deviceOnOff(final DeviceVO deviceVO, final int position) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));

            if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
                obj.put("status", deviceVO.getDeviceStatus());
                obj.put("bright", "");
                obj.put("is_rgb", "0");//1;
                obj.put("rgb_array", "");
                obj.put("room_device_id", deviceVO.getRoomDeviceId());
            } else {
                obj.put("room_device_id", deviceVO.getRoomDeviceId());
                obj.put("module_id", deviceVO.getModuleId());
                obj.put("device_id", deviceVO.getDeviceId());
                obj.put("device_status", deviceVO.getOldStatus());
                obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("onoff is "+obj);

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
        }
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
                sectionedExpandableLayoutHelper.updateItem(deviceVO.getModuleId(),
                        String.valueOf(deviceVO.getDeviceId()), String.valueOf(deviceVO.getOldStatus()), deviceVO.getIs_locked());
            }
        });
    }

    private void updateRoomOfflineMode(final RoomVO roomVO) {

        activity.runOnUiThread(new Runnable() {
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper.updatePanel(panelVO.getPanelId(), String.valueOf(panelVO.getOldStatus()), panelVO.getRoom_panel_id());
            }
        });
    }

    private void callDeviceOnOffApi(final DeviceVO deviceVO, JSONObject obj) {

        String url = "";
        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            url = ChatApplication.url + Constants.changeHueLightState;
        } else {
            url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;
        }

        ChatApplication.logDisplay("Device roomPanelOnOff obj " + obj.toString());

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
                try {
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code == 200) {
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(activity, message);
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
            }
        }).execute();
    }

    private void roomPanelOnOff(final PanelVO panelVO, final RoomVO roomVO, String roomId, String panelId, int panel_status, int type) {

        final JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
            obj.put("room_id", roomId);
            obj.put("panel_id", panelId);
            obj.put("device_status", panel_status);
            obj.put("operationtype", type);
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("roomPanelOnOff "+obj);

        if (mSocket != null && mSocket.connected()) {
            mSocket.emit("changeRoomPanelMoodStatusAck", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
                @Override
                public void call(Object... args) {
                    if (args != null) {
                        if (args[0].toString().equalsIgnoreCase("No Ack")) {

                            if (panelVO != null) {
                                updatePanelDeviceOfflineMode(panelVO);
                            } else if (roomVO != null) {
                                updateRoomOfflineMode(roomVO);
                            }

                        } else if (args[0].toString().equalsIgnoreCase("true")) {
                            cancelTimer();
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

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
                try {
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code == 200) {
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(activity, message);
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
            }
        }).execute();

    }




    /**
     * Delete Room
     *
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO) {
        if (!ActivityHelper.isConnectingToInternet(activity)) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", roomVO.getRoomId());
            object.put("room_name", roomVO.getRoomName());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());
        String url = webUrl + Constants.DELETE_ROOM;
        new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
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
        }).execute();
    }



    private void doorOpenCLose(DeviceVO item) {

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
    }

    private void sendRemoteCommand(final DeviceVO item, String philipslongClick) {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }
        String mRemoteCommandReq = "";
        String url = "";

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

        new GetJsonTaskRemote(getContext(), url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("result is ir " + result);
                    if (code == 200) {
                        ChatApplication.isMoodFragmentNeedResume = true;
                        SendRemoteCommandRes tmpIrBlasterCurrentStatusList = Common.jsonToPojo(result.toString(), SendRemoteCommandRes.class);
                    } else {
                        ChatApplication.showToast(activity, item.getSensor_name() + " " + activity.getString(R.string.ir_error));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("result is ir error " + error.toString());
                ChatApplication.showToast(activity, item.getSensor_name() + " " + activity.getString(R.string.ir_error));
            }
        }).execute();
    }

    private void callCameraToken(final CameraVO item, final String action) {
        showProgressDialog(activity, " Please Wait...", false);
        String url = ChatApplication.url + Constants.getCameraToken + item.getCamera_id();

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(activity, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");

                    if (code == 200) {
                        dismissProgressDialog();
                        JSONObject dataObject = result.optJSONObject("data");

                        String camera_vpn_port = dataObject.optString("camera_vpn_port");
                        String camera_url = dataObject.optString("camera_url");

                        String url="";

//                        String url = item.getCamera_ip();
//                        url = url + item.getCamera_videopath();
                        String camera_name = item.getCamera_name();

//                        ChatApplication.logDisplay("isCloudConnect : " + dataObject+" "+camera_vpn_port);

                        if (Main2Activity.isCloudConnected) {
                            url = Constants.CAMERA_DEEP + ":" + camera_vpn_port + "" + camera_url;
                        } else {
                            String tmpurl = ChatApplication.url + "" + camera_url;
                            url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
                        }
                        ChatApplication.logDisplay("isCloudConnect : " +camera_vpn_port+" "+url);
                        //start camera rtsp player
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
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
            }
        }).execute();

    }
    //counter for 7 wait depay
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            /*- SOCKET Response:
            - 'configureGatewayDevice'
                    - parameter: module_id, device_id
                    - Open Popup and Fill below data: module_id, device_id*/
            addRoom = false;
            dismissProgressDialog();
            ChatApplication.showToast(activity, "No New Device detected!");
        }

    };

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
    }

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
    }

    /*
     * camera key check is valid or not..
     * */

    private void saveCameraKey(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter key name");
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("key", roomName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(activity, "Searching Device attached ", false);

        String url = webUrl + Constants.validatecamerakey;

        new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, "1");
                        ChatApplication.showToast(activity, message);
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        addCamera();
                    } else if (code == 301) {
                        ChatApplication.showToast(activity, message);
                    } else {
                        ChatApplication.showToast(activity, message);
                    }
                    // Toast.makeText(activity.getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
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

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("room_name", roomName.getText().toString());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());
        showProgressDialog(activity, "Searching Device attached ", false);

        String url = webUrl + Constants.ADD_CUSTOME_ROOM;

        new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
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
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                ChatApplication.showToast(activity, getResources().getString(R.string.disconnect));
            }
        }).execute();
    }

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String name);

        void onLogout(boolean isLogout);
    }

    public void getMacAddress(String locaIp) {
        countFlow = 0;
        String url = ChatApplication.http + locaIp + Constants.getLocalMacAddress;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTaskMacAddress(activity, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                countFlow = 1;
                countDownTimer.cancel();
                // {"code":200,"message":"Success","data":{"mac_address":"b8:27:eb:f7:45:b3"}}

                ChatApplication.logDisplay("reponse is mac " + result.toString());
//                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result.toString());
                    JSONObject jsonObject = object.optJSONObject("data");

                    if (jsonObject != null && countFlow != 2) {
                        if (jsonObject.optString("mac_address").equalsIgnoreCase(Constants.getMacAddress(activity))) {
                            callColud(true);
                        } else {
                            callColud(false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
//                dismissProgressDialog();
                countDownTimer.cancel();
                if (countFlow != 2) {
                    countFlow = 1;
                    callColud(false);
                }


            }
        }).execute();

        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (countFlow == 0) {
                    countFlow = 2;
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
        ChatApplication.logDisplay("show is call 11  " + activity);
        if (isflag) {
            //local
            ((Main2Activity) activity).webUrl = ChatApplication.http + Constants.getuserIp(activity) + ":80";
            ChatApplication.url = ChatApplication.http + Constants.getuserIp(activity);
           Constants.startUrlset();
            ((Main2Activity) activity).callSocket();
            ((Main2Activity) activity).invalidateToolbarCloudImage();

            getDeviceLocal(0);
        } else {
            ChatApplication.logDisplay("get fragment "+activity);
            String colud = Constants.getuserCloudIP(activity);
            ChatApplication.url = colud;
            Constants.startUrlset();
            ((Main2Activity) activity).webUrl = colud;
            ((Main2Activity) activity).callSocket();
            ((Main2Activity) activity).invalidateToolbarCloudImage();

            getDeviceCloud(0);
        }

    }

    public void getDeviceCloud(int isShow) {

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (isShow == 0) {
            if (ChatApplication.url.startsWith(Constants.startUrl)) {
                if (m_progressDialog!=null && m_progressDialog.isShowing()) {
                   m_progressDialog.setMessage("Connecting to cloud...");
                }else {
                    showProgressDialog(activity, "Connecting to cloud...", true);
                }
            } else {
                if(!m_progressDialog.isShowing()){
                    showProgressDialog(activity, "Please Wait...", true);
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

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("room_type", 0);
//            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
            jsonObject.put("room_type", "room");

//            if (TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE))) {
//                jsonObject.put("admin", 1);
//            } else {
//                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE)));
//            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard cloud " + url + "   " + jsonObject.toString());
        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.isCallDeviceList = false;
                if (ChatApplication.isPushFound) {
                    getBadgeClear(activity);
                }
                isItemClickView=true;
                mMessagesView.setClickable(true);
                responseErrorCode.onSuccess();
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    ((Main2Activity)activity).tabShow(true);
                    Main2Activity.isCloudConnected=true;
                    webUrl = ChatApplication.url;
                    startSocketConnection();
                    dismissProgressDialog();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Constants.socketType = 1;
                        Constants.socketIp = ChatApplication.url;
                        hideAdapter(true);
                        JSONObject dataObject = result.getJSONObject("data");
                        ((Main2Activity) activity).getUserDialogClick(true);

                        if(gsonType==null){
                            gsonType=new Gson();
                        }

                        roomList = new ArrayList<>();

                        JSONArray userListArray = dataObject.optJSONArray("userList");
                        if (userListArray.length() == 0) {
                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");
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
                        String gateway_ip = userObject.optString("gateway_ip");

                        if (is_active.equalsIgnoreCase("0")) {
                            ((Main2Activity) activity).logoutCloudUser();
                            return;
                        }

                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        String userPassword = "";
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
                        }


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
                                userList1 = gsonType.fromJson(jsonTextTemp1, type);
                            }

                            for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        showLogoutAlert();
                                        ChatApplication.showToast(activity, "Password has been changed!");
                                        ((Main2Activity) activity).logoutCloudUser();
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

                        //JSONArray userList = dataObject.getJSONArray("userList");

                        ((Main2Activity)activity).changestatus();
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
                    if (roomList.size() == 0) {
                        mMessagesView.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                    dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                isItemClickView=false;
                swipeRefreshLayout.setRefreshing(false);
                dismissProgressDialog();
                //set custom homer controller not found error message on dashboard
                if (reCode == 503 || reCode == 404) {
                    responseErrorCode.onErrorCode(reCode);
                }
                ChatApplication.logDisplay("show is call could error" + error+" "+reCode);
                if(!TextUtils.isEmpty(error)){
                    ((Main2Activity)activity).tabShow(false);
                    ChatApplication.logDisplay("reCode getDeviceList onFailure " + reCode);
                    sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(activity, mMessagesView, MainFragment.this, MainFragment.this, MainFragment.this, Constants.SWITCH_NUMBER);
                    sectionedExpandableLayoutHelper.setCameraClick(MainFragment.this);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    sectionedExpandableLayoutHelper.notifyDataSetChanged();

                    ChatApplication.isCallDeviceList=true;
                    errorViewClick();
                }

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void getDeviceLocal(int isShow) {

        if (swipeRefreshLayout != null) {
            if (swipeRefreshLayout.isRefreshing()) {
                showDialog = 1;
            }
        }

        if (isShow == 0) {
//            if (m_progressDialog == null) {
                if(m_progressDialog!=null){
                    if(!m_progressDialog.isShowing()){
                        showProgressDialog(activity, "Please Wait...", true);
                    }
                }
//            }
        }

        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView =  view.findViewById(R.id.messages);
        }

        mMessagesView.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("room_type", 0);
//            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
            jsonObject.put("room_type", "room");

//            if (TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE))) {
//                jsonObject.put("admin", 1);
//            } else {
//                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE)));
//            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard local " + url + "   " + jsonObject.toString());
        new GetJsonTaslLocal(activity, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.isCallDeviceList = false;
                if (ChatApplication.isPushFound) {
                    getBadgeClear(activity);
                }
                mMessagesView.setClickable(true);
                responseErrorCode.onSuccess();
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay("getDeviceList onSuccess local " + result.toString());

                try {
                    Main2Activity.isCloudConnected=false;
                    ((Main2Activity)activity).tabShow(true);
                    isItemClickView=true;
                    webUrl = ChatApplication.url;
                    startSocketConnection();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if(gsonType==null){
                            gsonType=new Gson();
                        }
                        ((Main2Activity)activity).tabShow(true);
                        Constants.socketType = 0;
                        Constants.socketIp = ChatApplication.url;
                        hideAdapter(true);
                        mMessagesView.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                        JSONObject dataObject = result.optJSONObject("data");
                        ((Main2Activity) activity).getUserDialogClick(true);
                        roomList = new ArrayList<>();
                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        //oppen signup page if userList found zero length
                        if (userListArray.length() == 0) {
                            Common.savePrefValue(ChatApplication.getInstance(), "first_name", "");
                            Common.savePrefValue(ChatApplication.getInstance(), "last_name", "");

                            if (!TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ID))) {
                                ((Main2Activity) activity).logoutCloudUser();
                            }
                            return;
                        }

                        JSONObject userObject = userListArray.optJSONObject(0);
                        String userId = userObject.optString("user_id");
                        String userFirstName = userObject.optString("first_name");
                        String userLastName = userObject.optString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        String is_active = userObject.optString("is_active");
                        String gateway_ip = userObject.optString("gateway_ip");

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
//                        if (Main2Activity.isCloudConnected && !TextUtils.isEmpty(userPassword)) {
                        Gson gson=new Gson();
                        if (!TextUtils.isEmpty(userPassword)) {
                            String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                            List<User> userList1 = new ArrayList<User>();
                            if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                Type type = new TypeToken<List<User>>() {
                                }.getType();
                                userList1 = gson.fromJson(jsonTextTemp1, type);
                            }

                            for (User user : userList1) {
                                if (user.isActive()) {
                                    if (user.getUser_id().equalsIgnoreCase(userId) && !user.getPassword().equalsIgnoreCase(userPassword)) {
//                                        showLogoutAlert();
                                        ChatApplication.showToast(activity, "Password has been changed!");
                                        ((Main2Activity) activity).logoutCloudUser();
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


                        // activity.setTitle("" + userFirstName + " " + userLastName);
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

                        ((Main2Activity)activity).changestatus();
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
                    dismissProgressDialog();
                    showDialog = 0;
                    if (roomList != null) {
                        CameraDeviceLogActivity.getCameraList = roomList.get(roomList.size() - 1).getPanelList().get(0).getCameraList();
                    }
                    mMessagesView.setClickable(true);
                    mFab.setClickable(true);
                    swipeRefreshLayout.setRefreshing(false);
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
                ChatApplication.logDisplay("show is call local error" + error);
                if (!TextUtils.isEmpty(error)) {
                    isItemClickView=false;
                    ChatApplication.logDisplay("show is call local error22 " + error);
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
                    if (mSocket != null) {
                        mSocket.disconnect();
                    }
                    ((Main2Activity)activity).tabShow(false);
                    callColud(false);
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

        if(linear_retry.getVisibility()==View.VISIBLE){
            mFab.setVisibility(View.VISIBLE);
            linear_retry.setVisibility(View.GONE);
            ((Main2Activity)activity).mToolBarSettings.setClickable(true);
            ((Main2Activity)activity).tabShow(true);
            ChatApplication.isCallDeviceList=true;
        }

        if (showDialog == 1 || checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 10) {
            showProgressDialog(activity, " Please Wait...", true);
        }
        ChatApplication.logDisplay("show progress is " + showDialog);

//        roomList.clear();
        if (mMessagesView == null) {
            mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        }
        mMessagesView.setClickable(false);
        roomList = new ArrayList<>();
        if (sectionedExpandableLayoutHelper != null) {
            sectionedExpandableLayoutHelper.notifyDataSetChanged();
        }
        Constants.startUrlset();
        ChatApplication.logDisplay("show is call 1111  " + activity);
        if (ChatApplication.isCallDeviceList) {
            ChatApplication app = ChatApplication.getInstance();
            app.closeSocket(webUrl);
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
            }
            ((Main2Activity)activity).tabShow(false);
            hideAdapter(false);
            sectionedExpandableLayoutHelper.clearData();
            if (Common.isNetworkConnected(activity)) {
                ((Main2Activity)activity).tabShow(false);
                getMacAddress(Constants.getuserIp(activity));
            } else {
                callColud(false);
            }
        } else {
            if (ChatApplication.url.startsWith(Constants.startUrl)) {
//                getDeviceCould(1);
                getDeviceCloud(1);
            } else {
                getDeviceLocal(1);
            }
        }
    }

    public void setUserTypeValue() {
        if (!TextUtils.isEmpty(Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE))) {
            if (Common.getPrefValue(activity, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                mFab.setVisibility(View.GONE);
                ((Main2Activity) activity).toolbarImage.setVisibility(View.VISIBLE);
                ((Main2Activity) activity).toolbarTitle.setClickable(true);
                ((Main2Activity) activity).toolbarImage.setClickable(true);

                ((Main2Activity) activity).toolbarTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                mFab.setVisibility(View.VISIBLE);
                ((Main2Activity) activity).toolbarTitle.setClickable(true);
                ((Main2Activity) activity).toolbarImage.setClickable(true);
                ((Main2Activity) activity).toolbarImage.setVisibility(View.VISIBLE);
                ((Main2Activity) activity).toolbarTitle.setTextColor(getResources().getColor(R.color.sky_blue));
            }
        }
    }

    ProgressDialog m_progressDialog;
    public void showProgressDialog(Context context, String message, boolean iscancle){
        m_progressDialog=new ProgressDialog(activity);
        m_progressDialog.setMessage(message);
        m_progressDialog.setCanceledOnTouchOutside(true);
        m_progressDialog.show();
    }
    public void dismissProgressDialog(){
        if(m_progressDialog!=null){
            ChatApplication.logDisplay("m_progressDialog is null not");
            m_progressDialog.cancel();
            m_progressDialog.dismiss();
//            m_progressDialog=null;
        }
    }

    //getMoodList
    public static void getBadgeClear(final Context context) {
        if (!Constants.checkLoginAccountCount(context)) {
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

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isSocketConnected) {
                        isSocketConnected = true;
                    }
                    Constants.startUrlset();
                    mSocket.emit("socketconnection", "android == startconnect  " + mSocket.id());

                    mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);  // ac on off
                    mSocket.on("roomStatus", roomStatus);
                    mSocket.on("panelStatus", panelStatus);
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
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isSocketConnected = false;
                    //Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
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
    };////////isSensorClick
    private Emitter.Listener reloadDeviceStatusApp = new Emitter.Listener() {
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
                            //{"module_id":"SpikeBot10355128","device_id":"1563952394430_ANUROHD9-h","device_status":1,"is_locked":0}
                            JSONObject object = new JSONObject(args[0].toString());
                            String module_id = object.getString("module_id");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int is_locked = object.optInt("is_locked");

                            sectionedExpandableLayoutHelper.updateItem(module_id, device_id, device_status, is_locked);
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
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());

                            ChatApplication.logDisplay("panel status panelStatus " + object.toString());
                            String panel_id = object.optString("panel_id");
                            String panelstatusValue = object.getString("panel_status");
                            ChatApplication.logDisplay("panel status panelStatus update " + object.toString());
                            sectionedExpandableLayoutHelper.updatePanel(panel_id, panelstatusValue, "");

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
                            ChatApplication.logDisplay("panel status roomStatus update " + object.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                            ChatApplication.logDisplay("door is " + object);
                            String room_order = object.optString("room_order");
                            String door_sensor_id = object.optString("door_sensor_id");
                            String door_sensor_status = object.optString("door_sensor_status");
                            String door_lock_status = object.optString("door_lock_status");

                            sectionedExpandableLayoutHelper.updateDoorStatus(room_order, door_sensor_id, door_sensor_status, door_lock_status);

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

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(activity, Constants.USER_ID))) {
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

            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {

                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("temp is " + object);
                            String room_id = object.getString("room_id");
                            String room_order = object.getString("room_order");
                            String temp_sensor_id = object.getString("temp_sensor_id");
                            String temp_celsius = object.getString("temp_celsius");
                            String temp_fahrenheit = object.getString("temp_fahrenheit");
                            String is_in_C = object.getString("is_in_C");
                            String humidity = object.getString("humidity");

                            sectionedExpandableLayoutHelper.updateTempSensor(room_id, room_order, temp_sensor_id, temp_celsius, temp_fahrenheit, is_in_C, humidity);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    };

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

                            String message = object.optString("message");
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
                            ChatApplication.logDisplay("sensorStatus is " + jsonObject.toString());
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
}
