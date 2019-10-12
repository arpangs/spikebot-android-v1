package com.spike.bot.activity.TTLock;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.adapter.TTlockAdapter.LockListAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.Log;
import com.spike.bot.model.AccountInfo;
import com.spike.bot.model.LockInitResultObj;
import com.spike.bot.model.LockObj;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.callback.InitLockCallback;
import com.ttlock.bl.sdk.callback.ScanLockCallback;
import com.ttlock.bl.sdk.callback.SetAutoLockingPeriodCallback;
import com.ttlock.bl.sdk.callback.SetNBServerCallback;
import com.ttlock.bl.sdk.callback.SetRemoteUnlockSwitchCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.constant.Feature;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.SpecialValueUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 27/8/19.
 * Gmail : vipul patel
 */
/*ttchatcrash@gmail.com / ttchat$123 account  = ttlock account check access_token   */
public class AddTTlockActivity extends AppCompatActivity implements View.OnClickListener, LockListAdapter.onLockItemClick {

    //vipulgk@tasktower.com / vg99092vg

    protected static final int REQUEST_PERMISSION_REQ_CODE = 11;
    Toolbar toolbar;
    LinearLayout linearLockInfo;
    private LockListAdapter mListApapter;
    AppCompatButton btnStartScan;
    RecyclerView recyclerViewLock;
    public AccountInfo accountInfo;

    private Socket mSocket;

    ArrayList<LockObj> lockObjsAllList = new ArrayList<>();
    public boolean isBackFlag = false;
    public String isFlagView = "", lockName = "", room_id = "", panel_id = "", device_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt_lock);

        isFlagView = getIntent().getStringExtra("isFlagView");
        lockName = getIntent().getStringExtra("lockName");
        device_id = getIntent().getStringExtra("device_id");
        panel_id = getIntent().getStringExtra("panel_id");
        room_id = getIntent().getStringExtra("room_id");

        lockObjsAllList = (ArrayList<LockObj>) getIntent().getSerializableExtra("lockObjs");

        setUiId();
    }

    private void setUiId() {
        initBtService();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Add Lock");

        btnStartScan = findViewById(R.id.btnStartScan);
        recyclerViewLock = findViewById(R.id.recyclerViewLock);
        linearLockInfo = findViewById(R.id.linearLockInfo);
        btnStartScan.setOnClickListener(this);
        //check EnableBluetooth enable
        boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(AddTTlockActivity.this);
        if (!isBtEnable) {
            TTLockClient.getDefault().requestBleEnable(AddTTlockActivity.this);
        }

        startSocketConnection();
        initList();

    }

    private void startSocketConnection() {
        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initList() {
        linearLockInfo = findViewById(R.id.linearLockInfo);
        mListApapter = new LockListAdapter(this);
        recyclerViewLock.setAdapter(mListApapter);
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        mListApapter.setOnLockItemClick(this);

        //check access token refresh
        if (Constants.lockDate < 86400 || TextUtils.isEmpty(Common.getPrefValue(AddTTlockActivity.this, Constants.lock_token))) {
            calllogin();
        }

    }

    public void setView(boolean isFlag) {
        recyclerViewLock.setVisibility(isFlag ? View.VISIBLE : View.GONE);
        linearLockInfo.setVisibility(isFlag ? View.GONE : View.VISIBLE);
    }

    /**
     * Login for TT lock
     */
    public void calllogin() {

        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.auth(Constants.client_id, Constants.client_secret, "password", Constants.lockUserName, DigitUtil.getMD5(Constants.lockPassword), Constants.locK_base_uri);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    ChatApplication.logDisplay("tt lock reponse is response ");

                    String json = response.body();
                    accountInfo = GsonUtil.toObject(json, AccountInfo.class);
                    Log.d("System out", "accountInfo is " + accountInfo);

                /*{access_token='fac1734b6209dd5b3ea602c9cc7a15ae',
                refresh_token='5ca1a4bc670b16b571b1488a631e57fc', uid=1769341, openid=1930389027, scope='user,key,room', token_type='Bearer', expires_in=5432678}*/
                    if (accountInfo != null) {
                        if (accountInfo.errcode == 0) {
                            accountInfo.setMd5Pwd(Constants.lockPassword);
                        }


                        JSONObject object = null;
                        try {
                            object = new JSONObject(json);

                            Constants.lockDate = object.optInt("expires_in");
                            Constants.access_token = object.optString("access_token");
                            Common.savePrefValue(ChatApplication.getInstance(), Constants.lock_exe, "" + Constants.lockDate);
                            Common.savePrefValue(ChatApplication.getInstance(), Constants.lock_token, Constants.access_token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                    }
                } else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });

    }

    /**
     * before call startScanLock,the location permission should be granted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void startScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
        getScanLockCallback();
    }

    /**
     * start scan BT lock
     */
    private void getScanLockCallback() {
        TTLockClient.getDefault().startScanLock(new ScanLockCallback() {
            @Override
            public void onScanLockSuccess(ExtendedBluetoothDevice device) {
                ChatApplication.logDisplay("on result found ");
                if (mListApapter != null) {
                    mListApapter.updateData(device);
                }
            }

            @Override
            public void onFail(LockError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnStartScan) {
            isBackFlag = true;
            setView(true);
            startScan();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getScanLockCallback();
                } else {
                    if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    }
                }
                break;
            }
            default:
                break;
        }
    }


    @Override
    public void onClick(ExtendedBluetoothDevice device) {
        if (TextUtils.isEmpty(lockName)) {
            lockNameDialog(device);
        } else {
            addlockInit(device, lockName);
        }

    }

    private void lockNameDialog(ExtendedBluetoothDevice device) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_tt_lock_name);

        final TextInputEditText room_name = (TextInputEditText) dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (room_name.getText().toString().length() > 0) {
                    ChatApplication.keyBoardHideForce(AddTTlockActivity.this);
                    dialog.dismiss();
                    lockName = room_name.getText().toString();
                    addlockInit(device, room_name.getText().toString());
                } else {
                    ChatApplication.showToast(AddTTlockActivity.this, "Please enter lock name");
                }

            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void addlockInit(ExtendedBluetoothDevice device, String lockName) {

        ActivityHelper.showProgressDialog(AddTTlockActivity.this, "Please Wait...", false);
        TTLockClient.getDefault().initLock(device, new InitLockCallback() {
            @Override
            public void onInitLockSuccess(String lockData, int specialValue) {

                setAutoLockMode(lockData, device, specialValue);

                if (SpecialValueUtil.isSupportFeature(specialValue, Feature.NB_LOCK)) {
                    setNBServerForNBLock(lockData, device.getAddress(), lockName);
                } else {
                    upload2Server(lockData, lockName);
                }
            }

            @Override
            public void onFail(LockError error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(AddTTlockActivity.this, "" + error);
            }
        });
    }

    /**
     * Set auto lock mode
     */
    private void setAutoLockMode(String lockData, ExtendedBluetoothDevice device, int specialValue) {
        ChatApplication.logDisplay("lock is need " + lockData);
        ChatApplication.logDisplay("lock is need address " + device.getAddress());
        TTLockClient.getDefault().setAutomaticLockingPeriod(10, lockData, device.getAddress(), new SetAutoLockingPeriodCallback() {
            @Override
            public void onSetAutoLockingPeriodSuccess() {
                ChatApplication.logDisplay("auto lock done");
                setRemotlyOn(lockData, device, specialValue);
            }

            @Override
            public void onFail(LockError error) {
                ChatApplication.logDisplay("auto lock done error ");
                ChatApplication.showToast(AddTTlockActivity.this, "" + error);
            }
        });
    }

    public void setRemotlyOn(String lockData, ExtendedBluetoothDevice device, int specialValue) {
        if (!DigitUtil.isSupportAudioManagement(specialValue)) {
            ChatApplication.showToast(AddTTlockActivity.this, "this lock does not support remote unlock");
            return;
        }

        TTLockClient.getDefault().setRemoteUnlockSwitchState(true, lockData, device.getAddress(), new SetRemoteUnlockSwitchCallback() {
            @Override
            public void onSetRemoteUnlockSwitchSuccess(int specialValue) {
                ChatApplication.logDisplay("auto lock remotly done");

                setlock(lockData, device, specialValue);

            }

            @Override
            public void onFail(LockError error) {
                ChatApplication.logDisplay("auto lock remotly error");
            }
        });
    }

    private void setlock(String lockData, ExtendedBluetoothDevice device, int specialValue) {
        TTLockClient.getDefault().controlLock(ControlAction.LOCK, lockData, device.getAddress(), new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(int lockAction, int battery, int uniqueId) {
                ChatApplication.logDisplay("auto unlock done");
            }

            @Override
            public void onFail(LockError error) {
                ChatApplication.logDisplay("auto unlock error");
            }
        });

    }

    /**
     * if a NB-IoT lock you'd better do set NB-IoT server before upload lockData to server to active NB-IoT lock service.
     * And no matter callback is success or fail,upload lockData to server.
     *
     * @param lockData
     * @param lockMac
     */
    private void setNBServerForNBLock(String lockData, String lockMac, String ttlock_name) {
        //NB server port
        short mNBServerPort = 8011;
        String mNBServerAddress = "192.127.123.11";
        TTLockClient.getDefault().setNBServerInfo(mNBServerPort, mNBServerAddress, lockData, lockMac, new SetNBServerCallback() {
            @Override
            public void onSetNBServerSuccess(int battery) {
                ChatApplication.showToast(AddTTlockActivity.this, "--set NB server success--");
                upload2Server(lockData, ttlock_name);
            }

            @Override
            public void onFail(LockError error) {
                ChatApplication.showToast(AddTTlockActivity.this, "" + error);
                //no matter callback is success or fail,upload lockData to server.
                upload2Server(lockData, ttlock_name);
            }
        });
    }


    /**
     * prepareBTService should be called first,or all TTLock SDK function will not be run correctly
     */
    private void initBtService() {
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
    }


    private void upload2Server(String lockData, String ttlock_name) {

        GetDataService apiService = RetrofitAPIManager.provideClientApi();

        Call<ResponseBody> call = apiService.lockInit(Constants.client_id, Constants.access_token, lockData, ttlock_name, System.currentTimeMillis());
        RetrofitAPIManager.enqueue(call, new TypeToken<LockInitResultObj>() {
        }, result -> {
            ActivityHelper.dismissProgressDialog();
            if (!result.success) {
                ChatApplication.showToast(this, "-init fail-to server-" + result.getMsg());
                //if upload fail you should cache lockData and upload again until success,or you should reset lock and do init again.
                return;
            }

            deletLockFromSpikebot(result.getResult(), lockData);
            ChatApplication.showToast(this, ("lock is initialized success"));

        }, requestError -> {
            ActivityHelper.dismissProgressDialog();
        });
    }

    private void callAddTTlock(String lock_id, String lockData) {
        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        String url = ChatApplication.url + Constants.addTTLock;

        JSONObject jsonObject = new JSONObject();
        try {

            //"is_new":,
            // "bridge_id":"",
            //    "lock_id":"",
            //    "room_id":"",
            //    "panel_id":"",
            //    "door_sensor_id":"",
            //    "user_id":"",
            //    "lock_name":""

            // panel_id=null & is_new =0

            if (TextUtils.isEmpty(device_id) || device_id.length() == 0) {
                jsonObject.put("panel_id", "");
                jsonObject.put("door_sensor_id", "");
            } else {
                jsonObject.put("panel_id", "" + panel_id);
                jsonObject.put("door_sensor_id", "" + device_id);
            }

            // 1 means new room & new add
            // o means exting room add
            if (isFlagView.equalsIgnoreCase("1")) {
                jsonObject.put("is_new", 1);
            } else {
                jsonObject.put("is_new", 0);
            }
            jsonObject.put("lock_id", lock_id);
            jsonObject.put("room_id", room_id);
            jsonObject.put("lock_data", lockData);

            jsonObject.put("lock_name", lockName);
            jsonObject.put("user_id", "" + Common.getPrefValue(this, Constants.USER_ID));

            ChatApplication.logDisplay("url is " + jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject jsonObject1) {
                ChatApplication.logDisplay("result is " + jsonObject1);

                if (jsonObject1.optString("code").equalsIgnoreCase("200")) {
                    ActivityHelper.dismissProgressDialog();
                    //{"code":301,"message":"Lock Already Exists"}

                    JSONObject object = new JSONObject();
                    try {
                        object.put("lock_id", lock_id);
                        object.put("user_id", Common.getPrefValue(AddTTlockActivity.this, Constants.USER_ID));
                        object.put("door_sensor_status", 0);
                        object.put("lock_status", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ChatApplication.logDisplay("door json " + object);
                    if (mSocket != null) {
                        mSocket.emit("changeLockStatus", object);
                    }

                    Intent intent = new Intent(AddTTlockActivity.this, Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void deletLockFromSpikebot(LockInitResultObj result, String lockData) {

        ActivityHelper.showProgressDialog(this, " Please Wait...", false);
        String url = ChatApplication.url + Constants.deleteTTLock;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lock_id", result.getLockId());
            jsonObject.put("user_id", "" + Common.getPrefValue(this, Constants.USER_ID));

            ChatApplication.logDisplay("url is " + jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject jsonObject1) {
                ChatApplication.logDisplay("result is " + jsonObject1);
                callAddTTlock("" + result.getLockId(), lockData);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                callAddTTlock("" + result.getLockId(), lockData);
            }
        }).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TTLockClient.getDefault().stopBTService();
    }

    @Override
    public void onBackPressed() {
        if (isBackFlag) {
            isBackFlag = false;
            setView(false);
        } else {
            super.onBackPressed();
        }
    }
}
