package com.spike.bot.activity.ir.blaster;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodiebag.protractorview.ProtractorView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.FanSpeedAcRemoteAdapter;
import com.spike.bot.adapter.MoodRemoteAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.ACFanSpeedEvent;
import com.spike.bot.listener.MoodEvent;
import com.spike.bot.model.IRDeviceDetailsRes;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.model.RemoteMoodModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 31/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterRemote extends AppCompatActivity implements View.OnClickListener, MoodEvent, ACFanSpeedEvent {

    private static String TURN_ON = "TURN ON", TURN_OFF = "TURN OFF";
    //    private List<String> speedList;
    public RecyclerView recyclerMode, recycleFanSpeed;
    public MoodRemoteAdapter moodRemoteAdapter;
    public FanSpeedAcRemoteAdapter mFanSpeedAdapter;
    public String[] moodList = new String[]{"Auto", "Cool", "Dry"};
    public String[] moodListDialog = new String[]{"Auto", "High", "Medium", "Low", "Dry"};
    public String[] mFanSpeedList = new String[]{"High", "Medium", "Low"};
    RelativeLayout relative_plus, relative_minus;
    ProtractorView protractorView1, protractorView;
    private LinearLayout mPowerButton;
    private Button mBrandBottom, mTempMinus, mTempPlus;
    private ImageView mImageAuto, mToolbarBack, imgRemoteStatus, mImageEdit, mImageDelete;
    private TextView mTemp, mDefaultTemp, mImageAutoText, mRemoteName, txtRemoteState, txtAcState, txtswingState;
    private String mRemoteId, moodName = "", mRoomDeviceId, module_id = "", modeType = "", modeTypeDef = "", strswing = "", fanspeed = "Medium", fanspeedDef = "Medium";
    private boolean isPowerOn = false;
    private int isRemoteActive, tempMinus, tempPlus, tempCurrent, tempdefaultstatus;
    private RemoteDetailsRes mRemoteList;
    private RemoteDetailsRes.Data mRemoteCommandList;
    private ArrayList<RemoteMoodModel> arrayListMood = new ArrayList<>();
    private ArrayList<RemoteMoodModel> arrayListFanSpeed = new ArrayList<>();
    private List<IRDeviceDetailsRes.Data> mIRDeviceList = new ArrayList<>();
    /**
     * Edit AC Remote
     */
    private Spinner mSpinnerBlaster, mSpinnerMode;
    private TextView remote_room_txt;
    private EditText mRemoteDefaultTemp;
    private EditText mEdtRemoteName;
    private Dialog mDialog;
    private TextView mDefaultValueHeading;
    private boolean isCoolMode;
    private Socket mSocket, cloudsocket;

    private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            //{"device_id":"1571671238168_3FH8phZUCh","device_type":"switch","device_status":0,"device_sub_status":0}
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("status update panel device " + object.toString());
                            String device_type = object.getString("device_type");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");

                            String device_sub_status = "";

                            if (object.has("device_sub_status")) {
                                device_sub_status = object.getString("device_sub_status");
                            }

                            String value[] = device_sub_status.split("-");
                            modeType = value[0];
                            tempCurrent = Integer.parseInt(value[1]);
                            int status = Integer.parseInt(device_status);
                            mTemp.setText("" + tempCurrent + Common.getC());
                            setangle(tempCurrent);
                            updateMoodUi(value);
                            updateFanSpeedUi(true, value);


                            isPowerOn = status == 1 ? true : false;

                            txtRemoteState.setTextColor(status == 1 ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.username4));
                            txtRemoteState.setText(status == 1 ? "Turn off" : "Turn on");
                            mPowerButton.setBackground(status == 1 ? getResources().getDrawable(R.drawable.drawable_turn_on_btn) : getResources().getDrawable(R.drawable.drawable_turn_off_btn));
                            imgRemoteStatus.setImageDrawable(status == 1 ? getResources().getDrawable(R.drawable.ac_power_off) : getResources().getDrawable(R.drawable.ac_power_on));


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_blaster_remote);

        syncIntent();
        bindView();

        /*for ir list getting*/
        getIRDeviceDetails();
    }

    private void syncIntent() {
        mRemoteId = getIntent().getStringExtra("IR_BLASTER_ID");
        isRemoteActive = getIntent().getIntExtra("REMOTE_IS_ACTIVE", 0);
//        mRoomDeviceId = getIntent().getStringExtra("ROOM_DEVICE_ID");
//        module_id = getIntent().getStringExtra("IR_MODULE_ID");
    }

    private void bindView() {

        mImageEdit = findViewById(R.id.action_edit);
        mImageDelete = findViewById(R.id.action_delete);
        mBrandBottom = findViewById(R.id.brand_name_bottom);
        mToolbarBack = findViewById(R.id.remote_toolbar_back);
        mRemoteName = findViewById(R.id.remote_name);
        txtAcState = findViewById(R.id.txtAcState);
        mTemp = findViewById(R.id.remote_temp);
        mDefaultTemp = findViewById(R.id.remote_default_temp);
        mImageAuto = findViewById(R.id.remote_speed_img);
        imgRemoteStatus = findViewById(R.id.imgRemoteStatus);
        mImageAutoText = findViewById(R.id.remote_speed_text);
        txtRemoteState = findViewById(R.id.txtRemoteState);
        txtswingState = findViewById(R.id.txtswingState);
        mPowerButton = findViewById(R.id.remote_power_button);
        mTempMinus = findViewById(R.id.remote_temp_minus);
        mTempPlus = findViewById(R.id.remote_temp_plus);
        recyclerMode = findViewById(R.id.recyclerMode);
        recycleFanSpeed = findViewById(R.id.recyclerFanspeed);
        protractorView = findViewById(R.id.Protractor);
        protractorView1 = findViewById(R.id.Protractor1);
        relative_plus = findViewById(R.id.relative_plus);
        relative_minus = findViewById(R.id.relative_minus);
        mDefaultValueHeading = findViewById(R.id.txt_default_title);


        if (!Common.getPrefValue(IRBlasterRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            mImageEdit.setVisibility(View.VISIBLE);
        } else {
            mImageEdit.setVisibility(View.GONE);
        }

        mToolbarBack.setOnClickListener(this);
        mPowerButton.setOnClickListener(this);
        mTempMinus.setOnClickListener(this);
        mTempPlus.setOnClickListener(this);
        mImageEdit.setOnClickListener(this);
        mImageDelete.setOnClickListener(this);
        relative_plus.setOnClickListener(this);
        relative_minus.setOnClickListener(this);
        txtswingState.setOnClickListener(this);

        protractorView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (!Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equals("1")) {
            mImageDelete.setVisibility(View.GONE);
        }

        startSocketConnection();

    }

    /*set adapter*/
    private void setMoodAdapter() {
        arrayListMood.clear();

        for (int i = 0; i < moodList.length; i++) {
            RemoteMoodModel remoteMoodModel = new RemoteMoodModel();
            remoteMoodModel.setMoodName(moodList[i]);
            remoteMoodModel.setSelect(false);
            arrayListMood.add(remoteMoodModel);
        }

        String value[] = mRemoteCommandList.getDevice().getDeviceSubStatus().split("-");
        updateMoodUi(value);
        seFanSpeedAdapter(true);
    }

    private void updateMoodUi(String value[]) {

        modeType = value[0];
        tempdefaultstatus = Integer.parseInt(value[1]);
        for (int i = 0; i < arrayListMood.size(); i++) {
            if (arrayListMood.get(i).getMoodName().toLowerCase().equals(modeType.toLowerCase())) {
                arrayListMood.get(i).setSelect(true);
                recycleFanSpeed.setAlpha(0.5f);
                isCoolMode = false;
            } else if (modeType.toLowerCase().equals("high") || (modeType.toLowerCase().equals("medium")) || (modeType.toLowerCase().toLowerCase().equals("low"))) {
                arrayListMood.get(i).setSelect(false);
                arrayListMood.get(1).setSelect(true);
                isCoolMode = true;
                recycleFanSpeed.setAlpha(1f);
            } else {
                arrayListMood.get(i).setSelect(false);
                recycleFanSpeed.setAlpha(0.5f);
                isCoolMode = false;

            }
        }

        recyclerMode.setLayoutManager(new GridLayoutManager(this, 3));
        moodRemoteAdapter = new MoodRemoteAdapter(IRBlasterRemote.this, arrayListMood, this);
        recyclerMode.setAdapter(moodRemoteAdapter);

    }

    /*Set fan Speed*/
    private void seFanSpeedAdapter(boolean isChange) {
        arrayListFanSpeed.clear();

        for (int i = 0; i < mFanSpeedList.length; i++) {
            RemoteMoodModel remoteMoodModel = new RemoteMoodModel();
            remoteMoodModel.setMoodName(mFanSpeedList[i]);
            remoteMoodModel.setSelect(false);
            arrayListFanSpeed.add(remoteMoodModel);
        }

        String value[] = mRemoteCommandList.getDevice().getDeviceSubStatus().split("-");
        updateFanSpeedUi(isChange, value);
    }

    private void updateFanSpeedUi(boolean isChange, String value[]) {

        if (isChange) {
            fanspeed = value[0];

            if (fanspeed.toLowerCase().equals("dry") || (fanspeed.toLowerCase().equals("auto"))) {
                fanspeed = "Auto";
            }

            tempdefaultstatus = Integer.parseInt(value[1]);
        }

        for (int i = 0; i < arrayListFanSpeed.size(); i++) {
            if (arrayListFanSpeed.get(i).getMoodName().toLowerCase().equals(fanspeed.toLowerCase())) {
                arrayListFanSpeed.get(i).setSelect(true);
            } else {
                arrayListFanSpeed.get(i).setSelect(false);
            }
        }
        recycleFanSpeed.setLayoutManager(new GridLayoutManager(this, 3));
        mFanSpeedAdapter = new FanSpeedAcRemoteAdapter(IRBlasterRemote.this, arrayListFanSpeed, this, isCoolMode);
        recycleFanSpeed.setAdapter(mFanSpeedAdapter);
    }


    private void initView() {
        setMoodAdapter();
        /*for mode*/
        String value[] = mRemoteCommandList.getDevice().getDeviceSubStatus().split("-");
        modeType = value[0];
        tempCurrent = Integer.parseInt(value[1]);
        isRemoteActive = Integer.parseInt(mRemoteCommandList.getDevice().getDeviceStatus());
        mTemp.setText("" + tempCurrent + Common.getC());

        /*for default mode*/
        String value1[] = mRemoteCommandList.getDevice().getMeta_device_default_status().split("-");
        modeTypeDef = value1[0];

        if (modeTypeDef.toLowerCase().equals("dry") || (modeTypeDef.toLowerCase().equals("auto"))) {
            fanspeedDef = "Auto";
        } else {
            fanspeedDef = modeTypeDef.toLowerCase();
            modeTypeDef = "Cool";
        }


        tempdefaultstatus = Integer.parseInt(value1[1]);

        /*for fan speed*/
        String value2[] = mRemoteCommandList.getDevice().getDeviceSubStatus().split("-");
        fanspeed = value2[0];

        String statictext = "<font color=#0279e1><B>DEFAULT:-</B></font>" +
                "<small><font color=#4b74ff>\t\tMODE: </font><font color=#000000><B>" + modeTypeDef.toLowerCase() + "</b></font></small>" +
                "<small><font color=#4b74ff>\t\tFAN: </font><font color=#000000><B>" + fanspeedDef.toLowerCase() + "</b></font></small>" +
                "<small><font color=#4b74ff>\t\tTEMP: </font><font color=#000000><B>" + tempdefaultstatus + "°C </b></font></small>";
        Spanned text = Html.fromHtml(statictext);
        mDefaultValueHeading.setText(text);

        if ((modeType.toLowerCase().equals("high")) || (modeType.toLowerCase().equals("medium")) || (modeType.toLowerCase().equals("low"))) {
            fanspeed = modeType.toLowerCase();
            modeType = ("cool");
        }

        if (fanspeed.toLowerCase().equals("dry") || (fanspeed.toLowerCase().equals("auto"))) {
            fanspeed = "Auto";
        }

        mDefaultTemp.setText("\t  Mode : " + " " + modeType.toUpperCase() + " " + "\n" + "Fan Speed: " + " " + fanspeed.toUpperCase());
        if (mRemoteCommandList.getDevice().getDeviceStatus().equalsIgnoreCase("0")) {
            txtAcState.setText("Ac state : OFF");
        } else {
            txtAcState.setText("Ac state : ON");
        }
        mImageAutoText.setText(String.format("Speed %s", modeType));
        moodName = modeType;
        setPowerOnOff(mRemoteCommandList.getDevice().getDeviceStatus());
        mRemoteName.setText(mRemoteCommandList.getDevice().getDeviceName());
        mBrandBottom.setText(mRemoteCommandList.getDevice().getMeta_device_brand()/* + "{" + mRemoteCommandList.getDevice().getMeta_device_model() + "}"*/);

        tempMinus = 16;
        tempPlus = 30;

        if (tempCurrent == 16) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 0);
        } else if (tempCurrent == 17) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 30);
        } else if (tempCurrent == 18) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 40);
        } else if (tempCurrent == 19) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 50);
        } else if (tempCurrent == 20) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 60);
        } else if (tempCurrent == 21) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 70);
        } else if (tempCurrent == 22) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 80);
        } else if (tempCurrent == 23) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 90);
        } else if (tempCurrent == 24) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 100);
        } else if (tempCurrent == 25) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 110);
        } else if (tempCurrent == 26) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 120);
        } else if (tempCurrent == 27) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 130);
        } else if (tempCurrent == 28) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 140);
        } else if (tempCurrent == 29) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 150);
        } else if (tempCurrent == 30) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 180);
        }

    }

    /*get remote data*/
    private void getRemoteInfo() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deviceInfo(mRemoteId, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("Res : " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    ActivityHelper.dismissProgressDialog();
                    if (code == 200) {
                        mRemoteList = Common.jsonToPojo(result.toString(), RemoteDetailsRes.class);
                        mRemoteCommandList = mRemoteList.getData();
                        initView();

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

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    private void getIRDeviceDetails() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(IRBlasterRemote.this, "Please Wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getDeviceList("ir_blaster", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    if (code == 200) {
                        ChatApplication.logDisplay("remote res : " + result.toString());
                        IRDeviceDetailsRes irDeviceDetailsRes = Common.jsonToPojo(result.toString(), IRDeviceDetailsRes.class);
                        mIRDeviceList = irDeviceDetailsRes.getData();

                    }

                    getRemoteInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
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

    /*set speed test
     * */
    private void setSpeedText(String speedResult) {

        /*if (speedResult.equalsIgnoreCase("AUTO")) {
            mImageAuto.setImageResource(R.drawable.auto_new);
            setNormalText("Auto");
        } else if (speedResult.equalsIgnoreCase("LOW")) {
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity_low);
            setNormalText("Low");
        } else if (speedResult.equalsIgnoreCase("MEDIUM")) {
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity_middle);
            setNormalText("Medium");
        } else if (speedResult.equalsIgnoreCase("HIGH")) {
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity);
            setNormalText("High");
        }*/
    }

    /*set speed type*/
    private void setNormalText(String normalText) {
        mImageAutoText.setText("Speed " + normalText);
    }

    /*set power button with background*/
    private void setPowerOnOff(String powerText) {
        isPowerOn = powerText.contains("ON") || powerText.contains(TURN_ON);
        txtAcState.setText(isRemoteActive == 1 ? "Ac state : ON" : "Ac state : OFF");

        txtRemoteState.setTextColor(isRemoteActive == 1 ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.username4));
        txtRemoteState.setText(isRemoteActive == 1 ? "Turn off" : "Turn on");
        mPowerButton.setBackground(isRemoteActive == 1 ? getResources().getDrawable(R.drawable.drawable_turn_on_btn) : getResources().getDrawable(R.drawable.drawable_turn_off_btn));
        imgRemoteStatus.setImageDrawable(isRemoteActive == 1 ? getResources().getDrawable(R.drawable.ac_power_off) : getResources().getDrawable(R.drawable.ac_power_on));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remote_power_button:
                strswing = "";
                sendRemoteCommand(0);
                break;

            case R.id.remote_temp_minus:
                txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
                txtRemoteState.setText("Turn off");
                mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
                imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));

                ChatApplication.logDisplay("isOn : " + isPowerOn);
                // if (isRemoteActive == 1) {
                if (tempCurrent != tempMinus) {
                    tempCurrent--;
                    setangle(tempCurrent);
                    ChatApplication.logDisplay("temperature minus" + tempCurrent);

                    if (tempCurrent == tempMinus) {
                        tempCurrent = tempMinus;
                    }
                    strswing = "";
                    sendRemoteCommand(1);
                }
               /* } else {
                    Common.showToast("Remote is not active");
                }*/
                txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
                txtRemoteState.setText("Turn off");
                mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
                imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));

                break;

            case R.id.remote_temp_plus:

                txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
                txtRemoteState.setText("Turn off");
                mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
                imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));
                ChatApplication.logDisplay("isOn : " + isPowerOn);
//                if (isRemoteActive == 1) {
                if (tempCurrent != tempPlus) {
                    tempCurrent++;
                    setangle(tempCurrent);
                    ChatApplication.logDisplay("temperature plus" + tempCurrent);

                    if (tempCurrent == tempPlus) {
                        tempCurrent = tempPlus;
                    }
                    strswing = "";
                    sendRemoteCommand(1);
                }
               /* } else {
                    Common.showToast("Remote is not active");
                }*/

                break;
            case R.id.relative_plus:
                //if (isRemoteActive == 1) {
                if (tempCurrent != tempPlus) {
                    tempCurrent++;
                    setangle(tempCurrent);
                    ChatApplication.logDisplay("temperature plus" + tempCurrent);

                    if (tempCurrent == tempPlus) {
                        tempCurrent = tempPlus;
                    }
                    strswing = "";

                    txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
                    txtRemoteState.setText("Turn off");
                    mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
                    imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));
                    sendRemoteCommand(1);
                }
              /*  } else {
                    Common.showToast("Remote is not active");
                }*/
                break;
            case R.id.relative_minus:
                //  if (isRemoteActive == 1) {
                if (tempCurrent != tempMinus) {
                    tempCurrent--;
                    setangle(tempCurrent);
                    ChatApplication.logDisplay("temperature minus" + tempCurrent);

                    if (tempCurrent == tempMinus) {
                        tempCurrent = tempMinus;
                    }
                    strswing = "";
                    txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
                    txtRemoteState.setText("Turn off");
                    mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
                    imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));
                    sendRemoteCommand(1);

                }
               /* } else {
                    Common.showToast("Remote is not active");
                }*/
                break;
            case R.id.remote_toolbar_back:
                onBackPressed();
                break;
            case R.id.action_edit:
                showBottomSheetDialog();
                break;
            case R.id.action_delete:
                break;

            case R.id.txtswingState:
                strswing = "1";
                sendRemoteCommand(1);
                /* due to on command now not avail in database at 20oct 2020 as per discuss with madhulika*/

//                txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
//                txtRemoteState.setText("Turn off");
//                mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
//                imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));
                break;
        }
    }

    public void setangle(int angle) {
        switch (angle) {
            case 16:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 0);
                break;
            case 17:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 30);
                break;
            case 18:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 40);
                break;
            case 19:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 50);
                break;
            case 20:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 60);
                break;
            case 21:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 70);
                break;
            case 22:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 80);
                break;
            case 23:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 90);
                break;
            case 24:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 100);
                break;
            case 25:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 110);
                break;
            case 26:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 120);
                break;
            case 27:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 130);
                break;
            case 28:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 140);
                break;
            case 29:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 150);
                break;
            case 30:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 180);
                break;

        }

        mTemp.setText("" + angle + Common.getC());
        tempCurrent = angle;
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(IRBlasterRemote.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + mRemoteCommandList.getDevice().getDeviceName() + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showRemoteSaveDialog(true);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete remote?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteRemote();
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void showRemoteSaveDialog(final boolean isEdit) {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_save_remote);
        }

        mEdtRemoteName = mDialog.findViewById(R.id.edt_remote_name);
        mEdtRemoteName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        TextView mDialogTitle = mDialog.findViewById(R.id.tv_title);
        mDialogTitle.setText("Remote Details");

        ImageView iv_close = mDialog.findViewById(R.id.iv_close);
        Button mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button mBtnSave = mDialog.findViewById(R.id.btn_save);

        mSpinnerBlaster = mDialog.findViewById(R.id.remote_blaster_spinner);
        remote_room_txt = mDialog.findViewById(R.id.remote_room_txt);
        mSpinnerMode = mDialog.findViewById(R.id.remote_mode_spinner);
        mRemoteDefaultTemp = mDialog.findViewById(R.id.edt_remote_tmp);
        TextView mBlastername = mDialog.findViewById(R.id.txt_blastername);

        String modeDefult = "";
        try {
            if (!TextUtils.isEmpty(mRemoteCommandList.getDevice().getMeta_device_default_status())) {
                String[] spiltarray = mRemoteCommandList.getDevice().getMeta_device_default_status().split("-");
                mRemoteDefaultTemp.setText("" + spiltarray[1]);
                modeDefult = spiltarray[0];
            }
            mEdtRemoteName.setText("" + mRemoteCommandList.getDevice().getDeviceName());
            mEdtRemoteName.setSelection(mEdtRemoteName.getText().toString().length());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        getIRBlasterList(isEdit);

        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < mIRDeviceList.size(); i++) {
            arrayList.add(mIRDeviceList.get(i).getDeviceName());
        }

        if (arrayList.size() == 0) {
            arrayList.add("No IR Blaster");
        }

        mSpinnerBlaster.setEnabled(false);
        mSpinnerBlaster.setClickable(false);
        mSpinnerBlaster.setVisibility(View.VISIBLE);

        final ArrayAdapter roomAdapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner, arrayList);
        mSpinnerBlaster.setAdapter(roomAdapter);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equalsIgnoreCase(mRemoteCommandList.getDevice().getIr_blaster().getDevice_name())) {
                mSpinnerBlaster.setSelection(i);
            }
        }

        mSpinnerBlaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mIRDeviceList.get(position).getRoom() != null) {
                    remote_room_txt.setText("" + mIRDeviceList.get(position).getRoom().getRoomName());
                } else {
                    remote_room_txt.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, moodListDialog);
        mSpinnerMode.setAdapter(roomAdapter1);

        for (int i = 0; i < moodListDialog.length; i++) {
            if (moodListDialog[i].equalsIgnoreCase(modeDefult)) {
                mSpinnerMode.setSelection(i);
            }
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
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

                if (mSpinnerBlaster.getSelectedItem().toString().equalsIgnoreCase("No IR Blaster")) {
                    Common.showToast("Select Blaster");
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

                saveRemote(mEdtRemoteName.getText().toString().trim(), isEdit);

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
     * @param remoteName remotename
     * @param isEdit
     */
    private void saveRemote(String remoteName, boolean isEdit) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.dismissProgressDialog();
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().saveRemote(mRemoteCommandList.getDevice().getDevice_id(), remoteName,
                mSpinnerMode.getSelectedItem().toString() + "-" + mRemoteDefaultTemp.getText().toString().trim(), mIRDeviceList.get(mSpinnerBlaster.getSelectedItemPosition()).getDeviceId(),
                new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        try {
                            hideSaveDialog();
                            JSONObject result = new JSONObject(stringResponse);
                            int code = result.getInt("code");
                            String message = result.getString("message");

                            ChatApplication.showToast(IRBlasterRemote.this, message);
                            if (code == 200) {
                                getRemoteInfo();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ChatApplication.showToast(IRBlasterRemote.this, getResources().getString(R.string.something_wrong1));
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ChatApplication.showToast(IRBlasterRemote.this, getResources().getString(R.string.something_wrong1));
                    }
                });
    }

    /*
    remote mode change
    isactive=1 on & =0 off
    * */

    /*delete remote*/
    private void deleteRemote() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), "" + R.string.disconnect);
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(mRemoteCommandList.getDevice().getDevice_id(), new DataResponseListener() {

            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        ChatApplication.isMoodFragmentNeedResume = true;
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterRemote.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterRemote.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    private void sendRemoteCommand(final int counting) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), "" + R.string.disconnect);
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        String type = "";
        if (moodName.trim().toLowerCase().equalsIgnoreCase("cool")) {
            type = fanspeed.trim();
        } else {
            type = moodName.trim();
        }


        SpikeBotApi.getInstance().sendRemoteCommand(mRemoteId, isRemoteActive == 1 ? "0" : "1", type.toUpperCase() + "-" + tempCurrent, strswing, counting, "ac",
                new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        try {
                            JSONObject result = new JSONObject(stringResponse);
                            int code = result.getInt("code");
                            if (code == 200) {
                                ChatApplication.isMoodFragmentNeedResume = true;

                                if (counting == 0) {
                                    isRemoteActive = isRemoteActive == 1 ? 0 : 1;
                                    isPowerOn = isRemoteActive == 1 ? true : false;

                                    if (isPowerOn)
                                        setPowerOnOff(TURN_OFF);
                                    else
                                        setPowerOnOff(TURN_ON);
                                } else if (strswing.equalsIgnoreCase("1")) {
                                    isRemoteActive = isRemoteActive == 1 ? 0 : 1;
                                } else {
                                    isRemoteActive = 1;
                                }

                                updateRemoteUI();

                            } else if (code == 500) {
                                ChatApplication.showToast(IRBlasterRemote.this, result.getString("message"));
                                if (counting == 0) {
                                    isPowerOn = isRemoteActive == 1 ? true : false;

                                    if (isPowerOn)
                                        setPowerOnOff(TURN_OFF);
                                    else
                                        setPowerOnOff(TURN_ON);
                                }
                            } else {
                                ChatApplication.showToast(IRBlasterRemote.this, mRemoteName.getText() + " " + getString(R.string.ir_error));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(IRBlasterRemote.this, mRemoteName.getText() + " " + getString(R.string.ir_error));
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(IRBlasterRemote.this, mRemoteName.getText() + " " + getString(R.string.ir_error));
                    }
                });
    }

    /**
     * @param
     */
    private void updateRemoteUI() {
        mTemp.setText("" + tempCurrent + Common.getC());
        mImageAutoText.setText("" + String.format("Speed %s", fanspeed));

        setSpeedText("" + fanspeed);
        mRemoteName.setText("" + mRemoteCommandList.getDevice().getDeviceName());
    }

    /*check remote active or not -1 than means remote inactive */
    public void remoteSpeed(String modeName) {
//        if (isRemoteActive == 1) {
        setSpeedText(modeName);
        if (modeName.equalsIgnoreCase("Auto") || modeName.equalsIgnoreCase("Dry")) {
            recycleFanSpeed.setAlpha(0.5f);
            isCoolMode = false;
        } else {
            isCoolMode = true;
            recycleFanSpeed.setAlpha(1f);

        }
        mFanSpeedAdapter.setIsActive(isCoolMode);
        mFanSpeedAdapter.notifyDataSetChanged();
        strswing = "";
        sendRemoteCommand(2);
        txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
        txtRemoteState.setText("Turn off");
        mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
        imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));
//        } else {
//            Common.showToast("Remote is Not Active");
//        }
    }

    @Override
    public void selectMood(RemoteMoodModel remoteMoodModel) {
        moodName = remoteMoodModel.getMoodName();

        if (moodName.equalsIgnoreCase("Cool")) {
            fanspeed = "High";
        }

        remoteSpeed(moodName);
        if (moodName.equalsIgnoreCase("Auto") || (moodName.equalsIgnoreCase("Dry"))) {
            recycleFanSpeed.setAlpha(0.5f);
            isCoolMode = false;
        } else {
            isCoolMode = true;
            recycleFanSpeed.setAlpha(1f);

        }
        mFanSpeedAdapter.setIsActive(isCoolMode);
        mFanSpeedAdapter.notifyDataSetChanged();
        moodRemoteAdapter.notifyDataSetChanged();

        if ((modeType.toLowerCase().equals("high")) || (modeType.toLowerCase().equals("medium")) || (modeType.toLowerCase().equals("low"))) {
            fanspeed = modeType.toLowerCase();
            modeType = ("cool");

        } else if (moodName.equalsIgnoreCase("Cool")) {
            fanspeed = "High";
            seFanSpeedAdapter(false);
        } else {
            fanspeed = "Auto";
        }

        mDefaultTemp.setText("\t  Mode : " + " " + moodName.toUpperCase() + " " + "\n" + "Fan Speed: " + " " + fanspeed.toUpperCase());  // dev arp set static due to development pending from web
        txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
        txtRemoteState.setText("Turn off");
        mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
        imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));

    }

    @Override
    public void selectAcFanSpeed(RemoteMoodModel remoteMoodModel) {
        fanspeed = remoteMoodModel.getMoodName();
        mFanSpeedAdapter.notifyDataSetChanged();
//        modeType = ("cool");
        remoteSpeed(fanspeed);
        mDefaultTemp.setText("\t  Mode : " + " " + moodName.toUpperCase() + " " + "\n" + "Fan Speed: " + " " + fanspeed.toUpperCase());  // dev arp set static due to development pending from web
        txtRemoteState.setTextColor(getResources().getColor(R.color.automation_red));
        txtRemoteState.setText("Turn off");
        mPowerButton.setBackground(getResources().getDrawable(R.drawable.drawable_turn_on_btn));
        imgRemoteStatus.setImageDrawable(getResources().getDrawable(R.drawable.ac_power_off));

    }

    /*start connection*/
    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }

        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }

    }

    private void socketOn() {
        mSocket.on("changeDeviceStatus", changeDeviceStatus);
    }

    @Override
    protected void onResume() {
        if (mSocket != null) {
            socketOn();
            ChatApplication.logDisplay("socket is null");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSocket != null) {
            mSocket.off("changeDeviceStatus", changeDeviceStatus);
        }
    }
}
