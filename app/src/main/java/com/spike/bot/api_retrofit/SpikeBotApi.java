package com.spike.bot.api_retrofit;


import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import java.util.HashMap;

public class SpikeBotApi {


    /*dev arp add this class on 23 june 2020*/

    private static SpikeBotApi SpikeBotApi = null;

    private static ApiInterface apiService = RestClient.getInstance().getApiInterface();

    public static SpikeBotApi getInstance() {
        if (SpikeBotApi == null)
            return new SpikeBotApi();
        return SpikeBotApi;
    }

    public void loginUser(String userName, String password,String imei, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_name", userName);
        params.put("user_password", password);
        params.put("device_id", imei);
        params.put("device_type", "android");
        params.put("device_push_token", Common.getPrefValue(ChatApplication.getContext(),Constants.DEVICE_PUSH_TOKEN));
        params.put("uuid", ChatApplication.getUuid());
        params.put("fcm_token", "Android_test");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.postWebserviceCall( Constants.APP_LOGIN, params), params, dataResponseListener).call();

    }
}