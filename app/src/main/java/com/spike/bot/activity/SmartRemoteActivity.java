package com.spike.bot.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akhilpatoliya.floating_text_button.FloatingTextButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.SmartRemoteAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRDeviceDetailsRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 1/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SmartRemoteActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public RecyclerView recyclerRemoteList;
    TextView txtNodataFound;
    public FloatingTextButton floatingActionButton;
    public SmartRemoteAdapter smartRemoteAdapter;
    public List<IRDeviceDetailsRes.Data> arrayList = new ArrayList<>();

    private Socket mSocket;
    boolean addRoom = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_remote_activity);

        setUiId();
    }

    private void setUiId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Smart Remote List");
        recyclerRemoteList = findViewById(R.id.recyclerRemoteList);
        floatingActionButton = findViewById(R.id.fab_smart_remote);
        txtNodataFound = findViewById(R.id.txtNodataFound);

        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanelOption();
            }
        });


        getRemoteLIst();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerRemoteList.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        startSocketConnection();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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

        if (id == R.id.action_add_text) {
            showPanelOption();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();
    }

    private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SmartRemoteActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        JSONObject object = new JSONObject(args[0].toString());
                        ChatApplication.logDisplay("configureDevice is " + object);

                        //{"message":"","module_id":"1572951765645_ZE3EGTF4n","module_type":"smart_remote","total_devices":1,"room_list":[{"room_id":"1571986283894_cvUfVSpja","room_name":"Jhanvi M."},{"room_id":"1572851100674_c2sWW4eIR","room_name":"testRoom"}]}
                        String message = object.optString("message");

                        String module_id = object.optString("module_id");

                        ActivityHelper.dismissProgressDialog();

                        if (TextUtils.isEmpty(message)) {
                            showAddSensorDialog(module_id,object.optString("module_type"));
                        } else {
                            showConfigAlert(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /**
     * showAddSensorDialog
     *
     * @param door_module_id
     * @param
     */
    private void showAddSensorDialog(String door_module_id,String module_type) {

        final Dialog dialogtemp = new Dialog(SmartRemoteActivity.this);
        dialogtemp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogtemp.setContentView(R.layout.dialog_add_sensordoor);
        dialogtemp.setCanceledOnTouchOutside(false);

        final EditText edt_door_name =  dialogtemp.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id =  dialogtemp.findViewById(R.id.txt_module_id);

        TextView dialogTitle =  dialogtemp.findViewById(R.id.tv_title);
        TextView txt_sensor_name =  dialogtemp.findViewById(R.id.txt_sensor_name);
        LinearLayout linearListRoom = dialogtemp.findViewById(R.id.linearListRoom);
        linearListRoom.setVisibility(View.GONE);

        dialogTitle.setText("Add Smart Remote");
        txt_sensor_name.setText("Remote Name");

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        Button btn_cancel =  dialogtemp.findViewById(R.id.btn_door_cancel);
        Button btn_save =  dialogtemp.findViewById(R.id.btn_door_save);
        ImageView iv_close =  dialogtemp.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(SmartRemoteActivity.this);
                mSocket.off("configureDevice", configureDevice);
                dialogtemp.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.off("configureDevice", configureDevice);
                dialogtemp.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_door_name.getText().toString().length() == 0) {
                    ChatApplication.showToast(SmartRemoteActivity.this, "Please enter Smart remote name");
                } else {
                    ChatApplication.keyBoardHideForce(SmartRemoteActivity.this);
                    saveSensor(dialogtemp, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), module_type);
                    mSocket.off("configureDevice", configureDevice);
                }
            }
        });

//        if (!dialog.isShowing()) {
        dialogtemp.show();
//        }

    }

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


    private void saveSensor(final Dialog dialog, String door_name, String door_module_id ,String module_type) {

        if (!ActivityHelper.isConnectingToInternet(SmartRemoteActivity.this)) {
            Toast.makeText(SmartRemoteActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SmartRemoteActivity.this, "Please wait.", false);

       /* JSONObject obj = new JSONObject();
        try {
            obj.put("module_id", door_module_id);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("device_name", door_name);
            obj.put("module_type", module_type);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        String url = ChatApplication.url + Constants.deviceadd;

        /*new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(SmartRemoteActivity.this, message);
                        }

                        getRemoteLIst();
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                    } else {
                        ChatApplication.showToast(SmartRemoteActivity.this, message);
                    }
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
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().SaveSensor(door_name, door_module_id , module_type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(SmartRemoteActivity.this, message);
                        }
                        getRemoteLIst();
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                    } else {
                        ChatApplication.showToast(SmartRemoteActivity.this, message);
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

    private void showPanelOption() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txt_dialog_title = dialog.findViewById(R.id.txt_dialog_title);
        Button btn_sync = (Button) dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button) dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_add_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);

        btn_unaasign.setVisibility(View.GONE);
        btn_add_from_existing.setVisibility(View.GONE);
        txt_dialog_title.setText("Add New Smart Remote");
        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSocket != null) {
                    mSocket.on("configureDevice", configureDevice);
                }
                getconfigureData();
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

    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            Toast.makeText(getApplicationContext(), "No New Smart Remote detected!", Toast.LENGTH_SHORT).show();
        }

    };

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }

    private void getconfigureData() {
        if (!ActivityHelper.isConnectingToInternet(SmartRemoteActivity.this)) {
            Toast.makeText(SmartRemoteActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Searching Device attached ", false);
        startTimer();
        addRoom = true;

        String url = ChatApplication.url + Constants.deviceconfigure+"smart_remote";
        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void getRemoteLIst() {
        if (!ActivityHelper.isConnectingToInternet(SmartRemoteActivity.this)) {
            Toast.makeText(SmartRemoteActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        /*JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("device_type", "smart_remote");
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

       /* String url = ChatApplication.url + Constants.devicefind;
        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("remote res : " + result.toString());
                try {
                    IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                    arrayList = irDeviceDetailsRes.getData();

                    if (arrayList.size() > 0) {
                        txtNodataFound.setVisibility(View.GONE);
                        recyclerRemoteList.setVisibility(View.VISIBLE);
                        setAdapter();
                    }else {
                        txtNodataFound.setVisibility(View.VISIBLE);
                        recyclerRemoteList.setVisibility(View.VISIBLE);
                        ChatApplication.showToast(getApplicationContext(),"No data found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
            }
        }).execute();*/


        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().GetRemoteLIst(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("remote res : " + result.toString());
                    IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                    arrayList = irDeviceDetailsRes.getData();

                    if (arrayList.size() > 0) {
                        txtNodataFound.setVisibility(View.GONE);
                        recyclerRemoteList.setVisibility(View.VISIBLE);
                        setAdapter();
                    }else {
                        txtNodataFound.setVisibility(View.VISIBLE);
                        recyclerRemoteList.setVisibility(View.GONE);
                        ChatApplication.showToast(getApplicationContext(),"No data found.");
                    }
                } catch (Exception e) {
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

    private void setAdapter() {
        smartRemoteAdapter = new SmartRemoteAdapter(this, arrayList);
        recyclerRemoteList.setAdapter(smartRemoteAdapter);
        smartRemoteAdapter.notifyDataSetChanged();
    }

}
