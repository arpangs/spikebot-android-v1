package com.spike.bot.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.CameraListAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

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

public class CameraPlayBack extends AppCompatActivity implements CameraListAdapter.CameraClick{

    private TextInputEditText edt_start_date,edt_end_date;
    private Spinner sp_camera_list;
    private RoomVO roomVO;
    private ArrayList<CameraVO> cameraVOArrayList;
    private RecyclerView cameraList;
    private CameraListAdapter cameraListAdapter;
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
    public static String start_date ="";
    public static String end_date ="";
    ArrayList<String> cameraStr;

    private TextView txt_count;

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

        edt_start_date = (TextInputEditText) findViewById(R.id.edt_start_date);
        edt_end_date = (TextInputEditText)findViewById(R.id.edt_end_date);
        sp_camera_list = (Spinner) findViewById(R.id.sp_camera_list);

        txt_no_date = (TextView)findViewById(R.id.txt_no_date);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        imgTitle = (ImageView) findViewById(R.id.img_title);
        txt_count = (TextView) findViewById(R.id.txt_count);

        cameraList = (RecyclerView) findViewById(R.id.cameraList);
        cameraList.setLayoutManager(new GridLayoutManager(this,1));

       // roomVO = (RoomVO) getIntent().getExtras().getSerializable("room");
        cameraVOArrayList = (ArrayList<CameraVO>) getIntent().getExtras().getSerializable("cameraList");

       // ArrayList<CameraVO> cameraVOs = roomVO.getCameraList();

        Log.d("cameraVOs","cameraVOs : " + cameraVOArrayList.size());

        cameraStr = new ArrayList<>();

        if(!cameraVOArrayList.isEmpty()){
            for(CameraVO cameraVO :cameraVOArrayList){
                cameraStr.add(cameraVO.getCamera_name());
            }
        }

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this,cameraStr,1,false);
        sp_camera_list.setAdapter(customAdapter);

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
        arrayList = new ArrayList<>();

        cameraListAdapter = new CameraListAdapter(cameraVOs,CameraPlayBack.this);
        cameraList.setAdapter(cameraListAdapter);

        startSocketConnection();

        //camera List<String> to convert String toArray
        alertDialogItems = new String[cameraStr.size()];
        alertDialogItems = cameraStr.toArray(alertDialogItems);
      //  cameraIdList = new String[cameraStr.size()];

        selectedtruefalse = new boolean[alertDialogItems.length];
        for (int i = 0; i < alertDialogItems.length; i++) {
            selectedtruefalse[i] = false;
        }

        if(!cameraVOArrayList.isEmpty()){
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


    AlertDialog.Builder alertdialogbuilder;
    String[] alertDialogItems;
    boolean[] selectedtruefalse;
    List<String> ItemsIntoList;
    String[] cameraIdList;
    List<String> selectedCamera = new ArrayList<>();

    private void showMultiChoiceItemsDialogs(){
        alertdialogbuilder.setMultiChoiceItems(alertDialogItems, selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        alertdialogbuilder.setCancelable(false);
        alertdialogbuilder.setTitle("Select Camera Here");

        ItemsIntoList = Arrays.asList(alertDialogItems);

        alertdialogbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCamera.clear();
                int count = 0;
                int a = 0;
                while(a < selectedtruefalse.length) {
                    boolean value = selectedtruefalse[a];
                    if(value){
                        count ++;
                        selectedCamera.add(ItemsIntoList.get(a));
                    }
                    a++;
                }

                if(count>0){
                     txt_count.setText("("+count+")");
                }else{
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


    ArrayList<String> arrayList;
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
            searchCameraList(start_date,end_date);
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
    private void searchCameraList(String startDate, String endDate){

        if(TextUtils.isEmpty(edt_start_date.getText().toString())){
            Toast.makeText(getApplicationContext(),"Select Start Date.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_end_date.getText().toString())){
            Toast.makeText(getApplicationContext(),"Select End Date.",Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidDate = compareDate(start_date,end_date);
        if(!isValidDate){
            Toast.makeText(getApplicationContext(),"End date is not less than Start Date",Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedCamera.size()==0){
            Toast.makeText(getApplicationContext(),"Please Select Camera.",Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("camera_start_date",startDate);
            jsonObject.put("camera_end_date",endDate);

            try{
                JSONArray jsonArray = new JSONArray();
                for(CameraVO cameraVO : cameraVOArrayList){
                    for(String ss : selectedCamera){
                        if(cameraVO.getCamera_name().equalsIgnoreCase(ss)){
                            JSONObject ob = new JSONObject();
                            ob.put("camera_id",cameraVO.getCamera_id());
                            ob.put("camera_name",cameraVO.getCamera_name());
                            jsonArray.put(ob);
                        }
                    }
                }
                jsonObject.put("camera_details",jsonArray);

            }catch (Exception ex){
                ex.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("jsonCamera","object : "+ jsonObject.toString());

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
           // Log.d(TAG, "mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        arrayList.clear();
        String url = webUrl;
        /*if(Main2Activity.isCloudConnected){
            //rtmp://home.deepfoods.net:11111/live/livestream3
            url = Constants.CAMERA_IP_CLOUD_HTTP + Constants.GET_CAMERA_RECORD_BY_DATE ;
        }else{
            url = Constants.CAMERA_IP + Constants.GET_CAMERA_RECORD_BY_DATE ;
        }*/

        url = url + Constants.GET_CAMERA_RECORDING_BY_DATE;
        Log.d("cameraURL","url :" + url);

       // url = Constants.CAMERA_IP + Constants.GET_CAMERA_RECORD_BY_DATE ;


        ActivityHelper.showProgressDialog(CameraPlayBack.this,"Please wait...",false);

        new GetJsonTask(getApplicationContext(), url, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                cameraVOs.clear();

                ChatApplication.ADAPTER_POSITION = -1;

                ActivityHelper.dismissProgressDialog();
                Log.d("jsonCamera","resullt respone : " + result.toString());

                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        JSONObject object = result.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("cameraList");

                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject ob = jsonArray.getJSONObject(i);
                            String camera_id = ob.getString("camera_id");
                            String camera_name = ob.getString("camera_name");
                            JSONArray array = ob.getJSONArray("camera_files");

                            ArrayList<String> arrayListTemp = new ArrayList<String>();

                            for(int j=0; j<array.length();j++){
                                String u_name = array.get(j).toString();
                               // arrayList.add(camera_id+"@"+camera_name+"@"+u_name);
                                CameraVO cameraVO = new CameraVO();
                                cameraVO.setCamera_id(camera_id);
                                cameraVO.setCamera_name(camera_name);
                                cameraVO.setCamera_videopath(u_name);
                                cameraVOs.add(cameraVO);
                            }

                        }
                        cameraListAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if(cameraVOs.isEmpty()){
                        txt_no_date.setVisibility(View.VISIBLE);
                        cameraList.setVisibility(View.GONE);
                    }else{
                        txt_no_date.setVisibility(View.GONE);
                        cameraList.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d("onFailure","error : " + error);
                ActivityHelper.dismissProgressDialog();
                if(cameraVOs.isEmpty()){
                    txt_no_date.setVisibility(View.VISIBLE);
                    cameraList.setVisibility(View.GONE);
                }else{
                    txt_no_date.setVisibility(View.GONE);
                    cameraList.setVisibility(View.VISIBLE);
                }
            }
        }).execute();

    }

    @Override
    public void onCameraClick(int position, CameraVO cameraVO) {

        //rtmp://home.deepfoods.net:11114/1516175178515_T4bw34W/2018-03-23_15.00.mp4
        //rtmp://home.deepfoods.net:11111/live/livestream1

        //String ip = webUrl + "/static/storage/"; //old url
        String ip = webUrl + CAMERA_PATH;
       /* if(Main2Activity.isCloudConnected){
            ip = Constants.CAMERA_IP_CLOUD_RTMP ;
        }*/

        //http://192.168.75.202/static/storage/1516175108972_RtW51YhVf/2018-03-17_21.15.mp4

        Log.d("cameraURL","for show camera : " + ip+""+cameraVO.getCamera_id()+"/"+cameraVO.getCamera_videopath());

        Intent intent = new Intent(CameraPlayBack.this, VideoViewPLayer.class);
        intent.putExtra("videoUrl",ip+""+cameraVO.getCamera_id()+"/"+cameraVO.getCamera_videopath()); //static/storage/camera_id/name
        intent.putExtra("name", ""+cameraVO.getCamera_name());
        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
        startActivity(intent);

      //  getCameraUrl(cameraVO.getCamera_id(),cameraVO.getCamera_videopath());
    }

    /*
    *   getCameraURL fro playing playBack video streaming
    *   @param cameraId   : 1F89FD3H78
    *   @param cameraName : 2019-03-20_04.15.mp4
    *
    * */

    private void getCameraUrl(final String cameraId, final String cameraName){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("camera_name",cameraName);
            jsonObject.put("cameraID",cameraId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActivityHelper.showProgressDialog(CameraPlayBack.this,"Please wait...",false);

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            // Log.d(TAG, "mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = "";
       /* if(Main2Activity.isCloudConnected){
            url = Constants.GET_CAMERA_CLOUD ;
        }else{
            url = Constants.CAMERA_IP + Constants.SHOW_CAMERA_RECORDING ;
        }*/

        Log.d("cameraURL","url2 :" + url);

      //  url = Constants.CAMERA_IP + Constants.SHOW_CAMERA_RECORDING ;

        new GetJsonTask(getApplicationContext(), url, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("jsonCamera","onSuccess respone : " + result.toString());
                ActivityHelper.dismissProgressDialog();

                int code = 0;

                String message = null;
                try {
                    code = result.getInt("code");
                    message = result.getString("message");

                    if(code==200){

                        JSONObject object = result.getJSONObject("data");
                        String camera_path = object.getString("camera_path");

                        String ip = "";/*Constants.CAMERA_IP_LOCAL_RTMP +"";*/
                      /*  if(Main2Activity.isCloudConnected){
                            //ip = Constants.CAMERA_IP_CLOUD_RTMP;
                        }*/

                        //http://192.168.75.202/static/storage/1516175108972_RtW51YhVf/2018-03-17_21.15.mp4
                        Intent intent = new Intent(CameraPlayBack.this, CameraPlayer.class);
                        intent.putExtra("videoUrl", ip+""+camera_path);
                        intent.putExtra("name", ""+cameraName);
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
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

    public void startSocketConnection(){

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            Log.d("", "mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        if (Common.isConnected()){
        }
    }

    /*
    * @param editText   : start_date/end_date textview
    * @param isEndDate  : is end_date textView or not
    * */
    private void datePicker(final EditText editText, final boolean isEndDate){

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

    private void tiemPicker(final EditText editText, final boolean isEndDate){
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

                        editText.setText(""+changeDateFormat(on_date));

                        if(!TextUtils.isEmpty(edt_start_date.getText().toString()) && !TextUtils.isEmpty(edt_end_date.getText().toString())){
                            boolean isCompare = compareDate(start_date,end_date);
                            if(!isCompare){
                                editText.setText("");
                                Toast.makeText(getApplicationContext(),"End date is not less than Start Date",Toast.LENGTH_SHORT).show();
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


}
