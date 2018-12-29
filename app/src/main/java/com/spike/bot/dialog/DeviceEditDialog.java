package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Constants;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.model.DeviceVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kaushal on 27/12/17.
 */

public class DeviceEditDialog extends Dialog implements  View.OnClickListener {

    String TAG = "DeviceEditDialog";
    public Activity activity;
    public Dialog d;
    public Button btn_save;
    public ImageView iv_close;
    private ImageView spinner_arrow;

    EditText et_switch_name ;
    Spinner sp_device_type;
    TextView tv_title;
    LinearLayout ll_auto_mode_type;
    RadioGroup rg_auto_mode_type;
    RadioButton rb_auto_mode_type_normal,rb_auto_mode_type_dimmer;

    DeviceVO deviceVO;
    public String module_id="", device_id="",room_id="",room_name ="";
    ICallback iCallback;
    public JSONObject jsonObject;

    public DeviceEditDialog(Activity activity, DeviceVO deviceVO, JSONObject result, ICallback iCallback) {
        super(activity);
        this.activity = activity;
        this.deviceVO = deviceVO;
        this.iCallback=iCallback;
        this.jsonObject = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_device_edit);

        ll_auto_mode_type = (LinearLayout) findViewById(R.id.ll_auto_mode_type);
        ll_auto_mode_type.setVisibility(View.GONE);

        tv_title = (TextView) findViewById(R.id.tv_title);

        et_switch_name = (EditText) findViewById(R.id.et_switch_name );
        sp_device_type = (Spinner ) findViewById(R.id.sp_device_type );

        rg_auto_mode_type = (RadioGroup) findViewById(R.id.rg_auto_mode_type );

        rb_auto_mode_type_normal = (RadioButton) findViewById(R.id.rb_auto_mode_type_normal );
        rb_auto_mode_type_dimmer = (RadioButton ) findViewById(R.id.rb_auto_mode_type_dimmer );

        btn_save = (Button) findViewById(R.id.btn_save);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        spinner_arrow = (ImageView) findViewById(R.id.spinner_arrow);

        et_switch_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        flags.add("--");
        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(activity,flags,0,true);
        sp_device_type.setAdapter(customAdapter);

        spinner_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_device_type.performClick();
            }
        });

        getSwitchDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if(TextUtils.isEmpty(et_switch_name.getText().toString())){
                    Toast.makeText(activity,  "Enter Switch name" ,Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    deviceObj.put("device_name",et_switch_name.getText().toString());
                    deviceObj.put("device_icon",flags.get(sp_device_type.getSelectedItemPosition()).toString());
                    deviceObj.put("device_type",rg_auto_mode_type.getCheckedRadioButtonId()==R.id.rb_auto_mode_type_normal?0:1);

                    Log.d("deviceObj","updated deviceObj " + deviceObj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveSwithcDetails();
               // saveSwithcDetails();

                break;
            case R.id.iv_close:
                iCallback.onSuccess("no");
                dismiss();
                break;

            default:
                break;
        }
    }

    public void saveSwithcDetails(){

        Log.d(TAG, "saveSwithcDetails saveSwithcDetails");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            obj.put("device_details",deviceObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.SAVE_EDIT_SWITCH;

        Log.d("saveEdit","edit : " + obj.toString());

        new GetJsonTask(activity,url ,"POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "saveSwithcDetails onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(activity.getApplicationContext().getApplicationContext(),  message , Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dismiss();
                        iCallback.onSuccess("yes");
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
                Log.d(TAG, "saveSwithcDetails onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    JSONObject deviceObj;

    public void getSwitchDetails(){

        try {
            JSONObject obj = jsonObject.getJSONObject("data");
            JSONArray deviceArray = obj.getJSONArray("device_details");
            deviceObj = deviceArray.getJSONObject(0);
            JSONArray device_iconsArray = obj.getJSONArray("device_icons");

            setSpinnerValue(device_iconsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            setDeviceValue();
            ActivityHelper.dismissProgressDialog();
        }
        /*

        Log.d(TAG, "getSwitchDetails getSwitchDetails");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(activity,"Please wait.",false);
        String url = ChatApplication.url + Constants.CHECK_INDIVIDUAL_SWITCH_DETAILS;
        JSONObject obj = new JSONObject();
        try {
            *//*obj.put("module_id","1C7FC712004B1200");
            obj.put("device_id","4");*//*
            obj.put("module_id",deviceVO.getModuleId());
            obj.put("device_id",deviceVO.getDeviceId()+"");
            obj.put("room_device_id",deviceVO.getRoomDeviceId()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(activity,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getSwitchDetails onSuccess " + result.toString());

                try {
                    JSONObject obj = result.getJSONObject("data");
                    JSONArray deviceArray = obj.getJSONArray("device_details");
                    deviceObj = deviceArray.getJSONObject(0);
                    JSONArray device_iconsArray = obj.getJSONArray("device_icons");

                    setSpinnerValue(device_iconsArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    setDeviceValue();
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d("", "getSwitchDetails onFailure " + error );
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/
    }

    private void setDeviceValue() {

        try {
            et_switch_name.setText(deviceObj.getString("device_name"));
            int index = flags.indexOf(deviceObj.getString("device_icon"));
            sp_device_type.setSelection(index);
            int device_type = deviceObj.getInt("device_type");
            int device_id = deviceObj.getInt("device_id");

            rg_auto_mode_type.check(device_type == 0 ? R.id.rb_auto_mode_type_normal : R.id.rb_auto_mode_type_dimmer);

            if(device_id == 1){  //device_id //device_type
                ll_auto_mode_type.setVisibility(View.VISIBLE);
                rg_auto_mode_type.setVisibility(View.VISIBLE);
            }
            else{
                ll_auto_mode_type.setVisibility(View.GONE);
                rg_auto_mode_type.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ArrayList<String> flags = new ArrayList<>();

    private void setSpinnerValue(JSONArray device_iconsArray) {

        flags = new ArrayList<String>();
        flags.clear();
        for(int i=0;i<device_iconsArray.length();i++){
            try {
                //Log.d("ICONANME","::::" + device_iconsArray.getJSONObject(i).getString("device_icon_name"));
                flags.add(device_iconsArray.getJSONObject(i).getString("device_icon_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //int flags[] = {R.drawable.bulb_on, R.drawable.fan_on, R.drawable.tv_on, R.drawable.cfl_on, R.drawable.microwave_oven_on};
        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(activity,flags,0,true);
        sp_device_type.setAdapter(customAdapter);

    }
}