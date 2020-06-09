package com.spike.bot.activity.TTLock;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.Sensor.DoorSensorInfoActivity;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.DoorAlertAdapter;
import com.spike.bot.adapter.DoorSensorInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment12;
import com.spike.bot.model.LockObj;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.receiver.ConnectivityReceiver;
import com.ttlock.bl.sdk.api.TTLockClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 14/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class YaleLockInfoActivity extends AppCompatActivity implements View.OnClickListener, DoorSensorInfoAdapter.OnNotificationContextMenu, ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView door_sensor_list, recyclerAlert;
    private DoorSensorInfoAdapter tempSensorInfoAdapter;
    public Toolbar toolbar;

    CardView cardview_yalelook;
    private EditText sensorName;
    private TextView txt_empty_notification, txt_empty_notificationALert, txtTempCount, txtAlertTempCount, txt_battery_level;
    private ImageView img_door_on, imgBattery, view_rel_badge, imgLock, imgLockDelete,
            imgDoorDelete, imgLockBattery,img_passcode,img_onetimecode,img_log_adminpasscode,img_log_guestpasscode;
    private TextView batteryPercentage, txt_setpasscode, txt_setonetimecode, doorAddButton;
    private LinearLayout linearAlertDown, linearAlertExpand, linearLock, linearAddlockOptin, linear_yale_status, linear_door,
            linearLockDoor, linear_lock_enable_disable, linear_set_passcode,linear_set_onetimecode,
            linear_door_batterylvl, linear_battery, linear_pushnotify;
    public CardView cardViewLock, cardViewDoor;
    private ToggleButton toggleAlert;
    private TextView txtAlertCount;
    private AppCompatTextView txtAddLock, txtBettrylock, txtlockStatus, txtyDooryaleStatus, txtAutoLock;
    private Button btn_delete;
    public SwitchCompat switchAutoLock, switch_temp_lock;
    private boolean flagAlert = false, isRefreshAll = false;
    View view_section;
    //Declare timer
    CountDownTimer cTimer = null;

    //    public DoorSensorResModel.DATA.DoorList[] tempLists = new DoorSensorResModel.DATA.DoorList[0];
    private RemoteDetailsRes doorSensorResModelData;
    private RemoteDetailsRes.Data doorSensorResModel;
    private String door_sensor_id, door_room_name, door_unread_count = "", door_subtype = "", door_room_id, door_module_id, mSensorName = "",
            mac_address = "", enable_lock_unlock_from_app = "",
            strsensorname = "", strsensorpasscode = "",strguestpasscode="";
    private Socket mSocket;
    Dialog editdialog;
    //gatwway list
    ArrayList<LockObj> gatewayList = new ArrayList<>();
    ArrayList<LockObj> locklistAll = new ArrayList<>();
    public static String mSocketCountVal;
    private boolean isRefresh = false, setChecked = false;

    /**
     * @param o
     * @param isEdit
     */

    Dialog doorSensorNotificationDialog;

    /**
     * @param edtText
     * @param isStartTime
     */
    private String mStartTime = "";
    private String mEndTime = "";
    private SimpleDateFormat startTimeFormat;


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showToast("Disconnected, Please check your internet connection");
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_sensor_info);

        ChatApplication.logDisplay("door call is start " + ChatApplication.url);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        door_sensor_id = getIntent().getStringExtra("door_sensor_id");
        door_room_name = getIntent().getStringExtra("door_room_name");
        door_room_id = getIntent().getStringExtra("door_room_id");
        door_unread_count = getIntent().getStringExtra("door_unread_count");
        door_module_id = getIntent().getStringExtra("door_module_id");
        door_subtype = getIntent().getStringExtra("door_subtype");
    }

    @Override
    protected void onResume() {
        super.onResume();

        ChatApplication.logDisplay("door call is " + ChatApplication.url);
        if (ChatApplication.isLogResume) {
            ChatApplication.isLogResume = false;
            ChatApplication.isLocalFragmentResume = true;
            mSocketCountVal = "0";
        }
        bindView();

        getDoorSensorDetails();
        view_rel_badge.setClickable(true);
        ChatApplication.logDisplay("door call is " + ChatApplication.url);

        startSocketConnection();
    }

    private void bindView() {

        door_sensor_list = findViewById(R.id.sensor_list);
        recyclerAlert = findViewById(R.id.recyclerAlert);
        door_sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        door_sensor_list.setVerticalScrollBarEnabled(true);

        view_rel_badge = findViewById(R.id.view_rel_badge);

        sensorName = findViewById(R.id.sensor_name);
            linearAlertDown = findViewById(R.id.linearAlertDown);
        linearAlertExpand = findViewById(R.id.linearAlertExpand);
        toggleAlert = findViewById(R.id.toggleAlert);
        txtAlertTempCount = findViewById(R.id.txtAlertTempCount);
        txt_setpasscode = findViewById(R.id.txt_setpasscode);
        txt_setonetimecode = findViewById(R.id.txt_setonetimecode);

        sensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        sensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});


        //sensorLog = (TextView) findViewById(R.id.iv_icon_badge);
        imgBattery = findViewById(R.id.img_battery);
        img_door_on = findViewById(R.id.img_door_on);
        batteryPercentage = findViewById(R.id.txt_bettery_per);
        //notiSwitchOnOff = (SwitchCompat) findViewById(R.id.switch_noti_onoff);
        doorAddButton = findViewById(R.id.btnAdd);
        txtTempCount = findViewById(R.id.txtTempCount);
        btn_delete = findViewById(R.id.btn_delete);
        txt_empty_notification = findViewById(R.id.txt_empty_notification_alert);
        txt_empty_notificationALert = findViewById(R.id.txt_empty_notification);
        txtAlertCount = findViewById(R.id.txtAlertCount);
        txtAddLock = findViewById(R.id.txtAddLock);
        txt_battery_level = findViewById(R.id.txt_battery_level);
        linearLock = findViewById(R.id.linearLock);
        imgLock = findViewById(R.id.imgLock);
        linearLockDoor = findViewById(R.id.linearLockDoor);
        linearAddlockOptin = findViewById(R.id.linearAddlockOptin);
        linear_lock_enable_disable = findViewById(R.id.linear_lock_enable_disable);
        linear_set_passcode = findViewById(R.id.linear_set_passcode);
        linear_set_onetimecode = findViewById(R.id.linear_set_onetimecode);
        txtlockStatus = findViewById(R.id.txtlockStatus);
        txtBettrylock = findViewById(R.id.txtBettrylock);
        imgLockBattery = findViewById(R.id.imgLockBattery);
        txtyDooryaleStatus = findViewById(R.id.txtyDooryaleStatus);
        cardViewDoor = findViewById(R.id.cardViewDoor);
        cardViewLock = findViewById(R.id.cardViewLock);
        switchAutoLock = findViewById(R.id.switchAutoLock);
        switch_temp_lock = findViewById(R.id.switch_temp_lock);
        txtAutoLock = findViewById(R.id.txtAutoLock);
        imgDoorDelete = findViewById(R.id.imgDoorDelete);
        imgLockDelete = findViewById(R.id.imgLockDelete);
        img_passcode = findViewById(R.id.img_passcode);
        img_onetimecode = findViewById(R.id.img_onetimecode);
        view_section = findViewById(R.id.view_section);
        linear_door_batterylvl = findViewById(R.id.linear_door_batterylvl);
        linear_battery = findViewById(R.id.linear_battery);
        linear_pushnotify = findViewById(R.id.linear_pushnotify);
        cardview_yalelook = findViewById(R.id.cardview_yalelook);
        linear_yale_status = findViewById(R.id.linear_yale_status);
        linear_door = findViewById(R.id.linear_door);
        img_log_adminpasscode  = findViewById(R.id.img_log_adminpasscode);
        img_log_guestpasscode = findViewById(R.id.img_log_guestpasscode);

        linear_yale_status.setVisibility(View.VISIBLE);
        cardview_yalelook.setVisibility(View.VISIBLE);
        linear_pushnotify.setVisibility(View.GONE);
        linear_battery.setVisibility(View.GONE);
        view_section.setVisibility(View.GONE);
        linear_door_batterylvl.setVisibility(View.GONE);
        linear_door.setVisibility(View.GONE);

        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btn_delete.setVisibility(View.GONE);
        }
        btn_delete.setVisibility(View.GONE);
        btn_delete.setOnClickListener(this);
        view_rel_badge.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);
        txtAddLock.setOnClickListener(this);
        imgLock.setOnClickListener(this);
        txtAutoLock.setOnClickListener(this);
        imgLockDelete.setOnClickListener(this);
        imgDoorDelete.setOnClickListener(this);
        img_passcode.setOnClickListener(this);

        toggleAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagAlert) {
                    flagAlert = false;
                    toggleAlert.setChecked(flagAlert);
                    linearAlertExpand.setVisibility(View.GONE);
                } else {
                    flagAlert = true;
                    toggleAlert.setChecked(flagAlert);
                    linearAlertExpand.setVisibility(View.VISIBLE);

                    if (recyclerAlert.getVisibility() == View.VISIBLE) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 400);
                        recyclerAlert.setLayoutParams(lp);
                    }
                }
            }
        });
        linear_lock_enable_disable.setVisibility(View.VISIBLE);
        linear_set_passcode.setVisibility(View.VISIBLE);
        linear_set_onetimecode.setVisibility(View.VISIBLE);
        imgBattery.setVisibility(View.GONE);
        doorAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorSensorNotification(null, false);
            }
        });

        switch_temp_lock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setChecked = true;
                return false;
            }
        });

        switch_temp_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (setChecked) {
                    setChecked = false;
                    if (isChecked) {
                        enable_lock_unlock_from_app = "1";
                        updateDoorSensor(strsensorname, strsensorpasscode, strguestpasscode,enable_lock_unlock_from_app, editdialog);
                    } else {
                        enable_lock_unlock_from_app = "0";
                        updateDoorSensor(strsensorname, strsensorpasscode, strguestpasscode,enable_lock_unlock_from_app, editdialog);
                    }
                }
            }
        });

        linear_set_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(YaleLockInfoActivity.this, SetPasscodeActivity.class);
                intent.putExtra("device_id", door_sensor_id);
                intent.putExtra("enable_lock_unlock_from_app", enable_lock_unlock_from_app);
                intent.putExtra("device_name", strsensorname);
                startActivity(intent);*/

                dialogEditpasscode();
            }
        });

        txt_setpasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditpasscode();
            }
        });

        linear_set_onetimecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditguestpasscode();
            }
        });

        txt_setonetimecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditguestpasscode();
            }
        });

        img_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_setpasscode.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_passcode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    txt_setpasscode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    img_passcode.setImageResource(R.drawable.eye);
                    //Hide Password
                    txt_setpasscode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        img_onetimecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_setonetimecode.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_onetimecode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    txt_setonetimecode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    img_onetimecode.setImageResource(R.drawable.eye);
                    //Hide Password
                    txt_setonetimecode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        img_log_adminpasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YaleLockInfoActivity.this, DeviceLogActivity.class);
                intent.putExtra("ROOM_ID", door_sensor_id);
                intent.putExtra("Mood_Id", "" + door_sensor_id);
                intent.putExtra("activity_type", "door");
                intent.putExtra("IS_SENSOR", true);
                intent.putExtra("tabSelect", "show");
                intent.putExtra("isCheckActivity", "yalelock");
                intent.putExtra("isRoomName", "" + sensorName.getText().toString());
                startActivity(intent);
            }
        });

        img_log_guestpasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YaleLockInfoActivity.this, DeviceLogActivity.class);
                intent.putExtra("ROOM_ID", door_sensor_id);
                intent.putExtra("Mood_Id", "" + door_sensor_id);
                intent.putExtra("activity_type", "door");
                intent.putExtra("IS_SENSOR", true);
                intent.putExtra("tabSelect", "show");
                intent.putExtra("isCheckActivity", "yalelock");
                intent.putExtra("isRoomName", "" + sensorName.getText().toString());
                startActivity(intent);
            }
        });
    }


    /*start connection*/
    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("doorsensorvoltage", doorsensorvoltage);
            mSocket.on("unReadCount", unReadCount);
            mSocket.on("changeDeviceStatus", changeDoorSensorStatus);
        }

    }

    /*battery vol socket getting socket */
    private Emitter.Listener doorsensorvoltage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            YaleLockInfoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_order = object.getString("room_order");
                            String door_sensor_module_id = object.getString("door_sensor_module_id");
                            String doorSensorVoltage = object.getString("doorSensorVoltage");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    /* unread count getting socket*/
    private Emitter.Listener unReadCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            YaleLockInfoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String sensor_type = object.getString("sensor_type");
                            String sensor_unread = object.getString("sensor_unread");
                            String module_id = object.getString("module_id");
                            String room_id = object.getString("room_id");

                            if (sensor_type.equalsIgnoreCase("door") && door_module_id.equalsIgnoreCase(module_id) && door_room_id.equalsIgnoreCase(room_id)) {
                                mSocketCountVal = sensor_unread;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    /* door/lock status getting*/
    private Emitter.Listener changeDoorSensorStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            //{"door_sensor_id":"1568731205713_SbQBf-JvXS","door_sensor_status":0,"door_lock_status":0}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("door is " + object);

                            String device_type = object.getString("device_type");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int device_sub_status = object.optInt("device_sub_status");

                            if (device_id.equalsIgnoreCase(door_sensor_id)) {
                                doorSensorResModel.getDevice().setDeviceStatus(device_status);
                                inActivieStatusDoor(1);
                            }


//                            if (door_sensorid.equals(door_sensor_id)) {
//                                if (!TextUtils.isEmpty(door_lock_status) && !door_lock_status.equals("null") && door_lock_status.length() > 0) {
//                                    doorSensorResModel.getDate().getDoorLists()[0].setDoor_lock_status(door_lock_status);
//                                }
//
//                                if (!TextUtils.isEmpty(door_sensor_status) && !door_sensor_status.equals("null") && !door_sensor_status.equals("null")) {
//                                    doorSensorResModel.getDate().getDoorLists()[0].setmDoorSensorStatus("" + door_sensor_status);
//                                }
//
//                                //onlydoor, subtype=1
//                                //only lock, subtype=2
//                                //door+lock, subtype=3
//                                if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("2")) {
//                                    setLockStatus(2);
//                                } else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("3")) {
//                                    setLockStatus(3);
//                                } else {
//                                    setLockStatus(4);
//                                }
//                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("doorsensorvoltage", doorsensorvoltage);
            mSocket.off("unReadCount", unReadCount);
            mSocket.on("changeDeviceStatus", changeDoorSensorStatus);
        }
    }

    /**
     * get individual door sensor details
     */
    private void getDoorSensorDetails() {

        if (!ChatApplication.url.startsWith("http")) {
            ChatApplication.url = "http://" + ChatApplication.url;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String url = ChatApplication.url + Constants.deviceinfo;

        ChatApplication.logDisplay("door " + url + " ");

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", door_sensor_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("door sensor" + " " + url + " " + object);


        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("door sensor is " + result);
                doorSensorResModelData = Common.jsonToPojo(result.toString(), RemoteDetailsRes.class);
                doorSensorResModel = doorSensorResModelData.getData();

                if (doorSensorResModelData.getCode() == 200) {
                    fillData(doorSensorResModel);

                }

                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * fill Date from model class
     *
     * @param doorSensorResModel
     */

    private void fillData(RemoteDetailsRes.Data doorSensorResModel) {


        toolbar.setTitle(doorSensorResModel.getDevice().getDeviceName());
        sensorName.setText(doorSensorResModel.getDevice().getDeviceName());
        mSensorName = doorSensorResModel.getDevice().getDeviceName();
        sensorName.setSelection(sensorName.getText().length());
        enable_lock_unlock_from_app = doorSensorResModel.getDevice().getMeta_enable_lock_unlock_from_app();

        strsensorname = doorSensorResModel.getDevice().getDeviceName();
        strsensorpasscode = doorSensorResModel.getDevice().getMeta_pass_code();
        strguestpasscode = doorSensorResModel.getDevice().getMeta_onetime_code();

        txt_setpasscode.setText(strsensorpasscode);
        txt_setonetimecode.setText(strguestpasscode);


        if (enable_lock_unlock_from_app != null && !enable_lock_unlock_from_app.isEmpty()) {
            if (enable_lock_unlock_from_app.equalsIgnoreCase("1")) {
                switch_temp_lock.setChecked(true);
            } else {
                switch_temp_lock.setChecked(false);
            }
        }

        if (doorSensorResModel.getDevice().getMeta_battery_level() != null) {

            int perc = 0;
            if (!TextUtils.isEmpty("" + doorSensorResModel.getDevice().getMeta_battery_level())) {
                try {
                    perc = Integer.parseInt(doorSensorResModel.getDevice().getMeta_battery_level());
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
            }
            batteryPercentage.setText(perc + "%");


            if (doorSensorResModel.getDevice().getMeta_battery_level().equals("null")) {
                txt_battery_level.setText("-");
            } else {
                txt_battery_level.setText(perc + " %");
            }

            imgBattery.setImageResource(Common.getBatteryIcon("" + doorSensorResModel.getDevice().getMeta_battery_level()));

            if (perc >= 0 && perc <= 25)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_low));
            else if (perc >= 26 && perc <= 50)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_low1));
            else if (perc >= 51 && perc <= 75)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_medium));
            else if (perc >= 76 && perc <= 100)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_high));

            if (perc > 100) {
                txt_battery_level.setText("100" + "%");
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_high));
            }
        } else {
            txt_battery_level.setText("-");

        }

        if (doorSensorResModel.getAlerts() != null && doorSensorResModel.getAlerts().size() > 0) {
            door_sensor_list.setVisibility(View.VISIBLE);
            txtAlertTempCount.setVisibility(View.VISIBLE);
            txt_empty_notification.setVisibility(View.GONE);
            txtAlertTempCount.setText(doorSensorResModel.getAlerts().size() + " " + "Notification Added");
            tempSensorInfoAdapter = new DoorSensorInfoAdapter(doorSensorResModel.getAlerts(), true, YaleLockInfoActivity.this);
            door_sensor_list.setAdapter(tempSensorInfoAdapter);
        } else {
            txtAlertTempCount.setText("0" + " " + "Notification Added");
            door_sensor_list.setVisibility(View.GONE);
            txt_empty_notification.setVisibility(View.VISIBLE);
        }


//            if (doorSensorResModel.getDoor_subtype().equals("1")) {
//                cardViewLock.setVisibility(View.GONE);
//                cardViewDoor.setVisibility(View.VISIBLE);
//            } else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("2")) {
//                cardViewLock.setVisibility(View.VISIBLE);
//                cardViewDoor.setVisibility(View.GONE);
//            } else {
//                cardViewLock.setVisibility(View.VISIBLE);
//                cardViewDoor.setVisibility(View.VISIBLE);
//            }

//            if (TextUtils.isEmpty(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status()) ||
//                    doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equalsIgnoreCase("null")) {
//                doorSensorResModel.getDate().getDoorLists()[0].setDoor_lock_status("0");
//            }
//

        //onlydoor, subtype=1
        //only lock, subtype=2
        //door+lock, subtype=3

//            if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("1")) {
        cardViewDoor.setVisibility(View.VISIBLE);
        cardViewLock.setVisibility(View.GONE);

//                img_door_on.setImageResource(doorSensorResModel.getDevice().getDeviceStatus().equals("1") ? R.drawable.off_door : R.drawable.on_door);
//                txtDoorStatus.setText(doorSensorResModel.getDevice().getDeviceStatus().equals("1") ? "Door Close" : "Door Open");

        setLockStatus(4);
//            } else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("2")) {
//
//                cardViewDoor.setVisibility(View.GONE);
//                cardViewLock.setVisibility(View.VISIBLE);
//                setLockStatus(2);


//                switchAutoLock.setChecked(doorSensorResModel.getDate().getDoorLists()[0].getIs_autolock_enable() == 1 ? true : false);

        if (!isRefreshAll) {
        }
//            } else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("3")) {
//                cardViewDoor.setVisibility(View.VISIBLE);
//                cardViewLock.setVisibility(View.VISIBLE);
//                setLockStatus(3);
//
//                switchAutoLock.setChecked(doorSensorResModel.getDate().getDoorLists()[0].getIs_autolock_enable() == 1 ? true : false);
//                if (!isRefreshAll) {
//                    checkBridgeDetail();
//                }
//            } else {
//                linearLock.setVisibility(View.GONE);
//            }

        isRefreshAll = false;

//            if (doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs() != null) {
//                if (doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length > 0) {
//                    int countTemp = doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length;
//                    if (countTemp > 99) {
//                        txtAlertCount.setText("99+");
//                    } else {
//                        txtAlertCount.setText("" + countTemp);
//                    }
//
//                } else {
//                    txtAlertCount.setText("0");
//                }

        if (doorSensorResModel.getUnseenLogs() != null && doorSensorResModel.getUnseenLogs().size() > 0) {

            int countTemp = doorSensorResModel.getUnseenLogs().size();
            if (countTemp > 99) {
                txtAlertCount.setText("99+");
            } else {
                txtAlertCount.setText("" + countTemp);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(YaleLockInfoActivity.this);
            recyclerAlert.setLayoutManager(linearLayoutManager);
            DoorAlertAdapter doorAlertAdapter = new DoorAlertAdapter(YaleLockInfoActivity.this, doorSensorResModel.getUnseenLogs());
            recyclerAlert.setAdapter(doorAlertAdapter);
        } else {
            txtAlertCount.setVisibility(View.GONE);
            recyclerAlert.setVisibility(View.GONE);
            txt_empty_notificationALert.setVisibility(View.VISIBLE);
        }
//            } else {
//                txtAlertCount.setVisibility(View.GONE);
//                recyclerAlert.setVisibility(View.GONE);
//                txt_empty_notificationALert.setVisibility(View.VISIBLE);
//            }


        switchAutoLock.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onClick() {
                super.onClick();
                switchAutoLock.setChecked(switchAutoLock.isChecked());
                Autolockdialog();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                switchAutoLock.setChecked(switchAutoLock.isChecked());
                Autolockdialog();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                switchAutoLock.setChecked(switchAutoLock.isChecked());
                Autolockdialog();
            }
        });

    }

    //start timer function
    public void startTimer() {
        cTimer = new CountDownTimer(9500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
            }
        };
        cTimer.start();
    }


    //cancel timer
    public void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    /*auto lock enable/disable for
     * every 10 after auto lock */
    private void Autolockdialog() {
        final Dialog dialog = new Dialog(YaleLockInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_auto_lock);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        if (switchAutoLock.isChecked()) {
            txtTitle.setText("Disable AutoLock");
        } else {
            txtTitle.setText("Enable AutoLock");
        }
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAutoLock.setChecked(switchAutoLock.isChecked());
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(YaleLockInfoActivity.this);
                if (!isBtEnable) {
                    TTLockClient.getDefault().requestBleEnable(YaleLockInfoActivity.this);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setLockStatus(int type) {
//        if (type == 2) {
//            if (doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active().equals("-1")) {
//                inActivieStatus(-1);
//            } else {
//                imgLock.setImageResource(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? R.drawable.unlock_only : R.drawable.lock_only);
//                txtlockStatus.setText(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? "Unlocked" : "Locked");
//                txtlockStatus.setTextColor(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
//                imgLock.setClickable(true);
//            }
//        } else if (type == 4) {

        if (doorSensorResModel.getDevice().getIsActive().contains("n")) {
            inActivieStatusDoor(-1);
        } else {
            inActivieStatusDoor(1);
        }
        /*if (doorSensorResModel.getDevice().getIsActive().equals("-1")) {
            img_door_on.setImageResource(R.drawable.gray_lock_disabled);
            txtyDooryaleStatus.setText("InActive");
            txtyDooryaleStatus.setTextColor(getResources().getColor(R.color.automation_red));
            img_door_on.setClickable(false);
        } else {
                img_door_on.setImageResource(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? R.drawable.unlock_only : R.drawable.lock_only);
                txtyDooryaleStatus.setText(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? "Unlocked" : "Locked");
                txtyDooryaleStatus.setTextColor(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
                img_door_on.setClickable(true);

        }*/
//        } else {
//            imgLock.setImageResource(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? R.drawable.unlock_only : R.drawable.lock_only);
//            txtlockStatus.setText(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? "Unlocked" : "Locked");
//            txtlockStatus.setTextColor(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
//
//            img_door_on.setImageResource(doorSensorResModel.getDate().getDoorLists()[0].getmDoorSensorStatus().equals("1") ? R.drawable.on_door : R.drawable.off_door);
//            txtDoorStatus.setText(doorSensorResModel.getDate().getDoorLists()[0].getmDoorSensorStatus().equals("1") ? "Door Open" : "Door Close");
//            txtDoorStatus.setTextColor(doorSensorResModel.getDate().getDoorLists()[0].getmDoorSensorStatus().equals("1") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
//
//            if (doorSensorResModel.getDate().getDoorLists()[0].getIs_door_active().equals("-1")) {
//                img_door_on.setImageResource(R.drawable.door_off_inactive);
//                txtDoorStatus.setText("InActive");
//                txtDoorStatus.setTextColor(getResources().getColor(R.color.automation_red));
//            } else if (doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active().equals("-1")) {
//                inActivieStatus(-1);
//            }
//        }
    }

    /*inactive status set*/
    public void inActivieStatusDoor(int status) {
        if (status == 1) {
            img_door_on.setImageResource(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? R.drawable.unlock_only : R.drawable.lock_only);
            txtyDooryaleStatus.setText(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? "Unlocked" : "Locked");
            txtyDooryaleStatus.setTextColor(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
            img_door_on.setClickable(true);
        } else{
            img_door_on.setImageResource(R.drawable.gray_lock_disabled);
            txtyDooryaleStatus.setText("Inactive");
            txtyDooryaleStatus.setTextColor(getResources().getColor(R.color.automation_red));
            img_door_on.setClickable(false);
        }
    }


    public void inActivieStatus(int status) {
        if (status == -1) {
            doorSensorResModel.getDevice().setIsActive("-1");
            imgLock.setImageResource(R.drawable.gray_lock_disabled);
            txtlockStatus.setText("InActive");
            txtlockStatus.setTextColor(getResources().getColor(R.color.automation_red));
            imgLock.setClickable(false);
        } else {
//            imgLock.setImageResource(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? R.drawable.unlock_only : R.drawable.lock_only);
//            txtlockStatus.setText(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? "Unlocked" : "Locked");
//            txtlockStatus.setTextColor(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
//            doorSensorResModel.getDate().getDoorLists()[0].setIs_lock_active("1");
//            imgLock.setClickable(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            return true;
        } else if (id == R.id.actionEdit) {
            showBottomSheetDialog();
        }

        return super.onOptionsItemSelected(item);
    }


    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(YaleLockInfoActivity.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + mSensorName + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogEditName();
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteSensor(2);
            }
        });
    }

    /*edit admin password dialog*/
    private void dialogEditName() {
        editdialog = new Dialog(YaleLockInfoActivity.this);
        editdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editdialog.setCanceledOnTouchOutside(false);
        editdialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = editdialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) editdialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = (TextInputEditText) editdialog.findViewById(R.id.edSensorName);
        final EditText edSensorpasscode = editdialog.findViewById(R.id.edSensorPasscode);
        final EditText edsensorguestpasscode = editdialog.findViewById(R.id.edSensorguestPasscode);


        txtInputSensor.setVisibility(View.VISIBLE);
        edSensorName.setVisibility(View.VISIBLE);
        //     txtInputpasscode.setVisibility(View.VISIBLE);
        //    edSensorpasscode.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) editdialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) editdialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = editdialog.findViewById(R.id.iv_close);
        TextView tv_title = editdialog.findViewById(R.id.tv_title);

//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("1")) {
        txtInputSensor.setHint("Enter lock name");
        //   txtInputpasscode.setHint("Enter pass code");
        tv_title.setText("Sensor Name");

        edSensorName.setText(strsensorname);
        edsensorguestpasscode.setText(strguestpasscode);
        edSensorpasscode.setText(strsensorpasscode);
//        }
//        else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("2")) {
//            txtInputSensor.setHint("Enter Lock name");
//            tv_title.setText("Lock Name");
//        } else {
//            txtInputSensor.setHint("Enter Door/Lock name");
//            tv_title.setText("Door/Lock Name");
//        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(YaleLockInfoActivity.this);
                editdialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter lock name");
                } else if (edSensorpasscode.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter passcode");
                } else if(edsensorguestpasscode.getText().toString().equals(edSensorpasscode.getText().toString())){
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Admin password and guest password does not same");
                }
                else {
                    updateDoorSensor(edSensorName.getText().toString(),edSensorpasscode.getText().toString(), edsensorguestpasscode.getText().toString(),enable_lock_unlock_from_app, editdialog);
                }
            }
        });

        editdialog.show();

    }

    /*edit admin password dialog*/
    private void dialogEditpasscode() {
        editdialog = new Dialog(YaleLockInfoActivity.this);
        editdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editdialog.setCanceledOnTouchOutside(false);
        editdialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = editdialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) editdialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = (TextInputEditText) editdialog.findViewById(R.id.edSensorName);
        final EditText edSensorpasscode = editdialog.findViewById(R.id.edSensorPasscode);
        final EditText edsensorguestpasscode = editdialog.findViewById(R.id.edSensorguestPasscode);
         RelativeLayout relative_passcode = editdialog.findViewById(R.id.relative_passcode);
        ImageView img_show_passcode = editdialog.findViewById(R.id.img_show_passcode);


        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);

        relative_passcode.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) editdialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) editdialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = editdialog.findViewById(R.id.iv_close);
        TextView tv_title = editdialog.findViewById(R.id.tv_title);

        txtInputSensor.setHint("Enter password");
        tv_title.setText("Set Admin Password");

        edSensorName.setText(strsensorname);
        edsensorguestpasscode.setText(strguestpasscode);
        edSensorpasscode.setText(strsensorpasscode);

        img_show_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edSensorpasscode.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_passcode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    edSensorpasscode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    img_show_passcode.setImageResource(R.drawable.eye);
                    //Hide Password
                    edSensorpasscode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(YaleLockInfoActivity.this);
                editdialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter lock name");
                } else if (edSensorpasscode.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter passcode");
                } else {
                    updateDoorSensor(edSensorName.getText().toString(),edSensorpasscode.getText().toString(),edsensorguestpasscode.getText().toString() ,enable_lock_unlock_from_app, editdialog);
                }
            }
        });

        editdialog.show();

    }


    /*edit guest password dialog*/
    private void dialogEditguestpasscode() {
        editdialog = new Dialog(YaleLockInfoActivity.this);
        editdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editdialog.setCanceledOnTouchOutside(false);
        editdialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = editdialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) editdialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = (TextInputEditText) editdialog.findViewById(R.id.edSensorName);
        final EditText edSensorpasscode = editdialog.findViewById(R.id.edSensorPasscode);
        final EditText edsensorguestpasscode = editdialog.findViewById(R.id.edSensorguestPasscode);
        RelativeLayout relative_guestpasscode = editdialog.findViewById(R.id.relative_guestpasscode);
        ImageView img_show_guestpasscode = editdialog.findViewById(R.id.img_show_guestpasscode);


        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);

        relative_guestpasscode.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) editdialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) editdialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = editdialog.findViewById(R.id.iv_close);
        TextView tv_title = editdialog.findViewById(R.id.tv_title);

//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("1")) {
        txtInputSensor.setHint("Enter password");
        //   txtInputpasscode.setHint("Enter pass code");
        tv_title.setText("Set Guest Password");

        edSensorName.setText(strsensorname);
        edsensorguestpasscode.setText(strguestpasscode);
        edSensorpasscode.setText(strsensorpasscode);
//        }
//        else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("2")) {
//            txtInputSensor.setHint("Enter Lock name");
//            tv_title.setText("Lock Name");
//        } else {
//            txtInputSensor.setHint("Enter Door/Lock name");
//            tv_title.setText("Door/Lock Name");
//        }


        img_show_guestpasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edsensorguestpasscode.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_guestpasscode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    edsensorguestpasscode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    img_show_guestpasscode.setImageResource(R.drawable.eye);
                    //Hide Password
                    edsensorguestpasscode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(YaleLockInfoActivity.this);
                editdialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter lock name");
                } else if (edSensorpasscode.getText().toString().length() == 0) {
                    ChatApplication.showToast(YaleLockInfoActivity.this, "Please enter passcode");
                } else {
                    updateDoorSensor(edSensorName.getText().toString(), edSensorpasscode.getText().toString(),edsensorguestpasscode.getText().toString() ,enable_lock_unlock_from_app, editdialog);
                }
            }
        });

        editdialog.show();

    }

    /*call update sensor */
    private void updateDoorSensor(String sensor_name, String passcode, String guestpasscode,String enable_lock_unlock_from_app, Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.SAVE_EDIT_SWITCH;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("device_id", door_sensor_id);
            jsonNotification.put("enable_lock_unlock_from_app", enable_lock_unlock_from_app);
            jsonNotification.put("pass_code", passcode);
            jsonNotification.put("onetime_code",guestpasscode);
            jsonNotification.put("device_name", sensor_name);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("update yale lock url is " + webUrl + " " + jsonNotification);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("url is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        //    showToast(message);
                    }
                    if (code == 200) {
                        dialog.dismiss();
                        toolbar.setTitle(sensor_name);
//                        finish();
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /**
     * @param doorSensorNotificationId
     * @param notiSwitchOnOff
     * @param isActive
     * @param isNotification
     */

    private void showAlertDialog(final String doorSensorNotificationId, final SwitchCompat notiSwitchOnOff, final boolean isActive, final boolean isNotification) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Notification Alert");
        builder1.setMessage("Do you want to " + (isActive ? "enable " : "disable ") + " notificaiton ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doorSensorNotificationStatus(doorSensorNotificationId, !notiSwitchOnOff.isChecked(), isNotification);
                        notiSwitchOnOff.setChecked(!notiSwitchOnOff.isChecked());
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
                    }
                });

        AlertDialog alert11 = builder1.create();
        if (!alert11.isShowing()) {
            alert11.show();
        }
    }

    /**
     * @param doorSensorNotificationId
     * @param isActive
     * @param isNotification
     */
    private void doorSensorNotificationStatus(String doorSensorNotificationId, boolean isActive, boolean isNotification) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        String webUrl = "";
        if (!isNotification) {
            webUrl = ChatApplication.url + Constants.CHANGE_DOOR_SENSOR_STATUS;
        } else {
            webUrl = ChatApplication.url + Constants.UPDATE_TEMP_SENSOR_NOTIFICATION;
        }

        JSONObject jsonNotification = new JSONObject();

        try {
            // try {
            //            jsonNotification.put("is_active", isActive ? "y" :"n");
            //            jsonNotification.put("alert_id", tempSensorNotificationId);
            //            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            //            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            //            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            //
            //        } catch (JSONException e) {
            //            e.printStackTrace();
            //        }

            if (isNotification) {
                jsonNotification.put("alert_id", doorSensorNotificationId);
                jsonNotification.put("is_active", isActive ? "y" : "n");
            } else {
                jsonNotification.put("is_push_enable", isActive ? 1 : 0);
            }
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + webUrl + " " + jsonNotification);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("url is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        isRefresh = true;
                    }
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
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

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_delete) {

            deleteSensor(0);

        } else if (id == R.id.view_rel_badge) {
            view_rel_badge.setClickable(false);
            checkIntent(true);
        } else if (id == R.id.linearAlertDown) {
            if (flagAlert) {
                flagAlert = false;
                toggleAlert.setChecked(flagAlert);
                linearAlertExpand.setVisibility(View.GONE);
            } else {
                flagAlert = true;
                toggleAlert.setChecked(flagAlert);
                linearAlertExpand.setVisibility(View.VISIBLE);
            }
        } else if (v == txtAddLock) {
            Intent intent = new Intent(this, AddDeviceConfirmActivity.class);
            intent.putExtra("isViewType", "ttLock");
            startActivity(intent);
        } else if (v == imgLock) {
        } else if (v == txtAutoLock) {
            Autolockdialog();
        } else if (v == imgDoorDelete) {
            deleteSensor(1);
        } else if (v == imgLockDelete) {
            deleteSensor(2);
        }

    }

    /*delete sensor check is door , is lock , is door/lock*/
    private void deleteSensor(int count) {
        String message = "";

        if (count == 1) {
            message = "Delete Door";
        } else if (count == 2) {
            message = "Delete Lock";
        } else {
            message = "Delete Door/Lock";
        }

        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "" + message, "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteDoorSensor();
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }


    /**
     * delete individual Door sensor
     */
    private void deleteDoorSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        String webUrl = ChatApplication.url + Constants.DELETE_MODULE;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("device_id", door_sensor_id);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                    if (code == 200) {
                        finish();
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

    /*set notification */
    private void doorSensorNotification(final RemoteDetailsRes.Data.Alert notification, final boolean isEdit) {

        doorSensorNotificationDialog = new Dialog(this);
        doorSensorNotificationDialog.setContentView(R.layout.dialog_door_sensor_notification);
        doorSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = doorSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = doorSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_start_time = doorSensorNotificationDialog.findViewById(R.id.edit_start_time);
        final EditText edti_end_time = doorSensorNotificationDialog.findViewById(R.id.edit_end_time);

        edit_start_time.setFocusable(false);
        edti_end_time.setFocusable(false);

        if (isEdit) {
            try {
                String editStartTime = DateHelper.formateDate(DateHelper.parseTimeSimple(notification.getStartTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM);
                String editEndTime = DateHelper.formateDate(DateHelper.parseTimeSimple(notification.getEndTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM);

                edit_start_time.setText(editStartTime);
                edti_end_time.setText(editEndTime);

                Calendar calendarStart = Calendar.getInstance();
                calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notification.getStartTime().split(":")[0]));
                calendarStart.set(Calendar.MINUTE, Integer.parseInt(notification.getStartTime().split(":")[1]));

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notification.getEndTime().split(":")[0]));
                calendarEnd.set(Calendar.MINUTE, Integer.parseInt(notification.getEndTime().split(":")[1]));

                mStartTime = startTimeFormat().format(calendarStart.getTime());
                mEndTime = startTimeFormat().format(calendarEnd.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        edit_start_time.setInputType(InputType.TYPE_NULL);
        edti_end_time.setInputType(InputType.TYPE_NULL);

        edit_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePickerDialog(edit_start_time, true);
            }
        });
        edti_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(edti_end_time, false);
            }
        });

        closeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorSensorNotificationDialog.dismiss();
            }
        });

        button_save_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.hideKeyboard(YaleLockInfoActivity.this);
                addNotification(doorSensorNotificationDialog, edit_start_time, edti_end_time, isEdit, isEdit ? notification : null);
            }

        });

        doorSensorNotificationDialog.show();

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void showTimePickerDialog(final EditText edtText, final boolean isStartTime) {

        hideKeyboard();
        /*--TimePicker Dialg--*/
        TimePickerDialog mTimePicker;

        DialogFragment fromTimeFragment = new TimePickerFragment12(YaleLockInfoActivity.this, edtText.getText().toString(), new ICallback() {
            @Override
            public void onSuccess(String str) {

                try {

                    edtText.setText(str);
                    edtText.setError(null);
                    edtText.setFocusable(false);

                    if (isStartTime) {
                        mStartTime = DateHelper.formateDate(DateHelper.parseTimeSimple(str, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                    } else {
                        mEndTime = DateHelper.formateDate(DateHelper.parseTimeSimple(str, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        if (!fromTimeFragment.isVisible()) {
            fromTimeFragment.show(YaleLockInfoActivity.this.getFragmentManager(), "TimePicker");
        }

    }


    public SimpleDateFormat startTimeFormat() {
        return startTimeFormat = new SimpleDateFormat("H:mm");
    }

    /**
     * @param doorSensorNotificationDialog
     * @param stat_time
     * @param end_time
     */
    private void addNotification(final Dialog doorSensorNotificationDialog, EditText stat_time, final EditText end_time, boolean isEdit, RemoteDetailsRes.Data.Alert notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(stat_time.getText().toString().trim())) {
            stat_time.setFocusableInTouchMode(true);
            stat_time.requestFocus();
            stat_time.setError("Select Start Time");
            return;
        }
        if (TextUtils.isEmpty(end_time.getText().toString().trim())) {
            end_time.setFocusableInTouchMode(true);
            end_time.requestFocus();
            end_time.setError("Select End Time");
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject jsonNotification = new JSONObject();

        try {

//            if (isEdit) {
//                jsonNotification.put("door_sensor_notification_id", notification.getAlertId());
//            }
//
//            jsonNotification.put("door_sensor_id", door_sensor_id);
//            jsonNotification.put("start_datetime", mStartTime);
//            jsonNotification.put("end_datetime", mEndTime);
//            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
//            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
//            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));


            jsonNotification.put("start_time", mStartTime);
            jsonNotification.put("end_time", mEndTime);
            jsonNotification.put("alert_type", "door_lock_unlock");
            jsonNotification.put("days", "0,1,2,3,4,5,6");
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            if (isEdit) {
                jsonNotification.put("alert_id", notification.getAlertId());

            } else {
                jsonNotification.put("device_id", doorSensorResModel.getDevice().getDevice_id());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String webUrl = "";
        if (isEdit) {
            webUrl = ChatApplication.url + Constants.UPDATE_TEMP_SENSOR_NOTIFICATION;
        } else {
            webUrl = ChatApplication.url + Constants.ADD_TEMP_SENSOR_NOTIFICATION;
        }

        ChatApplication.logDisplay("url is " + webUrl + " " + jsonNotification);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    ChatApplication.logDisplay("url is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }

                        isRefresh = true;
                        isRefreshAll = true;
                        doorSensorNotificationDialog.dismiss();
                        getDoorSensorDetails();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            end_time.setFocusableInTouchMode(true);
                            end_time.requestFocus();
                            end_time.setError(message);
                        }
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

    /**
     * delete door sensor notification details
     *
     * @param notification
     */

    private void deleteDoorSensorNotification(RemoteDetailsRes.Data.Alert notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.DELETE_TEMP_SENSOR_NOTIFICATION;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("alert_id", notification.getAlertId());
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        isRefresh = true;
                        isRefreshAll = true;
                        getDoorSensorDetails();
                    }
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
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
     * @param notification
     * @param position
     * @param isEdit
     */
    @Override
    public void onEditOpetion(final RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {
        if (isEdit) {
            showBottomSheetDialognotification(notification, isEdit);
        }
    }


    public void showBottomSheetDialognotification(final RemoteDetailsRes.Data.Alert notification, boolean isEdit) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(YaleLockInfoActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + "notification" + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                doorSensorNotification(notification, isEdit);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteDoorSensorNotification(notification);
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }
                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onSwitchChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {
        showAlertDialog(notification.getAlertId(), swithcCompact, isActive, true);
    }

    public void unreadApiCall(final boolean b) {

        String webUrl = ChatApplication.url + Constants.MARK_SEEN;

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("device_id", door_sensor_id);
            jsonObject.put("log_type", "device");
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("unread log is " + webUrl + " " + jsonObject);
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new

                ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        ChatApplication.logDisplay("log is " + result);
                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        if (b) {
//                            callFilterData(0);
                        } else {

                        }
                    }
                }).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void checkIntent(boolean b) {
        if (b) {
            Intent intent = new Intent(YaleLockInfoActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", door_sensor_id);
            intent.putExtra("Mood_Id", "" + door_sensor_id);
            intent.putExtra("activity_type", "door");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity", "doorSensor");
            intent.putExtra("isRoomName", "" + sensorName.getText().toString());
            startActivity(intent);
        } else {
            YaleLockInfoActivity.this.finish();
        }
        return;
    }

    @Override
    public void onBackPressed() {
        unreadApiCall(false);
        super.onBackPressed();
    }
}
