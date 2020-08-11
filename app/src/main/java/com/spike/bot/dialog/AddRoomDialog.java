package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kaushal on 27/12/17.
 */

public class AddRoomDialog extends Dialog implements
        View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public Button btn_save;
    public ImageView iv_close, sp_drop_down;
    public String module_id = "", device_id = "", room_id = "", module_type = "";
    EditText et_panel_name, et_module_id;
    Spinner sp_no_of_devices, spinnerroomlist;
    TextView tv_title;
    LinearLayout ll_room;
    ArrayList<String> roomidlist;
    ArrayList<String> roomnamelist;
    boolean isRoom = false;
    ICallback iCallback;
    String TAG = "AddRoom";

    public AddRoomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
    }

    public AddRoomDialog(Activity activity, ArrayList<String> roomidlist1, ArrayList<String> roomnamelist1, String module_id, String device_id, String module_type, ICallback iCallback) {
        super(activity);
        this.activity = activity;
        this.module_id = module_id;
        this.device_id = device_id;
        this.roomidlist = roomidlist1;
        this.roomnamelist = roomnamelist1;
        this.module_type = module_type;
        this.iCallback = iCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_add_room);

        ChatApplication.isOpenDialog = true;

        ll_room = findViewById(R.id.ll_room);
        tv_title = findViewById(R.id.tv_title);
        et_panel_name = findViewById(R.id.et_panel_name);
        et_module_id = findViewById(R.id.et_module_id);
        sp_no_of_devices = findViewById(R.id.sp_no_of_devices);
        sp_drop_down = findViewById(R.id.sp_drop_down);
        spinnerroomlist = findViewById(R.id.sp_no_of_rooms);
        btn_save = findViewById(R.id.btn_save);
        iv_close = findViewById(R.id.iv_close);
        et_module_id.setText(module_id);


        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getContext(), roomnamelist, 1, false);
        spinnerroomlist.setAdapter(customAdapter);

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerroomlist.performClick();
            }
        });

        sp_no_of_devices.setSelection(Integer.parseInt(device_id) - 1);
        sp_no_of_devices.setEnabled(false);

        btn_save.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        tv_title.setText("Add Panel");
        ll_room.setVisibility(View.VISIBLE);


        spinnerroomlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room_id = roomidlist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:

                if (TextUtils.isEmpty(et_panel_name.getText().toString())) {
                    Toast.makeText(activity, "Enter Panel name", Toast.LENGTH_SHORT).show();
                    return;
                }
                configureNewRoom();

                break;
            case R.id.iv_close:
                iCallback.onSuccess("no");
                dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;

            default:
                break;
        }
    }

    public void configureNewRoom() {

        if (!ActivityHelper.isConnectingToInternet(activity)) {
            Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(activity, "Please wait.", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().ConfigureNewRoom(room_id, et_panel_name.getText().toString(), et_module_id.getText().toString()
                , module_type, isRoom, new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {

                        try {
                            JSONObject result = new JSONObject(stringResponse);
                            //{"code":200,"message":"success"}
                            int code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {
                                Common.hideSoftKeyboard(activity);
                                if (!TextUtils.isEmpty(message)) {
                                    Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }

                                ActivityHelper.dismissProgressDialog();
                                dismiss();
                                iCallback.onSuccess("yes");
                            } else {
                                Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ActivityHelper.dismissProgressDialog();

                        }

                    }

                    @Override
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                        Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ActivityHelper.dismissProgressDialog();
                        Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                    }
                });


    }

}
