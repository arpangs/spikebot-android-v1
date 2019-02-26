package com.spike.bot.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.RoomListArrayAdapter;
import com.spike.bot.adapter.TypeSpinnerAdapter;
import com.spike.bot.adapter.mood.MoodDeviceListLayoutHelper;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import io.socket.client.Socket;

public class AddMoodActivity extends AppCompatActivity implements ItemClickMoodListener,ItemClickListener {

    public static  String TAG = "AddMoodActivity";
    ArrayList<RoomVO> roomList = new ArrayList<>();
    private RecyclerView mMessagesView;
    MoodDeviceListLayoutHelper deviceListLayoutHelper;
    AutoCompleteTextView et_switch_name ;
    Spinner sp_device_type,spinner_mood_icon; //for mood selection
    ImageView sp_drop_down;
    String panel_id,panel_name;

    RoomVO moodVO = new RoomVO();
    RoomListArrayAdapter moodIconArrayAdapter;
    boolean editMode = false,isMap = false ,isMoodAdapter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("test");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mMessagesView = (RecyclerView) findViewById(R.id.messages);

        et_switch_name = (AutoCompleteTextView) findViewById(R.id.et_switch_name );

        sp_device_type = (Spinner ) findViewById(R.id.sp_device_type );

        spinner_mood_icon = (Spinner ) findViewById(R.id.spinner_mood_icon );
        //set the fixed height window popup menu dropdown in spinner
       /* try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            ListPopupWindow popupWindow = (ListPopupWindow) popup.get(spinner_mood_icon);
            popupWindow.setHeight(100);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/

        sp_drop_down = (ImageView)findViewById(R.id.sp_drop_down);

        //moodVO.getMood_name()
        JSONArray device_iconsArray = new JSONArray();
        try {
            JSONObject obj = new JSONObject();
            obj.put("device_icon_name","work");
            device_iconsArray.put(obj);

            JSONObject obj1 = new JSONObject();
            obj1.put("device_icon_name","home");
            device_iconsArray.put(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("device_icon_name","night");
            device_iconsArray.put(obj2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            editMode = getIntent().getExtras().getBoolean("editMode",false);
            moodVO = (RoomVO) getIntent().getExtras().getSerializable("moodVO");
            isMap  =  getIntent().getBooleanExtra("isMap",false);
            isMoodAdapter  =  getIntent().getBooleanExtra("isMoodAdapter",false);
            panel_id = getIntent().getStringExtra("panel_id");
            panel_name = getIntent().getStringExtra("panel_name");
            if(moodVO!=null){
                et_switch_name.setText(moodVO.getRoomName());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(editMode){
            toolbar.setTitle("EDIT MOOD");
        }else{
            toolbar.setTitle("ADD MOOD");
        }

        et_switch_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        setSpinnerValue(device_iconsArray);

        sp_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_device_type.performClick();
            }
        });

        getDeviceList();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    ArrayList<String> flags = new ArrayList<>();

    private void setSpinnerValue(JSONArray device_iconsArray) {
        try{

           ChatApplication.logDisplay( "setSpinnerValue  setSpinnerValue " + device_iconsArray.length() );
            flags = new ArrayList<String>();
            for(int i=0;i<device_iconsArray.length();i++){
                try {
                    flags.add(device_iconsArray.getJSONObject(i).getString("device_icon_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //int flags[] = {R.drawable.bulb_on, R.drawable.fan_on, R.drawable.tv_on, R.drawable.cfl_on, R.drawable.microwave_oven_on};
            TypeSpinnerAdapter customAdapter = new TypeSpinnerAdapter(this,flags,1,true);
            sp_device_type.setAdapter(customAdapter);

            if(moodVO!=null){

                for(int i=0;i<device_iconsArray.length();i++){
                    try {
                        if(moodVO.getRoom_icon()!=null){

                            if(device_iconsArray.getJSONObject(i).getString("device_icon_name").equalsIgnoreCase(moodVO.getRoom_icon())){
                                sp_device_type.setSelection(i);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch (Exception ex){ ex.printStackTrace(); }



    }
    List<DeviceVO> deviceVOArrayList;
    List<DeviceVO> deviceVOArrayListTemp=new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
           ChatApplication.logDisplay("action_save" );
            /*if(TextUtils.isEmpty(et_switch_name.getText().toString().trim())){
                Toast.makeText(getApplicationContext(),"Enter Mood Name", Toast.LENGTH_SHORT).show();
                return true;
            }*/

            try{
                room_device_id = deviceListLayoutHelper.getSelectedItemIds();
                deviceVOArrayList = deviceListLayoutHelper.getSelectedItemList();

                if(spinner_mood_icon.getSelectedItemPosition() == 0){
                    Toast.makeText(getApplicationContext(),"Select Mood", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if(deviceVOArrayList.size()==0){
                    Toast.makeText(getApplicationContext(),"Select atleast one Switch ", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(editMode){
                    saveMood();
                }
                else{
                    saveMood();
                }
            }catch (Exception ex){ ex.printStackTrace(); }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<String> moodList = new ArrayList<>();
    private List<RoomVO> moodIconList = new ArrayList<>();
    /// all webservice call below.
    public void getDeviceList(){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        roomList = new ArrayList<>();
        ChatApplication app = (ChatApplication) getApplication();
        String webUrl = app.url;

      //  String url =  webUrl + Constants.GET_DEVICES_LIST + "/" +Constants.DEVICE_TOKEN +"/0/0"; //old url for get mood name list
        String url =  webUrl + Constants.GET_MOOD_DETAILS ; //get mood name list with the mood icon name / not display sensor panel

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //clear moodIcon list
        if(moodIconList != null)
            moodIconList.clear();

        new GetJsonTask(this,url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    roomList = new ArrayList<>();
                    JSONObject dataObject = result.getJSONObject("data");
                    moodList.clear();

                    if(dataObject.has("moodNames")){

                        JSONArray moodNamesArray = dataObject.getJSONArray("moodNames");

                        for(int i=0 ; i < moodNamesArray.length(); i++){
                            JSONObject moodObject = moodNamesArray.getJSONObject(i);
                            String moodId = moodObject.getString("id");
                            String moodName = moodObject.getString("mood_name");

                            RoomVO roomVO = new RoomVO();
                            roomVO.setRoomId(moodId);
                            roomVO.setRoomName(moodName);
                            moodList.add(moodName);
                            moodIconList.add(roomVO);

                        }

                        RoomVO roomVO1 = new RoomVO();
                        roomVO1.setRoomId("");
                        roomVO1.setRoomName("Select Mood");
                        moodIconList.add(0,roomVO1);

                        /**
                         *  mood icon dropdown spinner...
                         */
                        moodIconArrayAdapter = new RoomListArrayAdapter(AddMoodActivity.this,R.layout.row_spinner_item,R.id.txt_spinner_title,
                                moodIconList,"");
                        spinner_mood_icon.setAdapter(moodIconArrayAdapter);

                         if(moodVO!=null){
                             for (int i = 0; i < moodIconList.size(); i++) {
                                 RoomVO roomVO = moodIconList.get(i);
                                 if (roomVO.getRoomName().equalsIgnoreCase(moodVO.getRoomName())) {
                                     spinner_mood_icon.setSelection(i);
                                 }
                             }
                         }

                        final ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, moodList);
                        et_switch_name.setAdapter(adapter);


                      //  et_switch_name.showDropDown();

                        /*et_switch_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus){
                                    et_switch_name.showDropDown();
                                }
                            }
                        });*/
                        et_switch_name.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                et_switch_name.showDropDown();
                                return false;
                            }
                        });

                    }

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseRoomArray(roomArray,true);

                    setData(roomList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
//                    deepsImage.setVisibility(View.GONE);
//                    txt_connection.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }


    public String selectedRoomId;

    private void sortList(final List<RoomVO> roomVOs){

        Collections.sort(roomVOs, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Boolean.compare(o2.isExpanded,o1.isExpanded);
            }
        });
    }


    public void setData(ArrayList<RoomVO> roomList){

        if(moodVO!=null){

            List<String> deviceVOList = moodVO.getRoomDeviceIdList();

                for(RoomVO roomVO : roomList){

                    List<PanelVO> panelVOList = roomVO.getPanelList();

                    for(PanelVO panelVO : panelVOList){
                        ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

                        for(DeviceVO deviceVO : deviceList){

                            ChatApplication.logDisplay("Name : "+ deviceVO.getDeviceName() + " id : " + deviceVO.getRoomDeviceId());

                            for(String deviceVORoot : deviceVOList){ ////select original devices

                                if(deviceVO.getSensor_type()!=null && deviceVO.getSensor_type().equalsIgnoreCase("remote")){ //if device type sensor than compare sensor id instead of room device id

                                    if(deviceVO.getSensor_id().equalsIgnoreCase(deviceVORoot) || deviceVO.getRoomDeviceId().equalsIgnoreCase(deviceVORoot)){

                                        roomVO.setExpanded(true);
                                        deviceVO.setSelected(true);
                                    }
                                }else{

                                    if(deviceVO.getRoomDeviceId().equalsIgnoreCase(deviceVORoot)){
                                        roomVO.setExpanded(true);
                                        deviceVO.setSelected(true);
                                    }
                                }
                            }
                        }

                    }
                }
        }

        /*sensor unselect condition */
        for(int i=0; i<roomList.size(); i++){
            for(int j=0; j<roomList.get(i).getPanelList().size(); j++){
                for(int k=0; k<roomList.get(i).getPanelList().get(j).getDeviceList().size(); k++){
                    if(roomList.get(i).getPanelList().get(j).getDeviceList().get(k).isSensor()){
                        if(roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("temp")){
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }else if(roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getSensor_type().equalsIgnoreCase("door")){
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }else if(roomList.get(i).getPanelList().get(j).getDeviceList().get(k).getTo_use().equalsIgnoreCase("0")){
                            roomList.get(i).getPanelList().get(j).getDeviceList().get(k).setSelected(false);
                        }
                    }
                }
            }
        }

        //sort room list vie selected device list available
        sortList(roomList);

        deviceListLayoutHelper = new MoodDeviceListLayoutHelper(this,mMessagesView,this,Constants.SWITCH_NUMBER,isMoodAdapter);
        deviceListLayoutHelper.addSectionList(roomList);
     //   if(!isMap){
           // if(moodVO!=null && !isMoodAdapter){
           //     deviceListLayoutHelper.setSelection(moodVO.getRoom_device_id());
          //  }
      //  }

        deviceListLayoutHelper.notifyDataSetChanged();

    }
    String room_device_id = "";

    public void saveMood(){
        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication app = (ChatApplication) getApplication();
        String webUrl = app.url;
        String url =  "";

        if(editMode){
            url =  webUrl + Constants.SAVEEDITMOOD ;
        }
        else{
          //  url =  webUrl + Constants.ADD_NEW_MOOD ;
            url =  webUrl + Constants.ADD_NEW_MOOD_NEW ;
        }
        deviceVOArrayListTemp.clear();
        deviceVOArrayListTemp.addAll(deviceVOArrayList);
        deviceVOArrayList.clear();
        deviceVOArrayList=removeDuplicates(deviceVOArrayListTemp);
        JSONObject moodObj = new JSONObject();
        try {

            moodObj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            moodObj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

            RoomVO room = (RoomVO) spinner_mood_icon.getSelectedItem();

            if(editMode){
                moodObj.put("room_id",moodVO.getRoomId());
                moodObj.put("panel_id",panel_id);
                moodObj.put("panel_name",panel_name);
            }else{
                moodObj.put("room_id","");
                moodObj.put("panel_id","");
            }

            /**
             * check if mood name entered is custom or selected in dropdown list
             */
           /* boolean isCustom = false;
            for(String mList : moodList){
                if(mList.equalsIgnoreCase(et_switch_name.getText().toString().trim())){
                    isCustom = true;
                }
            }*/

          //  moodObj.put("is_custom",isCustom ? 0 : 1);
            moodObj.put("is_custom",0);

              moodObj.put("room_name",room.getRoomName());
         //   moodObj.put("room_name",et_switch_name.getText().toString().trim());
           // moodObj.put("room_icon",flags.get(sp_device_type.getSelectedItemPosition()).toString());
            moodObj.put("room_icon","home");
          //  moodObj.put("room_device_id",room_device_id);

            JSONArray jsonArrayDevice = new JSONArray();
            String ss_room_devices = "";

            JSONArray array = new JSONArray();

            for(DeviceVO dPanel : deviceVOArrayList){

                    JSONObject ob1 = new JSONObject();
                    if (dPanel.getDeviceType() != null && dPanel.getDeviceType().equalsIgnoreCase("2")) { //old : AC

                        ob1.put("sensor_id", dPanel.getSensor_id());
                        ob1.put("device_id", dPanel.getDeviceId());
                        ob1.put("ir_blaster_id", dPanel.getIr_blaster_id());
                        ob1.put("sensor_name", dPanel.getSensor_name());

                        ob1.put("device_type", dPanel.getDeviceType());
                        ob1.put("is_active", dPanel.getIsActive());
                        ob1.put("sensor_icon", dPanel.getSensor_icon());
                        ob1.put("module_id", dPanel.getModuleId());
                        ob1.put("is_original", dPanel.getIs_original());

                        ob1.put("original_room_device_id", dPanel.getOriginal_room_device_id());
                        ob1.put("room_device_id", dPanel.getRoomDeviceId());
                        ob1.put("sensor_type", dPanel.getSensor_type());

                        ob1.put("device_status", dPanel.getDeviceStatus());
                        ob1.put("device_specific_value", dPanel.getDeviceSpecificValue());
                        ob1.put("remote_device_id", dPanel.getRemote_device_id()); //added  : 17-9-2018
                        ob1.put("room_panel_id", dPanel.getRoom_panel_id());

                    } else {

                        ob1.put("module_id", dPanel.getModuleId());
                        ob1.put("device_id", dPanel.getDeviceId());
                        ob1.put("room_device_id", dPanel.getRoomDeviceId());

                        ob1.put("device_icon", dPanel.getDevice_icon());
                        ob1.put("device_name", dPanel.getDeviceName());
                        ob1.put("device_status", dPanel.getDeviceStatus());
                        ob1.put("device_specific_value", dPanel.getDeviceSpecificValue());
                        ob1.put("device_type", dPanel.getDeviceType());

                        ob1.put("original_room_device_id", dPanel.getOriginal_room_device_id());
                        ob1.put("is_active", dPanel.getIsActive());
                        ob1.put("is_alive", dPanel.getIsAlive());
                        ob1.put("is_original", dPanel.getIs_original());
                        ob1.put("room_panel_id", dPanel.getRoom_panel_id());
                    }

                    jsonArrayDevice.put(ob1);
//                }

                // ss_room_devices += dPanel.getModuleId()+""+dPanel.getDeviceId()+",";
               // ss_room_devices += dPanel.getRoomDeviceId()+",";

                try{
                //    array.put(ss_room_devices.substring(0, ss_room_devices.lastIndexOf(","))); //remove last comma of String index @String str = "abc,xyz,"
                    array.put(dPanel.getRoomDeviceId()); //remove last comma of String index @String str = "abc,xyz,"
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            if(editMode){
                moodObj.put("room_devices",array);
            }
            moodObj.put("deviceList",jsonArrayDevice);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetJsonTask(this,url ,"POST",moodObj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {

                    ChatApplication.isMoodFragmentNeedResume = true;

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(!TextUtils.isEmpty(message)){
                        Toast.makeText(getApplicationContext(),  message , Toast.LENGTH_SHORT).show();
                    }

                    if(code == 200){
                        ChatApplication.isRefreshDashBoard = true;
                        //ChatApplication.isRefreshHome = true;
                        ChatApplication.isRefreshMood = true;
                        if(!TextUtils.isEmpty(message)){
                          //  Toast.makeText(getApplicationContext().getApplicationContext(),  message , Toast.LENGTH_SHORT).show();
                        }
                       // ActivityHelper.dismissProgressDialog();
                        finish();
                    }
                    else{
                      //  Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();
//                    deepsImage.setVisibility(View.GONE);
//                    txt_connection.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    @Override
    public void itemClicked(RoomVO item, String action) {
    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {
    }

    @Override
    public void itemClicked(DeviceVO deviceVO, String action) {
        if(action.equalsIgnoreCase("disable_device")){
            getDeviceDetails(deviceVO.getOriginal_room_device_id());
        }
    }

    @Override
    public void itemClicked(DeviceVO deviceVO, String action, int position) {
        if(action.equalsIgnoreCase("disable_device")){
            getDeviceDetails(deviceVO.getOriginal_room_device_id());
        }
    }

    @Override
    public void itemClicked(CameraVO cameraVO, String action) {
    }

    private Socket mSocket;
    String webUrl = "";

    private void getDeviceDetails(String original_room_device_id){

        //original_room_device_id

        ChatApplication app = ChatApplication.getInstance();
        if(mSocket!=null && mSocket.connected()){
           ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id() );
        }
        else{
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        String url = webUrl + Constants.GET_MOOD_DEVICE_DETAILS+"/"+original_room_device_id ;

        new GetJsonTask2(AddMoodActivity.this,url ,"GET","", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("onSuccess :  " + result.toString());
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");

                        //ActivityHelper.showDialog(AddMoodActivity.this,getString(R.string.app_name),room_name + " (" + panel_name + ")" ,ActivityHelper.NO_ACTION);
                        showDeviceDialog(room_name,panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Throwable throwable, String error , int responseCode) {
                ChatApplication.logDisplay("onFailure " + error);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Dialog dialog = null;
    public synchronized void showDeviceDialog(  String roomName, String panelName){

        if(dialog == null) {
            dialog = new Dialog(AddMoodActivity.this);
        }else{
            dialog.show();
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_virtual_devices);

        TextView txtRoom = (TextView)dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel = (TextView)dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK = (Button) dialog.findViewById(R.id.vbtn_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog=null;
            }
        });


        if(!dialog.isShowing()){
            dialog.show();
        }

    }

    public static List<DeviceVO>  removeDuplicates(List<DeviceVO> list){

        ArrayList<String> arrayList=new ArrayList<>();
        ArrayList<DeviceVO> listTemp=new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            arrayList.add(list.get(i).getRoomDeviceId());
        }

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(arrayList);
        arrayList.clear();
        arrayList.addAll(hashSet);


        for(int j=0; j<list.size(); j++){
            for(int i=0; i<arrayList.size(); i++){
                if(arrayList.get(i).length()>1 && list.get(j).getRoomDeviceId().equalsIgnoreCase(arrayList.get(i))){
                    arrayList.set(i,arrayList.get(i)+"i");
                    listTemp.add(list.get(i));
                }
            }
        }

        return listTemp;
    }
}
