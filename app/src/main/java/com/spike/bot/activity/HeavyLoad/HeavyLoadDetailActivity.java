package com.spike.bot.activity.HeavyLoad;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DataHeavyModel;
import com.spike.bot.receiver.MarkerBoxView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 29/6/19.
 * Gmail : vipul patel
 */
public class HeavyLoadDetailActivity extends AppCompatActivity {

    final ArrayList<String> arrayDay = new ArrayList<>();
    final ArrayList<String> arrayDayTemp = new ArrayList<>();
    public Toolbar toolbar;
    //        public LineChart barChart;
    public BarChart barChart;
    public ImageView imgHL, imageShowNext;
    public FrameLayout frameChart;
    public TextView txtGraphType, txtYAxis, txtGraphTital, txtNodataFound;
    public TextView txtCurrentValue, label_watts;
    public Spinner spinnerYear, spinnerMonth;
    public CountDownTimer countDownTimerSocket = null;
    public boolean isApiStatus = false;
    public int currentDay = 0;
    public String getRoomName = "", device_id = "";
    DataHeavyModel heavyModel = new DataHeavyModel();
    MyFormater formatter;
    ArrayList arrayListYearList = new ArrayList<>();
    //    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<BarEntry> entries = new ArrayList<>();
    RecyclerView rv_month_list, rv_year_list;
    ArrayList<String> monthlist = new ArrayList();
    ArrayList<String> yearlist = new ArrayList();
    int row_index = 1, mStartIndex = 4, strmonth = 0, row_index_year = 1, stryear;
    String selectedyear;
    TextView mCurrentMonthEnergy;
    private Socket mSocket;
    private DecimalFormat mFormat;
    private int currentmonthenergy = 0;
    /*heavy load value get & update view */
    private Emitter.Listener heavyLoadValue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (this == null) {
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
//  {"room_device_id":"1561365773929_OvHzT5WcFU","real_power":25}
                            ChatApplication.logDisplay("object  heavyLoadValue socket " + object);
                            if (device_id.equals(object.optString("device_id"))) {
                                String real_power = object.optString("real_power");
                                if (Integer.parseInt(object.optString("real_power")) > 0) {
                                    imgHL.setVisibility(View.VISIBLE);
                                    label_watts.setVisibility(View.VISIBLE);
                                    real_power = object.optString("real_power");
                                } else {
                                    real_power = "--";
                                    imgHL.setVisibility(View.GONE);
                                    label_watts.setVisibility(View.GONE);
                                }
                                txtCurrentValue.setText(real_power);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heavy_load);

        getRoomName = getIntent().getStringExtra("getRoomName");
        device_id = getIntent().getStringExtra("device_id");

        setUiId();


    }


    private void setUiId() {
        barChart = findViewById(R.id.barChart);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        imgHL = findViewById(R.id.imgHL);
        txtYAxis = findViewById(R.id.txtYAxis);
        txtGraphTital = findViewById(R.id.txtGraphTital);
        txtGraphType = findViewById(R.id.txtGraphType);
        imageShowNext = findViewById(R.id.imageShowNext);
        frameChart = findViewById(R.id.frameChart);
        txtNodataFound = findViewById(R.id.txtNodataFound);
        rv_month_list = findViewById(R.id.rv_month_list);
        rv_year_list = findViewById(R.id.rv_year_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getRoomName);
        txtCurrentValue = findViewById(R.id.txtCurrentValue);
        label_watts = findViewById(R.id.label_watts);

        mCurrentMonthEnergy = findViewById(R.id.txt_currnt_month);

        mFormat = new DecimalFormat("0,000");

        Glide.with(this)
                .load(R.drawable.heavy_load_yellow)
                .fitCenter()
                .error(R.drawable.cam_defult)
                .skipMemoryCache(true)
                .into(imgHL);

        Glide.with(this)
                .load(R.drawable.heavy_load_yellow)
                .fitCenter()
                .error(R.drawable.cam_defult)
                .skipMemoryCache(true)
                .into(imgHL);

        Glide.with(this)
                .load(R.drawable.next_scoll)
                .fitCenter()
                .error(R.drawable.cam_defult)
                .skipMemoryCache(true)
                .into(imageShowNext);

        startSocketConnection();
        setMonthList();
        setMonthAdpater();
        setYearlist();
        setSPinner();
        setYearAdpater();
        getHeavyloadDetails();

        /*for live data */
        getHeavyLoadValue();

        /*every 10 sec after emit data for status of heavyload data*/
        countDownTimerSocket = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (countDownTimerSocket != null) {
                    countDownTimerSocket.start();
                }

                getHeavyLoadValue();
            }
        };
        if (countDownTimerSocket != null) {
            countDownTimerSocket.start();
        }


        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        stryear = year;


       /* RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.rotation_animation);
        txtYAxis.setAnimation(rotate);*/
    }

    /*set spinner month & year */
    private void setSPinner() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;


        ArrayAdapter adapterMonth = new ArrayAdapter<String>(this, R.layout.item_spinner_selected_month, Constants.getMonthList());
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setSelection(month);

        ArrayAdapter adapterYear = new ArrayAdapter<String>(this, R.layout.item_spinner_selected_month, yearlist);
        spinnerYear.setAdapter(adapterYear);

        spinnerYear.setSelection(1);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* if(!isApiStatus){
                    filter();
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private void setYearAdpater() {
        rv_year_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_year_list.setLayoutManager(mLayoutManager);
        row_index_year = Constants.getCurentYear();
        ChatApplication.logDisplay("ROW INDEX YEAR" + row_index_year);
        YearAdapter adapter = new YearAdapter(this, yearlist);
        rv_year_list.setAdapter(adapter);
        rv_year_list.getLayoutManager().scrollToPosition(yearlist.size());
        ChatApplication.logDisplay("ROW INDEX year" + yearlist.size());
        rv_year_list.setHasFixedSize(true);
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

    /*set year dynamically */
    private void setYearlist() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        for (int i = 2016; i <= year; i++) {
            yearlist.add("" + i);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("statusMapping:heavy_load_voltage", heavyLoadValue);
        }
    }

    @Override
    protected void onStop() {
        if (mSocket != null) {
            mSocket.on("statusMapping:heavy_load_voltage", heavyLoadValue);
        }

        if (countDownTimerSocket != null) {
            countDownTimerSocket.cancel();
            countDownTimerSocket.onFinish();
            countDownTimerSocket = null;
        }
        super.onStop();
    }

    /*call service for heavy load*/
    public void getHeavyLoadValue() {
        if (!ActivityHelper.isConnectingToInternet(HeavyLoadDetailActivity.this)) {
            Toast.makeText(HeavyLoadDetailActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getHeavyLoadValue(device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is result " + result);
                    int code = result.getInt("code");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*call service for heavy load details*/
    public void getHeavyloadDetails() {
        if (!ActivityHelper.isConnectingToInternet(HeavyLoadDetailActivity.this)) {
            Toast.makeText(HeavyLoadDetailActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        isApiStatus = true;
        ActivityHelper.showProgressDialog(HeavyLoadDetailActivity.this, "Please wait... ", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getHeavyloadDetails(device_id, new DataResponseListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ActivityHelper.dismissProgressDialog();
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("json filter is first " + result);
//                        JSONObject object = result.optJSONObject("data");

                        try {
                            JSONArray jsonArray = result.optJSONArray("data");
                            if (jsonArray.length() == 0) {
                                Toast.makeText(HeavyLoadDetailActivity.this, "No data found...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        heavyModel = (DataHeavyModel) Common.fromJson(result.toString(), new TypeToken<DataHeavyModel>() {
                        }.getType());
                        chartDataset(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isApiStatus = false;
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                isApiStatus = false;
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                isApiStatus = false;
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }


    /*filter api */
    public void filter() {
        if (!ActivityHelper.isConnectingToInternet(HeavyLoadDetailActivity.this)) {
            Toast.makeText(HeavyLoadDetailActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);

        if (stryear == year && strmonth > month) {

            ChatApplication.showToast(this, "Please select valid month");
            return;
        }

        int value = row_index + 1;
        String strLess = "";
        if (value < 10) {
            strLess = "0";
        }

        ActivityHelper.showProgressDialog(HeavyLoadDetailActivity.this, "Please wait... ", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().HeavyloadFilter(device_id, strLess, value, stryear, new DataResponseListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    if (code == 200) {
                        ChatApplication.logDisplay("json filter is result " + result);

                        JSONObject object = result.optJSONObject("data");

                        heavyModel = (DataHeavyModel) Common.fromJson(result.toString(), new TypeToken<DataHeavyModel>() {
                        }.getType());

                        formatter = null;
                        entries.clear();
                        arrayDay.clear();
                        arrayDayTemp.clear();
                        currentDay = 0;
                        barChart.invalidate();
                        barChart.clear();
                        barChart.notifyDataSetChanged();
                        barChart.resetZoom();
                        barChart.setPinchZoom(true);
                        barChart.getDescription().setEnabled(false);
                        barChart.setHighlightFullBarEnabled(true);
                        barChart.setDrawValueAboveBar(true);

                      /*  MyMarkerView mv = new MyMarkerView(HeavyLoadDetailActivity.this, R.layout.custom_marker_view);
                        mv.setChartView(barChart); // For bounds control
                        barChart.setMarker(mv); // Set the marker to the chart*/

                        chartDataset(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    /*chart data set
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void chartDataset(boolean isFlag) {
        if (heavyModel != null && heavyModel.getGraphData() != null && heavyModel.getGraphData().size() > 0) {
            txtNodataFound.setVisibility(View.GONE);
            frameChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);

            if (isFlag) {
                if (spinnerMonth.getSelectedItem().equals("All")) {
                    currentDay = 12;
                    ArrayList<String> arrayList = monthlist;
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayDay.add("" + arrayList.get(i));
                        Log.i("All===?>", "" + arrayList.get(i));
                    }
                } else {
                    currentDay = Constants.getMonthOfDay(row_index, stryear);
                    ChatApplication.logDisplay("current day" + " " + row_index);
                    for (int i = 1; i <= currentDay; i++) {
                        arrayDay.add("" + i);
                        Log.i("All else===?>", "" + i);
                    }
                }
            } else {
                currentDay = Constants.getCurrentMonth();
                for (int i = 1; i <= currentDay; i++) {
                    arrayDay.add("" + i);
                    Log.i("All flag off else===?>", "" + i);
                }
            }

            boolean isflag = false;

            currentmonthenergy = 0;
            if (spinnerMonth.getSelectedItem().equals("All")) {

                for (int j = 0; j < arrayDay.size(); j++) {
                    isflag = false;

                    for (int i = 0; i < heavyModel.getGraphData().size(); i++) {
                        if (j + 1 == Integer.parseInt(heavyModel.getGraphData().get(i).getMonth())) {
                            isflag = true;
//                            entries.add(new Entry(j, heavyModel.getGraphData().get(i).getEnergy()));
                            currentmonthenergy = currentmonthenergy + heavyModel.getGraphData().get(i).getEnergy();
                            entries.add(new BarEntry(j, heavyModel.getGraphData().get(i).getEnergy()));
                            Log.i("All spinnerMonth===?>", "" + j);
                            break;
                        }
                    }
                    if (!isflag) {
//                        entries.add(new Entry(j, 0));
                        entries.add(new BarEntry(j, 0));

                    }
                }

            } else {

                for (int j = 0; j < arrayDay.size(); j++) {
                    isflag = false;

                    for (int i = 0; i < heavyModel.getGraphData().size(); i++) {
                        if (Integer.parseInt(arrayDay.get(j)) == Integer.parseInt(heavyModel.getGraphData().get(i).getDay())) {

                            isflag = true;
//                            entries.add(new Entry(j, heavyModel.getGraphData().get(i).getEnergy()));
                            currentmonthenergy = currentmonthenergy + heavyModel.getGraphData().get(i).getEnergy();
                            entries.add(new BarEntry(Integer.parseInt(arrayDay.get(j)), heavyModel.getGraphData().get(i).getEnergy()));
                            Log.i("Else spinnerMonth===?>", "" + j);
                            break;
                        }
                    }
                    if (!isflag) {
//                        entries.add(new Entry(j, 0));
                        entries.add(new BarEntry(j, 0));

                    }
                }
            }


//            LineDataSet dataSet = new LineDataSet(entries, "");


            mCurrentMonthEnergy.setText("Monthly Usage : " + currentmonthenergy + " KWh(Units)");

            BarDataSet dataSet = new BarDataSet(entries, "");

            dataSet.setColor(ContextCompat.getColor(this, R.color.sensor_button));
            dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.automation_black));
//            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.sensor_button));
//            dataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.sensor_button));
            dataSet.setValueTextSize(13f);

//            dataSet.setLineWidth(2);
            dataSet.setFormLineWidth(1.0f);

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            dataSet.setValueFormatter(new IValueFormatter() {

                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + (int) value;
                }
            });


//            dataSet.setCircleRadius(10);
            dataSet.setDrawValues(true);
            barChart.getDescription().setEnabled(false);
            barChart.getLegend().setEnabled(false);
            barChart.setFitBars(true);


            LocalDate today = LocalDate.now();
            int day = today.getDayOfMonth();

            barChart.moveViewToX((day - 1) + 0.5f);


            Log.i("day========?>", "" + day);
//

            //setfull zooom disble
//            barChart.setTouchEnabled(false);

            //****
            // Controlling X axis
            XAxis xAxis = barChart.getXAxis();
            // Set the xAxis position to bottom. Default is top


            arrayDayTemp.addAll(arrayDay);

            if (spinnerMonth.getSelectedItem().equals("All")) {
                formatter = new MyFormater() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value >= arrayDayTemp.size()) {
                            return String.valueOf(arrayDayTemp.get(arrayDayTemp.size() - 1));
                        }
                        return String.valueOf(getXAxisValues().get((int) value));

                    }
                };
            } else {
                formatter = new MyFormater() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value >= arrayDayTemp.size()) {

                            return String.valueOf(arrayDayTemp.get(arrayDayTemp.size() - 1));
                        }
                        if (value > 28) {
                            imageShowNext.setVisibility(View.GONE);
                        } else {
                            imageShowNext.setVisibility(View.VISIBLE);
                        }
                        return String.valueOf(arrayDayTemp.get((int) value));
                    }
                };
            }

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(new MyFormater());
            xAxis.setDrawGridLines(false);
            xAxis.setGranularityEnabled(true);
            xAxis.setDrawLabels(true);
            xAxis.setLabelCount(7);


            //***
            // Controlling right side of y axis
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setGranularity(1f);
            yAxisLeft.setAxisMinimum(0);
            yAxisLeft.setDrawGridLines(false);

            // Setting Data
//            LineData data = new LineData(dataSet);


            BarData data = new BarData(dataSet);
//            data.setBarWidth(barWidth);
            data.setValueTextSize(10f);
            data.setBarWidth(0.5f);


            barChart.setData(data);
            barChart.animateX(1500);


            IMarker marker = new MarkerBoxView(this, R.layout.activity_text_label);
            barChart.setMarker(marker);
            barChart.setScaleYEnabled(true);
            barChart.setScaleXEnabled(true);


            if (spinnerMonth.getSelectedItem().equals("All")) {

                barChart.setScaleMinima(5f, 0);
                barChart.setScaleX(5f);
            } else {
                barChart.setScaleMinima(5, 0);
                barChart.setScaleX(5f);
            }

            //refresh
            barChart.invalidate();

            txtGraphType.setVisibility(View.VISIBLE);
            txtYAxis.setVisibility(View.VISIBLE);
            txtGraphTital.setVisibility(View.VISIBLE);

            int totalWv = 0;
            for (int i = 0; i < heavyModel.getGraphData().size(); i++) {
                totalWv = totalWv + heavyModel.getGraphData().get(i).getEnergy();
            }
            if (currentDay == 12) {
                txtGraphType.setText("Year : " + stryear);
                // txtGraphTital.setText("Yearly Graph : "+totalWv);
            } else {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                String month = month_date.format(cal.getTime());

                ChatApplication.logDisplay("Current Month" + month);

                txtGraphType.setText("Month : " + month);
                //txtGraphTital.setText("Monthly Graph : "+totalWv);

                if (strmonth == 1) {
                    txtGraphType.setText("Month : " + "Jan");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 2) {
                    txtGraphType.setText("Month : " + "Feb");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 3) {
                    txtGraphType.setText("Month : " + "Mar");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 4) {
                    txtGraphType.setText("Month : " + "Apr");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 5) {
                    txtGraphType.setText("Month : " + "May");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 6) {
                    txtGraphType.setText("Month : " + "Jun");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 7) {
                    txtGraphType.setText("Month : " + "Jul");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 8) {
                    txtGraphType.setText("Month : " + "Aug");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 9) {
                    txtGraphType.setText("Month : " + "Sep");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 10) {
                    txtGraphType.setText("Month : " + "Oct");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 11) {
                    txtGraphType.setText("Month : " + "Nov");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                } else if (strmonth == 12) {
                    txtGraphType.setText("Month : " + "Dec");
                    //txtGraphTital.setText("Monthly Graph : "+totalWv);
                }
            }

        } else {
            frameChart.setVisibility(View.VISIBLE);
            txtNodataFound.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            txtGraphType.setVisibility(View.VISIBLE);
            txtYAxis.setVisibility(View.VISIBLE);
            txtGraphTital.setVisibility(View.GONE);
            ChatApplication.showToast(this, "No data found.");


            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month = month_date.format(cal.getTime());

            ChatApplication.logDisplay("Current Month" + month);

            txtGraphType.setText("Month : " + month);
            //txtGraphTital.setText("Monthly Graph : "+totalWv);

            if (strmonth == 1) {
                txtGraphType.setText("Month : " + "Jan");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 2) {
                txtGraphType.setText("Month : " + "Feb");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 3) {
                txtGraphType.setText("Month : " + "Mar");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 4) {
                txtGraphType.setText("Month : " + "Apr");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 5) {
                txtGraphType.setText("Month : " + "May");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 6) {
                txtGraphType.setText("Month : " + "Jun");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 7) {
                txtGraphType.setText("Month : " + "Jul");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 8) {
                txtGraphType.setText("Month : " + "Aug");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 9) {
                txtGraphType.setText("Month : " + "Sep");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 10) {
                txtGraphType.setText("Month : " + "Oct");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 11) {
                txtGraphType.setText("Month : " + "Nov");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            } else if (strmonth == 12) {
                txtGraphType.setText("Month : " + "Dec");
                //txtGraphTital.setText("Monthly Graph : "+totalWv);
            }
        }
    }


    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("January");
        xAxis.add("February");
        xAxis.add("March");
        xAxis.add("April");
        xAxis.add("May");
        xAxis.add("June");
        xAxis.add("July");
        xAxis.add("August");
        xAxis.add("September");
        xAxis.add("October");
        xAxis.add("November");
        xAxis.add("December");
        xAxis.add("");

        return xAxis;
    }


    public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

        private Context context;

        public MonthAdapter(Context context, ArrayList months) {
            this.context = context;
            monthlist = months;
        }

        @Override
        public MonthAdapter.MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthAdapter.MonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_month, parent, false));
        }

        @Override
        public void onBindViewHolder(MonthAdapter.MonthViewHolder holder, final int position) {
            holder.txtmonth.setText(monthlist.get(position));
            holder.linearlayout_month.setId(position);
            holder.linearlayout_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = holder.linearlayout_month.getId();
                    strmonth = row_index + 1;
                    if (!isApiStatus) {
                        filter();
                    }
                    //   getDatesInMonth(Calendar.getInstance().get(Calendar.YEAR), row_index);
                    notifyDataSetChanged();
                }
            });
            if (row_index == position) {
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

    public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder> {

        private Context context;

        public YearAdapter(Context context, ArrayList years) {
            this.context = context;
            yearlist = years;
        }

        @Override
        public YearAdapter.YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new YearAdapter.YearViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_year, parent, false));
        }

        @Override
        public void onBindViewHolder(YearAdapter.YearViewHolder holder, final int position) {
            holder.txtyear.setText(yearlist.get(position));
            holder.linearlayout_year.setId(position);
            holder.linearlayout_year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index_year = holder.linearlayout_year.getId();
                    stryear = Integer.parseInt(yearlist.get(position));
                    mStartIndex = position;
                    if (!isApiStatus) {
                        filter();
                    }
                    selectedyear = String.valueOf(stryear);

                    notifyDataSetChanged();
                }
            });
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            String stryear1 = String.valueOf(year);

            if (mStartIndex == position) {
                holder.txtyear.setTextColor(getResources().getColor(R.color.automation_black));
                holder.imgdots.setVisibility(View.VISIBLE);

            } else {
                holder.txtyear.setTextColor(getResources().getColor(R.color.device_button));
                holder.imgdots.setVisibility(View.GONE);
            }

           /* if (yearlist.get(position).equals(selectedyear))
            {
                holder.txtyear.setTextColor(getResources().getColor(R.color.automation_black));
                holder.imgdots.setVisibility(View.VISIBLE);

            } else {
                holder.txtyear.setTextColor(getResources().getColor(R.color.device_button));
                holder.imgdots.setVisibility(View.GONE);
            }*/


        }

        @Override
        public int getItemCount() {
            return yearlist.size();
        }

        class YearViewHolder extends RecyclerView.ViewHolder {

            private TextView txtyear;
            private ImageView imgdots;
            private LinearLayout linearlayout_year;

            YearViewHolder(View itemView) {
                super(itemView);
                txtyear = itemView.findViewById(R.id.txt_year_name);
                imgdots = itemView.findViewById(R.id.img_dots_year);
                linearlayout_year = itemView.findViewById(R.id.linearlayout_year);
                itemView.setTag(this);
            }
        }

    }


}
