package com.spike.bot.activity.Camera;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTaskVideo;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.SmartDevice.ExpandableTestAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraSearchModel;
import com.spike.bot.model.CameraVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;

import static com.spike.bot.core.Constants.CAMERA_PATH;

/**
 * Created by Sagar on 19/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CameraPlayBack extends AppCompatActivity implements ExpandableTestAdapter.CameraClick {

    private TextInputEditText edt_start_date, edt_end_date;
    private Spinner sp_camera_list;
    private ArrayList<CameraVO> cameraVOArrayList;

    private RecyclerView cameraList;
    private ExpandableTestAdapter cameraListAdapter;
    private TextView txt_no_date;
    private TextView txtTitle;
    private ImageView imgTitle;

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;
    int mSecond;

    String webUrl = "";
    private Socket mSocket;
    public static String start_date = "";
    public static String end_date = "";
    ArrayList<String> cameraStr;

    ArrayList<CameraSearchModel> cameraArrayList = new ArrayList<>();
    private TextView txt_count;

    AlertDialog.Builder alertdialogbuilder;
    String[] alertDialogItems;
    boolean[] selectedtruefalse;
    List<String> ItemsIntoList;
    String[] cameraIdList;
    List<String> selectedCamera = new ArrayList<>();
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_playback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Camera Play");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        edt_start_date =  findViewById(R.id.edt_start_date);
        edt_end_date =  findViewById(R.id.edt_end_date);
        sp_camera_list =  findViewById(R.id.sp_camera_list);

        txt_no_date =  findViewById(R.id.txt_no_date);

        txtTitle =  findViewById(R.id.txt_title);
        imgTitle =  findViewById(R.id.img_title);
        txt_count =  findViewById(R.id.txt_count);

        cameraList =  findViewById(R.id.cameraList);
        cameraList.setLayoutManager(new GridLayoutManager(this, 1));

        // roomVO = (RoomVO) getIntent().getExtras().getSerializable("room");
        cameraVOArrayList = (ArrayList<CameraVO>) getIntent().getExtras().getSerializable("cameraList");

        cameraStr = new ArrayList<>();

        if (!cameraVOArrayList.isEmpty()) {
            for (CameraVO cameraVO : cameraVOArrayList) {
                cameraStr.add(cameraVO.getCamera_name());
            }
        }

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this, cameraStr, 1, false);
        sp_camera_list.setAdapter(customAdapter);

        edt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(edt_start_date, false);
            }
        });

        edt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(edt_end_date, true);
            }
        });
        arrayList = new ArrayList<>();

        startSocketConnection();

        //camera List<String> to convert String toArray
        alertDialogItems = new String[cameraStr.size()];
        alertDialogItems = cameraStr.toArray(alertDialogItems);

        selectedtruefalse = new boolean[alertDialogItems.length];
        for (int i = 0; i < alertDialogItems.length; i++) {
            selectedtruefalse[i] = false;
        }

        if (!cameraVOArrayList.isEmpty()) {
            for (int i = 0; i < cameraVOArrayList.size(); i++) {
                CameraVO cameraVO = cameraVOArrayList.get(i);
                // cameraStr.add(cameraVO.getCamera_name());
                // cameraIdList[i] = cameraVO.getCamera_id();
            }
        }

        alertdialogbuilder = new AlertDialog.Builder(CameraPlayBack.this);

        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceItemsDialogs();
            }
        });
        imgTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceItemsDialogs();
            }
        });

    }

    private void showMultiChoiceItemsDialogs() {
        alertdialogbuilder.setMultiChoiceItems(alertDialogItems, selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        alertdialogbuilder.setCancelable(false);
        alertdialogbuilder.setTitle("Select Camera Here");

        ItemsIntoList = Arrays.asList(alertDialogItems);

        alertdialogbuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCamera.clear();
                int count = 0;
                int a = 0;
                while (a < selectedtruefalse.length) {
                    boolean value = selectedtruefalse[a];
                    if (value) {
                        count++;
                        selectedCamera.add(ItemsIntoList.get(a));
                    }
                    a++;
                }

                if (count > 0) {
                    txt_count.setText("(" + count + ")");
                } else {
                    txt_count.setText("");
                }

            }
        });

        alertdialogbuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            searchCameraList(start_date, end_date);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<CameraVO> cameraVOs = new ArrayList<>();

    /*
     *   search camera list between two date
     *   @param startDate  :    2018-03-18 16:00:00
     *   @param endDate    :    2018-03-19 14:00:00
     *   Date format       :    YYYY-MM-DD HH:mm:ss (second default 00)
     *
     * */
    private void searchCameraList(String startDate, String endDate) {

        if (TextUtils.isEmpty(edt_start_date.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Select Start Date.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edt_end_date.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Select End Date.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidDate = compareDate(start_date, end_date);
        if (!isValidDate) {
            Toast.makeText(getApplicationContext(), "End date is not less than Start Date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCamera.size() == 0) {
            Toast.makeText(getApplicationContext(), "Please Select Camera.", Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(CameraPlayBack.this, "Please wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().searchCameraList(startDate, endDate, cameraVOArrayList, selectedCamera, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ChatApplication.ADAPTER_POSITION = -1;
                ActivityHelper.dismissProgressDialog();
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        cameraArrayList.clear();


                        JSONObject object = result.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("cameraList");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject ob = jsonArray.getJSONObject(i);
                            String camera_id = ob.getString("camera_id");
                            String camera_name = ob.getString("camera_name");
                            JSONArray array = ob.getJSONArray("camera_files");

                            CameraSearchModel cameraViewModel = new CameraSearchModel(false, camera_id, camera_name);
                            cameraViewModel.setCamera_id(camera_id);
                            cameraViewModel.setCamera_name(camera_name);

                            String videoName = "";
                            ArrayList<CameraVO> templist = new ArrayList<>();
                            for (int j = 0; j < array.length(); j++) {

                                String u_name = array.get(j).toString();
                                CameraVO cameraVO = new CameraVO();
                                cameraVO.setCamera_id(camera_id);
                                cameraVO.setCamera_name(camera_name);
                                cameraVO.setCamera_videopath(u_name);

                                ChatApplication.logDisplay("name is " + u_name);

                                String[] separated = u_name.split("/");
                                if (separated != null) {
                                    if (separated[separated.length - 1] != null) {
                                        cameraVO.setLoadingUrl(separated[separated.length - 1]);
                                        String[] separed1 = separated[separated.length - 1].toString().split("-");
                                        videoName = "";
                                        for (int k = 1; k < separed1.length; k++) {
                                            if (k >= 2) {
                                                if (k == 2) {
                                                    videoName = videoName + separed1[k];
                                                } else {
                                                    videoName = videoName + "-" + separed1[k];
                                                }
                                            }

                                        }
                                        cameraVO.setCamera_ip(videoName);
                                    }
                                }

                                templist.add(cameraVO);
                            }
                            cameraViewModel.setArrayList(templist);
                            cameraArrayList.add(cameraViewModel);
                        }

                        setAdpater();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (cameraArrayList.isEmpty()) {
                        txt_no_date.setVisibility(View.VISIBLE);
                        cameraList.setVisibility(View.GONE);
                    } else {
                        txt_no_date.setVisibility(View.GONE);
                        cameraList.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                if (cameraVOs.isEmpty()) {
                    txt_no_date.setVisibility(View.VISIBLE);
                    cameraList.setVisibility(View.GONE);
                } else {
                    txt_no_date.setVisibility(View.GONE);
                    cameraList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                if (cameraVOs.isEmpty()) {
                    txt_no_date.setVisibility(View.VISIBLE);
                    cameraList.setVisibility(View.GONE);
                } else {
                    txt_no_date.setVisibility(View.GONE);
                    cameraList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setAdpater() {
        if (cameraArrayList.size() > 0) {
            cameraListAdapter = new ExpandableTestAdapter(this, cameraArrayList, CameraPlayBack.this);
            cameraList.setAdapter(cameraListAdapter);
            cameraListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCameraClick(int group, int postion, CameraVO cameraVO) {

        //rtmp://home.deepfoods.net:11114/1516175178515_T4bw34W/2018-03-23_15.00.mp4
        //rtmp://home.deepfoods.net:11111/live/livestream1

        //String ip = webUrl + "/static/storage/"; //old url
        String ip = webUrl + CAMERA_PATH;

        //http://home.deepfoods.net:10000/static/storage/volume/pi/1564123250335_hgTonaqUq-2019-07-26_13.20.mp4

        Intent intent = new Intent(CameraPlayBack.this, VideoViewPLayer.class);
        intent.putExtra("videoUrl", Constants.startUrlhttp+"//"+ip + "" + cameraVO.getCamera_id() + cameraVO.getCamera_videopath());
        intent.putExtra("videoUrl", Constants.startUrlhttp+"//"+ip + "" + cameraVO.getLoadingUrl());
        intent.putExtra("name", "" + cameraArrayList.get(group).getCamera_name());
        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
        startActivity(intent);
    }

    public void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        if (Common.isConnected()) {
        }
    }

    /*
     * @param editText   : start_date/end_date textview
     * @param isEndDate  : is end_date textView or not
     * */
    private void datePicker(final EditText editText, final boolean isEndDate) {

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
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void tiemPicker(final EditText editText, final boolean isEndDate) {
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
                            if (!isCompare) {
                                editText.setText("");
                                Toast.makeText(getApplicationContext(), "End date is not less than Start Date", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    /*
     * YYYY-MM-DD HH:mm:ss
     * @return
     * */
    private boolean compareDate(String startDate, String endDate) {

        Date start_date = getDate(startDate);
        Date end_date = getDate(endDate);

        return end_date.after(start_date);
    }

    /*
     * convert string format to date format
     * @return date
     * */

    private Date getDate(String dtStart) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_1);

        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
     * @param str_date : 2018-03-21 02:24:56
     * @return date    : 21-jan 2018 02:24 PM
     * @call getDate(str_date) String to date format
     * */

    private String changeDateFormat(String str_date) {
        String date = null;
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_3);
        date = format.format(getDate(str_date));
        return date;
    }

}
