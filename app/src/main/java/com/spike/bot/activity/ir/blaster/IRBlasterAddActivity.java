package com.spike.bot.activity.ir.blaster;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.SensorUnassignedActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.irblaster.IRBlasterAddAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.SensorUnassignedRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddActivity extends AppCompatActivity implements IRBlasterAddAdapter.BlasterAction{

    private Socket mSocket;
    private RecyclerView mBlasterList;
    private LinearLayout mEmptyView;
    public FloatingActionButton floatingActionButton;
    public static int SENSOR_TYPE_IR = 3;

    private IRBlasterAddAdapter irBlasterAddAdapter;
    private List<IRBlasterAddRes.Data.IrList> irList;

    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    List<IRBlasterAddRes.Data.RoomList> roomLists;
    private Dialog mDialog;
    EditText mBlasterName;
    Dialog irDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_blaster_add);

        bindView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isWifiConnect=false;
        Constants.isWifiConnectSave=false;
        startSocketConnection();
        getIRBlasterList();
    }

    @SuppressLint("RestrictedApi")
    private void bindView(){
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("IR Blaster List");
        mBlasterList =  findViewById(R.id.list_blaster);
        floatingActionButton =  findViewById(R.id.fab);
        mBlasterList.setLayoutManager(new GridLayoutManager(this,1));

        mEmptyView =  findViewById(R.id.txt_empty_blaster);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(SENSOR_TYPE_IR);
            }
        });

    }

    public void startSocketConnection(){
        ChatApplication app = (ChatApplication) getApplication();
        if(mSocket!=null && mSocket.connected()){
            return;
        }
        mSocket = app.getSocket();
        if(mSocket!=null){
            mSocket.on("configureIRBlaster",configureIRBlaster);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSocket!=null) {
            mSocket.off("configureIRBlaster",configureIRBlaster);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSocket!=null) {
            mSocket.off("configureIRBlaster",configureIRBlaster);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();

        if (id == R.id.action_edit_dots) {
            showOptionDialog(SENSOR_TYPE_IR);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener configureIRBlaster = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null){
                        try {

                            ActivityHelper.dismissProgressDialog();

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }

                            roomIdList.clear();
                            roomNameList.clear();

                            JSONObject object = new JSONObject(args[0].toString());

                            String message = object.getString("message");

                            String ir_module_id = object.getString("ir_blaster_module_id");

                            if(TextUtils.isEmpty(message)){

                                JSONArray jsonArray = object.getJSONArray("room_list");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject objectRoom = jsonArray.getJSONObject(i);
                                    String room_id = objectRoom.getString("room_id");
                                    String room_name = objectRoom.getString("room_name");

                                    roomIdList.add(room_id);
                                    roomNameList.add(room_name);
                                }

                            }

                            ActivityHelper.dismissProgressDialog();

                            if(TextUtils.isEmpty(message)){
                                showIRSensorDialog(ir_module_id);
                            }else{
                                showConfigAlert(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    /**
     * Display Alert dialog when found door or temp sensor config already configured
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showIRSensorDialog(String ir_module_id){

        if(irDialog == null){
            irDialog = new Dialog(this);
            irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            irDialog.setContentView(R.layout.dialog_add_sensordoor);
            irDialog.setCanceledOnTouchOutside(false);
        }

        final EditText edt_door_name =  irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id =  irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list =  irDialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = irDialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add IR Blaster");
        txt_sensor_name.setText("IR Name");

        edt_door_module_id.setText(ir_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getApplicationContext(),roomNameList,1,false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel =  irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save =  irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close =  irDialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irDialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIRBlaster(irDialog,edt_door_name,edt_door_name.getText().toString(),edt_door_module_id.getText().toString(), sp_room_list );
            }
        });

        if(!irDialog.isShowing()){
            irDialog.show();
        }

    }
    /**
     * save IR Blaster
     * @param dialog
     * @param textInputEditText
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     */
    private void saveIRBlaster(final Dialog dialog, EditText textInputEditText, String door_name,
                               String door_module_id, Spinner sp_room_list){

        if(TextUtils.isEmpty(textInputEditText.getText().toString())){
            textInputEditText.requestFocus();
            textInputEditText.setError("Enter IR Name");
            return;
        }

        if(!ActivityHelper.isConnectingToInternet(getApplicationContext())){
            ChatApplication.showToast(getApplicationContext(),IRBlasterAddActivity.this.getResources().getString( R.string.disconnect));
            return;
        }


        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("ir_blaster_name", door_name);
            obj.put("ir_blaster_module_id",door_module_id);

            obj.put("room_id",roomIdList.get(room_pos));
            obj.put("room_name",roomNameList.get(room_pos));

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("obj : " + obj.toString());
        String url = "";

        url = ChatApplication.url + Constants.ADD_IR_BLASTER;

        new GetJsonTask(this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        if(!TextUtils.isEmpty(message)){
                            Common.showToast(message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                        getIRBlasterList();
                    }
                    else{
                        Common.showToast(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void showOptionDialog(final int sensor_type){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle =  dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Sensor Type");

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel =  dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing =  dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(IRBlasterAddActivity.this,WifiBlasterActivity.class);
                startActivity(intent);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isUnassignedDoorSensor(sensor_type);

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(!dialog.isShowing()){
            dialog.show();
        }

    }
    private void isUnassignedDoorSensor(final int isDoorSensor){

        String url = "";
        url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir

        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result : " + result.toString());

                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(),SensorUnassignedRes.class);

                if(sensorUnassignedRes.getCode() == 200){

                    if(sensorUnassignedRes.getData()!=null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0){
                        Intent intent = new Intent(IRBlasterAddActivity.this, SensorUnassignedActivity.class);
                        ChatApplication.logDisplay("Type isDoorSensor : " + isDoorSensor);
                        intent.putExtra("isDoorSensor",isDoorSensor);
                        startActivity(intent);
                    }else{
                        ChatApplication.showToast(getApplicationContext(),sensorUnassignedRes.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
            }
        }).execute();


    }

    //startTimer();
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(),"No New Device detected!");
        }

    };

    /**
     * get IR Blaster list
     */
    private void getIRBlasterList(){

        if(!ActivityHelper.isConnectingToInternet(this)){
            ChatApplication.showToast(getApplicationContext(),IRBlasterAddActivity.this.getResources().getString( R.string.disconnect));
            return;
        }

        String URL = ChatApplication.url + Constants.GET_IR_BLASTER_LIST;

        if(irList!=null){
            irList.clear();
        }

        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        new GetJsonTask(this, URL, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result : " + result.toString());

                IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(),IRBlasterAddRes.class);
                if(irBlasterAddRes.getCode() == 200){
                    irList  = irBlasterAddRes.getData().getIrList();
                    roomLists = irBlasterAddRes.getData().getRoomList();

                    irBlasterAddAdapter = new IRBlasterAddAdapter(irList,IRBlasterAddActivity.this);
                    mBlasterList.setAdapter(irBlasterAddAdapter);
                    irBlasterAddAdapter.notifyDataSetChanged();

                }else if(irBlasterAddRes.getCode() == 301){
                    irList = new ArrayList<>();
                    irList.clear();
                    IRBlasterAddAdapter irBlasterAddAdapter = new IRBlasterAddAdapter(irList,IRBlasterAddActivity.this);
                    mBlasterList.setAdapter(irBlasterAddAdapter);
                    irBlasterAddAdapter.notifyDataSetChanged();
                    Common.showToast(irBlasterAddRes.getMessage());
                }else{
                    Common.showToast(irBlasterAddRes.getMessage());
                }

                if(irList.size() == 0 ){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mBlasterList.setVisibility(View.GONE);
                }else{
                    mEmptyView.setVisibility(View.GONE);
                    mBlasterList.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong));
            }
        }).execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onEdit(int position, IRBlasterAddRes.Data.IrList ir) {
        showBlasterEditDialog(ir);
    }

    @Override
    public void onDelete(int position, final IRBlasterAddRes.Data.IrList ir) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to delete "+ir.getIrBlasterName()+"?\n Note : All Schedules will be affected",new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteBlaster(ir.getIrBlasterId());
            }
            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void deleteBlaster(String irBlasterId){

        if(!ActivityHelper.isConnectingToInternet(getApplicationContext())){
            Common.showToast(""+getString(R.string.error_connect));
            return;
        }

        String URL = ChatApplication.url + Constants.DELETE_IR_BLASTER;

        JSONObject object = new JSONObject();
        try {
            object.put("ir_blaster_id",""+irBlasterId);
            object.put("phone_id",""+ APIConst.PHONE_ID_VALUE);
            object.put("phone_type",""+APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, URL, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result : " + result.toString());

                try {
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if(!TextUtils.isEmpty(message)){
                        Common.showToast(message);
                    }
                    if(code.equalsIgnoreCase("200")){
                        getIRBlasterList(); //update ir blaster list after delete blaster success
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong));
            }
        }).execute();

    }


    /**
     * Display IR Blaster Edit Dialog
     * Create Singleton class for prevent multiple instances of Dialog
     */
    private Dialog getDialogContext(){
        if(mDialog == null){
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(true);
            mDialog.setContentView(R.layout.dialog_ir_blaster_edit);
        }
        return mDialog;
    }

    /**
     *
     * @param ir
     */
    private void showBlasterEditDialog(final IRBlasterAddRes.Data.IrList ir){

        mDialog = getDialogContext();
        mBlasterName =  mDialog.findViewById(R.id.edt_blaster_name);
        final Spinner mRoomSpinner = mDialog.findViewById(R.id.blaster_room_spinner);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Remote Name max length to set only 25
        mBlasterName.setFilters(filterArray);

        Button btnCancel =  mDialog.findViewById(R.id.btn_cancel);
        Button btnSave = mDialog.findViewById(R.id.btn_save);
        ImageView btnClose =  mDialog.findViewById(R.id.iv_close);


        mBlasterName.setText(ir.getIrBlasterName());
        mBlasterName.setSelection(mBlasterName.getText().length());
        final ArrayAdapter roomAdapter = new ArrayAdapter(this, R.layout.spinner, roomLists);
        mRoomSpinner.setAdapter(roomAdapter);

        for (int i = 0; i < roomLists.size(); i++) {
            IRBlasterAddRes.Data.RoomList room = roomLists.get(i);
            if(room.getRoomName().equalsIgnoreCase(ir.getRoomName())){
                mRoomSpinner.setSelection(i);
                break;
            }
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IRBlasterAddRes.Data.RoomList roomList = (IRBlasterAddRes.Data.RoomList) mRoomSpinner.getSelectedItem();
                updateBlaster(mDialog,mBlasterName,roomList,ir.getIrBlasterId());
            }
        });

        if(!mDialog.isShowing()){
            mDialog.show();
        }

    }

    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     *  @param mDialog
     * @param mBlasterName
     * @param roomList
     * @param irBlasterId
     */
    private void updateBlaster(final Dialog mDialog, EditText mBlasterName, IRBlasterAddRes.Data.RoomList roomList, String irBlasterId){

        if(!ActivityHelper.isConnectingToInternet(getApplicationContext())){
            Common.showToast(""+getString(R.string.error_connect));
            return;
        }

        if(TextUtils.isEmpty(mBlasterName.getText().toString().trim())){
            mBlasterName.requestFocus();
            mBlasterName.setError("Enter Blaster Name");
            return;
        }

        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        JSONObject object = new JSONObject();
        try {
            object.put("ir_blaster_name",mBlasterName.getText().toString().trim());
            object.put("ir_blaster_id",""+irBlasterId);
            object.put("room_id",""+roomList.getRoomId());
            object.put("room_name",""+roomList.getRoomName());
            object.put("phone_id",""+ APIConst.PHONE_ID_VALUE);
            object.put("phone_type",""+APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("Object : " + object.toString());

        if(mBlasterName!=null){
            hideSoftKeyboard(mBlasterName);
        }

        String URL = ChatApplication.url + Constants.UPDATE_IR_BLASTER;

        new GetJsonTask(this, URL, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("result : " + result.toString());

                try {
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if(!TextUtils.isEmpty(message)){
                        Common.showToast(message);
                    }
                    if(code.equalsIgnoreCase("200")){
                        mDialog.dismiss();
                        getIRBlasterList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
                }
            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong));
            }
        }).execute();

    }

}
