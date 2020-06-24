package com.spike.bot.Beacon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.WifiListActivity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.WifiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.spike.bot.core.Common.showToast;

public class WifiScannerActivity extends AppCompatActivity implements View.OnClickListener
{
    public Toolbar toolbar;
    public TextView txtWifi;
    public Button btnNext;
    public ArrayList<WifiModel.WiFiList> arrayList = new ArrayList<>();
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray=new ArrayList<>();

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
        getRoomList();
        Constants.isWifiConnect = true;
        progressBar = new ProgressDialog(WifiScannerActivity.this);
        toolbar =  findViewById(R.id.toolbar);
        btnNext =  findViewById(R.id.btnNext);
        txtWifi =  findViewById(R.id.txtWifi);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Steps to configure");

        String s = "Connect to the Wi-Fi hotspot for the SpikeBot device that you are settings up. It will have <b>SpikeBot_xxxxxxx</b> on the end.";
        txtWifi.setText(Html.fromHtml(s));
        btnNext.setOnClickListener(this);

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

    /* get connect wifi ip address*/
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

                @SuppressLint("WifiManagerLeak") final WifiManager manager = (WifiManager) WifiScannerActivity.this.getSystemService(WIFI_SERVICE);
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
                wifiIP="192.168.4.1";
                getWifiList(wifiIP);

            }

            super.onPostExecute(o);
        }
    }
    /*get room list*/
    private void getRoomList() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", "room");
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.roomslist;

        ChatApplication.logDisplay("un assign is "+url+" "+jsonObject);

        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("un assign is "+result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {}.getType();
                        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optString("data").toString(), type);

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /*getting wifi list */

    public void getWifiList(final String wifiIP) {
        if (!ActivityHelper.isConnectingToInternet(WifiScannerActivity.this)) {
            Toast.makeText(WifiScannerActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        //http://10.0.2.2/wifi?

        ActivityHelper.showProgressDialog(WifiScannerActivity.this, "Please wait... ", false);
        String url = "http://" + wifiIP + "/getssid?";

        ChatApplication.logDisplay("WIFI LIST" + " " + url);
        new GetJsonTask(WifiScannerActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    if (result != null)
                    {

                        JSONObject object = result;

                        JSONArray jsonArray = object.optJSONArray("WiFi_List");

                        arrayList = (ArrayList<WifiModel.WiFiList>) Common.fromJson(jsonArray.toString(), new TypeToken<ArrayList<WifiModel.WiFiList>>() {
                        }.getType());

                        if (arrayList.size() > 0) {
                            setNextIntent();
                        } else {
                            Toast.makeText(WifiScannerActivity.this, "Please check your wifi connection", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WifiScannerActivity.this.getApplicationContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /*send next activity*/
    private void setNextIntent() {
        Constants.activityWifi = WifiScannerActivity.this;

        Intent intent = new Intent(WifiScannerActivity.this, ScannerWifiListActivity.class);
        intent.putExtra("arrayList", arrayList);
        intent.putExtra("wifiIP", wifiIP);
        intent.putExtra("roomListArray", roomListArray);
        startActivity(intent);
    }

    private void showConfigAlert(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiScannerActivity.this.finish();
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
