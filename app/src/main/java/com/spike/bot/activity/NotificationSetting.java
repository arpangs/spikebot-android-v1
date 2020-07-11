package com.spike.bot.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.NotificationSettingAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.NotificationListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sagar on 3/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class NotificationSetting extends AppCompatActivity implements NotificationSettingAdapter.SwitchChanges {

    private NotificationListRes mNotificationListRes;
    private List<NotificationListRes.Data> notificationDataList;

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

        mListNotification = (RecyclerView) findViewById(R.id.mListNotification);
        mListNotification.setLayoutManager(new GridLayoutManager(this, 1));


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
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the notification list
     */
    private void saveNotiSettingList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

//        JSONObject dataObject = new JSONObject();

            //changes in
            //URL: getNotificationList, URL: saveNotificationList
            //Added multisensor and gas enable/disable option.

            JsonArray dataArray = new JsonArray();
            for (NotificationListRes.Data data : notificationDataList) {
                JsonObject homeObject = new JsonObject();
                homeObject.addProperty("id", data.getId());
                homeObject.addProperty("title", data.getTitle());
                homeObject.addProperty("value", data.getValue());


                dataArray.add(homeObject);
            }

//            dataObject.put("data",dataArray);
//            dataObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));




       /* String webUrl = ChatApplication.url + Constants.SAVE_NOTIFICATION_LIST;
        ChatApplication.logDisplay("notification result is "+ webUrl + dataObject);*/
            ActivityHelper.showProgressDialog(this, "Please wait.", false);

       /* new GetJsonTask(this, webUrl, "POST", dataObject.toString(), new ICallBack() {
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
        }).execute();*/
            if (ChatApplication.url.contains("http://"))
                ChatApplication.url = ChatApplication.url.replace("http://", "");

            SpikeBotApi.getInstance().SaveNotiSettingList(dataArray, new DataResponseListener() {
                @Override
                public void onData_SuccessfulResponse(String stringResponse) {
                    ActivityHelper.dismissProgressDialog();
                    try {

                        JSONObject result = new JSONObject(stringResponse);
                        int code = result.getInt("code");
                        String message = result.getString("message");
                        if (code == 200) {
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onData_FailureResponse() {
                    ActivityHelper.dismissProgressDialog();
                }

                @Override
                public void onData_FailureResponse_with_Message(String error) {
                    ActivityHelper.dismissProgressDialog();
                }
            });



    }

    /**
     * Get All notification list
     */
    private void getNotificationList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
       /* String webUrl = ChatApplication.url + Constants.GET_NOTIFICATION_LIST;

        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            dataObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            dataObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("result is " + webUrl + dataObject);

        new GetJsonTask(this, webUrl, "POST", dataObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("result is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        mNotificationListRes = Common.jsonToPojo(result.toString(), NotificationListRes.class);
                        notificationDataList = mNotificationListRes.getData();
                        updateAdapter(notificationDataList);

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetNotificationList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("result is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        mNotificationListRes = Common.jsonToPojo(result.toString(), NotificationListRes.class);
                        notificationDataList = mNotificationListRes.getData();
                        updateAdapter(notificationDataList);

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateAdapter(List<NotificationListRes.Data> data) {

        notificationSettingAdapter = new NotificationSettingAdapter(data, this);
        mListNotification.setAdapter(notificationSettingAdapter);
    }

    @Override
    public void onCheckedChanged(NotificationListRes.Data data, int position) {


    }
}
