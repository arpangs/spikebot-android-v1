package com.spike.bot.activity.TTLock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.PinEntryView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.spike.bot.core.Common.showToast;

public class SetPasscodeActivity extends AppCompatActivity
{
    PinEntryView txtPinEntry;
    Button btnsave;
    Toolbar toolbar;
    String strpasscode = "",device_id = "",enable_lock_unlock_from_app="",device_name="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpasscode);


        device_id = getIntent().getStringExtra("device_id");
        enable_lock_unlock_from_app = getIntent().getStringExtra("enable_lock_unlock_from_app");
        device_name = getIntent().getStringExtra("device_name");
        bindview();
    }

    public void bindview(){
        txtPinEntry = findViewById(R.id.txt_pin_entry);
        btnsave = findViewById(R.id.btn_save);
        toolbar = findViewById(R.id.toolbar);


        ChatApplication.keyBoardShow(SetPasscodeActivity.this,txtPinEntry);
        txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strpasscode = s.toString();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Set passcode");


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (strpasscode.isEmpty()) {
                        ChatApplication.showToast(SetPasscodeActivity.this,"Please enter passcode");
                      //  ChatApplication.closeKeyboard(SetPasscodeActivity.this);

                    } else {
                        updateDoorSensor(strpasscode);
                        ChatApplication.closeKeyboard(SetPasscodeActivity.this);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /*call update sensor */
    private void updateDoorSensor(String strpasscode) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        String webUrl = ChatApplication.url + Constants.SAVE_EDIT_SWITCH;

        JSONObject jsonNotification = new JSONObject();
        try {

            jsonNotification.put("device_id", device_id);
            jsonNotification.put("enable_lock_unlock_from_app",enable_lock_unlock_from_app);
            jsonNotification.put("pass_code",strpasscode);
            jsonNotification.put("device_name", device_name);
            jsonNotification.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonNotification.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonNotification.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is "+webUrl+" "+jsonNotification);
        new GetJsonTask(this, webUrl, "POST", jsonNotification.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("url is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        //    showToast(message);
                    }
                    if (code == 200) {
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
