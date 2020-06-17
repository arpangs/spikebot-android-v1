package com.spike.bot.Beacon;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BeaconDetailActivity extends AppCompatActivity
{
    RecyclerView recycler_room;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail);

        setViewId();
    }

    public void setViewId() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recycler_room = findViewById(R.id.recycler_room);

    }

    /* room list getting  */
    public void getDeviceList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(this, getResources().getString(R.string.disconnect));
            return;
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", "room");
            jsonObject.put("only_on_off_device", 1);
            jsonObject.put("only_rooms_of_beacon_scanner", 1);
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("get beacon room is " + url + "     " + jsonObject.toString());

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ChatApplication.logDisplay("mood list is room " + result);
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    if (roomArray != null && roomArray.length() > 0) {
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                //  ChatApplication.showToast(BeaconConfigActivity.this, getResources().getString(R.string.something_wrong1));
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }



}
