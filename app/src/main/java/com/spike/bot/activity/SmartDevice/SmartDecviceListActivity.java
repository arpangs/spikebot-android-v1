package com.spike.bot.activity.SmartDevice;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * Created by Sagar on 31/7/19.
 * Gmail : vipul patel
 */
public class SmartDecviceListActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String brandId = "";
    public RecyclerView recyclerSmartDevice;

    public SmartBrandAdapter smartBrandAdapter;
    public ArrayList<SmartBrandModel> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        brandId = getIntent().getStringExtra("brandId");
        setId();
    }

    private void setId() {
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Device");
        getSmartDevice();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * get All smarat device list
     */
    public void getSmartDevice() {
        if (!ActivityHelper.isConnectingToInternet(SmartDecviceListActivity.this)) {
            Toast.makeText(SmartDecviceListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SmartDecviceListActivity.this, "Please wait... ", false);

        String url = ChatApplication.url + Constants.getSmartDeviceType + "/" + brandId;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(SmartDecviceListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject object = result.optJSONObject("data");

                        //{
                        //  "code": 200,
                        //  "message": "Success",
                        //  "data": {
                        //    "smart_device_Type_list": [
                        //      {
                        //        "id": 1,
                        //        "smart_device_type": "Smart Bulb"
                        //      }
                        //    ]
                        //  }
                        //}
                        ChatApplication.logDisplay("object is " + object);

                        JSONArray jsonArray = object.optJSONArray("smart_device_Type_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            SmartBrandModel smartBrandModel = new SmartBrandModel();
                            smartBrandModel.setId(jsonObject.optString("id"));
                            smartBrandModel.setSmart_device_brand_name(jsonObject.optString("smart_device_type"));
                            smartBrandModel.setIcon_image(jsonObject.optString("icon_image"));
                            arrayList.add(smartBrandModel);
                        }

                        if (arrayList.size() > 0) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SmartDecviceListActivity.this);
                            recyclerSmartDevice.setLayoutManager(linearLayoutManager);
                            smartBrandAdapter = new SmartBrandAdapter(SmartDecviceListActivity.this, arrayList, 2);
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

