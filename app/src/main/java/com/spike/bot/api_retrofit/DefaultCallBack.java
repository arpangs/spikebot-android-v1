package com.spike.bot.api_retrofit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*dev arp add this abstract class on 23 june 2020*/

public abstract class DefaultCallBack<T> implements Callback<T> {
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        Log.w("Dev_Arp_", "--->   " + "Status code : " + response.code());
        if (response.code() != 200) {
            Log.w("Dev_Arp_", " --->   " + " Response NOT OK : " + response.raw().message());
        }
        if (response.body() != null) {
            Log.w("Dev_Arp_", "--->   " + " RESPONSE = " + String.valueOf(response.body()));
            onSuccess(response.body(), response.code());
        } else {
            onError(response.errorBody(), response.code());
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.w("Dev_Arp_", "--->   " + " ON_Failure = " + t.toString());
        Log.w("Dev_Arp_", "--->   " + " ON_Failure Localize Message = " + t.getLocalizedMessage());
        Log.w("Dev_Arp_", "--->   " + " ON_Failure Message = " + t.getMessage());
        if (!call.isCanceled())
            onError(null, -1);
    }

    public abstract void onSuccess(final T response, int code);

    public abstract void onError(@Nullable ResponseBody body, int code);
}
