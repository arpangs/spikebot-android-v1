package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Sensor.DoorSensorInfoActivity;
import com.spike.bot.adapter.AddUnassignedPanelAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.UnassignedListRes;

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
public class AllUnassignedPanel extends AppCompatActivity implements AddUnassignedPanelAdapter.UnassignedClickEvent {

    private RecyclerView mListPanels;
    private LinearLayout mEmptyView;

    private Dialog mDialog;
    private Spinner mSpinnerRoom;
    private EditText mPanelName;
    private Button mBtnSave;
    private ImageView mImageClose;

    String type="";

    AddUnassignedPanelAdapter addUnassignedPanelAdapter;
    private ArrayList<String> roomStrList = new ArrayList<>();
    private ArrayList<String> roomIdList = new ArrayList<>();
    List<UnassignedListRes.Data.RoomdeviceList> roomdeviceList;
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray=new ArrayList<>();
    ArrayList<UnassignedListRes.Data> roomDeviceListArray=new ArrayList<>();

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

        type=getIntent().getStringExtra("type");
        bindViews();

        getRoomList();

    }

    private void bindViews() {
        mListPanels =  findViewById(R.id.list_un_panel);
        mListPanels.setLayoutManager(new GridLayoutManager(this, 1));
        mEmptyView =  findViewById(R.id.txt_empty_list);
        mEmptyView.setVisibility(View.GONE);
    }

    /*get room list */
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


    /*get all unassing list*/
    private void getUnAssignedList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("module_type", type);
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.deviceunassigned;

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
                       setAdapter(result.optString("data"));

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                    /**
                     * set empty view visibility if room device list found equal to 0
                     */
                    if (roomDeviceListArray.size() == 0) {
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

    /*set view*/
    private void setAdapter(String result) {
        Gson gson=new Gson();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mListPanels.setLayoutManager(linearLayoutManager);
        roomDeviceListArray = gson.fromJson(result, new TypeToken<List<UnassignedListRes.Data>>(){}.getType());
        addUnassignedPanelAdapter = new AddUnassignedPanelAdapter(this,roomDeviceListArray, AllUnassignedPanel.this);
        mListPanels.setAdapter(addUnassignedPanelAdapter);
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
    public void onClick(int position, UnassignedListRes.Data roomdeviceList,String action) {

        if(roomdeviceList.getModuleType().equals("repeater") || roomdeviceList.getModuleType().equals("smart_remote")){
            showBottomSheetDialog(roomdeviceList);
        }else if(roomdeviceList.getModuleType().equals("door") || roomdeviceList.getModuleType().equals("ttlock")){

        }else {

            if(action.equalsIgnoreCase("add")) {
                showBottomSheetDialog(roomdeviceList);
            }
        }
    }


    public void showBottomSheetDialog(UnassignedListRes.Data roomdeviceList) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);
        txt_edit.setText("Add");

        BottomSheetDialog dialog = new BottomSheetDialog(AllUnassignedPanel.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + roomdeviceList.getModuleType() + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(roomdeviceList.getModuleType().equals("repeater") || roomdeviceList.getModuleType().equals("smart_remote")){
                    showBottomSheetDialog(roomdeviceList);
                } else {
                    showAddDialog(roomdeviceList);
                }
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        DeleteDevice(roomdeviceList.getModuleId());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    /*delete alert dialog*/
    private void repetearAdd(UnassignedListRes.Data roomdeviceList) {
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

    public void DeleteDevice(String module_id) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.devicemoduledelete;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("module_id", module_id);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("delete device url is " + webUrl + " " + jsonNotification);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("delete device success " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    if (code == 200) {
                        getRoomList();
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
     * add unAssign Repeater
     */
    public void addunAssignRepater(UnassignedListRes.Data roomdeviceList) {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.deviceadd;

        JSONObject object = new JSONObject();

        try {
            object.put("room_id", "");
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("device_name", roomdeviceList.getModuleType());
            object.put("module_id", roomdeviceList.getModuleId());
            object.put("module_type", roomdeviceList.getModuleType());
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
                        ChatApplication.showToast(AllUnassignedPanel.this, result.optString("message"));
                        AllUnassignedPanel.this.finish();
                    } else {
                        ChatApplication.showToast(AllUnassignedPanel.this, result.optString("message"));
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

    /*show add dialog for panel & sensor */
    private void showAddDialog(final UnassignedListRes.Data roomdeviceList) {

        roomStrList.clear();
        roomIdList.clear();

        if (mDialog == null) {
            mDialog = new Dialog(AllUnassignedPanel.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_add_un_panel);
            mDialog.setCanceledOnTouchOutside(false);
        }

        mSpinnerRoom =  mDialog.findViewById(R.id.spinner_room_name);
        mPanelName =  mDialog.findViewById(R.id.et_panel_name);
        mBtnSave =  mDialog.findViewById(R.id.btn_save);
        mImageClose =  mDialog.findViewById(R.id.iv_close);
        TextView mAddName =  mDialog.findViewById(R.id.tv_panel_name);
        TextView txtModuleId =  mDialog.findViewById(R.id.txtModuleId);

        mPanelName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mPanelName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        //all","gas_sensor","temp_sensor",
        // "curtain","5","5f","door","ttlock","irblaster","repeater","smart_remote","heavy_load","double_heavy_load"
        if (roomdeviceList.getModuleType().equals("5") || roomdeviceList.getModuleType().equals("5f")
                || roomdeviceList.getModuleType().equals("heavy_load") || roomdeviceList.getModuleType().equals("double_heavy_load")) {
            mAddName.setText("Panel Name");
        }else {
            mAddName.setText("Sensor Name");
        }

        mPanelName.setText(roomdeviceList.getModuleType());
        txtModuleId.setText(roomdeviceList.getModuleId());
        mPanelName.setSelection(mPanelName.getText().length());

        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        if (roomListArray != null) {
            for (UnassignedListRes.Data.RoomList roomListModel : roomListArray) {
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
                if (mSpinnerRoom.getSelectedItemPosition() == 0) {
                    showToast("Please select Room Name");
                    return;
                }else if (TextUtils.isEmpty(mPanelName.getText().toString().trim())) {
                    mPanelName.setError("Enter Name");
                    mPanelName.requestFocus();
                    return;
                }else {
                    savePanel(roomdeviceList, roomIdList.get(mSpinnerRoom.getSelectedItemPosition()), mPanelName.getText().toString().trim(), mDialog);
                }
            }
        });

        mDialog.show();
    }


    /**
     * Save panel
     *
     * @param roomdeviceList roomDeviceList
     * @param roomId         roomId
     * @param panelName      panelName
     * @param mDialog
     */
    private void savePanel(UnassignedListRes.Data roomdeviceList, String roomId, String panelName, Dialog mDialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }


        ActivityHelper.showProgressDialog(this, "Loading...", false);


        JSONObject object = new JSONObject();
        try {
            //{
            //	"room_id": "1571407908196_uEVHoQJNR",
            //	"module_id": "1571407892909_OtecipqUB",
            //	"panel_name": "my panel 1",
            //	"user_id": "1568463607921_AyMe7ek9e",
            //	"device_name": ""
            //}

            if (roomdeviceList.getModuleType().equals("5") || roomdeviceList.getModuleType().equals("5f")
                    || roomdeviceList.getModuleType().equals("heavy_load") || roomdeviceList.getModuleType().equals("double_heavy_load")) {
                object.put("panel_name", panelName);
            } else {
                object.put("device_name", panelName);
            }
            object.put("room_id", roomId);
            object.put("module_type", roomdeviceList.getModuleType());
            object.put("module_id", roomdeviceList.getModuleId());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.deviceadd;

        ChatApplication.logDisplay("assign is " + url + " " + object);

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                mDialog.dismiss();
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (!TextUtils.isEmpty(message)) {
                        showToast("Added successfully");
                    }

                    if (code == 200) {
                        Common.hideSoftKeyboard(AllUnassignedPanel.this);
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
