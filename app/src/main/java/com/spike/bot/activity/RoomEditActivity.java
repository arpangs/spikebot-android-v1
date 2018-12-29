package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.RoomEditGridAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.dialog.DeviceEditDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/*Unuse class . IGONORS*/
public class RoomEditActivity extends AppCompatActivity implements ItemClickRoomEditListener {
    String TAG ="RoomEdit";

    RecyclerView mMessagesView;
    RoomEditGridAdapter roomEditGridAdapter ;
    EditText et_toolbar_title;
    RoomVO room;
    private Socket mSocket;
    boolean addRoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_toolbar_title = (EditText) toolbar.findViewById(R.id.et_toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        room = (RoomVO )getIntent().getSerializableExtra("room");

        et_toolbar_title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_toolbar_title.setText(room.getRoomName());

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, Constants.SWITCH_NUMBER);
        mMessagesView.setLayoutManager(gridLayoutManager);
        roomEditGridAdapter = new RoomEditGridAdapter(this, gridLayoutManager, this);
        roomEditGridAdapter.generateDataList(room);
        mMessagesView.setAdapter(roomEditGridAdapter);

        mMessagesView.getRecycledViewPool()
                .setMaxRecycledViews(0, 1);


        ActivityHelper.hideKeyboard(this);
        startSocketConnection();
        getDeviceList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ChatApplication.isEditActivityNeedResume){
            ChatApplication.isEditActivityNeedResume = false;
            getDeviceList();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void startSocketConnection(){
        ChatApplication app = (ChatApplication) getApplication();
        if(mSocket!=null && mSocket.connected()){
            Log.d("","mSocket.connected  return.." + mSocket.id() );
            return;
        }
        mSocket = app.getSocket();
        if(configureGatewayDevice!=null){
            mSocket.on("configureGatewayDevice", configureGatewayDevice);
            Log.d("","mSocket.connect()= "  + mSocket.id() );
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSocket!=null) {
            mSocket.off("configureGatewayDevice", configureGatewayDevice);
            Log.d("","mSocket onDestroy disconnect();");
        }
    }


    public AddRoomDialog addRoomDialog;
    private Emitter.Listener configureGatewayDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null) {
                        Log.d("configureGatewayDevice " + args.length,  mSocket.id() + " configureGatewayDevice RoomEdit " + args[0]);
                        //Log.d("configureGatewayDevice ", " configureGatewayDevice deviceid 2 " + args[1]);
                        String module_id = "" ;//args[0].toString();
                        String device_id = "";//args[1].toString();
                        String module_type = "";//args[2].toString();
                        try {
                            JSONObject obj = new JSONObject(args[0].toString());
                            module_id = obj.getString("module_id");
                            device_id = obj.getString("device_id");
                            module_type = obj.getString("module_type");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                           // if(addRoom){

                                ActivityHelper.dismissProgressDialog();

                                if(countDownTimer!=null){
                                    countDownTimer.cancel();
                                }
                            if(addRoomDialog!=null){
                                if(addRoomDialog.isShowing()){
                                    addRoomDialog.dismiss();
                                }
                            }
                                addRoomDialog = new AddRoomDialog(RoomEditActivity.this,room.getRoomId(),room.getRoomName(), module_id, device_id,module_type, new ICallback() {
                                    @Override
                                    public void onSuccess(String str) {
                                        Log.d("","str " + str );
                                        if(str.equalsIgnoreCase("yes")){
                                             //
                                            ChatApplication.isOpenDialog = true;
                                            ChatApplication.isRefreshDashBoard=true;
                                            ChatApplication.isMainFragmentNeedResume = true;
                                            getDeviceList();
                                        }
                                    }
                                });
                            if(!addRoomDialog.isShowing()){
                                addRoomDialog.show();
                            }

                                addRoom = false;
                                Log.d("configureGatewayDevice " ,  " configureGatewayDevice RoomEdit " + addRoom);
                          //  }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    CountDownTimer countDownTimer = new CountDownTimer(7000, 4000) {
        public void onTick(long millisUntilFinished) {
            Log.d("","getconfigureData configureGatewayDevice countDownTimer RoomEdit onTick " );
        }
        public void onFinish() {
            Log.d("","getconfigureData configureGatewayDevice countDownTimer RoomEdit onFinish " );
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            Toast.makeText(getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
        }

    };
    public void startTimer(){
        try {
            countDownTimer.start();
        } catch (Exception e) {
            Log.d("", "TimerTask configureGatewayDevice Exception " + e.getMessage());
        }
    }
    public void getconfigureData(){
        Log.d(TAG, "getconfigureData configureGatewayDevice");
        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this,"Searching Device attached ",false);
        startTimer();
        addRoom = true;

        String url =  ChatApplication.url + Constants.CONFIGURE_DEVICE_REQUEST;
        new GetJsonTask(this,url ,"POST","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getconfigureData onFailure " + error );
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Log.d("","action_add " );
            openAddPopup(findViewById(R.id.tv_header));
            return true;
        }
        else if (id == R.id.action_save) {
            Log.d("action_save","action_save " );
            //roomEditGridAdapter.notifyDataSetChanged();
           // roomEditGridAdapter.getValueFromView(mMessagesView);
            HashMap<String, String> selectedTextMap = roomEditGridAdapter.getTextValues();

            roomEditGridAdapter.getPanelEditValue();

            ArrayList<Object> mDataArrayList = roomEditGridAdapter.getDataArrayList();

            ArrayList<PanelVO> panelVOs = new ArrayList<>();
            for(int i=0;i<mDataArrayList.size();i++){

                if(mDataArrayList.get(i) instanceof PanelVO) {
                    PanelVO panelvo = (PanelVO) mDataArrayList.get(i);
                    panelVOs.add(panelvo);
                    Log.d("RoomEditTextValues : ","Panel1 : " + panelvo.getPanelName());
                }
            }
          /*  ArrayList<PanelVO> panelVOs = roomEditGridAdapter.getPanelArray();
            for(PanelVO panel:panelVOs){
                Log.d("roomEditGridAdapter"," panel.getPanelName() " + panel.getPanelName());
            }*/
            saveRoom(panelVOs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(RoomVO item, String action,View view) {
        //Log.d("isSensorClick","RoomVO isSensorClick okm......" + action);

    }

    @Override
    public void itemClicked(final PanelVO panelVO, String action, View view) {
        Log.d("isSensorClick","PanelVO isSensorClick okm......" + action);
        if(action.equalsIgnoreCase("delete")){
            //
            ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?" ,new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {

                    deletePanel(panelVO);

                    ChatApplication.isRefreshDashBoard=true;
                    ChatApplication.isMainFragmentNeedResume = true;
                }
                @Override
                public void onConfirmDialogNoClick() {

//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        }else if(action.equalsIgnoreCase("edit")){

            Log.d("DevicePanel","panelVO.getPanelId() : "+panelVO.getPanelId());

            Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
            intentPanel.putExtra("isDeviceAdd",true);
            intentPanel.putExtra("panelV0",panelVO);
            intentPanel.putExtra("roomId",room.getRoomId());
            intentPanel.putExtra("roomName",room.getRoomName());
            intentPanel.putExtra("panel_id",""+panelVO.getPanelId());
            intentPanel.putExtra("panel_name",""+panelVO.getPanelName());
            startActivity(intentPanel);
        }
    }

    @Override
    public void itemClicked(DeviceVO section, String action,View view) {
        Log.d("isSensorClick","DeviceVO isSensorClick okm......" + action);

        //openitemClickPopup(view,section);
        /*if(!action.equalsIgnoreCase("isSensor")){
            showDeviceEditDialog(section);
        }else{
            Intent intent = new Intent(getApplicationContext(),TempSensorInfoActivity.class);
            startActivity(intent);
        }*/
        showDeviceEditDialog(section);
    }

    private void deletePanel(PanelVO panelVO){


        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject object = new JSONObject();
        try {
            object.put("panel_id",panelVO.getPanelId());
            object.put("panel_name",panelVO.getPanelName());
            object.put("room_id",room.getRoomId());
            object.put("room_name",room.getRoomName());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("roomObj","ob : " + object.toString());

        //  ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);

        String url = ChatApplication.url + Constants.DELETE_ROOM_PANEL;


        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                Log.d(TAG, "getconfigureData onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                        getDeviceList();
                    }else{
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    /**
     *
     * @param deviceVO
     */

    JSONObject deviceObj;
    DeviceEditDialog deviceEditDialog;

    private void showDeviceEditDialog(final DeviceVO deviceVO){

        Log.d(TAG, "getSwitchDetails getSwitchDetails");
        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

       // ActivityHelper.showProgressDialog(this,"Please wait...",false);
        String url = ChatApplication.url + Constants.CHECK_INDIVIDUAL_SWITCH_DETAILS;

        JSONObject obj = new JSONObject();
        try {
            /*obj.put("module_id","1C7FC712004B1200");
            obj.put("device_id","4");*/
            obj.put("module_id",deviceVO.getModuleId());
            obj.put("device_id",deviceVO.getDeviceId()+"");
            obj.put("room_device_id",deviceVO.getRoomDeviceId()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetJsonTask(this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getSwitchDetails onSuccess " + result.toString());

               // ActivityHelper.dismissProgressDialog();
                try {

                    deviceEditDialog = new DeviceEditDialog(RoomEditActivity.this, deviceVO,result, new ICallback() {
                        @Override
                        public void onSuccess(String str) {
                            ChatApplication.isRefreshDashBoard=true;
                            ChatApplication.isMainFragmentNeedResume = true;
                            getDeviceList();
                        }
                    });
                    if(!deviceEditDialog.isShowing()){
                        deviceEditDialog.show();
                    }

                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d("", "getSwitchDetails onFailure " + error );
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }

    String action="";
    public void openAddPopup(final View v) {//,final ICallBackAction actionCallBack
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, v);

        popup.getMenuInflater().inflate(R.menu.menu_room_edit_add_popup,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(activity, " moveAction - "  ,Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_add_existing:
                        action="add_new_existing";
                        //
                        Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                        intentPanel.putExtra("roomId",room.getRoomId());
                        intentPanel.putExtra("roomName",room.getRoomName());
                        intentPanel.putExtra("isDeviceAdd",false);
                        startActivity(intentPanel);
                        break;
                    /*case R.id.action_add_panel:
                        action="add_panel";
                        showPanelOption();
                        //getconfigureData();

                        break;*/
                    case R.id.action_add_schedule:
                        action = "action_add_schedule";
                        Log.d("AddSchedule","RoomID : " +room.getRoomId());
                        Intent intent = new Intent(getApplicationContext(), ScheduleListActivity.class);
                        intent.putExtra("moodId3",room.getRoomId());
                        intent.putExtra("roomId",room.getRoomId());
                        intent.putExtra("selection",1);
                        intent.putExtra("isMoodAdapter",true);
                        startActivity(intent);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     *
     */
    private void showPanelOption(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        Button btn_sync = (Button)dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button)dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getconfigureData();
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                intentPanel.putExtra("roomId",room.getRoomId());
                intentPanel.putExtra("roomName",room.getRoomName());
                intentPanel.putExtra("isSync",true);
                intentPanel.putExtra("isDeviceAdd",false);
                startActivity(intentPanel);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(!dialog.isShowing()){
            dialog.show();
        }
    }


    public void saveRoom(ArrayList<PanelVO> panelVOs){

        boolean isEmptyPanel = false;

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            obj.put("room_id",room.getRoomId());
            obj.put("room_name",et_toolbar_title.getText().toString());

            //panelVOs

            JSONArray panelArray = new JSONArray();
            for(PanelVO panel : panelVOs){

                JSONObject objPanel = new JSONObject();
                objPanel.put("panel_id",panel.getPanelId());
                objPanel.put("panel_name",panel.getPanelName());
                objPanel.put("module_id",panel.getModule_id());
                panelArray.put(objPanel);

                if(TextUtils.isEmpty(panel.getPanelName())){
                    isEmptyPanel = true;
                }
            }
            obj.put("paneldata",panelArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isEmptyPanel){
            Toast.makeText(getApplicationContext(),"Enter Panel Name",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("textValuesPanel","obj " + obj.toString());

        String url = ChatApplication.url + Constants.SAVE_ROOM_AND_PANEL_NAME;

        ActivityHelper.showProgressDialog(RoomEditActivity.this,"Please wait...",false);

        new GetJsonTask(this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("getDeviceList", "getDeviceList onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        //Toast.makeText(getApplicationContext(), "Updated Successfully" , Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), TextUtils.isEmpty(message) ? "Updated Successfully" : message , Toast.LENGTH_SHORT).show();
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard=true;
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Log.d("", "getDeviceList onFailure " + error );
            }
        }).execute();
    }

    public void openitemClickPopup(final View v,final DeviceVO deviceVO) {//,final ICallBackAction actionCallBack
        PopupMenu popup = new PopupMenu(this, v);

        popup.getMenuInflater().inflate(R.menu.menu_room_edit_option_popup,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(activity, " moveAction - "  ,Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_edit:
                       /* Intent intent = new Intent(RoomEditActivity.this,ScheduleActivity.class);
                        intent.putExtra("item",deviceVO);
                        intent.putExtra("schedule",false);
                        startActivity(intent);*/
                       /* DeviceEditDialog deviceEditDialog = new DeviceEditDialog(RoomEditActivity.this, deviceVO, result,"", new ICallback() {
                            @Override
                            public void onSuccess(String str) {
                                ChatApplication.isRefreshDashBoard=true;
                                ChatApplication.isMainFragmentNeedResume = true;
                                getDeviceList();
                            }
                        });
                        deviceEditDialog.show();*/
                        break;
                    case R.id.action_delete:
                        //
                        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?",new ConfirmDialog.IDialogCallback() {
                            @Override
                            public void onConfirmDialogYesClick() {

                               // ChatApplication.isRefreshDashBoard=true;
                                ChatApplication.isEditActivityNeedResume = true;
                                ChatApplication.isMainFragmentNeedResume = true;

                                deleteDevice(deviceVO);
                            }

                            @Override
                            public void onConfirmDialogNoClick() {

//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
                            }

                        });
                        newFragment.show(getFragmentManager(), "dialog");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /*
    * Delete individual device item on room
    * */
    private void deleteDevice(DeviceVO deviceVO){

        if(!ActivityHelper.isConnectingToInternet(getApplicationContext())){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        JSONObject roomObj = new JSONObject();
        try {

            roomObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            roomObj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            roomObj.put("room_device_id",deviceVO.getRoomDeviceId());
            roomObj.put("module_id",deviceVO.getModuleId());
            roomObj.put("device_id",""+deviceVO.getDeviceId());

            roomObj.put("is_original",deviceVO.getIs_original());
            roomObj.put("room_type",0); //0 - Room   1-Mood

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("DeleteDeviceObj",""+roomObj.toString());

        String url = ChatApplication.url + Constants.DELETE_INDIVIDUAL_DEVICE;

        new GetJsonTask(getApplicationContext(),url ,"POST",roomObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                Log.d("DeleteDeviceObj", "DELETE_INDIVIDUAL_DEVICE onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(getApplicationContext(),  message , Toast.LENGTH_SHORT).show();
                        }

                    } else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
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


    public void getDeviceList(){
        Log.d(TAG, "getDeviceList");

        String url =  ChatApplication.url + Constants.GET_EDIT_ROOM_INFO+"/"+room.getRoomId();

        new GetJsonTask(this,url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
                try {
                    ArrayList<RoomVO> roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");

                    roomList = JsonHelper.parseRoomArray(roomArray,false);
                    if(roomList.size()>0) {
                        room = roomList.get(0);
                        et_toolbar_title.setText(room.getRoomName());
                    }
                    roomEditGridAdapter.generateDataList(room);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomEditGridAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getDeviceList onFailure " + error );
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}
