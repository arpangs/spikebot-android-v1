package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.kp.core.ActivityHelper;
import com.spike.bot.Beacon.BeaconListActivity;
import com.spike.bot.Beacon.BeaconScannerAddActivity;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Repeatar.RepeaterActivity;
import com.spike.bot.activity.SmartCam.AddJetSonActivity;
import com.spike.bot.activity.SmartRemoteActivity;
import com.spike.bot.activity.TTLock.LockBrandActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.model.DeviceList;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 15/10/19.
 * Gmail : vipul patel
 */
public class AddDeviceTypeListActivity extends AppCompatActivity {

    public static int SENSOR_TYPE_PANAL = 0, SENSOR_TYPE_DOOR = 1, SENSOR_TYPE_TEMP = 2, SENSOR_GAS = 5, Curtain = 6, typeSync = 0, SENSOR_WATER = 7, PIR_DETECTOR = 8, type1 = 0, sensorposition = 0;
    public AddRoomDialog addRoomDialog;
    Toolbar toolbar;
    RecyclerView recyclerSmartDevice;
    DeviceListAdapter deviceListAdapter;
    ArrayList<DeviceList> arrayList = new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    ProgressDialog m_progressDialog;
    RoomVO room;
    Dialog dialog;

    boolean addRoom = false, addTempSensor = false;
    private Socket mSocket;
    /**
     * Socket Listner for configure devices
     */
    private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddDeviceTypeListActivity.this.runOnUiThread(new Runnable() {
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

                        ChatApplication.logDisplay("configureDevice is " + object);

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
                        String total_devices = object.optString("total_devices");
                        ChatApplication.logDisplay("typeSync is " + typeSync);
                        if (TextUtils.isEmpty(object.getString("message"))) {
                            if (typeSync == 0) {
                                addRoomDialog = new AddRoomDialog(AddDeviceTypeListActivity.this, roomIdList, roomNameList, object.getString("module_id"), total_devices, object.getString("module_type"), new ICallback() {
                                    @Override
                                    public void onSuccess(String str) {
                                        if (str.equalsIgnoreCase("yes")) {
                                            ChatApplication.isOpenDialog = true;
                                            ChatApplication.isRefreshDashBoard = true;
                                            ChatApplication.isMainFragmentNeedResume = true;
                                            ChatApplication.closeKeyboard(AddDeviceTypeListActivity.this);
                                        }
                                    }
                                });
                                if (!addRoomDialog.isShowing()) {
                                    addRoomDialog.show();
                                }
                            } else {
                                showGasSensor(object.optString("module_id"), object.optString("module_type"));
                            }
                        } else {
                            showConfigAlert(object.getString("message"), object.optString("module_type"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    /*count down for 7 sec after finish*/
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
            mSocket.off("configureDevice", configureDevice);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);
        setviewId();
    }

    private void setviewId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Add device");

        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerSmartDevice.setLayoutManager(linearLayoutManager);*/

        arrayList = new ArrayList<>();
        deviceListAdapter = new DeviceListAdapter(this, arrayList);

       /* deviceListAdapter = new DeviceListAdapter(this, arrayList);
        recyclerSmartDevice.setAdapter(deviceListAdapter);
        deviceListAdapter.notifyDataSetChanged();*/


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerSmartDevice.setLayoutManager(mLayoutManager);
        recyclerSmartDevice.addItemDecoration(new GridSpacingItemDecoration(3, dpToPixel(10), true));
        recyclerSmartDevice.setItemAnimator(new DefaultItemAnimator());
        recyclerSmartDevice.setAdapter(deviceListAdapter);

        getArraylist();
        startSocketConnection();

    }

    public int dpToPixel(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /*add arraylist static data*/
    public void getArraylist() {
        int[] covers = new int[]{
                R.drawable.unassigned,
                R.drawable.room,
                R.drawable.switchboard,
                /* R.drawable.smartdevice,*/
                R.drawable.irblaster,
                R.drawable.remote_ac,
                R.drawable.remote,
                R.drawable.lock_only,
                R.drawable.dooron,
                R.drawable.fire_and_gas,
                R.drawable.on_temperature,
                R.drawable.curtains_on,
                R.drawable.repeaters,
                R.drawable.camera,
                R.drawable.smart_camera,
                R.drawable.drop,
                R.drawable.beaconsearch,
                R.drawable.beaconscanner,
                R.drawable.pir_detector_on,

        };


        DeviceList list = new DeviceList("Unassigned List", covers[0]);
        arrayList.add(list);

        list = new DeviceList("Room", covers[1]);
        arrayList.add(list);

        list = new DeviceList("Switch Board", covers[2]);
        arrayList.add(list);

      /*  list = new DeviceList("Smart Device", covers[3]);
        arrayList.add(list);*/

        list = new DeviceList("IR Blaster", covers[3]);
        arrayList.add(list);

        list = new DeviceList("IR Remote", covers[4]);
        arrayList.add(list);

        list = new DeviceList("Smart Remote", covers[5]);
        arrayList.add(list);

        list = new DeviceList("Door Lock", covers[6]);
        arrayList.add(list);

        list = new DeviceList("Door Sensor", covers[7]);
        arrayList.add(list);

        list = new DeviceList("Gas / Smoke Sensor", covers[8]);
        arrayList.add(list);

        list = new DeviceList("Temperature Sensor", covers[9]);
        arrayList.add(list);

        list = new DeviceList("Curtain", covers[10]);
        arrayList.add(list);

        list = new DeviceList("Repeaters", covers[11]);
        arrayList.add(list);

        list = new DeviceList("Camera", covers[12]);
        arrayList.add(list);

        list = new DeviceList("Smart Camera", covers[13]);
        arrayList.add(list);

        list = new DeviceList("Water Detector", covers[14]);
        arrayList.add(list);

        list = new DeviceList("Beacon", covers[15]);
        arrayList.add(list);

        list = new DeviceList("Beacon Scanner", covers[16]);
        arrayList.add(list);

        list = new DeviceList("PIR Detector", covers[17]);
        arrayList.add(list);
/*
        arrayList.add("Unassigned List");
        arrayList.add("Room");
        arrayList.add("Switch Board");
        arrayList.add("Smart Device");
        arrayList.add("IR Blaster");
        arrayList.add("IR Remote");
        arrayList.add("Smart Remote");
        arrayList.add("Door Lock");
        arrayList.add("Door Sensor");
        arrayList.add("Gas / Smoke Sensor");
        arrayList.add("Temperature Sensor");
        arrayList.add("Curtain");
        arrayList.add("Repeaters");
        arrayList.add("Camera");
        arrayList.add("Smart Camera");
        arrayList.add("Water Detector");
//        arrayList.add("Add Beacon");*/
    }

    /*item click */
    private void setIntent(int position) {
      /*  if (position == 0) {
            addCustomRoom();
        } else if (position == 1) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera();
            }
        } else if (position == 2) {
            startActivity(new Intent(this, IRBlasterAddActivity.class));
        } else if (position == 3) {
            Intent intent = new Intent(AddDeviceTypeListActivity.this, IRRemoteAdd.class);
            //         intent.putExtra("roomName", "" + room.getRoomName());
            //          intent.putExtra("roomId", "" + room.getRoomId());
            startActivity(intent);
        } else if (position == 4) {
            Intent intent = new Intent(this, BrandListActivity.class);
            startActivity(intent);
        } else if (position == 5) {
            startActivity(new Intent(this, LockBrandActivity.class));
        } else if (position == 6) {
            startActivity(new Intent(this, RepeaterActivity.class));
        } else if (position == 7) {
            unassignIntent("all");
        } else if (position == 8) {

        }*/


        if (position == 0) {
            unassignIntent("all");
        } else if (position == 1) {
            addCustomRoom();
        } else if (position == 2) {
            showPanelOption(position);
            sensorposition = position;
        }/* else if (position == 3) {
            Intent intent = new Intent(this, BrandListActivity.class);
            startActivity(intent);
        }*/ else if (position == 3) {
            startActivity(new Intent(this, IRBlasterAddActivity.class));
        } else if (position == 4) {
            Intent intent = new Intent(AddDeviceTypeListActivity.this, IRRemoteAdd.class);
            //         intent.putExtra("roomName", "" + room.getRoomName());
            //          intent.putExtra("roomId", "" + room.getRoomId());
            startActivity(intent);
        } else if (position == 5) {
            startActivity(new Intent(this, SmartRemoteActivity.class));
        } else if (position == 6) {
            startActivity(new Intent(this, LockBrandActivity.class));
        } else if (position == 7) {
            showOptionDialog(SENSOR_TYPE_DOOR);
            sensorposition = position;
        } else if (position == 8) {
            showOptionDialog(SENSOR_GAS);
            sensorposition = position;
        } else if (position == 9) {
            showOptionDialog(SENSOR_TYPE_TEMP);
            sensorposition = position;
        } else if (position == 10) {
            showOptionDialog(Curtain);
            sensorposition = position;
        } else if (position == 11) {
            startActivity(new Intent(this, RepeaterActivity.class));
        } else if (position == 12) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera();
            }
        } else if (position == 13) {
            Intent intent = new Intent(this, AddJetSonActivity.class);
            startActivity(intent);
        } else if (position == 14) {
            showOptionDialog(SENSOR_WATER);
            sensorposition = position;
        } else if (position == 15) {
            /*Intent intent = new Intent(this, AddBeaconActivity.class);
            startActivity(intent);*/
            Intent intent = new Intent(this, BeaconListActivity.class);
            startActivity(intent);
        } else if (position == 16) {
            Intent intent = new Intent(this, BeaconScannerAddActivity.class);
            startActivity(intent);
        } else if (position == 17) {
            showOptionDialog(PIR_DETECTOR);
            sensorposition = position;
        }
    }

    /*unassign list*/
    public void unassignIntent(String type) {
        Intent intent = new Intent(this, AllUnassignedPanel.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /*start connection socket*/
    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    /**
     * Call api for add custom room
     */
    public void addCustomRoom() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomRoom(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * Call api for save custom room
     *
     * @param roomName mRoomName
     * @param dialog   mDialog instance
     */
    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            return;
        }
        showProgressDialog(this, "Searching Device attached ", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().saveCustomRoom(roomName.getText().toString(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        Common.hideSoftKeyboard(AddDeviceTypeListActivity.this);
                        dialog.dismiss();
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                        ChatApplication.isMainFragmentNeedResume = true;
                        ChatApplication.CurrnetFragment = R.id.navigationDashboard;  // dev arpan on 15 june 2020
                        finish();
                    } else if (code == 301) {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    } else {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    {
                        dismissProgressDialog();
                    }
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
            }
        });
    }

    /**
     * show Panel option for sync panel
     *
     * @param position
     */
    private void showPanelOption(int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_add_from_existing = dialog.findViewById(R.id.add_from_existing);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSocket != null) {
                    mSocket.on("configureDevice", configureDevice);
                }
                getgasConfigData(SENSOR_TYPE_PANAL);

            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                unassignIntent("panel");
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_add_from_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
//                intentPanel.putExtra("roomId", room.getRoomId());
//                intentPanel.putExtra("roomName", room.getRoomName());
//                intentPanel.putExtra("isDeviceAdd", false);
                startActivity(intentPanel);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /*dialog for sensor type selection*/
    private void showOptionDialog(final int sensor_type) {

        final Dialog dialog = new Dialog(AddDeviceTypeListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = dialog.findViewById(R.id.txt_dialog_title);
        // txtDialogTitle.setText("Select Sensor Type");

        if (sensor_type == SENSOR_GAS) {
            txtDialogTitle.setText("Gas Sensor");
        } else if (sensor_type == Curtain) {
            txtDialogTitle.setText("Curtain");
        } else if (sensor_type == SENSOR_TYPE_TEMP) {
            txtDialogTitle.setText("Temperature Sensor");
        } else if (sensor_type == SENSOR_TYPE_DOOR) {
            txtDialogTitle.setText("Door Sensor");
        } else if (sensor_type == SENSOR_WATER) {
            txtDialogTitle.setText("Water Detector");
        } else if (sensor_type == PIR_DETECTOR) {
            txtDialogTitle.setText("PIR Detector");
        } else {
            txtDialogTitle.setText("Panel");
        }

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
                getgasConfigData(sensor_type);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (sensor_type == 1) {
                    unassignIntent("door_sensor");
                } else if (sensor_type == 2) {
                    unassignIntent("temp_sensor");
                } else if (sensor_type == PIR_DETECTOR) {
                    unassignIntent("pir_device");
                } else {
                    unassignIntent("gas_sensor");
                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /**
     * call api for get configure device data
     */
    private void getgasConfigData(int type) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceTypeListActivity.this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }
        type1 = type;
        if (type == SENSOR_GAS) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new gas sensor", false);
        } else if (type == Curtain) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new curtain", false);
        } else if (type == SENSOR_TYPE_TEMP) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new temperature sensor", false);
        } else if (type == SENSOR_TYPE_DOOR) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new door sensor", false);
        } else if (type == SENSOR_WATER) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new water detector", false);
        } else if (type == PIR_DETECTOR) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new PIR detector", false);
        } else {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new panel", false);
        }
        startTimer();

        addTempSensor = true;

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        //curtain | gas_sensor | temp_sensor | smart_remote | door_sensor | repeater | 5 | 5f | heavy_load | double_heavy_load
        String url = ChatApplication.url + Constants.deviceconfigure;

        if (type == SENSOR_GAS) {
            url = url + "gas_sensor";
        } else if (type == Curtain) {
            url = url + "curtain";
        } else if (type == SENSOR_TYPE_TEMP) {
            url = url + "temp_sensor";
        } else if (type == SENSOR_TYPE_DOOR) {
            url = url + "door_sensor";
        } else if (type == SENSOR_WATER) {
            url = url + "water_detector";
        } else {
            url = url + "5";
        }


        typeSync = type;

        ChatApplication.logDisplay("config device url is " + url);


        SpikeBotApi.getInstance().getConfigData(type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                JsonObject result = new JsonObject();
                ChatApplication.logDisplay("url is result " + result);
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

    /**
     * dialog enter key camera
     */
    public void addKeyCamera() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_camera_key);

        final TextInputEditText room_name = dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCameraKey(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * config device url is
     * camera key : spike123
     * camera key check is valid or not..
     */
    private void saveCameraKey(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceTypeListActivity.this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter key name");
            return;
        }

       /* JSONObject object = new JSONObject();
        try {
            object.put("key", roomName.getText().toString()); *//*key is spike123*//*
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        showProgressDialog(this, "Searching Device attached ", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().saveCameraKey(roomName.getText().toString(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, "1");
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                        addCamera();
                    } else if (code == 301) {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    } else {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });

    }

    /**
     * open dialog for add camera
     */
    private void addCamera() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_camera);
        dialog.setCanceledOnTouchOutside(false);

        final AppCompatEditText camera_name = dialog.findViewById(R.id.txt_camera_name);
        final AppCompatEditText camera_ip = dialog.findViewById(R.id.txt_camera_ip);
        final AppCompatEditText video_path = dialog.findViewById(R.id.txt_video_path);
        final AppCompatEditText user_name = dialog.findViewById(R.id.txt_user_name);
        LinearLayout linear_day = dialog.findViewById(R.id.linear_day);
        LinearLayout linear_night = dialog.findViewById(R.id.linear_night);
        // final TextInputEditText password = dialog.findViewById(R.id.txt_password);
        ImageView img_passcode = dialog.findViewById(R.id.img_show_passcode);

        EditText password = dialog.findViewById(R.id.txt_password);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        //   Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        linear_day.setVisibility(View.GONE);
        linear_night.setVisibility(View.GONE);
        img_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    img_passcode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    img_passcode.setImageResource(R.drawable.eye);
                    //Hide Password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        /*btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });*/

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(camera_name.getText().toString())) {
                    camera_name.requestFocus();
                    camera_name.setError("Enter Camera name");
                    return;
                }
                if (TextUtils.isEmpty(camera_ip.getText().toString())) {
                    camera_ip.requestFocus();
                    camera_ip.setError("Enter Camera IP");
                    return;
                }
                if (TextUtils.isEmpty(video_path.getText().toString())) {
                    video_path.requestFocus();
                    video_path.setError("Enter Video Path");
                    return;
                }
                if (TextUtils.isEmpty(user_name.getText().toString())) {
                    user_name.requestFocus();
                    user_name.setError("Enter User Name");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.requestFocus();
                    password.setError("Enter Password");
                    return;
                }

                addCameraCall(camera_name.getText().toString(), camera_ip.getText().toString(), video_path.getText().toString(), user_name.getText().toString(),
                        password.getText().toString(), dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /**
     * @param camera_name : c1
     * @param camera_ip   : 192.168.75.113
     * @param video_path  : /live/streaming
     * @param user_name   : abcd
     * @param password    : 123...
     * @param dialog      : if(response code == 200) dismiss dialog
     */
    private void addCameraCall(String camera_name, String camera_ip, String video_path, String user_name, String password, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceTypeListActivity.this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addCamera(camera_name, camera_ip, video_path, user_name, password, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                dismissProgressDialog();

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    if (code == 200) {
                        dialog.dismiss();
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                dismissProgressDialog();
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                dismissProgressDialog();
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        ChatApplication.CurrnetFragment = R.id.navigationDashboard;  // dev arpan on 15 june 2020
        onBackPressed();
        return true;
    }

    public void showProgressDialog(Context context, String message, boolean iscancle) {
        m_progressDialog = new ProgressDialog(this);
        m_progressDialog.setMessage(message);
        m_progressDialog.setCanceledOnTouchOutside(true);
        m_progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (m_progressDialog != null) {
            ChatApplication.logDisplay("m_progressDialog is null not");
            m_progressDialog.cancel();
            m_progressDialog.dismiss();
        }
    }

    /*dialog for sensor */
    private void showGasSensor(String door_module_id, String module_type) {

        if (dialog == null) {
            dialog = new Dialog(AddDeviceTypeListActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = dialog.findViewById(R.id.sp_room_list);

        TextView mTxtDeviceType = dialog.findViewById(R.id.txt_device_type);
        LinearLayout llview = dialog.findViewById(R.id.ll_device_type);


        TextView dialogTitle = dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = dialog.findViewById(R.id.txt_sensor_name);


        if (typeSync == SENSOR_GAS) {
            dialogTitle.setText("Add Gas Sensor");
            txt_sensor_name.setText("Gas Name");
        } else if (typeSync == Curtain) {
            dialogTitle.setText("Add Curtain Sensor");
            txt_sensor_name.setText("Curtain Name");
        } else if (typeSync == SENSOR_TYPE_TEMP) {
            dialogTitle.setText("Add Temperature Sensor");
            txt_sensor_name.setText("Temperature Name");
        } else if (typeSync == SENSOR_TYPE_DOOR) {
            dialogTitle.setText("Add Door Sensor");
            txt_sensor_name.setText("Door Name");
        } else if (typeSync == SENSOR_WATER) {
            dialogTitle.setText("Add Water Detector");
            txt_sensor_name.setText("Detector Name");
        } else if (typeSync == PIR_DETECTOR) {
            dialogTitle.setText("Add PIR Detector");
            txt_sensor_name.setText("Detector Name");
            llview.setVisibility(View.VISIBLE);
        } else {
            dialogTitle.setText("Add Panel");
            txt_sensor_name.setText("Panel Name");
        }

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);


        mTxtDeviceType.setText(module_type != null ? module_type.toUpperCase() : "Device type not found");


        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(AddDeviceTypeListActivity.this, roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                mSocket.off("configureDevice", configureDevice);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                mSocket.off("configureDevice", configureDevice);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_door_name.getText().toString().trim().length() == 0) {
                    ChatApplication.showToast(AddDeviceTypeListActivity.this, "Please enter name");
                } else {
                    addDevice(dialog, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, module_type);
                    mSocket.off("configureDevice", configureDevice);
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }

    /*add device common service */
    private void addDevice(final Dialog dialog, String door_name, String door_module_id, Spinner sp_room_list, String module_type) {

        ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Please wait.", false);
        int room_pos = sp_room_list.getSelectedItemPosition();
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addDevice(roomIdList.get(room_pos), door_name, door_module_id, module_type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Common.hideSoftKeyboard(AddDeviceTypeListActivity.this);
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                        finish();
                    } else {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
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

    /**
     * Display Alert dialog when found door or temp sensor config already configured
     *
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage, String module_type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceTypeListActivity.this);


        if (!module_type.equals("5f") || !module_type.equals("5") && sensorposition == 2) {
            builder.setMessage("Attached device is not switch board");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("heavy_load") || !module_type.equals("double_heavy_load") && sensorposition == 2) {
            builder.setMessage("Attached device is not heavy load");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("door_sensor") && sensorposition == 7) {
            builder.setMessage("Attached device is not door sensor");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("gas_sensor") && sensorposition == 8) {
            builder.setMessage("Attached device is not gas sensor");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("temp_sensor") && sensorposition == 9) {
            builder.setMessage("Attached device is not temprature sensor");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("curtain") && sensorposition == 10) {
            builder.setMessage("Attached device is not curtain");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("water_detector") && sensorposition == 14) {
            builder.setMessage("Attached device is not water detector");
        } else {
            builder.setMessage(alertMessage);
        }

        if (!module_type.equals("pir_detector") && sensorposition == 17) {
            builder.setMessage("Attached device is not pir detector");
        } else {
            builder.setMessage(alertMessage);
        }



      /*  else {
            builder.setMessage(alertMessage);
        }*/
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSocket.off("configureDevice", configureDevice);
            }
        });
        builder.create().show();
    }

    /*device list adapter*/
    public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.SensorViewHolder> {

        ArrayList<DeviceList> arrayListLog = new ArrayList<>();
        private Context mContext;


        public DeviceListAdapter(Context context, ArrayList<DeviceList> arrayListLog1) {
            this.mContext = context;
            this.arrayListLog = arrayListLog1;
        }

        @Override
        public DeviceListAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_list, parent, false);
            return new DeviceListAdapter.SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DeviceListAdapter.SensorViewHolder holder, final int position) {

            DeviceList devielist = arrayList.get(position);

            // loading album cover using Glide library
            Glide.with(mContext).load(devielist.getThumbnail()).into(holder.imgAdd);
            holder.txtUserName.setText(devielist.getDevicename());

           /* if (position == 0 || position == 3 || position == 4 || position == 6 || position == 7 || position == 12 || position == 14) {
                holder.imgAdd.setVisibility(View.INVISIBLE);
            } else {
                holder.imgAdd.setVisibility(View.VISIBLE);
            }*/

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setIntent(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayListLog.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public TextView txtUserName;
            public ImageView imgAdd;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtUserName = itemView.findViewById(R.id.txtUserName);
                imgAdd = itemView.findViewById(R.id.imgAdd);
            }
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

        /**
         * Converting dp to pixel
         */
        public int dpToPixel(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }
    }
}
