package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.RoomEditActivity_v2;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.RoomVO;

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

    private Socket mSocket;
    Toolbar toolbar;
    EditText edSensorName;
  //  TextView txtbatterylevel;
    Button btnDelete;
    ImageView imgEdit,img_gas_status,img_gassensor;
    AppCompatTextView txtSpeedCount;
    String room_id = "", room_name = "", sensor_id = "", device_id = "", gas_sensor_module_id = "",
            gas_sensor_id = "",battery_level,gas_sensor_name;
    JSONObject objectValue;
    boolean isClick = false;
    BottomSheetDialog bottomsheetdialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_sensor);

        sensor_id = getIntent().getStringExtra("sensor_id");
        room_name = getIntent().getStringExtra("room_name");
        room_id = getIntent().getStringExtra("room_id");
        device_id = getIntent().getStringExtra("device_id");

        setUiId();
    }

    @SuppressLint("NewApi")
    private void setUiId() {
        startSocketConnection();
        toolbar = findViewById(R.id.toolbar);
        txtSpeedCount = findViewById(R.id.txtSpeedCount);
        edSensorName = findViewById(R.id.edSensorName);
        img_gas_status = findViewById(R.id.img_gas_status);
        img_gassensor = findViewById(R.id.img_gassensor);
      //  txtbatterylevel = findViewById(R.id.txt_battery_level);
        btnDelete = findViewById(R.id.btnDelete);
        imgEdit = findViewById(R.id.imgEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        imgEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            btnDelete.setVisibility(View.GONE);
        }

        getGasSensorDetails();
        startTimer();
    }

    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onPause();

    }

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
        }
    }

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
                            ChatApplication.logDisplay("gas socket is update :-" +device_id+"  "+ object);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("changeDeviceStatus", changeDeviceStatus);
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
        } else if (v == imgEdit)
        {
            showBottomSheetDialog();
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        txt_bottomsheet_title.setText("What would you like to do in" + " " + gas_sensor_name + "" +" " +"?");


        bottomsheetdialog = new BottomSheetDialog(GasSensorActivity.this,R.style.AppBottomSheetDialogTheme);
        bottomsheetdialog.setContentView(view);
        bottomsheetdialog.show();


        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetdialog.dismiss();
                if (isClick) {
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
                        updateGasSensor();
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
                }
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
        });

    }

    /*getting value sensor*/
    private void getGasSensorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
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
        });

    }

    private void filldata(@NotNull JSONObject result) {

        try {
            JSONObject object = new JSONObject(result.toString());
            JSONObject data = object.optJSONObject("data");
            JSONObject device = data.optJSONObject("device");
            String  is_active = device.optString("is_active");
            gas_sensor_module_id = device.optString("module_id");
            gas_sensor_id = device.optString("device_id");
            gas_sensor_name = device.optString("device_name");
            edSensorName.setText("" + device.optString("device_name"));

            toolbar.setTitle(gas_sensor_name);

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
            if(is_active.equalsIgnoreCase("n")){
                img_gassensor.setBackgroundResource(R.drawable.fire_and_gas_gray_with_red_cross);
                txtSpeedCount.setText("- -");
                txtSpeedCount.setTextColor(getResources().getColor(R.color.signupblack));
                img_gas_status.setVisibility(View.GONE);

            } else{
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
    private void updateGasSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().updateDevice(device_id, edSensorName.getText().toString(), new DataResponseListener() {
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

}
