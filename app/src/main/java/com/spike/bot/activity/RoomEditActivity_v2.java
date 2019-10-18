package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AddDeviceTypeListActivity;
import com.spike.bot.activity.AddDevice.AddExistingPanel;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.adapter.room.RoomEditAdapterV2;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.dialog.DeviceEditDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditActivity_v2 extends AppCompatActivity implements ItemClickRoomEditListener {

    RecyclerView mMessagesView;
    RoomEditAdapterV2 roomEditGridAdapter;
    EditText et_toolbar_title;
    RoomVO room;
    private Socket mSocket;
    boolean addRoom = false;
    private LinearLayout txt_empty_room, vv_delete_button;
    private Button btn_edit_room_delete;
    private ImageView empty_add_image;

    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    ArrayList<RoomVO> roomList = new ArrayList<>();

    Dialog dialog;
    DeviceEditDialog deviceEditDialog;
    public int typeSync = 0;
    public boolean addTempSensor = false;
    public static int SENSOR_TYPE_PANAL = 0, SENSOR_TYPE_DOOR = 1, SENSOR_TYPE_TEMP = 2, SENSOR_GAS = 5, Curtain = 6;
    String action = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_v2);
        room = (RoomVO) getIntent().getSerializableExtra("room");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Room Details");
        et_toolbar_title = findViewById(R.id.et_toolbar_title);
        txt_empty_room = findViewById(R.id.txt_empty_room);
        vv_delete_button = findViewById(R.id.vv_delete_button);
        txt_empty_room.setVisibility(View.GONE);
        btn_edit_room_delete = findViewById(R.id.btn_edit_room_delete);
        mMessagesView = findViewById(R.id.list_room_edit);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        dialog = new Dialog(this);

        startSocketConnection();

        et_toolbar_title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_toolbar_title.setText(room.getRoomName());
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Set room name max length 25
        et_toolbar_title.setFilters(filterArray);

        empty_add_image = findViewById(R.id.empty_add_image);

        //setting the recycler view
        mMessagesView.setLayoutManager(new GridLayoutManager(this, 1));
        roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), this, this);
        mMessagesView.setAdapter(roomEditGridAdapter);

        mMessagesView.getRecycledViewPool().setMaxRecycledViews(0, 1);

        et_toolbar_title.setImeOptions(EditorInfo.IME_ACTION_DONE);

        final View activityRootView = findViewById(R.id.coordinator_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > Common.dpToPx(getApplicationContext(), 100)) { // if more than 100 pixels, its probably a keyboard...
                    vv_delete_button.setVisibility(View.GONE);
                } else {
                    vv_delete_button.setVisibility(View.VISIBLE);
                }
            }
        });
        clickLister();
        getDeviceList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (IRRemoteBrandListActivity.arrayList != null) {
            IRRemoteBrandListActivity.arrayList = null;
        }
        Constants.isWifiConnect = false;
        Constants.isWifiConnectSave = false;
        if (ChatApplication.isEditActivityNeedResume) {
            ChatApplication.isEditActivityNeedResume = false;
            getDeviceList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void clickLister() {

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RoomEditActivity_v2.this, AddDeviceTypeListActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_edit_room_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoomAction(room);
            }
        });
    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();
        if (mSocket != null) {
            mSocket.on("configureDevice", configureDevice);
        }
    }

    private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        //  {"message":"","module_id":"1571132453177_me7iAEteu","room_list":
                        //  [{"room_id":"1571125128842_9VIk2guMv","room_name":"jhanvi"},{"room_id":"1571129676518_D_axH9LXh","room_name":"Shivam Room"}]}
                        roomIdList.clear();
                        roomNameList.clear();

                        //message, gas_sensor_module_id,room_list
                        JSONObject object = new JSONObject(args[0].toString());

                        ChatApplication.logDisplay("gas is " + object);

                        if (TextUtils.isEmpty(object.getString("message"))) {

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            //  vv_delete_button.setVisibility(View.GONE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            //  vv_delete_button.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Delete Room
     *
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("room_id", roomVO.getRoomId());
            object.put("room_name", roomVO.getRoomName());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_ROOM;

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();
                    }
                    ChatApplication.showToast(getApplicationContext(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(RoomEditActivity_v2.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();
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
        newFragment.show(getFragmentManager(), "dialog");
    }

    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
        }

    };

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent RoomEditActivity_v2.this inm AndroidManifest.xml.
        // Handle the popup menu dialog

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            openAddPopup(findViewById(R.id.action_add));
            return true;
        } else if (id == R.id.action_save) {
            ActivityHelper.hideKeyboard(this);

            roomEditGridAdapter.getPanelEditValue();

            ArrayList<PanelVO> mDataArrayList = roomEditGridAdapter.getDataArrayList();

            ArrayList<PanelVO> panelVOs = new ArrayList<>();
            for (int i = 0; i < mDataArrayList.size(); i++) {

                if (mDataArrayList.get(i) != null) {
                    PanelVO panelvo = (PanelVO) mDataArrayList.get(i);
                    panelVOs.add(panelvo);
                }
            }

            saveRoom(panelVOs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveRoom(ArrayList<PanelVO> panelVOs) {

        if (TextUtils.isEmpty(et_toolbar_title.getText().toString())) {
            et_toolbar_title.setError(Html.fromHtml("<font color='#FFFFFF'>Enter Room Name</font>"));
            et_toolbar_title.requestFocus();
            return;
        }

        boolean isEmptyPanel = false;

        JSONObject obj = new JSONObject();
        try {

            //{
            //	"room_id": "1571125128842_9VIk2guMv",
            //	"room_name": "",
            //	"panel_data": [
            //		{
            //			"panel_id": "",
            //			"panel_name" : ""
            //		}
            //	]
            //}
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("room_id", room.getRoomId());
            obj.put("room_name", et_toolbar_title.getText().toString());

            //panelVOs

            JSONArray panelArray = new JSONArray();
            for (PanelVO panel : panelVOs) {

                JSONObject objPanel = new JSONObject();
                objPanel.put("panel_id", panel.getPanelId());
                objPanel.put("panel_name", panel.getPanelName());
                panelArray.put(objPanel);

                if (TextUtils.isEmpty(panel.getPanelName())) {
                    isEmptyPanel = true;
                }
            }
            obj.put("paneldata", panelArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isEmptyPanel) {
            return;
        }
        String url = ChatApplication.url + Constants.SAVE_ROOM_AND_PANEL_NAME;

        ChatApplication.logDisplay("ur is " + url + " " + obj);
        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Please wait...", false);

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard = true;
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();

                    } else if (code == 301) {
                        ChatApplication.showToast(getApplicationContext(), message);
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    public void getDeviceList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_id", room.getRoomId());
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.GET_EDIT_ROOM_INFO;

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    ArrayList<RoomVO> roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    ChatApplication.logDisplay("result is " + roomArray);
                    roomList = JsonHelper.parseRoomArray(roomArray, false);
                    if (roomList.size() > 0) {
                        room = roomList.get(0);
                        et_toolbar_title.setText(room.getRoomName());

                        if (room.getPanelList() != null && room.getPanelList().size() > 0) {
                            roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), RoomEditActivity_v2.this, RoomEditActivity_v2.this);
                            mMessagesView.setAdapter(roomEditGridAdapter);
                            roomEditGridAdapter.notifyDataSetChanged();
                        }

                    }
                    ActivityHelper.dismissProgressDialog();
                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    if (room.getPanelList().size() == 0) {
                        txt_empty_room.setVisibility(View.VISIBLE);
                        mMessagesView.setVisibility(View.GONE);
                    } else {
                        txt_empty_room.setVisibility(View.GONE);
                        mMessagesView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
                if (room.getPanelList().size() == 0) {
                    txt_empty_room.setVisibility(View.VISIBLE);
                    mMessagesView.setVisibility(View.GONE);
                } else {
                    txt_empty_room.setVisibility(View.GONE);
                    mMessagesView.setVisibility(View.VISIBLE);
                }
            }
        }).execute();
    }

    @Override
    public void itemClicked(RoomVO item, String action, View view) {
    }

    @Override
    public void itemClicked(final PanelVO panelVO, String action, View view) {

        if (action.equalsIgnoreCase("delete")) {
            //
            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {

                    deletePanel(panelVO);

                    ChatApplication.isRefreshDashBoard = true;
                    ChatApplication.isMainFragmentNeedResume = true;
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        } else if (action.equalsIgnoreCase("edit")) {

            Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
            intentPanel.putExtra("isDeviceAdd", true);
            intentPanel.putExtra("panelV0", panelVO);
            intentPanel.putExtra("roomId", room.getRoomId());
            intentPanel.putExtra("roomName", room.getRoomName());
            intentPanel.putExtra("panel_id", "" + panelVO.getPanelId());
            intentPanel.putExtra("panel_name", "" + panelVO.getPanelName());
            startActivity(intentPanel);
        } else if (action.equals("sensorPanel")) {
        }
    }

    @Override
    public void itemClicked(DeviceVO section, String action, View view) {
        if (action.equalsIgnoreCase("1")) {
            ActivityHelper.showProgressDialog(this, "Please wait.", false);
            showDeviceEditDialog(section);
        } else if (action.equalsIgnoreCase("isSensorClick")) {
        }
    }

    /**
     * @param deviceVO
     */
    private void showDeviceEditDialog(final DeviceVO deviceVO) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }
        String url = ChatApplication.url + Constants.CHECK_INDIVIDUAL_SWITCH_DETAILS;

        JSONObject obj = new JSONObject();
        try {
            /*obj.put("module_id","1C7FC712004B1200");
            obj.put("device_id","4");*/
            obj.put("module_id", deviceVO.getModuleId());
            obj.put("device_id", deviceVO.getDeviceId() + "");
            obj.put("room_device_id", deviceVO.getRoomDeviceId() + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    deviceEditDialog = new DeviceEditDialog(RoomEditActivity_v2.this, deviceVO, result, new ICallback() {
                        @Override
                        public void onSuccess(String str) {
                            ChatApplication.isRefreshDashBoard = true;
                            ChatApplication.isMainFragmentNeedResume = true;
                            getDeviceList();
                        }
                    });
                    if (!deviceEditDialog.isShowing()) {
                        deviceEditDialog.show();
                    }

                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();

    }

    private void deletePanel(PanelVO panelVO) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject object = new JSONObject();
        try {
            object.put("panel_id", panelVO.getPanelId());
            object.put("panel_name", panelVO.getPanelName());
            object.put("room_id", room.getRoomId());
            object.put("room_name", room.getRoomName());
            object.put("panel_type", panelVO.getPanel_type());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.DELETE_ROOM_PANEL;


        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        getDeviceList();
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    public void openAddPopup(final View v) {//,final ICallBackAction actionCallBack

        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, v);

        popup.getMenuInflater().inflate(R.menu.menu_room_edit_add_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_existing:
                        action = "add_new_existing";

                        Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                        intentPanel.putExtra("roomId", room.getRoomId());
                        intentPanel.putExtra("roomName", room.getRoomName());
                        intentPanel.putExtra("isDeviceAdd", false);
                        startActivity(intentPanel);
                        break;
                    case R.id.action_add_schedule:
                        action = "action_add_schedule";
                        Intent intent = new Intent(getApplicationContext(), ScheduleListActivity.class);
                        intent.putExtra("moodId3", room.getRoomId());
                        intent.putExtra("roomId", room.getRoomId());
                        intent.putExtra("roomName", room.getRoomName());
                        intent.putExtra("selection", 1);
                        intent.putExtra("isMoodAdapter", true);
                        startActivity(intent);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     *
     */
    private void showPanelOption() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_add_from_existing = dialog.findViewById(R.id.add_from_existing);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getgasConfigData(SENSOR_TYPE_PANAL);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getUnasignedDeviceList();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_add_from_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                intentPanel.putExtra("roomId", room.getRoomId());
                intentPanel.putExtra("roomName", room.getRoomName());
                intentPanel.putExtra("isDeviceAdd", false);
                startActivity(intentPanel);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void getgasConfigData(int type) {
        if (!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Searching Device attached ", false);
        startTimer();

        addTempSensor = true;
        //curtain | gas_sensor | temp_sensor | smart_remote | door_sensor | repeater | 5 | 5f | heavy_load | double_heavy_load
        String url = ChatApplication.url + Constants.deviceconfigure;
        if (type == SENSOR_GAS) {
            url = url + "gas_sensor";
        } else if (type == Curtain) {
            url = url + "curtain";
        } else if (type == SENSOR_TYPE_TEMP) {
            url = url + "temp_sensor";
        } else if (type == SENSOR_TYPE_DOOR) {
            url = url + "door_sensor";
        } else {
            url = url + "5";
        }

        typeSync = type;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(RoomEditActivity_v2.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    /*
     * get method fro call getOriginalDevices api
     * delete device list panel list
     * */
    private void getUnasignedDeviceList() {

        roomList.clear();
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = "";
        url = ChatApplication.url + Constants.GET_ORIGINAL_DEVICES + "/0";

        // ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait...",false);

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();

                try {

                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseExistPanelArray(roomArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
                if (roomList.size() == 0) {
                    ChatApplication.showToast(getApplicationContext(), "No Unassigned Panel Found.");
                } else {

                    Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                    intentPanel.putExtra("roomId", room.getRoomId());
                    intentPanel.putExtra("roomName", room.getRoomName());
                    intentPanel.putExtra("isSync", true);
                    intentPanel.putExtra("isDeviceAdd", false);
                    startActivity(intentPanel);
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();

    }

}
