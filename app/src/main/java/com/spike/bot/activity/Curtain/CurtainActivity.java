package com.spike.bot.activity.Curtain;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRBlasterAddActivity;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterAddRes;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 23/9/19.
 * Gmail : vipul patel
 */
public class CurtainActivity extends AppCompatActivity implements View.OnClickListener {

    Socket mSocket;

    Toolbar toolbar;
    ImageView imgClose, imgOpen, imgPause;
    // Button btn_delete;
    String curtain_id = "", module_id = "", curtain_name = "", curtain_status = "", panel_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain);

        module_id = getIntent().getStringExtra("module_id");
        curtain_id = getIntent().getStringExtra("curtain_id");
        curtain_name = getIntent().getStringExtra("curtain_name");
        curtain_status = getIntent().getStringExtra("curtain_status");
        panel_id = getIntent().getStringExtra("panel_id");
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
        imgPause = findViewById(R.id.imgPause);
        imgOpen = findViewById(R.id.imgOpen);
        imgClose = findViewById(R.id.imgClose);
        //   btn_delete = findViewById(R.id.btn_delete);

        imgClose.setOnClickListener(this);
        imgOpen.setOnClickListener(this);
        imgPause.setOnClickListener(this);
        //     btn_delete.setOnClickListener(this);

        setView();
    }

    private void setView() {
        if (curtain_status.equals("1")) {
            setCurtainClick(true, false, false);
        } else if (curtain_status.equals("2")) {
            setCurtainClick(false, false, true);
        } else if (curtain_status.equals("0")) {
            setCurtainClick(false, true, false);
        }
    }

    private void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null) {
            mSocket.on("changeDeviceStatus", updateCurtainStatus);
        }

    }

    @Override
    public void onPause() {
        Constants.startUrlset();
        if (mSocket != null) {
            mSocket.off("changeDeviceStatus", updateCurtainStatus);
        }

        super.onPause();
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
        if (v == imgOpen) {
            curtain_status = "1";
            updateStatus();
        } else if (v == imgPause) {
            curtain_status = "2";
            updateStatus();
        } else if (v == imgClose) {
            curtain_status = "0";
            updateStatus();
        }/* else if (v == btn_delete) {
            callDailog();
        }*/
    }

    /*geting curtain status*/
    private Emitter.Listener updateCurtainStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            ChatApplication.logDisplay("curtain socket is " + object);
                            String device_id = object.optString("device_id");
                            curtain_status = object.optString("device_status");
                            if (device_id.equalsIgnoreCase(curtain_id)) {
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


    public void setCurtainClick(boolean open, boolean close, boolean pause) {
        imgOpen.setImageResource(open ? R.drawable.open_enabled : R.drawable.open_disabled);
        imgClose.setImageResource(close ? R.drawable.close_enabled : R.drawable.close_disabled);
        imgPause.setImageResource(pause ? R.drawable.puse_enabled : R.drawable.puse_disabled);
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(CurtainActivity.this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();


        txt_bottomsheet_title.setText("What would you like to do in" + " " + curtain_name + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogEditName();
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callDialog();
            }
        });
    }

    /*dialog for add room*/
    private void dialogEditName() {
        final Dialog dialog = new Dialog(CurtainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = dialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = dialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = dialog.findViewById(R.id.edSensorName);
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
        edSensorName.setText(curtain_name);
        txtInputSensor.setHint("Enter Curtain name");
        tv_title.setText("Enter name");
        btn_cancel.setText("DELETE");

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
                // ChatApplication.keyBoardHideForce(CurtainActivity.this);

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

    private void callDialog() {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
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

    /*delete panel*/
    private void deletePanel() {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().deleteDevice(curtain_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("updateCurtain is " + result);
                    if (result.optInt("code") == 200) {
                        CurtainActivity.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /*update curtain Status*/
    private void updateStatus() {

        SpikeBotApi.getInstance().updateCutainStatus(curtain_id, panel_id, curtain_status, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    if (result.optInt("code") == 200) {

                        if (curtain_status.equals("1")) {
                            setCurtainClick(true, false, false);
                        } else if (curtain_status.equals("2")) {
                            setCurtainClick(false, false, true);
                        } else if (curtain_status.equals("0")) {
                            setCurtainClick(false, true, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {

            }
        });

    }

    /*update curtain name*/
    private void updateCurtain(String name) {
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        SpikeBotApi.getInstance().updateCurtain(curtain_id, name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ActivityHelper.dismissProgressDialog();
                    if (result.optInt("code") == 200) {
                        toolbar.setTitle(name);
                        ChatApplication.showToast(CurtainActivity.this, result.optString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }
        });

    }


}
