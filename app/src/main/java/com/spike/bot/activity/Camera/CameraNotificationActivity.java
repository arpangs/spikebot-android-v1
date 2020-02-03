package com.spike.bot.activity.Camera;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.spike.bot.core.APIConst;
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
import com.spike.bot.model.CameraViewModel;

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

    public Toolbar toolbar;
    public RecyclerView recyclerView, recyclerAlert;
    public TextView txtAlertCount;
    public CameraNotificationAdapter cameraNotificationAdapter;
    public AlertAdapter alertAdapter;
    public TextView txtSDevice, txt_empty_notification, txtEmptyAlert;
    public ImageView view_rel_badge;

    ArrayList<CameraVO> getCameraList = new ArrayList<>();
    ArrayList<CameraAlertList> arrayListLog = new ArrayList<>();
    ArrayList<CameraPushLog> arrayListAlert = new ArrayList<>();

    public int countCamera = 0;
    Dialog dialog;


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
        toolbar.setTitle("Camera Alerts");
        recyclerView = (RecyclerView) findViewById(R.id.sensor_list);
        recyclerAlert = (RecyclerView) findViewById(R.id.recyclerAlert);
        txtAlertCount = (TextView) findViewById(R.id.txtAlertCount);
        txt_empty_notification = (TextView) findViewById(R.id.txt_empty_notification);
        txtEmptyAlert = (TextView) findViewById(R.id.txtEmptyAlert);
        view_rel_badge = (ImageView) findViewById(R.id.view_rel_badge);

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
            //callupdateUnReadCameraLogs(true);

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
                    callAddCamera(et_schedule_on_time.getText().toString(), et_schedule_off_time.getText().toString(), strFlag, cameraAlertList, Integer.parseInt(edIntervalTime.getText().toString()));
                }
            }
        });

        dialog.show();

    }

    /**
     * Add camera
     */
    private void callAddCamera(String et_schedule_on_time, String et_schedule_off_time, String strflag, CameraAlertList cameraAlertList, int edIntervalTime) {

        if (strflag.equalsIgnoreCase("update")) {

            String onTime = "", offTime = "";

            try {
                onTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_on_time.toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_off_time.toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject deviceObj = new JSONObject();
            try {

                deviceObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                deviceObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
                deviceObj.put("start_time", onTime);
                deviceObj.put("end_time", offTime);
                deviceObj.put("alert_interval", edIntervalTime);
                deviceObj.put("camera_notification_id", cameraAlertList.getCameraNotificationId());
                deviceObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                deviceObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

                JSONArray roomDeviceArray = new JSONArray();
                JSONArray array = new JSONArray();
                for (CameraVO dPanel : getCameraList) {

                    if (dPanel.getIsSelect()) {
                        JSONObject object = new JSONObject();
                        object.put("camera_id", dPanel.getCamera_id());
                        object.put("camera_name", "" + dPanel.getCamera_name());
                        object.put("camera_url", "" + dPanel.getCamera_url());
                        object.put("camera_ip", "" + dPanel.getCamera_ip());
                        object.put("camera_videopath", "" + dPanel.getCamera_videopath());
                        object.put("camera_icon", "" + dPanel.getCamera_icon());
                        object.put("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                        object.put("user_name", "" + dPanel.getUserName());
                        object.put("password", "" + dPanel.getPassword());

                        roomDeviceArray.put(object);

                        array.put(dPanel.getCamera_id());
                    }
                }
                deviceObj.put("camera_ids", array);
                deviceObj.put("cameraList", roomDeviceArray);
                deviceObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

                addSchedule(deviceObj, strflag);
            } catch (Exception e) {

            }

        } else {
            String onTime = "", offTime = "";
            try {
                onTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_on_time.toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
                offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(et_schedule_off_time.toString(), DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject deviceObj = new JSONObject();
            try {

                deviceObj.put("start_time", onTime);
                deviceObj.put("end_time", offTime);
                deviceObj.put("alert_interval", edIntervalTime);
                deviceObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
                deviceObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

                JSONArray roomDeviceArray = new JSONArray();
                for (CameraVO dPanel : getCameraList) {

                    if (dPanel.getIsSelect()) {
                        JSONObject object = new JSONObject();
                        object.put("camera_id", dPanel.getCamera_id());
                        object.put("camera_name", "" + dPanel.getCamera_name());
                        object.put("camera_url", "" + dPanel.getCamera_url());
                        object.put("camera_ip", "" + dPanel.getCamera_ip());
                        object.put("camera_videopath", "" + dPanel.getCamera_videopath());
                        object.put("camera_icon", "" + dPanel.getCamera_icon());
                        object.put("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                        object.put("user_name", "" + dPanel.getUserName());
                        object.put("password", "" + dPanel.getPassword());

                        roomDeviceArray.put(object);
                    }
                }

                deviceObj.put("cameraList", roomDeviceArray);
                deviceObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
                addSchedule(deviceObj, strflag);
            } catch (Exception e) {

            }
        }
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


        new GetJsonTask(CameraNotificationActivity.this, url, "POST", deviceObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.hideKeyboard(CameraNotificationActivity.this);
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.getCameraNotificationAlertList;
        ChatApplication.logDisplay("json camera is " + url + "  " + jsonObject.toString());
        new GetJsonTask(CameraNotificationActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.optJSONObject("data");

                        if (object == null) {
                            recyclerView.setVisibility(View.GONE);
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
                            txt_empty_notification.setVisibility(View.VISIBLE);
                        }

                        if (unreadCount.equalsIgnoreCase("0")) {
                            txtAlertCount.setVisibility(View.GONE);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setAlert() {
        recyclerAlert.setVisibility(View.VISIBLE);
        txt_empty_notification.setVisibility(View.GONE);
        txtAlertCount.setText("" + arrayListAlert.size());

        recyclerAlert.getRecycledViewPool().clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CameraNotificationActivity.this);
        recyclerAlert.setLayoutManager(linearLayoutManager);

        alertAdapter = new AlertAdapter(CameraNotificationActivity.this, arrayListAlert);
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
        if (strFlag.equalsIgnoreCase("delete")) {
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
        } else if (strFlag.equalsIgnoreCase("switch")) {
            showAlertDialog(cameraAlertList, strFlag, switchAlert, position);
        } else {
            if (cameraAlertList.getCameraIds() != null) {
                addCameraNew(strFlag, cameraAlertList);
            }

        }

    }

    /*delete camera */
    private void deleteCamera(final CameraAlertList cameraAlertList, final String strFlag, SwitchCompat switchAlert, final int position) {

        /*
        "camera_notification_id":"1543230466926_b-hZ8qV9f",
		"is_active":1,
		"phone_id":"1234",
		"phone_type":"iOS"
        * */
        JSONObject deviceObj = new JSONObject();
        try {
            deviceObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            deviceObj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            deviceObj.put("camera_notification_id", cameraAlertList.getCameraNotificationId());
            deviceObj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            if (strFlag.equalsIgnoreCase("switch")) {
                if (cameraAlertList.getIsActive() == 1) {
                    deviceObj.put("is_active", "0");
                } else {
                    deviceObj.put("is_active", "1");
                }

            }
        } catch (Exception e) {

        }

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait.", false);

        String url = "";
        if (strFlag.equalsIgnoreCase("switch")) {
            url = ChatApplication.url + Constants.changeCameraAlertStatus;
        } else {
            url = ChatApplication.url + Constants.deleteCameraNotification;
        }

        new GetJsonTask(CameraNotificationActivity.this, url, "POST", deviceObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.hideKeyboard(CameraNotificationActivity.this);
                ChatApplication.isScheduleNeedResume = true;
                try {
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


    private void showAlertDialog(final CameraAlertList cameraAlertList, final String strFlag, final SwitchCompat switchAlert, final int position) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Notification Alert");
        builder1.setMessage("Do you want " + (cameraAlertList.getIsActive().toString().equalsIgnoreCase("1") ? "disable " : "enable") + " notificaiton ?");
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

        } else {
            if (b) {
                Intent intent = new Intent(CameraNotificationActivity.this, CameraDeviceLogActivity.class);
                intent.putExtra("getCameraList", getCameraList);
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("camera_id", "");
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.updateUnReadCameraLogs;

        ChatApplication.logDisplay("url is "+url+" "+jsonObject);
        new GetJsonTask(CameraNotificationActivity.this, url, "POST", jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("url is "+result);
                    if (b) {
                        Intent intent = new Intent(CameraNotificationActivity.this, CameraDeviceLogActivity.class);
                        intent.putExtra("getCameraList", getCameraList);
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
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(CameraNotificationActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public void onBackPressed() {
        //callupdateUnReadCameraLogs(false);
        super.onBackPressed();
    }
}
