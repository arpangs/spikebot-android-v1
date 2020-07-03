package com.spike.bot.Beacon;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.adapter.mood.MoodDeviceListLayoutHelper;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class BeaconConfigActivity extends AppCompatActivity implements ItemClickMoodListener, ItemClickListener {

    private RecyclerView mMessagesView;
    TextView txt_emptyview;
    AppCompatAutoCompleteTextView et_beacon_name;
    MoodDeviceListLayoutHelper deviceListLayoutHelper;
    RoomVO roomVO = new RoomVO();
    ArrayList<RoomVO> roomList = new ArrayList<>();
    List<DeviceVO> deviceVOArrayList;
    List<DeviceVO> deviceVOArrayListTemp = new ArrayList<>();
    IRBlasterAddRes.Datum beaconmodel;
    boolean editBeacon = false, isMap = false, isBeaconListAdapter = false, isunassign = false;

    String mScannername, mRoomname, mRoomid, mSensorid, mScannerdeviceid, mdevicetype,
            moduleid, beaconname, beaconaddress, panel_id;
    int beaconrssi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_config);

        mScannername = getIntent().getStringExtra("SCANNER_NAME");
        mRoomname = getIntent().getStringExtra("ROOM_NAME");
        mRoomid = getIntent().getStringExtra("ROOM_ID");
        mSensorid = getIntent().getStringExtra("SENSOR_ID");
        mScannerdeviceid = getIntent().getStringExtra("SCANNER_DEVICE_ID");
        //  mdevicetype = getIntent().getStringExtra("SCANNER_DEVICE_TYPE");
        moduleid = getIntent().getStringExtra("BEACON_MODULE_ID");
        beaconname = getIntent().getStringExtra("BEACON_DEVICE_NAME");
        beaconaddress = getIntent().getStringExtra("SCANNER_DEVICE_ADDRESS");
        beaconrssi = getIntent().getIntExtra("BEACON_RSSI", 0);
        mdevicetype = getIntent().getStringExtra("DEVICE_TYPE");
        isunassign = getIntent().getExtras().getBoolean("isUnassign", false);

        try {
            editBeacon = getIntent().getExtras().getBoolean("editBeacon", false);
            beaconmodel = (IRBlasterAddRes.Datum) getIntent().getExtras().getSerializable("beaconmodel");
            isMap = getIntent().getBooleanExtra("isMap", false);
            isBeaconListAdapter = getIntent().getBooleanExtra("isBeaconListAdapter", false);
            panel_id = getIntent().getStringExtra("panel_id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        setViewId();
        getDeviceList();
    }

    public void setViewId() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mMessagesView = findViewById(R.id.recycler_room);
        et_beacon_name = findViewById(R.id.et_beacon_name);

        if (beaconmodel != null) {
            et_beacon_name.setText(beaconmodel.getDeviceName());
        }

        if (!TextUtils.isEmpty(beaconname)) {
            et_beacon_name.setText(beaconname);
        }

        et_beacon_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                beaconname = et_beacon_name.getText().toString();
            }
        });

        if (editBeacon) {
            et_beacon_name.setEnabled(true);
            beaconname = et_beacon_name.getText().toString();
            toolbar.setTitle("Edit Group");
        } else {
            if (isunassign) {
                et_beacon_name.setEnabled(true);
                beaconname = et_beacon_name.getText().toString();
                toolbar.setTitle("Add Group");
            } else {
                et_beacon_name.setEnabled(false);
                toolbar.setTitle("Add Group");
            }
        }
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
                //  room_device_id = deviceListLayoutHelper.getSelectedItemIds();
                deviceVOArrayList = deviceListLayoutHelper.getSelectedItemList();


                if (et_beacon_name.getText().toString().length() == 0) {
                    ChatApplication.showToast(BeaconConfigActivity.this, "Please enter beacon name");

                } else if (deviceVOArrayList.size() == 0) {
                    ChatApplication.showToast(BeaconConfigActivity.this, "Select atleast one device ");
                    return true;
                } else {
                    saveBeacon(et_beacon_name.getText().toString());
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* room list getting  */
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getBeaconRoomList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    if (roomArray != null && roomArray.length() > 0) {
                        //  mMessagesView.setVisibility(View.VISIBLE);
                        //    txt_emptyview.setVisibility(View.GONE);
                        roomList = JsonHelper.parseRoomArray(roomArray, true);
                        setData(roomList);
                    } else {
                        //  txt_emptyview.setVisibility(View.VISIBLE);
                        //  mMessagesView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
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
     * save Beacon
     */
    private void saveBeacon(String beaconname) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        deviceVOArrayListTemp.clear();
        deviceVOArrayListTemp.addAll(deviceVOArrayList);
        deviceVOArrayList.clear();
        deviceVOArrayList = removeDuplicates(deviceVOArrayListTemp);

        ArrayList<String> deviceIdList = new ArrayList<>();
        for (DeviceVO dPanel : deviceVOArrayList) {
            deviceIdList.add(dPanel.getPanel_device_id());
        }

        JSONArray array = new JSONArray(deviceIdList);

        ActivityHelper.showProgressDialog(this, "Please wait.", true);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveBeacon(beaconname, beaconmodel.getDeviceId(), moduleid, array, editBeacon, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Common.showToast(message);
                            //finish();

                            Intent i = new Intent(BeaconConfigActivity.this,BeaconListActivity.class);
                            startActivity(i);
                            finish();


                        }
                        ActivityHelper.dismissProgressDialog();

                    } else {
                        Common.showToast(message);
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


    /* set view of room list
     * roles
     * only remote data data another all type sensor ignore it..
     * */
    public void setData(ArrayList<RoomVO> roomList) {

        if (roomVO != null) {

            List<String> deviceVOList = new ArrayList<>();


            for (int i = 0; i < roomVO.getPanelList().size(); i++) {
                for (int j = 0; j < roomVO.getPanelList().get(i).getDeviceList().size(); j++) {
                    ChatApplication.logDisplay("panel id " + roomVO.getPanelList().get(i).getDeviceList().get(j).getPanel_device_id());
                    deviceVOList.add(roomVO.getPanelList().get(i).getDeviceList().get(j).getDeviceId());
                }
            }


            for (RoomVO roomVO : roomList) {

                List<PanelVO> panelVOList = roomVO.getPanelList();
                List<String> beaconlist = null;
                if (editBeacon) {
                    beaconlist = beaconmodel.getRelatedDevices();

                    for (PanelVO panelVO : panelVOList) {
                        ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

                        for (DeviceVO deviceVO : deviceList) {

                            for (String deviceVORoot : beaconlist) {
                                ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType());
                                if (deviceVO.getDeviceType().equalsIgnoreCase("remote")
                                || deviceVO.getDeviceType().equalsIgnoreCase("heavyload")) { //if device type sensor than compare sensor id instead of room device id
                                    ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType() + "   " + deviceVO.getDeviceId() + "   " + deviceVORoot);
                                    if (deviceVO.getPanel_device_id().equalsIgnoreCase(deviceVORoot)) {
                                        roomVO.setExpanded(true);
                                        deviceVO.setSelected(true);
                                    }
                                } else {

                                    if (deviceVO.getPanel_device_id().equalsIgnoreCase(deviceVORoot)) {
                                        roomVO.setExpanded(true);
                                        deviceVO.setSelected(true);
                                    }
                                }
                            }
                        }

                    }
                } else {
                    for (PanelVO panelVO : panelVOList) {
                        ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

                        for (DeviceVO deviceVO : deviceList) {

                            for (String deviceVORoot : deviceVOList) {
                                ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType());
                                if (deviceVO.getDeviceType().equalsIgnoreCase("remote")) { //if device type sensor than compare sensor id instead of room device id
                                    ChatApplication.logDisplay("panel id is " + deviceVO.getDeviceType() + "   " + deviceVO.getDeviceId() + "   " + deviceVORoot);
                                    if (deviceVO.getDeviceId().equalsIgnoreCase(deviceVORoot)) {

                                     //   roomVO.setExpanded(true);
                                     //   deviceVO.setSelected(true);
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
        }
        sortList(roomList);

        deviceListLayoutHelper = new MoodDeviceListLayoutHelper(BeaconConfigActivity.this, mMessagesView, this, Constants.SWITCH_NUMBER, isBeaconListAdapter);
        deviceListLayoutHelper.addSectionList(roomList);
        deviceListLayoutHelper.notifyDataSetChanged();

    }

    private void sortList(final List<RoomVO> roomVOs) {

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded, o1.isExpanded);
            }
        });
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

    /*
     * Get room device details
     */
    private void getDeviceDetails(String original_room_device_id) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getDeviceDetails(original_room_device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");
                        //  showDeviceDialog(room_name, panel_name);
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
}
