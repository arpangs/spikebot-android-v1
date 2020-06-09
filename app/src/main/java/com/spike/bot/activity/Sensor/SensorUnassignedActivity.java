package com.spike.bot.activity.Sensor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.SensorUnassignedAdapter;
import com.spike.bot.adapter.UnAssignRepeatarAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.fragments.DashBoardFragment;
import com.spike.bot.model.SensorUnassignedRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 17/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorUnassignedActivity extends AppCompatActivity {

    private Spinner spinner_room;
    View viewLine;
    private RecyclerView list_sensor;
    private LinearLayout ll_sensor_list_empy, linear_progress;

    ArrayAdapter spinnerArrayAdapter;
    private SensorUnassignedAdapter sensorUnassignedAdapter;
    private UnAssignRepeatarAdapter unAssignRepeatarAdapter;
    private int isDoorSensor;
    public String roomName = "", roomId = "";

    ArrayList<SensorUnassignedRes.Data.UnassigendSensorList> arrayListRepeter = new ArrayList<>();

    List<SensorUnassignedRes.Data.RoomList> roomList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_unassigned);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinner_room =  findViewById(R.id.spinner_room);
        list_sensor =  findViewById(R.id.list_sensor);
        viewLine = findViewById(R.id.viewLine);
        list_sensor.setLayoutManager(new GridLayoutManager(this, 1));

        ll_sensor_list_empy =  findViewById(R.id.ll_sensor_list);
        linear_progress =  findViewById(R.id.linear_progress);

        isDoorSensor = getIntent().getIntExtra("isDoorSensor", 0);
        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");

        if (TextUtils.isEmpty(roomName)) {
            roomName = "";
        }

        if (isDoorSensor == DashBoardFragment.SENSOR_TYPE_DOOR) {
            spinner_room.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
            isDoorSensor = 0;
        } else if (isDoorSensor == DashBoardFragment.SENSOR_TYPE_TEMP) {
            isDoorSensor = 1;
        } else if (isDoorSensor == DashBoardFragment.SENSOR_TYPE_IR) {
            isDoorSensor = 2;
            getSupportActionBar().setTitle("Unassigned IR List");
        } else if (isDoorSensor == DashBoardFragment.Curtain) {
            isDoorSensor = 6;
            getSupportActionBar().setTitle("Curtain List");
        } else if (isDoorSensor == DashBoardFragment.SENSOR_REPEATAR) {
            spinner_room.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
            isDoorSensor = 10;
            getSupportActionBar().setTitle("Unassigned List");
        } else {
            isDoorSensor = 5;
            getSupportActionBar().setTitle("Multi Sensor");
        }

        //10 is repeatar unaasign
        if (isDoorSensor == DashBoardFragment.SENSOR_REPEATAR) {
            callReptorList();
        } else {
            getSensorUnAssignedDetails(isDoorSensor);
        }

    }

    /*call for repeater */
    private void callReptorList() {

        String url = ChatApplication.url + Constants.getUnassignedRepeaterList;

        linear_progress.setVisibility(View.VISIBLE);

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                //   ActivityHelper.dismissProgressDialog();
                linear_progress.setVisibility(View.GONE);

                if (result.optInt("code") == 200) {
                    list_sensor.setVisibility(View.VISIBLE);
                    ll_sensor_list_empy.setVisibility(View.GONE);

                    try {
                        arrayListRepeter.clear();
                        JSONObject object = new JSONObject(result.toString());

                        JSONArray array = object.optJSONArray("data");

                        // "repeator_module_id": "B04A1D1A004B1200",
                        //      "repeator_name": "repeater name g",
                        //      "is_active": 1
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = array.optJSONObject(i);
                            SensorUnassignedRes.Data.UnassigendSensorList repeaterModel = new SensorUnassignedRes.Data.UnassigendSensorList();
                            repeaterModel.setSensorName(object1.optString("repeator_name"));
                            repeaterModel.setSensorId(object1.optString("repeator_module_id"));
                            arrayListRepeter.add(repeaterModel);
                        }

                        if (arrayListRepeter.size() > 0) {
                            unAssignRepeatarAdapter = new UnAssignRepeatarAdapter(SensorUnassignedActivity.this, arrayListRepeter);
                            list_sensor.setAdapter(unAssignRepeatarAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), result.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                linear_progress.setVisibility(View.GONE);
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }


    private void getSensorUnAssignedDetails(int sensor_type) {

        String url = ChatApplication.url + Constants.deviceunassigned;

        linear_progress.setVisibility(View.VISIBLE);

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                linear_progress.setVisibility(View.GONE);

                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(), SensorUnassignedRes.class);

                if (sensorUnassignedRes.getCode() == 200) {

                    if (sensorUnassignedRes.getData().getUnassigendSensorList().size() == 0) {

                        list_sensor.setVisibility(View.GONE);
                        ll_sensor_list_empy.setVisibility(View.VISIBLE);

                    } else {
                        ArrayList<String> rooListSpinnerEmpty = new ArrayList<>();

                        roomList = sensorUnassignedRes.getData().getRoomList();
                        rooListSpinnerEmpty.add("Select Room Name");
                        int position = 0;
                        for (SensorUnassignedRes.Data.RoomList roomList1 : roomList) {
                            if (roomList1.getRoomId().equalsIgnoreCase(roomId)) {
                                position = position;
                            } else {
                                position++;
                            }
                            rooListSpinnerEmpty.add(roomList1.getRoomName());
                        }

                        spinnerArrayAdapter = new ArrayAdapter(SensorUnassignedActivity.this, android.R.layout.simple_spinner_dropdown_item, rooListSpinnerEmpty);
                        spinner_room.setAdapter(spinnerArrayAdapter);

                        for (int i = 0; i < roomList.size(); i++) {
                            if (roomList.get(i).getRoomId().equalsIgnoreCase(roomId)) {
                                spinner_room.setSelection(i + 1);
                                break;
                            }
                        }

                        list_sensor.setVisibility(View.VISIBLE);
                        ll_sensor_list_empy.setVisibility(View.GONE);

                        sensorUnassignedAdapter = new SensorUnassignedAdapter(sensorUnassignedRes.getData().getUnassigendSensorList());
                        list_sensor.setAdapter(sensorUnassignedAdapter);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), sensorUnassignedRes.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                linear_progress.setVisibility(View.GONE);
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {

            if (isDoorSensor == 0) {
                saveSensorUnassinged();
            } else if (isDoorSensor == 10) {
                saveRepeatar();
            } else if (isDoorSensor == 6) {
                saveCurtain();
            } else {
                if (spinner_room.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select room", Toast.LENGTH_LONG).show();
                } else {
                    saveSensorUnassinged();
                }
            }

            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCurtain() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        if (sensorUnassignedAdapter == null) {
            return;
        }

        SensorUnassignedRes.Data.UnassigendSensorList unassigendSensorList = sensorUnassignedAdapter.getSelectedSensor();
        if (unassigendSensorList == null) {

            String message = "";
            if (isDoorSensor == 6) {
                message = "Select at least one curtain";
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        int position = spinner_room.getSelectedItemPosition() - 1;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("curtain_module_id", unassigendSensorList.getModuleId());
            jsonObject.put("curtain_name", unassigendSensorList.getSensorName());
            jsonObject.put("room_id", roomList.get(position).getRoomId());
            jsonObject.put("room_name", roomList.get(position).getRoomName());
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String webUrl = ChatApplication.url + Constants.deviceadd;

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        ChatApplication.isEditActivityNeedResume = true;
                        finish();
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

    private void saveRepeatar() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (unAssignRepeatarAdapter == null) {
            return;
        }

        SensorUnassignedRes.Data.UnassigendSensorList unassigendSensorList = unAssignRepeatarAdapter.getSelectedSensor();
        if (unassigendSensorList == null) {
            String message = "Select at least one Repeater";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject jsonObject = new JSONObject();
        try {
            //"repeator_module_id"	: "B04A1D1A004B1200",
            //		"repeator_name":"repeator new name",
            //	"user_id":"1566980189559_18CfbqxQo",
            //	"phone_id":"1234567",
            //	"phone_type":"Android"
            jsonObject.put("repeator_module_id", unassigendSensorList.getSensorId());
            jsonObject.put("repeator_name", unassigendSensorList.getSensorName());
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String webUrl = ChatApplication.url + Constants.reassignRepeater;

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (code == 200) {
                        finish();
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

    private void saveSensorUnassinged() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        if (sensorUnassignedAdapter == null) {
            return;
        }

        SensorUnassignedRes.Data.UnassigendSensorList unassigendSensorList = sensorUnassignedAdapter.getSelectedSensor();
        if (unassigendSensorList == null) {

            String message = "";
            if (isDoorSensor == 0 || isDoorSensor == 1 || isDoorSensor == 10 || isDoorSensor == 5) {
                message = "Select at least one Sensor";
            } else if (isDoorSensor == 2) {
                message = "Select at least one blaster";
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDoorSensor == 0) {
            Intent intent = new Intent(this, AddDeviceConfirmActivity.class);
            intent.putExtra("isViewType", "syncDoor");
            intent.putExtra("door_sensor_module_id", "" + unassigendSensorList.getModuleId());
            intent.putExtra("door_sensor_name", "" + unassigendSensorList.getSensorName());
            intent.putExtra("door_type", "" + unassigendSensorList.getLock_subtype());
            if (unassigendSensorList.getLock_subtype().equals("2")) {
                intent.putExtra("lock_id", "" + unassigendSensorList.getLock_id());
                intent.putExtra("lock_data", "" + unassigendSensorList.getLock_data());
            }
            startActivity(intent);

            return;
        }
        // "sensor_id": "1559654114379_MnqPvjkOE",
        //                "module_id": "328E131A004B1200",
        //                "sensor_type": "multisensor",
        //                "sensor_name": "mmlti_test",
        //                "room_id": "1559035133696__gkuc3yQy",
        //                "room_name": "test1",
        //                "user_id":"1559035111028_VojOpeeBF",
        //				"phone_id":"1234567",
        //				"phone_type":"Android"

        int position = spinner_room.getSelectedItemPosition() - 1;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_id", roomList.get(position).getRoomId());
            jsonObject.put("room_name", roomList.get(position).getRoomName());
            jsonObject.put("sensor_id", unassigendSensorList.getSensorId());
            jsonObject.put("module_id", unassigendSensorList.getModuleId());

            String sensor_t = "";
            if (unassigendSensorList.getSensorIcon().equalsIgnoreCase("doorsensor")) {
                sensor_t = "door";
            } else if (unassigendSensorList.getSensorIcon().equalsIgnoreCase("tempsensor")) {
                sensor_t = "temp";
            } else if (unassigendSensorList.getSensorIcon().equalsIgnoreCase("irblaster")) {
                sensor_t = "irblaster";
            } else if (unassigendSensorList.getSensorIcon().equalsIgnoreCase("multisensor")) {
                sensor_t = "multisensor";
            } else if (unassigendSensorList.getSensorIcon().equalsIgnoreCase("gassensor")) {
                sensor_t = "gas";
            }

            jsonObject.put("sensor_type", sensor_t);
            jsonObject.put("sensor_name", unassigendSensorList.getSensorName());

            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String webUrl = ChatApplication.url + Constants.SAVE_UNCONFIGURED_SENSOR;

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        ChatApplication.isEditActivityNeedResume = true;
                        finish();
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
