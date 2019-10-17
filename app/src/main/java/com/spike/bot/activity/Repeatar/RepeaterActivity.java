package com.spike.bot.activity.Repeatar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SensorUnassignedActivity;
import com.spike.bot.adapter.RepeaterAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.RepeaterModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 14/9/19.
 * Gmail : vipul patel
 */
public class RepeaterActivity extends AppCompatActivity implements RepeaterAdapter.MoreOption {

    public Toolbar toolbar;
    public RecyclerView recyclerView;
    public AppCompatTextView txtNodata;
    private Socket mSocket;
    boolean addRoom = false;

    RepeaterAdapter repeaterAdapter;
    ArrayList<RepeaterModel> arrayList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeater);

        setUIId();
    }

    private void setUIId() {
        startSocketConnection();
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerRepeater);
        txtNodata = findViewById(R.id.txtNodata);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Repeater List");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        getRepeatorLists();
        super.onResume();
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
            showOptionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        if (mSocket != null) {
            mSocket.on("configureDevice", configureDevice);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    private Emitter.Listener configureDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ChatApplication.logDisplay("obj is is ");
                    if (args != null) {
                        try {
                            ActivityHelper.dismissProgressDialog();

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            addRoom = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject obj = new JSONObject(args[0].toString());
                            String message = obj.optString("message");

                            ChatApplication.logDisplay("obj is " + obj);

                            //{"module_id":"B04A1D1A004B1200"}
                            if (TextUtils.isEmpty(message)) {
                                showSensor(obj.optString("module_id"), true, false);
                            } else {
                                showConfigAlert(message);
                                //Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    /*searching device wait for 7 sec*/
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
        }

    };

    public void startTimer() {
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }


    /*
    * sync request */
    private void getconfigureRepeatorRequest() {
        if (!ActivityHelper.isConnectingToInternet(RepeaterActivity.this)) {
            Toast.makeText(RepeaterActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(RepeaterActivity.this, "Searching Device attached ", false);
        startTimer();

        String url = ChatApplication.url + Constants.deviceconfigure+"repeater";

        new GetJsonTask(RepeaterActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result is " + result);
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(RepeaterActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void showConfigAlert(String alertMessage) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showOptionDialog() {

        final Dialog dialog = new Dialog(RepeaterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Type");

        Button btn_sync = (Button) dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button) dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getconfigureRepeatorRequest();
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(RepeaterActivity.this, SensorUnassignedActivity.class);
                intent.putExtra("isDoorSensor", 10);
                intent.putExtra("roomName", "");
                startActivity(intent);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void showSensor(String door_module_id, final boolean isTempSensorRequest, final boolean isMultiSensor) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = (EditText) dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) dialog.findViewById(R.id.sp_room_list);
        final LinearLayout linearListRoom = dialog.findViewById(R.id.linearListRoom);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) dialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add Repeater");
        txt_sensor_name.setText("Repeater Name");

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        linearListRoom.setVisibility(View.GONE);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this, roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel =  dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save =  dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRepeater(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                dialog.dismiss();
                ChatApplication.closeKeyboard(RepeaterActivity.this);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }


    /**
     * Save individual repeater */
    private void saveRepeater(final Dialog dialog, EditText textInputEditText, String door_name,
                              String door_module_id, Spinner sp_room_list, final boolean isTempSensorRequest) {

        if (!ActivityHelper.isConnectingToInternet(RepeaterActivity.this)) {
            Toast.makeText(RepeaterActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.setError("Enter Gas Name");
            textInputEditText.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(RepeaterActivity.this, "Please wait.", false);

        JSONObject obj = new JSONObject();
        try {

            // "repeator_module_id": "C7371D1A004B1200",
            //  "repeator_name": "test",
            //  "user_id": "1566466327758_FJNyQzUUP",
            //  "phone_id": "1234567",
            //  "phone_type": "Android"

            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("repeator_name", door_name);
            obj.put("repeator_module_id", door_module_id);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.addRepeator;

        new GetJsonTask(RepeaterActivity.this, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(RepeaterActivity.this, message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        getRepeatorLists();
                    } else {
                        Toast.makeText(RepeaterActivity.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

    /**
     * get individual door sensor details
     */
    private void getRepeatorLists() {
        String url = ChatApplication.url + Constants.getRepeatorLists;

        ChatApplication.logDisplay("door " + url + " ");

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        fillData(result);
                    } else {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /** Fill data of individual repeater
    * Like :- active repeater, repeater name, repeater module id*/

    private void fillData(JSONObject result) {
        //{
        //  "code": 200,
        //  "message": "Success",
        //  "data": [
        //    {
        //      "repeator_module_id": "B04A1D1A004B1200",
        //      "repeator_name": "t",
        //      "is_active": 1
        //    }
        //  ]
        //}

        try {
            arrayList.clear();

            JSONObject object = new JSONObject(String.valueOf(result));
            JSONObject data = object.optJSONObject("data");
            JSONArray jsonArray = object.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.optJSONObject(i);
                RepeaterModel repeaterModel = new RepeaterModel();

                repeaterModel.setIs_active(object1.optString("is_active"));
                repeaterModel.setRepeator_name(object1.optString("repeator_name"));
                repeaterModel.setRepeator_module_id(object1.optString("repeator_module_id"));

                arrayList.add(repeaterModel);
            }

            if (arrayList.size() > 0) {
                txtNodata.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                setAdapter();
            } else {
                txtNodata.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                ChatApplication.showToast(RepeaterActivity.this, "No data found.");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /** Delete individual epeater */
    private void deleteRepater(RepeaterModel repeaterModel, int postion) {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.deleteRepeator;

        ChatApplication.logDisplay("door " + url + " ");

        JSONObject object = new JSONObject();


        try {
            object.put("repeator_module_id", repeaterModel.getRepeator_module_id());
            object.put("repeator_name", repeaterModel.getRepeator_name());
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//
//  	"repeator_module_id"	: "B04A1D1A004B1200",
//	"repeator_name":"repeator new name",
//	"user_id":"1566980189559_18CfbqxQo",
//	"phone_id":"1234567",
//	"phone_type":"Android"

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                        arrayList.remove(postion);
                        repeaterAdapter.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setAdapter() {
        repeaterAdapter = new RepeaterAdapter(this, arrayList, this);
        recyclerView.setAdapter(repeaterAdapter);
        repeaterAdapter.notifyDataSetChanged();
    }

    @Override
    public void moreOption(RepeaterModel repeaterModel, int postion, int type) {

        //edit name
        if (type == 1) {
            showEditNameDailog(repeaterModel, postion);
        } else {
            deleteRepaterDialog(repeaterModel, postion);
        }
    }

    private void deleteRepaterDialog(RepeaterModel repeaterModel, int postion) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                deleteRepater(repeaterModel, postion);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(this.getFragmentManager(), "dialog");
    }


    private void showEditNameDailog(RepeaterModel repeaterModel, int postion) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout inputRoom = dialog.findViewById(R.id.inputRoom);
        final TextInputLayout inputRepeator = dialog.findViewById(R.id.inputRepeator);
        final TextInputEditText edRepeator = dialog.findViewById(R.id.edRepeator);
        inputRoom.setVisibility(View.GONE);
        inputRepeator.setVisibility(View.VISIBLE);

        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText("Change Repeater Name");
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edRepeator.getText().toString().trim().length() == 0) {
                    ChatApplication.showToast(RepeaterActivity.this, "Please enter name");
                } else {
                    updateRepetar(repeaterModel, postion, dialog, edRepeator.getText().toString());
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /** Update individual repeater*/
    private void updateRepetar(RepeaterModel repeaterModel, int postion, Dialog dialog, String name) {

        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = ChatApplication.url + Constants.updateRepeator;

        JSONObject object = new JSONObject();

        try {
            object.put("repeator_module_id", repeaterModel.getRepeator_module_id());
            object.put("repeator_name", name);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("door " + url + " " + object);
//"repeator_module_id"	: "B04A1D1A004B1200",
//	"repeator_name":"repeator new name",
//	"user_id":"1566980189559_18CfbqxQo",
//	"phone_id":"1234567",
//	"phone_type":"Android"

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    dialog.dismiss();
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                        arrayList.get(postion).setRepeator_name(name);
                        repeaterAdapter.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                throwable.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
