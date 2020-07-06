package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTaskVideo;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.WifiAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.listener.WifiListner;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.WifiModel;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 4/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class WifiListActivity extends AppCompatActivity implements WifiListner, ConnectivityReceiver.ConnectivityReceiverWifi {

    public Toolbar toolbar;
    public RecyclerView recyclerWifi;
    public String wifiIP = "", moduleId = "";
    public boolean isNetworkChange = false;

    /*Socket*/
    ArrayList<WifiModel.WiFiList> arrayList = new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        ConnectivityReceiver.connectivityReceiverWifi = WifiListActivity.this;
        arrayList = (ArrayList<WifiModel.WiFiList>) getIntent().getSerializableExtra("arrayList");
        wifiIP = getIntent().getStringExtra("wifiIP");
        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) getIntent().getSerializableExtra("roomListArray");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Wi-Fi List");
        setUi();
    }

    private void setUi() {
        recyclerWifi = findViewById(R.id.recyclerWifi);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WifiListActivity.this);
        recyclerWifi.setLayoutManager(linearLayoutManager);
        WifiAdapter wifiAdapter = new WifiAdapter(WifiListActivity.this, arrayList, this);
        recyclerWifi.setAdapter(wifiAdapter);
        wifiAdapter.notifyDataSetChanged();
        ChatApplication.logDisplay("roomListArray is " + roomListArray.size());
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

    /*add ir blaster dialog */
    private void showIRSensorDialog() {

        final Dialog irDialog = new Dialog(this);
        irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        irDialog.setContentView(R.layout.dialog_add_sensordoor);
        irDialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = irDialog.findViewById(R.id.sp_room_list);
        ImageView sp_drop_down = irDialog.findViewById(R.id.sp_drop_down);
        TextView dialogTitle = irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = irDialog.findViewById(R.id.txt_sensor_name);
        TextView txtSelectRoom = irDialog.findViewById(R.id.txtSelectRoom);

        dialogTitle.setText("Add IR Blaster");
        txt_sensor_name.setText("IR Name");
        txtSelectRoom.setText("Room List");

        edt_door_module_id.setText(moduleId);
        edt_door_module_id.setFocusable(false);

        roomNameList.clear();
        if (roomListArray.size() > 0) {
            for (int i = 0; i < roomListArray.size(); i++) {
                roomNameList.add(roomListArray.get(i).getRoomName());
            }
        }

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getApplicationContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = irDialog.findViewById(R.id.iv_close);

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_room_list.performClick();
            }
        });

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
                if (TextUtils.isEmpty(edt_door_name.getText().toString())) {
                    edt_door_name.requestFocus();
                    edt_door_name.setError("Enter IR Name");
                } else if (roomListArray.size() == 0) {
                    ChatApplication.showToast(WifiListActivity.this, "Please create room.");
                } else {
                    saveIRBlaster(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list);
                }
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

        ActivityHelper.showProgressDialog(this, "Please wait.", true);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addDevice(roomListArray.get(sp_room_list.getSelectedItemPosition()).getRoomId(), door_name, door_module_id, "ir_blaster", new DataResponseListener() {

            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
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


    @Override
    public void wifiClick(final WifiModel.WiFiList wiFiList) {

        final Dialog dialog = new Dialog(WifiListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_wifi_password);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        final CustomEditText edWifiPassword = dialog.findViewById(R.id.edWifiPassword);
        final CustomEditText edWifiIP = dialog.findViewById(R.id.edWifiIP);
        TextView txtSave = dialog.findViewById(R.id.txtSave);
        TextView txtWait = dialog.findViewById(R.id.txtWait);

        edWifiIP.setSelection(edWifiPassword.getText().length());
        edWifiPassword.setSelection(edWifiPassword.getText().length());

        edWifiIP.setText("" + Constants.getuserIp(this));
//        edWifiIP.setText("192.168.175.222");
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edWifiPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (edWifiPassword.getText().toString().length() <= 7) {
                    Toast.makeText(getApplicationContext(), "Password lenth atleast 8 char.", Toast.LENGTH_SHORT).show();
                } else if (edWifiIP.getText().toString().length() == 0) {
                    ChatApplication.showToast(WifiListActivity.this, "Please enter Gateway ip address.");
                } else if (!Patterns.IP_ADDRESS.matcher(edWifiIP.getText()).matches()) {
                    ChatApplication.showToast(WifiListActivity.this, "Please enter valid ip address");
                } else {
//                    setViewButton(txtSave,false);
//                    txtWait.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) WifiListActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edWifiPassword.getWindowToken(), 0);
                    callWifiPasswordCheck(txtSave, txtWait, edWifiPassword.getText().toString(), wiFiList, edWifiIP.getText().toString(), dialog);
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

    /*blaster wifi connection request
     * */
    private void callWifiPasswordCheck(TextView txtSave, TextView txtWait, String s, WifiModel.WiFiList wiFiList, String edWifiIP, final Dialog dialog) {
        if (!ActivityHelper.isConnectingToInternet(WifiListActivity.this)) {
            Toast.makeText(WifiListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(WifiListActivity.this, "Please wait... ", true);
        String url = "http://" + wifiIP + "/wifisave?s=" + wiFiList.getNetworkName() + "&p=" + s + "&$=" + edWifiIP;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTaskVideo(WifiListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(final JSONObject result) {
                ChatApplication.logDisplay("ir blaster is found result " + result.toString());

                try {
                    if (result != null) {
                        if (result.optString("response").equalsIgnoreCase("success")) {
//                            dialog.dismiss();
                            Constants.isWifiConnectSave = true;
                            moduleId = result.optString("moduleId");
                            if (moduleId.length() > 1) {
                                setSaveView(txtSave, txtWait, dialog);
                            }
                        } else {
                            ActivityHelper.dismissProgressDialog();
                            ChatApplication.showToast(WifiListActivity.this, "" + result.optString("response"));
                        }
                    } else {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(WifiListActivity.this, "Please try again later.");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityHelper.dismissProgressDialog();
                } finally {
//                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(WifiListActivity.this, "Please try again later.");
                ChatApplication.logDisplay("ir blaster is found result error " + error.toString());
                //found
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /*for blaster wifi off & last wifi connectiong wait ..
    some time wifi connectig few sec so timer cout adding
    * */
    private void setSaveView(TextView txtSave, TextView txtWait, Dialog dialog) {
        new CountDownTimer(2500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                ActivityHelper.dismissProgressDialog();
                isNetworkChange = true;
                showIRSensorDialog();
            }

        }.start();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Constants.isWifiConnectSave = false;
        }
    }

    @Override
    protected void onDestroy() {
        Constants.isWifiConnect = false;
        Constants.isWifiConnectSave = false;
        super.onDestroy();
    }
}
