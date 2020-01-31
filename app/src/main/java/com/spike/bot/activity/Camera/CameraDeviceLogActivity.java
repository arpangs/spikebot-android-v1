package com.spike.bot.activity.Camera;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.adapter.CameraLogAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.NotificationList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sagar on 26/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraDeviceLogActivity extends AppCompatActivity {

    public RecyclerView rvDeviceLog;
    public LinearLayout ll_empty;
    public Toolbar toolbar;
    public TextView et_schedule_on_time, et_schedule_off_time;

    public boolean isLoading = false, isCompareDateValid = true;
    public int notification_number = 0;
    public String camera_id = "", end_date = "", start_date = "", date_time = "", cameraIdTemp = "";
    public int mYear, mMonth, mDay;
    public int mHour, mMinute, mSecond;

    public Dialog dialog;
    /*Adapter */
    public LinearLayoutManager linearLayoutManager;
    public CameraLogAdapter cameraLogAdapter;
    public ArrayList<NotificationList> arrayList = new ArrayList<>();
    public ArrayList<NotificationList> arrayListTemp = new ArrayList<>();
    public static ArrayList<CameraVO> getCameraList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_device_log);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUi();
    }

    private void setUi() {
        camera_id = getIntent().getStringExtra("cameraId");
        toolbar.setTitle("Camera Logs");
        rvDeviceLog = (RecyclerView) findViewById(R.id.rv_device_log);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

        linearLayoutManager = new LinearLayoutManager(CameraDeviceLogActivity.this);
        rvDeviceLog.setLayoutManager(linearLayoutManager);

        if (TextUtils.isEmpty(camera_id)) {
            camera_id = "";
        } else {
            cameraIdTemp = camera_id;
        }
        callCameraLog(camera_id, "", "", notification_number);
    }
    /*reset data when refresh page */
    public void resetData() {
        isLoading = false;
        arrayList.clear();
        if (cameraLogAdapter != null) {
            cameraLogAdapter.notifyDataSetChanged();
        }

        start_date = "";
        end_date = "";
        notification_number = 0;
        camera_id = "" + cameraIdTemp;


        callCameraLog(camera_id, start_date, end_date, notification_number);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        MenuItem menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setIcon(R.drawable.icn_filter);
        menuItem.setIcon(R.drawable.icn_reset);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            resetData();
            return true;
        } else if (id == R.id.action_reset) {
            openDialogFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    /*filter
    * interval user for camera detection time interval
    * show all camera list  */
    private void openDialogFilter() {
        dialog = new Dialog(CameraDeviceLogActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_filter_camera);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView iv_close =  dialog.findViewById(R.id.iv_close);
        et_schedule_on_time =  dialog.findViewById(R.id.et_schedule_on_time);
        et_schedule_off_time =  dialog.findViewById(R.id.et_schedule_off_time);
        TextView txtSave =  dialog.findViewById(R.id.txtSave);
        Spinner spinnerCamera =  dialog.findViewById(R.id.spinnerCamera);

        final ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("All");
        for (int i = 0; i < getCameraList.size(); i++) {
            stringArrayList.add(getCameraList.get(i).getCamera_name());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringArrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCamera.setAdapter(dataAdapter);

        spinnerCamera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    camera_id = "";
                } else {
                    camera_id = getCameraList.get(position - 1).getCamera_id();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        et_schedule_on_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(et_schedule_on_time, false);
            }
        });
        et_schedule_off_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(et_schedule_off_time, true);
            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_schedule_on_time.getText().toString().length() == 0) {
                    Toast.makeText(CameraDeviceLogActivity.this, "Please enter start date", Toast.LENGTH_SHORT).show();
                } else if (et_schedule_off_time.getText().toString().length() == 0) {
                    Toast.makeText(CameraDeviceLogActivity.this, "Please enter end date", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(et_schedule_on_time.getText().toString()) && !TextUtils.isEmpty(et_schedule_off_time.getText().toString()) &&
                        et_schedule_on_time.getText().toString().equalsIgnoreCase(et_schedule_off_time.getText().toString())) {
                    Toast.makeText(CameraDeviceLogActivity.this, "Please select different date", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.cancel();
                    notification_number = 0;
                    callCameraLog(camera_id, start_date, end_date, notification_number);
                }
            }
        });
        dialog.show();
    }


    /**
     * Get camera log
     */
    private void callCameraLog(String camera_id, String start_date, String end_date, final int notification_number) {
        if (!ActivityHelper.isConnectingToInternet(CameraDeviceLogActivity.this)) {
            Toast.makeText(CameraDeviceLogActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object = new JSONObject();
        try {
            /*"camera_id":"",	"start_date":"",	"end_date":""*/
//            object.put("camera_id", "" + camera_id);
//            object.put("start_date", "" + start_date);
//            object.put("end_date", "" + end_date);
//            object.put("notification_number", "" + notification_number);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("filter_type", "camera");
            object.put("start_date", ""+start_date);
            object.put("end_date", ""+end_date);
            object.put("camera_id", ""+camera_id);
            object.put("notification_number", ""+notification_number);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(CameraDeviceLogActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.logsfind;
        ChatApplication.logDisplay("camera is "+url+" "+object);
        new GetJsonTask(CameraDeviceLogActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (notification_number == 0) {
                            arrayList.clear();
                        }
                        arrayListTemp.clear();
                        ChatApplication.logDisplay("json is " + result);
                        JSONArray jsonArray = result.optJSONArray("data");

                        if(jsonArray!=null && jsonArray.length()>0){
                            arrayListTemp = (ArrayList<NotificationList>) Common.fromJson(jsonArray.toString(), new TypeToken<ArrayList<NotificationList>>() {}.getType());

                            arrayList.addAll(arrayListTemp);
                        }


                        if (arrayList.size() > 0) {
                            rvDeviceLog.setVisibility(View.VISIBLE);
                            isLoading = false;
                            setAdapter();
                        } else {
                            if (notification_number == 0) {
                                rvDeviceLog.setVisibility(View.GONE);
                            }
                            isLoading = true;
                        }
                        if (arrayListTemp.size() == 0) {
                            isLoading = true;
                        }
                    } else {
                        isLoading = true;
                        Toast.makeText(CameraDeviceLogActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraDeviceLogActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /*
     * @param editText   : start_date/end_date textview
     * @param isEndDate  : is end_date textView or not
     * */
    private void datePicker(final TextView editText, final boolean isEndDate) {

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

    /*time picker dialog*/
    private void tiemPicker(final TextView editText, final boolean isEndDate) {
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

                        editText.setText("" + DeviceLogActivity.changeDateFormat(on_date));

                        if (!TextUtils.isEmpty(et_schedule_on_time.getText().toString()) && !TextUtils.isEmpty(et_schedule_off_time.getText().toString())) {
                            boolean isCompare = DeviceLogActivity.compareDate(start_date, end_date);
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

    /*set view */
    private void setAdapter() {

        if (notification_number == 0) {
            cameraLogAdapter = new CameraLogAdapter(CameraDeviceLogActivity.this, arrayList);
            rvDeviceLog.setAdapter(cameraLogAdapter);
        } else {
            cameraLogAdapter.notifyDataSetChanged();
        }
        notification_number = notification_number + 20;

        rvDeviceLog.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && notification_number != 0) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        callCameraLog(camera_id, start_date, end_date, notification_number);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    /*use for camera unread count clear */
    private void callupdateUnReadCameraLogs(final boolean b) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("camera_id", "" + cameraIdTemp);
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.updateUnReadCameraLogs;
        ChatApplication.logDisplay("url is "+url+" "+jsonObject);
        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("url is "+result);
                try {
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraDeviceLogActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /*check unread count getting than clear count */
    @Override
    public void onBackPressed() {
        callupdateUnReadCameraLogs(false);
        super.onBackPressed();
    }
}
