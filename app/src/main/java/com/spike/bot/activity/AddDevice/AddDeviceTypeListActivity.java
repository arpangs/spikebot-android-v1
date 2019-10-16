package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddUnassignedPanel;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.adapter.AddCameraAdapter;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.fragments.MainFragment;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 15/10/19.
 * Gmail : vipul patel
 */
public class AddDeviceTypeListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerSmartDevice;
    DeviceListAdapter deviceListAdapter;
    ArrayList<String> arrayList=new ArrayList<>();
    ProgressDialog m_progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        setviewId();
    }

    private void setviewId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Configuration");

        recyclerSmartDevice=findViewById(R.id.recyclerSmartDevice);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerSmartDevice.setLayoutManager(linearLayoutManager);

        getArraylist();

        deviceListAdapter=new DeviceListAdapter(this,arrayList);
        recyclerSmartDevice.setAdapter(deviceListAdapter);
        deviceListAdapter.notifyDataSetChanged();

    }

    public void getArraylist(){
        arrayList.add("Unassigned List");
        arrayList.add("Room");
        arrayList.add("Switch Board");
        arrayList.add("Smart Device");
        arrayList.add("Ir Blaster");
        arrayList.add("Remote");
        arrayList.add("Door Lock");
        arrayList.add("Door Sensor");
        arrayList.add("Gas / Smoke Sensor");
        arrayList.add("Temperature Sensor");
        arrayList.add("Repeaters");
        arrayList.add("Camera");
    }


    private void setIntent(int position) {
        if(position==0){
            startActivity(new Intent(this, AddUnassignedPanel.class));
        }else if(position==1){
            addCustomRoom();
        }
    }

    public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.SensorViewHolder>{

        private Context mContext;
        ArrayList<String> arrayListLog=new ArrayList<>();


        public DeviceListAdapter(Context context, ArrayList<String> arrayListLog1){
            this.mContext=context;
            this.arrayListLog=arrayListLog1;
        }

        @Override
        public DeviceListAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_list,parent,false);
            return new DeviceListAdapter.SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DeviceListAdapter.SensorViewHolder holder, final int position) {

            holder.txtUserName.setText(arrayList.get(position));

            holder.txtUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setIntent(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayListLog.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder{

            public TextView txtUserName;

            public SensorViewHolder(View view) {
                super(view);
                txtUserName =  itemView.findViewById(R.id.txtUserName);
            }
        }
    }

    public void addCustomRoom() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomRoom(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * Call api for save custom room
     *
     * @param roomName mRoomName
     * @param dialog   mDialog instance
     */
    private void saveCustomRoom(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter Room name");
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("room_name", roomName.getText().toString());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("ob : " + object.toString());
        showProgressDialog(this, "Searching Device attached ", false);


        String url = ChatApplication.url + Constants.ADD_CUSTOME_ROOM;
        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();
                    } else if (code == 301) {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    } else {
                        ChatApplication.showToast(AddDeviceTypeListActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    {
                        dismissProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onFailure " + error);
                ChatApplication.showToast(AddDeviceTypeListActivity.this, getResources().getString(R.string.disconnect));
            }
        }).execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showProgressDialog(Context context, String message, boolean iscancle){
        m_progressDialog=new ProgressDialog(this);
        m_progressDialog.setMessage(message);
        m_progressDialog.setCanceledOnTouchOutside(true);
        m_progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(m_progressDialog!=null){
            ChatApplication.logDisplay("m_progressDialog is null not");
            m_progressDialog.cancel();
            m_progressDialog.dismiss();
        }
    }

}
