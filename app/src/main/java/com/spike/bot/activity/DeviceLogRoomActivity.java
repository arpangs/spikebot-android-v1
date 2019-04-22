package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.adapter.DeviceLogAdapter;
import com.spike.bot.adapter.LogRoomAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.Filter;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

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

    public String isNotification = "",room_name="", ROOM_ID = "", IS_SENSOR = "", typeSelection = "1";
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

            Intent intent = new Intent(this,DeviceLogActivity.class);
            intent.putExtra("ROOM_ID",ROOM_ID);
            intent.putExtra("activity_type","room");
            intent.putExtra("isCheckActivity","room");
            startActivity(intent);
            //showDialogFilter(toolbar);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialogFilter(Toolbar toolbar) {
        PopupMenu popup = new PopupMenu(DeviceLogRoomActivity.this, toolbar);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, toolbar, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, toolbar);
        }
        popup.getMenuInflater().inflate(R.menu.menu_logs, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionAll:
                        setTitel("All Logs");
                        setAllData("1");
                        break;
                    case R.id.actionSensor:
                        setTitel("Sensor Logs");
                        setAllData("0");
                        break;
                    case R.id.actionDevice:
                        setTitel("Device Logs");
                        setAllData("-1");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void setTitel(String titel){
        toolbar.setTitle(room_name+" "+titel);
    }
    private void setAllData(String all) {
        isScrollDown=false;
        typeSelection = all;
        mStartIndex = 0;
        deviceLogList.clear();
        filterArrayListTemp.clear();
        filterArrayList.clear();
        getDeviceList(mStartIndex);
    }

    private void init() {
        rv_device_log = (RecyclerView) findViewById(R.id.rv_device_log);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

        linearLayoutManager = new LinearLayoutManager(DeviceLogRoomActivity.this);
        rv_device_log.setLayoutManager(linearLayoutManager);


        if (isNotification.equalsIgnoreCase("roomSensorUnreadLogs")) {
            //toolbar.setTitle("Unread Logs");
            setTitel("Unread Logs");
            getDeviceList(mStartIndex);
        } else {
            //toolbar.setTitle("All Logs");
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

        String url="";
        if (isNotification.equals("roomSensorUnreadLogs")) {
            url = ChatApplication.url + Constants.roomSensorUnreadLogs;
        }else {
            url = ChatApplication.url + Constants.roomLogs;
        }

        JSONObject object = new JSONObject();
        try {
            if (isNotification.equals("roomSensorUnreadLogs")) {
                /*	"room_id":"",				"notification_number":	*/
                object.put("notification_number", ""+position);
                object.put("room_id", ROOM_ID);
            } else {
/*"room_id":"","log_type"://0 for sensor, -1 for device and 1 for all
"notification_number":
 * */
                object.put("notification_number",""+ position);
                object.put("room_id", ROOM_ID);
                object.put("log_type", Integer.parseInt(typeSelection));
            }

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

                        if (notificationArray==null || notificationArray.length()==0) {
                            isScrollDown=true;
                            if(mStartIndex==0){
                                ll_empty.setVisibility(View.VISIBLE);
                                rv_device_log.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
                            }
                            isLoading=true;
                        } else {
                            isScrollDown=false;
                            isLoading=false;
                        }
                        if(notificationArray!=null){
                            for (int i = 0; i < notificationArray.length(); i++) {
                                JSONObject jsonObject = notificationArray.getJSONObject(i);
                                String activity_action = jsonObject.getString("activity_action");
                                String activity_type = jsonObject.getString("activity_type");
                                String activity_description = jsonObject.getString("activity_description");
                                String activity_time = jsonObject.getString("activity_time");
                                //  String activity_state = jsonObject.getString("activity_state");

                                //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state

                                deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "",""));
                            }
                            if(!TextUtils.isEmpty(notificationArray.toString())){
                                setAdapter();
                            }

                        }

                        ActivityHelper.dismissProgressDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setAdapter() {
        if(rv_device_log.getVisibility()==View.GONE){
            ll_empty.setVisibility(View.GONE);
            rv_device_log.setVisibility(View.VISIBLE);
        }
        if(mStartIndex==0){
            deviceLogAdapter = new LogRoomAdapter(DeviceLogRoomActivity.this, deviceLogList,isNotification);
            rv_device_log.setAdapter(deviceLogAdapter);
            deviceLogAdapter.notifyDataSetChanged();
        }else {
            deviceLogAdapter.notifyDataSetChanged();
        }
        mStartIndex = mStartIndex + 25;
    }

    @Override
    public void onBackPressed() {
        if (isNotification.equalsIgnoreCase("roomSensorUnreadLogs")
                && deviceLogList.size()>0) {
            callreadCountApi();
        }
        super.onBackPressed();
    }

    private void callreadCountApi() {

        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

        JSONObject jsonObject = new JSONObject();
        try

        {

            JSONArray jsonArray = new JSONArray();

            JSONObject object = new JSONObject();
//
            object.put("sensor_type","");
            object.put("module_id", "");
            object.put("room_id", ""+ROOM_ID);
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
                       // checkIntent(b);

                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        DeviceLogRoomActivity.this.finish();
                      //  checkIntent(b);
                    }
                }).

                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
