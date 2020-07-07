package com.spike.bot.api_retrofit;

import android.content.Context;
import android.content.Intent;

import com.spike.bot.ChatApplication;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.core.Common;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class UnauthorisedInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 401) {
            Context context = ChatApplication.getContext();
            context.startService(new Intent(context, DeleteTokenService.class));
            Main2Activity activity = new Main2Activity();
            activity.logoutCloudUser();
            Common.removeAllKey();

            //            Intent intent = new Intent(context, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            context.startActivity(intent);
        }
        return response;
    }
}