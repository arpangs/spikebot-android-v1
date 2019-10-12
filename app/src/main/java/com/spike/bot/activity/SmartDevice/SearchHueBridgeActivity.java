package com.spike.bot.activity.SmartDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.SmartBrandModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 1/8/19.
 * Gmail : vipul patel
 */
public class SearchHueBridgeActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public boolean isBridgeAdd = false;
    public RecyclerView recyclerSmartDevice;
    public AppCompatButton btnNext;
    public ArrayList<SmartBrandModel> arrayList = new ArrayList<>();

    String brandId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_philips_bridge);

        brandId = getIntent().getStringExtra("brandId");

        setId();
    }

    private void setId() {
        Constants.activitySearchHueBridgeActivity = this;
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            nextIntentCall();
        }
    }

    private void nextIntentCall() {
        Intent intent = new Intent(this, SearchSubHueBridgeActivity.class);
        intent.putExtra("brandId", brandId);
        startActivity(intent);
    }
}
