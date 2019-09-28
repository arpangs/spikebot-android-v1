package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;

/**
 * Created by Sagar on 24/9/19.
 * Gmail : vipul patel
 */
public class DemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        ChatApplication.logDisplay("call is onresume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        ChatApplication.logDisplay("call is onstart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        ChatApplication.logDisplay("call is onpause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        ChatApplication.logDisplay("call is onstop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        ChatApplication.logDisplay("call is rester");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        ChatApplication.logDisplay("call is ondestory");
        super.onDestroy();
    }
}
