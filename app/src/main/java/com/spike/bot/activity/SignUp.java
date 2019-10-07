package com.spike.bot.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.fragments.MainFragment;
import com.spike.bot.model.PiDetailsModel;
import com.spike.bot.model.User;

import org.json.JSONArray;
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

    private Button btn_signup,btn_sign_up_cancel;
    private EditText edt_first_name,edt_last_name,edt_email_id,edt_user_name,edt_password,edt_con_password,edt_phone_no;

    String webUrl = "";
    private Socket mSocket;
    private  List<User> tempList;

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

        btn_signup = (Button) findViewById(R.id.btn_sign_up);
        btn_sign_up_cancel = (Button) findViewById(R.id.btn_sign_up_cancel);

        edt_first_name = (EditText) findViewById(R.id.edt_first_name_su);
        edt_last_name = (EditText) findViewById(R.id.edt_last_name_su);
        edt_email_id = (EditText) findViewById(R.id.edt_emailid_su);
        edt_user_name = (EditText) findViewById(R.id.edt_user_name_su);
        edt_password = (EditText) findViewById(R.id.edt_password_su);
        edt_con_password = (EditText) findViewById(R.id.edt_con_password_su);
        edt_phone_no = (EditText) findViewById(R.id.edt_phone_no_su);

        Gson gson = new Gson();
        String jsonText = Common.getPrefValue(getApplicationContext(),Common.USER_JSON);
        tempList = new ArrayList<User>();


        if(!TextUtils.isEmpty(jsonText)){
            Type type = new TypeToken<List<User>>() {}.getType();
            tempList = gson.fromJson(jsonText, type);
        }
        if(tempList.size() > 0){
            btn_sign_up_cancel.setVisibility(View.VISIBLE);
        }else{
            btn_sign_up_cancel.setVisibility(View.INVISIBLE);
        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToMobile(SignUp.this)){
                    ChatApplication.showToast(SignUp.this,"Please connect to your gateway's local network and try again.");
                }else {
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

    /**
     *
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

    String imei = "";
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    private void singUP(){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        InetAddress addr = Main2Activity.getLocalIpAddress(SignUp.this);
        String hostname = addr.toString().replace("/", "");

        String[] array = hostname.split("\\.");

//        String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + Constants.IP_END;
        String ipAddressPI = array[0] + "." + array[1] + "." + array[2] + "." + array[3];


        if(TextUtils.isEmpty(edt_first_name.getText().toString())){
            edt_first_name.requestFocus();
            edt_first_name.setError("Enter First Name");
            //Toast.makeText(getApplicationContext(),"Enter First Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_last_name.getText().toString())){
            edt_last_name.requestFocus();
            edt_last_name.setError("Enter Last Name");
            //Toast.makeText(getApplicationContext(),"Enter Last Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_email_id.getText().toString())){
            edt_email_id.requestFocus();
            edt_email_id.setError("Enter Emaid Id");
            //Toast.makeText(getApplicationContext(),"Enter Email Id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_user_name.getText().toString())){
            edt_user_name.requestFocus();
            edt_user_name.setError("Enter User Name");
            //Toast.makeText(getApplicationContext(),"Enter User Name",Toast.LENGTH_SHORT).show();
            return;
        }

        if (edt_user_name.getText().toString().trim().matches(".*([ \t]).*")){
            edt_user_name.requestFocus();
            edt_user_name.setError("Invalid Username");
            //Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(edt_password.getText().toString())){
            edt_password.requestFocus();
            edt_password.setError("Enter Password");
            //Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(edt_phone_no.getText().toString())){
            edt_phone_no.requestFocus();
            edt_phone_no.setError("Enter Phone No");
            //Toast.makeText(getApplicationContext(),"Enter Phone No",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidMobile(edt_phone_no.getText().toString())) {
            edt_phone_no.requestFocus();
            edt_phone_no.setError("Invalid Phone no");
            //Toast.makeText(getApplicationContext(), "Invalid Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(imei)){
            getUUID();
        }


        if(!edt_password.getText().toString().trim().equalsIgnoreCase(edt_con_password.getText().toString().trim())){
            edt_con_password.requestFocus();
            edt_con_password.setError("Password not match");
            //Toast.makeText(getApplicationContext(),"Password not match",Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SignUp.this,"Please wait...",false);
        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
//        webUrl = app.url;
//        String url = "http://" + ipAddressPI + ":"  + Constants.SIGNUP_API;
//        String url = "http://" + "192.168.75.111" + ":"  + Constants.SIGNUP_API;//111
        String url = "http://" + "192.168.175.117" + ":"  + Constants.SIGNUP_API;//117
        String token = FirebaseInstanceId.getInstance().getToken();
        Common.savePrefValue(getApplicationContext(),Constants.DEVICE_PUSH_TOKEN,token);


        JSONObject object = new JSONObject();
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

                        /*/*
                         *  {"code":200,"message":"Succesfully!
                         *  Your account has been created",
                         *  "data":{"first_name":"vipul",
                         *  "last_name":"patel",
                         *  "ip":"http:\/\/home.deepfoods.net:11150",
                         *  "user_id":"1545317988870_UrL_0HY9u","user_password":"c1bc06"}}
                         * */

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
//                        Intent resultIntent = new Intent();
//                        setResult(Activity.RESULT_OK, resultIntent);
//                        finish();
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
        }).execute();

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
        }else {
            imei = ActivityHelper.getIMEI(this);
        }
    }

}