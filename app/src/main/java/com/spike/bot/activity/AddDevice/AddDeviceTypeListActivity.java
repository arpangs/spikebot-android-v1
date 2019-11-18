package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Repeatar.RepeaterActivity;
import com.spike.bot.activity.SensorUnassignedActivity;
import com.spike.bot.activity.SmartDevice.BrandListActivity;
import com.spike.bot.activity.SmartRemoteActivity;
import com.spike.bot.activity.TTLock.LockBrandActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.dialog.ICallback;
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

    Toolbar toolbar;
    RecyclerView recyclerSmartDevice;
    DeviceListAdapter deviceListAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();

    ProgressDialog m_progressDialog;
    RoomVO room;
    private Socket mSocket;

    public AddRoomDialog addRoomDialog;
    Dialog dialog;

    boolean addRoom = false, addTempSensor = false;
    public static int SENSOR_TYPE_PANAL = 0, SENSOR_TYPE_DOOR = 1, SENSOR_TYPE_TEMP = 2, SENSOR_GAS = 5, Curtain = 6, typeSync = 0;

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
        toolbar.setTitle("Configuration");

        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerSmartDevice.setLayoutManager(linearLayoutManager);

        getArraylist();

        deviceListAdapter = new DeviceListAdapter(this, arrayList);
        recyclerSmartDevice.setAdapter(deviceListAdapter);
        deviceListAdapter.notifyDataSetChanged();
        startSocketConnection();

    }

    public void getArraylist() {
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
        arrayList.add("Repeaters");
        arrayList.add("Camera");
    }


    private void setIntent(int position) {
        if (position == 0) {
           unassignIntent("all");
        } else if (position == 1) {
            addCustomRoom();
        } else if (position == 2) {
            showPanelOption(position);
        } else if (position == 3) {
            Intent intent = new Intent(this, BrandListActivity.class);
            startActivity(intent);
        } else if (position == 4) {
            startActivity(new Intent(this, IRBlasterAddActivity.class));
        } else if (position == 5) {
            Intent intent = new Intent(AddDeviceTypeListActivity.this, IRRemoteAdd.class);
            //         intent.putExtra("roomName", "" + room.getRoomName());
            //          intent.putExtra("roomId", "" + room.getRoomId());
            startActivity(intent);
        } else if (position == 6) {
            startActivity(new Intent(this, SmartRemoteActivity.class));
        } else if (position == 7) {
            startActivity(new Intent(this, LockBrandActivity.class));
        } else if (position == 8) {
            showOptionDialog(SENSOR_TYPE_DOOR);
        } else if (position == 9) {
            showOptionDialog(SENSOR_GAS);
        } else if (position == 10) {
            showOptionDialog(SENSOR_TYPE_TEMP);
        } else if (position == 11) {
            startActivity(new Intent(this, RepeaterActivity.class));
        } else if (position == 12) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera();
            }
        }
    }

    public void unassignIntent(String type){
        Intent intent=new Intent(this, AllUnassignedPanel.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();
        if (mSocket != null) {
            mSocket.on("configureDevice", configureDevice);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }


    public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.SensorViewHolder> {

        private Context mContext;
        ArrayList<String> arrayListLog = new ArrayList<>();


        public DeviceListAdapter(Context context, ArrayList<String> arrayListLog1) {
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

            holder.txtUserName.setText(arrayList.get(position));

            if (position == 0 || position == 3 || position == 4 || position == 6 || position == 7 || position == 11) {
                holder.imgAdd.setVisibility(View.INVISIBLE);
            } else {
                holder.imgAdd.setVisibility(View.VISIBLE);
            }

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

        JSONObject object = new JSONObject();
        try {
            object.put("room_name", roomName.getText().toString());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());
        showProgressDialog(this, "Searching Device attached ", false);


        String url = ChatApplication.url + Constants.ADD_CUSTOME_ROOM;
        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        Common.hideSoftKeyboard(AddDeviceTypeListActivity.this);
                        dialog.dismiss();
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                        ChatApplication.isMainFragmentNeedResume = true;
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
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    /**
     * show Panel option for sync panel
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
                intentPanel.putExtra("roomId", room.getRoomId());
                intentPanel.putExtra("roomName", room.getRoomName());
                intentPanel.putExtra("isDeviceAdd", false);
                startActivity(intentPanel);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showOptionDialog(final int sensor_type) {

        final Dialog dialog = new Dialog(AddDeviceTypeListActivity.this);
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
                getgasConfigData(sensor_type);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(sensor_type==1){
                    unassignIntent("door_sensor");
                }else  if(sensor_type==2){
                    unassignIntent("temp_sensor");
                }else {
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

    private void isUnassignedDoorSensor(final int isDoorSensor) {
//        ActivityHelper.showProgressDialog(this, "Please wait...", false);
//        String url = "";
//        //muitl == 5
//        if (isDoorSensor == 3) {
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir
//        }
//        if (isDoorSensor == Curtain) {
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/curtain"; //0 door - 1 ir
//        } else if (isDoorSensor == 4) {
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/5"; //0 door - 1 ir
//        } else if (isDoorSensor == 5) {
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/5"; //0 door - 1 ir
//        } else {
////            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/0"; //0 door - 1 ir
//        }
//
//        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
//            @Override
//            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();
//                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(), SensorUnassignedRes.class);
//
//                if (sensorUnassignedRes.getCode() == 200) {
//                    if (sensorUnassignedRes.getData() != null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0) {
                        Intent intent = new Intent(AddDeviceTypeListActivity.this, SensorUnassignedActivity.class);
                        intent.putExtra("isDoorSensor", isDoorSensor);
                        intent.putExtra("roomId", room.getRoomId());
                        startActivity(intent);
//                    } else {
//                        ChatApplication.showToast(AddDeviceTypeListActivity.this, sensorUnassignedRes.getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String error) {
//                throwable.printStackTrace();
//                ChatApplication.showToast(AddDeviceTypeListActivity.this,getResources().getString(R.string.something_wrong1));
//            }
//        }).execute();

    }

    /**
     * call api for get configure device data
     */
    private void getgasConfigData(int type) {

        if (!ActivityHelper.isConnectingToInternet(AddDeviceTypeListActivity.this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (type == SENSOR_GAS) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new gas sensor", false);
        } else if (type == Curtain) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new curtain", false);
        } else if (type == SENSOR_TYPE_TEMP) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new temperature sensor", false);
        } else if (type == SENSOR_TYPE_DOOR) {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new door sensor", false);
        } else {
            ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Searching for new panel", false);
        }
        startTimer();

        addTempSensor = true;
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
        } else {
            url = url + "5";
        }

        typeSync = type;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(AddDeviceTypeListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("url is result " + result);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }


    /**
     * get method for call getOriginalDevices api
     * delete device list panel list
     */
    private void getUnasignedDeviceList() {

//        roomList.clear();
//        ActivityHelper.showProgressDialog(this, "Please wait...", false);
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
//            jsonObject.put("module_type", "5f");
//            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
//            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        String url = ChatApplication.url + Constants.deviceunassigned;
//
//        ChatApplication.logDisplay("url is "+url+" "+jsonObject);
//        new GetJsonTask(getApplicationContext(), url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
//            @Override
//            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();
//
//                try {
//
//                    JSONObject dataObject = result.getJSONObject("data");
//
//                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
//                    roomList = JsonHelper.parseExistPanelArray(roomArray);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//
//                }
//                if (roomList.size() == 0) {
//                    ChatApplication.showToast(AddDeviceTypeListActivity.this, "No Unassigned Panel Found.");
//                } else {

//                    Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
//                    intentPanel.putExtra("roomId","");
//                    intentPanel.putExtra("roomName", "");
//                    intentPanel.putExtra("isSync", true);
//                    intentPanel.putExtra("isDeviceAdd", false);
//                    startActivity(intentPanel);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String error) {
//                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.something_wrong1));
//            }
//        }).execute();

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
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
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

        JSONObject object = new JSONObject();
        try {
            object.put("key", roomName.getText().toString()); /*key is spike123*/
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(this, "Searching Device attached ", false);

        String url = ChatApplication.url + Constants.validatecamerakey;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.showToast(AddDeviceTypeListActivity.this,getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    /**
     * open dialog for add camera
     */
    private void addCamera() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_camera);
        dialog.setCanceledOnTouchOutside(false);

        final TextInputEditText camera_name = dialog.findViewById(R.id.txt_camera_name);
        final TextInputEditText camera_ip = dialog.findViewById(R.id.txt_camera_ip);
        final TextInputEditText video_path = dialog.findViewById(R.id.txt_video_path);
        final TextInputEditText user_name = dialog.findViewById(R.id.txt_user_name);
        final TextInputEditText password = dialog.findViewById(R.id.txt_password);

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

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(AddDeviceTypeListActivity.this, Constants.USER_ID));
            obj.put("camera_name", camera_name);
            obj.put("camera_ip", camera_ip);
            obj.put("video_path", video_path);
            obj.put("user_name", user_name);
            obj.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.ADD_CAMERA;

        new GetJsonTask(AddDeviceTypeListActivity.this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
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
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("onFailure " + error);
                ChatApplication.showToast(AddDeviceTypeListActivity.this,getResources().getString(R.string.something_wrong1));
            }
        }).execute();

    }

    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            addRoom = false;
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


    @Override
    public boolean onSupportNavigateUp() {
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
        } else {
            dialogTitle.setText("Add Panel");
            txt_sensor_name.setText("Panel Name");
        }

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

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
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_door_name.getText().toString().trim().length() == 0) {
                    ChatApplication.showToast(AddDeviceTypeListActivity.this, "Please enter name");
                } else {
                    addCurtain(dialog, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, module_type);
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }

    private void addCurtain(final Dialog dialog, String door_name, String door_module_id, Spinner sp_room_list, String  module_type) {


        ActivityHelper.showProgressDialog(AddDeviceTypeListActivity.this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {
            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("room_id", roomIdList.get(room_pos));
            obj.put("device_name", door_name);
            obj.put("module_id", door_module_id);
            obj.put("module_type", module_type);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.deviceadd;

        ChatApplication.logDisplay("door sensor" + url + obj);

        new GetJsonTask(AddDeviceTypeListActivity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();

            }
        }).execute();
    }


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

                        ChatApplication.logDisplay("typeSync is " + typeSync);
                        if (TextUtils.isEmpty(object.getString("message"))) {
                            if (typeSync == 0) {
                                addRoomDialog = new AddRoomDialog(AddDeviceTypeListActivity.this, roomIdList, roomNameList, object.getString("module_id"), "" + 5, object.getString("module_type"), new ICallback() {
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
                                showGasSensor(object.optString("module_id"),object.optString("module_type"));
                            }
                        } else {
                            showConfigAlert(object.getString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddDeviceTypeListActivity.this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

}
