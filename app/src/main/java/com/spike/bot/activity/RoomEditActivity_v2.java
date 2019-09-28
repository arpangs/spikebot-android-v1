package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SmartDevice.AddDeviceConfirmActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteAdd;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.activity.ir.blaster.WifiBlasterActivity;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.room.RoomEditAdapterV2;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.dialog.AddRoomDialog;
import com.spike.bot.dialog.DeviceEditDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.model.SensorUnassignedRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditActivity_v2 extends AppCompatActivity implements ItemClickRoomEditListener {

    String TAG ="RoomEdit";

    RecyclerView mMessagesView;
    RoomEditAdapterV2 roomEditGridAdapter ;
    EditText et_toolbar_title;
    RoomVO room;
    private Socket mSocket;
    boolean addRoom = false;
    private LinearLayout txt_empty_room,vv_delete_button;
    private Button btn_edit_room_delete;
    private ImageView empty_add_image;

    private FloatingActionButton mFab;
    private CardView mFabMenuLayout;
    private TextView fab_menu1,fab_menu2,fab_menu3,fab_menu4,fab_menu5,fab_menu6,fab_menu7,fab_Sensor,fabGas,fabCurtain;

    public boolean addIRBlasterSensor = false,addTempSensor = false;;
    public static int SENSOR_TYPE_DOOR = 1, SENSOR_TYPE_TEMP = 2 , SENSOR_TYPE_IR = 3,SENSOR_MULTITYPE=4,SENSOR_GAS=5,Curtain=6;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_v2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_toolbar_title = (EditText) findViewById(R.id.et_toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mMessagesView = (RecyclerView) findViewById(R.id.list_room_edit);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        room = (RoomVO )getIntent().getSerializableExtra("room");

        toolbar.setTitle("Room Details");

        txt_empty_room = (LinearLayout) findViewById(R.id.txt_empty_room);
        vv_delete_button = (LinearLayout) findViewById(R.id.vv_delete_button);
        txt_empty_room.setVisibility(View.GONE);
        btn_edit_room_delete = (Button) findViewById(R.id.btn_edit_room_delete);

        et_toolbar_title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_toolbar_title.setText(room.getRoomName());
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Set room name max length 25
        et_toolbar_title.setFilters(filterArray);

        empty_add_image = (ImageView) findViewById(R.id.empty_add_image);

        //setting the recycler view
        mMessagesView.setLayoutManager(new GridLayoutManager(this,1));
        roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), this,this);
        mMessagesView.setAdapter(roomEditGridAdapter);

        btn_edit_room_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoomAction(room);
            }
        });

        mMessagesView.getRecycledViewPool()
                .setMaxRecycledViews(0, 1);


      //  ActivityHelper.hideKeyboard(this);
        startSocketConnection();

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanelOption();
            }
        });

        et_toolbar_title.setImeOptions(EditorInfo.IME_ACTION_DONE);


        final View activityRootView = findViewById(R.id.coordinator_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > Common.dpToPx(getApplicationContext(),100)) { // if more than 100 pixels, its probably a keyboard...
                    vv_delete_button.setVisibility(View.GONE);
                }else {
                    vv_delete_button.setVisibility(View.VISIBLE);
                }
            }
        });


        mFabMenuLayout = (CardView) findViewById(R.id.fabLayout1);
        fab_menu1 = (TextView) findViewById(R.id.fab_menu1);
        fab_menu2 = (TextView) findViewById(R.id.fab_menu2);
        fab_menu3 = (TextView) findViewById(R.id.fab_menu3);
        fab_menu4 = (TextView) findViewById(R.id.fab_menu4);
        fab_menu5 = (TextView) findViewById(R.id.fab_menu5);
        fab_menu6 = (TextView) findViewById(R.id.fab_menu6);
        fab_menu7 = (TextView) findViewById(R.id.fab_menu7);
        fab_Sensor =  findViewById(R.id.fab_Sensor);
        fabGas =  findViewById(R.id.fabGas);
        fabCurtain =  findViewById(R.id.fabCurtain);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                ChatApplication.isOpenDialog = false;
                action="add_panel";
                showPanelOption();
            }
        });

        fab_menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                action = "action_add_schedule";
                Intent intent = new Intent(getApplicationContext(), ScheduleListActivity.class);
                intent.putExtra("moodId3",room.getRoomId());
                intent.putExtra("roomId",room.getRoomId());
                intent.putExtra("roomName",room.getRoomName());
                intent.putExtra("selection",1);
                intent.putExtra("isMoodAdapter",true);
                startActivity(intent);
            }
        });

        fab_menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_TYPE_DOOR);
            }
        });

        fab_menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_TYPE_TEMP);
            }
        });

        fab_menu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent=new Intent(RoomEditActivity_v2.this,IRRemoteAdd.class);
                intent.putExtra("roomName",""+et_toolbar_title.getText().toString());
                intent.putExtra("roomId",""+room.getRoomId());
                startActivity(intent);
                //startActivity(new Intent(RoomEditActivity_v2.this, IRRemoteAdd.class));
            }
        });

        fab_menu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                //startActivity(new Intent(RoomEditActivity_v2.this, IRBlasterAddActivity.class));

                showOptionDialogIr(SENSOR_TYPE_IR);
//                Intent intent=new Intent(RoomEditActivity_v2.this,WifiBlasterActivity.class);
//                intent.putExtra("roomId",""+room.getRoomId());
//                startActivity(intent);
            }
        });

        fab_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_MULTITYPE);
            }
        });

        fabGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(SENSOR_GAS);
            }
        });

        fabCurtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showOptionDialog(Curtain);
            }
        });

        getDeviceList();
    }

    /**
     * Configure Gateway Door Sensor
     */
    int count=0;
    private Emitter.Listener configureGatewayDoorSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        JSONObject object = new JSONObject(args[0].toString());
                        String door_sensor_module_id = object.getString("door_sensor_module_id");

                        String message = object.getString("message");

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
                            if(count==0){
                                count=1;
                                Intent intent=new Intent(RoomEditActivity_v2.this, AddDeviceConfirmActivity.class);
                                intent.putExtra("isViewType","syncDoor");
                                intent.putExtra("door_sensor_module_id",""+door_sensor_module_id);
                                intent.putExtra("door_type","1");
                                startActivity(intent);
                            }
                        } else {
                            showConfigAlert(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };


    private Emitter.Listener configureGasSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        // {"message":"","gas_sensor_module_id":"90A1131A004B1200",
                        // "room_list":[{"room_id":"1568871959065_EhP9gsNij","room_name":"jhanvi"},{"room_id":"1568878822877_a22pysXNL","room_name":"vpj"}]}
                        roomIdList.clear();
                        roomNameList.clear();

                        //message, gas_sensor_module_id,room_list
                        JSONObject object = new JSONObject(args[0].toString());

                        String message = object.getString("message");

                        String temp_sensor_module_id = object.getString("gas_sensor_module_id");

                        ChatApplication.logDisplay("gas is "+object);
                        if(TextUtils.isEmpty(message)){
                           /* String roomList = object.getString("room_list");

                            Object json = new JSONTokener(roomList).nextValue();*/

                            JSONArray jsonArray = object.getJSONArray("room_list");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }

                           /* if (json instanceof JSONObject){

                            }else if (json instanceof JSONArray){

                            }*/
                        }

                        ActivityHelper.dismissProgressDialog();

                        if(TextUtils.isEmpty(message)){
                            showGasSensor(temp_sensor_module_id,true,false);
                        }else{
                            showConfigAlert(message);
                        }

                        //  addRoom = false;
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };



    private Emitter.Listener configureCurtain = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

//                        ActivityHelper.dismissProgressDialog();
                        roomIdList.clear();
                        roomNameList.clear();

                        //{"message":"","curtain_module_id":"8AA1131A004B1200","module_type":3,"device_id":"2"}
                        // {"message":"Curtain is already configured before","curtain_module_id":""}
                        //{
                        //  "message": "",
                        //  "curtain_module_id": "8AA1131A004B1200",
                        //  "module_type": 3,
                        //  "device_id": "2",
                        //  "room_list": [
                        //    {
                        //      "room_id": "1569495690914_2SK_n6rX7",
                        //      "room_name": "vip"
                        //    },
                        //    {
                        //      "room_id": "1569564785527_3pspddidL",
                        //      "room_name": "jLock"
                        //    },
                        //    {
                        //      "room_id": "1569579320536_XEt-DV9HU",
                        //      "room_name": "hitesh"
                        //    }
                        //  ]
                        //}
                        JSONObject object = new JSONObject(args[0].toString());

                        String message = object.optString("message");

                        String temp_sensor_module_id = object.optString("curtain_module_id");

                        ChatApplication.logDisplay("cartain is "+object);
                        if(TextUtils.isEmpty(message)){
                           /* String roomList = object.getString("room_list");

                            Object json = new JSONTokener(roomList).nextValue();*/

                            JSONArray jsonArray = object.getJSONArray("room_list");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }
                        }

                        ActivityHelper.dismissProgressDialog();

                        if(TextUtils.isEmpty(message)){
                            showGasSensor(temp_sensor_module_id,true,true);
                        }else{
                            showConfigAlert(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };


    /**
     * Display Fab Menu with subFab Button
     *
     */
    boolean isFABOpen = false;
    private void showFABMenu(){

        isFABOpen=true;
        mFabMenuLayout.setVisibility(View.VISIBLE);
       // mFab.animate().rotationBy(180);
       // mFabMenuLayout.animate().translationY(-getResources().getDimension(R.dimen.fab_top_height));
    }
    private void closeFABMenu(){
        isFABOpen=false;
        mFabMenuLayout.setVisibility(View.GONE);
       // mFab.animate().rotationBy(-180);
       // mFabMenuLayout.animate().translationY(0);
        if(!isFABOpen){
            mFabMenuLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
          //  vv_delete_button.setVisibility(View.GONE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
          //  vv_delete_button.setVisibility(View.VISIBLE);
        }
    }

    private void deleteRoomAction(final RoomVO room){

        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ? \nNote: All the devices and sensors associated with this room will be deleted" ,new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {

                deleteRoom(room);

            }
            @Override
            public void onConfirmDialogNoClick() {

//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * Delete Room
     * @param roomVO
     */

    private void deleteRoom(RoomVO roomVO){


        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject object = new JSONObject();
        try {
            object.put("room_id",roomVO.getRoomId());
            object.put("room_name",roomVO.getRoomName());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_ROOM;

        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();
                    }

                    Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(IRRemoteBrandListActivity.arrayList!=null){
            IRRemoteBrandListActivity.arrayList=null;
        }
        Constants.isWifiConnect=false;
        Constants.isWifiConnectSave=false;
//        ActivityHelper.hideKeyboard(this);
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
            return;
        }
        mSocket = app.getSocket();
        if(mSocket!=null){
            mSocket.on("configureGatewayDevice", configureGatewayDevice);
            mSocket.on("configureTempSensor", configureTempSensor);
            mSocket.on("configureDoorSensor", configureGatewayDoorSensor);
            mSocket.on("configureMultiSensor", configureMultiSensor);
            mSocket.on("configureGasSensor", configureGasSensor);
            mSocket.on("configureCurtain", configureCurtain);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSocket!=null) {
            mSocket.off("configureGatewayDevice", configureGatewayDevice);
            mSocket.off("configureTempSensor", configureTempSensor);
            mSocket.off("configureMultiSensor", configureMultiSensor);
            mSocket.off("configureDoorSensor", configureGatewayDoorSensor);
            mSocket.off("configureGasSensor", configureGasSensor);
            mSocket.off("configureCurtain", configureCurtain);
        }
    }

    private void showGasSensor(String door_module_id, final boolean isTempSensorRequest, final boolean isMultiSensor){

        final Dialog dialog = new Dialog(RoomEditActivity_v2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = (EditText) dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) dialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) dialog.findViewById(R.id.txt_sensor_name);

        if(isMultiSensor){
            dialogTitle.setText("Add Curtain Sensor");
            txt_sensor_name.setText("Curtain Name");
        }else {
            dialogTitle.setText("Add Gas Sensor");
            txt_sensor_name.setText("Gas Name");
        }

        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(RoomEditActivity_v2.this,roomNameList,1,false);
        sp_room_list.setAdapter(customAdapter);

        int spinner_position = sp_room_list.getSelectedItemPosition();

        sp_room_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_door_name.getText().toString().trim().length() == 0) {
                    ChatApplication.showToast(RoomEditActivity_v2.this, "Please enter name");
                } else {
                    isShowSensor = false;
                    if (isMultiSensor) {
                        addCurtain(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                        dialog.dismiss();
                    }else {
                        saveMultiSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                        dialog.dismiss();
                    }

                }
            }
        });

        if(!dialog.isShowing() && !isShowSensor){
            isShowSensor = true;
            dialog.show();
        }

    }

    private void addCurtain(final Dialog dialog, EditText textInputEditText, String door_name,
                            String door_module_id, Spinner sp_room_list, final boolean isTempSensorRequest) {


        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {

            //{
            //    "phone_id": "6929FCC2-5124-4D93-B98B-0E1C100109CD",
            //    "phone_type": "IOS",
            //    "room_id": "1569564785527_3pspddidL",
            //    "room_name": "jLock",
            //    "user_id": "1569305102710_GtdsJ7yBl",
            //    "curtain_module_id": "8AA1131A004B1200",
            //    "curtain_name": "bari nu curtain"
            //}

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("room_id",roomIdList.get(room_pos));
            obj.put("room_name",roomNameList.get(room_pos));
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("curtain_name", door_name);
            obj.put("curtain_module_id",door_module_id);
            //curtain_type = h , v
            obj.put("curtain_type","v");

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.curtainadd;

        new GetJsonTask(RoomEditActivity_v2.this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        getDeviceList();
                    }
                    else{
                        Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
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
            }
        }).execute();
    }

    /**
     * showAddSensorDialog
     * @param door_module_id
     * @param isTempSensorRequest
     */
    private void showAddSensorDialog(String door_module_id, final boolean isTempSensorRequest, final boolean isMultiSensor){

        final Dialog dialog = new Dialog(RoomEditActivity_v2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sensordoor);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edt_door_name = (EditText) dialog.findViewById(R.id.txt_door_sensor_name);
        final TextView edt_door_module_id = (TextView) dialog.findViewById(R.id.txt_module_id);
        final Spinner sp_room_list = (Spinner) dialog.findViewById(R.id.sp_room_list);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView txt_sensor_name = (TextView) dialog.findViewById(R.id.txt_sensor_name);

        if(isTempSensorRequest){
            dialogTitle.setText("Add Temp Sensor");
            txt_sensor_name.setText("Temp Name");
        }else if(isMultiSensor){
            dialogTitle.setText("Add Multi Sensor");
            txt_sensor_name.setText("Sensor Name");
        }else{
            dialogTitle.setText("Add Door Sensor");
            txt_sensor_name.setText("Door Name");
        }
        edt_door_module_id.setText(door_module_id);
        edt_door_module_id.setFocusable(false);

        TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(RoomEditActivity_v2.this,roomNameList,1,false);
        sp_room_list.setAdapter(customAdapter);

        int spinner_position = sp_room_list.getSelectedItemPosition();

        sp_room_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_door_cancel);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_door_save);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSensor = false;
                if(isMultiSensor){
                    saveMultiSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                }else {
                    saveSensor(dialog, edt_door_name, edt_door_name.getText().toString(), edt_door_module_id.getText().toString(), sp_room_list, isTempSensorRequest);
                }
                dialog.dismiss();
            }
        });

        if(!dialog.isShowing() && !isShowSensor){
            isShowSensor = true;
            dialog.show();
        }

    }

    /**
     * Display Alert dialog when found door or temp sensor config already configured
     * @param alertMessage
     */
    private void showConfigAlert(String alertMessage){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RoomEditActivity_v2.this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /**
     * @param dialog - for dismiss
     * @param door_name
     * @param door_module_id
     * @param sp_room_list
     * @param isTempSensorRequest
     */

    private void saveMultiSensor(final Dialog dialog, EditText textInputEditText, String door_name,
                            String door_module_id, Spinner sp_room_list, final boolean isTempSensorRequest){

        if(!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)){
            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(textInputEditText.getText().toString())){
            textInputEditText.setError("Enter Gas Name");
            textInputEditText.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("room_id",roomIdList.get(room_pos));
            obj.put("room_name",roomNameList.get(room_pos));
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            obj.put("gas_sensor_name", door_name);
            obj.put("gas_sensor_module_id",door_module_id);

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.addGasSensor;

        new GetJsonTask(RoomEditActivity_v2.this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        getDeviceList();
                    }
                    else{
                        Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
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
            }
        }).execute();
    }

    private void saveSensor(final Dialog dialog, EditText textInputEditText, String door_name,
                            String door_module_id, Spinner sp_room_list, final boolean isTempSensorRequest){

        if(!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)){
            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(textInputEditText.getText().toString())){
            textInputEditText.setError(isTempSensorRequest ? "Enter Temp Name" : "Enter Door Name");
            textInputEditText.requestFocus();
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait...",false);

        JSONObject obj = new JSONObject();
        try {

            // "temp_sensor_module_id"	: "",
            //  "temp_sensor_name"	: "",
            //  "room_id":"",
            //  "room_name":"",
            //  "user_id":"",
            //  "phone_id":"",
            //  "phone_type":""

            int room_pos = sp_room_list.getSelectedItemPosition();

            obj.put("room_id",roomIdList.get(room_pos));
            obj.put("room_name",roomNameList.get(room_pos));
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));

            if (isTempSensorRequest) {
                obj.put("temp_sensor_name", door_name);
                obj.put("temp_sensor_module_id",door_module_id);
            }else {
                obj.put("door_sensor_name", door_name);
                obj.put("door_sensor_module_id",door_module_id);
            }
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        if (isTempSensorRequest){
            url = ChatApplication.url + Constants.ADD_TEMP_SENSOR;
        }else{
            url = ChatApplication.url + Constants.ADD_DOOR_SENSOR;
        }

        new GetJsonTask(RoomEditActivity_v2.this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code==200){

                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        }
                        ActivityHelper.dismissProgressDialog();
                        dialog.dismiss();

                        getDeviceList();
                    }
                    else{
                        Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), message , Toast.LENGTH_SHORT).show();
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
            }
        }).execute();
    }


    ArrayList<String> roomIdList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    private boolean isShowSensor = false;

    private Emitter.Listener configureTempSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        //{"message":"Temperature Sensor already configured. Add from Unconfigured Sensor devices","temp_module_id":"","room_list":""}
                        JSONObject object = new JSONObject(args[0].toString());

                        String message = object.getString("message");

                        String temp_sensor_module_id = object.getString("temp_sensor_module_id");

                        if(TextUtils.isEmpty(message)){
                           /* String roomList = object.getString("room_list");

                            Object json = new JSONTokener(roomList).nextValue();*/

                            JSONArray jsonArray = object.getJSONArray("room_list");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }

                           /* if (json instanceof JSONObject){

                            }else if (json instanceof JSONArray){

                            }*/
                        }

                        ActivityHelper.dismissProgressDialog();

                        if(TextUtils.isEmpty(message)){
                            showAddSensorDialog(temp_sensor_module_id,true,false);
                        }else{
                            showConfigAlert(message);
                            //Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        }


                        //  addRoom = false;
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


 private Emitter.Listener configureMultiSensor = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            RoomEditActivity_v2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        roomIdList.clear();
                        roomNameList.clear();

                        JSONObject object = new JSONObject(args[0].toString());

                        ChatApplication.logDisplay("multi sensor is "+object);

                        String message = object.getString("message");
//parameter: message, multi_sensor_module_id,room_list
                        String multi_sensor_module_id = object.getString("multi_sensor_module_id");
//
                        if(TextUtils.isEmpty(message)){

                            JSONArray jsonArray = object.getJSONArray("room_list");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject objectRoom = jsonArray.getJSONObject(i);
                                String room_id = objectRoom.getString("room_id");
                                String room_name = objectRoom.getString("room_name");

                                roomIdList.add(room_id);
                                roomNameList.add(room_name);
                            }
                        }

                        ActivityHelper.dismissProgressDialog();

                        if(TextUtils.isEmpty(message)){
                            showAddSensorDialog(multi_sensor_module_id,false,true);
                        }else{
                            showConfigAlert(message);
                        }
//

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };



    public AddRoomDialog addRoomDialog;
    private Emitter.Listener configureGatewayDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null) {
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
                            addRoomDialog = new AddRoomDialog(RoomEditActivity_v2.this,room.getRoomId(),room.getRoomName(), module_id, device_id,module_type, new ICallback() {
                                @Override
                                public void onSuccess(String str) {
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
        }
        public void onFinish() {
            addRoom = false;
            ActivityHelper.dismissProgressDialog();
            ChatApplication.showToast(getApplicationContext(),"No New Device detected!");
        }

    };
    public void startTimer(){
        try {
            countDownTimer.start();
        } catch (Exception e) {
        }
    }


    public void getconfigureDoor() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(this, "Searching Device attached ", false);
        startTimer();
        addRoom = true;
        String url = ChatApplication.url + Constants.CONFIGURE_DOOR_SENSOR_REQUEST;
        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL //POST
            @Override
            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void getconfigureData(){
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
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menu.findItem(R.id.action_add).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent RoomEditActivity_v2.this inm AndroidManifest.xml.
        // Handle the popup menu dialog

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            openAddPopup(findViewById(R.id.action_add));
            return true;
        }
        else if (id == R.id.action_save) {
            ActivityHelper.hideKeyboard(this);

            roomEditGridAdapter.getPanelEditValue();

            ArrayList<PanelVO> mDataArrayList = roomEditGridAdapter.getDataArrayList();

            ArrayList<PanelVO> panelVOs = new ArrayList<>();
            for(int i=0;i<mDataArrayList.size();i++){

                if(mDataArrayList.get(i) != null) {
                    PanelVO panelvo = (PanelVO) mDataArrayList.get(i);
                    panelVOs.add(panelvo);
                }
            }

            saveRoom(panelVOs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void saveRoom(ArrayList<PanelVO> panelVOs){

        if(TextUtils.isEmpty(et_toolbar_title.getText().toString())){
            et_toolbar_title.setError(Html.fromHtml("<font color='#FFFFFF'>Enter Room Name</font>"));
            et_toolbar_title.requestFocus();
            return;
        }

        boolean isEmptyPanel = false;

        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
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
           // Toast.makeText(getApplicationContext(),"Enter Panel Name",Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ChatApplication.url + Constants.SAVE_ROOM_AND_PANEL_NAME;

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait...",false);

        new GetJsonTask(this,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){
                        //Toast.makeText(getApplicationContext(), "Updated Successfully" , Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshDashBoard=true;
                        ChatApplication.isMainFragmentNeedResume = true;
                        finish();

                    }else if(code == 301){
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
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
            }
        }).execute();
    }

    public void getDeviceList(){
//        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_id", room.getRoomId());
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url =  ChatApplication.url + Constants.GET_EDIT_ROOM_INFO;

        new GetJsonTask(this,url ,"POST",jsonObject.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ActivityHelper.dismissProgressDialog();
                    ArrayList<RoomVO> roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    ChatApplication.logDisplay("result is "+roomArray);
                    roomList = JsonHelper.parseRoomArray(roomArray,false);
                    if(roomList.size()>0) {
                        room = roomList.get(0);
                        et_toolbar_title.setText(room.getRoomName());

                        if(room.getPanelList()!=null && room.getPanelList().size()>0){
                            roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), RoomEditActivity_v2.this,RoomEditActivity_v2.this);
                            mMessagesView.setAdapter(roomEditGridAdapter);
                            roomEditGridAdapter.notifyDataSetChanged();
                        }

                    }
                   // roomEditGridAdapter.generateDataList(room);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                            ActivityHelper.dismissProgressDialog();
//                            roomEditGridAdapter = new RoomEditAdapterV2(room.getPanelList(), RoomEditActivity_v2.this,RoomEditActivity_v2.this);
//                            mMessagesView.setAdapter(roomEditGridAdapter);
//                            roomEditGridAdapter.notifyDataSetChanged();
//                        }
//                    });

                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
                    if(room.getPanelList().size()==0){
                        txt_empty_room.setVisibility(View.VISIBLE);
                        mMessagesView.setVisibility(View.GONE);
                    }else{
                        txt_empty_room.setVisibility(View.GONE);
                        mMessagesView.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                if(room.getPanelList().size()==0){
                    txt_empty_room.setVisibility(View.VISIBLE);
                    mMessagesView.setVisibility(View.GONE);
                }else{
                    txt_empty_room.setVisibility(View.GONE);
                    mMessagesView.setVisibility(View.VISIBLE);
                }
            }
        }).execute();
    }

    @Override
    public void itemClicked(RoomVO item, String action,View view) {
        closeFABMenu();
    }

    @Override
    public void itemClicked(final PanelVO panelVO, String action, View view) {
        closeFABMenu();
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

//                      Toast.makeText(RoomEditActivity_v2.this, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
                }
            });
            newFragment.show(getFragmentManager(), "dialog");

        }else if(action.equalsIgnoreCase("edit")){

            Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
            intentPanel.putExtra("isDeviceAdd",true);
            intentPanel.putExtra("panelV0",panelVO);
            intentPanel.putExtra("roomId",room.getRoomId());
            intentPanel.putExtra("roomName",room.getRoomName());
            intentPanel.putExtra("panel_id",""+panelVO.getPanelId());
            intentPanel.putExtra("panel_name",""+panelVO.getPanelName());
            startActivity(intentPanel);
        }else if(action.equals("sensorPanel")){
        }
    }

    @Override
    public void itemClicked(DeviceVO section, String action,View view) {
        closeFABMenu();
        if(action.equalsIgnoreCase("1")){
            ActivityHelper.showProgressDialog(this,"Please wait.",false);
            showDeviceEditDialog(section);
        }else if(action.equalsIgnoreCase("isSensorClick")){
        }
    }

    /**
     * Display IR Blaster Edit Dialog
     * Create Singleton class for prevent multiple instances of Dialog
     */
    private Dialog mDialog;
    EditText mBlasterName;
    private Dialog getDialogContext(){
        if(mDialog == null){
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(true);
            mDialog.setContentView(R.layout.dialog_ir_blaster_edit);
        }
        return mDialog;
    }

    /**
     *
     * @param ir
     */
    private void showBlasterEditDialog(final IRBlasterAddRes.Data.IrList ir){

        mDialog = getDialogContext();
        mBlasterName = (EditText) mDialog.findViewById(R.id.edt_blaster_name);
        final Spinner mRoomSpinner = (Spinner)mDialog.findViewById(R.id.blaster_room_spinner);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25); //Remote Name max length to set only 25
        mBlasterName.setFilters(filterArray);

        Button btnCancel = (Button) mDialog.findViewById(R.id.btn_cancel);
        Button btnSave = (Button) mDialog.findViewById(R.id.btn_save);
        ImageView btnClose = (ImageView) mDialog.findViewById(R.id.iv_close);


        mBlasterName.setText(ir.getIrBlasterName());

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

                IRBlasterAddRes.Data.RoomList roomList = (IRBlasterAddRes.Data.RoomList) mRoomSpinner.getSelectedItem();
              //  updateBlaster(mDialog,mBlasterName,roomList,ir.getIrBlasterId());
            }
        });

        if(!mDialog.isShowing()){
            mDialog.show();
        }

    }


    /**
     *
     * @param deviceVO
     */

    JSONObject deviceObj;
    DeviceEditDialog deviceEditDialog;

    private void showDeviceEditDialog(final DeviceVO deviceVO){

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

                 ActivityHelper.dismissProgressDialog();
                try {

                    deviceEditDialog = new DeviceEditDialog(RoomEditActivity_v2.this, deviceVO,result, new ICallback() {
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
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();

    }

    private void deletePanel(PanelVO panelVO){


        if (!ActivityHelper.isConnectingToInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this, "Please wait...", false);

        JSONObject object = new JSONObject();
        try {
            object.put("panel_id",panelVO.getPanelId());
            object.put("panel_name",panelVO.getPanelName());
            object.put("room_id",room.getRoomId());
            object.put("room_name",room.getRoomName());
            object.put("panel_type",panelVO.getPanel_type());
          //  object.put("is_sensor_panel",panelVO.isSensorPanel() ? 1 : 0);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ChatApplication.url + Constants.DELETE_ROOM_PANEL;


        new GetJsonTask(getApplicationContext(), url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
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
                        Intent intent = new Intent(getApplicationContext(), ScheduleListActivity.class);
                        intent.putExtra("moodId3",room.getRoomId());
                        intent.putExtra("roomId",room.getRoomId());
                        intent.putExtra("roomName",room.getRoomName());
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
        Button btn_add_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);

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

                getUnasignedDeviceList();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_add_from_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                intentPanel.putExtra("roomId",room.getRoomId());
                intentPanel.putExtra("roomName",room.getRoomName());
                intentPanel.putExtra("isDeviceAdd",false);
                startActivity(intentPanel);
            }
        });

        if(!dialog.isShowing()){
            dialog.show();
        }
    }


    private void showOptionDialog(final int sensor_type){

        final Dialog dialog = new Dialog(RoomEditActivity_v2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Sensor Type");

        Button btn_sync = (Button)dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button)dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(sensor_type == SENSOR_TYPE_DOOR){
//                    getconfigureData();
                    getconfigureDoor();
                }else if(sensor_type == SENSOR_TYPE_TEMP){
                    getTempConfigData(false);
                }else if(sensor_type == SENSOR_TYPE_IR){
                    getIRBlasterConfigData();
                }else if(sensor_type == SENSOR_MULTITYPE){
                    getTempConfigData(true);
                }else if(sensor_type == SENSOR_GAS){
                    getgasConfigData(SENSOR_GAS);
                }else if(sensor_type == Curtain){
                    getgasConfigData(Curtain);
                }
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isUnassignedDoorSensor(sensor_type);

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


    private void getIRBlasterConfigData() {
        if (!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)) {
            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Searching IR Blaster attached ", false);
        startTimer();
        addIRBlasterSensor = true;
        String url = ChatApplication.url + Constants.CONFIGURE_IR_BLASTER_REQUEST;
        new GetJsonTask(RoomEditActivity_v2.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    // Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void getgasConfigData(int type) {
        if (!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)) {
            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Searching Device attached ", false);
        startTimer();

        addTempSensor = true;
        String url="";
        if(type==SENSOR_GAS){
           url= ChatApplication.url + Constants.configureGasSensorRequest;
        }else {
            url = ChatApplication.url + Constants.curtainconfigure;
        }


        new GetJsonTask(RoomEditActivity_v2.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void getTempConfigData(boolean flag) {
        if (!ActivityHelper.isConnectingToInternet(RoomEditActivity_v2.this)) {
            Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(RoomEditActivity_v2.this, "Searching Device attached ", false);
        startTimer();

        addTempSensor = true;

        String url="";
        if(flag){
            url = ChatApplication.url + Constants.configureMultiSensorRequest;
        }else {
            url = ChatApplication.url + Constants.CONFIGURE_TEMP_SENSOR_REQUEST;
        }

        new GetJsonTask(RoomEditActivity_v2.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
//                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(RoomEditActivity_v2.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void isUnassignedDoorSensor(){

        String url = "";
        url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir

        new GetJsonTask(RoomEditActivity_v2.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(),SensorUnassignedRes.class);

                if(sensorUnassignedRes.getCode() == 200){

                    if(sensorUnassignedRes.getData()!=null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0){
                        Intent intent = new Intent(RoomEditActivity_v2.this, SensorUnassignedActivity.class);
                        intent.putExtra("isDoorSensor",2);
                        intent.putExtra("roomName",et_toolbar_title.getText().toString());
                        startActivity(intent);
                    }else{
                        Toast.makeText(RoomEditActivity_v2.this,sensorUnassignedRes.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();


    }

    /*
  * get method fro call getOriginalDevices api
  * delete device list panel list
  * */
    ArrayList<RoomVO> roomList = new ArrayList<>();
    private void getUnasignedDeviceList() {

        roomList.clear();
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = "";
        url = ChatApplication.url + Constants.GET_ORIGINAL_DEVICES + "/0";

       // ActivityHelper.showProgressDialog(RoomEditActivity_v2.this,"Please wait...",false);

        new GetJsonTask(getApplicationContext(), url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();

                try {

                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseExistPanelArray(roomArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
                if(roomList.size()==0){
                    Toast.makeText(ChatApplication.getInstance(), "No Unassigned Panel Found.", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intentPanel = new Intent(getApplicationContext(), AddExistingPanel.class);
                    intentPanel.putExtra("roomId",room.getRoomId());
                    intentPanel.putExtra("roomName",room.getRoomName());
                    intentPanel.putExtra("isSync",true);
                    intentPanel.putExtra("isDeviceAdd",false);
                    startActivity(intentPanel);
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    private void showOptionDialogIr(final int sensorType){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_panel_option);

        TextView txtDialogTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
        txtDialogTitle.setText("Select Sensor Type");

        Button btn_sync = (Button)dialog.findViewById(R.id.btn_panel_sync);
        Button btn_unaasign = (Button)dialog.findViewById(R.id.btn_panel_unasigned);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_panel_cancel);
        Button btn_from_existing = (Button) dialog.findViewById(R.id.add_from_existing);
        btn_from_existing.setVisibility(View.GONE);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(RoomEditActivity_v2.this,WifiBlasterActivity.class);
                intent.putExtra("roomId",""+room.getRoomId());
                intent.putExtra("roomName",""+room.getRoomName());
                startActivity(intent);
            }
        });
        btn_unaasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isUnassignedDoorSensor(sensorType);

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
    private void isUnassignedDoorSensor(final int isDoorSensor){
        ActivityHelper.showProgressDialog(this, "Please wait...", false);
        String url = "";
        //muitl == 5
        if(isDoorSensor==3){
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir
        }if(isDoorSensor==Curtain){
            url = ChatApplication.url + Constants.curtainunassigned+"/curtain" ; //0 door - 1 ir
        }else if(isDoorSensor==4){
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/5"; //0 door - 1 ir
        }else if(isDoorSensor==5){
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/5"; //0 door - 1 ir
        }else {
//            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/2"; //0 door - 1 ir
            url = ChatApplication.url + Constants.GET_UNASSIGNED_SENSORS + "/0"; //0 door - 1 ir
        }

        new GetJsonTask(this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                SensorUnassignedRes sensorUnassignedRes = Common.jsonToPojo(result.toString(),SensorUnassignedRes.class);

                if(sensorUnassignedRes.getCode() == 200){

                    if(sensorUnassignedRes.getData()!=null && sensorUnassignedRes.getData().getUnassigendSensorList().size() > 0){
                        Intent intent = new Intent(RoomEditActivity_v2.this, SensorUnassignedActivity.class);
                        intent.putExtra("isDoorSensor",isDoorSensor);
                        intent.putExtra("roomId",room.getRoomId());
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),sensorUnassignedRes.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

}
