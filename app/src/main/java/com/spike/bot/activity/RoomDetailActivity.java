package com.spike.bot.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.Camera.CameraDeviceLogActivity;
import com.spike.bot.activity.Camera.CameraEdit;
import com.spike.bot.activity.Camera.CameraGridActivity;
import com.spike.bot.activity.Camera.CameraPlayBack;
import com.spike.bot.activity.Curtain.CurtainActivity;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.Sensor.DoorSensorInfoActivity;
import com.spike.bot.activity.Sensor.GasSensorActivity;
import com.spike.bot.activity.Sensor.MultiSensorActivity;
import com.spike.bot.activity.Sensor.WaterSensorActivity;
import com.spike.bot.activity.TTLock.YaleLockInfoActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.CameraDetailAdapter;
import com.spike.bot.adapter.PanelExpandableLayoutHelper;
import com.spike.bot.adapter.SectionedExpandableGridAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.Utility;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.onCallback;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraCounterModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kp.core.ActivityHelper.dismissProgressDialog;
import static com.kp.core.ActivityHelper.showProgressDialog;
import static com.spike.bot.core.Common.showToast;

public class RoomDetailActivity extends AppCompatActivity implements ItemClickListener, TempClickListener, SwipeRefreshLayout.OnRefreshListener, OnSmoothScrollList, CameraDetailAdapter.CameraClickListener,
        CameraDetailAdapter.JetsonClickListener, CameraDetailAdapter.ClickListener, View.OnClickListener {
    Toolbar toolbar;
    RecyclerView roomrecycleview;
    String room_id, jetson_id, camera_id, room_name, homecontrollerid, cloudurl = "", pir_device_on_off_timer = "30";
    public static Boolean isRefreshCheck = true;
    RoomVO room;

    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView main_scroll;
    public PanelExpandableLayoutHelper sectionedExpandableLayoutHelper;
    public CameraDetailAdapter cameraadapter;
    private ArrayList<PanelVO> panelList = new ArrayList<>();
    private ArrayList<CameraVO> cameraList = new ArrayList<>();
    public static DeviceVO tmpDeviceV0;
    public static int tmpPosition = -1;
    private Socket mSocket, cloudsocket;
    List<CameraCounterModel.Data.CameraCounterList> cameracounterlist;
    Dialog mPIRModedialog;
    boolean isPIREdit;
    TextView txt_empty_message;
    private TextInputEditText edSensorName;
    private Spinner mDeviceTurnOnOffTimer;
    private Button btnPIRSave;
    LinearLayout card_layout, linear_empty_panel, linear_recording, linear_preview, linear_refresh;
    int mNoOfColumns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        room_id = getIntent().getStringExtra("room_id");
        jetson_id = getIntent().getStringExtra("jetson_id");
        camera_id = getIntent().getStringExtra("camera_id");
        room_name = getIntent().getStringExtra("room_name");
        homecontrollerid = getIntent().getStringExtra("homecontrollerId");
        room = (RoomVO) getIntent().getSerializableExtra("room");

        setUIId();
    }

    private void setUIId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(room_name);

        card_layout = findViewById(R.id.card_layout);
        linear_preview = findViewById(R.id.linear_preview);
        linear_refresh = findViewById(R.id.linear_refresh);
        linear_recording = findViewById(R.id.linear_recording);
        linear_empty_panel = findViewById(R.id.linear_empty_panel);
        txt_empty_message = findViewById(R.id.txt_empty_message);
        main_scroll = findViewById(R.id.main_scroll);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        roomrecycleview = findViewById(R.id.room_recycleview);

        roomrecycleview.setLayoutManager(new LinearLayoutManager(RoomDetailActivity.this));
        if (!TextUtils.isEmpty(room_id)) {
            card_layout.setVisibility(View.GONE);
            getRoomDetails();
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(),90);
            } else {
                // code for landscape mode
                mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(),90);
            }
            sectionedExpandableLayoutHelper = new PanelExpandableLayoutHelper(RoomDetailActivity.this, roomrecycleview, RoomDetailActivity.this, RoomDetailActivity.this, RoomDetailActivity.this,mNoOfColumns);
            startSocketConnection();
        } else {
            card_layout.setVisibility(View.VISIBLE);
            startLiveSocketConnection();
            getCameraDetails();

        }

        linear_recording.setOnClickListener(this);
        linear_preview.setOnClickListener(this);
        linear_refresh.setOnClickListener(this);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /* below layout height and width connected with sectionexpandablegridadpater and roomdetailactivity*/
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode

            mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), Float.parseFloat(getResources().getString(R.string.gridcolumnwidth)));
        } else {
            // code for landscape mode
            mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(),Float.parseFloat(getResources().getString(R.string.gridcolumnwidth)));
        }
        sectionedExpandableLayoutHelper = new PanelExpandableLayoutHelper(RoomDetailActivity.this, roomrecycleview, RoomDetailActivity.this, RoomDetailActivity.this, RoomDetailActivity.this,mNoOfColumns);
        sectionedExpandableLayoutHelper.notifyDataSetChanged();
    }

    private void setAdapter() {
        cameraadapter = new CameraDetailAdapter(this, cameraList);
        roomrecycleview.setAdapter(cameraadapter);

        cameraadapter.setCameraClickListener(this);
        cameraadapter.setJetsonClickListener(this);
        cameraadapter.setClickListener(this);
        cameraadapter.notifyDataSetChanged();
    }

    /*start connection*/
    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }

        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }

    }

    public void startLiveSocketConnection() {
       /* ChatApplication app = ChatApplication.getInstance();

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
        }*/

        ChatApplication app = ChatApplication.getInstance();

        if (cloudsocket != null && cloudsocket.connected()) {
        } else {
            cloudsocket = app.getCloudSocket();
        }

        if (cloudsocket != null) {
            cloudsocket();
            ChatApplication.logDisplay("socket is null");
        }
    }

    private void socketOn() {
        if (!TextUtils.isEmpty(room_id)) {
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
            mSocket.on("changePanelStatus", panelStatus);
            mSocket.on("changeModuleStatus", changeModuleStatus);
            mSocket.on("updateDeviceBadgeCounter", unReadCount);
            mSocket.on("changeIrBlasterTemperature", changeIrBlasterTemperature);
        }
    }

    private void cloudsocket() {
        cloudsocket.on("camera-" + Common.getPrefValue(RoomDetailActivity.this, Constants.USER_ID), updateCameraCounter);
    }

    @Override
    protected void onResume() {
        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }
        if (!TextUtils.isEmpty(room_id)) {
            getRoomDetails();
        } else {
            getCameraDetails();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            if (!TextUtils.isEmpty(room_id)) {
                mSocket.off("changeDeviceStatus", changeDeviceStatus);
                mSocket.off("changePanelStatus", panelStatus);
                mSocket.off("changeModuleStatus", changeModuleStatus);
                mSocket.off("updateDeviceBadgeCounter", unReadCount);
                mSocket.off("changeIrBlasterTemperature", changeIrBlasterTemperature);
            }
        }

        if (cloudsocket != null) {
            cloudsocket.off("camera-" + Common.getPrefValue(RoomDetailActivity.this, Constants.USER_ID), updateCameraCounter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        if (!TextUtils.isEmpty(jetson_id) || !TextUtils.isEmpty(camera_id)) {
            actionEdit.setVisible(false);
        } else {
            actionEdit.setVisible(true);
        }
        menu.findItem(R.id.actionEdit).setIcon(resizeImage(R.drawable.edit_white_new, 190, 190));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionEdit) {
            showBottomSheetDialog(room);

        }
        return super.onOptionsItemSelected(item);
    }

    private Drawable resizeImage(int resId, int w, int h) {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    public void getRoomDetails() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        showProgressDialog(RoomDetailActivity.this, "Please wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getRoomDetail(room_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    dismissProgressDialog();
                    swipeRefreshLayout.setRefreshing(false);
                    sectionedExpandableLayoutHelper.setClickable(true);
                    if (result.optInt("code") == 200) {
                        sectionedExpandableLayoutHelper.clearData();
                        JSONObject dataObject = result.getJSONObject("data");
                        JSONArray panelArray = dataObject.getJSONArray("panelList");
                        panelList = JsonHelper.parsePanelArray(panelArray, false);

                        if (panelList.size() == 0) {
                            linear_empty_panel.setVisibility(View.VISIBLE);
                            roomrecycleview.setVisibility(View.GONE);
                        } else {
                            roomrecycleview.setVisibility(View.VISIBLE);
                            linear_empty_panel.setVisibility(View.GONE);
                            sectionedExpandableLayoutHelper.addPanelSectionList(panelList);
                            sectionedExpandableLayoutHelper.notifyDataSetChanged();
                            String room_name = dataObject.getString("room_name");
                            toolbar.setTitle(room_name);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
            }
        });
    }

    public void getCameraDetails() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        showProgressDialog(RoomDetailActivity.this, "Please wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getCameraDetail(jetson_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    dismissProgressDialog();
                    swipeRefreshLayout.setRefreshing(false);
                    if (result.optInt("code") == 200) {
                        JSONArray cameraArray = result.getJSONArray("data");

                        cameraList = JsonHelper.parseCameraArray(cameraArray);

                        if (cameraList.size() > 0) {
                            linear_empty_panel.setVisibility(View.GONE);
                            roomrecycleview.setVisibility(View.VISIBLE);
                            setAdapter();
                        } else {
                            linear_empty_panel.setVisibility(View.VISIBLE);
                            txt_empty_message.setText("No Camera Available");
                            roomrecycleview.setVisibility(View.GONE);
                        }

                        getCameraBadgeCount();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    /**
     * Get camera badge count
     */
    private void getCameraBadgeCount() {

        if (!ActivityHelper.isConnectingToInternet(RoomDetailActivity.this)) {
            Toast.makeText(RoomDetailActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
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

                        cameraadapter.setCounterlist(cameracounterlist);
                        cameraadapter.setCounterres(counterres.getData());
                        //sectionedExpandableLayoutHelper.getCameracounter();
                        cameraadapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {

        if (!TextUtils.isEmpty(room_id)) {
            isRefreshCheck = true;
            ChatApplication.isCallDeviceList = true;
            swipeRefreshLayout.setRefreshing(true);
            getRoomDetails();
            sectionedExpandableLayoutHelper.setClickable(false);
        } else {
            isRefreshCheck = true;
            getCameraDetails();
            swipeRefreshLayout.setRefreshing(true);
        }

    }

    /**
     * {@link SectionedExpandableGridAdapter#}
     *
     * @param position
     */
    @Override
    public void onPoisitionClick(final int position) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = ((LinearLayoutManager) roomrecycleview.getLayoutManager());
                main_scroll.getParent().requestChildFocus(roomrecycleview, roomrecycleview);
                main_scroll.smoothScrollBy(50, 100);
            }
        });
    }

    @Override
    public void itemClicked(RoomVO item, String action) {

    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {
        if (action.equalsIgnoreCase("onOffclick")) {
            roomPanelOnOff(panelVO, null, "", panelVO.getPanelId(), panelVO.getPanel_status(), 2);
        }
    }

    @Override
    public void itemClicked(DeviceVO item, String action, int position) {
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
            Intent intent = new Intent(RoomDetailActivity.this, CurtainActivity.class);
            intent.putExtra("curtain_id", item.getDeviceId());
            intent.putExtra("module_id", item.getModuleId());
            intent.putExtra("curtain_name", item.getDeviceName());
            intent.putExtra("panel_id", item.getPanel_id());
            intent.putExtra("curtain_status", "" + item.getDeviceStatus());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("scheduleclick")) {
            Intent intent = new Intent(RoomDetailActivity.this, ScheduleActivity.class);
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
                FanDialog fanDialog = new FanDialog(RoomDetailActivity.this, item.getDeviceId(), item.getDeviceName(), getDeviceSpecificValue, new onCallback() {
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
            Intent intent = new Intent(RoomDetailActivity.this, CameraDeviceLogActivity.class);
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
        if (action.equalsIgnoreCase("showjetsoncameraLog")) {
            Intent intent = new Intent(RoomDetailActivity.this, CameraDeviceLogActivity.class);
            intent.putExtra("jetsoncameralog", "jetsoncameralog");
            intent.putExtra("jetson_device_id", jetsonid);
            intent.putExtra("cameraId", camera_id);
            //  intent.putExtra("cameraList", panelVO.getCameraList());
            intent.putExtra("homecontrollerId", homecontrollerid);
            startActivity(intent);
        }
    }

    @Override
    public void itemClicked(DeviceVO item, String action, boolean isTemp, int position) {

        if (action.equalsIgnoreCase("isSensorClick") && isTemp) { //start sensor details activity

            tmpDeviceV0 = item;
            tmpPosition = position;

            if (item.getDeviceType().equalsIgnoreCase("temp_sensor")) {
                Intent intent = new Intent(RoomDetailActivity.this, MultiSensorActivity.class);
                intent.putExtra("temp_sensor_name", item.getSensor_name());
                intent.putExtra("temp_sensor_id", item.getSensor_id());
                intent.putExtra("temp_room_name", item.getRoomName());
                intent.putExtra("temp_room_id", item.getRoomId());
                intent.putExtra("temp_unread_count", item.getIs_unread());
                intent.putExtra("temp_module_id", item.getDeviceId());

                startActivity(intent);

            } else if (item.getDeviceType().equalsIgnoreCase("gas_sensor")) {

                Intent intent = new Intent(RoomDetailActivity.this, GasSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("device_id", item.getDeviceId());
                startActivity(intent);

            } else if (item.getDeviceType().equalsIgnoreCase("door_sensor")) {
                ChatApplication.logDisplay("door call is intent " + mSocket.connected());
                Intent intent = new Intent(RoomDetailActivity.this, DoorSensorInfoActivity.class);
                intent.putExtra("door_sensor_id", item.getDeviceId());
                intent.putExtra("door_room_name", item.getRoomName());
                intent.putExtra("door_room_id", item.getRoomId());
                intent.putExtra("door_unread_count", item.getIs_unread());
                intent.putExtra("door_module_id", item.getModuleId());
                intent.putExtra("door_subtype", "" + item.getDoor_subtype());
                startActivity(intent);
            } else if (item.getDeviceType().equalsIgnoreCase("lock")) {
                if (item.getIsActive() == -1) {
                    return;
                } else {
                    ChatApplication.logDisplay("yale lock call is intent " + mSocket.connected());
                    deviceOnOff(item, position);
                }
            } else if (item.getDeviceType().equalsIgnoreCase("water_detector")) {
                ChatApplication.logDisplay("water is intent " + mSocket.connected());
                Intent intent = new Intent(RoomDetailActivity.this, WaterSensorActivity.class);
                intent.putExtra("sensor_id", item.getSensor_id());
                intent.putExtra("room_name", item.getRoomName());
                intent.putExtra("room_id", item.getRoomId());
                intent.putExtra("device_id", item.getDeviceId());
                startActivity(intent);
            }
        } else if (action.equalsIgnoreCase("heavyloadlongClick")) {
            Intent intent = new Intent(RoomDetailActivity.this, HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomName", item.getRoomName());
            intent.putExtra("getModuleId", item.getModuleId());
            intent.putExtra("device_id", item.getDeviceId());
            startActivity(intent);

        } else if (action.equalsIgnoreCase("philipslongClick")) {
            //on-off remote
            if (item.getDeviceStatus() == 1) {
                Intent intent = new Intent(RoomDetailActivity.this, SmartColorPickerActivity.class);
                intent.putExtra("roomDeviceId", item.getRoomDeviceId());
                intent.putExtra("getOriginal_room_device_id", item.getOriginal_room_device_id());
                startActivity(intent);
            } else {
                ChatApplication.showToast(RoomDetailActivity.this, "Please device on");
            }

        } else if (action.equalsIgnoreCase("isIRSensorClick")) {
            //on-off remote
            sendRemoteCommand(item, "isIRSensorClick");
        } else if (action.equalsIgnoreCase("doorOpenClose") && item.getDoor_subtype() != 1) {
            callDoorRemotly(item);
        } else if (action.equalsIgnoreCase("isIRSensorLongClick")) {

            Intent intent = new Intent(RoomDetailActivity.this, IRBlasterRemote.class);
            intent.putExtra("IR_MODULE_ID", item.getModuleId());
            intent.putExtra("IR_BLASTER_ID", item.getDeviceId());
            intent.putExtra("ROOM_DEVICE_ID", item.getRoomDeviceId());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("isLockLongClick")) {

            ChatApplication.logDisplay("yale door call is intent " + mSocket.connected());
            Intent intent = new Intent(RoomDetailActivity.this, YaleLockInfoActivity.class);
            intent.putExtra("door_sensor_id", item.getDeviceId());
            intent.putExtra("door_room_name", item.getRoomName());
            intent.putExtra("door_room_id", item.getRoomId());
            intent.putExtra("door_unread_count", item.getIs_unread());
            intent.putExtra("door_module_id", item.getModuleId());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("isPIRLongClick")) {
            //  if (item.getIsActive() != -1)
            dialogNewPassword(item);
        } else if (action.equalsIgnoreCase("pir")) {
            deviceOnOff(item, position);
        }
    }

    public void showBottomSheetDialogCamera(CameraVO item) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + item.getCamera_name() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (item.getJetson_device_id().startsWith("JETSON-")) {
                    Intent intent = new Intent(RoomDetailActivity.this, CameraEdit.class);
                    intent.putExtra("cameraList", cameraList);
                    intent.putExtra("cameraSelcet", item);
                    intent.putExtra("JETSON_ID", item.getJetson_device_id());
                    intent.putExtra("isjetsonedit", true);
                    intent.putExtra("isEditable", false);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RoomDetailActivity.this, CameraEdit.class);
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
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteCamera(item.getCamera_id());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }


    public void showBottomSheetDialog(RoomVO roomVO) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(RoomDetailActivity.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + roomVO.getRoomName() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(RoomDetailActivity.this, RoomEditActivity_v2.class);
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
    }

    private void deleteRoomAction(final RoomVO room) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ? \nNote: All the devices and sensors associated with this room will be deleted", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteRoom(room);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(this.getFragmentManager(), "dialog");
    }


    /**
     * Delete Room
     *
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        if (!ActivityHelper.isConnectingToInternet(RoomDetailActivity.this)) {
            ChatApplication.showToast(RoomDetailActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        SpikeBotApi.getInstance().DeleteRoom(roomVO.getRoomId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(RoomDetailActivity.this, message);
                        finish();
                    } else {
                        ChatApplication.showToast(RoomDetailActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.logDisplay("getconfigureData onFailure ");
                ChatApplication.showToast(RoomDetailActivity.this, getResources().getString(R.string.disconnect));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("getconfigureData onFailure ");
                ChatApplication.showToast(RoomDetailActivity.this, getResources().getString(R.string.disconnect));
            }
        });
    }

    /**
     * DeleteCamera
     */

    private void deleteCamera(String camera_id) {


        if (!ActivityHelper.isConnectingToInternet(RoomDetailActivity.this)) {
            Toast.makeText(RoomDetailActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }

        ActivityHelper.showProgressDialog(RoomDetailActivity.this, "Please wait...", false);

        SpikeBotApi.getInstance().DeleteCamera(camera_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(RoomDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            getCameraDetails();
                        }
                    } else {
                        Toast.makeText(RoomDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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

    /*panel on off */
    private void roomPanelOnOff(final PanelVO panelVO, final RoomVO roomVO, String roomId, String panelId, int panel_status, int type) {
        ChatApplication.logDisplay("roomPanelOnOff ");
        callPanelOnOffApi(roomId, panelId, panel_status, type);

    }

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
                        ChatApplication.showToast(RoomDetailActivity.this, message);
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
        runOnUiThread(new Runnable() {
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
                        ChatApplication.showToast(RoomDetailActivity.this, message);
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


    /*send ir remote command */
    private void sendRemoteCommand(final DeviceVO item, String philipslongClick) {


        if (!ActivityHelper.isConnectingToInternet(RoomDetailActivity.this)) {
            ChatApplication.showToast(RoomDetailActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().SendRemoteCommand(item, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
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
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                ChatApplication.showToast(RoomDetailActivity.this, item.getSensor_name() + " " + getResources().getString(R.string.ir_error));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                ChatApplication.showToast(RoomDetailActivity.this, item.getSensor_name() + " " + getResources().getString(R.string.ir_error));
            }
        });

    }

    private void callDoorRemotly(DeviceVO item) {
        showProgressDialog(RoomDetailActivity.this, "Please Wait...", false);
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

    private void doorOpenCLose(DeviceVO item) {
        if (mSocket != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("lock_id", item.getLock_id());
                object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
                object.put("door_sensor_status", item.getDoor_sensor_status().equals("1") ? 0 : 1);
                object.put("lock_status", item.getDoor_sensor_status().equals("1") ? 0 : 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatApplication.logDisplay("door json " + object);
            mSocket.emit("changeLockStatus", object);
        }
    }

    private void dialogNewPassword(DeviceVO item) {

        isPIREdit = false;

        mPIRModedialog = new Dialog(RoomDetailActivity.this);
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
                RoomDetailActivity.this,
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

                if (item.getIsActive() != -1) {

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
                        Toast.makeText(RoomDetailActivity.this, "Please tap on edit icon for update the device.", Toast.LENGTH_SHORT).show();
                    }
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
                ChatApplication.keyBoardHideForce(RoomDetailActivity.this);
                if (isPIREdit) {
                    item.setDeviceName(edSensorName.getText().toString());
                    UpdatePIRDevice(item);
                } else {
                    Toast.makeText(RoomDetailActivity.this, "Please edit before save the device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPIRModedialog.show();
    }

    private void UpdatePIRDevice(DeviceVO item) {

        if (!ActivityHelper.isConnectingToInternet(RoomDetailActivity.this)) {
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

                        //  Intent intent = new Intent(ChatApplication.getContext(), RoomDetailActivity.class);
                        // RoomDetailActivity.this.startActivity(intent);
                        mPIRModedialog.dismiss();
                        getRoomDetails();
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

    }

    public void showBottomSheetDialog_PIR(DeviceVO item) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(RoomDetailActivity.this, R.style.AppBottomSheetDialogTheme);
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
    }

    private void showDelete(DeviceVO item) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure want to delete?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deletePIRDetector(item);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(this.getFragmentManager(), "dialog");
    }

    private void deletePIRDetector(DeviceVO item) {
        ActivityHelper.showProgressDialog(RoomDetailActivity.this, "Please wait.", false);
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
                        ChatApplication.showToast(RoomDetailActivity.this, message);
                        Intent intent = new Intent(ChatApplication.getContext(), Main2Activity.class);
                        RoomDetailActivity.this.startActivity(intent);
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

    }

    /*camera token */
    private void callCameraToken(final CameraVO item, final String action) {
        showProgressDialog(RoomDetailActivity.this, " Please Wait...", false);
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
                        Intent intent = new Intent(RoomDetailActivity.this, CameraPlayer.class);
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


    /*change device status socket getting
    like devic eon /  off , value change
    * */
    private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
    };
    /**
     * Update panel status
     * on, off
     */
    private Emitter.Listener panelStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
    };

    /* door sensor open / close
     * sensor temp , door , ir remote */
    private Emitter.Listener changeModuleStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
    };

    /* uprecount like notification icon upper showing count*/
    private Emitter.Listener unReadCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(RoomDetailActivity.this, Constants.USER_ID))) {
                                sectionedExpandableLayoutHelper.updateBadgeCount(device_id, counter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener changeIrBlasterTemperature = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
    };

    private Emitter.Listener updateCameraCounter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_recording:
                Intent intent = new Intent(RoomDetailActivity.this, CameraPlayBack.class);
                intent.putExtra("cameraList", cameraList);
                startActivity(intent);
                break;
            case R.id.linear_preview:
                Intent i = new Intent(RoomDetailActivity.this, CameraGridActivity.class);
                if (TextUtils.isEmpty(jetson_id)) {
                    i.putExtra("isshowGridCamera", true);
                } else {
                    i.putExtra("jetson_device_id", jetson_id);
                }
                startActivity(i);
                break;
            case R.id.linear_refresh:
                getCameraDetails();
                break;
        }
    }
}
