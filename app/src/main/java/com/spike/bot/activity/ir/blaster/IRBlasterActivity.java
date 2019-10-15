package com.spike.bot.activity.ir.blaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.adapter.irblaster.IRBlasterInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterInfoRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sagar on 31/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterActivity extends AppCompatActivity implements IRBlasterInfoAdapter.IRBlasterInfoClick,View.OnClickListener{

    private LinearLayout linear_progress;
    private RecyclerView mIRListView;
    private Button btn_delete_remote;
    private String mIRBlasterId,mIRBlasterModuleId,mRoomId;

    List<IRBlasterInfoRes.Data.IrBlasterList.RemoteList> irRemoteLists;
    List<IRBlasterInfoRes.Data.IrBlasterList.RemoteList.RemoteCommandList> irRemoteCommandLists;
    IRBlasterInfoRes.Data.IrBlasterList.RemoteList.RemoteCurrentStatusDetails irRemoteCurrentStatusLists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_blaster);

        getIntentData();
        bindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIRBlasterInfo();
    }

    private void bindView(){
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_delete_remote =  findViewById(R.id.btn_delete_remote);
        linear_progress = findViewById(R.id.linear_progress);
        mIRListView = findViewById(R.id.list_ir_sensor);
        mIRListView.setLayoutManager(new GridLayoutManager(this,1));
        btn_delete_remote.setOnClickListener(this);

    }

    private void getIntentData(){
        mIRBlasterId = getIntent().getStringExtra("SENSOR_ID");
        mRoomId = getIntent().getStringExtra("ROOM_ID");
        mIRBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_ID");
    }

    private void showProgress(){

        linear_progress.setVisibility(View.VISIBLE);
        mIRListView.setVisibility(View.GONE);
    }
    private void hideProgress(){
        linear_progress.setVisibility(View.GONE);
        mIRListView.setVisibility(View.VISIBLE);
    }

    /**
     * getIRBlasterInfo
     */
    private void getIRBlasterInfo(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(),IRBlasterActivity.this.getResources().getString( R.string.disconnect));
            return;
        }

        showProgress();

        String url = ChatApplication.url + Constants.GET_IR_BLASTER_INFO + "/"+mIRBlasterId;
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        IRBlasterInfoRes irBlasterInfoRes = Common.jsonToPojo(result.toString(),IRBlasterInfoRes.class);
                        irRemoteLists = irBlasterInfoRes.getData().getIrBlasterList().get(0).getRemoteList();

                        irRemoteCommandLists = irRemoteLists.get(0).getRemoteCommandList();
                        irRemoteCurrentStatusLists = irRemoteLists.get(0).getRemoteCurrentStatusDetails();

                        IRBlasterInfoAdapter irBlasterInfoAdapter = new IRBlasterInfoAdapter(irRemoteLists, IRBlasterActivity.this);
                        mIRListView.setAdapter(irBlasterInfoAdapter);

                    }else{
                        ChatApplication.showToast(getApplicationContext(),message);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Menu menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }else if(id == R.id.action_add){
            Intent intent = new Intent(this,IRRemoteAdd.class);
            intent.putExtra("SENSOR_ID",mIRBlasterId);
            intent.putExtra("IR_BLASTER_ID",mIRBlasterId);
            intent.putExtra("IR_BLASTER_MODULE_ID",mIRBlasterModuleId);
            intent.putExtra("ROOM_ID",mRoomId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onIRBlasterClick(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList) {

        Intent intent = new Intent(this,IRBlasterRemote.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("IR_BLASTER_ID",mIRBlasterId);
        bundle.putSerializable("IR_REMOTE_STATUS",irBlasterList);
        intent.putExtra("IR_BLASTER_MODULE_ID",mIRBlasterModuleId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onIRBlasterEdit(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList) {
        Intent intent = new Intent(this,IRBlasterEditActivity.class);
        intent.putExtra("ROOM_ID",mRoomId);
        intent.putExtra("REMOTE_NAME",irBlasterList.getRemoteCurrentStatusDetails().getRemoteName());
        intent.putExtra("REMOTE_ID",irBlasterList.getRemoteCurrentStatusDetails().getRemoteId());
        Bundle bundle = new Bundle();
        bundle.putSerializable("REMOTE_SPEED",irBlasterList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onIRBlasterDelete(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList) {
        showDialog(irBlasterList);
    }
    private void showDialog(final IRBlasterInfoRes.Data.IrBlasterList.RemoteList remoteList){
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure ?",new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteSchedule(remoteList);
            }
            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }


    private void deleteSchedule(IRBlasterInfoRes.Data.IrBlasterList.RemoteList remoteList){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(),IRBlasterActivity.this.getResources().getString( R.string.disconnect));
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("remote_id",remoteList.getRemoteCurrentStatusDetails().getRemoteId());
            object.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_REMOTE;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
                        ChatApplication.showToast(getApplicationContext(),message);
                        getIRBlasterInfo();

                    }else{
                        ChatApplication.showToast(getApplicationContext(),message);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete_remote:
                ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure ?",new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteRemote();
                    }
                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");

                break;
        }
    }

    /**
     * delete IR Blaster
     */
    private void deleteRemote(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(),IRBlasterActivity.this.getResources().getString( R.string.disconnect));
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("ir_blaster_id",mIRBlasterId);
            object.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants. DELETE_IR_BLASTER;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        ChatApplication.showToast(getApplicationContext(),message);
                        finish();

                    }else{
                        ChatApplication.showToast(getApplicationContext(),message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterActivity.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }
}
