package com.spike.bot.activity.TTLock;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Constants;
import com.spike.bot.model.GatewayObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.ttlock.bl.sdk.util.NetworkUtil;
import com.ttlock.gateway.sdk.api.GatewayAPI;
import com.ttlock.gateway.sdk.callback.GatewayCallback;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Sagar on 31/8/19.
 * Gmail : vipul patel
 */
public class AddGatewayActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView wifiName;
    EditText password, etGatewayname;
    Button btnNext, btnSubmit;
    LinearLayout linearWifiBridge, linearBridge;

    String wifiUserName = "", wifiPassword = "", gatewayName = "";
    boolean isFlagClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateway);
        setViewId();
    }

    private void setViewId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Add Bridge");

        btnNext = findViewById(R.id.btnNext);
        etGatewayname = findViewById(R.id.etGatewayname);
        password = findViewById(R.id.password);
        wifiName = findViewById(R.id.wifiName);
        btnSubmit = findViewById(R.id.btnSubmit);
        linearBridge = findViewById(R.id.linearBridge);
        linearWifiBridge = findViewById(R.id.linearWifiBridge);

        btnNext.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        startScan();
        initView();

    }

    /*check permission */
    @TargetApi(Build.VERSION_CODES.M)
    private void startScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            return;
        }
        initView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case 11: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (password.getText().toString().length() == 0) {
                ChatApplication.showToast(this, "Please enter password");
            } else if (etGatewayname.getText().toString().length() == 0) {
                ChatApplication.showToast(this, "Please enter Bridge name");
            } else {
                wifiUserName = wifiName.getText().toString();
                wifiPassword = password.getText().toString();
                gatewayName = etGatewayname.getText().toString();
                ChatApplication.keyBoardHideForce(AddGatewayActivity.this);
                isFlagClick = true;
                boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(AddGatewayActivity.this);
                if (!isBtEnable) {
                    TTLockClient.getDefault().requestBleEnable(AddGatewayActivity.this);
                }

                setView(true);
            }
        } else if (v == btnSubmit) {
            callServerGateway();
        }
    }

    /* set wifi name */
    private void initView() {
        if (NetworkUtil.isWifiConnected(this)) {
            wifiName.setText(NetworkUtil.getWifiSSid(this));
        }
    }

    /*
    * lock init*/
    private void isInitSuccess(String macaddress) {
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.gatewayIsInitSuccess(Constants.client_id, Constants.access_token, macaddress, System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                ActivityHelper.dismissProgressDialog();
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    GatewayObj gatewayObj = GsonUtil.toObject(json, GatewayObj.class);
                    if (gatewayObj.errcode == 0) {

                        callUploadToserver(gatewayObj.getGatewayId());

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    /*upload data to tt server
    * */
    private void callUploadToserver(int gatewayId) {
        GetDataService apiService = RetrofitAPIManager.provideClientApi();

        Call<String> call = apiService.bridgeUploadtoServer(ChatApplication.url + "/addLockBridge", "" + gatewayId, gatewayName);
        LogUtil.d("call server api callUploadToserver");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.code() == 200) {
                    callAddBridge(gatewayId);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    /*call bridge list
    * */
    private void callAddBridge(int gatewayId) {

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().callAddBridge(gatewayId, gatewayName, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(AddGatewayActivity.this, "" + message);
                        ActivityHelper.dismissProgressDialog();
                        AddGatewayActivity.this.finish();
                    } else {
                        deleteGateway(gatewayId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*delete gateway
    * every time add device first delete device after add lock*/
    private void deleteGateway(int gatewayId) {


        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteGateway(gatewayId, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    if (code == 200) {
                        callAddBridge(gatewayId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

        String webUrl = ChatApplication.url + Constants.deleteTTLockBridge;

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("bridge_id", gatewayId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* add bridge in spikebot
    * */
    private void callServerGateway() {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        GatewayAPI gatewayAPI = new GatewayAPI(AddGatewayActivity.this, new GatewayCallback() {
            @Override
            public void onConnectTimeOut() {
                AddGatewayActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(AddGatewayActivity.this, "Please try again.");
                    }
                });

            }

            @Override
            public void onConnectOk(String mac) {
                isInitSuccess(mac);

            }
        });
        //start link
        gatewayAPI.startConnectLink(Constants.lockUserId, Constants.lockPassword, wifiUserName, wifiPassword);
    }

    public void setView(boolean isClick) {
        linearWifiBridge.setVisibility(isClick ? View.GONE : View.VISIBLE);
        linearBridge.setVisibility(isClick ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (isFlagClick) {
            isFlagClick = false;
            setView(false);
        } else {
            super.onBackPressed();
        }

    }
}
