package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by Sagar on 27/3/18.
 * Gmail : jethvasagar2@gmail.com
 */


public class CameraEdit extends AppCompatActivity{

    public boolean isEditable=false;
    private ArrayList<CameraVO> cameraVOArrayList;
    private CameraVO cameraSelcet;
    ArrayList<String> cameraStr;
    ArrayList<String> cameraId;

    private Spinner sp_camera_list;
    private ImageView sp_drop_down;

    private EditText edt_camera_name;
    private EditText edt_camera_ip;
    private EditText edt_video_path;
    private EditText edt_user_name;
    private TextInputEditText edt_user_password;

    private Button btnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_camera);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

       // room = (RoomVO )getIntent().getSerializableExtra("room");
        isEditable = getIntent().getBooleanExtra("isEditable",false);
        cameraVOArrayList = (ArrayList<CameraVO>) getIntent().getExtras().getSerializable("cameraList");
        cameraSelcet = (CameraVO) getIntent().getExtras().getSerializable("cameraSelcet");

        setTitle("Camera");

        sp_camera_list = (Spinner) findViewById(R.id.sp_camera_list);
        sp_drop_down = (ImageView) findViewById(R.id.sp_drop_down);

        btnDelete = (Button) findViewById(R.id.btnDelete);

        edt_camera_name = (EditText) findViewById(R.id.edt_camera_name);
        edt_camera_ip = (EditText) findViewById(R.id.edt_camera_ip);
        edt_video_path = (EditText) findViewById(R.id.edt_video_path);
        edt_user_name = (EditText) findViewById(R.id.edt_user_name);
        edt_user_password = (TextInputEditText) findViewById(R.id.edt_password);

        cameraStr = new ArrayList<>();
        cameraStr.clear();

        cameraId = new ArrayList<>();
        cameraId.clear();

//        if(isEditable){
            sp_camera_list.setEnabled(false);
        sp_drop_down.setEnabled(false);
            sp_camera_list.setClickable(false);
            sp_drop_down.setClickable(false);
//        }

        if(!cameraVOArrayList.isEmpty()){
            for(CameraVO cameraVO :cameraVOArrayList){
                cameraStr.add(cameraVO.getCamera_name());
                cameraId.add(cameraVO.getCamera_id());
            }
        }

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this,cameraStr,1,false);
        sp_camera_list.setAdapter(customAdapter);

        int spinner_position = sp_camera_list.getSelectedItemPosition();

//        initUI(cameraStr.get(spinner_position));
        initUI(cameraSelcet.getCamera_name());

        for(int i=0; i<cameraId.size(); i++){
            if(cameraId.get(i).equalsIgnoreCase(cameraSelcet.getCamera_id())){
                sp_camera_list.setSelection(i);
                break;
            }
        }

        sp_camera_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  initUI(cameraStr.get(position));
                getCameraInfo(cameraId.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_camera_list.performClick();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    /**
     * getCameraInfo by camera_id
     */

    private void getCameraInfo(String camera_id){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl +Constants.GET_CAMERA_INFO;

        JSONObject object = new JSONObject();
        try {
            object.put("camera_id",camera_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(CameraEdit.this,"Please wait...",false);

        new GetJsonTask(this,url ,"POST",object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
                        JSONArray array = result.getJSONArray("data");

                        JSONObject deviceObj = array.getJSONObject(0);

                        String camera_id = deviceObj.getString("camera_id");
                        String user_id = deviceObj.getString("user_id");
                        String home_controller_device_id = deviceObj.getString("home_controller_device_id");
                        String camera_name = deviceObj.getString("camera_name");

                        String camera_ip = deviceObj.getString("camera_ip");
                        String camera_videopath = deviceObj.getString("camera_videopath");
                        String camera_icon = deviceObj.getString("camera_icon");

                        String userName = deviceObj.getString("user_name");
                        String password = deviceObj.getString("password");
                        int is_active  = deviceObj.getInt("is_active");

                        CameraVO cameraVO = new CameraVO();
                        cameraVO.setCamera_id(camera_id);
                        cameraVO.setUserId(user_id);
                        cameraVO.setHomeControllerDeviceId(home_controller_device_id);
                        cameraVO.setCamera_name(camera_name);
                        cameraVO.setCamera_ip(camera_ip);
                        cameraVO.setCamera_videopath(camera_videopath);
                        cameraVO.setUserName(userName);
                        cameraVO.setPassword(password);
                        cameraVO.setIsActive(is_active);
                        cameraVO.setCamera_icon(camera_icon);

                        initUI(cameraVO);
                    }

                    if(code!=200){
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }

    private void initUI(String cameraName){

        for(CameraVO cameraVO : cameraVOArrayList){
            if(cameraVO.getCamera_name().equalsIgnoreCase(cameraName)){

                edt_camera_name.setText(cameraVO.getCamera_name());
                edt_camera_ip.setText(cameraVO.getCamera_ip());
                edt_video_path.setText(cameraVO.getCamera_videopath());
                edt_user_name.setText(cameraVO.getUserName());
                edt_user_password.setText(cameraVO.getPassword());

            }
        }

    }

    private void initUI(CameraVO cameraVO){

        edt_camera_name.setText(cameraVO.getCamera_name());
        edt_camera_ip.setText(cameraVO.getCamera_ip());
        edt_video_path.setText(cameraVO.getCamera_videopath());
        edt_user_name.setText(cameraVO.getUserName());
        edt_user_password.setText(cameraVO.getPassword());

    }

    /**
     * Delete selected camera
     */

    private void showDialog(){
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure ?",new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {

                int spinner_position = sp_camera_list.getSelectedItemPosition();
                deleteCamera(cameraId.get(spinner_position).toString());
            }
            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     *  DeleteCamera
     */

    private void deleteCamera(String camera_id){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl+Constants.DELETE_CAMERA;

        JSONObject object = new JSONObject();

        try {
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("camera_id",camera_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(CameraEdit.this,"Please wait...",false);

        new GetJsonTask(this,url ,"POST",object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        ChatApplication.isMainFragmentNeedResume = true;
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        //if deletre success return initUI with 0 Index position
                       if(cameraVOArrayList.size()>0){
                         //  initUI(cameraVOArrayList.get(0).getCamera_id());
                       }else{
                         //  finish();
                       }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        menuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            return true;
        }
        else if (id == R.id.action_save) {
            updateCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String webUrl = "";
    private Socket mSocket;

    /**
     *
     * @param ipAddress
     * @return
     */

    private boolean isValidateIP(String ipAddress){
        return Patterns.IP_ADDRESS.matcher(ipAddress).matches();
    }

    private void updateCamera(){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(edt_camera_name.getText().toString().trim())){
            edt_camera_name.setError("Enter Camera name");
            edt_camera_name.requestFocus();
           // Toast.makeText(getApplicationContext(),"Enter Camera name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_camera_ip.getText().toString().trim())){
            edt_camera_ip.setError("Enter Camera IP");
            edt_camera_ip.requestFocus();
           // Toast.makeText(getApplicationContext(),"Enter Camera IP",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_video_path.getText().toString().trim())){
            edt_video_path.setError("Enter Camera Video Path");
            edt_video_path.requestFocus();
          //  Toast.makeText(getApplicationContext(),"Enter Camera Video Path",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_user_name.getText().toString().trim())){
            edt_user_name.setError("Enter User Name");
            edt_user_name.requestFocus();
          //  Toast.makeText(getApplicationContext(),"Enter User Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edt_user_password.getText().toString().trim())){
            edt_user_password.setError("Enter Password name");
            edt_user_password.requestFocus();
            //Toast.makeText(getApplicationContext(),"Enter Password name",Toast.LENGTH_SHORT).show();
            return;
        }
       /* if(!ActivityHelper.isValidUrl(edt_camera_ip.getText().toString())){
            edt_camera_ip.setError("Invalid Camera IP Address");
            edt_camera_ip.requestFocus();
          //  Toast.makeText(getApplicationContext(),"Invalid Camera IP Address",Toast.LENGTH_SHORT).show();
            return;
        }*/

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            int spinner_position = sp_camera_list.getSelectedItemPosition();

            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("camera_id",cameraId.get(spinner_position));
            obj.put("camera_name", edt_camera_name.getText().toString().trim());
            obj.put("camera_ip", edt_camera_ip.getText().toString().trim());
            obj.put("camera_icon", "camera");
            obj.put("camera_videopath", edt_video_path.getText().toString().trim());
            obj.put("user_name", edt_user_name.getText().toString().trim());
            obj.put("password", edt_user_password.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl +  Constants.SAVE_EDIT_CAMERA;

        ActivityHelper.showProgressDialog(CameraEdit.this,"Please wait...",false);

        new GetJsonTask(this,url ,"POST",obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        ChatApplication.isMainFragmentNeedResume = true;
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }


}
