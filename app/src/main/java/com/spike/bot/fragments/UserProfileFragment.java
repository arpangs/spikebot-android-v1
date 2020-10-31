package com.spike.bot.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.LoginSplashActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.User;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

/**
 * Created by Sagar on 27/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    String TAG = "ProfileActivity", stroldPassword = "", strconfirmpassword = "", strnewpassword = "";

    EditText et_profile_first_name, et_profile_last_name, et_profile_contact_no, et_profile_email, et_profile_user_name;
    LinearLayout ll_password_view_expand, ll_pass_edittext_view;
    ImageView img_pass_arrow;
    Button btn_save, btnChangePassword, btnforgotPassword;
    EditText edt_new_password, edt_confrim_password;
    Animation slideUpAnimation, slideDownAnimation;
    Dialog otpdialog, passworddialog;
    OtpTextView otp_view;
    ProgressDialog m_progressDialog;
    Dialog dialog;

    public UserProfileFragment() {
        super();
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param phone
     * @return
     */

    public static boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 10 || phone.length() > 13) {
                check = false;
            } else {
                check = android.util.Patterns.PHONE.matcher(phone).matches();
            }
        } else {
            check = false;
        }
        return check;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        et_profile_first_name = view.findViewById(R.id.et_profile_first_name);
        et_profile_last_name = view.findViewById(R.id.et_profile_last_name);
        et_profile_contact_no = view.findViewById(R.id.et_profile_contact_no);
        et_profile_email = view.findViewById(R.id.et_profile_email);
        et_profile_user_name = view.findViewById(R.id.et_profile_user_name);
        btn_save = view.findViewById(R.id.btn_save);
        ll_password_view_expand = view.findViewById(R.id.ll_password_view);
        ll_pass_edittext_view = view.findViewById(R.id.ll_pass_edittext_view);
        img_pass_arrow = view.findViewById(R.id.img_pass_arrow);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        btnforgotPassword = view.findViewById(R.id.btnforgotPassword);
        btnforgotPassword.setOnClickListener(this);
        edt_new_password = view.findViewById(R.id.et_new_password);
        edt_confrim_password = view.findViewById(R.id.et_new_password_confirm);

        et_profile_first_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_profile_last_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_profile_user_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);


        slideUpAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_out_bottom);

        slideDownAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_in_bottom);


        ll_password_view_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_pass_edittext_view.getVisibility() == View.VISIBLE) {
                    //   ll_pass_edittext_view.setAnimation(slideDownAnimation);
                    ll_pass_edittext_view.setVisibility(View.GONE);
                    img_pass_arrow.setImageResource(R.drawable.icn_arrow_right);

                } else {
                    // ll_pass_edittext_view.setAnimation(slideUpAnimation);
                    img_pass_arrow.setImageResource(R.drawable.down);
                    ll_pass_edittext_view.setVisibility(View.VISIBLE);

                }

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveProfile();
            }
        });

        getProfile();

        return view;
    }

    /*user profile */
    public void getProfile() {

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }


        ActivityHelper.showProgressDialog(getContext(), "Please wait....", false);

        /*String url = ChatApplication.url + Constants.GET_USER_PROFILE_INFO;

        new GetJsonTask(getContext(),url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    //{"code":200,"message":"success","data":{"userProfileData":[{"user_email":"test@gmail.com","first_name":"test","last_name":"patel","user_name":"test","user_phone":"123123"}]}}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        JSONObject data = result.getJSONObject("data");
                        JSONArray profileArray = data.getJSONArray("userProfileData");
                        JSONObject obj = profileArray.getJSONObject(0);

                        ChatApplication.logDisplay( "getProfile obj " + obj.toString() );
                        String user_email = obj.getString("user_email");
                        String first_name = obj.getString("first_name");
                        String last_name = obj.getString("last_name");
                        String user_name = obj.getString("user_name");
                        String user_phone = obj.getString("user_phone");

                        et_profile_first_name.setText(first_name);
                        et_profile_last_name.setText(last_name);
                        et_profile_contact_no.setText(user_phone);
                        et_profile_email.setText(user_email);
                        et_profile_user_name.setText(user_name);
                        et_profile_first_name.setSelection(et_profile_first_name.getText().length());

                        ActivityHelper.dismissProgressDialog();

                    }else{
                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay( "Exception getProfile e.getMessage() " + e.getMessage() );
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetProfile(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ActivityHelper.dismissProgressDialog();
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        JSONArray profileArray = data.getJSONArray("userProfileData");
                        JSONObject obj = profileArray.getJSONObject(0);

                        ChatApplication.logDisplay("getProfile obj " + obj.toString());
                        String user_email = obj.getString("user_email");
                        String first_name = obj.getString("first_name");
                        String last_name = obj.getString("last_name");
                        String user_name = obj.getString("user_name");
                        String user_phone = obj.getString("user_phone");

                        et_profile_first_name.setText(first_name);
                        et_profile_last_name.setText(last_name);
                        et_profile_contact_no.setText(user_phone);
                        et_profile_email.setText(user_email);
                        et_profile_user_name.setText(user_name);
                        et_profile_first_name.setSelection(et_profile_first_name.getText().length());

                        ActivityHelper.dismissProgressDialog();

                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay("Exception getProfile e.getMessage() " + e.getMessage());
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void saveProfile() {

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication.logDisplay("saveProfile saveProfile");
        if (TextUtils.isEmpty(et_profile_first_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_last_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_user_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter User Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_contact_no.getText().toString())) {
            Toast.makeText(getContext(), "Enter Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidMobile(et_profile_contact_no.getText().toString())) {
            Toast.makeText(getContext(), "Invalid Phone No", Toast.LENGTH_SHORT).show();
            return;
        }


        if (et_profile_user_name.getText().toString().trim().matches(".*([ \t]).*")) {
            Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(getContext(), "Please wait.", false);
/*
        JSONObject obj = new JSONObject();
        try {
            obj.put("first_name",et_profile_first_name.getText().toString());
            obj.put("last_name",et_profile_last_name.getText().toString());
            obj.put("user_name",et_profile_user_name.getText().toString());
            obj.put("user_phone",et_profile_contact_no.getText().toString());
            obj.put("user_email",et_profile_email.getText().toString());
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            if(strPassword.length()>0){
                obj.put("user_password",""+strPassword);
            }else {
                obj.put("user_password","");
            }

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.SAVE_USER_PROFILE_DETAILS;

        new GetJsonTask(getContext(),url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay( "getProfile onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success","data":{"userProfileData":[{"user_email":"test@gmail.com","first_name":"test","last_name":"patel","user_name":"test","user_phone":"123123"}]}}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        Common.savePrefValue(ChatApplication.getInstance(), "first_name", et_profile_first_name.getText().toString());
                        Common.savePrefValue(ChatApplication.getInstance(), "last_name", et_profile_last_name.getText().toString());

                        JSONObject jsonObject=new JSONObject(result.toString());
                        JSONObject jdata=jsonObject.optJSONObject("data");
                        String password=jdata.optString("user_password");
                        String user_id=jdata.optString("user_id");
                        String ip=jdata.optString("ip");
                        String first_name=jdata.optString("first_name");
                        String last_name=jdata.optString("last_name");


                        String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                        List<User> userList1 = new ArrayList<User>();
                        Gson gson = new Gson();
                        if (!TextUtils.isEmpty(jsonTextTemp1)) {
                            Type type = new TypeToken<List<User>>() {}.getType();
                            userList1 = gson.fromJson(jsonTextTemp1, type);
                        }

                        for (int i=0; i<userList1.size(); i++) {
                            if (userList1.get(i).isActive()) {
                                userList1.get(i).setPassword(password);
                                userList1.get(i).setFirstname(first_name);
                                userList1.get(i).setLastname(last_name);
                                break;
                            }
                        }

                        String jsonCurProduct = gson.toJson(userList1);
                        Common.savePrefValue(getActivity(), Common.USER_JSON, jsonCurProduct);

                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                        ActivityHelper.hideKeyboard(getActivity());
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshUserData = true;


                        getActivity().finish();

                    }else{
                        ChatApplication.isRefreshUserData = false;
                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay( "Exception saveProfile e.getMessage() " + e.getMessage() );
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().SaveProfile(et_profile_first_name.getText().toString(), et_profile_last_name.getText().toString(),
                et_profile_user_name.getText().toString(), et_profile_contact_no.getText().toString(), et_profile_email.getText().toString(),
                stroldPassword, new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        try {
                            JSONObject result = new JSONObject(stringResponse);
                            ChatApplication.logDisplay("getProfile onSuccess " + result.toString());
                            //{"code":200,"message":"success","data":{"userProfileData":[{"user_email":"test@gmail.com","first_name":"test","last_name":"patel","user_name":"test","user_phone":"123123"}]}}
                            int code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {
                                Common.savePrefValue(ChatApplication.getInstance(), "first_name", et_profile_first_name.getText().toString());
                                Common.savePrefValue(ChatApplication.getInstance(), "last_name", et_profile_last_name.getText().toString());

                                JSONObject jsonObject = new JSONObject(result.toString());
                                JSONObject jdata = jsonObject.optJSONObject("data");
                                String password = jdata.optString("user_password");
                                String user_id = jdata.optString("user_id");
                                String ip = jdata.optString("ip");
                                String first_name = jdata.optString("first_name");
                                String last_name = jdata.optString("last_name");


                                String jsonTextTemp1 = Common.getPrefValue(getContext(), Common.USER_JSON);
                                List<User> userList1 = new ArrayList<User>();
                                Gson gson = new Gson();
                                if (!TextUtils.isEmpty(jsonTextTemp1)) {
                                    Type type = new TypeToken<List<User>>() {
                                    }.getType();
                                    userList1 = gson.fromJson(jsonTextTemp1, type);
                                }

                                for (int i = 0; i < userList1.size(); i++) {
                                    if (userList1.get(i).isActive()) {
                                        userList1.get(i).setPassword(password);
                                        userList1.get(i).setFirstname(first_name);
                                        userList1.get(i).setLastname(last_name);
                                        break;
                                    }
                                }

                                String jsonCurProduct = gson.toJson(userList1);
                                Common.savePrefValue(getActivity(), Common.USER_JSON, jsonCurProduct);

                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                ActivityHelper.hideKeyboard(getActivity());
                                ActivityHelper.dismissProgressDialog();
                                ChatApplication.isRefreshUserData = true;


                                getActivity().finish();

                            } else {
                                ChatApplication.isRefreshUserData = false;
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ChatApplication.logDisplay("Exception saveProfile e.getMessage() " + e.getMessage());
                        } finally {
                            ActivityHelper.dismissProgressDialog();

                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                        Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ActivityHelper.dismissProgressDialog();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void ChangePassword(String stroldPassword, String strnewpassword) {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        ActivityHelper.showProgressDialog(getContext(), "Please wait.", false);

        SpikeBotApi.getInstance().ChangePassword(stroldPassword, strnewpassword, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay("Exception saveProfile e.getMessage() " + e.getMessage());
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
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addCustomRoom() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);
        dialog.setCanceledOnTouchOutside(true);

        final TextInputLayout inputRoom = dialog.findViewById(R.id.inputRoom);
        final TextInputLayout inputPassword = dialog.findViewById(R.id.inputPassword);
        final TextInputLayout inputnewPassword = dialog.findViewById(R.id.inputnewPassword);
        final TextInputLayout inputconfirmPassword = dialog.findViewById(R.id.inputconfirmPassword);

        final TextInputEditText edtOldPasswordChild = dialog.findViewById(R.id.edtOldPasswordChild);
        final TextInputEditText edtnewPasswordChild = dialog.findViewById(R.id.edtnewPasswordChild);
        final TextInputEditText edtconfirmPasswordChild = dialog.findViewById(R.id.edtconfirmPasswordChild);

        inputRoom.setVisibility(View.GONE);
        inputPassword.setVisibility(View.VISIBLE);
        inputnewPassword.setVisibility(View.VISIBLE);
        inputconfirmPassword.setVisibility(View.VISIBLE);

        inputPassword.setHint("Enter current password");

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_title.setText("Change Password");

//        room_name.setHint("Enter Password");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(edtOldPasswordChild.getText().length()>0)
                {
                    ChatApplication.keyBoardHideForce(getActivity());
                    dialog.cancel();
                    stroldPassword=edtOldPasswordChild.getText().toString();
                    strnewpassword=edtnewPasswordChild.getText().toString();
                    strconfirmpassword=edtconfirmPasswordChild.getText().toString();
                }else {
                    ChatApplication.showToast(getActivity(), "Please enter password");
                }*/

                strnewpassword = edtnewPasswordChild.getText().toString();
                strconfirmpassword = edtconfirmPasswordChild.getText().toString();

                if (TextUtils.isEmpty(edtOldPasswordChild.getText().toString())) {
                    ChatApplication.showToast(getActivity(), "Please enter old password");
                } else if (TextUtils.isEmpty(edtnewPasswordChild.getText().toString())) {
                    ChatApplication.showToast(getActivity(), "Please enter new password");
                } else if (TextUtils.isEmpty(edtconfirmPasswordChild.getText().toString())) {
                    ChatApplication.showToast(getActivity(), "Please enter confirm password");
                } else if (!strnewpassword.equals(strconfirmpassword)) {
                    ChatApplication.showToast(getActivity(), "New password and confirm password must be same");
                } else {
                    stroldPassword = edtOldPasswordChild.getText().toString();
                    strnewpassword = edtnewPasswordChild.getText().toString();
                    ChangePassword(stroldPassword, strnewpassword);
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        //ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

    }

    @Override
    public void onClick(View v) {
        if (v == btnChangePassword) {
            addCustomRoom();
        }

        if (v == btnforgotPassword) {

            if (!ActivityHelper.isConnectingToInternet(getActivity())) {
                ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
                return;
            }

            if (ChatApplication.url.contains("http://"))
                ChatApplication.url = ChatApplication.url.replace("http://", "");
            showProgressDialog(getActivity(), "Please wait...", false);
            SpikeBotApi.getInstance().forgetPassword(et_profile_contact_no.getText().toString(), new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {

                    try {
                        JSONObject result = new JSONObject(stringResponse);
                        dismissProgressDialog();
                        if (result.has("code")) {
                            if (result.getString("code").equalsIgnoreCase("200")) {

                                if (stringResponse.contains("Success")) {
                                    dialogOtp();
                                    Common.savePrefValue(getActivity(), Constants.FORGETPASSWORD_MOBILENUMBER, et_profile_contact_no.getText().toString());
                                }
                            } else {

                                String message;

                                if (result.has("message")) {
                                    message = result.getString("message");
                                } else {
                                    message = "Something went wrong, Please try again later";
                                }

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onData_FailureResponse() {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*otp dialog*/
    private void dialogOtp() {
        otpdialog = new Dialog(getActivity());
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
//                OTPValidate();  // this facility disable due to consistency with IOS
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

                if (!ActivityHelper.isConnectingToInternet(getActivity())) {
                    ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
                    return;
                }

                showProgressDialog(getActivity(), "Please wait...", false);

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
                        Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        otpdialog.show();

    }

    private boolean isOTPValidate() {

        if (otp_view.getOTP().length() != 6) {
            Toast.makeText(getActivity(), "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
            otp_view.requestFocus();
            return false;

        } else {
            return true;
        }


    }

    private void OTPValidate() {

        if (isOTPValidate()) {

            if (!ActivityHelper.isConnectingToInternet(getActivity())) {
                ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
                return;
            }
            showProgressDialog(getActivity(), "Please wait...", false);
            SpikeBotApi.getInstance().OTPVerify(otp_view.getOTP(), new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    dismissProgressDialog();
                    if (stringResponse.contains("Success")) {
                        dialogNewPassword();
                        Common.savePrefValue(getActivity(), Constants.CURRENT_OPT, otp_view.getOTP().toString());
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(stringResponse);
                            if (!stringResponse.contains("200")) {
                                if (jsonObject.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onData_FailureResponse() {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    /*set new password password dialog*/
    private void dialogNewPassword() {
        passworddialog = new Dialog(getActivity());
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

        EditText mPassword = passworddialog.findViewById(R.id.edSensorguestPasscode);

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
                if (!ActivityHelper.isConnectingToInternet(getActivity())) {
                    ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
                    return;
                }
                showProgressDialog(getActivity(), "Please wait...", false);
                SpikeBotApi.getInstance().SetNewPassword(mPassword.getText().toString(), new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        dismissProgressDialog();
                        try {
                            JSONObject result = new JSONObject(stringResponse);
                            int code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {

                                JSONObject data = result.getJSONObject("data");
                                String cloudIp = data.getString("ip");
                                String local_ip = data.getString("local_ip_address");
                                ChatApplication.logDisplay("login response is " + data.toString());

                                Common.savePrefValue(getActivity(), Constants.PREF_CLOUDLOGIN, "true");
                                Common.savePrefValue(getActivity(), Constants.PREF_IP, cloudIp);

                                String first_name = data.getString("first_name");
                                String last_name = data.getString("last_name");
                                String user_id = data.getString("user_id");
                                String admin = data.getString("admin");
                                String mac_address = data.getString("mac_address");
                                String auth_key = "";

                                if (data.has("auth_key"))
                                    auth_key = data.getString("auth_key"); // dev arp add on 22 june 2020

                                Constants.adminType = Integer.parseInt(admin);

                                ChatApplication.currentuserId = user_id;
                                Common.savePrefValue(getActivity(), Constants.PREF_CLOUDLOGIN, "true");
                                Common.savePrefValue(getActivity(), Constants.PREF_IP, cloudIp);
                                Common.savePrefValue(getActivity(), Constants.USER_ID, user_id);
                                Common.savePrefValue(getActivity(), Constants.USER_ADMIN_TYPE, admin);
                                Common.savePrefValue(getActivity(), Constants.USER_TYPE, user_id);

                                Common.savePrefValue(getActivity(), Constants.AUTHORIZATION_TOKEN, auth_key); // dev arp add new key on 22 june 2020

                                if (Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                                    Constants.room_type = 0;
                                    Common.savePrefValue(getActivity(), Constants.USER_ROOM_TYPE, "" + 0);
                                } else {
                                    Constants.room_type = 2;
                                    Common.savePrefValue(getActivity(), Constants.USER_ROOM_TYPE, "" + 2);
                                }
                                String user_password = "";
                                if (data.has("user_password")) {
                                    user_password = data.getString("user_password");
                                }
                                Common.savePrefValue(getActivity(), Constants.USER_PASSWORD, user_password);

                                User user = new User(user_id, first_name, last_name, cloudIp, false, user_password, admin, local_ip, mac_address, auth_key);
                                Gson gson = new Gson();
                                String jsonText = Common.getPrefValue(getActivity(), Common.USER_JSON);
                                List<User> userList = new ArrayList<User>();
                                //*set active user *//*
                                if (!TextUtils.isEmpty(jsonText) && !jsonText.equals("[]") && !jsonText.equals("null")) {
                                    Type type = new TypeToken<List<User>>() {
                                    }.getType();
                                    userList = gson.fromJson(jsonText, type);

                                    if (userList != null && userList.size() != 0) {
                                        boolean isFound = false;
                                        for (User user1 : userList) {
                                            if (user1.getUser_id().equalsIgnoreCase(user.getUser_id())) {
                                                isFound = true;
                                                user1.setIsActive(true);
                                            } else {
                                                user1.setIsActive(false);
                                            }
                                        }
                                        if (!isFound) {
                                            user.setIsActive(true);
                                            userList.add(user);
                                        }
                                    }

                                    String jsonCurProduct = gson.toJson(userList);
                                    Common.savePrefValue(getActivity(), Common.USER_JSON, jsonCurProduct);


                                } else {

                                    user.setIsActive(true);
                                    userList.add(user);

                                    String jsonCurProduct = gson.toJson(userList);
                                    Common.savePrefValue(getActivity(), Common.USER_JSON, jsonCurProduct);
                                }

                                ChatApplication.isCallDeviceList = true;
                                startHomeIntent();

                                ActivityHelper.dismissProgressDialog();

                            } else {
                                ChatApplication.showToast(getActivity(), message);
                            }
                        } catch (Exception e) {
                            ActivityHelper.dismissProgressDialog();
                            e.printStackTrace();
                        } finally {
                            ActivityHelper.dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onData_FailureResponse() {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        dismissProgressDialog();
                        Toast.makeText(getActivity(), "Something went wrong, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        passworddialog.show();

    }

    /* start home screen */
    private void startHomeIntent() {
        ConnectivityReceiver.counter = 0;
        Intent intent = new Intent(getActivity(), Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

    }

    public void showProgressDialog(Context context, String message, boolean iscancle) {
        m_progressDialog = new ProgressDialog(getActivity());
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
