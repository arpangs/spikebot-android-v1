package com.spike.bot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AddDeviceTypeListActivity;
import com.spike.bot.activity.AddDevice.AddExistingPanel;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.adapter.HumiditySensorAdapter;
import com.spike.bot.adapter.room.RoomEditAdapterV2;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.dialog.DeviceEditDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditActivity_v2 extends AppCompatActivity implements ItemClickRoomEditListener {

    RecyclerView mMessagesView;
    EditText et_toolbar_title;
    private LinearLayout txt_empty_room, vv_delete_button;
    private Button btn_edit_room_delete;
    private ImageView empty_add_image;

    Dialog dialog;
    RoomEditAdapterV2 roomEditGridAdapter;
    DeviceEditDialog deviceEditDialog;
    RoomVO room;
    String action = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_v2);
        room = (RoomVO) getIntent().getSerializableExtra("room");

        setViewId();
        clickLister();
        getDeviceList();
    }

    private void setViewId() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Room Details");
        et_toolbar_title = findViewById(R.id.et_toolbar_title);
        txt_empty_room = findViewById(R.id.txt_empty_room);
        vv_delete_button = findViewById(R.id.vv_delete_button);
        txt_empty_room.setVisibility(View.GONE);
        btn_edit_room_delete = findViewById(R.id.btn_edit_room_delete);
        mMessagesView = findViewById(R.id.list_room_edit);
        empty_add_image = findViewById(R.id.empty_add_image);
        View activityRootView = findViewById(R.id.coordinator_view);

        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        dialog = new Dialog(this);

       // et_toolbar_title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_toolbar_title.setText(room.getRoomName());
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Set room name max length 25
        et_toolbar_title.setFilters(filterArray);

        //setting the recycler view
        mMessagesView.setLayoutManager(new GridLayoutManager(this, 1));
        roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), this, this);
        mMessagesView.setAdapter(roomEditGridAdapter);

        mMessagesView.getRecycledViewPool().setMaxRecycledViews(0, 1);

        et_toolbar_title.setImeOptions(EditorInfo.IME_ACTION_DONE);


        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > Common.dpToPx(getApplicationContext(), 100)) { // if more than 100 pixels, its probably a keyboard...
                    vv_delete_button.setVisibility(View.GONE);
                } else {
                    vv_delete_button.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (IRRemoteBrandListActivity.arrayList != null) {
            IRRemoteBrandListActivity.arrayList = null;
        }
        Constants.isWifiConnect = false;
        Constants.isWifiConnectSave = false;
        if (ChatApplication.isEditActivityNeedResume) {
            ChatApplication.isEditActivityNeedResume = false;
            getDeviceList();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void clickLister() {
        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RoomEditActivity_v2.this, AddDeviceTypeListActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_edit_room_delete.setVisibility(View.GONE);
        btn_edit_room_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoomAction(room);
            }
        });
    }

    /**
     * Delete Room
     *
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }

       /* JSONObject object = new JSONObject();
        try {
            object.put("room_id", roomVO.getRoomId());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_ROOM;*/

       /* ChatApplication.logDisplay("url is "+url+" "+object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("url is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();
                    }
                    ChatApplication.showToast(getApplicationContext(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(RoomEditActivity_v2.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().DeleteRoom(roomVO.getRoomId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("url is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();
                    }
                    ChatApplication.showToast(getApplicationContext(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(RoomEditActivity_v2.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(RoomEditActivity_v2.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }


    private void deleteRoomAction(final RoomVO room) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ? \nNote: All the devices and sensors associated with this room will be deleted", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteRoom(room);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent RoomEditActivity_v2.this inm AndroidManifest.xml.
        // Handle the popup menu dialog

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
//            openAddPopup(findViewById(R.id.action_add));
            return true;
        } else if (id == R.id.action_save) {
            ActivityHelper.hideKeyboard(this);

            roomEditGridAdapter.getPanelEditValue();

            ArrayList<PanelVO> mDataArrayList = roomEditGridAdapter.getDataArrayList();

            ArrayList<PanelVO> panelVOs = new ArrayList<>();
            for (int i = 0; i < mDataArrayList.size(); i++) {

                if (mDataArrayList.get(i) != null) {
                    PanelVO panelvo = (PanelVO) mDataArrayList.get(i);
                    panelVOs.add(panelvo);
                }
            }

            saveRoom(panelVOs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*save room call*/
    public void saveRoom(ArrayList<PanelVO> panelVOs) {

        if (TextUtils.isEmpty(et_toolbar_title.getText().toString())) {
            et_toolbar_title.setError(Html.fromHtml("<font color='#FFFFFF'>Enter Room Name</font>"));
            et_toolbar_title.requestFocus();
            return;
        }



        /*

        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("room_id", room.getRoomId());
            obj.put("room_name", et_toolbar_title.getText().toString());

            //panelVOs

            JSONArray panelArray = new JSONArray();
            for (PanelVO panel : panelVOs) {

                JSONObject objPanel = new JSONObject();
                objPanel.put("panel_id", panel.getPanelId());
                objPanel.put("panel_name", panel.getPanelName());
                panelArray.put(objPanel);

                if (TextUtils.isEmpty(panel.getPanelName())) {
                    isEmptyPanel = true;
                }
            }
            obj.put("panel_data", panelArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isEmptyPanel) {
            return;
        }
        String url = ChatApplication.url + Constants.SAVE_ROOM_AND_PANEL_NAME;

        ChatApplication.logDisplay("ur is " + url + " " + obj);*/
        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Please wait...", false);

       /* new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard = true;
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();

                    } else if (code == 301) {
                        ChatApplication.showToast(getApplicationContext(), message);
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
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
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();*/

        boolean isEmptyPanel = false;
        JSONArray panelArray = new JSONArray();
        try {

            for (PanelVO panel : panelVOs) {
                JSONObject objPanel = new JSONObject();
                objPanel.put("panel_id", panel.getPanelId());
                objPanel.put("panel_name", panel.getPanelName());
                panelArray.put(objPanel);
                if (TextUtils.isEmpty(panel.getPanelName())) {
                    isEmptyPanel = true;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isEmptyPanel) {
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().SaveRoom(room.getRoomId(), et_toolbar_title.getText().toString(), panelArray, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard = true;
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();

                    } else if (code == 301) {
                        ChatApplication.showToast(getApplicationContext(), message);
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
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
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        });





    }

    public void getDeviceList() {
        JSONObject jsonObject = new JSONObject();
       /* try {
            //"room_id": "1571409634267_43TZShCIQ",
            //	"room_type": "room"
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("room_type", "room");
            jsonObject.put("room_id", room.getRoomId());
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

       /* String url = ChatApplication.url + Constants.roomsget;

        ChatApplication.logDisplay("edit room "+url+" "+jsonObject);

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    ArrayList<RoomVO> roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
//                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    ChatApplication.logDisplay("result is " + dataObject);
                    roomList = JsonHelper.parseRoomObject(dataObject, false);
                    if (roomList.size() > 0) {
                        room = roomList.get(0);
                        et_toolbar_title.setText(room.getRoomName());

                        if (room.getPanelList() != null && room.getPanelList().size() > 0) {
                            roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), RoomEditActivity_v2.this, RoomEditActivity_v2.this);
                            mMessagesView.setAdapter(roomEditGridAdapter);
                            roomEditGridAdapter.notifyDataSetChanged();
                        }

                    }
                    ActivityHelper.dismissProgressDialog();
                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    if (room.getPanelList().size() == 0) {
                        txt_empty_room.setVisibility(View.VISIBLE);
                        mMessagesView.setVisibility(View.GONE);
                    } else {
                        txt_empty_room.setVisibility(View.GONE);
                        mMessagesView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
                if (room.getPanelList().size() == 0) {
                    txt_empty_room.setVisibility(View.VISIBLE);
                    mMessagesView.setVisibility(View.GONE);
                } else {
                    txt_empty_room.setVisibility(View.GONE);
                    mMessagesView.setVisibility(View.VISIBLE);
                }
            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().GetDeviceList(room.getRoomId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ActivityHelper.dismissProgressDialog();
                    ArrayList<RoomVO> roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
                    ChatApplication.logDisplay("result is " + dataObject);
                    roomList = JsonHelper.parseRoomObject(dataObject, false);
                    if (roomList.size() > 0) {
                        room = roomList.get(0);
                        et_toolbar_title.setText(room.getRoomName());

                        if (room.getPanelList() != null && room.getPanelList().size() > 0) {
                            roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), RoomEditActivity_v2.this, RoomEditActivity_v2.this);
                            mMessagesView.setAdapter(roomEditGridAdapter);
                            roomEditGridAdapter.notifyDataSetChanged();
                        }

                    }
                    ActivityHelper.dismissProgressDialog();
                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    if (room.getPanelList().size() == 0) {
                        txt_empty_room.setVisibility(View.VISIBLE);
                        mMessagesView.setVisibility(View.GONE);
                    } else {
                        txt_empty_room.setVisibility(View.GONE);
                        mMessagesView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
                if (room.getPanelList().size() == 0) {
                    txt_empty_room.setVisibility(View.VISIBLE);
                    mMessagesView.setVisibility(View.GONE);
                } else {
                    txt_empty_room.setVisibility(View.GONE);
                    mMessagesView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
                if (room.getPanelList().size() == 0) {
                    txt_empty_room.setVisibility(View.VISIBLE);
                    mMessagesView.setVisibility(View.GONE);
                } else {
                    txt_empty_room.setVisibility(View.GONE);
                    mMessagesView.setVisibility(View.VISIBLE);
                }
            }
        });



    }

    @Override
    public void itemClicked(RoomVO item, String action, View view) {
    }

    @Override
    public void itemClicked(final PanelVO panelVO, String action, View view) {

        if (action.equalsIgnoreCase("delete")) {
            //
            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {

                    deletePanel(panelVO);

                    ChatApplication.isRefreshDashBoard = true;
                    ChatApplication.isMainFragmentNeedResume = true;
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        } else if (action.equalsIgnoreCase("edit")) {

            Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
            intentPanel.putExtra("isDeviceAdd", true);
            intentPanel.putExtra("panelV0", panelVO);
            intentPanel.putExtra("roomId", room.getRoomId());
            intentPanel.putExtra("roomName", room.getRoomName());
            intentPanel.putExtra("panel_id", "" + panelVO.getPanelId());
            intentPanel.putExtra("panel_name", "" + panelVO.getPanelName());
            startActivity(intentPanel);
        } else if (action.equals("sensorPanel")) {
        }
    }

    @Override
    public void itemClicked(DeviceVO section, String action, View view) {
        if (action.equalsIgnoreCase("1")) {
            ActivityHelper.showProgressDialog(this, "Please wait.", false);
            showDeviceEditDialog(section);
        }
    }

    /**
     * @param deviceVO , device icon list & show dialog > change name , icon
     */
    private void showDeviceEditDialog(final DeviceVO deviceVO) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }
        /*String url = ChatApplication.url + Constants.CHECK_INDIVIDUAL_SWITCH_DETAILS;
        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("icon list "+ " " + url + " " + obj.toString());*/

       /* new GetJsonTask(this, url, "POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    deviceEditDialog = new DeviceEditDialog(RoomEditActivity_v2.this, deviceVO, result, new ICallback()
                    {
                        @Override
                        public void onSuccess(String str) {
                            ChatApplication.isRefreshDashBoard = true;
                            ChatApplication.isMainFragmentNeedResume = true;
                            getDeviceList();
                        }
                    });
                    if (!deviceEditDialog.isShowing()) {
                        deviceEditDialog.show();
                    }

                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().ShowDeviceEditDialog(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                ActivityHelper.dismissProgressDialog();
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    deviceEditDialog = new DeviceEditDialog(RoomEditActivity_v2.this, deviceVO, result, new ICallback()
                    {
                        @Override
                        public void onSuccess(String str) {
                            ChatApplication.isRefreshDashBoard = true;
                            ChatApplication.isMainFragmentNeedResume = true;
                            getDeviceList();
                        }
                    });
                    if (!deviceEditDialog.isShowing()) {
                        deviceEditDialog.show();
                    }

                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        });

    }

    private void deletePanel(PanelVO panelVO) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.disconnect));
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

       /* JSONObject object = new JSONObject();
        try {
            object.put("panel_id", panelVO.getPanelId());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.panelDelete;

        ChatApplication.logDisplay("panel delete "+url+" "+object);
        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        getDeviceList();
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().DeletePanel(panelVO.getPanelId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getApplicationContext(), message);
                        getDeviceList();
                    } else {
                        ChatApplication.showToast(getApplicationContext(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getApplicationContext(), getResources().getString(R.string.something_wrong1));
            }
        });
    }
}
