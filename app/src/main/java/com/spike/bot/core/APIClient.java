package com.spike.bot.core;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sagar on 20/6/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class APIClient {

    private static Retrofit retrofit = null;

    public static APIService getApiClient(){

        if(retrofit == null){

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.CLOUD_SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APIService.class);
    }

    public APIService getInstance(){
        return retrofit.create(APIService.class);
    }
}
