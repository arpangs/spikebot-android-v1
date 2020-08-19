package com.spike.bot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akhilpatoliya.floating_text_button.FloatingTextButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.adapter.ScheduleAdapter;
import com.spike.bot.adapter.ScheduleClickListener;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.model.Device;
import com.spike.bot.model.ScheduleVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A chat fragment containing messages view and input form.
 */
public class ScheduleFragment extends Fragment implements View.OnClickListener, ScheduleClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static DashBoardFragment instance = null;
    public boolean isFilterType = false, isMood, isMoodAdapter, isRefreshonScroll = false;
    ImageView iv_mood_add, iv_room_add;
    LinearLayout txt_empty_scheduler, ll_room, linearMood, linearTabSchedule, ll_mood_view, ll_recycler;
    TextView txt_mood_title, txtRoomScdule;
    Button btnMoodSchedule, btnRoomSchedule;
    ScheduleAdapter scheduleRoomAdapter;
    ArrayList<ScheduleVO> scheduleRoomArrayList = new ArrayList<>();
    String isRoomMainFm = "", isActivityType = "", moodId = "", moodId2 = "", moodId3 = "", roomId = "", userName = "";
    int selection = 0;
    boolean isCallVisibleHint = false, isFABOpen = false;
    Menu mainMenu;
    DashBoardFragment.OnHeadlineSelectedListener mCallback;
    View view;
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    ResponseErrorCode responseErrorCode;
    private RecyclerView rv_mood;
    private ImageView empty_add_image;
    private Socket mSocket;
    private FloatingTextButton mFab;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
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
    private Emitter.Listener reloadScheduleList = new Emitter.Listener() {
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
                            if (args[0] != null) {

                                JSONObject jsonObject = new JSONObject(args[0].toString());
                                String schedule_id = jsonObject.getString("schedule_id");
                                String schedule_status = jsonObject.getString("schedule_status");
                                String schedule_type = jsonObject.getString("schedule_type");

                                if (isMood) {
                                    // scheduleMoodAdapter.chandeScheduleStatus(schedule_id, schedule_status);
                                    scheduleRoomAdapter.chandeScheduleStatus(schedule_id, schedule_status);
                                } else {

                                    if (isFilterType) {
                                        scheduleRoomAdapter.chandeScheduleStatus(schedule_id, schedule_status);
                                    } else {
                                        scheduleRoomAdapter.chandeScheduleStatus(schedule_id, schedule_status);
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    public ScheduleFragment() {
        super();
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMood", false);
        fragment.setArguments(args);
        return fragment;
    }

    public static ScheduleFragment newInstance(boolean isMood, String moodId, String moodId2, String moodId3, int selection, String roomId, boolean isMoodAdapter, String isActivityType, String isRoomMainFm) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMood", isMood);
        args.putString("moodId", moodId);
        args.putString("moodId2", moodId2);
        args.putString("moodId3", moodId3);
        args.putString("roomId", roomId);
        args.putString("isActivityType", isActivityType);
        args.putString("isRoomMainFm", isRoomMainFm);
        args.putInt("selection", selection);
        args.putBoolean("isMoodAdapter", isMoodAdapter);
        fragment.setArguments(args);
        return fragment;
    }

    public static DashBoardFragment getInstance() {
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
            if (activity instanceof ScheduleListActivity) {
            } else {
                try {
                    responseErrorCode = (ResponseErrorCode) activity;
                    mCallback = (DashBoardFragment.OnHeadlineSelectedListener) activity;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // startSocketConnection();
        try {
            ((Main2Activity) activity).invalidateToolbarCloudImage();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onRefresh() {
        try {
            isRefreshonScroll = true;
            swipeRefreshLayout.setRefreshing(true);
            scheduleRoomAdapter.setClickable(false);
            getDeviceList();
        } catch (NullPointerException e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInartanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        try {
            isMood = getArguments().getBoolean("isMood");
            moodId = getArguments().getString("moodId");
            roomId = getArguments().getString("roomId");
            isActivityType = getArguments().getString("isActivityType");
            moodId2 = getArguments().getString("moodId2");
            moodId3 = getArguments().getString("moodId3");
            isRoomMainFm = getArguments().getString("isRoomMainFm");
            selection = getArguments().getInt("selection");
            isMoodAdapter = getArguments().getBoolean("isMoodAdapter");
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearTabSchedule = view.findViewById(R.id.linearTabSchedule);
        mFab = view.findViewById(R.id.fab);
        setId();

        rv_mood.setHasFixedSize(true);
        rv_mood.setItemViewCacheSize(20);
        rv_mood.setDrawingCacheEnabled(true);
        rv_mood.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        if (!TextUtils.isEmpty(moodId3) || !TextUtils.isEmpty(moodId2)) {
            ll_mood_view.setVisibility(View.GONE);
        }

        iv_mood_add = view.findViewById(R.id.iv_mood_add);
        iv_room_add = view.findViewById(R.id.iv_room_add);

        if (isMood || !TextUtils.isEmpty(moodId) || !TextUtils.isEmpty(moodId2)) {
            iv_mood_add.setVisibility(View.VISIBLE);
            iv_room_add.setVisibility(View.VISIBLE);

        } else {
            iv_mood_add.setVisibility(View.GONE);
            iv_room_add.setVisibility(View.GONE);
        }

        iv_mood_add.setOnClickListener(this);
        iv_room_add.setOnClickListener(this);
        btnMoodSchedule.setOnClickListener(this);
        btnRoomSchedule.setOnClickListener(this);

        txt_empty_scheduler.setVisibility(View.GONE);
        ll_recycler.setVisibility(View.VISIBLE);

        if (!isCallVisibleHint) {
            startSocketConnection();
            onLoadFragment(0); //uncomment
        }

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        empty_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
//                if (selection != 0) {
//                    intent.putExtra("selection", selection);
//                } else {
//                    intent.putExtra("selection", !isMood ? 1 : 2);
//                }

                String moodIdPass = moodId3;
                if (TextUtils.isEmpty(moodId3)) {
                    moodIdPass = moodId2;
                }
//                intent.putExtra("isScheduleClick", true);
//                intent.putExtra("moodId", moodIdPass);
//                intent.putExtra("roomId", moodIdPass);
                if (TextUtils.isEmpty(moodId) && TextUtils.isEmpty(moodId2) && TextUtils.isEmpty(moodId3)) {
                    intent.putExtra("isEditOpen", false);
                } else {
                    intent.putExtra("isEditOpen", true);
                }
//                intent.putExtra("isMoodSelected", true);
                intent.putExtra("isActivityType", "" + 2);
                startActivity(intent);

            }
        });

        setViewClickLister();

        return view;
    }

    private void setViewClickLister() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFABMenu();
            }
        });
    }

    /**
     * Display Fab Menu with subFab Button
     */

    private void showFABMenu() {

        isFABOpen = true;

        Intent intent = new Intent(getActivity(), ScheduleActivity.class);
        if (isMood) {
            if (isRoomMainFm.equals("room")) {
                intent.putExtra("selection", 1);
            } else {
                intent.putExtra("selection", 2);
            }
        } else {
            intent.putExtra("selection", !isFilterType ? 1 : 2);
        }

        String moodIdPass = moodId3;
        if (TextUtils.isEmpty(moodId3)) {
            moodIdPass = moodId2;
        }
        intent.putExtra("isScheduleClick", true);
        intent.putExtra("moodId", moodIdPass);
        intent.putExtra("roomId", moodIdPass);
        if (TextUtils.isEmpty(moodId) && TextUtils.isEmpty(moodId2) && TextUtils.isEmpty(moodId3)) {
            intent.putExtra("isEditOpen", false);
        } else {
            intent.putExtra("isEditOpen", false);
        }
        intent.putExtra("isMoodSelected", isFilterType);
        intent.putExtra("isActivityType", "" + 2);
        startActivity(intent);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCallVisibleHint = true;
        if (isVisibleToUser) {
            startSocketConnection();
        }
    }

    public void setId() {
        ll_mood_view = view.findViewById(R.id.ll_mood_view);
        ll_recycler = view.findViewById(R.id.ll_recycler);
        linearTabSchedule = view.findViewById(R.id.linearTabSchedule);
        btnRoomSchedule = view.findViewById(R.id.btnRoomSchedule);
        btnMoodSchedule = view.findViewById(R.id.btnMoodSchedule);

        txtRoomScdule = view.findViewById(R.id.txtRoomScdule);
        txt_mood_title = view.findViewById(R.id.txt_mood_title);
        empty_add_image = view.findViewById(R.id.empty_add_image);

        ll_room = view.findViewById(R.id.ll_room);
        txt_empty_scheduler = view.findViewById(R.id.txt_empty_schedule);
        linearMood = view.findViewById(R.id.linearMood);

        rv_mood = view.findViewById(R.id.rv_mood);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            ((Main2Activity) activity).invalidateToolbarCloudImage();
        }catch (Exception e){
            e.printStackTrace();
        }
        // 11
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_mood.setLayoutManager(linearLayoutManager);

        if (activity instanceof Main2Activity) {
            userName = ((Main2Activity) getActivity()).toolbarTitle.getText().toString();
        }
        setId();
        if (isMood) {
            linearTabSchedule.setVisibility(View.GONE);
        } else {
            linearTabSchedule.setVisibility(View.VISIBLE);
        }
        if (scheduleRoomArrayList.size() == 0) {
            txt_empty_scheduler.setVisibility(View.VISIBLE);
            rv_mood.setVisibility(View.GONE);
        } else if (scheduleRoomArrayList.size() > 0) {
            txt_empty_scheduler.setVisibility(View.GONE);
            rv_mood.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            txt_empty_scheduler.setVisibility(View.GONE);
            ll_recycler.setVisibility(View.VISIBLE);
            rv_mood.setVisibility(View.GONE);
        }
        updateButton(btnRoomSchedule, btnMoodSchedule);

        if (ChatApplication.isScheduleNeedResume) {
            scheduleRoomArrayList.clear();
            ChatApplication.isScheduleNeedResume = false;
            onLoadFragment(1);
        }

    }

    /*start socket connection */
    public void startSocketConnection() {

        ChatApplication app = ChatApplication.getInstance();

        if (mSocket != null && mSocket.connected()) {
            try {
                mSocket.on("changeScheduleStatus", reloadScheduleList);
                mSocket.on("updateChildUser", updateChildUser);
                onLoadFragment(2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            mSocket = app.getSocket();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("updateChildUser", updateChildUser);
            mSocket.off("ReloadDeviceStatusApp", reloadScheduleList);

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_mood_add) {

            ScheduleVO scheduleVO = new ScheduleVO();
            if (scheduleRoomArrayList.size() > 0) {
                for (ScheduleVO scheduleVOL : scheduleRoomArrayList) {

                    if (scheduleVOL.getMood_id().equalsIgnoreCase(moodId)) {
                        scheduleVO = scheduleVOL;
                    }
                }
            }
            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
            //scheduleVO //moodId
            intent.putExtra("isScheduleClick", true);
            intent.putExtra("scheduleVO", scheduleVO);
            intent.putExtra("moodId", moodId2);
            intent.putExtra("selection", 2);
            intent.putExtra("isActivityType", "" + 1);
            startActivity(intent);

        } else if (view.getId() == R.id.iv_room_add) {
            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
            intent.putExtra("isScheduleClick", true);
            intent.putExtra("roomId", roomId);
            intent.putExtra("selection", 1);
            intent.putExtra("isActivityType", "" + 1);
            startActivity(intent);
        } else if (view.getId() == R.id.btnRoomSchedule) {
            isFilterType = false;
            updateButton(btnRoomSchedule, btnMoodSchedule);
            getDeviceList();
        } else if (view.getId() == R.id.btnMoodSchedule) {
            isFilterType = true;
            updateButton(btnRoomSchedule, btnMoodSchedule);
            getDeviceList();
        }
    }

    /*update button status */
    private void updateButton(Button btnDeviceDialog, Button btnSensorDialog) {

        if (isFilterType) {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnDeviceDialog.setTextColor(getResources().getColor(R.color.solid_blue));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.automation_white));
        } else {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));

            btnDeviceDialog.setTextColor(getResources().getColor(R.color.automation_white));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.solid_blue));
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.menu_main, menu);
        mainMenu = menu;
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
            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
            if (selection != 0) {
                intent.putExtra("selection", selection);
            } else {
                intent.putExtra("selection", !isMood ? 1 : 2);
            }

            String moodIdPass = moodId3;
            if (TextUtils.isEmpty(moodId3)) {
                moodIdPass = moodId2;
            }
            intent.putExtra("isScheduleClick", true);
            intent.putExtra("moodId", moodIdPass);
            intent.putExtra("roomId", moodIdPass);
            if (TextUtils.isEmpty(moodId) && TextUtils.isEmpty(moodId2) && TextUtils.isEmpty(moodId3)) {
                intent.putExtra("isEditOpen", false);
            } else {
                intent.putExtra("isEditOpen", true);
            }
            intent.putExtra("isMoodSelected", true);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_setting) {
            //openSettingPopup(tv_header);
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    public void onLoadFragment(int countShow) {

        ChatApplication app = ChatApplication.getInstance();
        if (mSocket != null && mSocket.connected()) {
        } else {
            mSocket = app.getSocket();
        }
        if (view == null) {
            return;
        }
        if (linearTabSchedule == null) {
            linearTabSchedule = view.findViewById(R.id.linearTabSchedule);
        }

        getDeviceList();

        if (scheduleRoomArrayList.size() == 0) {
            if (txt_empty_scheduler != null) {
                txt_empty_scheduler.setVisibility(View.VISIBLE);
                rv_mood.setVisibility(View.GONE);
            }
        } else if (scheduleRoomArrayList.size() > 0) {
            if (txt_empty_scheduler != null) {
                txt_empty_scheduler.setVisibility(View.GONE);
                ll_recycler.setVisibility(View.VISIBLE);
                rv_mood.setVisibility(View.VISIBLE);
            }
        } else {
            if (txt_empty_scheduler != null) {
                txt_empty_scheduler.setVisibility(View.GONE);
                ll_recycler.setVisibility(View.VISIBLE);
                rv_mood.setVisibility(View.VISIBLE);
            }
        }
    }

    /// all webservice call below.
    public void getDeviceList() {

        try {
            ((Main2Activity) activity).invalidateToolbarCloudImage();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (ChatApplication.url.contains("http://")) // dev arpan add this condition on 27 june
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        if (getActivity() == null) {
            return;
        }

       /* String url = ChatApplication.url + Constants.GET_SCHEDULE_LIST;//+userId;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

            *//*type check & pass value *//*
            if (TextUtils.isEmpty(isActivityType)) {
                jsonObject.put("schedule_device_type", isFilterType ? "mood" : "room");
            } else {
                if (TextUtils.isEmpty(moodId)) {
                    jsonObject.put("schedule_device_type", "room");
                    jsonObject.put("room_id", "" + moodId3);
                } else {
                    jsonObject.put("schedule_device_type", "mood");
                    jsonObject.put("room_id", "" + moodId); //moodId1
                }
            }

            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        if (ChatApplication.isShowProgress) {
            ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);
        }

       /* ChatApplication.logDisplay("jsonObject is schedule " + url + " " + jsonObject.toString());
        new GetJsonTask2(getActivity(), url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.isShowProgress = false;
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                try {
                    ll_recycler.setVisibility(View.VISIBLE);
                    txt_empty_scheduler.setVisibility(View.VISIBLE);
                    rv_mood.setVisibility(View.GONE);
                    ChatApplication.logDisplay("schedule is " + result.toString());
                    if (result.optJSONArray("data") != null && result.optJSONArray("data").length() > 0) {
                        scheduleRoomArrayList.clear();
                        scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(result.optJSONArray("data")));

                        setAdapter();

                        if (isRefreshonScroll) {
                            isRefreshonScroll = false;
                        }
                    }


                } catch (Exception e) {
                    ChatApplication.isScheduleNeedResume = true;

                    scheduleRoomArrayList.clear();
                    scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
                    rv_mood.setAdapter(scheduleRoomAdapter);
                    scheduleRoomAdapter.notifyDataSetChanged();


                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int responseCode) {
                swipeRefreshLayout.setRefreshing(false);
                if (responseCode == 503) {
                    responseErrorCode.onErrorCode(responseCode);
                }
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getScheduleList onFailure " + error);
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().GetScheduleList(isActivityType, isFilterType, moodId, moodId3, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ChatApplication.isShowProgress = false;
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ll_recycler.setVisibility(View.VISIBLE);
                    txt_empty_scheduler.setVisibility(View.VISIBLE);
                    rv_mood.setVisibility(View.GONE);
                    ChatApplication.logDisplay("schedule is " + result.toString());
                    if (result.optJSONArray("data") != null && result.optJSONArray("data").length() > 0) {
                        scheduleRoomArrayList.clear();
                        scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(result.optJSONArray("data")));

                        setAdapter();

                        if (isRefreshonScroll) {
                            isRefreshonScroll = false;
                        }
                    }


                } catch (Exception e) {
                    ChatApplication.isScheduleNeedResume = true;

                    scheduleRoomArrayList.clear();
                    scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
                    rv_mood.setAdapter(scheduleRoomAdapter);
                    scheduleRoomAdapter.notifyDataSetChanged();


                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onData_FailureResponse() {
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getScheduleList onFailure " + error);
            }
        });


    }

    private void setAdapter() {
        if (scheduleRoomArrayList.size() > 0) {
            txt_empty_scheduler.setVisibility(View.GONE);
            ll_recycler.setVisibility(View.VISIBLE);
            rv_mood.setVisibility(View.VISIBLE);

            scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
            rv_mood.setAdapter(scheduleRoomAdapter);
            scheduleRoomAdapter.notifyDataSetChanged();

            updateButton(btnRoomSchedule, btnMoodSchedule);
            showHeader();
        } else {
            scheduleRoomArrayList.clear();
            scheduleRoomAdapter.notifyDataSetChanged();
            ll_recycler.setVisibility(View.VISIBLE);
            rv_mood.setVisibility(View.GONE);
            txt_empty_scheduler.setVisibility(View.VISIBLE);
        }

        if (isMood) {
            linearTabSchedule.setVisibility(View.GONE);
        }

    }


    /*
     *   set header for add sch... screen
     *   @moodId3 for if intent come to EditDeviceActivity
     *   @moodId2 for id intent come to Mood Sch edit fragment
     *   set Mood text for if intent come to Sch fragment
     * */

    public void showHeader() {
        underLine("Room", txtRoomScdule);
        txt_empty_scheduler.setVisibility(View.GONE);
        ll_recycler.setVisibility(View.VISIBLE);

        rv_mood.setVisibility(View.VISIBLE);
        if (isMood) {
            linearTabSchedule.setVisibility(View.GONE);
        } else {
            linearTabSchedule.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(moodId3) || !TextUtils.isEmpty(moodId2)) {
            if (!TextUtils.isEmpty(moodId3)) {
                txt_mood_title.setText("Added Schedule on Room");
            } else if (!TextUtils.isEmpty(moodId2)) {
                txt_mood_title.setText("Added Schedule on Mood");
            }
            linearMood.setVisibility(View.VISIBLE);
            iv_mood_add.setVisibility(View.GONE);
        } else {
            underLine("Mood", txt_mood_title);
        }
    }

    /*under line for name */
    public void underLine(String text, TextView textTemp) {
        String mystring = new String(text);
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textTemp.setText(content);
    }

    /*delete schedule */
    public void deleteSchedule(String schedule_id, int is_timer) {
        ChatApplication.logDisplay("deleteSchedule deleteSchedule");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            ChatApplication.showToast(getActivity().getApplicationContext(), "" + R.string.disconnect);
            return;
        }
       /* JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("schedule_id", schedule_id);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait.", false);
        /*String url = ChatApplication.url + Constants.DELETE_SCHEDULE;
        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.isScheduleNeedResume = true;
                ChatApplication.logDisplay("deleteSchedule onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ChatApplication.isScheduleNeedResume = true;

                        scheduleRoomArrayList.clear();
                        scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
                        rv_mood.setAdapter(scheduleRoomAdapter);
                        scheduleRoomAdapter.notifyDataSetChanged();
                        ChatApplication.isScheduleNeedResume = false;

                        onLoadFragment(3);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("deleteSchedule onFailure " + error);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().DeleteSchedule(schedule_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.isScheduleNeedResume = true;
                    ChatApplication.logDisplay("deleteSchedule onSuccess " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        ChatApplication.isScheduleNeedResume = true;

                        scheduleRoomArrayList.clear();
                        scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
                        rv_mood.setAdapter(scheduleRoomAdapter);
                        scheduleRoomAdapter.notifyDataSetChanged();
                        ChatApplication.isScheduleNeedResume = false;

                        onLoadFragment(3);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("deleteSchedule onFailure " + error);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*change schedule status */
    public void changeScheduleStatus(final ScheduleVO scheduleVO) {
        ChatApplication.logDisplay("changeScheduleStatus changeScheduleStatus");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);

        /*String timer_on_date = "";
        String timer_off_date = "";

        JSONObject obj = new JSONObject();
        try {
            ChatApplication.logDisplay("is ative is " + scheduleVO.getIs_active());
            obj.put("schedule_id", scheduleVO.getSchedule_id());
            obj.put("is_active", scheduleVO.getIs_active() == 1 ? "y" : "n");

            if (scheduleVO.getIs_active() == 1 && scheduleVO.getSchedule_type() == 1) {

                String sch_on_after = scheduleVO.getSchedule_device_on_time().trim();
                String sch_off_date = scheduleVO.getSchedule_device_off_time().trim();

                if (!TextUtils.isEmpty(sch_on_after) && !TextUtils.isEmpty(sch_off_date) && !sch_off_date.equalsIgnoreCase("null")) {
                    ChatApplication.logDisplay("timer is " + sch_on_after);
                    try {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        sch_on_after = Common.getHH(sch_on_after);
                        ChatApplication.logDisplay("timer is " + sch_on_after);
                        calendar.add(Calendar.HOUR, Integer.parseInt(sch_on_after.split(":")[0]));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(sch_on_after.split(":")[1]));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                        String formattedDate = dateFormat.format(calendar.getTime()).toString();

                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                        Date sendDate = null;
                        try {
                            sendDate = format.parse(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar sendCalendar = Calendar.getInstance();
                        sendCalendar.setTime(sendDate);


                        SimpleDateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        timer_on_date = sendDateFormat.format(calendar.getTime()).toString();


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(sch_off_date) && !sch_off_date.equalsIgnoreCase("null")) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            sch_off_date = Common.getHH(sch_off_date);
                            calendar.add(Calendar.HOUR, Integer.parseInt(sch_off_date.split(":")[0]));
                            calendar.add(Calendar.MINUTE, Integer.parseInt(sch_off_date.split(":")[1]));

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                            String formattedDate = dateFormat.format(calendar.getTime()).toString();

                            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                            Date sendDate = null;
                            try {
                                sendDate = format.parse(formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar sendCalendar = Calendar.getInstance();
                            sendCalendar.setTime(sendDate);


                            SimpleDateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            timer_off_date = sendDateFormat.format(calendar.getTime()).toString();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    scheduleVO.setSchedule_device_on_time(timer_on_date + " " + sch_on_after);
                    scheduleVO.setSchedule_device_off_time(timer_off_date + " " + sch_off_date);
                    obj.put("on_time", timer_on_date + " " + sch_on_after + ":00");
                    obj.put("off_time", timer_off_date + " " + sch_off_date + ":00");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.scheduleedit;
        ChatApplication.logDisplay("url is " + url + " " + obj);

        new GetJsonTask(getActivity(), url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("SchEdit changeScheduleStatus onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(getActivity().getApplicationContext(), message);
                        }
                        try {
                            for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                ScheduleVO scheduleVO2 = scheduleRoomArrayList.get(i);
                                if (scheduleVO2.getSchedule_id().equalsIgnoreCase(scheduleVO.getSchedule_id())) {
                                    scheduleRoomAdapter.notifyItemChanged(i, scheduleVO);
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay("changeScheduleStatus onFailure " + error);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().ChangeScheduleStatus(scheduleVO, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("SchEdit changeScheduleStatus onSuccess " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (!TextUtils.isEmpty(message)) {
                            ChatApplication.showToast(getActivity().getApplicationContext(), message);
                        }
                        try {
                            for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                ScheduleVO scheduleVO2 = scheduleRoomArrayList.get(i);
                                if (scheduleVO2.getSchedule_id().equalsIgnoreCase(scheduleVO.getSchedule_id())) {
                                    scheduleRoomAdapter.notifyItemChanged(i, scheduleVO);
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ChatApplication.logDisplay("changeScheduleStatus onFailure " + error);
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });


    }

    //isMood boolean used for is intent direct in Room list

    @Override
    public void itemClicked(final ScheduleVO scheduleVO, String action, boolean isMood) {

        ChatApplication.logDisplay(" action " + action);
        if (action.equalsIgnoreCase("active")) {
            changeScheduleStatus(scheduleVO);
        } else if (action.equalsIgnoreCase("edit")) {
            showBottomSheetDialog(scheduleVO, isMood);
        } else if (action.equalsIgnoreCase("delete")) {
        } else if (action.equalsIgnoreCase("log")) {

        }

    }

    @Override
    public void itemClicked(final ScheduleVO scheduleVO, String action) {

    }

    public void showBottomSheetDialog(final ScheduleVO scheduleVO, boolean isMood) {
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

        txt_bottomsheet_title.setText("What would you like to do in" + " " + scheduleVO.getSchedule_name() + "" + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String room_device_id = "";
                Gson gson = new Gson();
                List<Device> deviceList = gson.fromJson(scheduleVO.getDevicesList(), new TypeToken<List<Device>>() {
                }.getType());

                for (int i = 0; i < deviceList.size(); i++) {
                    if (i == 0) {
                        room_device_id = deviceList.get(i).getPanelDeviceId();
                    } else {
                        room_device_id = room_device_id + "," + deviceList.get(i).getPanelDeviceId();
                    }
                }
                scheduleVO.setRoom_device_id(room_device_id);
                ChatApplication.isScheduleNeedResume = true;
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("scheduleVO", scheduleVO);
                intent.putExtra("isMap", true);
                intent.putExtra("isEdit", true);

                if (isMoodAdapter) {
                    intent.putExtra("isMoodAdapter", isMoodAdapter);
                } else {
                    intent.putExtra("isMoodAdapter", isMood);
                }
                startActivity(intent);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteSchedule(scheduleVO.getSchedule_id(), scheduleVO.getIs_timer());
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
                intent.putExtra("Schedule_id", "" + scheduleVO.getSchedule_id());
                intent.putExtra("ROOM_ID", "" + scheduleVO.getSchedule_id());
                intent.putExtra("activity_type", "" + scheduleVO.getIs_timer());
                intent.putExtra("isRoomName", "" + scheduleVO.getSchedule_name());
//                                if (scheduleArrayList.get(holder.iv_schedule_dots.getId()).getIs_timer() == 0) {
                intent.putExtra("isCheckActivity", "schedule");
//                                } else {
//                                    intent.putExtra("isCheckActivity", "Timer");
//                                }
                startActivity(intent);
            }
        });
    }


}
