package com.spike.bot.activity.Camera;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.AddCameraAdapter;
import com.spike.bot.adapter.AlertAdapter;
import com.spike.bot.adapter.filter.CameraNotificationAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.customview.DrawableClickListener;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.dialog.TimePickerFragment;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.listener.UpdateCameraAlert;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraPushLog;
import com.spike.bot.model.CameraVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sagar on 26/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraNotificationActivity extends AppCompatActivity implements SelectCamera, UpdateCameraAlert, View.OnClickListener {

    public static boolean jetsoncameranotification = false;
    public Toolbar toolbar;
    public RecyclerView recyclerView, recyclerAlert;
    public TextView txtAlertCount;
    public CameraNotificationAdapter cameraNotificationAdapter;
    public AlertAdapter alertAdapter;
    public TextView txtSDevice, txt_empty_notification, txtEmptyAlert;
    public ImageView view_rel_badge;
    public int countCamera = 0;
    ArrayList<CameraVO> getCameraList = new ArrayList<>();
    ArrayList<CameraAlertList> arrayListLog = new ArrayList<>();
    ArrayList<CameraPushLog> arrayListAlert = new ArrayList<>();
    ArrayList<CameraVO> cameraVOArrayList = new ArrayList<>();
    Dialog dialog;
    View view_starttime, view_header;
    private String homecontroller_id = "", jetson_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_notification);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setUi();

    }

    private void setUi() {
        toolbar.setTitle("Camera Notifications");
        cameraVOArrayList = (ArrayList<CameraVO>) getIntent().getExtras().getSerializable("cameraList");
        homecontroller_id = getIntent().getStringExtra("homecontrollerId");
        jetson_id = getIntent().getStringExtra("jetson_device_id");
        jetsoncameranotification = getIntent().getExtras().getBoolean("jetsoncameraNotification");
        recyclerView = (RecyclerView) findViewById(R.id.sensor_list);
        recyclerAlert = (RecyclerView) findViewById(R.id.recyclerAlert);
        txtAlertCount = (TextView) findViewById(R.id.txtAlertCount);
        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification);
        txtEmptyAlert = (TextView) findViewById(R.id.txtEmptyAlert);
        view_rel_badge = (ImageView) findViewById(R.id.view_rel_badge);
        view_header = findViewById(R.id.view_header);

        view_rel_badge.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        getconfigureData();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == view_rel_badge) {
            callupdateUnReadCameraLogs(true);
            callCameraUnseenLog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        menuAdd.setVisible(false);
        MenuItem menuAddSave = menu.findItem(R.id.action_save);
        menuAddSave.setTitle("Add");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            addCameraNew("add", null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* add camera alert notification*/
    private void addCameraNew(final String strFlag, final CameraAlertList cameraAlertList) {
        dialog = new Dialog(CameraNotificationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_new_camera);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        final CustomEditText et_schedule_on_time = (CustomEditText) dialog.findViewById(R.id.et_schedule_on_time);
        final CustomEditText et_schedule_off_time = (CustomEditText) dialog.findViewById(R.id.et_schedule_off_time);
        final CustomEditText edIntervalTime = (CustomEditText) dialog.findViewById(R.id.edIntervalTime);
        txtSDevice = (TextView) dialog.findViewById(R.id.txtSDevice);
        RecyclerView recyclerCamera = (RecyclerView) dialog.findViewById(R.id.recyclerCamera);
        TextView txtSave = (TextView) dialog.findViewById(R.id.txtSave);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        if (strFlag.equalsIgnoreCase("update")) {
            txtSave.setText("Update");
            tv_title.setText("Update Alert");

            edIntervalTime.setText("" + cameraAlertList.getAlert_interval());
            try {
                et_schedule_on_time.setText("" + DateHelper.formateDate(DateHelper.parseTimeSimple(cameraAlertList.getStartTime(),
                        DateHelper.DATE_FROMATE_HH_MM_TEMP), DateHelper.DATE_FROMATE_H_M_AMPM));


                et_schedule_off_time.setText("" + DateHelper.formateDate(DateHelper.parseTimeSimple(cameraAlertList.getEndTime(),
                        DateHelper.DATE_FROMATE_HH_MM_TEMP), DateHelper.DATE_FROMATE_H_M_AMPM));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (cameraAlertList.getCameraIds() != null && cameraAlertList.getCameraIds().length() > 0) {
                List<String> myList = new ArrayList<String>(Arrays.asList(cameraAlertList.getCameraIds().split(",")));

                if (myList.size() > 0) {
                    countCamera = 0;
                    for (int i = 0; i < getCameraList.size(); i++) {
                        getCameraList.get(i).setIsSelect(false);
                    }

                    for (int j = 0; j < myList.size(); j++) {
                        for (int i = 0; i < getCameraList.size(); i++) {
                            if (getCameraList.get(i).getCamera_id().equalsIgnoreCase(myList.get(j))) {
                                getCameraList.get(i).setIsSelect(true);
                                countCamera++;
                            }
                        }
                    }
                    txtSDevice.setText("(" + countCamera + " Selected)");
                }

            }

        } else {
            txtSave.setText("Save");
            for (int i = 0; i < getCameraList.size(); i++) {
                getCameraList.get(i).setIsSelect(false);
            }
        }

        edIntervalTime.setSelection(edIntervalTime.getText().length());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CameraNotificationActivity.this, Constants.SWITCH_NUMBER);
        recyclerCamera.setLayoutManager(gridLayoutManager);

        AddCameraAdapter addCameraAdapter = new AddCameraAdapter(CameraNotificationActivity.this, getCameraList, this);
        recyclerCamera.setAdapter(addCameraAdapter);


        et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        et_schedule_on_time.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:

                        et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        et_schedule_on_time.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
        et_schedule_on_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_schedule_on_time.length() > 0) {
                    et_schedule_on_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    et_schedule_on_time.setCompoundDrawablePadding(8);
                }
            }
        });

        et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        et_schedule_off_time.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        et_schedule_off_time.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
        et_schedule_off_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_schedule_off_time.length() > 0) {
                    et_schedule_off_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_close, 0);
                    et_schedule_off_time.setCompoundDrawablePadding(8);
                }
            }
        });


        et_schedule_off_time.setOnKeyListener(null);
        et_schedule_off_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.hideKeyboard(CameraNotificationActivity.this);
                DialogFragment fromTimeFragment = new TimePickerFragment(CameraNotificationActivity.this, et_schedule_off_time.getText().toString(), new ICallback() {
                    @Override
                    public void onSuccess(String str) {
                        et_schedule_off_time.setText(str);

                    }
                });
                if (!fromTimeFragment.isVisible()) {
                    fromTimeFragment.show(CameraNotificationActivity.this.getFragmentManager(), "timePicker");
                }
            }
        });

        et_schedule_on_time.setOnKeyListener(null);
        et_schedule_on_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityHelper.hideKeyboard(CameraNotificationActivity.this);
                DialogFragment fromTimeFragment = new TimePickerFragment(CameraNotificationActivity.this, et_schedule_on_time.getText().toString(), new ICallback() {
                    @Override
                    public void onSuccess(String str) {
                        et_schedule_on_time.setText(str);

                    }
                });
                //TODO <code></code>
                if (!fromTimeFragment.isVisible()) {
                    fromTimeFragment.show(CameraNotificationActivity.this.getFragmentManager(), "timePicker");
                }
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_schedule_on_time.getText().toString().length() == 0) {
                    Toast.makeText(CameraNotificationActivity.this, "Please enter start time", Toast.LENGTH_SHORT).show();
                } else if (et_schedule_off_time.getText().toString().length() == 0) {
                    Toast.makeText(CameraNotificationActivity.this, "Please enter end time", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(et_schedule_on_time.getText().toString()) && !TextUtils.isEmpty(et_schedule_off_time.getText().toString()) &&
                        et_schedule_on_time.getText().toString().equalsIgnoreCase(et_schedule_off_time.getText().toString())) {
                    Toast.makeText(CameraNotificationActivity.this, "Please select different time", Toast.LENGTH_SHORT).show();
                } else if (edIntervalTime.getText().toString().length() == 0) {
                    Toast.makeText(CameraNotificationActivity.this, "Please enter interval time", Toast.LENGTH_SHORT).show();
                } else if (countCamera == 0) {
                    Toast.makeText(CameraNotificationActivity.this, "Select atleast one or more camera", Toast.LENGTH_SHORT).show();
                } else {

                    if (strFlag.equalsIgnoreCase("update")) {
                        callUpdateCamera(et_schedule_on_time.getText().toString(), et_schedule_off_time.getText().toString(), strFlag, cameraAlertList, Integer.parseInt(edIntervalTime.getText().toString()));
                    } else {
                        callAddCamera(et_schedule_on_time.getText().toString(), et_schedule_off_time.getText().toString(), strFlag, cameraAlertList, Integer.parseInt(edIntervalTime.getText().toString()));
                    }

                }
            }
        });

        dialog.show();

    }

    /**
     * Add camera
     */
    private void callAddCamera(String et_schedule_on_time, String et_schedule_off_time, String strflag, CameraAlertList cameraAlertList, int edIntervalTime) {

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().callAddCamera(et_schedule_on_time, et_schedule_off_time, edIntervalTime, getCameraList, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getconfigureData();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Call update camera
    private void callUpdateCamera(String et_schedule_on_time, String et_schedule_off_time, String strflag, CameraAlertList cameraAlertList, int edIntervalTime) {
        JsonArray roomDeviceArray = new JsonArray();
        JsonArray array = new JsonArray();
        for (CameraVO dPanel : getCameraList) {

            if (dPanel.getIsSelect()) {
                JsonObject object = new JsonObject();
                //  try {
                object.addProperty("camera_id", dPanel.getCamera_id());
                object.addProperty("camera_name", "" + dPanel.getCamera_name());
                object.addProperty("camera_url", "" + dPanel.getCamera_url());
                object.addProperty("camera_ip", "" + dPanel.getCamera_ip());
                object.addProperty("camera_videopath", "" + dPanel.getCamera_videopath());
                object.addProperty("camera_icon", "" + dPanel.getCamera_icon());
                object.addProperty("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                object.addProperty("user_name", "" + dPanel.getUser_name());
                object.addProperty("password", "" + dPanel.getPassword());

                roomDeviceArray.add(object);

                array.add(dPanel.getCamera_id());
               /* } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }



        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        SpikeBotApi.getInstance().callUpdateCamera(et_schedule_on_time, et_schedule_off_time, cameraAlertList, jetson_id, edIntervalTime, getCameraList,array,roomDeviceArray,new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getconfigureData();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*add schudule & update*/
    public void addSchedule(JSONObject deviceObj, String strflag) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);

        String url = "";
        if (strflag.equalsIgnoreCase("update")) {
            url = ChatApplication.url + Constants.updateCameraNotification;
        } else {
            url = ChatApplication.url + Constants.addCameraNotification;
        }

        ChatApplication.logDisplay("Add camera alert " + " " + url + " " + deviceObj.toString());
        new GetJsonTask(CameraNotificationActivity.this, url, "POST", deviceObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
//                ActivityHelper.hideKeyboard(CameraNotificationActivity.this);
                ChatApplication.isScheduleNeedResume = true;
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        getconfigureData();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /**/
    public void getconfigureData() {
        if (!ActivityHelper.isConnectingToInternet(CameraNotificationActivity.this)) {
            Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(CameraNotificationActivity.this, "Please wait... ", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getconfigureData(jetsoncameranotification, jetson_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        JSONObject object = result.optJSONObject("data");

                        if (object == null) {
                            recyclerView.setVisibility(View.GONE);
                            view_starttime.setVisibility(View.GONE);
                            txtEmptyAlert.setVisibility(View.VISIBLE);
                            recyclerAlert.setVisibility(View.GONE);
                            txt_empty_notification.setVisibility(View.VISIBLE);
                            txtAlertCount.setVisibility(View.GONE);
                            return;
                        }

                        JSONArray jsonArray = object.optJSONArray("cameraAlertList");
                        String unreadCount = object.optString("unreadCount");

                        JSONArray jsonArray2 = object.optJSONArray("cameraPushLogs");
                        JSONArray jsonArray3 = object.optJSONArray("cameraIdList");

                        arrayListLog.clear();
                        arrayListAlert.clear();

                        if (jsonArray3 != null) {
                            getCameraList = (ArrayList<CameraVO>) Common.fromJson(jsonArray3.toString(),
                                    new TypeToken<ArrayList<CameraVO>>() {
                                    }.getType());
                        }

                        if (jsonArray != null) {
                            arrayListLog = (ArrayList<CameraAlertList>) Common.fromJson(jsonArray.toString(),
                                    new TypeToken<ArrayList<CameraAlertList>>() {
                                    }.getType());
                        }

                        if (jsonArray2 != null) {
                            arrayListAlert = (ArrayList<CameraPushLog>) Common.fromJson(jsonArray2.toString(),
                                    new TypeToken<ArrayList<CameraPushLog>>() {
                                    }.getType());
                        }

                        if (arrayListLog.size() > 0) {
                            setAdapter();
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            txtEmptyAlert.setVisibility(View.VISIBLE);
                        }
                        if (arrayListAlert.size() > 0) {
                            setAlert();
                        } else {
                            recyclerAlert.setVisibility(View.GONE);
                            view_header.setVisibility(View.GONE);
                            txt_empty_notification.setVisibility(View.VISIBLE);
                        }

                        if (unreadCount.equalsIgnoreCase("0")) {
                            txtAlertCount.setVisibility(View.GONE);
                        }
                        callCameraUnseenLog();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Get camera unseen log
     */
    private void callCameraUnseenLog() {
        if (!ActivityHelper.isConnectingToInternet(CameraNotificationActivity.this)) {
            Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(CameraNotificationActivity.this, "Please wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().callCameraUnseenLog(homecontroller_id, jetsoncameranotification, jetson_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        //   JSONObject object = result.optJSONObject("data");

                        if (result == null) {
                            recyclerView.setVisibility(View.GONE);
                            txtEmptyAlert.setVisibility(View.VISIBLE);
                            recyclerAlert.setVisibility(View.GONE);
                            txt_empty_notification.setVisibility(View.VISIBLE);
                            txtAlertCount.setVisibility(View.GONE);
                            return;
                        }

                        JSONArray jsonArray = result.optJSONArray("data");

                        arrayListAlert.clear();


                        if (jsonArray != null) {
                            arrayListAlert = (ArrayList<CameraPushLog>) Common.fromJson(jsonArray.toString(),
                                    new TypeToken<ArrayList<CameraPushLog>>() {
                                    }.getType());
                        }

                        if (arrayListAlert.size() > 0) {
                            setAlert();
                        } else {
                            recyclerAlert.setVisibility(View.GONE);
                            txt_empty_notification.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void setAlert() {
        recyclerAlert.setVisibility(View.VISIBLE);
        txt_empty_notification.setVisibility(View.GONE);
        txtAlertCount.setText("" + arrayListAlert.size());

        recyclerAlert.getRecycledViewPool().clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CameraNotificationActivity.this);
        recyclerAlert.setLayoutManager(linearLayoutManager);

        alertAdapter = new AlertAdapter(CameraNotificationActivity.this, arrayListAlert, homecontroller_id);
        recyclerAlert.setAdapter(alertAdapter);
        alertAdapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        recyclerView.setVisibility(View.VISIBLE);
        txtEmptyAlert.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CameraNotificationActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CameraNotificationActivity.this, Constants.SWITCH_NUMBER);

        cameraNotificationAdapter = new CameraNotificationAdapter(CameraNotificationActivity.this, gridLayoutManager, arrayListLog, getCameraList, this);
        recyclerView.setAdapter(cameraNotificationAdapter);
        cameraNotificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectcameraList(ArrayList<CameraVO> arrayListLog) {
        countCamera = 0;
        for (int i = 0; i < arrayListLog.size(); i++) {
            if (arrayListLog.get(i).getIsSelect()) {
                countCamera++;
            }

        }
        txtSDevice.setText("(" + countCamera + " Selected)");
    }

    @Override
    public void updatecameraALert(final String strFlag, final CameraAlertList cameraAlertList, final SwitchCompat switchAlert, final int position) {
        if (strFlag.equalsIgnoreCase("switch")) {
            showAlertDialog(cameraAlertList, strFlag, switchAlert, position);
        } else if (strFlag.equalsIgnoreCase("update")) {
            if (cameraAlertList.getCameraIds() != null) {
                showBottomSheetDialog(strFlag, cameraAlertList, switchAlert, position);
            }

        }

    }

    public void showBottomSheetDialog(final String strFlag, final CameraAlertList cameraAlertList, final SwitchCompat switchAlert, final int position) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(CameraNotificationActivity.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in camera notification?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addCameraNew(strFlag, cameraAlertList);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteCamera(cameraAlertList, strFlag, switchAlert, position);
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(CameraNotificationActivity.this.getFragmentManager(), "dialog");
            }
        });
    }

    /*delete camera */
    private void deleteCamera(final CameraAlertList cameraAlertList, final String strFlag, SwitchCompat switchAlert, final int position) {

        /*
        "camera_notification_id":"1543230466926_b-hZ8qV9f",
		"is_active":1,
		"phone_id":"1234",
		"phone_type":"iOS"
        * */
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteCamera(jetson_id, cameraAlertList.getCameraNotificationId(), jetsoncameranotification, strFlag, cameraAlertList, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!strFlag.equalsIgnoreCase("switch")) {
                            Toast.makeText(getApplicationContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            getconfigureData();
                        } else {
                            if (cameraAlertList.getIsActive() == 1) {
                                arrayListLog.get(position).setIsActive(0);
                            } else {
                                arrayListLog.get(position).setIsActive(1);
                            }
                            cameraNotificationAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
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

    private void showAlertDialog(final CameraAlertList cameraAlertList, final String strFlag, final SwitchCompat switchAlert, final int position) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Notification Alert");
        builder1.setMessage("Do you want to " + (cameraAlertList.getIsActive().toString().equalsIgnoreCase("1") ? "disable " : "enable") + " notificaiton ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteCamera(cameraAlertList, strFlag, switchAlert, position);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchAlert.setChecked(switchAlert.isChecked());
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        if (!alert11.isShowing()) {
            alert11.show();
        }
    }


    /*clear unread count*/
    private void callupdateUnReadCameraLogs(final boolean b) {
        if (arrayListAlert.size() > 0) {
            if (b) {
                Intent intent = new Intent(CameraNotificationActivity.this, CameraDeviceLogActivity.class);
                if (jetsoncameranotification) {
                    intent.putExtra("getCameraList", cameraVOArrayList);
                    intent.putExtra("jetson_device_id", jetson_id);
                    intent.putExtra("jetsonnotification", true);
                    intent.putExtra("homecontrollerId", homecontroller_id);
                } else {
                    intent.putExtra("getCameraList", cameraVOArrayList);
                    intent.putExtra("jetson_device_id", jetson_id);
                    intent.putExtra("homecontrollerId", homecontroller_id);
                }
                startActivity(intent);
            } else {
                CameraNotificationActivity.this.finish();
            }
        } else {
            if (b) {
                Intent intent = new Intent(CameraNotificationActivity.this, CameraDeviceLogActivity.class);
                if (jetsoncameranotification) {
                    intent.putExtra("getCameraList", cameraVOArrayList);
                    intent.putExtra("jetson_device_id", jetson_id);
                    intent.putExtra("jetsonnotification", true);
                    intent.putExtra("homecontrollerId", homecontroller_id);
                } else {
                    intent.putExtra("getCameraList", cameraVOArrayList);
                    intent.putExtra("jetson_device_id", jetson_id);
                    intent.putExtra("homecontrollerId", homecontroller_id);
                }
                startActivity(intent);
            } else {
                CameraNotificationActivity.this.finish();
            }
            return;
        }
        if (!ActivityHelper.isConnectingToInternet(CameraNotificationActivity.this)) {
            Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().callupdateUnReadCameraLogs(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    if (b) {
                        Intent intent = new Intent(CameraNotificationActivity.this, CameraDeviceLogActivity.class);
                        if (jetsoncameranotification) {
                            intent.putExtra("cameraList", cameraVOArrayList);
                            intent.putExtra("jetson_device_id", jetson_id);
                            intent.putExtra("jetsonnotification", true);
                            intent.putExtra("homecontrollerId", homecontroller_id);
                        } else {
                            intent.putExtra("getCameraList", cameraVOArrayList);
                            intent.putExtra("jetson_device_id", jetson_id);
                            intent.putExtra("homecontrollerId", homecontroller_id);
                        }
                        startActivity(intent);
                    } else {
                        CameraNotificationActivity.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callupdateUnReadCameraLogs(false);
    }
}
