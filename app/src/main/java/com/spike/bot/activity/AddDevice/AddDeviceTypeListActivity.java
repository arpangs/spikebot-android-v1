package com.spike.bot.activity.AddDevice;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.AddUnassignedPanel;
import com.spike.bot.adapter.AddCameraAdapter;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;

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
            startActivity(new Intent(this, AddUnassignedPanel.class));
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

            setIntent(position);

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
//                saveCustomRoom(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

}
