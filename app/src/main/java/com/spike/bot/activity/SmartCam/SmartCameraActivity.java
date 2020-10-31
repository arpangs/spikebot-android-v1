package com.spike.bot.activity.SmartCam;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.CameraEdit;
import com.spike.bot.adapter.JetSonAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.model.CameraVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vipul on 13/1/20.
 * Gmail : vipul patel
 */
public class SmartCameraActivity extends AppCompatActivity implements View.OnClickListener, JetSonAdapter.JetsonAction {

    public Toolbar toolbar;
    public RecyclerView recyclerview;
    public SmartCamAdapter jetSonAdapter;
    public ArrayList<CameraVO> arrayList = new ArrayList<>();
    ImageView empty_add_image;
    TextView txt_empty_text;
    LinearLayout linearNodataFound;
    String jetson_id = "", confidence_score_day = "40", confidence_score_night = "28";
    private TextView txt_daythreashvalue, txt_nightthreashvalue;
    private SeekBar sb_daythresh, sb_nightthresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetson);

        setViewId();
    }

    private void setViewId() {
        jetson_id = getIntent().getStringExtra("jetson_id");
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
        if (v == txt_empty_text || v == empty_add_image) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera(false, 0);
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
        MenuItem menuaddtext = menu.findItem(R.id.action_add_text);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        menuaddtext.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            if (Common.getPrefValue(this, Common.camera_key).equalsIgnoreCase("0")) {
                addKeyCamera();
            } else {
                addCamera(false, 0);
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

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText("Enter key");
        room_name.setText("");

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

        ActivityHelper.showProgressDialog(this, "Searching Device attached", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveJetsonCameraKey(roomName.getText().toString(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {
                        dialog.dismiss();
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, "1");
                        ChatApplication.showToast(SmartCameraActivity.this, message);
                        addCamera(false, 0);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });
    }

    /**
     * open dialog for add camera
     */
    private void addCamera(Boolean isflag, int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_camera);
        dialog.setCanceledOnTouchOutside(false);

        final AppCompatEditText camera_name = dialog.findViewById(R.id.txt_camera_name);
        final AppCompatEditText camera_ip = dialog.findViewById(R.id.txt_camera_ip);
        final AppCompatEditText video_path = dialog.findViewById(R.id.txt_video_path);
        final AppCompatEditText user_name = dialog.findViewById(R.id.txt_user_name);
        final AppCompatEditText password = dialog.findViewById(R.id.txt_password);
        ImageView img_passcode = dialog.findViewById(R.id.img_show_passcode);
        sb_daythresh = dialog.findViewById(R.id.sb_daythresh);
        sb_nightthresh = dialog.findViewById(R.id.sb_nightthresh);

        txt_daythreashvalue = dialog.findViewById(R.id.txt_daythreashvalue);
        txt_nightthreashvalue = dialog.findViewById(R.id.txt_nightthreashvalue);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        //      Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        img_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    img_passcode.setImageResource(R.drawable.eyeclosed);
                    //Show Password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    img_passcode.setImageResource(R.drawable.eye);
                    //Hide Password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        sb_daythresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                confidence_score_day = String.valueOf(i);
                txt_daythreashvalue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_nightthresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                confidence_score_night = String.valueOf(i);
                txt_nightthreashvalue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       /* btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });*/

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

                /*remove validation on 16 sep 2020 due to rtsp url and ios has no validation for same-*/
                /*if(!Patterns.IP_ADDRESS.matcher(camera_ip.getText().toString()).matches()){
                    camera_ip.requestFocus();
                    camera_ip.setError("Please enter valid ip address");
                    return;
                }*/

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

                addCameraCall(isflag, position, camera_name.getText().toString(), camera_ip.getText().toString(), video_path.getText().toString(), user_name.getText().toString(),
                        password.getText().toString(), confidence_score_day, confidence_score_night, dialog);
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
    private void addCameraCall(boolean isflag, int position, String camera_name, String camera_ip, String video_path, String user_name, String password, String confidence_score_day, String confidence_score_night, Dialog dialog) {

        if (!ActivityHelper.isConnectingToInternet(SmartCameraActivity.this)) {
            ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.disconnect));
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addCameraCall(camera_name, camera_ip, user_name, password, confidence_score_day, confidence_score_night, video_path, jetson_id, isflag,
                new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {
                        ActivityHelper.dismissProgressDialog();

                        try {
                            JSONObject result = new JSONObject(stringResponse);
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
                    public void onData_FailureResponse() {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.something_wrong1));
                    }

                    @Override
                    public void onData_FailureResponse_with_Message(String error) {
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.showToast(SmartCameraActivity.this, getResources().getString(R.string.something_wrong1));
                    }
                });
    }


    /*view hide use for getting error*/
    public void showView(boolean isflag) {
        recyclerview.setVisibility(isflag ? View.VISIBLE : View.GONE);
        linearNodataFound.setVisibility(isflag ? View.GONE : View.VISIBLE);
    }

    /*call smart camera */
    private void callGetSmarCamera() {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().callGetSmartCamera(jetson_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is " + result);
                    String message = result.getString("message");

                    if (code == 200) {
                        showView(true);

                        Gson gson = new Gson();
                        arrayList.clear();

                        JSONObject object = new JSONObject(String.valueOf(result));
                        JSONArray jsonArray = object.optJSONArray("data");

                        if (jsonArray != null && jsonArray.length() > 0) {
                            arrayList = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<CameraVO>>() {
                            }.getType());
                            if (arrayList.size() > 0) {
                                setAdapter();
                            } else {
                                showView(false);
                            }
                        } else {
                            showView(false);
                        }

                        ChatApplication.logDisplay("response is " + result);
                    } else {
                        showView(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                showView(false);
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                showView(false);
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*delete jetson camera*/
    private void deleteJetsoncamera(int position) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteJetsoncamera(arrayList.get(position).getCamera_id(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is " + result);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*set adapter*/
    private void setAdapter() {
        jetSonAdapter = new SmartCamAdapter(this, this, arrayList);
        recyclerview.setAdapter(jetSonAdapter);
        jetSonAdapter.notifyDataSetChanged();
    }


    @Override
    public void action(int position, String action) {
        if (action.equals("edit")) {
//            addCamera(true,position);

            showBottomSheetDialog(position);

        }

    }

    public void showBottomSheetDialog(int position) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(SmartCameraActivity.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + arrayList.get(position).getCamera_name() + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(SmartCameraActivity.this, CameraEdit.class);
                intent.putExtra("cameraSelcet", arrayList.get(position));
                intent.putExtra("isEditable", false);
                intent.putExtra("isjetsonedit", true);
                startActivity(intent);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
        });
    }
}
