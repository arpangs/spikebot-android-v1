package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.NotificationSettingAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.NotificationListRes;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sagar on 3/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class NotificationSetting extends AppCompatActivity implements NotificationSettingAdapter.SwitchChanges{

    private NotificationListRes mNotificationListRes;
    private List<NotificationListRes.Data> notificationDataList;

    //private SwitchCompat sHome,sTemp,sDoor;
    private RecyclerView mListNotification;
    private NotificationSettingAdapter notificationSettingAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrences_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notification Setting");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

       /* sHome = (SwitchCompat) findViewById(R.id.switch_home);
        sTemp = (SwitchCompat) findViewById(R.id.switch_temp);
        sDoor = (SwitchCompat) findViewById(R.id.switch_door);*/

        mListNotification = (RecyclerView)findViewById(R.id.mListNotification);
        mListNotification.setLayoutManager(new GridLayoutManager(this,1));



        getNotificationList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveNotiSettingList();
            return true;
        }else if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNotiSettingList(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject dataObject = new JSONObject();

        try {

            //changes in
            //URL: getNotificationList, URL: saveNotificationList
            //Added multisensor and gas enable/disable option.

            JSONArray dataArray = new JSONArray();
            for(NotificationListRes.Data data : notificationDataList){
                JSONObject homeObject = new JSONObject();
                homeObject.put("id",data.getId());
                homeObject.put("title",data.getTitle());
                homeObject.put("value",data.getValue());


                dataArray.put(homeObject);
            }

            dataObject.put("data",dataArray);
            dataObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));



        } catch (JSONException e) {
            e.printStackTrace();
        }
        String webUrl = ChatApplication.url + Constants.SAVE_NOTIFICATION_LIST;
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        new GetJsonTask(this, webUrl, "POST", dataObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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


    private void getNotificationList(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String webUrl = ChatApplication.url + Constants.GET_NOTIFICATION_LIST;

        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        new GetJsonTask(this, webUrl, "POST", dataObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){
                        mNotificationListRes = Common.jsonToPojo(result.toString(),NotificationListRes.class);
                        notificationDataList = mNotificationListRes.getData();
                        updateAdapter(notificationDataList);

                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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

    private void updateAdapter(List<NotificationListRes.Data> data){

        notificationSettingAdapter = new NotificationSettingAdapter(data,this);
        mListNotification.setAdapter(notificationSettingAdapter);
    }

    @Override
    public void onCheckedChanged(NotificationListRes.Data data, int position) {


    }
}
