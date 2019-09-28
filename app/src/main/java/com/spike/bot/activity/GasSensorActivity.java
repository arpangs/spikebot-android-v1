package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DoorSensorResModel;
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
    String room_id="",room_name="",sensor_id="",module_id="",gas_sensor_module_id="",gas_sensor_id="";
    JSONObject  objectValue;
    boolean isClick=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_sensor);

        sensor_id = getIntent().getStringExtra("sensor_id");
        room_name = getIntent().getStringExtra("room_name");
        room_id = getIntent().getStringExtra("room_id");
        module_id = getIntent().getStringExtra("module_id");

        setUiId();
    }

    @SuppressLint("NewApi")
    private void setUiId() {
        startSocketConnection();
        toolbar =  findViewById(R.id.toolbar);
        txtSpeedCount =  findViewById(R.id.txtSpeedCount);
        edSensorName =  findViewById(R.id.edSensorName);
        btnDelete =  findViewById(R.id.btnDelete);
        imgEdit =  findViewById(R.id.imgEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Gas Sensor");

        imgEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        getGasSensorDetails();

        startTimer();
    }

    @Override
    protected void onPause() {
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
        super.onPause();

    }

    CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        public void onTick(long millisUntilFinished) { }
        public void onFinish() {
            ChatApplication.logDisplay("start count is ");
            startTimer();
            if(mSocket!=null){
                ChatApplication.logDisplay("start count is socket "+objectValue);
                mSocket.emit("socketGasSensorValues", objectValue);
            }
        }

    };
    public void startTimer(){
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if(mSocket!=null){
            mSocket.on("socketGasSensorValues", socketGasSensorValues);
            mSocket.on("changeGasSensorValue", changeGasSensorValue);
        }
    }

    private Emitter.Listener changeGasSensorValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            GasSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            //{"room_id":"1568962984563_I4dhDWr54","gas_sensor_id":"1568968977282_uG3YUsQ1I","gas_value":"Normal","gas_current_value":2,"threshold_value":10}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("gas socket is update "+object);

                            //"room_id":"1569405933287_jyzwqWwos","gas_sensor_id":"1569409977792_MP9OiH_QS","gas_value":"Normal","gas_current_value":5,"threshold_value":10}

                            setLevel(object.optString("gas_value"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener socketGasSensorValues = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            GasSensorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("gas socket is "+object);
                            // setDoorUnreadCount(doorSensorVoltage);

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
            mSocket.on("changeGasSensorValue", changeGasSensorValue);
            mSocket.on("socketGasSensorValues", socketGasSensorValues);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==btnDelete){
            showEditDialog();
        }else if(v==imgEdit){

            if(isClick){
                if(edSensorName.getText().toString().length()==0){
                    ChatApplication.showToast(this,"Please enter gas name");
                }else {
                    isClick=false;
                    edSensorName.setFocusable(false);
                    edSensorName.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                    edSensorName.setClickable(false);
                    imgEdit.setImageResource(R.drawable.edit_blue);
                    ChatApplication.keyBoardHideForce(this);
                    updateGasSensor();
                }
            }else {
                isClick=true;
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
//        MenuItem menuAdd = menu.findItem(R.id.action_add);
//        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
//        MenuItem action_save = menu.findItem(R.id.action_save);
//        menuAdd.setVisible(false);
//        action_save.setVisible(true);
//        actionEdit.setVisible(false);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_save) {
//            if(edSensorName.getText().toString().length()==0){
//                ChatApplication.showToast(this,"Please enter gas name");
//            }else {
//                ChatApplication.keyBoardHideForce(this);
//                updateGasSensor();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void showEditDialog() {

        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?" ,new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteGas();
            }
            @Override
            public void onConfirmDialogNoClick() { }
        });
        newFragment.show(getFragmentManager(), "dialog");

    }

    private void deleteGas() {

        String url = ChatApplication.url + Constants.deleteGasSensor;

        JSONObject object = new JSONObject();
        try {
            //gas_sensor_id"	: "",
            //  "user_id":"",
            //  "phone_id":"",
            //  "phone_type":""
            object.put("gas_sensor_id",sensor_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("gas "+url+" "+object);


        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("door is "+result);
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(GasSensorActivity.this,message);
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


    private void getGasSensorDetails() {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        String url = ChatApplication.url + Constants.getGasSensorInfo;

        JSONObject object = new JSONObject();
        try {
            //Parameteres:{ "gas_sensor_id" and "user_id"}
            object.put("gas_sensor_id",sensor_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("gas "+url+" "+object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("door is "+result);
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        filldata(result);
                        //{"code":200,"message":"Success","data":{"gas_sensor_id":"1568885313159_JFUdxkSHH","gas_sensor_module_id":"90A1131A004B1200"
                        // ,"gas_sensor_name":"gas !!","room_id":"1568885033394_wTSzIhXxu","room_name":"Jhanvi","is_gas_detected":"","gas_value":""}}
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

    private void filldata(JSONObject result) {

        try {
            //{"code":200,"message":"Success","data":{"gas_sensor_id":"1568968977282_uG3YUsQ1I"
            // ,"gas_sensor_module_id":"236F131A004B1200","gas_sensor_name":"ggh","room_id":"1568962984563_I4dhDWr54","room_name":"vpk","is_gas_detected":0,"gas_value":"Normal"}}
            JSONObject object=new JSONObject(result.toString());
            JSONObject data= object.optJSONObject("data");
            gas_sensor_module_id=data.optString("gas_sensor_module_id");
            gas_sensor_id=data.optString("gas_sensor_id");
            edSensorName.setText(""+data.optString("gas_sensor_name"));

            objectValue=new JSONObject();
            objectValue.put("module_id",gas_sensor_module_id);

            if(!TextUtils.isEmpty(data.optString("gas_value"))){
                if(data.optString("gas_value").equals("Normal")){
                    txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
                    txtSpeedCount.setText("Normal");
                }else {
                    txtSpeedCount.setTextColor(Color.RED);
                    txtSpeedCount.setText("High");
                }
            }

            if(mSocket!=null){
                ChatApplication.logDisplay("start count is socket "+objectValue);
                mSocket.emit("socketGasSensorValues", objectValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLevel(String optString) {
            if(optString.equals("Normal")){
                txtSpeedCount.setTextColor(getResources().getColor(R.color.greenDark));
                txtSpeedCount.setText("Normal");
            }else {
                txtSpeedCount.setTextColor(getResources().getColor(R.color.automation_red));
                txtSpeedCount.setText("High");
            }
    }

    private void updateGasSensor() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.updateGasSensor;

        JSONObject jsonNotification = new JSONObject();
        try {
            //  "gas_sensor_id"	: "",
            //  "gas_sensor_name"	: "",
            //  "room_id":"",
            //  "room_name":"",

            jsonNotification.put("gas_sensor_id", gas_sensor_id);
            jsonNotification.put("gas_sensor_name", edSensorName.getText().toString());
            jsonNotification.put("room_id", room_id);
            jsonNotification.put("room_name", room_name);
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
                        isClick=false;
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
