package com.spike.bot.activity.Camera;

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

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by Sagar on 27/3/18.
 * Gmail : jethvasagar2@gmail.com
 */


public class CameraEdit extends AppCompatActivity {

    public boolean isEditable = false;
    private CameraVO cameraSelcet;

    private Spinner sp_camera_list;
    private ImageView sp_drop_down;

    private EditText edt_camera_name;
    private EditText edt_camera_ip;
    private EditText edt_video_path;
    private EditText edt_user_name;
    private TextInputEditText edt_user_password;

    private Button btnDelete;
    String webUrl = "";
    private Socket mSocket;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_edit);

        Toolbar toolbar = findViewById(R.id.toolbar_camera);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        isEditable = getIntent().getBooleanExtra("isEditable", false);
        cameraSelcet = (CameraVO) getIntent().getExtras().getSerializable("cameraSelcet");

        setTitle("Camera");

        sp_camera_list = findViewById(R.id.sp_camera_list);
        sp_drop_down = findViewById(R.id.sp_drop_down);

        btnDelete = findViewById(R.id.btnDelete);

        edt_camera_name = findViewById(R.id.edt_camera_name);
        edt_camera_ip = findViewById(R.id.edt_camera_ip);
        edt_video_path = findViewById(R.id.edt_video_path);
        edt_user_name = findViewById(R.id.edt_user_name);
        edt_user_password = findViewById(R.id.edt_password);

        sp_camera_list.setEnabled(false);
        sp_drop_down.setEnabled(false);
        sp_camera_list.setClickable(false);
        sp_drop_down.setClickable(false);

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

        initUI();
    }

    /*set data */
    private void initUI() {
        edt_camera_name.setText(cameraSelcet.getCamera_name());
        edt_camera_ip.setText(cameraSelcet.getCamera_ip());
        edt_video_path.setText(cameraSelcet.getCamera_videopath());
        if (TextUtils.isEmpty(cameraSelcet.getUserName())) {
            edt_user_name.setText(cameraSelcet.getUser_name());
        } else {
            edt_user_name.setText(cameraSelcet.getUserName());
        }

        edt_user_password.setText(cameraSelcet.getPassword());
    }

    /**
     * Delete selected camera
     */

    private void showDialog() {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure ?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteCamera(cameraSelcet.getCamera_id().toString());
            }

            @Override
            public void onConfirmDialogNoClick() {
            }

        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * DeleteCamera
     */

    private void deleteCamera(String camera_id) {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl + Constants.DELETE_CAMERA;

        JSONObject object = new JSONObject();

        try {
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("camera_id", camera_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(CameraEdit.this, "Please wait...", false);

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
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
        if (id == R.id.action_add) {

            return true;
        } else if (id == R.id.action_save) {
            updateCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*camera update like name ,ip , path , username*/
    private void updateCamera() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(edt_camera_name.getText().toString().trim())) {
            edt_camera_name.setError("Enter Camera name");
            edt_camera_name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edt_camera_ip.getText().toString().trim())) {
            edt_camera_ip.setError("Enter Camera IP");
            edt_camera_ip.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edt_video_path.getText().toString().trim())) {
            edt_video_path.setError("Enter Camera Video Path");
            edt_video_path.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edt_user_name.getText().toString().trim())) {
            edt_user_name.setError("Enter User Name");
            edt_user_name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edt_user_password.getText().toString().trim())) {
            edt_user_password.setError("Enter Password name");
            edt_user_password.requestFocus();
            return;
        }

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

            int spinner_position = sp_camera_list.getSelectedItemPosition();

            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("camera_id", cameraSelcet.getCamera_id());
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

        String url = webUrl + Constants.SAVE_EDIT_CAMERA;
        ChatApplication.logDisplay("url is " + url + " " + obj);

        ActivityHelper.showProgressDialog(CameraEdit.this, "Please wait...", false);

        new GetJsonTask(this, url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.isMainFragmentNeedResume = true;
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
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
