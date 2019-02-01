package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.adapter.DeviceLogAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.AddRemoteReq;
import com.spike.bot.model.DataSearch;
import com.spike.bot.model.DeviceBrandRemoteList;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.IRRemoteOnOffReq;
import com.spike.bot.model.IRRemoteOnOffRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TEST_BUTTONS = "Test buttons";
    private TextView mTestButtons;
    private ImageView mImgLeft, mImgRight;
    private ImageView mImgPower;
    private LinearLayout mRespondView;
    private TextView mRespondNo;
    private TextView mRespondYes;
    private TextView mPowerValue;
    private TextView mTxtBlasterName,txtModelNumber;

    private String mIRDeviceId, mIrDeviceType, mRoomId;
    private String mBrandId;
    private String mIRBlasterModuleId;
    private String mIRBrandType;
    private String mIRBLasterId;
    private String mRoomName;
    private String mBlasterName;
    private String mBrandType,brand_name="",model_number="",onOffValue="",remote_codeset_id="";

    private Spinner mSpinnerBlaster, mSpinnerMode;
    private TextView remote_room_txt;
    private EditText mRemoteDefaultTemp;


    IRRemoteOnOffRes irRemoteOnOffRes;

    private String mOn, mOff, mCodeSet;
    private boolean isRequestTypeOn = false;

    private int mTotalState = 1;
    private int mCurrentState = 1;
    private int RESPOND_CONST = 1; //1 on command : 2 off command

    public DataSearch arrayList;

    public static class Power {

        public static String ON = "On";
        public static String OFF = "Off";
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_config);

        arrayList=( DataSearch)getIntent().getSerializableExtra("arrayList");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Remote Config");

        isRequestTypeOn = true;
        RESPOND_CONST = 1;

        syncIntent();

        bindView();

        //getIRRemoteDetails();
    }

    private void syncIntent() {
        mBrandType = getIntent().getStringExtra("BRAND_NAME");
        mBlasterName = getIntent().getStringExtra("BLASTER_NAME");
        mRoomName = getIntent().getStringExtra("ROOM_NAME");
        mIRBLasterId = getIntent().getStringExtra("SENSOR_ID");
        mIRBrandType = getIntent().getStringExtra("IR_BRAND_TYPE");
        mRoomId = getIntent().getStringExtra("ROOM_ID");
        mIrDeviceType = getIntent().getStringExtra("IR_DEVICE_TYPE");
        mIRDeviceId = getIntent().getStringExtra("IR_DEVICE_ID");
        mBrandId = getIntent().getStringExtra("BRAND_ID");
        mIRBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_MODULE_ID");

        Log.i("BlasterName", "Found : " + mBlasterName);
        getSupportActionBar().setTitle("" + mBrandType);

        if(arrayList.getDeviceBrandRemoteList()!=null &&
                arrayList.getDeviceBrandRemoteList().size()>0){
            onOffValue = arrayList.getDeviceBrandRemoteList().get(0).getIrCode();
            model_number = arrayList.getDeviceBrandRemoteList().get(0).getModelNumber();
            brand_name = arrayList.getDeviceBrandRemoteList().get(0).getBrandName();
            remote_codeset_id = ""+arrayList.getDeviceBrandRemoteList().get(0).getremote_codeset_id();
        }
    }

    private void bindView() {

        mTestButtons = (TextView) findViewById(R.id.remote_total_step);
        mImgLeft = (ImageView) findViewById(R.id.remote_left_slider);
        mImgRight = (ImageView) findViewById(R.id.remote_right_slider);
        mImgPower = (ImageView) findViewById(R.id.remote_power_onoff);
        mPowerValue = (TextView) findViewById(R.id.remote_power_value);

        mRespondView = (LinearLayout) findViewById(R.id.remote_respond_view);
        mRespondNo = (TextView) findViewById(R.id.remote_respond_no);
        mRespondYes = (TextView) findViewById(R.id.remote_respond_yes);
        txtModelNumber = (TextView) findViewById(R.id.txtModelNumber);

        mImgLeft.setOnClickListener(this);
        mImgRight.setOnClickListener(this);
        mImgPower.setOnClickListener(this);
        mRespondNo.setOnClickListener(this);
        mRespondYes.setOnClickListener(this);

        hideRespondView();
        hideLeftSlider();
        initData(arrayList.getDeviceBrandRemoteList());
        setStepText(mCurrentState);
        setPowerValue(Power.ON);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        MenuItem menuItemReset = menu.findItem(R.id.action_reset);
        menuItemReset.setVisible(false);
        menuItem.setVisible(true);
        menuItem.setIcon(R.drawable.ic_search_black_24dp);

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
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("mBrandId",""+mBrandId);
            intent.putExtra("arrayList",arrayList);
            startActivityForResult(intent, 1);
            return true;
        }else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {


                hideRespondView();
                respondNoEvent();
                setPowerValue(Power.ON);
                onOffValue = data.getStringExtra("onOffValue");
                brand_name = data.getStringExtra("brand_name");
                remote_codeset_id = data.getStringExtra("remote_codeset_id");
                model_number = data.getStringExtra("model_number");
                txtModelNumber.setText("Model : "+model_number);
                ChatApplication.logDisplay("model_number is "+model_number);
//
//                try {
//                    JSONObject object=new JSONObject(onOffValue.toString().trim());
//                    onValue=object.optString("ON");
//                    offValue=object.optString("OFF");
//
//                    ChatApplication.logDisplay("on value is "+onValue);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void getIRRemoteDetails() {

//        if (!ActivityHelper.isConnectingToInternet(this)) {
//            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String url = ChatApplication.url + Constants.GET_IR_DEVICE_DETAILS + "/" + mIRDeviceId + "/" + mBrandId;
//        new GetJsonTask(this, url, "GET", "", new ICallBack() {
//            @Override
//            public void onSuccess(JSONObject result) {
//
//                try {
//
//                    int code = result.getInt("code");
//                    String message = result.getString("message");
//
//                    if (code == 200) {
//
//                        Log.d("IRDeviceDetails", "remote res : " + result.toString());
//                        irRemoteOnOffRes = Common.jsonToPojo(result.toString(), IRRemoteOnOffRes.class);
//                        initData(irRemoteOnOffRes.getData());
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String error) {
//                throwable.printStackTrace();
//            }
//        }).execute();

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(IRRemoteConfigActivity.this, "Please Wait...", false);

        String url = ChatApplication.url + Constants.getDeviceBrandRemoteList + "/" + mBrandId;
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        arrayList = Common.jsonToPojo(result.getString("data").toString(), DataSearch.class);

                        //initData(irRemoteOnOffRes.getData());
                        initData(arrayList.getDeviceBrandRemoteList());

                        if(arrayList.getDeviceBrandRemoteList()!=null &&
                            arrayList.getDeviceBrandRemoteList().size()>0){
                            onOffValue = arrayList.getDeviceBrandRemoteList().get(0).getIrCode();
                            model_number = arrayList.getDeviceBrandRemoteList().get(0).getModelNumber();
                            brand_name = arrayList.getDeviceBrandRemoteList().get(0).getBrandName();
                            remote_codeset_id = ""+arrayList.getDeviceBrandRemoteList().get(0).getremote_codeset_id();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * @param data
     */
   // private List<IRRemoteOnOffRes.Data.OnoffList> mOnoffLists;

    private void initData(List<DeviceBrandRemoteList> data) {

//        mOnoffLists = data.getOnoffList();
        mTotalState = data.size();

//        mCodeSet = data.getOnoffList().get(0).getId();
//        mOn = data.getOnoffList().get(0).getON();
//        mOff = data.getOnoffList().get(0).getOFF();

        setTotalText(mTotalState);
        if(data.size()>0){
            model_number=data.get(0).getModelNumber();
            txtModelNumber.setText("Model : "+model_number);
            onOffValue=arrayList.getDeviceBrandRemoteList().get(0).getIrCode();
            remote_codeset_id=""+arrayList.getDeviceBrandRemoteList().get(0).getremote_codeset_id();
            brand_name=arrayList.getDeviceBrandRemoteList().get(0).getBrandName();
        }
    }

    /**
     * update current remote state in const
     */
    private void getOnOffCommandPositon() {

//        if (mOnoffLists != null) {
//            mCodeSet = mOnoffLists.get(mCurrentState - 1).getId();
//            mOn = mOnoffLists.get(mCurrentState - 1).getON();
//            mOff = mOnoffLists.get(mCurrentState - 1).getOFF();
//        }
    }

    /**
     * send OnOff Request when user press power button
     */
    private void sendOnOfOffRequest() {

        isRequestTypeOn = !isRequestTypeOn;

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        IRRemoteOnOffReq irRemoteOnOffReq = new IRRemoteOnOffReq(mIRBlasterModuleId, mCodeSet, (RESPOND_CONST == 1) ? "ON" : "OFF"
                , APIConst.PHONE_ID_VALUE, APIConst.PHONE_TYPE_VALUE,onOffValue);
        Gson gson = new Gson();
        String mStrOnOffReq = gson.toJson(irRemoteOnOffReq);

        Log.i("OnOffRequest", "Request : " + mStrOnOffReq);

        String url = ChatApplication.url + Constants.SEND_ON_OFF_COMMAND;
        new GetJsonTask(this, url, "POST", mStrOnOffReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    if (result != null && !TextUtils.isEmpty(result.toString())) {
                        int code = result.getInt("code");
                        String message = result.getString("message");

                        if (code == 200) {

                            Log.d("IRDeviceDetails", "remote res : " + result.toString());
                            showRespondView();

                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    resetConfig();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
            }
        }).execute();
    }

    EditText mEdtRemoteName;
    private Dialog mDialog;

    private void showRemoteSaveDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_save_remote);
        }

        mEdtRemoteName = mDialog.findViewById(R.id.edt_remote_name);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Remote Name max length to set only 25
        mEdtRemoteName.setFilters(filterArray);

        ImageView iv_close = (ImageView) mDialog.findViewById(R.id.iv_close);
        Button mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button mBtnSave = mDialog.findViewById(R.id.btn_save);

        mSpinnerBlaster = (Spinner) mDialog.findViewById(R.id.remote_blaster_spinner);
        remote_room_txt = (TextView) mDialog.findViewById(R.id.remote_room_txt);
        mSpinnerMode = (Spinner) mDialog.findViewById(R.id.remote_mode_spinner);

        mRemoteDefaultTemp = (EditText) mDialog.findViewById(R.id.edt_remote_tmp);
        mRemoteDefaultTemp.setText("");
        mEdtRemoteName.setText("");

        mTxtBlasterName = (TextView) mDialog.findViewById(R.id.txt_blastername);

        mRemoteDefaultTemp.setError(null);
        mEdtRemoteName.requestFocus();

        remote_room_txt.setText("" + mBlasterName);
        mTxtBlasterName.setText("" + mRoomName);

        // getIRBlasterList();

        String[] moodList = new String[]{"Select Mode", "AUTO", "LOW", "MEDIUM", "HIGH"};
        final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, moodList);
        mSpinnerMode.setAdapter(roomAdapter1);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSaveDialog();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSaveDialog();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatApplication.logDisplay(" mNumber is "+model_number);
                ChatApplication.logDisplay(" mNumber is "+brand_name);
                ChatApplication.logDisplay(" mNumber is "+onOffValue);
                ChatApplication.logDisplay(" mNumber is "+remote_codeset_id);

                if (TextUtils.isEmpty(mEdtRemoteName.getText().toString().trim())) {
                    mEdtRemoteName.requestFocus();
                    mEdtRemoteName.setError("Enter Remote name");
                    return;
                }

                if (TextUtils.isEmpty(mRemoteDefaultTemp.getText().toString().trim())) {
                    mRemoteDefaultTemp.requestFocus();
                    mRemoteDefaultTemp.setError("Enter Remote Default Temp");
                    return;
                }

                int mDefaultTemp = Integer.parseInt(mRemoteDefaultTemp.getText().toString());
                if (mDefaultTemp >= 17 && mDefaultTemp <= 30) {

                } else {
                    mRemoteDefaultTemp.requestFocus();
                    mRemoteDefaultTemp.setError("Default Temp range between 17 to 30");
                    return;
                }

                if (mSpinnerMode.getSelectedItemPosition() == 0) {
                    Common.showToast("Select Mode");
                    return;
                }
                saveRemote(mEdtRemoteName.getText().toString().trim());
            }
        });

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void hideSaveDialog() {
        if (mEdtRemoteName != null) {
            hideSoftKeyboard(mEdtRemoteName);
        }
        if (mRemoteDefaultTemp != null) {
            hideSoftKeyboard(mRemoteDefaultTemp);
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    private List<IRBlasterAddRes.Data.IrList> irList;
    List<IRBlasterAddRes.Data.RoomList> roomLists;

    /**
     * get IR Blaster list
     */
    private void getIRBlasterList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String URL = ChatApplication.url + Constants.GET_IR_BLASTER_LIST;


        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(this, URL, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d("getIRBlasterList", "result : " + result.toString());

                IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(), IRBlasterAddRes.class);
                if (irBlasterAddRes.getCode() == 200) {
                    irList = irBlasterAddRes.getData().getIrList();
                    roomLists = irBlasterAddRes.getData().getRoomList();

                    final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, irList);
                    mSpinnerBlaster.setAdapter(roomAdapter1);

                    remote_room_txt.setText("" + mSpinnerBlaster.getSelectedItem().toString());

                    for (IRBlasterAddRes.Data.IrList ir : irList) {
                        if (ir.getIrBlasterName().equalsIgnoreCase(mBlasterName)) {
                            remote_room_txt.setText("" + ir.getRoomName());
                        }
                    }

                   /* mSpinnerBlaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            IRBlasterAddRes.Data.IrList irList = (IRBlasterAddRes.Data.IrList) mSpinnerBlaster.getSelectedItem();
                            remote_room_txt.setText(""+irList.getRoomName());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });*/

                } else {
                    Common.showToast(irBlasterAddRes.getMessage());
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    /**
     * save Remote
     *
     * @param remoteName
     */
    private void saveRemote(String remoteName) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        AddRemoteReq addRemoteReq = new AddRemoteReq(mIRDeviceId, mIrDeviceType, mBrandId, mIRBrandType, mCodeSet,
                remoteName, mIRBLasterId, mIRBlasterModuleId, mRoomId, mSpinnerMode.getSelectedItem().toString(), mRemoteDefaultTemp.getText().toString().trim(),
                APIConst.PHONE_ID_KEY, APIConst.PHONE_TYPE_VALUE,brand_name,remote_codeset_id,model_number,onOffValue);
        addRemoteReq.setUpdate_type(0);

        Gson gson = new Gson();
        String mStrOnOffReq = gson.toJson(addRemoteReq);

        Log.i("IRDeviceDetails", "Request : " + mStrOnOffReq);

        String url = ChatApplication.url + Constants.ADD_REMOTE;
        new GetJsonTask(this, url, "POST", mStrOnOffReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        ChatApplication.isEditActivityNeedResume = true;
                        hideSaveDialog();
                        Log.d("IRDeviceDetails", "remote res : " + result.toString());
                        hideRespondView();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
            }
        }).execute();
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.remote_left_slider:
                leftTestEvent();
                break;
            case R.id.remote_right_slider:
                rightTestEvent();
                break;
            case R.id.remote_power_onoff:
                powerOnOffEvent();
                break;
            case R.id.remote_respond_no:
                respondNoEvent();
                break;
            case R.id.remote_respond_yes:
                respondYesEvent();
                break;
        }
    }

    /**
     * set Power value on or off
     *
     * @param text
     */
    private void setPowerValue(String text) {
        mPowerValue.setText(text);
    }

    private void leftTestEvent() {
        RESPOND_CONST = 1;
        stateDown();
        setPowerValue(Power.ON);
    }

    private void rightTestEvent() {
        RESPOND_CONST = 1;
        stateUp();
        setPowerValue(Power.ON);
    }

    private void powerOnOffEvent() {
        if (mRespondView.getVisibility() != View.VISIBLE) //Request can't sent if respond view dialog already appear in screen
            sendOnOfOffRequest();
    }

    private void respondNoEvent() {
        RESPOND_CONST = 1;

        //TODO code here for update or redirect to first remote config(first remote test status) when current state will reach to lastState
        if (mCurrentState == mTotalState) {
            mCurrentState = 1;
            showRightSlider();
            hideLeftSlider();
            setStepText(mCurrentState);
        } else {
            stateUp();
        }

        hideRespondView();
        setPowerValue(Power.ON);

    }

    private void respondYesEvent() {
        if (RESPOND_CONST == 2) {
            resetConfig();
            showRemoteSaveDialog();
            return;
        }
        RESPOND_CONST = 2; //send next request for off command
        hideRespondView();

    }

    private void hideRespondView() {
        mRespondView.setVisibility(View.INVISIBLE);
        mRespondView.setBackgroundColor(Color.WHITE);
    }

    private void showRespondView() {

        if (RESPOND_CONST == 1) {
            setPowerValue(Power.ON);
        } else if (RESPOND_CONST == 2) {
            setPowerValue(Power.OFF);
        }

        mRespondView.setVisibility(View.VISIBLE);
        mRespondView.setBackgroundColor(Color.parseColor("#20808080"));
    }

    private void hideLeftSlider() {
        mImgLeft.setVisibility(View.INVISIBLE);
    }

    private void showLeftSlider() {
        mImgLeft.setVisibility(View.VISIBLE);
    }

    private void hideRightSlider() {
        mImgRight.setVisibility(View.INVISIBLE);
    }

    private void showRightSlider() {
        mImgRight.setVisibility(View.VISIBLE);
    }

    /**
     * Remote current Temp state down
     * Down until not reach min remote state
     */
    private void stateDown() {

        if (mCurrentState == -1) {
            mCurrentState = 1;
        } else {
            mCurrentState--;
        }

        if (mCurrentState == 1) {
            hideLeftSlider();
        }
        if (mCurrentState != mTotalState) {
            showRightSlider();
        }
        setStepText(mCurrentState);
    }

    /**
     * Remote current Temp state up
     * Up until not reach temp max state up
     */
    private void stateUp() {
        if (mCurrentState != mTotalState) {
            mCurrentState++;
        }
        if (mCurrentState >= 2) {
            showLeftSlider();
        }
        if (mCurrentState == mTotalState) {
            hideRightSlider();
        } else {
            showRightSlider();
        }
        setStepText(mCurrentState);

    }

    private void setStepText(int state) {

        getOnOffCommandPositon();
        mTestButtons.setText("Test buttons(" + state + "/" + mTotalState + ")");
        if(state>0){
            model_number=arrayList.getDeviceBrandRemoteList().get(state-1).getModelNumber();
            onOffValue=arrayList.getDeviceBrandRemoteList().get(state-1).getIrCode();
            remote_codeset_id=""+arrayList.getDeviceBrandRemoteList().get(state-1).getremote_codeset_id();
            brand_name=arrayList.getDeviceBrandRemoteList().get(state-1).getBrandName();
        }else {
            model_number=arrayList.getDeviceBrandRemoteList().get(state).getModelNumber();
            onOffValue=arrayList.getDeviceBrandRemoteList().get(state).getIrCode();
            remote_codeset_id=""+arrayList.getDeviceBrandRemoteList().get(state).getremote_codeset_id();
            brand_name=arrayList.getDeviceBrandRemoteList().get(state).getBrandName();
        }

        txtModelNumber.setText("Model : "+model_number);
    }

    /**
     * Set Total test button like : 1/17)
     *
     * @param state
     */
    private void setTotalText(int state) {
        mTestButtons.setText("Test buttons(" + mCurrentState + "/" + state + ")");
    }

    /**
     * Reset remote config
     */
    private void resetConfig() {
        RESPOND_CONST = 1;
        setPowerValue(Power.ON);
        hideRespondView();
    }
}
