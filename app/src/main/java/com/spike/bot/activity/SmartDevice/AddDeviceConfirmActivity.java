package com.spike.bot.activity.SmartDevice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandDeviceModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sagar on 6/8/19.
 * Gmail : vipul patel
 */
public class AddDeviceConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btnExtingBridge,btnAddToroom;

    SmartBrandDeviceModel smartBrandDeviceModel;
    String host_ip="",getBridge_name="",bridge_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_confrim);

        bridge_id=getIntent().getStringExtra("bridge_id");
        getBridge_name=getIntent().getStringExtra("getBridge_name");
        host_ip=getIntent().getStringExtra("host_ip");
        smartBrandDeviceModel=(SmartBrandDeviceModel)getIntent().getSerializableExtra("searchModel");
        setId();
    }

    private void setId() {
        Constants.activityAddDeviceConfirmActivity=this;
        toolbar=findViewById(R.id.toolbar);
        btnAddToroom=findViewById(R.id.btnAddToroom);
        btnExtingBridge=findViewById(R.id.btnExtingBridge);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("Select Type");
        btnAddToroom.setOnClickListener(this);
        btnExtingBridge.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==btnAddToroom){
            addCustomRoom();
        }else if(v==btnExtingBridge){
            Intent intent=new Intent(this,AddSMartDevicetoRoomActivity.class);
            intent.putExtra("searchModel",smartBrandDeviceModel);
            intent.putExtra("bridge_id",bridge_id);
            intent.putExtra("getBridge_name",getBridge_name);
            intent.putExtra("host_ip",host_ip);
            startActivity(intent);
        }
    }

    public void addCustomRoom() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
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

    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceConfirmActivity.this)) {
            Toast.makeText(AddDeviceConfirmActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            // Toast.makeText(getContext(),"Enter Room name",Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(AddDeviceConfirmActivity.this, "Please wait...", false);
        JSONObject object = new JSONObject();
        try {
            object.put("room_name", roomName.getText().toString());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(AddDeviceConfirmActivity.this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());

        String url = ChatApplication.url + Constants.ADD_CUSTOME_ROOM;

        new GetJsonTask(AddDeviceConfirmActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(AddDeviceConfirmActivity.this,message);
                    ChatApplication.logDisplay("data is room "+result);
                    if (code == 200) {
                        dialog.dismiss();
                        JSONObject jsonObject=new JSONObject(result.toString());
                        getHueBridgeLightList(jsonObject.optString("data"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                        ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

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
            jsonObject.put("is_new",1);
            jsonObject.put("room_id",room_id);
            jsonObject.put("panel_id","");
            jsonObject.put("room_device_id","");
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
                        Constants.activityPhilipsHueBridgeDeviceListActivity.finish();
                        Intent intent=new Intent(AddDeviceConfirmActivity.this, Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }else {
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
