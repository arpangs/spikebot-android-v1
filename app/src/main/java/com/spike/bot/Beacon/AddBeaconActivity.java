package com.spike.bot.Beacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class AddBeaconActivity extends AppCompatActivity implements View.OnClickListener,ScannerAddListAdapter.BeaconDeviceClickListener{

    private LinearLayout linear_beacon_progress;
    private RecyclerView list_beacon_add;
    private Spinner beacon_add_scanner_spinner;
    private TextView beacon_add_room_txt, txtNoScanner;
    ScannerAddListAdapter scanneraddlistadapter;
    private List<IRDeviceDetailsRes.Data> scannerLists=new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> mBeaconDeviceList=new ArrayList<>();
    private List<String> scannerArraylist=new ArrayList<>();
    private String roomName = "", roomId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_add);

        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Add Beacon");
        getSupportActionBar().setTitle("Add Beacon");

        bindView();
        getBeacondeviceDetails();
    }

    private void bindView() {
        linear_beacon_progress =  findViewById(R.id.linear_beacon_progress);
        beacon_add_scanner_spinner = findViewById(R.id.beacon_add_scanner_spinner);
        beacon_add_room_txt =  findViewById(R.id.beacon_add_room_txt);
        txtNoScanner =  findViewById(R.id.txtNoScanner);
        beacon_add_room_txt.setText("" + roomName);

        list_beacon_add =  findViewById(R.id.list_beacon_add);
        list_beacon_add.setLayoutManager(new GridLayoutManager(this, 3));
    }

    /* get details beacon
     * device_type = beacon_scanner*/
    private void getBeacondeviceDetails() {

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

        ChatApplication.logDisplay("url is " + url+" "+obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                hideProgress();
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        ChatApplication.logDisplay("beacon res : " + result.toString());
                        IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                        mBeaconDeviceList = irDeviceDetailsRes.getData();

                        if (mBeaconDeviceList.size() > 0) {
                            for(int i=0; i<mBeaconDeviceList.size(); i++){
                                scannerArraylist.add(mBeaconDeviceList.get(i).getDeviceName());
                            }
                            scanneraddlistadapter = new ScannerAddListAdapter(mBeaconDeviceList, AddBeaconActivity.this);
                            list_beacon_add.setAdapter(scanneraddlistadapter);
                        }

                        scannerLists = irDeviceDetailsRes.getData();
                        //If not IR Blaster then add default String add in Spinner
                        if (scannerLists.size() == 0) {
                            IRDeviceDetailsRes.Data irEmpty = new IRDeviceDetailsRes.Data();
                            irEmpty.setDeviceName("");
                            irEmpty.setDeviceName("No Scanner");
                            scannerLists.add(0, irEmpty);
                        }

                        ArrayAdapter customBlasterAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, scannerArraylist);
                        beacon_add_scanner_spinner.setAdapter(customBlasterAdapter);

                        beacon_add_scanner_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (!scannerArraylist.get(position).equalsIgnoreCase("No Scanner")) {
                                    if( mBeaconDeviceList.get(position).getRoom()!=null){
                                        beacon_add_room_txt.setText("" + mBeaconDeviceList.get(position).getRoom().getRoomName());
                                        roomId = mBeaconDeviceList.get(position).getRoom().getRoomId();
                                    }

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconDeviceClick(IRDeviceDetailsRes.Data devicelist) {

        if (roomId==null) {
            ChatApplication.showToast(AddBeaconActivity.this, "No Room found Please select another IR Blaster.");
            return;
        }else if ( roomId.equalsIgnoreCase("")) {
            ChatApplication.showToast(AddBeaconActivity.this, "Please select Ir Blaster");
            return;
        }

        Intent intent = new Intent(this, BeaconActivity.class);
//        intent.putExtra("REMOTE_NAME", devicelist.getDeviceName());
        intent.putExtra("SCANNER_NAME", beacon_add_scanner_spinner.getSelectedItem().toString());
        intent.putExtra("ROOM_NAME", beacon_add_room_txt.getText().toString());
        intent.putExtra("ROOM_ID", "" + roomId);
        intent.putExtra("SENSOR_ID", mBeaconDeviceList.get(beacon_add_scanner_spinner.getSelectedItemPosition()).getDeviceId());
        intent.putExtra("SCANNER_DEVICE_ID", ""+mBeaconDeviceList.get(beacon_add_scanner_spinner.getSelectedItemPosition()).getDeviceId());
        intent.putExtra("SCANNER_DEVICE_TYPE", "beacon_scanner");
        intent.putExtra("BEACON_SCANNER_MODULE_ID", mBeaconDeviceList.get(beacon_add_scanner_spinner.getSelectedItemPosition()).getDeviceId());
        //startActivityForResult(intent, Constants.BEACON_REQUEST_CODE);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == txtNoScanner) {
            txtNoScanner.setVisibility(View.GONE);
            beacon_add_scanner_spinner.setVisibility(View.VISIBLE);
            beacon_add_scanner_spinner.performClick();
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.BEACON_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }*/
}
