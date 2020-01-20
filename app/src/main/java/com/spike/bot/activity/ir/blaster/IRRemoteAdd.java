package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.irblaster.IRBlasterAddListAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRDeviceDetailsRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 1/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteAdd extends AppCompatActivity implements View.OnClickListener, IRBlasterAddListAdapter.IRDeviceClikListener {

    private LinearLayout linear_progress;
    private RecyclerView mIRListView;
    private Spinner mSpinnerBlaster;
    private TextView mRoomText, txtNoBlaster;
    private IRBlasterAddListAdapter irBlasterAddAdapter;
    private List<IRDeviceDetailsRes.Data> irLists=new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> mIRDeviceList=new ArrayList<>();
    private List<String> blasterArraylist=new ArrayList<>();
    private String roomName = "", roomId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_add);

        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Add Remote");
        getSupportActionBar().setTitle("Add Remote");

        bindView();
        getIRDeviceDetails();
    }

    private void bindView() {
        linear_progress =  findViewById(R.id.linear_progress);
        mSpinnerBlaster = findViewById(R.id.remote_add_blaster_spinner);
        mRoomText =  findViewById(R.id.remote_add_room_txt);
        txtNoBlaster =  findViewById(R.id.txtNoBlaster);
        mRoomText.setText("" + roomName);

        mIRListView =  findViewById(R.id.list_ir_sensor_add);
        mIRListView.setLayoutManager(new GridLayoutManager(this, 3));
    }
    /* get details ir
    * device_type = ir_blaster*/
    private void getIRDeviceDetails() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();
        ActivityHelper.showProgressDialog(IRRemoteAdd.this, "Please Wait...", false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("device_type", "ir_blaster");
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.devicefind;

        irLists = new ArrayList<>();
        irLists.clear();

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

                        ChatApplication.logDisplay("remote res : " + result.toString());
                        IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                        mIRDeviceList = irDeviceDetailsRes.getData();

                        if (mIRDeviceList.size() > 0) {
                            for(int i=0; i<mIRDeviceList.size(); i++){
                                blasterArraylist.add(mIRDeviceList.get(i).getDeviceName());
                            }
                            irBlasterAddAdapter = new IRBlasterAddListAdapter(mIRDeviceList, IRRemoteAdd.this);
                            mIRListView.setAdapter(irBlasterAddAdapter);

                        }

                        irLists = irDeviceDetailsRes.getData();
                        //If not IR Blaster then add default String add in Spinner
                        if (irLists.size() == 0) {
                            IRDeviceDetailsRes.Data irEmpty = new IRDeviceDetailsRes.Data();
                            irEmpty.setDeviceName("");
                            irEmpty.setDeviceName("No IR Blaster");
                            irLists.add(0, irEmpty);
                        }

                        ArrayAdapter customBlasterAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, blasterArraylist);
                        mSpinnerBlaster.setAdapter(customBlasterAdapter);

                        mSpinnerBlaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (!blasterArraylist.get(position).equalsIgnoreCase("No IR Blaster")) {
                                        if( mIRDeviceList.get(position).getRoom()!=null){
                                            mRoomText.setText("" + mIRDeviceList.get(position).getRoom().getRoomName());
                                            roomId = mIRDeviceList.get(position).getRoom().getRoomId();
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
        linear_progress.setVisibility(View.VISIBLE);
        mIRListView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        linear_progress.setVisibility(View.GONE);
        mIRListView.setVisibility(View.VISIBLE);
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
    public void onIRDeviceClick(IRDeviceDetailsRes.Data devicelist) {
        //call listing all device on off screen

        if (roomId.equalsIgnoreCase("")) {
            ChatApplication.showToast(IRRemoteAdd.this, "Please select Ir Blaster");
            return;
        }

        Intent intent = new Intent(this, IRRemoteBrandListActivity.class);
//        intent.putExtra("REMOTE_NAME", devicelist.getDeviceName());
        intent.putExtra("BLASTER_NAME", mSpinnerBlaster.getSelectedItem().toString());
        intent.putExtra("ROOM_NAME", mRoomText.getText().toString());
        intent.putExtra("ROOM_ID", "" + roomId);
        intent.putExtra("SENSOR_ID", mIRDeviceList.get(mSpinnerBlaster.getSelectedItemPosition()).getDeviceId());
        intent.putExtra("IR_DEVICE_ID", ""+mIRDeviceList.get(mSpinnerBlaster.getSelectedItemPosition()).getDeviceId());
        intent.putExtra("IR_DEVICE_TYPE", "remote");
        intent.putExtra("IR_BLASTER_MODULE_ID", mIRDeviceList.get(mSpinnerBlaster.getSelectedItemPosition()).getDeviceId());
        startActivityForResult(intent, Constants.REMOTE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REMOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == txtNoBlaster) {
            txtNoBlaster.setVisibility(View.GONE);
            mSpinnerBlaster.setVisibility(View.VISIBLE);
            mSpinnerBlaster.performClick();
        }
    }
}