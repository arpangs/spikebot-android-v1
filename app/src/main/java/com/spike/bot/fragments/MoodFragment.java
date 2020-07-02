package com.spike.bot.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akhilpatoliya.floating_text_button.FloatingTextButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.AddMoodActivity;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.HeavyLoad.HeavyLoadDetailActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.adapter.MoodExpandableLayoutHelper;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.dialog.FanDialog;
import com.spike.bot.dialog.onCallback;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A chat fragment containing messages view and input form.
 */
public class MoodFragment extends Fragment implements ItemClickMoodListener, SwipeRefreshLayout.OnRefreshListener {

    public Dialog dialog = null;
    FloatingTextButton mFab;
    DashBoardFragment.OnHeadlineSelectedListener mCallback;
    MoodExpandableLayoutHelper sectionedExpandableLayoutHelper;
    ResponseErrorCode responseErrorCode;
    private RecyclerView mMessagesView;
    private RelativeLayout txt_empty_schedule;
    private ImageView empty_add_image;
    private Socket mSocket;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private ArrayList<RoomVO> moodList = new ArrayList<>();
    private Boolean isCallVisibleHint = false;
    private String userId = "0", token_id = "", panel_name = "", panel_id = "";
    /*update child user like room , camera */
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
                            String message = object.optString("message");
                            String user_id = object.optString("user_id");
                            if (Common.getPrefValue(getActivity(), Constants.USER_ID).equalsIgnoreCase(user_id)) {
                                getDeviceList();
                                ChatApplication.showToast(getActivity(), message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    /*device status on , off change */
    private Emitter.Listener changeDeviceStatus = new Emitter.Listener() {
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
                            ChatApplication.logDisplay("status update device mood " + object.toString());
                            String device_type = object.getString("device_type");
                            String device_id = object.getString("device_id");
                            String device_status = object.getString("device_status");
                            int device_sub_status = object.optInt("device_sub_status");

//                            sectionedExpandableLayoutHelper.updateItem(module_id, device_id, device_status, is_locked);
                            sectionedExpandableLayoutHelper.updateDeviceItem(device_type, device_id, device_status, device_sub_status);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            });
        }
    };
    /*mood on off than found this socket */
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
                            ChatApplication.logDisplay("status update room mood " + object.toString());
                            String room_id = object.getString("mood_id");
                            String room_status = object.getString("mood_status");
                            sectionedExpandableLayoutHelper.updateMood(room_id, room_status);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        try {
            responseErrorCode = (ResponseErrorCode) activity;
            mCallback = (DashBoardFragment.OnHeadlineSelectedListener) activity;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood, container, false);
    }

    @Override
    public void onPause() {
        if (mSocket != null) {
            mSocket.off("changeDeviceStatus", changeDeviceStatus);
            mSocket.off("updateChildUser", updateChildUser);
            mSocket.off("changeMoodStatus", roomStatus);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // Room buttons click

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        sectionedExpandableLayoutHelper.setClickable(false);
        getDeviceList();
    }

    // com.deep.automation.customview.ExpandableGridView exp_list;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        txt_empty_schedule = view.findViewById(R.id.txt_empty_mood);
        empty_add_image = view.findViewById(R.id.empty_add_image);
        mFab = view.findViewById(R.id.fab);
        txt_empty_schedule.setVisibility(View.GONE);


        try {
            startSocketConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isCallVisibleHint) {
            onLoadFragment();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAddPopup(tv_header);
                Intent intent = new Intent(getActivity(), AddMoodActivity.class);
                intent.putExtra("isMoodAdapter", false);
                intent.putExtra("editMode", false);
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

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddMoodActivity.class);
                intent.putExtra("isMoodAdapter", false);
                intent.putExtra("editMode", false);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ChatApplication.isMoodFragmentNeedResume) {
            ChatApplication.isMoodFragmentNeedResume = false;
            try {
                startSocketConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onLoadFragment();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCallVisibleHint = true;
        if (isVisibleToUser) {
            try {
                startSocketConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onLoadFragment();
        }
    }

    /**
     * @param roomVO
     * @param action
     */

    @Override
    public void itemClicked(final RoomVO roomVO, String action) {

        ArrayList<PanelVO> panelList = roomVO.getPanelList();
        for (PanelVO panelVO : panelList) {
            panel_id = panelVO.getPanelId();
            panel_name = panelVO.getPanelName();
        }

        if (action.equalsIgnoreCase("onoffclick")) {
            changeMoodStatus(roomVO, panel_id);//panel_id added in last

        } else if (action.equalsIgnoreCase("deleteclick")) {

        } else if (action.equalsIgnoreCase("editclick")) {
            showBottomSheetDialog(roomVO);

        } else if (action.equalsIgnoreCase("imgLog")) {
            Intent intent = new Intent(getActivity(), DeviceLogActivity.class);
            intent.putExtra("ROOM_ID", roomVO.getRoomId());
//            intent.putExtra("isCheckActivity","mood");
            intent.putExtra("isCheckActivity", "mode");
            intent.putExtra("isRoomName", "" + roomVO.getRoomName());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("imgSch")) {
            Intent intent = new Intent(getActivity(), ScheduleListActivity.class);
//            intent.putExtra("moodName",roomVO.getRoomName());
//            intent.putExtra("moodId",roomVO.getRoomId());
//            intent.putExtra("isMoodAdapter",true); //added in last call
//            intent.putExtra("isActivityType","2");

            intent.putExtra("moodName", roomVO.getRoomName());
            intent.putExtra("roomName", roomVO.getRoomName());
            intent.putExtra("moodId", roomVO.getRoomId());
            intent.putExtra("selection", 2);
            intent.putExtra("isMoodAdapter", true); //added in last call
            intent.putExtra("moodId2", roomVO.getRoomId());
            intent.putExtra("moodId3", roomVO.getRoomId());
            intent.putExtra("isActivityType", "2");
            startActivity(intent);

        }
    }

    /*device on off */
    private void deviceOnOff(DeviceVO deviceVO) {

        /*dev arpan update on 24 june 2020  */

       /* JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
                obj.put("status", deviceVO.getDeviceStatus());
                obj.put("bright", "");
                obj.put("is_rgb", "0");//1;
                obj.put("rgb_array", "");
                obj.put("room_device_id", deviceVO.getRoomDeviceId());
            } else {
                obj.put("device_id", deviceVO.getDeviceId());
                obj.put("panel_id", deviceVO.getPanel_id());
                obj.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

       /* String url = "";
        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            url = ChatApplication.url + Constants.changeHueLightState;
        } else {
            url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;
        }*/
//        ChatApplication.logDisplay("Device roomPanelOnOff mood " + url + "  " + obj.toString());
           /* new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int code = result.getInt("code"); //message
                        String message = result.getString("message");
                        if (code == 200) {
                        } else {
                            ChatApplication.showToast(getActivity(), message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }

                @Override
                public void onFailure(Throwable throwable, String error) {
                    ChatApplication.showToast(getActivity(),getResources().getString(R.string.something_wrong1));
                }
            }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().DeviceOnOff(deviceVO, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code"); //message
                    String message = result.getString("message");
                    if (code != 200) {
                        ChatApplication.showToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.showToast(getActivity(), getResources().getString(R.string.something_wrong1));
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.showToast(getActivity(), getResources().getString(R.string.something_wrong1));
            }
        });

    }

    // Panel buttons click
    @Override
    public void itemClicked(final DeviceVO item, String action) {

        if (action.equalsIgnoreCase("itemOnOffclick")) {
            deviceOnOff(item);
        } else if (action.equalsIgnoreCase("heavyloadlongClick")) {
            if (item.getIsActive() == -1) {
                return;
            }
            Intent intent = new Intent(getActivity(), HeavyLoadDetailActivity.class);
            intent.putExtra("getRoomDeviceId", item.getOriginal_room_device_id());
            intent.putExtra("getRoomName", item.getRoomName());
            intent.putExtra("getModuleId", item.getModuleId());
            intent.putExtra("device_id", item.getDeviceId());
            startActivity(intent);
        } else if (action.equalsIgnoreCase("philipslongClick")) {
            if (item.getDeviceStatus() == 1) {
                Intent intent = new Intent(getActivity(), SmartColorPickerActivity.class);
                intent.putExtra("roomDeviceId", item.getRoomDeviceId());
                intent.putExtra("getOriginal_room_device_id", item.getOriginal_room_device_id());
                startActivity(intent);
            } else {
                ChatApplication.showToast(getActivity(), "Please device on");
            }
        } else if (action.equalsIgnoreCase("isIRSensorOnClick")) {
            sendRemoteCommand(item);
        }

        if (action.equalsIgnoreCase("longclick")) {
            int getDeviceSpecificValue = 0;
            if (!TextUtils.isEmpty(item.getDevice_sub_status())) {
                getDeviceSpecificValue = Integer.parseInt(item.getDevice_sub_status());
            }
            FanDialog fanDialog = new FanDialog(getActivity(), item.getDeviceId(), item.getDeviceName(), getDeviceSpecificValue, new onCallback() {
                @Override
                public void onSuccess(int str) {
                    sectionedExpandableLayoutHelper.updateFanDevice(item.getDeviceId(), str);
                }
            });
            fanDialog.show();
        } else if (action.equalsIgnoreCase("textclick")) {
            getDeviceDetails(item.getOriginal_room_device_id());

        } else if (action.equalsIgnoreCase("isIRSensorClick")) {
            Intent intent = new Intent(getActivity(), IRBlasterRemote.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("REMOTE_IS_ACTIVE", item.getDeviceStatus());
            bundle.putSerializable("REMOTE_ID", item.getOriginal_room_device_id());
            bundle.putSerializable("ROOM_DEVICE_ID", item.getRoomDeviceId()); //MOOD_DEVICE_ID
            intent.putExtra("IR_BLASTER_ID", item.getSensor_id());
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    /**
     * click device item adapter click event
     *
     * @param item
     * @param action
     */
    @Override
    public void itemClicked(final PanelVO item, String action) {
    }

    public void showBottomSheetDialog(RoomVO roomVO) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);
        LinearLayout linear_bottom_log = view.findViewById(R.id.linear_bottom_log);
        View view_log = view.findViewById(R.id.view_log);

        view_log.setVisibility(View.VISIBLE);
        linear_bottom_log.setVisibility(View.VISIBLE);

        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + roomVO.getRoomName() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(getActivity(), AddMoodActivity.class);
                intent.putExtra("editMode", true);
                intent.putExtra("moodVO", roomVO);
                intent.putExtra("panel_id", panel_id);
                intent.putExtra("panel_name", panel_name);
                intent.putExtra("isMap", true);
                intent.putExtra("isMoodAdapter", true);
                startActivity(intent);//
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                if (roomVO.getIs_schedule() == 1) {
                    msg = "You have schedule set on this mood.\nAre you sure you want to delete mood ?";
                } else {
                    msg = "Are you sure you want to delete mood ?";
                }
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", msg, new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteMood(roomVO.getRoomId(), panel_id, roomVO.getRoomName());
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getActivity().getFragmentManager(), "dialog");
            }
        });

        linear_bottom_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), DeviceLogActivity.class);
                intent.putExtra("ROOM_ID", roomVO.getRoomId());
//            intent.putExtra("isCheckActivity","mood");
                intent.putExtra("isCheckActivity", "mode");
                intent.putExtra("isRoomName", "" + roomVO.getRoomName());
                startActivity(intent);
            }
        });
    }

    /**
     * get Room name and Panel name using original_room_device_id
     *
     * @param original_room_device_id - original room device id
     */

    private void getDeviceDetails(String original_room_device_id) {

    /*    String url = ChatApplication.url + Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id;
        if (!token_id.equalsIgnoreCase("")) {
            url = url + "/" + token_id;
        }

        new GetJsonTask2(getActivity(), url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");

                        showDeviceDialog(room_name, panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error, int responseCode) {
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().GetDeviceDetails(original_room_device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                int code = 0;
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        JSONObject object = result.getJSONObject("data");
                        String room_name = object.getString("room_name");
                        String panel_name = object.getString("panel_name");

                        showDeviceDialog(room_name, panel_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public synchronized void showDeviceDialog(String roomName, String panelName) {

        if (dialog == null) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_virtual_devices);
        }

        TextView txtRoom = dialog.findViewById(R.id.vtxt_room);
        TextView txtPanel = dialog.findViewById(R.id.vtvt_panel);

        txtRoom.setText(roomName);
        txtPanel.setText(panelName);

        Button btnOK = dialog.findViewById(R.id.vbtn_ok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    /*--move all onResume code--*/
    public void onLoadFragment() {

        ChatApplication.isRefreshMood = true;
        try {

            ChatApplication app = ChatApplication.getInstance();
            if (mSocket != null && mSocket.connected()) {
                ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id());
            } else {
                mSocket = app.getSocket();
            }

            if (moodList.size() == 0 || ChatApplication.isRefreshHome || ChatApplication.isRefreshMood) {
                getDeviceList();
                ChatApplication.isRefreshHome = false;  //true
                ChatApplication.isRefreshMood = false; //true
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*start socket connection */
    public void startSocketConnection() throws Exception {

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
            ChatApplication.logDisplay("mSocket.connected  return.." + mSocket.id());
        } else {
            mSocket = app.getSocket();
        }
        if (mSocket != null && mSocket.connected()) {
            mSocket.on("changeDeviceStatus", changeDeviceStatus);
            mSocket.on("updateChildUser", updateChildUser);
            mSocket.on("changeMoodStatus", roomStatus);

            ChatApplication.logDisplay("mSocket.connect()= " + mSocket.id());
        }
    }

    /// all webservice call below.
    public void getDeviceList() {
        ChatApplication.logDisplay("getDeviceList");
        if (getActivity() == null) {
            return;
        }

       /* String url = ChatApplication.url + Constants.moodList;
        if (!token_id.equalsIgnoreCase("")) {
            url = url + "/" + token_id;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(activity, Constants.USER_ID));
            jsonObject.put("room_type", "mood");
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("url is " + url + " " + jsonObject);
        new GetJsonTask2(getActivity(), url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                responseErrorCode.onSuccess();
                sectionedExpandableLayoutHelper.setClickable(true);
                swipeRefreshLayout.setRefreshing(false);
                ChatApplication.logDisplay(" getDeviceList onSuccess " + result.toString());
                try {
                    moodList.clear();
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    moodList.addAll(JsonHelper.parseRoomArray(roomArray, false));

                    sectionedExpandableLayoutHelper.addSectionList(moodList);
                    sectionedExpandableLayoutHelper.notifyDataSetChanged();

                    if (moodList.size() > 0) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    } else {
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    if (moodList.size() == 0) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int responseCode) {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                if (responseCode == 503) {
                    responseErrorCode.onErrorCode(responseCode);
                }
                if (moodList.size() == 0) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                } else {
                    txt_empty_schedule.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetMoodList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                responseErrorCode.onSuccess();
                sectionedExpandableLayoutHelper.setClickable(true);
                swipeRefreshLayout.setRefreshing(false);
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay(" getDeviceList onSuccess " + result.toString());
                    moodList.clear();
                    JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    moodList.addAll(JsonHelper.parseRoomArray(roomArray, false));

                    sectionedExpandableLayoutHelper.addSectionList(moodList);
                    sectionedExpandableLayoutHelper.notifyDataSetChanged();

                    if (moodList.size() > 0) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        txt_empty_schedule.setVisibility(View.GONE);
                    } else {
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    if (moodList.size() == 0) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        txt_empty_schedule.setVisibility(View.VISIBLE);
                    } else {
                        txt_empty_schedule.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onData_FailureResponse() {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);

                if (moodList.size() == 0) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                } else {
                    txt_empty_schedule.setVisibility(View.GONE);
                }
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                swipeRefreshLayout.setRefreshing(false);
                sectionedExpandableLayoutHelper.setClickable(true);
                if (moodList.size() == 0) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    txt_empty_schedule.setVisibility(View.VISIBLE);
                } else {
                    txt_empty_schedule.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * DeleteMood
     *
     * @param moodId
     * @param panel_id
     * @param room_name
     */
    public void deleteMood(String moodId, String panel_id, String room_name) {

        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
            return;
        }

        /*dev arpan update on 24 june 2020*/

        /*JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("mood_id", roomId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(getActivity(), "Please Wait.", false);
        String url = ChatApplication.url + Constants.mooddelete;
        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override

            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("deleteMood onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(getActivity(), message);
                        }
                        getDeviceList();
                    } else {
                        ChatApplication.showToast(getActivity(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("deleteMood onFailure " + error);
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().DeleteMood(moodId, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("deleteMood onSuccess " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(getActivity(), message);
                        }
                        getDeviceList();
                    } else {
                        ChatApplication.showToast(getActivity(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.logDisplay("deleteMood onFailure ");
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("deleteMood onFailure ");
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    /*changeMoodStatus like on/off*/
    public void changeMoodStatus(RoomVO moodVO, String panel_id) {

        ChatApplication.logDisplay("changeMoodStatus changeMoodStatus");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
            return;
        }

        /*JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("mood_id", moodVO.getRoomId());
            *//*  obj.put("panel_id",panel_id);*//*
            obj.put("mood_status", moodVO.getRoom_status());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.mood_status;
        ChatApplication.logDisplay("status is " + url + " " + obj);*/
       /* new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("moodStatusOnOff onSuccess " + result.toString());
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("changeMoodStatus onFailure " + error);
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().ChangeMoodStatus(moodVO, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("moodStatusOnOff onSuccess " + result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ChatApplication.logDisplay("changeMoodStatus onFailure ");
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("changeMoodStatus onFailure " + error);
                ActivityHelper.dismissProgressDialog();
            }
        });

    }

    private void sendRemoteCommand(final DeviceVO item) {

        if (!ActivityHelper.isConnectingToInternet(getContext())) {
            ChatApplication.showToast(getActivity(), getResources().getString(R.string.disconnect));
            return;
        }

     /*   JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("device_id", item.getDeviceId());
            obj.put("device_status", item.getDeviceStatus());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.CHANGE_DEVICE_STATUS;

        ChatApplication.logDisplay("result is ir " + url + " " + obj);*/

       /* new GetJsonTaskRemote(getContext(), url, "POST", obj.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("result is ir " + result);
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    if (code == 200) {
                    } else {
                        ChatApplication.showToast(getActivity(), item.getDeviceName() + " " + getString(R.string.ir_error));
                        sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result is ir error " + error.toString());
                ChatApplication.showToast(getActivity(), item.getDeviceName() + " " + getString(R.string.ir_error));
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
            }

        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().SendRemoteCommand(item, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("result is ir " + result);
                    ActivityHelper.dismissProgressDialog();
                    int code = result.getInt("code");
                    if (code == 200) {
                    } else {
                        ChatApplication.showToast(getActivity(), item.getDeviceName() + " " + getString(R.string.ir_error));
                        sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(getActivity(), item.getDeviceName() + " " + getString(R.string.ir_error));
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("result is ir error " + error.toString());
                ChatApplication.showToast(getActivity(), item.getDeviceName() + " " + getString(R.string.ir_error));
                sectionedExpandableLayoutHelper.updateDeviceBlaster(item.getDeviceId(), "" + item.getOldStatus());
            }
        });

    }
}