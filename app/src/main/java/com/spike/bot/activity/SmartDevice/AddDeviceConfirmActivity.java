package com.spike.bot.activity.SmartDevice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.TTLock.AddTTlockActivity;
import com.spike.bot.activity.TTLock.AddTTlockToRoomActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.LockObj;
import com.spike.bot.model.SmartBrandDeviceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 6/8/19.
 * Gmail : vipul patel
 */
public class AddDeviceConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btnExtingBridge, btnAddToroom;
    String isViewType = "", host_ip = "", getBridge_name = "", bridge_id = "", door_sensor_module_id = "", door_name = "", door_type = "", lock_data = "", lock_id = "";

    SmartBrandDeviceModel smartBrandDeviceModel;
    ArrayList<LockObj> lockObjs = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_confrim);

        isViewType = getIntent().getStringExtra("isViewType");
        if (isViewType.equalsIgnoreCase("ttLock")) {
            lockObjs = (ArrayList<LockObj>) getIntent().getSerializableExtra("lockObjs");
        } else if (isViewType.equalsIgnoreCase("syncDoor")) {
            door_sensor_module_id = getIntent().getStringExtra("door_sensor_module_id");
            door_name = getIntent().getStringExtra("door_sensor_name");
            door_type = getIntent().getStringExtra("door_type");
            lock_id = getIntent().getStringExtra("lock_id");
            lock_data = getIntent().getStringExtra("lock_data");

            if (TextUtils.isEmpty(door_name)) {
                door_name = "";
            }
            if (TextUtils.isEmpty(door_type)) {
                door_type = "";
            }
        } else {
            bridge_id = getIntent().getStringExtra("bridge_id");
            getBridge_name = getIntent().getStringExtra("getBridge_name");
            host_ip = getIntent().getStringExtra("host_ip");
            smartBrandDeviceModel = (SmartBrandDeviceModel) getIntent().getSerializableExtra("searchModel");
        }
        setId();
    }

    private void setId() {
        Constants.activityAddDeviceConfirmActivity = this;
        toolbar = findViewById(R.id.toolbar);
        btnAddToroom = findViewById(R.id.btnAddToroom);
        btnExtingBridge = findViewById(R.id.btnExtingBridge);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnAddToroom.setOnClickListener(this);
        btnExtingBridge.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        ChatApplication.CurrnetFragment = R.id.navigationDashboard; // dev arpan on 15 june 2020
//        startActivity(new Intent(this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // dev arpan on 15 june 2020
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddToroom) {
            addCustomRoom();
        } else if (v == btnExtingBridge) {
            if (isViewType.equalsIgnoreCase("ttLock") || isViewType.equalsIgnoreCase("syncDoor")) {
                Intent intent = new Intent(this, AddTTlockToRoomActivity.class);
                intent.putExtra("door_sensor_module_id", "" + door_sensor_module_id);
                intent.putExtra("door_sensor_name", "" + door_name);
                intent.putExtra("door_type", "" + door_type);
                intent.putExtra("lockObjs", lockObjs);
                if (door_type.equals("2")) {
                    intent.putExtra("lock_id", "" + lock_id);
                    intent.putExtra("lock_data", "" + lock_data);
                }
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, AddSMartDevicetoRoomActivity.class);
                intent.putExtra("searchModel", smartBrandDeviceModel);
                intent.putExtra("bridge_id", bridge_id);
                intent.putExtra("getBridge_name", getBridge_name);
                intent.putExtra("host_ip", host_ip);
                startActivity(intent);
            }
        }
    }

    /*create room*/
    public void addCustomRoom() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomRoom(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /*room add service*/
    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceConfirmActivity.this)) {
            Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            return;
        }

        ActivityHelper.showProgressDialog(AddDeviceConfirmActivity.this, "Please wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveCustomRoomDevice(roomName.getText().toString(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(AddDeviceConfirmActivity.this, message);
                    ChatApplication.logDisplay("data is room " + result);
                    if (code == 200) {
                        dialog.dismiss();
                        JSONObject jsonObject = new JSONObject(result.toString());

                        if (isViewType.equalsIgnoreCase("ttLock")) {
                            intentNext(roomName.getText().toString(), jsonObject.optString("data"));
                        } else if (isViewType.equalsIgnoreCase("syncDoor")) {
                            roomNameList.add(roomName.getText().toString());
                            showAddSensorDialog(jsonObject.optString("data"));
                        } else {
                            getHueBridgeLightList(jsonObject.optString("data"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*check add  door / lock */
    private void showAddSensorDialog(String room_id) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = dialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = dialog.findViewById(R.id.txt_sensor_name);

        if (!TextUtils.isEmpty(door_type)) {
            if (door_type.equals("1")) {
                dialogTitle.setText("Add Door Sensor");
                txt_sensor_name.setText("Door Name");
            } else if (door_type.equals("2")) {
                dialogTitle.setText("Add Lock");
                txt_sensor_name.setText("Lock Name");
            }
        } else {
            dialogTitle.setText("Add Door Sensor");
            txt_sensor_name.setText("Door Name");
        }


        edt_door_module_id.setText(door_sensor_module_id);
        edt_door_module_id.setFocusable(false);
        edt_door_name.setText("" + door_name);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this, roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // door_type= 1 sensor- door , 2 = lock
                if (!TextUtils.isEmpty(door_type)) {
                    if (door_type.equals("1")) {
                        saveSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, room_id);
                    } else if (door_type.equals("2")) {
                        callAddTTlock(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, room_id);
                    }
                } else {
                    saveSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, room_id);
                }

                dialog.dismiss();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * Add sensor lock
     */
    private void callAddTTlock(final Dialog dialog, EditText textInputEditText, String door_name,
                               String door_module_id, Spinner sp_room_list, String room_id) {
        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().callAddTTlock(lock_id, room_id, lock_data, door_name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    if (result.optString("code").equalsIgnoreCase("200")) {
                        dialog.dismiss();
                        ActivityHelper.dismissProgressDialog();
                        setIntentMain();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /**
     * Save Sensor lock
     */
    private void saveSensor(final Dialog dialog, EditText textInputEditText, String door_name,
                            String door_module_id, Spinner sp_room_list, String room_id) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceConfirmActivity.this)) {
            Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.setError("Enter Name");
            textInputEditText.requestFocus();
            return;
        }
        int room_pos = sp_room_list.getSelectedItemPosition();
        ActivityHelper.showProgressDialog(AddDeviceConfirmActivity.this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveSensor(room_id, roomNameList.get(room_pos), door_name, door_module_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        setIntentMain();
                    } else {
                        Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    private void setIntentMain() {
        Intent intent = new Intent(AddDeviceConfirmActivity.this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ChatApplication.CurrnetFragment = R.id.navigationDashboard;  // dev arpan on 15 june 2020
//        startActivity(new Intent(this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // dev arpan on 15 june 2020
        this.finish();
    }


    private void intentNext(String lockName, String room_id) {
        Intent intent = new Intent(AddDeviceConfirmActivity.this, AddTTlockActivity.class);
        intent.putExtra("lockName", "");
        intent.putExtra("room_id", room_id);
        intent.putExtra("isFlagView", "" + 1);
        intent.putExtra("lockObjs", lockObjs);
        startActivity(intent);
    }

    /* add smart device bulb*/
    public void getHueBridgeLightList(String room_id) {
        if (!ActivityHelper.isConnectingToInternet(AddDeviceConfirmActivity.this)) {
            Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(AddDeviceConfirmActivity.this, "Please wait... ", false);

        //{
        //  "id":"8",
        //        "uniqueid":"00:17:88:01:04:1a:94:d9-0b",
        //        "host_ip":"192.168.175.69",
        //        "smart_device_brand":"Philips Hue",
        //        "smart_device_type":"Smart Bulb",
        //        "smart_device_status":1,
        //        "smart_device_brightness":100,
        //        "smart_device_rgb":"[255,255,255]",
        //        "smart_device_name":"Iot",
        //  "user_id":"1559035111028_VojOpeeBF",
        //  "is_new": 0,
        //  "room_id":"1564203743397_0XzNFAfth",
        //  "panel_id":"1564204083330_j9Rz-Agqr",
        //  "room_device_id":"1564204083339_M3rJKZiVvY"
        //}
        String url = ChatApplication.url + Constants.addHueLight;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", smartBrandDeviceModel.getId());
            jsonObject.put("uniqueid", smartBrandDeviceModel.getUniqueid());
            jsonObject.put("host_ip", host_ip);
            jsonObject.put("smart_device_brand", "Philips Hue");
            jsonObject.put("smart_device_type", "Smart Bulb");
            jsonObject.put("smart_device_status", smartBrandDeviceModel.getOn().equalsIgnoreCase("true") ? 1 : 0);
            jsonObject.put("smart_device_brightness", smartBrandDeviceModel.getBri());
            jsonObject.put("smart_device_rgb", "[255,255,255]");
            jsonObject.put("smart_device_name", smartBrandDeviceModel.getName());
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("is_new", 1);
            jsonObject.put("room_id", room_id);
            jsonObject.put("panel_id", "");
            jsonObject.put("room_device_id", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(AddDeviceConfirmActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isCallDeviceList = true;
                        Constants.activityPhilipsHueBridgeDeviceListActivity.finish();
                        Intent intent = new Intent(AddDeviceConfirmActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();

            }
        }).execute();
    }
}
