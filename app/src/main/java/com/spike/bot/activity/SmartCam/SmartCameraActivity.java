package com.spike.bot.activity.SmartCam;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.CameraEdit;
import com.spike.bot.adapter.JetSonAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.JetSonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vipul on 13/1/20.
 * Gmail : vipul patel
 */
public class SmartCameraActivity extends AppCompatActivity implements View.OnClickListener , JetSonAdapter.JetsonAction {

    public Toolbar toolbar;
    ImageView empty_add_image;
    TextView txt_empty_text;
    LinearLayout linearNodataFound;
    public RecyclerView recyclerview;

    public SmartCamAdapter jetSonAdapter;
    public ArrayList<CameraVO> arrayList=new ArrayList<>();

    String jetson_id="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetson);

        setViewId();
    }

    private void setViewId() {
        jetson_id=getIntent().getStringExtra("jetson_id");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Smart Camera");
        recyclerview = findViewById(R.id.recyclerRemoteList);
        linearNodataFound = findViewById(R.id.linearNodataFound);
        empty_add_image = findViewById(R.id.empty_add_image);
        txt_empty_text = findViewById(R.id.txt_empty_text);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);

        txt_empty_text.setText("No Smart Camera Added");
        empty_add_image.setOnClickListener(this);
        txt_empty_text.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        callGetSmarCamera();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == txt_empty_text || v==empty_add_image) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera(false,0);
            }
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
        menuAdd.setVisible(true);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera(false,0);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * dialog enter key camera
     */
    public void addKeyCamera() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_camera_key);

        final TextInputEditText room_name = dialog.findViewById(R.id.edt_room_name);
        room_name.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        room_name.setFilters(filterArray);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCameraKey(room_name, dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * camera key : spike123
     * camera key check is valid or not..
     */
    private void saveCameraKey(EditText roomName, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(SmartCameraActivity.this)) {
            ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        if (TextUtils.isEmpty(roomName.getText().toString().trim())) {
            roomName.setError("Enter key name");
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("key", roomName.getText().toString()); /*key is spike123*/
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(this, "Searching Device attached", false);

        String url = ChatApplication.url + Constants.validatecamerakey;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, "1");
                        ChatApplication.showToast(SmartCameraActivity.this, message);
                        addCamera(false,0);
                    } else if (code == 301) {
                        ChatApplication.showToast(SmartCameraActivity.this, message);
                    } else {
                        ChatApplication.showToast(SmartCameraActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(SmartCameraActivity.this,getResources().getString(R.string.something_wrong1));
            }
        }).execute();
    }

    /**
     * open dialog for add camera
     */
    private void addCamera(Boolean isflag,int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_camera);
        dialog.setCanceledOnTouchOutside(false);

        final TextInputEditText camera_name = dialog.findViewById(R.id.txt_camera_name);
        final TextInputEditText camera_ip = dialog.findViewById(R.id.txt_camera_ip);
        final TextInputEditText video_path = dialog.findViewById(R.id.txt_video_path);
        final TextInputEditText user_name = dialog.findViewById(R.id.txt_user_name);
        final TextInputEditText password = dialog.findViewById(R.id.txt_password);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(camera_name.getText().toString())) {
                    camera_name.requestFocus();
                    camera_name.setError("Enter Camera name");
                    return;
                }
                if (TextUtils.isEmpty(camera_ip.getText().toString())) {
                    camera_ip.requestFocus();
                    camera_ip.setError("Enter Camera IP");
                    return;
                }

                if(!Patterns.IP_ADDRESS.matcher(camera_ip.getText()).matches()){
                    camera_ip.requestFocus();
                    camera_ip.setError("Please enter valid ip address");
                    return;
                }

                if (TextUtils.isEmpty(video_path.getText().toString())) {
                    video_path.requestFocus();
                    video_path.setError("Enter Video Path");
                    return;
                }
                if (TextUtils.isEmpty(user_name.getText().toString())) {
                    user_name.requestFocus();
                    user_name.setError("Enter User Name");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.requestFocus();
                    password.setError("Enter Password");
                    return;
                }

                addCameraCall(isflag,position,camera_name.getText().toString(), camera_ip.getText().toString(), video_path.getText().toString(), user_name.getText().toString(),
                        password.getText().toString(), dialog);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /**
     * @param camera_name : c1
     * @param camera_ip   : 192.168.75.113
     * @param video_path  : /live/streaming
     * @param user_name   : abcd
     * @param password    : 123...
     * @param dialog      : if(response code == 200) dismiss dialog
     */
    private void addCameraCall(boolean isflag,int position,String camera_name, String camera_ip, String video_path, String user_name, String password, final Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(SmartCameraActivity.this)) {
            ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(SmartCameraActivity.this, Constants.USER_ID));
            obj.put("camera_name", camera_name);
            obj.put("camera_ip", camera_ip);

            obj.put("user_name", user_name);
            obj.put("password", password);

//            if(isflag){
//                obj.put("camera_videopath", video_path);
//                obj.put("camera_id",arrayList.get(position).getCamera_id());
//            }else {
                obj.put("video_path", video_path);
                obj.put("jetson_device_id", jetson_id);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url="";
        if(isflag){
            url=ChatApplication.url+Constants.SAVE_EDIT_CAMERA;
        }else {
            url = ChatApplication.url + Constants.ADD_CAMERA;
        }

        ChatApplication.logDisplay("url is "+url+" "+obj);

        new GetJsonTask(SmartCameraActivity.this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(SmartCameraActivity.this, message);
                    if (code == 200) {
                        dialog.dismiss();
                        ChatApplication.keyBoardHideForce(SmartCameraActivity.this);
                        ChatApplication.showToast(SmartCameraActivity.this, message);
                        callGetSmarCamera();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("onFailure " + error);
                ChatApplication.showToast(SmartCameraActivity.this,getResources().getString(R.string.something_wrong1));
            }
        }).execute();

    }


    /*view hide use for getting error*/
    public void showView(boolean isflag){
        recyclerview.setVisibility(isflag ? View.VISIBLE : View.GONE);
        linearNodataFound.setVisibility(isflag ? View.GONE : View.VISIBLE);
    }

    /*call smart camera */
    private void callGetSmarCamera() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            obj.put("jetson_device_id", jetson_id);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.cameralistbyjetson;

        ChatApplication.logDisplay("door sensor" + url + obj);

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");

                    if (code == 200) {
                        showView(true);

                        Gson gson=new Gson();
                        arrayList.clear();

                        JSONObject  object= new JSONObject(String.valueOf(result));
                        JSONArray jsonArray= object.optJSONArray("data");

                        if(jsonArray!=null && jsonArray.length()>0){
                            arrayList = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<CameraVO>>(){}.getType());
                            if(arrayList.size()>0){
                                setAdapter();
                            }else {
                                showView(false);
                            }
                        }else {
                            showView(false);
                        }



                        ChatApplication.logDisplay("response is "+result);
                    }else {
                        showView(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                showView(false);
                ActivityHelper.dismissProgressDialog();

            }
        }).execute();
    }

    /*delete jetson camera*/
    private void deleteJetsoncamera(int position) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("camera_id", arrayList.get(position).getCamera_id());
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url= ChatApplication.url + Constants.DELETE_CAMERA;

        ChatApplication.logDisplay("door sensor " + url + obj);

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");
                    ChatApplication.showToast(SmartCameraActivity.this, message);
                    if (code == 200) {
                        arrayList.remove(position);
                        setAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();

            }
        }).execute();
    }

    /*set adapter*/
    private void setAdapter() {
        jetSonAdapter =new SmartCamAdapter(this,this,arrayList);
        recyclerview.setAdapter(jetSonAdapter);
        jetSonAdapter.notifyDataSetChanged();
    }



    @Override
    public void action(int position, String action) {
        if(action.equals("edit")){
//            addCamera(true,position);
            Intent intent=new Intent(this, CameraEdit.class);
            intent.putExtra("cameraSelcet", arrayList.get(position));
            intent.putExtra("isEditable", false);
            startActivity(intent);

        }else {
            ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteJetsoncamera(position);
                }

                @Override
                public void onConfirmDialogNoClick() {
                }
            });
            newFragment.show(getFragmentManager(), "dialog");
        }

    }
}
