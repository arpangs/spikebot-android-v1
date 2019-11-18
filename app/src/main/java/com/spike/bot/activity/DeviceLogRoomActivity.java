package com.spike.bot.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.LogRoomAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.Filter;
import com.spike.bot.model.RemoteDetailsRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by Sagar on 24/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class DeviceLogRoomActivity extends AppCompatActivity {

    public String isNotification = "", room_name = "", ROOM_ID = "", IS_SENSOR = "", typeSelection = "1";
    public int mStartIndex = 0,lastVisibleItem;
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    Toolbar toolbar;
    RecyclerView rv_device_log;
    LinearLayout ll_empty;


    LinearLayoutManager linearLayoutManager;
    LogRoomAdapter deviceLogAdapter;
    public boolean isScrollDown = false, isLoading = false;
    private ArrayList<Filter> filterArrayList = new ArrayList<>();
    private ArrayList<Filter> filterArrayListTemp = new ArrayList<>();
    public List<DeviceLog> deviceLogList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log_room);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ROOM_ID = getIntent().getStringExtra("ROOM_ID");
        IS_SENSOR = getIntent().getStringExtra("IS_SENSOR");
        isNotification = getIntent().getStringExtra("isNotification");
        room_name = getIntent().getStringExtra("room_name");
        init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!isNotification.equalsIgnoreCase("roomSensorUnreadLogs")) {
            getMenuInflater().inflate(R.menu.menu_filter, menu);
            MenuItem menuItem = menu.findItem(R.id.action_filter);
            MenuItem menuItemReset = menu.findItem(R.id.action_reset);
            menuItemReset.setVisible(false);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {

            Intent intent = new Intent(this, DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", ROOM_ID);
            intent.putExtra("activity_type", "room");
            intent.putExtra("isCheckActivity", "room");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTitel(String titel) {
        toolbar.setTitle(room_name + " " + titel);
    }

    private void init() {
        rv_device_log =  findViewById(R.id.rv_device_log);
        ll_empty =  findViewById(R.id.ll_empty);

        linearLayoutManager = new LinearLayoutManager(DeviceLogRoomActivity.this);
        rv_device_log.setLayoutManager(linearLayoutManager);

        if (isNotification.equalsIgnoreCase("roomSensorUnreadLogs")) {
            setTitel("Notification");
            getDeviceList(mStartIndex);
        } else {
            setTitel("All Logs");
            getDeviceList(mStartIndex);

        }

        rv_device_log.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mScrollState = newState;

                if (newState == SCROLL_STATE_IDLE) {

                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (deviceLogList.size() != 0 && mScrollState == SCROLL_STATE_IDLE) {
                        if (deviceLogList.size() >= 25) {
                            if ((deviceLogList.size() - 1) == lastVisibleItem) {
                                if ( !isScrollDown) {
                                    isLoading = true;
                                    isScrollDown = true;
                                    getDeviceList(mStartIndex);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    public void getDeviceList(final int position) {

        if (!ActivityHelper.isConnectingToInternet(DeviceLogRoomActivity.this)) {
            Toast.makeText(DeviceLogRoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(DeviceLogRoomActivity.this, "Please wait.", false);

        String url = ChatApplication.url + Constants.logsfind;

        JSONObject object = new JSONObject();
        try {
            object.put("filter_action","door_open,door_close,temp_alert,gas_detected");
            object.put("filter_type","room");
            object.put("room_id",""+ROOM_ID);
            object.put("unseen",1);
            object.put("notification_number",""+position);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is "+url+" "+object);
        new GetJsonTask(DeviceLogRoomActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("reult is "+result);
                        JSONArray dataObj = result.optJSONArray("data");
                        Gson gson=new Gson();
                        List<DeviceLog> deviceLogs = gson.fromJson(dataObj.toString(), new TypeToken<List<DeviceLog>>(){}.getType());;

                        deviceLogList.addAll(deviceLogs);
                        if (deviceLogs == null || deviceLogs.size() == 0) {
                            isScrollDown = true;
                            if (mStartIndex == 0) {
                                ll_empty.setVisibility(View.VISIBLE);
                                rv_device_log.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
                            }
                            isLoading = true;
                        } else {
                            isScrollDown = false;
                            isLoading = false;

                            setAdapter();
                        }


                        ActivityHelper.dismissProgressDialog();
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
                ll_empty.setVisibility(View.VISIBLE);
                rv_device_log.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setAdapter() {
        if (rv_device_log.getVisibility() == View.GONE) {
            ll_empty.setVisibility(View.GONE);
            rv_device_log.setVisibility(View.VISIBLE);
        }
        if (mStartIndex == 0) {
            deviceLogAdapter = new LogRoomAdapter(DeviceLogRoomActivity.this, deviceLogList, isNotification);
            rv_device_log.setAdapter(deviceLogAdapter);
            deviceLogAdapter.notifyDataSetChanged();
        } else {
            deviceLogAdapter.notifyDataSetChanged();
        }
        mStartIndex = mStartIndex + 25;
    }

    @Override
    public void onBackPressed() {
        if (isNotification.equalsIgnoreCase("roomSensorUnreadLogs")
                && deviceLogList.size() > 0) {
            callreadCountApi();
        }
        super.onBackPressed();
    }

    private void callreadCountApi() {

        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();

            JSONObject object = new JSONObject();
            object.put("sensor_type", "");
            object.put("module_id", "");
            object.put("room_id", "" + ROOM_ID);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonArray.put(object);
            jsonObject.put("update_logs", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new

                ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        DeviceLogRoomActivity.this.finish();
                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        DeviceLogRoomActivity.this.finish();
                    }
                }).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
