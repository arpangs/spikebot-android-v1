package com.spike.bot.activity.HeavyLoad;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DataHeavyModel;
import com.spike.bot.model.HeavyModel;
import com.spike.bot.receiver.YourMarkerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 29/6/19.
 * Gmail : vipul patel
 */
public class HeavyLoadDetailActivity extends AppCompatActivity  {

    public Toolbar toolbar;
    public LineChart barChart;
    public ImageView imgHL,imageShowNext;
    public FrameLayout frameChart;
    public TextView txtGraphType,txtYAxis,txtGraphTital,txtNodataFound;
    public TextView txtCurrentValue;
    public Spinner spinnerYear, spinnerMonth;

    DataHeavyModel heavyModel = new DataHeavyModel();
    public CountDownTimer countDownTimerSocket = null;
    private Socket mSocket;
    IAxisValueFormatter formatter;

    public boolean isApiStatus=false;
    public int currentDay = 0;
    public String room_device_id = "",getRoomName="",getModuleId="",device_id="";

    ArrayList arrayListYearList = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();
    final ArrayList<String> arrayDay = new ArrayList<>();
    final ArrayList<String> arrayDayTemp = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heavy_load);

        room_device_id = getIntent().getStringExtra("getRoomDeviceId");
        getRoomName = getIntent().getStringExtra("getRoomName");
        getModuleId = getIntent().getStringExtra("getModuleId");
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getRoomName);
        txtCurrentValue = findViewById(R.id.txtCurrentValue);

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
        setYearlist();
        setSPinner();

        getHeavyloadDetails();

        final JSONObject object = new JSONObject();
        try {
            object.put("module_id", getModuleId);
            object.put("device_id","0"+ device_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket!=null){
            mSocket.emit("socketHeavyLoadValues", object);
        }

        ChatApplication.logDisplay("json is "+object.toString());

        /*every 10 sec after emit data for status of heavyload data*/
        countDownTimerSocket = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }public void onFinish() {

                if(mSocket!=null  && mSocket.connected()) {
                    ChatApplication.logDisplay("countDownTimerSocket calling " + object);
                    mSocket.emit("socketHeavyLoadValues", object);
                }
                if(countDownTimerSocket!=null){
                    countDownTimerSocket.start();
                }
            }
        };
        if(countDownTimerSocket!=null){
            countDownTimerSocket.start();
        }

        RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.rotation_animation);
        txtYAxis.setAnimation(rotate);
    }


    private void setSPinner() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;

        ArrayAdapter adapterMonth = new ArrayAdapter<String>(this,R.layout.item_spinner_selected_month, Constants.getMonthList());
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setSelection(month);

        ArrayAdapter adapterYear = new ArrayAdapter<String>(this, R.layout.item_spinner_selected_month, arrayListYearList);
        spinnerYear.setAdapter(adapterYear);

        spinnerYear.setSelection(1);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isApiStatus){
                    filter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isApiStatus){
                    filter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setYearlist() {
//        arrayListYearList.add("2018");
//        arrayListYearList.add("2019");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        for(int i=2018; i<=year; i++){
            arrayListYearList.add(""+i);
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
        if(mSocket!=null){
            mSocket.on("heavyLoadValue", heavyLoadValue);
        }
    }

    @Override
    protected void onPause() {
        if(mSocket!=null){
            mSocket.on("heavyLoadValue", heavyLoadValue);
        }

        if(countDownTimerSocket!=null){
            countDownTimerSocket.cancel();
            countDownTimerSocket.onFinish();
            countDownTimerSocket=null;
        }
        super.onPause();
    }

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

                            if(room_device_id.equals(object.optString("room_device_id"))){
                                String real_power=object.optString("real_power");
                                if(Integer.parseInt(object.optString("real_power"))>0){
                                    imgHL.setVisibility(View.VISIBLE);
                                    real_power=object.optString("real_power")+" W";
                                }else {
                                    real_power="--";
                                    imgHL.setVisibility(View.GONE);
                                }
                                txtCurrentValue.setText(real_power);

                                ChatApplication.logDisplay("object  heavyLoadValue " + object);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    public void getHeavyloadDetails() {
        if (!ActivityHelper.isConnectingToInternet(HeavyLoadDetailActivity.this)) {
            Toast.makeText(HeavyLoadDetailActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        isApiStatus=true;
        ActivityHelper.showProgressDialog(HeavyLoadDetailActivity.this, "Please wait... ", false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_device_id", room_device_id);
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.getHeavyLoadDetails;
        ChatApplication.logDisplay("json filter is first " + jsonObject.toString()+" "+url);
        new GetJsonTask(HeavyLoadDetailActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("json filter is first " + result);
                        JSONObject object = result.optJSONObject("data");

                        heavyModel = (DataHeavyModel) Common.fromJson(object.toString(), new TypeToken<DataHeavyModel>() {}.getType());

                        chartDataset(false);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isApiStatus=false;
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                isApiStatus=false;
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }


    /*filter api */
    public void filter() {
        if (!ActivityHelper.isConnectingToInternet(HeavyLoadDetailActivity.this)) {
            Toast.makeText(HeavyLoadDetailActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        int strmonth = spinnerMonth.getSelectedItemPosition();
        int strYear =  Integer.parseInt(String.valueOf(spinnerYear.getSelectedItem()));

        if(strYear== year && strmonth > month){

            ChatApplication.showToast(this,"Please select valid month");
            return;
        }

        ActivityHelper.showProgressDialog(HeavyLoadDetailActivity.this, "Please wait... ", false);
        JSONObject jsonObject = new JSONObject();
        try {
            // "room_device_id":"1561365773929_OvHzT5WcFU",
            // "filter_type":"month",   //year
            // "filter_value":"06"
            jsonObject.put("room_device_id", room_device_id);

            if (spinnerMonth.getSelectedItem().equals("All")) {
                jsonObject.put("filter_type", "year");
                jsonObject.put("filter_value", spinnerYear.getSelectedItem());
            } else {
                int value = spinnerMonth.getSelectedItemPosition();
                String strLess = "";
                if (value < 10) {
                    strLess = "0";
                }

                jsonObject.put("filter_type", "month");
                jsonObject.put("filter_value", strLess + value + "," + spinnerYear.getSelectedItem());
            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.filterHeavyLoadData;
        ChatApplication.logDisplay("json filter is " + jsonObject.toString()+" "+url);
        new GetJsonTask(HeavyLoadDetailActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("json filter is result " + result);

                        JSONObject object = result.optJSONObject("data");

                        heavyModel = (DataHeavyModel) Common.fromJson(object.toString(), new TypeToken<DataHeavyModel>() {}.getType());

                        formatter=null;
                        entries.clear();
                        arrayDay.clear();
                        arrayDayTemp.clear();
                        currentDay=0;
                        barChart.invalidate();
                        barChart.clear();
                        barChart.notifyDataSetChanged();
                        barChart.resetZoom();

                        chartDataset(true);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(HeavyLoadDetailActivity.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();
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
                    ArrayList<String> arrayList=getXAxisValues();
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayDay.add("" + arrayList.get(i));
                    }
                } else {
                    currentDay = Constants.getMonthOfDay(spinnerMonth.getSelectedItemPosition(), Integer.parseInt(String.valueOf(spinnerYear.getSelectedItem())));

                    for (int i = 1; i <= currentDay; i++) {
                        arrayDay.add("" + i);
                    }
                }
            } else {
                currentDay = Constants.getCurrentMonth();
                for (int i = 1; i <= currentDay; i++) {
                    arrayDay.add("" + i);
                }
            }

            boolean isflag = false;

            if (spinnerMonth.getSelectedItem().equals("All")) {

                for (int j = 0; j < arrayDay.size(); j++) {
                    isflag = false;
                    for (int i = 0; i < heavyModel.getGraphData().size(); i++) {
                        if (j+1 == Integer.parseInt(heavyModel.getGraphData().get(i).getMonth())) {
                            isflag = true;
                            entries.add(new Entry(j, heavyModel.getGraphData().get(i).getEnergy()));

                            break;
                        }
                    }
                    if (!isflag) {
                        entries.add(new Entry(j,0));
                    }
                }

            } else {

                for (int j = 0; j < arrayDay.size(); j++) {
                    isflag = false;
                    for (int i = 0; i < heavyModel.getGraphData().size(); i++) {
                        if (Integer.parseInt(arrayDay.get(j)) == Integer.parseInt(heavyModel.getGraphData().get(i).getDay())) {
                            isflag = true;
                            entries.add(new Entry(j, heavyModel.getGraphData().get(i).getEnergy()));

                            break;
                        }
                    }
                    if (!isflag) {
                        entries.add(new Entry(j,0));
                    }
                }
            }


            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(this, R.color.sensor_button));
            dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.automation_black));
            dataSet.setValueTextSize(13);
            dataSet.setLineWidth(2);
            dataSet.setCircleRadius(13);
            dataSet.setDrawValues(false);
            barChart.getDescription().setEnabled(false);
            barChart.getLegend().setEnabled(false);

            //setfull zooom disble
//            barChart.setTouchEnabled(false);

            //****
            // Controlling X axis
            XAxis xAxis = barChart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            arrayDayTemp.addAll(arrayDay);

            if (spinnerMonth.getSelectedItem().equals("All")) {
                formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(value>=arrayDayTemp.size()){
                            return String.valueOf(arrayDayTemp.get(arrayDayTemp.size()-1));
                        }
//                        return String.valueOf(getXAxisValues().get((int) value));
                        return String.valueOf(arrayDayTemp.get((int) value));
                    }
                };
            } else {
                formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(value>=arrayDayTemp.size()){

                            return String.valueOf(arrayDayTemp.get(arrayDayTemp.size()-1));
                        }
                        if(value>28){
                            imageShowNext.setVisibility(View.GONE);
                        }else {
                            imageShowNext.setVisibility(View.VISIBLE);
                        }
                        return String.valueOf(arrayDayTemp.get((int) value));
                    }
                };
            }

            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setGranularity(1f);
            yAxisLeft.setAxisMinimum(1);

            // Setting Data
            LineData data = new LineData(dataSet);
            barChart.setData(data);
            barChart.animateX(1500);


            IMarker marker = new YourMarkerView(this,R.layout.activity_text_label);
            barChart.setMarker(marker);
            barChart.setScaleYEnabled(false);
            barChart.setScaleXEnabled(false);


            if (spinnerMonth.getSelectedItem().equals("All")) {

                barChart.setScaleMinima(5f,0);
                barChart.setScaleX(5f);
            }else {
                barChart.setScaleMinima(5,0);
                barChart.setScaleX(5);
            }

            //refresh
            barChart.invalidate();

            txtGraphType.setVisibility(View.VISIBLE);
            txtYAxis.setVisibility(View.VISIBLE);
            txtGraphTital.setVisibility(View.VISIBLE);

            int totalWv=0;
            for(int i=0; i<heavyModel.getGraphData().size(); i++){
                totalWv=totalWv+heavyModel.getGraphData().get(i).getEnergy();
            }
            if(currentDay==12){
                txtGraphType.setText("Year : "+spinnerYear.getSelectedItem());
                txtGraphTital.setText("Yearly Graph : "+totalWv);
            }else {
                txtGraphType.setText("Month : "+spinnerMonth.getSelectedItem());
                txtGraphTital.setText("Monthly Graph : "+totalWv);
            }

        }else {
            frameChart.setVisibility(View.GONE);
            txtNodataFound.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            txtGraphType.setVisibility(View.GONE);
            txtYAxis.setVisibility(View.GONE);
            txtGraphTital.setVisibility(View.GONE);
            ChatApplication.showToast(this,"No data found.");
        }
    }


    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Jan");
        xAxis.add("Feb");
        xAxis.add("Mar");
        xAxis.add("Apr");
        xAxis.add("May");
        xAxis.add("Jun");
        xAxis.add("Jul");
        xAxis.add("Aug");
        xAxis.add("Sep");
        xAxis.add("Oct");
        xAxis.add("Nov");
        xAxis.add("Dec");
        xAxis.add("");

        return xAxis;
    }

}
