package com.spike.bot.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.activity.ProfileActivity;
import com.spike.bot.activity.UserChildActivity;
import com.spike.bot.adapter.ChildUserAdapter;
import com.spike.bot.adapter.MoodExpandableLayoutHelper;
import com.spike.bot.adapter.RoomeListAdapter;
import com.spike.bot.adapter.UserRoomExpandableLayoutHelper;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.listener.UserChildAction;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Sagar on 6/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserChildListFragment extends Fragment implements View.OnClickListener ,UserChildAction {

    public String userIdDown="";
    public View view;
    public RecyclerView recyclerUserList;
    ChildUserAdapter childUserAdapter;
    public ArrayList<User> userArrayList=new ArrayList<>();
    public static ArrayList<RoomVO> roomList = new ArrayList<>();
    public static ArrayList<CameraVO> cameraList = new ArrayList<CameraVO>();

    UserRoomExpandableLayoutHelper sectionedExpandableLayoutHelper;

    public static UserChildListFragment newInstance() {
        UserChildListFragment fragment = new UserChildListFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_users_child_list, container, false);

        setUiId();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    private void setUiId() {

        recyclerUserList = view.findViewById(R.id.recyclerUserList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerUserList.setLayoutManager(linearLayoutManager);

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sectionedExpandableLayoutHelper = new UserRoomExpandableLayoutHelper(getActivity(), recyclerUserList, UserChildListFragment.this, Constants.SWITCH_NUMBER);
//            }
//        });

        getUserCHildList();
    }

    @Override
    public void onClick(View v) {
    }

    public void getUserCHildList() {

        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        String url = ChatApplication.url + Constants.getChildUsers;

        ChatApplication.logDisplay("url is "+url);
        new GetJsonTask2(getActivity(), url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    if(userArrayList!=null){
                        userArrayList.clear();
                    }

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){

//                        roomList.clear();
//                        JSONObject dataObject = result.getJSONObject("data");
//                        JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
//                        roomList.addAll(JsonHelper.parseRoomArray(roomArray,false));

//                        sectionedExpandableLayoutHelper.addSectionList(roomList);
//                        sectionedExpandableLayoutHelper.notifyDataSetChanged();

                        JSONObject object=new JSONObject(result.toString());

                        JSONObject object1=object.optJSONObject("data");
                        JSONArray jsonArray=object1.getJSONArray("child_list");
                        for(int i=0; i<jsonArray.length(); i++){

                            JSONObject object2=jsonArray.optJSONObject(i);

                            User user=new User();
                            user.setUser_id(object2.optString("user_id"));
                            user.setFirstname(object2.optString("user_name"));
                            user.setAdmin("0");
                            if(userIdDown.equals(object2.optString("user_id"))){
                                user.setIsopen(true);
                            }else {
                                user.setIsopen(false);
                            }


                            JSONArray jsonArray1=object2.optJSONArray("roomList");
                            JSONArray jsonArray2=object2.optJSONArray("cameraList");

                            String roomList="";
                            for(int j=0;j<jsonArray1.length(); j++){
                                JSONObject object3=jsonArray1.optJSONObject(j);

                                roomList=roomList+object3.optString("room_id")+",";
                            }

                            String cameraList="";
                            for(int j=0;j<jsonArray2.length(); j++){
                                JSONObject object3=jsonArray2.optJSONObject(j);

                                cameraList=cameraList+object3.optString("camera_id")+",";
                            }

                            if(!TextUtils.isEmpty(roomList)){
                                ArrayList<String> myList = new ArrayList<String>(Arrays.asList(roomList.split(",")));
                                user.setRoomList(myList);
                            }

                            if(!TextUtils.isEmpty(cameraList)){
                                ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(cameraList.split(",")));
                                user.setCameralist(arrayList);
                            }

                            userArrayList.add(user);
                        }

                        setAdapter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                ActivityHelper.dismissProgressDialog();

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setAdapter() {
        if(userArrayList.size()>0){
            childUserAdapter=new ChildUserAdapter(getActivity(),userArrayList,this,roomList,cameraList);
            recyclerUserList.setAdapter(childUserAdapter);
        }
    }

    private void showDeleteDialog(final int position, final User user){
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete child user ? "+user.getFirstname(), new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {

                deleteUserChild(position,user);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    private void deleteUserChild(final int position, User user) {
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        String url = ChatApplication.url + Constants.DeleteChildUser;

        JSONObject object=new JSONObject();
        try {
            object.put("child_user_id",user.getUser_id());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is "+url);
        new GetJsonTask2(getActivity(), url, "POST", object.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        ChatApplication.showToast(getActivity(),message);

                        userArrayList.remove(position);
                        childUserAdapter.notifyDataSetChanged();
                    }else {
                        ChatApplication.showToast(getActivity(),message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                ActivityHelper.dismissProgressDialog();

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void actionCHild(String type,int position,User user) {
        if(type.equalsIgnoreCase("update")){
            userIdDown=user.getUser_id();
            Intent intent=new Intent(getActivity(),UserChildActivity.class);
            intent.putExtra("modeType","update");
            intent.putExtra("arraylist",user);
            startActivityForResult(intent,1001);
        }else {
            showDeleteDialog(position,user);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ChatApplication.logDisplay("activity is 11 "+requestCode+" "+requestCode);
        if(requestCode==1001 && resultCode == RESULT_OK){
            setUiId();
        }
    }

//    @Override
//    public void itemClicked(RoomVO item, String action) {
//        if(action.equalsIgnoreCase("deleteclick")){
//            showDeleteDialog(item);
//        }else if(action.equalsIgnoreCase("editclick")){
//            Intent intent=new Intent(getActivity(),UserChildActivity.class);
//            intent.putExtra("modeType","update");
//            intent.putExtra("arraylist",item);
//            startActivity(intent);
//        }
//
//    }

}
