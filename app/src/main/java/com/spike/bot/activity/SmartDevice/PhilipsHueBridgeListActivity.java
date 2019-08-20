package com.spike.bot.activity.SmartDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.SmartDeviceAdapter.BridgeListAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/8/19.
 * Gmail : vipul patel
 */
public class PhilipsHueBridgeListActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public String brandId="";
    TextView txtNodataFound;
    public RecyclerView recyclerSmartDevice;
    public BridgeListAdapter bridgeListAdapter;
    public ArrayList<SmartBrandModel> arrayList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        brandId=getIntent().getStringExtra("brandId");
        setId();
    }

    private void setId() {
        toolbar=findViewById(R.id.toolbar);
        txtNodataFound=findViewById(R.id.txtNodataFound);
        recyclerSmartDevice=findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Bridge");

        getHueBridgeList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionAdd:
                Intent intent=new Intent(PhilipsHueBridgeListActivity.this, SearchHueBridgeActivity.class);
                intent.putExtra("brandId",""+brandId);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_smart_device, menu);
        return true;
    }

    public void getHueBridgeList() {
        if (!ActivityHelper.isConnectingToInternet(PhilipsHueBridgeListActivity.this)) {
            Toast.makeText(PhilipsHueBridgeListActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(PhilipsHueBridgeListActivity.this, "Please wait... ", false);

        String url = ChatApplication.url + Constants.getHueBridgeList;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(PhilipsHueBridgeListActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject jsonObject=new JSONObject(result.toString());
                        ChatApplication.logDisplay("data is "+result);
                        //{"code":200,"message":"Success","data":{"hue_bridge_list":[]}}
                        JSONObject object=jsonObject.optJSONObject("data");
                        JSONArray jsonArray=object.optJSONArray("hue_bridge_list");
                        if(jsonArray==null || jsonArray.length()==0){
                            txtNodataFound.setVisibility(View.VISIBLE);
                            recyclerSmartDevice.setVisibility(View.GONE);
                            ChatApplication.showToast(PhilipsHueBridgeListActivity.this,"No bridge found");
//                            Intent intent=new Intent(PhilipsHueBridgeListActivity.this, SearchHueBridgeActivity.class);
//                            intent.putExtra("brandId",""+brandId);
//                            startActivity(intent);
//                            finish();
                        }else {
                            //{"code":200,"message":"Success",
                            // "data":{"hue_bridge_list":[{"id":1,"host_ip":"192.168.175.74","bridge_id":"TTfzwGKNNCTzoyqnIe99x0tYfAGIBwRoGW-7uQrd","bridge_type":"Philips Hue"
                            // ,"bridge_name":"test"}]}}

                            txtNodataFound.setVisibility(View.GONE);
                            recyclerSmartDevice.setVisibility(View.VISIBLE);

                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1=jsonArray.optJSONObject(i);

                                SmartBrandModel smartBrandModel=new SmartBrandModel();
                                smartBrandModel.setId(jsonObject1.optString("id"));
                                smartBrandModel.setHost_ip(jsonObject1.optString("host_ip"));
                                smartBrandModel.setBridge_id(jsonObject1.optString("bridge_id"));
                                smartBrandModel.setBridge_type(jsonObject1.optString("bridge_type"));
                                smartBrandModel.setBridge_name(jsonObject1.optString("bridge_name"));
                                arrayList.add(smartBrandModel);
                            }
                            
                            if(arrayList.size()>0){
                                setAdpater();
                            }
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

    private void setAdpater() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerSmartDevice.setLayoutManager(layoutManager);

        bridgeListAdapter=new BridgeListAdapter(this,arrayList,1);
        recyclerSmartDevice.setAdapter(bridgeListAdapter);

    }
}
