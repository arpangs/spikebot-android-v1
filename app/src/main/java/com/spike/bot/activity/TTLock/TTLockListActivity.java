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
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.TTlockAdapter.UserLockListAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.LockObj;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Sagar on 2/9/19.
 * Gmail : vipul patel
 */
public class TTLockListActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView recyclerViewLock;
    public TextView txtNodataFound;

    ArrayList<LockObj> lockObjs=new ArrayList<>();
    private UserLockListAdapter mListApapter;
    String gateWayId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        gateWayId=getIntent().getStringExtra("gateWayId");

        setviewId();
    }

    private void setviewId() {
        fab=findViewById(R.id.fab);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Lock List");

        txtNodataFound=findViewById(R.id.txtNodataFound);
        recyclerViewLock=findViewById(R.id.recyclerSmartDevice);
        fab.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        getAllLockList();
//        lockList();
//        AllLockList();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
//            startActivity(new Intent(this, AddTTlockActivity.class));

            Intent intent=new Intent(this,AddDeviceConfirmActivity.class);
            intent.putExtra("isViewType","ttLock");
            intent.putExtra("lockObjs",lockObjs);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllLockList() {

        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        String url = ChatApplication.url + Constants.getLockLists;

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result is "+result);
                ActivityHelper.dismissProgressDialog();
                if(result.optInt("code")==200){
                    lockObjs.clear();
//{"code":200,"message":"Success","data":[{"lock_id":"1578230","lock_name":"ttlock"},{"lock_id":"1444344","lock_name":"lockj"}]}
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object1=jsonArray.optJSONObject(i);
                            LockObj lockObj=new LockObj();
                            lockObj.setLockId(object1.optInt("lock_id"));
                            lockObj.setLockAlias(object1.optString("lock_name"));

                            lockObjs.add(lockObj);
                        }

                        setAdapter();

                    } catch (JSONException e) {
                        ActivityHelper.dismissProgressDialog();
                        e.printStackTrace();
                    }
                    finally {
                        ActivityHelper.dismissProgressDialog();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void lockList() {
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);

        if(TextUtils.isEmpty(gateWayId)){
            AllLockList();
            return;
        }

        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.getlockbygateway(Constants.client_id, Constants.access_token, gateWayId, System.currentTimeMillis());
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
                        lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});

                        setAdapter();
                    } catch (JSONException e) {
                        recyclerViewLock.setVisibility(View.GONE);
                        txtNodataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                        ChatApplication.showToast(TTLockListActivity.this,"No data found");
                    }
                } else {
                    recyclerViewLock.setVisibility(View.GONE);
                    txtNodataFound.setVisibility(View.VISIBLE);
                    ChatApplication.showToast(TTLockListActivity.this,"No data found");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(TTLockListActivity.this,t.getMessage());
            }
        });
    }

    private void AllLockList() {

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
                         lockObjs = GsonUtil.toObject(array.toString(), new TypeToken<ArrayList<LockObj>>(){});

                         setAdapter();
                    } catch (JSONException e) {
                        recyclerViewLock.setVisibility(View.GONE);
                        txtNodataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    recyclerViewLock.setVisibility(View.GONE);
                    txtNodataFound.setVisibility(View.VISIBLE);
                    ChatApplication.showToast(TTLockListActivity.this,"No data found");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(TTLockListActivity.this,t.getMessage());
            }
        });
    }

    public void setAdapter(){
        if(lockObjs.size()>0){
            recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
            mListApapter = new UserLockListAdapter(this,lockObjs);
            recyclerViewLock.setAdapter(mListApapter);
            mListApapter.notifyDataSetChanged();
        }else {
            ChatApplication.showToast(TTLockListActivity.this,"No data found");
        }

    }
}
