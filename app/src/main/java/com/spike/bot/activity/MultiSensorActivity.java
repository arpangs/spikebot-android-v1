package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.OnHumitySensorContextMenu;
import com.spike.bot.listener.OnNotificationSensorContextMenu;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.receiver.ConnectivityReceiver;

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

    private RecyclerView sensor_list, recyclerAlert, sensor_list_temp;
    private TempMultiSensorAdapter tempSensorInfoAdapter;
    private HumiditySensorAdapter humiditySensorAdapter;

    public ScrollView scrollviewMulti;
    private EditText sensorName;
    public View viewEditSensor;
    private ImageView view_rel_badge, iv_icon_edit, imgLog;
    private TextView txtAlertCount, txtEmpty, txtEmpty_temp, txtHumity, txtTempCount, tempCFValue,
            txtTempAlertCount, txtCButton, txtFButton, txtTempAlertCount_temp, txtTempCount_temp, txtAddButton, txtAddButtonTemp;
    private Button btn_delete;
    private ToggleButton toggleAlert, toggleAlert_temp, toggleAlertSensor;
    private LinearLayout linearAlertExpand, linearAlertDown, linearAlertExpand_temp, linearAlertDown_temp, linearMultisensor;
    private boolean flagAlert = false, flagAlertTemp = false, flagEditSensor = false, flagSensorNoti = false;

    private String temp_room_name, temp_room_id, temp_module_id;
    private int isCFSelected = -1;
    private Socket mSocket;

    private RemoteDetailsRes mRemoteList;
    private RemoteDetailsRes.Data mRemoteCommandList;
    //    SensorResModel.DATA.TempList[] tempLists;
    ArrayList<RemoteDetailsRes.Data.Alert> notificationList = new ArrayList<>();
    ArrayList<RemoteDetailsRes.Data.Alert> notificationHumidityList = new ArrayList<>();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int lastVisibleItem, totalItemCount;
    private boolean isScrollToEndPosition = false;

    private TextView text_day_1, text_day_2, text_day_3, text_day_4, text_day_5, text_day_6, text_day_7;
    Dialog tempSensorNotificationDialog;
    public boolean isCFDone = false;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showToast("Disconnected, Please check your internet connection");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_sensor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        temp_room_name = getIntent().getStringExtra("temp_room_name");
        temp_room_id = getIntent().getStringExtra("temp_room_id");
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
        }
        bindView();
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

        if (mSocket != null) {
            mSocket.on("changeTempSensorValue", changeTempSensorValue);
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

                            // {"room_id":"1568356634757_Yy4oiLUbl","room_order":0,"temp_sensor_id":"1568367831844_0ygOKL1C-","is_in_C":1,"temp_celsius":30,"temp_fahrenheit":86,"humidity":36}

                            ChatApplication.logDisplay("temp is " + object);

                            if (!TextUtils.isEmpty(object.optString("humidity"))) {
                                txtHumity.setText(object.optString("humidity") + " %");
                            } else {
                                txtHumity.setText("--");
                            }

                            if (isCFSelected == 1) {
                                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                                tempCFValue.setText(object.optString("temp_celsius") + " ");
                            } else {
                                setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                                tempCFValue.setText(object.optString("temp_fahrenheit") + " ");
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
        if (mSocket != null) {
            mSocket.off("changeTempSensorValue", changeTempSensorValue);
        }
    }


    private void bindView() {

        sensor_list = findViewById(R.id.sensor_list);
        sensor_list_temp = findViewById(R.id.sensor_list_temp);
        sensor_list.setHasFixedSize(true);
        sensor_list.setLayoutManager(new GridLayoutManager(this, 1));
        sensor_list.setVerticalScrollBarEnabled(true);

        sensor_list_temp.setHasFixedSize(true);
        sensor_list_temp.setLayoutManager(new GridLayoutManager(this, 1));
        sensor_list_temp.setVerticalScrollBarEnabled(true);

        view_rel_badge = findViewById(R.id.view_rel_badge);
        recyclerAlert = findViewById(R.id.recyclerAlert);

        sensorName = findViewById(R.id.sensor_name);
        tempCFValue = findViewById(R.id.txt_tmp_incf);
        linearAlertDown = findViewById(R.id.linearAlertDown);
        linearAlertDown_temp = findViewById(R.id.linearAlertDown_temp);
        linearAlertExpand = findViewById(R.id.linearAlertExpand);
        linearAlertExpand_temp = findViewById(R.id.linearAlertExpand_temp);
        toggleAlert = findViewById(R.id.toggleAlert);
        toggleAlert_temp = findViewById(R.id.toggleAlert_temp);
        txtAlertCount = findViewById(R.id.txtAlertCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtEmpty_temp = findViewById(R.id.txtEmpty_temp);
        txtTempAlertCount = findViewById(R.id.txtTempAlertCount);
        txtTempAlertCount_temp = findViewById(R.id.txtTempAlertCount_temp);
        txtHumity = findViewById(R.id.txtHumity);
        iv_icon_edit = findViewById(R.id.iv_icon_edit);
        viewEditSensor = findViewById(R.id.viewEditSensor);
        scrollviewMulti = findViewById(R.id.scrollviewMulti);
        toggleAlertSensor = findViewById(R.id.toggleAlertSensor);
        linearMultisensor = findViewById(R.id.linearMultisensor);
        imgLog = findViewById(R.id.imgLog);

        txtTempCount = findViewById(R.id.txtTempCount);
        txtTempCount_temp = findViewById(R.id.txtTempCount_temp);

        txtAddButton = findViewById(R.id.btnAdd);
        txtAddButtonTemp = findViewById(R.id.btnAdd_temp);

        txtCButton = findViewById(R.id.txt_c_button);
        txtFButton = findViewById(R.id.txt_f_button);

        btn_delete = findViewById(R.id.btn_delete);

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
                if (flagSensorNoti) {
                    flagSensorNoti = false;
                    toggleAlertSensor.setChecked(flagSensorNoti);
                    recyclerAlert.setVisibility(View.GONE);
                } else {
                    flagSensorNoti = true;
                    toggleAlertSensor.setChecked(flagSensorNoti);
                    recyclerAlert.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * @param tempSensorNotificationId
     * @param notiSwitchOnOff
     * @param isActive
     * @param isNotification
     * @param position
     */
    private void showAlertDialog(final String tempSensorNotificationId, final SwitchCompat notiSwitchOnOff, final boolean isActive, final boolean isNotification, final int position, int isType) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Notification Alert");
        builder1.setMessage("Do you want " + (isActive ? "enable " : "disable ") + " notificaiton ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tempSensorNotificationStatus(tempSensorNotificationId, notiSwitchOnOff, !notiSwitchOnOff.isChecked(), isNotification, position, isType);
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
    private void tempSensorNotificationStatus(String tempSensorNotificationId, final SwitchCompat notiSwitchOnOff, boolean isActive, boolean isNotification, final int position, final int isType) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }


        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = "";
        if (!isNotification) {
            webUrl = ChatApplication.url + Constants.changeMultiSensorStatus;
        } else {
            webUrl = ChatApplication.url + Constants.changeTempSensorNotificationStatus;
        }

        JSONObject jsonNotification = new JSONObject();

        try {
            jsonNotification.put("temp_sensor_notification_id", tempSensorNotificationId);
            jsonNotification.put("is_active", isActive ? 1 : 0);
            jsonNotification.put("temp_sensor_id", temp_module_id);
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
                        JSONObject dataObject = result.getJSONObject("data");
                        String is_active = dataObject.getString("is_active");

//                        if (isType == 1) {
//                            if (position != -1) {
//                                mRemoteCommandList.getAlerts().getTempLists()[0].getTempNotificationList().get(position).setIsActive(is_active);
//                            }
//
//                            boolean isCF = false;
//                            if (isCFSelected == 1) {
//                                isCF = true;
//                            }
//
//                            notificationList = sensorResModel.getDate().getTempLists()[0].getTempNotificationList();
//                            tempSensorInfoAdapter.notifyItemChanged(position, sensorResModel.getDate().getTempLists()[0].getTempNotificationList().get(position));
//
//                            tempSensorInfoAdapter.notifyDataSetChanged();
//                        } else {
//                            if (position != -1) {
//                                sensorResModel.getDate().getTempLists()[0].getHumidityNotificationList().get(position).setIsActive(Integer.valueOf(is_active));
//                            }
//
//                            boolean isCF = false;
//                            if (isCFSelected == 1) {
//                                isCF = true;
//                            }
//
//                            humiditySensorAdapter.notifyItemChanged(position, sensorResModel.getDate().getTempLists()[0].getHumidityNotificationList().get(position));
//                            humiditySensorAdapter.notifyDataSetChanged();
//                        }

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
        MenuItem menuItem = menu.findItem(R.id.action_add);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            updateTempSensor();
        } else if (id == R.id.action_log) {
            checkIntent(true);
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

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.UPDATE_TEMP_SENSOR;

        JSONObject jsonNotification = new JSONObject();
        try {

            //"temp_sensor_id"	: "",
            //  "temp_sensor_name"	: "",
            //  "room_id":"",
            //  "room_name":"",
            //  "is_in_C":0,
            //  "user_id":"",
            //  "phone_id":"",
            //  "phone_type":""
            jsonNotification.put("temp_sensor_id", temp_module_id);
            jsonNotification.put("temp_sensor_name", sensorName.getText().toString().trim());
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
                    }
                    if (code == 200) {
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

    /**
     * @param notificationList
     * @param isEdit
     */

    private void humitySensorNotification(final RemoteDetailsRes.Data.Alert notificationList, final boolean isEdit) {

        tempSensorNotificationDialog = new Dialog(this);
        tempSensorNotificationDialog.setContentView(R.layout.dialog_temp_sensor_notificaiton);
        tempSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = (Button) tempSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = tempSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_min_value = tempSensorNotificationDialog.findViewById(R.id.edit_min_value);
        final EditText edit_max_value = tempSensorNotificationDialog.findViewById(R.id.edit_max_value);

        final TextView txt_notification_alert = tempSensorNotificationDialog.findViewById(R.id.txt_notification_alert);

        txt_notification_alert.setText("Humidity Alert");
        text_day_1 = tempSensorNotificationDialog.findViewById(R.id.text_day_1);
        text_day_2 = tempSensorNotificationDialog.findViewById(R.id.text_day_2);
        text_day_3 = tempSensorNotificationDialog.findViewById(R.id.text_day_3);
        text_day_4 = tempSensorNotificationDialog.findViewById(R.id.text_day_4);
        text_day_5 = tempSensorNotificationDialog.findViewById(R.id.text_day_5);
        text_day_6 = tempSensorNotificationDialog.findViewById(R.id.text_day_6);
        text_day_7 = tempSensorNotificationDialog.findViewById(R.id.text_day_7);

        edit_min_value.setHint("Min %");
        edit_max_value.setHint("Max %");


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
            edit_min_value.setText("" + notificationList.getMinHumidity());
            edit_max_value.setText("" + notificationList.getMaxHumidity());

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
                addHumity(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getDeviceId() : "");
            }

        });

        tempSensorNotificationDialog.show();

    }

    private void tempSensorNotification(final RemoteDetailsRes.Data.Alert notificationList, final boolean isEdit) {

        tempSensorNotificationDialog = new Dialog(this);
        tempSensorNotificationDialog.setContentView(R.layout.dialog_temp_sensor_notificaiton);
        tempSensorNotificationDialog.setCanceledOnTouchOutside(false);

        Button button_save_notification = (Button) tempSensorNotificationDialog.findViewById(R.id.button_save_notification);
        ImageView closeNotification = tempSensorNotificationDialog.findViewById(R.id.btn_close_notification);

        final EditText edit_min_value = tempSensorNotificationDialog.findViewById(R.id.edit_min_value);
        final EditText edit_max_value = tempSensorNotificationDialog.findViewById(R.id.edit_max_value);

        final TextView txt_notification_alert = tempSensorNotificationDialog.findViewById(R.id.txt_notification_alert);

        text_day_1 = tempSensorNotificationDialog.findViewById(R.id.text_day_1);
        text_day_2 = tempSensorNotificationDialog.findViewById(R.id.text_day_2);
        text_day_3 = tempSensorNotificationDialog.findViewById(R.id.text_day_3);
        text_day_4 = tempSensorNotificationDialog.findViewById(R.id.text_day_4);
        text_day_5 = tempSensorNotificationDialog.findViewById(R.id.text_day_5);
        text_day_6 = tempSensorNotificationDialog.findViewById(R.id.text_day_6);
        text_day_7 = tempSensorNotificationDialog.findViewById(R.id.text_day_7);

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
                edit_min_value.setText(notificationList.getMin_temp());
                edit_max_value.setText(notificationList.getMax_temp());
            } else {
                edit_min_value.setText(notificationList.getMin_temp());
                edit_max_value.setText(notificationList.getMax_temp());
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
                addNotification(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getDeviceId() : "");
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

    /**
     * add humidity
     */
    private void addHumity(final TextView txt_notification_alert, EditText minValue, final EditText maxValue, boolean isEdit, String temp_sensor_notification_id) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(minValue.getText().toString().trim()) || minValue.getText().toString().equals("+") || minValue.getText().toString().equals("-")) {
            minValue.requestFocus();
            minValue.setError("Enter Min. Value");
            return;
        }
        if (TextUtils.isEmpty(maxValue.getText().toString().trim()) || maxValue.getText().toString().equals("+") || maxValue.getText().toString().equals("-")) {
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
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonNotification = new JSONObject();
        try {
            if (isEdit) {
                jsonNotification.put("temp_sensor_notification_id", temp_sensor_notification_id);
            }

            jsonNotification.put("temp_sensor_id", temp_module_id);
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
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(minValue.getText().toString().trim()) || minValue.getText().toString().equals("+") || minValue.getText().toString().equals("-")) {
            minValue.requestFocus();
            minValue.setError("Enter Min. Value");
            return;
        }
        if (TextUtils.isEmpty(maxValue.getText().toString().trim()) || maxValue.getText().toString().equals("+") || maxValue.getText().toString().equals("-")) {
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
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        try {

            //  "temp_sensor_id"	: "",
            //  "min_temp_value": "",
            //  "max_temp_value": "",
            //  "is_in_C": ,                   //0 and 1 for Temp, -1 for Humidity
            //  "min_humidity_value": ,
            //  "max_humidity_value": ,
            //  "days": "",
            //  "user_id":"",
            //  "phone_id": "",
            //  "phone_type": ""
            //

            if (isEdit) {
                jsonNotification.put("temp_sensor_notification_id", temp_sensor_notification_id);
            }

            jsonNotification.put("temp_sensor_id", temp_module_id);
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ((id == R.id.text_day_1) || (id == R.id.text_day_2) || (id == R.id.text_day_3) || (id == R.id.text_day_4)
                || (id == R.id.text_day_5) || (id == R.id.text_day_6) || (id == R.id.text_day_7)) {
            Common.setOnOffBackground(this, tempSensorNotificationDialog.findViewById(id));
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
        } else if (v == iv_icon_edit || v == sensorName) {
            if (flagEditSensor) {
                viewEditSensor.setVisibility(View.GONE);
                flagEditSensor = false;
                sensorName.setCursorVisible(false);
                sensorName.setFocusableInTouchMode(false);
                sensorName.setClickable(false);
                ChatApplication.closeKeyboard(this);
            } else {
                viewEditSensor.setVisibility(View.VISIBLE);

                flagEditSensor = true;
                sensorName.setCursorVisible(true);
                sensorName.setFocusable(true);
                sensorName.setFocusableInTouchMode(true);
                sensorName.setClickable(true);
                sensorName.requestFocus();
                ChatApplication.keyBoardShow(this, sensorName);
            }
        } else if (v == toggleAlertSensor || v == linearMultisensor) {
            if (flagSensorNoti) {
                flagSensorNoti = false;
                toggleAlertSensor.setChecked(flagSensorNoti);
                recyclerAlert.setVisibility(View.GONE);
            } else {
                flagSensorNoti = true;
                toggleAlertSensor.setChecked(flagSensorNoti);
                recyclerAlert.setVisibility(View.VISIBLE);
            }
        } else if (v == imgLog) {
            checkIntent(true);
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
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonNotification = new JSONObject();
        try {

            // {
            //	"multi_sensor_id"	: "1559630413440_xZzVL01iz",
            //	"user_id":"1559035111028_VojOpeeBF",
            //	"phone_id":"1234567",
            //	"phone_type":"Android"
            //	 }
            jsonNotification.put("device_id", temp_module_id);
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
            }
        }).execute();

    }


    /**
     * notify adapter if user selected C or F toggle button
     *
     * @param isCF
     */
    private void notifyAdapter(boolean isCF) {

        if (isCF) {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            tempCFValue.setText(mRemoteCommandList.getDevice().getDeviceStatus()+ " ");

        } else {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));

            tempCFValue.setText(mRemoteCommandList.getDevice().getFahrenheitvalue()+ " ");
        }


        if (notificationList != null && tempSensorInfoAdapter != null) {

            for (int i = 0; i < notificationList.size(); i++) {
                if (isCF) {
                    notificationList.get(i).setCFActive(true);
                } else {
                    notificationList.get(i).setCFActive(false);
                }

            }
            tempSensorInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param notification
     * @param position
     * @param isEdit
     */
    @Override
    public void onEditOpetion(final RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {

        if (isEdit) {
            tempSensorNotification(notification, true);
        } else {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensorNotification(notification, null);
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
     *  @param notification
     * @param swithcCompact
     * @param position
     * @param isActive
     */
    @Override
    public void onSwitchChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {

        showAlertDialog(notification.getDeviceId(), swithcCompact, isActive, true, position, 1);

    }

    /**
     * Delete temp sensor notification
     *  @param notificationList
     * @param notification*/
    private void deleteTempSensorNotification(RemoteDetailsRes.Data.Alert notificationList, RemoteDetailsRes.Data.Alert notification) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
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

        String webUrl = ChatApplication.url + Constants.DELETE_TEMP_SENSOR_NOTIFICATION;
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        JSONObject jsonNotification = new JSONObject();
        try {

            if (notificationList != null) {
                jsonNotification.put("temp_sensor_notification_id", notificationList.getDeviceId());

            } else {
                jsonNotification.put("temp_sensor_notification_id", notification.getDeviceId());
            }
            jsonNotification.put("temp_sensor_id", temp_module_id);
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
//    SensorResModel sensorResModel = new SensorResModel();
    private void getSensorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
//        String url = ChatApplication.url + Constants.getMultiSensorInfo ;
        String url = ChatApplication.url + Constants.deviceinfo;

        JSONObject object = new JSONObject();
        try {
            //{
            //	"device_id":"1571998596623_EE9U9Ovz7w",
            //	"alert_type":"temperature"
            //}
            object.put("device_id", temp_module_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ChatApplication.logDisplay("url is " + url + "  " + object);
        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                startSocketConnection();
                ChatApplication.logDisplay("result is " + result);
                ActivityHelper.dismissProgressDialog();

                mRemoteList = Common.jsonToPojo(result.toString(), RemoteDetailsRes.class);
                mRemoteCommandList = mRemoteList.getData();

                if (mRemoteList.getCode() == 200) {
                    txtEmpty.setVisibility(View.GONE);
                    fillData(mRemoteCommandList, true);

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
     * fill Date from model class
     *
     * @param sensorResModel
     * @param isCF
     */

    public int init = 1;

    private void fillData(RemoteDetailsRes.Data sensorResModel, boolean isCF) {

        sensorName.setText(sensorResModel.getDevice().getDeviceName());
        sensorName.setSelection(sensorName.getText().toString().length());

        if (sensorResModel.getDevice().getDeviceMeta().getUnit().equalsIgnoreCase("C") && !isCFDone) {
            isCFSelected = 1;
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            tempCFValue.setText(sensorResModel.getDevice().getDeviceStatus()+ " ");
        } else {
            isCFSelected = 0;
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white, R.drawable.txt_background_yellow, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));

            /*every getting c value only unit sign change*/
            sensorResModel.getDevice().setFahrenheitvalue(Constants.getFTemp(sensorResModel.getDevice().getDeviceStatus()));
            tempCFValue.setText(sensorResModel.getDevice().getFahrenheitvalue() + " ");
        }

        /*sub_status is humidity*/
        if (!TextUtils.isEmpty(sensorResModel.getDevice().getDeviceSubStatus())) {
            txtHumity.setText(sensorResModel.getDevice().getDeviceSubStatus() + " %");
        } else {
            txtHumity.setText("--");
        }


        for (int i = 0; i < sensorResModel.getAlerts().size(); i++) {
            if (sensorResModel.getAlerts().get(i).getAlertType().equalsIgnoreCase("temperature")) {
                notificationList.add(sensorResModel.getAlerts().get(i));
            } else {
                notificationHumidityList.add(sensorResModel.getAlerts().get(i));
            }
        }

        //humibity
        if (notificationHumidityList != null && notificationHumidityList.size() > 0) {
            txtEmpty_temp.setVisibility(View.GONE);
            sensor_list_temp.setVisibility(View.VISIBLE);
            txtTempAlertCount_temp.setVisibility(View.VISIBLE);
            txtTempAlertCount_temp.setText("(" + notificationHumidityList.size() + " Added)");

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
                RemoteDetailsRes.Data.Alert notificationListModel = notificationHumidityList.get(i);

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
        }

        /*for temp list set*/
        if (notificationList != null && notificationList.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
            sensor_list.setVisibility(View.VISIBLE);
            txtTempAlertCount.setVisibility(View.VISIBLE);
            txtTempAlertCount.setText("(" + notificationList.size() + " Added)");

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
                RemoteDetailsRes.Data.Alert notificationListModel = notificationList.get(i);

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

                        isScrollToEndPosition = (notificationList.size() - 1) == lastVisibleItem && mScrollState == SCROLL_STATE_IDLE;
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
            txtTempCount.setText("Temperature Alert");
            sensor_list.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
        }
        txtAlertCount.setVisibility(View.VISIBLE);

    }

    @Override
    public void onEditHumityOpetion(final RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {

        if (isEdit) {
            humitySensorNotification(notification, true);
        } else {

            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteTempSensorNotification(null, notification);
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        }
    }

    @Override
    public void onSwitchHumityChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {

        showAlertDialog(notification.getDeviceId(), swithcCompact, isActive, true, position, 2);
    }

    private void checkIntent(boolean b) {
        if (b) {
            Intent intent = new Intent(MultiSensorActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", "" +temp_room_id);
            intent.putExtra("Mood_Id", "" + mRemoteCommandList.getDevice().getDevice_id());
            intent.putExtra("activity_type", "tempsensor");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity", "tempsensor");
            intent.putExtra("isRoomName", "" + sensorName.getText().toString());
            startActivity(intent);
        } else {
            MultiSensorActivity.this.finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
