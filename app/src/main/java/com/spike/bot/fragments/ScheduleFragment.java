package com.spike.bot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.ScheduleActivity;
import com.spike.bot.activity.ScheduleListActivity;
import com.spike.bot.adapter.ScheduleAdapter;
import com.spike.bot.adapter.ScheduleClickListener;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.listener.ResponseErrorCode;
import com.spike.bot.model.Device;
import com.spike.bot.model.ScheduleVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A chat fragment containing messages view and input form.
 */
public class ScheduleFragment extends Fragment implements View.OnClickListener, ScheduleClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ScheduleFragment";

    private RecyclerView rv_mood;
    ImageView iv_mood_add, iv_room_add;
    LinearLayout txt_empty_scheduler, ll_room, linearMood, linearTabSchedule, ll_mood_view, ll_recycler;
    TextView txt_mood_title, txtRoomScdule;
    Button btnMoodSchedule, btnRoomSchedule;
    private ImageView empty_add_image;

    ScheduleAdapter scheduleRoomAdapter;
    ArrayList<ScheduleVO> scheduleRoomArrayList = new ArrayList<>();

//    ScheduleAdapter scheduleMoodAdapter;
//    ArrayList<ScheduleVO> scheduleMoodArrayList = new ArrayList<>();

    public boolean isFilterType = false, isMood, isMoodAdapter, isRefreshonScroll = false;
    String isRoomMainFm = "", isActivityType = "", moodId = "", moodId2 = "", moodId3 = "", roomId = "", userName = "";
    int selection = 0;
    boolean isCallVisibleHint = false;
    Menu mainMenu;

    DashBoardFragment.OnHeadlineSelectedListener mCallback;
    private Socket mSocket;
    private FloatingActionButton mFab;
    View view;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    ResponseErrorCode responseErrorCode;

    private static DashBoardFragment instance = null;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;

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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    public static DashBoardFragment getInstance() {
        return instance;
    }

    @Override
    public void onRefresh() {
        isRefreshonScroll = true;
        swipeRefreshLayout.setRefreshing(true);
        scheduleRoomAdapter.setClickable(false);
        if (activity instanceof Main2Activity) {
            if (getUserList().size() > 0) {
                displayTital();
            }

        }
//        if (isMood) {
//            //  ll_room.setVisibility(View.GONE);
//            linearTabSchedule.setVisibility(View.GONE);
//            getScheduleFromMood();
//        } else {
        getDeviceList();
//        }
    }

    private void displayTital() {
        for (int i = 0; i < getUserList().size(); i++) {
            if (getUserList().get(i).getIsActive()) {
                //mCallback.onArticleSelected("" +userName);
                break;
            }
        }
    }

    public List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        String jsonText = Common.getPrefValue(getActivity(), Common.USER_JSON);
        if (!TextUtils.isEmpty(jsonText)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(jsonText, type);
        }
        return userList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        ChatApplication.logDisplay("mood is " + isMood);
        ChatApplication.logDisplay("mood is moodId2 " + moodId2);
        ChatApplication.logDisplay("mood is moodId3 " + moodId3);
        ChatApplication.logDisplay("mood is moodId " + moodId);
        ChatApplication.logDisplay("mood is " + isActivityType);

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
    boolean isFABOpen = false;

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
            intent.putExtra("isEditOpen", true);
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
//                                if (isMood) {
//                                    linearTabSchedule.setVisibility(View.GONE);
//                                    getScheduleFromMood();
//                                } else {
                                getDeviceList();
//                                }
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

    private void updateButton(Button btnDeviceDialog, Button btnSensorDialog) {

        if (isFilterType) {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnDeviceDialog.setTextColor(getResources().getColor(R.color.txtPanal));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.automation_white));
        } else {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));

            btnDeviceDialog.setTextColor(getResources().getColor(R.color.automation_white));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.txtPanal));
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


        //showProgress();
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
//        if (isMood) {
////            ll_room.setVisibility(View.GONE);
//            linearTabSchedule.setVisibility(View.GONE);
//            getScheduleFromMood();
//        } else {
//            linearTabSchedule.setVisibility(View.VISIBLE);
        getDeviceList();
//        }

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
        if (getActivity() == null) {
            return;
        }

        String url = ChatApplication.url + Constants.GET_SCHEDULE_LIST;//+userId;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));

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
        }
        if (ChatApplication.isShowProgress) {
            ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);
        }

        ChatApplication.logDisplay("jsonObject is schedule " + url + " " + jsonObject.toString());
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
                            getDeviceListUserData(13);
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
                //scheduleMoodAdapter.setClickable(true);
//                scheduleRoomAdapter.setClickable(true);
                swipeRefreshLayout.setRefreshing(false);
                if (responseCode == 503) {
                    responseErrorCode.onErrorCode(responseCode);
                }
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getScheduleList onFailure " + error);
            }
        }).execute();

    }

    private void setAdapter() {
        if (scheduleRoomArrayList.size() > 0) {
            txt_empty_scheduler.setVisibility(View.GONE);
            ll_recycler.setVisibility(View.VISIBLE);
            rv_mood.setVisibility(View.VISIBLE);

//            if(scheduleRoomAdapter==null){
            scheduleRoomAdapter = new ScheduleAdapter(getActivity(), scheduleRoomArrayList, ScheduleFragment.this, true, false);
            rv_mood.setAdapter(scheduleRoomAdapter);
            scheduleRoomAdapter.notifyDataSetChanged();
//            }else {
//                scheduleRoomAdapter.notifyDataSetChanged();
//            }


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

        //   ll_mood_view.setVisibility(View.VISIBLE);
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

    public void underLine(String text, TextView textTemp) {
        String mystring = new String(text);
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textTemp.setText(content);
    }

    //
    public void getScheduleFromMood() {
        ChatApplication.logDisplay("getScheduleFromMood");
        if (getActivity() == null) {
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(moodId3)) {
                obj.put("room_id", moodId3);
                obj.put("room_type", 0);
            } else {
                obj.put("room_id", moodId2); //moodId1
                obj.put("room_type", 1);
            }
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ChatApplication.isShowProgress) {
            ActivityHelper.showProgressDialog(getActivity(), "Please wait...", false);
        }
        String url = ChatApplication.url + Constants.GET_SCHEDULE_ON_ROOM;

        ChatApplication.logDisplay("jsonObject is schedule mood " + url + " " + obj.toString());

        new GetJsonTask2(getActivity(), url, "POST", obj.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                scheduleRoomArrayList.clear();
                swipeRefreshLayout.setRefreshing(false);

                ChatApplication.logDisplay(" getScheduleFromMood onSuccess " + result.toString());
                try {

                    ActivityHelper.dismissProgressDialog();
                    if (result.has("data")) {

                        JSONObject scheduleObject = result.getJSONObject("data");

                        JSONArray moodArray = null;

                        if (!TextUtils.isEmpty(moodId3)) {
                            moodArray = scheduleObject.getJSONArray("scheduleList");
                        } else {

                            if (!TextUtils.isEmpty(moodId2)) {
                                moodArray = scheduleObject.getJSONArray("scheduleList");
                            } else {
                                moodArray = scheduleObject.getJSONArray("moodSchedule");
                            }
                            //  moodArray = scheduleObject.getJSONArray("roomSchedule");
                        }

                        scheduleRoomArrayList.addAll(JsonHelper.parseRoomScheduleArray(moodArray));

                        setAdapter();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int errorCode) {
                scheduleRoomAdapter.setClickable(true);
                swipeRefreshLayout.setRefreshing(false);
                ActivityHelper.dismissProgressDialog();
                try {
                    if (errorCode == 503) {
                        responseErrorCode.onErrorCode(errorCode);
                    }
                    ChatApplication.logDisplay("getScheduleFromMood onFailure " + error);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }).execute();

    }

    public void deleteSchedule(String schedule_id, int is_timer) {
        ChatApplication.logDisplay("deleteSchedule deleteSchedule");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            ChatApplication.showToast(getActivity().getApplicationContext(), "" + R.string.disconnect);
            return;
        }
        JSONObject obj = new JSONObject();
        try {

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            obj.put("schedule_id", schedule_id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait.", false);
        String url = ChatApplication.url + Constants.DELETE_SCHEDULE;
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
        }).execute();
    }

    public void changeScheduleStatus(final ScheduleVO scheduleVO) {
        ChatApplication.logDisplay("changeScheduleStatus changeScheduleStatus");
        if (!ActivityHelper.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait.", false);

        String timer_on_date = "";
        String timer_off_date = "";

        JSONObject obj = new JSONObject();
        try {
            ChatApplication.logDisplay("is ative is "+scheduleVO.getIs_active());
            obj.put("schedule_id", scheduleVO.getSchedule_id());
            obj.put("is_active", scheduleVO.getIs_active()==1 ? "y":"n");

            if (scheduleVO.getIs_active() == 1 && scheduleVO.getSchedule_type() == 1) {

                String sch_on_after = scheduleVO.getSchedule_device_on_time().trim();
                String sch_off_date = scheduleVO.getSchedule_device_off_time().trim();

                if (!TextUtils.isEmpty(sch_on_after) && !TextUtils.isEmpty(sch_off_date) && !sch_off_date.equalsIgnoreCase("null")) {
                    ChatApplication.logDisplay("timer is "+sch_on_after);
                    try {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        sch_on_after = Common.getHH(sch_on_after);
                        ChatApplication.logDisplay("timer is "+sch_on_after);
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

                    scheduleVO.setSchedule_device_on_time(timer_on_date+" "+sch_on_after);
                    scheduleVO.setSchedule_device_off_time(timer_off_date+" "+sch_off_date);
                    obj.put("on_time",timer_on_date+" "+sch_on_after+":00");
                    obj.put("off_time",timer_off_date+" "+sch_off_date+":00");

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
        ChatApplication.logDisplay("url is "+url+" "+obj);

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

//                        JSONObject jsonObjectData = result.getJSONObject("data");
//                        String schedule_id = jsonObjectData.getString("schedule_id");
//                        int is_timer = jsonObjectData.getInt("is_timer");
//                        int schedule_status = jsonObjectData.getInt("schedule_status");
//                        String schedule_device_on_time = jsonObjectData.getString("schedule_device_on_time");
//                        String timer_on_date = jsonObjectData.getString("timer_on_date");
//                        String schedule_device_off_time = jsonObjectData.getString("schedule_device_off_time");
//                        String timer_off_date = jsonObjectData.getString("timer_off_date");
//
//                        ScheduleVO scheduleVO1 = new ScheduleVO();
//                        scheduleVO1.setSchedule_id(schedule_id);
//                        scheduleVO1.setIs_timer(is_timer);
//                        scheduleVO1.setSchedule_status(schedule_status);
//                        scheduleVO1.setSchedule_device_on_time(schedule_device_on_time);
//                        scheduleVO1.setTimer_on_date(timer_on_date);
//                        scheduleVO1.setSchedule_device_off_time(schedule_device_off_time);
//                        scheduleVO1.setTimer_off_date(timer_off_date);
//                        scheduleVO1.setUser_id(scheduleVO.getUser_id());
//                        // scheduleVO1.setIs_active();

//                        if (isMood) {
//                            ll_room.setVisibility(View.GONE);
//
//                            try {
//                                for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
//                                    ScheduleVO scheduleVO2 = scheduleRoomArrayList.get(i);
//                                    if (scheduleVO2.getSchedule_id().equalsIgnoreCase(scheduleVO1.getSchedule_id())) {
//
//                                        schSetV02ToV01(scheduleVO1, scheduleVO2);
//
//                                        scheduleRoomArrayList.set(i, scheduleVO1);
//                                        scheduleRoomAdapter.notifyItemChanged(i, scheduleVO1);
//                                    }
//                                }
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//
//                        } else {

                            try {
                                for (int i = 0; i < scheduleRoomArrayList.size(); i++) {
                                    ScheduleVO scheduleVO2 = scheduleRoomArrayList.get(i);
                                    if (scheduleVO2.getSchedule_id().equalsIgnoreCase(scheduleVO.getSchedule_id())) {
//                                        scheduleRoomArrayList.set(i, scheduleVO);
                                        scheduleRoomAdapter.notifyItemChanged(i,scheduleVO);
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
//                        }

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
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*
     * Update ScheduleV0 data and notifyDataSetChanged
     * @ScheduleV01 : Json api schedule data
     * @ScheduleV02 : Old schedule data
     * schV02 old data copy/set to schV01
     *
     * */

    private void schSetV02ToV01(ScheduleVO scheduleVO1, ScheduleVO scheduleVO2) {
        scheduleVO1.setSchedule_name(scheduleVO2.getSchedule_name());
        scheduleVO1.setId(scheduleVO2.getId());
        scheduleVO1.setSchedule_type(scheduleVO2.getSchedule_type());
        scheduleVO1.setRoom_id(scheduleVO2.getRoom_id());
        scheduleVO1.setMood_id(scheduleVO2.getMood_id());
        scheduleVO1.setRoom_device_id(scheduleVO2.getRoom_device_id());
        scheduleVO1.setSchedule_device_day(scheduleVO2.getSchedule_device_day());

        scheduleVO1.setRoom_name(scheduleVO2.getRoom_name());
        scheduleVO1.setTimer_on_after(scheduleVO2.getTimer_on_after());
        scheduleVO1.setTimer_off_after(scheduleVO2.getTimer_off_after());
    }


    //isMood boolean used for is intent direct in Room list

    @Override
    public void itemClicked(final ScheduleVO scheduleVO, String action, boolean isMood) {

        ChatApplication.logDisplay(" action " + action);
        if (action.equalsIgnoreCase("active")) {
            changeScheduleStatus(scheduleVO);
        } else if (action.equalsIgnoreCase("edit")) {
            String room_device_id = "";
            Gson gson = new Gson();
            List<Device> deviceList = gson.fromJson(scheduleVO.getDevicesList(), new TypeToken<List<Device>>() {}.getType());

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
        } else if (action.equalsIgnoreCase("delete")) {
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

    }

    @Override
    public void itemClicked(final ScheduleVO scheduleVO, String action) {

    }

    /// all webservice call below.
    public void getDeviceListUserData(final int checkmessgae) {
        //showProgress();

        if (getActivity() == null) {
            return;
        }

        if (checkmessgae == 1 || checkmessgae == 6 || checkmessgae == 7 || checkmessgae == 8 || checkmessgae == 9 || checkmessgae == 10) {
            ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        }

        String url = ChatApplication.url + Constants.GET_DEVICES_LIST;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", 0);
            jsonObject.put("is_sensor_panel", 1);
            jsonObject.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            if (TextUtils.isEmpty(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE))) {
                jsonObject.put("admin", "");
            } else {
                jsonObject.put("admin", Integer.parseInt(Common.getPrefValue(getActivity(), Constants.USER_ADMIN_TYPE)));
            }
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatApplication.logDisplay("jsonObject is schedule getuser " + url + " " + jsonObject.toString());
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
                        String gateway_ip = userObject.optString("gateway_ip");
                        Common.savePrefValue(ChatApplication.getInstance(), Common.camera_key, camera_key);
                        ChatApplication.currentuserId = userId;
                        String userPassword = "";
                        mCallback.onArticleSelected("" + userFirstName);

//                        DashBoardFragment.saveCurrentId(getActivity(), userId, gateway_ip);
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
