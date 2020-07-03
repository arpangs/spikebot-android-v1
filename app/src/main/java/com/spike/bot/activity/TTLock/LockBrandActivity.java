package com.spike.bot.activity.TTLock;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AllUnassignedPanel;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.LockClickListener;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.model.YaleLockVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 2/9/19.
 * Gmail : vipul patel
 */
public class LockBrandActivity extends AppCompatActivity implements LockClickListener {

    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView recyclerViewLock;
    SmartLockBrandAdapter smartLockBrandAdapter;
    SmartLockOptionBrandAdapter smartLockOptionBrandAdapter;
    TTLockOptionBrandAdapter ttlockoptionadapter;
    TextView txtNodataFound;
    ArrayList<String> stringArrayList = new ArrayList<>();
    List<YaleLockVO.Data> yalelocklist = new ArrayList<>();
    YaleLockVO.Data yalelockVO;
    public boolean isFlagClick = false;
    MenuItem menuAdd;
    private Socket mSocket;
    Dialog dialog;
    public AddRoomDialog addRoomDialog;
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    int typeSync = 0;
    TextView label_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        setviewId();
    }

    private void setviewId() {
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Your Brand");

        fab.setVisibility(View.GONE);
        txtNodataFound = findViewById(R.id.txtNodataFound);
        recyclerViewLock = findViewById(R.id.recyclerSmartDevice);
        label_add = findViewById(R.id.label_add);

        label_add.setVisibility(View.GONE);
        setAdapter();
          stringArrayList.add("Bridge");
          stringArrayList.add("Lock");

        startSocketConnection();
    }

    private void setAdapter() {
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        smartLockBrandAdapter = new SmartLockBrandAdapter(this);
        recyclerViewLock.setAdapter(smartLockBrandAdapter);
        smartLockBrandAdapter.notifyDataSetChanged();
    }

    private void setAdapter1() {
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        smartLockOptionBrandAdapter = new SmartLockOptionBrandAdapter(this, yalelocklist, LockBrandActivity.this);
        recyclerViewLock.setAdapter(smartLockOptionBrandAdapter);
        smartLockOptionBrandAdapter.notifyDataSetChanged();
    }

    private void setAdapterTTlock(){
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        ttlockoptionadapter = new TTLockOptionBrandAdapter(this, stringArrayList);
        recyclerViewLock.setAdapter(ttlockoptionadapter);
        ttlockoptionadapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menuAdd = menu.findItem(R.id.action_add_text);
        MenuItem actionsave = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        actionsave.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            showOptionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*start socket connection*/
    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

  /*  private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LockBrandActivity.this.runOnUiThread(new Runnable() {
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
                            showAddSensorDialog(module_id, object.optString("module_type"));
                        } else {
                            showConfigAlert(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };*/

    /**
     * Socket Listner for configure devices
     */
    private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LockBrandActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        //message, gas_sensor_module_id,room_list
                        JSONObject object = new JSONObject(args[0].toString());
                        String message = object.getString("message");

                        ChatApplication.logDisplay("configureDevice is " + object);
                        String module_id = object.optString("module_id");
                        String module_type = object.optString("module_type");

                        if (TextUtils.isEmpty(object.getString("message"))) {

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
                            showLockSensor(module_id, module_type);
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


    /*dialog for sensor */
    private void showLockSensor(String door_module_id, String module_type) {

        if (dialog == null) {
            dialog = new Dialog(LockBrandActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = dialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = dialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add Yale Lock");
        txt_sensor_name.setText("Lock Name");


        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(LockBrandActivity.this, roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSocket.off("configureDevice", configureDevice);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSocket.off("configureDevice", configureDevice);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_door_name.getText().toString().length() == 0) {
                    ChatApplication.showToast(LockBrandActivity.this, "Please enter Yale lock name");
                } else {
                    ChatApplication.keyBoardHideForce(LockBrandActivity.this);
                    saveSensor(dialog, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list,module_type);
                    mSocket.off("configureDevice", configureDevice);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    /**
     * showAddSensorDialog
     *
     * @param door_module_id
     * @param
     */
    private void showAddSensorDialog(String door_module_id, String module_type) {

        final Dialog dialogtemp = new Dialog(LockBrandActivity.this);
        dialogtemp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogtemp.setContentView(R.layout.dialog_add_sensordoor);
        dialogtemp.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = dialogtemp.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = dialogtemp.findViewById(R.id.txt_module_id);

        TextView dialogTitle = dialogtemp.findViewById(R.id.tv_title);
        TextView txt_sensor_name = dialogtemp.findViewById(R.id.txt_sensor_name);
        LinearLayout linearListRoom = dialogtemp.findViewById(R.id.linearListRoom);
        linearListRoom.setVisibility(View.VISIBLE);

        dialogTitle.setText("Add Yale Lock");
        txt_sensor_name.setText("Lock Name");

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        Button btn_cancel = dialogtemp.findViewById(R.id.btn_door_cancel);
        Button btn_save = dialogtemp.findViewById(R.id.btn_door_save);
        ImageView iv_close = dialogtemp.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(LockBrandActivity.this);
                dialogtemp.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogtemp.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_door_name.getText().toString().length() == 0) {
                    ChatApplication.showToast(LockBrandActivity.this, "Please enter Yale lock name");
                } else {
                    ChatApplication.keyBoardHideForce(LockBrandActivity.this);
                   // saveSensor(dialogtemp, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(),spro ,module_type);
                }
            }
        });

//        if (!dialog.isShowing()) {
        dialogtemp.show();
//        }

    }

    /**
     * Display Alert dialog when found lock config already configured
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


    private void saveSensor(final Dialog dialog, String lock_name, String lock_module_id,  Spinner sp_room_list,String module_type) {

        if (!ActivityHelper.isConnectingToInternet(LockBrandActivity.this)) {
            Toast.makeText(LockBrandActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(LockBrandActivity.this, "Please wait.", false);
        int room_pos = sp_room_list.getSelectedItemPosition();
        SpikeBotApi.getInstance().addDevice(roomIdList.get(room_pos), lock_name,lock_module_id, module_type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(LockBrandActivity.this, message);
                        }

                        getLockList();
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                    } else {
                        ChatApplication.showToast(LockBrandActivity.this, message);
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
        });
    }

    /*searching device wait for 7 sec*/
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
        }

    };

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }

    /*
     * sync request */
    private void getconfigureyalelockRequest() {
        if (!ActivityHelper.isConnectingToInternet(LockBrandActivity.this)) {
            ChatApplication.showToast(LockBrandActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(LockBrandActivity.this, "Searching for new Yale Lock", false);
        startTimer();

        String url = ChatApplication.url + Constants.deviceconfigure + "yale_lock";

        new GetJsonTask(LockBrandActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("yale lock result is " + result);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void getLockList() {
        if (!ActivityHelper.isConnectingToInternet(LockBrandActivity.this)) {
            Toast.makeText(LockBrandActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().getDeviceList("lock",new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("remote res : " + result.toString());

                    YaleLockVO yalelockres = Common.jsonToPojo(result.toString(), YaleLockVO.class);
                    yalelocklist = yalelockres.getData();

                    if (yalelocklist.size() > 0) {
                        txtNodataFound.setVisibility(View.GONE);
                        recyclerViewLock.setVisibility(View.VISIBLE);
                        setAdapter1();
                    } else {
                        txtNodataFound.setVisibility(View.VISIBLE);
                        recyclerViewLock.setVisibility(View.GONE);
                        //  ChatApplication.showToast(getApplicationContext(),"No data found.");
                    }

                   /* IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                    arrayList = irDeviceDetailsRes.getData();

                    if (arrayList.size() > 0) {
                        txtNodataFound.setVisibility(View.GONE);
                        recyclerRemoteList.setVisibility(View.VISIBLE);
                        setAdapter();
                    }else {
                        txtNodataFound.setVisibility(View.VISIBLE);
                        recyclerRemoteList.setVisibility(View.VISIBLE);
                        ChatApplication.showToast(getApplicationContext(),"No data found.");
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*show sync dialog
     * sync means add new device .
     * Unassigned means already adding device but delete this device than its showing in Unassigned
     * Add From Existing means create cusotm panel
     *
     * */
    private void showOptionDialog() {

        final Dialog dialog = new Dialog(LockBrandActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Type");

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSocket != null) {
                    mSocket.on("configureDevice", configureDevice);
                }
                getconfigureyalelockRequest();
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(LockBrandActivity.this, AllUnassignedPanel.class);
                intent.putExtra("type", "yale_lock");
                startActivity(intent);
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

    @Override
    public void itemClicked(YaleLockVO.Data yalelockVO, String action) {
        String device_id = yalelockVO.getDeviceId();
        if (action.equalsIgnoreCase("edit")) {
            showBottomSheetDialog(yalelockVO);
        }
    }


    public void showBottomSheetDialog(YaleLockVO.Data yalelockVO) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(LockBrandActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + yalelockVO.getDeviceName() + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogEditName(yalelockVO.getDeviceId());
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        DeleteLockSensor(yalelockVO.getDeviceId());
                    }


                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }


    /*edit sensor dialog*/
    private void dialogEditName(String deviceid) {
        final Dialog dialog = new Dialog(LockBrandActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = dialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = (TextInputEditText) dialog.findViewById(R.id.edSensorName);
        txtInputSensor.setVisibility(View.VISIBLE);
        edSensorName.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);

//        if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("1")) {
        txtInputSensor.setHint("Enter lock name");
        tv_title.setText("Lock Name");
//        }
//        else if (doorSensorResModel.getDate().getDoorLists()[0].getDoor_subtype().equals("2")) {
//            txtInputSensor.setHint("Enter Lock name");
//            tv_title.setText("Lock Name");
//        } else {
//            txtInputSensor.setHint("Enter Door/Lock name");
//            tv_title.setText("Door/Lock Name");
//        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(LockBrandActivity.this);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().length() == 0) {
                    ChatApplication.showToast(LockBrandActivity.this, "Please enter lock name");
                } else {
                    updateLockSensor(edSensorName.getText().toString(), deviceid, dialog);
                }
            }
        });

        dialog.show();

    }

    /*call update sensor */
    private void updateLockSensor(String sensor_name, String deviceid, Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().updateDevice(deviceid, sensor_name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    if (code == 200) {
                        dialog.dismiss();
                        //      toolbar.setTitle(sensor_name);
//                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    public void DeleteLockSensor(String device_id) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().deleteDevice(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    if (code == 200) {
                        menuAdd.setVisible(false);
                        setAdapter();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    public class SmartLockBrandAdapter extends RecyclerView.Adapter<SmartLockBrandAdapter.SensorViewHolder> {

        private Context mContext;
        public int type = 0;

        public SmartLockBrandAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
            if(position ==0){
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_brand);
                holder.txtBrandName.setText("TT Lock");
            } else {
                holder.imgBrand.setImageResource(R.drawable.ic_yale_lock);
                holder.txtBrandName.setText("Yale Lock");
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == 0){
                        isFlagClick = true;
                        setAdapterTTlock();
                    } else {
                        isFlagClick = true;
                        toolbar.setTitle("Lock List");
                        menuAdd.setVisible(true);
                        getLockList();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return 2;
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgBrand;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBrandName = itemView.findViewById(R.id.txtBrandName);
                imgBrand = itemView.findViewById(R.id.img_Brand);
            }
        }
    }

    public class SmartLockOptionBrandAdapter extends RecyclerView.Adapter<SmartLockOptionBrandAdapter.SensorViewHolder> {

        private Context mContext;
        public int type = 0;
        LockClickListener lockClickListener;

        public SmartLockOptionBrandAdapter(Context context, List<YaleLockVO.Data> yaleLockList, LockClickListener lockClickListener1) {
            this.mContext = context;
            this.lockClickListener = lockClickListener1;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lock_brand_list, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
        /*    if (position == 0) {
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_bridge_small_icon);
            } else {
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_brand);
            }*/

            yalelockVO = yalelocklist.get(position);

            holder.imgBrand.setImageResource(R.drawable.lock_only);
            holder.txtBrandName.setText(yalelocklist.get(position).getDeviceName());

            holder.img_more_option.setId(position);
            holder.img_more_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lockClickListener.itemClicked(yalelocklist.get(holder.img_more_option.getId()), "edit");

                   /* PopupMenu popup = new PopupMenu(mContext, v);
                    @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                    } else {
                        popup = new PopupMenu(wrapper, v);
                    }
                    popup.getMenuInflater().inflate(R.menu.menu_dots, popup.getMenu());

                    popup.getMenu().findItem(R.id.action_log).setVisible(false);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_edit_dots:

                                    break;
                                case R.id.action_delete_dots:
                                    lockClickListener.itemClicked(yalelocklist.get(holder.img_more_option.getId()), "delete");
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();*/
                }
            });


           /* holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(mContext, GatewayTypeActivity.class);
                    } else {
                        intent = new Intent(mContext, TTLockListActivity.class);
                    }

                    startActivity(intent);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return yalelocklist.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgBrand, img_more_option;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBrandName = itemView.findViewById(R.id.txtBrandName);
                imgBrand = itemView.findViewById(R.id.img_Brand);
                img_more_option = itemView.findViewById(R.id.img_more_option);
            }
        }
    }

    public class TTLockOptionBrandAdapter extends RecyclerView.Adapter<TTLockOptionBrandAdapter.SensorViewHolder> {

        private Context mContext;
        public int type = 0;

        public TTLockOptionBrandAdapter(Context context, ArrayList<String> stringArrayList) {
            this.mContext = context;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
            if (position == 0) {
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_bridge_small_icon);
            } else {
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_brand);
            }

            holder.txtBrandName.setText(stringArrayList.get(position));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(mContext, GatewayTypeActivity.class);
                    } else {
                        intent = new Intent(mContext, TTLockListActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgBrand;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBrandName = itemView.findViewById(R.id.txtBrandName);
                imgBrand = itemView.findViewById(R.id.img_Brand);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isFlagClick) {
            isFlagClick = false;
            menuAdd.setVisible(false);
            toolbar.setTitle("Select Your Brand");

            txtNodataFound.setVisibility(View.GONE);
            recyclerViewLock.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            super.onBackPressed();
        }
    }
}
