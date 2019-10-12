package com.spike.bot.activity.SmartDevice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.SmartDeviceAdapter.SmartBrandAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 16/5/19.
 * Gmail : vipul patel
 */
public class BrandListActivity extends AppCompatActivity {

    public Toolbar toolbar;
    FloatingActionButton fab;
    public RecyclerView recyclerSmartDevice;

    public SmartBrandAdapter smartBrandAdapter;
    public ArrayList<SmartBrandModel> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        setId();
    }

    private void setId() {
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Your Brand");
        fab.setVisibility(View.GONE);
        getSmartDevice();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /** Get smart device list */
    public void getSmartDevice() {
        if (!ActivityHelper.isConnectingToInternet(BrandListActivity.this)) {
            Toast.makeText(BrandListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(BrandListActivity.this, "Please wait... ", false);

        String url = ChatApplication.url + Constants.getSmartDeviceBrands;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(BrandListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject object = result.optJSONObject("data");
                        //{
                        //  "smart_device_brand_list": [
                        //    {
                        //      "id": 1,
                        //      "smart_device_brand_name": "Philips Hue"
                        //    }
                        //  ]
                        //}
                        ChatApplication.logDisplay("object is " + object);

                        JSONArray jsonArray = object.optJSONArray("smart_device_brand_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            SmartBrandModel smartBrandModel = new SmartBrandModel();
                            smartBrandModel.setId(jsonObject.optString("id"));
                            smartBrandModel.setSmart_device_brand_name(jsonObject.optString("smart_device_brand_name"));
                            smartBrandModel.setIcon_image(jsonObject.optString("icon_image"));
                            arrayList.add(smartBrandModel);
                        }

                        if (arrayList.size() > 0) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BrandListActivity.this);
                            recyclerSmartDevice.setLayoutManager(linearLayoutManager);
                            smartBrandAdapter = new SmartBrandAdapter(BrandListActivity.this, arrayList, 1);
                            recyclerSmartDevice.setAdapter(smartBrandAdapter);
                        }

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
}
