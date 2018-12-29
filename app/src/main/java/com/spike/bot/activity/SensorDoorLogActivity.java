package com.spike.bot.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.RoomListArrayAdapter;
import com.spike.bot.adapter.SensorLogNotificationAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.listener.OnLoadMoreListener;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.SensorLogNotificationRes;
import com.spike.bot.model.SensorLogRes;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

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

/**
 * Created by Sagar on 15/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorDoorLogActivity extends AppCompatActivity implements  OnLoadMoreListener {

    private SensorLogRes mSensorLogRes;
    private Spinner mSpinnerRoomList;
    private ImageView sp_drop_down;

    private RadioGroup rg_sensor_type;
    private RadioButton rb_door,rb_temp;

    private RecyclerView sensor_list;
    private SensorLogNotificationAdapter sensorLogNotificationAdapter;

    private TextView txt_empty_notification;
    CustomEditText edt_start_date;
    CustomEditText edt_end_date;
    private View view_bottom_line;

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading = false;
    private int visibleThreshold = 5;

    private String mSensorName;
    private String mSensorType;
    private boolean isSensorIntent,is_global;

    private OnLoadMoreListener onLoadMoreListener;
    public List<SensorLogNotificationRes.Data.NotificationList> notificationListList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_door_log);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Sensor Log");

        mSensorName = getIntent().getStringExtra("sensor_name");
        mSensorType = getIntent().getStringExtra("sensor_type");
        isSensorIntent = getIntent().getBooleanExtra("is_sensor",false);
        is_global = getIntent().getBooleanExtra("is_global",false);


        if(is_global){
            mSensorName = "All";
            mSensorType = "door";
            isSensorIntent = true;
        }

        bindView();

        onLoadMoreListener = this;

        getSensorRoomList();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) sensor_list.getLayoutManager();
        sensor_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mScrollState = newState;

                if(newState == SCROLL_STATE_IDLE){

                    totalItemCount  = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if(notificationListList.size()!=0 && mScrollState == SCROLL_STATE_IDLE){
                        if((notificationListList.size()-1) == lastVisibleItem){
                            if (onLoadMoreListener != null) {
                                isLoading = true;
                                onLoadMoreListener.onLoadMore(notificationListList.size());
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

        sensorLogNotificationAdapter = new SensorLogNotificationAdapter(notificationListList);
        sensor_list.setAdapter(sensorLogNotificationAdapter);


    }

    RelativeLayout rel_spinner;
    RoomListArrayAdapter roomListArrayAdapter;

    private void bindView(){
        mSpinnerRoomList = (Spinner) findViewById(R.id.spinner_room_list);
        sp_drop_down = (ImageView) findViewById(R.id.sp_drop_down);

        sensor_list = (RecyclerView) findViewById(R.id.sensor_list);
        sensor_list.setLayoutManager(new GridLayoutManager(this,1));

        rel_spinner = (RelativeLayout) findViewById(R.id.rel_spinner);

        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification);

        edt_start_date = (CustomEditText) findViewById(R.id.edt_start_date);
        edt_end_date = (CustomEditText) findViewById(R.id.edt_end_date);

        view_bottom_line = findViewById(R.id.view_bottom_line);

        rg_sensor_type = (RadioGroup) findViewById(R.id.rg_sensor_type);
        rb_door = (RadioButton) findViewById(R.id.rb_door);
        rb_temp = (RadioButton) findViewById(R.id.rb_temp);

        rg_sensor_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.rb_door){

                    roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,getDoorSpinnerData(),mSensorName);
                    mSpinnerRoomList.setAdapter(roomListArrayAdapter);

                }else if(checkedId == R.id.rb_temp){

                    roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,getTempSpinnerData(),mSensorName);
                    mSpinnerRoomList.setAdapter(roomListArrayAdapter);

                }

            }
        });


        if(mSpinnerRoomList!=null){

            mSpinnerRoomList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                 //   RoomVO room = (RoomVO) mSpinnerRoomList.getSelectedItem();
                    //mSensorName = room.getRoomName();
                 //   mSensorName = room.getRoomName();
                 //   roomListArrayAdapter.setRoomName(room.getRoomName());
                 //   roomListArrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinnerRoomList.performClick();
            }
        });

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


        if(isSensorIntent){
            if(mSensorType.equalsIgnoreCase("door")){
                rb_door.setChecked(true);
            }else if(mSensorType.equalsIgnoreCase("temp")){
                rb_temp.setChecked(true);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d("onResume","doInBackground call...");
                unreadApiCall();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ChatApplication.isLogResume = true;
        super.onBackPressed();
    }

    public void unreadApiCall(){

        if(unReadLogs!=null){

          /*  if (!ActivityHelper.isConnectingToInternet(this)) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                return;
            }*/

            String webUrl = ChatApplication.url + Constants.UPDATE_UNREAD_LOGS;

            JSONObject jsonObject = new JSONObject();
            try {

                JSONArray jsonArray = new JSONArray();

                //code for removed duplication in array list //not working
               /* HashSet<RoomVO> hashSet = new HashSet<RoomVO>();
                hashSet.addAll(unReadLogs);
                unReadLogs.clear();
                unReadLogs.addAll(hashSet);*/

                for(RoomVO roomVO : unReadLogs){

                    JSONObject object = new JSONObject();

                    object.put("sensor_type",roomVO.getType());

                    if(roomVO.getRoomName().equalsIgnoreCase("All")){
                        object.put("module_id","");
                        object.put("room_id","");
                    }else{
                        object.put("module_id",roomVO.getModule_id());
                        object.put("room_id",roomVO.getRoomId());
                    }

                    jsonArray.put(object);
                }

                jsonObject.put("update_logs",jsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("JSONREAD","read : " + jsonObject.toString());

        //    ActivityHelper.showProgressDialog(this, "Please wait.", false);
            new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new ICallBack() {
                @Override
                public void onSuccess(JSONObject result) {

                    Log.d("SensorLog","onSuccess result : " + result.toString());
                    /*ActivityHelper.dismissProgressDialog();
                    try {

                        int code = result.getInt("code");
                        String message = result.getString("message");
                        if(code == 200){
                            onBackPressed();
                        }else{
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }

                @Override
                public void onFailure(Throwable throwable, String error) {
                    //ActivityHelper.dismissProgressDialog();
                   // onBackPressed();
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }


    ArrayAdapter spinnerArrayAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        menuAdd.setVisible(true);
        menuAdd.setIcon(R.drawable.icn_reset);

        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setTitle("Apply");

        return super.onCreateOptionsMenu(menu);
    }

    private List<RoomVO> unReadLogs = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {

           // roomListArrayAdapter.setRoomName(mSensorName);
           // roomListArrayAdapter.notifyDataSetChanged();

            notificationListList.clear();
            isSensorIntent = false;
            isEndOfRecord = false;

            RoomVO room = (RoomVO) mSpinnerRoomList.getSelectedItem();
            addUnreadRoomInList(room);

            applyFilter(0, isSensorIntent);
            return true;
        }else if(id == R.id.action_add){
            int sId = rg_sensor_type.getCheckedRadioButtonId();
            if(sId == R.id.rb_door){

                roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,getDoorSpinnerData(),"All");
                mSpinnerRoomList.setAdapter(roomListArrayAdapter);

            }else if(sId == R.id.rb_temp){

                roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,getTempSpinnerData(),"All");
                mSpinnerRoomList.setAdapter(roomListArrayAdapter);
            }


            resetFilter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * reset applied filter
     */
    private void resetFilter(){

        //rb_temp.setChecked(false);
        //rb_door.setChecked(false);

        edt_start_date.setText("");
        edt_end_date.setText("");

        start_date = "";
        end_date = "";

        notificationListList.clear();
        sensorLogNotificationAdapter = new SensorLogNotificationAdapter(notificationListList);
        sensor_list.setAdapter(sensorLogNotificationAdapter);
        sensorLogNotificationAdapter.notifyDataSetChanged();

        /*txt_empty_notification.setVisibility(View.VISIBLE);
        view_bottom_line.setVisibility(View.VISIBLE);
        txt_empty_notification.setText("No Record Found.");*/

        mSpinnerRoomList.setSelection(0);
        isSensorIntent = false;
        isEndOfRecord = false;

        applyFilter(0,isSensorIntent);

       /* int sId = rg_sensor_type.getCheckedRadioButtonId();
        if(sId == R.id.rb_door){


        }else if(sId == R.id.rb_temp){

            ArrayList<RoomVO> roomVOArrayList = getTempSpinnerData();

            RoomListArrayAdapter adapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,roomVOArrayList);
            mSpinnerRoomList.setAdapter(adapter);
        }
        sensorLogNotificationAdapter.notifyDataSetChanged();*/
    }

    private ArrayList<RoomVO> getDoorSpinnerData(){

        ArrayList<RoomVO> moodListSpinnerEmpty = new ArrayList<>();

        if(mSensorLogRes!=null){

            List<SensorLogRes.Data.DoorSensor> tempSensors = mSensorLogRes.getData().getDoorSensor();

            if(tempSensors!=null){


                for(SensorLogRes.Data.DoorSensor doorSensor : tempSensors){
                    RoomVO roomT = new RoomVO();
                    roomT.setRoomId(doorSensor.getRoomId());
                    roomT.setRoomName(doorSensor.getSensorName());
                    roomT.setModule_id(doorSensor.getModuleId());
                    roomT.setType("door");
                    roomT.setDropSensorList(doorSensor.getDoorSensorName());

                    moodListSpinnerEmpty.add(roomT);
                }
            }

            RoomVO room = new RoomVO();
            room.setRoomId("");
            room.setRoomName("All");
            room.setDropSensorList("All");
            room.setType("door");
            moodListSpinnerEmpty.add(0,room);


            if(isSensorIntent){
                for (int i = 0; i < moodListSpinnerEmpty.size(); i++) {
                    RoomVO roomVO = moodListSpinnerEmpty.get(i);
                    if (roomVO.getDropSensorList().equalsIgnoreCase(mSensorName)) {
                        mSpinnerRoomList.setSelection(i);

                        addUnreadRoomInList(roomVO);
                    }
                }
            }
        }
        return moodListSpinnerEmpty;
    }


    private ArrayList<RoomVO> getTempSpinnerData(){

        ArrayList<RoomVO> moodListSpinnerEmpty = new ArrayList<>();

        if(mSensorLogRes!=null){

            List<SensorLogRes.Data.TempSensor> tempSensors = mSensorLogRes.getData().getTempSensor();

            if(tempSensors!=null){

                for(SensorLogRes.Data.TempSensor tempSensor : tempSensors){
                    RoomVO roomT = new RoomVO();
                    roomT.setRoomId(tempSensor.getRoomId());
                    roomT.setRoomName(tempSensor.getSensorName());
                    roomT.setModule_id(tempSensor.getModuleId());
                    roomT.setDropSensorList(tempSensor.getTempSensorName());
                    roomT.setType("temp");
                    moodListSpinnerEmpty.add(roomT);
                }
            }

            RoomVO room = new RoomVO();
            room.setRoomId("");
            room.setRoomName("All");
            room.setDropSensorList("All");
            room.setType("temp");
            moodListSpinnerEmpty.add(0,room);


            if(isSensorIntent){
                for (int i = 0; i < moodListSpinnerEmpty.size(); i++) {
                    RoomVO roomVO = moodListSpinnerEmpty.get(i);
                    if (roomVO.getDropSensorList().equalsIgnoreCase(mSensorName)) {
                        mSpinnerRoomList.setSelection(i);

                        addUnreadRoomInList(roomVO);

                    }
                }
            }
        }

       return moodListSpinnerEmpty;

    }

    @Override
    public void onLoadMore(int lastVisibleItem) {
        applyFilter(lastVisibleItem, isSensorIntent);
    }

    SensorLogNotificationRes sensorLogNotificationRes;
    public boolean isEndOfRecord = false;

    private void applyFilter(int position, boolean isSensorIntent){

        if(!isSensorIntent){

            if(rg_sensor_type.getCheckedRadioButtonId() == -1){
                Toast.makeText(getApplicationContext(),"Select Sensor Type",Toast.LENGTH_SHORT).show();
                return;
            }

        }
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String webUrl = ChatApplication.url + Constants.SENSOR_NOTIFICATION;

        JSONObject jsonNotification = new JSONObject();

        String sensor_type = "";
        int typeId = rg_sensor_type.getCheckedRadioButtonId();

        if (typeId == R.id.rb_door) {
            sensor_type = "door";
        } else if (typeId == R.id.rb_temp) {
            sensor_type = "temp";
        }

        RoomVO room = (RoomVO) mSpinnerRoomList.getSelectedItem();

        try {
            jsonNotification.put("notification_number",position);

            String mId = "",mRid = "";
            if(mSpinnerRoomList.getSelectedItemPosition()==0){
                mId = "";
                mRid = "";
            }else{
                mId = room.getModule_id();
                mRid = room.getRoomId();
            }

            jsonNotification.put("module_id",mId);
            jsonNotification.put("room_id",mRid);
            jsonNotification.put("start_date",start_date);
            jsonNotification.put("end_date",end_date);
            jsonNotification.put("sensor_type",sensor_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("SensorLog","json Request : " + jsonNotification.toString());

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.d("SensorLog","onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){

                        sensor_list.setVisibility(View.VISIBLE);
                        txt_empty_notification.setVisibility(View.GONE);
                        view_bottom_line.setVisibility(View.GONE);

                        sensorLogNotificationRes = Common.jsonToPojo(result.toString(),SensorLogNotificationRes.class);
                        notificationListList.addAll(sensorLogNotificationRes.getData().getNotificationList());

                        if(sensorLogNotificationRes.getData().getNotificationList().size()==0 && isLoading){

                            if(!isEndOfRecord){
                                showAToast("End of Record...");
                                SensorLogNotificationRes.Data.NotificationList notificationList = new  SensorLogNotificationRes.Data.NotificationList();
                                notificationList.setActivityAction("End Of Record");
                                notificationListList.add(notificationList);
                            }
                            isEndOfRecord = true;
                        }

                        if(sensorLogNotificationRes.getData().getNotificationList().size()==0 && notificationListList.size()==0){
                            sensor_list.setVisibility(View.GONE);
                            txt_empty_notification.setVisibility(View.VISIBLE);
                            view_bottom_line.setVisibility(View.VISIBLE);
                            txt_empty_notification.setText("No Record Found.");
                        }

                        sensorLogNotificationAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }

    private Toast mToast;

    /**
     *
     * @param st
     */
    public void showAToast (String st){ //"Toast toast" is declared in the class
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void getSensorRoomList(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ChatApplication.url + Constants.SENSOR_ROOM_DETAILS;

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.d("SensorLog","result : " + result.toString());

                ActivityHelper.dismissProgressDialog();
                mSensorLogRes = Common.jsonToPojo(result.toString(),SensorLogRes.class);

                if(mSensorLogRes.getCode() == 200){

                    int sId = rg_sensor_type.getCheckedRadioButtonId();
                    if(sId == R.id.rb_door){

                        ArrayList<RoomVO> roomVOArrayList = getDoorSpinnerData();

                     //   spinnerArrayAdapter = new ArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,roomVOArrayList);
                     //   mSpinnerRoomList.setAdapter(spinnerArrayAdapter);

                        roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,roomVOArrayList,mSensorName);
                        mSpinnerRoomList.setAdapter(roomListArrayAdapter);

                        if(isSensorIntent){
                            for (int i = 0; i < roomVOArrayList.size(); i++) {
                                RoomVO roomVO = roomVOArrayList.get(i);
                                if (roomVO.getDropSensorList().equalsIgnoreCase(mSensorName)) {
                                    mSpinnerRoomList.setSelection(i);

                                    addUnreadRoomInList(roomVO);

                                }
                            }
                        }

                    }else if(sId == R.id.rb_temp){

                        ArrayList<RoomVO> roomVOArrayList = getTempSpinnerData();

                      //  spinnerArrayAdapter = new ArrayAdapter(SensorDoorLogActivity.this,android.R.layout.simple_spinner_dropdown_item,roomVOArrayList);
                      //  mSpinnerRoomList.setAdapter(spinnerArrayAdapter);

                        roomListArrayAdapter = new RoomListArrayAdapter(SensorDoorLogActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,roomVOArrayList,mSensorName);
                        mSpinnerRoomList.setAdapter(roomListArrayAdapter);

                        if(isSensorIntent){
                            for (int i = 0; i < roomVOArrayList.size(); i++) {
                                RoomVO roomVO = roomVOArrayList.get(i);
                                if (roomVO.getDropSensorList().equalsIgnoreCase(mSensorName)) {
                                    mSpinnerRoomList.setSelection(i);

                                    addUnreadRoomInList(roomVO);
                                }
                            }
                        }
                    }

                    if(isSensorIntent){
                        applyFilter(0,isSensorIntent);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).execute();
    }

    /**
     *
     * @param room
     */

    private void addUnreadRoomInList(RoomVO room){

        String sensor_type = "";
        int typeId = rg_sensor_type.getCheckedRadioButtonId();
        if (typeId == R.id.rb_door) {
            sensor_type = "door";
        } else if (typeId == R.id.rb_temp) {
            sensor_type = "temp";
        }

        RoomVO unReadRoom = new RoomVO();
        unReadRoom.setRoomId(room.getRoomId());
        unReadRoom.setModule_id(room.getModule_id());
        unReadRoom.setType(sensor_type);

        /*boolean isAlreadAdd = false;
        for (RoomVO rvo : unReadLogs){
            if(rvo.getModule_id().equalsIgnoreCase("") && rvo.getRoomId().equalsIgnoreCase("") && rvo.getType().contains(unReadRoom.getType())){
                isAlreadAdd = true;
            }

        }
        if(!isAlreadAdd){
            unReadLogs.add(unReadRoom);
        }*/
        unReadLogs.add(unReadRoom);

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

    private String changeDateFormat(String str_date){
        String date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_2);
        date = format.format(getDate(str_date));
        return date;
    }
    /*
      * YYYY-MM-DD HH:mm:ss
      * @return
      * */
    private boolean compareDate(String startDate, String endDate){

        Date start_date = getDate(startDate) ;
        Date end_date  = getDate(endDate);

        return end_date.after(start_date);
    }

        /*
    * convert string format to date format
    * @return date
    * */

    private Date getDate(String dtStart){
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

}
