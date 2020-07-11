package com.spike.bot.activity.Sensor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import java.util.ArrayList;

/**
 * Created by Sagar on 21/5/19.
 * Gmail : vipul patel
 */
public class TempActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edIpEnd;
    Spinner spCouldIp;
    Button btnSubmit;
    public ArrayList<String> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        init();
    }

    private void init() {
        if (!TextUtils.isEmpty(Common.getPrefValue(this, "couldIp"))) {
            callIntent();
            return;
        }

        btnSubmit = findViewById(R.id.btnSubmit);
        edIpEnd = findViewById(R.id.edIpEnd);
        spCouldIp = findViewById(R.id.spCouldIp);
        btnSubmit.setOnClickListener(this);

        arrayList.add("http://18.237.74.22:8079");
        arrayList.add("http://34.212.76.50:8079");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCouldIp.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            ChatApplication.logDisplay("spCouldIp is " + spCouldIp.getSelectedItem().toString());
            if (edIpEnd.getText().length() == 0) {
                ChatApplication.showToast(this, "Please enter ip end");
            } else {
                setValue();
            }
        }
    }

    private void setValue() {
        Common.savePrefValue(ChatApplication.getInstance(), "couldIp", spCouldIp.getSelectedItem().toString());
        Common.savePrefValue(ChatApplication.getInstance(), "startIp", edIpEnd.getText().toString());

        callIntent();
    }

    private void callIntent() {
       // Constants.CLOUD_SERVER_URL = Common.getPrefValue(this, "couldIp");
        Constants.IP_END = Common.getPrefValue(this, "startIp");

      //  ChatApplication.logDisplay("CLOUD_SERVER_URL is " + Constants.CLOUD_SERVER_URL);
        ChatApplication.logDisplay("CLOUD_SERVER_URL is IP_END " + Constants.IP_END);

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        this.finish();
    }
}
