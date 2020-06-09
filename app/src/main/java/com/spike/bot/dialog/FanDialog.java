package com.spike.bot.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.goodiebag.protractorview.ProtractorView;
import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
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
    ProtractorView protractorView1, protractorView;
    SeekBar sb_fan;
    TextView tv_seek_value, tv_title, textview_fanspeed;
    RelativeLayout relative_plus, relative_minus;

    public String room_device_id = "", device_id = "", device_name = "";
    onCallback iCallback;
    int fanSpeed = 3, /*numadd = 1, numsub = 1,*/ angle = 0;

    public FanDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    public FanDialog(Activity activity, String room_device_id, String devicename, int fanSpeed, onCallback iCallback) {
        super(activity);
        this.activity = activity;
        this.room_device_id = room_device_id;
        this.fanSpeed = fanSpeed;
        this.device_name = devicename;
        this.iCallback = iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_fan);

        tv_seek_value = findViewById(R.id.tv_seek_value);
        textview_fanspeed = findViewById(R.id.textview_fanspeed);
        tv_title = findViewById(R.id.tv_title);
        sb_fan = findViewById(R.id.sb_fan);
        btn_save = findViewById(R.id.btn_save);
        iv_close = findViewById(R.id.iv_close);
        protractorView = findViewById(R.id.Protractor);
        protractorView1 = findViewById(R.id.Protractor1);
        relative_plus = findViewById(R.id.relative_plus);
        relative_minus = findViewById(R.id.relative_minus);

        protractorView.setTouchInside(false);
        protractorView1.setTouchInside(false);

        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        sb_fan.setProgress(fanSpeed - 1);
        tv_title.setText(device_name);

        protractorView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (fanSpeed == 1) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 0);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        } else if (fanSpeed == 2) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 50);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        } else if (fanSpeed == 3) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 90);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        } else if (fanSpeed == 4) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 140);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        } else if (fanSpeed == 5) {
            protractorView.setVisibility(View.GONE);
            protractorView1.setVisibility(View.VISIBLE);
            protractorView1.setAngle(180 - 180);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        }

        sb_fan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                fanSpeed = i + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        relative_plus.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                increment();

              /*  int ng = 140;
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);

                protractorView1.setAngle(180 - ng);*/

            }
        });

        relative_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });
//        getFanDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                changeFanSpeed();
                //  configureNewRoom();

                break;
            case R.id.iv_close:
                iCallback.onSuccess(fanSpeed);
                dismiss();
                break;

            default:
                break;
        }
    }

    public void getFanDetails() {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {
            //{
            //	"device_id": "1571916523890_M5cxIPtxSa",
            //	"device_status": "1",
            //	"device_sub_status":"2"
            //}
            obj.put("device_id", room_device_id);
            obj.put("device_status", room_device_id);
            obj.put("room_device_id", room_device_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONArray dataJson = result.getJSONArray("data");
                        fanSpeed = dataJson.getJSONObject(0).getInt("device_specific_value");
                        sb_fan.setProgress(fanSpeed - 1);
                        //{"code":200,"message":"success","data":[{"device_specific_value":4}]}
                        ActivityHelper.dismissProgressDialog();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void changeFanSpeed() {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
      //  ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            //{
            //	"device_id": "1571916523890_M5cxIPtxSa",
            //	"device_status": "1",
            //	"device_sub_status":"2"
            //}
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(activity.getApplicationContext(), Constants.USER_ID));
            obj.put("device_id", room_device_id);
            obj.put("device_status", "1");
            obj.put("device_sub_status", fanSpeed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;

        ChatApplication.logDisplay("fan is " + url + " " + obj);

        new GetJsonTask(activity, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ChatApplication.logDisplay("fan is result " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200)
                    {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                      //  ActivityHelper.dismissProgressDialog();
                        dismiss();
//                        iCallback.onSuccess("yes"+"-"+fanSpeed);
                        iCallback.onSuccess(fanSpeed);
                    } else {
                       // Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
              //  ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    private void increment() {
        fanSpeed++;

        if (fanSpeed > 5) {
            fanSpeed = 5;
            textview_fanspeed.setText(String.valueOf(fanSpeed));
            ChatApplication.logDisplay("Increase number" + " " + fanSpeed);

        } else {
            ChatApplication.logDisplay("Increase number" + " " + fanSpeed);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
        }

        if (fanSpeed == 1) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 2) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 3) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 4) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 5) {
            angle = fanSpeed;
            setangle(angle);
        }

    }

    private void decrement() {
        fanSpeed--;

        if (fanSpeed < 1) {
            fanSpeed = 1;
            ChatApplication.logDisplay("Decrease number" + " " + fanSpeed);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
            //setangle(angle);
        } else {
            ChatApplication.logDisplay("Decrease number" + " " + fanSpeed);
            textview_fanspeed.setText(String.valueOf(fanSpeed));
           // setangle(angle);
        }

        if (fanSpeed == 1) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 2) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 3) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 4) {
            angle = fanSpeed;
            setangle(angle);
        } else if (fanSpeed == 5) {
            angle = fanSpeed;
            setangle(angle);
        }
    }

    public void setangle(int angle) {
        switch (angle) {
            case 1:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 0);
               // changeFanSpeed(angle);
                break;
            case 2:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 50);
              //  changeFanSpeed(angle);
                break;
            case 3:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 90);
               // changeFanSpeed(angle);
                break;
            case 4:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 140);
             //   changeFanSpeed(angle);
                break;
            case 5:
                protractorView.setVisibility(View.GONE);
                protractorView1.setVisibility(View.VISIBLE);
                protractorView1.setAngle(180 - 180);
               // changeFanSpeed(angle);
                break;


        }
    }
}