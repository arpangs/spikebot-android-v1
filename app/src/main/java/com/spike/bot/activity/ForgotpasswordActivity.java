package com.spike.bot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;

public class ForgotpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_forgotpassword_SKIP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        img_forgotpassword_SKIP = findViewById(R.id.img_forgotpassword_SKIP);

        img_forgotpassword_SKIP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == img_forgotpassword_SKIP) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
