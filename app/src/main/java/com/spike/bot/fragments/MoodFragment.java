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
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.ack.AckWithTimeOut;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.ScheduleListActivity;
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
import com.spike.bot.listener.RunServiceInterface;
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
import com.spike.bot.model.SendRemoteCommandRes;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A chat fragment containing messages view and input form.
 */
public class MoodFragment extends Fragment implements View.OnClickListener,ItemClickMoodListener ,SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainFragment";

    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    MainFragment.OnHeadlineSelectedListener mCallback;

    private Boolean isSocketConnected = true,isRefreshonScroll=false;
    private RecyclerView mMessagesView;
    private Socket mSocket;
    MoodExpandableLayoutHelper sectionedExpandableLayoutHelper;

    private ArrayList<RoomVO> moodList = new ArrayList<>();
    private String userId = "0" ;
    private String token_id = "",userName="";

    private LinearLayout txt_empty_schedule;
    private TextView txt_empty_text;
    private ImageView empty_add_image;

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

    RunServiceInterface runServiceInterface;
    ResponseErrorCode responseErrorCode;

    private Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
        try {
            runServiceInterface = (RunServiceInterface) activity;
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
//            mSocket.off("deleteChildUser", deleteChildUser);
          //  mSocket.off("changeMoodSocket", changeMoodSocket);
            mSocket.off("roomStatus", roomStatus);
          //  mSocket.off("panelStatus", panelStatus);

        }
    }
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onRefresh() {
        isRefreshonScroll=true;
        if(Main2Activity.isCloudConnected){
           // runServiceInterface.executeService();
            String jsonText = Common.getPrefValue(getActivity(), Common.USER_JSON);
            if (!TextUtils.isEmpty(jsonText)) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>() {}.getType();
                List<User> userList = gson.fromJson(jsonText, type);
                if(userList.size()==1){
                }else {
                    ((Main2Activity)getActivity()).setCouldMethod();
                    //  runServiceInterface.executeService();
                }
            }
        }
        mCallback.onArticleSelected(""+userName);
        ((Main2Activity)getActivity()).invalidateToolbarCloudImage();

        swipeRefreshLayout.setRefreshing(true);
        sectionedExpandableLayoutHelper.setClickable(false);
        getDeviceList();
    }



    FloatingActionButton mFab;

   // com.deep.automation.customview.ExpandableGridView exp_list;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //String videoRtspUrl = "rtsp://192.168.175.101/video/play2.sdp";

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        linear_progress = (LinearLayout) view.findViewById(R.id.linear_progress);
        txt_empty_text = (TextView)view.findViewById(R.id.txt_empty_text);
        txt_empty_schedule = (LinearLayout)view.findViewById(R.id.txt_empty_schedule);

        empty_add_image = (ImageView) view.findViewById(R.id.empty_add_image);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);


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

        //////////////////
       // videoRtspUrl = "rtsp://192.168.175.105:10554/udp/av0_0";
//        videoRtspUrl ="rtsp://192.168.75.111/Streaming/Channels/1";
//        Intent intent = new Intent(getActivity().getApplicationContext(), VideoVLC2Activity.class);
//        intent.putExtra("videoUrl", videoRtspUrl);
//        startActivity(intent);

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

    Menu menu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
    }
    public void hideShowMenu(boolean flag ){
        if(menu!=null){
                try {
                        menu.findItem(R.id.action_add).setVisible(flag);
                        menu.findItem(R.id.action_setting).setVisible(flag);
                }
                catch (Exception e){
                }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //openAddPopup(tv_header);
            Intent intent = new Intent(getActivity(), AddMoodActivity.class);
            intent.putExtra("isMoodAdapter",false);
            intent.putExtra("editMode",false);
            startActivity(intent);

            return true;
        }
        else if (id == R.id.action_setting) {
            //openSettingPopup(tv_header);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private Emitter.Listener deleteChildUser = new Emitter.Listener() {
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
                                ((Main2Activity)getActivity()).logoutCloudUser();
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
//    private Emitter.Listener panelStatus = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            if (getActivity() == null) {
//                return;
//            }
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (args != null) {
//                        try{
//
//                            Log.d("sensorV3","panelStatus mood : " + args.length + " toString : " + args.toString());
//
//                            JSONObject object = new JSONObject(args[0].toString());
//                            // String room_order = object.getString("room_order");
//                            // String panel_order = object.getString("panel_order");
//                            String panel_id = object.getString("panel_id");
//                            String panel_status = object.getString("panel_status");
//
//                            sectionedExpandableLayoutHelper.updatePanel(panel_id, panel_status);
//                            // sectionedExpandableLayoutHelper.notifyDataSetChanged();
//
//                        }catch (Exception ex){ ex.printStackTrace(); }
//
//                    }
//                }
//            });
//        }
//    };

    String panel_id = "";
    String panel_name = "";
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
          /*  int index = moodList.indexOf(moodVO);
            if(index!=-1){
             moodList.get(index)
            }*/



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

            //edit
//            Intent intent = new Intent(getActivity(),RoomEditActivity.class);
//            intent.putExtra("room",moodVO);
//            startActivity(intent);


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
            //intent.putExtra("scheduleIcon",true);
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
            obj.put("room_device_id", deviceVO.getRoomDeviceId());
            obj.put("module_id", deviceVO.getModuleId());
            obj.put("device_id", deviceVO.getDeviceId());
            obj.put("device_status", deviceVO.getOldStatus());
            obj.put("localData", userId.equalsIgnoreCase("0") ? "0" : "1");
            //  obj.put("is_change","1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!mSocket.connected()){


            String url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;

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

        }else{

            //TODO code here for ACK TimeOut
            mSocket.emit("socketChangeDevice", obj);

//            mSocket.emit("socketChangeDevice", obj, new AckWithTimeOut(Constants.ACK_TIME_OUT) {
//                @Override
//                public void call(Object... args) {
//
//                    if(args!=null){
//
//                        if(args[0].toString().equalsIgnoreCase("No Ack")){
//
//                            Log.d("ACK_SOCKET_2","Panel AckWithTimeOut : "+ args[0].toString());
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    sectionedExpandableLayoutHelper.notifyDataSetChanged();
//                                }
//                            });
//
//
//                        }else if(args[0].toString().equalsIgnoreCase("true")){
//                            cancelTimer();
//                            Log.d("ACK_SOCKET_2","Panel AckWithTimeOut : "+ args[0].toString());
//                        }
//                    }
//                }
//            });

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
            bundle.putSerializable("REMOTE_IS_ACTIVE",item.getIsActive());
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
            /*Intent intent = new Intent(getActivity(),ScheduleActivity.class);
            intent.putExtra("item",item);
            intent.putExtra("schedule",true);
            startActivity(intent);*/

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
    String webUrl = "";

    private LinearLayout  linear_progress;
    private TextView txt_connection;

    public static boolean isResumeConnect = false;
    /// all webservice call below.
    public void getDeviceList(){
        ChatApplication.logDisplay( "getDeviceList");
        if(getActivity()==null){
            return;
        }

        String url = webUrl + Constants.GET_DEVICES_LIST;
//        String url = webUrl + Constants.GET_DEVICES_LIST + "/"+Constants.DEVICE_TOKEN + "/1/0";
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
     * sort moodList
     * @param moodList
     */
    
    private void sortMoodList(ArrayList<RoomVO> moodList) {
        Collections.sort(moodList, new Comparator<RoomVO>() {
            @Override
            public int compare(RoomVO o1, RoomVO o2) {
                return Integer.parseInt(o2.getRoomId()) - Integer.parseInt(o1.getRoomId());
            }
        });

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

        if(mSocket!=null && !mSocket.connected()){


                // String url =  webUrl + Constants.CHANGE_MOOD_STATUS;
            String url =  webUrl + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW;
            new GetJsonTask(getActivity(),url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
                @Override
                public void onSuccess(JSONObject result) {
                    ChatApplication.logDisplay( "moodStatusOnOff onSuccess " + result.toString());
                    try {
                        int code = result.getInt("code");
                        String message = result.getString("message");
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
                    ChatApplication.logDisplay( "changeMoodStatus onFailure " + error );
                    ActivityHelper.dismissProgressDialog();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                }
            }).execute();

        }else{
            mSocket.emit("changeRoomPanelMoodStatus", obj);
        }

    }

    private void sendRemoteCommand(DeviceVO item){

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        SendRemoteCommandReq sendRemoteCommandReq = new SendRemoteCommandReq();
        sendRemoteCommandReq.setRemoteid(item.getOriginal_room_device_id());


        // sendRemoteCommandReq.setCodesetid(String.valueOf(irBlasterCurrentStatusList.getCodesetId()));
        //   sendRemoteCommandReq.setIrblasterid(mIrBlasterId);
        //   sendRemoteCommandReq.setIrblasterModuleid(mIrBlasterModuleId);


//        if(item.getIsActive()==0){
//            sendRemoteCommandReq.setPower("OFF");
//        }else {
//            sendRemoteCommandReq.setPower("ON");
//        }
        sendRemoteCommandReq.setPower(item.getPower());
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
        new GetJsonTask(getContext(), url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                com.spike.bot.core.Log.d("SendRemote","onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
                     //   sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //responseErrorCode.onProgress();
        new GetJsonTask2(activity, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
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
                        String userPassword = "";
                        mCallback.onArticleSelected("" + userFirstName + " " + userLastName);

                        MainFragment.saveCurrentId(getActivity(),userId);
                        if (userObject.has("user_password")) {
                            userPassword = userObject.getString("user_password");
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

}
//http://192.168.175.222/caminfo/1514298076050_BJEkoRy7f dsp blackrock equal nifty 50 fund