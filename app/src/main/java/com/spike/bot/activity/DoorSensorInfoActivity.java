package com.spike.bot.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.DoorAlertAdapter;
import com.spike.bot.adapter.DoorSensorInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.Log;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment12;
import com.spike.bot.model.DoorSensorResModel;
import com.spike.bot.model.RoomVO;
import com.spike.bot.receiver.ConnectivityReceiver;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 14/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DoorSensorInfoActivity extends AppCompatActivity implements View.OnClickListener, DoorSensorInfoAdapter.OnNotificationContextMenu, ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView door_sensor_list, recyclerAlert;
    private DoorSensorInfoAdapter tempSensorInfoAdapter;

    private EditText sensorName;
    private TextView txt_empty_notification, txt_empty_notificationALert, txtTempCount, txtAlertTempCount;
    private ImageView img_door_on, imgBattery, view_rel_badge;
    private TextView batteryPercentage;
    private LinearLayout linearAlertDown, linearAlertExpand;
    private ToggleButton toggleAlert;
    //  private SwitchCompat notiSwitchOnOff;
    private TextView doorAddButton, txtAlertCount;
    private Button btn_delete;
    private String mSensorName;
    private boolean flagAlert = false;

    public DoorSensorResModel.DATA.DoorList[] tempLists=new DoorSensorResModel.DATA.DoorList[0];
    DoorSensorResModel doorSensorResModel;
    private String door_sensor_id, door_room_name, door_room_id, door_module_id;
    private Socket mSocket;
    private String door_unread_count = "";


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showToast("Disconnected, Please check your internet connection");
        }
    }

    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_sensor_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        door_sensor_id = getIntent().getStringExtra("door_sensor_id");
        door_room_name = getIntent().getStringExtra("door_room_name");
        door_room_id = getIntent().getStringExtra("door_room_id");
        door_unread_count = getIntent().getStringExtra("door_unread_count");
        door_module_id = getIntent().getStringExtra("door_module_id");


        toolbar.setTitle(door_room_name);


    }

    @Override
    protected void onResume() {
        super.onResume();

        ChatApplication.getInstance().setConnectivityListener(this);
        if (ChatApplication.isLogResume) {
            ChatApplication.isLogResume = false;
            ChatApplication.isLocalFragmentResume = true;
            //  getDoorSensorDetails();
            mSocketCountVal = "0";
            //sensorLog.setVisibility(View.GONE);
        }
        bindView();

        connectivityReceiver = new ConnectivityReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        view_rel_badge.setClickable(true);
        getDoorSensorDetails();
    }


    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if(mSocket!=null){
            mSocket.on("doorsensorvoltage", doorsensorvoltage);
            mSocket.on("unReadCount", unReadCount);
        }

    }

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

                            // setDoorUnreadCount(doorSensorVoltage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    public static String mSocketCountVal;
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
                            String room_unread = object.getString("room_unread");
                            String user_id = object.getString("user_id");

                            if (sensor_type.equalsIgnoreCase("door") && door_module_id.equalsIgnoreCase(module_id) && door_room_id.equalsIgnoreCase(room_id)) {
                                mSocketCountVal = sensor_unread;
                                setDoorUnreadCount(sensor_unread);
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
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
        if (mSocket != null) {
            mSocket.off("doorsensorvoltage", doorsensorvoltage);
            mSocket.off("unReadCount", unReadCount);
        }
    }

    /**
     * get individual door sensor details
     */
    private void getDoorSensorDetails() {

        String url = ChatApplication.url + Constants.GET_DOOR_SENSOR_INFO  ;

        JSONObject object = new JSONObject();
        try {
            object.put("door_sensor_id",door_sensor_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                startSocketConnection();
                ActivityHelper.dismissProgressDialog();
                doorSensorResModel = Common.jsonToPojo(result.toString(), DoorSensorResModel.class);
                if (doorSensorResModel.getCode() == 200) {
                    fillData(doorSensorResModel);
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * @param count
     */
    private void setDoorUnreadCount(String count) {

//        if(!TextUtils.isEmpty(count) && Integer.parseInt(count) > 0){
//            sensorLog.setVisibility(View.VISIBLE);
//            sensorLog.setText(count);
//
//            if(Integer.parseInt(count) > 99){
//                sensorLog.setText("99+");
//                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(),20);
//                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(),20);
//            }else{
//                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(),20);
//                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(),20);
//            }
//
//        }else{
//            sensorLog.setVisibility(View.GONE);
//        }
    }

    private boolean isRefresh = false;

    /**
     * fill Date from model class
     *
     * @param doorSensorResModel
     */
    private void fillData(DoorSensorResModel doorSensorResModel) {

        tempLists = doorSensorResModel.getDate().getDoorLists();

        if (tempLists.length > 0) {

            img_door_on.setImageResource(R.drawable.on_door);

//            notiSwitchOnOff.setChecked(Integer.parseInt(tempLists[0].getmIsPushEnable()) == 1);
//            notiSwitchOnOff.setVisibility(View.VISIBLE);

            sensorName.setText(tempLists[0].getmDoorSensorName());
            mSensorName = tempLists[0].getmDoorSensorName();
            sensorName.setSelection(sensorName.getText().length());
           /* if(isRefresh){
                setDoorUnreadCount(mSocketCountVal);
            }else{
                setDoorUnreadCount(door_unread_count);
            }*/

            int perc = 0;
            if (!TextUtils.isEmpty(tempLists[0].getmDoorSensorPerc())) {
                try {
                    perc = (int) Double.parseDouble((tempLists[0].getmDoorSensorPerc()));
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
            }
            batteryPercentage.setText(perc + "%");
            imgBattery.setImageResource(Common.getBatteryIcon(tempLists[0].getmDoorSensorPerc()));

            if (doorSensorResModel.getDate().getDoorLists()[0].getNotificationLists().length > 0) {

                door_sensor_list.setVisibility(View.VISIBLE);
                txtAlertTempCount.setVisibility(View.VISIBLE);
                txt_empty_notification.setVisibility(View.GONE);
                txtAlertTempCount.setText("(" + doorSensorResModel.getDate().getDoorLists()[0].getNotificationLists().length + " Added" + ")");
                tempSensorInfoAdapter = new DoorSensorInfoAdapter(doorSensorResModel.getDate().getDoorLists()[0].getNotificationLists(), true, DoorSensorInfoActivity.this);
                door_sensor_list.setAdapter(tempSensorInfoAdapter);
            } else {
                txtAlertTempCount.setVisibility(View.INVISIBLE);
                door_sensor_list.setVisibility(View.GONE);
                txt_empty_notification.setVisibility(View.VISIBLE);
            }


            if (doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs() != null) {
                if (doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length > 0) {
                    int countTemp=doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length;
                    if(countTemp>99){
                        txtAlertCount.setText( "99+");
                    }else {
                        txtAlertCount.setText(""+countTemp);
                    }

                } else {
                    txtAlertCount.setText("0");
                }

                if (doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length > 0) {

//                    txtAlertCount.setVisibility(View.VISIBLE);
                    int countTemp=doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length;
                    if(countTemp>99){
                        txtAlertCount.setText("99+");
                    }else {
                        txtAlertCount.setText(""+countTemp);
                    }
                   // txtAlertCount.setText("" + doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs().length);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DoorSensorInfoActivity.this);
                    recyclerAlert.setLayoutManager(linearLayoutManager);
                    DoorAlertAdapter doorAlertAdapter = new DoorAlertAdapter(DoorSensorInfoActivity.this, doorSensorResModel.getDate().getDoorLists()[0].getUnreadLogs());
                    recyclerAlert.setAdapter(doorAlertAdapter);
                } else {
                    txtAlertCount.setVisibility(View.GONE);
                    recyclerAlert.setVisibility(View.GONE);
                    txt_empty_notificationALert.setVisibility(View.VISIBLE);
                }

            } else {
                txtAlertCount.setVisibility(View.GONE);
                recyclerAlert.setVisibility(View.GONE);
                txt_empty_notificationALert.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        menuAdd.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            updateDoorSensor();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDoorSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            // Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(sensorName.getText().toString().trim())) {
            sensorName.setError("Enter Door Sensor Name");
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.UPDATE_DOOR_SENSOR;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("door_sensor_id", door_sensor_id);
            jsonNotification.put("door_sensor_name", sensorName.getText().toString().trim());
            jsonNotification.put("room_id", door_room_id);
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
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                        //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void bindView() {

        door_sensor_list = (RecyclerView) findViewById(R.id.sensor_list);
        recyclerAlert = (RecyclerView) findViewById(R.id.recyclerAlert);
        door_sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        door_sensor_list.setVerticalScrollBarEnabled(true);

        view_rel_badge = (ImageView) findViewById(R.id.view_rel_badge);

        sensorName = (EditText) findViewById(R.id.sensor_name);
        linearAlertDown = (LinearLayout) findViewById(R.id.linearAlertDown);
        linearAlertExpand = (LinearLayout) findViewById(R.id.linearAlertExpand);
        toggleAlert = (ToggleButton) findViewById(R.id.toggleAlert);
        txtAlertTempCount = (TextView) findViewById(R.id.txtAlertTempCount);

        sensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        sensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});


        //sensorLog = (TextView) findViewById(R.id.iv_icon_badge);
        imgBattery = (ImageView) findViewById(R.id.img_battery);
        img_door_on = (ImageView) findViewById(R.id.img_temp_on);
        batteryPercentage = (TextView) findViewById(R.id.txt_bettery_per);
        //notiSwitchOnOff = (SwitchCompat) findViewById(R.id.switch_noti_onoff);
        doorAddButton = (TextView) findViewById(R.id.btnAdd);
        txtTempCount = (TextView) findViewById(R.id.txtTempCount);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification_alert);
        txt_empty_notificationALert = (TextView) findViewById(R.id.txt_empty_notification);
        txtAlertCount = (TextView) findViewById(R.id.txtAlertCount);

        btn_delete.setOnClickListener(this);
        view_rel_badge.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);

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

                    if(recyclerAlert.getVisibility()==View.VISIBLE){
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 400);
                        recyclerAlert.setLayoutParams(lp);
                    }
                }
            }
        });

        doorAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorSensorNotification(null, false);
            }
        });

//        notiSwitchOnOff.setOnTouchListener(new OnSwipeTouchListener(DoorSensorInfoActivity.this){
//            @Override
//            public void onClick() {
//                super.onClick();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff,!notiSwitchOnOff.isChecked(),false);
//            }
//
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff,!notiSwitchOnOff.isChecked(),false);
//            }
//
//            @Override
//            public void onSwipeRight() {
//                super.onSwipeRight();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff,!notiSwitchOnOff.isChecked(),false);
//            }
//
//        });

//        notiSwitchOnOff.setVisibility(View.GONE);

        setDoorUnreadCount(door_unread_count);
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
        builder1.setMessage("Do you want " + (isActive ? "enable " : "disable ") + " notificaiton ?");
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
            //  Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        String webUrl = "";
        if (!isNotification) {
            webUrl = ChatApplication.url + Constants.CHANGE_DOOR_SENSOR_STATUS;
        } else {
            webUrl = ChatApplication.url + Constants.CHANGE_DOOR_SENSOR_NOTIFICATION_STATUS;
        }

        JSONObject jsonNotification = new JSONObject();

        try {
            if (isNotification) {
                jsonNotification.put("door_sensor_notification_id", doorSensorNotificationId);
                jsonNotification.put("is_active", isActive ? 1 : 0);
            } else {
                jsonNotification.put("is_push_enable", isActive ? 1 : 0);
            }
            jsonNotification.put("door_sensor_id", door_sensor_id);
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
                    }
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

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

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Delete Temp Sensor", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteDoorSensor();
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        } else if (id == R.id.view_rel_badge) {
//            Intent intent = new Intent(DoorSensorInfoActivity.this,SensorDoorLogActivity.class);
//            intent.putExtra("sensor_name",mSensorName);
//            intent.putExtra("sensor_type","door");
//            intent.putExtra("is_sensor",true);
//            startActivity(intent);

            view_rel_badge.setClickable(false);
            unreadApiCall(true);
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
        }

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

        String webUrl = ChatApplication.url + Constants.DELETE_DOOR_SENSOR;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("door_sensor_id", door_sensor_id);
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

                    //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

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
     * @param o
     * @param isEdit
     */

    Dialog doorSensorNotificationDialog;

    private void doorSensorNotification(final DoorSensorResModel.DATA.DoorList.NotificationList notification, final boolean isEdit) {

        doorSensorNotificationDialog = new Dialog(this);
        doorSensorNotificationDialog.setContentView(R.layout.dialog_door_sensor_notification);
        doorSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = (Button) doorSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = doorSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_start_time = (EditText) doorSensorNotificationDialog.findViewById(R.id.edit_start_time);
        final EditText edti_end_time = (EditText) doorSensorNotificationDialog.findViewById(R.id.edit_end_time);

        edit_start_time.setFocusable(false);
        edti_end_time.setFocusable(false);

        if (isEdit) {

            try {
                String editStartTime = DateHelper.formateDate(DateHelper.parseTimeSimple(notification.getmStartDateTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM);
                String editEndTime = DateHelper.formateDate(DateHelper.parseTimeSimple(notification.getmEndDateTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM);

                edit_start_time.setText(editStartTime);
                edti_end_time.setText(editEndTime);

                Calendar calendarStart = Calendar.getInstance();
                calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notification.getmStartDateTime().split(":")[0]));
                calendarStart.set(Calendar.MINUTE, Integer.parseInt(notification.getmStartDateTime().split(":")[1]));

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(notification.getmEndDateTime().split(":")[0]));
                calendarEnd.set(Calendar.MINUTE, Integer.parseInt(notification.getmEndDateTime().split(":")[1]));

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

    /**
     * @param time
     */
    private void convStrToTime(String time) {

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = dateFormat2.parse(time);

            String out = dateFormat2.format(date);
        } catch (ParseException e) {
        }
    }

    /**
     * @param edtText
     * @param isStartTime
     */
    private String mStartTime = "";
    private String mEndTime = "";

    private void showTimePickerDialog(final EditText edtText, final boolean isStartTime) {

        hideKeyboard();

        /*Calendar mcurrentTime = Calendar.getInstance();
        String eEndDate = edtText.getText().toString().trim();
        Calendar edtCalendar = Calendar.getInstance();

        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        if(!TextUtils.isEmpty(eEndDate)){
            try {
                Date conEndDate = ActivityHelper.parseTimeSimple(eEndDate, DateHelper.DATE_FROMATE_HH_MM_AMPM);
                edtCalendar.setTime(conEndDate);

                hour = edtCalendar.get(Calendar.HOUR_OF_DAY);
                minute = edtCalendar.get(Calendar.MINUTE);

            } catch (ParseException e) {

                e.printStackTrace();
            }
        }else{
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }

        Log.d("TimePickerTime","Hour :" + hour + " Minute : " + minute);*/

        /*--TimePicker Dialg--*/
        TimePickerDialog mTimePicker;
       /* mTimePicker = new TimePickerDialog(DoorSensorInfoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                calendar.set(Calendar.MINUTE,selectedMinute);


                String dateTime = selectedHour + ":" + selectedMinute;
                Log.d("TimePi","str : " + dateTime); //19:17
                try {

                    String conDate = DateHelper.formateDate(DateHelper.parseTimeSimple(dateTime,DateHelper.DATE_FROMATE_HH_MM),DateHelper.DATE_FROMATE_H_M_AMPM);
                    edtText.setText(conDate);
                    edtText.setError(null);
                    edtText.setFocusable(false);

                    if(isStartTime){
                        mStartTime = startTimeFormat().format(calendar.getTime());
                    }else{
                        mEndTime = startTimeFormat().format(calendar.getTime());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, hour, minute, false);*///Yes 24 hour time
        //mTimePicker.show();
        /*--End--*/

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

    private SimpleDateFormat startTimeFormat;

    public SimpleDateFormat startTimeFormat() {
        return startTimeFormat = new SimpleDateFormat("H:mm");
    }

    /**
     * @param doorSensorNotificationDialog
     * @param stat_time
     * @param end_time
     */
    private void addNotification(final Dialog doorSensorNotificationDialog, EditText stat_time, final EditText end_time, boolean isEdit, DoorSensorResModel.DATA.DoorList.NotificationList notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(stat_time.getText().toString().trim())) {
            stat_time.setFocusableInTouchMode(true);
            stat_time.requestFocus();
            stat_time.setError("Select Start Time");
            //   Toast.makeText(getApplicationContext(),"Select Start Time", Toast.LENGTH_SHORT).show();
            /*Toast toast= Toast.makeText(getApplicationContext(),
                    "Select Start Time", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();*/
            return;
        }
        if (TextUtils.isEmpty(end_time.getText().toString().trim())) {
            end_time.setFocusableInTouchMode(true);
            end_time.requestFocus();
            end_time.setError("Select End Time");
            //   Toast.makeText(getApplicationContext(), "Select End Time", Toast.LENGTH_SHORT).show();
           /* Toast toast= Toast.makeText(getApplicationContext(),
                    "Select End Time", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();*/
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject jsonNotification = new JSONObject();

        try {

            if (isEdit) {
                jsonNotification.put("door_sensor_notification_id", notification.getmDoorSensorNotificationId());
            }

            jsonNotification.put("door_sensor_id", door_sensor_id);
            jsonNotification.put("start_datetime", mStartTime);
            jsonNotification.put("end_datetime", mEndTime);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String webUrl = "";
        if (!isEdit) {
            webUrl = ChatApplication.url + Constants.ADD_DOOR_SENSOR_NOTIFICATION;
        } else {
            webUrl = ChatApplication.url + Constants.UPDATE_DOOR_SENSOR_NOTIFICATION;
        }

        //  ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }

                        isRefresh = true;
                        //setDoorUnreadCount(mSocketCountVal);
                        doorSensorNotificationDialog.dismiss();
                        getDoorSensorDetails();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            end_time.setFocusableInTouchMode(true);
                            end_time.requestFocus();
                            end_time.setError(message);
                            //showToastCenter(message);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
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

    private void deleteDoorSensorNotification(DoorSensorResModel.DATA.DoorList.NotificationList notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.DELETE_DOOR_SENSOR_NOTIFICATION;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("door_sensor_notification_id", notification.getmDoorSensorNotificationId());
            jsonNotification.put("door_sensor_id", door_sensor_id);
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
                        // setDoorUnreadCount(mSocketCountVal);
                        getDoorSensorDetails();
                    }
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                        //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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
    public void onEditOpetion(final DoorSensorResModel.DATA.DoorList.NotificationList notification, int position, boolean isEdit) {
        if (isEdit) {
            doorSensorNotification(notification, isEdit);
        } else {

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
    }

    @Override
    public void onSwitchChanged(DoorSensorResModel.DATA.DoorList.NotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive) {
        showAlertDialog(notification.getmDoorSensorNotificationId(), swithcCompact, isActive, true);
    }

    public void unreadApiCall(final boolean b) {

        if(recyclerAlert.getVisibility()==View.VISIBLE){

        }else {
            checkIntent(b);
            return;
        }
        ChatApplication.logDisplay("tempLists is "+tempLists.length);
        if(tempLists.length==0){
            return;
        }

        if(TextUtils.isEmpty(tempLists[0].getmDoorSensorMoudleId())){
            return;
        }


        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;
        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();

//            for (RoomVO roomVO : unReadLogs) {

                JSONObject object = new JSONObject();

                object.put("sensor_type", "door");

//                if (roomVO.getRoomName().equalsIgnoreCase("All")) {
                    object.put("module_id", ""+tempLists[0].getmDoorSensorMoudleId());
                    object.put("room_id", ""+tempLists[0].getRoom_id());
                    object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
//                } else {
//                    object.put("module_id", roomVO.getModule_id());
//                    object.put("room_id", roomVO.getRoomId());
//                }

                jsonArray.put(object);
//            }

            jsonObject.put("update_logs", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("door is "+ jsonObject.toString());
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                checkIntent(b);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                checkIntent(b);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void checkIntent(boolean b){
            if (b) {
                Intent intent = new Intent(DoorSensorInfoActivity.this, DeviceLogActivity.class);
                intent.putExtra("ROOM_ID", tempLists[0].getRoom_id());
                intent.putExtra("Mood_Id", ""+tempLists[0].getmDoorSensorMoudleId());
                intent.putExtra("activity_type", "door");
                intent.putExtra("IS_SENSOR", true);
                intent.putExtra("tabSelect", "show");
                intent.putExtra("isCheckActivity","doorSensor");
                intent.putExtra("isRoomName",""+sensorName.getText().toString());
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
