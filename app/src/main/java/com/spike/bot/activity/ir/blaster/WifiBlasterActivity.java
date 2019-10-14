package com.spike.bot.activity.ir.blaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.WifiModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Sagar on 3/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class WifiBlasterActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public TextView txtWifi;
    public Button btnNext;
    public ArrayList<WifiModel.WiFiList> arrayList = new ArrayList<>();

    public ProgressDialog progressBar;
    public String wifiIP = "", roomId = "", roomName = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_blaster);

        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");

        setUi();
    }

    private void setUi() {

        Constants.isWifiConnect = true;
        progressBar = new ProgressDialog(WifiBlasterActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnNext = (Button) findViewById(R.id.btnNext);
        txtWifi = (TextView) findViewById(R.id.txtWifi);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Wi-Fi");

        String s = "Connect to the Wi-Fi hotspot for the SpikeBot device that you are settings up. It will have <b>SpikeBot_xxxxxxx</b> on the end.";
        txtWifi.setText(Html.fromHtml(s));

        btnNext.setOnClickListener(this);
    }

    public static boolean setMobileDataEnabled(Activity wifiBlasterActivity) {

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) wifiBlasterActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        return mobileDataEnabled;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            new ServiceEventAsync().execute();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class ServiceEventAsync extends AsyncTask {
        private WifiManager.MulticastLock lock;

        public ServiceEventAsync() {
            android.util.Log.i("ServiceEventLog", "constructor call...");
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            lock = wifi.createMulticastLock("lock");
            lock.setReferenceCounted(true);
            lock.acquire();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setMessage("Connecting...");
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String hostname = "";
            try {
                if (lock != null) {
                    if (lock.isHeld()) {
                        lock.release();
                        lock = null;
                    }
                }

                @SuppressLint("WifiManagerLeak") final WifiManager manager = (WifiManager) WifiBlasterActivity.this.getSystemService(WIFI_SERVICE);
                final DhcpInfo dhcp = manager.getDhcpInfo();
                hostname = Formatter.formatIpAddress(dhcp.gateway);

                android.util.Log.d("System out", "ip address is " + hostname);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return hostname;
        }

        @Override
        protected void onPostExecute(Object o) {
            wifiIP = o.toString();
            android.util.Log.i("ServiceEventLog", "onPostExecute...");
            progressBar.dismiss();

            if (wifiIP.equalsIgnoreCase("0.0.0.0")) {
                Toast.makeText(getApplicationContext(), "Please connect to wifi", Toast.LENGTH_SHORT).show();
            } else {
                //wifiIP="192.168.4.1";
                getWifiList(wifiIP);

            }

            super.onPostExecute(o);
        }
    }

    /*getting wifi list */

    public void getWifiList(final String wifiIP) {
        if (!ActivityHelper.isConnectingToInternet(WifiBlasterActivity.this)) {
            Toast.makeText(WifiBlasterActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(WifiBlasterActivity.this, "Please wait... ", false);
        String url = "http://" + wifiIP + "/wifi?";
        new GetJsonTask(WifiBlasterActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    if (result != null) {

                        JSONObject object = result;
                        JSONArray jsonArray = object.optJSONArray("WiFi_List");

                        arrayList = (ArrayList<WifiModel.WiFiList>) Common.fromJson(jsonArray.toString(), new TypeToken<ArrayList<WifiModel.WiFiList>>() {
                        }.getType());

                        if (arrayList.size() > 0) {
                            setNextIntent();
                        } else {
                            Toast.makeText(WifiBlasterActivity.this, "Please check your wifi connection", Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(WifiBlasterActivity.this.getApplicationContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setNextIntent() {
        Constants.activityWifi = WifiBlasterActivity.this;

        Intent intent = new Intent(WifiBlasterActivity.this, WifiListActivity.class);
        intent.putExtra("arrayList", arrayList);
        intent.putExtra("wifiIP", wifiIP);
        intent.putExtra("roomId", roomId);
        intent.putExtra("roomName", roomName);
        startActivity(intent);
    }

    private void showConfigAlert(String alertMessage) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiBlasterActivity.this.finish();
            }
        });
        builder.create().show();
    }


    @Override
    public void onBackPressed() {
        if (Constants.isWifiConnect) {
            // switch network
            showConfigAlert("Please change your wifi connection after move to another screen.");
        } else {
            super.onBackPressed();
        }

    }
}
