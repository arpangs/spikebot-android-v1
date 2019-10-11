package com.spike.bot.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.GetJsonTaskRemote;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.MoodExpandableLayoutHelper;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.ICallback;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.model.SendRemoteCommandReq;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A chat fragment containing messages view and input form.
 */
public class MoodFragment extends Fragment implements View.OnClickListener,ItemClickMoodListener ,SwipeRefreshLayout.OnRefreshListener {

    MainFragment.OnHeadlineSelectedListener mCallback;
    private Boolean isRefreshonScroll=false;
    private RecyclerView mMessagesView;
    private Socket mSocket;
    MoodExpandableLayoutHelper sectionedExpandableLayoutHelper;

    private ArrayList<RoomVO> moodList = new ArrayList<>();
    private String userId = "0" ,token_id = "",userName="",panel_name="",panel_id="",webUrl="";

    private LinearLayout txt_empty_schedule;
    private ImageView empty_add_image;

    ResponseErrorCode responseErrorCode;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;

    public MoodFragment() {
        super();
    }
    public static MoodFragment newInstance() {
        MoodFragment fragment = new MoodFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
        try {
            responseErrorCode = (ResponseErrorCode) activity;
            mCallback = (MainFragment.OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
           e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       // startSocketConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSocket!=null) {
            mSocket.off("ReloadDeviceStatusApp", reloadDeviceStatusApp);
            mSocket.off("updateChildUser", updateChildUser);
            mSocket.off("roomStatus", roomStatus);
        }
    }


    @Override
    public void onRefresh() {
        isRefreshonScroll=true;
        mCallback.onArticleSelected(""+userName);
        swipeRefreshLayout.setRefreshing(true);
        sectionedExpandableLayoutHelper.setClickable(false);
        getDeviceList();
    }



    FloatingActionButton mFab;

   // com.deep.automation.customview.ExpandableGridView exp_list;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView =  view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        txt_empty_schedule = view.findViewById(R.id.txt_empty_schedule);

        empty_add_image =  view.findViewById(R.id.empty_add_image);

        mFab = view.findViewById(R.id.fab);


        txt_empty_schedule.setVisibility(View.GONE);


        try {
            startSocketConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!isCallVisibleHint){
            onLoadFragment();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //openAddPopup(tv_header);
                Intent intent = new Intent(getActivity(), AddMoodActivity.class);
                intent.putExtra("isMoodAdapter",false);
                intent.putExtra("editMode",false);
                startActivity(intent);
            }
        });


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionedExpandableLayoutHelper = new MoodExpandableLayoutHelper(getActivity(), mMessagesView, MoodFragment.this, Constants.SWITCH_NUMBER);
            }
        });

        ChatApplication.isRefreshHome = true;

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
              //  swipeRefreshLayout.setRefreshing(true);
              //  getDeviceList();
            }
        });

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddMoodActivity.class);
                intent.putExtra("isMoodAdapter",false);
                intent.putExtra("editMode",false);
                startActivity(intent);
            }
        });


    }


    private boolean isCallVisibleHint = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCallVisibleHint = true;
        if(isVisibleToUser){
            try {
                startSocketConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onLoadFragment();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_login){
          //  loginCloud();
            //Login
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userName= ((Main2Activity)getActivity()).toolbarTitle.getText().toString();
        if(ChatApplication.isMoodFragmentNeedResume){
            ChatApplication.isMoodFragmentNeedResume = false;
            try {
                startSocketConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onLoadFragment();
        }

    }

    // Room buttons click

    /**
     *
     * @param roomVO
     * @param action
     */

    @Override
    public void itemClicked(final RoomVO roomVO,String action) {

        ArrayList<PanelVO> panelList = roomVO.getPanelList();
        for(PanelVO panelVO : panelList){
            panel_id = panelVO.getPanelId();
            panel_name = panelVO.getPanelName();
        }

        if(action.equalsIgnoreCase("onoffclick")){
            changeMoodStatus(roomVO,panel_id);//panel_id added in last

        }else if(action.equalsIgnoreCase("expandclick")){

        }else if(action.equalsIgnoreCase("deleteclick")){

          String msg ="";
            if(roomVO.getIs_schedule()==1){
                msg = "You have schedule set on this mood.\nAre you sure you want to delete mood ?";
            }
            else{
                msg = "Are you sure you want to delete mood ?";
            }
            ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", msg,new ConfirmDialog.IDialogCallback() {
                @Override
                public void onConfirmDialogYesClick() {
                    deleteMood(roomVO.getRoomId(),panel_id,roomVO.getRoomName());
                }
                @Override
                public void onConfirmDialogNoClick() {

//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
                }

            });
            newFragment.show(getActivity().getFragmentManager(), "dialog");

        }
        else if(action.equalsIgnoreCase("editclick")){

            Intent intent = new Intent(getActivity(), AddMoodActivity.class);
            intent.putExtra("editMode",true);
            intent.putExtra("moodVO",roomVO);
            intent.putExtra("panel_id",panel_id);
            intent.putExtra("panel_name",panel_name);
            intent.putExtra("isMap",true);
            intent.putExtra("isMoodAdapter",true);
            startActivity(intent);//

        }else if(action.equalsIgnoreCase("imgLog")){
            Intent intent = new Intent(getActivity(),DeviceLogActivity.class);
            intent.putExtra("ROOM_ID",roomVO.getRoomId());
            intent.putExtra("isCheckActivity","mode");
            intent.putExtra("isRoomName",""+roomVO.getRoomName());
            startActivity(intent);
        }else if(action.equalsIgnoreCase("imgSch")){

            Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
            //intent.putExtra("isMood",true);
            intent.putExtra("moodName",roomVO.getRoomName());
            //intent.putExtra("moodId",item.getPanelId());
            intent.putExtra("moodId",roomVO.getRoomId());
            intent.putExtra("isMoodAdapter",true); //added in last call
            intent.putExtra("moodId2",roomVO.getRoomId());
            intent.putExtra("isActivityType","2");
            startActivity(intent);

        }

       // roomOnOff(roomVO);
    }


    private void deviceOnOff(DeviceVO deviceVO) {
        //if (null == mUsername) return;
      //  if (!mSocket.connected()) return; //uncomment


        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            if(deviceVO.getDeviceType().equalsIgnoreCase("3")){
                obj.put("status",deviceVO.getDeviceStatus());
                obj.put("bright","");
                obj.put("is_rgb","0");//1;
                obj.put("rgb_array","");
                obj.put("room_device_id",deviceVO.getRoomDeviceId());
            }else {
                obj.put("room_device_id", deviceVO.getRoomDeviceId());
                obj.put("module_id", deviceVO.getModuleId());
                obj.put("device_id", deviceVO.getDeviceId());
                obj.put("device_status", deviceVO.getOldStatus());
                obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
                //  obj.put("is_change","1");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket != null && mSocket.connected()) {
            //TODO code here for ACK TimeOut
            mSocket.emit("socketChangeDevice", obj);
        }else{

            String url="";
            if(deviceVO.getDeviceType().equalsIgnoreCase("3")){
                url = ChatApplication.url + Constants.changeHueLightState;
            }else {
                url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;
            }

            ChatApplication.logDisplay("Device roomPanelOnOff mood "+url+"  " + obj.toString());
            //  ChatApplication.logDisplay( "roomPanelOnOff obj " + obj.toString());
            //  ChatApplication.logDisplay( "roomPanelOnOff url " + url );

            new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int code = result.getInt("code"); //message
                        String message = result.getString("message");
                        if (code == 200) {
                            //   sectionedExpandableLayoutHelper.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }

                @Override
                public void onFailure(Throwable throwable, String error) {
                    Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }).execute();

        }

    }

    // Panel buttons click
    @Override
    public void itemClicked(final DeviceVO item,String action) {
        if(action.equalsIgnoreCase("itemclick")){
          //  deviceOnOff(item);
          //  item.setDeviceStatus(item.getDeviceStatus()==0?1:0);
        }else if(action.equalsIgnoreCase("itemOnOffclick")){
            deviceOnOff(item);
        }else if(action.equalsIgnoreCase("heavyloadlongClick")){
            Intent intent=new Intent(getActivity(), HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomDeviceId",item.getOriginal_room_device_id());
            intent.putExtra("getRoomName",item.getRoomName());
            intent.putExtra("getModuleId",item.getModuleId());
            intent.putExtra("device_id",item.getDeviceId());
            startActivity(intent);
        }else if(action.equalsIgnoreCase("philipslongClick")){
            if(item.getDeviceStatus()==1){
                Intent intent=new Intent(getActivity(), SmartColorPickerActivity.class);
                intent.putExtra("roomDeviceId",item.getRoomDeviceId());
                intent.putExtra("getOriginal_room_device_id",item.getOriginal_room_device_id());
                startActivity(intent);
            }else {
                ChatApplication.showToast(getActivity(),"Please device on");
            }


        }else if(action.equalsIgnoreCase("isIRSensorOnClick")){
            sendRemoteCommand(item);
        }
        else if(action.equalsIgnoreCase("scheduleclick")){
            Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
            startActivity(intent);
        }
        if(action.equalsIgnoreCase("longclick")){
           int getDeviceSpecificValue=0;
           if(!TextUtils.isEmpty( item.getDeviceSpecificValue())){
               getDeviceSpecificValue= Integer.parseInt(item.getDeviceSpecificValue());
           }
            FanDialog fanDialog = new FanDialog(getActivity(), item.getRoomDeviceId(),  getDeviceSpecificValue, new ICallback() {
                @Override
                public void onSuccess(String str) {
                    if(str.contains("yes")){
                    }
                }
            });
            fanDialog.show();
        } else if(action.equalsIgnoreCase("textclick")){

            String fName = Common.getPrefValue(ChatApplication.getInstance(),"first_name");
            String lName = Common.getPrefValue(ChatApplication.getInstance(),"last_name");

            getDeviceDetails(item.getOriginal_room_device_id());


        //    ActivityHelper.showDialog(getActivity(),getString(R.string.app_name),item.getRoomName() + " (" + item.getPanel_name() + ")" ,ActivityHelper.NO_ACTION);
        }else if(action.equalsIgnoreCase("isIRSensorClick")){
            Intent intent = new Intent(getActivity(),IRBlasterRemote.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("REMOTE_IS_ACTIVE",item.getDeviceStatus());
            bundle.putSerializable("REMOTE_ID",item.getOriginal_room_device_id());
            bundle.putSerializable("ROOM_DEVICE_ID",item.getRoomDeviceId()); //MOOD_DEVICE_ID
            bundle.putSerializable("IR_BLASTER_ID",item.getSensor_id());
            // intent.putExtra("IR_BLASTER_ID",item.getIr_blaster_id());
            intent.putExtra("IR_BLASTER_ID",item.getSensor_id());
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    /**
     * click device item adapter click event
     * @param item
     * @param action
     */
    @Override
    public void itemClicked(final PanelVO item, String action) {

        if(action.equalsIgnoreCase("scheduleclick")){
           ChatApplication.logDisplay(action +" itemClicked itemClicked DeviceVO "  );

            String moodName = "";
            String moodId = "";
            if(moodList.size()>0){  //get MoodId

                for(RoomVO moodVO : moodList){
                    ArrayList<PanelVO> panelVOArrayList = moodVO.getPanelList();
                    for(PanelVO panelVO : panelVOArrayList){
                        if(panelVO.getPanelId().equalsIgnoreCase(item.getPanelId())){
                            moodName = moodVO.getRoomName();
                            moodId = moodVO.getRoomId();
                        }
                    }
                }
            }

            Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
            //intent.putExtra("scheduleIcon",true);
            intent.putExtra("moodName",moodName);
            intent.putExtra("moodId",item.getPanelId());
            intent.putExtra("isMoodAdapter",true); //added in last call
            intent.putExtra("moodId2",moodId);
            intent.putExtra("isActivityType","2");
            startActivity(intent);
        }

    }

    /**
     * get Room name and Panel name using original_room_device_id
     * @param original_room_device_id - original room device id
     */

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
        if(!token_id.equalsIgnoreCase("")){
            url = url +"/" + token_id;
        }

        new GetJsonTask2(getActivity(),url ,"GET","", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");

                        //ActivityHelper.showDialog(getActivity(),getString(R.string.app_name),room_name + " (" + panel_name + ")" ,ActivityHelper.NO_ACTION);
                        showDeviceDialog(room_name,panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Throwable throwable, String error , int responseCode) {
            }
        }).execute();
    }

    public Dialog dialog = null;
    public synchronized void showDeviceDialog(  String roomName, String panelName){

        if(dialog == null) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_virtual_devices);
        }

        TextView txtRoom = (TextView)dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel = (TextView)dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK = (Button) dialog.findViewById(R.id.vbtn_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if(!dialog.isShowing()){
            dialog.show();
        }

    }


////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    /*--move all onResume code--*/
    public void onLoadFragment(){


        ChatApplication.isRefreshMood = true;
        try{

          //  ChatApplication app = (ChatApplication) getActivity().getApplication();
            ChatApplication app = ChatApplication.getInstance();
            if(mSocket!=null && mSocket.connected()){
               ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id() );
            }
            else{
                mSocket = app.getSocket();
            }
            webUrl = app.url;

           ChatApplication.logDisplay("onResume webUrl " + webUrl);
            if(moodList.size()==0 || ChatApplication.isRefreshHome || ChatApplication.isRefreshMood){
                getDeviceList();
                ChatApplication.isRefreshHome = false;  //true
                ChatApplication.isRefreshMood = false; //true
            }
        }catch (Exception ex){ ex.printStackTrace(); }

    }

    public void startSocketConnection() throws Exception{

        ChatApplication app = ChatApplication.getInstance();
        if(mSocket!=null && mSocket.connected()){
           ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id() );
        }else{
            mSocket = app.getSocket();
        }
        webUrl = app.url;

        if(mSocket!=null && mSocket.connected()){
           ChatApplication.logDisplay("startSocketConnection   startSocketConnection.." + webUrl );
            mSocket.on("ReloadDeviceStatusApp", reloadDeviceStatusApp);
            mSocket.on("updateChildUser", updateChildUser);
//            mSocket.on("deleteChildUser", deleteChildUser);
            //  mSocket.on("changeMoodSocket", changeMoodSocket);
            mSocket.on("roomStatus", roomStatus);
         //   mSocket.on("panelStatus", panelStatus);

           ChatApplication.logDisplay("mSocket.connect()= "  + mSocket.id() );
        }
    }

    /// all webservice call below.
    public void getDeviceList(){
        ChatApplication.logDisplay( "getDeviceList");
        if(getActivity()==null){
            return;
        }

        String url = webUrl + Constants.GET_DEVICES_LIST;
        if(!token_id.equalsIgnoreCase("")){
            url = url +"/" + token_id;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 1);
            jsonObject.put("is_sensor_panel", 0);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            if(TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))){
                jsonObject.put("admin","");
            }else {
                jsonObject.put("admin",Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE)));
            }
            jsonObject.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(moodList == null){
            moodList = new ArrayList<>();
        }

        ChatApplication.logDisplay("jsonObject is mood " + jsonObject.toString());
        new GetJsonTask2(getActivity(),url ,"POST",jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                responseErrorCode.onSuccess();
                sectionedExpandableLayoutHelper.setClickable(true);
                swipeRefreshLayout.setRefreshing(false);
                ChatApplication.logDisplay( " getDeviceList onSuccess " + result.toString());
                try {

                    moodList.clear();

                    JSONObject dataObject = result.getJSONObject("data");

                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    moodList.addAll(JsonHelper.parseRoomArray(roomArray,false));

                   // sortMoodList(moodList);

                    sectionedExpandableLayoutHelper.addSectionList( moodList );

                    sectionedExpandableLayoutHelper.notifyDataSetChanged();


                    if(moodList.size()>0){
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    }

                    if(isRefreshonScroll){
                        isRefreshonScroll=false;
                        getDeviceListUserData(15);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    swipeRefreshLayout.setRefreshing(false);
                    //hideProgress();
                    if(moodList.size()==0){
                        swipeRefreshLayout.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error , int responseCode) {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                ChatApplication.logDisplay( "getDeviceList onFailure " + error );
             //   Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                if(responseCode == 503){
                    responseErrorCode.onErrorCode(responseCode);
                }
                if(moodList.size()==0){
                    swipeRefreshLayout.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * DeleteMood
     * @param roomId
     * @param panel_id
     * @param room_name
     */
    public void deleteMood(String roomId, String panel_id, String room_name){
       // ChatApplication.logDisplay( "deleteMood deleteMood");
        if(!ActivityHelper.isConnectingToInternet(getActivity())){
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("room_id",roomId);
            obj.put("panel_id",panel_id);
            obj.put("room_name",room_name);

        }catch (Exception e){
            e.printStackTrace();
        }


        ActivityHelper.showProgressDialog(getActivity(),"Please Wait.",false);
        String url =  webUrl + Constants.DELETE_MOOD;
        new GetJsonTask(getActivity(),url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override

            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay( "deleteMood onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code == 200){
                        if(!TextUtils.isEmpty(message)){
                            Toast.makeText(getActivity().getApplicationContext(),  message , Toast.LENGTH_SHORT).show();
                        }
                        getDeviceList();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), message , Toast.LENGTH_SHORT).show();
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
                ChatApplication.logDisplay( "deleteMood onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    public void changeMoodStatus(RoomVO moodVO,String panel_id){
        ChatApplication.logDisplay( "changeMoodStatus changeMoodStatus");
        if(!ActivityHelper.isConnectingToInternet(getActivity())){
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }


        if (mSocket==null){
            ChatApplication.logDisplay("mSocket is null mood");
           return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("room_id", moodVO.getRoomId());
            obj.put("panel_id",panel_id);
            obj.put("device_status", moodVO.getOld_room_status());
            obj.put("operationtype", 3);
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mSocket!=null && mSocket.connected()){
            mSocket.emit("changeRoomPanelMoodStatus", obj);

        }else{
            String url =  webUrl + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW;
            new GetJsonTask(getActivity(),url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
                @Override
                public void onSuccess(JSONObject result) {
                    ChatApplication.logDisplay( "moodStatusOnOff onSuccess " + result.toString());
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        ActivityHelper.dismissProgressDialog();
                    }
                }
                @Override
                public void onFailure(Throwable throwable, String error) {
                    ChatApplication.logDisplay( "changeMoodStatus onFailure " + error );
                    ActivityHelper.dismissProgressDialog();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }

    }

    private void sendRemoteCommand(final DeviceVO item){

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        SendRemoteCommandReq sendRemoteCommandReq = new SendRemoteCommandReq();
        sendRemoteCommandReq.setRemoteid(item.getOriginal_room_device_id());

        sendRemoteCommandReq.setPower(item.getDeviceStatus()==0 ? "ON" : "OFF");
        sendRemoteCommandReq.setSpeed(item.getSpeed());
        sendRemoteCommandReq.setTemperature(Integer.parseInt(item.getTemperature()));
        sendRemoteCommandReq.setRoomDeviceId(item.getRoomDeviceId());
        sendRemoteCommandReq.setPhoneId(APIConst.PHONE_ID_VALUE);
        sendRemoteCommandReq.setPhoneType(APIConst.PHONE_TYPE_VALUE);
        sendRemoteCommandReq.setSpeed(item.getMode());

        Gson gson = new Gson();
        String mRemoteCommandReq = gson.toJson(sendRemoteCommandReq);

        com.spike.bot.core.Log.d("sendRemoteCommand","" + mRemoteCommandReq);

        String url = ChatApplication.url + Constants.SEND_REMOTE_COMMAND;

        new GetJsonTaskRemote(getContext(), url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result is ir "+result);
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");

                    if(code == 200){
                    }else{
                        ChatApplication.showToast(getActivity(),item.getDeviceName()+" "+getString(R.string.ir_error));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result is ir error "+error.toString());
                ChatApplication.showToast(getActivity(),item.getDeviceName()+" "+getString(R.string.ir_error));
            }

        }).execute();
    }

    /// all webservice call below.
    public  void getDeviceListUserData(final int checkmessgae) {
        //showProgress();

        if (getActivity() == null) {
            return;
        }

        if (checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 9 || checkmessgae == 10) {
            ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        }

       // String url = webUrl + Constants.GET_DEVICES_LIST + "/" + Constants.DEVICE_TOKEN + "/0/1";
        String url = webUrl + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            jsonObject.put("admin",1);
            jsonObject.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //responseErrorCode.onProgress();
        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    int code = result.getInt("code");
                    if (code == 200) {

                        JSONObject dataObject = result.getJSONObject("data");

                        JSONArray userListArray = dataObject.getJSONArray("userList");

                        JSONObject userObject = userListArray.getJSONObject(0);
                        String userId = userObject.getString("user_id");
                        String userFirstName = userObject.getString("first_name");
                        String userLastName = userObject.getString("last_name");
                        String camera_key = userObject.optString("camera_key");
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        ChatApplication.currentuserId = userId;
                        mCallback.onArticleSelected("" + userFirstName);

                        MainFragment.saveCurrentId(getActivity(), userId, userId);
                        if (userObject.has("user_password")) {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    private Emitter.Listener updateChildUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {

                        try {
                            JSONObject object = new JSONObject(args[0].toString());

                            String message=object.optString("message");
                            String user_id=object.optString("user_id");
                            if(Common.getPrefValue(getActivity(), Constants.USER_ID).equalsIgnoreCase(user_id)){
                                getDeviceList();
                                ChatApplication.showToast(getActivity(),message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    private Emitter.Listener reloadDeviceStatusApp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity()==null){
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null) {

                        try{

                            JSONObject object = new JSONObject(args[0].toString());
                            String module_id = object.getString("module_id");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int is_locked = object.optInt("is_locked");

                            sectionedExpandableLayoutHelper.updateItem(module_id, device_id, device_status,is_locked);
                            // sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        }catch (Exception ex){ ex.printStackTrace(); }

                    }
                }
            });
        }
    };
    private Emitter.Listener changeMoodSocket = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity()==null){
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null) {
                        try {
                            JSONObject moodJsonObj = new JSONObject(args[0].toString());
                            String mood_id = moodJsonObj.getString("mood_id");
                            String mood_status = moodJsonObj.getString("mood_status");


                            sectionedExpandableLayoutHelper.updateMood(mood_id, mood_status);
                            //  sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener roomStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args != null) {
                        try {


                            JSONObject object = new JSONObject(args[0].toString());
                            String room_id = object.getString("room_id");
                            String room_status = object.getString("room_status");

                            sectionedExpandableLayoutHelper.updateMood(room_id, room_status);
                            // sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
}