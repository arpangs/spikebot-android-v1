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
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.Log;
import com.spike.bot.model.IRDeviceDetailsRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 1/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteAdd extends AppCompatActivity implements View.OnClickListener,IRBlasterAddListAdapter.IRDeviceClikListener{

    private LinearLayout linear_progress,ll_sensor_list;
    private RecyclerView mIRListView;
    private IRBlasterAddListAdapter irBlasterAddAdapter;
    private String mIrBlasterId;

    private List<IRDeviceDetailsRes.Data.Devicelist> mIRDeviceList;
    private String mIRBlasterModuleId,mRoomId,roomName="",roomId="";

    private Spinner mSpinnerBlaster, mSpinnerRoom;
    private TextView mRoomText,txtNoBlaster;
    boolean flagisBlaster=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_add);

        roomName=getIntent().getStringExtra("roomName");
        roomId=getIntent().getStringExtra("roomId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Add Remote");
        getSupportActionBar().setTitle("Add Remote");
       // syncIntent();

        bindView();

        getIRDeviceDetails();

    }

    private void syncIntent(){

        mIRBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_MODULE_ID");
        mIrBlasterId = getIntent().getStringExtra("SENSOR_ID");
        mRoomId = getIntent().getStringExtra("ROOM_ID");
    }

    private void bindView(){

        linear_progress = (LinearLayout) findViewById(R.id.linear_progress);
        ll_sensor_list  = (LinearLayout) findViewById(R.id.ll_sensor_list);

        mSpinnerBlaster = (Spinner) findViewById(R.id.remote_add_blaster_spinner);
        mSpinnerRoom = (Spinner) findViewById(R.id.remote_add_room_spinner);

        mRoomText = (TextView) findViewById(R.id.remote_add_room_txt);
        txtNoBlaster = (TextView) findViewById(R.id.txtNoBlaster);
        mRoomText.setText(""+roomName);

        mIRListView = (RecyclerView) findViewById(R.id.list_ir_sensor_add);
        mIRListView.setLayoutManager(new GridLayoutManager(this,3));
    }

    private String mRemoteRoomId;
    private List<IRDeviceDetailsRes.Data.IrList> irLists;
    private void getIRDeviceDetails(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();

        String url = ChatApplication.url + Constants.GET_IR_DEVICE_TYPE_LIST;

        irLists = new ArrayList<>();
        irLists.clear();

        ChatApplication.logDisplay("url is "+url);

        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                hideProgress();

                // Log.d(TAG,"onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        Log.d("IRDeviceDetails","remote res : " + result.toString());
                        IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(),IRDeviceDetailsRes.class);
                        mIRDeviceList = irDeviceDetailsRes.getData().getDevicelist();

                        if(mIRDeviceList.size()>0){
                            irBlasterAddAdapter = new IRBlasterAddListAdapter(mIRDeviceList,IRRemoteAdd.this);
                            mIRListView.setAdapter(irBlasterAddAdapter);

                        }

                        irLists = irDeviceDetailsRes.getData().getIrList();
                        List<IRDeviceDetailsRes.Data.RoomList> roomLists = irDeviceDetailsRes.getData().getRoomList();

                        //If not IR Blaster then add default String add in Spinner

                        if(irLists.size() == 0){
                            IRDeviceDetailsRes.Data.IrList irEmpty = new IRDeviceDetailsRes.Data.IrList();
                            irEmpty.setRoomName("");
                            irEmpty.setIrBlasterId("");
                            irEmpty.setIrBlasterModuleId("");
                            irEmpty.setIrBlasterName("No IR Blaster");
                            irEmpty.setRoomId("");
                            irLists.add(0,irEmpty);
                        }

                        ArrayAdapter customBlasterAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, irLists);
                        mSpinnerBlaster.setAdapter(customBlasterAdapter);

                        //ArrayAdapter customRoomAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, roomLists);
                       // mSpinnerRoom.setAdapter(customRoomAdapter);
                        if(irLists.size() > 0){

                           for(int i=0; i<irLists.size(); i++){
                               if(roomId.equals(irLists.get(i).getRoomId())){
                                   mSpinnerBlaster.setSelection(i);
                                   roomId=irLists.get(i).getRoomId();
                                   flagisBlaster=true;
                                   break;
                               }
                           }

                           if(flagisBlaster==false){
                               roomId="";
                               txtNoBlaster.setVisibility(View.VISIBLE);
                               mSpinnerBlaster.setVisibility(View.GONE);
                               txtNoBlaster.setOnClickListener(IRRemoteAdd.this);
                               mRoomText.setText("");
                           }
                        }

                        mSpinnerBlaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                IRDeviceDetailsRes.Data.IrList irList = (IRDeviceDetailsRes.Data.IrList) mSpinnerBlaster.getSelectedItem();
                                if(irList.getRoomName().equalsIgnoreCase("No IR Blaster")){
                                   // mRoomText.setText("");
                                }else{
                                    mRoomText.setText(""+irList.getRoomName());
                                    roomId=irList.getRoomId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }


                    if(irLists!=null && irLists.size() == 0){

                        IRDeviceDetailsRes.Data.IrList irEmpty = new IRDeviceDetailsRes.Data.IrList();
                        irEmpty.setRoomName("");
                        irEmpty.setIrBlasterId("");
                        irEmpty.setIrBlasterModuleId("");
                        irEmpty.setIrBlasterName("No IR Blaster");
                        irEmpty.setRoomId("");
                        irLists.add(0,irEmpty);

                        ArrayAdapter customBlasterAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, irLists);
                        mSpinnerBlaster.setAdapter(customBlasterAdapter);
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

    private void showProgress(){

        linear_progress.setVisibility(View.VISIBLE);
        mIRListView.setVisibility(View.GONE);
    }
    private void hideProgress(){
        linear_progress.setVisibility(View.GONE);
        mIRListView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIRDeviceClick(IRDeviceDetailsRes.Data.Devicelist devicelist) {
        //call listing all device on off screen

        if(roomId.equalsIgnoreCase("")){
           ChatApplication.showToast(IRRemoteAdd.this,"Please select Ir Blaster");
            return;
        }
        IRDeviceDetailsRes.Data.IrList irList = (IRDeviceDetailsRes.Data.IrList) mSpinnerBlaster.getSelectedItem();
        IRDeviceDetailsRes.Data.RoomList room = (IRDeviceDetailsRes.Data.RoomList) mSpinnerRoom.getSelectedItem();

        Intent intent = new Intent(this,IRRemoteBrandListActivity.class);
       // intent.putExtra("ROOM_ID",room.getRoomId());
        intent.putExtra("REMOTE_NAME",devicelist.getDeviceType());
        intent.putExtra("BLASTER_NAME",irList.getIrBlasterName());
        /*intent.putExtra("ROOM_NAME",irList.getRoomName());
        intent.putExtra("ROOM_ID",irList.getRoomId());*/
        intent.putExtra("ROOM_NAME",mRoomText.getText().toString());
        intent.putExtra("ROOM_ID",""+roomId);
        intent.putExtra("SENSOR_ID",irList.getIrBlasterId());
        intent.putExtra("IR_DEVICE_ID",""+devicelist.getDeviceId());
        intent.putExtra("IR_DEVICE_TYPE",""+devicelist.getDeviceType());
        intent.putExtra("IR_BLASTER_MODULE_ID",irList.getIrBlasterModuleId());
        startActivityForResult(intent,Constants.REMOTE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REMOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
           finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==txtNoBlaster){
            txtNoBlaster.setVisibility(View.GONE);
            mSpinnerBlaster.setVisibility(View.VISIBLE);
            mSpinnerBlaster.performClick();
        }
    }
}
//No Ir blaster added in this room