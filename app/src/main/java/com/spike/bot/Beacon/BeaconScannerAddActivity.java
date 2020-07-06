package com.spike.bot.Beacon;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddDevice.AllUnassignedPanel;
import com.spike.bot.activity.ir.blaster.WifiBlasterActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.beacon.BeaconAddAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.spike.bot.core.Common.showToast;

public class BeaconScannerAddActivity extends AppCompatActivity implements BeaconAddAdapter.BeaconAction {
    private RecyclerView mBeaconList;
    private LinearLayout mEmptyView;
    public static int SENSOR_TYPE_BEACON = 4;
    private Socket mSocket;

    private BeaconAddAdapter beaconAddAdapter;
    private List<IRBlasterAddRes.Datum> scannerList;
    ArrayList<UnassignedListRes.Data.RoomList> roomListArray=new ArrayList<>();
    ArrayList<String> roomListString=new ArrayList<>();
    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    private Dialog mDialog;
    EditText mBeaconscannerName;
    Dialog irDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_scanner_add);

        bindView();
    }

    private void bindView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Beacon Scanner List");

        mBeaconList = findViewById(R.id.list_scanner);
        mEmptyView = findViewById(R.id.txt_empty_scanner);

        mBeaconList.setLayoutManager(new GridLayoutManager(this, 1));
        getScannerList();
      /*  beaconAddAdapter = new BeaconAddAdapter(scannerList, BeaconScannerAddActivity.this);
        mBeaconList.setAdapter(beaconAddAdapter);
        beaconAddAdapter.notifyDataSetChanged();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        roomListArray.clear();
        roomListString.clear();
        Constants.isWifiConnect = false;
        Constants.isWifiConnectSave = false;
        startSocketConnection();
        getScannerList();
    }

    public void startSocketConnection() {
        ChatApplication app = (ChatApplication) getApplication();
        if (mSocket != null && mSocket.connected()) {
            return;
        }
        mSocket = app.getSocket();
        if (mSocket != null) {
            mSocket.on("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("configureIRBlaster", configureIRBlaster);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //startTimer();
    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(), "No New Device detected!");
        }

    };

    /*getting socket for beacon scanner any update & change*/
    private Emitter.Listener configureIRBlaster = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {

                            ActivityHelper.dismissProgressDialog();

                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }

                            roomIdList.clear();
                            roomNameList.clear();

                            JSONObject object = new JSONObject(args[0].toString());

                            String message = object.getString("message");

                            String ir_module_id = object.getString("ir_blaster_module_id");

                            if (TextUtils.isEmpty(message)) {

                                JSONArray jsonArray = object.getJSONArray("room_list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objectRoom = jsonArray.getJSONObject(i);
                                    String room_id = objectRoom.getString("room_id");
                                    String room_name = objectRoom.getString("room_name");

                                    roomIdList.add(room_id);
                                    roomNameList.add(room_name);
                                }

                            }

                            ActivityHelper.dismissProgressDialog();

                            if (TextUtils.isEmpty(message)) {
                                showBeaconScannerDialog(ir_module_id);
                            } else {
                                showConfigAlert(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    /**
     * Display Alert dialog when found door or temp sensor config already configured
     *
     * @param alertMessage
     */
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
            showOptionDialogbeacon(SENSOR_TYPE_BEACON);
            return true;
        }

        if (id == R.id.action_add) {
            showOptionDialogbeacon(SENSOR_TYPE_BEACON);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* show dialog for sync device*/
    private void showOptionDialogbeacon(final int sensorType) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Beacon Scanner");

        Button btn_sync = dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(BeaconScannerAddActivity.this, WifiScannerActivity.class);
                //  intent.putExtra("roomId",""+room.getRoomId());
                //  intent.putExtra("roomName",""+room.getRoomName());
                startActivity(intent);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                unassignIntent("beacon_scanner");

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

    /*unassign list*/
    public void unassignIntent(String type) {
        Intent intent = new Intent(this, AllUnassignedPanel.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void onEdit(int position, IRBlasterAddRes.Datum ir) {
        showBottomSheetDialog(position,ir);
    }


    public void showBottomSheetDialog(int position, IRBlasterAddRes.Datum scanner) {
            View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

            TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
            LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
            LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

            TextView txt_edit = view.findViewById(R.id.txt_edit);

            BottomSheetDialog dialog = new BottomSheetDialog(BeaconScannerAddActivity.this,R.style.AppBottomSheetDialogTheme);
            dialog.setContentView(view);
            dialog.show();

         String scannername =  scannerList.get(position).getDeviceName();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + scannername + " " +"?");

        //txt_bottomsheet_title.setText("What would you like to do in "+"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showScannerEditDialog(position,scanner);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteScanner(scanner.getDeviceId());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onDelete(int position, final IRBlasterAddRes.Datum ir) {
    }

    /*delete scanner */
    private void deleteScanner(String scannerId) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Common.showToast("" + getString(R.string.error_connect));
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().deleteDevice(scannerId, new DataResponseListener() {
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
                        getScannerList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(BeaconScannerAddActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(BeaconScannerAddActivity.this, getResources().getString(R.string.something_wrong1));
            }
        });

    }

    /**
     * Display Beacon scanner Edit Dialog
     * Create Singleton class for prevent multiple instances of Dialog
     */
    private Dialog getDialogContext() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(true);
            mDialog.setContentView(R.layout.dialog_beacon_scanner_edit);
        }
        return mDialog;
    }

    /**
     * @param
     */
    private void showScannerEditDialog(int position,final IRBlasterAddRes.Datum scanner) {

        mDialog = getDialogContext();
        mBeaconscannerName = mDialog.findViewById(R.id.edt_scanner_name);

        final Spinner mRoomSpinner = mDialog.findViewById(R.id.scanner_room_spinner);
        mRoomSpinner.setEnabled(false);
        mRoomSpinner.setClickable(false);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Remote Name max length to set only 25
        mBeaconscannerName.setFilters(filterArray);

        Button btnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button btnSave = mDialog.findViewById(R.id.btn_save);
        ImageView btnClose = mDialog.findViewById(R.id.iv_close);

        TextView txt_scanner_room_name = mDialog.findViewById(R.id.txt_scanner_room_name);
        txt_scanner_room_name.setFocusable(false);
        txt_scanner_room_name.setEnabled(false);


        String irblastername =  scannerList.get(position).getDeviceName();
        ChatApplication.logDisplay("Scanner name" + " " + irblastername);

        mBeaconscannerName.setText(irblastername);
        mBeaconscannerName.setSelection(mBeaconscannerName.getText().length());
        final ArrayAdapter roomAdapter = new ArrayAdapter(this, R.layout.spinner, roomListString);
        mRoomSpinner.setAdapter(roomAdapter);

        try {
            for (int i = 0; i < roomListString.size(); i++) {
                if (roomListString.get(i).equalsIgnoreCase(scanner.getRoom().getRoomName())) {
                    mRoomSpinner.setSelection(i);
                    txt_scanner_room_name.setText(scanner.getRoom().getRoomName());
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateBeaconScanner(mDialog, mBeaconscannerName, scanner.getDeviceId(),scanner);
            }
        });

        if (!mDialog.isShowing()) {
            mDialog.show();
        }

    }

    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     * @param mDialog
     * @param mScannerName
     * @param scannerId
     * @param scanner
     */
    private void updateBeaconScanner(final Dialog mDialog, EditText mScannerName, String scannerId, IRBlasterAddRes.Datum scanner) {

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Common.showToast("" + getString(R.string.error_connect));
            return;
        }

        if (TextUtils.isEmpty(mScannerName.getText().toString().trim())) {
            mScannerName.requestFocus();
            mScannerName.setError("Enter Scanner Name");
            return;
        }
        if (mBeaconscannerName != null) {
            hideSoftKeyboard(mBeaconscannerName);
        }

        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().updateDevice(scannerId,mScannerName.getText().toString().trim(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    if (code.equalsIgnoreCase("200")) {
                        mDialog.dismiss();
                        scanner.setDeviceName(mBeaconscannerName.getText().toString().trim());
                        beaconAddAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

    /* show dialog for add beacon scanner*/
    private void showBeaconScannerDialog(String ir_module_id) {

        if (irDialog == null) {
            irDialog = new Dialog(this);
            irDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            irDialog.setContentView(R.layout.dialog_add_sensordoor);
            irDialog.setCanceledOnTouchOutside(false);
        }

        final EditText edt_door_name = irDialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = irDialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = irDialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = irDialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = irDialog.findViewById(R.id.txt_sensor_name);

        dialogTitle.setText("Add Beacon Scanner");
        txt_sensor_name.setText("Scanner Name");

        edt_door_module_id.setText(ir_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(getApplicationContext(), roomNameList, 1, false);
        sp_room_list.setAdapter(customAdapter);

        Button btn_cancel = irDialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = irDialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = irDialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irDialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBeaconScanner(irDialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list);
            }
        });

        if (!irDialog.isShowing()) {
            irDialog.show();
        }

    }
    /*get room list*/
    private void getRoomList() {


        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getRoomList("rooom", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {}.getType();
                        roomListArray = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optString("data").toString(), type);

                        for(int i=0; i<roomListArray.size(); i++){
                            roomListString.add(roomListArray.get(i).getRoomName());
                        }

                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
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

            }
        });
    }


    /**
     * get Scanner list
     */
    private void getScannerList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            ChatApplication.showToast(getApplicationContext(), BeaconScannerAddActivity.this.getResources().getString(R.string.disconnect));
            return;
        }
        if (scannerList != null) {
            scannerList.clear();
        }
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getDeviceList("beacon_scanner", new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(), IRBlasterAddRes.class);
                    if (irBlasterAddRes.getCode() == 200) {
                        scannerList = irBlasterAddRes.getData();
                        scannerList = irBlasterAddRes.getData();

                        beaconAddAdapter = new BeaconAddAdapter(scannerList, BeaconScannerAddActivity.this);
                        mBeaconList.setAdapter(beaconAddAdapter);
                        beaconAddAdapter.notifyDataSetChanged();

                    } else if (irBlasterAddRes.getCode() == 301) {
                        scannerList = new ArrayList<>();
                        scannerList.clear();
                        BeaconAddAdapter scannerAddAdapter = new BeaconAddAdapter(scannerList, BeaconScannerAddActivity.this);
                        mBeaconList.setAdapter(scannerAddAdapter);
                        scannerAddAdapter.notifyDataSetChanged();
                        Common.showToast(irBlasterAddRes.getMessage());
                    } else {
                        Common.showToast(irBlasterAddRes.getMessage());
                    }

                    if (scannerList.size() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mBeaconList.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mBeaconList.setVisibility(View.VISIBLE);
                    }

                    getRoomList();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(BeaconScannerAddActivity.this, getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {

            }
        });

    }

    /**
     * save Beacon Scanner
     *
     * @param dialog
     * @param textInputEditText
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     */
    private void saveBeaconScanner(final Dialog dialog, EditText textInputEditText, String door_name,
                               String door_module_id, Spinner sp_room_list) {

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            textInputEditText.requestFocus();
            textInputEditText.setError("Enter IR Name");
            return;
        }

        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            ChatApplication.showToast(getApplicationContext(), BeaconScannerAddActivity.this.getResources().getString(R.string.disconnect));
            return;
        }

        int room_pos = sp_room_list.getSelectedItemPosition();
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().addBeaconScanner(door_name, door_module_id, roomIdList.get(room_pos), roomNameList.get(room_pos), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        if (!TextUtils.isEmpty(message)) {
                            Common.showToast(message);
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();
                        getScannerList();
                    } else {
                        Common.showToast(message);
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

            }
        });
    }
}
