package com.spike.bot.activity.Repeatar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AllUnassignedPanel;
import com.spike.bot.adapter.RepeaterAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.RepeaterModel;
import com.spike.bot.model.UnassignedListRes;

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
        MenuItem menuaddtext = menu.findItem(R.id.action_add_text);
        menuAdd.setVisible(false);
        menuaddtext.setVisible(true);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            showOptionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*start socket connection*/
    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureDevice", configureDevice);
        }
    }

    /*configure device socket */
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

                            /*
                            * if message getting null means module add first time
                            * another module already add or getting any error message showing */

                            if (TextUtils.isEmpty(message)) {
                                showSensor(obj.optString("module_id"), obj.optString("module_type"));
                            } else {
                                showConfigAlert(message);
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
           ChatApplication.showToast(RepeaterActivity.this, getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(RepeaterActivity.this, "Searching for new Repeater", false);
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
            }
        }).execute();
    }

    private void showConfigAlert(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /*show sync dialog
    * sync means add new device .
    * Unassigned means already adding device but delete this device than its showing in Unassigned
    * Add From Existing means create cusotm panel
    *
    * */
    private void showOptionDialog() {

        final Dialog dialog = new Dialog(RepeaterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle =  dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Repeater");

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSocket != null) {
                    mSocket.on("configureDevice", configureDevice);
                }
                getconfigureRepeatorRequest();
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(RepeaterActivity.this, AllUnassignedPanel.class);
                intent.putExtra("type","repeater");
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

    private void showSensor(String door_module_id,String module_type) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name =  dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id =  dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list =  dialog.findViewById(R.id.sp_room_list);
        final LinearLayout linearListRoom = dialog.findViewById(R.id.linearListRoom);

        TextView dialogTitle =  dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name =  dialog.findViewById(R.id.txt_sensor_name);

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
                mSocket.off("configureDevice", configureDevice);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSocket.off("configureDevice", configureDevice);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_door_name.getText().toString())) {
                    edt_door_name.setError("Enter Gas Name");
                    edt_door_name.requestFocus();
                } else {
                    saveRepeater(dialog, module_type, edt_door_name.getText().toString(), edt_door_module_id.getText().toString());
                    dialog.dismiss();
                    ChatApplication.closeKeyboard(RepeaterActivity.this);
                    mSocket.off("configureDevice", configureDevice);
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /**
     * Save individual repeater */
    private void saveRepeater(final Dialog dialog, String module_type, String door_name, String door_module_id) {

        if (!ActivityHelper.isConnectingToInternet(RepeaterActivity.this)) {
            Toast.makeText(RepeaterActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(RepeaterActivity.this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().saveRepeater(door_name, door_module_id, module_type, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("rep is "+result);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /**
     * get individual door sensor details
     */
    private void getRepeatorLists() {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getDeviceList("repeater",new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        fillData(result);
                    } else {
                        emptyView();
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
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

    /** Fill data of individual repeater
    * Like :- active repeater, repeater name, repeater module id*/

    private void fillData(JSONObject result) {
        try {
            arrayList.clear();
            JSONObject object = new JSONObject(String.valueOf(result));
            JSONArray jsonArray = object.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.optJSONObject(i);
                RepeaterModel repeaterModel = new RepeaterModel();

                repeaterModel.setIs_active(object1.optString("is_active"));
                repeaterModel.setRepeator_name(object1.optString("device_name"));
                repeaterModel.setRepeator_module_id(object1.optString("device_id"));

                arrayList.add(repeaterModel);
            }

            emptyView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void emptyView(){
        if (arrayList.size() > 0) {
            txtNodata.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            txtNodata.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            ChatApplication.showToast(RepeaterActivity.this, "No data found.");
        }

    }

    /** Delete individual epeater */
    private void deleteRepater(RepeaterModel repeaterModel, int postion) {
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(repeaterModel.getRepeator_module_id(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("repeatar is " + result);
                    if (code == 200) {
                        ChatApplication.showToast(RepeaterActivity.this, result.optString("message"));
                        arrayList.remove(postion);
                        repeaterAdapter.notifyDataSetChanged();
                        emptyView();
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });

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
            showBottomSheetDialog(repeaterModel,postion);
        } /*else {
            deleteRepaterDialog(repeaterModel, postion);
        }*/
    }

    public void showBottomSheetDialog(RepeaterModel repeaterModel, int postion) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(RepeaterActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + repeaterModel.getRepeator_name() + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showEditNameDailog(repeaterModel,postion);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteRepaterDialog(repeaterModel, postion);
            }
        });
    }


    /*delete dialog*/
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


    /*update repeater dialog*/
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

        Button btnSave =  dialog.findViewById(R.id.btn_save);
        Button btn_cancel =  dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);
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

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().updateRepetar(repeaterModel.getRepeator_module_id(), name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }
}
