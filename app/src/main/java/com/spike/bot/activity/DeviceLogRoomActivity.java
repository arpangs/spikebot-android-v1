package com.spike.bot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.LogRoomAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.Filter;

import org.json.JSONArray;
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
    public int mStartIndex = 0, lastVisibleItem, row_index = -1;
    public boolean isScrollDown = false, isLoading = false;
    public List<DeviceLog> deviceLogList = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView rv_device_log;
    LinearLayout ll_empty;
    LinearLayoutManager linearLayoutManager;
    LogRoomAdapter deviceLogAdapter;
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private ArrayList<Filter> filterArrayList = new ArrayList<>();
    private ArrayList<Filter> filterArrayListTemp = new ArrayList<>();
    private ArrayList datelist = new ArrayList();
    private ArrayList monthlist = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log_room);

        toolbar = findViewById(R.id.toolbar);
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
            intent.putExtra("isCheckActivity", "AllTypeNotification"); // for notification log dev arpan added on 29 july 2020
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTitel(String title) {
        if (room_name != null) {
            toolbar.setTitle(room_name + " " + title);
        } else {
            toolbar.setTitle(title);
        }
    }

    private void init() {
        rv_device_log = findViewById(R.id.rv_device_log);
        ll_empty = findViewById(R.id.ll_empty);

        linearLayoutManager = new LinearLayoutManager(DeviceLogRoomActivity.this);
        rv_device_log.setLayoutManager(linearLayoutManager);

        if (isNotification.equalsIgnoreCase("roomSensorUnreadLogs")) {
            setTitel("Notifications");
            getDeviceList(mStartIndex);
        } else if (isNotification.equalsIgnoreCase("All Notification")) {
            setTitel("All Notification");
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
                                if (!isScrollDown) {
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


    /*geting   log*/
    public void getDeviceList(final int position) {

        if (!ActivityHelper.isConnectingToInternet(DeviceLogRoomActivity.this)) {
            Toast.makeText(DeviceLogRoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(DeviceLogRoomActivity.this, "Please wait.", false);



       /* String url = ChatApplication.url + Constants.logsfind;

        JSONObject object = new JSONObject();
        try {
            object.put("filter_action", "door_open,door_close,temp_alert,gas_detected,water_detected,door_lock,door_unlock");
            object.put("filter_type", "room");
            object.put("room_id", "" + ROOM_ID);
            object.put("unseen", 1);
            object.put("notification_number", "" + position);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("device find url is " + url + " " + object);
        new GetJsonTask(DeviceLogRoomActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("result is " + result);
                        JSONArray dataObj = result.optJSONArray("data");
                        Gson gson = new Gson();
                        List<DeviceLog> deviceLogs = gson.fromJson(dataObj.toString(), new TypeToken<List<DeviceLog>>() {
                        }.getType());
                        ;

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
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetLogFind(ROOM_ID, position, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("result is " + result);
                        JSONArray dataObj = result.optJSONArray("data");
                        Gson gson = new Gson();
                        List<DeviceLog> deviceLogs = gson.fromJson(dataObj.toString(), new TypeToken<List<DeviceLog>>() {
                        }.getType());
                        ;

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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ll_empty.setVisibility(View.VISIBLE);
                rv_device_log.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ll_empty.setVisibility(View.VISIBLE);
                rv_device_log.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
            }
        });

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

    /*back pressed after clear log count*/
    private void callreadCountApi() {

//        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

//        JSONObject jsonObject = new JSONObject();

        JsonArray jsonArray = new JsonArray();

        JsonObject object = new JsonObject();
        object.addProperty("sensor_type", "");
        object.addProperty("module_id", "");
        object.addProperty("room_id", "" + ROOM_ID);
        object.addProperty("user_id", Common.getPrefValue(this, Constants.USER_ID));
        jsonArray.add(object);
//            jsonObject.put("update_logs", jsonArray);



       /* new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new

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
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/


        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().CallreadCountApi(jsonArray, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                DeviceLogRoomActivity.this.finish();
            }

            @Override
            public void onData_FailureResponse() {
                DeviceLogRoomActivity.this.finish();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                DeviceLogRoomActivity.this.finish();
            }
        });

    }
}
