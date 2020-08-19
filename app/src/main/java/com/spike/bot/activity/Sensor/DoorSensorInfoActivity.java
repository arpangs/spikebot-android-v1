package com.spike.bot.activity.Sensor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.DeviceLogRoomActivity;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.DoorAlertAdapter;
import com.spike.bot.adapter.DoorSensorInfoAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
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
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 14/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DoorSensorInfoActivity extends AppCompatActivity implements View.OnClickListener, DoorSensorInfoAdapter.OnNotificationContextMenu, ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView door_sensor_list, recyclerAlert;
    private DoorSensorInfoAdapter tempSensorInfoAdapter;
    public Toolbar toolbar;

    private EditText sensorName;
    private TextView txt_empty_notification, txt_empty_notificationALert, txtTempCount, txtAlertTempCount,txt_battery_level;
    private ImageView img_door_on, imgBattery, view_rel_badge, imgLock, imgLockDelete, imgDoorDelete, imgLockBattery;
    private TextView batteryPercentage,doorAddButton;
    private LinearLayout linearAlertDown, linearAlertExpand, linearLock, linearAddlockOptin, linearLockDoor;
    public CardView cardViewLock, cardViewDoor;
    private ToggleButton toggleAlert;
    private TextView txtAlertCount;
    private AppCompatTextView txtAddLock, txtBettrylock, txtlockStatus, txtDoorStatus, txtAutoLock;
    private Button btn_delete;
    public SwitchCompat switchAutoLock;
    private String mSensorName;
    private boolean flagAlert = false, isRefreshAll = false;
    private RelativeLayout mRelLog;

    //Declare timer
    CountDownTimer cTimer = null;

//    public DoorSensorResModel.DATA.DoorList[] tempLists = new DoorSensorResModel.DATA.DoorList[0];
    private RemoteDetailsRes doorSensorResModelData;
    private RemoteDetailsRes.Data doorSensorResModel;
    private String door_sensor_id, door_room_name, door_room_id, door_module_id, mac_address = "";
    private Socket mSocket;
    private String door_unread_count = "", door_subtype = "";

    //gatwway list
    ArrayList<LockObj> gatewayList = new ArrayList<>();
    ArrayList<LockObj> locklistAll = new ArrayList<>();
    public static String mSocketCountVal;
    private boolean isRefresh = false;

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

        toolbar =  findViewById(R.id.toolbar);
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

        /*check refresh token time
        * 90 day after token expired */
        if (!door_subtype.equals("1") && Constants.lockDate < 86400 || TextUtils.isEmpty(Common.getPrefValue(DoorSensorInfoActivity.this, Constants.lock_token))) {
            //check access token refresh
            callTTAuth();
        } else {
            getDoorSensorDetails();
        }
        view_rel_badge.setClickable(true);
        mRelLog.setClickable(true);
        ChatApplication.logDisplay("door call is " + ChatApplication.url);

        startSocketConnection();
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

            DoorSensorInfoActivity.this.runOnUiThread(new Runnable() {
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
            DoorSensorInfoActivity.this.runOnUiThread(new Runnable() {
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

                            if(device_id.equalsIgnoreCase(door_sensor_id)){
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
            mSocket.off("changeDeviceStatus", changeDoorSensorStatus);
        }
    }

    /**
     * get individual door sensor details
     */
    private void getDoorSensorDetails() {

      /*  if (!ChatApplication.url.startsWith("http")) {
            ChatApplication.url = "http://" + ChatApplication.url;
        }*/

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getDoorSensorDetails(door_sensor_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                doorSensorResModelData = Common.jsonToPojo(stringResponse, RemoteDetailsRes.class);
                doorSensorResModel = doorSensorResModelData.getData();

                if (doorSensorResModelData.getCode() == 200) {
                    fillData(doorSensorResModel);
                }
                ActivityHelper.dismissProgressDialog();
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


            if(doorSensorResModel.getDevice().getMeta_battery_level()!=null)
            {

                int perc = 0;
                if (!TextUtils.isEmpty(""+doorSensorResModel.getDevice().getMeta_battery_level())) {
                    try {
                        perc = Integer.parseInt(doorSensorResModel.getDevice().getMeta_battery_level());
                    } catch (Exception ex) {
                        perc = 100;
                        ex.printStackTrace();
                    }
                }


             //   imgBattery.setImageResource(Common.getBatteryIcon(""+doorSensorResModel.getDevice().getMeta_battery_level()));

                if (perc >= 0 && perc <= 25)
                    batteryPercentage.setTextColor(getResources().getColor(R.color.battery_low));
                else if (perc >= 26 && perc <= 50)
                    batteryPercentage.setTextColor(getResources().getColor(R.color.battery_low1));
                else if (perc >= 51 && perc <= 75)
                    batteryPercentage.setTextColor(getResources().getColor(R.color.battery_medium));
                else if (perc >= 76 && perc <= 100)
                    batteryPercentage.setTextColor(getResources().getColor(R.color.battery_high));

                if(perc > 100){
                    txt_battery_level.setText("100" + "%");
                    txt_battery_level.setTextColor(getResources().getColor(R.color.battery_high));
                    batteryPercentage.setText("100" + "%");
                    batteryPercentage.setTextColor(getResources().getColor(R.color.battery_high));
                } else{
                    txt_battery_level.setText(perc + " %");
                    batteryPercentage.setText(perc + " %");
                }

            } else{
                batteryPercentage.setText("- -");
            }

            if (doorSensorResModel.getAlerts()!=null && doorSensorResModel.getAlerts().size()>0) {
                door_sensor_list.setVisibility(View.VISIBLE);
                txtAlertTempCount.setVisibility(View.VISIBLE);
                txt_empty_notification.setVisibility(View.GONE);
                txtAlertTempCount.setText(doorSensorResModel.getAlerts().size() + " " + "Notifications" + " Added");
                tempSensorInfoAdapter = new DoorSensorInfoAdapter(doorSensorResModel.getAlerts(), true, DoorSensorInfoActivity.this);
                door_sensor_list.setAdapter(tempSensorInfoAdapter);
            } else {
                txtAlertTempCount.setText("0" + " " + "Notifications" + " Added");
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
                    checkBridgeDetail();
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

                if (doorSensorResModel.getUnseenLogs()!=null && doorSensorResModel.getUnseenLogs().size()>0) {

                    int countTemp = doorSensorResModel.getUnseenLogs().size();
                    if (countTemp > 99) {
                        txtAlertCount.setText("99+");
                    } else {
                        txtAlertCount.setText("" + countTemp);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DoorSensorInfoActivity.this);
                    recyclerAlert.setLayoutManager(linearLayoutManager);
                    DoorAlertAdapter doorAlertAdapter = new DoorAlertAdapter(DoorSensorInfoActivity.this, doorSensorResModel.getUnseenLogs());
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

    private void checkBridgeDetail() {
//        if (doorSensorResModel.getDate().getBridgeList() != null && doorSensorResModel.getDate().getBridgeList().length > 0) {
//            callGatewayList();
//        } else {
//            inactiveStatusUpdate(-1);
//            callLockBatterystatus();
//        }
    }

    //start timer function
    public void startTimer() {
        cTimer = new CountDownTimer(9500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                doorOpenCLose();
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
        final Dialog dialog = new Dialog(DoorSensorInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_auto_lock);

        Button btnSave =  dialog.findViewById(R.id.btn_save);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);
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
                boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(DoorSensorInfoActivity.this);
                if (!isBtEnable) {
                    TTLockClient.getDefault().requestBleEnable(DoorSensorInfoActivity.this);
                }
                dialog.dismiss();
                setAUtolock();

            }
        });

        dialog.show();
    }

    private void setAUtolock() {
//        int count = 10;
//        if (switchAutoLock.isChecked()) {
//            count = 0;
//        }
//        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
//        ChatApplication.logDisplay("count is " + count);
//        ChatApplication.logDisplay("count is " + mac_address);
//        ChatApplication.logDisplay("count is " + doorSensorResModel.getDate().getDoorLists()[0].getLock_data());
//        TTLockClient.getDefault().setAutomaticLockingPeriod(count, doorSensorResModel.getDate().getDoorLists()[0].getLock_data(), mac_address, new SetAutoLockingPeriodCallback() {
//            @Override
//            public void onSetAutoLockingPeriodSuccess() {
//                switchAutoLock.setChecked(!switchAutoLock.isChecked());
//                ChatApplication.logDisplay("auto lock done");
//                callAulockSpikebot(1);
//            }
//
//            @Override
//            public void onFail(LockError error) {
//                ActivityHelper.dismissProgressDialog();
//                switchAutoLock.setChecked(switchAutoLock.isChecked());
//                ChatApplication.logDisplay("auto lock done error ");
//            }
//        });
    }

    private void callAulockSpikebot(int count) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String webUrl = ChatApplication.url + Constants.changeLockSensorAutoLockStatus;

        JSONObject jsonNotification = new JSONObject();
        try {
            // "door_sensor_id":"",
            // "is_autolock_enable":"",
            // "user_id":"",
            // "phone_id":"",
            // "phone_type":

            jsonNotification.put("door_sensor_id", door_sensor_id);
            if (count == 0) {
                jsonNotification.put("is_autolock_enable", 0);
            } else {
                jsonNotification.put("is_autolock_enable", switchAutoLock.isChecked() == true ? 1 : 0);
            }
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("auto lock is " + webUrl + " " + jsonNotification);

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("auto lock is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
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
        if (status == -1) {
            img_door_on.setImageResource(R.drawable.door_off_inactive);
            txtDoorStatus.setText("InActive");
            batteryPercentage.setText("- -");
            txtDoorStatus.setTextColor(getResources().getColor(R.color.automation_red));
            img_door_on.setClickable(false);
        } else {
            txtDoorStatus.setText("");
            img_door_on.setImageResource(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? R.drawable.dooron : R.drawable.dooroff);
            txtDoorStatus.setText(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? "Door Opened" : "Door Closed");
            txtDoorStatus.setTextColor(doorSensorResModel.getDevice().getDeviceStatus().equals("0") ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.green));
            img_door_on.setClickable(true);
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

    /**
     * Get doorLock battery status
     */
    private void callLockBatterystatus() {

//        ActivityHelper.showProgressDialog(this, "Fetching Lock Details...", false);
//        GetDataService apiService = RetrofitAPIManager.provideClientApi();

//        Call<String> call = apiService.lockDetails(Constants.client_id, Constants.access_token, Integer.parseInt(doorSensorResModel.getDate().getDoorLists()[0].getLock_id()), System.currentTimeMillis());
//
//        ChatApplication.logDisplay("door call");
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                ActivityHelper.dismissProgressDialog();
//                if (response.code() == 200) {
//                    try {
//                        String json = response.body().toString();
//                        ChatApplication.logDisplay("lock is " + json);
//                        JSONObject object = new JSONObject(json);
//
//                        if (!TextUtils.isEmpty(object.optString("electricQuantity"))) {
//                            imgLockBattery.setImageResource(Common.getBatteryIcon(object.optString("electricQuantity")));
//                            txtBettrylock.setText(object.optString("electricQuantity") + " %");
//                            mac_address = object.optString("lockMac");
//
//                            if (object.optInt("electricQuantity") < 1) {
//                                inactiveStatusUpdate(-1);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    ChatApplication.logDisplay("lock is " + response.toString());
//
//                } else {
//                    ChatApplication.logDisplay("tt lock reponse is error ff ");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                ActivityHelper.dismissProgressDialog();
//                ChatApplication.logDisplay("tt lock reponse is error");
//            }
//        });
    }

    /*get token & expires_in */
    private void callTTAuth() {

        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.auth(Constants.client_id, Constants.client_secret, "password", Constants.lockUserName, DigitUtil.getMD5(Constants.lockPassword), Constants.locK_base_uri);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {

                    String json = response.body();
                    try {
                        //}{
                        //  access_token='fac1734b6209dd5b3ea602c9cc7a15ae',
                        //  refresh_token='5ca1a4bc670b16b571b1488a631e57fc',
                        //  uid=1769341,
                        //  openid=1930389027,
                        //  scope='user,key,room',
                        //  token_type='Bearer',
                        //  expires_in=2496266
                        //}

                        JSONObject object = new JSONObject(json);
                        Constants.lockDate = object.optInt("expires_in");
                        Constants.access_token = object.optString("access_token");
                        Common.savePrefValue(ChatApplication.getInstance(), Constants.lock_exe, "" + Constants.lockDate);
                        Common.savePrefValue(ChatApplication.getInstance(), Constants.lock_token, Constants.access_token);
                        ChatApplication.logDisplay("tt lock reponse is response " + object);
                        getDoorSensorDetails();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });
    }


    public void inactiveStatusUpdate(int status) {

//        if (!ActivityHelper.isConnectingToInternet(this)) {
//            showToast("" + R.string.disconnect);
//            return;
//        }
//
//        //{"lock_id":1578230,"status":"1"}
//        //1 = active &&& -1 = inactive
//        ActivityHelper.showProgressDialog(this, "Please wait.", false);
//        String webUrl = ChatApplication.url + Constants.updateTTLockActiveStatus;
//
//        JSONObject jsonNotification = new JSONObject();
//        try {
//            jsonNotification.put("lock_id", doorSensorResModel.getDate().getDoorLists()[0].getLock_id());
//            jsonNotification.put("status", status);
//            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
//            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
//            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
//            @Override
//            public void onSuccess(JSONObject result) {
//
//                ActivityHelper.dismissProgressDialog();
//                try {
//
//                    int code = result.getInt("code");
//                    ChatApplication.logDisplay("status is " + result);
//                    if (code == 200) {
//                        inActivieStatus(status);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String error) {
//                ActivityHelper.dismissProgressDialog();
//            }
//        }).execute();

    }

    private void callGatewayList() {

        ActivityHelper.showProgressDialog(this, "Fetching Bridge Details...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.gatewaylist(Constants.client_id, Constants.access_token, 1, 20, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (json.contains("list")) {
                    try {
                        gatewayList.clear();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        ChatApplication.logDisplay("gateway is " + jsonObject);
                        gatewayList = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>() {
                        });

                        if (gatewayList.size() > 0) {
                            checkLocklist();
                        } else {
                            inactiveStatusUpdate(-1);
                            callLockBatterystatus();
                        }
                    } catch (JSONException e) {
                        ActivityHelper.dismissProgressDialog();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    private void getAlllockList(int gatewayId) {
//        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
//
//        GetDataService apiService = RetrofitAPIManager.provideClientApi();
//        Call<String> call = apiService.getlockbygateway(Constants.client_id, Constants.access_token, "" + gatewayId, System.currentTimeMillis());
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                String json = response.body();
//                ActivityHelper.dismissProgressDialog();
//                if (json.contains("list")) {
//                    try {
//                        locklistAll.clear();
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONArray array = jsonObject.getJSONArray("list");
//                        locklistAll = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>() {
//                        });
//
//                        if (locklistAll.size() > 0) {
//                            boolean isFlag = false;
//                            for (int i = 0; i < locklistAll.size(); i++) {
//                                if (locklistAll.get(i).getLockId() == Integer.parseInt(doorSensorResModel.getDate().getDoorLists()[0].getLock_id())) {
//                                    isFlag = true;
//                                    callLockBatterystatus();
//                                    break;
//                                }
//                            }
//
//                            if (!isFlag) {
//                                inactiveStatusUpdate(-1);
//                                callLockBatterystatus();
//                            }
//                        } else {
//                            inactiveStatusUpdate(-1);
//                            callLockBatterystatus();
//                        }
//
//                    } catch (JSONException e) {
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                ActivityHelper.dismissProgressDialog();
//            }
//        });
    }

    private void checkLocklist() {
//        for (int i = 0; i < gatewayList.size(); i++) {
//            for (int j = 0; j < doorSensorResModel.getDate().getBridgeList().length; j++) {
//                if (gatewayList.get(i).getGatewayId() == doorSensorResModel.getDate().getBridgeList()[j].getBridge_id()) {
//                    if (gatewayList.get(i).getIsOnline() == 1 && gatewayList.get(i).getLockNum() > 0) {
//                        // online gateway
//                        if (!TextUtils.isEmpty(doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active()) &&
//                                doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active().length() > 0 &&
//                                !doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active().equals("null") &&
//                                doorSensorResModel.getDate().getDoorLists()[0].getIs_lock_active().equals("-1")) {
//                            inactiveStatusUpdate(1);
//                        }
//
//                        getAlllockList(gatewayList.get(i).getGatewayId());
//                    } else {
//                        // call inactive status
//                        inactiveStatusUpdate(-1);
//                        callLockBatterystatus();
//                    }
//                }
//            }
//        }
    }

    private void doorOpenCLose() {

//        if (mSocket != null) {
//            // "lock_id":"",
//            // "user_id":"",
//            // "door_sensor_status":"",
//            // "lock_status":""
//
//            JSONObject object = new JSONObject();
//            try {
//                object.put("lock_id", doorSensorResModel.getDate().getDoorLists()[0].getLock_id());
//                object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
//                object.put("lock_status", doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? 0 : 1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            ChatApplication.logDisplay("door json " + object);
//            mSocket.emit("changeLockStatus", object);
//
//            doorSensorResModel.getDate().getDoorLists()[0].setDoor_lock_status(doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equals("1") ? "0" : "1");
//
//            //onlydoor, subtype=1
//            //only lock, subtype=2
//            //door+lock, subtype=3
//            if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("2")) {
//                setLockStatus(2);
//            } else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equalsIgnoreCase("3")) {
//                setLockStatus(3);
//            }
//        }

    }

    private void callDoorRemotly() {
//        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
//        GetDataService apiService = RetrofitAPIManager.provideClientApi();
//
//        Call<ResponseBody> call;
//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equalsIgnoreCase("1")) {
//            call = apiService.lockGatewayUse(Constants.client_id, Constants.access_token, Integer.parseInt(doorSensorResModel.getDate().getDoorLists()[0].getLock_id()), System.currentTimeMillis());
//        } else {
//            call = apiService.unlockGatewayUse(Constants.client_id, Constants.access_token, Integer.parseInt(doorSensorResModel.getDate().getDoorLists()[0].getLock_id()), System.currentTimeMillis());
//        }
//        ChatApplication.logDisplay("door json call");
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ActivityHelper.dismissProgressDialog();
//                if (response.code() == 200) {
//                    ChatApplication.logDisplay("lock is " + response.body().source());
//                    ChatApplication.logDisplay("lock is " + response.message());
//                    ChatApplication.logDisplay("lock is " + response.toString());
//                    ChatApplication.logDisplay("lock is " + response.isSuccessful());
//
//                    int found = response.body().source().toString().indexOf("failed or means no");
//                    if (found == -1) {
//                        if (switchAutoLock.isChecked() && doorSensorResModel.getDate().getDoorLists()[0].getDoor_lock_status().equalsIgnoreCase("0")) {
//                            startTimer();
//                        } else {
//                            cancelTimer();
//                        }
//
//                        doorOpenCLose();
//                        ChatApplication.logDisplay("lock is " + response.body().toString());
//                    } else {
//                        ChatApplication.showToast(DoorSensorInfoActivity.this, "failed");
//                    }
//                } else {
//                    ChatApplication.logDisplay("tt lock reponse is error ff ");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ActivityHelper.dismissProgressDialog();
//                ChatApplication.logDisplay("tt lock reponse is error");
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
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


        BottomSheetDialog dialog = new BottomSheetDialog(DoorSensorInfoActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + mSensorName + " " +"?");
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
                deleteSensor(0);
            }
        });
    }

    /*edit sensor dialog*/
    private void dialogEditName() {
        final Dialog dialog = new Dialog(DoorSensorInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = dialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = (TextInputEditText) dialog.findViewById(R.id.edSensorName);
        txtInputSensor.setVisibility(View.VISIBLE);
        edSensorName.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);

//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("1")) {
            txtInputSensor.setHint("Enter sensor name");
            tv_title.setText("Sensor Name");
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
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(DoorSensorInfoActivity.this);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(DoorSensorInfoActivity.this, "Please enter name");
                } else {
                    updateDoorSensor(edSensorName.getText().toString(),dialog);
                }
            }
        });

        dialog.show();

    }

    /*call update sensor */
    private void updateDoorSensor(String sensor_name, Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().updateDevice(door_sensor_id, sensor_name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void bindView() {

        door_sensor_list =  findViewById(R.id.sensor_list);
        recyclerAlert =  findViewById(R.id.recyclerAlert);
        door_sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        door_sensor_list.setVerticalScrollBarEnabled(true);

        view_rel_badge =  findViewById(R.id.view_rel_badge);

        mRelLog = findViewById(R.id.rel_log);

        sensorName =  findViewById(R.id.sensor_name);
        linearAlertDown =  findViewById(R.id.linearAlertDown);
        linearAlertExpand =  findViewById(R.id.linearAlertExpand);
        toggleAlert =  findViewById(R.id.toggleAlert);
        txtAlertTempCount =  findViewById(R.id.txtAlertTempCount);

        sensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        sensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});


        //sensorLog = (TextView) findViewById(R.id.iv_icon_badge);
        imgBattery =  findViewById(R.id.img_battery);
        img_door_on =  findViewById(R.id.img_temp_on);
        batteryPercentage =  findViewById(R.id.txt_bettery_per);
        //notiSwitchOnOff = (SwitchCompat) findViewById(R.id.switch_noti_onoff);
        doorAddButton = findViewById(R.id.btnAdd);
        txtTempCount =  findViewById(R.id.txtTempCount);
        btn_delete =  findViewById(R.id.btn_delete);
        txt_empty_notification =  findViewById(R.id.txt_empty_notification_alert);
        txt_empty_notificationALert =  findViewById(R.id.txt_empty_notification);
        txtAlertCount =  findViewById(R.id.txtAlertCount);
        txtAddLock = findViewById(R.id.txtAddLock);
        txt_battery_level = findViewById(R.id.txt_battery_level);
        linearLock = findViewById(R.id.linearLock);
        imgLock = findViewById(R.id.imgLock);
        linearLockDoor = findViewById(R.id.linearLockDoor);
        linearAddlockOptin = findViewById(R.id.linearAddlockOptin);
        txtlockStatus = findViewById(R.id.txtlockStatus);
        txtBettrylock = findViewById(R.id.txtBettrylock);
        imgLockBattery = findViewById(R.id.imgLockBattery);
        txtDoorStatus = findViewById(R.id.txtDoorStatus);
        cardViewDoor = findViewById(R.id.cardViewDoor);
        cardViewLock = findViewById(R.id.cardViewLock);
        switchAutoLock = findViewById(R.id.switchAutoLock);
        txtAutoLock = findViewById(R.id.txtAutoLock);
        imgDoorDelete = findViewById(R.id.imgDoorDelete);
        imgLockDelete = findViewById(R.id.imgLockDelete);



        if (!Common.getPrefValue(DoorSensorInfoActivity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            imgDoorDelete.setVisibility(View.VISIBLE);
        } else{
            imgDoorDelete.setVisibility(View.GONE);
        }

        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btn_delete.setVisibility(View.GONE);
        }
        btn_delete.setVisibility(View.GONE);
        btn_delete.setOnClickListener(this);
        view_rel_badge.setOnClickListener(this);
        mRelLog.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);
        txtAddLock.setOnClickListener(this);
        imgLock.setOnClickListener(this);
        txtAutoLock.setOnClickListener(this);
        imgLockDelete.setOnClickListener(this);
        imgDoorDelete.setOnClickListener(this);

        txtAlertTempCount.setOnClickListener(new View.OnClickListener() {
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

        toggleAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoorSensorInfoActivity.this, DeviceLogRoomActivity.class);
                intent.putExtra("isNotification", "DoorSensor");
                intent.putExtra("ROOM_ID",doorSensorResModel.getDevice().getDevice_id());
                intent.putExtra("Sensorname",doorSensorResModel.getDevice().getDeviceName());
                startActivity(intent);
            }
        });

        doorAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorSensorNotification(null, false);
            }
        });
    }

    /**
     * @param doorSensorNotificationId
     * @param notiSwitchOnOff
     * @param isActive
     * @param isNotification
     */

    private void showAlertDialog(final String doorSensorNotificationId, final SwitchCompat notiSwitchOnOff, final boolean isActive, final boolean isNotification) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder1.setTitle("Notification Alert");
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
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().doorSensorNotificationStatus(doorSensorNotificationId, isActive,isNotification, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is "+result);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.btn_delete) {

        } else if (id == R.id.view_rel_badge) {
            view_rel_badge.setClickable(false);
            checkIntent(true);
        } else if (id == R.id.rel_log) {
            mRelLog.setClickable(false);
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
            callDoorRemotly();
        } else if (v == txtAutoLock) {
            Autolockdialog();
        } else if (v == imgDoorDelete) {
           // deleteSensor(1);

            showBottomSheetDialog();
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
//                if (count == 1 || count == 2) {
//                    deleteManual(count);
//                } else {
                    deleteDoorSensor();

//                }

            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void deleteManual(int count) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        String webUrl = ChatApplication.url + Constants.deleteDoorLock;
        //POST: 192.168.175.118/deleteDoorLock
        //{
        //    "door_sensor_id": "1568297809815_MNal_LsCM8",
        //    "door_subtype": ,
        //    "user_id": "1566980189559_18CfbqxQo",
        //    "admin": 1,
        //    "phone_id": "990012001131769",
        //    "phone_type": "android"
        //}

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("door_sensor_id", door_sensor_id);
            jsonNotification.put("door_subtype", count);
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
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(door_sensor_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

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
                ActivityHelper.hideKeyboard(DoorSensorInfoActivity.this);
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

        DialogFragment fromTimeFragment = new TimePickerFragment12(DoorSensorInfoActivity.this, edtText.getText().toString(), new ICallback() {
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
            fromTimeFragment.show(DoorSensorInfoActivity.this.getFragmentManager(), "TimePicker");
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
    private void addNotification(final Dialog doorSensorNotificationDialog, EditText stat_time, final EditText end_time, boolean isEdit,RemoteDetailsRes.Data.Alert  notification) {

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

        HashMap<String, Object> params = new HashMap<>();
        params.put("start_time", mStartTime);
        params.put("end_time", mEndTime);
        params.put("alert_type", "door_open_close");
        params.put("days", "0,1,2,3,4,5,6");
        if (isEdit) {
            params.put("alert_id", notification.getAlertId());

        } else {
            params.put("device_id", doorSensorResModel.getDevice().getDevice_id());
        }

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addNotification(params, isEdit, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is "+result);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
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
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDoorSensorNotification(notification.getAlertId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }


    /**
     * @param notification
     * @param position
     * @param isEdit
     */
    @Override
    public void onEditOpetion(final RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {
        if (isEdit) {
            showBottomSheetDialognotification(notification,isEdit);
        }
    }

    public void showBottomSheetDialognotification(final RemoteDetailsRes.Data.Alert notification, boolean isEdit) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(DoorSensorInfoActivity.this,R.style.AppBottomSheetDialogTheme);
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
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().unreadNotification(door_sensor_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
            }

            @Override
            public void onData_FailureResponse() {
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });


    }

    public void checkIntent(boolean b) {
        if (b) {
            Intent intent = new Intent(DoorSensorInfoActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", door_sensor_id);
            intent.putExtra("Mood_Id", "" + door_sensor_id);
            intent.putExtra("activity_type", "door");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity", "doorSensor");
            intent.putExtra("isRoomName", "" + sensorName.getText().toString());
            startActivity(intent);
        } else {
            DoorSensorInfoActivity.this.finish();
        }
        return;
    }

    @Override
    public void onBackPressed() {
        unreadApiCall(false);
        super.onBackPressed();
    }
}
