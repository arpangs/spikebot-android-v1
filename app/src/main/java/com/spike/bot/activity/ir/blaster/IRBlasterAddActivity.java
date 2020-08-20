package com.spike.bot.activity.ir.blaster;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AllUnassignedPanel;
import com.spike.bot.activity.Curtain.CurtainActivity;
import com.spike.bot.activity.Sensor.SensorUnassignedActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.irblaster.IRBlasterAddAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.SensorUnassignedRes;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddActivity extends AppCompatActivity implements IRBlasterAddAdapter.BlasterAction {

    private Socket mSocket;
    private RecyclerView mBlasterList;
    private LinearLayout mEmptyView;
    public FloatingActionButton floatingActionButton;
    public static int SENSOR_TYPE_IR = 3;

    private IRBlasterAddAdapter irBlasterAddAdapter;
    private List<IRBlasterAddRes.Datum> irList;
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray=new ArrayList<>();
    ArrayList<String> roomListString=new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
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
        roomListArray.clear();
        roomListString.clear();
        Constants.isWifiConnect = false;
        Constants.isWifiConnectSave = false;
        startSocketConnection();
        getIRBlasterList();
    }

    @SuppressLint("RestrictedApi")
    private void bindView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("IR Blaster List");
        mBlasterList = findViewById(R.id.list_blaster);
        floatingActionButton = findViewById(R.id.fab);
        mBlasterList.setLayoutManager(new GridLayoutManager(this, 1));

        mEmptyView = findViewById(R.id.txt_empty_blaster);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(SENSOR_TYPE_IR);
            }
        });

    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();
        if (mSocket != null) {
            mSocket.on("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        MenuItem txtmenuadd = menu.findItem(R.id.action_add_text);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        txtmenuadd.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_dots) {
            showOptionDialog(SENSOR_TYPE_IR);
            return true;
        }

        if (id == R.id.action_add_text) {
            showOptionDialogIr(SENSOR_TYPE_IR);
            return true;
        }

        if (id == R.id.action_add) {
            showOptionDialogIr(SENSOR_TYPE_IR);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* show dialog for sync device*/
    private void showOptionDialogIr(final int sensorType) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle =  dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("IR Blaster");

        Button btn_sync =  dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign =  dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel =  dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing =  dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(IRBlasterAddActivity.this, WifiBlasterActivity.class);
                //  intent.putExtra("roomId",""+room.getRoomId());
                //  intent.putExtra("roomName",""+room.getRoomName());
                intent.putExtra("roomListArray", roomListArray);
                startActivity(intent);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                unassignIntent("ir_blaster");

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /*unassign list*/
    public void unassignIntent(String type) {
        Intent intent = new Intent(this, AllUnassignedPanel.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /*getting socket fot ir blaster any update & change*/
    private Emitter.Listener configureIRBlaster = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
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

                            if (TextUtils.isEmpty(message)) {

                                JSONArray jsonArray = object.getJSONArray("room_list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objectRoom = jsonArray.getJSONObject(i);
                                    String room_id = objectRoom.getString("room_id");
                                    String room_name = objectRoom.getString("room_name");

                                    roomIdList.add(room_id);
                                    roomNameList.add(room_name);
                                }

                            }

                            ActivityHelper.dismissProgressDialog();

                            if (TextUtils.isEmpty(message)) {
                                showIRSensorDialog(ir_module_id);
                            } else {
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
     *
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /* show dialog for add ir blaster*/
    private void showIRSensorDialog(String ir_module_id) {

        if (irDialog == null) {
            irDialog = new Dialog(this);
            irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            irDialog.setContentView(R.layout.dialog_add_sensordoor);
            irDialog.setCanceledOnTouchOutside(false);
        }

        final EditText edt_door_name = irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = irDialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = irDialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add IR Blaster");
        txt_sensor_name.setText("IR Name");

        edt_door_module_id.setText(ir_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getApplicationContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = irDialog.findViewById(R.id.iv_close);

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
                saveIRBlaster(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list);
            }
        });

        if (!irDialog.isShowing()) {
            irDialog.show();
        }

    }

    /**
     * save IR Blaster
     *
     * @param dialog
     * @param textInputEditText
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     */
    private void saveIRBlaster(final Dialog dialog, EditText textInputEditText, String door_name,
                               String door_module_id, Spinner sp_room_list) {

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.requestFocus();
            textInputEditText.setError("Enter IR Name");
            return;
        }

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            ChatApplication.showToast(getApplicationContext(), IRBlasterAddActivity.this.getResources().getString(R.string.disconnect));
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        int room_pos = sp_room_list.getSelectedItemPosition();
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong1));

        SpikeBotApi.getInstance().saveIRBlaster(door_name, door_module_id, roomIdList.get(room_pos), roomNameList.get(room_pos), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Common.showToast(message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                        getIRBlasterList();
                    } else {
                        Common.showToast(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
    }

    private void showOptionDialog(final int sensor_type) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Sensor Type");

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(IRBlasterAddActivity.this, WifiBlasterActivity.class);
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

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /*call for ir sensor */
    private void isUnassignedDoorSensor(final int isDoorSensor) {

        String url = "";
        url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir

        ChatApplication.logDisplay("IR Unassigned" + " " + url);

        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result : " + result.toString());

                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(), SensorUnassignedRes.class);

                if (sensorUnassignedRes.getCode() == 200) {

                    if (sensorUnassignedRes.getData() != null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0) {
                        Intent intent = new Intent(IRBlasterAddActivity.this, SensorUnassignedActivity.class);
                        ChatApplication.logDisplay("Type isDoorSensor : " + isDoorSensor);
                        intent.putExtra("isDoorSensor", isDoorSensor);
                        startActivity(intent);
                    } else {
                        ChatApplication.showToast(getApplicationContext(), sensorUnassignedRes.getMessage());
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
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
        }

    };

    /*get room list */
    private void getRoomList(){
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getRoomList("room", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("un assign is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {}.getType();
                        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optString("data").toString(), type);


                        for(int i=0; i<roomListArray.size(); i++){
                            roomListString.add(roomListArray.get(i).getRoomName());
                        }

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onData_FailureResponse() {

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

    /**
     * get IR Blaster list
     */
    private void getIRBlasterList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), IRBlasterAddActivity.this.getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        if (irList != null) {
            irList.clear();
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getDeviceList("ir_blaster",new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try{
                    JSONObject result = new JSONObject(stringResponse);
                    IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(), IRBlasterAddRes.class);
                    if (irBlasterAddRes.getCode() == 200) {
                        irList = irBlasterAddRes.getData();
                        irList = irBlasterAddRes.getData();

                        irBlasterAddAdapter = new IRBlasterAddAdapter(irList, IRBlasterAddActivity.this);
                        mBlasterList.setAdapter(irBlasterAddAdapter);
                        irBlasterAddAdapter.notifyDataSetChanged();

                    } else if (irBlasterAddRes.getCode() == 301) {
                        irList = new ArrayList<>();
                        irList.clear();
                        IRBlasterAddAdapter irBlasterAddAdapter = new IRBlasterAddAdapter(irList, IRBlasterAddActivity.this);
                        mBlasterList.setAdapter(irBlasterAddAdapter);
                        irBlasterAddAdapter.notifyDataSetChanged();
                        Common.showToast(irBlasterAddRes.getMessage());
                    } else {
                        Common.showToast(irBlasterAddRes.getMessage());
                    }

                    if (irList.size() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mBlasterList.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mBlasterList.setVisibility(View.VISIBLE);
                    }

                    getRoomList();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onEdit(int position, IRBlasterAddRes.Datum ir) {
        showBottomSheetDialog(position,ir);
    }


    public void showBottomSheetDialog(int position, IRBlasterAddRes.Datum ir) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(IRBlasterAddActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        String irblastername =  irList.get(position).getDeviceName();
        ChatApplication.logDisplay("BLaster name" + " " + irblastername);


        txt_bottomsheet_title.setText("What would you like to do in" + " " + irblastername + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showBlasterEditDialog(position,ir);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete " + ir.getDeviceName() + "?\n\nNote : All Schedules will be affected", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteBlaster(ir.getDeviceId());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }



    @Override
    public void onDelete(int position, final IRBlasterAddRes.Datum ir) {
    }

    /*delete blaster */
    private void deleteBlaster(String irBlasterId) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Common.showToast("" + getString(R.string.error_connect));
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().deleteDevice("" + irBlasterId, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        Common.showToast(message);
                    }
                    if (code.equalsIgnoreCase("200")) {
                        getIRBlasterList(); //update ir blaster list after delete blaster success
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });
    }


    /**
     * Display IR Blaster Edit Dialog
     * Create Singleton class for prevent multiple instances of Dialog
     */
    private Dialog getDialogContext() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(true);
            mDialog.setContentView(R.layout.dialog_ir_blaster_edit);
        }
        return mDialog;
    }

    /**
     * @param ir
     */
    private void showBlasterEditDialog(int position,final IRBlasterAddRes.Datum ir) {

        mDialog = getDialogContext();
        mBlasterName = mDialog.findViewById(R.id.edt_blaster_name);
        final Spinner mRoomSpinner = mDialog.findViewById(R.id.blaster_room_spinner);
        mRoomSpinner.setEnabled(false);
        mRoomSpinner.setClickable(false);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Remote Name max length to set only 25
        mBlasterName.setFilters(filterArray);

        Button btnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button btnSave = mDialog.findViewById(R.id.btn_save);
        ImageView btnClose = mDialog.findViewById(R.id.iv_close);
        TextView txt_blaster_room_name = mDialog.findViewById(R.id.txt_blaster_room_name);
        txt_blaster_room_name.setFocusable(false);
        txt_blaster_room_name.setEnabled(false);



        String irblastername =  irList.get(position).getDeviceName();
        ChatApplication.logDisplay("BLaster name" + " " + irblastername);

        mBlasterName.setText(irblastername);
        mBlasterName.setSelection(mBlasterName.getText().length());
        final ArrayAdapter roomAdapter = new ArrayAdapter(this, R.layout.spinner, roomListString);
        mRoomSpinner.setAdapter(roomAdapter);

        try {
            for (int i = 0; i < roomListString.size(); i++) {
                if (roomListString.get(i).equalsIgnoreCase(ir.getRoom().getRoomName())) {
                    mRoomSpinner.setSelection(i);
                    txt_blaster_room_name.setText(ir.getRoom().getRoomName());
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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

                updateBlaster(mDialog, mBlasterName, ir.getDeviceId(),ir);
            }
        });

        if (!mDialog.isShowing()) {
            mDialog.show();
        }

    }

    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     * @param mDialog
     * @param mBlasterName
     * @param irBlasterId
     * @param ir
     */
    private void updateBlaster(final Dialog mDialog, EditText mBlasterName, String irBlasterId, IRBlasterAddRes.Datum ir) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Common.showToast("" + getString(R.string.error_connect));
            return;
        }

        if (TextUtils.isEmpty(mBlasterName.getText().toString().trim())) {
            mBlasterName.requestFocus();
            mBlasterName.setError("Enter Blaster Name");
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().updateDevice(irBlasterId,mBlasterName.getText().toString().trim(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try{
                    JSONObject result = new JSONObject(stringResponse);
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    if (code.equalsIgnoreCase("200")) {
                        mDialog.dismiss();
                        ir.setDeviceName(mBlasterName.getText().toString().trim());
                        irBlasterAddAdapter.notifyDataSetChanged();

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterAddActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

}
