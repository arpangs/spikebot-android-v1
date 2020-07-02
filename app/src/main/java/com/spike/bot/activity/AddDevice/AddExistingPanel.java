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

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.panel.ExistPanelRoomAdapter;
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

        Toolbar toolbar =  findViewById(R.id.toolbar);

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

        ll_panel_view =  findViewById(R.id.ll_panel_view);
        ll_un_view =  findViewById(R.id.ll_un_view);
        spinnerRoom =  findViewById(R.id.spinnerRoom);

        if (isDeviceAdd) {
            getSupportActionBar().setTitle("Add Custom Device");
            ll_panel_view.setVisibility(View.GONE);
        } else if (isSync) {
            getSupportActionBar().setTitle("Add Unassigned Panel");
        } else {
            getSupportActionBar().setTitle("Add Panel");
            ll_panel_view.setVisibility(View.VISIBLE);
            if(!isDeviceAdd){
                ll_un_view.setVisibility(View.VISIBLE);
            }else {
                ll_un_view.setVisibility(View.GONE);
            }

        }

        linear_progress =  findViewById(R.id.linear_progress);
        ll_panel_list =  findViewById(R.id.ll_panel_list);

        et_panel_name_existing =  findViewById(R.id.et_panel_name_existing);
        et_panel_name_existing.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        list_panel =  findViewById(R.id.list_panel);
        list_panel.setHasFixedSize(true);
        list_panel.setLayoutManager(new LinearLayoutManager(this));


        existPanelRoomAdapter = new ExistPanelRoomAdapter(roomList, isSync);
        list_panel.setAdapter(existPanelRoomAdapter);

        ActivityHelper.hideKeyboard(this);
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

//        ActivityHelper.showProgressDialog(this, "Loading...", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", "room");
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.getRoomCameraList;

        ChatApplication.logDisplay("un assign is " + url + " " + jsonObject);

        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("un assign is " + result);
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * get method for call getOriginalDevices api
     */
    private void getDeviceList() {

        roomList.clear();
        roomList = new ArrayList<>();

        String url = "";
//        if (isSync) {
////            url = webUrl + Constants.deviceunassigned; //add new unassigned
////        } else {
////            url = webUrl + Constants.GET_ORIGINAL_DEVICES + "/" + 1; //Add from existing
////        }

        url = webUrl + Constants.GET_ORIGINAL_DEVICES ; //add new unassigned
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("web url : " + url+ jsonObject);

        showProgress();

        new GetJsonTask(getApplicationContext(), url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());
                try {
                    getRoomList();

                    roomList = new ArrayList<>();

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

                                    ChatApplication.logDisplay("id is "+deviceVO.getDeviceId()+"  "+devicePanelVO.getDeviceId());
//                                    if (deviceVO.getDeviceId() == devicePanelVO.getDeviceId() && deviceVO.getModuleId().equalsIgnoreCase(devicePanelVO.getModuleId())) {
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
            public void onFailure(Throwable throwable, String error) {
                hideProgress();
                if (roomList.size() == 0) {
                    list_panel.setVisibility(View.GONE);
                    ll_panel_list.setVisibility(View.VISIBLE);
                    if (menu != null) {
                        menu.findItem(R.id.action_save).setEnabled(false);
                    }
                }
            }
        }).execute();
    }

    private void setRoomSpinner(ArrayList<UnassignedListRes.Data.RoomList> roomListtemp) {
        roomNameList.clear();
        for(int i = 0; i< roomListtemp.size(); i++){
            roomNameList.add(roomListtemp.get(i).getRoomName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, roomNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(dataAdapter);

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomId= roomListName.get(position).getRoomId();
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
            ChatApplication.showToast(getApplicationContext(),getResources().getString(R.string.disconnect));
            return;
        }
        if (!isDeviceAdd) {
            if (TextUtils.isEmpty(roomId)) {
                Toast.makeText(getApplicationContext(), "Please select room", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(et_panel_name_existing.getText().toString())) {
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

        if (!isSelected) {
            Toast.makeText(getApplicationContext(),
                    isDeviceAdd ? "Please select Device" : "Please select Panel", Toast.LENGTH_SHORT).show();
            return;
        } else {

            String url = "";
            if (!isDeviceAdd) {
                url = ChatApplication.url + Constants.ADD_CUSTOM_PANEL;
            } else {
                url = ChatApplication.url + Constants.ADD_CUSTOME_DEVICE;
            }

            ChatApplication.logDisplay("URL CALL : " + url + " isDeviceAdd : " + isDeviceAdd);
            JSONObject panelObj = new JSONObject();
            try {

                if (!isDeviceAdd) {
                    panelObj.put("room_id", roomId);
                    panelObj.put("panel_name", et_panel_name_existing.getText().toString());
                }else {
                    panelObj.put("panel_id", panelId);
                }

                panelObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                panelObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
                panelObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

                JSONArray jsonArrayDevice = new JSONArray(listDevice);
                panelObj.put("devices", jsonArrayDevice);

                ChatApplication.logDisplay("JSONObject : " + panelObj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ActivityHelper.showProgressDialog(AddExistingPanel.this, "Please wait...", false);


            new GetJsonTask(this, url, "POST", panelObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
                @Override
                public void onSuccess(JSONObject result) {
                    ActivityHelper.dismissProgressDialog();
                    ChatApplication.logDisplay("saveMood onSuccess " + result.toString());
                    try {

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
                public void onFailure(Throwable throwable, String error) {
                    ActivityHelper.dismissProgressDialog();
                }
            }).execute();
        }

    }

}
