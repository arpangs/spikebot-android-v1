package com.spike.bot.Beacon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.adapter.beacon.ScannerAddListAdapter;
import com.spike.bot.adapter.irblaster.IRBlasterAddListAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRDeviceDetailsRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddBeaconActivity extends AppCompatActivity implements View.OnClickListener, ScannerAddListAdapter.BeaconDeviceClickListener {

    private LinearLayout linear_beacon_progress, ll_scanner_list;
    private RecyclerView list_beacon_add;
    private AppCompatSpinner beacon_add_scanner_spinner;
    private TextView beacon_add_room_txt, txtNoScanner;
    ScannerAddListAdapter scanneraddlistadapter;
    private List<IRDeviceDetailsRes.Data> scannerLists = new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> mBeaconDeviceList = new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> mBeaconList = new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> tempBeaconList = new ArrayList<>();
    private List<String> scannerArraylist = new ArrayList<>();
    private String roomName = "", roomId = "", device_id;
    Dialog beacondialog;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_add);

        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Add Beacon");
        getSupportActionBar().setTitle("Add Beacon");

        ll_scanner_list = findViewById(R.id.ll_scanner_list);
        linear_beacon_progress = findViewById(R.id.linear_beacon_progress);
        beacon_add_scanner_spinner = findViewById(R.id.beacon_add_scanner_spinner);
        beacon_add_room_txt = findViewById(R.id.beacon_add_room_txt);
        txtNoScanner = findViewById(R.id.txtNoScanner);
        beacon_add_room_txt.setText("" + roomName);

        list_beacon_add = findViewById(R.id.list_beacon_add);

        getBeaconscannerDetails();


    }

    /* get Beacon Detail
       device_id;
     */

    private void getbeaconDeviceDetails(String device_id) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
     //   showProgress();
       // ActivityHelper.showProgressDialog(AddBeaconActivity.this, "Please Wait...", false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("device_id", device_id);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.beaconscannerscan;

    //    scannerLists = new ArrayList<>();
    //    scannerLists.clear();
        ChatApplication.logDisplay("beacon list url is " + url + " " + obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

             //   hideProgress();
             //   ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        try {
                            mBeaconList = new ArrayList<>();
                            mBeaconList.clear();
                            tempBeaconList = new ArrayList<>();
                            tempBeaconList.clear();
                            ChatApplication.logDisplay("beacon list res : " + result.toString());
                            IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                            mBeaconList = irDeviceDetailsRes.getData();
                            tempBeaconList = irDeviceDetailsRes.getData();

                            if (mBeaconList != null) {
                                ll_scanner_list.setVisibility(View.GONE);
                                list_beacon_add.setVisibility(View.VISIBLE);
                                if (mBeaconList.size() > 0) {
                                    for (int i = 0; i < mBeaconList.size(); i++) {
                                        if(!mBeaconList.get(i).getMac().equalsIgnoreCase(tempBeaconList.get(i).getMac())){
                                            mBeaconList.addAll(irDeviceDetailsRes.getData());
                                            scanneraddlistadapter.notifyDataSetChanged();
                                        } else if(mBeaconList.get(i).getMac().equalsIgnoreCase(tempBeaconList.get(i).getMac())
                                        && !mBeaconList.get(i).getSs().equalsIgnoreCase(tempBeaconList.get(i).getSs())){
                                            scanneraddlistadapter.notifyItemChanged(i);

                                        }
                                    }

                                    scanneraddlistadapter = new ScannerAddListAdapter(mBeaconList, AddBeaconActivity.this);
                                    list_beacon_add.setLayoutManager(new LinearLayoutManager(AddBeaconActivity.this));
                                    list_beacon_add.setAdapter(scanneraddlistadapter);
                                }
                            } else {
                                ll_scanner_list.setVisibility(View.VISIBLE);
                                list_beacon_add.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
               // hideProgress();
              //  ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }


    /* get details beacon scanner
     * device_type = beacon_scanner*/
    private void getBeaconscannerDetails() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();
        ActivityHelper.showProgressDialog(AddBeaconActivity.this, "Please Wait...", false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("device_type", "beacon_scanner");
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.devicefind;

        scannerLists = new ArrayList<>();
        scannerLists.clear();

        ChatApplication.logDisplay("url is " + url + " " + obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                hideProgress();
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        try {
                            mBeaconDeviceList.clear();
                            ChatApplication.logDisplay("beacon res : " + result.toString());
                            IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                            mBeaconDeviceList = irDeviceDetailsRes.getData();

                            if (mBeaconDeviceList.size() > 0) {
                                for (int i = 0; i < mBeaconDeviceList.size(); i++) {
                                    scannerArraylist.add(mBeaconDeviceList.get(i).getDeviceName());
                                }
                          /*  scanneraddlistadapter = new ScannerAddListAdapter(mBeaconDeviceList, AddBeaconActivity.this);
                            list_beacon_add.setAdapter(scanneraddlistadapter);*/
                            }

                            scannerLists = irDeviceDetailsRes.getData();
                            //If not IR Blaster then add default String add in Spinner
                            if (scannerLists.size() == 0) {
                                IRDeviceDetailsRes.Data irEmpty = new IRDeviceDetailsRes.Data();
                                irEmpty.setDeviceName("");
                                irEmpty.setDeviceName("No Scanner");
                                scannerLists.add(0, irEmpty);
                            }

                            if (scannerArraylist != null){
                               /* ArrayAdapter customBlasterAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, scannerArraylist);
                                customBlasterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                beacon_add_scanner_spinner.setAdapter(customBlasterAdapter);*/
                                try {
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddBeaconActivity.this, R.layout.spinner, scannerArraylist);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    beacon_add_scanner_spinner.setAdapter(dataAdapter);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            beacon_add_scanner_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (!scannerArraylist.get(position).equalsIgnoreCase("No Scanner")) {
                                        if (mBeaconDeviceList.get(position).getRoom() != null) {
                                            beacon_add_room_txt.setText("" + mBeaconDeviceList.get(position).getRoom().getRoomName());
                                            roomId = mBeaconDeviceList.get(position).getRoom().getRoomId();

                                            if (mBeaconDeviceList.get(position).getDeviceId() != null) {
                                                device_id = mBeaconDeviceList.get(position).getDeviceId();
                                            }
                                            if (device_id !=null) {

                                                handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //Do something after 20 seconds
                                                        handler.postDelayed(this, 500);
                                                        getbeaconDeviceDetails(device_id);
                                                        ChatApplication.logDisplay("timer" + device_id);
                                                    }
                                                }, 1000);

                                            }
                                        }

                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();


    }

    private void showProgress() {
        linear_beacon_progress.setVisibility(View.VISIBLE);
        list_beacon_add.setVisibility(View.GONE);
    }

    private void hideProgress() {
        linear_beacon_progress.setVisibility(View.GONE);
        list_beacon_add.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            handler.removeCallbacksAndMessages(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconDeviceClick(IRDeviceDetailsRes.Data devicelist) {

        if (roomId == null) {
            ChatApplication.showToast(AddBeaconActivity.this, "No Room found Please select another IR Blaster.");
            return;
        } else if (roomId.equalsIgnoreCase("")) {
            ChatApplication.showToast(AddBeaconActivity.this, "Please select Ir Blaster");
            return;
        }

        dialogAddBeacon(devicelist);


    }

    @Override
    public void onClick(View v) {
        if (v == txtNoScanner) {
            txtNoScanner.setVisibility(View.GONE);
            beacon_add_scanner_spinner.setVisibility(View.VISIBLE);
            beacon_add_scanner_spinner.performClick();
        }
    }

    /*add beacon dialog*/
    private void dialogAddBeacon(IRDeviceDetailsRes.Data devicelist) {
        beacondialog = new Dialog(AddBeaconActivity.this);
        beacondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        beacondialog.setCanceledOnTouchOutside(false);
        beacondialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = beacondialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = beacondialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = beacondialog.findViewById(R.id.edSensorName);

        txtInputSensor.setVisibility(View.VISIBLE);
        edSensorName.setVisibility(View.VISIBLE);

        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = beacondialog.findViewById(R.id.btn_save);
        Button btn_cancel = beacondialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = beacondialog.findViewById(R.id.iv_close);
        TextView tv_title = beacondialog.findViewById(R.id.tv_title);

        txtInputSensor.setHint("Enter Beacon Name");
        tv_title.setText("Set Beacon Name");


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beacondialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(AddBeaconActivity.this);
                beacondialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beacondialog.dismiss();
                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(AddBeaconActivity.this, "Please enter beacon name");
                } else {
                    handler.removeCallbacksAndMessages(null);
                    Intent intent = new Intent(AddBeaconActivity.this, BeaconConfigActivity.class);
                    intent.putExtra("SCANNER_NAME", beacon_add_scanner_spinner.getSelectedItem().toString());
                    intent.putExtra("ROOM_NAME", beacon_add_room_txt.getText().toString());
                    intent.putExtra("ROOM_ID", "" + roomId);
                    intent.putExtra("SENSOR_ID", mBeaconDeviceList.get(beacon_add_scanner_spinner.getSelectedItemPosition()).getDeviceId());
                    intent.putExtra("SCANNER_DEVICE_ID", "" + mBeaconDeviceList.get(beacon_add_scanner_spinner.getSelectedItemPosition()).getDeviceId());
                    intent.putExtra("DEVICE_TYPE", "beacon");
                    intent.putExtra("isMap",true);
                    intent.putExtra("isBeaconListAdapter",true);
                    intent.putExtra("BEACON_MODULE_ID", devicelist.getMac());
                    intent.putExtra("BEACON_DEVICE_NAME",edSensorName.getText().toString());
                    //startActivityForResult(intent, Constants.BEACON_REQUEST_CODE);
                    startActivity(intent);

                }
            }
        });

        beacondialog.show();

    }

}
