package com.spike.bot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.receiver.ConnectivityReceiver;

public class SplashActivity extends Activity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Constants.checkLoginAccountCount(SplashActivity.this)) {

                    ChatApplication.currentuserId = Constants.getuser(SplashActivity.this).getUser_id();
                    startHomeIntent();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginSplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);


    }

    /* start home screen */
    private void startHomeIntent() {
        ConnectivityReceiver.counter = 0;
        Intent intent = new Intent(this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ChatApplication.CurrnetFragment = R.id.navigationDashboard; // dev arpan add 15 june 2020
        startActivity(intent);
        this.finish();

    }

}
