package com.spike.bot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.User;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 26/8/19.
 * Gmail : vipul patel
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText et_username,et_password;
    public Button btn_login,btnSignUp;
    public AppCompatTextView btn_SKIP;
    public String imei="",token="",isFlag="";

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isFlag=getIntent().getStringExtra("isFlag");
        if(TextUtils.isEmpty(isFlag)){
            isFlag="";
        }
        setUiId();
    }

    private void setUiId() {
        getUUId();

        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        btn_SKIP=findViewById(R.id.btn_SKIP);
        btn_login=findViewById(R.id.btn_login);
        btnSignUp=findViewById(R.id.btnSignUp);

        btn_login.setOnClickListener(this);
        btn_SKIP.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        if(TextUtils.isEmpty(Common.getPrefValue(LoginActivity.this, Constants.lock_exe)) &&
                Common.getPrefValue(LoginActivity.this, Constants.lock_exe).length()==0 || TextUtils.isEmpty(Common.getPrefValue(LoginActivity.this, Constants.lock_token))){
            Constants.lockDate=0;
        }else {
            Constants.lockDate= Integer.parseInt(Common.getPrefValue(LoginActivity.this, Constants.lock_exe));
            Constants.access_token= Common.getPrefValue(LoginActivity.this, Constants.lock_token);
        }
        if (isFlag.equalsIgnoreCase("true")) {
            btn_SKIP.setVisibility(View.VISIBLE);
        }else if( Constants.checkLoginAccountCount(this)){
            ChatApplication.currentuserId = Constants.getuser(this).getUser_id();
            startHomeIntent();
        }else {
            btn_SKIP.setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    private void getUUId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            } else {
                imei = ActivityHelper.getIMEI(this);
            }
        } else {
            imei = ActivityHelper.getIMEI(this);
        }

        token = FirebaseInstanceId.getInstance().getToken();
        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);

    }

    @Override
    public void onClick(View v) {
        if(v==btn_SKIP){
            ChatApplication.isLocalFragmentResume=false;
            ChatApplication.isMainFragmentNeedResume=false;
            this.finish();
        }else if(v==btn_login){
            if (TextUtils.isEmpty(imei)) {
                getUUId();
            }
            if (TextUtils.isEmpty(et_username.getText().toString())) {
                et_username.requestFocus();
                et_username.setError("Enter User Name");
            } else if (TextUtils.isEmpty(et_password.getText().toString())) {
                et_password.requestFocus();
                et_password.setError("Enter Password");
            } else {
                loginCloud(et_username.getText().toString(), et_password.getText().toString());
            }

        }else if(v==btnSignUp){
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        }
    }

    public void loginCloud(String username, String password) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(LoginActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(imei)) {
            getUUId();
        }


        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_name", et_username.getText().toString().trim());
            obj.put("user_password", et_password.getText().toString().trim());
            obj.put("device_id", imei);
            obj.put("device_type", "android");
            obj.put("device_push_token", token);
            obj.put("uuid", ChatApplication.getUuid());
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = Constants.CLOUD_SERVER_URL + Constants.APP_LOGIN;

        ActivityHelper.showProgressDialog(LoginActivity.this, "Please wait...", false);

        ChatApplication.logDisplay("login is " + url + " " + obj.toString());
        //http://34.212.76.50/applogin
        new GetJsonTask(LoginActivity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

//                        ActivityHelper.hideKeyboard(LoginActivity.this);

                        JSONObject data = result.getJSONObject("data");
                        String cloudIp = data.getString("ip");
                        String local_ip = data.getString("local_ip");
                        ChatApplication.logDisplay("login response is " + data.toString());

                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);

                        String first_name = data.getString("first_name");
                        String last_name = data.getString("last_name");
                        String user_id = data.getString("user_id");
                        String admin = data.getString("admin");
                        String mac_address = data.getString("mac_address");
                        Constants.adminType = Integer.parseInt(admin);

                        Common.savePrefValue(LoginActivity.this, Constants.PREF_CLOUDLOGIN, "true");
                        Common.savePrefValue(LoginActivity.this, Constants.PREF_IP, cloudIp);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ID, user_id);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_ADMIN_TYPE, admin);
                        Common.savePrefValue(LoginActivity.this, Constants.USER_TYPE, user_id);

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
                        ChatApplication.logDisplay("jsonText text is " + jsonText);
                        List<User> userList = new ArrayList<User>();
                        if (!TextUtils.isEmpty(jsonText) && !jsonText.equals("[]") && !jsonText.equals("null")) {
                            Type type = new TypeToken<List<User>>() {}.getType();
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

                        ChatApplication.isCallDeviceList=true;
                        startHomeIntent();

                        ActivityHelper.dismissProgressDialog();

                    } else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void startHomeIntent() {
        Intent intent=new Intent(this,Main2Activity.class);
        startActivity(intent);
        this.finish();

    }
}
