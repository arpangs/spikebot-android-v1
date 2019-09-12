package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.WifiAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.listener.SocketListener;
import com.spike.bot.listener.WifiListner;
import com.spike.bot.model.NotificationList;
import com.spike.bot.model.RoomListModel;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.WifiModel;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 4/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class WifiListActivity extends AppCompatActivity implements WifiListner ,ConnectivityReceiver.ConnectivityReceiverWifi {

    public Toolbar toolbar;
    public RecyclerView recyclerWifi;

    public String wifiIP = "",moduleId="",roomId="",roomName="";
    public boolean isNetworkChange=false;
    ArrayList<WifiModel.WiFiList> arrayList = new ArrayList<>();

    /*Socket*/
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    ArrayList<RoomListModel> roomListarrayList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        ConnectivityReceiver.connectivityReceiverWifi=WifiListActivity.this;
        arrayList = (ArrayList<WifiModel.WiFiList>) getIntent().getSerializableExtra("arrayList");
        wifiIP = getIntent().getStringExtra("wifiIP");
        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Wi-Fi List");
        setUi();
    }

    private void setUi() {
        recyclerWifi = (RecyclerView) findViewById(R.id.recyclerWifi);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WifiListActivity.this);
        recyclerWifi.setLayoutManager(linearLayoutManager);
        WifiAdapter wifiAdapter = new WifiAdapter(WifiListActivity.this, arrayList, this);
        recyclerWifi.setAdapter(wifiAdapter);
        wifiAdapter.notifyDataSetChanged();

        boolean flag=WifiBlasterActivity.setMobileDataEnabled(WifiListActivity.this);
//        if(flag){
//            Toast.makeText(getApplicationContext(),"Please disble your mobile data",Toast.LENGTH_LONG);
//            WifiListActivity.this.finish();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //startTimer();
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {

            ActivityHelper.dismissProgressDialog();
            Toast.makeText(getApplicationContext(), "No New Device detected!", Toast.LENGTH_SHORT).show();
            //showAddSensorDialog("212121");
        }

    };

    /**
     * Display Alert dialog when found door or temp sensor config already configured
     *
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    private void showIRSensorDialog() {

        final Dialog irDialog = new Dialog(this);
        irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        irDialog.setContentView(R.layout.dialog_add_sensordoor);
        irDialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = (EditText) irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) irDialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = (TextView) irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) irDialog.findViewById(R.id.txt_sensor_name);
        TextView txtSelectRoom =  irDialog.findViewById(R.id.txtSelectRoom);

        dialogTitle.setText("Add IR Blaster");
        txt_sensor_name.setText("IR Name");
        txtSelectRoom.setText("Room name");

        edt_door_module_id.setText(moduleId);
        edt_door_module_id.setFocusable(false);

//        for (int i = 0; i < roomListarrayList.size(); i++) {
//            if(roomListarrayList.get(i).getRoom_id().equalsIgnoreCase(roomId)){
                roomNameList.add(roomName);
                roomIdList.add(roomId);
//            }
//
//        }

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getApplicationContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);
        sp_room_list.setEnabled(false);
        sp_room_list.setClickable(false);


        sp_room_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_cancel = (Button) irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = (Button) irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = (ImageView) irDialog.findViewById(R.id.iv_close);

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
                saveIRBlaster(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(),
                        sp_room_list);
            }
        });


        irDialog.show();

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

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.requestFocus();
            textInputEditText.setError("Enter IR Name");
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("ir_blaster_name", door_name);
            obj.put("ir_blaster_module_id", door_module_id);

            obj.put("room_id",roomId);
            obj.put("room_name", roomName);

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("obj : " + obj.toString());
        String url = "";

        url = ChatApplication.url + Constants.ADD_IR_BLASTER;

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Common.showToast(message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        Constants.activityWifi.finish();
                        WifiListActivity.this.finish();
                        //     getIRBlasterList();
                    } else {
                        Common.showToast(message);
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
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


    @Override
    public void wifiClick(final WifiModel.WiFiList wiFiList) {

//        boolean flag=WifiBlasterActivity.setMobileDataEnabled(WifiListActivity.this);
//        if(flag){
//            Toast.makeText(getApplicationContext(),"Please disable your mobile data",Toast.LENGTH_LONG);
//            WifiListActivity.this.finish();
//            return;
//        }

        final Dialog dialog = new Dialog(WifiListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_wifi_password);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        final CustomEditText edWifiPassword = (CustomEditText) dialog.findViewById(R.id.edWifiPassword);
        final CustomEditText edWifiIP = (CustomEditText) dialog.findViewById(R.id.edWifiIP);
        TextView txtSave = (TextView) dialog.findViewById(R.id.txtSave);
        TextView txtWifiName = (TextView) dialog.findViewById(R.id.txtWifiName);

        //edWifiPassword.setText("ccocco123456");

       // edWifiIP.setText(Constants.getGateway(this));
        edWifiIP.setSelection(edWifiPassword.getText().length());
        edWifiPassword.setSelection(edWifiPassword.getText().length());

        edWifiIP.setText(""+Constants.getuserIp(this));
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edWifiPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (edWifiPassword.getText().toString().length() <= 7) {
                    Toast.makeText(getApplicationContext(), "Password lenth atleast 8 char.", Toast.LENGTH_SHORT).show();
                } else if(edWifiIP.getText().toString().length()==0){
                    ChatApplication.showToast(WifiListActivity.this,"Please enter Gateway ip address.");
                }else if(!Patterns.IP_ADDRESS.matcher(edWifiIP.getText()).matches()){
                    ChatApplication.showToast(WifiListActivity.this,"Please enter valid ip address");
                }else {
                    InputMethodManager imm = (InputMethodManager) WifiListActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edWifiPassword.getWindowToken(), 0);
                    callWifiPasswordCheck(edWifiPassword.getText().toString(), wiFiList,edWifiIP.getText().toString(),dialog);
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void callWifiPasswordCheck(String s, WifiModel.WiFiList wiFiList, String edWifiIP, final Dialog dialog) {
        if (!ActivityHelper.isConnectingToInternet(WifiListActivity.this)) {
            Toast.makeText(WifiListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(WifiListActivity.this, "Please wait... ", false);
        String url = "http://" + wifiIP + "/wifisave?s=" + wiFiList.getNetworkName() + "&p=" + s+"&$="+edWifiIP;

        ChatApplication.logDisplay("url is "+url);
        new GetJsonTask(WifiListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(final JSONObject result) {

                ChatApplication.logDisplay( "ir blaster is found result " + result.toString());

                try {
                    if (result != null) {
                        if (result.optString("response").equalsIgnoreCase("success")) {
                            dialog.dismiss();
                            Constants.isWifiConnectSave=true;
                            moduleId=result.optString("moduleId");
                            if(moduleId.length()>1){
                                isNetworkChange=true;
                                showIRSensorDialog();
                            }
                        }else {
                            ChatApplication.showToast(WifiListActivity.this,""+result.optString("response"));
                        }
                    }else {
                        ChatApplication.showToast(WifiListActivity.this,"Please try again later.");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(WifiListActivity.this,"Please try again later.");
                ChatApplication.logDisplay( "ir blaster is found result error " + error.toString());
                //found
                ActivityHelper.dismissProgressDialog();
                //  intentBlaster();
              //  Toast.makeText(WifiListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Constants.isWifiConnectSave=false;
            //roomListGet();
        }
    }

    @Override
    protected void onDestroy() {
        Constants.isWifiConnect=false;
        Constants.isWifiConnectSave=false;
        super.onDestroy();
    }
}
