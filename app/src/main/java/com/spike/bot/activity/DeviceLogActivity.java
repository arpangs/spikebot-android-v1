package com.spike.bot.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.DeviceLogAdapter;
import com.spike.bot.adapter.DeviceLogNewAdapter;
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
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;
import com.spike.bot.model.SensorLogRes;

import static com.spike.bot.core.Constants.getNextDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

public class DeviceLogActivity extends AppCompatActivity implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, FilterMarkAll {
    Activity activity;
    RecyclerView rv_device_log;
    RecyclerView rv_month_list;
    Toolbar toolbar;

    public CardView cardViewBtn;
    private LinearLayout ll_empty;
    // DeviceLogAdapter deviceLogAdapter;
    DeviceLogNewAdapter deviceLogNewAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private OnLoadMoreListener onLoadMoreListener;

    public boolean isScrollview = false, isFilterType = false, isFilterActive = false, isSensorLog = false, isDateFilterActive = false, isScrollDown = false,
            isMultiSensor = false;

    private String isSelectItem = "", isSelectItemSub = "", isSelectItemSubTemp = "", isRoomName = "", strDeviceId = "", strpanelId = "", mRoomId, Schedule_id = "", activity_type = "", tabSelect = "", Mood_Id = "",
            actionType = "", isCheckActivity = "";
    //    private Button btnDevice, btnSensor;
    CheckBox checkBoxMark;

    ArrayAdapter<RoomVO> adapterRoom;

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    private boolean isLoading = false;
    public List<DeviceLog> deviceLogList = new ArrayList<>();
    public boolean isEndOfRecord = false, isCompareDateValid = true;

    String date_time = "";
    int mYear, mMonth, mDay, mHour, mMinute, mSecond, lastVisibleItem;

    public static String start_date = "";
    public static String end_date = "";

    private List<String> mListRoomMood;
    private List<RoomVO> mListRoom;
    private List<RoomVO> mListRoomTemp = new ArrayList<>();
    ArrayList<DeviceVO> arrayListDeviceTemp = new ArrayList<>();
    ArrayList<ScheduleVO> scheduleRoomArrayList = new ArrayList<>();
    ArrayList datelist = new ArrayList();
    ArrayList monthlist = new ArrayList();
    int row_index = -1;

    MenuItem menuItem, menuItemReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);
        toolbar = findViewById(R.id.toolbar);

        activity = this;
        rv_device_log = findViewById(R.id.rv_device_log);
        rv_month_list = findViewById(R.id.rv_monthlist);
        ll_empty = findViewById(R.id.ll_empty);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        ll_empty.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        mRoomId = getIntent().getStringExtra("ROOM_ID");
        Schedule_id = getIntent().getStringExtra("Schedule_id");
        activity_type = getIntent().getStringExtra("activity_type");
        isSensorLog = getIntent().getBooleanExtra("IS_SENSOR", false);
        tabSelect = getIntent().getStringExtra("tabSelect");
        Mood_Id = getIntent().getStringExtra("Mood_Id");
        isCheckActivity = getIntent().getStringExtra("isCheckActivity");
        isRoomName = getIntent().getStringExtra("isRoomName");

        if (isCheckActivity.equals("room") || isCheckActivity.equals("schedule") || isCheckActivity.equals("mode")) {
            setTitle("" + isRoomName + " Logs");
        } else if (isCheckActivity.equals("doorSensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName + " Logs");
        } else if (isCheckActivity.equals("tempSensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName + " Logs");
        } else if (isCheckActivity.equals("multisensor")) {
            isFilterType = true;
            isSensorLog = true;
            setTitle(isRoomName + " Logs");
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

      /*  deviceLogAdapter = new DeviceLogAdapter(DeviceLogActivity.this, deviceLogList);
        rv_device_log.setAdapter(deviceLogAdapter);*/

        deviceLogNewAdapter = new DeviceLogNewAdapter(DeviceLogActivity.this, deviceLogList);
        rv_device_log.setAdapter(deviceLogNewAdapter);
        deviceLogNewAdapter.notifyDataSetChanged();

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

//        btnDevice = findViewById(R.id.btn_device);
//        btnSensor = findViewById(R.id.btn_sensor);
        cardViewBtn = findViewById(R.id.cardViewBtn);

        if (tabSelect.equalsIgnoreCase("hide")) {
            cardViewBtn.setVisibility(View.GONE);
        } else {
            cardViewBtn.setVisibility(View.GONE);
        }

        getDeviceLog(0);
//        if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {
//            getSensorLog(0);
//        } else {
        // callFilterData(0, "");
//        }
        setMonthList();
        setMonthAdpater();
    }

    private void udpateDailogButton(Button btnDeviceDialog, Button btnSensorDialog) {

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


    @Override
    public void onLoadMore(int lastVisibleItem) {
        isScrollview = true;
        callFilterData(lastVisibleItem, "");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        start_date = "";
        end_date = "";
        ListUtils.start_date_filter = "";
        ListUtils.end_date_filter = "";
        if (isMultiSensor) {
            unreadApiCall(false);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        menuItem = menu.findItem(R.id.action_filter);
        menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setVisible(false);
        menuItem.setVisible(true);
        if (isFilterActive) {
            rv_month_list.setVisibility(View.GONE);
            menuItemReset.setVisible(true);
            menuItem.setVisible(false);
            menuItemReset.setIcon(R.drawable.icn_reset);
        } else {
            menuItemReset.setVisible(false);
            menuItem.setVisible(true);
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
            rv_month_list.setVisibility(View.VISIBLE);
            menuItemReset.setVisible(false);
            menuItem.setVisible(true);

            callFilterData(0, "refresh");
           /* isFilterActive = false;
            isEndOfRecord = false;


            invalidateOptionsMenu();
            deviceLogList.clear();
            isDateFilterActive = false;
            deviceLogNewAdapter = new DeviceLogNewAdapter(DeviceLogActivity.this, deviceLogList);
            rv_device_log.setAdapter(deviceLogNewAdapter);
            deviceLogNewAdapter.notifyDataSetChanged();
            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {

                if (isCheckActivity.equals("multisensor") && isMultiSensor) {
                    unreadApiCall(true);
                } else {
                    getDeviceLog(0);
                }
            } else {
                //   udpateButton();
                if (isCheckActivity.equals("AllType")) {
                    isFilterType = false;
                    isFilterActive = false;
                }
                getDeviceLog(0);
            }*/
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
        isScrollview = false;

        if (dialog == null) {
            dialog = new Dialog(this, R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_filter);

            btnCancel = dialog.findViewById(R.id.btn_cancel_filter);
            btnSave = dialog.findViewById(R.id.btn_save_filter);
            btnReset = dialog.findViewById(R.id.btn_cancel_reset);

            checkBoxMark = dialog.findViewById(R.id.chk_all);

            mSpinnerRoomMood = dialog.findViewById(R.id.spinner_room_mood);
            mSpinnerRoomList = dialog.findViewById(R.id.spinner_room_list);
            mSpinnerPanelList = dialog.findViewById(R.id.spinner_panel_list);
            mSpinnerDeviceList = dialog.findViewById(R.id.spinner_device_list);
            panel_view = dialog.findViewById(R.id.panel_view);
            frame_living_room = dialog.findViewById(R.id.frame_living_room);
            frame_all_devices = dialog.findViewById(R.id.frame_all_devices);
            btnSensorDialog = dialog.findViewById(R.id.btnSensorDialog);
            btnDeviceDialog = dialog.findViewById(R.id.btnDeviceDialog);

            edt_start_date = dialog.findViewById(R.id.edt_log_start_date);
            edt_end_date = dialog.findViewById(R.id.edt_log_end_date);

            recyclerView = dialog.findViewById(R.id.root_list);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            end_date = ChatApplication.getCurrentDateTime();
            start_date = ChatApplication.getCurrentDate();
            edt_start_date.setText("" + ChatApplication.getCurrentDate());
            edt_end_date.setText("" + ChatApplication.getCurrentDateTime());

            checkBoxMark.setChecked(isMarkAll);
            strpanelId = "";
            strDeviceId = "";

            if (isCheckActivity.equals("tempsensor")) {
                isSelectItem = "2";
            } else if (isCheckActivity.equals("doorSensor")) {
                isSelectItem = "3";
            }
        } else {
            if (mSpinnerRoomMood != null) {
                isSelectItem = "" + mSpinnerRoomMood.getSelectedItemPosition();
            } else {
                isSelectItem = "";
            }

            if (mSpinnerRoomList != null) {
                isSelectItemSub = "" + mSpinnerRoomList.getSelectedItemPosition();
                isSelectItemSubTemp = "" + mSpinnerRoomList.getSelectedItemPosition();
            } else {
                isSelectItemSub = "";
                isSelectItemSubTemp = "";
            }
        }

        setAdapterFilterVIew();
        final FilterRootAdapter filterRootAdapter = new FilterRootAdapter(filterArrayList, this);
        recyclerView.setAdapter(filterRootAdapter);
        filterRootAdapter.notifyDataSetChanged();


        if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isFilterActive = false;
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
                swipeRefreshLayout.setRefreshing(false);
                menuItem.setVisible(false);
                menuItemReset.setVisible(true);
                invalidateOptionsMenu();
                deviceLogList.clear();
                dialog.dismiss();
//                getDeviceLog(0);
                callFilterData(0, "");
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
                    if (isCheckActivity.equals("tempsensor")) {
                        isSelectItem = "1";
                    } else if (isCheckActivity.equals("doorSensor")) {
                        isSelectItem = "2";
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

                strpanelId = "";
                strDeviceId = "";
                setAdapterFilterVIew();
                initSpinnerData(filterRootAdapter);
            }
        });

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

    private void setMonthAdpater() {
        rv_month_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_month_list.setLayoutManager(mLayoutManager);
        row_index = Constants.getCurentMonth();
        MonthAdapter adapter = new MonthAdapter(this, monthlist);
        rv_month_list.setAdapter(adapter);
        rv_month_list.getLayoutManager().scrollToPosition(row_index);
        rv_month_list.setHasFixedSize(true);
    }

    private void setMonthList() {
        monthlist.add("January");
        monthlist.add("February");
        monthlist.add("March");
        monthlist.add("April");
        monthlist.add("May");
        monthlist.add("June");
        monthlist.add("July");
        monthlist.add("August");
        monthlist.add("September");
        monthlist.add("October");
        monthlist.add("November");
        monthlist.add("December");
    }

    private void setAdapterFilterVIew() {
        if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
            if (filterArrayList != null) {
                filterArrayList.clear();
            }
            if (isCheckActivity.equals("tempsensor")) {
                for (int j = 0; j < filterArrayListSensorTemp.size(); j++)
                    if (filterArrayListSensorTemp.get(j).getName().equals("Temp sensor")) {
                        for (int i = 0; i < filterArrayListSensorTemp.get(j).getSubFilters().size(); i++) {
                            Filter filter = new Filter();
                            filter.setName(filterArrayListSensorTemp.get(j).getSubFilters().get(i).getName());
                            filter.setChecked(true);
                            filterArrayList.add(filter);
                        }
                    }
            }

            if (isCheckActivity.equals("doorSensor")) {
                for (int j = 0; j < filterArrayListSensorTemp.size(); j++)
                    if (filterArrayListSensorTemp.get(j).getName().equals("Door sensor")) {
                        for (int i = 0; i < filterArrayListSensorTemp.get(j).getSubFilters().size(); i++) {
                            Filter filter = new Filter();
                            filter.setName(filterArrayListSensorTemp.get(j).getSubFilters().get(i).getName());
                            filter.setChecked(true);
                            filterArrayList.add(filter);
                        }
                    }

            }
        } else {
            ChatApplication.logDisplay("isSelectItem is " + isSelectItem);
            if (TextUtils.isEmpty(isSelectItem) || isSelectItem.equals("0")) {
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
                                Filter filter = new Filter();
                                filter.setName(filterArrayListTemp.get(j).getSubFilters().get(i).getName());
                                filter.setChecked(true);
                                filterArrayList.add(filter);
                            }
                    } else if (isSelectItem.equals("0")) {
                        if (filterArrayList != null) {
                            filterArrayList.clear();
                        }

//                        for (int j = 0; j < filterArrayListTemp.size(); j++)
                        for (int i = 0; i < filterArrayListTemp.get(0).getSubFilters().size(); i++) {
                            Filter filter = new Filter();
                            filter.setName(filterArrayListTemp.get(0).getSubFilters().get(i).getName());
                            filter.setChecked(true);
                            filterArrayList.add(filter);
                        }
                    }
                }
            } else {

            }

        }
    }

    //fill spinner adapter
    private void initSpinnerData(final FilterRootAdapter filterRootAdapter) {

        mListRoomMood = new ArrayList<>();
        mListRoom = new ArrayList<>();

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
        }

        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item_spinner_selected, mListRoomMood);
        mSpinnerRoomMood.setAdapter(adapterRoomMood);

        if (!isFilterType) {
            if (TextUtils.isEmpty(isSelectItem)) {
                mSpinnerRoomMood.setSelection(ChatApplication.checkSelection(isCheckActivity, mListRoomMood));
                if (isCheckActivity.equals("room")) {
                    panel_view.setVisibility(View.VISIBLE);
                    frame_all_devices.setVisibility(View.INVISIBLE);
                    frame_living_room.setVisibility(View.VISIBLE);

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
                    getDeviceList("Room");
                } else if (isCheckActivity.equals("schedule")) {

                    panel_view.setVisibility(View.GONE);
                    frame_living_room.setVisibility(View.VISIBLE);
                    getDeviceList("Schedule");
                } else if (isCheckActivity.equals("mode")) {
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
                isSelectItemSub = "";
                if (isFilterType) {
                    if (position != 0) {
                        frame_living_room.setVisibility(View.VISIBLE);
                        panel_view.setVisibility(View.GONE);
                        actionType = mListRoomMood.get(position);
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
                        setAdapterFilterVIew();
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


    private void getDeviceList(final String room) {
        String url = "", urlType = "POST", parameter = "";
        actionType = room;

        JSONObject jsonObject = new JSONObject();

        if (room.equalsIgnoreCase("Room")) {
            //   url = ChatApplication.url + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
            url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        } else if (room.equalsIgnoreCase("Mood")) {
            url = ChatApplication.url + Constants.moodList;
        } else if (room.equalsIgnoreCase("Schedule")) {
            url = ChatApplication.url + Constants.GET_SCHEDULE_LIST;
        } else if (room.equalsIgnoreCase("sensor") || room.equalsIgnoreCase("Gas sensor")
                || room.equalsIgnoreCase("Temp sensor") || room.equalsIgnoreCase("Door sensor")) {
            urlType = "sensor";
            url = ChatApplication.url + Constants.devicefind;
        } else {
            url = ChatApplication.url + Constants.GET_DEVICES_LIST;
        }

        try {
            if (room.equalsIgnoreCase("Room")) {
                jsonObject.put("room_type", "room");

            } else if (room.equalsIgnoreCase("Mood")) {
                jsonObject.put("room_type", "mood");
            } else if (room.equalsIgnoreCase("Schedule")) {
            }
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = "";
        if (urlType.equalsIgnoreCase("sensor")) {
            urlType = "POST";
            //user_id": "someuser_id",
            //    "phone_id": "somephone_id",
            //    "phone_type": "IOS",
            //    "device_type": "ir_blaster"

            try {
                if (room.equalsIgnoreCase("Gas sensor")) {
                    jsonObject.put("device_type", "gas_sensor");
                } else if (room.equalsIgnoreCase("Temp sensor")) {
                    jsonObject.put("device_type", "temp_sensor");
                } else if (room.equalsIgnoreCase("Door sensor")) {
                    jsonObject.put("device_type", "door_sensor");
                }
                jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
                jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
                json = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            json = jsonObject.toString();
        }
        ChatApplication.logDisplay("json is " + url + " " + json.toString());
        new GetJsonTask2(activity, url, urlType, json, new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                try
                {

                    if (mListRoom != null) {
                        mListRoom.clear();
                        mListRoomTemp.clear();
                    }
                    int code = result.getInt("code");
                    if (code == 200)
                    {
                        scheduleRoomArrayList.clear();

//                        ChatApplication.logDisplay("json is  data  " + result);

                        if (isFilterType) {
                            frame_living_room.setVisibility(View.VISIBLE);
                            panel_view.setVisibility(View.GONE);
                            SensorLogRes mSensorLogRes = Common.jsonToPojo(result.toString(), SensorLogRes.class);

                            RoomVO oneRoom = new RoomVO();
                            oneRoom.setRoomId("0");
                            if (room.equalsIgnoreCase("Temp sensor") || room.equalsIgnoreCase("Gas sensor") || room.equalsIgnoreCase("Temp sensor")) {
                                oneRoom.setRoomName("All");
                                mListRoom.add(0, oneRoom);
                            } else {
                                oneRoom.setRoomName("All Door");
                                mListRoom.add(0, oneRoom);
                            }

                            if (mSensorLogRes.getData() != null) {
                                for (int i = 0; i < mSensorLogRes.getData().size(); i++) {
                                    RoomVO roomVO = new RoomVO();
                                    roomVO.setRoomName(mSensorLogRes.getData().get(i).getDeviceName());
                                    roomVO.setRoomId(mSensorLogRes.getData().get(i).getDeviceId());
                                    mListRoom.add(roomVO);
                                }
                            }

                        } else {
                            if (room.equalsIgnoreCase("") || room.equalsIgnoreCase("Mood") || room.equalsIgnoreCase("Room")) {
                                JSONObject dataObject = result.getJSONObject("data");
                                if (dataObject != null) {
                                    JSONArray roomArray = dataObject.optJSONArray("roomdeviceList");
                                    if (roomArray != null) {
                                        for (int i = 0; i < roomArray.length(); i++) {
                                            JSONObject roomelement = roomArray.getJSONObject(i);
                                            if (roomelement != null) {
                                                JSONArray panel = roomelement.getJSONArray("panelList");
                                                if (panel != null) {
                                                    for (int j = 0; j < panel.length(); j++) {
                                                        JSONObject innerElem = panel.getJSONObject(j);
                                                        if (innerElem != null) {
                                                            String name = innerElem.getString("panel_name");
                                                            if (name.equals("Sensor Panel")) {
                                                                panel.remove(j);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    mListRoom = JsonHelper.parseRoomArray(roomArray, false);
                                }

                                if (room.equalsIgnoreCase("Mood")) {
                                    frame_living_room.setVisibility(View.VISIBLE);
                                    RoomVO oneRoom = new RoomVO();
                                    oneRoom.setRoomId("0");
                                    oneRoom.setRoomName("All Mood");
                                    mListRoom.add(0, oneRoom);
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

                                }

                            } else {
                                JSONArray dataObject = result.optJSONArray("data");
                                if (dataObject != null) {

                                    if (room.equalsIgnoreCase("Schedule") || room.equalsIgnoreCase("Timer")) {
                                        scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(dataObject));
                                    }

                                    if (room.equalsIgnoreCase("Schedule")) {
                                        frame_living_room.setVisibility(View.VISIBLE);
                                        RoomVO oneRoom = new RoomVO();
                                        oneRoom.setRoomId("0");
                                        oneRoom.setRoomName("All Schedule");
                                        mListRoom.add(0, oneRoom);

                                        for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                            if (scheduleRoomArrayList.get(i).getIs_timer() == 0) {
                                                RoomVO roomVO = new RoomVO();
                                                roomVO.setRoomName(scheduleRoomArrayList.get(i).getSchedule_name());
                                                roomVO.setRoomId(scheduleRoomArrayList.get(i).getSchedule_id());
                                                mListRoom.add(roomVO);
                                            }
                                        }

                                    } else {
                                        frame_living_room.setVisibility(View.GONE);
                                        RoomVO oneRoom = new RoomVO();
                                        oneRoom.setRoomId("0");
                                        oneRoom.setRoomName("All Schedule");
                                        mListRoom.add(0, oneRoom);
                                    }
                                } else {
                                    frame_living_room.setVisibility(View.GONE);
                                }
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
        if (roomVOSList.size() < 1) {

            frame_living_room.setVisibility(View.GONE);
            return;
        }
        if (!TextUtils.isEmpty(isSelectItemSubTemp)) {
            isSelectItemSubTemp = "";
            isSelectItemSub = "";
        } else {
            if (TextUtils.isEmpty(isSelectItemSub)) {
                if (roomVOSList.size() > 0) {
                    adapterRoom = new ArrayAdapter<RoomVO>(getApplicationContext(),
                            R.layout.item_spinner_selected, roomVOSList);
                    mSpinnerRoomList.setAdapter(adapterRoom);

                    if (ChatApplication.checkActivity(isCheckActivity) != -1) {
                        for (int i = 0; i < roomVOSList.size(); i++) {
                            ChatApplication.logDisplay("room id is " + roomVOSList.get(i).getRoomId() + " " + mRoomId);
                            if (roomVOSList.get(i).getRoomId().equals(mRoomId)) {
                                mSpinnerRoomList.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    frame_living_room.setVisibility(View.GONE);
                }
            }
        }
        if (roomVOSList.size() == 0) {
            frame_living_room.setVisibility(View.GONE);
        }

        mSpinnerRoomList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strpanelId = "";
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
                        RoomVO roomVO = roomVOSList.get(position);
                        initPanelSpinner(roomVO);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void defaultDeviceSpinner(String all_device) {

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

        arrayPanelList.add("All Panel");
        for (int i = 0; i < panelVOSList.size(); i++) {
            if (isFilterType) {
                if (panelVOSList.get(i).getPanel_type() == 2) {
                    arrayPanelList.add(panelVOSList.get(i).getPanelName());
                }
            } else {
                if (panelVOSList.get(i).getPanel_type() == 0) {
                    arrayPanelList.add(panelVOSList.get(i).getPanelName());
                }
            }


        }
        defaultDeviceSpinner("All Device");

        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_selected, arrayPanelList);
        mSpinnerPanelList.setAdapter(adapterRoomMood);


        final ArrayList<PanelVO> finalPanelVOSList = panelVOSList;
        final ArrayList<PanelVO> finalPanelVOSList1 = panelVOSList;
        mSpinnerPanelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strDeviceId = "";

                if (position == 0) {
                    strpanelId = "";
                    frame_all_devices.setVisibility(View.INVISIBLE);
                    defaultDeviceSpinner("All Device");
                } else if (position != 0) {
                    frame_all_devices.setVisibility(View.VISIBLE);
                    strpanelId = finalPanelVOSList1.get(position - 1).getPanelId();
                    PanelVO panelVO = finalPanelVOSList.get(position - 1);
                    initDeviceAdapter(panelVO);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initDeviceAdapter(PanelVO panelVO) {
        ArrayList<String> arrayListDeviceList = new ArrayList<>();
        ArrayList<DeviceVO> mListDeviceSub = new ArrayList<>();
        arrayListDeviceTemp.clear();
        mListDeviceSub = panelVO.getDeviceList();
        arrayListDeviceTemp.add(0, new DeviceVO("All Device", "0"));
        arrayListDeviceList.add("All Device");

        for (int i = 0; i < mListDeviceSub.size(); i++) {
            DeviceVO deviceVO = new DeviceVO();
            if (!TextUtils.isEmpty(mListDeviceSub.get(i).getDeviceName())) {
                arrayListDeviceList.add(mListDeviceSub.get(i).getDeviceName());
                deviceVO.setDeviceName(mListDeviceSub.get(i).getDeviceName());
                deviceVO.setDeviceId(mListDeviceSub.get(i).getDeviceId());
            } else {
                arrayListDeviceList.add(mListDeviceSub.get(i).getSensor_name());
                deviceVO.setDeviceName(mListDeviceSub.get(i).getSensor_name());
                deviceVO.setDeviceId(mListDeviceSub.get(i).getDeviceId());
            }
            arrayListDeviceTemp.add(deviceVO);
        }

        frame_all_devices.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapterRoomMood = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_selected, arrayListDeviceList);
        mSpinnerDeviceList.setAdapter(adapterRoomMood);

        mSpinnerDeviceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    strDeviceId = "";
                } else {
                    strDeviceId = arrayListDeviceTemp.get(position).getDeviceId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countDownTimer = new CountDownTimer(1500, 1000) {

            public void onTick(long millisUntilFinished) {
                ChatApplication.logDisplay("strDeviceId is " + strDeviceId);
            }

            public void onFinish() {
                countDownTimer.start();
            }
        };
        countDownTimer.start();
    }

    CountDownTimer countDownTimer;

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
                        timePicker(editText, isEndDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void timePicker(final CustomEditText editText, final boolean isEndDate) {
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


    public void callFilterData(final int position, String typeofFilter) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);

        String url = ChatApplication.url + Constants.logsfind;


        //{
        //	"user_id":"someuser_id",
        //	"phone_id":"somephone_id",
        //	"phone_type":"IOS",
        //	"filter_type":"all",
        //	"filter_action":"schedule_delete",
        //	"room_id":"1573120794804_ag6n2_MrR"
        //}

        JSONObject object = new JSONObject();
        try {
            object.put("notification_number", position);


//            if (actionType.equals("Mood") || actionType.equals("Room")) {
//                object.put("is_room", 1);
//            } else {
//                object.put("is_room", 0);
//            }

            if (typeofFilter.equalsIgnoreCase("refresh")) {
                object.put("start_date", "");
                object.put("end_date", "");
//                if (isCheckActivity.equals("room") || isCheckActivity.equals("mood")) {
                if (isCheckActivity.equals("room")) {
                    object.put("room_id", "" + mRoomId);
                } else if (isCheckActivity.equals("mode")) {
                    object.put("mood_id", "" + mRoomId);
                } else if (isCheckActivity.equalsIgnoreCase("schedule")) {
                    object.put("schedule_id", "" + mRoomId);
                }

                if (isCheckActivity.equals("room")) {
                    object.put("filter_type", "room");
                } else if (isCheckActivity.equalsIgnoreCase("mode")) {
                    object.put("filter_type", "mood");
                } else if (isCheckActivity.equals("schedule")) {
                    object.put("filter_type", "schedule");
                } else if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
                    object.put("filter_type", "device");
                    object.put("device_id", "" + mRoomId);
                } else {
                    object.put("filter_type", "all");
                }


                object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
                object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            } else {
                object.put("start_date", start_date);
                object.put("end_date", getNextDate(end_date));
                RoomVO roomVO = null;
                if (mSpinnerRoomList != null) {
                    roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
                    if (mSpinnerRoomMood.getSelectedItem().toString().equals("All")) {
                        if (isFilterType)
                        {
                            String actionname = "";
                            ArrayList<String> stringArrayList = new ArrayList<>();
                            if (filterArrayList.size() > 0) {
                                for (int i = 0; i < filterArrayList.size(); i++) {
                                    if (filterArrayList.get(i).isChecked()) {
                                        stringArrayList.add(filterArrayList.get(i).getName());
                                    }
                                }

                                for (int i = 0; i < stringArrayList.size(); i++) {
                                    if (i == 0) {
                                        actionname = stringArrayList.get(i).toLowerCase();
                                    } else {
                                        actionname = actionname + "," + stringArrayList.get(i).toLowerCase();
                                    }
                                }
                                object.put("filter_action", actionname);
                            }

                            if (actionType.equalsIgnoreCase("Door sensor")) {
                                object.put("filter_type", "door_sensor");
                            } else if (actionType.equalsIgnoreCase("Gas sensor")) {
                                object.put("filter_type", "gas_sensor");
                            } else if (actionType.equalsIgnoreCase("Temp sensor")) {
                                object.put("filter_type", "temp_sensor");
                            } else {
                                object.put("filter_type", "all-sensor");
                            }

                            if (roomVO != null) {
                                object.put("device_id", roomVO.getRoomId());
                            }
                        }


                    } else {

                        if (isFilterType) {

                            if (actionType.equalsIgnoreCase("Door sensor")) {
                                object.put("filter_type", "door_sensor");
                            } else if (actionType.equalsIgnoreCase("Gas sensor")) {
                                object.put("filter_type", "gas_sensor");
                            } else if (actionType.equalsIgnoreCase("Temp sensor")) {
                                object.put("filter_type", "temp_sensor");
                            } else {
                                object.put("filter_type", "all-sensor");
                            }

                            if (roomVO != null) {
                                object.put("device_id", roomVO.getRoomId());
                            }

                        } else {

                            if (roomVO != null && !roomVO.getRoomId().equalsIgnoreCase("0")) {

                                if (mSpinnerRoomMood != null) {
                                    if (mSpinnerRoomMood.getSelectedItem().toString().toLowerCase().equals("mood")) {
                                        object.put("mood_id", "" + roomVO.getRoomId());
                                    } else {
                                        object.put("room_id", "" + roomVO.getRoomId());
                                    }
                                } else {
                                    object.put("room_id", "" + roomVO.getRoomId());
                                }
                            }

                            if (!TextUtils.isEmpty(strDeviceId) && strDeviceId.length() > 0) {
                                object.put("device_id", "" + strDeviceId);
                            } else {
                                object.put("device_id", "");
                            }

                            if (!TextUtils.isEmpty(strpanelId) && strpanelId.length() > 0) {
                                object.put("panel_id", "" + strpanelId);
                            } else {
                                object.put("panel_id", "");
                            }

                        }
                        String actionname = "";
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        for (int i = 0; i < filterArrayList.size(); i++) {

                            if (filterArrayList.get(i).isChecked()) {
                                stringArrayList.add(filterArrayList.get(i).getName());
                            }
                        }

                        for (int i = 0; i < stringArrayList.size(); i++) {
                            if (i == 0) {
                                actionname = stringArrayList.get(i).toLowerCase();
                            } else {
                                actionname = actionname + "," + stringArrayList.get(i).toLowerCase();
                            }
                        }

                        object.put("filter_action", actionname);
                    }
                } else {
                    if (roomVO != null) {
                        object.put("room_id", "" + roomVO.getRoomId());
                    } else if (isCheckActivity.equals("room") || isCheckActivity.equalsIgnoreCase("mode")) {
                        if (isCheckActivity.equalsIgnoreCase("mode")) {
                            object.put("mood_id", "" + mRoomId);
                        } else {
                            object.put("room_id", "" + mRoomId);
                        }
                    } else if (isCheckActivity.equalsIgnoreCase("schedule")) {
                        object.put("schedule_id", "" + mRoomId);
                    } else if (isCheckActivity.equals("AllType")) {
                        object.put("filter_type", "all");
                    } else {
                        object.put("room_id", "");
                    }
                }

                if (mSpinnerRoomMood != null) {
                    if (mSpinnerRoomMood.getSelectedItemPosition() == 0 && isFilterType) {
                        object.put("filter_type", "all-sensor");
                    } else {
                        object.put("filter_type", mSpinnerRoomMood.getSelectedItem().toString().toLowerCase());
                    }

                } else {
                    if (isCheckActivity.equals("room")) {
                        object.put("filter_type", "room");
                    } else if (isCheckActivity.equalsIgnoreCase("mode")) {
                        object.put("filter_type", "mood");
                    } else if (isCheckActivity.equals("schedule")) {
                        object.put("filter_type", "schedule");
                    } else if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor")) {
                        object.put("filter_type", "device");
                        object.put("device_id", "" + mRoomId);
                    } else {
                        object.put("filter_type", "all");
                    }

                }
            }

            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("object is " + url + "  " + object.toString());
        new GetJsonTask(activity, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if (position == 0) {
                            deviceLogList.clear();
                        }
                        ll_empty.setVisibility(View.GONE);
                        rv_device_log.setVisibility(View.VISIBLE);

                        ChatApplication.logDisplay("object is response " + result);

                        JSONArray notificationArray = result.optJSONArray("data");

                        if (notificationArray != null && notificationArray.length() == 0) {
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
                            String seen_by = jsonObject.optString("seen_by");
                            String logmessage = jsonObject.optString("message");

                            deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "", user_name, is_unread, logmessage, seen_by));
                        }

                        if (position == 0) {
                            if (!TextUtils.isEmpty(notificationArray.toString())) {
                                if (notificationArray.toString().equals("[]")) {
                                    swipeRefreshLayout.setRefreshing(false);
                                    showAToast("No Record Found...");
                                }
                            }
                        }

                        if (notificationArray.length() == 0 && isFilterActive && isLoading) {

                            if (!isEndOfRecord) {
                                deviceLogList.add(new DeviceLog("End of Record", "End of Record", "", "", "", "", "", "",""));
                                deviceLogNewAdapter.setEOR(true);
                            }
                            isEndOfRecord = true;
                        } else if (notificationArray.length() == 0 && isFilterActive) {
                            swipeRefreshLayout.setRefreshing(false);
                            deviceLogList.add(new DeviceLog("End of Record", "No Record Found", "", "", "", "", "", "",""));
                            deviceLogNewAdapter.setEOR(true);
                            showAToast("No Record Found...");

                        }
                        deviceLogNewAdapter.notifyDataSetChanged();

                        ActivityHelper.dismissProgressDialog();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
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

    public void getDeviceLog(final int position) {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ChatApplication.url + Constants.GET_FILTER_NOTIFICATION_INFO;


        //{
        //	"user_id":"someuser_id",
        //	"phone_id":"somephone_id",
        //	"phone_type":"IOS",
        //	"filter_type":"all",
        //	"filter_action":"schedule_delete",
        //	"room_id":"1573120794804_ag6n2_MrR"
        //}

        JSONObject object = new JSONObject();
        try {
            if (isFilterActive) {
                object.put("notification_number", position);
                object.put("room_id", "" + mRoomId);
                object.put("start_datetime", start_date);
                object.put("end_datetime", end_date);


                RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
                JSONArray array = new JSONArray();
                if (mSpinnerRoomMood.getSelectedItem().toString().equals("All")) {
//                    object.put("room_id", "");
//                    object.put("panel_id", "");
//                    object.put("module_id", "");
//                    object.put("filter_data", "");

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

                } else {

                    if (isFilterType) {

                        if (actionType.equalsIgnoreCase("Door Sensor")) {
                            object.put("sensor_type", "door");
                        } else {
                            object.put("sensor_type", "temp");
                        }

                        object.put("room_id", "");
                        object.put("panel_id", "");
                        if (roomVO != null) {
                            object.put("module_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));
                        } else {
                            object.put("module_id", "");
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
                    String actionname = "";
                    ArrayList<String> stringArrayList = new ArrayList<>();
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
                    subObject.put("activity_type", "" + actionType);

                    array.put(subObject);
                    object.put("filter_data", array);
                }


            } else {
                /*{
	"user_id":"someuser_id",
	"phone_id":"somephone_id",
	"phone_type":"IOS",
	"filter_type":"all",
	"filter_action":"schedule_delete",
	"room_id":"1573120794804_ag6n2_MrR"
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
                if (isCheckActivity.equals("mode")) {
                    object.put("filter_type", "mood");
                } else if (isCheckActivity.equalsIgnoreCase("mood")) {
                    object.put("filter_type", "room");
                } else if (isCheckActivity.equalsIgnoreCase("schedule")) {
                    object.put("filter_type", "schedule");
                } else {
                    object.put("filter_type", "");
                }
            }
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ChatApplication.logDisplay("object is " + object.toString() + url);
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

                        // JSONObject dataObj = result.getJSONObject("data");
                        ChatApplication.logDisplay("data obj is " + result);

                        if (result.has("data")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = result.optJSONObject("data").optJSONArray("general");
                            if (!isFilterActive) {
                                if (filterArrayListTemp != null) {
                                    filterArrayListTemp.clear();
                                    filterArrayList.clear();
                                }
                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    String filtername = "";
                                    char firstChar = logName.charAt(0);
                                    char firstCharToUpperCase = Character.toUpperCase(firstChar);
                                    filtername = filtername + firstCharToUpperCase;
                                    boolean terminalCharacterEncountered = false;
                                    char[] terminalCharacters = {'.', '?', '!'};
                                    for (int j = 1; j < logName.length(); j++) {
                                        char currentChar = logName.charAt(j);
                                        if (terminalCharacterEncountered) {
                                            if (currentChar == ' ') {
                                                filtername = filtername + currentChar;
                                            } else {
                                                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                                                filtername = filtername + currentCharToUpperCase;
                                                terminalCharacterEncountered = false;
                                            }
                                        } else {
                                            char currentCharToLowerCase = Character.toLowerCase(currentChar);
                                            filtername = filtername + currentCharToLowerCase;
                                        }
                                        for (int k = 0; k < terminalCharacters.length; k++) {
                                            if (currentChar == terminalCharacters[k]) {
                                                terminalCharacterEncountered = true;
                                                break;
                                            }
                                        }
                                    }

                                    String action_name = object.getString("action_name");

                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {

                                        String actionname = "";
                                        char fChar = subLog.charAt(0);
                                        char fCharToUpperCase = Character.toUpperCase(fChar);
                                        actionname = actionname + fCharToUpperCase;
                                        boolean terminalCharacterEncountere = false;
                                        char[] terminalCharacter = {'.', '?', '!'};
                                        for (int j = 1; j < subLog.length(); j++) {
                                            char currentChar = subLog.charAt(j);
                                            if (terminalCharacterEncountere) {
                                                if (currentChar == ' ') {
                                                    actionname = actionname + currentChar;
                                                } else {
                                                    char currentCharToUpperCase = Character.toUpperCase(currentChar);
                                                    actionname = actionname + currentCharToUpperCase;
                                                    terminalCharacterEncountere = false;
                                                }
                                            } else {
                                                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                                                actionname = actionname + currentCharToLowerCase;
                                            }
                                            for (int k = 0; k < terminalCharacter.length; k++) {
                                                if (currentChar == terminalCharacter[k]) {
                                                    terminalCharacterEncountere = true;
                                                    break;
                                                }
                                            }
                                        }
                                        subFilterArrayList.add(new Filter.SubFilter(actionname, false));
                                    }

                                    filterArrayListTemp.add(new Filter(filtername, false, false, subFilterArrayList));

                                }
                            }
                        }

                        if (result.has("data")) {
                            isScrollDown = false;
                            JSONArray logJsonArray = result.optJSONObject("data").optJSONArray("sensor");

                            if (!isFilterActive) {
                                if (filterArrayListSensorTemp != null) {
                                    filterArraySensor.clear();
                                    filterArrayListSensorTemp.clear();
                                }

                                for (int i = 0; i < logJsonArray.length(); i++) {
                                    JSONObject object = logJsonArray.getJSONObject(i);

                                    String logName = object.getString("filter_name");
                                    logName = logName.substring(0, 1).toUpperCase() + logName.substring(1);
                                    logName = logName.replace("_", " ");
                                    String action_name = object.getString("action_name");
                                    ArrayList<Filter.SubFilter> subFilterArrayList = new ArrayList<Filter.SubFilter>();
                                    for (String subLog : action_name.split(",")) {
                                        subFilterArrayList.add(new Filter.SubFilter(subLog, false));
                                    }
                                    filterArrayListSensorTemp.add(new Filter(logName, false, false, subFilterArrayList));

                                }
                            }
                        }

                        JSONArray notificationArray = result.optJSONArray("notificationList");

                        if (notificationArray != null && notificationArray.length() == 0) {
                            isScrollDown = true;
                        } else {
                            isScrollDown = false;
                        }
                        if (notificationArray != null) {
                            for (int i = 0; i < notificationArray.length(); i++) {
                                JSONObject jsonObject = notificationArray.getJSONObject(i);
                                String activity_action = jsonObject.getString("activity_action");
                                String activity_type = jsonObject.getString("activity_type");
                                String activity_description = jsonObject.getString("activity_description");
                                String activity_time = jsonObject.getString("activity_time");
                                String user_name = jsonObject.getString("user_name");
                                String is_unread = jsonObject.optString("is_unread");
                                String logmessage = jsonObject.optString("message");
                                String seen_by = jsonObject.optString("seen_by");

                                deviceLogList.add(new DeviceLog(activity_action, activity_type, activity_description, activity_time, "", user_name, is_unread, logmessage,seen_by));
                            }

                            if (position == 0) {
                                if (!TextUtils.isEmpty(notificationArray.toString())) {
                                    if (notificationArray.toString().equals("[]")) {
                                        showAToast("No Record Found...");
                                    }
                                }
                            }

                            if (notificationArray.length() == 0 && isFilterActive && isLoading) {

                                if (!isEndOfRecord) {
                                    deviceLogList.add(new DeviceLog("End of Record", "End of Record", "", "", "", "", "", "",""));
                                    deviceLogNewAdapter.setEOR(true);
                                }
                                isEndOfRecord = true;
                            } else if (notificationArray.length() == 0 && isFilterActive) {
                                deviceLogList.add(new DeviceLog("End of Record", "No Record Found", "", "", "", "", "", "",""));
                                deviceLogNewAdapter.setEOR(true);
                                showAToast("No Record Found...");
                            }
                            deviceLogNewAdapter.notifyDataSetChanged();

                        }

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
       /*     deviceLogNewAdapter.notifyDataSetChanged();*/
//            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {
//                getSensorLog(0);
//            } else {

            getDatesInMonth(Calendar.getInstance().get(Calendar.YEAR), row_index);
            callFilterData(0, "");

            //       callFilterData(0, "refresh");
//            }
            swipeRefreshLayout.setRefreshing(true);
            rv_month_list.setVisibility(View.VISIBLE);
            deviceLogNewAdapter.notifyDataSetChanged();
        } else {
            dialog = null;
            isFilterActive = false;
            isFilterType = false;
            isEndOfRecord = false;
            isSelectItemSubTemp = "";
            isSelectItem = "";
            isSelectItemSub = "";
            invalidateOptionsMenu();
            deviceLogList.clear();
            isDateFilterActive = false;
            deviceLogNewAdapter = new DeviceLogNewAdapter(DeviceLogActivity.this, deviceLogList);
            rv_device_log.setAdapter(deviceLogNewAdapter);
            deviceLogNewAdapter.notifyDataSetChanged();
//            if (isCheckActivity.equals("doorSensor") || isCheckActivity.equals("tempsensor") || isCheckActivity.equals("multisensor")) {
//                getSensorLog(0);
//            } else {
            if (isCheckActivity.equals("AllType")) {
                isFilterType = false;
                isFilterActive = false;
            }
            rv_month_list.setVisibility(View.VISIBLE);
            getDatesInMonth(Calendar.getInstance().get(Calendar.YEAR), row_index);
            callFilterData(0, "");

//            }
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

        String webUrl = ChatApplication.url + Constants.logsfind;

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();

            JSONObject object = new JSONObject();
            object.put("log_type", "room");
         //   object.put("module_id", "" + Mood_Id);
            object.put("room_id", "" + mRoomId);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
           // jsonArray.put(object);

       //     jsonObject.put("update_logs", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("log is " + webUrl + " " + jsonObject);
        new GetJsonTask(this, webUrl, "POST", jsonObject.toString(), new

                ICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        ChatApplication.logDisplay("log is " + result);
                        if (b) {
                            isMultiSensor = false;
//                            callFilterData(0);
                        } else {
                            DeviceLogActivity.this.finish();
                        }

                    }

                    @Override
                    public void onFailure(Throwable throwable, String error) {
                        if (b) {
//                            callFilterData(0);
                        } else {
                            DeviceLogActivity.this.finish();
                        }
                    }
                }).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getDatesInMonth(int year, int month) {
        SimpleDateFormat format = new SimpleDateFormat(Constants.LOG_DATE_FORMAT_1);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < daysInMonth; i++) {
            System.out.println(format.format(cal.getTime()));
            datelist.add(format.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        start_date = datelist.get(0).toString();
        end_date = datelist.get(datelist.size() - 1).toString();
        ChatApplication.logDisplay("current_start_date " + start_date);
        ChatApplication.logDisplay("current_end_date " + end_date);
        callFilterData(0, "");
    }

    public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

        private Context context;

        public MonthAdapter(Context context, ArrayList months) {
            this.context = context;
            monthlist = months;
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_month, parent, false));
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, final int position) {
            holder.txtmonth.setText(monthlist.get(position).toString());
            holder.linearlayout_month.setId(position);
            holder.linearlayout_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = holder.linearlayout_month.getId();
                    datelist.clear();
                    getDatesInMonth(Calendar.getInstance().get(Calendar.YEAR), row_index);
                    notifyDataSetChanged();
                }
            });
            if (row_index == position) {
                getDatesInMonth(Calendar.getInstance().get(Calendar.YEAR), row_index);
                holder.txtmonth.setTextColor(getResources().getColor(R.color.automation_black));
                holder.imgdots.setVisibility(View.VISIBLE);
            } else {
                holder.txtmonth.setTextColor(getResources().getColor(R.color.device_button));
                holder.imgdots.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return monthlist.size();
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {

            private TextView txtmonth;
            private ImageView imgdots;
            private LinearLayout linearlayout_month;

            MonthViewHolder(View itemView) {
                super(itemView);
                txtmonth = itemView.findViewById(R.id.txt_month_name);
                imgdots = itemView.findViewById(R.id.img_dots_month);
                linearlayout_month = itemView.findViewById(R.id.linearlayout_month);
                itemView.setTag(this);
            }
        }

    }
}
