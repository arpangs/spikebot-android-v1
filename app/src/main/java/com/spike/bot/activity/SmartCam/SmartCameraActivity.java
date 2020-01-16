package com.spike.bot.activity.SmartCam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
import com.spike.bot.model.JetSonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vipul on 13/1/20.
 * Gmail : vipul patel
 */
public class SmartCameraActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    TextView txtNodataFound;
    public FloatingActionButton floatingActionButton;
    public RecyclerView recyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_remote_activity);

        setViewId();
    }

    private void setViewId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Add Smart Camera");
        recyclerview = findViewById(R.id.recyclerRemoteList);
        floatingActionButton = findViewById(R.id.fab);
        txtNodataFound = findViewById(R.id.txtNodataFound);
        floatingActionButton.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);

        callGetSmarCamera();
    }
    /*view hide use for getting error*/
    public void showView(boolean isflag){
        recyclerview.setVisibility(isflag ? View.VISIBLE : View.GONE);
        txtNodataFound.setVisibility(isflag ? View.GONE : View.VISIBLE);
    }

    /*call smart camera */
    private void callGetSmarCamera() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            obj.put("device_type", "jetson");
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.devicefind;

        ChatApplication.logDisplay("door sensor" + url + obj);

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");
                    ChatApplication.showToast(SmartCameraActivity.this, message);
                    if (code == 200) {
                        showView(true);
                        JSONObject  object= new JSONObject(String.valueOf(result));
                        JSONArray jsonArray= object.optJSONArray("data");

                        Gson gson=new Gson();
//                        arrayList.clear();
//                        arrayList = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<JetSonModel.Datum>>(){}.getType());
//                        if(arrayList.size()>0){
//                            setAdapter();
//                        }else {
//                            showView(false);
//                        }

                        ChatApplication.logDisplay("response is "+result);
                    }else {
                        showView(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                showView(false);
                ActivityHelper.dismissProgressDialog();

            }
        }).execute();
    }

    @Override
    public void onClick(View v) {
        if(v==v){

        }
    }
}
