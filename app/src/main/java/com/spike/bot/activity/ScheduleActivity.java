package com.spike.bot.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.DeviceListLayoutHelper;
import com.spike.bot.adapter.ScheduleAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment;
import com.spike.bot.listener.SelectDevicesListener;
import com.spike.bot.model.AutoModeVO;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import io.socket.client.Socket;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener, ItemClickListener, SelectDevicesListener {

    EditText et_schedule_name;
    CustomEditText et_schedule_on_time, et_schedule_off_time;
    Spinner sp_schedule_list;
    RadioGroup rg_schedule_type,rg_schedule_select;
    RadioButton rb_schedule_type_room, rb_schedule_type_mood,rb_schedule_select_schedule, rb_schedule_select_auto;

    TextView text_schedule_1, text_schedule_2, text_schedule_3, text_schedule_4, text_schedule_5, text_schedule_6, text_schedule_7,txt_empty_sch;
    ImageView iv_schedule_on_time_clear, iv_schedule_off_time_clear;
    Button btnMoodSchedule, btnRoomSchedule,btn_add_schedule, btn_cancel_schedule;
    ImageView imgArrow, imgArraoTime;
    LinearLayout ll_schedule, ll_spinner_hide,ll_on_time, ll_off_time, ll_week_title, ll_week_days,ll_on_time_auto, ll_on_time_bottom,ll_off_time_auto, ll_off_time_bottom,ll_spinner_mood,empty_ll_view;
    private Spinner sp_mood_selection;
    CustomEditText et_on_time_hours, et_on_time_min,et_off_time_hours, et_off_time_min;
    TextView et_on_time_bottom_header, et_off_time_bottom_header,et_on_time_bottom_header_at, et_on_time_bottom_header_at_time, et_on_time_bottom_header_at_ampm,
            et_off_time_bottom_header_at, et_off_time_bottom_header_at_time, et_off_time_bottom_header_at_ampm,tv_schedule_list;
    RecyclerView rv_auto_mode, rv_schedule;

    JSONObject deviceObj = new JSONObject();
    ScheduleVO scheduleVO = new ScheduleVO();
    int position = 0,selection=0,nextFalse=0;
    boolean isEdit = false, isEditOpen = false, isSelectMode = false,isScheduleClick = false, isMoodSelected = false, isMap = false, isMoodAdapter;
    String webUrl = "",moodId = "", roomId = "", startCheckDate = "", endCheckDate = "",on_time_date = "",off_time_date = "", isActivityType = "",on_at_time = "", off_at_time = "";

    ArrayList<RoomVO> roomList = new ArrayList<>();
    ArrayList<RoomVO> roomListAdd = new ArrayList<>();
    ArrayList<MoodVO> moodList = new ArrayList<>();
    ArrayList<RoomVO> moodListSpinner = new ArrayList<>();
    ArrayList<RoomVO> moodListAdd = new ArrayList<>();
    ArrayList<RoomVO> roomArrayListTemp = new ArrayList<>();

    DeviceListLayoutHelper deviceListLayoutHelper;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {

            /*
            isEditOpen means is Expand Or Not true check
            * */

            isScheduleClick = getIntent().getBooleanExtra("isScheduleClick", false);
            if (isScheduleClick) {
                selection = getIntent().getIntExtra("selection", 0);
            }
            scheduleVO = (ScheduleVO) getIntent().getSerializableExtra("scheduleVO");
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            isMap = getIntent().getBooleanExtra("isMap", false);
            isEditOpen = getIntent().getBooleanExtra("isEditOpen", false);
            moodId = getIntent().getStringExtra("moodId");
            roomId = getIntent().getStringExtra("roomId");
            isActivityType = getIntent().getStringExtra("isActivityType");
            isMoodSelected = getIntent().getBooleanExtra("isMoodSelected", false);

            //isMoodAdapter true : is moodeDevices/2 if false : Room devices/1 if
            isMoodAdapter = getIntent().getBooleanExtra("isMoodAdapter", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isEdit) {
            setTitle("Edit Schedule");
        } else {
            setTitle("Add Schedule");
        }

        ChatApplication app = (ChatApplication) getApplication();
        webUrl = app.url;


        ll_on_time = findViewById(R.id.ll_on_time);
        ll_off_time = findViewById(R.id.ll_off_time);
        ll_week_title = findViewById(R.id.ll_week_title);
        ll_week_days = findViewById(R.id.ll_week_days);
        ll_on_time_auto = findViewById(R.id.ll_on_time_auto);
        ll_on_time_bottom = findViewById(R.id.ll_on_time_bottom);
        ll_off_time_auto = findViewById(R.id.ll_off_time_auto);
        ll_off_time_bottom = findViewById(R.id.ll_off_time_bottom);
        imgArrow =  findViewById(R.id.imgArrow);
        imgArraoTime =  findViewById(R.id.imgArraoTime);
        empty_ll_view =  findViewById(R.id.empty_ll_view);
        empty_ll_view.setVisibility(View.GONE);
        ll_spinner_mood =  findViewById(R.id.ll_spinner_mood);
        sp_mood_selection =  findViewById(R.id.sp_mood_selection);
        et_on_time_hours = findViewById(R.id.et_on_time_hours);
        et_on_time_min = findViewById(R.id.et_on_time_min);
        et_off_time_hours = findViewById(R.id.et_off_time_hours);
        et_off_time_min = findViewById(R.id.et_off_time_min);
        txt_empty_sch =  findViewById(R.id.txt_empty_sch);
        btnRoomSchedule =  findViewById(R.id.btnRoomSchedule);
        btnMoodSchedule =  findViewById(R.id.btnMoodSchedule);
        //headr on time
        et_on_time_bottom_header =  findViewById(R.id.et_on_time_bottom_header);
        et_on_time_bottom_header_at =  findViewById(R.id.et_on_time_bottom_header_at);
        et_on_time_bottom_header_at_time =  findViewById(R.id.et_on_time_bottom_header_at_time);
        et_on_time_bottom_header_at_ampm =  findViewById(R.id.et_on_time_bottom_header_at_ampm);

        //header off time
        et_off_time_bottom_header =  findViewById(R.id.et_off_time_bottom_header);

        et_off_time_bottom_header_at =  findViewById(R.id.et_off_time_bottom_header_at);
        et_off_time_bottom_header_at_time =  findViewById(R.id.et_off_time_bottom_header_at_time);
        et_off_time_bottom_header_at_ampm =  findViewById(R.id.et_off_time_bottom_header_at_ampm);

        //auto end
        sp_schedule_list = findViewById(R.id.sp_schedule_list);
        rg_schedule_type = findViewById(R.id.rg_schedule_type);
        rg_schedule_select = findViewById(R.id.rg_schedule_select);
        rb_schedule_type_room =  findViewById(R.id.rb_schedule_type_room);
        rb_schedule_type_mood =  findViewById(R.id.rb_schedule_type_mood);
        rb_schedule_select_schedule =  findViewById(R.id.rb_schedule_select_schedule);
        rb_schedule_select_auto =  findViewById(R.id.rb_schedule_select_timer);
        tv_schedule_list =  findViewById(R.id.tv_schedule_list);
        rv_auto_mode =  findViewById(R.id.rv_auto_mode);
        et_schedule_name =  findViewById(R.id.et_schedule_name);
        et_schedule_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        et_schedule_on_time = findViewById(R.id.et_schedule_on_time);
        et_schedule_off_time = findViewById(R.id.et_schedule_off_time);

        iv_schedule_on_time_clear =  findViewById(R.id.iv_schedule_on_time_clear);
        iv_schedule_off_time_clear =  findViewById(R.id.iv_schedule_off_time_clear);

        text_schedule_1 =  findViewById(R.id.text_schedule_1);
        text_schedule_2 =  findViewById(R.id.text_schedule_2);
        text_schedule_3 =  findViewById(R.id.text_schedule_3);
        text_schedule_4 =  findViewById(R.id.text_schedule_4);
        text_schedule_5 =  findViewById(R.id.text_schedule_5);
        text_schedule_6 =  findViewById(R.id.text_schedule_6);
        text_schedule_7 =  findViewById(R.id.text_schedule_7);
        rv_schedule = findViewById(R.id.rv_schedule);
        btn_add_schedule =  findViewById(R.id.btn_add_schedule);
        btn_cancel_schedule =  findViewById(R.id.btn_cancel_schedule);
        ll_spinner_hide =  findViewById(R.id.ll_spinner_hide);
        ll_schedule =  findViewById(R.id.ll_schedule);

        btn_add_schedule.setOnClickListener(this);
        btn_cancel_schedule.setOnClickListener(this);
        et_on_time_hours.setOnClickListener(this);
        et_on_time_min.setOnClickListener(this);
        btnMoodSchedule.setOnClickListener(this);
        btnRoomSchedule.setOnClickListener(this);
        text_schedule_1.setOnClickListener(this);
        text_schedule_2.setOnClickListener(this);
        text_schedule_3.setOnClickListener(this);
        text_schedule_4.setOnClickListener(this);
        text_schedule_5.setOnClickListener(this);
        text_schedule_6.setOnClickListener(this);
        text_schedule_7.setOnClickListener(this);
        text_schedule_1.setTag(false);
        text_schedule_2.setTag(false);
        text_schedule_3.setTag(false);
        text_schedule_4.setTag(false);
        text_schedule_5.setTag(false);
        text_schedule_6.setTag(false);
        text_schedule_7.setTag(false);

        sp_mood_selection.setOnItemSelectedListener(this);

        et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        et_schedule_on_time.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:

                        et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        et_schedule_on_time.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
        et_schedule_on_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_schedule_on_time.length() > 0) {
                    et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    et_schedule_on_time.setCompoundDrawablePadding(8);
                }
            }
        });

        et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        et_schedule_off_time.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        // et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                        et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        et_schedule_off_time.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
        et_schedule_off_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_schedule_off_time.length() > 0) {
                    et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    et_schedule_off_time.setCompoundDrawablePadding(8);
                }
            }
        });


        et_schedule_on_time.setOnKeyListener(null);
        et_schedule_on_time.setOnClickListener(this);

        et_schedule_off_time.setOnKeyListener(null);
        et_schedule_off_time.setOnClickListener(this);

        iv_schedule_on_time_clear.setOnClickListener(this);
        iv_schedule_off_time_clear.setOnClickListener(this);

        rv_auto_mode.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_schedule.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rg_schedule_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                if (id == R.id.rb_schedule_type_room) {
                    isMoodAdapter = false;
                    ll_spinner_mood.setVisibility(View.GONE);
                    getDeviceList();
                    //getRoomList();
                } else if (id == R.id.rb_schedule_type_mood) {
                    isMoodAdapter = true;
                    //getMoodList();
                    ll_spinner_mood.setVisibility(View.VISIBLE);
                    getMoodListAdd();
                }
            }
        });
        sp_schedule_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (isSelectMode) {
                    ArrayList<RoomVO> tempList = new ArrayList<RoomVO>();
                    if (position != 0) {
                        for (int i = 1; i < roomList.size(); i++) {
                            tempList.add(roomList.get(i));
                        }
                    }
                    setData(tempList, 2, false);

                    tv_schedule_list.setText("Room List");
                } else {
                    ArrayList<MoodVO> tempMoodList = new ArrayList<MoodVO>();
                    ArrayList<RoomVO> tempList = new ArrayList<RoomVO>();
                    if (position != 0) {
                        for (int i = 1; i < moodList.size(); i++) {
                            MoodVO moodVo = moodList.get(i);
                            RoomVO roomVO = new RoomVO();
                            roomVO.setRoomId(moodVo.getMood_id());
                            roomVO.setRoomName(moodVo.getMood_name());
                            roomVO.setPanelList(moodVo.getPanelList());
                            tempList.add(roomVO);
                        }
                    }
                    setData(tempList, 1, false);

                    tv_schedule_list.setText("Mood List");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (isEdit) {
            if (scheduleVO != null) {

                if (scheduleVO.getSchedule_type() == 0) {
                    rb_schedule_type_room.setChecked(true);
                    setBackGroundColorButton(true);
                    btnMoodSchedule.setVisibility(View.GONE);

                } else {
                    rb_schedule_type_mood.setChecked(true);
                    setBackGroundColorButton(false);
                    btnRoomSchedule.setVisibility(View.GONE);
                }
                et_schedule_name.setText(scheduleVO.getSchedule_name());
                et_schedule_name.setSelection(et_schedule_name.getText().length());

                if (!scheduleVO.getSchedule_device_on_time().equalsIgnoreCase("")) {
                    try {
                        et_schedule_on_time.setText(DateHelper.formateDate(DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_on_time(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (!scheduleVO.getSchedule_device_off_time().equalsIgnoreCase("")) {
                    try {
                        et_schedule_off_time.setText(DateHelper.formateDate(DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_off_time(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                repeatDayString = scheduleVO.getSchedule_device_day();
                if (repeatDayString.contains("0")) {
                    Common.setBackground(this, text_schedule_1, true);
                }
                if (repeatDayString.contains("1")) {
                    Common.setBackground(this, text_schedule_2, true);
                }
                if (repeatDayString.contains("2")) {
                    Common.setBackground(this, text_schedule_3, true);
                }
                if (repeatDayString.contains("3")) {
                    Common.setBackground(this, text_schedule_4, true);
                }
                if (repeatDayString.contains("4")) {
                    Common.setBackground(this, text_schedule_5, true);
                }
                if (repeatDayString.contains("5")) {
                    Common.setBackground(this, text_schedule_6, true);
                }
                if (repeatDayString.contains("6")) {
                    Common.setBackground(this, text_schedule_7, true);
                }
                repeatDayString = "";

                int typeId = rg_schedule_type.getCheckedRadioButtonId();

                if (isSelectMode) {
                    rb_schedule_type_mood.setEnabled(false);
                    setBackGroundColorButton(true);

                } else {
                    rb_schedule_type_room.setEnabled(false);
                    setBackGroundColorButton(false);
                }

                rb_schedule_select_auto.setClickable(false);
                rb_schedule_select_schedule.setClickable(false);

                if (scheduleVO.getIs_timer() == 1) {

                    rb_schedule_select_auto.setChecked(true);
                    rb_schedule_select_schedule.setEnabled(false);

                    String on_after = scheduleVO.getTimer_on_after();
                    if (!TextUtils.isEmpty(on_after)) {

                        if (on_after.split(":").length > 0) {
                            int hOn = Integer.parseInt(on_after.split(":")[0]);
                            int mOn = Integer.parseInt(on_after.split(":")[1]);
                            et_on_time_hours.setText(ActivityHelper.hmZero(hOn));
                            et_on_time_min.setText(ActivityHelper.hmZero(mOn));
                        } else {
                            et_on_time_hours.setText("");
                            et_on_time_min.setText("");
                        }
                    }

                    if (!TextUtils.isEmpty(scheduleVO.getTimer_on_date())) {
                        et_on_time_bottom_header.setText(scheduleVO.getTimer_on_date());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.HOUR, Integer.parseInt(on_after.split(":")[0]));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(on_after.split(":")[1]));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                        String formattedDate = dateFormat.format(calendar.getTime()).toString();

                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                        Date sendDate = null;
                        try {
                            sendDate = format.parse(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar sendCalendar = Calendar.getInstance();
                        sendCalendar.setTime(sendDate);

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_on_hour_ampm_12 = on_after;

                        et_on_time_bottom_header_at_time.setText(hourMinute);
                    }

                    String off_after = scheduleVO.getTimer_off_after();

                    if (!TextUtils.isEmpty(off_after) && !off_after.equalsIgnoreCase("0:0")) {

                        if (off_after.split(":").length > 0) {

                            int hFn = Integer.parseInt(off_after.split(":")[0]);
                            int mFn = Integer.parseInt(off_after.split(":")[1]);

                            et_off_time_hours.setText(ActivityHelper.hmZero(hFn));
                            et_off_time_min.setText(ActivityHelper.hmZero(mFn));

                        } else {
                            et_off_time_hours.setText("");
                            et_off_time_min.setText("");
                        }
                    }

                    if (!TextUtils.isEmpty(scheduleVO.getTimer_off_date())) {

                        et_off_time_bottom_header.setText(scheduleVO.getTimer_off_date());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.HOUR, Integer.parseInt(off_after.split(":")[0]));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(off_after.split(":")[1]));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                        String formattedDate = dateFormat.format(calendar.getTime()).toString();

                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                        Date sendDate = null;
                        try {
                            sendDate = format.parse(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar sendCalendar = Calendar.getInstance();
                        sendCalendar.setTime(sendDate);

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_off_hour_ampm_12 = off_after;

                        et_off_time_bottom_header_at_time.setText(hourMinute);
                    }


                    Date currentDate = new Date();

                    off_time_date = scheduleVO.getTimer_off_date();
                    off_at_time = scheduleVO.getSchedule_device_off_time();
                    /*
                     * start
                     * */

                    String hours = "0";
                    if (!TextUtils.isEmpty(et_on_time_hours.getText().toString())) {
                        hours = et_on_time_hours.getText().toString().trim();
                    }

                    String minutes = "0";
                    if (!TextUtils.isEmpty(et_on_time_min.getText().toString())) {
                        minutes = et_on_time_min.getText().toString().trim();
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(currentDate);
                        calendar.add(Calendar.HOUR, Integer.parseInt(hours));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(minutes));

                        //only date
                        //  String outputPattern2 = "MMM dd, yyyy";//:ss
                        String outputPattern2 = Constants.SIMPLE_DATE_FORMAT_1;
                        SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
                        String finalJustDate = outputFormat2.format(calendar.getTime());

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

                        on_time_date = finalJustDate;
                        on_at_time = hourMinute;

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_on_time_bottom_header.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(scheduleVO.getSchedule_device_on_time())) {

                            String ampmOn_Time = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                            final String time = scheduleVO.getSchedule_device_on_time();
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(time);

                                hourMinute = new SimpleDateFormat("K:mm").format(dateObj);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateObj);
                                et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }

                            setEtOnTimeHeader(finalJustDate, hourMinute, ampmOn_Time);
                        } else {
                            setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
                        }
                    }

                    String hoursO = et_off_time_hours.getText().toString().trim();
                    String minutesO = et_off_time_min.getText().toString().trim();

                    if (!TextUtils.isEmpty(hoursO) && !TextUtils.isEmpty(minutesO)) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(currentDate);
                        calendar.add(Calendar.HOUR, Integer.parseInt(hoursO));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(minutesO));

                        //only date
                        //  String outputPattern2 = "MMM dd, yyyy";//:ss
                        String outputPattern2 = Constants.SIMPLE_DATE_FORMAT_1;
                        SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
                        String finalJustDate = outputFormat2.format(calendar.getTime());

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        off_time_date = finalJustDate;
                        off_at_time = hourMinute;

                        et_off_time_bottom_header.setVisibility(View.VISIBLE);

                        setEtOffTimeHeader(finalJustDate, hourMinute, ampm);

                        if (!TextUtils.isEmpty(scheduleVO.getSchedule_device_off_time())) {

                            String ampmOff_Time = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                            final String time = scheduleVO.getSchedule_device_off_time();
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf.parse(time);

                                hourMinute = new SimpleDateFormat("K:mm").format(dateObj);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateObj);
                                et_off_hour_ampm_12 = ActivityHelper.hourMinuteZero(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }

                            setEtOffTimeHeader(finalJustDate, hourMinute, ampmOff_Time);
                        } else {
                            setEtOffTimeHeader(finalJustDate, hourMinute, ampm);
                        }
                    }
                    /*end*/

                } else {
                    rb_schedule_select_auto.setEnabled(false);
                    rb_schedule_select_schedule.setChecked(true);
                }
                btnRoomSchedule.setClickable(false);
                btnMoodSchedule.setClickable(false);
            }

            int rId = rg_schedule_select.getCheckedRadioButtonId();
            if (rId == R.id.rb_schedule_select_schedule) {
                showScheduleUI();
                rb_schedule_select_auto.setVisibility(View.GONE);
            } else if (rId == R.id.rb_schedule_select_timer) {
                showTimerUI();
                rb_schedule_select_schedule.setVisibility(View.GONE);
            }


            final Date currentDate = new Date();
            et_on_time_hours.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_on_time_hours.getText().toString().trim();
                    String minutes = et_on_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(minutes)) {
                        minutes = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));
                        String finalJustDate = Common.getConvertDateString(calendar);

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        on_time_date = finalJustDate;
                        on_at_time = hourMinute;


                        et_on_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
                    }

                    if (et_on_time_hours.getText().toString().length() == 2) {
                        et_on_time_min.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            et_on_time_min.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_on_time_hours.getText().toString().trim();
                    String minutes = et_on_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(hours)) {
                        hours = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);

                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        on_time_date = finalJustDate;
                        on_at_time = hourMinute;

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));
                        et_on_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

            et_off_time_hours.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_off_time_hours.getText().toString().trim();
                    String minutes = et_off_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(minutes)) {
                        minutes = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);
                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_off_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        off_time_date = finalJustDate;
                        off_at_time = hourMinute;

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_off_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOffTimeHeader(finalJustDate, hourMinute, ampm);
                    }
                    if (et_off_time_hours.getText().toString().length() == 2) {
                        et_off_time_min.requestFocus();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });


            et_off_time_min.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_off_time_hours.getText().toString().trim();
                    String minutes = et_off_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(hours)) {
                        hours = "00";
                    }
                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_off_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        off_time_date = finalJustDate;
                        off_at_time = hourMinute;

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_off_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOffTimeHeader(finalJustDate, hourMinute, ampm);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            //   String outputPattern2 = "MMM dd, yyyy";//:ss
            String outputPattern2 = Constants.SIMPLE_DATE_FORMAT_1;
            SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
            String finalJustDate = outputFormat2.format(new Date().getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            if (TextUtils.isEmpty(et_on_hour_ampm_12)) {
                et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            }


            String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

            if (TextUtils.isEmpty(off_time_date)) {
                setEtOffTimeHeader(finalJustDate, hourMinute, ampm);
            }
            if (TextUtils.isEmpty(on_time_date)) {
                setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
            }

            sp_mood_selection.setEnabled(false);


        } else {

            /*
             * isActivityType > Room & mood fragment getting not empty isActivityType
             * isEDit > edit schedule
             * Roomid : means roomFragment to redrection
             * moodid : means MoodFragment
             * */

            if (!TextUtils.isEmpty(isActivityType) && !isEdit) {
                if (!TextUtils.isEmpty(roomId) || !TextUtils.isEmpty(moodId)) {
                    btnRoomSchedule.setClickable(false);
                    btnMoodSchedule.setClickable(false);
                } else {
                    btnRoomSchedule.setClickable(true);
                    btnMoodSchedule.setClickable(true);
                }

            } else {
                btnRoomSchedule.setClickable(true);
                btnMoodSchedule.setClickable(true);
            }

            rb_schedule_select_schedule.setChecked(true);
            Date d = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            String dayOfWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

            if (dayOfWeek.contains("1")) {
                Common.setBackground(this, text_schedule_1, true);
            } else if (dayOfWeek.contains("2")) {
                Common.setBackground(this, text_schedule_2, true);
            } else if (dayOfWeek.contains("3")) {
                Common.setBackground(this, text_schedule_3, true);
            } else if (dayOfWeek.contains("4")) {
                Common.setBackground(this, text_schedule_4, true);
            } else if (dayOfWeek.contains("5")) {
                Common.setBackground(this, text_schedule_5, true);
            } else if (dayOfWeek.contains("6")) {
                Common.setBackground(this, text_schedule_6, true);
            } else if (dayOfWeek.contains("7")) {
                Common.setBackground(this, text_schedule_7, true);
            }

            if (scheduleVO == null) {
                scheduleVO = new ScheduleVO();
            }
            if (isScheduleClick) {
                if (selection == 1) {
                    rb_schedule_type_room.setChecked(true);
                    setBackGroundColorButton(true);
                } else if (selection == 2) {
                    rb_schedule_type_mood.setChecked(true);
                    setBackGroundColorButton(false);
                }
            } else {
                //add default room checked
                rb_schedule_type_room.setChecked(true);
                setBackGroundColorButton(true);
            }

            int rId = rg_schedule_select.getCheckedRadioButtonId();
            if (rId == R.id.rb_schedule_select_schedule) {
                showScheduleUI();
            } else if (rId == R.id.rb_schedule_select_timer) {
                showTimerUI();
            }


            rg_schedule_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rb_schedule_select_schedule) {
                        showScheduleUI();
                    } else if (checkedId == R.id.rb_schedule_select_timer) {
                        showTimerUI();
                    }
                }
            });

            final Date currentDate = new Date();

            et_on_time_hours.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_on_time_hours.getText().toString().trim();
                    String minutes = et_on_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(minutes)) {
                        minutes = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        on_time_date = finalJustDate;
                        on_at_time = hourMinute;

                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_on_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
                    }

                    if (et_on_time_hours.getText().toString().length() == 2) {
                        et_on_time_min.requestFocus();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            et_on_time_min.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_on_time_hours.getText().toString().trim();
                    String minutes = et_on_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(hours)) {
                        hours = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        on_time_date = finalJustDate;
                        on_at_time = hourMinute;
                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_on_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOnTimeHeader(finalJustDate, hourMinute, ampm);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

            //  String outputPattern2 = "MMM dd, yyyy";//:ss
            String outputPattern2 = Constants.SIMPLE_DATE_FORMAT_1;
            SimpleDateFormat outputFormat2 = new SimpleDateFormat(outputPattern2);
            String finalJustDate = outputFormat2.format(new Date().getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            et_on_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            if (!isEdit) {
                String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                setEtOnTimeHeader(finalJustDate, hourMinute, ampm);
            }


            //for select off_time

            et_off_time_hours.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_off_time_hours.getText().toString().trim();
                    String minutes = et_off_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(minutes)) {
                        minutes = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_off_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        off_time_date = finalJustDate;
                        off_at_time = hourMinute;
                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_off_time_bottom_header.setVisibility(View.VISIBLE);

                        setEtOffTimeHeader(finalJustDate, hourMinute, ampm);
                    }
                    if (et_off_time_hours.getText().toString().length() == 2) {
                        et_off_time_min.requestFocus();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            et_off_time_min.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String hours = et_off_time_hours.getText().toString().trim();
                    String minutes = et_off_time_min.getText().toString().trim();

                    if (TextUtils.isEmpty(hours)) {
                        hours = "00";
                    }

                    if (!TextUtils.isEmpty(hours) && !TextUtils.isEmpty(minutes)) {

                        Calendar calendar = Common.getCalendarHourMinute(currentDate, hours, minutes);

                        String finalJustDate = Common.getConvertDateString(calendar);
                        String hourMinute = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                        et_off_hour_ampm_12 = ActivityHelper.hourMinuteZero(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                        off_time_date = finalJustDate;
                        off_at_time = hourMinute;
                        String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));

                        et_off_time_bottom_header.setVisibility(View.VISIBLE);
                        setEtOffTimeHeader(finalJustDate, hourMinute, ampm);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            if (!isEdit) {
                String ampm = DateUtils.getAMPMString(calendar.get(Calendar.AM_PM));
                setEtOffTimeHeader(finalJustDate, hourMinute, ampm);
            }

        }

        //select radio button label

        //hide spinner if scheduler on edit mode
        sp_schedule_list.setVisibility(View.INVISIBLE);
        tv_schedule_list.setVisibility(View.INVISIBLE);
        ll_spinner_hide.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

    }


    /*
     * set button background
     */

    public void setBackGroundColorButton(boolean isFlag) {
        if (isFlag) {
            isSelectMode = true;
            btnRoomSchedule.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnRoomSchedule.setTextColor(getResources().getColor(R.color.automation_white));
            btnMoodSchedule.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnMoodSchedule.setTextColor(getResources().getColor(R.color.txtPanal));
        } else {
            isSelectMode = false;
            btnMoodSchedule.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnMoodSchedule.setTextColor(getResources().getColor(R.color.automation_white));
            btnRoomSchedule.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnRoomSchedule.setTextColor(getResources().getColor(R.color.txtPanal));
        }

    }

    public String et_on_hour_ampm_12 = "";
    public String et_off_hour_ampm_12 = "";

    /**
     * set Label data at and am/pm
     *
     * @param _finalJustDate: Mar 07, 2018
     * @param _hourMinute     : 15:35
     * @param _ampm           : PM
     */

    private void setEtOnTimeHeader(String _finalJustDate, String _hourMinute, String _ampm) {

        et_on_time_bottom_header.setText(_finalJustDate);
        et_on_time_bottom_header_at_time.setText(_hourMinute);
        et_on_time_bottom_header_at_ampm.setText(_ampm);

        startCheckDate = _finalJustDate + " " + _hourMinute + _ampm;

    }

    private void setEtOffTimeHeader(String _finalJustDate, String _hourMinute, String _ampm) {

        et_off_time_bottom_header.setText(_finalJustDate);
        et_off_time_bottom_header_at_time.setText(_hourMinute);
        et_off_time_bottom_header_at_ampm.setText(_ampm);

        endCheckDate = _finalJustDate + " " + _hourMinute + _ampm;
    }


    //if select schedule radio button display sch ui and invisible timer ui
    private void showScheduleUI() {

        ll_on_time_auto.setVisibility(View.GONE);
        ll_on_time_bottom.setVisibility(View.GONE);
        ll_off_time_auto.setVisibility(View.GONE);
        ll_off_time_bottom.setVisibility(View.GONE);
        imgArrow.setVisibility(View.VISIBLE);
        imgArraoTime.setVisibility(View.GONE);


        ll_on_time.setVisibility(View.VISIBLE);
        ll_off_time.setVisibility(View.VISIBLE);
        ll_week_title.setVisibility(View.VISIBLE);
        ll_week_days.setVisibility(View.VISIBLE);
    }

    private void showTimerUI() {

        ll_on_time.setVisibility(View.GONE);
        ll_off_time.setVisibility(View.GONE);
        ll_week_title.setVisibility(View.GONE);
        ll_week_days.setVisibility(View.GONE);
        imgArrow.setVisibility(View.GONE);

        //visible auto layour
        imgArraoTime.setVisibility(View.VISIBLE);
        ll_on_time_auto.setVisibility(View.VISIBLE);
        ll_on_time_bottom.setVisibility(View.VISIBLE);
        ll_off_time_auto.setVisibility(View.VISIBLE);
        ll_off_time_bottom.setVisibility(View.VISIBLE);
    }


    String repeatDayString = "";

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.et_schedule_on_time) {
            ActivityHelper.hideKeyboard(this);
            DialogFragment fromTimeFragment = new TimePickerFragment(ScheduleActivity.this, et_schedule_on_time.getText().toString(), new ICallback() {
                @Override
                public void onSuccess(String str) {
                    et_schedule_on_time.setText(str);

                }
            });
            //TODO <code></code>
            if (!fromTimeFragment.isVisible()) {
                fromTimeFragment.show(ScheduleActivity.this.getFragmentManager(), "timePicker");
            }
        } else if (id == R.id.et_schedule_off_time) {
            ActivityHelper.hideKeyboard(this);
            DialogFragment fromTimeFragment = new TimePickerFragment(ScheduleActivity.this, et_schedule_off_time.getText().toString(), new ICallback() {
                @Override
                public void onSuccess(String str) {
                    et_schedule_off_time.setText(str);

                }
            });
            if (!fromTimeFragment.isVisible()) {
                fromTimeFragment.show(ScheduleActivity.this.getFragmentManager(), "timePicker");
            }
        } else if (id == R.id.iv_schedule_on_time_clear) {
            ActivityHelper.hideKeyboard(this);
            et_schedule_on_time.setText("");
        } else if (id == R.id.iv_schedule_off_time_clear) {
            ActivityHelper.hideKeyboard(this);
            et_schedule_off_time.setText("");
        } else if ((id == R.id.text_schedule_1) || (id == R.id.text_schedule_2) || (id == R.id.text_schedule_3) || (id == R.id.text_schedule_4) || (id == R.id.text_schedule_5) || (id == R.id.text_schedule_6) || (id == R.id.text_schedule_7)) {
            Common.setOnOffBackground(this,  findViewById(id));

        } else if (id == R.id.btnRoomSchedule) {
            setBackGroundColorButton(true);
            isMoodAdapter = false;
            ll_spinner_mood.setVisibility(View.GONE);
            getDeviceList();
        } else if (id == R.id.btnMoodSchedule) {
            setBackGroundColorButton(false);
            isMoodAdapter = true;
            ll_spinner_mood.setVisibility(View.VISIBLE);
            getMoodListAdd();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            if (rg_schedule_type.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Select Schedule For", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(et_schedule_name.getText().toString())) {
                ////
                String errorMsg = "";
                if (rg_schedule_select.getCheckedRadioButtonId() == R.id.rb_schedule_select_schedule) {
                    errorMsg = "Enter Schedule name";

                } else if (rg_schedule_select.getCheckedRadioButtonId() == R.id.rb_schedule_select_timer) {
                    errorMsg = "Add Timer Name";
                }

                ////
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                return false;
            }
            //select validation
            int r_id_select = rg_schedule_select.getCheckedRadioButtonId();

            if (r_id_select == R.id.rb_schedule_select_timer) {

                if (TextUtils.isEmpty(et_on_time_hours.getText().toString()) && TextUtils.isEmpty(et_off_time_hours.getText().toString())) {

                    if (TextUtils.isEmpty(et_on_time_hours.getText().toString()) && !TextUtils.isEmpty(et_on_time_min.getText().toString())) {

                    } else {

                        if (TextUtils.isEmpty(et_off_time_hours.getText().toString()) && !TextUtils.isEmpty(et_off_time_min.getText().toString())) {

                        } else {
                            Toast.makeText(this, "Input On or Off Time Hours", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }

                if (et_on_time_hours.getText().toString().length() == 0 && !TextUtils.isEmpty(et_on_time_min.getText().toString())) {
                    et_on_time_hours.setText("00");
                }
                if (et_on_time_min.getText().toString().length() == 0 && !TextUtils.isEmpty(et_on_time_hours.getText().toString())) {
                    et_on_time_min.setText("00");
                }

                if (et_off_time_hours.getText().toString().length() == 0 && !TextUtils.isEmpty(et_off_time_min.getText().toString())) {
                    et_off_time_hours.setText("00");
                }
                if (et_off_time_min.getText().toString().length() == 0 && !TextUtils.isEmpty(et_off_time_hours.getText().toString())) {
                    et_off_time_min.setText("00");
                }

                int rId = rg_schedule_select.getCheckedRadioButtonId();

                int on_hour = 0;

                if (!TextUtils.isEmpty(et_on_time_hours.getText().toString())) {
                    on_hour = Integer.parseInt(et_on_time_hours.getText().toString().trim());
                }

                int on_min = 0;
                if (!TextUtils.isEmpty(et_on_time_min.getText().toString())) {
                    on_min = Integer.parseInt(et_on_time_min.getText().toString().trim());
                }


                int off_hour = 0;
                if (!TextUtils.isEmpty(et_off_time_hours.getText().toString())) {
                    off_hour = Integer.parseInt(et_off_time_hours.getText().toString().trim());
                }

                int off_min = 0;
                if (!TextUtils.isEmpty(et_off_time_min.getText().toString())) {
                    off_min = Integer.parseInt(et_off_time_min.getText().toString().trim());
                }

                if (on_hour == off_hour && on_min == off_min) {
                    Toast.makeText(this, "Input diffrent On and Off Time", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {

                if (TextUtils.isEmpty(et_schedule_on_time.getText().toString()) && TextUtils.isEmpty(et_schedule_off_time.getText().toString())) {
                    Toast.makeText(this, "Select On or Off Schedule time", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!TextUtils.isEmpty(et_schedule_on_time.getText().toString()) && !TextUtils.isEmpty(et_schedule_off_time.getText().toString()) &&
                        et_schedule_on_time.getText().toString().equalsIgnoreCase(et_schedule_off_time.getText().toString())) {
                    Toast.makeText(this, "Select different On and Off Schedule time", Toast.LENGTH_SHORT).show();
                    return false;
                }
                getRepeatString();
            }

            if (TextUtils.isEmpty(deviceListLayoutHelper.getSelectedItemIds()) && deviceListLayoutHelper != null) {
                Toast.makeText(this, "Select atleast one or more devices", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {

                deviceObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                deviceObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

                if (isEdit) {
                    deviceObj.put("schedule_id", scheduleVO.getSchedule_id());
                    deviceObj.put("schedule_status", scheduleVO.getSchedule_status()); //added
                }


                deviceObj.put("schedule_type", isSelectMode ? 0 : 1);
                deviceObj.put("schedule_name", et_schedule_name.getText().toString());

                String onTime = "", offTime = "";
                if (!TextUtils.isEmpty(et_schedule_on_time.getText().toString())) {
                    onTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_on_time.getText().toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                }
                if (!TextUtils.isEmpty(et_schedule_off_time.getText().toString())) {
                    offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_off_time.getText().toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                }

                List<String> mRoomIdList = new ArrayList<>();
                List<String> roomName = new ArrayList<>();

                List<DeviceVO> deviceVOArrayList = new ArrayList<>();

                deviceVOArrayList = removeDuplicates(deviceListLayoutHelper.getSelectedItemList());
                if (deviceVOArrayList.size() == 0) {
                    Toast.makeText(this, "Select atleast one or more devices", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String ss = "";
                JSONArray array = new JSONArray();

                JSONArray roomDeviceArray = new JSONArray();

                for (DeviceVO dPanel : deviceVOArrayList) {

                    JSONObject object = new JSONObject();
                    mRoomIdList.add("" + dPanel.getRoomId());
                    roomName.add("" + dPanel.getRoomName());

                    if (dPanel.getSensor_type() != null && dPanel.getSensor_type().equalsIgnoreCase("remote")) {
                        object.put("module_id", dPanel.getModuleId());
                        object.put("room_device_id", "" + dPanel.getRoomDeviceId());
                        object.put("sensor_icon", "" + dPanel.getSensor_icon());
                        object.put("is_active", "" + dPanel.getIsActive());
                        object.put("device_type", "" + dPanel.getDeviceType());
                        object.put("original_room_device_id", "" + dPanel.getOriginal_room_device_id());
                        object.put("sensor_id", "" + dPanel.getSensor_id());
                        object.put("sensor_type", "" + dPanel.getSensor_type());
                        object.put("device_id", "" + dPanel.getDeviceId());
                        object.put("ir_blaster_id", "" + dPanel.getIr_blaster_id());
                        object.put("sensor_name", "" + dPanel.getSensor_name());
                        object.put("is_original", "" + dPanel.getIs_original());
                        object.put("device_status", "" + dPanel.getDeviceStatus());
                        object.put("device_specific_value", "" + dPanel.getDeviceSpecificValue());
                        object.put("device_type", "" + dPanel.getDeviceType());
                        object.put("remote_status", "" + dPanel.getRemote_status());
                        object.put("panel_id", "" + dPanel.getPanel_id());
                        object.put("remote_device_id", "" + dPanel.getRemote_device_id());
                        object.put("ir_blaster_id", "" + dPanel.getIr_blaster_id());

                    } else {
                        object.put("device_icon", dPanel.getDevice_icon());
                        object.put("room_device_id", "" + dPanel.getRoomDeviceId());
                        object.put("module_id", "" + dPanel.getModuleId());
                        object.put("device_id", "" + dPanel.getDeviceId());

                        //added

                        object.put("device_name", dPanel.getDeviceName());
                        object.put("device_status", dPanel.getDeviceStatus());
                        object.put("device_specific_value", dPanel.getDeviceSpecificValue());
                        object.put("device_type", dPanel.getDeviceType());

                        object.put("original_room_device_id", dPanel.getOriginal_room_device_id());
                        object.put("is_active", dPanel.getIsActive());
                        object.put("is_alive", dPanel.getIsAlive());
                        object.put("is_original", dPanel.getIs_original());

                    }


                    array.put(object);
                    roomDeviceArray.put(dPanel.getRoomDeviceId());
                }
                //room_devices
                if (isEdit) {
                    deviceObj.put("room_devices", roomDeviceArray);
                }
                deviceObj.put("deviceList", array);
                if (isSelectMode) {

                    RoomVO room = (RoomVO) sp_schedule_list.getSelectedItem();
                    if (sp_schedule_list.getSelectedItem() != null) {
                        deviceObj.put("room_id", room.getRoomId());

                    } else {
                        //Remove Duplicate room id from mRoomIdList List
                        ArrayList<String> resultRoomId = new ArrayList<>();
                        ArrayList<String> resultRoomName = new ArrayList<>();

                        HashSet<String> set = new HashSet<>();
                        HashSet<String> setName = new HashSet<>();

                        for (String roomId : mRoomIdList) {
                            if (!set.contains(roomId)) {
                                resultRoomId.add(roomId);
                                set.add(roomId);
                            }
                        }

                        for (String roomId : roomName) {
                            if (!setName.contains(roomId)) {
                                resultRoomName.add(roomId);
                                setName.add(roomId);
                            }
                        }

                        JSONArray jsonArray = new JSONArray();

                        for (int i = 0; i < resultRoomId.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("room_id", resultRoomId.get(i));
                            jsonObject.put("room_name", resultRoomName.get(i));

                            jsonArray.put(jsonObject);
                        }

                        //deviceObj.put("room_id", "");
                        String collectionRoomId = "";
                        for (String mRoomId : resultRoomId) {
                            collectionRoomId += mRoomId + ",";
                        }
                        //deviceObj.put("room_id_is", ""+collectionRoomId.substring(0, collectionRoomId.lastIndexOf(",")));
                        deviceObj.put("room_id", jsonArray);
                    }
                    if (!TextUtils.isEmpty(roomId)) {
                        ArrayList<String> resultRoomId = new ArrayList<>();
                        ArrayList<String> resultRoomName = new ArrayList<>();

                        HashSet<String> set = new HashSet<>();
                        HashSet<String> setName = new HashSet<>();

                        for (String roomId : mRoomIdList) {
                            if (!set.contains(roomId)) {
                                resultRoomId.add(roomId);
                                set.add(roomId);
                            }
                        }

                        for (String roomId : roomName) {
                            if (!setName.contains(roomId)) {
                                resultRoomName.add(roomId);
                                setName.add(roomId);
                            }
                        }

                        JSONArray jsonArray = new JSONArray();

                        for (int i = 0; i < resultRoomId.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("room_id", resultRoomId.get(i));
                            jsonObject.put("room_name", resultRoomName.get(i));

                            jsonArray.put(jsonObject);
                        }


                        String collectionRoomId = "";
                        for (String mRoomId : resultRoomId) {
                            collectionRoomId += mRoomId + ",";
                        }
                        deviceObj.put("room_id", jsonArray);
                    }

                } else {
                    int mood_selected_id = sp_mood_selection.getSelectedItemPosition();
                    String mood_string_id = moodListSpinner.get(mood_selected_id).getRoomId();

                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("room_id", mood_string_id);
                    jsonObject.put("room_name", moodListSpinner.get(mood_selected_id).getRoomName());
                    jsonArray.put(jsonObject);

                    deviceObj.put("room_id", jsonArray);
                }
                deviceObj.put("is_duplicate", 0);
                deviceObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

                //add after select

                if (r_id_select == R.id.rb_schedule_select_timer) {

                    deviceObj.put("schedule_device_day", "");

                    deviceObj.put("is_timer", 1);

                    if (!TextUtils.isEmpty(et_on_time_hours.getText().toString()) && TextUtils.isEmpty(et_on_time_min.getText().toString())) {
                        et_on_time_min.setText("00");
                    }
                    if (TextUtils.isEmpty(et_on_time_hours.getText().toString()) && !TextUtils.isEmpty(et_on_time_min.getText().toString())) {
                        et_on_time_hours.setText("00");
                    }

                    if (!TextUtils.isEmpty(et_on_time_hours.getText().toString()) && !TextUtils.isEmpty(et_on_time_min.getText().toString())) {

                        int hOn = Integer.parseInt(et_on_time_hours.getText().toString());
                        int mOn = Integer.parseInt(et_on_time_min.getText().toString());

                        if (hOn == 0 && mOn == 0) {
                            deviceObj.put("schedule_device_on_time", "");
                            deviceObj.put("timer_on_after", "");
                            deviceObj.put("timer_on_date", "");
                        } else {
                            if (!TextUtils.isEmpty(et_on_time_bottom_header_at_time.getText().toString())) {

                                deviceObj.put("schedule_device_on_time", et_on_hour_ampm_12);
                            } else {
                                deviceObj.put("schedule_device_on_time", on_at_time);
                            }
                            deviceObj.put("timer_on_after", ActivityHelper.hourMinuteZero(hOn, mOn));
                            deviceObj.put("timer_on_date", on_time_date);
                        }
                    } else {
                        deviceObj.put("schedule_device_on_time", "");
                        deviceObj.put("timer_on_after", "");
                        deviceObj.put("timer_on_date", "");
                    }


                    if (!TextUtils.isEmpty(et_off_time_hours.getText().toString()) && TextUtils.isEmpty(et_off_time_min.getText().toString())) {
                        et_off_time_min.setText("00");
                    }
                    if (TextUtils.isEmpty(et_off_time_hours.getText().toString()) && !TextUtils.isEmpty(et_off_time_min.getText().toString())) {
                        et_off_time_hours.setText("00");
                    }

                    if (!TextUtils.isEmpty(et_off_time_hours.getText().toString()) && !TextUtils.isEmpty(et_off_time_min.getText().toString())) {

                        int hFn = Integer.parseInt(et_off_time_hours.getText().toString());
                        int mFn = Integer.parseInt(et_off_time_min.getText().toString());

                        if (hFn == 0 && mFn == 0) {
                            deviceObj.put("schedule_device_off_time", "");
                            deviceObj.put("timer_off_after", "");
                            deviceObj.put("timer_off_date", "");
                        } else {

                            if (!TextUtils.isEmpty(et_off_time_bottom_header_at_time.getText().toString())) {
                                deviceObj.put("schedule_device_off_time", et_off_hour_ampm_12);
                            } else {
                                deviceObj.put("schedule_device_off_time", off_at_time);
                            }
                            deviceObj.put("timer_off_after", ActivityHelper.hourMinuteZero(hFn, mFn));
                            deviceObj.put("timer_off_date", off_time_date);
                        }
                    } else {
                        deviceObj.put("schedule_device_off_time", "");
                        deviceObj.put("timer_off_after", "");
                        deviceObj.put("timer_off_date", "");
                    }

                } else {

                    deviceObj.put("schedule_device_day", repeatDayString);

                    deviceObj.put("schedule_device_on_time", onTime);
                    deviceObj.put("schedule_device_off_time", offTime);

                    deviceObj.put("is_timer", 0);
                    deviceObj.put("timer_on_after", "");
                    deviceObj.put("timer_on_date", "");
                    deviceObj.put("timer_off_after", "");
                    deviceObj.put("timer_off_date", "");
                }


                addSchedule();


            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /// all webservice call below.
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        roomListAdd = new ArrayList<>();
        ChatApplication app = (ChatApplication) getApplication();
        String webUrl = app.url;

        //  String url = webUrl + Constants.GET_DEVICES_LIST + "/"+Constants.DEVICE_TOKEN + "/0/0"; //0/1
        String url = webUrl + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 0);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            if (TextUtils.isEmpty(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", "");
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    roomListAdd = new ArrayList<>();
                    roomListAdd.clear();
                    roomList.clear();
                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomListAdd = JsonHelper.parseRoomArray(roomArray, false);

                    roomList = JsonHelper.parseRoomArray(roomArray, false);

                    if (isEdit || isEditOpen) {

                        int typeId = rg_schedule_type.getCheckedRadioButtonId();
                        if (isSelectMode) {
                            rb_schedule_type_mood.setEnabled(false);
                            setBackGroundColorButton(true);
                        } else {
                            rb_schedule_type_room.setEnabled(false);
                            setBackGroundColorButton(false);
                        }

                        rb_schedule_type_mood.setClickable(false);
                        rb_schedule_type_room.setClickable(false);

                        sp_schedule_list.setEnabled(false);


                    }
                    setData(roomListAdd, 2, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

                if (roomListAdd.size() == 0) {
                    empty_ll_view.setVisibility(View.VISIBLE);
                    txt_empty_sch.setText("No Room Found");
                    rv_schedule.setVisibility(View.GONE);
                } else {
                    empty_ll_view.setVisibility(View.GONE);
                    rv_schedule.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("getDeviceList onFailure " + error);
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                if (roomListAdd.size() == 0) {
                    empty_ll_view.setVisibility(View.VISIBLE);
                    txt_empty_sch.setText("No Room Found");
                    rv_schedule.setVisibility(View.GONE);
                } else {
                    empty_ll_view.setVisibility(View.GONE);
                    rv_schedule.setVisibility(View.VISIBLE);
                }
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    ArrayAdapter spinnerArrayAdapter;

    //getMoodList
    public void getMoodListAdd() {
        ChatApplication.logDisplay("getMoodList");

        //  String url = webUrl + Constants.GET_DEVICES_LIST + "/"+Constants.DEVICE_TOKEN + "/1/0";
        String url = webUrl + Constants.GET_DEVICES_LIST;
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 1);
            jsonObject.put("is_sensor_panel", 0);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            if (TextUtils.isEmpty(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", "");
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(ScheduleActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay(" getMoodList onSuccess " + result.toString());
                try {
                    moodListAdd = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    moodListAdd = JsonHelper.parseRoomArray(roomArray, false);

                    // sortMoodList(moodListAdd);
                    moodListSpinner.clear();
                    moodListSpinner = moodListAdd;

                    if (moodListSpinner.size() == 0) {

                        ArrayList<RoomVO> moodListSpinnerEmpty = new ArrayList<>();

                        RoomVO room = new RoomVO();
                        room.setRoomId("select");
                        room.setRoomName("-- Select --");
                        moodListSpinnerEmpty.add(0, room);

                        spinnerArrayAdapter = new ArrayAdapter(ScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, moodListSpinnerEmpty);
                        sp_mood_selection.setAdapter(spinnerArrayAdapter);

                    } else {
                        spinnerArrayAdapter = new ArrayAdapter(ScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, moodListSpinner);
                        sp_mood_selection.setAdapter(spinnerArrayAdapter);
                    }


                    //if isEdit true in Scheduer fragment
                    if (isEdit && !TextUtils.isEmpty(scheduleVO.getRoom_id())) {
                        for (int i = 0; i < moodListSpinner.size(); i++) {
                            if (moodListSpinner.get(i).getRoomId().equalsIgnoreCase(scheduleVO.getRoom_id())) {
                                sp_mood_selection.setSelection(i);
                                break;
                            }
                        }
                    }

                    //moodId if moodId coming to MoodFragment in scheduler add click event
                    if (!TextUtils.isEmpty(moodId) && !isEdit) {
                        sp_mood_selection.setEnabled(false);
                        for (int i = 0; i < moodListSpinner.size(); i++) {
                            if (moodListSpinner.get(i).getRoomId().equalsIgnoreCase(moodId)) {
                                sp_mood_selection.setSelection(i);
                                break;
                            }
                        }
                    }

                    //mood spinner

                    if (isEdit || isEditOpen) {

                        int typeId = rg_schedule_type.getCheckedRadioButtonId();
                        if (isSelectMode) {
                            rb_schedule_type_mood.setEnabled(false);
                            setBackGroundColorButton(true);
                        } else {
                            rb_schedule_type_room.setEnabled(false);
                            setBackGroundColorButton(false);
                        }

                        rb_schedule_type_mood.setClickable(false);
                        rb_schedule_type_room.setClickable(false);
                        sp_schedule_list.setEnabled(false);

                    }

                    ArrayList<RoomVO> tempList = new ArrayList<RoomVO>();

                    for (int i = 0; i < moodListAdd.size(); i++) { //i=1
                        RoomVO moodVo = moodListAdd.get(i);

                        int mPosition = sp_mood_selection.getSelectedItemPosition();
                        String moodSelectedId = moodListSpinner.get(mPosition).getRoomId();

                        if (moodSelectedId.equalsIgnoreCase(moodVo.getRoomId()) && !moodSelectedId.equalsIgnoreCase("select")) {

                            RoomVO roomVO = new RoomVO();

                            roomVO.setRoomId(moodVo.getRoomId());
                            roomVO.setRoomName(moodVo.getRoomName());
                            roomVO.setPanelList(moodVo.getPanelList());

                            for (PanelVO panelVO : roomVO.getPanelList()) {
                                for (DeviceVO deviceVO : panelVO.getDeviceList()) {
                                    roomVO.setExpanded(true);
                                    deviceVO.setSelected(false);
                                }
                            }

                            tempList.add(roomVO);
                        }
                    }

                    if (moodListAdd.size() > 0) {
                        setData(tempList, 1, false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

                if (moodListAdd.size() == 0) {
                    empty_ll_view.setVisibility(View.VISIBLE);
                    txt_empty_sch.setText("No Mood Found");
                    rv_schedule.setVisibility(View.GONE);
                } else {
                    empty_ll_view.setVisibility(View.GONE);
                    rv_schedule.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                if (moodListAdd.size() == 0) {
                    empty_ll_view.setVisibility(View.VISIBLE);
                    txt_empty_sch.setText("No Mood Found");
                    rv_schedule.setVisibility(View.GONE);
                } else {
                    empty_ll_view.setVisibility(View.GONE);
                    rv_schedule.setVisibility(View.VISIBLE);
                }
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();


    }

    int check = 0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<RoomVO> tempList = new ArrayList<RoomVO>();

        if (moodListAdd.size() > 0) {

            for (int i = 0; i < moodListAdd.size(); i++) { //i=1
                RoomVO moodVo = moodListAdd.get(i);

                int mPosition = sp_mood_selection.getSelectedItemPosition();
                String moodSelectedId = moodListSpinner.get(mPosition).getRoomId();

                if (moodSelectedId.equalsIgnoreCase(moodVo.getRoomId()) && !moodSelectedId.equalsIgnoreCase("select")) {

                    RoomVO roomVO = new RoomVO();

                    roomVO.setRoomId(moodVo.getRoomId());
                    roomVO.setRoomName(moodVo.getRoomName());
                    roomVO.setPanelList(moodVo.getPanelList());

                    for (PanelVO panelVO : roomVO.getPanelList()) {
                        for (DeviceVO deviceVO : panelVO.getDeviceList()) {
                            roomVO.setExpanded(true);
                            deviceVO.setSelected(false);
                        }
                    }

                    tempList.add(roomVO);
                }
            }
            setData(tempList, 1, false);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        nextFalse = 0;
        ActivityHelper.hideKeyboard(this);
        super.onBackPressed();
    }

    /*
     * Type : mood=1/room=2
     * */
    public void setData(ArrayList<RoomVO> roomArrayList, int type, boolean isAddOutDevice) {

        if (!TextUtils.isEmpty(scheduleVO.getRoom_device_id())/* && !isAddOutDevice*/) {

            String[] strArray = scheduleVO.getRoom_device_id().split(",");

            List<String> listDeviceIds = Arrays.asList(strArray);

            //for get device id and module id
            List<DeviceVO> tempDevice = new ArrayList<>();

            for (String sID : listDeviceIds) {


                for (int i = 0; i < roomArrayList.size(); i++) {
                    RoomVO roomVO = roomArrayList.get(i);

                    List<PanelVO> listPanel = roomVO.getPanelList();

                    for (int j = 0; j < listPanel.size(); j++) {
                        List<DeviceVO> deviceVOList = listPanel.get(j).getDeviceList();

                        for (int k = 0; k < deviceVOList.size(); k++) {

                            if (type == 1) {

                                if (deviceVOList.get(k).getDevice_icon().equalsIgnoreCase("Remote_AC")) {
                                    if (/*sID.equals(deviceVOList.get(k).getOriginal_room_device_id()) ||*/
                                            sID.equals(deviceVOList.get(k).getRoomDeviceId())) {
                                        tempDevice.add(deviceVOList.get(k));
                                    }
                                } else {
                                    if (sID.equals(deviceVOList.get(k).getRoomDeviceId())) {
                                        tempDevice.add(deviceVOList.get(k));
                                    }
                                }

                            } else {

                                if (sID.equals(deviceVOList.get(k).getRoomDeviceId())) {
                                    tempDevice.add(deviceVOList.get(k));
                                }
                            }

                        }
                    }

                }
            }
            //end


            //for find and expanded scheduler devices
            for (String sID : listDeviceIds) {

                for (int i = 0; i < roomArrayList.size(); i++) {
                    RoomVO roomVO = roomArrayList.get(i);

                    if (!isEdit) {
                        ArrayList<String> roomDeviceList = roomVO.getRoomDeviceId();

                        for (String deviceId : roomDeviceList) {
                            if (sID.equals(deviceId)) {
                                roomVO.setExpanded(true);
                            }
                        }
                    } else {
                        List<PanelVO> listPanel = roomVO.getPanelList();

                        for (int j = 0; j < listPanel.size(); j++) {
                            List<DeviceVO> deviceVOList = listPanel.get(j).getDeviceList();

                            for (int k = 0; k < deviceVOList.size(); k++) {


                                //1==mood 2==room
                                if (type == 1) {

                                    for (DeviceVO deviceVO : tempDevice) {

                                        ChatApplication.logDisplay("selected type == 1");

                                        if (deviceVO.getDevice_icon().equalsIgnoreCase("Remote_AC")) {

                                            ChatApplication.logDisplay("selected type == 1 Remote_AC if");

                                            if (deviceVO.getOriginal_room_device_id().equalsIgnoreCase(
                                                    deviceVOList.get(k).getOriginal_room_device_id()) || deviceVO.getRoomDeviceId().equalsIgnoreCase(
                                                    deviceVOList.get(k).getRoomDeviceId())) { //getRoomDeviceId

                                                deviceVOList.get(k).setSelected(true);
                                                roomVO.setExpanded(true);
                                            }

                                        } else {

                                            ChatApplication.logDisplay("selected type == 1 Remote_AC else");

                                            if (deviceVO.getDeviceId() == deviceVOList.get(k).getDeviceId() &&
                                                    deviceVO.getModuleId().equalsIgnoreCase(deviceVOList.get(k).getModuleId())) {

                                                String typesensor = "";
                                                if (!TextUtils.isEmpty(deviceVO.getSensor_type())) {
                                                    typesensor = deviceVO.getSensor_type();
                                                }
                                                if (typesensor.equalsIgnoreCase("temp") ||
                                                        typesensor.equalsIgnoreCase("door")) {
                                                    deviceVOList.get(k).setSelected(false);
                                                    roomVO.setExpanded(true);
                                                } else {
                                                    deviceVOList.get(k).setSelected(true);
                                                    roomVO.setExpanded(true);
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    if (deviceVOList.get(k).getSensor_type() != null &&
                                            deviceVOList.get(k).getSensor_type().equalsIgnoreCase("remote")) {

                                        ChatApplication.logDisplay("selected type == 2 remote if");

                                        if (sID.equals(deviceVOList.get(k).getRoomDeviceId())) {

                                            deviceVOList.get(k).setSelected(true);
                                            roomVO.setExpanded(true);
                                        }

                                    } else {

                                        ChatApplication.logDisplay("selected type == 2 remote else");


                                        if (sID.equals(deviceVOList.get(k).getRoomDeviceId())) {
                                            String typesensor = "";
                                            if (!TextUtils.isEmpty(deviceVOList.get(k).getSensor_type())) {
                                                typesensor = roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type();
                                            }
                                            if (typesensor.equalsIgnoreCase("temp") ||
                                                    typesensor.equalsIgnoreCase("door")) {
                                                deviceVOList.get(k).setSelected(false);
                                                roomVO.setExpanded(true);
                                            } else {
                                                deviceVOList.get(k).setSelected(true);
                                                roomVO.setExpanded(true);
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }

                }
            }

        }

        //if add cheduler default expanded devices list //for select all room deviceid
        if (isEditOpen && !TextUtils.isEmpty(roomId)) {

            ArrayList<RoomVO> roomListTemp = new ArrayList<>();

            for (int i = 0; i < roomArrayList.size(); i++) {
                RoomVO roomVO = roomArrayList.get(i);
                if (roomId.equals(roomVO.getRoomId())) {
                    roomVO.setExpanded(true);
                    roomListTemp.add(roomVO);
                }
            }
            //for sort order
            for (int i = 0; i < roomArrayList.size(); i++) {
                RoomVO roomVO = roomArrayList.get(i);
                if (!roomId.equals(roomVO.getRoomId())) {
                    roomListTemp.add(roomVO);
                }
            }

            for (int i = 0; i < roomArrayList.size(); i++) {
                for (int j = 0; j < roomArrayList.get(i).getDeviceList().size(); j++) {
                    String typesensor = "";
                    if (!TextUtils.isEmpty(roomArrayList.get(i).getDeviceList().get(j).getSensor_type())) {
                        typesensor = roomArrayList.get(i).getDeviceList().get(j).getSensor_type();
                    }
                    if (typesensor.equalsIgnoreCase("gas_sensor") ||
                            typesensor.equalsIgnoreCase("temp_sensor") ||
                            typesensor.equalsIgnoreCase("temp_sensor") ||
                            typesensor.equalsIgnoreCase("door_sensor" )) {
                        roomArrayList.get(i).getDeviceList().get(j).setSelected(false);
                    }
                }

            }
            roomArrayList = roomListTemp;

        }


        sortList(roomArrayList);
        roomArrayListTemp.clear();
        roomArrayListTemp = roomArrayList;
        deviceListLayoutHelper = new DeviceListLayoutHelper(this, rv_schedule, this, Constants.SWITCH_NUMBER, isMoodAdapter, this);

        deviceListLayoutHelper.addSectionList(roomArrayListTemp);

        if (isEditOpen && !TextUtils.isEmpty(roomId)) {
            deviceListLayoutHelper.setSelectionAll();
        } else {
            selectdeviceList();
        }
        deviceListLayoutHelper.notifyDataSetChanged();
    }

    private void sortList(final List<RoomVO> roomVOs) {

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded(), o1.isExpanded());
            }
        });
    }

    /**
     * show confirm save dialog if duplicate device found
     *
     * @param msg
     */
    private void showConfirmSave(String msg) {

        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Do you want to save the Schedule ?", msg, new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                try {
                    deviceObj.put("is_duplicate", 1);
                    deviceObj.put("user_id", Common.getPrefValue(ScheduleActivity.this, Constants.USER_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addSchedule();
            }

            @Override
            public void onConfirmDialogNoClick() {
//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void addSchedule() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait.", false);
        String url = webUrl + Constants.ADD_NEW_SCHEDULE;
        if (isEdit) {
            url = webUrl + Constants.UPDATE_SCHEDULE;
        }

        ChatApplication.logDisplay("schu is " + deviceObj.toString());

        new GetJsonTask(ScheduleActivity.this, url, "POST", deviceObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.hideKeyboard(ScheduleActivity.this);
                ChatApplication.isScheduleNeedResume = true;
                ChatApplication.logDisplay("addSchedule onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isShowProgress = true;
                        if (result.has("data")) {
                            JSONObject obj = result.getJSONObject("data");
                            int is_duplicate = obj.getInt("is_duplicate");
                            if (is_duplicate == 1) {
                                showConfirmSave(message);
                            }
                        } else {
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            ScheduleActivity.this.finish();
                        }
                        //getDeviceList();
                    } else if (code == 3001) { //3001 old status code
                        showConfirmSave(message);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    repeatDayString = "";
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("addSchedule onFailure " + error);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void getRepeatString() {

        repeatDayString = "";

        if (Boolean.parseBoolean(text_schedule_1.getTag().toString())) {
            repeatDayString = repeatDayString + "0";
        }
        if (Boolean.parseBoolean(text_schedule_2.getTag().toString())) {
            repeatDayString = repeatDayString + ",1";
        }
        if (Boolean.parseBoolean(text_schedule_3.getTag().toString())) {
            repeatDayString = repeatDayString + ",2";
        }
        if (Boolean.parseBoolean(text_schedule_4.getTag().toString())) {
            repeatDayString = repeatDayString + ",3";
        }
        if (Boolean.parseBoolean(text_schedule_5.getTag().toString())) {
            repeatDayString = repeatDayString + ",4";
        }
        if (Boolean.parseBoolean(text_schedule_6.getTag().toString())) {
            repeatDayString = repeatDayString + ",5";
        }
        if (Boolean.parseBoolean(text_schedule_7.getTag().toString())) {
            repeatDayString = repeatDayString + ",6";
        }
        //replace first comma.
        if (repeatDayString.startsWith(",")) {
            repeatDayString = repeatDayString.replaceFirst(",", "");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void itemClicked(RoomVO item, String action) {

    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {

    }

    @Override
    public void itemClicked(DeviceVO section, String action, int position) {
        if (action.equalsIgnoreCase("disable_device")) {
            getDeviceDetails(section.getOriginal_room_device_id());
        }
    }

    @Override
    public void itemClicked(CameraVO cameraVO, String action) {

    }

    /**
     * get Room name and Panel name using original_room_device_id
     *
     * @param original_room_device_id
     */
    private Socket mSocket;

    private void getDeviceDetails(String original_room_device_id) {

        //original_room_device_id

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }


        webUrl = app.url;

        String url = webUrl + Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id;

        new GetJsonTask2(ScheduleActivity.this, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");
                        showDeviceDialog(room_name, panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error, int responseCode) {
            }
        }).execute();
    }

    public Dialog dialog = null;

    public synchronized void showDeviceDialog(String roomName, String panelName) {

        if (dialog == null) {
            dialog = new Dialog(ScheduleActivity.this);
        } else {
            dialog.show();
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_virtual_devices);

        TextView txtRoom =  dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel =  dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK =  dialog.findViewById(R.id.vbtn_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /*select device list getting */
    @Override
    public void selectdeviceList() {

        for (int i = 0; i < roomArrayListTemp.size(); i++) {
            int count = 0;
            for (int j = 0; j < roomArrayListTemp.get(i).getPanelList().size(); j++) {

                for (int k = 0; k < roomArrayListTemp.get(i).getPanelList().get(j).getDeviceList().size(); k++) {

                    if (roomArrayListTemp.get(i).getPanelList().get(j).getDeviceList().get(k).isSelected()) {
                        String isUse = "";
                        if (!TextUtils.isEmpty(roomArrayListTemp.get(i).getPanelList().get(j).getDeviceList().get(k).getTo_use())) {
                            isUse = roomArrayListTemp.get(i).getPanelList().get(j).getDeviceList().get(k).getTo_use();
                        }

                        if (!isUse.equalsIgnoreCase("0")) {
                            count++;
                        }
                    }

                }
            }
            roomArrayListTemp.get(i).setSelectDevice("" + count);
        }

        deviceListLayoutHelper.notifyDataSetChanged();
    }

    public static List<DeviceVO> removeDuplicates(ArrayList<DeviceVO> list) {

        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<DeviceVO> listTemp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(list.get(i).getRoomDeviceId());
        }

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(arrayList);
        arrayList.clear();
        arrayList.addAll(hashSet);


        for (int j = 0; j < list.size(); j++) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).length() > 2 && list.get(j).getRoomDeviceId().equalsIgnoreCase(arrayList.get(i))) {
                    arrayList.set(i, arrayList.get(i) + "i");
                    listTemp.add(list.get(i));
                }
            }
        }
        return listTemp;
    }
}