package com.spike.bot.activity.SmartDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.spike.bot.R;

/**
 * Created by Sagar on 8/8/19.
 * Gmail : vipul patel
 */
public class AddOrSelectBridgeActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    AppCompatTextView txtSelectBridge;
    String brandId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bridge);

        brandId=getIntent().getStringExtra("brandId");
        setUiId();
    }

    private void setUiId() {
        toolbar=findViewById(R.id.toolbar);
        txtSelectBridge=findViewById(R.id.txtSelectBridge);
        txtSelectBridge.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("Select Bridge");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionAdd:
                Intent intent=new Intent(this, SearchHueBridgeActivity.class);
                intent.putExtra("brandId",""+brandId);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_smart_device, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==txtSelectBridge){
            Intent intent=new Intent(this, PhilipsHueBridgeListActivity.class);
            intent.putExtra("brandId",brandId);
            startActivity(intent);
        }

    }
}
