package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Constants;
import com.spike.bot.R;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kaushal on 27/12/17.
 */

public class AddRoomDialog extends Dialog implements
        View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public Button btn_save;
    public ImageView iv_close;
    EditText et_room_name,et_panel_name,et_module_id;
    Spinner sp_no_of_devices;
    TextView tv_title;
    LinearLayout ll_room;

    public String module_id="", device_id="",room_id="",room_name ="",module_type ="";
    boolean isRoom=false;
    ICallback iCallback;
    public AddRoomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    public AddRoomDialog(Activity activity,String room_id,String room_name, String module_id, String device_id,String module_type, ICallback iCallback) {
        super(activity);
        this.activity = activity;
        this.module_id = module_id;
        this.device_id=device_id;
        this.room_id= room_id;
        this.room_name = room_name;
        this.module_type = module_type;
        if(room_id.equalsIgnoreCase("")){
            isRoom = true;
        }
        else{
            isRoom = false;
        }
        this.iCallback=iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_add_room);

        ChatApplication.isOpenDialog = true;

        ll_room = (LinearLayout) findViewById(R.id.ll_room);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_room_name = (EditText) findViewById(R.id.et_room_name);
        et_panel_name = (EditText) findViewById(R.id.et_panel_name);
        et_module_id = (EditText) findViewById(R.id.et_module_id);
        sp_no_of_devices = (Spinner) findViewById(R.id.sp_no_of_devices);
        btn_save = (Button) findViewById(R.id.btn_save);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        et_module_id.setText(module_id);

        sp_no_of_devices.setSelection(Integer.parseInt(device_id)-1);
        sp_no_of_devices.setEnabled(false);

        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        if(!isRoom){
         et_room_name.setText(room_name);
            tv_title.setText("Add Panel");
            ll_room.setVisibility(View.GONE);
        }
        else {
            tv_title.setText("Add Room");
            ll_room.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if(TextUtils.isEmpty(et_room_name.getText().toString())){
                    Toast.makeText(activity,  "Enter Room name" ,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(et_panel_name.getText().toString())){
                    Toast.makeText(activity,  "Enter Panel name" ,Toast.LENGTH_SHORT ).show();
                    return;
                }
                configureNewRoom();

                break;
            case R.id.iv_close:
                iCallback.onSuccess("no");
                dismiss();
                break;

            default:
                break;
        }
    }
    String TAG = "AddRoom";
    public void configureNewRoom(){

        Log.d(TAG, "configureNewRoom configureGatewayDevice");
        if(!ActivityHelper.isConnectingToInternet(activity)){
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {
            if(!isRoom){
                obj.put("room_id",room_id);
            }
            obj.put("room_name",et_room_name.getText().toString());
            obj.put("panel_name",et_panel_name.getText().toString());
            obj.put("module_id",et_module_id.getText().toString());
            obj.put("device_id",sp_no_of_devices.getSelectedItem().toString());
            obj.put("module_type",module_type);

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomEDIT","obj : " + obj.toString());

        String url = ChatApplication.url + Constants.CONFIGURE_NEWROOM;
        if(isRoom){
            url = ChatApplication.url + Constants.CONFIGURE_NEWROOM;
        }
        else{
            url = ChatApplication.url + Constants.CONFIGURE_NEW_PANEL;
        }
        new GetJsonTask(activity,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "configureNewRoom onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(activity.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
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
                Log.d(TAG, "configureNewRoom onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

}