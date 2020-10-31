package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogRoomActivity;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

public class WaterSensorActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edSensorName;
    TextView txtbatterylevel;
    Button btnDelete;
    ImageView imgEdit, img_water_status, img_watersensor, mImgback;
    AppCompatTextView txtSpeedCount;
    SwitchCompat switchtemp;
    String room_id = "", room_name = "", sensor_id = "", device_id = "",
            water_detector_module_id = "", water_detector_id = "", battery_level, is_alert_set = "", water_sensor_name;
    JSONObject objectValue;
    boolean isClick = false, isAlertOn = false;
    BottomSheetDialog bottomsheetdialog;
    TextView mRemoteName;
    CardView mNotificationCard;
    boolean isBack;
    private Socket mSocket;
    private TextView mNotificationCounter;
    private long mLastClickTime = 0;
    /*change water value & getting socket
     * check */
    private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            WaterSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            //{"room_id":"1568962984563_I4dhDWr54","gas_sensor_id":"1568968977282_uG3YUsQ1I","gas_value":"Normal","gas_current_value":2,"threshold_value":10}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("water socket is update :-" + device_id + "  " + object);

                            if (device_id.equals(object.optString("device_id"))) {
                                setLevel(object.optString("device_status"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener unReadCountNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WaterSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String device_id = object.getString("device_id");
                            String counter = object.getString("counter");
                            String user_id = object.getString("user_id");

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(WaterSensorActivity.this, Constants.USER_ID))) {

                                if (water_detector_id.equalsIgnoreCase(device_id)) {
                                    if (!counter.equalsIgnoreCase("") || !counter.equalsIgnoreCase("null")) {
                                        try {
                                            int count = Integer.parseInt(counter);

                                            if (count > 0) {
                                                mNotificationCounter.setVisibility(View.VISIBLE);
                                                mNotificationCounter.setText(count + "");

                                                if (count > 99) {
                                                    mNotificationCounter.setText("99+");
                                                    mNotificationCounter.setBackground(getResources().getDrawable(R.drawable.badge_background_oval));
                                                } else {
                                                    mNotificationCounter.setBackground(getResources().getDrawable(R.drawable.badge_background));
                                                }
                                            } else {
                                                mNotificationCounter.setVisibility(View.INVISIBLE);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_sensor);

        sensor_id = getIntent().getStringExtra("sensor_id");
        room_name = getIntent().getStringExtra("room_name");
        room_id = getIntent().getStringExtra("room_id");
        device_id = getIntent().getStringExtra("device_id");

        isBack = false;
        setUiId();
    }

    @SuppressLint("NewApi")
    private void setUiId() {
        startSocketConnection();

        txtSpeedCount = findViewById(R.id.txtSpeedCount);
        edSensorName = findViewById(R.id.edSensorName);
        switchtemp = findViewById(R.id.switch_temp);
        txtbatterylevel = findViewById(R.id.txt_battery_level);
        img_water_status = findViewById(R.id.img_water_status);
        img_watersensor = findViewById(R.id.img_watersensor);
        btnDelete = findViewById(R.id.btnDelete);
        imgEdit = findViewById(R.id.imgEdit);


        mNotificationCounter = findViewById(R.id.txt_notification_badge);
        mNotificationCounter.setVisibility(View.INVISIBLE);
        mImgback = findViewById(R.id.remote_toolbar_back);

        mImgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mNotificationCard = findViewById(R.id.card_notification);

        mNotificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                isBack = true;
                Intent intent = new Intent(WaterSensorActivity.this, DeviceLogRoomActivity.class);
                intent.putExtra("isNotification", "WaterSensor");
                intent.putExtra("ROOM_ID", device_id);
                intent.putExtra("Sensorname", water_sensor_name);
                startActivity(intent);

            }
        });
        mRemoteName = findViewById(R.id.remote_name);

        if (!Common.getPrefValue(WaterSensorActivity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            imgEdit.setVisibility(View.VISIBLE);
        } else {
            imgEdit.setVisibility(View.GONE);
        }


        switchtemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addalert();
                } else {
                    deletealert();
                }
            }
        });

        imgEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        btnDelete.setVisibility(View.GONE);
        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btnDelete.setVisibility(View.GONE);
        }
        getWaterdetectorDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /*start connection*/
    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
            mSocket.on("updateDeviceBadgeCounter", unReadCountNotification);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("changeDeviceStatus", changeDeviceStatus);
            mSocket.off("updateDeviceBadgeCounter", unReadCountNotification);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if(isAlertOn) {
                addalert();
            } else{
                deletealert();
            }
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {
        if (v == btnDelete) {
            showEditDialog();
        } else if (v == imgEdit) {
//            if(isClick == false) {
            showBottomSheetDialog();
//            } else{
//                updateWaterSensor();
//            }
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        txt_bottomsheet_title.setText("What would you like to do in" + " " + water_sensor_name + "" + " " + "?");


        bottomsheetdialog = new BottomSheetDialog(WaterSensorActivity.this, R.style.AppBottomSheetDialogTheme);
        bottomsheetdialog.setContentView(view);
        bottomsheetdialog.show();


        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetdialog.dismiss();
                dialogEditName();
               /* if (isClick) {
                    if (edSensorName.getText().toString().length() == 0) {
                        ChatApplication.showToast(WaterSensorActivity.this, "Please enter sensor name");
                    } else {
                        bottomsheetdialog.dismiss();
                        isClick = false;
                        edSensorName.setFocusable(false);
                        edSensorName.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        edSensorName.setClickable(false);
                        imgEdit.setImageResource(R.drawable.edit_blue);
                        ChatApplication.keyBoardHideForce(WaterSensorActivity.this);
                        bottomsheetdialog.dismiss();
                    }
                } else {
                    isClick = true;
                    edSensorName.setFocusable(true);
                    edSensorName.setFocusableInTouchMode(true);
                    edSensorName.setClickable(true);
                    edSensorName.setCursorVisible(true);
                    edSensorName.setEnabled(true);
                    edSensorName.requestFocus();
                    edSensorName.setSelection(edSensorName.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edSensorName, InputMethodManager.SHOW_IMPLICIT);

                    imgEdit.setImageResource(R.drawable.save_btn);
                }*/
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetdialog.dismiss();
                showEditDialog();
            }
        });
    }


    private void showEditDialog() {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteWater();
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");

    }

    private void addalert() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addalert(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        //  ChatApplication.showToast(WaterSensorActivity.this, message);
                        //   WaterSensorActivity.this.finish();
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

    private void deletealert() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deletealert(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        //      ChatApplication.showToast(WaterSensorActivity.this, message);
                        //  WaterSensorActivity.this.finish();
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

    /*delete sensor*/
    private void deleteWater() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(WaterSensorActivity.this, message);
                        WaterSensorActivity.this.finish();
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

    /*getting value sensor*/
    private void getWaterdetectorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deviceInfo(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        filldata(result);
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
    protected void onResume() {


        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }

        ChatApplication.logDisplay("door call is " + ChatApplication.url);
        if (ChatApplication.isLogResume) {
            ChatApplication.isLogResume = false;
            ChatApplication.isLocalFragmentResume = true;
        }
        setUiId();

        if (isBack) {
            unreadApiCall(false);
        }

        ChatApplication.logDisplay("Water resume call is " + ChatApplication.url);

        super.onResume();
    }

    private void socketOn() {
        if (mSocket != null) {
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
            mSocket.on("updateDeviceBadgeCounter", unReadCountNotification);
        }
    }

    private void filldata(@NotNull JSONObject result) {

        try {
            JSONObject object = new JSONObject(result.toString());
            JSONObject data = object.optJSONObject("data");
            JSONObject device = data.optJSONObject("device");
            String is_active = device.optString("is_active");
            is_alert_set = data.optString("is_alert_set");
            battery_level = device.optString("meta_battery_level");

            try {
                int count = data.optInt("unread_count");
                if (count > 0) {
                    mNotificationCounter.setVisibility(View.VISIBLE);
                    mNotificationCounter.setText(count + "");

                    if (count > 99) {
                        mNotificationCounter.setText("99+");
                        mNotificationCounter.setBackground(getResources().getDrawable(R.drawable.badge_background_oval));
                    } else {
                        mNotificationCounter.setBackground(getResources().getDrawable(R.drawable.badge_background));
                    }
                } else {
                    mNotificationCounter.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //  JSONArray alerts = data.getJSONArray("alerts");


         /*   for (int i = 0; i < alerts.size(); i++) {
                if (sensorResModel.getAlerts().get(i).getAlertType().equalsIgnoreCase("temperature")) {
                    notificationList.add(sensorResModel.getAlerts().get(i));
                } else {
                    notificationHumidityList.add(sensorResModel.getAlerts().get(i));
                }
            }*/
          /*  try {
                JSONArray ja_data = data.getJSONArray("alerts");
                int length = object.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObj = ja_data.getJSONObject(i);
                    is_alert_set = jsonObj.getString("is_alert_set");
                    Log.e("is_alert_set", is_alert_set); // you will get
                }
            }catch (Exception e){
                e.printStackTrace();
            }*/

            if (is_alert_set.equalsIgnoreCase("false")) {
                switchtemp.setChecked(false);
            } else {
                switchtemp.setChecked(true);

            }

            water_detector_module_id = device.optString("module_id");
            water_detector_id = device.optString("device_id");
            water_sensor_name = device.optString("device_name");
            edSensorName.setText("" + device.optString("device_name"));
            mRemoteName.setText(water_sensor_name);
            if (!battery_level.equals("null")) {
                int btrPer = (int) Double.parseDouble(battery_level);
                if (btrPer >= 0 && btrPer <= 25)
                    txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low));
                else if (btrPer >= 26 && btrPer <= 50)
                    txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low1));
                else if (btrPer >= 51 && btrPer <= 75)
                    txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_medium));
                else if (btrPer >= 76 && btrPer <= 100)
                    txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_high));

                int perc = 0;
                try {
                    perc = Integer.parseInt(battery_level);
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
                if (!TextUtils.isEmpty("" + battery_level)) {

                }
                if (btrPer > 100) {
                    txtbatterylevel.setText("100" + "%");
                    txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_high));
                } else {
                    txtbatterylevel.setText(perc + "%");
                }
            } else {
                txtbatterylevel.setText("- -");

            }

            objectValue = new JSONObject();
            objectValue.put("module_id", water_detector_module_id);

            /* device_status is 0 than show green color set
             * another high value than set red color*/
            if (is_active.equalsIgnoreCase("n")) {
                txtSpeedCount.setText("- -");
                txtbatterylevel.setText("- -");
                txtbatterylevel.setTextColor(getResources().getColor(R.color.signupblack));
                img_watersensor.setBackgroundResource(R.drawable.drop_disabled);
                img_water_status.setVisibility(View.GONE);
            } else {
                img_water_status.setVisibility(View.VISIBLE);
                img_watersensor.setBackgroundResource(R.drawable.drop);
                if (!TextUtils.isEmpty(device.optString("device_status"))) {
                    if (device.optString("device_status").equals("0")) {
                        txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_yellow));
                        txtSpeedCount.setText("Dry");
                        img_water_status.setImageResource(R.drawable.dry);
                    } else {
                        txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
                        txtSpeedCount.setText("Water Detected");
                        img_water_status.setImageResource(R.drawable.high);
                    }
                }
            }



          /*  if (mSocket != null) {
                ChatApplication.logDisplay("start count is socket " + objectValue);
                mSocket.emit("socketGasSensorValues", objectValue);
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*set normal & high value */
    private void setLevel(@NotNull String optString) {
        if (optString.equals("0")) {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_yellow));
            txtSpeedCount.setText("Dry");
            img_water_status.setImageResource(R.drawable.dry);
        } else {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
            txtSpeedCount.setText("Water Detected");
            img_water_status.setImageResource(R.drawable.high);
        }
    }

    /*update sensor*/
    private void updateWaterSensor(String sensorName) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().updateDevice(device_id, sensorName, new DataResponseListener() {
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
                        isClick = false;
                        edSensorName.setFocusable(false);
                        edSensorName.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        edSensorName.setClickable(false);
                        getWaterdetectorDetails();
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

    private void dialogEditName() {
        final Dialog dialog = new Dialog(WaterSensorActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = dialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText mEdSensorName = (TextInputEditText) dialog.findViewById(R.id.edSensorName);
        txtInputSensor.setVisibility(View.VISIBLE);
        mEdSensorName.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        mEdSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        mEdSensorName.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);


        mEdSensorName.setText(edSensorName.getText().toString());
//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("1")) {
        txtInputSensor.setHint("Enter water sensor name");
        tv_title.setText("Water Sensor Name");
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
                ChatApplication.keyBoardHideForce(WaterSensorActivity.this);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEdSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(WaterSensorActivity.this, "Please enter name");
                } else {
                    edSensorName.setText(mEdSensorName.getText().toString());
                    dialog.dismiss();
                    updateWaterSensor(mEdSensorName.getText().toString());

                }
            }
        });

        dialog.show();

    }

    public void unreadApiCall(final boolean b) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().unreadNotification(water_detector_id, "", "", new DataResponseListener() {
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
}
