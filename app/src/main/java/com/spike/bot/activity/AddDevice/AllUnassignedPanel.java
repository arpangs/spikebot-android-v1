package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import com.spike.bot.Beacon.AddBeaconActivity;
import com.spike.bot.Beacon.BeaconConfigActivity;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Sensor.DoorSensorInfoActivity;
import com.spike.bot.adapter.AddUnassignedPanelAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.RestClient;
import com.spike.bot.api_retrofit.SpikeBotApi;
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

       // getRoomList();

        getRoomList();

    }

    private void bindViews() {
        mListPanels =  findViewById(R.id.list_un_panel);
        mListPanels.setLayoutManager(new GridLayoutManager(this, 1));
        mEmptyView =  findViewById(R.id.txt_empty_list);
        mEmptyView.setVisibility(View.GONE);
    }

    /*get room list */
    private void getRoomList(){
        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        SpikeBotApi.getInstance().getRoomList("room", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {

            }
        });

    }

    /*get all unassign list*/
    private void getUnAssignedList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(AllUnassignedPanel.this, "Please wait...", false);
        SpikeBotApi.getInstance().getUnAssignedList(type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
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
                    if(roomdeviceList.getModuleType().equals("beacon")){
                        Intent intent = new Intent(AllUnassignedPanel.this, BeaconConfigActivity.class);
                        intent.putExtra("DEVICE_TYPE", "beacon");
                        intent.putExtra("isUnassign",true);
                        intent.putExtra("isMap",true);
                        intent.putExtra("isBeaconListAdapter",true);
                        intent.putExtra("BEACON_MODULE_ID",roomdeviceList.getModuleId());
                        startActivity(intent);
                        finish();
                    } else{
                        showAddDialog(roomdeviceList);
                    }

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

        SpikeBotApi.getInstance().DeleteDevice(module_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ActivityHelper.dismissProgressDialog();
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }


    /**
     * add unAssign Repeater
     */
    public void addunAssignRepater(UnassignedListRes.Data roomdeviceList) {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        SpikeBotApi.getInstance().addunAssignRepater("",roomdeviceList.getModuleType(), roomdeviceList.getModuleId(),roomdeviceList.getModuleType(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);

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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
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

        SpikeBotApi.getInstance().savePanel(roomdeviceList,roomId,panelName, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);

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
        });
    }
}
