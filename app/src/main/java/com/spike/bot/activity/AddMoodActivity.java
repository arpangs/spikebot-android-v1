package com.spike.bot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.RoomListArrayAdapter;
import com.spike.bot.adapter.mood.MoodDeviceListLayoutHelper;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.fragments.MoodFragment;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AddMoodActivity extends AppCompatActivity implements ItemClickMoodListener, ItemClickListener {

    public Dialog dialog = null;
    androidx.appcompat.widget.AppCompatAutoCompleteTextView et_switch_name;
    Spinner spinner_mood_icon; //for mood selection

    boolean editMode = false, isMap = false, isMoodAdapter = false;
    String room_device_id = "", panel_id, panel_name, select_mood_id = "";
    Button btn_mood_save;
    RoomVO moodVO = new RoomVO();
    RoomListArrayAdapter moodIconArrayAdapter;
    MoodDeviceListLayoutHelper deviceListLayoutHelper;
    List<DeviceVO> deviceVOArrayList;
    List<DeviceVO> deviceVOArrayListTemp = new ArrayList<>();
    ArrayList<RoomVO> roomList = new ArrayList<>();
    ArrayList<String> arrayListRoomId = new ArrayList<>();
    List<String> moodList = new ArrayList<>();
    MoodFragment moodFragment;
    private RecyclerView mMessagesView;
    private List<RoomVO> moodIconList = new ArrayList<>();

    /*remove duplicate device */
    public static List<DeviceVO> removeDuplicates(List<DeviceVO> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<DeviceVO> listTemp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(list.get(i).getPanel_device_id());
        }

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(arrayList);
        arrayList.clear();
        arrayList.addAll(hashSet);

        for (int j = 0; j < list.size(); j++) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).length() > 1 && list.get(j).getPanel_device_id().equalsIgnoreCase(arrayList.get(i))) {
                    arrayList.set(i, arrayList.get(i) + "i");
                    listTemp.add(list.get(i));
                }
            }
        }

        return listTemp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_add);
        setViewId();

    }

    public void setViewId() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mMessagesView = findViewById(R.id.messages);
        et_switch_name = findViewById(R.id.et_switch_name);
        spinner_mood_icon = findViewById(R.id.spinner_mood_icon);
        btn_mood_save = findViewById(R.id.btn_mood_save);

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
            toolbar.setTitle("Edit Mood");
        } else {
            toolbar.setTitle("Add Mood");
        }

        //  et_switch_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        getMoodNameList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            try {
                room_device_id = deviceListLayoutHelper.getSelectedItemIds();
                deviceVOArrayList = deviceListLayoutHelper.getSelectedItemList();

                /*if (spinner_mood_icon.getSelectedItemPosition() == 0) {
                    ChatApplication.showToast(AddMoodActivity.this, "Select Mood");
                    return true;
                }*/

                if (et_switch_name.getListSelection() == 0) {
                    ChatApplication.showToast(AddMoodActivity.this, "Select Mood");
                    return true;
                }

                if (deviceVOArrayList.size() == 0) {
                    ChatApplication.showToast(AddMoodActivity.this, "Select atleast one Switch ");
                    return true;
                }

                saveMood();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* set view of room list
     * roles
     * only remote data data another all type sensor ignore it..
     * */
    public void setData(ArrayList<RoomVO> roomList) {

        if (moodVO != null) {

            List<String> deviceVOList = new ArrayList<>();


            for (int i = 0; i < moodVO.getPanelList().size(); i++) {
                for (int j = 0; j < moodVO.getPanelList().get(i).getDeviceList().size(); j++) {
                    ChatApplication.logDisplay("panel id " + moodVO.getPanelList().get(i).getDeviceList().get(j).getPanel_device_id());
                    deviceVOList.add(moodVO.getPanelList().get(i).getDeviceList().get(j).getDeviceId());
                }
            }


            for (RoomVO roomVO : roomList) {

                List<PanelVO> panelVOList = roomVO.getPanelList();

                for (PanelVO panelVO : panelVOList) {
                    ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

                    for (DeviceVO deviceVO : deviceList) {

                        for (String deviceVORoot : deviceVOList) {
                            ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType());
                            if (deviceVO.getDeviceType().equalsIgnoreCase("remote")) { //if device type sensor than compare sensor id instead of room device id
                                ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType() + "   " + deviceVO.getDeviceId() + "   " + deviceVORoot);
                                if (deviceVO.getDeviceId().equalsIgnoreCase(deviceVORoot)) {

                                    roomVO.setExpanded(true);
                                    deviceVO.setSelected(true);
                                }
                            } else {

                                if (deviceVO.getDeviceId().equalsIgnoreCase(deviceVORoot)) {
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

                    if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).isSensor() && roomList.get(i).getPanelList().get(j).isActivePanel()) {
                        if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("temp_sensor") ||
                                roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_icon().equalsIgnoreCase("gas_sensor") ||
                                roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_icon().equalsIgnoreCase("door_sensor")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
//                            roomList.get(i).getPanelList().get(j).setActivePanel(false);
                        }
                    }
                }
            }
        }


        //sort room list vie selected device list available
        sortList(roomList);

       /* int spanCount = 2; // 3 columns
        int spacing = 100; // 50px
        boolean includeEdge = false;
        mMessagesView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing, includeEdge));
*/

        deviceListLayoutHelper = new MoodDeviceListLayoutHelper(this, mMessagesView, this, Constants.SWITCH_NUMBER, isMoodAdapter);
        deviceListLayoutHelper.addSectionList(roomList);
        deviceListLayoutHelper.notifyDataSetChanged();

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

    /*get mood name list*/
    private void getMoodNameList() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.getMoodName; //get mood name list with the mood icon name / not display sensor panel

        ChatApplication.logDisplay("add mood is " + url + " " + jsonObject);*/

       /* new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {

                    ChatApplication.logDisplay("mood list is " + result);
                    getDeviceList();

                    moodList.clear();
                    JSONArray moodNamesArray = new JSONArray(result.getString("data"));

               *//*     RoomVO roomVO1 = new RoomVO();
                    roomVO1.setRoomId("");
                   // roomVO1.setRoomName("Select Mood");
                    moodIconList.add(0, roomVO1);*//*

                    if (editMode) {
                        select_mood_id = moodVO.getMood_name_id();
                        RoomVO roomVO2 = new RoomVO();
                        roomVO2.setRoomId(moodVO.getMood_name_id());
                        roomVO2.setRoomName(moodVO.getRoomName());
                        moodIconList.add(roomVO2);
                    }

                    for (int i = 0; i < moodNamesArray.length(); i++) {
                        JSONObject moodObject = moodNamesArray.getJSONObject(i);
                        String moodId = moodObject.getString("mood_id");
                        String moodName = moodObject.getString("mood_name");

                        RoomVO roomVO = new RoomVO();
                        roomVO.setRoomId(moodId);
                        roomVO.setRoomName(moodName);
                        moodList.add(moodName);
                        moodIconList.add(roomVO);

                    }

                    *//**
         *  mood icon dropdown spinner...
         *//*
                    setDropDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1) + "mood name list");
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/


        SpikeBotApi.getInstance().GetMoodNameList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("mood list is " + result);
                    getDeviceList();

                    moodList.clear();
                    JSONArray moodNamesArray = new JSONArray(result.getString("data"));

                    if (editMode) {
                        select_mood_id = moodVO.getMood_name_id();
                        RoomVO roomVO2 = new RoomVO();
                        roomVO2.setRoomId(moodVO.getMood_name_id());
                        roomVO2.setRoomName(moodVO.getRoomName());
                        moodIconList.add( roomVO2);
                    }

                    for (int i = 0; i < moodNamesArray.length(); i++) {
                        JSONObject moodObject = moodNamesArray.getJSONObject(i);
                        String moodId = moodObject.getString("mood_id");
                        String moodName = moodObject.getString("mood_name");

                        RoomVO roomVO = new RoomVO();
                        roomVO.setRoomId(moodId);
                        roomVO.setRoomName(moodName);
                        moodList.add(moodName);
                        moodIconList.add(roomVO);

                    }
                    setDropDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /* room list getting  */
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }
//        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        /*String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("add mood is " + url + "     " + jsonObject.toString());*/

      /*  new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ChatApplication.logDisplay("mood list is room " + result);
                    JSONObject dataObject = result.getJSONObject("data");
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
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1) + "device list");
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetDeviceList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("mood list is room " + result);
                    JSONObject dataObject = result.getJSONObject("data");
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
            public void onData_FailureResponse() {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    /*add mood web service*/
    public void saveMood() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }

        arrayListRoomId.clear();
//        String url = "";

        ActivityHelper.showProgressDialog(AddMoodActivity.this, "Please Wait...", false);


        deviceVOArrayListTemp.clear();
        deviceVOArrayListTemp.addAll(deviceVOArrayList);
        deviceVOArrayList.clear();
        deviceVOArrayList = removeDuplicates(deviceVOArrayListTemp);

        HashMap<String, Object> moodObj = new HashMap<>();
        try {
            if (editMode) {
                /*old_mood_id : last mood_id
                mood_id select latest mood_id send
                * */
                moodObj.put("mood_id", select_mood_id);
                moodObj.put("old_mood_id", moodVO.getRoomId());
            } else {
                moodObj.put("mood_name_id", select_mood_id);
            }
            moodObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            moodObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            moodObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            ArrayList<String> deviceIdList = new ArrayList<>();
            for (DeviceVO dPanel : deviceVOArrayList) {
                deviceIdList.add(dPanel.getPanel_device_id());
            }

            JSONArray array = new JSONArray(deviceIdList);
            moodObj.put("panel_device_ids", array);
//

       /* ChatApplication.logDisplay("modd add code is " + url + "  " + moodObj);
        new GetJsonTask(this, url, "POST", moodObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    resultMood(result);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1) + "save mood");
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/


            SpikeBotApi.getInstance().SaveMood(editMode, moodObj, new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    try {
                        JSONObject result = new JSONObject(stringResponse);
                        ActivityHelper.dismissProgressDialog();
                        resultMood(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ActivityHelper.dismissProgressDialog();
                    }
                }

                @Override
                public void onData_FailureResponse() {
                    ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                    ActivityHelper.dismissProgressDialog();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                    ActivityHelper.dismissProgressDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resultMood(JSONObject result) {
        ChatApplication.isMoodFragmentNeedResume = true;

        ChatApplication.logDisplay("mood is " + result);

        int code = 0;
        try {
            code = result.getInt("code");
            String message = result.getString("message");
            if (!TextUtils.isEmpty(message)) {
                ChatApplication.showToast(AddMoodActivity.this, message);
            }

            if (code == 200) {
                ActivityHelper.dismissProgressDialog();
                //ChatApplication.isRefreshDashBoard = true;
                ChatApplication.isRefreshMood = true;
                ChatApplication.CurrnetFragment = R.id.navigationMood;  // dev arpan on 15 june 2020
                startActivity(new Intent(this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // dev arpan on 15 june 2020
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * Get room device details
     */
    private void getDeviceDetails(String original_room_device_id) {

       /* String url = ChatApplication.url + Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id;

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
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().GetDeviceDetails_Schedule(ChatApplication.url, original_room_device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("onSuccess :  " + result.toString());
                    int code = 0;
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
            public void onData_FailureResponse() {
                ChatApplication.logDisplay("onFailure ");
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("onFailure " + error);
            }
        });
    }

    private void setDropDown() {
        moodIconArrayAdapter = new RoomListArrayAdapter(AddMoodActivity.this, R.layout.row_spinner_item, R.id.txt_spinner_title, moodIconList, "");
        spinner_mood_icon.setAdapter(moodIconArrayAdapter);

        if (moodVO != null) {
            for (int i = 0; i < moodIconList.size(); i++) {
                RoomVO roomVO = moodIconList.get(i);
                if (roomVO.getRoomName().equalsIgnoreCase(moodVO.getRoomName())) {
                    spinner_mood_icon.setSelection(i);
                }
            }
        }

       /* et_switch_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                moodIconArrayAdapter = new RoomListArrayAdapter(AddMoodActivity.this, R.layout.row_spinner_item, R.id.txt_spinner_title, moodIconList, "");
                et_switch_name.setAdapter(moodIconArrayAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, moodList);
        et_switch_name.setAdapter(adapter);

        if (moodVO != null) {
            for (int i = 0; i < moodIconList.size(); i++) {
                RoomVO roomVO = moodIconList.get(i);
                if (roomVO.getRoomName().equalsIgnoreCase(moodVO.getRoomName())) {
                    et_switch_name.setSelection(i);
                }
            }
        }

     /*   moodIconArrayAdapter = new RoomListArrayAdapter(AddMoodActivity.this, R.layout.row_spinner_item, R.id.txt_spinner_title, moodIconList, "");
        et_switch_name.setAdapter(moodIconArrayAdapter);*/


        et_switch_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_switch_name.showDropDown();
                return false;
            }
        });

        et_switch_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           /* @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                select_mood_id=moodIconList.get(position).getRoomId();
            }*/

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String selectedItem;
                selectedItem = moodIconList.get(position).getRoomId();
                select_mood_id = selectedItem;
                ChatApplication.logDisplay("Selected ITEM id " + " " + selectedItem);
            }
        });


     /*   spinner_mood_icon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_mood_id=moodIconList.get(position).getRoomId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    private void sortList(final List<RoomVO> roomVOs) {

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded, o1.isExpanded);
            }
        });
    }

    // disable device to show dialog
    public synchronized void showDeviceDialog(String roomName, String panelName) {

        if (dialog == null) {
            dialog = new Dialog(AddMoodActivity.this);
        } else {
            dialog.show();
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_virtual_devices);

        TextView txtRoom = dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel = dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK = dialog.findViewById(R.id.vbtn_ok);

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

    // dev arp for navigation issue on 15 june 2020
    @Override
    public void onBackPressed() {
        ChatApplication.CurrnetFragment = R.id.navigationMood;  // dev arpan on 15 june 200
        startActivity(new Intent(this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // dev arpan on 15 june 2020
        super.onBackPressed();
    }
}
