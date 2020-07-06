package com.spike.bot.activity.AddDevice;

import android.os.Bundle;

import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.panel.ExistPanelRoomAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.model.DevicePanelVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 21/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class AddExistingPanel extends AppCompatActivity {

    RecyclerView list_panel;
    private Socket mSocket;

    ExistPanelRoomAdapter existPanelRoomAdapter;

    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private ArrayList<UnassignedListRes.Data.RoomList> roomListName = new ArrayList<>();
    private ArrayList<String> roomNameList = new ArrayList<>();
    private LinearLayout linear_progress;
    private EditText et_panel_name_existing;
    private String roomId;
    private boolean isDeviceAdd, isSync;
    private LinearLayout ll_panel_view;
    private String panelId, panel_name;
    private String roomName;

    private PanelVO panelVO;
    private AppCompatSpinner spinnerRoom;
    private LinearLayout ll_panel_list, ll_un_view;

    String webUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_panel);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
//            roomId = getIntent().getExtras().getString("roomId");
            isDeviceAdd = getIntent().getExtras().getBoolean("isDeviceAdd");
            isSync = getIntent().getExtras().getBoolean("isSync");
            panelId = getIntent().getExtras().getString("panel_id");
            panel_name = getIntent().getExtras().getString("panel_name");
            roomName = getIntent().getExtras().getString("roomName");
            panelVO = (PanelVO) getIntent().getExtras().getSerializable("panelV0");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ll_panel_view = findViewById(R.id.ll_panel_view);
        ll_un_view = findViewById(R.id.ll_un_view);
        spinnerRoom = findViewById(R.id.spinnerRoom);

        if (isDeviceAdd) {
            getSupportActionBar().setTitle("Add Custom Device");
            ll_panel_view.setVisibility(View.GONE);
        } else if (isSync) {
            getSupportActionBar().setTitle("Add Unassigned Panel");
        } else {
            getSupportActionBar().setTitle("Add Panel");
            ll_panel_view.setVisibility(View.VISIBLE);
            if (!isDeviceAdd) {
                ll_un_view.setVisibility(View.VISIBLE);
            } else {
                ll_un_view.setVisibility(View.GONE);
            }

        }

        linear_progress = findViewById(R.id.linear_progress);
        ll_panel_list = findViewById(R.id.ll_panel_list);

        et_panel_name_existing = findViewById(R.id.et_panel_name_existing);
        et_panel_name_existing.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        list_panel = findViewById(R.id.list_panel);
        list_panel.setHasFixedSize(true);
        list_panel.setLayoutManager(new LinearLayoutManager(this));


        existPanelRoomAdapter = new ExistPanelRoomAdapter(roomList, isSync);
        list_panel.setAdapter(existPanelRoomAdapter);

//        ActivityHelper.hideKeyboard(this);
        startSocketConnection();

    }

    private void showProgress() {
        list_panel.setVisibility(View.GONE);
        linear_progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        list_panel.setVisibility(View.VISIBLE);
        linear_progress.setVisibility(View.GONE);
    }

    private void getRoomList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getroomcameralist("room", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {
                        }.getType();
                        roomListName = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optJSONObject("data").optJSONArray("roomList").toString(), type);

                        setRoomSpinner(roomListName);
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
     * get method for call getOriginalDevices api
     */
    private void getDeviceList() {

        roomList.clear();
        roomList = new ArrayList<>();
        showProgress();
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getCustomPanelDetail(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                getRoomList();
                roomList = new ArrayList<>();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.optJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseExistPanelArray(roomArray);

                    //selected device list by panelID
                    if (panelVO != null && panelVO.getDeviceList() != null) {
                        List<DeviceVO> deviceVOList = panelVO.getDeviceList();
                        for (DeviceVO deviceVO : deviceVOList) {

                            for (RoomVO roomVO : roomList) {
                                ArrayList<DevicePanelVO> panelList = roomVO.getDevicePanelList();
                                for (DevicePanelVO devicePanelVO : panelList) {

                                    ChatApplication.logDisplay("id is " + deviceVO.getDeviceId() + "  " + devicePanelVO.getDeviceId());
                                    if (deviceVO.getDeviceId().equals(devicePanelVO.getDeviceId())) {
                                        roomVO.setExpanded(true);
                                        devicePanelVO.setSelected(true);
                                }
                                }
                            }
                        }
                    }
                    existPanelRoomAdapter = new ExistPanelRoomAdapter(roomList, isSync);
                    list_panel.setAdapter(existPanelRoomAdapter);
                    existPanelRoomAdapter.notifyDataSetChanged();

                    if (roomList.size() == 0) {
                        list_panel.setVisibility(View.GONE);
                        ll_panel_list.setVisibility(View.VISIBLE);
                        menu.findItem(R.id.action_save).setEnabled(false);
                    } else {
                        menu.findItem(R.id.action_save).setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    hideProgress();
                }
            }

            @Override
            public void onData_FailureResponse() {
                hideProgress();
                if (roomList.size() == 0) {
                    list_panel.setVisibility(View.GONE);
                    ll_panel_list.setVisibility(View.VISIBLE);
                    if (menu != null) {
                        menu.findItem(R.id.action_save).setEnabled(false);
                    }
                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                hideProgress();
                if (roomList.size() == 0) {
                    list_panel.setVisibility(View.GONE);
                    ll_panel_list.setVisibility(View.VISIBLE);
                    if (menu != null) {
                        menu.findItem(R.id.action_save).setEnabled(false);
                    }
                }
            }
        });
    }

    private void setRoomSpinner(ArrayList<UnassignedListRes.Data.RoomList> roomListtemp) {
        roomNameList.clear();
        for (int i = 0; i < roomListtemp.size(); i++) {
            roomNameList.add(roomListtemp.get(i).getRoomName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, roomNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(dataAdapter);

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomId = roomListName.get(position).getRoomId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        if (Common.isConnected()) {
            getDeviceList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        this.menu = menu;
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveExistPanel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*save existing panel */
    private void saveExistPanel() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }
        if (!isDeviceAdd) {
            if (TextUtils.isEmpty(roomId)) {
                Toast.makeText(getApplicationContext(), "Please select room", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(et_panel_name_existing.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.enter_panel_name, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean isSelected = false;
        ArrayList<DevicePanelVO> selectedDevice = new ArrayList<>();
        ArrayList<String> listDevice = new ArrayList<>();
        if (roomList.size() > 0) {

            for (RoomVO room : roomList) {
                ArrayList<DevicePanelVO> deviceList = room.getDevicePanelList();
                for (DevicePanelVO devie : deviceList) {

                    if (devie.isSelected()) {
                        isSelected = true;
                        selectedDevice.add(devie);
                        listDevice.add(devie.getDeviceId());
                    }
                }
            }
        }
        ActivityHelper.showProgressDialog(AddExistingPanel.this, "Please wait...", false);
        JSONArray jsonArrayDevice = new JSONArray(listDevice);
        if (!isSelected) {
            Toast.makeText(getApplicationContext(),
                    isDeviceAdd ? "Please select Device" : "Please select Panel", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (ChatApplication.url.contains("http://"))
                ChatApplication.url = ChatApplication.url.replace("http://", "");
            SpikeBotApi.getInstance().saveExistPanel(roomId, et_panel_name_existing.getText().toString(), panelId, isDeviceAdd, jsonArrayDevice, new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    try {
                        JSONObject result = new JSONObject(stringResponse);

                        int code = result.getInt("code");
                        String message = result.getString("message");
                        if (code == 200) {
                            ChatApplication.isMainFragmentNeedResume = true;
                            ChatApplication.isEditActivityNeedResume = true;
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
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

    }

}
