package com.spike.bot.api_retrofit;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.activity.LoginSplashActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*dev arp add this class on 23 june 2020*/

class GeneralRetrofit {

    private final Call<JsonElement> call;
    private final Object params;
    private DataResponseListener dataResponseListener;
    private Callback<JsonElement> postCall = new Callback<JsonElement>() {
        @Override
        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
            Log.w("Dev_Arp_", "--->   " + " Status code : " + response.code());
            if (response.code() != 200) {
                Log.w("Dev_Arp_", "--->   " + " Response NOT OK : " + response.raw().message());
            }
            if (response.body() != null) {
                Log.w("Dev_Arp_", "--->   " + " RESPONSE = " + String.valueOf(response.body()));

                String responseString = String.valueOf(response.body());


                if (dataResponseListener != null)
                    dataResponseListener.onData_SuccessfulResponse(responseString);

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.has("code")) {
                        if (jsonObject.getString("code").equalsIgnoreCase("401")) {  /*dev arpan add 06 july 2020 for web developer provide status code in response string with 200 status code so manage like this*/
                            Gson gson = new Gson();
                            String jsonText = Common.getPrefValue(ChatApplication.getContext(), Common.USER_JSON);
                            String USER_ID = Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID);

                            List<User> uList = new ArrayList<User>();

                            if (!TextUtils.isEmpty(jsonText)) {
                                Type type = new TypeToken<List<User>>() {
                                }.getType();
                                uList = gson.fromJson(jsonText, type);

                                for (User user : uList) {
                                    if (user.getUser_id().equals(USER_ID)) {
                                        uList.remove(user);
                                        break;
                                    }
                                }
                            }

                            if (uList != null && uList.size() == 0) {
                                Intent intent = new Intent(ChatApplication.getContext(), LoginSplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ChatApplication.getContext().startActivity(intent);
                            } else {
                                Common.savePrefValue(ChatApplication.getContext(), Constants.PREF_CLOUDLOGIN, "true");
                                Common.savePrefValue(ChatApplication.getContext(), Constants.PREF_IP, uList.get(0).getCloudIP());
                                Common.savePrefValue(ChatApplication.getContext(), Constants.USER_ID, uList.get(0).getUser_id());
                                Common.savePrefValue(ChatApplication.getContext(), Constants.USER_PASSWORD, uList.get(0).getPassword());
                                Common.savePrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE, uList.get(0).getAdmin());

                                Common.savePrefValue(ChatApplication.getContext(), Constants.AUTHORIZATION_TOKEN, uList.get(0).getAuth_key());

                                if (Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
                                    Constants.room_type = 0;
                                    Common.savePrefValue(ChatApplication.getContext(), Constants.USER_ROOM_TYPE, "" + 0);
                                } else {
                                    Constants.room_type = 2;
                                    Common.savePrefValue(ChatApplication.getContext(), Constants.USER_ROOM_TYPE, "" + 2);
                                }

                                ChatApplication.url = uList.get(0).getLocal_ip();

                                uList.get(0).setActive(true);

                                String jsonCurProduct = gson.toJson(uList);
                                Common.savePrefValue(ChatApplication.getContext(), Common.USER_JSON, jsonCurProduct);

                                Intent intent = new Intent(ChatApplication.getContext(), Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ChatApplication.getContext().startActivity(intent);
                            }
                        } else if (!jsonObject.getString("code").equalsIgnoreCase("200")) {
                            if (jsonObject.has("message")) {
                                Toast.makeText(ChatApplication.getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (dataResponseListener != null)
                    dataResponseListener.onData_FailureResponse();
            }

        }

        @Override
        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
            Log.w("Dev_Arp_", "--->   " + " ON_Failure = " + t.toString());
            Log.w("Dev_Arp_", "--->   " + " ON_Failure Localize Message = " + t.getLocalizedMessage());
            Log.w("Dev_Arp_", "--->   " + " ON_Failure Message = " + t.getMessage());
            if (!call.isCanceled())
                if (dataResponseListener != null)
                    dataResponseListener.onData_FailureResponse();

            dataResponseListener.onData_FailureResponse_with_Message(t.getMessage());
        }
    };

    GeneralRetrofit(Call<JsonElement> call, Object params, DataResponseListener dataResponseListener) {
        this.call = call;
        this.params = params;
        this.dataResponseListener = dataResponseListener;
    }

    Call<JsonElement> call() {
        Log.w("Dev_Arp_", "--->   " + " API URL = " + call.request().url());
        if (params != null && call.request().body() != null) {
            Log.w("Dev_Arp_", "--->   " + " Passing Params = " + new Gson().toJson(params));
        }
        call.enqueue(postCall);
        return call;
    }


}
