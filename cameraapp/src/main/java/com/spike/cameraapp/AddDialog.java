package com.spike.cameraapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kaushal on 27/12/17.
 */

public class AddDialog extends Dialog implements
        View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public Button btn_save;
    public ImageView iv_close;
    EditText et_room_name, et_panel_name, et_module_id;
    Spinner sp_no_of_devices;
    TextView tv_title;
    LinearLayout ll_room;

    public String url = "", name = "";
    int index=-1;
    ICallback iCallback;

    public AddDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    public AddDialog(Activity activity,int index, String url, String name, ICallback iCallback) {
        super(activity);
        this.activity = activity;
        this.url = url;
        this.name = name;
        this.index = index;
        this.iCallback = iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_add);

        ll_room = (LinearLayout) findViewById(R.id.ll_room);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_room_name = (EditText) findViewById(R.id.et_room_name);
        et_panel_name = (EditText) findViewById(R.id.et_panel_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        iv_close = (ImageView) findViewById(R.id.iv_close);


        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        if(url.equalsIgnoreCase("")){
            url="rtsp://";
        }
        et_room_name.setText(url);
        et_panel_name.setText(name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (TextUtils.isEmpty(et_room_name.getText().toString())) {
                    Toast.makeText(activity, "Enter Url", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_panel_name.getText().toString())) {
                    Toast.makeText(activity, "Enter camera name", Toast.LENGTH_SHORT).show();
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

    public void configureNewRoom() {

        Log.d(TAG, "configureNewRoom configureGatewayDevice" + index);

        //ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            obj.put("url", et_room_name.getText().toString());
            obj.put("name", et_panel_name.getText().toString());


            array = Common.getCameraList(activity);
            if(index==-1){
                array.put(obj);
            }
            else{
                array.put(index,obj);
            }

            Common.saveCamera(activity,array);

            iCallback.onSuccess("yes");
            dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}