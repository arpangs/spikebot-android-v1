package com.spike.bot.api_retrofit;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*dev arp add this class on 23 june 2020*/

class GeneralRetrofit {

    private final Call<JsonElement> call;
    private final Object params;
    private DataResponseListener dataResponseListener;

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



}