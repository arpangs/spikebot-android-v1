package com.spike.bot.activity.ir.blaster;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.irblaster.CustomSpinnerAdapter;
import com.spike.bot.adapter.irblaster.IRBlasterScheduleAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment;
import com.spike.bot.model.IRBlasterInfoRes;
import com.spike.bot.model.RemoteAddScheduleReq;
import com.spike.bot.model.RemoteSchListRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 7/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterEditActivity extends AppCompatActivity implements View.OnClickListener,IRBlasterScheduleAdapter.RemoteSchClickEvent{

    private RecyclerView schList;
    private IRBlasterScheduleAdapter irBlasterScheduleAdapter;
    private IRBlasterInfoRes.Data.IrBlasterList.RemoteList remoteList;

    private String mRemoteId,mRemoteName,mRoomId;
    private TextView mIrAdd;
    private EditText mIrRemoteName;

    private List<String> speedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irblaster_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        syncIntent();
        bindView();
        getSchDetails();

    }
    private void syncIntent(){
        remoteList  = (IRBlasterInfoRes.Data.IrBlasterList.RemoteList) getIntent().getSerializableExtra("REMOTE_SPEED");
        mRoomId     = getIntent().getStringExtra("REMOTE_ID");
        mRemoteName = getIntent().getStringExtra("REMOTE_NAME");
        mRemoteId   = getIntent().getStringExtra("REMOTE_ID");
        speedList   = remoteList.getRemoteCommandList().get(0).getSPEED();
    }

    private void bindView(){

        mIrRemoteName = (EditText) findViewById(R.id.ir_remote_name);
        mIrAdd = (TextView) findViewById(R.id.ir_add_sch);
        schList = (RecyclerView) findViewById(R.id.remote_sch_list);
        schList.setLayoutManager(new GridLayoutManager(this,1));

        mIrAdd.setOnClickListener(this);

        mIrRemoteName.setText(mRemoteName);

    }

    List<RemoteSchListRes.Data.RemoteScheduleList> remoteScheduleList;
    private void getSchDetails(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+R.string.disconnect);
            return;
        }

        String url = ChatApplication.url + Constants.GET_REMOTE_SCHEDULE_LIST + "/"+mRemoteId;
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
                        RemoteSchListRes remoteSchListRes = Common.jsonToPojo(result.toString(), RemoteSchListRes.class);
                        remoteScheduleList = remoteSchListRes.getData().getRemoteScheduleList();
                        irBlasterScheduleAdapter =
                                new IRBlasterScheduleAdapter(remoteScheduleList,IRBlasterEditActivity.this);
                        schList.setAdapter(irBlasterScheduleAdapter);

                    }else{
                        showToast(message);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }else if(id == R.id.action_save){
            updateRemote();
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateRemote(){

        //mRemoteName

        if(TextUtils.isEmpty(getEditText(mIrRemoteName))){
            mIrRemoteName.requestFocus();
            mIrRemoteName.setError("Enter Remote Name");
            return;
        }

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+R.string.disconnect);
            return;
        }

        String url = ChatApplication.url + Constants.UPDATE_REMOTE_DETAILS;

        JSONObject object = new JSONObject();
        try {
            object.put("remote_id",mRemoteId);
            object.put("remote_name",mIrRemoteName.getText().toString().trim());
            object.put("room_id",mRoomId);
            object.put("phone_id",APIConst.PHONE_ID_VALUE);
            object.put("phone_type",APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(!TextUtils.isEmpty(message)){
                        showToast(message);
                    }
                    if(code == 200){
                        finish();
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


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ir_add_sch:
                addSch(false);
                break;
            case R.id.iv_close:
                repeatDayString = "";
                dismissDialog();
                break;
            case R.id.btn_save:
                addSchApiCall();
                break;
            case R.id.tv_sch_start_time:
                ActivityHelper.hideKeyboard(this);
                DialogFragment fromTimeFragment = new TimePickerFragment(this, mDStartTime.getText().toString(), new ICallback() {
                    @Override
                    public void onSuccess(String str) {
                        mDStartTime.setText(str);
                    }
                });
                if (!fromTimeFragment.isVisible()) {
                    fromTimeFragment.show(this.getFragmentManager(), "timePicker");
                }
                break;

            case R.id.tv_sch_end_time:
                ActivityHelper.hideKeyboard(this);
                DialogFragment fromTimeFragmentEnd = new TimePickerFragment(this, mDStartTime.getText().toString(), new ICallback() {
                    @Override
                    public void onSuccess(String str) {
                        mDEndTime.setText(str);
                    }
                });
                if (!fromTimeFragmentEnd.isVisible()) {
                    fromTimeFragmentEnd.show(this.getFragmentManager(), "timePicker");
                }
                break;
        }
    }

    private String getEditText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void addSchApiCall(){

        if(TextUtils.isEmpty(getEditText(mDiaSchName))){
            mDiaSchName.requestFocus();
            mDiaSchName.setError("Enter Schedule Name");
            return;
        }


        //
        if(TextUtils.isEmpty(getEditText(mDStartTime)) && TextUtils.isEmpty(getEditText(mDEndTime)) ){
            showToast("Enter start or end time");
            return;
        }

        if(!TextUtils.isEmpty(getEditText(mDStartTime))){
            if(TextUtils.isEmpty(getEditText(mDStartTemp))){
                mDStartTemp.requestFocus();
                mDStartTemp.setError("Enter Temp");
                return;
            }
            if(mModeSpinnerStart.getSelectedItemPosition() == 0){
                showToast("Select mode type");
                return;
            }
        }else if(TextUtils.isEmpty(getEditText(mDStartTime)) && !TextUtils.isEmpty(getEditText(mDStartTemp))){
            showToast("Enter Start Time");
            return;
        }

        if(!TextUtils.isEmpty(getEditText(mDEndTime))){
            if(TextUtils.isEmpty(getEditText(mDEndTemp))){
                mDEndTemp.requestFocus();
                mDEndTemp.setError("Enter Temp");
                return;
            }
            if(mModeSpinnerEnd.getSelectedItemPosition() == 0){
                showToast("Select mode type");
                return;
            }
        }else if(TextUtils.isEmpty(getEditText(mDEndTime)) && !TextUtils.isEmpty(getEditText(mDEndTemp))){
            showToast("Enter Start Time");
            return;
        }



        String onTime = "", offTime = "";
        if(isEdit){

            if (!tmpRemoteList.getStartTime().equalsIgnoreCase("")) {
                try {
                    mDStartTime.setText(DateHelper.formateDate(DateHelper.parseTimeSimple(tmpRemoteList.getStartTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            if (!tmpRemoteList.getEndTime().equalsIgnoreCase("")) {
                try {
                    mDEndTime.setText(DateHelper.formateDate(DateHelper.parseTimeSimple(tmpRemoteList.getEndTime(), DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        if (!TextUtils.isEmpty(mDStartTime.getText().toString())) {
            try {
                onTime = DateHelper.formateDate(DateHelper.parseTimeSimple(mDStartTime.getText().toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(mDEndTime.getText().toString())) {
            try {
                offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(mDEndTime.getText().toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM); } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        getRepeatString();
        ChatApplication.logDisplay("OK : " + repeatDayString);

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+ R.string.disconnect);
            return;
        }


        String url;
        if(isEdit){
            url = ChatApplication.url + Constants.UPDATE_REMOTE_SCHEDULE;
        }else{
            url = ChatApplication.url + Constants.ADD_REMOTE_SCHEDULE;
        }

        String startMode = mModeSpinnerStart.getSelectedItem().toString();
        String endMode = mModeSpinnerEnd.getSelectedItem().toString();


        if(TextUtils.isEmpty(getEditText(mDStartTime))){
            onTime = "";
            startMode = "";
        }
        if(TextUtils.isEmpty(getEditText(mDEndTime))){
            offTime = "";
            endMode = "";
        }

        RemoteAddScheduleReq remoteAddScheduleReq = new RemoteAddScheduleReq();
        remoteAddScheduleReq.setRemoteId(mRemoteId);
        remoteAddScheduleReq.setIrScheduleName(getEditText(mDiaSchName));
        remoteAddScheduleReq.setStartTime(onTime);
        remoteAddScheduleReq.setEndTime(offTime);
        remoteAddScheduleReq.setStartTemperature(getEditText(mDStartTemp));
        remoteAddScheduleReq.setEndTemperature(getEditText(mDEndTemp));
        remoteAddScheduleReq.setStartMode(startMode);
        remoteAddScheduleReq.setEndMode(endMode);
        remoteAddScheduleReq.setScheduleDay(repeatDayString);
        remoteAddScheduleReq.setPhoneId(APIConst.PHONE_ID_VALUE);
        remoteAddScheduleReq.setPhoneType(APIConst.PHONE_TYPE_VALUE);
        if(isEdit){
            remoteAddScheduleReq.setSchedule_id(tmpRemoteList.getScheduleId());
        }

        Gson gson = new Gson();
        String addSchReqJson = gson.toJson(remoteAddScheduleReq);

        ChatApplication.logDisplay("JOSN : " + addSchReqJson);

        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        new GetJsonTask(this, url, "POST", addSchReqJson, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    showToast(message);
                    if(code == 200){
                        dismissDialog();
                        getSchDetails();
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

    private Dialog mDialog;
    private EditText mDiaSchName;
    private Spinner mModeSpinnerStart,mModeSpinnerEnd;
    private EditText mDStartTime,mDStartTemp;
    private EditText mDEndTime,mDEndTemp;
    private Button mBtnSave;
    private ImageView iv_close;
    private ImageView img_start_clear,img_end_clear;

    String repeatDayString = "";
    TextView text_schedule_1, text_schedule_2, text_schedule_3, text_schedule_4, text_schedule_5, text_schedule_6, text_schedule_7;

    private void addSch(boolean isEdit){
        if(mDialog == null){
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setContentView(R.layout.dialog_add_remote_sch);
        }

        mDiaSchName = (EditText) mDialog.findViewById(R.id.tv_sch_name);
        mModeSpinnerStart = (Spinner) mDialog.findViewById(R.id.dg_mode_start);
        mModeSpinnerEnd = (Spinner) mDialog.findViewById(R.id.dg_mode_end);

        mDStartTime = (EditText) mDialog.findViewById(R.id.tv_sch_start_time);
        mDStartTemp = (EditText) mDialog.findViewById(R.id.tv_sch_start_temp);

        mDEndTime = (EditText) mDialog.findViewById(R.id.tv_sch_end_time);
        mDEndTemp = (EditText) mDialog.findViewById(R.id.tv_sch_end_temp);

        mBtnSave = (Button)mDialog.findViewById(R.id.btn_save);
        iv_close = (ImageView) mDialog.findViewById(R.id.iv_close);

        img_start_clear = (ImageView) mDialog.findViewById(R.id.img_start_clear);
        img_end_clear = (ImageView) mDialog.findViewById(R.id.img_end_clear);

        img_start_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDStartTime.setText("");
                mDStartTemp.setText("");
                mModeSpinnerStart.setSelection(0);
            }
        });
        img_end_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDEndTime.setText("");
                mDEndTemp.setText("");
                mModeSpinnerEnd.setSelection(0);
            }
        });

        speedList.add(0,"MODE");

        CustomSpinnerAdapter customAdapter =
                new CustomSpinnerAdapter(this,R.layout.row_spinner_dropdown_item,R.id.spinner_item,speedList);
        mModeSpinnerStart.setAdapter(customAdapter);

        mModeSpinnerEnd.setAdapter(customAdapter);

        text_schedule_1 = (TextView) mDialog.findViewById(R.id.text_day_1);
        text_schedule_2 = (TextView) mDialog.findViewById(R.id.text_day_2);
        text_schedule_3 = (TextView) mDialog.findViewById(R.id.text_day_3);
        text_schedule_4 = (TextView) mDialog.findViewById(R.id.text_day_4);
        text_schedule_5 = (TextView) mDialog.findViewById(R.id.text_day_5);
        text_schedule_6 = (TextView) mDialog.findViewById(R.id.text_day_6);
        text_schedule_7 = (TextView) mDialog.findViewById(R.id.text_day_7);

       // text_schedule_1.setOnClickListener(this);
        text_schedule_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_1);
            }
        });
        text_schedule_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_2);
            }
        });
        text_schedule_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_3);
            }
        });
        text_schedule_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_4);
            }
        });
        text_schedule_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_5);
            }
        });
        text_schedule_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_6);
            }
        });
        text_schedule_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setOnOffBackground(getApplicationContext(), text_schedule_7);
            }
        });

        text_schedule_1.setTag(false);
        text_schedule_2.setTag(false);
        text_schedule_3.setTag(false);
        text_schedule_4.setTag(false);
        text_schedule_5.setTag(false);
        text_schedule_6.setTag(false);
        text_schedule_7.setTag(false);

        iv_close.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

        mDStartTime.setOnClickListener(this);
        mDEndTime.setOnClickListener(this);

        if(!isEdit){
            Common.setBackground(this, text_schedule_1, true);
            Common.setBackground(this, text_schedule_2, true);
            Common.setBackground(this, text_schedule_3, true);
            Common.setBackground(this, text_schedule_4, true);
            Common.setBackground(this, text_schedule_5, true);
            Common.setBackground(this, text_schedule_6, true);
            Common.setBackground(this, text_schedule_7, true);
        }else{

            //if Edit sch all content paste here
            //tmpRemoteList
            mDiaSchName.setText(tmpRemoteList.getIrScheduleName());
            mDStartTime.setText(tmpRemoteList.getStartTime());

            if(!TextUtils.isEmpty(tmpRemoteList.getStartMode())){

                String mModeNameStart = tmpRemoteList.getStartMode();
                int sPosStart = customAdapter.getPosition(mModeNameStart);
                mModeSpinnerStart.setSelection(sPosStart);
            }

            mDStartTemp.setText(""+tmpRemoteList.getStartTemperature());

            mDEndTime.setText(tmpRemoteList.getEndTime());
            if(!TextUtils.isEmpty(tmpRemoteList.getEndMode())){

                String mModeNameEnd = tmpRemoteList.getEndMode();
                int sPosEnd = customAdapter.getPosition(mModeNameEnd);
                mModeSpinnerEnd.setSelection(sPosEnd);
            }
            mDEndTemp.setText(""+tmpRemoteList.getEndTemperature());

            String mRepeatDays = tmpRemoteList.getScheduleDay();
            if (mRepeatDays.contains("0")) {
                Common.setBackground(this, text_schedule_1, true);
            }
            if (mRepeatDays.contains("1")) {
                Common.setBackground(this, text_schedule_2, true);
            }
            if (mRepeatDays.contains("2")) {
                Common.setBackground(this, text_schedule_3, true);
            }
            if (mRepeatDays.contains("3")) {
                Common.setBackground(this, text_schedule_4, true);
            }
            if (mRepeatDays.contains("4")) {
                Common.setBackground(this, text_schedule_5, true);
            }
            if (mRepeatDays.contains("5")) {
                Common.setBackground(this, text_schedule_6, true);
            }
            if (mRepeatDays.contains("6")) {
                Common.setBackground(this, text_schedule_7, true);
            }
            repeatDayString = "";
        }

        if(!mDialog.isShowing()){
            mDialog.show();
        }
    }

    private void dismissDialog(){
        if(mDialog != null){
            mDialog.dismiss();
            isEdit = false;
            mDialog = null;
        }
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
        ChatApplication.logDisplay( " repeatDayString " + repeatDayString);

    }

    @Override
    public void onClickActive(RemoteSchListRes.Data.RemoteScheduleList remoteList) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+R.string.disconnect);
            return;
        }

        int isActive = remoteList.getIsActive() == 1 ? 0 : 1;

        JSONObject object = new JSONObject();
        try {
            object.put("schedule_id",remoteList.getScheduleId());
            object.put("is_active",isActive);
            object.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.CHANGE_REMOTE_SCHEDULE_STATUS;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(!TextUtils.isEmpty(message)){
                        showToast(message);
                    }

                    if(code == 200){
                        getSchDetails();
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

    private RemoteSchListRes.Data.RemoteScheduleList tmpRemoteList;
    private boolean isEdit = false;
    @Override
    public void onClickEdit(RemoteSchListRes.Data.RemoteScheduleList remoteList) {
        tmpRemoteList = null;
        tmpRemoteList = remoteList;
        isEdit = true;
        addSch(true);
    }

    @Override
    public void onClickDelete(RemoteSchListRes.Data.RemoteScheduleList remoteList) {
        showDialog(remoteList);
    }
    private void showDialog(final RemoteSchListRes.Data.RemoteScheduleList remoteList){
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure ?",new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteSchedule(remoteList);
            }
            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void deleteSchedule(RemoteSchListRes.Data.RemoteScheduleList remoteList){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast(""+ R.string.disconnect);
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("remote_id",remoteList.getRemoteId());
            object.put("schedule_id",remoteList.getScheduleId());
            object.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_REMOTE_SCHEDULE;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(!TextUtils.isEmpty(message)){
                        showToast(message);
                    }
                    if(code == 200){
                        getSchDetails();
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
}
