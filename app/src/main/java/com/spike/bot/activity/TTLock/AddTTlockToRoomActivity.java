package com.spike.bot.activity.TTLock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.model.LockObj;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 4/9/19.
 * Gmail : vipul patel
 */
public class AddTTlockToRoomActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public AppCompatSpinner spinnerDevice, spinnerPanel, spinnerRoom;
    public AppCompatButton btnSubmit;
    public AppCompatEditText edtLockName;
    public AppCompatTextView txtLockName, txtDeviceList;

    String room_id = "", room_Name = "", door_name = "", panel_id = "", device_id = "", door_sensor_module_id = "", door_type = "", lock_data = "", lock_id = "";

    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private ArrayList<String> stringPanellist = new ArrayList<>();
    private ArrayList<String> stringRoomlist = new ArrayList<>();
    private ArrayList<String> stringDevicelist = new ArrayList<>();
    private ArrayList<String> stringDevicelistId = new ArrayList<>();
    ArrayList<LockObj> lockObjArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        setId();
    }

    private void setId() {
        door_sensor_module_id = getIntent().getStringExtra("door_sensor_module_id");
        door_name = getIntent().getStringExtra("door_sensor_name");
        door_type = getIntent().getStringExtra("door_type");
        lockObjArrayList = (ArrayList<LockObj>) getIntent().getSerializableExtra("lockObjs");
        if (TextUtils.isEmpty(door_name)) {
            door_name = "";
        }
        if (TextUtils.isEmpty(door_type)) {
            door_type = "";
        }
        if (door_type.equals("2")) {
            lock_id = getIntent().getStringExtra("lock_id");
            lock_data = getIntent().getStringExtra("lock_data");
        }

        toolbar = findViewById(R.id.toolbar);
        txtLockName = findViewById(R.id.txtLockName);
        edtLockName = findViewById(R.id.edtLockName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnSubmit = findViewById(R.id.btnSubmit);
        spinnerDevice = findViewById(R.id.spinnerDevice);
        spinnerPanel = findViewById(R.id.spinnerPanel);
        spinnerRoom = findViewById(R.id.spinnerRoom);
        txtDeviceList = findViewById(R.id.txtDeviceList);

        txtDeviceList.setText("Sensor List");

        edtLockName.setVisibility(View.VISIBLE);
        txtLockName.setVisibility(View.VISIBLE);
        btnSubmit.setOnClickListener(this);

        if (!TextUtils.isEmpty(door_type)) {
            txtLockName.setText(door_type.equals("1") ? "Door Name" : "Lock Name");
            toolbar.setTitle(door_type.equals("1") ? "Select Lock" : "Select Door");
            edtLockName.setText("" + door_name);
            edtLockName.setHint(door_type.equals("1") ? "Door Name" : "Lock Name");
            if (!TextUtils.isEmpty(door_name) && door_name.length() > 0) {
                edtLockName.setFocusable(false);
                edtLockName.setFocusableInTouchMode(false);
            }
        } else {
            toolbar.setTitle("Add Lock");
        }
        getDeviceList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (!TextUtils.isEmpty(door_type)) {
                if (edtLockName.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, door_type.equals("1") ? "Please enter door name" : "Please enter lock name");
                } else {
                    if (door_type.equals("1")) {
                        saveSensor();
                    } else {
                        calllTTLockNew();
                    }
                }
            } else {
                if (edtLockName.getText().toString().length() == 0 && TextUtils.isEmpty(device_id)) {
                    ChatApplication.showToast(this, "Please enter lock name");
                } else if (TextUtils.isEmpty(room_id)) {
                    ChatApplication.showToast(this, "Please select room");
                } else {
                    Intent intent = new Intent(this, AddTTlockActivity.class);
                    intent.putExtra("isFlagView", "0");
                    intent.putExtra("lockName", edtLockName.getText().toString());
                    intent.putExtra("device_id", device_id);
                    intent.putExtra("panel_id", panel_id);
                    intent.putExtra("room_id", room_id);

                    startActivity(intent);
                }
            }
        }
    }

    /**
     * Add Sensor lock
     */
    private void calllTTLockNew() {

        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        String url = ChatApplication.url + Constants.addTTLock;

        JSONObject jsonObject = new JSONObject();
        try {
            if (TextUtils.isEmpty(device_id) || device_id.length() == 0) {
                jsonObject.put("panel_id", "");
                jsonObject.put("door_sensor_id", "");
            } else {
                jsonObject.put("panel_id", "" + panel_id);
                jsonObject.put("door_sensor_id", "" + device_id);
            }
            jsonObject.put("is_new", 0);
            jsonObject.put("lock_id", lock_id);
            jsonObject.put("room_id", room_id);
            jsonObject.put("lock_data", lock_data);
            jsonObject.put("lock_name", edtLockName.getText().toString());
            jsonObject.put("user_id", "" + Common.getPrefValue(this, Constants.USER_ID));
            ChatApplication.logDisplay("url is " + jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject jsonObject1) {
                ChatApplication.logDisplay("result is " + jsonObject1);
                if (jsonObject1.optString("code").equalsIgnoreCase("200")) {
                    ActivityHelper.dismissProgressDialog();
                    setIntentMain();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * Save individual sensor
     */

    private void saveSensor() {

        if (!ActivityHelper.isConnectingToInternet(AddTTlockToRoomActivity.this)) {
            ChatApplication.showToast(AddTTlockToRoomActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(AddTTlockToRoomActivity.this, "Please wait...", false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("door_sensor_name", edtLockName.getText().toString());
            if (TextUtils.isEmpty(device_id) || device_id.length() == 0) {
                obj.put("panel_id", "");
                obj.put("door_sensor_id", "");
            } else {
                obj.put("panel_id", "" + panel_id);
                obj.put("door_sensor_id", "" + device_id);
            }

            obj.put("door_sensor_module_id", door_sensor_module_id);
            obj.put("room_id", room_id);
            obj.put("room_name", room_Name);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("is_new", 0);

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.ADD_DOOR_SENSOR;

        ChatApplication.logDisplay("url is " + url + " " + obj);

        new GetJsonTask(AddTTlockToRoomActivity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("url is result " + result);
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(AddTTlockToRoomActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        setIntentMain();
                    } else {
                        Toast.makeText(AddTTlockToRoomActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("url is result error " + error);
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void setIntentMain() {
        ChatApplication.isCallDeviceList = true;
        Intent intent = new Intent(AddTTlockToRoomActivity.this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    /**
     * Get DeviceList
     */
    public void getDeviceList() {

        ActivityHelper.showProgressDialog(this, " Please Wait...", false);

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            if (TextUtils.isEmpty(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", 1);
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is dashboard " + url + "   " + jsonObject.toString());
        new GetJsonTask2(this, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        roomList = new ArrayList<>();
                        JSONObject dataObject = result.getJSONObject("data");
                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                        roomList = JsonHelper.parseRoomArray(roomArray, false);

                        setViewRoom();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                ActivityHelper.dismissProgressDialog();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setViewRoom() {

        for (int i = 0; i < roomList.size(); i++) {
            stringRoomlist.add(roomList.get(i).getRoomName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringRoomlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(dataAdapter);

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room_id = roomList.get(position).getRoomId();
                room_Name = roomList.get(position).getRoomName();
                panel_id = "";
                device_id = "";
                stringPanellist.clear();
                int posTemp = 0;
                for (int i = 0; i < roomList.get(position).getPanelList().size(); i++) {
                    if (roomList.get(position).getPanelList().get(i).getPanel_type() == 2) {
                        posTemp = i;
                        stringPanellist.add(roomList.get(position).getPanelList().get(i).getPanelName());
                    }

                }

                if (stringPanellist.size() > 0) {
                    spinnerPanel.setVisibility(View.VISIBLE);

                    setPanelList(position, roomList.get(position).getPanelList().get(posTemp));
                } else {
                    spinnerPanel.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Set room panel list
     */
    private void setPanelList(int positionRoom, PanelVO panelVO) {
        panel_id = "";
        device_id = "";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringPanellist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPanel.setAdapter(dataAdapter);

        spinnerPanel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panel_id = panelVO.getPanelId();
                device_id = "";
                door_name = "";
                stringDevicelist.clear();
                for (int i = 0; i < panelVO.getDeviceList().size(); i++) {
                    if (!TextUtils.isEmpty(door_sensor_module_id)) {
                        //only lock show
                        ChatApplication.logDisplay("door_sensor_module_id is " + door_type);
                        if (door_type.equals("1")) {
                            ChatApplication.logDisplay("door_sensor_module_id subtype " + " " + panelVO.getDeviceList().get(i).getDeviceName() + " " + panelVO.getDeviceList().get(i).getDoor_subtype());
                            if (panelVO.getDeviceList().get(i).getDoor_subtype() == 2) {
                                stringDevicelist.add(panelVO.getDeviceList().get(i).getSensor_name());
                                stringDevicelistId.add(panelVO.getDeviceList().get(i).getSensor_id());
                            }
                        } else {
                            if (panelVO.getDeviceList().get(i).getDoor_subtype() == 1) {
                                stringDevicelist.add(panelVO.getDeviceList().get(i).getSensor_name());
                                stringDevicelistId.add(panelVO.getDeviceList().get(i).getSensor_id());
                            }
                        }

                    } else {
                        // only door show
                        if (panelVO.getDeviceList().get(i).getDoor_subtype() == 1) {
                            stringDevicelist.add(panelVO.getDeviceList().get(i).getSensor_name());
                            stringDevicelistId.add(panelVO.getDeviceList().get(i).getSensor_id());
                        }
                    }
                }

                if (stringDevicelist.size() > 0) {
                    spinnerDevice.setVisibility(View.VISIBLE);
                    setDeviceList(positionRoom, position);
                } else {
                    spinnerDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDeviceList(final int positionRoom, final int positionpanel) {
        device_id = "";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringDevicelist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(dataAdapter);

        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                door_name = stringDevicelist.get(position);
                device_id = stringDevicelistId.get(position);

                ChatApplication.logDisplay("device is " + device_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
