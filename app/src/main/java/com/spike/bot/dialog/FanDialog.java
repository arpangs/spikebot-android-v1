package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Constants;
import com.spike.bot.R;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kaushal on 27/12/17.
 */

public class FanDialog extends Dialog implements
        View.OnClickListener {
    String TAG = "FanDialog";
    public Activity activity;
    public Dialog d;
    public Button btn_save;
    public ImageView iv_close;

    SeekBar sb_fan;
    TextView tv_seek_value;

    public String room_device_id="", device_id="";
    ICallback iCallback;
    int fanSpeed=3;
    public FanDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    public FanDialog(Activity activity, String room_device_id, int fanSpeed, ICallback iCallback) {
        super(activity);
        this.activity = activity;
        this.room_device_id = room_device_id;
        this.fanSpeed=fanSpeed;

        this.iCallback=iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_fan);

        tv_seek_value = (TextView) findViewById(R.id.tv_seek_value);
        sb_fan = (SeekBar) findViewById(R.id.sb_fan);
        btn_save = (Button) findViewById(R.id.btn_save);
        iv_close = (ImageView) findViewById(R.id.iv_close);


        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        Log.d("","fanSpeed " + fanSpeed);

        sb_fan.setProgress(fanSpeed-1);
        sb_fan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("","onProgressChanged fan " + i);
               // tv_seek_value.setText("" +(i+1));
                fanSpeed = i+1 ;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getFanDetails();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                changeFanSpeed();
              //  configureNewRoom();

                break;
            case R.id.iv_close:
                iCallback.onSuccess("no");
                dismiss();
                break;

            default:
                break;
        }
    }

    public void getFanDetails(){

        Log.d(TAG, "getFanDetails getFanDetails");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("room_device_id",room_device_id);
            /*obj.put("room_device_id",room_device_id);
            obj.put("device_id",device_id);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.GET_FAN_SPEED;

        new GetJsonTask(activity,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getFanDetails onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        JSONArray dataJson = result.getJSONArray("data");
                        fanSpeed = dataJson.getJSONObject(0).getInt("device_specific_value");
                        sb_fan.setProgress(fanSpeed-1);
                //{"code":200,"message":"success","data":[{"device_specific_value":4}]}
                        ActivityHelper.dismissProgressDialog();
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getFanDetails onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void changeFanSpeed(){

        Log.d(TAG, "changeFanSpeed changeFanSpeed");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            obj.put("room_device_id",room_device_id);
            obj.put("device_specific_value",fanSpeed);
            obj.put("localData","0");
            {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.CHANGE_FAN_SPEED;

        new GetJsonTask(activity,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "changeFanSpeed onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dismiss();
                        iCallback.onSuccess("yes"+"-"+fanSpeed);
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "changeFanSpeed onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

}