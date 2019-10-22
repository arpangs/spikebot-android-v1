package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.AddUnassignedPanelAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddUnassignedPanel extends AppCompatActivity implements AddUnassignedPanelAdapter.UnassignedClickEvent {

    private RecyclerView mListPanels;
    private List<UnassignedListRes.Data.RoomList> roomList;
    private LinearLayout mEmptyView;

    private Dialog mDialog;
    private Spinner mSpinnerRoom;
    private EditText mPanelName;
    private Button mBtnSave;
    private ImageView mImageClose;
    private ArrayList<String> roomStrList = new ArrayList<>();
    private ArrayList<String> roomIdList = new ArrayList<>();
    List<UnassignedListRes.Data.RoomdeviceList> roomdeviceList;
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unassigned_panel);

        Toolbar toolbar =  findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Unassigned List");

        bindViews();

        getRoomList();

    }

    private void bindViews() {
        mListPanels =  findViewById(R.id.list_un_panel);
        mListPanels.setLayoutManager(new GridLayoutManager(this, 1));
        mEmptyView =  findViewById(R.id.txt_empty_list);
        mEmptyView.setVisibility(View.GONE);

    }

    private void getRoomList() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        roomdeviceList = new ArrayList<>();

        ActivityHelper.showProgressDialog(this, "Loading...", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", "room");
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.roomslist;

        ChatApplication.logDisplay("un assign is "+url+" "+jsonObject);

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("un assign is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {}.getType();
                        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optString("data").toString(), type);

                        getUnAssignedList();
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



    private void getUnAssignedList() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        roomdeviceList = new ArrayList<>();

//        ActivityHelper.showProgressDialog(this, "Loading...", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST+"/n";

        ChatApplication.logDisplay("un assign is "+url+" "+jsonObject);

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("un assign is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

//                        roomListArray = Common.jsonToPojo(result.toString(), UnassignedListRes.class);
//                        roomList = unassignedListRes.getData().getRoomList();
//                        roomdeviceList = unassignedListRes.getData().getRoomdeviceList();
//                        addUnassignedPanelAdapter = new AddUnassignedPanelAdapter(roomdeviceList, AddUnassignedPanel.this);
//                        mListPanels.setAdapter(addUnassignedPanelAdapter);

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                    /**
                     * set empty view visibility if room device list found equal to 0
                     */
                    if (roomdeviceList.size() == 0) {
                        mListPanels.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onClick
     *
     * @param position       position adapter
     * @param roomdeviceList roomDeviceList
     */

    @Override
    public void onClick(int position, UnassignedListRes.Data.RoomdeviceList roomdeviceList) {

        if (roomdeviceList.getIsModule() == 1 || roomdeviceList.getIsModule() == 3) {
            showAddDialog(roomdeviceList);
        } else {
            if (roomdeviceList.getSensorIcon().equals("doorsensor")) {
                Intent intent = new Intent(this, AddDeviceConfirmActivity.class);
                intent.putExtra("isViewType", "syncDoor");
                intent.putExtra("door_sensor_module_id", "" + roomdeviceList.getModuleId());
                intent.putExtra("door_sensor_name", "" + roomdeviceList.getSensorName());
                intent.putExtra("door_type", "" + roomdeviceList.getLock_subtype());
                if (roomdeviceList.getLock_subtype().equals("2")) {
                    intent.putExtra("lock_id", roomdeviceList.getLock_id());
                    intent.putExtra("lock_data", roomdeviceList.getLock_data());
                }
                startActivity(intent);

            } else if (roomdeviceList.getSensorIcon().equals("repeater")) {
                repetearAdd(roomdeviceList);
            } else {
                showAddDialog(roomdeviceList);
            }
        }

    }

    private void repetearAdd(UnassignedListRes.Data.RoomdeviceList roomdeviceList) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Add?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                addunAssignRepater(roomdeviceList);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(this.getFragmentManager(), "dialog");
    }


    /**
     * add unAssign Repeater
     */
    public void addunAssignRepater(UnassignedListRes.Data.RoomdeviceList roomdeviceList) {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.reassignRepeater;

        JSONObject object = new JSONObject();

        try {
            object.put("repeator_module_id", roomdeviceList.getRepeator_module_id());
            object.put("repeator_name", roomdeviceList.getRepeator_name());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("json " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        ChatApplication.showToast(AddUnassignedPanel.this, result.optString("message"));
                        AddUnassignedPanel.this.finish();
                    } else {
                        ChatApplication.showToast(AddUnassignedPanel.this, result.optString("message"));
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
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void showAddDialog(final UnassignedListRes.Data.RoomdeviceList roomdeviceList) {

        roomStrList.clear();
        roomIdList.clear();

        if (mDialog == null) {
            mDialog = new Dialog(AddUnassignedPanel.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_add_un_panel);
            mDialog.setCanceledOnTouchOutside(false);
        }

        mSpinnerRoom = (Spinner) mDialog.findViewById(R.id.spinner_room_name);
        mPanelName = (EditText) mDialog.findViewById(R.id.et_panel_name);
        mBtnSave = (Button) mDialog.findViewById(R.id.btn_save);
        mImageClose = (ImageView) mDialog.findViewById(R.id.iv_close);
        TextView mAddName = (TextView) mDialog.findViewById(R.id.tv_panel_name);

        mPanelName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mPanelName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        if (roomdeviceList.getIsModule() == 1) {
            mAddName.setText("Panel Name");
            mPanelName.setText(roomdeviceList.getModuleName());
        } else if (roomdeviceList.getIsModule() == 3) {
            mAddName.setText("Panel Name");
            mPanelName.setText(roomdeviceList.getModuleName());
        } else {
            mAddName.setText("Sensor Name");
            mPanelName.setText(roomdeviceList.getSensorName());
        }

        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        if (roomList != null) {
            for (UnassignedListRes.Data.RoomList roomListModel : roomList) {
                roomStrList.add(roomListModel.getRoomName());
                roomIdList.add(roomListModel.getRoomId());
            }

            roomStrList.add(0, "Select Room Name");
            roomIdList.add(0, "0");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_panel, roomStrList);
            mSpinnerRoom.setAdapter(adapter);

        }

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomdeviceList.getIsModule() == 3) {
                    savePanelCurtain(roomdeviceList, roomIdList.get(mSpinnerRoom.getSelectedItemPosition()), mPanelName.getText().toString().trim(), mDialog);
                } else {
                    savePanel(roomdeviceList, roomIdList.get(mSpinnerRoom.getSelectedItemPosition()), mPanelName.getText().toString().trim(), mDialog);
                }
            }
        });

        mDialog.show();
    }

    private void savePanelCurtain(UnassignedListRes.Data.RoomdeviceList roomdeviceList, String roomId, String panelName, Dialog mDialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSpinnerRoom.getSelectedItemPosition() == 0) {
            showToast("Please select Room Name");
            return;
        }

        if (TextUtils.isEmpty(mPanelName.getText().toString().trim())) {
            mPanelName.setError("Enter Panel Name");
            mPanelName.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Loading...", false);
        String mRequestJson = "";
        JSONObject object = new JSONObject();
        try {
            //{
            //    "phone_id": "6929FCC2-5124-4D93-B98B-0E1C100109CD",
            //    "phone_type": "IOS",
            //    "room_id": "1569564785527_3pspddidL",
            //    "room_name": "jLock",
            //    "user_id": "1569305102710_GtdsJ7yBl",
            //    "curtain_module_id": "8AA1131A004B1200",
            //    "curtain_name": "bari nu curtain"
            //}

            object.put("curtain_module_id", roomdeviceList.getModuleId());
            object.put("curtain_name", roomdeviceList.getSensorName());
            object.put("room_id", roomId);
            object.put("room_name", mSpinnerRoom.getSelectedItem().toString());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestJson = object.toString();

        String url = ChatApplication.url + Constants.curtainadd;

        ChatApplication.logDisplay("assign is " + url + " " + mRequestJson);

        new GetJsonTask(this, url, "POST", mRequestJson, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                mDialog.dismiss();
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (!TextUtils.isEmpty(message)) {
                        showToast("Panel added successfully");
                    }

                    if (code == 200) {
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

    /**
     * Save panel
     *
     * @param roomdeviceList roomDeviceList
     * @param roomId         roomId
     * @param panelName      panelName
     * @param mDialog
     */
    private void savePanel(UnassignedListRes.Data.RoomdeviceList roomdeviceList, String roomId, String panelName, Dialog mDialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSpinnerRoom.getSelectedItemPosition() == 0) {
            showToast("Please select Room Name");
            return;
        }

        if (TextUtils.isEmpty(mPanelName.getText().toString().trim())) {
            mPanelName.setError("Enter Panel Name");
            mPanelName.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Loading...", false);

        String mRequestJson = "";
        final boolean isSensor = roomdeviceList.getIsModule() == 0;

        JSONObject object = new JSONObject();
        try {
            if (roomdeviceList.getIsModule() == 0) {
                object.put("sensor_name", panelName);
            } else {
                object.put("panel_name", panelName);
            }
            object.put("is_module", roomdeviceList.getIsModule());
            object.put("room_id", roomId);
            object.put("sensor_id", roomdeviceList.getSensorId());
            //  object.put("sensor_name",panelName);
            object.put("room_name", mSpinnerRoom.getSelectedItem().toString());
            object.put("module_id", roomdeviceList.getModuleId());
            object.put("sensor_type", roomdeviceList.getSensorType());
            object.put("sensor_icon", roomdeviceList.getSensorIcon());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestJson = object.toString();
        String url = ChatApplication.url + Constants.ADD_UN_CONFIGURED_DEVICE;

        ChatApplication.logDisplay("assign is " + url + " " + mRequestJson);

        new GetJsonTask(this, url, "POST", mRequestJson, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                mDialog.dismiss();
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (!TextUtils.isEmpty(message)) {
                        showToast(isSensor ? "Sensor added successfully" : "Panel added successfully");
                    }

                    if (code == 200) {
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
