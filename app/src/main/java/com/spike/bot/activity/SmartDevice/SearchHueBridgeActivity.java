package com.spike.bot.activity.SmartDevice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandModel;
import com.spike.bot.model.SmartDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 1/8/19.
 * Gmail : vipul patel
 */
public class SearchHueBridgeActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public boolean isBridgeAdd=false;
    public RecyclerView recyclerSmartDevice;
    public AppCompatButton btnNext;
    public ArrayList<SmartBrandModel> arrayList=new ArrayList<>();

    String brandId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_philips_bridge);

        brandId=getIntent().getStringExtra("brandId");

        setId();
    }

    private void setId() {
        Constants.activitySearchHueBridgeActivity=this;
        toolbar=findViewById(R.id.toolbar);
        recyclerSmartDevice=findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("Select Your Brand");

        btnNext=findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

//        callCheckUnalredBridgeAdded();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==btnNext){
//            if(isBridgeAdd){
//                showDialogBridge();
//            }else {
                nextIntentCall();
//            }
        }
    }

    private void nextIntentCall() {
        Intent intent=new Intent(this, SearchSubHueBridgeActivity.class);
        intent.putExtra("brandId",brandId);
        startActivity(intent);
    }

    private void showDialogBridge() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_bridge_add);

        dialog. getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btnSearchBridge =  dialog.findViewById(R.id.btnSearchBridge);
        Button btnExtingBridge =  dialog.findViewById(R.id.btnExtingBridge);

        btnExtingBridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBrideDetail();
            }
        });

        btnSearchBridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                nextIntentCall();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void callBrideDetail() {
        if (!ActivityHelper.isConnectingToInternet(SearchHueBridgeActivity.this)) {
            Toast.makeText(SearchHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchHueBridgeActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.getHueRouterDetails;
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(SearchHueBridgeActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        toolbar.setTitle("Search Device");
                        ChatApplication.logDisplay("add hue " + result.toString());
                        /*{"code":200,"message":"Success","data":{"host_ip":"192.168.175.74",
                        "bridge_id":"fV7ZNEHxojK8x2-TWSExQ4C6SF8ZwcXr3p2ZeXnp","smart_device":"Philips Hue"}}*/

                        JSONObject object = new JSONObject(result.toString());
                        JSONObject object1 = object.optJSONObject("data");

                        String smart_device = object1.optString("smart_device");
                        String bridge_id = object1.optString("bridge_id");
                        String host_ip = object1.optString("host_ip");

                        callHueLightsList(bridge_id, host_ip);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void callCheckUnalredBridgeAdded() {
        if (!ActivityHelper.isConnectingToInternet(SearchHueBridgeActivity.this)) {
            Toast.makeText(SearchHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SearchHueBridgeActivity.this, "Please wait... ", false);
        String url = ChatApplication.url + Constants.getHueRouterDetails;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(SearchHueBridgeActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        isBridgeAdd=true;
                        JSONObject object = result.optJSONObject("data");
                        ChatApplication.logDisplay("bridge is "+object);
                    }else {
                        isBridgeAdd=false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                isBridgeAdd=false;
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void callHueLightsList(final String bridge_id, final String host_ip) {
        if (!ActivityHelper.isConnectingToInternet(SearchHueBridgeActivity.this)) {
            Toast.makeText(SearchHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SearchHueBridgeActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.HueLightsList;

        final JSONObject object = new JSONObject();
        // host_ip, smart_device,  bridge_id
        try {
            object.put("bridge_id", bridge_id);
            object.put("host_ip", host_ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object.toString());
        new GetJsonTask(SearchHueBridgeActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (arrayList != null) {
                            arrayList.clear();
                        }
                        ChatApplication.logDisplay("add hue is HueLightsList " + result.toString());

                        JSONObject object1 = new JSONObject(result.toString());
                        JSONArray jsonArray = object1.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object2 = jsonArray.optJSONObject(i);
                            String name = object2.optString("name");
                            JSONObject object3 = object2.optJSONObject("state");
                            String onOff = object3.optString("on");
                            String id = object2.optString("id");
                            SmartDeviceModel smartRemoteModel = new SmartDeviceModel();
                            smartRemoteModel.setName(name);
                            smartRemoteModel.setOnfff(onOff);
                            smartRemoteModel.setId(id);
                            smartRemoteModel.setBridge_id(bridge_id);
                            smartRemoteModel.setHost_ip(host_ip);
//                            arrayList.add(smartRemoteModel);
                        }


                        if (arrayList.size() > 0) {
//                            setAdapter();
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
                Toast.makeText(SearchHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


}
