package com.spike.bot.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.kp.core.ActivityHelper;
import com.spike.bot.Beacon.ScannerWifiListActivity;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.TTLock.YaleLockInfoActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_forgotpassword_SKIP;
    Button btn_submit;
    Dialog otpdialog, passworddialog;
    EditText edit_contact_number;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        img_forgotpassword_SKIP = findViewById(R.id.img_forgotpassword_SKIP);
        btn_submit = findViewById(R.id.btn_submit);
        edit_contact_number = findViewById(R.id.edit_contact_number);
        countryCodePicker = findViewById(R.id.ccp);

        countryCodePicker = findViewById(R.id.ccp);

        countryCodePicker.setNumberAutoFormattingEnabled(true);
        countryCodePicker.registerCarrierNumberEditText(edit_contact_number);

        img_forgotpassword_SKIP.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == img_forgotpassword_SKIP) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (v == btn_submit) {
            dialogOtp();
        }
    }


    /*otp dialog*/
    private void dialogOtp() {
        otpdialog = new Dialog(ForgotpasswordActivity.this);
        otpdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpdialog.setCanceledOnTouchOutside(false);
        otpdialog.setContentView(R.layout.dialog_otp);

        OtpTextView otp_view = otpdialog.findViewById(R.id.otp_view);
        ImageView iv_close = otpdialog.findViewById(R.id.iv_close);
        Button btnSave = otpdialog.findViewById(R.id.btn_save);

        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpdialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpdialog.dismiss();
                dialogNewPassword();
            }
        });

        otpdialog.show();

    }


    /*set new password password dialog*/
    private void dialogNewPassword() {
        passworddialog = new Dialog(ForgotpasswordActivity.this);
        passworddialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passworddialog.setCanceledOnTouchOutside(false);
        passworddialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = passworddialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = passworddialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = passworddialog.findViewById(R.id.edSensorName);
        TextView label_password = passworddialog.findViewById(R.id.label_password);
        RelativeLayout relative_guestpasscode = passworddialog.findViewById(R.id.relative_guestpasscode);
        ImageView img_show_guestpasscode = passworddialog.findViewById(R.id.img_show_guestpasscode);

        img_show_guestpasscode.setVisibility(View.GONE);
        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);
        label_password.setVisibility(View.VISIBLE);

        relative_guestpasscode.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = passworddialog.findViewById(R.id.btn_save);
        ImageView iv_close = passworddialog.findViewById(R.id.iv_close);
        TextView tv_title = passworddialog.findViewById(R.id.tv_title);

        txtInputSensor.setHint("Enter password");
        tv_title.setText("New Password");


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passworddialog.dismiss();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        passworddialog.show();

    }
}
