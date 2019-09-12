package com.spike.bot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.DeviceLogAdapter;
import com.spike.bot.adapter.filter.FilterRootAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.listener.FilterMarkAll;
import com.spike.bot.listener.OnLoadMoreListener;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.Filter;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;
import com.spike.bot.model.SensorLogRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

public class DeviceLogActivity extends AppCompatActivity implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, FilterMarkAll {
    String TAG = "DeviceLogActivity";
    Activity activity;
    RecyclerView rv_device_log;
    Toolbar toolbar;

    public CardView cardViewBtn;
    private LinearLayout ll_empty;
    DeviceLogAdapter deviceLogAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private OnLoadMoreListener onLoadMoreListener;
    public SwipeRefreshLayout swiperefresh;

    public boolean isScrollview = false, isFilterType = false, isFilterActive = false, isSensorLog = false, isDateFilterActive = false, isScrollDown = false,
    isMultiSensor=false;

    private String isSelectItem = "",isSelectItemSub="",isSelectItemSubTemp="", isRoomName = "", strDeviceId = "", strpanelId = "", mRoomId, Schedule_id = "", activity_type = "", tabSelect = "", Mood_Id = "",
            actionType = "", isCheckActivity = "";
    private Button btnDevice;
    private Button btnSensor;
    CheckBox checkBoxMark;

    ArrayAdapter<RoomVO> adapterRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //toolbar.setTitle("Device Log");
        activity = this;
        rv_device_log = (RecyclerView) findViewById(R.id.rv_device_log);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        ll_empty.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        String userFname = Common.getPrefValue(ChatApplication.getInstance(), "first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(), "last_name");

        //  setTitle(""+userFname + " " + userLname);

        mRoomId = getIntent().getStringExtra("ROOM_ID");
        Schedule_id = getIntent().getStringExtra("Schedule_id");
        activity_type = getIntent().getStringExtra("activity_type");
        isSensorLog = getIntent().getBooleanExtra("IS_SENSOR", false);
        tabSelect = getIntent().getStringExtra("tabSelect");
        Mood_Id = getIntent().getStringExtra("Mood_Id");
        isCheckActivity = getIntent().getStringExtra("isCheckActivity");
        isRoomName = getIntent().getStringExtra("isRoomName");

        if (isCheckActivity.equals("Timer") || isCheckActivity.equals("room")
                || isCheckActivity.equals("schedule") || isCheckActivity.equals("mode")) {
            setTitle("" + isRoomName + " Logs");
        } else if (isCheckActivity.equals("doorSensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName+" Logs");
        } else if (isCheckActivity.equals("tempSensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName+" Logs");
        } else if (isCheckActivity.equals("multisensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName+" Logs");
        } else {
            setTitle("LOGS");
        }


        if (TextUtils.isEmpty(Schedule_id)) {
            Schedule_id = "";
        }
        if (TextUtils.isEmpty(tabSelect)) {
            tabSelect = "";
        }
        if (TextUtils.isEmpty(mRoomId)) {
            mRoomId = "";
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rv_device_log.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        onLoadMoreListener = this;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv_device_log.getLayoutManager();

        deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this, deviceLogList);
        rv_device_log.setAdapter(deviceLogAdapter);

        rv_device_log.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mScrollState = newState;

                if (newState == SCROLL_STATE_IDLE) {

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (deviceLogList.size() != 0 && mScrollState == SCROLL_STATE_IDLE) {
                        if (deviceLogList.size() >= 25) {
                            if ((deviceLogList.size() - 1) == lastVisibleItem) {
                                if (onLoadMoreListener != null && !isScrollDown) {
                                    isLoading = true;
                                    isScrollDown = true;
                                    onLoadMoreListener.onLoadMore(deviceLogList.size());
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
        swipeRefreshLayout.setOnRefreshListener(this);

        btnDevice = (Button) findViewById(R.id.btn_device);
        btnSensor = (Button) findViewById(R.id.btn_sensor);
        cardViewBtn = (CardView) findViewById(R.id.cardViewBtn);

        if (tabSelect.equalsIgnoreCase("hide")) {
            cardViewBtn.setVisibility(View.GONE);
        } else {
            cardViewBtn.setVisibility(View.GONE);
        }

        udpateButton();

        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = false;
                isScrollDown = false;
                deviceLogList.clear();
                udpateButton();
                getDeviceLog(0);
            }
        });

        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = true;
                isScrollDown = false;
                udpateButton();
                getSensorLog(0);
            }
        });

        //added by sagar
//        if (isSensorLog) {
//            getSensorLog(0);
//        } else {
        if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")|| isCheckActivity.equals("multisensor")) {
            getSensorLog(0);
        } else {
            getDeviceLog(0);
        }
//        }

    }

    private void udpateButton() {

        if (isSensorLog) {
            btnDevice.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensor.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));

        } else {
            btnDevice.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensor.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
        }

        btnDevice.setTextColor(getResources().getColor(R.color.automation_white));
        btnSensor.setTextColor(getResources().getColor(R.color.automation_white));
    }

    private void udpateDailogButton(Button btnDeviceDialog, Button btnSensorDialog) {

        //if (isSensorLog) {
        if (isFilterType) {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));

        } else {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
        }

        btnDeviceDialog.setTextColor(getResources().getColor(R.color.automation_white));
        btnSensorDialog.setTextColor(getResources().getColor(R.color.automation_white));
    }

    //get sensor log when click on sensor button
    private void getSensorLog(int lastVisibleItem) {
        if (lastVisibleItem == 0) {
            deviceLogList.clear();
        }
        if (deviceLogAdapter != null) {
            deviceLogAdapter.notifyDataSetChanged();
        }

        applyFilter(lastVisibleItem, true);
    }

    private void applyFilter(final int position, boolean isSensorIntent) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        //API =  sensorRoomDetails

        String webUrl = ChatApplication.url + Constants.SENSOR_NOTIFICATION;

        JSONObject jsonNotification = new JSONObject();

        String sensor_type = "";

        sensor_type = "";

        try {
            jsonNotification.put("notification_number", position);
          /*  jsonNotification.put("module_id", TextUtils.isEmpty(Mood_Id) ? "" : Mood_Id);
            jsonNotification.put("room_id", TextUtils.isEmpty(mRoomId) ? "" : mRoomId);*/
            jsonNotification.put("module_id", "");
            jsonNotification.put("room_id", "");
            jsonNotification.put("end_datetime", "");
            jsonNotification.put("start_datetime", "");

            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {
                if (isCheckActivity.equals("doorSensor")) {
                    jsonNotification.put("sensor_type", "door");
                } else if (isCheckActivity.equals("tempsensor")) {
                    jsonNotification.put("sensor_type", "temp");
                }else if (isCheckActivity.equals("multisensor")) {
                    jsonNotification.put("sensor_type", "temp");
                }

                jsonNotification.put("module_id", "" + Mood_Id);
                jsonNotification.put("room_id", "" + mRoomId);
            } else {
                jsonNotification.put("sensor_type", "" + sensor_type);
            }

            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonNotification.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
            jsonNotification.put("filter_data", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                swipeRefreshLayout.setRefreshing(false);
                try {
//{"room_id":"1559719722430_hp_nJGEwO","room_order":0,"multi_sensor_id":"1561119898083_wE4svmRfN","is_in_C":1,"temp_in_C":26,"temp_in_F":78,"gas_status":"Normal","gas_value":1,"gas_threshold_value":15,"humidity":38}

                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (position == 0) {
                            deviceLogList.clear();
                        }
                        ll_empty.setVisibility(View.GONE);
                        rv_device_log.setVisibility(View.VISIBLE);

                        JSONObject dataObj = result.getJSONObject("data");
                        ChatApplication.logDisplay("dataObj is "+dataObj);

                        if (dataObj.has("devicefilterList")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = dataObj.getJSONArray("devicefilterList");
                            //  ListUtils.filters.clear();
//                            if(!isScrollview){
                            if (!isFilterActive) {
                                if (filterArrayListTemp != null) {
                                    filterArrayListTemp.clear();
                                    filterArrayList.clear();
                                }
                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    String action_name = object.getString("action_name");
//
//                                if (isCheckActivity.equals("mode") && logName.equals("Mood")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("schedule") && logName.equals("Schedule")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("room") && logName.equals("Room")) {
//                                    addValueFilter(action_name,logName);
//                                } else {
//                                    addValueFilter(action_name,logName);
                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {
                                        subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                    }
//                                    filterArrayList.add(new Filter(logName, false, false, subFilterArrayList));
                                    filterArrayListTemp.add(new Filter(logName, false, false, subFilterArrayList));

//                                }
                                }
                            }

//                            }


                        }

                        if (dataObj.has("sensorfilterList")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = dataObj.getJSONArray("sensorfilterList");

                            if (!isFilterActive) {
                                if (filterArrayListSensorTemp != null) {
                                    filterArraySensor.clear();
                                    filterArrayListSensorTemp.clear();
                                }

                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    String action_name = object.getString("action_name");
//
//                                if (isCheckActivity.equals("mode") && logName.equals("Mood")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("schedule") && logName.equals("Schedule")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("room") && logName.equals("Room")) {
//                                    addValueFilter(action_name,logName);
//                                } else {
//                                    addValueFilter(action_name,logName);
                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {
                                        subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                    }
//                                    filterArraySensor.add(new Filter(logName, false, false, subFilterArrayList));
                                    filterArrayListSensorTemp.add(new Filter(logName, false, false, subFilterArrayList));

                                }
//                                }
                            }


                        }

                        JSONArray notificationArray = dataObj.getJSONArray("notificationList");

                        if (notificationArray.length() == 0) {
                            isScrollDown = true;
                        } else {
                            isScrollDown = false;
                        }
                        for (int i = 0; i < notificationArray.length(); i++) {
                            JSONObject jsonObject = notificationArray.getJSONObject(i);
                            String activity_action = jsonObject.getString("activity_action");
                            String activity_type = jsonObject.getString("activity_type");
                            String activity_description = jsonObject.getString("activity_description");
                            String activity_time = jsonObject.getString("activity_time");
                            String user_name = jsonObject.optString("user_name");
                            String is_unread = jsonObject.optString("is_unread");
                            //  String activity_state = jsonObject.getString("activity_state");

                            if(!isMultiSensor && is_unread.equalsIgnoreCase("1")){
                                isMultiSensor=true;
                            }
                            //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state

                            deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "",user_name,is_unread));
                        }

                        if (notificationArray.length() == 0 && isFilterActive && isLoading) {

                            if (!isEndOfRecord) {
                                deviceLogList.add(new DeviceLog("End of Record", "End of Record", "", "", "","",""));
                                deviceLogAdapter.setEOR(true);
                            }
                            isEndOfRecord = true;

                            //showAToast("End of Record...");
                        } else if (notificationArray.length() == 0 && isFilterActive) {
                            deviceLogList.add(new DeviceLog("End of Record", "No Record Found", "", "", "","",""));
                            deviceLogAdapter.setEOR(true);
                            showAToast("No Record Found...");
                        }
                        deviceLogAdapter.notifyDataSetChanged();

                        ActivityHelper.dismissProgressDialog();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            }
        }).execute();

    }

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    private int lastVisibleItem, totalItemCount;
    private boolean isLoading = false;
    private int visibleThreshold = 5;

    @Override
    public void onLoadMore(int lastVisibleItem) {
        isScrollview = true;
        getDeviceLog(lastVisibleItem);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        ListUtils.start_date_filter = "";
        ListUtils.end_date_filter = "";
        if(isMultiSensor){
            unreadApiCall(false);
        }else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        MenuItem menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setVisible(false);
        menuItem.setVisible(true);
        if (isFilterActive) {
            menuItemReset.setVisible(false);
            menuItem.setIcon(R.drawable.icn_filter_on);
        } else {
            menuItemReset.setVisible(false);
            menuItem.setIcon(R.drawable.icn_filter);
        }

        if (tabSelect.equalsIgnoreCase("hide")) {
            menuItem.setVisible(false);
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
            isMarkAll = true;
            openDialogFilter();
            return true;
        } else if (id == R.id.action_reset) {

            isFilterActive = false;
            isEndOfRecord = false;

            invalidateOptionsMenu();
            deviceLogList.clear();
            isDateFilterActive = false;
            deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this, deviceLogList);
            rv_device_log.setAdapter(deviceLogAdapter);
            deviceLogAdapter.notifyDataSetChanged();
            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {

                if(isCheckActivity.equals("multisensor") && isMultiSensor){
                    unreadApiCall(true);
                }else {
                    getSensorLog(0);
                }
            } else {
                //   udpateButton();
                if (isCheckActivity.equals("AllType")) {
                    isFilterType = false;
                    isFilterActive = false;
                }
                getDeviceLog(0);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Filter> filterArrayList = new ArrayList<>();
    private ArrayList<Filter> filterArrayListTemp = new ArrayList<>();
    private ArrayList<Filter> filterArraySensor = new ArrayList<>();
    private ArrayList<Filter> filterArrayListSensorTemp = new ArrayList<>();

    CustomEditText edt_start_date;
    CustomEditText edt_end_date;

    public boolean isMarkAll = true;

    private Spinner mSpinnerRoomMood;
    private Spinner mSpinnerRoomList;
    private Spinner mSpinnerPanelList;
    private Spinner mSpinnerDeviceList;
    public LinearLayout panel_view;
    public FrameLayout frame_living_room, frame_all_devices;
    public Button btnDeviceDialog, btnSensorDialog, btnReset, btnSave, btnCancel;
    public RecyclerView recyclerView;
    public Dialog dialog;

    private void openDialogFilter() {
        //isFilterType = false;


//        if(isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")){
//            isFilterType=true;
//        }
        isScrollview = false;

        if (dialog == null) {
            dialog = new Dialog(this, R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_filter);

            btnCancel = dialog.findViewById(R.id.btn_cancel_filter);
            btnSave = dialog.findViewById(R.id.btn_save_filter);
            btnReset = dialog.findViewById(R.id.btn_cancel_reset);

            checkBoxMark = dialog.findViewById(R.id.chk_all);
            final TextView title_check_all = dialog.findViewById(R.id.title_check_all);

            mSpinnerRoomMood = (Spinner) dialog.findViewById(R.id.spinner_room_mood);
            mSpinnerRoomList = (Spinner) dialog.findViewById(R.id.spinner_room_list);
            mSpinnerPanelList = (Spinner) dialog.findViewById(R.id.spinner_panel_list);
            mSpinnerDeviceList = (Spinner) dialog.findViewById(R.id.spinner_device_list);
            panel_view = (LinearLayout) dialog.findViewById(R.id.panel_view);
            frame_living_room = (FrameLayout) dialog.findViewById(R.id.frame_living_room);
            frame_all_devices = (FrameLayout) dialog.findViewById(R.id.frame_all_devices);
            btnSensorDialog = (Button) dialog.findViewById(R.id.btnSensorDialog);
            btnDeviceDialog = (Button) dialog.findViewById(R.id.btnDeviceDialog);

            edt_start_date = (CustomEditText) dialog.findViewById(R.id.edt_log_start_date);
            edt_end_date = (CustomEditText) dialog.findViewById(R.id.edt_log_end_date);

            recyclerView = (RecyclerView) dialog.findViewById(R.id.root_list);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            end_date = ChatApplication.getCurrentDateTime();
            start_date = ChatApplication.getCurrentDate();
            edt_start_date.setText("" + ChatApplication.getCurrentDate());
            edt_end_date.setText("" + ChatApplication.getCurrentDateTime());

            checkBoxMark.setChecked(isMarkAll);
            strpanelId = "";
            strDeviceId = "";

            if(isCheckActivity.equals("tempsensor")){
                isSelectItem="1";
            }else if(isCheckActivity.equals("doorSensor")){
                isSelectItem="2";
            }
        }else {
            if (mSpinnerRoomMood != null) {
                isSelectItem = "" + mSpinnerRoomMood.getSelectedItemPosition();
            } else {
                isSelectItem = "";
            }

            if(mSpinnerRoomList!=null){
                isSelectItemSub = "" + mSpinnerRoomList.getSelectedItemPosition();
                isSelectItemSubTemp = "" + mSpinnerRoomList.getSelectedItemPosition();
            }else {
                isSelectItemSub="";
                isSelectItemSubTemp="";
            }
        }

        setAdapterFilterVIew();

//        filterArrayList = ListUtils.getFilterList();

        final FilterRootAdapter filterRootAdapter = new FilterRootAdapter(filterArrayList, this);
        recyclerView.setAdapter(filterRootAdapter);
        filterRootAdapter.notifyDataSetChanged();




//        if(isMarkAll){
//            title_check_all.setText(R.string.umnark_all);
//        }else{
//            title_check_all.setText(R.string.mark_all);
//        }


        if(isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")){
            isSensorLog = true;
            isFilterType = true;

        }

        udpateDailogButton(btnDeviceDialog, btnSensorDialog);

        initSpinnerData(filterRootAdapter);

        btnSensorDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = true;
                isFilterType = true;
                isSelectItem = "";
                isSelectItemSub = "";
                isSelectItemSubTemp = "";
                udpateDailogButton(btnDeviceDialog, btnSensorDialog);
                initSpinnerData(filterRootAdapter);
            }
        });

        btnDeviceDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = false;
                isFilterType = false;
                isSelectItem = "";
                isSelectItemSub = "";
                isSelectItemSubTemp = "";
                udpateDailogButton(btnDeviceDialog, btnSensorDialog);
                initSpinnerData(filterRootAdapter);
            }
        });

        // udpateDailogButton(btnDeviceDialog, btnSensorDialog);

        checkBoxMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxMark.isChecked()) {
                    isMarkAll = true;
                    for (Filter filter : filterArrayList) {
                        filter.setChecked(true);
                        for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                            subFilter.setChecked(true);
                        }
                    }
                    filterRootAdapter.notifyDataSetChanged();
                } else {
                    isMarkAll = false;
                    for (Filter filter : filterArrayList) {
                        filter.setChecked(false);
                        for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                            subFilter.setChecked(false);
                        }
                    }
                    filterRootAdapter.notifyDataSetChanged();
                }
            }
        });

//        checkBoxMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                isMarkAll = isChecked;
//                for (Filter filter : filterArrayList) {
//                    filter.setChecked(isChecked);
//                    for (Filter.SubFilter subFilter : filter.getSubFilters()) {
//                        subFilter.setChecked(isChecked);
//                    }
//                }
//                filterRootAdapter.notifyDataSetChanged();
//            }
//        });


//        edt_start_date.setText(ListUtils.start_date_filter);
//        edt_end_date.setText(ListUtils.end_date_filter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isCompareDateValid) {
                    Toast.makeText(getApplicationContext(), "End date is not less than Start Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isFlagMark = false;

                for (int i = 0; i < filterArrayList.size(); i++) {
                    if (filterArrayList.get(i).isChecked()) {
                        isFlagMark = true;
                    }
                }

                if (!isFlagMark) {
                    Toast.makeText(getApplicationContext(), "Please select atleast one Filter type", Toast.LENGTH_SHORT).show();
                    return;
                }
                ListUtils.start_date_filter = edt_start_date.getText().toString();
                ListUtils.end_date_filter = edt_end_date.getText().toString();

                isEndOfRecord = false;

                isFilterActive = true;
                invalidateOptionsMenu();
                deviceLogList.clear();
                dialog.dismiss();
                getDeviceLog(0);
                // generateFilterList();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListUtils.start_date_filter = "";
                ListUtils.end_date_filter = "";
                isSelectItem = "";
                isSelectItemSub = "";
                isSelectItemSubTemp = "";
                edt_start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                edt_end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                end_date = ChatApplication.getCurrentDateTime();
                start_date = ChatApplication.getCurrentDate();
                edt_start_date.setText("" + ChatApplication.getCurrentDate());
                edt_end_date.setText("" + ChatApplication.getCurrentDateTime());

                if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
                    isSensorLog = true;
                    isFilterType = true;
                    if(isCheckActivity.equals("tempsensor")){
                        isSelectItem="1";
                    }else if(isCheckActivity.equals("doorSensor")){
                        isSelectItem="2";
                    }
                }

                checkBoxMark.setChecked(true);
                isMarkAll = true;

                isDateFilterActive = false;

                isEndOfRecord = false;

                start_date = "";
                end_date = "";

                udpateDailogButton(btnDeviceDialog, btnSensorDialog);

                for (Filter filter : filterArrayList) {
                    filter.setExpanded(false);
                    filter.setChecked(true);
                    for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                        subFilter.setChecked(false);
                    }
                }
                filterRootAdapter.notifyDataSetChanged();

//                frame_living_room.setVisibility(View.GONE);
//                panel_view.setVisibility(View.GONE);

                strpanelId = "";
                strDeviceId = "";
                setAdapterFilterVIew();
                initSpinnerData(filterRootAdapter);
            }
        });

        //2018-04-09 18:05:13
        //2018-04-10 18:51:23

        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_date = "";
                datePicker(edt_start_date, false);
            }
        });
        edt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_date = "";
                datePicker(edt_end_date, true);
            }
        });

        //start date
        if (TextUtils.isEmpty(edt_start_date.getText().toString())) {
            edt_start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        edt_start_date.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:

                        isCompareDateValid = true;

                        edt_start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        edt_start_date.setText("");

                        ListUtils.start_date_filter = "";
                        start_date = "";

                        break;

                    default:
                        break;
                }
            }
        });
        edt_start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edt_start_date.length() > 0) {
                    edt_start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    edt_start_date.setCompoundDrawablePadding(8);
                }
            }
        });

        //end date
        if (TextUtils.isEmpty(edt_end_date.getText().toString())) {
            edt_end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        edt_end_date.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //

                        isCompareDateValid = true;
                        ListUtils.end_date_filter = "";
                        end_date = "";

                        edt_end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        edt_end_date.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
        edt_end_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edt_end_date.length() > 0) {
                    edt_end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    edt_end_date.setCompoundDrawablePadding(8);
                }
            }
        });


        if (!TextUtils.isEmpty(start_date)) {
            edt_start_date.setText("" + start_date);
            edt_end_date.setText("" + end_date);
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void setAdapterFilterVIew() {
        if(isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")){
            if (filterArrayList != null) {
                filterArrayList.clear();
            }
            if(isCheckActivity.equals("tempsensor")){
                for (int j = 0; j < filterArrayListSensorTemp.size(); j++)
                    if(filterArrayListSensorTemp.get(j).getName().equals("Temperature Sensor")){
                            for (int i = 0; i < filterArrayListSensorTemp.get(j).getSubFilters().size(); i++) {
                                Filter filter = new Filter();
                                filter.setName(filterArrayListSensorTemp.get(j).getSubFilters().get(i).getName());
                                filter.setChecked(true);
                                filterArrayList.add(filter);
                            }
                    }
            }

            if(isCheckActivity.equals("doorSensor")){
                for (int j = 0; j < filterArrayListSensorTemp.size(); j++)
                    if(filterArrayListSensorTemp.get(j).getName().equals("Door Sensor")){
                            for (int i = 0; i < filterArrayListSensorTemp.get(j).getSubFilters().size(); i++) {
                                Filter filter = new Filter();
                                filter.setName(filterArrayListSensorTemp.get(j).getSubFilters().get(i).getName());
                                filter.setChecked(true);
                                filterArrayList.add(filter);
                            }
                    }

            }


        }else {
            if (TextUtils.isEmpty(isSelectItem)) {
                if (isFilterType) {
                    if (filterArrayList != null) {
                        filterArrayList.clear();
                    }
                    for (int j = 0; j < filterArrayListSensorTemp.size(); j++)
                        for (int i = 0; i < filterArrayListSensorTemp.get(j).getSubFilters().size(); i++) {
                            Filter filter = new Filter();
                            filter.setName(filterArrayListSensorTemp.get(j).getSubFilters().get(i).getName());
                            filter.setChecked(true);
                            filterArrayList.add(filter);
                        }
                } else {
                    if (!isCheckActivity.equals("AllType")) {

                        if (filterArrayList != null) {
                            filterArrayList.clear();
                        }

                        for (int j = 0; j < filterArrayListTemp.size(); j++)
                            for (int i = 0; i < filterArrayListTemp.get(j).getSubFilters().size(); i++) {
//                        if (isCheckActivity.equals("mode") && filterArrayListTemp.get(j).getName().equals("Mood")) {
//                            actionType = "Mood";
//                            Filter filter = new Filter();
//                            filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
//                            filter.setChecked(true);
//                            filterArrayList.add(filter);
//                        } else if (isCheckActivity.equals("schedule") && filterArrayListTemp.get(j).getName().equals("Schedule")) {
//                            actionType = "Schedule";
//                            Filter filter = new Filter();
//                            filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
//                            filter.setChecked(true);
//                            filterArrayList.add(filter);
//                        } else if (isCheckActivity.equals("room") && filterArrayListTemp.get(j).getName().equals("Room")) {
//                            actionType = "Room";
//                            Filter filter = new Filter();
//                            filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
//                            filter.setChecked(true);
//                            filterArrayList.add(filter);
//                        } else if (isCheckActivity.equals("AllType") && filterArrayListTemp.get(j).getName().equals("All")) {
                                Filter filter = new Filter();
                                filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
                                filter.setChecked(true);
                                filterArrayList.add(filter);
//                        }

                            }
                    }
                }
            }

        }
    }

    private List<String> mListRoomMood;
    private List<RoomVO> mListRoom;
    private List<RoomVO> mListRoomTemp = new ArrayList<>();
    private List<PanelVO> mListPanel;
    private List<DeviceVO> mListDevice;
    ArrayList<DeviceVO> arrayListDeviceTemp = new ArrayList<>();
    ArrayList<ScheduleVO> scheduleRoomArrayList = new ArrayList<>();

    //fill spinner adapter
    private void initSpinnerData(final FilterRootAdapter filterRootAdapter) {

        mListRoomMood = new ArrayList<>();
        mListRoom = new ArrayList<>();
        mListPanel = new ArrayList<>();
        mListDevice = new ArrayList<>();


        if (isFilterType) {
            panel_view.setVisibility(View.VISIBLE);
            frame_living_room.setVisibility(View.GONE);
            for (int i = 0; i < filterArrayListSensorTemp.size(); i++) {
                mListRoomMood.add(filterArrayListSensorTemp.get(i).getName());
            }

        } else {
            for (int i = 0; i < filterArrayListTemp.size(); i++) {
                mListRoomMood.add(filterArrayListTemp.get(i).getName());
            }


            /*if (isCheckActivity.equals("room")) {
//                mListRoomMood.add("Room");
                mSpinnerRoomMood.setSelection(1);
                panel_view.setVisibility(View.VISIBLE);
                frame_living_room.setVisibility(View.VISIBLE);
//                getDeviceList("Room");
            } else if (isCheckActivity.equals("schedule")) {
//                mListRoomMood.add("Schedule");
                mSpinnerRoomMood.setSelection(3);
            }else if (isCheckActivity.equals("Timer")) {
                //mListRoomMood.add("Timer");
                mSpinnerRoomMood.setSelection(4);
            } else if (isCheckActivity.equals("mode")) {
              //  mListRoomMood.add("Mood");
                mSpinnerRoomMood.setSelection(2);
                panel_view.setVisibility(View.GONE);
                frame_living_room.setVisibility(View.VISIBLE);
               // getDeviceList("Mood");
            } else {

            }*/

        }

//        mListRoomMood.add("Room");
//        mListRoomMood.add("Mood");

        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected, mListRoomMood);
        mSpinnerRoomMood.setAdapter(adapterRoomMood);

        if (!isFilterType) {
            if (TextUtils.isEmpty(isSelectItem)) {
                mSpinnerRoomMood.setSelection(ChatApplication.checkSelection(isCheckActivity, mListRoomMood));
                if (isCheckActivity.equals("room")) {
//                mListRoomMood.add("Room");
                    // mSpinnerRoomMood.setSelection(1);
                    panel_view.setVisibility(View.VISIBLE);
                    frame_all_devices.setVisibility(View.INVISIBLE);
                    frame_living_room.setVisibility(View.VISIBLE);

                    if(mSpinnerRoomList.getSelectedItemPosition()>0){
                        panel_view.setVisibility(View.VISIBLE);
                    }else {
                        panel_view.setVisibility(View.GONE);
                    }

                    if(mSpinnerPanelList.getSelectedItemPosition()>0){
                        frame_all_devices.setVisibility(View.VISIBLE);
                    }else {
                        frame_all_devices.setVisibility(View.INVISIBLE);
                    }
                    getDeviceList("Room");
                } else if (isCheckActivity.equals("schedule")) {
//                mListRoomMood.add("Schedule");
                    //   mSpinnerRoomMood.setSelection(3);
                    panel_view.setVisibility(View.GONE);
                    frame_living_room.setVisibility(View.VISIBLE);
                    getDeviceList("Schedule");
                } else if (isCheckActivity.equals("Timer")) {
                    //mListRoomMood.add("Timer");
                    //  mSpinnerRoomMood.setSelection(4);
                    panel_view.setVisibility(View.GONE);
                    frame_living_room.setVisibility(View.VISIBLE);
                    getDeviceList("Timer");
                } else if (isCheckActivity.equals("mode")) {
                    //  mListRoomMood.add("Mood");
                    // mSpinnerRoomMood.setSelection(2);
                    panel_view.setVisibility(View.GONE);
                    frame_living_room.setVisibility(View.VISIBLE);
                    getDeviceList("Mood");
                } else {
                    getDeviceList("");
                }
            }
        }

        if (!TextUtils.isEmpty(isSelectItem)) {
            mSpinnerRoomMood.setSelection(Integer.parseInt(isSelectItem));
        }

        mSpinnerRoomMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelectItemSub="";
                if (isFilterType) {
                    if (position != 0) {
                        frame_living_room.setVisibility(View.VISIBLE);
                        panel_view.setVisibility(View.GONE);
//                       if(mListRoomMood.get(position).equalsIgnoreCase("Door Sensor")){
                        actionType = mListRoomMood.get(position);
//                       }else {
//                           actionType = "temp";
//                       }
                        getDeviceList(actionType);

                    } else {
                        frame_living_room.setVisibility(View.GONE);
                        panel_view.setVisibility(View.GONE);
                        actionType = "";
                    }

                } else {
                    if (mListRoomMood.get(position).equalsIgnoreCase("All")) {
                        frame_living_room.setVisibility(View.GONE);
                        panel_view.setVisibility(View.GONE);
                        actionType = "";
                    } else if (mListRoomMood.get(position).equalsIgnoreCase("Room")) {
                        if (isCheckActivity.equals("room")) {
                            panel_view.setVisibility(View.VISIBLE);
                            frame_living_room.setVisibility(View.GONE);
                        } else {
                            panel_view.setVisibility(View.VISIBLE);
                            frame_living_room.setVisibility(View.VISIBLE);
                        }

                        getDeviceList("Room");
                        actionType = "Room";
                    } else if (mListRoomMood.get(position).equalsIgnoreCase("Schedule")) {
                        panel_view.setVisibility(View.GONE);
                        frame_living_room.setVisibility(View.VISIBLE);
                        getDeviceList("Schedule");
                        actionType = "Schedule";
                    } else if (mListRoomMood.get(position).equalsIgnoreCase("Timer")) {
                        panel_view.setVisibility(View.GONE);
                        frame_living_room.setVisibility(View.VISIBLE);
                        getDeviceList("Timer");
                        actionType = "Timer";
                    } else {
                        actionType = mSpinnerRoomMood.getSelectedItem().toString();
                        panel_view.setVisibility(View.GONE);
                        if (mListRoomMood.get(position).equalsIgnoreCase("Mood")) {
                            frame_living_room.setVisibility(View.VISIBLE);
                            getDeviceList("Mood");
                        } else {
                            frame_living_room.setVisibility(View.GONE);
                        }
                    }
                }

                if (TextUtils.isEmpty(isSelectItem)) {
                    isSelectItem = "" + mSpinnerRoomMood.getSelectedItemPosition();

                    if (isFilterType) {
                        filterArrayList.clear();
                        for (int i = 0; i < filterArrayListSensorTemp.get(position).getSubFilters().size(); i++) {
                            Filter filter = new Filter();
                            filter.setName(filterArrayListSensorTemp.get(position).getSubFilters().get(i).getName());
                            filter.setChecked(true);
                            filterArrayList.add(filter);
                        }

                        filterRootAdapter.notifyDataSetChanged();
                    } else {
                        filterArrayList.clear();
                        for (int j = 0; j < filterArrayListTemp.size(); j++)
                            for (int i = 0; i < filterArrayListTemp.get(j).getSubFilters().size(); i++) {
                                if (filterArrayListTemp.get(j).getName().equals(mListRoomMood.get(position))) {
                                    Filter filter = new Filter();
                                    filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
                                    filter.setChecked(true);
                                    filterArrayList.add(filter);
                                }

                            }
                    }
                    isMarkAll = true;
                    checkBoxMark.setChecked(isMarkAll);
                    filterRootAdapter.notifyDataSetChanged();
                } else {
                    if (Integer.parseInt(isSelectItem) != position) {
                        if (isFilterType) {
                            filterArrayList.clear();
                            for (int i = 0; i < filterArrayListSensorTemp.get(position).getSubFilters().size(); i++) {
                                Filter filter = new Filter();
                                filter.setName(filterArrayListSensorTemp.get(position).getSubFilters().get(i).getName());
                                filter.setChecked(true);
                                filterArrayList.add(filter);
                            }

                            filterRootAdapter.notifyDataSetChanged();
                        } else {
                            filterArrayList.clear();
                            for (int j = 0; j < filterArrayListTemp.size(); j++)
                                for (int i = 0; i < filterArrayListTemp.get(j).getSubFilters().size(); i++) {
                                    if (filterArrayListTemp.get(j).getName().equals(mListRoomMood.get(position))) {
                                        Filter filter = new Filter();
                                        filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
                                        filter.setChecked(true);
                                        filterArrayList.add(filter);
                                    }

                                }
                        }
                        isMarkAll = true;
                        checkBoxMark.setChecked(isMarkAll);
                        filterRootAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private JSONArray panelArray;
    private JSONArray deviceArray;

    private void getDeviceList(final String room) {
        String url = "",urlType="POST",parameter="";
        actionType = room;

        JSONObject jsonObject = new JSONObject();

        if (room.equalsIgnoreCase("Room")) {
         //   url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
            url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        } else if (room.equalsIgnoreCase("Schedule") || room.equalsIgnoreCase("Timer")) {
            url = ChatApplication.url + Constants.GET_SCHEDULE_LIST_LOG;
        } else if (room.equalsIgnoreCase("sensor") || room.equalsIgnoreCase("Door Sensor")
                || room.equalsIgnoreCase("Temperature Sensor")|| room.equalsIgnoreCase("Multi Sensor")) {
            urlType="GET";
            url = ChatApplication.url + Constants.SENSOR_ROOM_DETAILS;
        } else {
            url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        }

        try {
            if(room.equalsIgnoreCase("Room")){
                jsonObject.put("room_type", 0);
                jsonObject.put("is_sensor_panel", 1);
                jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            }else if(room.equalsIgnoreCase("Mood")) {
                jsonObject.put("room_type", 1);
                jsonObject.put("is_sensor_panel", 0);
                jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            } else if(room.equalsIgnoreCase("Schedule") || room.equalsIgnoreCase("Timer")) {

            }
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("admin", Common.getPrefValue(this, Constants.USER_ADMIN_TYPE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json="";
        if(urlType.equalsIgnoreCase("GET")){
            json="";
        }else {
            try {
                jsonObject.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
                jsonObject.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            json=jsonObject.toString();
        }
        ChatApplication.logDisplay("json is "+json.toString()+ " "+url);
        new GetJsonTask2(activity, url, urlType , json, new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    if (mListRoom != null) {
                        mListRoom.clear();
                        mListRoomTemp.clear();
                    }
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        scheduleRoomArrayList.clear();

                        ChatApplication.logDisplay("data is "+result);

                        if (isFilterType) {
                            frame_living_room.setVisibility(View.VISIBLE);
                            panel_view.setVisibility(View.GONE);
                            SensorLogRes mSensorLogRes = Common.jsonToPojo(result.toString(), SensorLogRes.class);

                            RoomVO oneRoom = new RoomVO();
                            oneRoom.setRoomId("0");
//                            if (room.equalsIgnoreCase("Temperature Sensor")) {
                            if (room.equalsIgnoreCase("Multi Sensor") || room.equalsIgnoreCase("Temperature Sensor") ) {
//                                oneRoom.setRoomName("All Temp");
                                oneRoom.setRoomName("All Temperature sensor");
                                mListRoom.add(0, oneRoom);
                                if(mSensorLogRes.getData().getTempSensor()!=null){
                                    for (int i = 0; i < mSensorLogRes.getData().getTempSensor().size(); i++) {
                                        RoomVO roomVO = new RoomVO();
                                        roomVO.setRoomName(mSensorLogRes.getData().getTempSensor().get(i).getTempSensorName());
                                        roomVO.setRoomId(mSensorLogRes.getData().getTempSensor().get(i).getRoomId());
                                        mListRoom.add(roomVO);
                                    }
                                }


                            } else {
                                oneRoom.setRoomName("All Door");
                                mListRoom.add(0, oneRoom);
                                if( mSensorLogRes.getData().getDoorSensor()!=null){
                                    for (int i = 0; i < mSensorLogRes.getData().getDoorSensor().size(); i++) {
                                        RoomVO roomVO = new RoomVO();
                                        roomVO.setRoomName(mSensorLogRes.getData().getDoorSensor().get(i).getDoorSensorName());
                                        roomVO.setRoomId(mSensorLogRes.getData().getDoorSensor().get(i).getRoomId());
                                        mListRoom.add(roomVO);
                                    }
                                }

                            }

                        } else {
                            JSONObject dataObject = result.optJSONObject("data");
                            if(dataObject!=null) {

                                if (room.equalsIgnoreCase("Schedule") || room.equalsIgnoreCase("Timer")) {
                                    JSONArray moodArray = dataObject.optJSONArray("moodSchedule");
                                    JSONArray roomArray = dataObject.optJSONArray("roomSchedule");

                                    scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(moodArray));
                                    scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(roomArray));

                                } else {
                                    JSONArray roomArray = dataObject.optJSONArray("roomdeviceList");
                                    mListRoom = JsonHelper.parseRoomArray(roomArray, false);
                                }

                                if (room.equalsIgnoreCase("Mood")) {
                                    frame_living_room.setVisibility(View.VISIBLE);
                                    RoomVO oneRoom = new RoomVO();
                                    oneRoom.setRoomId("0");
                                    oneRoom.setRoomName("All Mood");
                                    mListRoom.add(0, oneRoom);
//                                }
                                } else if (room.equalsIgnoreCase("Room")) {
                                    if (mSpinnerRoomList.getSelectedItemPosition() > 0) {
                                        panel_view.setVisibility(View.VISIBLE);
                                    } else {
                                        panel_view.setVisibility(View.GONE);
                                    }

                                    if (mSpinnerPanelList.getSelectedItemPosition() > 0) {
                                        frame_all_devices.setVisibility(View.VISIBLE);
                                    } else {
                                        frame_all_devices.setVisibility(View.INVISIBLE);
                                    }

                                    frame_living_room.setVisibility(View.VISIBLE);
                                    RoomVO oneRoom = new RoomVO();
                                    oneRoom.setRoomId("0");
                                    oneRoom.setRoomName("All Room");
                                    mListRoom.add(0, oneRoom);
//                                }
                                } else if (room.equalsIgnoreCase("schedule") || room.equalsIgnoreCase("Timer")) {
//                                if (isCheckActivity.equals("schedule")) {
//                                    for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
//                                        if (!TextUtils.isEmpty(scheduleRoomArrayList.get(i).getSchedule_id())) {
//                                            if (scheduleRoomArrayList.get(i).getSchedule_id().equals(mRoomId)) {
//                                                RoomVO roomVO = new RoomVO();
//                                                roomVO.setRoomName(scheduleRoomArrayList.get(i).getSchedule_name());
//                                                roomVO.setRoomId(scheduleRoomArrayList.get(i).getSchedule_id());
//                                                mListRoom.add(roomVO);
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    frame_living_room.setVisibility(View.VISIBLE);
//                                } else {
                                    frame_living_room.setVisibility(View.VISIBLE);
                                    RoomVO oneRoom = new RoomVO();
                                    oneRoom.setRoomId("0");
                                    if (room.equalsIgnoreCase("Timer")) {
                                        oneRoom.setRoomName("All Timer");
                                    } else {
                                        oneRoom.setRoomName("All Schedule");
                                    }

                                    mListRoom.add(0, oneRoom);

                                    if (room.equalsIgnoreCase("Timer")) {
                                        for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                            if (scheduleRoomArrayList.get(i).getIs_timer() == 1) {
                                                RoomVO roomVO = new RoomVO();
                                                roomVO.setRoomName(scheduleRoomArrayList.get(i).getSchedule_name());
                                                roomVO.setRoomId(scheduleRoomArrayList.get(i).getSchedule_id());
                                                mListRoom.add(roomVO);
                                            }
                                        }
                                    } else {
                                        for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                            if (scheduleRoomArrayList.get(i).getIs_timer() == 0) {
                                                RoomVO roomVO = new RoomVO();
                                                roomVO.setRoomName(scheduleRoomArrayList.get(i).getSchedule_name());
                                                roomVO.setRoomId(scheduleRoomArrayList.get(i).getSchedule_id());
                                                mListRoom.add(roomVO);
                                            }

                                        }

                                    }


//                                }

                                } else {
                                    frame_living_room.setVisibility(View.GONE);
                                    RoomVO oneRoom = new RoomVO();
                                    oneRoom.setRoomId("0");
                                    oneRoom.setRoomName("All Schedule");
                                    mListRoom.add(0, oneRoom);
                                }
                            }else {
                                frame_living_room.setVisibility(View.GONE);
                            }
                        }

                        if (mListRoomTemp.size() > 0) {
                            initRoomAdapter(mListRoomTemp);
                        } else {
                            initRoomAdapter(mListRoom);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initRoomAdapter(final List<RoomVO> roomVOSList) {
//        mSpinnerRoomList.setSelection(-1);
//        mSpinnerRoomList.setAdapter(null);
//        mSpinnerRoomList.cl

        if(roomVOSList.size()<1){

            frame_living_room.setVisibility(View.GONE);
            return;
        }
        if(!TextUtils.isEmpty(isSelectItemSubTemp)){
            isSelectItemSubTemp="";
            isSelectItemSub="";
        }else {
            if (TextUtils.isEmpty(isSelectItemSub)) {
                if(roomVOSList.size()>0){
                    adapterRoom = new ArrayAdapter<RoomVO>(getApplicationContext(),
                            R.layout.item_spinner_selected, roomVOSList);
                    mSpinnerRoomList.setAdapter(adapterRoom);

                    if (ChatApplication.checkActivity(isCheckActivity) != -1) {
                        for (int i = 0; i < roomVOSList.size(); i++) {
                            if (roomVOSList.get(i).getRoomId().equals(mRoomId)) {
                                mSpinnerRoomList.setSelection(i);
                                break;
                            }
                        }
                    }
                }else {
                    frame_living_room.setVisibility(View.GONE);
                }
            }
        }

//        if(adapterRoom==null){
//            adapterRoom = new ArrayAdapter<RoomVO>(getApplicationContext(),
//                    R.layout.item_spinner_selected, roomVOSList);
//            mSpinnerRoomList.setAdapter(adapterRoom);
//
//        }

        if(roomVOSList.size()==0){
            frame_living_room.setVisibility(View.GONE);
        }
//        defaultPanelSpinner("Select Panel");
//        defaultDeviceSpinner("Select Device");

        mSpinnerRoomList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strpanelId="";
                if (position == 0) {
                    panel_view.setVisibility(View.GONE);
                    return;
                }
                if (!actionType.equals("Mood") && !actionType.equals("Schedule")) {
                    if (position != 0 || actionType.equals("Room")) {
                        if (mSpinnerRoomMood.getSelectedItem().equals("Room")) {
                            panel_view.setVisibility(View.VISIBLE);
                        } else {
                            panel_view.setVisibility(View.GONE);
                        }

                        //  RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
                        RoomVO roomVO = roomVOSList.get(position);
                        //initPanelSpinner(mListPanel, roomVO);
                        initPanelSpinner(roomVO);
                    } else {
//                    defaultPanelSpinner("Select Panel");
//                    defaultDeviceSpinner("Select Device");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void defaultPanelSpinner(String all_panel) {

        mListPanel.clear();
        mListPanel.add(0, new PanelVO(all_panel, "0"));
        ArrayAdapter<PanelVO> adapterRoomMood = new ArrayAdapter<PanelVO>(getApplicationContext(),
                R.layout.item_spinner_selected, mListPanel);
        mSpinnerPanelList.setAdapter(adapterRoomMood);
    }

    private void defaultDeviceSpinner(String all_device) {

        //  mListDevice.clear();
        //mListDevice.add(0, new DeviceVO(all_device, "0"));

        ArrayList<String> arrayListDevice = new ArrayList<>();
        arrayListDevice.add("All device");
        ArrayAdapter<String> adapterDevice = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected, arrayListDevice);
        mSpinnerDeviceList.setAdapter(adapterDevice);
    }

    private void initPanelSpinner(RoomVO roomVO) {
        strpanelId = "";
        ArrayList<PanelVO> panelVOSList = new ArrayList<>();
        ArrayList<String> arrayPanelList = new ArrayList<>();
        panelVOSList = roomVO.getPanelList();

//        if (panelVOSList.size() > 0) {
//            if (!panelVOSList.get(0).getPanelName().equals("All Panel")) {
//                panelVOSList.add(0, new PanelVO("All Panel", "0"));
//            }
//        }

        arrayPanelList.add("All Panel");
        for (int i = 0; i < panelVOSList.size(); i++) {
            if(isFilterType){
                if(panelVOSList.get(i).getPanel_type()==2){
                    arrayPanelList.add(panelVOSList.get(i).getPanelName());
                }
            }else {
                if(panelVOSList.get(i).getPanel_type()==0){
                    arrayPanelList.add(panelVOSList.get(i).getPanelName());
                }
            }


        }
        defaultDeviceSpinner("All Device");

        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected, arrayPanelList);
        mSpinnerPanelList.setAdapter(adapterRoomMood);


        final ArrayList<PanelVO> finalPanelVOSList = panelVOSList;
        final ArrayList<PanelVO> finalPanelVOSList1 = panelVOSList;
        mSpinnerPanelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strDeviceId="";

                if (position != 0) {
                    frame_all_devices.setVisibility(View.VISIBLE);
                    strpanelId = finalPanelVOSList1.get(position - 1).getPanelId();
                    //PanelVO panelVO = (PanelVO) mSpinnerPanelList.getSelectedItem();
                    PanelVO panelVO = finalPanelVOSList.get(position - 1);
                    initDeviceAdapter(panelVO);
                } else if (position == 0) {
                    strpanelId="";
                    frame_all_devices.setVisibility(View.INVISIBLE);
                    defaultDeviceSpinner("All Device");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Log.d("System out","panel position is "+mSpinnerPanelList.getSelectedItemPosition());
//        if(mSpinnerPanelList.getSelectedItemPosition()>0){
//            frame_all_devices.setVisibility(View.VISIBLE);
//            strpanelId = finalPanelVOSList1.get(mSpinnerPanelList.getSelectedItemPosition()).getPanelId();
//            PanelVO panelVO = finalPanelVOSList.get(mSpinnerPanelList.getSelectedItemPosition());
//            initDeviceAdapter(panelVO);
//        }
    }

    private void initDeviceAdapter(PanelVO panelVO) {
        strDeviceId = "";
        ArrayList<String> arrayListDeviceList = new ArrayList<>();
        ArrayList<DeviceVO> mListDeviceSub = new ArrayList<>();
        arrayListDeviceTemp.clear();
        //  mListDevice = panelVO.getDeviceList();
        mListDeviceSub = panelVO.getDeviceList();

        // arrayListDeviceTemp = panelVO.getDeviceList();

//        if (mListDevice.size() > 0) {
//            for (int i = 0; i < mListDevice.size(); i++) {
//                if (TextUtils.isEmpty(mListDevice.get(i).getDeviceName())) {
//                    arrayListDeviceTemp.get(i).setDeviceName(mListDevice.get(i).getSensor_name());
//                    arrayListDeviceTemp.get(i).setDeviceId(mListDevice.get(i).getSensor_id());
//                }
//            }
//        }

        arrayListDeviceTemp.add(0, new DeviceVO("All Device", "0"));
        arrayListDeviceList.add("All Device");

        for (int i = 0; i < mListDeviceSub.size(); i++) {
            DeviceVO deviceVO = new DeviceVO();
            if (!TextUtils.isEmpty(mListDeviceSub.get(i).getDeviceName())) {
                arrayListDeviceList.add(mListDeviceSub.get(i).getDeviceName());
                deviceVO.setDeviceName(mListDeviceSub.get(i).getDeviceName());
                deviceVO.setDeviceId(mListDeviceSub.get(i).getRoomDeviceId());
            } else {
                arrayListDeviceList.add(mListDeviceSub.get(i).getSensor_name());
                deviceVO.setDeviceName(mListDeviceSub.get(i).getSensor_name());
                deviceVO.setDeviceId(mListDeviceSub.get(i).getModuleId());
            }
            arrayListDeviceTemp.add(deviceVO);
        }
//        if (arrayListDeviceTemp.size() > 0) {
//            if (!TextUtils.isEmpty(arrayListDeviceTemp.get(0).getDeviceName())) {
//                if (!arrayListDeviceTemp.get(0).getDeviceName().equals("All Device")) {
//
//                }
//            }
//        }

        frame_all_devices.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected, arrayListDeviceList);
        mSpinnerDeviceList.setAdapter(adapterRoomMood);

        mSpinnerDeviceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    strDeviceId = arrayListDeviceTemp.get(position).getDeviceId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;
    int mSecond;

    public static String start_date = "";
    public static String end_date = "";

    /*
     * @param editText   : start_date/end_date textview
     * @param isEndDate  : is end_date textView or not
     * */
    private void datePicker(final CustomEditText editText, final boolean isEndDate) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //YYYY-MM-DD HH:mm:ss

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = year + "-" + ActivityHelper.hmZero(monthOfYear + 1) + "-" + ActivityHelper.hmZero(dayOfMonth);
                        //*************Call Time Picker Here ********************
                        tiemPicker(editText, isEndDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void tiemPicker(final CustomEditText editText, final boolean isEndDate) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        String on_date = date_time + " " + ActivityHelper.hmZero(hourOfDay) + ":" + ActivityHelper.hmZero(minute) + ":" + ActivityHelper.hmZero(mSecond);

                        if (isEndDate) {
                            end_date = on_date;
                        } else {
                            start_date = on_date;
                        }

                        editText.setText("" + changeDateFormat(on_date));

                        if (!TextUtils.isEmpty(edt_start_date.getText().toString()) && !TextUtils.isEmpty(edt_end_date.getText().toString())) {
                            boolean isCompare = compareDate(start_date, end_date);
                            isCompareDateValid = isCompare;
                            if (!isCompare) {
                                editText.setText("");
                                Toast.makeText(getApplicationContext(), "End date is not less than Start Date", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private boolean isCompareDateValid = true;

    /*
     * @param str_date : 2018-03-21 02:24:56
     * @return date    : 21-jan 2018 02:24 PM
     * @call getDate(str_date) String to date format
     * */

    public static String changeDateFormat(String str_date) {
        String date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_2);
        date = format.format(getDate(str_date));
        return date;
    }

    /*
     * YYYY-MM-DD HH:mm:ss
     * @return
     * */
    public static boolean compareDate(String startDate, String endDate) {

        Date start_date = getDate(startDate);
        Date end_date = getDate(endDate);

        return end_date.after(start_date);
    }

    /*
     * convert string format to date format
     * @return date
     * */

    public static Date getDate(String dtStart) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_1);
        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * filterArrayList for find selected type/action filter
     */

    Date startDate = null;
    Date endDate = null;
    boolean isFilterChecked = false;

    List<DeviceLog> tempLogList = new ArrayList<>();

    /**
     * not used (for Local filter)
     */
    private void generateFilterList() {

        //filter by date
        if (!TextUtils.isEmpty(start_date) && !TextUtils.isEmpty(end_date)) {
            isDateFilterActive = true;
            startDate = getDate(start_date);
            endDate = getDate(end_date);
        }

        for (Filter filter : filterArrayList) {
            if (filter.isChecked()) {

                isFilterChecked = true;
                boolean isSubFilterActive = false;
                ArrayList<Filter.SubFilter> subFilters = filter.getSubFilters();

                for (Filter.SubFilter subFilter : subFilters) {
                    if (subFilter.isChecked()) {
                        isSubFilterActive = true;

                        for (DeviceLog deviceLog : deviceLogList) {
                            if (deviceLog.getActivity_type().equalsIgnoreCase(filter.getName())) {

                                //if selected start and end date then filter by with name and date between
                                if (isDateFilterActive) {
                                    Date logTime = getDate(deviceLog.getActivity_time());
                                    if (deviceLog.getActivity_action().equalsIgnoreCase(subFilter.getName()) && logTime.after(startDate) && logTime.before(endDate)) {
                                        tempLogList.add(deviceLog);
                                    }
                                } else {
                                    if (deviceLog.getActivity_action().equalsIgnoreCase(subFilter.getName())) { //action metch with rootList
                                        tempLogList.add(deviceLog);
                                    }
                                }
                            }
                        }
                    }
                }

                /**
                 * if subFilter not found so added all root type filter
                 */
                if (!isSubFilterActive) {
                    for (DeviceLog deviceLog : deviceLogList) {
                        if (deviceLog.getActivity_type().equalsIgnoreCase(filter.getName())) {
                            tempLogList.add(deviceLog);
                        }
                    }
                }
            }
        }

        //if only selected date filter
        boolean isDateFilterFound = false;
        if (!isFilterChecked && isDateFilterActive) {
            for (DeviceLog deviceLog : deviceLogList) {
                Date logTime = getDate(deviceLog.getActivity_time());
                if (logTime.after(startDate) && logTime.before(endDate)) {
                    isDateFilterFound = true;
                    tempLogList.add(deviceLog);
                }
            }
        }

        /*if(!isDateFilterFound && isDateFilterActive){
            Toast.makeText(getApplicationContext(), "You are not between a after and before date.", Toast.LENGTH_SHORT).show();
        }*/


        if (isFilterChecked || isDateFilterActive) {
            if (tempLogList.size() == 0) {

                ll_empty.setVisibility(View.GONE);
                rv_device_log.setVisibility(View.GONE);

                //   Toast.makeText(getApplicationContext(),"No Device Log for Filter Device Type",Toast.LENGTH_LONG).show();

            } else {
                ll_empty.setVisibility(View.GONE);
                rv_device_log.setVisibility(View.VISIBLE);

                //notyfy adapter with temp list
                deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this, tempLogList);
                rv_device_log.setAdapter(deviceLogAdapter);
                deviceLogAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "Filter Not Apply", Toast.LENGTH_SHORT).show();
        }

    }

    public List<DeviceLog> deviceLogList = new ArrayList<>();

    public boolean isEndOfRecord = false;

    public void getDeviceLog(final int position) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        //  ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        String url = "";
//        if (Schedule_id.length() >= 1) {
//            if (isFilterActive) {
        if (isFilterType ) {
            ActivityHelper.showProgressDialog(this, "Please Wait...", false);
            url = ChatApplication.url + Constants.SENSOR_NOTIFICATION;
        } else {
            url = ChatApplication.url + Constants.GET_FILTER_NOTIFICATION_INFO;
        }
//            } else {
//                url = ChatApplication.url + Constants.getScheduleNotification;
//            }
//        } else {
//            if (isFilterActive) {
//                url = ChatApplication.url + Constants.GET_FILTER_NOTIFICATION_INFO;
//            } else {
//                url = ChatApplication.url + Constants.GET_NOTIFICATION_INFO;
//            }
//        }

        JSONObject object = new JSONObject();
        try {
            if (isFilterActive) {
                object.put("notification_number", position);

                // object.put("room_id",TextUtils.isEmpty(mRoomId) ? "" : mRoomId);
                // object.put("schedule_id", TextUtils.isEmpty(Schedule_id) ? "" : Schedule_id);

//                if (!TextUtils.isEmpty(activity_type)) {
//                    object.put("activity_type", activity_type);
//                }

//            if(!TextUtils.isEmpty(mRoomId) && TextUtils.isEmpty(Schedule_id)){

//                if (isCheckActivity.equals("schedule")) {
//                    object.put("is_room", 0);
//                } else {
//                    object.put("is_room", 1);
//                }

                object.put("room_id", "" + mRoomId);
//            }
                object.put("start_datetime", start_date);
                object.put("end_datetime", end_date);

//                if (!(ChatApplication.checkActivity(isCheckActivity) == -1)) {
//                    object.put("is_room", ChatApplication.checkActivity(isCheckActivity));
//                } else {
//                    object.put("is_room", "");
//                }
                //object.put("is_room",end_date);

                //mSpinnerRoomMood is main list
                //mSpinnerRoomList second list

                if (actionType.equals("Mood") || actionType.equals("Room")) {
                    object.put("is_room", 1);
                } else {
                    object.put("is_room", 0);
                }

                RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
//                PanelVO panelVO = (PanelVO) mSpinnerPanelList.getSelectedItem();
//                DeviceVO deviceVO = (DeviceVO) mSpinnerDeviceList.getSelectedItem();

                String moodType = mSpinnerRoomMood.getSelectedItem().toString();
                JSONArray array = new JSONArray();
                if (mSpinnerRoomMood.getSelectedItem().toString().equals("All")) {
                    object.put("room_id", "");
                    object.put("panel_id", "");
                    object.put("module_id", "");
                    object.put("filter_data", "");

                    if (isFilterType) {
                        object.put("sensor_type", "all");

                        String actionname = "";
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        if (filterArrayList.size() > 0) {
                            for (int i = 0; i < filterArrayList.size(); i++) {
                                if (filterArrayList.get(i).isChecked()) {
                                    stringArrayList.add(filterArrayList.get(i).getName());
                                }
                            }

                            for (int i = 0; i < stringArrayList.size(); i++) {
                                if (i == stringArrayList.size() - 1) {
                                    actionname = actionname + "'" + stringArrayList.get(i) + "'";
                                } else {
                                    actionname = actionname + "'" + stringArrayList.get(i) + "',";
                                }
                            }
                            JSONObject subObject = new JSONObject();
                            subObject.put("activity_action", actionname);
                            subObject.put("activity_type", "All");
                            array.put(subObject);
                            object.put("filter_data", array);
                        }

                    } else {
                        String actionname = "";
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        if (filterArrayList.size() > 0) {
                            for (int i = 0; i < filterArrayList.size(); i++) {
                                if (filterArrayList.get(i).isChecked()) {
                                    stringArrayList.add(filterArrayList.get(i).getName());
                                }
                            }

                            for (int i = 0; i < stringArrayList.size(); i++) {
                                if (i == stringArrayList.size() - 1) {
                                    actionname = actionname + "'" + stringArrayList.get(i) + "'";
                                } else {
                                    actionname = actionname + "'" + stringArrayList.get(i) + "',";
                                }
                            }
                            JSONObject subObject = new JSONObject();
                            subObject.put("activity_action", actionname);

                            subObject.put("activity_type", "All");
                            array.put(subObject);
                            object.put("filter_data", array);
                        }

                    }


//                    JSONObject subObject = new JSONObject();
//
//                    subObject.put("activity_action", "");
//                    subObject.put("activity_type", "");

                    //  array.put("");
                } else {

                    // object.put("mode_type",""+ (moodType.equalsIgnoreCase("Room") ? "0" :  "1"));

                    if (isFilterType) {

                        if (actionType.equalsIgnoreCase("Door Sensor")) {
                            object.put("sensor_type", "door");
                        } else {
                            object.put("sensor_type", "temp");
                        }

                        object.put("room_id", "");
                        object.put("panel_id", "");
                        if(roomVO!=null){
                            object.put("module_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));
                        }else {
                            object.put("module_id", "" );
                        }

                    } else {
                        if (roomVO != null) {
                            object.put("room_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));
                        } else if (isCheckActivity.equals("room")) {
                            object.put("room_id", "" + mRoomId);
                        } else {
                            object.put("room_id", "");
                        }

                        if (actionType.equals("Room")) {
                            object.put("module_id", "" + strDeviceId);
                            object.put("panel_id", "" + strpanelId);
                        } else {
                            object.put("panel_id", "");
                            object.put("module_id", "");
                        }
                    }

//                    if (panelVO != null) {
//                        object.put("panel_id", "" + (panelVO.getPanelId().equalsIgnoreCase("0") ? "" : panelVO.getPanelId()));
//                    } else {
//                        object.put("panel_id", "");
//                    }
//
//                    if (deviceVO != null) {
//                        object.put("module_id", "" + (deviceVO.getDeviceId().equalsIgnoreCase("0") ? "" : deviceVO.getModuleId()));
//                    } else {
//                        object.put("module_id", "");
//                    }


                    String actionname = "";
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < filterArrayList.size(); i++) {

                        if (filterArrayList.get(i).isChecked()) {
                            stringArrayList.add(filterArrayList.get(i).getName());
                        }


//                        if (filter.isChecked()) {
//
//
//                            ArrayList<Filter.SubFilter> subFilterList = filter.getSubFilters();
//                            String subAction = "";
//                            for (Filter.SubFilter subFilter : subFilterList) {
//                                if (subFilter.isChecked()) {
//                                    subAction += "," + "'" + subFilter.getName() + "'";
//                                }
//                            }
//                            if (subAction.startsWith(",")) {
//                                subAction = subAction.replaceFirst(",", "");
//                            }
//                            Log.d("Filter", "subString : " + subAction);
//                            actionname=actionname+",";
//
//
//                       /* subObject.put("activity_type",filter.getName());
//                        subObject.put("activity_action",subAction);*/
//
//                        }
                    }

                    for (int i = 0; i < stringArrayList.size(); i++) {
                        if (i == stringArrayList.size() - 1) {
                            actionname = actionname + "'" + stringArrayList.get(i) + "'";
                        } else {
                            actionname = actionname + "'" + stringArrayList.get(i) + "',";
                        }
                    }
                    JSONObject subObject = new JSONObject();
                    subObject.put("activity_action", actionname);
                    subObject.put("activity_type", "" + actionType);

                    array.put(subObject);
                    object.put("filter_data", array);
                }


            } else {
                /*	"notification_number":0,	"start_datetime":"",	"end_datetime":"",
                "filter_data":"",	"room_id":"",	"panel_id":"",	"module_id":"",	"is_room":1}
                * */

                object.put("notification_number", position);
                object.put("start_datetime", "");
                object.put("end_datetime", "");
                object.put("filter_data", "");
                if (TextUtils.isEmpty(mRoomId)) {
                    object.put("room_id", "");
                } else {
                    object.put("room_id", "" + mRoomId);
                }

                object.put("panel_id", "");
                object.put("module_id", "");
//                if (isCheckActivity.equals("AllType") ||isCheckActivity.equals("mode") || isCheckActivity.equals("room")) {
                if (isCheckActivity.equals("mode") || isCheckActivity.equals("room")) {
                    object.put("is_room", 1);
                } else {
                    object.put("is_room", 0);
                }

            }

            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("admin", Common.getPrefValue(this, Constants.USER_ADMIN_TYPE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("object is "+object.toString());
        new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if (position == 0) {
                            deviceLogList.clear();
                        }
                        ll_empty.setVisibility(View.GONE);
                        rv_device_log.setVisibility(View.VISIBLE);

                        JSONObject dataObj = result.getJSONObject("data");
                        ChatApplication.logDisplay("data obj is "+dataObj);

                        if (dataObj.has("devicefilterList")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = dataObj.getJSONArray("devicefilterList");
                            //  ListUtils.filters.clear();
//                            if(!isScrollview){
                            if (!isFilterActive) {
                                if (filterArrayListTemp != null) {
                                    filterArrayListTemp.clear();
                                    filterArrayList.clear();
                                }
                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    String action_name = object.getString("action_name");
//
//                                if (isCheckActivity.equals("mode") && logName.equals("Mood")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("schedule") && logName.equals("Schedule")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("room") && logName.equals("Room")) {
//                                    addValueFilter(action_name,logName);
//                                } else {
//                                    addValueFilter(action_name,logName);
                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {
                                        subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                    }
//                                    filterArrayList.add(new Filter(logName, false, false, subFilterArrayList));
                                    filterArrayListTemp.add(new Filter(logName, false, false, subFilterArrayList));

//                                }
                                }
                            }

//                            }


                        }

                        if (dataObj.has("sensorfilterList")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = dataObj.getJSONArray("sensorfilterList");

                            if (!isFilterActive) {
                                if (filterArrayListSensorTemp != null) {
                                    filterArraySensor.clear();
                                    filterArrayListSensorTemp.clear();
                                }

                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    String action_name = object.getString("action_name");
//
//                                if (isCheckActivity.equals("mode") && logName.equals("Mood")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("schedule") && logName.equals("Schedule")) {
//                                    addValueFilter(action_name,logName);
//                                } else if (isCheckActivity.equals("room") && logName.equals("Room")) {
//                                    addValueFilter(action_name,logName);
//                                } else {
//                                    addValueFilter(action_name,logName);
                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {
                                        subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                    }
//                                    filterArraySensor.add(new Filter(logName, false, false, subFilterArrayList));
                                    filterArrayListSensorTemp.add(new Filter(logName, false, false, subFilterArrayList));

                                }
//                                }
                            }


                        }

                        JSONArray notificationArray = dataObj.getJSONArray("notificationList");

                        if (notificationArray.length() == 0) {
                            isScrollDown = true;
                        } else {
                            isScrollDown = false;
                        }
                        for (int i = 0; i < notificationArray.length(); i++) {
                            JSONObject jsonObject = notificationArray.getJSONObject(i);
                            String activity_action = jsonObject.getString("activity_action");
                            String activity_type = jsonObject.getString("activity_type");
                            String activity_description = jsonObject.getString("activity_description");
                            String activity_time = jsonObject.getString("activity_time");
                            String user_name = jsonObject.getString("user_name");
                            String is_unread = jsonObject.optString("is_unread");
                            //  String activity_state = jsonObject.getString("activity_state");

                            //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state

                            deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "",user_name,is_unread));
                        }

                        if(position==0){
                            if(!TextUtils.isEmpty(notificationArray.toString())){
                                if(notificationArray.toString().equals("[]")){
                                    showAToast("No Record Found...");
                                }
                            }
                        }

                        if (notificationArray.length() == 0 && isFilterActive && isLoading) {

                            if (!isEndOfRecord) {
                                deviceLogList.add(new DeviceLog("End of Record", "End of Record", "", "", "","",""));
                                deviceLogAdapter.setEOR(true);
                            }
                            isEndOfRecord = true;

                            //showAToast("End of Record...");
                        } else if (notificationArray.length() == 0 && isFilterActive) {
                            deviceLogList.add(new DeviceLog("End of Record", "No Record Found", "", "", "","",""));
                            deviceLogAdapter.setEOR(true);
                            showAToast("No Record Found...");
                        }
                        deviceLogAdapter.notifyDataSetChanged();

                        ActivityHelper.dismissProgressDialog();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private Toast mToast;

    public void showAToast(String st) { //"Toast toast" is declared in the class
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onRefresh() {
        if (!isFilterActive) {
            isScrollDown = false;
            deviceLogList.clear();
            isFilterType = false;
            deviceLogAdapter.notifyDataSetChanged();
//            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {

                getSensorLog(0);
            } else {
                getDeviceLog(0);
            }
            swipeRefreshLayout.setRefreshing(true);
        } else {
            // swipeRefreshLayout.setRefreshing(false);

            dialog=null;
            isFilterActive = false;
            isFilterType = false;
            isEndOfRecord = false;
            isSelectItemSubTemp="";
            isSelectItem="";
            isSelectItemSub="";
            invalidateOptionsMenu();
            deviceLogList.clear();
            isDateFilterActive = false;
            deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this, deviceLogList);
            rv_device_log.setAdapter(deviceLogAdapter);
            deviceLogAdapter.notifyDataSetChanged();
            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")|| isCheckActivity.equals("multisensor")) {
                getSensorLog(0);
            } else {
                //   udpateButton();
                if (isCheckActivity.equals("AllType")) {
                    isFilterType = false;
                    isFilterActive = false;
                }
                getDeviceLog(0);
            }
        }

    }

    @Override
    public void filterAllMark(ArrayList<Filter> filters) {

        if (filters.size() > 0) {
            boolean isFlag = false;
            int count = 0;
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i).isChecked()) {
                    isFlag = true;
                    count++;
                }
            }
            if (!isFlag) {
                isMarkAll = false;
                checkBoxMark.setChecked(isMarkAll);
            } else if (count == filters.size()) {
                isMarkAll = true;
                checkBoxMark.setChecked(isMarkAll);
            } else {
                isMarkAll = false;
                checkBoxMark.setChecked(isMarkAll);
            }
        }
    }


    public void unreadApiCall(final boolean b) {

        String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();

            JSONObject object = new JSONObject();
            object.put("sensor_type","temp");
            object.put("module_id", ""+Mood_Id);
            object.put("room_id", ""+mRoomId);
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

                        if(b){
                            isMultiSensor=false;
                            getSensorLog(0);
                        }else {
                            DeviceLogActivity.this.finish();
                        }

                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        if(b){
                            getSensorLog(0);
                        }else {
                            DeviceLogActivity.this.finish();
                        }
                    }
                }).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


}
