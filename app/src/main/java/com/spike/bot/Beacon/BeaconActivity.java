package com.spike.bot.Beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.googlecode.mp4parser.authoring.Edit;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteConfigActivity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.BeaconDiffCallBack;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;


public class BeaconActivity extends AppCompatActivity implements View.OnClickListener, BeaconListAdapter.BeaconListClickEvent {

    private BluetoothLeScannerCompat scanner;

    public ProgressDialog progressDialog;
    public int count = 0;

    List<LeDeviceItem> itemList = new ArrayList<>();
    List<LeDeviceItem> itemListTemp = new ArrayList<>();
    List<ScanResult> scanresult = new ArrayList<>();
    BeaconListAdapter beaconListAdapter;
    public RecyclerView recycler;
    public Button btnClick;
    BluetoothAdapter bAdapter;
    String mScannername, mRoomname, mRoomid, mSensorid, mScannerdeviceid, mdevicetype, moduleid, strbeaconname;
    EditText et_beacon_name;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        btnClick = findViewById(R.id.btnClick);
        recycler = findViewById(R.id.recycler);
        et_beacon_name = findViewById(R.id.et_beacon_name);

        mScannername = getIntent().getStringExtra("SCANNER_NAME");
        mRoomname = getIntent().getStringExtra("ROOM_NAME");
        mRoomid = getIntent().getStringExtra("ROOM_ID");
        mSensorid = getIntent().getStringExtra("SENSOR_ID");
        mScannerdeviceid = getIntent().getStringExtra("SCANNER_DEVICE_ID");
        mdevicetype = getIntent().getStringExtra("SCANNER_DEVICE_TYPE");
        moduleid = getIntent().getStringExtra("BEACON_SCANNER_MODULE_ID");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Select Beacon");
        getSupportActionBar().setTitle("Select Beacon");

        bAdapter = BluetoothAdapter.getDefaultAdapter();
        setViewid();
    }

    private void setViewid() {
        btnClick.setOnClickListener(this);

        itemList.clear();
        itemListTemp.clear();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching nearby beacons...");
        progressDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        scanresult.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        if (id == R.id.action_scan) {
            progressDialog.show();
            itemList.clear();
            itemListTemp.clear();
            scanresult.clear();
            checkpermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        if (!bAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }


        scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(2000)
                .setUseHardwareBatchingIfSupported(true)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        scanner.startScan(filters, settings, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);
            }

            @Override
            public void onBatchScanResults(@NonNull List<ScanResult> results) {
                super.onBatchScanResults(results);
                scanresult.clear();
                if (results != null) {
                    progressDialog.dismiss();
                    for (ScanResult result : results) {
                        scanresult.add(result);
                    }

                    Collections.sort(results, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult s1, ScanResult s2) {
                            return s1.getDevice().getAddress().compareTo(s2.getDevice().getAddress());
                        }
                    });

                    beaconListAdapter.updatebeaconlistitem(scanresult);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BeaconActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recycler.setLayoutManager(linearLayoutManager);
                    beaconListAdapter = new BeaconListAdapter(scanresult, BeaconActivity.this);
                    recycler.setAdapter(beaconListAdapter);


                    setSyncRange();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == btnClick) {
        }
    }

    public void checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(BeaconActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(BeaconActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BeaconActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                startScan();
                return;
            } else {
                startScan();
            }
        } else {
            startScan();
        }

    }

    private void setSyncRange() {
        try {
            if (scanresult.size() > 0) {
                for (int i = 0; i < scanresult.size(); i++) {
                    ChatApplication.logDisplay("name is " + scanresult.get(i).getScanRecord().getDeviceName());
                    if (scanresult.get(i).getRssi() > -100) {
//                    itemList.get(i).setOnOff(true);
//                    itemListTemp.get(i).setOnOff(true);
                   /* if(!dialog.isShowing()){
                        showDialog1(i);
                    }*/


                    } else if (scanresult.get(i).getRssi() > -20) {
                        Log.d("System out", "mLeScanCallback is false found " + itemListTemp.get(i).isOnOff());

                        if (itemListTemp.get(i).isOnOff()) {
                            itemListTemp.get(i).setOnOff(false);
                            itemList.get(i).setOnOff(false);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * save Beacon
     *
     * @param beacon_name
     * @param beacon_module_id
     */
    private void saveBeacon(String beacon_name, String beacon_module_id, int beaconrssi) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", true);

        JSONObject obj = new JSONObject();
        try {
            obj.put("device_name", "SBCON");
            obj.put("module_id", beacon_module_id);
            obj.put("module_type", "beacon");
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        url = ChatApplication.url + Constants.deviceadd;

        ChatApplication.logDisplay("obj : " + url + "  " + obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ChatApplication.logDisplay("obj result: " + result);

                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Common.showToast(message);
                        }
                        ActivityHelper.dismissProgressDialog();

                        //  Constants.activityWifi.finish();
                        Intent intent = new Intent(BeaconActivity.this, BeaconConfigActivity.class);
                        intent.putExtra("SCANNER_NAME", mScannername);
                        intent.putExtra("ROOM_NAME", mRoomname);
                        intent.putExtra("ROOM_ID", "" + mRoomid);
                        intent.putExtra("SENSOR_ID", mSensorid);
                        intent.putExtra("SCANNER_DEVICE_ID", "" + mScannerdeviceid);
                        intent.putExtra("SCANNER_DEVICE_TYPE", mdevicetype);
                        intent.putExtra("BEACON_SCANNER_MODULE_ID", moduleid);
                        intent.putExtra("BEACON_DEVICE_NAME", "" + beacon_name);
                        intent.putExtra("SCANNER_DEVICE_ADDRESS", beacon_module_id);
                        intent.putExtra("BEACON_RSSI", beaconrssi);
                        startActivity(intent);
                    } else {
                        Common.showToast(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("obj result: error " + error);
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClickBeaconList(ScanResult scanresultlist) {
        String beaconname = scanresultlist.getScanRecord().getDeviceName();
        String beaconaddress = scanresultlist.getDevice().getAddress();
        int beaconrssi = scanresultlist.getRssi();
        strbeaconname = et_beacon_name.getText().toString();
        if (TextUtils.isEmpty(strbeaconname)) {
            ChatApplication.showToast(BeaconActivity.this, "Please enter beacon name");
        } else {
            saveBeacon(strbeaconname, beaconaddress, beaconrssi);
        }

    }
}
