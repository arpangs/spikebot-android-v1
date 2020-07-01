package com.spike.bot.activity.TTLock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.adapter.TTlockAdapter.UserLockListAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Constants;
import com.spike.bot.model.LockObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/9/19.
 * Gmail : vipul patel
 */
public class TTLockListActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView recyclerViewLock;
    public TextView txtNodataFound;

    ArrayList<LockObj> lockObjs = new ArrayList<>();
    private UserLockListAdapter mListApapter;
    String gateWayId = "";
    public Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        gateWayId = getIntent().getStringExtra("gateWayId");

        setviewId();
    }

    private void setviewId() {
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Lock List");

        txtNodataFound = findViewById(R.id.txtNodataFound);
        recyclerViewLock = findViewById(R.id.recyclerSmartDevice);
        fab.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        getAllLockList();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        this.menu = menu;
        menu.findItem(R.id.action_save).setTitle("Add Bridge").setVisible(false);

        menu.findItem(R.id.action_log).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_add_text).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            Intent intent = new Intent(this, AddDeviceConfirmActivity.class);
            intent.putExtra("isViewType", "ttLock");
            intent.putExtra("lockObjs", lockObjs);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get all lock list
     */
    private void getAllLockList() {

        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        SpikeBotApi.getInstance().getAllLockList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();


                    lockObjs.clear();
                    //{"code":200,"message":"Success","data":[{"lock_id":"1578230","lock_name":"ttlock"},{"lock_id":"1444344","lock_name":"lockj"}]}
                    try {
                        JSONObject result = new JSONObject(stringResponse);
                        JSONArray jsonArray = result.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.optJSONObject(i);
                            LockObj lockObj = new LockObj();
                            lockObj.setLockId(object1.optInt("lock_id"));
                            lockObj.setLockAlias(object1.optString("lock_name"));

                            lockObjs.add(lockObj);
                        }
                        setAdapter();

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
            }
        });
    }

    public void setAdapter() {
        if (lockObjs.size() > 0) {
            recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
            mListApapter = new UserLockListAdapter(this, lockObjs);
            recyclerViewLock.setAdapter(mListApapter);
            mListApapter.notifyDataSetChanged();
        } else {
            ChatApplication.showToast(TTLockListActivity.this, "No data found");
        }

    }
}
