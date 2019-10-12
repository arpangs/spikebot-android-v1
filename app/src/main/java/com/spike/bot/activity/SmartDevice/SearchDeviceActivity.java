package com.spike.bot.activity.SmartDevice;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.SmartBrandDeviceModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 12/8/19.
 * Gmail : vipul patel
 */
public class SearchDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public ImageView imgBridge;
    public TextView txtTitleBridge, txtInfo;
    public Button btnNext;
    public int isFlag = 0;

    public String host_ip = "", getBridge_name = "", bridge_id = "";

    public ArrayList<SmartBrandDeviceModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_philips_bridge);

        arrayList = (ArrayList<SmartBrandDeviceModel>) getIntent().getSerializableExtra("searchModel");
        host_ip = getIntent().getStringExtra("host_ip");
        getBridge_name = getIntent().getStringExtra("getBridge_name");
        bridge_id = getIntent().getStringExtra("bridge_id");

        setId();
    }

    private void setId() {
        toolbar = findViewById(R.id.toolbar);
        imgBridge = findViewById(R.id.imgBridge);
        txtInfo = findViewById(R.id.txtInfo);
        txtTitleBridge = findViewById(R.id.txtTitleBridge);
        btnNext = findViewById(R.id.btnNext);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnNext.setOnClickListener(this);

        setView(getResources().getDrawable(R.drawable.bulb_image_on), getResources().getString(R.string.bulb_title), getResources().getString(R.string.bulb_info));
    }

    public void setView(Drawable drawable, String title, String message) {
        imgBridge.setImageDrawable(drawable);

        txtTitleBridge.setText(title);
        txtInfo.setText(message);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (isFlag == 0) {
                isFlag = 1;
                setView(getResources().getDrawable(R.drawable.device_bridge_on_image), getResources().getString(R.string.bulb_title1), getResources().getString(R.string.bulb_info1));
            } else {
                Intent intent = new Intent(this, PhilipsBulbNewListActivity.class);
                intent.putExtra("searchModel", arrayList);
                intent.putExtra("bridge_id", bridge_id);
                intent.putExtra("getBridge_name", getBridge_name);
                intent.putExtra("host_ip", host_ip);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (isFlag == 1) {
            isFlag = 0;
            setView(getResources().getDrawable(R.drawable.bulb_image_on), getResources().getString(R.string.bulb_title), getResources().getString(R.string.bulb_info));
        } else {
            super.onBackPressed();
        }

    }
}
