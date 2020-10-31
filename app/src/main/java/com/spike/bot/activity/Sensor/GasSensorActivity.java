package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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

/**
 * Created by Sagar on 13/9/19.
 * Gmail : vipul patel
 */
public class GasSensorActivity extends AppCompatActivity implements View.OnClickListener {

    public static String mSocketCountVal;
    EditText edSensorName;
    TextView mRemoteName;
    Button btnDelete;
    ImageView imgEdit, img_gas_status, img_gassensor, mImgback;
    AppCompatTextView txtSpeedCount;
    String room_id = "", room_name = "", sensor_id = "", device_id = "", gas_sensor_module_id = "",
            gas_sensor_id = "", battery_level, gas_sensor_name;
    JSONObject objectValue;
    boolean isClick = false;
    BottomSheetDialog bottomsheetdialog;
    CardView mNotificationCard;
    boolean isback;
    private Socket mSocket;
    /*every 10 sec emit & getting value */
    CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            ChatApplication.logDisplay("start count is ");
            startTimer();
            if (mSocket != null) {
                ChatApplication.logDisplay("start count is socket " + objectValue);
                mSocket.emit("socketGasSensorValues", objectValue);
            }
        }

    };
    private long mLastClickTime = 0;
    private TextView mNotificationCounter;
    /*change gas value & getting socket
     * check */
    private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            GasSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            //{"room_id":"1568962984563_I4dhDWr54","gas_sensor_id":"1568968977282_uG3YUsQ1I","gas_value":"Normal","gas_current_value":2,"threshold_value":10}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("gas socket is update :-" + device_id + "  " + object);

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
            GasSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String device_id = object.getString("device_id");
                            String counter = object.getString("counter");
                            String user_id = object.getString("user_id");

                            if (user_id.equalsIgnoreCase(Common.getPrefValue(GasSensorActivity.this, Constants.USER_ID))) {

                                if (gas_sensor_id.equalsIgnoreCase(device_id)) {
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
        setContentView(R.layout.activity_gas_sensor);

        sensor_id = getIntent().getStringExtra("sensor_id");
        room_name = getIntent().getStringExtra("room_name");
        room_id = getIntent().getStringExtra("room_id");
        device_id = getIntent().getStringExtra("device_id");

        isback = false;
        setUiId();

    }

    @SuppressLint("NewApi")
    private void setUiId() {
        startSocketConnection();

        txtSpeedCount = findViewById(R.id.txtSpeedCount);
        edSensorName = findViewById(R.id.edSensorName);
        img_gas_status = findViewById(R.id.img_gas_status);
        img_gassensor = findViewById(R.id.img_gassensor);
        //  txtbatterylevel = findViewById(R.id.txt_battery_level);
        mRemoteName = findViewById(R.id.remote_name);
        btnDelete = findViewById(R.id.btnDelete);
        imgEdit = findViewById(R.id.imgEdit);
        mImgback = findViewById(R.id.remote_toolbar_back);

        mNotificationCounter = findViewById(R.id.txt_notification_badge);
        mNotificationCounter.setVisibility(View.INVISIBLE);

        mNotificationCard = findViewById(R.id.card_notification);

        mNotificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                isback = true;
                Intent intent = new Intent(GasSensorActivity.this, DeviceLogRoomActivity.class);
                intent.putExtra("isNotification", "GasSensor");
                intent.putExtra("ROOM_ID", device_id);
                intent.putExtra("Sensorname", gas_sensor_name);
                startActivity(intent);


            }
        });

        mImgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!Common.getPrefValue(GasSensorActivity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            imgEdit.setVisibility(View.VISIBLE);
        } else {
            imgEdit.setVisibility(View.GONE);
        }
        imgEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btnDelete.setVisibility(View.GONE);
        }

        getGasSensorDetails();
        startTimer();
    }

    public void unreadApiCall(final boolean b) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().unreadNotification(gas_sensor_id, "", "", new DataResponseListener() {
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

    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onPause();

    }

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
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
    protected void onResume() {


        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }

        ChatApplication.logDisplay("door call is " + ChatApplication.url);
        if (ChatApplication.isLogResume) {
            ChatApplication.isLogResume = false;
            ChatApplication.isLocalFragmentResume = true;
            mSocketCountVal = "0";
        }
        setUiId();

        ChatApplication.logDisplay("Gas resume call is " + ChatApplication.url);

        if (isback) {
            unreadApiCall(false);
        }

        super.onResume();
    }

    private void socketOn() {
        if (mSocket != null) {
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
            mSocket.on("updateDeviceBadgeCounter", unReadCountNotification);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnDelete) {
            showEditDialog();
        } else if (v == imgEdit) {
            if (isClick == false) {
                showBottomSheetDialog();
            }/* else {
                updateGasSensor();
            }*/
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        txt_bottomsheet_title.setText("What would you like to do in" + " " + gas_sensor_name + "" + " " + "?");


        bottomsheetdialog = new BottomSheetDialog(GasSensorActivity.this, R.style.AppBottomSheetDialogTheme);
        bottomsheetdialog.setContentView(view);
        bottomsheetdialog.show();


        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetdialog.dismiss();
                dialogEditName();
                /*if (isClick) {
                    if (edSensorName.getText().toString().length() == 0) {
                        ChatApplication.showToast(GasSensorActivity.this, "Please enter gas name");
                    } else {
                        isClick = false;
                        edSensorName.setFocusable(false);
                        edSensorName.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        edSensorName.setClickable(false);
                        imgEdit.setImageResource(R.drawable.edit_blue);
                        ChatApplication.keyBoardHideForce(GasSensorActivity.this);
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
//                    imgEdit.setImageResource(R.drawable.save_btn);
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
                deleteGas();
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");

    }

    /*delete sensor*/
    private void deleteGas() {
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
                        ChatApplication.showToast(GasSensorActivity.this, message);
                        GasSensorActivity.this.finish();
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
    private void getGasSensorDetails() {

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

    private void filldata(@NotNull JSONObject result) {

        try {
            JSONObject object = new JSONObject(result.toString());
            JSONObject data = object.optJSONObject("data");
            JSONObject device = data.optJSONObject("device");
            String is_active = device.optString("is_active");
            gas_sensor_module_id = device.optString("module_id");
            gas_sensor_id = device.optString("device_id");
            gas_sensor_name = device.optString("device_name");
            edSensorName.setText("" + device.optString("device_name"));

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


            mRemoteName.setText(gas_sensor_name);

            //  battery_level = device.optString("meta_battery_level");

         /*   int btrPer = (int) Double.parseDouble(battery_level);
            if (btrPer >= 0 && btrPer <= 25)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low));
            else if (btrPer >= 26 && btrPer <= 50)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low1));
            else if (btrPer >= 51 && btrPer <= 75)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_medium));
            else if (btrPer >= 76 && btrPer <= 100)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_high));


            int perc = 0;
            if (!TextUtils.isEmpty(""+battery_level)) {
                try {
                    perc = Integer.parseInt(battery_level);
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
            }

            txtbatterylevel.setText(perc + "%");*/


            objectValue = new JSONObject();
            objectValue.put("module_id", gas_sensor_module_id);

            /* device_status is 0 than show green color set
             * another high value than set red color*/
            if (is_active.equalsIgnoreCase("n")) {
                img_gassensor.setBackgroundResource(R.drawable.fire_and_gas_gray_with_red_cross);
                txtSpeedCount.setText("- -");
                txtSpeedCount.setTextColor(getResources().getColor(R.color.signupblack));
                img_gas_status.setVisibility(View.GONE);

            } else {
                img_gas_status.setVisibility(View.VISIBLE);
                img_gassensor.setBackgroundResource(R.drawable.fire_and_gas);
                if (!TextUtils.isEmpty(device.optString("device_status"))) {
                    if (device.optString("device_status").equals("0")) {
                        txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_yellow));
                        txtSpeedCount.setText("Normal");
                        img_gas_status.setImageResource(R.drawable.dry);
                    } else {
                        txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
                        txtSpeedCount.setText("High");
                        img_gas_status.setImageResource(R.drawable.high);
                    }
                }
            }

            if (mSocket != null) {
                ChatApplication.logDisplay("start count is socket " + objectValue);
                mSocket.emit("socketGasSensorValues", objectValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*set normal & high value */
    private void setLevel(@NotNull String optString) {
        if (optString.equals("0")) {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_yellow));
            txtSpeedCount.setText("Normal");
            img_gas_status.setImageResource(R.drawable.dry);
        } else {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
            txtSpeedCount.setText("High");
            img_gas_status.setImageResource(R.drawable.high);
        }
    }

    /*update sensor*/
    private void updateGasSensor(String mGasSensorName) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().updateDevice(device_id, mGasSensorName, new DataResponseListener() {
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
                        getGasSensorDetails();
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

    /*edit sensor dialog*/
    private void dialogEditName() {
        final Dialog dialog = new Dialog(GasSensorActivity.this);
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
        txtInputSensor.setHint("Enter Gas sensor name");
        tv_title.setText("Gas Sensor Name");
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
                ChatApplication.keyBoardHideForce(GasSensorActivity.this);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEdSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(GasSensorActivity.this, "Please enter name");
                } else {
                    edSensorName.setText(mEdSensorName.getText().toString());
                    dialog.dismiss();
                    updateGasSensor(mEdSensorName.getText().toString());

                }
            }
        });

        dialog.show();

    }

}
