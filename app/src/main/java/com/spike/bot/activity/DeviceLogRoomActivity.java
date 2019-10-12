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
import android.widget.LinearLayout;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 24/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class DeviceLogRoomActivity extends AppCompatActivity {

    public String isNotification = "", room_name = "", ROOM_ID = "", IS_SENSOR = "", typeSelection = "1";
    public int mStartIndex = 0;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        rv_device_log = (RecyclerView) findViewById(R.id.rv_device_log);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

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

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && mStartIndex != 0 && !isScrollDown) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        getDeviceList(mStartIndex);
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

        String url = "";
        if (isNotification.equals("roomSensorUnreadLogs")) {
            url = ChatApplication.url + Constants.roomSensorUnreadLogs;
        } else {
            url = ChatApplication.url + Constants.roomLogs;
        }

        JSONObject object = new JSONObject();
        try {
            if (isNotification.equals("roomSensorUnreadLogs")) {
                /*	"room_id":"",				"notification_number":	*/
                object.put("notification_number", "" + position);
                object.put("room_id", ROOM_ID);
            } else {
/*"room_id":"","log_type"://0 for sensor, -1 for device and 1 for all
"notification_number":
 * */
                object.put("notification_number", "" + position);
                object.put("room_id", ROOM_ID);
                object.put("log_type", Integer.parseInt(typeSelection));
            }

            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(DeviceLogRoomActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject dataObj = result.getJSONObject("data");

                        if (dataObj.has("filterList")) {
                            filterArrayList.clear();
                            JSONArray logJsonArray = dataObj.getJSONArray("filterList");
                            //  ListUtils.filters.clear();

                            for (int i = 0; i < logJsonArray.length(); i++) {
                                JSONObject object = logJsonArray.getJSONObject(i);

                                String logName = object.getString("filter_name");
                                String action_name = object.getString("action_name");

                                ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                for (String subLog : action_name.split(",")) {
                                    subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                }
                                filterArrayList.add(new Filter(logName, false, false, subFilterArrayList));
                                filterArrayListTemp.add(new Filter(logName, false, false, subFilterArrayList));
                            }
                        }

                        JSONArray notificationArray = dataObj.optJSONArray("notificationList");

                        if (notificationArray == null || notificationArray.length() == 0) {
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
                        }
                        if (notificationArray != null) {
                            for (int i = 0; i < notificationArray.length(); i++) {
                                JSONObject jsonObject = notificationArray.getJSONObject(i);
                                String activity_action = jsonObject.getString("activity_action");
                                String activity_type = jsonObject.getString("activity_type");
                                String activity_description = jsonObject.getString("activity_description");
                                String activity_time = jsonObject.getString("activity_time");
                                String is_unread = jsonObject.optString("is_unread");

                                deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "", "", is_unread));
                            }
                            if (!TextUtils.isEmpty(notificationArray.toString())) {
                                setAdapter();
                            }

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
