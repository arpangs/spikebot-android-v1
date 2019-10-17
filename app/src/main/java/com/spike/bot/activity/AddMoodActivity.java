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
import android.widget.AdapterView;
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

    private RecyclerView mMessagesView;
    AutoCompleteTextView et_switch_name;
    Spinner spinner_mood_icon; //for mood selection

    boolean editMode = false, isMap = false, isMoodAdapter = false;
    String room_device_id = "", panel_id, panel_name,select_mood_id="";

    RoomVO moodVO = new RoomVO();
    public Dialog dialog = null;
    RoomListArrayAdapter moodIconArrayAdapter;
    MoodDeviceListLayoutHelper deviceListLayoutHelper;
    List<DeviceVO> deviceVOArrayList;
    List<DeviceVO> deviceVOArrayListTemp = new ArrayList<>();
    ArrayList<RoomVO> roomList = new ArrayList<>();
    ArrayList<String> arrayListRoomId = new ArrayList<>();
    List<String> moodList = new ArrayList<>();
    private List<RoomVO> moodIconList = new ArrayList<>();

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

        getMoodNameList();
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

                if (spinner_mood_icon.getSelectedItemPosition() == 0) {
                    ChatApplication.showToast(AddMoodActivity.this, "Select Mood");
                    return true;
                }

                if (deviceVOArrayList.size() == 0) {
                    ChatApplication.showToast(AddMoodActivity.this, "Select atleast one Switch ");
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

    /* set view of room list
     * roles
     * only remote data data another all type sensor ignore it..
     * */
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
                    if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).isSensor() &&
                            roomList.get(i).getPanelList().get(j).isActivePanel()) {
                        if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("temp_sensor")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        } else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_icon().equalsIgnoreCase("door_sensor")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_icon().equalsIgnoreCase("door_sensor")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        } else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_icon().equalsIgnoreCase("gas_sensor")) {
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }
//                        else if (roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getTo_use().equalsIgnoreCase("0")) {
//                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
//                        }
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

        String url = ChatApplication.url + Constants.getMoodName; //get mood name list with the mood icon name / not display sensor panel

        ChatApplication.logDisplay("add mood is " + url);

        new GetJsonTask(this, url, "POST", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {

                    getDeviceList();

                    moodList.clear();
                    //"data": [
                    //        {
                    //            "id": 3,
                    //            "mood_name": "lobby",
                    //            "is_active": 1
                    //        },
                    //        {
                    JSONArray moodNamesArray = new JSONArray(result.getString("data"));

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
                    setDropDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /* room list getting  */
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }
//        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("add mood is " + url + "     " + jsonObject.toString());

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
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
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    public void saveMood() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }

        arrayListRoomId.clear();
        String url = "";

        ActivityHelper.showProgressDialog(AddMoodActivity.this, "Please Wait...", false);

        if (editMode) {
            url = ChatApplication.url + Constants.SAVEEDITMOOD;
        } else {
            url = ChatApplication.url + Constants.ADD_NEW_MOOD_NEW;
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

            if (editMode) {
                moodObj.put("room_id", moodVO.getRoomId());
                moodObj.put("panel_id", panel_id);
                moodObj.put("panel_name", panel_name);
            } else {
//                moodObj.put("room_id", "");
//                moodObj.put("panel_id", "");
            }


            ArrayList<String> deviceIdList = new ArrayList<>();
            for (DeviceVO dPanel : deviceVOArrayList) {
                //  {
                //	"mood_name_id": 3,
                //	"user_id": "1568463607921_AyMe7ek9e",
                //	"panel_device_ids": ["1571149643833_GeqT4vQuUG","1571149643852_R0NbfvM_Yx"]
                //}

                deviceIdList.add(dPanel.getDeviceId());
            }

            JSONArray array = new JSONArray(deviceIdList);
            moodObj.put("panel_device_ids", array);
            moodObj.put("mood_name_id", select_mood_id);

            ChatApplication.logDisplay("hash code is " + moodObj);



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
                        ChatApplication.showToast(AddMoodActivity.this, message);
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
                ChatApplication.showToast(AddMoodActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /*
     * Get room device details
     */
    private void getDeviceDetails(String original_room_device_id) {

        String url = ChatApplication.url + Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id;

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

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, moodList);
        et_switch_name.setAdapter(adapter);


        et_switch_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_switch_name.showDropDown();
                return false;
            }
        });

        spinner_mood_icon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_mood_id=moodIconList.get(position).getRoomId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

}
