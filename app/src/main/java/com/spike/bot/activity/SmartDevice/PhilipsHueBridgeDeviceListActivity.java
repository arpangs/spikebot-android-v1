package com.spike.bot.activity.SmartDevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/8/19.
 * Gmail : vipul patel
 */
public class PhilipsHueBridgeDeviceListActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String host_ip = "", getBridge_name = "", bridge_id = "";
    public RecyclerView recyclerSmartDevice;
    public TextView txtNodataFound;
    public DeviceListAdapter bridgeListAdapter;
    public ArrayList<SmartBrandDeviceModel> arrayListDeviceListTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        host_ip = getIntent().getStringExtra("host_ip");
        getBridge_name = getIntent().getStringExtra("getBridge_name");
        bridge_id = getIntent().getStringExtra("bridge_id");

    }

    @Override
    protected void onResume() {
        setId();
        super.onResume();
    }

    private void setId() {
        Constants.activityPhilipsHueBridgeDeviceListActivity = this;
        txtNodataFound = findViewById(R.id.txtNodataFound);
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getBridge_name + " devices");
        txtNodataFound.setText("No device found.");
        getHueBridgeLightList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAdd:
                showMenu(toolbar);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void showMenu(View actionView) {
        PopupMenu popup = new PopupMenu(PhilipsHueBridgeDeviceListActivity.this, actionView);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, actionView, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, actionView);
        }
        popup.getMenuInflater().inflate(R.menu.menu_smart_option, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionPlug:
                        break;
                    case R.id.actionBuld:
                        Intent intent = new Intent(PhilipsHueBridgeDeviceListActivity.this, SearchDeviceActivity.class);
                        intent.putExtra("searchModel", arrayListDeviceListTemp);
                        intent.putExtra("bridge_id", bridge_id);
                        intent.putExtra("getBridge_name", getBridge_name);
                        intent.putExtra("host_ip", host_ip);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_smart_device, menu);
        return true;
    }

    public void getHueBridgeLightList() {
        if (!ActivityHelper.isConnectingToInternet(PhilipsHueBridgeDeviceListActivity.this)) {
            Toast.makeText(PhilipsHueBridgeDeviceListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(PhilipsHueBridgeDeviceListActivity.this, "Please wait... ", false);

        String url = ChatApplication.url + Constants.getSpikebotHueLightList;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("host_ip", host_ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(PhilipsHueBridgeDeviceListActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        ChatApplication.logDisplay("data is " + result);
                        JSONObject object = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = object.optJSONArray("smart_device_list");

                        if (arrayListDeviceListTemp != null) {
                            arrayListDeviceListTemp.clear();
                        }

                        if (jsonArray != null && jsonArray.length() > 0) {
                            txtNodataFound.setVisibility(View.GONE);
                            recyclerSmartDevice.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SmartBrandDeviceModel smartBrandModel = new SmartBrandDeviceModel();
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                                smartBrandModel.setId(jsonObject1.optString("room_name"));
                                smartBrandModel.setName(jsonObject1.optString("smart_device_name"));
                                smartBrandModel.setUniqueid(jsonObject1.optString("smart_device_module_id"));
                                smartBrandModel.setOn(jsonObject1.optString("smart_device_id"));

                                arrayListDeviceListTemp.add(smartBrandModel);
                            }

                            if (arrayListDeviceListTemp.size() > 0) {
                                setAdpater();
                            }
                        } else {
                            txtNodataFound.setVisibility(View.VISIBLE);
                            recyclerSmartDevice.setVisibility(View.GONE);
                            ChatApplication.showToast(PhilipsHueBridgeDeviceListActivity.this, "No data found.");
                        }

                    } else {
                        ChatApplication.showToast(PhilipsHueBridgeDeviceListActivity.this, message);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerSmartDevice.setLayoutManager(layoutManager);

        bridgeListAdapter = new DeviceListAdapter(this, arrayListDeviceListTemp, 1);
        recyclerSmartDevice.setAdapter(bridgeListAdapter);
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
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bridge_device, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            holder.txtBridgeName.setText(arrayListLog.get(position).getName());
            holder.txtroomName.setText("Room : " + arrayListLog.get(position).getId());

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDelete(arrayListLog.get(position));
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
            public TextView txtBridgeName, txtroomName;
            public ImageView imgDelete;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBridgeName = itemView.findViewById(R.id.txtBridgeName);
                imgDelete = itemView.findViewById(R.id.imgDelete);
                txtroomName = itemView.findViewById(R.id.txtroomName);
            }
        }
    }


    private void showDelete(final SmartBrandDeviceModel showDeleteshowDelete) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure want to delete?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteDevice(showDeleteshowDelete);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void deleteDevice(final SmartBrandDeviceModel showDeleteshowDelete) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this.getApplicationContext(), Constants.USER_ID));
            obj.put("original_room_device_id", showDeleteshowDelete.getOn());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.deletePhilipsHue;

        ChatApplication.logDisplay("save switch " + obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();

                        arrayListDeviceListTemp.remove(showDeleteshowDelete);
                        bridgeListAdapter.notifyDataSetChanged();

                        if (arrayListDeviceListTemp.size() == 0) {
                            txtNodataFound.setVisibility(View.VISIBLE);
                            recyclerSmartDevice.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
