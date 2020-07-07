package com.spike.bot.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_forgotpassword_SKIP;
    Button btn_submit;
    Dialog otpdialog, passworddialog;
    EditText edit_contact_number;
    CountryCodePicker countryCodePicker;
    OtpTextView otp_view;
    ProgressDialog m_progressDialog;

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

            if (isMobileNumberValid())

                if (!ActivityHelper.isConnectingToInternet(this)) {
                    ChatApplication.showToast(ForgotpasswordActivity.this, getResources().getString(R.string.disconnect));
                    return;
                }
            showProgressDialog(ForgotpasswordActivity.this, "Please wait...", false);
            SpikeBotApi.getInstance().forgetPassword("+" + countryCodePicker.getSelectedCountryCode() + edit_contact_number.getText().toString(), new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    dismissProgressDialog();
                    if (stringResponse.contains("Success")) {
                        dialogOtp();
                        Common.savePrefValue(ForgotpasswordActivity.this, Constants.FORGETPASSWORD_MOBILENUMBER, "+" + countryCodePicker.getSelectedCountryCode() + edit_contact_number.getText().toString());
                    }
                }

                @Override
                public void onData_FailureResponse() {
                    dismissProgressDialog();
                    Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    dismissProgressDialog();
                    Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    /*otp dialog*/
    private void dialogOtp() {
        otpdialog = new Dialog(ForgotpasswordActivity.this);
        otpdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpdialog.setCanceledOnTouchOutside(false);
        otpdialog.setContentView(R.layout.dialog_otp);

        otp_view = otpdialog.findViewById(R.id.otp_view);
        ImageView iv_close = otpdialog.findViewById(R.id.iv_close);
        Button btnSave = otpdialog.findViewById(R.id.btn_save);

        TextView mRetryOtp = otpdialog.findViewById(R.id.txt_retry_otp);

        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                OTPValidate();
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

                OTPValidate();


            }
        });

        mRetryOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ActivityHelper.isConnectingToInternet(ForgotpasswordActivity.this)) {
                    ChatApplication.showToast(ForgotpasswordActivity.this, getResources().getString(R.string.disconnect));
                    return;
                }
                showProgressDialog(ForgotpasswordActivity.this, "Please wait...", false);
                SpikeBotApi.getInstance().RetryPassword(Common.getPrefValue(ChatApplication.getContext(), Constants.FORGETPASSWORD_MOBILENUMBER), new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        dismissProgressDialog();
                        if (stringResponse.contains("Success")) {

                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        dismissProgressDialog();
                        Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        dismissProgressDialog();
                        Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });

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
        final TextInputEditText newpassword = passworddialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = passworddialog.findViewById(R.id.edSensorName);
        TextView label_password = passworddialog.findViewById(R.id.label_password);
        RelativeLayout relative_guestpasscode = passworddialog.findViewById(R.id.relative_guestpasscode);
        ImageView img_show_guestpasscode = passworddialog.findViewById(R.id.img_show_guestpasscode);
        EditText mPassword = passworddialog.findViewById(R.id.edSensorguestPasscode);

        img_show_guestpasscode.setVisibility(View.GONE);
        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);
        label_password.setVisibility(View.VISIBLE);

        relative_guestpasscode.setVisibility(View.VISIBLE);
        newpassword.setVisibility(View.GONE);
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

                if (!ActivityHelper.isConnectingToInternet(ForgotpasswordActivity.this)) {
                    ChatApplication.showToast(ForgotpasswordActivity.this, getResources().getString(R.string.disconnect));
                    return;
                }
                showProgressDialog(ForgotpasswordActivity.this, "Please wait...", false);
                SpikeBotApi.getInstance().SetNewPassword(mPassword.getText().toString(), new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        dismissProgressDialog();
                        if (stringResponse.contains("Success")) {
                            Intent intent = new Intent(ForgotpasswordActivity.this, LoginSplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        dismissProgressDialog();
                        Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        dismissProgressDialog();
                        Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        passworddialog.show();

    }

    private boolean isMobileNumberValid() {

        if (edit_contact_number.getText().length() != 10) {
            Toast.makeText(this, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
            edit_contact_number.requestFocus();
            return false;
        } else {
            return true;
        }


    }

    private boolean isOTPValidate() {

        if (otp_view.getOTP().length() != 6) {
            Toast.makeText(this, "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
            otp_view.requestFocus();
            return false;

        } else {
            return true;
        }


    }

    private void OTPValidate() {

        if (isOTPValidate()) {

            if (!ActivityHelper.isConnectingToInternet(this)) {
                ChatApplication.showToast(ForgotpasswordActivity.this, getResources().getString(R.string.disconnect));
                return;
            }
            showProgressDialog(this, "Please wait...", false);
            SpikeBotApi.getInstance().OTPVerify(otp_view.getOTP(), new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    dismissProgressDialog();
                    if (stringResponse.contains("Success")) {
                        dialogNewPassword();
                        Common.savePrefValue(ForgotpasswordActivity.this, Constants.CURRENT_OPT, otp_view.getOTP().toString());
                    }
                }

                @Override
                public void onData_FailureResponse() {
                    dismissProgressDialog();
                    Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    dismissProgressDialog();
                    Toast.makeText(ForgotpasswordActivity.this, "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    public void showProgressDialog(Context context, String message, boolean iscancle) {
        m_progressDialog = new ProgressDialog(this);
        m_progressDialog.setMessage(message);
        m_progressDialog.setCanceledOnTouchOutside(true);
        m_progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (m_progressDialog != null) {
            ChatApplication.logDisplay("m_progressDialog is null not");
            m_progressDialog.cancel();
            m_progressDialog.dismiss();
        }
    }
}
