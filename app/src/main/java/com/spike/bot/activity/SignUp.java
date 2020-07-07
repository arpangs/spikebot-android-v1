package com.spike.bot.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.iid.FirebaseInstanceId;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.socket.client.Socket;


/**
 * Created by Sagar on 29/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SignUp extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    String webUrl = "";
    String imei = "";
    private Button btn_signup, btn_sign_up_cancel;
    private EditText edt_first_name, edt_last_name, edt_email_id, edt_user_name, edt_password, edt_con_password, edt_phone_no, edtIPAddress;
    private ImageView btn_signupback;
    private Socket mSocket;
    private List<User> tempList;
    CountryCodePicker countryCodePicker;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_camera);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Sign Up");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_signup = findViewById(R.id.btn_sign_up);
        btn_sign_up_cancel = findViewById(R.id.btn_sign_up_cancel);
        btn_signupback = findViewById(R.id.btn_signupback);

        edt_first_name = findViewById(R.id.edt_first_name_su);
        edt_last_name = findViewById(R.id.edt_last_name_su);
        edt_email_id = findViewById(R.id.edt_emailid_su);
        edt_user_name = findViewById(R.id.edt_user_name_su);
        edt_password = findViewById(R.id.edt_password_su);
        edt_con_password = findViewById(R.id.edt_con_password_su);
        edt_phone_no = findViewById(R.id.edt_phone_no_su);
        edtIPAddress = findViewById(R.id.edtIPAddress);

        //  countryCodePicker = findViewById(R.id.ccp);

        //  countryCodePicker.setNumberAutoFormattingEnabled(true);
        //  countryCodePicker.registerCarrierNumberEditText(edt_phone_no);

        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
        tempList = new ArrayList<User>();


        if (!TextUtils.isEmpty(jsonText)) {
            Type type = new TypeToken<List<User>>() {
            }.getType();
            tempList = gson.fromJson(jsonText, type);
        }
       /* if(tempList.size() > 0){
            btn_sign_up_cancel.setVisibility(View.VISIBLE);
        }else{
            btn_sign_up_cancel.setVisibility(View.INVISIBLE);
        }*/

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToMobile(SignUp.this)) {
                    ChatApplication.showToast(SignUp.this, "Please connect to your gateway's local network and try again.");
                } else {
                    singUP();
                }

            }
        });

        btn_sign_up_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        btn_signupback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getUUID();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imei = ActivityHelper.getIMEI(this);
            //onResume();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* frist check wifi connect in your pie network */
    private void singUP() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        InetAddress addr = Constants.getLocalIpAddress(SignUp.this);
        String hostname = addr.toString().replace("/", "");

        String[] array = hostname.split("\\.");

        String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + array[3];


        if (TextUtils.isEmpty(edt_first_name.getText().toString())) {
            edt_first_name.requestFocus();
            edt_first_name.setError("Enter First Name");
            return;
        }
        if (TextUtils.isEmpty(edt_last_name.getText().toString())) {
            edt_last_name.requestFocus();
            edt_last_name.setError("Enter Last Name");
            return;
        }
        if (TextUtils.isEmpty(edt_email_id.getText().toString())) {
            edt_email_id.requestFocus();
            edt_email_id.setError("Enter Emaid Id");
            return;
        }
        if (TextUtils.isEmpty(edt_user_name.getText().toString())) {
            edt_user_name.requestFocus();
            edt_user_name.setError("Enter User Name");
            return;
        }

        if (edt_user_name.getText().toString().trim().matches(".*([ \t]).*")) {
            edt_user_name.requestFocus();
            edt_user_name.setError("Invalid Username");
            return;
        }

        if (TextUtils.isEmpty(edt_password.getText().toString())) {
            edt_password.requestFocus();
            edt_password.setError("Enter Password");
            return;
        }

        if (TextUtils.isEmpty(edt_phone_no.getText().toString())) {
            edt_phone_no.requestFocus();
            edt_phone_no.setError("Enter Phone No");
            return;
        }

        if (!isValidMobile(edt_phone_no.getText().toString())) {
            edt_phone_no.requestFocus();
            edt_phone_no.setError("Invalid Phone no");
            return;
        }

//        if (TextUtils.isEmpty(edtIPAddress.getText().toString())) {
//            edtIPAddress.requestFocus();
//            edtIPAddress.setError("Please enter ip address");
//            //Toast.makeText(getApplicationContext(), "Invalid Phone No", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (TextUtils.isEmpty(imei)) {
            getUUID();
        }


        if (!edt_password.getText().toString().trim().equalsIgnoreCase(edt_con_password.getText().toString().trim())) {
            edt_con_password.requestFocus();
            edt_con_password.setError("Password not match");
            //Toast.makeText(getApplicationContext(),"Password not match",Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SignUp.this, "Please wait...", false);
        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
//        webUrl = app.url;
        /*set statically ip address*/
        String url = "http://" + ipAddressPI + ":" + Constants.SIGNUP_API;
//        String url = "http://" + "192.168.175.121" + ":"  + Constants.SIGNUP_API;//111
//        String url = "http://" + edtIPAddress.getText().toString() + ":"  + Constants.SIGNUP_API;//117
        String token = FirebaseInstanceId.getInstance().getToken();
        Common.savePrefValue(getApplicationContext(), Constants.DEVICE_PUSH_TOKEN, token);


        /*JSONObject object = new JSONObject();
        try {
            object.put("first_name",edt_first_name.getText().toString());
            object.put("last_name",edt_last_name.getText().toString());
            object.put("user_name",edt_user_name.getText().toString().trim());
            object.put("user_email",edt_email_id.getText().toString());
            object.put("user_password",edt_password.getText().toString());
            object.put("user_phone",edt_phone_no.getText().toString());
            object.put("phone_id",imei);
            object.put("phone_type","android");
            object.put("device_push_token",token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is "+url+" "+object);


        new GetJsonTask(this,url ,"POST",object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        ChatApplication.isMainFragmentNeedResume = true;
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }

                        *//*//**
         *  {"code":200,"message":"Succesfully!
         *  Your account has been created",
         *  "data":{"first_name":"vipul",
         *  "last_name":"patel",
         *  "ip":"http:\/\/home.deepfoods.net:11150",
         *  "user_id":"1545317988870_UrL_0HY9u","user_password":"c1bc06"}}
         * *//*

                        JSONObject data=result.getJSONObject("data");
                     //   JSONObject object1=data.optJSONObject(data.toString());
                        String first_name=data.optString("first_name");
                        String ip=data.optString("ip");
                        String last_name=data.optString("last_name");
                        String user_id=data.optString("user_id");
                        String user_password=data.optString("user_password");
                        String admin=data.optString("admin");
                        String local_ip=data.optString("local_ip_address");
                        String mac_address = data.getString("mac_address");

                        Common.savePrefValue(SignUp.this,Constants.USER_PASSWORD,user_password);
                        Common.savePrefValue(SignUp.this,Constants.PREF_IP,ip);
                        Common.savePrefValue(SignUp.this,Constants.USER_ID,user_id);

                        Common.savePrefValue(SignUp.this, Constants.USER_ADMIN_TYPE, admin);

                        if (Common.getPrefValue(SignUp.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                            Constants.room_type = 0;
                            Common.savePrefValue(SignUp.this, Constants.USER_ROOM_TYPE, ""+0);
                        } else {
                            Constants.room_type = 2;
                            Common.savePrefValue(SignUp.this, Constants.USER_ROOM_TYPE, ""+2);
                        }

                        User user = new User(user_id,first_name,last_name,ip,false,user_password,admin,local_ip,mac_address);

                        Gson gson = new Gson();
                        String jsonText = Common.getPrefValue(getApplicationContext(),Common.USER_JSON);
                        List<User> userList = new ArrayList<User>();
                        user.setIsActive(true);
                        userList.add(user);

                        String jsonCurProduct = gson.toJson(userList);
                        Common.savePrefValue(getApplicationContext(),Common.USER_JSON,jsonCurProduct);

                        ChatApplication.logDisplay("response is signup "+data.toString());
                        ChatApplication.isRefreshHome=false;
                        ChatApplication.isSignUp=true;
                        Main2Activity.isResumeConnect=false;

                        webUrl = ipAddressPI;
                        ChatApplication.url = webUrl;
                        ChatApplication.isCallDeviceList=true;
                        Intent intent=new Intent(SignUp.this,Main2Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("error is "+error);
                ChatApplication.showToast(SignUp.this,"Please connect to your gateway's local network and try again.");
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/


        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().Signup(ipAddressPI + ":", edt_first_name.getText().toString(), edt_last_name.getText().toString(), edt_user_name.getText().toString().trim(),
                edt_email_id.getText().toString(), edt_password.getText().toString(), edt_phone_no.getText().toString(), imei
                , token, new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {

                        ActivityHelper.dismissProgressDialog();
                        try {

                            JSONObject result = new JSONObject(stringResponse);

                            int code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {
                                ChatApplication.isMainFragmentNeedResume = true;
                                if (!TextUtils.isEmpty(message)) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }

                                /*/*
                                 *  {"code":200,"message":"Succesfully!
                                 *  Your account has been created",
                                 *  "data":{"first_name":"vipul",
                                 *  "last_name":"patel",
                                 *  "ip":"http:\/\/home.deepfoods.net:11150",
                                 *  "user_id":"1545317988870_UrL_0HY9u","user_password":"c1bc06"}}
                                 * */

                                JSONObject data = result.getJSONObject("data");
                                //   JSONObject object1=data.optJSONObject(data.toString());
                                String first_name = data.optString("first_name");
                                String ip = data.optString("ip");
                                String last_name = data.optString("last_name");
                                String user_id = data.optString("user_id");
                                String user_password = data.optString("user_password");
                                String admin = data.optString("admin");
                                String local_ip = data.optString("local_ip_address");
                                String mac_address = data.getString("mac_address");

                                Common.savePrefValue(SignUp.this, Constants.USER_PASSWORD, user_password);
                                Common.savePrefValue(SignUp.this, Constants.PREF_IP, ip);
                                Common.savePrefValue(SignUp.this, Constants.USER_ID, user_id);

                                Common.savePrefValue(SignUp.this, Constants.USER_ADMIN_TYPE, admin);

                                if (Common.getPrefValue(SignUp.this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                                    Constants.room_type = 0;
                                    Common.savePrefValue(SignUp.this, Constants.USER_ROOM_TYPE, "" + 0);
                                } else {
                                    Constants.room_type = 2;
                                    Common.savePrefValue(SignUp.this, Constants.USER_ROOM_TYPE, "" + 2);
                                }

                                String auth_key = "";  // value of this key will add at login time

                                User user = new User(user_id, first_name, last_name, ip, false, user_password, admin, local_ip, mac_address, auth_key);

                                Gson gson = new Gson();
                                String jsonText = Common.getPrefValue(getApplicationContext(), Common.USER_JSON);
                                List<User> userList = new ArrayList<User>();
                                user.setIsActive(true);
                                userList.add(user);

                                String jsonCurProduct = gson.toJson(userList);
                                Common.savePrefValue(getApplicationContext(), Common.USER_JSON, jsonCurProduct);

                                ChatApplication.logDisplay("response is signup " + data.toString());
                                ChatApplication.isRefreshHome = false;
                                ChatApplication.isSignUp = true;
                                Main2Activity.isResumeConnect = false;

                                webUrl = ipAddressPI;
                                ChatApplication.url = webUrl;
                                ChatApplication.isCallDeviceList = true;
                                Intent intent = new Intent(SignUp.this, Main2Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
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
                        ChatApplication.showToast(SignUp.this, "Please connect to your gateway's local network and try again.");
                        ActivityHelper.dismissProgressDialog();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ChatApplication.logDisplay("error is " + error);
                        ChatApplication.showToast(SignUp.this, "Please connect to your gateway's local network and try again.");
                        ActivityHelper.dismissProgressDialog();
                    }
                });


    }

    private void getUUID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            } else {
                imei = ActivityHelper.getIMEI(this);
            }
        } else {
            imei = ActivityHelper.getIMEI(this);
        }
    }

}