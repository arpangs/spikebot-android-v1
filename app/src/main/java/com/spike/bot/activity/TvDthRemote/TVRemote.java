package com.spike.bot.activity.TvDthRemote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRDeviceDetailsRes;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.model.TVCustomAddResponseButton;
import com.spike.bot.model.TvCustomCommandModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TVRemote extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public String[] CustomButtonList = new String[]{"Select Button", "Button 1", "Button 2", "Button 3", "Button 4", "Button 5", "Button 6"};
    TextView txt_tv_remote, txt_dth_remote;
    RelativeLayout relative_tv_remote;
    CoordinatorLayout dth_remote;
    TextView mTVButton1, mTVButton2, mTVButton3, mTVButton4, mTVButton5, mTVButton6, mTvPower, mTVInput, mTvmute, mTVVolPlus, mTVVolMinus, mNumberWindo, mRemoteName, mTVMenuOk,
            mDTHPower, mDTHTV, mDTHMute, mDTHVolPlus, mDTHVolMinus, mDTHMenuOk, mDTHHome, mDTHGuide, mDTHRed, mDTHGreen, mDTHYellow, mDTHBlue, mDTHBack,
            mDTH0, mDTH1, mDTH2, mDTH3, mDTH4, mDTH5, mDTH6, mDTH7, mDTH8, mDTH9, mTVButton1Label, mTVButton2Label, mTVButton3Label, mTVButton4Label, mTVButton5Label, mTVButton6Label;
    boolean isTVOn = false;
    boolean isDTHOn = false;
    BottomSheetBehavior behavior;
    RelativeLayout detailsview;
    ImageView mToolbarBack, mImageEdit, mTVChPlus, mTVChMinus, mTVMenuUp, mTVMenuLeft, mTVMenuRight, mTVMenuDown, mDTHMenuUp, mDTHMenuLeft, mDTHMenuRight, mDTHMenuDown, mDTHCHUp, mDTHCHDown;
    boolean mIsTvAttach = false;
    boolean mIsDthAttch = false;
    String mRemoteType = "";
    RelativeLayout mEmptyRemote;
    boolean isTV = false;
    int DelRemoteButtonPos = 0;
    String CustomButtonPos = "";
    private String mRemoteId, remoteName = "", mRoomDeviceId, module_id = "", device_sub_status = "", modeTypeDef = "", strswing = "", fanspeed = "Medium", remote = "tv";
    private int isRemoteActive;
    private boolean isPowerOn = false;
    private RemoteDetailsRes mRemoteList;
    private RemoteDetailsRes.Data mRemoteCommandList;
    private String mTVStr1Label = "", mTVStr2Label = "", mTVStr3Label = "", mTVStr4Label = "", mTVStr5Label = "", mTVStr6Label = "";
    private EditText mEdtButtonName;
    private TextInputLayout txtinputRemoteDeleteDialog;
    private Spinner mSpinnerMode, mSpinnerCustomButton;
    private List<IRDeviceDetailsRes.Data> mIRDeviceList = new ArrayList<>();
    private List<TvCustomCommandModel.Data> mTvCommandNameList = new ArrayList<>();
    private List<TvCustomCommandModel.Data> mTvCommandNameListtemp = new ArrayList<>();
    /*Edit TV remote*/
    private Dialog mDialog;
    private Spinner mSpinnerBlaster;
    private TextView remote_room_txt;
    private EditText mEdtRemoteName;
    private TextView mDefaultValueHeading;
    private boolean isCoolMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_remote);

        remote = getIntent().getStringExtra("from");

        mRemoteId = getIntent().getStringExtra("IR_BLASTER_ID");
        isRemoteActive = getIntent().getIntExtra("REMOTE_IS_ACTIVE", 0);
        /*mRoomDeviceId = getIntent().getStringExtra("ROOM_DEVICE_ID");
        module_id = getIntent().getStringExtra("IR_MODULE_ID");*/

        getRemoteInfo();
        getTVCommandNameList();
        bindView();

        /*for ir list getting*/
        getIRDeviceDetails();
    }

    private void bindView() {

        txt_tv_remote = findViewById(R.id.txt_tv_remote);
        txt_dth_remote = findViewById(R.id.txt_dth_remote);
        dth_remote = findViewById(R.id.relative_dth_remote);
        relative_tv_remote = findViewById(R.id.relative_tv_remote);
        detailsview = findViewById(R.id.detailsview);
        mTVInput = findViewById(R.id.tv_input);
        mTvmute = findViewById(R.id.tv_mute);
        mToolbarBack = findViewById(R.id.remote_toolbar_back);
        mRemoteName = findViewById(R.id.remote_name);
        mImageEdit = findViewById(R.id.action_edit);
        mTVMenuUp = findViewById(R.id.tv_menu_up);
        mTVMenuLeft = findViewById(R.id.tv_menu_left);
        mTVMenuOk = findViewById(R.id.tv_menu_ok);
        mTVMenuRight = findViewById(R.id.tv_menu_right);
        mTVMenuDown = findViewById(R.id.tv_menu_down);
        behavior = BottomSheetBehavior.from(detailsview);
        mTVChPlus = findViewById(R.id.tv_ch_plus);
        mTVChMinus = findViewById(R.id.tv_ch_minus);
        mTVVolPlus = findViewById(R.id.tv_vol_plus);
        mTVVolMinus = findViewById(R.id.tv_vol_minus);
        mTvPower = findViewById(R.id.tv_power);


        mDTHPower = findViewById(R.id.dth_power);
        mDTHTV = findViewById(R.id.dth_tv);
        mDTHMute = findViewById(R.id.dth_mute);
        mDTHVolPlus = findViewById(R.id.dth_vol_plus);
        mDTHVolMinus = findViewById(R.id.dth_vol_minus);
        mDTHCHUp = findViewById(R.id.dth_ch_plus);
        mDTHCHDown = findViewById(R.id.dth_ch_minus);
        mDTHMenuUp = findViewById(R.id.dth_menu_up);
        mDTHMenuLeft = findViewById(R.id.dth_menu_left);
        mDTHMenuOk = findViewById(R.id.dth_menu_ok);
        mDTHMenuRight = findViewById(R.id.dth_menu_right);
        mDTHMenuDown = findViewById(R.id.dth_menu_down);
        mDTHHome = findViewById(R.id.dth_home);
        mDTHGuide = findViewById(R.id.dth_guide);
        mDTHRed = findViewById(R.id.dth_red);
        mDTHGreen = findViewById(R.id.dth_green);
        mDTHYellow = findViewById(R.id.dth_yellow);
        mDTHBlue = findViewById(R.id.dth_blue);
        mDTHBack = findViewById(R.id.dth_back);
        mDTH0 = findViewById(R.id.dth_no_zero);
        mDTH1 = findViewById(R.id.dth_no_one);
        mDTH2 = findViewById(R.id.dth_no_two);
        mDTH3 = findViewById(R.id.dth_no_three);
        mDTH4 = findViewById(R.id.dth_no_four);
        mDTH5 = findViewById(R.id.dth_no_five);
        mDTH6 = findViewById(R.id.dth_no_six);
        mDTH7 = findViewById(R.id.dth_no_seven);
        mDTH8 = findViewById(R.id.dth_no_eight);
        mDTH9 = findViewById(R.id.dth_no_nine);


        mTVButton1 = findViewById(R.id.tv_button_one);
        mTVButton2 = findViewById(R.id.tv_button_two);
        mTVButton3 = findViewById(R.id.tv_button_three);
        mTVButton4 = findViewById(R.id.tv_button_four);
        mTVButton5 = findViewById(R.id.tv_button_five);
        mTVButton6 = findViewById(R.id.tv_button_six);

        mTVButton1Label = findViewById(R.id.tv_button_one_label);
        mTVButton2Label = findViewById(R.id.tv_button_two_label);
        mTVButton3Label = findViewById(R.id.tv_button_three_label);
        mTVButton4Label = findViewById(R.id.tv_button_four_label);
        mTVButton5Label = findViewById(R.id.tv_button_five_label);
        mTVButton6Label = findViewById(R.id.tv_button_six_label);


        mEmptyRemote = findViewById(R.id.rel_empty_remote);


        if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            mImageEdit.setVisibility(View.VISIBLE);
        } else {
            mImageEdit.setVisibility(View.GONE);
        }

        mImageEdit.setOnClickListener(this);
        mToolbarBack.setOnClickListener(this);
        mTVInput.setOnClickListener(this);
        mTVMenuUp.setOnClickListener(this);
        mTVMenuLeft.setOnClickListener(this);
        mTVMenuOk.setOnClickListener(this);
        mTVMenuRight.setOnClickListener(this);
        mTVMenuDown.setOnClickListener(this);
        mTvmute.setOnClickListener(this);
        mTVChMinus.setOnClickListener(this);
        mTVVolPlus.setOnClickListener(this);
        mTVVolMinus.setOnClickListener(this);
        mTVChPlus.setOnClickListener(this);
        txt_tv_remote.setOnClickListener(this);
        txt_dth_remote.setOnClickListener(this);
        mTvPower.setOnClickListener(this);

        mDTHTV.setOnClickListener(this);
        mDTHPower.setOnClickListener(this);
        mDTHMute.setOnClickListener(this);
        mDTHVolPlus.setOnClickListener(this);
        mDTHVolMinus.setOnClickListener(this);
        mDTHCHUp.setOnClickListener(this);
        mDTHCHDown.setOnClickListener(this);
        mDTHMenuUp.setOnClickListener(this);
        mDTHMenuLeft.setOnClickListener(this);
        mDTHMenuOk.setOnClickListener(this);
        mDTHMenuRight.setOnClickListener(this);
        mDTHMenuDown.setOnClickListener(this);
        mDTHHome.setOnClickListener(this);
        mDTHGuide.setOnClickListener(this);
        mDTHRed.setOnClickListener(this);
        mDTHGreen.setOnClickListener(this);
        mDTHYellow.setOnClickListener(this);
        mDTHBlue.setOnClickListener(this);
        mDTHBack.setOnClickListener(this);
        mDTH0.setOnClickListener(this);
        mDTH1.setOnClickListener(this);
        mDTH2.setOnClickListener(this);
        mDTH3.setOnClickListener(this);
        mDTH4.setOnClickListener(this);
        mDTH5.setOnClickListener(this);
        mDTH6.setOnClickListener(this);
        mDTH7.setOnClickListener(this);
        mDTH8.setOnClickListener(this);
        mDTH9.setOnClickListener(this);

        mEmptyRemote.setOnClickListener(this);

//        mTVButton1.setOnLongClickListener(this);
//        mTVButton2.setOnLongClickListener(this);
//        mTVButton3.setOnLongClickListener(this);
//        mTVButton4.setOnLongClickListener(this);
//        mTVButton5.setOnLongClickListener(this);
//        mTVButton6.setOnLongClickListener(this);

        mTVButton1.setOnClickListener(this);
        mTVButton2.setOnClickListener(this);
        mTVButton3.setOnClickListener(this);
        mTVButton4.setOnClickListener(this);
        mTVButton5.setOnClickListener(this);
        mTVButton6.setOnClickListener(this);


        mNumberWindo = findViewById(R.id.dth_input);

        mNumberWindo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        try {
            if (remote.equalsIgnoreCase("dth")) {
                mIsDthAttch = true;
                dth_remote.setVisibility(View.VISIBLE);
                relative_tv_remote.setVisibility(View.GONE);
                txt_tv_remote.setBackgroundResource(R.drawable.buttom_capsule_gray);
                txt_tv_remote.setTextColor(getResources().getColor(R.color.automation_black));
                txt_dth_remote.setBackgroundResource(R.drawable.buttom_capsule_blue);
                txt_dth_remote.setTextColor(getResources().getColor(R.color.automation_white));
                isTV = false;

            } else if (remote.equalsIgnoreCase("tv_dth")) {
                mIsDthAttch = true;
                mIsTvAttach = true;
                isTV = true;

            } else {
                isTV = true;
                mIsTvAttach = true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                detailsview.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_tv_remote:

                if (mIsTvAttach) {
                    mEmptyRemote.setVisibility(View.GONE);
                    relative_tv_remote.setVisibility(View.VISIBLE);
                    isTV = true;
                } else {
                    isTV = false;
                    mEmptyRemote.setVisibility(View.VISIBLE);
                }

                dth_remote.setVisibility(View.GONE);
                txt_dth_remote.setBackgroundResource(R.drawable.buttom_capsule_gray);
                txt_dth_remote.setTextColor(getResources().getColor(R.color.automation_black));
                txt_tv_remote.setBackgroundResource(R.drawable.buttom_capsule_blue);
                txt_tv_remote.setTextColor(getResources().getColor(R.color.automation_white));

                break;
            case R.id.txt_dth_remote:

                relative_tv_remote.setVisibility(View.GONE);
                isTV = false;
                txt_tv_remote.setBackgroundResource(R.drawable.buttom_capsule_gray);
                txt_tv_remote.setTextColor(getResources().getColor(R.color.automation_black));
                txt_dth_remote.setBackgroundResource(R.drawable.buttom_capsule_blue);
                txt_dth_remote.setTextColor(getResources().getColor(R.color.automation_white));

                if (mIsDthAttch) {
                    mEmptyRemote.setVisibility(View.GONE);
                    dth_remote.setVisibility(View.VISIBLE);
                } else {
                    mEmptyRemote.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.tv_power:

                if (isTVOn) {
                    device_sub_status = "turn_off";
                    isTVOn = false;
                } else {
                    device_sub_status = "turn_on";
                    isTVOn = true;
                }
                sendRemoteCommand(0, "tv");
                break;

            case R.id.tv_ch_plus:
                device_sub_status = "channel_up";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_ch_plus:
                device_sub_status = "channel_up";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.tv_ch_minus:
                device_sub_status = "channel_down";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_ch_minus:
                device_sub_status = "channel_down";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.tv_vol_plus:
                device_sub_status = "volume_up";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_vol_plus:
                device_sub_status = "volume_up";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.tv_vol_minus:
                device_sub_status = "volume_down";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_vol_minus:
                device_sub_status = "volume_down";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.remote_toolbar_back:
                onBackPressed();
                break;
            case R.id.action_edit:
                showBottomSheetDialog();
                break;
            case R.id.tv_input:
                device_sub_status = "av/tv";
                sendRemoteCommand(1, "tv");
                break;

            case R.id.tv_mute:
                device_sub_status = "mute";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_mute:
                device_sub_status = "mute";
                sendRemoteCommand(1, "dth");
                break;
            case R.id.tv_menu_up:
                device_sub_status = "arrow_top";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_menu_up:
                device_sub_status = "arrow_top";
                sendRemoteCommand(1, "dth");
                break;
            case R.id.tv_menu_left:
                device_sub_status = "arrow_left";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_menu_left:
                device_sub_status = "arrow_left";
                sendRemoteCommand(1, "dth");
                break;
            case R.id.tv_menu_ok:
                device_sub_status = "ok";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_menu_ok:
                device_sub_status = "ok";
                sendRemoteCommand(1, "dth");
                break;
            case R.id.tv_menu_right:
                device_sub_status = "arrow_right";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_menu_right:
                device_sub_status = "arrow_right";
                sendRemoteCommand(1, "dth");
                break;
            case R.id.tv_menu_down:
                device_sub_status = "arrow_bottom";
                sendRemoteCommand(1, "tv");
                break;
            case R.id.dth_menu_down:
                device_sub_status = "arrow_bottom";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_power:
                if (isDTHOn) {
                    device_sub_status = "turn_off";
                    isDTHOn = false;
                } else {
                    device_sub_status = "turn_on";
                    isDTHOn = true;
                }
                sendRemoteCommand(0, "dth");
                break;

            case R.id.dth_tv:
                device_sub_status = "tv";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_home:
                device_sub_status = "home";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_guide:
                device_sub_status = "guide";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_red:
                device_sub_status = "red";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_green:
                device_sub_status = "green";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_yellow:
                device_sub_status = "yellow";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_blue:
                device_sub_status = "blue";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_back:
                device_sub_status = "back";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_zero:
                device_sub_status = "0";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_one:
                device_sub_status = "1";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_two:
                device_sub_status = "2";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_three:
                device_sub_status = "3";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_four:
                device_sub_status = "4";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_five:
                device_sub_status = "5";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_six:
                device_sub_status = "6";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_seven:
                device_sub_status = "7";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_eight:
                device_sub_status = "8";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.dth_no_nine:
                device_sub_status = "9";
                sendRemoteCommand(1, "dth");
                break;

            case R.id.rel_empty_remote:

                if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {


                    Intent intent = new Intent();


                    if (!mIsTvAttach) {
                        intent = new Intent(this, TVRemoteBrandListActivity.class);
                    } else if (!mIsDthAttch) {
                        intent = new Intent(this, DTHRemoteBrandListActivity.class);
                    }


                    intent.putExtra("REMOTE_NAME", mRemoteCommandList.getDevice().getDeviceName());
                    intent.putExtra("BLASTER_NAME", mRemoteCommandList.getDevice().getIr_blaster().getDevice_name());
                    intent.putExtra("ROOM_NAME", mRemoteCommandList.getDevice().getRoom().getRoom_name());
                    intent.putExtra("ROOM_ID", "" + mRemoteCommandList.getDevice().getRoom().getRoom_id());
                    intent.putExtra("SENSOR_ID", mRemoteCommandList.getDevice().getDevice_id());
                    intent.putExtra("IR_DEVICE_ID", "" + mRemoteCommandList.getDevice().getDevice_id());
                    intent.putExtra("IR_DEVICE_TYPE", "remote");
                    intent.putExtra("IR_BLASTER_MODULE_ID", mRemoteCommandList.getDevice().getIr_blaster().getDevice_id());
                    intent.putExtra("device_sub_type", "tv_dth");

                    startActivityForResult(intent, Constants.REMOTE_REQUEST_CODE);

                } else {
                    Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_button_one:


                if (mTVStr1Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("1", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    device_sub_status = mTVStr1Label;
                    sendRemoteCommand(1, "tv");
                }


                break;
            case R.id.tv_button_two:
                if (mTVStr2Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("2", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    device_sub_status = mTVStr2Label;
                    sendRemoteCommand(1, "tv");
                }
                break;
            case R.id.tv_button_three:
                if (mTVStr3Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("3", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    device_sub_status = mTVStr3Label;
                    sendRemoteCommand(1, "tv");
                }
                break;
            case R.id.tv_button_four:
                if (mTVStr4Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("4", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    device_sub_status = mTVStr4Label;
                    sendRemoteCommand(1, "tv");
                }
                break;
            case R.id.tv_button_five:
                if (mTVStr5Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("5", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    device_sub_status = mTVStr5Label;
                    sendRemoteCommand(1, "tv");
                }
                break;
            case R.id.tv_button_six:

                if (mTVStr6Label.equalsIgnoreCase("")) {
                    if (!Common.getPrefValue(TVRemote.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                        AddCustomButtomCodeDialog("6", false);
                    } else {
                        Toast.makeText(this, "You dont have the privileges to perform this operation.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    device_sub_status = mTVStr6Label;
                    sendRemoteCommand(1, "tv");
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {

        switch (view.getId()) {
            case R.id.tv_button_one:
                if (mTVStr1Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr1Label, "1");
                }
                break;
            case R.id.tv_button_two:
                if (mTVStr2Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr2Label, "2");
                }
                break;
            case R.id.tv_button_three:
                if (mTVStr3Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr3Label, "3");
                }
                break;
            case R.id.tv_button_four:
                if (mTVStr4Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr4Label, "4");
                }
                break;
            case R.id.tv_button_five:
                if (mTVStr5Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr5Label, "5");
                }
                break;
            case R.id.tv_button_six:
                if (mTVStr6Label.length() > 0) {
                    showBottomSheetDialogForRemoteButton(mTVStr6Label, "6");
                }
                break;


        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        menu.findItem(R.id.actionEdit).setIcon(resizeImage(R.drawable.edit_white_new, 190, 190));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionEdit) {
//              showBottomSheetDialog(room);

        }
        return super.onOptionsItemSelected(item);
    }

    private Drawable resizeImage(int resId, int w, int h) {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }


    private void sendRemoteCommand(final int counting, String remoteType) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), "" + R.string.disconnect);
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().sendRemoteCommand(mRemoteId, isRemoteActive == 1 ? "0" : "1", device_sub_status.toLowerCase(), "", counting, remoteType,
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
                                }
//                                if (isPowerOn)
//                                    setPowerOnOff(TURN_OFF);
//                                else
//                                    setPowerOnOff(TURN_ON);
////
//                                updateRemoteUI();

                            } else if (code == 500) {
                                ChatApplication.showToast(TVRemote.this, result.getString("message"));

                            } /*else {
                                ChatApplication.showToast(TVRemote.this, "mRemoteName.getText()" + " " + getString(R.string.ir_error));
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
//                        ChatApplication.showToast(TVRemote.this, "mRemoteName.getText()" + " " + getString(R.string.ir_error));
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ActivityHelper.dismissProgressDialog();
//                        ChatApplication.showToast(TVRemote.this, "mRemoteName.getText()" + " " + getString(R.string.ir_error));
                    }
                });
    }


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

                        remoteName = mRemoteCommandList.getDevice().getDeviceName();
                        mRemoteName.setText(remoteName);


                        mRemoteType = (String) mRemoteCommandList.getDevice().getDeviceSubType();

                        if (mRemoteType != null || !mRemoteType.equalsIgnoreCase("null")) {

                            switch (mRemoteType) {
                                case "tv":
                                    mIsTvAttach = true;
                                    mIsDthAttch = false;
                                    break;
                                case "dth":
                                    mIsTvAttach = false;
                                    mIsDthAttch = true;
                                    break;
                                case "tv_dth":
                                    mIsTvAttach = true;
                                    mIsDthAttch = true;
                                    break;

                            }

                        }

                        /*set tv custom button*/

                        try {
                            JSONObject jsonObject = result.optJSONObject("data");
                            JSONObject job = jsonObject.optJSONObject("device");
                            JSONArray jsonArray = new JSONArray(job.optString("meta_tv_codes"));
                            Log.i("getting Jsonarray", "" + jsonArray);

                            ArrayList<TVCustomAddResponseButton> customcodelist = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<TVCustomAddResponseButton>>() {
                            }.getType());
                            Log.i("getting Jsonarray", "" + customcodelist.size());

                            for (TVCustomAddResponseButton custombutton : customcodelist) {
                                if (custombutton.getButton_no() != null) {

                                    switch (custombutton.getButton_no()) {
                                        case "1":
                                            mTVButton1.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton1.setText("");
                                            mTVButton1Label.setText(custombutton.getButton_name());
                                            mTVStr1Label = custombutton.getName();
                                            break;

                                        case "2":
                                            mTVButton2.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton2.setText("");
                                            mTVButton2Label.setText(custombutton.getButton_name());
                                            mTVStr2Label = custombutton.getName();

                                            break;

                                        case "3":
                                            mTVButton3.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton3Label.setText(custombutton.getButton_name());
                                            mTVStr3Label = custombutton.getName();
                                            mTVButton3.setText("");
                                            break;
                                        case "4":
                                            mTVButton4.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton4Label.setText(custombutton.getButton_name());
                                            mTVStr4Label = custombutton.getName();
                                            mTVButton4.setText("");
                                            break;
                                        case "5":
                                            mTVButton5.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton5Label.setText(custombutton.getButton_name());
                                            mTVStr5Label = custombutton.getName();
                                            mTVButton5.setText("");

                                            break;
                                        case "6":
                                            mTVButton6.setBackgroundResource(R.drawable.badge_background_remote_blue);
                                            mTVButton6Label.setText(custombutton.getButton_name());
                                            mTVStr6Label = custombutton.getName();
                                            mTVButton6.setText("");
                                            break;
                                    }
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        bindView();

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

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog_tvdth, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);
        LinearLayout mEditRemteLL = view.findViewById(R.id.linear_bottom_edit_remote);
        LinearLayout mdeleteRemteLL = view.findViewById(R.id.linear_bottom_delete_remote_button);


        if (isTV) {
            mEditRemteLL.setVisibility(View.VISIBLE);
            mdeleteRemteLL.setVisibility(View.VISIBLE);
        } else {
            mEditRemteLL.setVisibility(View.GONE);
            mdeleteRemteLL.setVisibility(View.GONE);
        }


        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialogremote = new BottomSheetDialog(TVRemote.this, R.style.AppBottomSheetDialogTheme);
        dialogremote.setContentView(view);
        dialogremote.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + mRemoteCommandList.getDevice().getDeviceName() + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogremote.dismiss();
                showRemoteSaveDialog(true);
            }
        });

        mEditRemteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogremote.dismiss();
                AddCustomButtomCodeDialog("10", true);


            }
        });

        mdeleteRemteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogremote.dismiss();
                deleteCustomButtomCodeDialog("10", true);


            }
        });


        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogremote.dismiss();
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
                ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    private void showRemoteSaveDialog(final boolean isEdit) {

        try {

            if (mDialog != null) {
                mDialog = null;
            }

            if (mDialog == null) {
                mDialog = new Dialog(this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.dialog_save_remote_edit);
            }

            mEdtRemoteName = mDialog.findViewById(R.id.edt_remote_name);
            try {
                mEdtRemoteName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView mDialogTitle = mDialog.findViewById(R.id.tv_title);
            mDialogTitle.setText("Remote Details");

            ImageView iv_close = mDialog.findViewById(R.id.iv_close);
            Button mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
            Button mBtnSave = mDialog.findViewById(R.id.btn_save);

            mSpinnerBlaster = mDialog.findViewById(R.id.remote_blaster_spinner);
            remote_room_txt = mDialog.findViewById(R.id.remote_room_txt);

            try {
                if (isEdit) {
                    mEdtRemoteName.setText("" + mRemoteCommandList.getDevice().getDeviceName());
                    mEdtRemoteName.setSelection(mEdtRemoteName.getText().toString().length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            ArrayList<String> arrayList = new ArrayList<>();

            for (int i = 0; i < mIRDeviceList.size(); i++) {
                arrayList.add(mIRDeviceList.get(i).getDeviceName());
            }

            if (arrayList.size() == 0) {
                arrayList.add("No IR Blaster");
            }

            mSpinnerBlaster.setEnabled(false);
            mSpinnerBlaster.setClickable(false);

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

                    EditRemote(mEdtRemoteName.getText().toString().trim());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void getIRDeviceDetails() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(TVRemote.this, "Please Wait...", false);

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

    private void EditRemote(String remoteName) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.dismissProgressDialog();
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().saveRemote(mRemoteCommandList.getDevice().getDevice_id(), remoteName,
                "", mIRDeviceList.get(mSpinnerBlaster.getSelectedItemPosition()).getDeviceId(),
                new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        try {
                            hideSaveDialog();
                            JSONObject result = new JSONObject(stringResponse);
                            int code = result.getInt("code");
                            String message = result.getString("message");

                            ChatApplication.showToast(TVRemote.this, message);
                            if (code == 200) {
                                getRemoteInfo();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
                    }
                });
    }


    private void hideSaveDialog() {
        try {
            if (mEdtRemoteName != null) {
                hideSoftKeyboard(mEdtRemoteName);
            }
            if (mDialog != null) {
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }


    private void getTVCommandNameList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(TVRemote.this, "Please Wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getTVCommand("tv", mRemoteId, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                   /* JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    if (code == 200) {*/

                    JSONObject result = new JSONObject(stringResponse);
                    JSONArray jsonArray = new JSONArray(result.optString("data"));
                    Log.i("TVCommand Jsonarray", "" + jsonArray);

                    ArrayList<TVCustomAddResponseButton> customcodelist = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<TVCustomAddResponseButton>>() {
                    }.getType());
                    Log.i("TVCommand Jsonarray", "" + customcodelist.size());
                    mTvCommandNameList = new ArrayList<>();
                    for (TVCustomAddResponseButton custombutton : customcodelist) {
//                        if (custombutton.getButton_no() == null) {     // dev arp added for drop down
                            TvCustomCommandModel.Data data = new TvCustomCommandModel.Data();
                            data.setName(custombutton.getName());
                            data.setValue(custombutton.getValue());
                            mTvCommandNameList.add(data);
//                        }
                    }

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

    /*add custom button code dialog*/
    private void AddCustomButtomCodeDialog(String buttonPos, boolean isEdit) {
        try {

            if (mDialog != null) {
                mDialog = null;
            }

            if (mDialog == null) {
                mDialog = new Dialog(this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.dialog_add_tv_custom_command);
            }


            LinearLayout buttondropdown = mDialog.findViewById(R.id.ll_button_dropdown);

            TextView mDialogTitle = mDialog.findViewById(R.id.tv_title);


            if (buttonPos == "10" && isEdit) {
                buttondropdown.setVisibility(View.VISIBLE);
                mDialogTitle.setText("Edit Custom Button");
            } else {
                mDialogTitle.setText("Add Custom Button");
                buttondropdown.setVisibility(View.GONE);
            }


            mSpinnerCustomButton = mDialog.findViewById(R.id.custom_button);

            final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, CustomButtonList);
            mSpinnerCustomButton.setAdapter(roomAdapter1);

            mSpinnerCustomButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isEdit) {


                        String buttoncode = "";

                        switch (adapterView.getItemAtPosition(i).toString()) {

                            case "Button 1":
                                mEdtButtonName.setText(mTVButton1Label.getText().toString());
                                buttoncode = mTVStr1Label;
                                CustomButtonPos = "1";
                                break;
                            case "Button 2":
                                mEdtButtonName.setText(mTVButton2Label.getText().toString());
                                buttoncode = mTVStr2Label;
                                CustomButtonPos = "2";
                                break;
                            case "Button 3":
                                mEdtButtonName.setText(mTVButton3Label.getText().toString());
                                buttoncode = mTVStr3Label;
                                CustomButtonPos = "3";
                                break;
                            case "Button 4":
                                mEdtButtonName.setText(mTVButton4Label.getText().toString());
                                buttoncode = mTVStr4Label;
                                CustomButtonPos = "4";
                                break;
                            case "Button 5":
                                mEdtButtonName.setText(mTVButton5Label.getText().toString());
                                buttoncode = mTVStr5Label;
                                CustomButtonPos = "5";
                                break;
                            case "Button 6":
                                mEdtButtonName.setText(mTVButton6Label.getText().toString());
                                buttoncode = mTVStr6Label;
                                CustomButtonPos = "6";
                                break;

                        }

                        if (mEdtButtonName.getText().toString().equals("Add Button")) {
                            Toast.makeText(TVRemote.this, "Please add button first", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        } else {

                            for (int a = 0; a < mTvCommandNameListtemp.size(); a++) {
                                if (mTvCommandNameListtemp.get(a).getName().toLowerCase().equalsIgnoreCase(buttoncode.toLowerCase())) {
                                    mSpinnerMode.setSelection(a, false);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            mEdtButtonName = mDialog.findViewById(R.id.edt_button_name);

            try {
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(10); //Button Name max length to set only 10
                mEdtButtonName.setFilters(filterArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ImageView iv_close = mDialog.findViewById(R.id.iv_close);
            Button mBtnSave = mDialog.findViewById(R.id.btn_save);


            mSpinnerMode = mDialog.findViewById(R.id.custom_button_code_spinner);

            if (mEdtButtonName != null) {
                mEdtButtonName.setText("");
                mEdtButtonName.requestFocus();
            }


            TvCustomCommandModel.Data select = new TvCustomCommandModel.Data();
            select.setName("Select");
            select.setValue("0");

            mTvCommandNameListtemp.clear();
            mTvCommandNameListtemp.add(select);

            mTvCommandNameListtemp.addAll(mTvCommandNameList);

//            for (TvCustomCommandModel.Data data : mTvCommandNameList) {
//
//                if (mTvCommandNameListtemp.size() > 0) {
//                    for (int a = 0; a < mTvCommandNameListtemp.size(); a++) {
//                        if (mTvCommandNameListtemp.get(a).getName().equals(data.getName())) {
//                            mTvCommandNameListtemp.add(data);
//                        }
//                    }
//                } else {
//                    mTvCommandNameListtemp.add(data);
//                }
//
//            }


            final ArrayAdapter roomAdaptermode = new ArrayAdapter(getApplicationContext(), R.layout.spinner, mTvCommandNameListtemp);
            mSpinnerMode.setAdapter(roomAdaptermode);


            if (isEdit) {

                String buttoncode = "";

                switch (buttonPos) {


                    case "Button 1":
                        mEdtButtonName.setText(mTVButton1Label.getText().toString());
                        buttoncode = mTVStr1Label;
                        CustomButtonPos = "1";
                        break;
                    case "Button 2":
                        mEdtButtonName.setText(mTVButton2Label.getText().toString());
                        buttoncode = mTVStr2Label;
                        CustomButtonPos = "2";
                        break;
                    case "Button 3":
                        mEdtButtonName.setText(mTVButton3Label.getText().toString());
                        buttoncode = mTVStr3Label;
                        CustomButtonPos = "3";
                        break;
                    case "Button 4":
                        mEdtButtonName.setText(mTVButton4Label.getText().toString());
                        buttoncode = mTVStr4Label;
                        CustomButtonPos = "4";
                        break;
                    case "Button 5":
                        mEdtButtonName.setText(mTVButton5Label.getText().toString());
                        buttoncode = mTVStr5Label;
                        CustomButtonPos = "5";
                        break;
                    case "Button 6":
                        mEdtButtonName.setText(mTVButton6Label.getText().toString());
                        buttoncode = mTVStr6Label;
                        CustomButtonPos = "6";
                        break;


                }
                for (int a = 0; a < mTvCommandNameList.size(); a++) {
                    if (mTvCommandNameList.get(a).getName().equalsIgnoreCase(buttoncode)) {
                        mSpinnerMode.setSelection(a, false);
                    }
                }


            }


            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSaveDialog();
                }
            });

            mBtnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(mEdtButtonName.getText().toString().trim())) {
                        mEdtButtonName.requestFocus();
                        mEdtButtonName.setError("Enter Remote name");
                        return;
                    }

                    if (mSpinnerMode.getSelectedItemPosition() == 0) {
                        Common.showToast("Select Button");
                        return;
                    }
                    hideSoftKeyboard(mEdtButtonName);
                    mDialog.dismiss();
                    AddCustomButton(buttonPos == "10" ? CustomButtonPos : buttonPos, mEdtButtonName.getText().toString().trim(),
                            mTvCommandNameListtemp.get(mSpinnerMode.getSelectedItemPosition()).getName(),
                            mTvCommandNameListtemp.get(mSpinnerMode.getSelectedItemPosition()).getValue());


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void AddCustomButton(String buttonPos, String BName, String buttonCodeName, String buttonCode) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().AddTVCustomButton(mRemoteId, "tv", buttonPos, BName, buttonCodeName, buttonCode, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        ChatApplication.isEditActivityNeedResume = true;
                        hideSaveDialog();

                        ChatApplication.logDisplay("remote res : " + result.toString());
                        getRemoteInfo();
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

    public void showBottomSheetDialogForRemoteButton(String buttonName, String pos) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialogbutton = new BottomSheetDialog(TVRemote.this, R.style.AppBottomSheetDialogTheme);
        dialogbutton.setContentView(view);
        dialogbutton.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + buttonName + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbutton.dismiss();
                AddCustomButtomCodeDialog(pos, true);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbutton.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete " + buttonName + " button?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteButton(pos);
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void deleteButton(String btnPos) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), "" + R.string.disconnect);
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteCustomeRemoteButton(mRemoteId, btnPos, new DataResponseListener() {

            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        switch (btnPos) {
                            case "1":
                                mTVButton1.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton1.setText("+");
                                mTVButton1Label.setText("Add Button");
                                mTVStr1Label = "";
                                break;
                            case "2":
                                mTVButton2.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton2.setText("+");
                                mTVButton2Label.setText("Add Button");
                                mTVStr2Label = "";
                                break;
                            case "3":
                                mTVButton3.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton3.setText("+");
                                mTVButton3Label.setText("Add Button");
                                mTVStr3Label = "";
                                break;
                            case "4":
                                mTVButton4.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton4.setText("+");
                                mTVButton4Label.setText("Add Button");
                                mTVStr4Label = "";
                                break;
                            case "5":
                                mTVButton5.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton5.setText("+");
                                mTVButton5Label.setText("Add Button");
                                mTVStr5Label = "";
                                break;
                            case "6":
                                mTVButton6.setBackgroundResource(R.drawable.badge_background_gray);
                                mTVButton6.setText("+");
                                mTVButton6Label.setText("Add Button");
                                mTVStr6Label = "";
                                break;
                        }


                        ChatApplication.isMoodFragmentNeedResume = true;
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getRemoteInfo();

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
                ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(TVRemote.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }


    /*delete custom button code dialog*/
    private void deleteCustomButtomCodeDialog(String buttonPos, boolean isEdit) {
        try {

            if (mDialog != null) {
                mDialog = null;
            }

            if (mDialog == null) {
                mDialog = new Dialog(this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.dialog_add_tv_custom_command);
            }


            LinearLayout buttondropdown = mDialog.findViewById(R.id.ll_button_dropdown);
            buttondropdown.setVisibility(View.VISIBLE);

            LinearLayout llcodeView = mDialog.findViewById(R.id.ll_buttoncode);
            llcodeView.setVisibility(View.GONE);


            TextView mDialogTitle = mDialog.findViewById(R.id.tv_title);
            mDialogTitle.setText("Delete buttons");

            mSpinnerCustomButton = mDialog.findViewById(R.id.custom_button);

            final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, CustomButtonList);
            mSpinnerCustomButton.setAdapter(roomAdapter1);


            mSpinnerCustomButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isEdit) {

                        String buttoncode = "";

                        switch (adapterView.getItemAtPosition(i).toString()) {

                            case "Button 1":
                                mEdtButtonName.setText(mTVButton1Label.getText().toString());
                                buttoncode = mTVStr1Label;
                                DelRemoteButtonPos = 1;
                                break;
                            case "Button 2":
                                mEdtButtonName.setText(mTVButton2Label.getText().toString());
                                buttoncode = mTVStr2Label;
                                DelRemoteButtonPos = 2;
                                break;
                            case "Button 3":
                                mEdtButtonName.setText(mTVButton3Label.getText().toString());
                                buttoncode = mTVStr3Label;
                                DelRemoteButtonPos = 3;
                                break;
                            case "Button 4":
                                mEdtButtonName.setText(mTVButton4Label.getText().toString());
                                buttoncode = mTVStr4Label;
                                DelRemoteButtonPos = 4;
                                break;
                            case "Button 5":
                                mEdtButtonName.setText(mTVButton5Label.getText().toString());
                                buttoncode = mTVStr5Label;
                                DelRemoteButtonPos = 5;
                                break;
                            case "Button 6":
                                mEdtButtonName.setText(mTVButton6Label.getText().toString());
                                buttoncode = mTVStr6Label;
                                DelRemoteButtonPos = 6;
                                break;

                        }

                        if (mEdtButtonName.getText().toString().equals("Add Button")) {
                            Toast.makeText(TVRemote.this, "Please add button first", Toast.LENGTH_LONG).show();
                            DelRemoteButtonPos = 0;
                            mDialog.dismiss();
                        } else {

                            for (int a = 0; a < mTvCommandNameList.size(); a++) {
                                if (mTvCommandNameList.get(a).getName().equalsIgnoreCase(buttoncode)) {
                                    mSpinnerMode.setSelection(a, false);
                                }
                            }


                        }

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            mEdtButtonName = mDialog.findViewById(R.id.edt_button_name);

            mEdtButtonName.setVisibility(View.GONE);

            txtinputRemoteDeleteDialog = mDialog.findViewById(R.id.txtinput);
            txtinputRemoteDeleteDialog.setVisibility(View.GONE);

            ImageView iv_close = mDialog.findViewById(R.id.iv_close);
            Button mBtnSave = mDialog.findViewById(R.id.btn_save);

            mBtnSave.setText("Delete");


            mSpinnerMode = mDialog.findViewById(R.id.custom_button_code_spinner);

            if (mEdtButtonName != null) {
                mEdtButtonName.setText("");
                mEdtButtonName.requestFocus();
            }


            TvCustomCommandModel.Data select = new TvCustomCommandModel.Data();
            select.setName("Select");
            select.setValue("0");

            mTvCommandNameListtemp.clear();
            mTvCommandNameListtemp.add(select);

            mTvCommandNameListtemp.addAll(mTvCommandNameList);


            final ArrayAdapter roomAdaptermode = new ArrayAdapter(getApplicationContext(), R.layout.spinner, mTvCommandNameListtemp);
            mSpinnerMode.setAdapter(roomAdaptermode);


            if (isEdit) {

                String buttoncode = "";


                switch (buttonPos) {


                    case "Button 1":
                        mEdtButtonName.setText(mTVButton1Label.getText().toString());
                        buttoncode = mTVStr1Label;
                        DelRemoteButtonPos = 1;
                        break;
                    case "Button 2":
                        mEdtButtonName.setText(mTVButton2Label.getText().toString());
                        buttoncode = mTVStr2Label;
                        DelRemoteButtonPos = 2;
                        break;
                    case "Button 3":
                        mEdtButtonName.setText(mTVButton3Label.getText().toString());
                        buttoncode = mTVStr3Label;
                        DelRemoteButtonPos = 3;
                        break;
                    case "Button 4":
                        mEdtButtonName.setText(mTVButton4Label.getText().toString());
                        buttoncode = mTVStr4Label;
                        DelRemoteButtonPos = 4;
                        break;
                    case "Button 5":
                        mEdtButtonName.setText(mTVButton5Label.getText().toString());
                        buttoncode = mTVStr5Label;
                        DelRemoteButtonPos = 5;
                        break;
                    case "Button 6":
                        mEdtButtonName.setText(mTVButton6Label.getText().toString());
                        buttoncode = mTVStr6Label;
                        DelRemoteButtonPos = 6;
                        break;


                }
                for (int a = 0; a < mTvCommandNameList.size(); a++) {
                    if (mTvCommandNameList.get(a).getName().equalsIgnoreCase(buttoncode)) {
                        mSpinnerMode.setSelection(a, false);
                    }
                }


            }


            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSaveDialog();
                }
            });

            mBtnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (DelRemoteButtonPos > 0) {
                        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete " + mEdtButtonName.getText().toString() + " button?", new ConfirmDialog.IDialogCallback() {
                            @Override
                            public void onConfirmDialogYesClick() {
                                if (DelRemoteButtonPos > 0) {
                                    mDialog.dismiss();
                                    deleteButton(String.valueOf(DelRemoteButtonPos));
                                }
                            }

                            @Override
                            public void onConfirmDialogNoClick() {
                            }

                        });
                        newFragment.show(getFragmentManager(), "dialog");
                    } else {
                        Toast.makeText(TVRemote.this, "Please select button which you want to delete.", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void onBackPressed() {

        if (ChatApplication.CurrnetFragment == R.id.navigationMood) {
            startActivity(new Intent(TVRemote.this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        super.onBackPressed();
    }
}
