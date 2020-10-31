package com.spike.bot.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.User;
import com.spike.bot.receiver.ConnectivityReceiver;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 26/8/19.
 * Gmail : vipul patel
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private static String fcmId;
    public EditText et_username, et_password;
    public Button btn_login, btnSignUp;
    public ImageView btn_SKIP;
    public String imei = "", token = "", isFlag = "";
    TextView txt_forgot_password;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isFlag = getIntent().getStringExtra("isFlag");
        if (TextUtils.isEmpty(isFlag)) {
            isFlag = "";
        }
        setUiId();
    }

    private void setUiId() {
        getUUId();

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_SKIP = findViewById(R.id.btn_SKIP);
        btn_login = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btnSignUp);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);
        //   edit_contact_number = findViewById(R.id.edit_contact_number);
        //  countryCodePicker = findViewById(R.id.ccp);

        //  countryCodePicker.setNumberAutoFormattingEnabled(true);
        //  countryCodePicker.registerCarrierNumberEditText(edit_contact_number);


        String styledText = "<u><font color='#333333'>" + getResources().getString(R.string.forgotpassword) + "</font></u>";
        txt_forgot_password.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(imei)) {
                    getUUId();
                    return;
                }
                if (TextUtils.isEmpty(et_username.getText().toString())) {
                    et_username.requestFocus();
                    et_username.setError("Enter User Name");
                } else if (TextUtils.isEmpty(et_password.getText().toString())) {
                    et_password.requestFocus();
                    et_password.setError("Enter Password");
                } else {
                    Common.hideSoftKeyboard(LoginActivity.this);
                    loginCloud();
                }
            }
        });
        btn_SKIP.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        txt_forgot_password.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        if (TextUtils.isEmpty(Common.getPrefValue(LoginActivity.this, Constants.lock_exe)) &&
                Common.getPrefValue(LoginActivity.this, Constants.lock_exe).length() == 0 || TextUtils.isEmpty(Common.getPrefValue(LoginActivity.this, Constants.lock_token))) {
            Constants.lockDate = 0;
        } else {
            Constants.lockDate = Integer.parseInt(Common.getPrefValue(LoginActivity.this, Constants.lock_exe));
            Constants.access_token = Common.getPrefValue(LoginActivity.this, Constants.lock_token);
        }
        if (isFlag.equalsIgnoreCase("true")) {
            btn_SKIP.setVisibility(View.VISIBLE);
        } else if (Constants.checkLoginAccountCount(this)) {
            ChatApplication.currentuserId = Constants.getuser(this).getUser_id();
            startHomeIntent();
        } else {
            //  btn_SKIP.setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    /* get uuid for login api*/
    private void getUUId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            } else {
                imei = ActivityHelper.getIMEI(this);
            }
        } else {
            imei = ActivityHelper.getIMEI(this);
        }

//        token = FirebaseInstanceId.getInstance().getToken();


            FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG---->", "getInstanceId failed", task.getException());

                            return;
                        }

                        /*id device token get null- please check your device date and time and network connection first- */

                        token = task.getResult().getToken();
                        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);

                    }

                });

    }



    @Override
    public void onClick(View v) {
        if (v == btn_SKIP) {
            if (isFlag.equalsIgnoreCase("true")) {
                ChatApplication.isLocalFragmentResume = false;
                ChatApplication.isMainFragmentNeedResume = false;
                this.finish();
            } else {
                Intent intent = new Intent(this, LoginSplashActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (v == btnSignUp) {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        } else if (v == txt_forgot_password) {
            Intent intent = new Intent(this, ForgotpasswordActivity.class);
            startActivity(intent);
        }
    }

    /*login call*/
    public void loginCloud() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(LoginActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        }

       /* JSONObject obj = new JSONObject();
        try {
            obj.put("user_name", et_username.getText().toString().trim());
            obj.put("user_password", et_password.getText().toString().trim());
            obj.put("device_id", imei);
            obj.put("device_type", "android");
            obj.put("device_push_token", token);
            obj.put("uuid", ChatApplication.getUuid());
            obj.put("fcm_token", "Android_test"); // dev arp add new key on 22 june 2020 - web dev parth
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        } catch (Exception e) {
            e.printStackTrace();
        }*/

//        String url = Constants.CLOUD_SERVER_URL + Constants.APP_LOGIN;

        ActivityHelper.showProgressDialog(LoginActivity.this, "Please wait...", false);

//        ChatApplication.logDisplay("login is " + url + " " + obj.toString());

        //http://34.212.76.50/applogin
        /*new GetJsonTask(LoginActivity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject data = result.getJSONObject("data");
                        String cloudIp = data.getString("ip");
//                        String local_ip = data.getString("local_ip");
                        String local_ip = data.getString("local_ip_address"); // web developer change the key on 10 july 2020 - update by dev arpan
                        ChatApplication.logDisplay("login response is " + data.toString());

                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);

                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        String user_id = data.getString("user_id");
                        String admin = data.getString("admin");
                        String mac_address = data.getString("mac_address");
                        Constants.adminType = Integer.parseInt(admin);

                        ChatApplication.currentuserId = user_id;
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ID, user_id);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ADMIN_TYPE, admin);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_TYPE, user_id);
                        Common.savePrefValue(LoginActivity.this, Constants.AUTHORIZATION_TOKEN, user_id); // dev arp add new key on 22 june 2020

                        if (Common.getPrefValue(LoginActivity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                            Constants.room_type = 0;
                            Common.savePrefValue(LoginActivity.this, Constants.USER_ROOM_TYPE, "" + 0);
                        } else {
                            Constants.room_type = 2;
                            Common.savePrefValue(LoginActivity.this, Constants.USER_ROOM_TYPE, "" + 2);
                        }
                        String user_password = "";
                        if (data.has("user_password")) {
                            user_password = data.getString("user_password");
                        }
                        Common.savePrefValue(LoginActivity.this, Constants.USER_PASSWORD, user_password);

                        User user = new User(user_id, first_name, last_name, cloudIp, false, user_password, admin, local_ip, mac_address);
                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
                        List<User> userList = new ArrayList<User>();
                        *//*set active user *//*
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
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);


                        } else {

                            user.setIsActive(true);
                            userList.add(user);

                            String jsonCurProduct = gson.toJson(userList);
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);
                        }

                        ChatApplication.isCallDeviceList = true;
                        startHomeIntent();

                        ActivityHelper.dismissProgressDialog();

                    } else {
                        ChatApplication.showToast(LoginActivity.this, message);
                    }
                } catch (Exception e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong1));
            }
        }).execute();*/

        /*dev arp add on 23 june 2020*/

        SpikeBotApi.getInstance().loginUser(et_username.getText().toString().trim(), et_password.getText().toString().trim(), imei, new DataResponseListener() {

            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject data = result.getJSONObject("data");
                        String cloudIp = data.getString("ip");
                        String local_ip = data.getString("local_ip_address");
                        ChatApplication.logDisplay("login response is " + data.toString());

                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);

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
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ID, user_id);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ADMIN_TYPE, admin);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_TYPE, user_id);

                        Common.savePrefValue(LoginActivity.this, Constants.AUTHORIZATION_TOKEN, auth_key); // dev arp add new key on 22 june 2020

                        if (Common.getPrefValue(LoginActivity.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                            Constants.room_type = 0;
                            Common.savePrefValue(LoginActivity.this, Constants.USER_ROOM_TYPE, "" + 0);
                        } else {
                            Constants.room_type = 2;
                            Common.savePrefValue(LoginActivity.this, Constants.USER_ROOM_TYPE, "" + 2);
                        }
                        String user_password = "";
                        if (data.has("user_password")) {
                            user_password = data.getString("user_password");
                        }
                        Common.savePrefValue(LoginActivity.this, Constants.USER_PASSWORD, user_password);

                        User user = new User(user_id, first_name, last_name, cloudIp, false, user_password, admin, local_ip, mac_address, auth_key);
                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
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
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);


                        } else {

                            user.setIsActive(true);
                            userList.add(user);

                            String jsonCurProduct = gson.toJson(userList);
                            Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);
                        }

                        ChatApplication.isCallDeviceList = true;
                        startHomeIntent();

                        ActivityHelper.dismissProgressDialog();

                    } else {
                        ChatApplication.showToast(LoginActivity.this, message);
                    }
                } catch (Exception e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                    //startSocketConnection();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    /* start home screen */
    private void startHomeIntent() {
        ConnectivityReceiver.counter = 0;
        Intent intent = new Intent(this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();

    }
}
