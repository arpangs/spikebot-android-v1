package com.spike.bot.api_retrofit;


import android.util.Log;

import androidx.annotation.NonNull;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    /*dev arp add this abstract class on 23 june 2020*/

    private static final int TIME = 60 * 15; // 90O milli second
    private static final String TAG = RestClient.class.getSimpleName();
    private static Retrofit retrofit;
    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static RestClient clientInstance;
    //TODO is you use HTTPS then use follow code in place of httpClient
    OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(TIME, TimeUnit.SECONDS)
            .readTimeout(TIME, TimeUnit.SECONDS)
            .writeTimeout(TIME, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(new UnauthorisedInterceptor()) // if user is not authorised or the token has been changed
            .addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    String Auth_Token = Common.getPrefValue(ChatApplication.getContext(), Constants.AUTHORIZATION_TOKEN);
                    if (Auth_Token != null && Auth_Token.length() > 0) {
                        requestBuilder.addHeader("Authorization", "Bearer " + Auth_Token);
                        Log.d(TAG + " OkHttp Header -- ", "AuthorizedToken : " + "Bearer " + Auth_Token);
                    }
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            })
            .build();

    //---------- another solution ----------------
    private ApiInterface apiInterface;


    private RestClient() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://vpn.spikebot.io");
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient);  // dev arp   // for http
        Retrofit mretrofit = builder.build();


        apiInterface = mretrofit.create(ApiInterface.class);
    }

    public static RestClient getInstance() {
        if (clientInstance == null) {
            clientInstance = new RestClient();
        }
        return clientInstance;
    }


    public ApiInterface getApiInterface() {
        return apiInterface;
    }


    /* dev arpan add below code for multiple base url*/

}


