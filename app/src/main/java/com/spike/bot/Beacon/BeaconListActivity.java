package com.spike.bot.Beacon;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.spike.bot.activity.AddDevice.AddDeviceTypeListActivity;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.adapter.beacon.BeaconAddAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterAddRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BeaconListActivity extends AppCompatActivity implements BeaconListAdapter.BeaconClickListener{
    RecyclerView recycler_beaconlist;
    private LinearLayout mEmptyView;
    private BeaconListAdapter beaconlistAdapter;
    private List<IRBlasterAddRes.Datum> beaconList;
    Dialog beacondialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);
        bindView();



    }

    public void bindView() {
        recycler_beaconlist = findViewById(R.id.recycler_beaconlist);
        mEmptyView = findViewById(R.id.txt_empty_beacon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Beacon List");
        getSupportActionBar().setTitle("Beacon List");

        recycler_beaconlist.setLayoutManager(new GridLayoutManager(this, 1));
        getBeaconList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBeaconList();
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
        MenuItem txtmenuadd = menu.findItem(R.id.action_add_text);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        txtmenuadd.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_text) {
            //dialogAddBeacon();
            showOptionDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * get Beacon list
     */
    public void getBeaconList(){
        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), BeaconListActivity.this.getResources().getString(R.string.disconnect));
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);

        if (beaconList != null) {
            beaconList.clear();
        }

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getDeviceList("beacon",new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(), IRBlasterAddRes.class);
                    if (irBlasterAddRes.getCode() == 200) {
                        ActivityHelper.dismissProgressDialog();
                        beaconList = irBlasterAddRes.getData();
                        beaconList = irBlasterAddRes.getData();

                        beaconlistAdapter = new BeaconListAdapter(beaconList, BeaconListActivity.this);
                        recycler_beaconlist.setAdapter(beaconlistAdapter);
                        beaconlistAdapter.notifyDataSetChanged();

                    } else {
                        Common.showToast(irBlasterAddRes.getMessage());
                    }

                    if (beaconList.size() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        recycler_beaconlist.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        recycler_beaconlist.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(BeaconListActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(BeaconListActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });

    }

    @Override
    public void editClicked(IRBlasterAddRes.Datum beaconmodel, int position, int type) {
        showBottomSheetDialog(beaconmodel,position);
    }

    public void showBottomSheetDialog(IRBlasterAddRes.Datum beacon,int position) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(BeaconListActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        String beaconname =  beaconList.get(position).getDeviceName();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + beaconname + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(BeaconListActivity.this, BeaconConfigActivity.class);
                intent.putExtra("editBeacon",true);
                intent.putExtra("beaconmodel",beacon);
                intent.putExtra("panel_id",beacon.getPanelDeviceId());
                intent.putExtra("isMap",true);
                intent.putExtra("isBeaconListAdapter",true);
                startActivity(intent);
                finish();
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteBeacon(beacon.getDeviceId());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }


    /*delete beacon */
    private void deleteBeacon(String beaconid) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Common.showToast("" + getString(R.string.error_connect));
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(beaconid, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        Common.showToast(message);
                    }
                    if (code.equalsIgnoreCase("200")) {
                        getBeaconList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(BeaconListActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(BeaconListActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });

    }

    /*add beacon dialog*/
    private void dialogAddBeacon() {
        beacondialog = new Dialog(BeaconListActivity.this);
        beacondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        beacondialog.setCanceledOnTouchOutside(false);
        beacondialog.setContentView(R.layout.dialog_add_custome_room);

        final TextInputLayout txtInputSensor = beacondialog.findViewById(R.id.txtInputSensor);
        final TextInputEditText room_name = beacondialog.findViewById(R.id.edt_room_name);
        final TextInputEditText edSensorName = beacondialog.findViewById(R.id.edSensorName);
        Button btnSave = beacondialog.findViewById(R.id.btn_save);
        Button btn_cancel = beacondialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = beacondialog.findViewById(R.id.iv_close);
        TextView tv_title = beacondialog.findViewById(R.id.tv_title);
        LinearLayout linear_scanoption = beacondialog.findViewById(R.id.linear_scanoption);
        TextView textview_bluetooth = beacondialog.findViewById(R.id.textview_bluetooth);
        TextView textview_scanner = beacondialog.findViewById(R.id.textview_scanner);

        linear_scanoption.setVisibility(View.VISIBLE);

        txtInputSensor.setVisibility(View.GONE);
        edSensorName.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);

        room_name.setVisibility(View.GONE);
        edSensorName.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        edSensorName.setFilters(filterArray);
        tv_title.setText("Scan Beacon Using");

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beacondialog.dismiss();
            }
        });

        textview_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        textview_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        beacondialog.show();

    }


    /*dialog for sensor type selection*/
    private void showOptionDialog() {

        final Dialog dialog = new Dialog(BeaconListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Scan Beacon");


        Button btn_bluetooth = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_scanner = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_bluetooth.setText("Mobile Bluetooth");
        btn_scanner.setText("Scanner");

        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(BeaconListActivity.this, BeaconActivity.class);
                startActivity(intent);
            }
        });
        btn_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(BeaconListActivity.this, AddBeaconActivity.class);
                startActivity(intent);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }
}
