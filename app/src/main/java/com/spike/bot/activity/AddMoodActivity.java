package com.spike.bot.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.RoomListArrayAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.mood.MoodDeviceListLayoutHelper;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.socket.client.Socket;

public class AddMoodActivity extends AppCompatActivity implements ItemClickMoodListener, ItemClickListener {

    public static String TAG = "AddMoodActivity";
    ArrayList<RoomVO> roomList = new ArrayList<>();
    ArrayList<String> arrayListRoomId = new ArrayList<>();
    private RecyclerView mMessagesView;
    MoodDeviceListLayoutHelper deviceListLayoutHelper;
    AutoCompleteTextView et_switch_name;
    Spinner sp_device_type, spinner_mood_icon; //for mood selection
    ImageView sp_drop_down;
    String panel_id, panel_name;

    RoomVO moodVO = new RoomVO();
    RoomListArrayAdapter moodIconArrayAdapter;
    boolean editMode = false, isMap = false, isMoodAdapter = false;

    List<DeviceVO> deviceVOArrayList;
    List<DeviceVO> deviceVOArrayListTemp = new ArrayList<>();
    String room_device_id = "";
    public Dialog dialog = null;
    private Socket mSocket;
    String webUrl = "";
    List<String> moodList = new ArrayList<>();
    private List<RoomVO> moodIconList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mMessagesView = (RecyclerView) findViewById(R.id.messages);

        et_switch_name = (AutoCompleteTextView) findViewById(R.id.et_switch_name);

        sp_device_type = (Spinner) findViewById(R.id.sp_device_type);

        spinner_mood_icon = (Spinner) findViewById(R.id.spinner_mood_icon);

        sp_drop_down = (ImageView) findViewById(R.id.sp_drop_down);

        //moodVO.getMood_name()
        JSONArray device_iconsArray = new JSONArray();
        try {
            JSONObject obj = new JSONObject();
            obj.put("device_icon_name", "work");
            device_iconsArray.put(obj);

            JSONObject obj1 = new JSONObject();
            obj1.put("device_icon_name", "home");
            device_iconsArray.put(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("device_icon_name", "night");
            device_iconsArray.put(obj2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            editMode = getIntent().getExtras().getBoolean("editMode", false);
            moodVO = (RoomVO) getIntent().getExtras().getSerializable("moodVO");
            isMap = getIntent().getBooleanExtra("isMap", false);
            isMoodAdapter = getIntent().getBooleanExtra("isMoodAdapter", false);
            panel_id = getIntent().getStringExtra("panel_id");
            panel_name = getIntent().getStringExtra("panel_name");
            if (moodVO != null) {
                et_switch_name.setText(moodVO.getRoomName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (editMode) {
            toolbar.setTitle("EDIT MOOD");
        } else {
            toolbar.setTitle("ADD MOOD");
        }

        et_switch_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        setSpinnerValue(device_iconsArray);

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_device_type.performClick();
            }
        });

        getDeviceList();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    ArrayList<String> flags = new ArrayList<>();

    private void setSpinnerValue(JSONArray device_iconsArray) {
        try {

            ChatApplication.logDisplay("setSpinnerValue  setSpinnerValue " + device_iconsArray.length());
            flags = new ArrayList<String>();
            for (int i = 0; i < device_iconsArray.length(); i++) {
                try {
                    flags.add(device_iconsArray.getJSONObject(i).getString("device_icon_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this, flags, 1, true);
            sp_device_type.setAdapter(customAdapter);

            if (moodVO != null) {

                for (int i = 0; i < device_iconsArray.length(); i++) {
                    try {
                        if (moodVO.getRoom_icon() != null) {

                            if (device_iconsArray.getJSONObject(i).getString("device_icon_name").equalsIgnoreCase(moodVO.getRoom_icon())) {
                                sp_device_type.setSelection(i);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            ChatApplication.logDisplay("action_save");

            try {
                room_device_id = deviceListLayoutHelper.getSelectedItemIds();
                deviceVOArrayList = deviceListLayoutHelper.getSelectedItemList();

                if (spinner_mood_icon.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Select Mood", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (deviceVOArrayList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Select atleast one Switch ", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (editMode) {
                    saveMood();
                } else {
                    saveMood();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /// all webservice call below.
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        roomList = new ArrayList<>();
        ChatApplication app = (ChatApplication) getApplication();
        String webUrl = app.url;

        String url = webUrl + Constants.GET_MOOD_DETAILS; //get mood name list with the mood icon name / not display sensor panel

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //clear moodIcon list
        if (moodIconList != null)
            moodIconList.clear();

        ChatApplication.logDisplay("add mood is " + url + "     " + jsonObject.toString());

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
                    moodList.clear();

                    if (dataObject.has("moodNames")) {

                        JSONArray moodNamesArray = dataObject.getJSONArray("moodNames");

                        RoomVO roomVO1 = new RoomVO();
                        roomVO1.setRoomId("");
                        roomVO1.setRoomName("Select Mood");
                        moodIconList.add(0, roomVO1);

                        if (editMode) {
                            RoomVO roomVO2 = new RoomVO();
                            roomVO2.setRoomId(moodVO.getModule_id());
                            roomVO2.setRoomName(moodVO.getRoomName());
                            moodIconList.add(1, roomVO2);
                        }

                        for (int i = 0; i < moodNamesArray.length(); i++) {
                            JSONObject moodObject = moodNamesArray.getJSONObject(i);
                            String moodId = moodObject.getString("id");
                            String moodName = moodObject.getString("mood_name");

                            RoomVO roomVO = new RoomVO();
                            roomVO.setRoomId(moodId);
                            roomVO.setRoomName(moodName);
                            moodList.add(moodName);
                            moodIconList.add(roomVO);

                        }


                        /**
                         *  mood icon dropdown spinner...
                         */
                        moodIconArrayAdapter = new RoomListArrayAdapter(AddMoodActivity.this, R.layout.row_spinner_item, R.id.txt_spinner_title,
                                moodIconList, "");
                        spinner_mood_icon.setAdapter(moodIconArrayAdapter);

                        if (moodVO != null) {
                            for (int i = 0; i < moodIconList.size(); i++) {
                                RoomVO roomVO = moodIconList.get(i);
                                if (roomVO.getRoomName().equalsIgnoreCase(moodVO.getRoomName())) {
                                    spinner_mood_icon.setSelection(i);
                                }
                            }
                        }

                        final ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, moodList);
                        et_switch_name.setAdapter(adapter);


                        et_switch_name.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                et_switch_name.showDropDown();
                                return false;
                            }
                        });

                    }

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseRoomArray(roomArray, true);

                    setData(roomList);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void sortList(final List<RoomVO> roomVOs) {

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded, o1.isExpanded);
            }
        });
    }


    public void setData(ArrayList<RoomVO> roomList) {

        if (moodVO != null) {

            List<String> deviceVOList = moodVO.getRoomDeviceIdList();

            for (RoomVO roomVO : roomList) {

                List<PanelVO> panelVOList = roomVO.getPanelList();

                for (PanelVO panelVO : panelVOList) {
                    ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

                    for (DeviceVO deviceVO : deviceList) {

                        ChatApplication.logDisplay("Name : " + deviceVO.getDeviceName() + " id : " + deviceVO.getRoomDeviceId());

                        for (String deviceVORoot : deviceVOList) { ////select original devices

                            if (deviceVO.getSensor_type() != null && deviceVO.getSensor_type().equalsIgnoreCase("remote")) { //if device type sensor than compare sensor id instead of room device id

                                if (deviceVO.getSensor_id().equalsIgnoreCase(deviceVORoot) || deviceVO.getRoomDeviceId().equalsIgnoreCase(deviceVORoot)) {

                                    roomVO.setExpanded(true);
                                    deviceVO.setSelected(true);
                                }
                            } else {

                                if (deviceVO.getRoomDeviceId().equalsIgnoreCase(deviceVORoot)) {
                                    roomVO.setExpanded(true);
                                    deviceVO.setSelected(true);
                                }
                            }
                        }
                    }

                }
            }
        }

        /**sensor unselect condition */
        for (int i = 0; i < roomList.size(); i++) {
            for (int j = 0; j < roomList.get(i).getPanelList().size(); j++) {

                if (roomList.get(i).getPanelList().get(j).getDeviceList().size() == 0) {
                    roomList.get(i).getPanelList().get(j).setPanelName("");
                }

                for (int k = 0; k < roomList.get(i).getPanelList().get(j).getDeviceList().size(); k++) {
                    if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).isSensor()) {
                        if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("temp")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        } else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("door")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        } else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getTo_use().equalsIgnoreCase("0")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }
                    }
                }
            }
        }


        //sort room list vie selected device list available
        sortList(roomList);

        deviceListLayoutHelper = new MoodDeviceListLayoutHelper(this, mMessagesView, this, Constants.SWITCH_NUMBER, isMoodAdapter);
        deviceListLayoutHelper.addSectionList(roomList);
        deviceListLayoutHelper.notifyDataSetChanged();

    }


    public void saveMood() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        arrayListRoomId.clear();
        ChatApplication app = (ChatApplication) getApplication();
        String webUrl = app.url;
        String url = "";

        ActivityHelper.showProgressDialog(AddMoodActivity.this, "Please Wait...", false);

        if (editMode) {
            url = webUrl + Constants.SAVEEDITMOOD;
        } else {
            url = webUrl + Constants.ADD_NEW_MOOD_NEW;
        }
        deviceVOArrayListTemp.clear();
        deviceVOArrayListTemp.addAll(deviceVOArrayList);
        deviceVOArrayList.clear();
        deviceVOArrayList = removeDuplicates(deviceVOArrayListTemp);
        JSONObject moodObj = new JSONObject();
        try {

            moodObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            moodObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            moodObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            moodObj.put("admin", Common.getPrefValue(this, Constants.USER_ADMIN_TYPE));
            RoomVO room = (RoomVO) spinner_mood_icon.getSelectedItem();

            if (editMode) {
                moodObj.put("room_id", moodVO.getRoomId());
                moodObj.put("panel_id", panel_id);
                moodObj.put("panel_name", panel_name);
            } else {
                moodObj.put("room_id", "");
                moodObj.put("panel_id", "");
            }

            moodObj.put("is_custom", 0);

            moodObj.put("room_name", room.getRoomName());
            moodObj.put("room_icon", "home");

            JSONArray jsonArrayDevice = new JSONArray();
            String ss_room_devices = "";

            JSONArray array = new JSONArray();

            for (DeviceVO dPanel : deviceVOArrayList) {

                JSONObject ob1 = new JSONObject();

                //  "room_id": "1562588968606_HfJ0o0Akn",
                //                  "room_device_id": "1563890900351_1kjJTcI1k",
                //                  "module_id": "SpikeBot10355128",
                //                  "panel_id": "1563877989328_SY8_lcy1d",
                //                  "room_panel_id": "1563877989328_SY8_lcy1d",
                //                  "device_id": "1563890900368_RYEYDrXcyI",
                //                  "device_name": "final",
                //                  "device_icon": "Remote_AC",
                //                  "device_status": 1,
                //                  "device_specific_value": 1,
                //                  "is_locked": 0,
                //                  "device_type": 2,
                //                  "original_room_device_id": "1563890900351_1kjJTcI1k",
                //                  "is_active": 1,
                //                  "is_alive": 1,
                //                  "is_original": 1,
                //                  "mode": "LOW",
                //                  "temperature": 20,
                //                  "power": "ON"
                if (dPanel.getDeviceType() != null && dPanel.getDeviceType().equalsIgnoreCase("2")) { //old : AC
                    arrayListRoomId.add(dPanel.getRoomId());
                    ob1.put("room_id", dPanel.getRoomId());
                    ob1.put("room_device_id", dPanel.getRoomDeviceId());
                    ob1.put("module_id", dPanel.getModuleId());
                    ob1.put("panel_id", dPanel.getPanel_id());
                    ob1.put("sensor_id", dPanel.getSensor_id());
                    ob1.put("device_id", dPanel.getDeviceId());
                    ob1.put("ir_blaster_id", dPanel.getIr_blaster_id());
                    ob1.put("device_name", dPanel.getDeviceName());
                    ob1.put("is_locked", dPanel.getIs_locked());
                    ob1.put("is_active", dPanel.getIsActive());
                    ob1.put("is_alive", dPanel.getIsAlive());

                    ob1.put("device_type", dPanel.getDeviceType());
                    ob1.put("is_active", dPanel.getIsActive());
                    ob1.put("sensor_icon", dPanel.getSensor_icon());
                    ob1.put("module_id", dPanel.getModuleId());
                    ob1.put("is_original", dPanel.getIs_original());

                    ob1.put("original_room_device_id", dPanel.getOriginal_room_device_id());
                    ob1.put("room_device_id", dPanel.getRoomDeviceId());
                    ob1.put("device_icon", dPanel.getDevice_icon());

                    ob1.put("device_status", dPanel.getDeviceStatus());
                    ob1.put("device_specific_value", dPanel.getDeviceSpecificValue());
                    ob1.put("remote_device_id", dPanel.getRemote_device_id()); //added  : 17-9-2018
                    ob1.put("room_panel_id", dPanel.getRoom_panel_id());

                } else {
                    arrayListRoomId.add(dPanel.getRoomId());
                    ob1.put("room_id", dPanel.getRoomId());
                    ob1.put("module_id", dPanel.getModuleId());
                    ob1.put("device_id", dPanel.getDeviceId());
                    ob1.put("room_device_id", dPanel.getRoomDeviceId());

                    ob1.put("device_icon", dPanel.getDevice_icon());
                    ob1.put("device_name", dPanel.getDeviceName());
                    ob1.put("device_status", dPanel.getDeviceStatus());
                    ob1.put("device_specific_value", dPanel.getDeviceSpecificValue());
                    ob1.put("device_type", dPanel.getDeviceType());

                    ob1.put("original_room_device_id", dPanel.getOriginal_room_device_id());
                    ob1.put("is_active", dPanel.getIsActive());
                    ob1.put("is_alive", dPanel.getIsAlive());
                    ob1.put("is_original", dPanel.getIs_original());
                    ob1.put("room_panel_id", dPanel.getRoom_panel_id());
                }
                jsonArrayDevice.put(ob1);
                try {
                    array.put(dPanel.getRoomDeviceId()); //remove last comma of String index @String str = "abc,xyz,"
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (editMode) {
                moodObj.put("room_devices", array);
            }
            moodObj.put("deviceList", jsonArrayDevice);
            JSONArray array1 = new JSONArray(countRoomLIst(arrayListRoomId));
            moodObj.put("room_List", array1);

            ChatApplication.logDisplay("hash code is ");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetJsonTask(this, url, "POST", moodObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {

                    ChatApplication.isMoodFragmentNeedResume = true;

                    ChatApplication.logDisplay("mood is " + result);

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                    if (code == 200) {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard = true;
                        ChatApplication.isRefreshMood = true;
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    @Override
    public void itemClicked(RoomVO item, String action) {
    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {
    }

    @Override
    public void itemClicked(DeviceVO deviceVO, String action) {
        if (action.equalsIgnoreCase("disable_device")) {
            getDeviceDetails(deviceVO.getOriginal_room_device_id());
        }
    }

    @Override
    public void itemClicked(DeviceVO deviceVO, String action, int position) {
        if (action.equalsIgnoreCase("disable_device")) {
            getDeviceDetails(deviceVO.getOriginal_room_device_id());
        }
    }

    @Override
    public void itemClicked(CameraVO cameraVO, String action) {
    }

    /**
     * Get room device details
     */
    private void getDeviceDetails(String original_room_device_id) {

        //original_room_device_id
        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl + Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id;

        new GetJsonTask2(AddMoodActivity.this, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("onSuccess :  " + result.toString());
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");
                        showDeviceDialog(room_name, panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error, int responseCode) {
                ChatApplication.logDisplay("onFailure " + error);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized void showDeviceDialog(String roomName, String panelName) {

        if (dialog == null) {
            dialog = new Dialog(AddMoodActivity.this);
        } else {
            dialog.show();
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_virtual_devices);

        TextView txtRoom = (TextView) dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel = (TextView) dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK = (Button) dialog.findViewById(R.id.vbtn_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
            }
        });


        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    public static List<DeviceVO> removeDuplicates(List<DeviceVO> list) {

        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<DeviceVO> listTemp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(list.get(i).getRoomDeviceId());
        }

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(arrayList);
        arrayList.clear();
        arrayList.addAll(hashSet);


        for (int j = 0; j < list.size(); j++) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).length() > 1 && list.get(j).getRoomDeviceId().equalsIgnoreCase(arrayList.get(i))) {
                    arrayList.set(i, arrayList.get(i) + "i");
                    listTemp.add(list.get(i));
                }
            }
        }

        return listTemp;
    }

    public static ArrayList<String> countRoomLIst(final ArrayList<String> list) {
        Set<String> setItems = new LinkedHashSet<String>(list);
        list.clear();
        list.addAll(setItems);
        return list;
    }
}
