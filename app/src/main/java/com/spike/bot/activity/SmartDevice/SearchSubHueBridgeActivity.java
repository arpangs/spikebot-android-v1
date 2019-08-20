package com.spike.bot.activity.SmartDevice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 1/8/19.
 * Gmail : vipul patel
 */
public class SearchSubHueBridgeActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public RecyclerView recyclerSmartDevice;
    public AppCompatButton btnNext;
    public ArrayList<SmartBrandModel> arrayList = new ArrayList<>();
    public String brandId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_philips_bridge);

        brandId = getIntent().getStringExtra("brandId");
        setId();
    }

    private void setId() {
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("Search Bridge");

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            callsearchBridges();
        }
    }

    public void callsearchBridges() {
        if (!ActivityHelper.isConnectingToInternet(SearchSubHueBridgeActivity.this)) {
            Toast.makeText(SearchSubHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SearchSubHueBridgeActivity.this, "Please wait... ", false);
        String url = ChatApplication.url + Constants.searchBridges;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(SearchSubHueBridgeActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject object = result.optJSONObject("data");
                        //{"host_ip":"192.168.175.74","bridge_id":"ZAYPATBX7vD01ifFHV9sfBNHpLb5hJn2hvSpazdm","smart_device":"Philips Hue"}
                        ChatApplication.logDisplay("bridge is " + object);

                        showNameDialog(object);

                    } else {
                        ChatApplication.showToast(SearchSubHueBridgeActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void showNameDialog(final JSONObject object) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edtName = (TextInputEditText) dialog.findViewById(R.id.edtName);
        final TextInputLayout inputName = dialog.findViewById(R.id.inputName);

        room_name.setVisibility(View.GONE);
        inputName.setVisibility(View.VISIBLE);
        edtName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edtName.setFilters(filterArray);

        inputName.setHint("Enter name");
//        edtName.setHint("Enter name");
//        room_name.setHint("Enter name");
        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText("Add Bridge Name");
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().toString().length() > 0) {

                    View view = dialog.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    dialog.dismiss();
                    callAddbridge(object, edtName.getText().toString(), edtName);
                } else {
                    ChatApplication.showToast(SearchSubHueBridgeActivity.this, "Please enter name");
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    public void callAddbridge(final JSONObject object, final String s, final TextInputEditText edtName) {
        if (!ActivityHelper.isConnectingToInternet(SearchSubHueBridgeActivity.this)) {
            Toast.makeText(SearchSubHueBridgeActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(SearchSubHueBridgeActivity.this, "Please wait... ", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("host_ip", "" + object.optString("host_ip"));
            jsonObject.put("bridge_id", "" + object.optString("bridge_id"));
            jsonObject.put("smart_device", "" + object.optString("smart_device"));
            jsonObject.put("unique_id", "" + object.optString("unique_id"));
            jsonObject.put("bridge_name", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.addHueBridge;
        ChatApplication.logDisplay("smart is " + url);
        new GetJsonTask(SearchSubHueBridgeActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(SearchSubHueBridgeActivity.this, "" + message);
                    if (code == 200) {
                        Constants.activitySearchHueBridgeActivity.finish();
                        Intent intent = new Intent(SearchSubHueBridgeActivity.this, PhilipsHueBridgeListActivity.class);
                        intent.putExtra("brandId", "" + brandId);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

}
