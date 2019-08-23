package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.HumiditySensorAdapter;
import com.spike.bot.adapter.TempMultiSensorAdapter;
import com.spike.bot.adapter.TempMultiSensorAlertAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.listener.OnHumitySensorContextMenu;
import com.spike.bot.listener.OnNotificationSensorContextMenu;
import com.spike.bot.model.SensorResModel;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static com.spike.bot.core.Common.showToast;
import static com.spike.bot.core.Common.showToastCenter;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public class MultiSensorActivity extends AppCompatActivity implements View.OnClickListener,
        OnNotificationSensorContextMenu, OnHumitySensorContextMenu,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView sensor_list, recyclerAlert,sensor_list_temp;
    private TempMultiSensorAdapter tempSensorInfoAdapter;
    private HumiditySensorAdapter humiditySensorAdapter;

    public ScrollView scrollviewMulti;
    private EditText sensorName;
    public View viewEditSensor;
    private TextView txtAlert, txtAlertCount, txtEmpty,txtEmpty_temp,txtHumity,txtGasSensor;
    private ImageView imgBattery, view_rel_badge,iv_icon_edit,imgLog;
    private TextView batteryPercentage, txt_empty_notification, txtTempCount;
    private TextView tempCFValue, txtTempAlertCount;
    private TextView txtCButton, txtFButton,txtTempAlertCount_temp,txtTempCount_temp;
//    private SwitchCompat notiSwitchOnOff,switch_noti_onoff_temp;
    private TextView txtAddButton,txtAddButtonTemp;
    private Button btn_delete;
    private ToggleButton toggleAlert,toggleAlert_temp,toggleAlertSensor;
    private LinearLayout linearAlertExpand, linearAlertDown,linearAlertExpand_temp,linearAlertDown_temp,linearMultisensor;
    private boolean flagAlert = false,flagAlertTemp=false,flagEditSensor=false,flagSensorNoti=false;

    private String temp_sensor_id, temp_room_name, temp_room_id, temp_module_id;
    private int isCFSelected = -1;
    private String mSensorName;
    private String temp_unread_count = "";
    private Socket mSocket;

    SensorResModel.DATA.MultiSensorList[] tempLists;
    ArrayList<SensorResModel.DATA.MultiSensorList.TempNotificationList> notificationList =new ArrayList<>();
    ArrayList<SensorResModel.DATA.MultiSensorList.HumidityNotificationList> notificationHumidityList =new ArrayList<>();

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
        setContentView(R.layout.activity_multi_sensor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        temp_sensor_id = getIntent().getStringExtra("temp_sensor_id");
        temp_room_name = getIntent().getStringExtra("temp_room_name");
        temp_room_id = getIntent().getStringExtra("temp_room_id");
        temp_unread_count = getIntent().getStringExtra("temp_unread_count");
        temp_module_id = getIntent().getStringExtra("temp_module_id");

        toolbar.setTitle(temp_room_name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ChatApplication.getInstance().setConnectivityListener(this);
        if (ChatApplication.isLogResume) {
            ChatApplication.isLogResume = false;
            ChatApplication.isLocalFragmentResume = true;
            //  getSensorDetails();
//            sensorLog.setVisibility(View.GONE);
        }
        bindView();

        connectivityReceiver = new ConnectivityReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        view_rel_badge.setClickable(true);
        getSensorDetails();
        scrollviewMulti.fullScroll(ScrollView.FOCUS_UP);
    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }

        if(mSocket!=null){
            mSocket.on("changeTempSensorValue", changeTempSensorValue);
            mSocket.on("unReadCount", unReadCount);
            mSocket.on("changeMultiSensorValue", changeMultiSensorValue);
        }
    }


    private Emitter.Listener changeTempSensorValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            MultiSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String temp_sensor_id = object.getString("temp_sensor_id");
                            String temp_celsius = object.getString("temp_celsius");
                            String temp_fahrenheit = object.getString("temp_fahrenheit");


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
            MultiSensorActivity.this.runOnUiThread(new Runnable() {
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

                            if (sensor_type.equalsIgnoreCase("temp") && temp_module_id.equalsIgnoreCase(module_id) && temp_room_id.equalsIgnoreCase(room_id)) {
                                setTempUnreadCount(sensor_unread);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener changeMultiSensorValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MultiSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            //[{
                            //    "gas_status" = Normal;
                            //    humidity = 22;
                            //    "is_in_C" = 1;
                            //    "multi_sensor_id" = "1561119898083_wE4svmRfN";
                            //    "room_id" = "1559719722430_hp_nJGEwO";
                            //    "room_order" = 0;
                            //    "temp_in_C" = 28;
                            //    "temp_in_F" = 82;
                            //}]

                            txtHumity.setText(object.optString("humidity")+" %");

                            if (isCFSelected==1) {
                                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                                tempCFValue.setText(object.optString("temp_in_C") +" ");
                            } else {
                                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                                tempCFValue.setText(object.optString("temp_in_F")+" ");
                            }

                            if(!TextUtils.isEmpty(object.optString("gas_status"))){
                                if(object.optString("gas_status").equalsIgnoreCase("Normal")){
                                    txtGasSensor.setText("Normal");
                                    txtGasSensor.setTextColor(getResources().getColor(R.color.username5));
                                }else {
                                    txtGasSensor.setText("High");
                                    txtGasSensor.setTextColor(getResources().getColor(R.color.automation_red));
                                }

                            }

                            ChatApplication.logDisplay("changeMultiSensorValue is "+object);

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
            mSocket.off("changeTempSensorValue", changeTempSensorValue);
            mSocket.off("unReadCount", unReadCount);
            mSocket.off("changeMultiSensorValue", changeMultiSensorValue);
        }
    }

    private GestureDetector gestureDetector;




    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    private void bindView() {

        sensor_list = (RecyclerView) findViewById(R.id.sensor_list);
        sensor_list_temp = (RecyclerView) findViewById(R.id.sensor_list_temp);
        sensor_list.setHasFixedSize(true);
        sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        sensor_list.setVerticalScrollBarEnabled(true);

        sensor_list_temp.setHasFixedSize(true);
        sensor_list_temp.setLayoutManager(new GridLayoutManager(this, 1));
        sensor_list_temp.setVerticalScrollBarEnabled(true);

        view_rel_badge = (ImageView) findViewById(R.id.view_rel_badge);
        recyclerAlert = (RecyclerView) findViewById(R.id.recyclerAlert);

        sensorName = (EditText) findViewById(R.id.sensor_name);
//        sensorLog = (TextView) findViewById(R.id.iv_icon_badge);
        imgBattery = (ImageView) findViewById(R.id.img_battery);
        batteryPercentage = (TextView) findViewById(R.id.txt_bettery_per);
        tempCFValue = (TextView) findViewById(R.id.txt_tmp_incf);
        linearAlertDown = (LinearLayout) findViewById(R.id.linearAlertDown);
        linearAlertDown_temp = (LinearLayout) findViewById(R.id.linearAlertDown_temp);
        linearAlertExpand = (LinearLayout) findViewById(R.id.linearAlertExpand);
        linearAlertExpand_temp = (LinearLayout) findViewById(R.id.linearAlertExpand_temp);
        toggleAlert = (ToggleButton) findViewById(R.id.toggleAlert);
        toggleAlert_temp = (ToggleButton) findViewById(R.id.toggleAlert_temp);
        txtAlertCount = (TextView) findViewById(R.id.txtAlertCount);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        txtEmpty_temp = (TextView) findViewById(R.id.txtEmpty_temp);
        txtTempAlertCount = (TextView) findViewById(R.id.txtTempAlertCount);
        txtTempAlertCount_temp = (TextView) findViewById(R.id.txtTempAlertCount_temp);
        txtHumity = (TextView) findViewById(R.id.txtHumity);
        iv_icon_edit =  findViewById(R.id.iv_icon_edit);
        viewEditSensor =  findViewById(R.id.viewEditSensor);
        scrollviewMulti =  findViewById(R.id.scrollviewMulti);
        toggleAlertSensor =  findViewById(R.id.toggleAlertSensor);
        txtGasSensor =  findViewById(R.id.txtGasSensor);
        linearMultisensor =  findViewById(R.id.linearMultisensor);
        imgLog =  findViewById(R.id.imgLog);

        txtTempCount = (TextView) findViewById(R.id.txtTempCount);
        txtTempCount_temp = (TextView) findViewById(R.id.txtTempCount_temp);
        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification);

//        notiSwitchOnOff = (SwitchCompat) findViewById(R.id.switch_noti_onoff);
//        switch_noti_onoff_temp = (SwitchCompat) findViewById(R.id.switch_noti_onoff_temp);
        txtAddButton = (TextView) findViewById(R.id.btnAdd);
        txtAddButtonTemp = (TextView) findViewById(R.id.btnAdd_temp);

        txtCButton = (TextView) findViewById(R.id.txt_c_button);
        txtFButton = (TextView) findViewById(R.id.txt_f_button);

        btn_delete = (Button) findViewById(R.id.btn_delete);

        sensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        sensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        txtCButton.setOnClickListener(this);
        txtFButton.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);
        linearAlertDown_temp.setOnClickListener(this);
        iv_icon_edit.setOnClickListener(this);
        sensorName.setOnClickListener(this);
        linearMultisensor.setOnClickListener(this);
        toggleAlertSensor.setOnClickListener(this);
        imgLog.setOnClickListener(this);

        view_rel_badge.setOnClickListener(this);

        txtAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSensorNotification(null, false);
            }
        });

        txtAddButtonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humitySensorNotification(null, false);
            }
        });

        sensorName.setFocusable(false);
        sensorName.setFocusableInTouchMode(false);
        sensorName.setClickable(false);
        // gestureDetector = new GestureDetector(this, new SingleTapConfirm());

//        notiSwitchOnOff.setOnTouchListener(new OnSwipeTouchListener(MultiSensorActivity.this) {
//            @Override
//            public void onClick() {
//                super.onClick();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
//            }
//
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
//            }
//
//            @Override
//            public void onSwipeRight() {
//                super.onSwipeRight();
//                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
//                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
//            }
//
//        });

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
                }
            }
        });

        toggleAlert_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagAlertTemp) {
                    flagAlertTemp = false;
                    toggleAlert_temp.setChecked(flagAlertTemp);
                    linearAlertExpand_temp.setVisibility(View.GONE);
                } else {
                    flagAlertTemp = true;
                    toggleAlert_temp.setChecked(flagAlertTemp);
                    linearAlertExpand_temp.setVisibility(View.VISIBLE);
                }
            }
        });

        toggleAlertSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagSensorNoti){
                    flagSensorNoti=false;
                    toggleAlertSensor.setChecked(flagSensorNoti);
                    recyclerAlert.setVisibility(View.GONE);
                }else {
                    flagSensorNoti=true;
                    toggleAlertSensor.setChecked(flagSensorNoti);
                    recyclerAlert.setVisibility(View.VISIBLE);
                }
            }
        });
        /*notiSwitchOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notiSwitchOnOff.setChecked(!notiSwitchOnOff.isChecked());
                showAlertDialog("", notiSwitchOnOff,!notiSwitchOnOff.isChecked(),false);
            }
        });*/

//        notiSwitchOnOff.setVisibility(View.GONE);

        setTempUnreadCount(temp_unread_count);


    }

    /**
     * @param tempSensorNotificationId
     * @param notiSwitchOnOff
     * @param isActive
     * @param isNotification
     * @param position
     */
    private void showAlertDialog(final String tempSensorNotificationId, final SwitchCompat notiSwitchOnOff, final boolean isActive, final boolean isNotification, final int position) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Notification Alert");
        builder1.setMessage("Do you want " + (isActive ? "enable " : "disable ") + " notificaiton ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tempSensorNotificationStatus(tempSensorNotificationId, notiSwitchOnOff, !notiSwitchOnOff.isChecked(), isNotification, position);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        if (!alert11.isShowing()) {
            alert11.show();
        }
    }

    /**
     * call api for change notification sensor status on/off
     *
     * @param tempSensorNotificationId - if is Coming from adapter
     * @param notiSwitchOnOff
     * @param isActive
     * @param position
     */
    private void tempSensorNotificationStatus(String tempSensorNotificationId, final SwitchCompat notiSwitchOnOff, boolean isActive, boolean isNotification, final int position) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        //{
        //	"multi_sensor_id": "1559654114379_MnqPvjkOE",
        //	"is_push_enable":0,
        //	"user_id":"1559035111028_VojOpeeBF",
        //	"phone_id":"1234567",
        //	"phone_type":"Android"
        //	}

        //{
        //	   "multi_sensor_notification_id": "1559711839917_iVUxolRrq",
        //	    "multi_sensor_id": "1559654114379_MnqPvjkOE",
        //	    "is_active":0,
        //	    "user_id":"1559035111028_VojOpeeBF",
        //		"phone_id":"1234567",
        //		"phone_type":"Android"
        //
        //	}
        //

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = "";
        if (!isNotification) {
            webUrl = ChatApplication.url + Constants.changeMultiSensorStatus;
        } else {
            webUrl = ChatApplication.url + Constants.changeMultiSensorNotificationStatus;
        }

        JSONObject jsonNotification = new JSONObject();

        try {
            if (isNotification) {
                jsonNotification.put("multi_sensor_notification_id", tempSensorNotificationId);
                jsonNotification.put("is_active", isActive ? 1 : 0);
            } else {
                jsonNotification.put("is_push_enable", isActive ? 1 : 0);
            }
            jsonNotification.put("multi_sensor_id", temp_sensor_id);
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
                        //  notiSwitchOnOff.setChecked(!notiSwitchOnOff.isChecked());
                        JSONObject dataObject = result.getJSONObject("data");
                        String is_active = dataObject.getString("is_active");
                        if (position != -1) {
                            sensorResModel.getDate().getMultiSensorList()[position].getTempNotificationList().get(position).setIsActive(is_active);
                        }

                        boolean isCF = false;
                        if (isCFSelected == 1) {
                            isCF = true;
                        }

                        notificationList = sensorResModel.getDate().getMultiSensorList()[0].getTempNotificationList();
                        tempSensorInfoAdapter.notifyItemChanged(position, sensorResModel.getDate().getMultiSensorList()[0].getTempNotificationList().get(position));

                        //tempSensorInfoAdapter = new TempSensorInfoAdapter(notificationList,isCF,MultiSensorActivity.this);
                        //sensor_list.setAdapter(tempSensorInfoAdapter);
                        tempSensorInfoAdapter.notifyDataSetChanged();

                        //adapter auto scroll
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menulog = menu.findItem(R.id.action_log).setVisible(false);
        MenuItem menuAdd = menu.findItem(R.id.action_add).setVisible(false);

        Drawable drawable = menu.findItem(R.id.action_log).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.automation_white));
        menulog.setIcon(drawable);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            updateTempSensor();
        }else if (id == R.id.action_log) {
//            unreadApiCall(true);
            checkIntent(true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTempSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            // Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(sensorName.getText().toString().trim())) {
            sensorName.setError("Enter Sensor Name");
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.updateMultiSensor;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("multi_sensor_id", temp_sensor_id);
            jsonNotification.put("multi_sensor_name", sensorName.getText().toString().trim());
            jsonNotification.put("room_id", temp_room_id);
            jsonNotification.put("is_in_C", isCFSelected);
            jsonNotification.put("room_name", temp_room_name);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
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


    //Common.setBackground(this, text_schedule_1, true);

    private TextView text_day_1, text_day_2, text_day_3, text_day_4, text_day_5, text_day_6, text_day_7;

    Dialog tempSensorNotificationDialog;

    /**
     * @param notificationList
     * @param isEdit
     */

    private void humitySensorNotification(final SensorResModel.DATA.MultiSensorList.HumidityNotificationList notificationList, final boolean isEdit) {

        tempSensorNotificationDialog = new Dialog(this);
        tempSensorNotificationDialog.setContentView(R.layout.dialog_temp_sensor_notificaiton);
        tempSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = (Button) tempSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = tempSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_min_value = (EditText) tempSensorNotificationDialog.findViewById(R.id.edit_min_value);
        final EditText edit_max_value = (EditText) tempSensorNotificationDialog.findViewById(R.id.edit_max_value);

        final TextView txt_notification_alert = (TextView) tempSensorNotificationDialog.findViewById(R.id.txt_notification_alert);

        txt_notification_alert.setText("Humidity Alert");
        text_day_1 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_1);
        text_day_2 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_2);
        text_day_3 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_3);
        text_day_4 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_4);
        text_day_5 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_5);
        text_day_6 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_6);
        text_day_7 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_7);

//        if (isCFSelected == 1) {
//            edit_min_value.setHint("Min Value " + Common.getC());
//            edit_max_value.setHint("Max Value " + Common.getC());
//        } else {
            edit_min_value.setHint("Min %" );
            edit_max_value.setHint("Max %");
//        }

        edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit_min_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int cStartLength = 2, cEndLength = 3;
                if (isCFSelected == 1) {  //1C 0F
                    cStartLength = 2;
                    cEndLength = 3;
                } else {
                    cStartLength = 3;
                    cEndLength = 4;
                }

                if (edit_min_value.getText().toString().contains("-")) {
                    edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cEndLength)});
                } else {
                    edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cStartLength)});
                }
            }
        });

        edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit_max_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cStartLength = 2, cEndLength = 3;
                if (isCFSelected == 1) {  //1C 0F
                    cStartLength = 2;
                    cEndLength = 3;
                } else {
                    cStartLength = 3;
                    cEndLength = 4;
                }

                if (edit_max_value.getText().toString().contains("-")) {
                    edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cEndLength)});
                } else {
                    edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cStartLength)});
                }
            }
        });


        text_day_1.setOnClickListener(this);
        text_day_2.setOnClickListener(this);
        text_day_3.setOnClickListener(this);
        text_day_4.setOnClickListener(this);
        text_day_5.setOnClickListener(this);
        text_day_6.setOnClickListener(this);
        text_day_7.setOnClickListener(this);

        text_day_1.setTag(false);
        text_day_2.setTag(false);
        text_day_3.setTag(false);
        text_day_4.setTag(false);
        text_day_5.setTag(false);
        text_day_6.setTag(false);
        text_day_7.setTag(false);

        String dayOfWeek = "";

        if (!isEdit) {
            Date d = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            dayOfWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            Common.setBackground(this, text_day_1, true);
            Common.setBackground(this, text_day_2, true);
            Common.setBackground(this, text_day_3, true);
            Common.setBackground(this, text_day_4, true);
            Common.setBackground(this, text_day_5, true);
            Common.setBackground(this, text_day_6, true);
            Common.setBackground(this, text_day_7, true);

        } else {

            dayOfWeek = notificationList.getDays();
//            if (isCFSelected == 1) {
                edit_min_value.setText(""+notificationList.getMinHumidity());
                edit_max_value.setText(""+notificationList.getMaxHumidity());
//            } else {
//                edit_min_value.setText(notificationList.getMinHumidity());
//                edit_max_value.setText(notificationList.getMaxHumidity());
//            }

            if (dayOfWeek.contains("0")) {
                Common.setBackground(this, text_day_1, true);
            }
            if (dayOfWeek.contains("1")) {
                Common.setBackground(this, text_day_2, true);
            }
            if (dayOfWeek.contains("2")) {
                Common.setBackground(this, text_day_3, true);
            }
            if (dayOfWeek.contains("3")) {
                Common.setBackground(this, text_day_4, true);
            }
            if (dayOfWeek.contains("4")) {
                Common.setBackground(this, text_day_5, true);
            }
            if (dayOfWeek.contains("5")) {
                Common.setBackground(this, text_day_6, true);
            }
            if (dayOfWeek.contains("6")) {
                Common.setBackground(this, text_day_7, true);
            }

        }

        closeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSensorNotificationDialog.dismiss();
            }
        });

        button_save_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                addHumity(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getMultiSensorNotificationId() : "");
            }

        });

        tempSensorNotificationDialog.show();

    }

    private void tempSensorNotification(final SensorResModel.DATA.MultiSensorList.TempNotificationList notificationList, final boolean isEdit) {

        tempSensorNotificationDialog = new Dialog(this);
        tempSensorNotificationDialog.setContentView(R.layout.dialog_temp_sensor_notificaiton);
        tempSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = (Button) tempSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = tempSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_min_value = (EditText) tempSensorNotificationDialog.findViewById(R.id.edit_min_value);
        final EditText edit_max_value = (EditText) tempSensorNotificationDialog.findViewById(R.id.edit_max_value);

        final TextView txt_notification_alert = (TextView) tempSensorNotificationDialog.findViewById(R.id.txt_notification_alert);

        text_day_1 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_1);
        text_day_2 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_2);
        text_day_3 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_3);
        text_day_4 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_4);
        text_day_5 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_5);
        text_day_6 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_6);
        text_day_7 = (TextView) tempSensorNotificationDialog.findViewById(R.id.text_day_7);

        if (isCFSelected == 1) {
            edit_min_value.setHint("Min Value " + Common.getC());
            edit_max_value.setHint("Max Value " + Common.getC());
        } else {
            edit_min_value.setHint("Min Value " + Common.getF());
            edit_max_value.setHint("Max Value " + Common.getF());
        }

        edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit_min_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int cStartLength = 2, cEndLength = 3;
                if (isCFSelected == 1) {  //1C 0F
                    cStartLength = 2;
                    cEndLength = 3;
                } else {
                    cStartLength = 3;
                    cEndLength = 4;
                }

                if (edit_min_value.getText().toString().contains("-")) {
                    edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cEndLength)});
                } else {
                    edit_min_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cStartLength)});
                }
            }
        });

        edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edit_max_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cStartLength = 2, cEndLength = 3;
                if (isCFSelected == 1) {  //1C 0F
                    cStartLength = 2;
                    cEndLength = 3;
                } else {
                    cStartLength = 3;
                    cEndLength = 4;
                }

                if (edit_max_value.getText().toString().contains("-")) {
                    edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cEndLength)});
                } else {
                    edit_max_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cStartLength)});
                }
            }
        });


        text_day_1.setOnClickListener(this);
        text_day_2.setOnClickListener(this);
        text_day_3.setOnClickListener(this);
        text_day_4.setOnClickListener(this);
        text_day_5.setOnClickListener(this);
        text_day_6.setOnClickListener(this);
        text_day_7.setOnClickListener(this);

        text_day_1.setTag(false);
        text_day_2.setTag(false);
        text_day_3.setTag(false);
        text_day_4.setTag(false);
        text_day_5.setTag(false);
        text_day_6.setTag(false);
        text_day_7.setTag(false);

        String dayOfWeek = "";

        if (!isEdit) {
            Date d = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            dayOfWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            Common.setBackground(this, text_day_1, true);
            Common.setBackground(this, text_day_2, true);
            Common.setBackground(this, text_day_3, true);
            Common.setBackground(this, text_day_4, true);
            Common.setBackground(this, text_day_5, true);
            Common.setBackground(this, text_day_6, true);
            Common.setBackground(this, text_day_7, true);

        } else {

            dayOfWeek = notificationList.getDays();
            if (isCFSelected == 1) {
                edit_min_value.setText(notificationList.getMinValueC());
                edit_max_value.setText(notificationList.getMaxValueC());
            } else {
                edit_min_value.setText(notificationList.getMinValueF());
                edit_max_value.setText(notificationList.getMaxValueF());
            }

            if (dayOfWeek.contains("0")) {
                Common.setBackground(this, text_day_1, true);
            }
            if (dayOfWeek.contains("1")) {
                Common.setBackground(this, text_day_2, true);
            }
            if (dayOfWeek.contains("2")) {
                Common.setBackground(this, text_day_3, true);
            }
            if (dayOfWeek.contains("3")) {
                Common.setBackground(this, text_day_4, true);
            }
            if (dayOfWeek.contains("4")) {
                Common.setBackground(this, text_day_5, true);
            }
            if (dayOfWeek.contains("5")) {
                Common.setBackground(this, text_day_6, true);
            }
            if (dayOfWeek.contains("6")) {
                Common.setBackground(this, text_day_7, true);
            }

        }

        closeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSensorNotificationDialog.dismiss();
            }
        });

        button_save_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                addNotification(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getMultiSensorNotificationId() : "");
            }

        });

        tempSensorNotificationDialog.show();

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addHumity(final TextView txt_notification_alert, EditText minValue, final EditText maxValue, boolean isEdit, String temp_sensor_notification_id) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(minValue.getText().toString().trim())) {
            minValue.requestFocus();
            minValue.setError("Enter Min. Value");
            return;
        }
        if (TextUtils.isEmpty(maxValue.getText().toString().trim())) {
            maxValue.requestFocus();
            maxValue.setError("Enter Max. Value");
            return;
        }

       /* int min = Integer.parseInt(minValue.getText().toString().trim());
        int max = Integer.parseInt(maxValue.getText().toString().trim());

        if(max < min){
            maxValue.setError("Max value Less than Min Value");
            return;
        }*/

        getRepeatString();

        if (TextUtils.isEmpty(repeatDayString)) {
            //Toast.makeText(this, "Select Repeat Days", Toast.LENGTH_SHORT).show();
            showToastCenter("Select Repeat Days");
            return;
        }


        String webUrl = "";
        if (!isEdit) {
            webUrl = ChatApplication.url + Constants.addMultiSensorNotification;
        } else {
            webUrl = ChatApplication.url + Constants.updateMultiSensorNotification;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonNotification = new JSONObject();
        try {

            if (isEdit) {
                jsonNotification.put("multi_sensor_notification_id", temp_sensor_notification_id);
            }

            jsonNotification.put("multi_sensor_id", temp_sensor_id);
            jsonNotification.put("is_in_C", -1); //1=C 0=F
            jsonNotification.put("min_temp_value", "");
            jsonNotification.put("max_temp_value", "");
            jsonNotification.put("min_humidity_value", Integer.parseInt(minValue.getText().toString().trim()));
            jsonNotification.put("max_humidity_value", Integer.parseInt(maxValue.getText().toString().trim()));
            jsonNotification.put("days", repeatDayString);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("json : " + jsonNotification.toString());

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }

                        tempSensorNotificationDialog.dismiss();
                        repeatDayString = "";

                        if (isCFSelected == 0) {
                            isCFDone = true;
                        } else {
                            isCFDone = false;
                        }
                        getSensorDetails();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            maxValue.setFocusableInTouchMode(true);
                            maxValue.requestFocus();
                            maxValue.setError(message);
                            // showToastCenter(message);
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

    private void addNotification(final TextView txt_notification_alert, EditText minValue, final EditText maxValue, boolean isEdit, String temp_sensor_notification_id) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(minValue.getText().toString().trim())) {
            minValue.requestFocus();
            minValue.setError("Enter Min. Value");
            return;
        }
        if (TextUtils.isEmpty(maxValue.getText().toString().trim())) {
            maxValue.requestFocus();
            maxValue.setError("Enter Max. Value");
            return;
        }

       /* int min = Integer.parseInt(minValue.getText().toString().trim());
        int max = Integer.parseInt(maxValue.getText().toString().trim());

        if(max < min){
            maxValue.setError("Max value Less than Min Value");
            return;
        }*/

        getRepeatString();

        if (TextUtils.isEmpty(repeatDayString)) {
            //Toast.makeText(this, "Select Repeat Days", Toast.LENGTH_SHORT).show();
            showToastCenter("Select Repeat Days");
            return;
        }

        String webUrl = "";
        if (!isEdit) {
            webUrl = ChatApplication.url + Constants.addMultiSensorNotification;
        } else {
            webUrl = ChatApplication.url + Constants.updateMultiSensorNotification;
        }

        JSONObject jsonNotification = new JSONObject();
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        try {

            if (isEdit) {
                jsonNotification.put("multi_sensor_notification_id", temp_sensor_notification_id);
            }

            jsonNotification.put("multi_sensor_id", temp_sensor_id);
            jsonNotification.put("is_in_C", isCFSelected); //1=C 0=F
            jsonNotification.put("min_temp_value", Integer.parseInt(minValue.getText().toString().trim()));
            jsonNotification.put("max_temp_value", Integer.parseInt(maxValue.getText().toString().trim()));
            jsonNotification.put("min_humidity_value", "");
            jsonNotification.put("max_humidity_value", "");
            jsonNotification.put("days", repeatDayString);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("json : " + jsonNotification.toString());

        //  ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }

                        tempSensorNotificationDialog.dismiss();
                        repeatDayString = "";

                        if (isCFSelected == 0) {
                            isCFDone = true;
                        } else {
                            isCFDone = false;
                        }
                        getSensorDetails();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            maxValue.setFocusableInTouchMode(true);
                            maxValue.requestFocus();
                            maxValue.setError(message);
                            // showToastCenter(message);
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

    public boolean isCFDone = false;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ((id == R.id.text_day_1) || (id == R.id.text_day_2) || (id == R.id.text_day_3) || (id == R.id.text_day_4)
                || (id == R.id.text_day_5) || (id == R.id.text_day_6) || (id == R.id.text_day_7)) {
            Common.setOnOffBackground(this, (TextView) tempSensorNotificationDialog.findViewById(id));
        } else if (id == R.id.txt_c_button) {
            isCFSelected = 1;
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            notifyAdapter(true);

        } else if (id == R.id.txt_f_button) {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
            isCFSelected = 0;
            notifyAdapter(false);

        } else if (id == R.id.btn_delete) {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Delete Temp Sensor", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensor();
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        } else if (id == R.id.view_rel_badge) {
            view_rel_badge.setClickable(false);
//            unreadApiCall(true);
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
        } else if (id == R.id.linearAlertDown_temp) {
            if (flagAlertTemp) {
                flagAlertTemp = false;
                toggleAlert_temp.setChecked(flagAlertTemp);
                linearAlertExpand_temp.setVisibility(View.GONE);
            } else {
                flagAlertTemp = true;
                toggleAlert_temp.setChecked(flagAlertTemp);
                linearAlertExpand_temp.setVisibility(View.VISIBLE);
            }
        }else if(v==iv_icon_edit || v==sensorName){
            if(flagEditSensor){
                viewEditSensor.setVisibility(View.GONE);
                flagEditSensor=false;
                sensorName.setCursorVisible(false);
                sensorName.setFocusableInTouchMode(false);
                sensorName.setClickable(false);
                ChatApplication.closeKeyboard(this);
//                scrollviewMulti.fullScroll(ScrollView.FOCUS_UP);
            }else {
                viewEditSensor.setVisibility(View.VISIBLE);

                flagEditSensor=true;
                sensorName.setCursorVisible(true);
                sensorName.setFocusable(true);
                sensorName.setFocusableInTouchMode(true);
                sensorName.setClickable(true);
                sensorName.requestFocus();
                ChatApplication.keyBoardShow(this,sensorName);
            }
        }else if(v==toggleAlertSensor || v==linearMultisensor){
            if(flagSensorNoti){
                flagSensorNoti=false;
                toggleAlertSensor.setChecked(flagSensorNoti);
                recyclerAlert.setVisibility(View.GONE);
            }else {
                flagSensorNoti=true;
                toggleAlertSensor.setChecked(flagSensorNoti);
                recyclerAlert.setVisibility(View.VISIBLE);
            }
        }else if(v==imgLog){
            checkIntent(true);
        }
    }

    /**
     * delete individual temp sensor
     */
    private void deleteTempSensor() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        String webUrl = ChatApplication.url + Constants.deleteMultiSensor;
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonNotification = new JSONObject();
        try {

            // {
            //	"multi_sensor_id"	: "1559630413440_xZzVL01iz",
            //	"user_id":"1559035111028_VojOpeeBF",
            //	"phone_id":"1234567",
            //	"phone_type":"Android"
            //	 }
            jsonNotification.put("multi_sensor_id", temp_sensor_id);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());
                  ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
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
                //  ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }


    /**
     * notify adapter if user selected C or F toggle button
     *
     * @param isCF
     */
    private void notifyAdapter(boolean isCF) {

        if (sensorResModel != null) {

            tempLists = sensorResModel.getDate().getMultiSensorList();

            if (tempLists.length > 0) {

                String tmpC = "-- ", tmpF = "-- ";
                if (TextUtils.isEmpty(tempLists[0].getTempInC()) || tempLists[0].getTempInC().equalsIgnoreCase("null"))
                    tmpC = "-- ";
                else
                    tmpC = tempLists[0].getTempInC();

                if (TextUtils.isEmpty(tempLists[0].getTempInF()) || tempLists[0].getTempInF().equalsIgnoreCase("null"))
                    tmpF = "-- ";
                else
                    tmpF = tempLists[0].getTempInF();


                if (isCF) {
                    setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                    tempCFValue.setText(tmpC +" ");

                } else {
                    setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                    tempCFValue.setText(tmpF+" ");
                }


            }

            if (notificationList != null && tempSensorInfoAdapter!=null) {

                for (int i = 0; i < notificationList.size(); i++) {

                    SensorResModel.DATA.MultiSensorList.TempNotificationList notificationListModel=notificationList.get(i);

                    if (isCF) {
                        notificationListModel.setCFActive(true);
                    } else {
                        notificationListModel.setCFActive(false);
                    }

                    tempSensorInfoAdapter.notifyItemChanged(i, notificationListModel);
                }
                tempSensorInfoAdapter.notifyDataSetChanged();
            }
//
            final int speedScroll = 150;
            if (isScrollToEndPosition) {
               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(notificationList!=null){
                            sensor_list.smoothScrollToPosition(lastVisibleItem);//TODO
                        }
                    }
                },speedScroll);*/
                if (notificationList != null) {
                    //  sensor_list.smoothScrollToPosition(lastVisibleItem);
                }
            }
        }
    }

    /**
     * @param notification
     * @param position
     * @param isEdit
     */
    @Override
    public void onEditOpetion(final SensorResModel.DATA.MultiSensorList.TempNotificationList notification, int position, boolean isEdit) {

        if (isEdit) {
            tempSensorNotification(notification, true);
        } else {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensorNotification(notification,null);
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        }
    }


    /**
     * check if temp sensro notification status update or not
     *
     * @param notification
     * @param swithcCompact
     * @param position
     * @param isActive
     */
    @Override
    public void onSwitchChanged(SensorResModel.DATA.MultiSensorList.TempNotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive) {

        showAlertDialog(notification.getMultiSensorNotificationId(), swithcCompact, isActive, true, position);

    }

    /**
     * Delete temp sensor notification
     *
     * @param notificationList
     * @param notification
     */
    private void deleteTempSensorNotification(SensorResModel.DATA.MultiSensorList.TempNotificationList notificationList, SensorResModel.DATA.MultiSensorList.HumidityNotificationList notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        //{
        //	"multi_sensor_notification_id": "1559639810756_LKcGVoOLU",
        //	"multi_sensor_id": "1559632953885_qOnDEDp4r",
        //	"user_id":"1559035111028_VojOpeeBF",
        //	"phone_id":"1234567",
        //	"phone_type":"Android"
        //	}

        String webUrl = ChatApplication.url + Constants.deleteMultiSensorNotification;
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        JSONObject jsonNotification = new JSONObject();
        try {
            if(notificationList!=null){
                jsonNotification.put("multi_sensor_notification_id", notificationList.getMultiSensorNotificationId());

            }else {
                jsonNotification.put("multi_sensor_notification_id", notification.getMultiSensorNotificationId());
            }
            jsonNotification.put("multi_sensor_id", temp_sensor_id);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());
                 ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (isCFSelected == 0) {
                            isCFDone = true;
                        } else {
                            isCFDone = false;
                        }
                        getSensorDetails();
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


    String repeatDayString = "";

    public void getRepeatString() {

        repeatDayString = "";

        if (Boolean.parseBoolean(text_day_1.getTag().toString())) {
            repeatDayString = repeatDayString + "0";
        }
        if (Boolean.parseBoolean(text_day_2.getTag().toString())) {
            repeatDayString = repeatDayString + ",1";
        }
        if (Boolean.parseBoolean(text_day_3.getTag().toString())) {
            repeatDayString = repeatDayString + ",2";
        }
        if (Boolean.parseBoolean(text_day_4.getTag().toString())) {
            repeatDayString = repeatDayString + ",3";
        }
        if (Boolean.parseBoolean(text_day_5.getTag().toString())) {
            repeatDayString = repeatDayString + ",4";
        }
        if (Boolean.parseBoolean(text_day_6.getTag().toString())) {
            repeatDayString = repeatDayString + ",5";
        }
        if (Boolean.parseBoolean(text_day_7.getTag().toString())) {
            repeatDayString = repeatDayString + ",6";
        }
        //replace first comma.
        if (repeatDayString.startsWith(",")) {
            repeatDayString = repeatDayString.replaceFirst(",", "");
        }


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * @param txtC
     * @param txtF
     * @param backTxtC
     * @param backTxtF
     * @param txtCColor
     * @param txtFColor
     */
    private void setTxtBackColor(TextView txtC, TextView txtF, int backTxtC, int backTxtF, int txtCColor, int txtFColor) {

        txtF.setBackgroundResource(backTxtF);
        txtC.setBackgroundResource(backTxtC);
        txtF.setTextColor(txtFColor);
        txtC.setTextColor(txtCColor);
    }

    /**
     * getSensorDetails
     */
    SensorResModel sensorResModel=new SensorResModel();

    private void getSensorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.getMultiSensorInfo ;

        JSONObject object = new JSONObject();
        try {
            object.put("multi_sensor_id",temp_sensor_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                startSocketConnection();
                ChatApplication.logDisplay("result is "+result);
                ActivityHelper.dismissProgressDialog();
                sensorResModel = Common.jsonToPojo(result.toString(), SensorResModel.class);
                if (sensorResModel.getCode() == 200) {
                    txtEmpty.setVisibility(View.GONE);
                    fillData(sensorResModel, true);

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setTempUnreadCount(String count) {

//        if (!TextUtils.isEmpty(count) && Integer.parseInt(count) > 0) {
//            sensorLog.setVisibility(View.VISIBLE);
//            sensorLog.setText(count); //getIsUnread
//
//            if (Integer.parseInt(count) > 99) {
//                sensorLog.setText("99+");
//                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(), 20);
//                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(), 20);
//            } else {
//                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(), 20);
//                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(), 20);
//            }
//        } else {
//            sensorLog.setVisibility(View.GONE);
//        }
    }

    /**
     * fill Date from model class
     *
     * @param sensorResModel
     * @param isCF
     */

    public int init = 1;

    private void fillData(SensorResModel sensorResModel, boolean isCF) {

        SensorResModel.DATA.MultiSensorList[] tempLists = sensorResModel.getDate().getMultiSensorList();

        if (tempLists.length > 0) {

            sensorName.setText(tempLists[0].getMultiSensorName());
            sensorName.setSelection(sensorName.getText().toString().length());
            mSensorName = tempLists[0].getMultiSensorName();

//            notiSwitchOnOff.setChecked(Integer.parseInt(String.valueOf(tempLists[0].getIsPushEnable())) == 1);
//            notiSwitchOnOff.setVisibility(View.VISIBLE);

//            int perc = 0;
//            if (!TextUtils.isEmpty(tempLists[0].getTempSensorPerc())) {
//                perc = (int) Double.parseDouble((tempLists[0].getTempSensorPerc()));
//            }

//            batteryPercentage.setText(perc + "%");
//            imgBattery.setImageResource(Common.getBatteryIcon(tempLists[0].getTempSensorPerc()));

            String tmpC = "-- ", tmpF = "-- ";
            if (TextUtils.isEmpty(tempLists[0].getTempInC()) || tempLists[0].getTempInC().equalsIgnoreCase("null"))
                tmpC = "-- ";
            else
                tmpC = tempLists[0].getTempInC();

            if (TextUtils.isEmpty(tempLists[0].getTempInF()) || tempLists[0].getTempInF().equalsIgnoreCase("null"))
                tmpF = "-- ";
            else
                tmpF = tempLists[0].getTempInF();


            if (tempLists[0].getIsInC()==1 && !isCFDone) {
                isCFSelected = 1;
                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                tempCFValue.setText(tmpC +" ");
            } else {
                isCFSelected = 0;
                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                tempCFValue.setText(tmpF +" ");
                //isCF = false;
            }

            if(!TextUtils.isEmpty(sensorResModel.getDate().getMultiSensorList()[0].getHumidity()) &&
                    sensorResModel.getDate().getMultiSensorList()[0].getHumidity()!=null){

                txtHumity.setText(sensorResModel.getDate().getMultiSensorList()[0].getHumidity()+" %");
            }
            //humibity

            if (sensorResModel.getDate().getMultiSensorList()[0].getHumidityNotificationList().size() > 0) {
                txtEmpty_temp.setVisibility(View.GONE);
                sensor_list_temp.setVisibility(View.VISIBLE);
                txtTempAlertCount_temp.setVisibility(View.VISIBLE);
                txtTempAlertCount_temp.setText("("+sensorResModel.getDate().getMultiSensorList()[0].getHumidityNotificationList().size() + " Added)");

                notificationHumidityList = sensorResModel.getDate().getMultiSensorList()[0].getHumidityNotificationList();
                if (isCFDone) {
                    humiditySensorAdapter = new HumiditySensorAdapter(notificationHumidityList, false, MultiSensorActivity.this);
                } else {

                    if (isCF) {
                        if (isCFSelected == 0) {
                            humiditySensorAdapter = new HumiditySensorAdapter(notificationHumidityList, false, MultiSensorActivity.this);
                        } else {
                            humiditySensorAdapter = new HumiditySensorAdapter(notificationHumidityList, isCF, MultiSensorActivity.this);
                        }
                    }
                }
                for (int i = 0; i < notificationHumidityList.size(); i++) {
                    SensorResModel.DATA.MultiSensorList.HumidityNotificationList notificationListModel = notificationHumidityList.get(i);

                    if (isCFDone) {
                        notificationListModel.setCFActive(false);
                    } else {
                        if (isCF) {
                            if (isCFSelected == 0) {
                                notificationListModel.setCFActive(false);
                            } else {
                                notificationListModel.setCFActive(true);
                            }
                        }
                    }

                    humiditySensorAdapter.notifyItemChanged(i, notificationListModel);
                }

                sensor_list_temp.setAdapter(humiditySensorAdapter);
                humiditySensorAdapter.notifyDataSetChanged();


                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) sensor_list.getLayoutManager();

                sensor_list_temp.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        mScrollState = newState;

                        if (newState == SCROLL_STATE_IDLE) {

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            isScrollToEndPosition = (notificationList.size() - 1) == lastVisibleItem && mScrollState == SCROLL_STATE_IDLE;
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                if (isScrollToEndPosition) {
                    if (notificationHumidityList != null) { //auto alert list view scroll if user scroll end of list
                        sensor_list_temp.smoothScrollToPosition(lastVisibleItem);
                    }
                }


            } else {
                txtTempAlertCount_temp.setVisibility(View.INVISIBLE);
                txtTempCount_temp.setText("Humidity Alert");
                sensor_list_temp.setVisibility(View.GONE);
                txtEmpty_temp.setVisibility(View.GONE);
                // txt_empty_notification.setVisibility(View.VISIBLE);
            }

            //for temp
            if (sensorResModel.getDate().getMultiSensorList()[0].getTempNotificationList().size() > 0) {
                txtEmpty.setVisibility(View.GONE);
                sensor_list.setVisibility(View.VISIBLE);
                txtTempAlertCount.setVisibility(View.VISIBLE);
                txtTempAlertCount.setText("("+sensorResModel.getDate().getMultiSensorList()[0].getTempNotificationList().size() + " Added)");
                //    txt_empty_notification.setVisibility(View.GONE);

                notificationList = sensorResModel.getDate().getMultiSensorList()[0].getTempNotificationList();
                if (isCFDone) {
                    tempSensorInfoAdapter = new TempMultiSensorAdapter(notificationList, false, MultiSensorActivity.this);
                } else {

                    if (isCF) {
                        if (isCFSelected == 0) {
                            tempSensorInfoAdapter = new TempMultiSensorAdapter(notificationList, false, MultiSensorActivity.this);
                        } else {
                            tempSensorInfoAdapter = new TempMultiSensorAdapter(notificationList, isCF, MultiSensorActivity.this);
                        }
                    }
                }
                for (int i = 0; i < notificationList.size(); i++) {
                    SensorResModel.DATA.MultiSensorList.TempNotificationList notificationListModel = notificationList.get(i);

                    if (isCFDone) {
                        notificationListModel.setCFActive(false);
                    } else {
                        if (isCF) {
                            if (isCFSelected == 0) {
                                notificationListModel.setCFActive(false);
                            } else {
                                notificationListModel.setCFActive(true);
                            }
                        }
                    }

                    tempSensorInfoAdapter.notifyItemChanged(i, notificationListModel);
                }

                sensor_list.setAdapter(tempSensorInfoAdapter);
                tempSensorInfoAdapter.notifyDataSetChanged();

//                if (notificationList.length > 0) {
//                    txtTempCount.setText("Alert " + notificationList.length + " Added");
//                } else {
//                    txtTempCount.setText("Alert 0 Added");
//                }


                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) sensor_list.getLayoutManager();

                sensor_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        mScrollState = newState;

                        if (newState == SCROLL_STATE_IDLE) {

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            isScrollToEndPosition = (notificationList.size() - 1) == lastVisibleItem && mScrollState == SCROLL_STATE_IDLE;
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                /*final int speedScroll = 150;
                if(isScrollToEndPosition){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(notificationList!=null){
                                sensor_list.smoothScrollToPosition(lastVisibleItem);
                            }
                        }
                    },speedScroll);
                }*/
                if (isScrollToEndPosition) {
                    if (notificationList != null) { //auto alert list view scroll if user scroll end of list
                        sensor_list.smoothScrollToPosition(lastVisibleItem);
                    }
                }


            } else {
                txtTempAlertCount.setVisibility(View.INVISIBLE);
                txtTempCount.setText("Temperature Alert");
                sensor_list.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.GONE);
                // txt_empty_notification.setVisibility(View.VISIBLE);
            }

            if(TextUtils.isEmpty(""+sensorResModel.getDate().getMultiSensorList()[0].getGas_value()) || TextUtils.isEmpty(""+sensorResModel.getDate().getMultiSensorList()[0].getGas_threshold_value())){
                txtGasSensor.setText("--");
            }else {
                if (sensorResModel.getDate().getMultiSensorList()[0].getGas_value() >= sensorResModel.getDate().getMultiSensorList()[0].getGas_threshold_value()) {
                    txtGasSensor.setText("High");
                    txtGasSensor.setTextColor(getResources().getColor(R.color.automation_red));
                } else if (sensorResModel.getDate().getMultiSensorList()[0].getGas_value() <= sensorResModel.getDate().getMultiSensorList()[0].getGas_threshold_value()) {
                    txtGasSensor.setText("Normal");
                    txtGasSensor.setTextColor(getResources().getColor(R.color.username5));
                }
            }
//            if (sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs() != null) {


//                if (sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs().size() > 0) {
//
//                    int countTemp=sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs().size();
//                    if(countTemp>99){
//                        txtAlertCount.setText("99+");
//                    }else {
//                        txtAlertCount.setText("" + countTemp);
//                    }
//
//                } else {
//                    txtAlertCount.setText("0");
//                }

//                if (sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs().size() > 0) {
//                    recyclerAlert.setVisibility(View.VISIBLE);
                    txtAlertCount.setVisibility(View.VISIBLE);
//                    int countTemp=sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs().size();
//                    if(countTemp>99){
//                        txtAlertCount.setText("99+");
//                    }else {
//                        txtAlertCount.setText("" + countTemp);
//                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultiSensorActivity.this);
                    recyclerAlert.setLayoutManager(linearLayoutManager);
                    TempMultiSensorAlertAdapter doorAlertAdapter = new TempMultiSensorAlertAdapter(MultiSensorActivity.this, sensorResModel.getDate().getMultiSensorList()[0].getNotificationSettingList(),sensorResModel.getDate().getMultiSensorList()[0].getMultiSensorId());
                    recyclerAlert.setAdapter(doorAlertAdapter);
                    recyclerAlert.setHasFixedSize(true);
//                } else {
//                    txtAlertCount.setVisibility(View.GONE);
//                    recyclerAlert.setVisibility(View.GONE);
//                    txt_empty_notification.setVisibility(View.GONE);
//                }

//            } else {
//                txtAlertCount.setVisibility(View.GONE);
//                recyclerAlert.setVisibility(View.GONE);
//                txt_empty_notification.setVisibility(View.GONE);
//            }
        } else {
            txtTempCount.setText("Temperature Alert");
        }
    }

    @Override
    public void onEditHumityOpetion(final SensorResModel.DATA.MultiSensorList.HumidityNotificationList notification, int position, boolean isEdit) {

        if (isEdit) {
            humitySensorNotification(notification, true);
        } else {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensorNotification(null,notification);
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        }
    }

    @Override
    public void onSwitchHumityChanged(SensorResModel.DATA.MultiSensorList.HumidityNotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive) {

    }


    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading = false;
    private int visibleThreshold = 0;
    private boolean isScrollToEndPosition = false;


    public void unreadApiCall(final boolean b) {
//
//        if(recyclerAlert.getVisibility()==View.VISIBLE){
//
//        }else {
//            checkIntent(b);
//            return;
//        }
//
//        if(sensorResModel.getDate()==null){
//            return;
//        }
//        if(sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs()==null){
//            return;
//        }
//        if(sensorResModel.getDate().getMultiSensorList()[0].getUnreadLogs().size()==0){
//            return;
//        }

        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

        JSONObject jsonObject = new JSONObject();
        try

        {

            JSONArray jsonArray = new JSONArray();

//            for (RoomVO roomVO : unReadLogs) {
//
            JSONObject object = new JSONObject();
//
            object.put("sensor_type","multisensor");
//
//                if (roomVO.getRoomName().equalsIgnoreCase("All")) {
            // object.put("module_id", ""+sensorResModel.getDate().getTempLists()[0].getTempSensorMoudleId());
            object.put("module_id", ""+sensorResModel.getDate().getMultiSensorList()[0].getMultiSensorModuleId());
            object.put("room_id", ""+sensorResModel.getDate().getMultiSensorList()[0].getRoomId());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
//                } else {
//                    object.put("module_id", roomVO.getModule_id());
//                    object.put("room_id", roomVO.getRoomId());
//                }
//
            jsonArray.put(object);
//            }

            jsonObject.put("update_logs", jsonArray);

        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }

        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new

                ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        checkIntent(b);

                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        checkIntent(b);
                    }
                }).

                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void checkIntent(boolean b) {
        if (b) {
            Intent intent = new Intent(MultiSensorActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", ""+sensorResModel.getDate().getMultiSensorList()[0].getRoomId());
            intent.putExtra("Mood_Id", ""+sensorResModel.getDate().getMultiSensorList()[0].getMultiSensorModuleId());
            intent.putExtra("activity_type", "multisensor");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity","multisensor");
            intent.putExtra("isRoomName",""+sensorName.getText().toString());
            startActivity(intent);
        } else {
            MultiSensorActivity.this.finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
//        unreadApiCall(false);
        super.onBackPressed();
    }
}