package com.spike.bot.activity.SmartDevice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
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
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.SmartBrandDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 6/8/19.
 * Gmail : vipul patel
 */
public class AddSMartDevicetoRoomActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public AppCompatSpinner spinnerDevice,spinnerPanel,spinnerRoom;
    public AppCompatButton btnSubmit;
    SmartBrandDeviceModel smartBrandDeviceModel;
    String host_ip="",getBridge_name="",bridge_id="",room_id="",panel_id="",device_id="";

    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private ArrayList<String> stringRoomlist = new ArrayList<>();
    private ArrayList<String> stringPanellist = new ArrayList<>();
    private ArrayList<String> stringDevicelist = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        bridge_id=getIntent().getStringExtra("bridge_id");
        getBridge_name=getIntent().getStringExtra("getBridge_name");
        host_ip=getIntent().getStringExtra("host_ip");
        smartBrandDeviceModel=(SmartBrandDeviceModel)getIntent().getSerializableExtra("searchModel");
        setId();
    }

    private void setId() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Device");
        btnSubmit=findViewById(R.id.btnSubmit);
        spinnerDevice=findViewById(R.id.spinnerDevice);
        spinnerPanel=findViewById(R.id.spinnerPanel);
        spinnerRoom=findViewById(R.id.spinnerRoom);


        btnSubmit.setOnClickListener(this);
        getDeviceList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View v) {
        if(v==btnSubmit){
            if(TextUtils.isEmpty(room_id)){
                ChatApplication.showToast(this,"Please select room");
            }else {
                getHueBridgeLightList();
            }
        }

    }

    public void getHueBridgeLightList() {
        if (!ActivityHelper.isConnectingToInternet(AddSMartDevicetoRoomActivity.this)) {
            Toast.makeText(AddSMartDevicetoRoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(AddSMartDevicetoRoomActivity.this, "Please wait... ", false);

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

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",smartBrandDeviceModel.getId());
            jsonObject.put("uniqueid",smartBrandDeviceModel.getUniqueid());
            jsonObject.put("host_ip",host_ip);
            jsonObject.put("smart_device_brand","Philips Hue");
            jsonObject.put("smart_device_type","Smart Bulb");
            jsonObject.put("smart_device_status",smartBrandDeviceModel.getOn().equalsIgnoreCase("true") ? 1:0);
            jsonObject.put("smart_device_brightness",smartBrandDeviceModel.getBri());
            jsonObject.put("smart_device_rgb","[255,255,255]");
            jsonObject.put("smart_device_name",smartBrandDeviceModel.getName());
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            if(TextUtils.isEmpty(panel_id)){
                jsonObject.put("is_new",1);
            }else {
                jsonObject.put("is_new",0);
            }

            jsonObject.put("room_id",room_id);
            jsonObject.put("panel_id",""+panel_id);
            jsonObject.put("room_device_id",""+device_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(AddSMartDevicetoRoomActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(AddSMartDevicetoRoomActivity.this,message);
                    if (code == 200) {
                        Constants.activityAddDeviceConfirmActivity.finish();
                        Constants.activityPhilipsHueBridgeDeviceListActivity.finish();
                        Intent intent=new Intent(AddSMartDevicetoRoomActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
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

        for(int i=0; i<roomList.size(); i++){
            stringRoomlist.add(roomList.get(i).getRoomName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stringRoomlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(dataAdapter);

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room_id=roomList.get(position).getRoomId();
                panel_id="";
                device_id="";
                stringPanellist.clear();
                for(int i=0; i<roomList.get(position).getPanelList().size(); i++){
                    if(!roomList.get(position).getPanelList().get(i).isSensorPanel()){
                        stringPanellist.add(roomList.get(position).getPanelList().get(i).getPanelName());
                    }

                }

                if(stringPanellist.size()>0){
                    spinnerPanel.setVisibility(View.VISIBLE);

                    setPanelList(position);
                }else {
                    spinnerPanel.setVisibility(View.GONE);
                    spinnerDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setPanelList(final int positionRoom) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stringPanellist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPanel.setAdapter(dataAdapter);

        spinnerPanel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panel_id=roomList.get(positionRoom).getPanelList().get(position).getPanelId();
                device_id="";
                stringDevicelist.clear();
                for(int i=0; i<roomList.get(positionRoom).getPanelList().get(position).getDeviceList().size(); i++){
                    stringDevicelist.add(roomList.get(positionRoom).getPanelList().get(position).getDeviceList().get(i).getDeviceName());
                }

                if(stringDevicelist.size()>0){
                    spinnerDevice.setVisibility(View.VISIBLE);
                    setDeviceList(positionRoom,position);
                }else {
                    spinnerDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDeviceList(final int positionRoom, final int positionpanel) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stringDevicelist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(dataAdapter);

        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                device_id=roomList.get(positionRoom).getPanelList().get(positionpanel).getDeviceList().get(position).getOriginal_room_device_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
