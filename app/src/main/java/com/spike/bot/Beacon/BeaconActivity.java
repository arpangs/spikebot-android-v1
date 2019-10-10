package com.spike.bot.Beacon;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceVO;
import com.ttlock.bl.sdk.api.TTLockClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;

/**
 * Created by Sagar on 4/10/19.
 * Gmail : vipul patel
 */
public class BeaconActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeScanner mScanner;
    private BluetoothLeDeviceStore mDeviceStore;

    public int count=0;
    private long lastSyncTimeStamp = 0;
    Dialog dialog;
    List<LeDeviceItem> itemList = new ArrayList<>();
    List<LeDeviceItem> itemListTemp = new ArrayList<>();
    BeaconListAdapter beaconListAdapter;
    public RecyclerView recycler;
    public Button btnClick;

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            mDeviceStore.addDevice(deviceLe);

            for (final BluetoothLeDevice leDevice : mDeviceStore.getDeviceList()) {
                if (BeaconUtils.getBeaconType(leDevice) == BeaconType.IBEACON) {
                    Log.d("System out","mLeScanCallback is 11");
//                    itemList.add(new IBeaconItem(new IBeaconDevice(leDevice)));
                } else {
                    if(!TextUtils.isEmpty(leDevice.getName())){
                        Log.d("System out","mLeScanCallback is 22 name "+leDevice.getName());
                        if(leDevice.getName().startsWith("SBC")){
                            if(itemList.size()==0){
                                itemList.add(new LeDeviceItem(leDevice));
                                itemListTemp.add(new LeDeviceItem(leDevice));
                            }else {
                                for (int i = 0; i < itemList.size(); i++) {
                                    if (itemList.get(i).getDevice().getAddress().equals(leDevice.getAddress())) {
                                        itemList.set(i,new LeDeviceItem(leDevice));
                                        LeDeviceItem leDeviceItem=new LeDeviceItem(leDevice);
                                        leDeviceItem.setRssRange(itemList.get(i).isRssRange());

                                        itemListTemp.set(i,leDeviceItem);
                                    }else {
                                        itemList.add(new LeDeviceItem(leDevice));
                                        itemListTemp.add(new LeDeviceItem(leDevice));
                                    }
                                }
                            }
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = System.currentTimeMillis();
                            if((currentTime - lastSyncTimeStamp) >= 2000 ){
                                Log.d("System out","mLeScanCallback is 22 size "+itemList.size());
                                beaconListAdapter.notifyDataSetChanged();
                                setSyncRange();
                                lastSyncTimeStamp=currentTime;
                            }
                        }
                    });
                }
            }

        }
    };



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        btnClick=findViewById(R.id.btnClick);
        recycler=findViewById(R.id.recycler);

        setViewid();
    }

    private void setViewid() {
        btnClick.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);

        beaconListAdapter=new BeaconListAdapter(this,itemList);
        recycler.setAdapter(beaconListAdapter);
        beaconListAdapter.notifyDataSetChanged();

        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
    }

    @Override
    protected void onStop() {
        mScanner.scanLeDevice(-1, false);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void startScan() {
        final boolean isBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean isBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (isBluetoothOn && isBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnClick){
            itemList.clear();
            itemListTemp.clear();
            startScan();
        }
    }

    private void setSyncRange() {
        if(itemListTemp.size()>0){
            for(int i=0; i<itemListTemp.size(); i++){
                ChatApplication.logDisplay("rss is "+itemListTemp.get(i).getDevice().getRunningAverageRssi());
                if(itemListTemp.get(i).getDevice().getRunningAverageRssi()> -40){
                    itemListTemp.get(i).setRssRange(true);
                    showDialog();
                    break;
                }else if(itemListTemp.get(i).getDevice().getRunningAverageRssi()< -90){
                    if(itemListTemp.get(i).isRssRange()){
                        callDeviceOnOffApi();
                    }
                    itemListTemp.get(i).setRssRange(false);
                   if(dialog!=null){
                       dialog.dismiss();
                       dialog.cancel();
                   }
                }
            }
        }
    }
    public void showDialog(){

        if(dialog==null){
            dialog = new Dialog(BeaconActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
        }

        dialog.setContentView(R.layout.dialog_auto_lock);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        AppCompatTextView txtMessage = dialog.findViewById(R.id.txtMessage);

        txtTitle.setText("Near by Beacon");
        txtMessage.setText("Found Near by Beacon.....");

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callDeviceOnOffApi();
            }
        });
        if(!dialog.isShowing()){
            dialog.show();
        }
    }


    private void callDeviceOnOffApi() {

        String url = "";

        JSONObject obj=new JSONObject();

        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", "1528702842286_HyGYPsieQ");
            obj.put("room_device_id", "1528703556507_H1f2SqsieX");
            obj.put("module_id", "0785ED0B004B1200");
            obj.put("device_id", "3");
            if(count==1){
                obj.put("device_status",0);
                count=0;
            }else {
                count=1;
                obj.put("device_status",0);
            }

            obj.put("localData", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;


        ChatApplication.logDisplay("Device roomPanelOnOff obj " + obj.toString());

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("roomPanelOnOff onSuccess " + result.toString());
                try {
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code == 200) {
                    } else {
                        ChatApplication.showToast(BeaconActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ChatApplication.logDisplay("Device roomPanelOnOff finally ");
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("roomPanelOnOff onFailure " + error);
            }
        }).execute();
    }

}
