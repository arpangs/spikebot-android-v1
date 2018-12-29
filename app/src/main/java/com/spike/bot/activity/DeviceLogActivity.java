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
import android.util.Log;
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
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.listener.OnLoadMoreListener;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.Filter;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

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

public class DeviceLogActivity extends AppCompatActivity implements  OnLoadMoreListener ,SwipeRefreshLayout.OnRefreshListener {
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

    public boolean isFilterActive = false,isSensorLog=false,isDateFilterActive = false,isScrollDown=false;

    private String mRoomId,Schedule_id="",activity_type="",tabSelect="",Mood_Id="",actionType="";
    private Button btnDevice;
    private Button btnSensor;

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

        String userFname = Common.getPrefValue(ChatApplication.getInstance(),"first_name");
        String userLname = Common.getPrefValue(ChatApplication.getInstance(),"last_name");

      //  setTitle(""+userFname + " " + userLname);
        setTitle("LOGS");


        mRoomId = getIntent().getStringExtra("ROOM_ID");
        Schedule_id = getIntent().getStringExtra("Schedule_id");
        activity_type = getIntent().getStringExtra("activity_type");
        isSensorLog = getIntent().getBooleanExtra("IS_SENSOR",false);
        tabSelect = getIntent().getStringExtra("tabSelect");
        Mood_Id = getIntent().getStringExtra("Mood_Id");

        if(TextUtils.isEmpty(Schedule_id)){
            Schedule_id="";
        }
        if(TextUtils.isEmpty(tabSelect)){
            tabSelect="";
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rv_device_log.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        onLoadMoreListener = this;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv_device_log.getLayoutManager();

        deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this,deviceLogList);
        rv_device_log.setAdapter(deviceLogAdapter);

        rv_device_log.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mScrollState = newState;

                if(newState == SCROLL_STATE_IDLE){

                    totalItemCount  = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if(deviceLogList.size()!=0 && mScrollState == SCROLL_STATE_IDLE){
                        if((deviceLogList.size()-1) == lastVisibleItem){
                            if (onLoadMoreListener != null && !isScrollDown) {
                                isLoading = true;
                                isScrollDown = true;
                                onLoadMoreListener.onLoadMore(deviceLogList.size());
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

        if(tabSelect.equalsIgnoreCase("hide")){
            cardViewBtn.setVisibility(View.GONE);
        }else {
            cardViewBtn.setVisibility(View.VISIBLE);
        }

        udpateButton();

        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = false;
                isScrollDown=false;
                deviceLogList.clear();
                udpateButton();
                getDeviceLog(0);
            }
        });

        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensorLog = true;
                isScrollDown=false;
                udpateButton();
                getSensorLog(0);
            }
        });

        //added by sagar
        if(isSensorLog){
            getSensorLog(0);
        }else {
            getDeviceLog(0);
        }

    }

    private void udpateButton(){

        if(isSensorLog){
            btnDevice.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensor.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));

        }else{
            btnDevice.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensor.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
        }

        btnDevice.setTextColor(getResources().getColor(R.color.automation_white));
        btnSensor.setTextColor(getResources().getColor(R.color.automation_white));
    }

    private void udpateDailogButton(Button btnDeviceDialog, Button btnSensorDialog){

        if(isSensorLog){
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));

        }else{
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
        }

        btnDeviceDialog.setTextColor(getResources().getColor(R.color.automation_white));
        btnSensorDialog.setTextColor(getResources().getColor(R.color.automation_white));
    }

    //get sensor log when click on sensor button
    private void getSensorLog(int lastVisibleItem){
        if(lastVisibleItem==0){
            deviceLogList.clear();
        }
        if(deviceLogAdapter!=null){
            deviceLogAdapter.notifyDataSetChanged();
        }

        applyFilter(lastVisibleItem,true);
    }

    private void applyFilter(int position, boolean isSensorIntent){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String webUrl = ChatApplication.url + Constants.SENSOR_NOTIFICATION;

        JSONObject jsonNotification = new JSONObject();

        String sensor_type = "";

        sensor_type = "";


        try {
            jsonNotification.put("notification_number",position);

            jsonNotification.put("module_id",TextUtils.isEmpty(Mood_Id) ? "" : Mood_Id);
            jsonNotification.put("room_id",TextUtils.isEmpty(mRoomId) ? "" : mRoomId);
            jsonNotification.put("start_date","");
            jsonNotification.put("end_date","");
            jsonNotification.put("sensor_type",sensor_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("SensorLog","json Request : " + jsonNotification.toString());

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.d("Filter","filterPos Success: ");
                  swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "getDeviceLog onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        ll_empty.setVisibility(View.GONE);
                        rv_device_log.setVisibility(View.VISIBLE);

                        JSONObject dataObj = result.getJSONObject("data");

                        if(dataObj.has("filterList")){
                            filterArrayList.clear();
                            JSONArray logJsonArray = dataObj.getJSONArray("filterList");
                            //  ListUtils.filters.clear();

                            for(int i=0; i<logJsonArray.length();i++){
                                JSONObject object = logJsonArray.getJSONObject(i);

                                String logName = object.getString("filter_name");
                                String action_name = object.getString("action_name");

                                ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                for(String subLog : action_name.split(",")){
                                    subFilterArrayList.add(new Filter.SubFilter(subLog,false));
                                }
                                filterArrayList.add(new Filter(logName,false,false,subFilterArrayList));
                                filterArrayListTemp.add(new Filter(logName,false,false,subFilterArrayList));
                            }
                        }

                        JSONArray notificationArray = dataObj.getJSONArray("notificationList");

                        for(int i=0; i <notificationArray.length() ; i++){
                            JSONObject jsonObject = notificationArray.getJSONObject(i);
                            String activity_action = jsonObject.getString("activity_action");
                            String activity_type = jsonObject.getString("activity_type");
                            String activity_description = jsonObject.getString("activity_description");
                            String activity_time = jsonObject.getString("activity_time");
                            //  String activity_state = jsonObject.getString("activity_state");

                            //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state

                            deviceLogList.add(new DeviceLog(activity_action,activity_type,activity_description,activity_time,""));
                        }

                        if(notificationArray.length()==0){
                           isScrollDown=true;
                        }else {
                            isScrollDown=false;
                        }

                        if(notificationArray.length()==0 && isFilterActive && isLoading){

                            if(!isEndOfRecord){
                                deviceLogList.add(new DeviceLog("End of Record","End of Record","","",""));
                                deviceLogAdapter.setEOR(true);
                            }
                            isEndOfRecord = true;

                            showAToast("End of Record...");
                        }else if(notificationArray.length() ==0 && isFilterActive){
                            deviceLogList.add(new DeviceLog("End of Record","No Record Found","","",""));
                            deviceLogAdapter.setEOR(true);
                            showAToast("No Record Found...");
                        }
                        deviceLogAdapter.notifyDataSetChanged();

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
        Log.d("Filter","lastVisibleItem : " + lastVisibleItem);
        /*if(!isEndOfRecord){
            getDeviceLog(lastVisibleItem);
        }else{
            showAToast("End of Record...");
        }*/

        if(isSensorLog){
            getSensorLog(lastVisibleItem);
        }else {
            getDeviceLog(lastVisibleItem);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        //clear the start and end date EditText field
        ListUtils.start_date_filter = "";
        ListUtils.end_date_filter = "";

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        MenuItem menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setVisible(false);
        menuItem.setVisible(true);
        if(isFilterActive){
            menuItemReset.setVisible(true);
            menuItem.setIcon(R.drawable.icn_filter_on);
        }else{
            menuItemReset.setVisible(false);
            menuItem.setIcon(R.drawable.icn_filter);
        }

        if(tabSelect.equalsIgnoreCase("hide")){
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
            isMarkAll=true;
            openDialogFilter();
            return true;
        }else if(id == R.id.action_reset){

            isFilterActive = false;
            isEndOfRecord = false;

            invalidateOptionsMenu();
            deviceLogList.clear();
            isDateFilterActive = false;
            deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this,deviceLogList);
            rv_device_log.setAdapter(deviceLogAdapter);
            deviceLogAdapter.notifyDataSetChanged();
            getDeviceLog(0);
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Filter> filterArrayList = new ArrayList<>();
    private ArrayList<Filter> filterArrayListTemp = new ArrayList<>();

    CustomEditText edt_start_date;
    CustomEditText edt_end_date;

    public boolean isMarkAll = true;

    private Spinner mSpinnerRoomMood;
    private Spinner mSpinnerRoomList;
    private Spinner mSpinnerPanelList;
    private Spinner mSpinnerDeviceList;
    public LinearLayout panel_view;
    public FrameLayout frame_living_room;
    public Button btnDeviceDialog,btnSensorDialog;

    private void openDialogFilter(){

        final Dialog dialog = new Dialog(this,R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);

        RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.root_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));


        //filterArrayList = ListUtils.getFilterList();

        final FilterRootAdapter filterRootAdapter = new FilterRootAdapter(filterArrayList);
        recyclerView.setAdapter(filterRootAdapter);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel_filter);
        Button btnSave = dialog.findViewById(R.id.btn_save_filter);
        Button btnReset = dialog.findViewById(R.id.btn_cancel_reset);

        final CheckBox checkBox = dialog.findViewById(R.id.chk_all);
        final TextView title_check_all = dialog.findViewById(R.id.title_check_all);

        mSpinnerRoomMood = (Spinner) dialog.findViewById(R.id.spinner_room_mood);
        mSpinnerRoomList = (Spinner) dialog.findViewById(R.id.spinner_room_list);
        mSpinnerPanelList = (Spinner) dialog.findViewById(R.id.spinner_panel_list);
        mSpinnerDeviceList = (Spinner) dialog.findViewById(R.id.spinner_device_list);
        panel_view = (LinearLayout) dialog.findViewById(R.id.panel_view);
        frame_living_room = (FrameLayout) dialog.findViewById(R.id.frame_living_room);
        btnSensorDialog = (Button) dialog.findViewById(R.id.btnSensorDialog);
        btnDeviceDialog = (Button) dialog.findViewById(R.id.btnDeviceDialog);

        initSpinnerData(filterRootAdapter);

        checkBox.setChecked(isMarkAll);
//        if(isMarkAll){
//            title_check_all.setText(R.string.umnark_all);
//        }else{
//            title_check_all.setText(R.string.mark_all);
//        }

        udpateDailogButton(btnDeviceDialog,btnSensorDialog);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isMarkAll = isChecked;
//                if(isChecked){
//                    title_check_all.setText(R.string.umnark_all);
//                }else{
//                    title_check_all.setText(R.string.mark_all);
//                }

                for(Filter filter : filterArrayList){
                    filter.setChecked(isChecked);
                    for(Filter.SubFilter subFilter : filter.getSubFilters()){
                        subFilter.setChecked(isChecked);
                    }
                }
                filterRootAdapter.notifyDataSetChanged();
            }
        });

        edt_start_date = (CustomEditText) dialog.findViewById(R.id.edt_log_start_date);
        edt_end_date = (CustomEditText) dialog.findViewById(R.id.edt_log_end_date);

        edt_start_date.setText(ListUtils.start_date_filter);
        edt_end_date.setText(ListUtils.end_date_filter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isCompareDateValid){
                    Toast.makeText(getApplicationContext(),"End date is not less than Start Date",Toast.LENGTH_SHORT).show();
                    return;
                }

                ListUtils.start_date_filter = edt_start_date.getText().toString();
                ListUtils.end_date_filter   = edt_end_date.getText().toString();

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

                edt_start_date.setText("");
                edt_end_date.setText("");

                ListUtils.start_date_filter = "";
                ListUtils.end_date_filter = "";

                edt_start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                edt_end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                checkBox.setChecked(true);
                isMarkAll = true;

                isDateFilterActive = false;

                isEndOfRecord = false;

                start_date = "";
                end_date = "";

                for(Filter filter : filterArrayList){
                    filter.setExpanded(false);
                    filter.setChecked(true);
                    for(Filter.SubFilter subFilter : filter.getSubFilters()){
                        subFilter.setChecked(false);
                    }
                }
                filterRootAdapter.notifyDataSetChanged();
            }
        });

        //2018-04-09 18:05:13
        //2018-04-10 18:51:23

        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(edt_start_date,false);
            }
        });
        edt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(edt_end_date,true);
            }
        });

        //start date
        if(TextUtils.isEmpty(edt_start_date.getText().toString())){
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
        if(TextUtils.isEmpty(edt_end_date.getText().toString())){
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
        //end

        if(!dialog.isShowing()){
            dialog.show();
        }

    }

    private List<String> mListRoomMood;
    private List<RoomVO> mListRoom;
    private List<PanelVO> mListPanel;
    private List<DeviceVO> mListDevice;

    //fill spinner adapter
    private void initSpinnerData(final FilterRootAdapter filterRootAdapter){

        mListRoomMood = new ArrayList<>();
        mListRoom = new ArrayList<>();
        mListPanel = new ArrayList<>();
        mListDevice = new ArrayList<>();

        for(int i=0; i<filterArrayListTemp.size(); i++){
            mListRoomMood.add(filterArrayListTemp.get(i).getName());
        }

//        mListRoomMood.add("Room");
//        mListRoomMood.add("Mood");

        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected,mListRoomMood);
        mSpinnerRoomMood.setAdapter(adapterRoomMood);

        mSpinnerRoomMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mListRoomMood.get(position).equalsIgnoreCase("All")){
                    frame_living_room.setVisibility(View.GONE);
                    panel_view.setVisibility(View.GONE);
                    actionType="";
                } else if(mListRoomMood.get(position).equalsIgnoreCase("Room")){
                    panel_view.setVisibility(View.VISIBLE);
                    frame_living_room.setVisibility(View.VISIBLE);
                    getDeviceList("Room");
                }else {
                    panel_view.setVisibility(View.GONE);
                    if(mListRoomMood.get(position).equalsIgnoreCase("Mood")){
                        frame_living_room.setVisibility(View.VISIBLE);
                        getDeviceList("Mood");
                    }else {
                        frame_living_room.setVisibility(View.GONE);
                    }
                }

              //  List<String> myList = new ArrayList<String>(Arrays.asList(filterArrayListTemp.get(position).getSubFilters().get(0).getName().split(",")));

//                ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
//                for(String subLog : action_name.split(",")){
//                    subFilterArrayList.add(new Filter.SubFilter(subLog,false));
//                }

                filterArrayList.clear();
                for(int i=0; i<filterArrayListTemp.get(position).getSubFilters().size(); i++){
                    Filter filter=new Filter();
                    filter.setName(filterArrayListTemp.get(position).getSubFilters().get(i).getName());
                    filter.setChecked(true);
                    filterArrayList.add(filter);
                }

                filterRootAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getDeviceList("");

    }

    private JSONArray panelArray;
    private JSONArray deviceArray;

    private void getDeviceList(final String room){
        String url ="";
        actionType=room;
        if(room.equalsIgnoreCase("Room")){
           url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/"+Constants.DEVICE_TOKEN + "/0/1";
        }else {
            url=ChatApplication.url + Constants.GET_DEVICES_LIST + "/"+Constants.DEVICE_TOKEN + "/1/0";
        }

        new GetJsonTask2(activity, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        JSONObject dataObject = result.getJSONObject("data");
                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");


                        mListRoom = JsonHelper.parseRoomArray(roomArray,false);
                        RoomVO oneRoom = new RoomVO();
                        oneRoom.setRoomId("0");
                        if(room.equalsIgnoreCase("Room")){
                            oneRoom.setRoomName("All Room");
                        }else if(room.equalsIgnoreCase("Mood")){
                            oneRoom.setRoomName("All Mood");
                        }else {
                            frame_living_room.setVisibility(View.GONE);
                        }


                        mListRoom.add(0,oneRoom);

                        initRoomAdapter(mListRoom);

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

    private void initRoomAdapter(List<RoomVO> roomVOSList){

        ArrayAdapter<RoomVO> adapterRoom = new ArrayAdapter<RoomVO>(getApplicationContext(),
                R.layout.item_spinner_selected,roomVOSList);
        mSpinnerRoomList.setAdapter(adapterRoom);

        defaultPanelSpinner("Select Panel");
        defaultDeviceSpinner("Select Device");

        mSpinnerRoomList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
                    initPanelSpinner(mListPanel,roomVO);
                }else{
                    defaultPanelSpinner("Select Panel");
                    defaultDeviceSpinner("Select Device");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void defaultPanelSpinner(String all_panel){

        mListPanel.clear();
        mListPanel.add(0,new PanelVO(all_panel,"0"));
        ArrayAdapter<PanelVO> adapterRoomMood = new ArrayAdapter<PanelVO>(getApplicationContext(),
                R.layout.item_spinner_selected,mListPanel);
        mSpinnerPanelList.setAdapter(adapterRoomMood);
    }

    private void defaultDeviceSpinner(String all_device){

        mListDevice.clear();
        mListDevice.add(0,new DeviceVO(all_device,"0"));
        ArrayAdapter<DeviceVO> adapterDevice = new ArrayAdapter<DeviceVO>(getApplicationContext(),
                R.layout.item_spinner_selected,mListDevice);
        mSpinnerDeviceList.setAdapter(adapterDevice);
    }

    private void initPanelSpinner(List<PanelVO> panelVOSList, RoomVO roomVO){

        panelVOSList.clear();
        panelVOSList = roomVO.getPanelList();

        panelVOSList.add(0,new PanelVO("All Panel","0"));
        ArrayAdapter<PanelVO> adapterRoomMood = new ArrayAdapter<PanelVO>(getApplicationContext(),
                R.layout.item_spinner_selected,panelVOSList);
        mSpinnerPanelList.setAdapter(adapterRoomMood);

        mSpinnerPanelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    PanelVO panelVO = (PanelVO) mSpinnerPanelList.getSelectedItem();
                    initDeviceAdapter(panelVO);
                }else{
                    defaultDeviceSpinner("All Device");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initDeviceAdapter(PanelVO panelVO){

        mListDevice = panelVO.getDeviceList();
        mListDevice.add(0,new DeviceVO("All Device","0"));

        ArrayAdapter<DeviceVO> adapterRoomMood = new ArrayAdapter<DeviceVO>(getApplicationContext(),
                R.layout.item_spinner_selected,mListDevice);
        mSpinnerDeviceList.setAdapter(adapterRoomMood);
    }


    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;
    int mSecond;

    public static String start_date ="";
    public static String end_date ="";

    /*
   * @param editText   : start_date/end_date textview
   * @param isEndDate  : is end_date textView or not
   * */
    private void datePicker(final CustomEditText editText, final boolean isEndDate){

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
                        tiemPicker(editText,isEndDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void tiemPicker(final CustomEditText editText, final boolean isEndDate){
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

                        String on_date = date_time+" "+ ActivityHelper.hmZero(hourOfDay) + ":" + ActivityHelper.hmZero(minute) +":" + ActivityHelper.hmZero(mSecond);

                        if(isEndDate){
                            end_date = on_date;
                        }else{
                            start_date = on_date;
                        }

                        Log.d("Date_format",""+on_date + " >>> " + changeDateFormat(on_date));
                        editText.setText(""+changeDateFormat(on_date));

                        if(!TextUtils.isEmpty(edt_start_date.getText().toString()) && !TextUtils.isEmpty(edt_end_date.getText().toString())){
                            boolean isCompare = compareDate(start_date,end_date);
                            isCompareDateValid = isCompare;
                            if(!isCompare){
                                editText.setText("");
                                Toast.makeText(getApplicationContext(),"End date is not less than Start Date",Toast.LENGTH_SHORT).show();
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

    public static String changeDateFormat(String str_date){
        String date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_2);
        date = format.format(getDate(str_date));
        return date;
    }
    /*
      * YYYY-MM-DD HH:mm:ss
      * @return
      * */
    public static boolean compareDate(String startDate, String endDate){

        Date start_date = getDate(startDate) ;
        Date end_date  = getDate(endDate);

        return end_date.after(start_date);
    }

        /*
    * convert string format to date format
    * @return date
    * */

    public static Date getDate(String dtStart){
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
    private void generateFilterList(){

        //filter by date
        Log.d("Filter","start_date : " + start_date);
        if(!TextUtils.isEmpty(start_date) && !TextUtils.isEmpty(end_date)){
            isDateFilterActive = true;
            startDate = getDate(start_date);
            endDate   = getDate(end_date);
        }

        for(Filter filter : filterArrayList){
            if(filter.isChecked()){

                isFilterChecked = true;
                boolean isSubFilterActive = false;
                ArrayList<Filter.SubFilter> subFilters = filter.getSubFilters();

                for(Filter.SubFilter subFilter : subFilters){
                    if(subFilter.isChecked()){
                        isSubFilterActive = true;

                        for(DeviceLog deviceLog : deviceLogList){
                            if(deviceLog.getActivity_type().equalsIgnoreCase(filter.getName())){

                                //if selected start and end date then filter by with name and date between
                                if(isDateFilterActive){
                                    Date logTime = getDate(deviceLog.getActivity_time());
                                    if(deviceLog.getActivity_action().equalsIgnoreCase(subFilter.getName()) && logTime.after(startDate) && logTime.before(endDate)){
                                        tempLogList.add(deviceLog);
                                    }
                                }else{
                                    if(deviceLog.getActivity_action().equalsIgnoreCase(subFilter.getName())){ //action metch with rootList
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
                if(!isSubFilterActive){
                    for(DeviceLog deviceLog : deviceLogList){
                        if(deviceLog.getActivity_type().equalsIgnoreCase(filter.getName())){
                            tempLogList.add(deviceLog);
                        }
                    }
                }
            }
        }

        //if only selected date filter
        boolean isDateFilterFound = false;
        if(!isFilterChecked && isDateFilterActive){
            for(DeviceLog deviceLog : deviceLogList){
                Date logTime = getDate(deviceLog.getActivity_time());
                if(logTime.after(startDate) && logTime.before(endDate)){
                    isDateFilterFound = true;
                    tempLogList.add(deviceLog);
                }
            }
        }

        /*if(!isDateFilterFound && isDateFilterActive){
            Toast.makeText(getApplicationContext(), "You are not between a after and before date.", Toast.LENGTH_SHORT).show();
        }*/


        if(isFilterChecked || isDateFilterActive){
            if(tempLogList.size()==0){

                ll_empty.setVisibility(View.GONE);
                rv_device_log.setVisibility(View.GONE);

                //   Toast.makeText(getApplicationContext(),"No Device Log for Filter Device Type",Toast.LENGTH_LONG).show();

            }else{
                ll_empty.setVisibility(View.GONE);
                rv_device_log.setVisibility(View.VISIBLE);

                //notyfy adapter with temp list
                deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this,tempLogList);
                rv_device_log.setAdapter(deviceLogAdapter);
                deviceLogAdapter.notifyDataSetChanged();
            }
        }else{
            Toast.makeText(this, "Filter Not Apply", Toast.LENGTH_SHORT).show();
        }

    }

    public List<DeviceLog> deviceLogList = new ArrayList<>();

    public boolean isEndOfRecord = false;

    public void getDeviceLog(final int position){

        Log.d("Filter","filterPos : " + position + " size : " + deviceLogList.size());

        Log.d(TAG, "getDeviceLog getDeviceLog");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        String url = "";
        if(Schedule_id.length()>=1){
            if (isFilterActive) {
                url = ChatApplication.url + Constants.GET_FILTER_NOTIFICATION_INFO;
            }else {
                url = ChatApplication.url + Constants.getScheduleNotification;
            }
        }else {
            if (isFilterActive) {
                url = ChatApplication.url + Constants.GET_FILTER_NOTIFICATION_INFO;
            } else {
                url = ChatApplication.url + Constants.GET_NOTIFICATION_INFO;
            }
        }

        JSONObject object = new JSONObject();
        try {
            object.put("notification_number",position);

           // object.put("room_id",TextUtils.isEmpty(mRoomId) ? "" : mRoomId);
            object.put("schedule_id",TextUtils.isEmpty(Schedule_id) ? "" : Schedule_id);

            if(!TextUtils.isEmpty(activity_type)){
                object.put("activity_type",activity_type);
            }

//            if(!TextUtils.isEmpty(mRoomId) && TextUtils.isEmpty(Schedule_id)){
                object.put("room_id",""+mRoomId);
//            }

            if(isFilterActive){
                object.put("start_datetime",start_date);
                object.put("end_datetime",end_date);

                RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
                PanelVO panelVO = (PanelVO) mSpinnerPanelList.getSelectedItem();
                DeviceVO deviceVO = (DeviceVO) mSpinnerDeviceList.getSelectedItem();

                String moodType = mSpinnerRoomMood.getSelectedItem().toString();
                JSONArray array = new JSONArray();
                if(mSpinnerRoomMood.getSelectedItemPosition()==0){
                    object.put("room_id", "" );
                    object.put("panel_id", "");
                    object.put("module_id", "");

                    JSONObject subObject = new JSONObject();

                    subObject.put("activity_action", "");
                    subObject.put("activity_type", "");

                    array.put(subObject);
                }else {

                    // object.put("mode_type",""+ (moodType.equalsIgnoreCase("Room") ? "0" :  "1"));

                    object.put("room_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));

                    object.put("panel_id", "" + (panelVO.getPanelId().equalsIgnoreCase("0") ? "" : panelVO.getPanelId()));

                    object.put("module_id", "" + (deviceVO.getDeviceId().equalsIgnoreCase("0") ? "" : deviceVO.getModuleId()));

                    String actionname="";
                    for (int i=0; i<filterArrayList.size(); i++) {

                        if(i==filterArrayList.size()-1){
                            actionname=actionname+"'"+filterArrayList.get(i).getName()+"'";
                        }else {
                            actionname=actionname+"'"+filterArrayList.get(i).getName()+"',";
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

                    JSONObject subObject = new JSONObject();
                    subObject.put("activity_action", actionname);
                    subObject.put("activity_type", "" + actionType);

                    array.put(subObject);
                }
                object.put("filter_data",array);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Filter","json : " + object.toString());


        new GetJsonTask(activity,url ,"POST",object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("Filter","filterPos Success: ");
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "getDeviceLog onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        ll_empty.setVisibility(View.GONE);
                        rv_device_log.setVisibility(View.VISIBLE);

                        JSONObject dataObj = result.getJSONObject("data");

                        if(dataObj.has("filterList")){
                            filterArrayList.clear();
                            isScrollDown=false;
                            JSONArray logJsonArray = dataObj.getJSONArray("filterList");
                          //  ListUtils.filters.clear();

                            for(int i=0; i<logJsonArray.length();i++){
                                JSONObject object = logJsonArray.getJSONObject(i);

                                String logName = object.getString("filter_name");
                                String action_name = object.getString("action_name");

                                ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                for(String subLog : action_name.split(",")){
                                    subFilterArrayList.add(new Filter.SubFilter(subLog,false));
                                }
                                filterArrayList.add(new Filter(logName,false,false,subFilterArrayList));
                                filterArrayListTemp.add(new Filter(logName,false,false,subFilterArrayList));
                            }
                        }

                        JSONArray notificationArray = dataObj.getJSONArray("notificationList");

                        if(notificationArray.length()==0){
                            isScrollDown=true;
                        }else {
                            isScrollDown=false;
                        }
                        for(int i=0; i <notificationArray.length() ; i++){
                            JSONObject jsonObject = notificationArray.getJSONObject(i);
                            String activity_action = jsonObject.getString("activity_action");
                            String activity_type = jsonObject.getString("activity_type");
                            String activity_description = jsonObject.getString("activity_description");
                            String activity_time = jsonObject.getString("activity_time");
                          //  String activity_state = jsonObject.getString("activity_state");

                            //String activity_action, String activity_type, String activity_description, String activity_time, String activity_state

                            deviceLogList.add(new DeviceLog(activity_action,activity_type,activity_description,activity_time,""));
                        }

                        if(notificationArray.length()==0 && isFilterActive && isLoading){

                            if(!isEndOfRecord){
                                deviceLogList.add(new DeviceLog("End of Record","End of Record","","",""));
                                deviceLogAdapter.setEOR(true);
                            }
                            isEndOfRecord = true;

                            showAToast("End of Record...");
                        }else if(notificationArray.length() ==0 && isFilterActive){
                            deviceLogList.add(new DeviceLog("End of Record","No Record Found","","",""));
                            deviceLogAdapter.setEOR(true);
                            showAToast("No Record Found...");
                        }
                        deviceLogAdapter.notifyDataSetChanged();

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
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "getDeviceLog onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    private Toast mToast;

    public void showAToast (String st){ //"Toast toast" is declared in the class
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onRefresh() {
        isScrollDown=false;
       if(isSensorLog){
           udpateButton();
           getSensorLog(0);
       }else {
           deviceLogList.clear();
           udpateButton();
           getDeviceLog(0);
       }
        swipeRefreshLayout.setRefreshing(true);
    }
}
