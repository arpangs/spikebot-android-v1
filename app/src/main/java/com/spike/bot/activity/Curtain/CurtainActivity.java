package com.spike.bot.activity.Curtain;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DoorSensorResModel;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 23/9/19.
 * Gmail : vipul patel
 */
public class CurtainActivity extends AppCompatActivity implements View.OnClickListener{

    Socket mSocket;

    Toolbar toolbar;
    ImageView imgClose,imgOpen,imgPause;
    Button btn_delete;
    String curtain_id="",module_id="",curtain_name="",curtain_status="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain);

        module_id=getIntent().getStringExtra("module_id");
        curtain_id=getIntent().getStringExtra("curtain_id");
        curtain_name=getIntent().getStringExtra("curtain_name");
        curtain_status=getIntent().getStringExtra("curtain_status");
        setUIId();
    }

    private void setUIId() {
        startSocketConnection();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(curtain_name);
        imgPause=findViewById(R.id.imgPause);
        imgOpen=findViewById(R.id.imgOpen);
        imgClose=findViewById(R.id.imgClose);
        btn_delete=findViewById(R.id.btn_delete);

        imgClose.setOnClickListener(this);
        imgOpen.setOnClickListener(this);
        imgPause.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        setView();
    }

    private void setView() {
        if(curtain_status.equals("1")){
            setCurtainClick(true,false,false);
        }else if(curtain_status.equals("0")){
            setCurtainClick(false,false,true);
        }else if(curtain_status.equals("-1")){
            setCurtainClick(false,true,false);
        }
    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("updateCurtainStatus", updateCurtainStatus);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionEdit) {
            dialogEditName();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v==imgOpen){
            curtain_status="1";
            updateStatus();
        }else if(v==imgPause){
            curtain_status="0";
            updateStatus();
        }else if(v==imgClose){
            curtain_status="-1";
            updateStatus();
        }else if(v==btn_delete){
            callDailog();
        }
    }

    private Emitter.Listener updateCurtainStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("curatain socket is " + object);
                            String curtain_module_id = object.optString("curtain_module_id");

                            if(curtain_module_id.equals(module_id)){
                                curtain_status = object.optString("curtain_status");
                                setView();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };


    public void setCurtainClick(boolean open,boolean close ,boolean pause){
        imgOpen.setImageResource(open ? R.drawable.open_enabled:R.drawable.open_disabled);
        imgClose.setImageResource(close ? R.drawable.close_enabled:R.drawable.close_disabled);
        imgPause.setImageResource(pause ? R.drawable.puse_enabled:R.drawable.puse_disabled);
    }

    private void dialogEditName() {
        final Dialog dialog = new Dialog(CurtainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = dialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name =  dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName =  dialog.findViewById(R.id.edSensorName);
        txtInputSensor.setVisibility(View.VISIBLE);
        edSensorName.setVisibility(View.VISIBLE);
        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);

        txtInputSensor.setHint("Enter Curtain name");
        tv_title.setText("Enter name");


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(CurtainActivity.this);
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edSensorName.getText().toString().trim().length() == 0) {
                    ChatApplication.showToast(CurtainActivity.this, "Please enter name");
                } else {
                    ChatApplication.keyBoardHideForce(CurtainActivity.this);
                    dialog.dismiss();
                    updateCurtain(edSensorName.getText().toString());
                }
            }
        });

        dialog.show();

    }

    private void callDailog() {
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?" ,new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deletePanel();

            }
            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void deletePanel() {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.curtaindelete;

        JSONObject object = new JSONObject();
        try {
            object.put("curtain_module_id", module_id);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("curtaindelete " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("updateCurtain is " + result);
                if(result.optInt("code")==200){
                    CurtainActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateStatus() {

//        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.curtainupdatestatus;

        ChatApplication.logDisplay("updateCurtain " + url);

        JSONObject object = new JSONObject();
        try {
            //pass curtain_id and curtain_name
            object.put("curtain_module_id", module_id);
            object.put("curtain_status", Integer.parseInt(curtain_status));
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("door " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                if(result.optInt("code")==200){

                    if(curtain_status.equals("1")){
                        setCurtainClick(true,false,false);
                    }else if(curtain_status.equals("0")){
                        setCurtainClick(false,false,true);
                    }else if(curtain_status.equals("-1")){
                        setCurtainClick(false,true,false);
                    }
                }
                ChatApplication.logDisplay("updateCurtain is " + result);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateCurtain(String name) {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.curtainupdate;

        ChatApplication.logDisplay("updateCurtain " + url);

        JSONObject object = new JSONObject();
        try {
            //pass curtain_id and curtain_name
            object.put("curtain_id", curtain_id);
            object.put("curtain_name", name);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("door " + url + " " + object);

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                if(result.optInt("code")==200){
                    toolbar.setTitle(name);
                    ChatApplication.showToast(CurtainActivity.this,result.optString("message"));
                }
                ChatApplication.logDisplay("updateCurtain is " + result);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



}
