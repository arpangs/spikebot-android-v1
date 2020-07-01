package com.spike.bot.Beacon;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.TTLock.YaleLockInfoActivity;
import com.spike.bot.adapter.beacon.BeaconDeviceListLayoutHelper;
import com.spike.bot.adapter.mood.MoodDeviceListLayoutHelper;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

public class BeaconDetailActivity extends AppCompatActivity {
    RecyclerView recycler_room;
    ArrayList<RoomVO> roomList = new ArrayList<>();
    BeaconDeviceListLayoutHelper deviceListLayoutHelper;
    RoomVO moodVO = new RoomVO();
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray = new ArrayList<>();
    ArrayList<String> roomListString = new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    Dialog roomdialog;
    String room_id;
    private Socket mSocket;
    LinearLayout ll_beacon_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail);

        setViewId();
    }

    public void setViewId() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recycler_room = findViewById(R.id.recycler_roomlist);
        ll_beacon_list = findViewById(R.id.ll_beacon_list);

        room_id = getIntent().getStringExtra("room_id");

        toolbar.setTitle("Beacons");
        startSocketConnection();
        getRoomList();
        getBeaconLocationList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("beaconAddedToRoom", beaconAddedToRoom);
            mSocket.off("beaconRemovedFromRoom", beaconRemovedFromRoom);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void socketOn() {
        mSocket.on("beaconAddedToRoom", beaconAddedToRoom);
        mSocket.on("beaconRemovedFromRoom", beaconRemovedFromRoom);
    }

    /* beacon location list getting  */
    public void getBeaconLocationList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(BeaconDetailActivity.this, "Please Wait", true);
        SpikeBotApi.getInstance().getBeaconLocationList(room_id, roomListString, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("beacon list is room " + result);
                    //  JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = result.getJSONArray("data");
                    if (roomArray != null && roomArray.length() > 0) {
                        ll_beacon_list.setVisibility(View.GONE);
                        recycler_room.setVisibility(View.VISIBLE);
                        roomList = JsonHelper.parseBeaconRoomArray(roomArray, true);
                        setData(roomList);
                    } else {

                        ll_beacon_list.setVisibility(View.VISIBLE);
                        recycler_room.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(BeaconDetailActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /* set view of room list
     * roles
     * only remote data data another all type sensor ignore it..
     * */
    public void setData(ArrayList<RoomVO> roomList) {

        if (moodVO != null) {



            for (int i = 0; i < moodVO.getPanelList().size(); i++) {
                for (int j = 0; j < moodVO.getPanelList().get(i).getDeviceList().size(); j++) {
                    ChatApplication.logDisplay("panel id " + moodVO.getPanelList().get(i).getDeviceList().get(j).getPanel_device_id());
                    //   deviceVOList.add(moodVO.getPanelList().get(i).getDeviceList().get(j).getDeviceId());
                }
            }
        }

        //sort room list vie selected device list available
        sortList(roomList);

        deviceListLayoutHelper = new BeaconDeviceListLayoutHelper(BeaconDetailActivity.this, recycler_room, Constants.SWITCH_NUMBER, true);
        deviceListLayoutHelper.addSectionList(roomList);
        deviceListLayoutHelper.notifyDataSetChanged();

    }

    /*get room list*/
    private void getRoomList() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        SpikeBotApi.getInstance().getRoomList("room", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {
                        }.getType();
                        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optString("data").toString(), type);
                        roomListString.add("All");
                        for (int i = 0; i < roomListArray.size(); i++) {
                            roomListString.add(roomListArray.get(i).getRoomName());
                            roomIdList.add(roomListArray.get(i).getRoomId());
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
                ActivityHelper.dismissProgressDialog();
            }
        });

    }


    private void sortList(final List<RoomVO> roomVOs) {

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded, o1.isExpanded);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        MenuItem menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setIcon(R.drawable.icn_reset);
        menuItem.setIcon(R.drawable.icn_filter);
        menuItemReset.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            dialogLogFilter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*room filter dialog*/
    private void dialogLogFilter() {
        roomdialog = new Dialog(BeaconDetailActivity.this);
        roomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        roomdialog.setCanceledOnTouchOutside(false);
        roomdialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = roomdialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = roomdialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = roomdialog.findViewById(R.id.edSensorName);
        final RelativeLayout relativeroom = roomdialog.findViewById(R.id.relative_room);
        final AppCompatSpinner spinneroom = roomdialog.findViewById(R.id.room_spinner);
        final TextView label_roomname = roomdialog.findViewById(R.id.label_roomname);

        relativeroom.setVisibility(View.VISIBLE);
        label_roomname.setVisibility(View.VISIBLE);
        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);

        room_name.setVisibility(View.GONE);

        Button btnSave = roomdialog.findViewById(R.id.btn_save);
        Button btn_cancel = roomdialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = roomdialog.findViewById(R.id.iv_close);
        TextView tv_title = roomdialog.findViewById(R.id.tv_title);

        tv_title.setText("Filter");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomListString);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneroom.setAdapter(dataAdapter);

        spinneroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    room_id = "";
                } else {
                    room_id = roomIdList.get(position - 1);
                    ChatApplication.logDisplay("ROOM NAME" + room_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomdialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(BeaconDetailActivity.this);
                roomdialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomdialog.dismiss();
                getBeaconLocationList();
            }
        });
        roomdialog.show();

    }

    /*start connection*/
    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("beaconAddedToRoom", beaconAddedToRoom);
            mSocket.on("beaconRemovedFromRoom", beaconRemovedFromRoom);
        }
    }

    private Emitter.Listener beaconAddedToRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            BeaconDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String device_id = object.getString("device_id");
                            String device = object.getString("device");
                            ChatApplication.logDisplay("add beacon socket" + object.toString());
                            getBeaconLocationList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    private Emitter.Listener beaconRemovedFromRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            BeaconDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String device_id = object.getString("device_id");
                            ChatApplication.logDisplay("remove beacon socket" + object.toString());
                            getBeaconLocationList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };


}
