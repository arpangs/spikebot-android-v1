package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.adapter.HumiditySensorAdapter;
import com.spike.bot.adapter.TempMultiSensorAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
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

    public NestedScrollView scrollviewMulti;
    private EditText edsensorName;
    public View viewEditSensor;
    private ImageView view_rel_badge, iv_icon_edit, imgLog;
    private TextView txtAlertCount, txtEmpty, txtEmpty_temp, txtHumity, txtTempCount, tempCFValue,
            txtTempAlertCount, txtCButton, txtFButton, txtTempAlertCount_temp, txtTempCount_temp, txtAddButton, txtAddButtonTemp,txt_battery_level;
    private Button btn_delete;
    private ToggleButton toggleAlert, toggleAlert_temp, toggleAlertSensor;
    private LinearLayout linearAlertExpand, linearAlertDown, linearAlertExpand_temp, linearAlertDown_temp, linearMultisensor,edt_txt_layout2,edt_txt_layout_temp;
    private boolean flagAlert = false, flagAlertTemp = false, flagEditSensor = false, flagSensorNoti = false;

    private String temp_room_name, temp_room_id, temp_module_id, temp_sensor_name;
    private int isCFSelected = -1;
    private Socket mSocket;

    private RemoteDetailsRes mRemoteList;
    private RemoteDetailsRes.Data mRemoteCommandList;
    //    SensorResModel.DATA.TempList[] tempLists;
    ArrayList<RemoteDetailsRes.Data.Alert> notificationList = new ArrayList<>();
    ArrayList<RemoteDetailsRes.Data.Alert> notificationHumidityList = new ArrayList<>();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int lastVisibleItem;
    private boolean isScrollToEndPosition = false;

    private TextView text_day_1, text_day_2, text_day_3, text_day_4, text_day_5, text_day_6, text_day_7;
    Dialog tempSensorNotificationDialog;
    public boolean isCFDone = false;
    View view_line_top_temp,view_line_top;

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

        temp_sensor_name = getIntent().getStringExtra("temp_sensor_name");
        temp_room_name = getIntent().getStringExtra("temp_room_name");
        temp_room_id = getIntent().getStringExtra("temp_room_id");
        temp_module_id = getIntent().getStringExtra("temp_module_id");

        toolbar.setTitle(temp_sensor_name);
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
        startSocketConnection();
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
            mSocket.on("changeDeviceStatus", changeDeviceStatus );
        }
    }


    /*change device status socket getting
    * defult value is celsius */
    private Emitter.Listener changeDeviceStatus  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            MultiSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());

                            //{"device_id":"1571998596623_EE9U9Ovz7w","device_type":"temp_sensor","device_status":"28","device_sub_status":43}

                            ChatApplication.logDisplay("temp is " + object);

                            if(mRemoteCommandList.getDevice().getDevice_id().equalsIgnoreCase(object.optString("device_id"))) {
                                if (!TextUtils.isEmpty(object.optString("device_sub_status"))) {
                                    txtHumity.setText(object.optString("device_sub_status") + " %");
                                } else {
                                    txtHumity.setText("--");
                                }

                                if (!TextUtils.isEmpty(object.optString("device_status"))) {
                                    mRemoteCommandList.getDevice().setDeviceStatus(object.optString("device_status"));
                                    mRemoteCommandList.getDevice().setFahrenheitvalue(Constants.getFTemp(object.optString("device_status")));
                                }

                                if (isCFSelected == 1) {
                                    setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
                                    tempCFValue.setText(mRemoteCommandList.getDevice().getDeviceStatus() + " " +  Common.getC());
                                } else {
                                    setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white_temp, R.drawable.txt_background_blue, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
                                    tempCFValue.setText(mRemoteCommandList.getDevice().getFahrenheitvalue() + " " + "F");
                                }
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
            mSocket.off("changeDeviceStatus ", changeDeviceStatus);
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

        edsensorName = findViewById(R.id.sensor_name);
        tempCFValue = findViewById(R.id.txt_tmp_incf);
        linearAlertDown = findViewById(R.id.linearAlertDown);
        linearAlertDown_temp = findViewById(R.id.linearAlertDown_temp);
        linearAlertExpand = findViewById(R.id.linearAlertExpand);
        linearAlertExpand_temp = findViewById(R.id.linearAlertExpand_temp);
        edt_txt_layout_temp = findViewById(R.id.edt_txt_layout_temp);
        edt_txt_layout2 = findViewById(R.id.edt_txt_layout2);
        toggleAlert = findViewById(R.id.toggleAlert);
        toggleAlert_temp = findViewById(R.id.toggleAlert_temp);
        txtAlertCount = findViewById(R.id.txtAlertCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtEmpty_temp = findViewById(R.id.txtEmpty_temp);
        txtTempAlertCount = findViewById(R.id.txtTempAlertCount);
        txtTempAlertCount_temp = findViewById(R.id.txtTempAlertCount_temp);
        txt_battery_level = findViewById(R.id.txt_battery_level);
        txtHumity = findViewById(R.id.txtHumity);
        iv_icon_edit = findViewById(R.id.iv_icon_edit);
        viewEditSensor = findViewById(R.id.viewEditSensor);
        scrollviewMulti = findViewById(R.id.scrollviewMulti);
        toggleAlertSensor = findViewById(R.id.toggleAlertSensor);
        linearMultisensor = findViewById(R.id.linearMultisensor);
        imgLog = findViewById(R.id.imgLog);
        view_line_top_temp = findViewById(R.id.view_line_top_temp);
        view_line_top= findViewById(R.id.view_line_top);

        txtTempCount = findViewById(R.id.txtTempCount);
        txtTempCount_temp = findViewById(R.id.txtTempCount_temp);

        txtAddButton = findViewById(R.id.btnAdd);
        txtAddButtonTemp = findViewById(R.id.btnAdd_temp);

        txtCButton = findViewById(R.id.txt_c_button);
        txtFButton = findViewById(R.id.txt_f_button);

        btn_delete = findViewById(R.id.btn_delete);

        edsensorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edsensorName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        edsensorName.setTypeface(edsensorName.getTypeface(), Typeface.BOLD);

        txtCButton.setOnClickListener(this);
        txtFButton.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        linearAlertDown.setOnClickListener(this);
        linearAlertDown_temp.setOnClickListener(this);
        iv_icon_edit.setOnClickListener(this);
        edsensorName.setOnClickListener(this);
        linearMultisensor.setOnClickListener(this);
        toggleAlertSensor.setOnClickListener(this);
        imgLog.setOnClickListener(this);

        view_rel_badge.setOnClickListener(this);

        btn_delete.setVisibility(View.GONE);
        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btn_delete.setVisibility(View.GONE);
        }

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

        edsensorName.setFocusable(false);
        edsensorName.setFocusableInTouchMode(false);
        edsensorName.setClickable(false);

        toggleAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagAlert) {
                    flagAlert = false;
                    toggleAlert.setChecked(flagAlert);
                    linearAlertExpand.setVisibility(View.GONE);
                    view_line_top.setVisibility(View.GONE);
                  //  edt_txt_layout2.setBackground(getResources().getDrawable(R.drawable.background_shadow));
                } else {
                    flagAlert = true;
                    toggleAlert.setChecked(flagAlert);
                    view_line_top.setVisibility(View.VISIBLE);
                    linearAlertExpand.setVisibility(View.VISIBLE);
                   // edt_txt_layout2.setBackground(getResources().getDrawable(R.drawable.background_shadow_bottom_side));
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
                    view_line_top_temp.setVisibility(View.GONE);
                //    edt_txt_layout_temp.setBackground(getResources().getDrawable(R.drawable.background_shadow));
                } else {
                    flagAlertTemp = true;
                    toggleAlert_temp.setChecked(flagAlertTemp);
                    linearAlertExpand_temp.setVisibility(View.VISIBLE);
                    view_line_top_temp.setVisibility(View.VISIBLE);
                  //  edt_txt_layout_temp.setBackground(getResources().getDrawable(R.drawable.background_shadow_bottom_side));
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
        builder1.setMessage("Do you want to " + (isActive ? "enable " : "disable ") + " notificaiton ?");
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
            showToast("" + R.string.disconnect);
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().tempSensorNotificationStatus(tempSensorNotificationId, isActive, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is result "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if (isType == 1) {
                            if (position != -1) {
                                notificationList.get(position).setIsActive(isActive ? "y" :"n");
                            }

                            tempSensorInfoAdapter.notifyItemChanged(position, notificationList.get(position));
                            tempSensorInfoAdapter.notifyDataSetChanged();
                        }else {
                            if (position != -1) {
                                notificationHumidityList.get(position).setIsActive(isActive ? "y" :"n");
                            }

                            humiditySensorAdapter.notifyItemChanged(position, notificationHumidityList.get(position));
                            humiditySensorAdapter.notifyDataSetChanged();
                        }
//                        JSONObject dataObject = result.getJSONObject("data");
//                        String is_active = dataObject.getString("is_active");

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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {


            viewEditSensor.setVisibility(View.GONE);
            flagEditSensor = false;
            edsensorName.setCursorVisible(false);
            edsensorName.setFocusableInTouchMode(false);
            edsensorName.setClickable(false);

            updateTempSensor();
        } else if (id == R.id.action_log) {
            checkIntent(true);
        }

        return super.onOptionsItemSelected(item);
    }

    /*update sensor value*/
    private void updateTempSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        if (TextUtils.isEmpty(edsensorName.getText().toString().trim())) {
            edsensorName.setError("Enter Sensor Name");
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().updateTempSensor(temp_module_id, edsensorName.getText().toString().trim(), isCFSelected, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
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

        Button button_save_notification = tempSensorNotificationDialog.findViewById(R.id.button_save_notification);
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
                edit_min_value.setText(Constants.getFTemp(notificationList.getMin_temp()));
                edit_max_value.setText(Constants.getFTemp(notificationList.getMax_temp()));
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
                addNotification(txt_notification_alert, edit_min_value, edit_max_value, isEdit, isEdit ? notificationList.getAlertId() : "");
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

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().addHumity(Integer.parseInt(minValue.getText().toString().trim()), Integer.parseInt(maxValue.getText().toString().trim()),
                repeatDayString, temp_module_id, mRemoteCommandList.getDevice().getDevice_id(), isEdit, new DataResponseListener()
                {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        ActivityHelper.dismissProgressDialog();
                        try {
                            JSONObject result = new JSONObject(stringResponse);
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
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                    }
                });

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

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().addTempNotification(Integer.parseInt(minValue.getText().toString().trim()),Integer.parseInt(maxValue.getText().toString().trim()),repeatDayString,
                temp_sensor_notification_id,mRemoteCommandList.getDevice().getDevice_id(),isEdit,isCFSelected, new DataResponseListener()
                {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        ActivityHelper.dismissProgressDialog();
                        try {
                            JSONObject result = new JSONObject(stringResponse);
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
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                    }
                });

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
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white_temp, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            notifyAdapter(true);

        } else if (id == R.id.txt_f_button) {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white_temp, R.drawable.txt_background_blue, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));
            isCFSelected = 0;
         //   ChatApplication.showToast(MultiSensorActivity.this,"F CLick");
            notifyAdapter(false);

        } else if (id == R.id.btn_delete) {

        } else if (id == R.id.view_rel_badge) {
            view_rel_badge.setClickable(false);
        } else if (id == R.id.linearAlertDown) {
            if (flagAlert) {
                flagAlert = false;
                toggleAlert.setChecked(flagAlert);
                linearAlertExpand.setVisibility(View.GONE);
                view_line_top.setVisibility(View.GONE);
               // edt_txt_layout2.setBackground(getResources().getDrawable(R.drawable.background_shadow));
            } else {
                flagAlert = true;
                toggleAlert.setChecked(flagAlert);
                linearAlertExpand.setVisibility(View.VISIBLE);
                view_line_top.setVisibility(View.VISIBLE);
               // edt_txt_layout2.setBackground(getResources().getDrawable(R.drawable.background_shadow_bottom_side));

            }
        } else if (id == R.id.linearAlertDown_temp) {
            if (flagAlertTemp) {
                flagAlertTemp = false;
                toggleAlert_temp.setChecked(flagAlertTemp);
                linearAlertExpand_temp.setVisibility(View.GONE);
                view_line_top_temp.setVisibility(View.GONE);
             //   edt_txt_layout_temp.setBackground(getResources().getDrawable(R.drawable.background_shadow));
            } else {
                flagAlertTemp = true;
                toggleAlert_temp.setChecked(flagAlertTemp);
                linearAlertExpand_temp.setVisibility(View.VISIBLE);
                view_line_top_temp.setVisibility(View.VISIBLE);
              //  edt_txt_layout_temp.setBackground(getResources().getDrawable(R.drawable.background_shadow_bottom_side));
            }
        } else if (v == iv_icon_edit || v == edsensorName) {

            showBottomSheetDialog();
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

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(MultiSensorActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + temp_sensor_name + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (flagEditSensor) {
                    //viewEditSensor.setVisibility(View.GONE);
                    flagEditSensor = false;
                    edsensorName.setCursorVisible(false);
                    edsensorName.setFocusableInTouchMode(false);
                    edsensorName.setClickable(false);
                    ChatApplication.closeKeyboard(MultiSensorActivity.this);
                } else {
                    // viewEditSensor.setVisibility(View.VISIBLE);

                    flagEditSensor = true;
                    edsensorName.setCursorVisible(true);
                    edsensorName.setFocusable(true);
                    edsensorName.setFocusableInTouchMode(true);
                    edsensorName.setClickable(true);
                    edsensorName.requestFocus();
                    ChatApplication.keyBoardShow(MultiSensorActivity.this, edsensorName);
                }
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
            }
        });
    }


    /**
     * delete individual temp sensor
     */
    private void deleteTempSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().deleteDevice(temp_module_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {

            }
        });

    }


    /**
     * notify adapter if user selected C or F toggle button
     *
     * @param isCF
     */
    private void notifyAdapter(boolean isCF) {

        if (isCF) {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            tempCFValue.setText(mRemoteCommandList.getDevice().getDeviceStatus() + " " +  Common.getC());

        } else {
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white_temp, R.drawable.txt_background_blue, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));

            tempCFValue.setText(mRemoteCommandList.getDevice().getFahrenheitvalue() + " " + "F");
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
            showBottomSheetDialognotification(notification,isEdit);
        }
    }


    public void showBottomSheetDialognotification(final RemoteDetailsRes.Data.Alert notification, boolean isEdit) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(MultiSensorActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + "temperature notification" + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tempSensorNotification(notification, true);

            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
        });
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
    public void onSwitchChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {

        showAlertDialog(notification.getAlertId(), swithcCompact, isActive, true, position, 1);

    }

    /**
     * Delete temp sensor notification
     *
     * @param notificationList
     * @param notification
     */
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
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().deleteTempSensorNotification(notificationList, notification, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
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
        SpikeBotApi.getInstance().deviceInfo(temp_module_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    notificationHumidityList.clear();
                    notificationList.clear();
                    JSONObject result = new JSONObject(stringResponse);
                    mRemoteList = Common.jsonToPojo(result.toString(), RemoteDetailsRes.class);
                    mRemoteCommandList = mRemoteList.getData();

                    if (mRemoteList.getCode() == 200) {
                        txtEmpty.setVisibility(View.GONE);
                        fillData(mRemoteCommandList, true);
                    }
                    ActivityHelper.dismissProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /**
     * fill Date from model class
     *
     * @param sensorResModel
     * @param isCF
     */

    public int init = 1;

    private void fillData(RemoteDetailsRes.Data sensorResModel, boolean isCF) {


        edsensorName.setText(sensorResModel.getDevice().getDeviceName());
        edsensorName.setSelection(edsensorName.getText().toString().length());

        if (sensorResModel.getDevice().getmeta_unit().equalsIgnoreCase("C") && !isCFDone) {
            isCFSelected = 1;
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_yellow, R.drawable.txt_background_white, Color.parseColor("#FFFFFF"), Color.parseColor("#111111"));
            tempCFValue.setText(sensorResModel.getDevice().getDeviceStatus() + " " + Common.getC());

            /*every getting c value only unit sign change*/
            sensorResModel.getDevice().setFahrenheitvalue(Constants.getFTemp(sensorResModel.getDevice().getDeviceStatus()));

        } else {
            isCFSelected = 0;
            setTxtBackColor(txtCButton, txtFButton, R.drawable.txt_background_white_temp, R.drawable.txt_background_blue, Color.parseColor("#111111"), Color.parseColor("#FFFFFF"));

            sensorResModel.getDevice().setFahrenheitvalue(Constants.getFTemp(sensorResModel.getDevice().getDeviceStatus()));

            tempCFValue.setText(sensorResModel.getDevice().getFahrenheitvalue() + " " + "F");
        }

        if(sensorResModel.getDevice().getMeta_battery_level()!=null)
        {

            int perc = 0;
            if (!TextUtils.isEmpty(""+sensorResModel.getDevice().getMeta_battery_level())) {
                try {
                    perc = Integer.parseInt(sensorResModel.getDevice().getMeta_battery_level());
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
            }


            txt_battery_level.setText(perc + " %");
            if (perc >= 0 && perc <= 25)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_low));
            else if (perc >= 26 && perc <= 50)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_low1));
            else if (perc >= 51 && perc <= 75)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_medium));
            else if (perc >= 76 && perc <= 100)
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_high));

            if(perc > 100){
                txt_battery_level.setText("100" + "%");
                txt_battery_level.setTextColor(getResources().getColor(R.color.battery_high));
            }
        } else{
            txt_battery_level.setText("- -");
        }

        /*sub_status is humidity*/
        if (!TextUtils.isEmpty(sensorResModel.getDevice().getDeviceSubStatus())) {
            txtHumity.setText(sensorResModel.getDevice().getDeviceSubStatus() + " %");
        } else {
            txtHumity.setText("   - -");
        }

        if(sensorResModel.getDevice().getIsActive().contains("n")){
            tempCFValue.setText("   - -");
            txtHumity.setText("   - -");
            txt_battery_level.setText("- -");
        }

        for (int i = 0; i < sensorResModel.getAlerts().size(); i++) {
            if (sensorResModel.getAlerts().get(i).getAlertType().equalsIgnoreCase("temperature")) {
                notificationList.add(sensorResModel.getAlerts().get(i));
            } else {
                notificationHumidityList.add(sensorResModel.getAlerts().get(i));
            }
        }

        //humibity
        if (notificationHumidityList != null && notificationHumidityList.size() > 0)
        {
            txtEmpty_temp.setVisibility(View.GONE);
            sensor_list_temp.setVisibility(View.VISIBLE);
            txtTempAlertCount_temp.setVisibility(View.VISIBLE);
            txtTempAlertCount_temp.setText(notificationHumidityList.size() + " " + " Humidity Notification");

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
            txtTempAlertCount_temp.setText("0"+ " " + " Humidity Notification");
            txtTempCount_temp.setText("Humidity Notification");
            sensor_list_temp.setVisibility(View.GONE);
            txtEmpty_temp.setVisibility(View.GONE);
        }

        /*for temp list set*/
        if (notificationList != null && notificationList.size() > 0)
        {
            txtEmpty.setVisibility(View.GONE);
            sensor_list.setVisibility(View.VISIBLE);
            txtTempAlertCount.setVisibility(View.VISIBLE);
            txtTempAlertCount.setText(notificationList.size() + " " + " Temperature Notification");

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
            txtTempAlertCount.setText("0" + " " +" Temperature Notification");
            txtTempCount.setText("Temperature Notification");
            sensor_list.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
        }
        txtAlertCount.setVisibility(View.VISIBLE);

    }

    @Override
    public void onEditHumityOpetion(final RemoteDetailsRes.Data.Alert notification, int position, boolean isEdit) {

        if (isEdit) {

            showBottomSheetDialoghumidity(notification,isEdit);
        }
    }

    public void showBottomSheetDialoghumidity(final RemoteDetailsRes.Data.Alert notification, boolean isEdit) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(MultiSensorActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + "humidity notification" + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                humitySensorNotification(notification, true);

            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    @Override
    public void onSwitchHumityChanged(RemoteDetailsRes.Data.Alert notification, SwitchCompat swithcCompact, int position, boolean isActive) {

        showAlertDialog(notification.getAlertId(), swithcCompact, isActive, true, position, 2);
    }

    private void checkIntent(boolean b) {
        if (b) {
            Intent intent = new Intent(MultiSensorActivity.this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", "" + mRemoteCommandList.getDevice().getDevice_id());
            intent.putExtra("Mood_Id", "" + mRemoteCommandList.getDevice().getDevice_id());
            intent.putExtra("activity_type", "tempsensor");
            intent.putExtra("IS_SENSOR", true);
            intent.putExtra("tabSelect", "show");
            intent.putExtra("isCheckActivity", "tempsensor");
            intent.putExtra("isRoomName", "" + edsensorName.getText().toString());
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
