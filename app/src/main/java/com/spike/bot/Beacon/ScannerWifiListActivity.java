package com.spike.bot.Beacon;

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
import com.spike.bot.activity.Camera.ImageZoomActivity;
import com.spike.bot.adapter.BeaconWifiAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.listener.WifiListner;
import com.spike.bot.model.CameraRecordingResmodel;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.WifiModel;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScannerWifiListActivity extends AppCompatActivity implements WifiListner, ConnectivityReceiver.ConnectivityReceiverWifi {

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

        ConnectivityReceiver.connectivityReceiverWifi = ScannerWifiListActivity.this;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ScannerWifiListActivity.this);
        recyclerWifi.setLayoutManager(linearLayoutManager);
        BeaconWifiAdapter wifiAdapter = new BeaconWifiAdapter(ScannerWifiListActivity.this, arrayList, this);
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

    /*add beacon scanner dialog */
    private void showBeaconscannerDialog() {

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

        dialogTitle.setText("Add Beacon Scanner");
        txt_sensor_name.setText("Scanner Name");
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
                    edt_door_name.setError("Enter Scanner Name");
                } else if (roomListArray.size() == 0) {
                    ChatApplication.showToast(ScannerWifiListActivity.this, "Please create room.");
                } else {
                    saveBeaconScanner(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list);
                }
            }
        });
        irDialog.show();
    }

    /**
     * save Beacon Scanner
     *
     * @param dialog
     * @param textInputEditText
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     */
    private void saveBeaconScanner(final Dialog dialog, EditText textInputEditText, String door_name,
                                   String door_module_id, Spinner sp_room_list) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", true);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveBeaconScanner(door_name,door_module_id,roomListArray.get(sp_room_list.getSelectedItemPosition()).getRoomId(), new DataResponseListener() {
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
                        ScannerWifiListActivity.this.finish();
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
                ActivityHelper.dismissProgressDialog();
            }
        });
    }


    @Override
    public void wifiClick(final WifiModel.WiFiList wiFiList) {

        final Dialog dialog = new Dialog(ScannerWifiListActivity.this);
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
                    Toast.makeText(getApplicationContext(), "Password length atleast 8 char.", Toast.LENGTH_SHORT).show();
                } else if (edWifiIP.getText().toString().length() == 0) {
                    ChatApplication.showToast(ScannerWifiListActivity.this, "Please enter Gateway ip address.");
                } else if (!Patterns.IP_ADDRESS.matcher(edWifiIP.getText()).matches()) {
                    ChatApplication.showToast(ScannerWifiListActivity.this, "Please enter valid ip address");
                } else {
//                    setViewButton(txtSave,false);
//                    txtWait.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) ScannerWifiListActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edWifiPassword.getWindowToken(), 0);
                    //   callWifiPasswordCheck(txtSave,txtWait,edWifiPassword.getText().toString(), wiFiList, edWifiIP.getText().toString(), dialog);
                    callPostdevCFG(txtSave, txtWait, edWifiPassword.getText().toString(), wiFiList, edWifiIP.getText().toString(), dialog);
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

    /* scanner wifi connection request
     * */
    private void callWifiPasswordCheck(TextView txtSave, TextView txtWait, String s, WifiModel.WiFiList wiFiList, String edWifiIP, final Dialog dialog) {
        if (!ActivityHelper.isConnectingToInternet(ScannerWifiListActivity.this)) {
            Toast.makeText(ScannerWifiListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(ScannerWifiListActivity.this, "Please wait... ", true);
        String url = "http://" + wifiIP + "/wifisave?s=" + wiFiList.getSSID() + "&p=" + s + "&$=" + edWifiIP;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTaskVideo(ScannerWifiListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(final JSONObject result) {
                ChatApplication.logDisplay("scanner is found result " + result.toString());

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
                            ChatApplication.showToast(ScannerWifiListActivity.this, "" + result.optString("response"));
                        }
                    } else {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(ScannerWifiListActivity.this, "Please try again later.");
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
                ChatApplication.showToast(ScannerWifiListActivity.this, "Please try again later.");
                ChatApplication.logDisplay("scanner is found result error " + error.toString());
                //found
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    public void callPostdevCFG(TextView txtSave, TextView txtWait, String s, WifiModel.WiFiList wiFiList, String edWifiIP, final Dialog dialog) {
        if (!ActivityHelper.isConnectingToInternet(ScannerWifiListActivity.this)) {
            Toast.makeText(ScannerWifiListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        String url1 = "mqtt://" + edWifiIP;
        /*   String URL[] = url1.split("http://");*/
        String spilturl = url1.replaceAll("http://", "mqtt://");

        ChatApplication.logDisplay("SPLIT URL" + " " + spilturl);
        JSONObject object = new JSONObject();
        try {
            object.put("SSID", wiFiList.getSSID());
            object.put("PSWD", s);
            object.put("URL", url1);
            object.put("CFG", "01");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActivityHelper.showProgressDialog(ScannerWifiListActivity.this, "Please wait...", false);

        String apiurl = "http://192.168.4.1/postdevcfg";
        ChatApplication.logDisplay("Beacon POST success" + " " + apiurl + " " + object.toString().replaceAll("\\\\", ""));


        new GetJsonTask(ScannerWifiListActivity.this, apiurl, "POST", object.toString().replaceAll("\\\\", ""), new ICallBack() {
            @Override
            public void onSuccess(final JSONObject result) {
                ChatApplication.logDisplay("scanner is found result" + result.toString());

                try {
                    if (result != null)
                    {
                        if (result.optString("message").equalsIgnoreCase("Success")) {
//                            dialog.dismiss();
                            Constants.isWifiConnectSave = true;
                            moduleId = result.optString("moduleId").toLowerCase();
                            // setSaveView(txtSave,txtWait,dialog);
                            if (moduleId.length() > 1) {
                                setSaveView(txtSave, txtWait, dialog);
                            }
                        } else {
                            ActivityHelper.dismissProgressDialog();
                            ChatApplication.showToast(ScannerWifiListActivity.this, "" + result.optString("response"));
                        }
                    } else {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.logDisplay("scanner is found result null" + result.toString());
                        ChatApplication.showToast(ScannerWifiListActivity.this, "Please try again later.");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay("scanner is found result error exception" + result.toString());
                    ActivityHelper.dismissProgressDialog();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    ChatApplication.logDisplay("scanner is found result error finally" + result.toString());
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {

                ChatApplication.showToast(ScannerWifiListActivity.this, "Failed response from device." + error);
                ChatApplication.logDisplay("scanner is found result error " + error);
                //found
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /*for scanner wifi off & last wifi connectiong wait ..
    some time wifi connecting few sec so timer count adding
    * */
    private void setSaveView(TextView txtSave, TextView txtWait, Dialog dialog) {
        new CountDownTimer(2500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                ActivityHelper.dismissProgressDialog();
                isNetworkChange = true;
                showBeaconscannerDialog();
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