package com.spike.bot.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.AddUnassignedPanelAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.UnassignedPanelRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddUnassignedPanel extends AppCompatActivity implements AddUnassignedPanelAdapter.UnassignedClickEvent {

    private RecyclerView mListPanels;
    private AddUnassignedPanelAdapter addUnassignedPanelAdapter;
    private List<UnassignedListRes.Data.RoomList> roomList;
    private LinearLayout mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unassigned_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Unassigned List");

        bindViews();
        getUnAssignedList();

    }

    private void bindViews(){
        mListPanels = (RecyclerView) findViewById(R.id.list_un_panel);
        mListPanels.setLayoutManager(new GridLayoutManager(this,1));
        mEmptyView = (LinearLayout) findViewById(R.id.txt_empty_list);
        mEmptyView.setVisibility(View.GONE);

    }

    List<UnassignedListRes.Data.RoomdeviceList> roomdeviceList;
    private void getUnAssignedList(){


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+R.string.disconnect);
            return;
        }

        roomdeviceList = new ArrayList<>();

        ActivityHelper.showProgressDialog(this,"Loading...",false);

        String url = ChatApplication.url + Constants.GET_ALL_UNASSIGNED_DEVICES;

        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        if(!TextUtils.isEmpty(message)){
                            //showToast(message);
                        }

                        UnassignedListRes unassignedListRes = Common.jsonToPojo(result.toString(),UnassignedListRes.class);
                        roomList = unassignedListRes.getData().getRoomList();
                        roomdeviceList = unassignedListRes.getData().getRoomdeviceList();
                        addUnassignedPanelAdapter = new AddUnassignedPanelAdapter(roomdeviceList,AddUnassignedPanel.this);
                        mListPanels.setAdapter(addUnassignedPanelAdapter);

                    }else{
                        if(!TextUtils.isEmpty(message)){
                            showToast(message);
                        }
                    }

                    /**
                     * set empty view visibility if room device list found equal to 0
                     */
                    if(roomdeviceList.size() == 0){
                        mListPanels.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    }else {
                        mListPanels.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onClick
     * @param position position adapter
     * @param roomdeviceList roomDeviceList
     */

    @Override
    public void onClick(int position, UnassignedListRes.Data.RoomdeviceList roomdeviceList) {
        showAddDialog(roomdeviceList);
    }

    private Dialog mDialog;
    private Spinner mSpinnerRoom;
    private EditText mPanelName;
    private Button mBtnSave;
    private ImageView mImageClose;
    private ArrayList<String> roomStrList = new ArrayList<>();
    private ArrayList<String> roomIdList = new ArrayList<>();

    private void showAddDialog(final UnassignedListRes.Data.RoomdeviceList roomdeviceList){

        roomStrList.clear();
        roomIdList.clear();

        if(mDialog == null){
            mDialog = new Dialog(AddUnassignedPanel.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_add_un_panel);
            mDialog.setCanceledOnTouchOutside(false);
        }

        mSpinnerRoom = (Spinner) mDialog.findViewById(R.id.spinner_room_name);
        mPanelName = (EditText) mDialog.findViewById(R.id.et_panel_name);
        mBtnSave = (Button) mDialog.findViewById(R.id.btn_save);
        mImageClose = (ImageView)mDialog.findViewById(R.id.iv_close);
        TextView mAddName = (TextView) mDialog.findViewById(R.id.tv_panel_name);



        mPanelName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mPanelName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        if(roomdeviceList.getIsModule() == 1){
            mAddName.setText("Panel Name");
            mPanelName.setText(roomdeviceList.getModuleName());
        }else{
            mAddName.setText("Sensor Name");
            mPanelName.setText(roomdeviceList.getSensorName());
        }

        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        if(roomList!=null){
            for(UnassignedListRes.Data.RoomList roomListModel : roomList){
                roomStrList.add(roomListModel.getRoomName());
                roomIdList.add(roomListModel.getRoomId());
            }

            roomStrList.add(0,"Select Room Name");
            roomIdList.add(0,"0");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_panel,roomStrList);
            mSpinnerRoom.setAdapter(adapter);

        }

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePanel(roomdeviceList,roomIdList.get(mSpinnerRoom.getSelectedItemPosition()),mPanelName.getText().toString().trim());
            }
        });


        if(!mDialog.isShowing()){
            mDialog.show();
        }
    }

    /**
     * Save panel
     * @param roomdeviceList roomDeviceList
     * @param roomId roomId
     * @param panelName panelName
     */
    private void savePanel(UnassignedListRes.Data.RoomdeviceList roomdeviceList, String roomId, String panelName){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        if(mSpinnerRoom.getSelectedItemPosition() == 0){
            showToast("Please select Room Name");
            return;
        }

        if(TextUtils.isEmpty(mPanelName.getText().toString().trim())){
            mPanelName.setError("Enter Panel Name");
            mPanelName.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(this,"Loading...",false);

        String mRequestJson = "";
        final boolean isSensor = roomdeviceList.getIsModule() == 0;

        JSONObject object = new JSONObject();
            try {
                if(roomdeviceList.getIsModule()==0){
                    object.put("sensor_name",panelName);
                }else {
                    object.put("panel_name",panelName);
                }
                object.put("is_module",roomdeviceList.getIsModule());
                object.put("room_id",roomId);
                object.put("sensor_id",roomdeviceList.getSensorId());
              //  object.put("sensor_name",panelName);
                object.put("room_name",mSpinnerRoom.getSelectedItem().toString());
                object.put("module_id",roomdeviceList.getModuleId());
                object.put("sensor_type",roomdeviceList.getSensorType());
                object.put("sensor_icon",roomdeviceList.getSensorIcon());
                object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mRequestJson = object.toString();

//        if(roomdeviceList.getIsModule() == 1){
//
//            UnassignedPanelRes unassignedPanelRes = new UnassignedPanelRes();
//            unassignedPanelRes.setIsModule(roomdeviceList.getIsModule());
//            unassignedPanelRes.setPanelName(panelName);
//            unassignedPanelRes.setRoomId(roomId);
//
//            List<UnassignedPanelRes.DeviceList> unPanelList = new ArrayList<>();
//            List<UnassignedListRes.Data.RoomdeviceList.DeviceList> deviceLists = roomdeviceList.getDeviceList();
//
//            for(UnassignedListRes.Data.RoomdeviceList.DeviceList device : deviceLists){
//                UnassignedPanelRes.DeviceList dList = new UnassignedPanelRes.DeviceList();
//                dList.setModuleId(device.getModuleId());
//                dList.setDeviceId(device.getDeviceId());
//                dList.setDeviceIcon(device.getDeviceIcon());
//                dList.setDeviceName(device.getDeviceName());
//                dList.setModuleName(device.getModuleName());
//                dList.setOriginalRoomDeviceId(device.getOriginalRoomDeviceId());
//                dList.setRoomDeviceId(device.getRoomDeviceId());
//                dList.setPanelId(device.getPanelId());
//                dList.setDeviceStatus(device.getDeviceStatus());
//                dList.setDeviceSpecificValue(device.getDeviceSpecificValue());
//                dList.setDeviceType(device.getDeviceType());
//
//                unPanelList.add(dList);
//                unassignedPanelRes.setDeviceList(unPanelList);
//            }
//
//            Gson gson = new Gson();
//            mRequestJson =  gson.toJson(unassignedPanelRes);
//
//
//        }else{
//
//            JSONObject object = new JSONObject();
//            try {
//                object.put("is_module",roomdeviceList.getIsModule());
//                object.put("room_id",roomId);
//                object.put("sensor_id",roomdeviceList.getSensorId());
//                object.put("sensor_name",panelName);
//                object.put("room_name",mSpinnerRoom.getSelectedItem().toString());
//                object.put("module_id",roomdeviceList.getModuleId());
//                object.put("sensor_type",roomdeviceList.getSensorType());
//                object.put("sensor_icon",roomdeviceList.getSensorIcon());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            mRequestJson = object.toString();
//        }

        String url = ChatApplication.url + Constants.ADD_UN_CONFIGURED_DEVICE;

        new GetJsonTask(this, url, "POST", mRequestJson, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(!TextUtils.isEmpty(message)){
                        showToast(isSensor ? "Sensor added successfully" : "Panel added successfully");
                    }

                    if(code == 200){
                        finish();
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
}
