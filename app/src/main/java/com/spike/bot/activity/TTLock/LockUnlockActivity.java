package com.spike.bot.activity.TTLock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.Retrofit.GetDataService;
import com.spike.bot.Retrofit.RetrofitAPIManager;
import com.spike.bot.core.Constants;
import com.spike.bot.model.AccountInfo;
import com.spike.bot.model.KeyListObj;
import com.spike.bot.model.KeyObj;
import com.spike.bot.model.LockObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.callback.ResetLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sagar on 30/8/19.
 * Gmail : vipul patel
 */
public class LockUnlockActivity extends AppCompatActivity implements View.OnClickListener {

    public LockObj mCurrentLock;
    KeyObj mMyTestLockEKey;
    Button btn_lock,btn_unlock,btn_Remotlyunlock,btnStatus,btnDelete,btn_Remotlylock;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);

//        mCurrentLock = ChatApplication.mTestLockObj;

        mCurrentLock= (LockObj) getIntent().getSerializableExtra("LockObj");

        setUIId();
        getUserKeyList();
    }

    private void setUIId() {
        btn_unlock=findViewById(R.id.btn_unlock);
        btn_lock=findViewById(R.id.btn_lock);
        btn_Remotlyunlock=findViewById(R.id.btn_Remotlyunlock);
        btnStatus=findViewById(R.id.btnStatus);
        btnDelete=findViewById(R.id.btnDelete);
        btn_Remotlylock=findViewById(R.id.btn_Remotlylock);

        btn_lock.setOnClickListener(this);
        btn_unlock.setOnClickListener(this);
        btn_Remotlyunlock.setOnClickListener(this);
        btnStatus.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btn_Remotlylock.setOnClickListener(this);
    }

    //user should get a key list and show them with a list.In demo,we just have one admin key.
    private void getUserKeyList(){
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        HashMap<String,String> param = new HashMap<>(6);
        param.put("clientId", Constants.client_id);
        param.put("accessToken",Constants.access_token);
        param.put("pageNo","1");
        param.put("pageSize","1000");
        param.put("date",String.valueOf(System.currentTimeMillis()));

        Call<ResponseBody> call = apiService.getUserKeyList(param);
        RetrofitAPIManager.enqueue(call, new TypeToken<KeyListObj>(){}, result -> {
            if(!result.success){
                ChatApplication.showToast(this, "--get my key list fail-" + result.getMsg());
                return;
            }
            Log.d("OMG","===result===" + result.getResult() + "===" + result);
            KeyListObj keyListObj = result.getResult();
            ArrayList<KeyObj> myKeyList = keyListObj.getList();
            if(!myKeyList.isEmpty()){
                for(KeyObj keyObj : myKeyList){
                    if(keyObj.getLockId() == mCurrentLock.getLockId()){
                        mMyTestLockEKey = keyObj;
                    }
                }
            }
        }, requestError -> {
            ChatApplication.showToast(this,"--get key list fail-" + requestError.getMessage());
        });
    }

    /**
     * make sure Bluetooth is enabled.
     */
    public void ensureBluetoothIsEnabled(){
        if(!TTLockClient.getDefault().isBLEEnabled(this)){
            TTLockClient.getDefault().requestBleEnable(this);
        }
    }

    private void blueToothUnlock(){
        if(mMyTestLockEKey == null){
            ChatApplication.showToast(this," you should get your key list first ");
            return;
        }

        ensureBluetoothIsEnabled();

        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, mMyTestLockEKey.getLockData(), mMyTestLockEKey.getLockMac(),new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(int lockAction, int battery, int uniqueId) {
                Toast.makeText(LockUnlockActivity.this,"lock is unlock  success!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(LockError error) {
                Toast.makeText(LockUnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * use eKey for controlLock interface
     */
    private void doUnlockRemotly(){
//        if(mMyTestLockEKey == null){
//            ChatApplication.showToast(this," you should get your key list first ");
//            return;
//        }

        callUnlockGateway();
        //ensureBluetoothIsEnabled();

//        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, mMyTestLockEKey.getLockData(), mMyTestLockEKey.getLockMac(),new ControlLockCallback() {
//            @Override
//            public void onControlLockSuccess(int lockAction, int battery, int uniqueId) {
//                Toast.makeText(LockUnlockActivity.this,"lock is unlock  success!",Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFail(LockError error) {
//                Toast.makeText(LockUnlockActivity.this,"unLock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
//            }
//        });
    }

    /**
     * use eKey for controlLock interface
     */
    private void doLockLock(){
        if(mMyTestLockEKey == null){
            ChatApplication.showToast(this," you should get your key list first ");
            return;
        }
        ensureBluetoothIsEnabled();
        TTLockClient.getDefault().controlLock(ControlAction.LOCK, mMyTestLockEKey.getLockData(), mMyTestLockEKey.getLockMac(),new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(int lockAction, int battery, int uniqueId) {
                Toast.makeText(LockUnlockActivity.this,"lock is locked!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(LockError error) {
                Toast.makeText(LockUnlockActivity.this,"lock lock fail!--" + error.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
    }


    public void callUnlockGateway(){
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<ResponseBody> call = apiService.unlockGatewayUse(Constants.client_id, Constants.access_token, mCurrentLock.getLockId(),System.currentTimeMillis());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ActivityHelper.dismissProgressDialog();
                if(response.code()==200){
                    ChatApplication.logDisplay("lock is "+response.body().toString());

                }else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });

    }


    public void callLockStatus(){
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.lockStatus(Constants.client_id, Constants.access_token, mCurrentLock.getLockId(),System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    ChatApplication.logDisplay("lock is "+response.body().toString());

                    try {
                        JSONObject jsonObject=new JSONObject(response.body().toString());

                        if(jsonObject.optString("status").equalsIgnoreCase("0")){
                            btnStatus.setText("Locked");
                        }else {
                            btnStatus.setText("Unlocked");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });

    }


 public void callDeleteLock(){
     ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.lockdelete(Constants.client_id, Constants.access_token, mCurrentLock.getLockId(),System.currentTimeMillis());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ActivityHelper.dismissProgressDialog();
                if(response.code()==200){
                    ChatApplication.showToast(LockUnlockActivity.this,"Lock Deleted.");
                }else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });

    }



    public void calllockGateway(){
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        GetDataService apiService = RetrofitAPIManager.provideClientApi();
        Call<ResponseBody> call = apiService.lockGatewayUse(Constants.client_id, Constants.access_token, mCurrentLock.getLockId(),System.currentTimeMillis());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ActivityHelper.dismissProgressDialog();
                if(response.code()==200){
                    ChatApplication.logDisplay("lock is "+response.body().toString());

                }else {
                    ChatApplication.logDisplay("tt lock reponse is error ff ");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("tt lock reponse is error");
            }
        });

    }

    /**
     * stopBTService should be called when Activity is finishing to release Bluetooth resource.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        TTLockClient.getDefault().stopBTService();
    }

    @Override
    public void onClick(View v) {
        if(v==btn_unlock){
            blueToothUnlock();
        } else if(v==btn_lock){
            doLockLock();
        }else if(v==btn_Remotlyunlock){
            doUnlockRemotly();
        }else if(v==btn_Remotlylock){
            calllockGateway();
        }else if(v==btnStatus){
            callLockStatus();
        }else if(v==btnDelete){

            TTLockClient.getDefault().resetLock(mCurrentLock.getLockData(), mCurrentLock.getLockMac(),new ResetLockCallback() {
                @Override
                public void onResetLockSuccess() {
                    callDeleteLock();
                }

                @Override
                public void onFail(LockError error) {

                }
            });

        }
    }
}
