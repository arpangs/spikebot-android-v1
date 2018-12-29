package com.spike.bot.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.DeviceLogAdapterFilter;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 19/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DeviceLogFilterActivity extends AppCompatActivity{

    private static final String TAG = "LogFilter";
    private RecyclerView rv_device_log_filter;
    Activity activity;

    public List<DeviceLog> deviceLogList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Device Log");
        setSupportActionBar(toolbar);
        toolbar.setElevation(0);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        activity = this;

        rv_device_log_filter = (RecyclerView)findViewById(R.id.rv_device_log_filter);
        rv_device_log_filter.setLayoutManager(new GridLayoutManager(this,1));

        deviceLogList = new ArrayList<>();

        getDeviceLog();


    }

    public void getDeviceLog(){

        Log.d(TAG, "getDeviceLog getDeviceLog");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        String url = ChatApplication.url + Constants.GET_NOTIFICATION_INFO;

        new GetJsonTask(activity,url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getDeviceLog onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        deviceLogList.clear();

                        JSONObject dataObj = result.getJSONObject("data");
                        JSONArray notificationArray = dataObj.getJSONArray("notificationList");

                        for(int i=0; i <notificationArray.length() ; i++){
                            JSONObject jsonObject = notificationArray.getJSONObject(i);
                            String activity_action = jsonObject.getString("activity_action");
                            String activity_type = jsonObject.getString("activity_type");
                            String activity_description = jsonObject.getString("activity_description");
                            String activity_time = jsonObject.getString("activity_time");
                            String activity_state = jsonObject.getString("activity_state");

                            //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state
                            deviceLogList.add(new DeviceLog(activity_action,activity_type,activity_description,activity_time,activity_state));
                        }

                        DeviceLogAdapterFilter deviceLogFilterAdapter = new DeviceLogAdapterFilter(deviceLogList);
                        rv_device_log_filter.setAdapter(deviceLogFilterAdapter);

                        ActivityHelper.dismissProgressDialog();
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getDeviceLog onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
