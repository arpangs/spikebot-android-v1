package com.spike.bot.activity.Sensor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

public class WaterSensorActivity extends AppCompatActivity implements View.OnClickListener{

    private Socket mSocket;
    Toolbar toolbar;
    EditText edSensorName;
    TextView txtbatterylevel;
    Button btnDelete;
    ImageView imgEdit;
    AppCompatTextView txtSpeedCount;
    SwitchCompat switchtemp;
    String room_id = "", room_name = "", sensor_id = "", device_id = "", water_detector_module_id = "", water_detector_id = "",battery_level, is_alert_set="";
    JSONObject objectValue;
    boolean isClick = false,isAlertOn=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_sensor);

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
        switchtemp = findViewById(R.id.switch_temp);
        txtbatterylevel = findViewById(R.id.txt_battery_level);
        btnDelete = findViewById(R.id.btnDelete);
        imgEdit = findViewById(R.id.imgEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(room_name);

        switchtemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    addalert();
                } else{
                    deletealert();
                }
            }
        });


     /*   switchtemp.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onClick() {
                super.onClick();
                isAlertOn = true;
                switchtemp.setChecked(switchtemp.isChecked());
             //   onNotificationContextMenu.onSwitchChanged(notificationList.get(holder.imgOptions.getId()), holder.switchCompat, holder.switchCompat.getId(), !holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                switchtemp.setChecked(switchtemp.isChecked());
                           }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                switchtemp.setChecked(switchtemp.isChecked());
            }

        });
*/
        imgEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

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
        }
    }

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
                            ChatApplication.logDisplay("water socket is update :-" +device_id+"  "+ object);

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
                    updateWaterSensor();
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
                deleteWater();
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");

    }

    private void addalert(){
        String url = ChatApplication.url + Constants.ADD_TEMP_SENSOR_NOTIFICATION;

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", device_id);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("water alert" + " " + url + " " + object);
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("water is " + result);
                int code = 0;
                try {
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void deletealert(){
        String url = ChatApplication.url + Constants.DELETE_TEMP_SENSOR_NOTIFICATION;

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", device_id);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("water alert" + " " + url + " " + object);
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("water is " + result);
                int code = 0;
                try {
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /*delete sensor*/
    private void deleteWater() {

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
        ChatApplication.logDisplay("water " + url + " " + object);
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("water is " + result);
                int code = 0;
                try {
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /*getting value sensor*/
    private void getWaterdetectorDetails() {

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
        ChatApplication.logDisplay("water " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("water is " + result);
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

            is_alert_set = data.optString("is_alert_set");

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
            }
*/
            if(is_alert_set.equalsIgnoreCase("false")){
                switchtemp.setChecked(false);
            } else{
                switchtemp.setChecked(true);

            }

            water_detector_module_id = device.optString("module_id");
            water_detector_id = device.optString("device_id");
            edSensorName.setText("" + device.optString("device_name"));
            battery_level = device.optString("meta_battery_level");

            int btrPer = (int) Double.parseDouble(battery_level);
            if (btrPer >= 0 && btrPer <= 25)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low));
            else if (btrPer >= 26 && btrPer <= 50)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_low1));
            else if (btrPer >= 51 && btrPer <= 75)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_medium));
            else if (btrPer >= 76 && btrPer <= 100)
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_high));

            if(btrPer > 100){
                txtbatterylevel.setText("100" + "%");
                txtbatterylevel.setTextColor(getResources().getColor(R.color.battery_high));
            }

            int perc = 0;
            if (!TextUtils.isEmpty(""+battery_level)) {
                try {
                    perc = Integer.parseInt(battery_level);
                } catch (Exception ex) {
                    perc = 100;
                    ex.printStackTrace();
                }
            }

            txtbatterylevel.setText(perc + "%");


            objectValue = new JSONObject();
            objectValue.put("module_id", water_detector_module_id);

            /* device_status is 0 than show green color set
             * another high value than set red color*/
            if (!TextUtils.isEmpty(device.optString("device_status"))) {
                if (device.optString("device_status").equals("0")) {
                    txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
                    txtSpeedCount.setText("Dry");
                } else {
                    txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
                    txtSpeedCount.setText("Water Detected");
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
            txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
            txtSpeedCount.setText("Dry");
        } else {
            txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
            txtSpeedCount.setText("Water Detected");
        }
    }

    /*update sensor*/
    private void updateWaterSensor() {

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
