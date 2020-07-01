package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.AddRemoteReq;
import com.spike.bot.model.DataSearch;
import com.spike.bot.model.DeviceBrandRemoteList;
import com.spike.bot.model.IRRemoteOnOffReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 *
 * activity drc
 * first on click than set show yes or no dialog than check ac responed or not if yes than click yes & click to no than check to ac reposend or not
 *
 */
public class IRRemoteConfigActivity extends AppCompatActivity implements View.OnClickListener {

    public static DataSearch arrayList;
    private TextView remote_room_txt, mTestButtons, mRespondNo, mRespondYes, mPowerValue, mTxtBlasterName, txtModelNumber;
    private ImageView mImgLeft, mImgRight,mImgPower;
    private LinearLayout mRespondView;
    private Spinner  mSpinnerMode;
    private EditText mRemoteDefaultTemp,mEdtRemoteName;

    private String mIRDeviceId, mIrDeviceType, mRoomId, mBrandId, mIRBlasterModuleId, mIRBrandType, mIRBLasterId, mRoomName,
    mBlasterName, mBrandType, brand_name = "", model_number = "", onOffValue = "",
    remote_codeset_id = "", mCodeSet;

    private boolean isRequestTypeOn = false;
    private int mTotalState = 1, mCurrentState = 1, RESPOND_CONST = 1; //1 on command : 2 off command
    private Dialog mDialog;

    public static class Power {
        public static String ON = "On";
        public static String OFF = "Off";
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_config);

        arrayList = IRRemoteBrandListActivity.arrayList;


        isRequestTypeOn = true;
        RESPOND_CONST = 1;

        syncIntent();
        bindView();
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

        if (arrayList.getDeviceBrandRemoteList() != null &&
                arrayList.getDeviceBrandRemoteList().size() > 0) {
            onOffValue = arrayList.getDeviceBrandRemoteList().get(0).getIrCode();
            model_number = arrayList.getDeviceBrandRemoteList().get(0).getModelNumber();
            brand_name = arrayList.getDeviceBrandRemoteList().get(0).getBrandName();
            remote_codeset_id = "" + arrayList.getDeviceBrandRemoteList().get(0).getremote_codeset_id();
        }
    }

    private void bindView() {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("Remote Config");
        getSupportActionBar().setTitle("" + mBrandType);

        mTestButtons =  findViewById(R.id.remote_total_step);
        mImgLeft =  findViewById(R.id.remote_left_slider);
        mImgRight =  findViewById(R.id.remote_right_slider);
        mImgPower =  findViewById(R.id.remote_power_onoff);
        mPowerValue =  findViewById(R.id.remote_power_value);

        mRespondView =  findViewById(R.id.remote_respond_view);
        mRespondNo =  findViewById(R.id.remote_respond_no);
        mRespondYes =  findViewById(R.id.remote_respond_yes);
        txtModelNumber =  findViewById(R.id.txtModelNumber);

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
            intent.putExtra("mBrandId", "" + mBrandId);
            startActivityForResult(intent, 1);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                hideRespondView();
                respondNoEvent();
                setPowerValue(Power.ON);
                onOffValue = data.getStringExtra("onOffValue");
                brand_name = data.getStringExtra("brand_name");
                remote_codeset_id = data.getStringExtra("remote_codeset_id");
                model_number = data.getStringExtra("model_number");
                txtModelNumber.setText("Model : " + model_number);
                ChatApplication.logDisplay("model_number is " + model_number);
            }
        }
    }


    private void initData(List<DeviceBrandRemoteList> data) {

        mTotalState = data.size();

        setTotalText(mTotalState);
        if (data.size() > 0) {
            model_number = data.get(0).getModelNumber();
            txtModelNumber.setText("Model : " + model_number);
            onOffValue = arrayList.getDeviceBrandRemoteList().get(0).getIrCode();
            remote_codeset_id = "" + arrayList.getDeviceBrandRemoteList().get(0).getremote_codeset_id();
            brand_name = arrayList.getDeviceBrandRemoteList().get(0).getBrandName();
        }
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

        IRRemoteOnOffReq irRemoteOnOffReq = new IRRemoteOnOffReq(mIRBlasterModuleId, mCodeSet, (RESPOND_CONST == 1) ? "ON" : "OFF", APIConst.PHONE_ID_VALUE, APIConst.PHONE_TYPE_VALUE, onOffValue);
        Gson gson = new Gson();
        String mStrOnOffReq = gson.toJson(irRemoteOnOffReq);

        SpikeBotApi.getInstance().sendOnOfOffRequest(mIRBlasterModuleId, (RESPOND_CONST == 1) ? "ON" : "OFF", onOffValue, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("response : " + result.toString());
                    if (result != null && !TextUtils.isEmpty(result.toString())) {
                        int code = result.getInt("code");
                        String message = result.getString("message");

                        if (code == 200) {

                            ChatApplication.logDisplay("remote res : " + result.toString());
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
            public void onData_FailureResponse() {

            }
        });

    }

    /*save remote dialog*/
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

        ImageView iv_close =  mDialog.findViewById(R.id.iv_close);
        Button mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button mBtnSave = mDialog.findViewById(R.id.btn_save);

        remote_room_txt =  mDialog.findViewById(R.id.remote_room_txt);
        mSpinnerMode =  mDialog.findViewById(R.id.remote_mode_spinner);

        mRemoteDefaultTemp =  mDialog.findViewById(R.id.edt_remote_tmp);
        mRemoteDefaultTemp.setText("");
        mEdtRemoteName.setText("");

        mTxtBlasterName =  mDialog.findViewById(R.id.txt_blastername);

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



      /*  ActivityHelper.showProgressDialog(IRRemoteConfigActivity.this, "Please Wait...", false);
        AddRemoteReq addRemoteReq = new AddRemoteReq(Common.getPrefValue(this, Constants.USER_ID), mIRDeviceId, mIrDeviceType, mBrandId, mIRBrandType, mCodeSet,
                remoteName, mIRBLasterId, mIRBlasterModuleId, mRoomId,  mSpinnerMode.getSelectedItem().toString()+"-"+mRemoteDefaultTemp.getText().toString().trim(),
                APIConst.PHONE_ID_KEY, APIConst.PHONE_TYPE_VALUE, brand_name, remote_codeset_id, model_number, onOffValue);
        addRemoteReq.setUpdate_type(0);

        Gson gson = new Gson();
        String mStrOnOffReq = gson.toJson(addRemoteReq);
        String url = ChatApplication.url + Constants.deviceadd;*/

        SpikeBotApi.getInstance().saveremote(mIRDeviceId, mIrDeviceType, remoteName, mIRBLasterId, mIRBlasterModuleId, mRoomId, mSpinnerMode.getSelectedItem().toString() + "-" + mRemoteDefaultTemp.getText().toString().trim(),
                mBrandId, mCodeSet, model_number, onOffValue, new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        try {
                            JSONObject result = new JSONObject(stringResponse);
                            int code = result.getInt("code");
                            String message = result.getString("message");

                            if (code == 200) {
                                ChatApplication.isEditActivityNeedResume = true;
                                hideSaveDialog();
                                ChatApplication.logDisplay("remote res : " + result.toString());
                                hideRespondView();
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ActivityHelper.dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                    }
                });
    }

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
        } else{
            setPowerValue(Power.OFF);
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

        mTestButtons.setText("Test buttons(" + state + "/" + mTotalState + ")");
        if (state > 0) {
            model_number = arrayList.getDeviceBrandRemoteList().get(state - 1).getModelNumber();
            onOffValue = arrayList.getDeviceBrandRemoteList().get(state - 1).getIrCode();
            remote_codeset_id = "" + arrayList.getDeviceBrandRemoteList().get(state - 1).getremote_codeset_id();
            brand_name = arrayList.getDeviceBrandRemoteList().get(state - 1).getBrandName();
        } else {
            model_number = arrayList.getDeviceBrandRemoteList().get(state).getModelNumber();
            onOffValue = arrayList.getDeviceBrandRemoteList().get(state).getIrCode();
            remote_codeset_id = "" + arrayList.getDeviceBrandRemoteList().get(state).getremote_codeset_id();
            brand_name = arrayList.getDeviceBrandRemoteList().get(state).getBrandName();
        }

        txtModelNumber.setText("Model : " + model_number);
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
