package com.spike.bot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TempAlertAdapter;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.model.SensorResModel;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static com.spike.bot.core.Common.showToast;
import static com.spike.bot.core.Common.showToastCenter;


/**
 * Created by Sagar on 7/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class TempSensorInfoActivity extends AppCompatActivity implements View.OnClickListener, TempSensorInfoAdapter.OnNotificationContextMenu,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView sensor_list, recyclerAlert;
    private TempSensorInfoAdapter tempSensorInfoAdapter;

    private EditText sensorName;
    private TextView sensorLog, txtAlert, txtAlertCount, txtEmpty;
    private ImageView imgBattery, view_rel_badge;
    private TextView batteryPercentage, txt_empty_notification, txtTempCount;
    private TextView tempCFValue, txtTempAlertCount;
    private TextView txtCButton, txtFButton;
    private SwitchCompat notiSwitchOnOff;
    private TextView txtAddButton;
    private Button btn_delete;
    private ToggleButton toggleAlert;
    private LinearLayout linearAlertExpand, linearAlertDown;
    private boolean flagAlert = false;

    private String temp_sensor_id, temp_room_name, temp_room_id, temp_module_id;
    private int isCFSelected = -1;
    private String mSensorName;
    private String temp_unread_count = "";
    private Socket mSocket;

    SensorResModel.DATA.TempList[] tempLists;
    SensorResModel.DATA.TempList.NotificationList[] notificationList;

    private TextView text_day_1, text_day_2, text_day_3, text_day_4, text_day_5, text_day_6, text_day_7;
    Dialog tempSensorNotificationDialog;

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading = false;
    private int visibleThreshold = 0;
    private boolean isScrollToEndPosition = false;

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
        setContentView(R.layout.activity_temp_sensor_info);

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
            sensorLog.setVisibility(View.GONE);
        }
        bindView();

        connectivityReceiver = new ConnectivityReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) connectivityReceiver, intentFilter);
        view_rel_badge.setClickable(true);
        getSensorDetails();

    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        mSocket.on("unReadCount", unReadCount);
    }


    private Emitter.Listener unReadCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            TempSensorInfoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String sensor_type = object.getString("sensor_type");
                            String sensor_unread = object.getString("sensor_unread");
                            String module_id = object.getString("module_id");
                            String room_id = object.getString("room_id");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
        if (mSocket != null) {
            mSocket.off("unReadCount", unReadCount);
        }
    }

    private void bindView() {

        sensor_list = (RecyclerView) findViewById(R.id.sensor_list);
        sensor_list.setHasFixedSize(true);
        sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        sensor_list.setVerticalScrollBarEnabled(true);

        view_rel_badge = (ImageView) findViewById(R.id.view_rel_badge);
        recyclerAlert = (RecyclerView) findViewById(R.id.recyclerAlert);

        sensorName = (EditText) findViewById(R.id.sensor_name);
        sensorLog = (TextView) findViewById(R.id.iv_icon_badge);
        imgBattery = (ImageView) findViewById(R.id.img_battery);
        batteryPercentage = (TextView) findViewById(R.id.txt_bettery_per);
        tempCFValue = (TextView) findViewById(R.id.txt_tmp_incf);
        linearAlertDown = (LinearLayout) findViewById(R.id.linearAlertDown);
        linearAlertExpand = (LinearLayout) findViewById(R.id.linearAlertExpand);
        toggleAlert = (ToggleButton) findViewById(R.id.toggleAlert);
        txtAlertCount = (TextView) findViewById(R.id.txtAlertCount);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        txtTempAlertCount = (TextView) findViewById(R.id.txtTempAlertCount);

        txtTempCount = (TextView) findViewById(R.id.txtTempCount);
        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification);

        notiSwitchOnOff = (SwitchCompat) findViewById(R.id.switch_noti_onoff);
        txtAddButton = (TextView) findViewById(R.id.btnAdd);

        txtCButton = (TextView) findViewById(R.id.txt_c_button);
        txtFButton = (TextView) findViewById(R.id.txt_f_button);

        btn_delete = (Button) findViewById(R.id.btn_delete);

        sensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        sensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        txtCButton.setOnClickListener(this);
        txtFButton.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);

        view_rel_badge.setOnClickListener(this);

        txtAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSensorNotification(null, false);
            }
        });


        notiSwitchOnOff.setOnTouchListener(new OnSwipeTouchListener(TempSensorInfoActivity.this) {
            @Override
            public void onClick() {
                super.onClick();
                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                notiSwitchOnOff.setChecked(notiSwitchOnOff.isChecked());
                showAlertDialog("", notiSwitchOnOff, !notiSwitchOnOff.isChecked(), false, -1);
            }

        });

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

        notiSwitchOnOff.setVisibility(View.GONE);

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

        String webUrl = "";
        if (!isNotification) {
            webUrl = ChatApplication.url + Constants.CHANGE_TEMP_SENSOR_STATUS;
        } else {
            webUrl = ChatApplication.url + Constants.CHANGE_TEMP_SENSOR_NOTIFICATION_STATUS;
        }

        JSONObject jsonNotification = new JSONObject();

        try {
            if (isNotification) {
                jsonNotification.put("temp_sensor_notification_id", tempSensorNotificationId);
                jsonNotification.put("is_active", isActive ? 1 : 0);
            } else {
                jsonNotification.put("is_push_enable", isActive ? 1 : 0);
            }
            jsonNotification.put("temp_sensor_id", temp_sensor_id);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

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
                            sensorResModel.getDate().getTempLists()[0].getNotificationLists()[position].setmIsActive(is_active);
                        }

                        boolean isCF = false;
                        if (isCFSelected == 1) {
                            isCF = true;
                        }

                        notificationList = sensorResModel.getDate().getTempLists()[0].getNotificationLists();
                        tempSensorInfoAdapter.notifyItemChanged(position, sensorResModel.getDate().getTempLists()[0].getNotificationLists()[position]);

                        tempSensorInfoAdapter.notifyDataSetChanged();

                        //adapter auto scroll
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
            updateTempSensor();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTempSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(sensorName.getText().toString().trim())) {
            sensorName.setError("Enter Sensor Name");
            return;
        }


        String webUrl = ChatApplication.url + Constants.UPDATE_TEMP_SENSOR;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("temp_sensor_id", temp_sensor_id);
            jsonNotification.put("temp_sensor_name", sensorName.getText().toString().trim());
            jsonNotification.put("room_id", temp_room_id);
            jsonNotification.put("is_in_C", isCFSelected);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
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
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * @param notificationList
     * @param isEdit
     */
    private void tempSensorNotification(final SensorResModel.DATA.TempList.NotificationList notificationList, final boolean isEdit) {

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
                addNotification(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getTempSensorNotificationId() : "");
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

    private void addNotification(final TextView txt_notification_alert, EditText minValue, final EditText maxValue, boolean isEdit, String temp_sensor_notification_id) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
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


        getRepeatString();

        if (TextUtils.isEmpty(repeatDayString)) {
            showToastCenter("Select Repeat Days");
            return;
        }

        String webUrl = "";
        if (!isEdit) {
            webUrl = ChatApplication.url + Constants.ADD_TEMP_SENSOR_NOTIFICATION;
        } else {
            webUrl = ChatApplication.url + Constants.UPDATE_TEMP_SENSOR_NOTIFICATION;
        }

        JSONObject jsonNotification = new JSONObject();
        try {
            if (isEdit) {
                jsonNotification.put("temp_sensor_notification_id", temp_sensor_notification_id);
            }

            jsonNotification.put("temp_sensor_id", temp_sensor_id);
            jsonNotification.put("is_in_C", isCFSelected); //1=C 0=F
            jsonNotification.put("min_temp_value", minValue.getText().toString().trim());
            jsonNotification.put("max_temp_value", maxValue.getText().toString().trim());
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
     * delete individual temp sensor
     */
    private void deleteTempSensor() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        String webUrl = ChatApplication.url + Constants.DELETE_MODULE;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("module_id", temp_module_id);
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
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

            tempLists = sensorResModel.getDate().getTempLists();

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
                    tempCFValue.setText(tmpC + " " + Common.getC() + " ");

                } else {
                    setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                    tempCFValue.setText(tmpF + " " + Common.getF() + " ");
                }


            }

            if (notificationList != null && tempSensorInfoAdapter != null) {

                for (int i = 0; i < notificationList.length; i++) {
                    SensorResModel.DATA.TempList.NotificationList notificationListModel = notificationList[i];

                    if (isCF) {
                        notificationListModel.setCFActive(true);
                    } else {
                        notificationListModel.setCFActive(false);
                    }

                    tempSensorInfoAdapter.notifyItemChanged(i, notificationListModel);
                }
                tempSensorInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * @param notification
     * @param position
     * @param isEdit
     */
    @Override
    public void onEditOpetion(final SensorResModel.DATA.TempList.NotificationList notification, int position, boolean isEdit) {

        if (isEdit) {
            tempSensorNotification(notification, true);

        } else {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensorNotification(notification);
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
    public void onSwitchChanged(SensorResModel.DATA.TempList.NotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive) {
        showAlertDialog(notification.getTempSensorNotificationId(), swithcCompact, isActive, true, position);

    }

    /**
     * Delete temp sensor notification
     *
     * @param notificationList
     */
    private void deleteTempSensorNotification(SensorResModel.DATA.TempList.NotificationList notificationList) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        String webUrl = ChatApplication.url + Constants.DELETE_TEMP_SENSOR_NOTIFICATION;
        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("temp_sensor_notification_id", notificationList.getTempSensorNotificationId());
            jsonNotification.put("temp_sensor_id", temp_sensor_id);
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
                // ActivityHelper.dismissProgressDialog();
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
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
    SensorResModel sensorResModel = new SensorResModel();

    private void getSensorDetails() {

        String url = ChatApplication.url + Constants.GET_TEMP_SENSOR_INFO;

        JSONObject object = new JSONObject();
        try {
            //{
            //  "temp_sensor_id"	: "",
            //  "user_id":"",
            //  "phone_id":"",
            //  "phone_type":""
            // }

            object.put("temp_sensor_id", temp_sensor_id);
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

                startSocketConnection();

                ActivityHelper.dismissProgressDialog();
                sensorResModel = Common.jsonToPojo(result.toString(), SensorResModel.class);
                if (sensorResModel.getCode() == 200) {
                    txtEmpty.setVisibility(View.GONE);
                    txt_empty_notification.setVisibility(View.GONE);
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

        if (!TextUtils.isEmpty(count) && Integer.parseInt(count) > 0) {
            sensorLog.setVisibility(View.VISIBLE);
            sensorLog.setText(count); //getIsUnread

            if (Integer.parseInt(count) > 99) {
                sensorLog.setText("99+");
                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(), 20);
                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(), 20);
            } else {
                sensorLog.getLayoutParams().width = Common.dpToPx(getApplicationContext(), 20);
                sensorLog.getLayoutParams().height = Common.dpToPx(getApplicationContext(), 20);
            }
        } else {
            sensorLog.setVisibility(View.GONE);
        }
    }

    /**
     * fill Date from model class
     *
     * @param sensorResModel
     * @param isCF
     */

    public int init = 1;

    private void fillData(SensorResModel sensorResModel, boolean isCF) {

        SensorResModel.DATA.TempList[] tempLists = sensorResModel.getDate().getTempLists();

        if (tempLists.length > 0) {

            sensorName.setText(tempLists[0].getTempSensorName());
            sensorName.setSelection(sensorName.getText().toString().length());
            mSensorName = tempLists[0].getTempSensorName();

            notiSwitchOnOff.setChecked(Integer.parseInt(tempLists[0].getIsPushEnable()) == 1);
            notiSwitchOnOff.setVisibility(View.VISIBLE);

            int perc = 0;
            if (!TextUtils.isEmpty(tempLists[0].getTempSensorPerc())) {
                perc = (int) Double.parseDouble((tempLists[0].getTempSensorPerc()));
            }

            batteryPercentage.setText(perc + "%");
            imgBattery.setImageResource(Common.getBatteryIcon(tempLists[0].getTempSensorPerc()));

            String tmpC = "-- ", tmpF = "-- ";
            if (TextUtils.isEmpty(tempLists[0].getTempInC()) || tempLists[0].getTempInC().equalsIgnoreCase("null"))
                tmpC = "-- ";
            else
                tmpC = tempLists[0].getTempInC();

            if (TextUtils.isEmpty(tempLists[0].getTempInF()) || tempLists[0].getTempInF().equalsIgnoreCase("null"))
                tmpF = "-- ";
            else
                tmpF = tempLists[0].getTempInF();


            if (tempLists[0].getIsInC().equalsIgnoreCase("1") && !isCFDone) {
                isCFSelected = 1;
                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                tempCFValue.setText(tmpC + " " + Common.getC() + " ");
            } else {
                isCFSelected = 0;
                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                tempCFValue.setText(tmpF + " " + Common.getF() + " ");
                //isCF = false;
            }

            if (sensorResModel.getDate().getTempLists()[0].getNotificationLists() != null && sensorResModel.getDate().getTempLists()[0].getNotificationLists().length > 0) {
                txtEmpty.setVisibility(View.GONE);
                sensor_list.setVisibility(View.VISIBLE);
                txtTempAlertCount.setVisibility(View.VISIBLE);
                txtTempAlertCount.setText("(" + sensorResModel.getDate().getTempLists()[0].getNotificationLists().length + " Added)");

                notificationList = sensorResModel.getDate().getTempLists()[0].getNotificationLists();
                if (isCFDone) {
                    tempSensorInfoAdapter = new TempSensorInfoAdapter(notificationList, false, TempSensorInfoActivity.this);
                } else {

                    if (isCF) {
                        if (isCFSelected == 0) {
                            tempSensorInfoAdapter = new TempSensorInfoAdapter(notificationList, false, TempSensorInfoActivity.this);
                        } else {
                            tempSensorInfoAdapter = new TempSensorInfoAdapter(notificationList, isCF, TempSensorInfoActivity.this);
                        }
                    }
                }
                for (int i = 0; i < notificationList.length; i++) {
                    SensorResModel.DATA.TempList.NotificationList notificationListModel = notificationList[i];

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

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) sensor_list.getLayoutManager();

                sensor_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        mScrollState = newState;

                        if (newState == SCROLL_STATE_IDLE) {

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            isScrollToEndPosition = (notificationList.length - 1) == lastVisibleItem && mScrollState == SCROLL_STATE_IDLE;
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                if (isScrollToEndPosition) {
                    if (notificationList != null) { //auto alert list view scroll if user scroll end of list
                        sensor_list.smoothScrollToPosition(lastVisibleItem);
                    }
                }


            } else {
                txtTempAlertCount.setVisibility(View.INVISIBLE);
                txtTempCount.setText("Alert");
                sensor_list.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);
            }


            if (sensorResModel.getDate().getTempLists()[0].getUnreadLogs() != null) {
                if (sensorResModel.getDate().getTempLists()[0].getUnreadLogs().size() > 0) {

                    int countTemp = sensorResModel.getDate().getTempLists()[0].getUnreadLogs().size();
                    if (countTemp > 99) {
                        txtAlertCount.setText("99+");
                    } else {
                        txtAlertCount.setText("" + countTemp);
                    }

                } else {
                    txtAlertCount.setText("0");
                }

                if (sensorResModel.getDate().getTempLists()[0].getUnreadLogs().size() > 0) {
                    recyclerAlert.setVisibility(View.VISIBLE);
                    txtAlertCount.setVisibility(View.VISIBLE);
                    txt_empty_notification.setVisibility(View.GONE);
                    int countTemp = sensorResModel.getDate().getTempLists()[0].getUnreadLogs().size();
                    if (countTemp > 99) {
                        txtAlertCount.setText("99+");
                    } else {
                        txtAlertCount.setText("" + countTemp);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TempSensorInfoActivity.this);
                    recyclerAlert.setLayoutManager(linearLayoutManager);
                    TempAlertAdapter doorAlertAdapter = new TempAlertAdapter(TempSensorInfoActivity.this, sensorResModel.getDate().getTempLists()[0].getUnreadLogs());
                    recyclerAlert.setAdapter(doorAlertAdapter);
                } else {
                    txtAlertCount.setVisibility(View.GONE);
                    recyclerAlert.setVisibility(View.GONE);
                    txt_empty_notification.setVisibility(View.VISIBLE);
                }

            } else {
                txtAlertCount.setVisibility(View.GONE);
                recyclerAlert.setVisibility(View.GONE);
                txt_empty_notification.setVisibility(View.VISIBLE);
            }
        } else {
            txtTempCount.setText("Alert");
        }
    }

    public void unreadApiCall(final boolean b) {

        if (recyclerAlert.getVisibility() == View.VISIBLE) {

        } else {
            checkIntent(b);
            return;
        }

        if (sensorResModel.getDate() == null) {
            return;
        }
        if (sensorResModel.getDate().getTempLists() == null) {
            return;
        }
        if (sensorResModel.getDate().getTempLists().length == 0) {
            return;
        }

        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("sensor_type", "temp");
            object.put("module_id", "" + sensorResModel.getDate().getTempLists()[0].getTempSensorMoudleId());
            object.put("room_id", "" + sensorResModel.getDate().getTempLists()[0].getRoomId());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonArray.put(object);
            jsonObject.put("update_logs", jsonArray);

        } catch (
                JSONException e) {
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
            Intent intent = new Intent(TempSensorInfoActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", "" + sensorResModel.getDate().getTempLists()[0].getRoomId());
            intent.putExtra("Mood_Id", "" + sensorResModel.getDate().getTempLists()[0].getTempSensorMoudleId());
            intent.putExtra("activity_type", "temp");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity", "tempSensor");
            intent.putExtra("isRoomName", "" + sensorName.getText().toString());
            startActivity(intent);
        } else {
            TempSensorInfoActivity.this.finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        unreadApiCall(false);
        super.onBackPressed();
    }
}
