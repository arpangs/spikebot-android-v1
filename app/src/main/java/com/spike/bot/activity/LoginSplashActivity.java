package com.spike.bot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.spike.bot.R;

public class LoginSplashActivity extends Activity implements View.OnClickListener {
    public Button btn_login_splash, btn_signup_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_splash);

        setUi();
    }

    private void setUi() {
        btn_login_splash = findViewById(R.id.btn_login_splash);
        btn_signup_splash = findViewById(R.id.btn_signup_splash);

        btn_login_splash.setOnClickListener(this);
        btn_signup_splash.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btn_login_splash) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("isFlag","true");
            startActivity(intent);

        } else if (v == btn_signup_splash) {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
