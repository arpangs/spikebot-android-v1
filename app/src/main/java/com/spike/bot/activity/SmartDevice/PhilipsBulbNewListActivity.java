package com.spike.bot.activity.SmartDevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 7/8/19.
 * Gmail : vipul patel
 */
public class PhilipsBulbNewListActivity extends AppCompatActivity {

    public Toolbar toolbar;
    FloatingActionButton fab;
    public String host_ip = "", getBridge_name = "", bridge_id = "";
    public RecyclerView recyclerSmartDevice;
    public TextView txtNodataFound;

    public ArrayList<SmartBrandDeviceModel> arrayList = new ArrayList<>();
    public ArrayList<SmartBrandDeviceModel> arrayListDeviceListTemp = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        arrayList = (ArrayList<SmartBrandDeviceModel>) getIntent().getSerializableExtra("searchModel");
        host_ip = getIntent().getStringExtra("host_ip");
        getBridge_name = getIntent().getStringExtra("getBridge_name");
        bridge_id = getIntent().getStringExtra("bridge_id");
        setId();
    }

    private void setId() {
        Constants.activityPhilipsHueBridgeDeviceListActivity = this;
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        txtNodataFound = findViewById(R.id.txtNodataFound);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("New Devices");
        getHueLightsList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /* getting hue light list*/
    public void getHueLightsList() {
        if (!ActivityHelper.isConnectingToInternet(PhilipsBulbNewListActivity.this)) {
            Toast.makeText(PhilipsBulbNewListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(PhilipsBulbNewListActivity.this, "Please wait... ", false);

        JSONObject object = new JSONObject();
        try {
            object.put("bridge_id", bridge_id);
            object.put("host_ip", host_ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.HueLightsList;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(PhilipsBulbNewListActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        ChatApplication.logDisplay("data is " + result);
                        //{"code":200,"message":"Success","data":{"hue_bridge_list":[]}}
                        JSONObject object = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = object.optJSONArray("philips_hue_light_list");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            SmartBrandDeviceModel smartBrandModel = new SmartBrandDeviceModel();
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                            JSONObject jsonstatus = jsonObject1.optJSONObject("state");
                            smartBrandModel.setOn(jsonstatus.optString("on"));
                            smartBrandModel.setBri(jsonstatus.optString("bri"));

                            smartBrandModel.setId(jsonObject1.optString("id"));
                            smartBrandModel.setName(jsonObject1.optString("name"));
                            smartBrandModel.setUniqueid(jsonObject1.optString("uniqueid"));

                            arrayListDeviceListTemp.add(smartBrandModel);
                        }

                        if (arrayListDeviceListTemp.size() > 0) {
                            setAdpater();
                        } else {
                            txtNodataFound.setVisibility(View.VISIBLE);
                            recyclerSmartDevice.setVisibility(View.GONE);
                        }

                    } else {
                        ChatApplication.showToast(PhilipsBulbNewListActivity.this, "No data found.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void setAdpater() {
        // check same light not adding
        ArrayList<SmartBrandDeviceModel> arrayAdapter = new ArrayList<>();
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            stringArrayList.add(arrayList.get(i).getUniqueid());
        }
        for (int i = 0; i < arrayListDeviceListTemp.size(); i++) {
            stringArrayList.add(arrayListDeviceListTemp.get(i).getUniqueid());
        }

        arrayAdapter.addAll(arrayListDeviceListTemp);

        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayListDeviceListTemp.size(); i++) {
                for (int j = 0; j < stringArrayList.size(); j++) {
                    if (stringArrayList.get(j).equalsIgnoreCase(arrayListDeviceListTemp.get(i).getUniqueid())) {
                        arrayAdapter.remove(arrayListDeviceListTemp.get(i));
                    }
                }
            }
        }

        if (arrayAdapter != null && arrayAdapter.size() > 0) {
            txtNodataFound.setVisibility(View.GONE);
            recyclerSmartDevice.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerSmartDevice.setLayoutManager(layoutManager);

            DeviceListAdapter bridgeListAdapter = new DeviceListAdapter(this, arrayAdapter, 1);
            recyclerSmartDevice.setAdapter(bridgeListAdapter);
        } else {
            txtNodataFound.setVisibility(View.VISIBLE);
            recyclerSmartDevice.setVisibility(View.GONE);
            ChatApplication.showToast(this, "No data find.");
        }

    }

    public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.SensorViewHolder> {

        private Context mContext;
        public int type = 0;
        ArrayList<SmartBrandDeviceModel> arrayListLog = new ArrayList<>();

        public DeviceListAdapter(Context context, ArrayList<SmartBrandDeviceModel> arrayList, int type) {
            this.mContext = context;
            this.type = type;
            this.arrayListLog = arrayList;
        }

        @Override
        public DeviceListAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_device_bridge, parent, false);
            return new DeviceListAdapter.SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DeviceListAdapter.SensorViewHolder holder, final int position) {

            holder.txtBridgeName.setText(arrayListLog.get(position).getName());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callIntentConfi(arrayListLog.get(position));
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

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public TextView txtBridgeName;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBridgeName = itemView.findViewById(R.id.txtBridgeName);
            }
        }
    }

    private void callIntentConfi(SmartBrandDeviceModel smartBrandDeviceModel) {
        Intent intent = new Intent(this, AddDeviceConfirmActivity.class);
        intent.putExtra("isViewType", "smartDevice");
        intent.putExtra("searchModel", smartBrandDeviceModel);
        intent.putExtra("bridge_id", bridge_id);
        intent.putExtra("getBridge_name", getBridge_name);
        intent.putExtra("host_ip", host_ip);
        startActivity(intent);
    }

}
