package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
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

    private Socket mSocket;
    Toolbar toolbar;
    EditText edSensorName;
    Button btnDelete;
    ImageView imgEdit;
    AppCompatTextView txtSpeedCount;
    String room_id = "", room_name = "", sensor_id = "", device_id = "", gas_sensor_module_id = "", gas_sensor_id = "";
    JSONObject objectValue;
    boolean isClick = false;

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
        btnDelete = findViewById(R.id.btnDelete);
        imgEdit = findViewById(R.id.imgEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(room_name);

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
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
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

            if (isClick) {
                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter gas name");
                } else {
                    isClick = false;
                    edSensorName.setFocusable(false);
                    edSensorName.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                    edSensorName.setClickable(false);
                    imgEdit.setImageResource(R.drawable.edit_blue);
                    ChatApplication.keyBoardHideForce(this);
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

        String url = ChatApplication.url + Constants.DELETE_MODULE;

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", device_id);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("gas " + url + " " + object);
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("door is " + result);
                int code = 0;
                try {
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /*getting value sensor*/
    private void getGasSensorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        String url = ChatApplication.url + Constants.deviceinfo;

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", device_id);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("gas " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("door is " + result);
                int code = 0;
                try {
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void filldata(@NotNull JSONObject result) {

        try {
            JSONObject object = new JSONObject(result.toString());
            JSONObject data = object.optJSONObject("data");
            JSONObject device = data.optJSONObject("device");
            gas_sensor_module_id = device.optString("module_id");
            gas_sensor_id = device.optString("device_id");
            edSensorName.setText("" + device.optString("device_name"));

            objectValue = new JSONObject();
            objectValue.put("module_id", gas_sensor_module_id);

            /* device_status is 0 than show green color set
            * another high value than set red color*/
            if (!TextUtils.isEmpty(device.optString("device_status"))) {
                if (device.optString("device_status").equals("0")) {
                    txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
                    txtSpeedCount.setText("Normal");
                } else {
                    txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
                    txtSpeedCount.setText("High");
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
            txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
            txtSpeedCount.setText("Normal");
        } else {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
            txtSpeedCount.setText("High");
        }
    }

    /*update sensor*/
    private void updateGasSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.SAVE_EDIT_SWITCH;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("device_id", device_id);
            jsonNotification.put("device_name", edSensorName.getText().toString());
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }

}
