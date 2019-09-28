package com.spike.bot.activity.TTLock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.activity.ir.blaster.WifiBlasterActivity;
import com.spike.bot.activity.ir.blaster.WifiListActivity;
import com.spike.bot.adapter.TTlockAdapter.GatewayListAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.model.GatewayListModel;
import com.spike.bot.model.LockObj;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Sagar on 30/8/19.
 * Gmail : vipul patel
 */
public class GateWayListActivity extends AppCompatActivity implements  GatewayListAdapter.clickList {

    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView recyclerViewLock;
    public TextView  txtNodataFound;

    String ttlockId="",ttbridgeId="",lockName="";

    private GatewayListAdapter mListApapter;
    ArrayList<GatewayListModel> lockObjs=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        //for geteway
        ttbridgeId=getIntent().getStringExtra("gatewayId");
        ttlockId=getIntent().getStringExtra("lockId");
        lockName=getIntent().getStringExtra("lockName");

        GatewayClient.getDefault().prepareBTService(getApplicationContext());

        setviewId();
    }

    private void setviewId() {
        fab=findViewById(R.id.fab);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerViewLock=findViewById(R.id.recyclerSmartDevice);
        txtNodataFound=findViewById(R.id.txtNodataFound);

        if(!TextUtils.isEmpty(ttbridgeId)){
            toolbar.setTitle("Select Bridge");
        }else {
            toolbar.setTitle("Bridge List");
        }
    }

    @Override
    protected void onResume() {
        gatewayList();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_room_edit, menu);
            this.menu = menu;
            menu.findItem(R.id.action_save).setTitle("Add Bridge").setVisible(false);

            menu.findItem(R.id.action_log).setVisible(false);
        if(!TextUtils.isEmpty(ttbridgeId)){
            menu.findItem(R.id.action_add).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
//            if(WifiBlasterActivity.setMobileDataEnabled(this)){
                startActivity(new Intent(this, AddGatewayActivity.class));
//            }else {
//                ChatApplication.showToast(this,"Please enable your wifi");
//            }
//        if(flag){
//            Toast.makeText(getApplicationContext(),"Please disble your mobile data",Toast.LENGTH_LONG);
//            WifiListActivity.this.finish();
//        }

//            startActivity(new Intent(this, AddTTlockActivity.class));
            return true;
        }else if (id == R.id.action_save) {
//            if(WifiBlasterActivity.setMobileDataEnabled(this)){
                startActivity(new Intent(this, AddGatewayActivity.class));
//            }else {
//                ChatApplication.showToast(this,"Please enable your wifi");
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gatewayList() {
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
//        Call<String> call = apiService.gatewaylist(Constants.client_id, Constants.access_token, 1, 100, System.currentTimeMillis());
        Call<String> call = apiService.bridgelist(ChatApplication.url+"/getTTLockBridgeLists");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                ActivityHelper.dismissProgressDialog();
                if (!TextUtils.isEmpty(json)) {
                if (json.contains("data")) {
                    try {
                        lockObjs.clear();
                        recyclerViewLock.setVisibility(View.VISIBLE);
                        txtNodataFound.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("data");
//                        lockObjs= GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<GatewayListModel>>(){});

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.optJSONObject(i);

                            GatewayListModel gatewayListModel = new GatewayListModel();

                            gatewayListModel.setGatewayId(Integer.valueOf(object.optString("bridge_id")));
                            gatewayListModel.setGatewayName(object.optString("bridge_name"));
                            gatewayListModel.setGatewayMac(object.optString("id"));

                            lockObjs.add(gatewayListModel);
                        }

                        setAdapter();
                    } catch (JSONException e) {
                        noDataFOund();
                    }
                }
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                noDataFOund();
                ChatApplication.showToast(GateWayListActivity.this,t.getMessage());
            }
        });
    }

    public void noDataFOund(){
        recyclerViewLock.setVisibility(View.GONE);
        txtNodataFound.setVisibility(View.VISIBLE);
        ChatApplication.showToast(GateWayListActivity.this,"No data found");
    }
    private void setAdapter() {
        if(lockObjs.size()>0){
            recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
            mListApapter = new GatewayListAdapter(this,lockObjs,this);
            recyclerViewLock.setAdapter(mListApapter);
            mListApapter.notifyDataSetChanged();
        }else {
            if(!TextUtils.isEmpty(ttbridgeId)){
                clickList(null);
            }

            ChatApplication.showToast(GateWayListActivity.this,"No data found");
            noDataFOund();

        }

    }

    private void lockList() {
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getLockList(Constants.client_id, Constants.access_token, 1, 100, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                ActivityHelper.dismissProgressDialog();
                if (json.contains("list")) {
                    try {
                        recyclerViewLock.setVisibility(View.VISIBLE);
                        txtNodataFound.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("list");
                        ArrayList<LockObj> lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});
                    } catch (JSONException e) {
                        recyclerViewLock.setVisibility(View.GONE);
                        txtNodataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    recyclerViewLock.setVisibility(View.GONE);
                    txtNodataFound.setVisibility(View.VISIBLE);
                    ChatApplication.showToast(GateWayListActivity.this,"No data found");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(GateWayListActivity.this,t.getMessage());
            }
        });
    }

    @Override
    public void clickList(GatewayListModel gatewayListModel) {
        if(!TextUtils.isEmpty(ttbridgeId)){
            Intent intent=new Intent(this, AddDeviceConfirmActivity.class);
            intent.putExtra("isViewType","ttLock");
            intent.putExtra("lockId",""+ttlockId);
//            intent.putExtra("gatewayId","7035");
            if(gatewayListModel!=null){
                intent.putExtra("gatewayId",""+gatewayListModel.getGatewayId());
            }else {
                intent.putExtra("gatewayId","");
            }

            intent.putExtra("lockName",lockName);
            startActivity(intent);
        }else {
            Intent intent=new Intent(this, TTLockListActivity.class);
            intent.putExtra("gateWayId",""+gatewayListModel.getGatewayId());
            startActivity(intent);
        }
    }
}
