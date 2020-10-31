package com.spike.bot.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.UserChildActivity;
import com.spike.bot.adapter.ChildUserAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.UserChildAction;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Sagar on 6/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserChildListFragment extends Fragment implements View.OnClickListener, UserChildAction {

    public String userIdDown = "";
    public View view;
    public RecyclerView recyclerUserList;
    public ArrayList<User> userArrayList = new ArrayList<>();
    ChildUserAdapter childUserAdapter;
    LinearLayout linear_child_list;

    public static UserChildListFragment newInstance() {
        UserChildListFragment fragment = new UserChildListFragment();
        Bundle args = new Bundle();
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

    private void setUiId() {
        recyclerUserList = view.findViewById(R.id.recyclerUserList);
        linear_child_list = view.findViewById(R.id.linear_child_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerUserList.setLayoutManager(linearLayoutManager);
        getUserCHildList();
    }

    @Override
    public void onClick(View v) {
    }

    /* child user list */
    public void getUserCHildList() {

        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
        String url = ChatApplication.url + Constants.getChildUsers;

        /*ChatApplication.logDisplay("url is " + url);
        new GetJsonTask2(getActivity(), url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    if (userArrayList != null) {
                        userArrayList.clear();
                    }

                    int code = result.getInt("code");

                    if (code == 200) {

                        JSONObject object = new JSONObject(result.toString());

                        JSONObject object1 = object.optJSONObject("data");
                        JSONArray jsonArray = object1.getJSONArray("child_list");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object2 = jsonArray.optJSONObject(i);

                            User user = new User();
                            user.setUser_id(object2.optString("user_id"));
                            user.setFirstname(object2.optString("user_name"));
                            user.setAdmin("0");
                            if (userIdDown.equals(object2.optString("user_id"))) {
                                user.setIsopen(true);
                            } else {
                                user.setIsopen(false);
                            }


                            JSONArray jsonArray1 = object2.optJSONArray("roomList");
                            JSONArray jsonArray2 = object2.optJSONArray("cameraList");

                            String roomList = "";
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject object3 = jsonArray1.optJSONObject(j);

                                roomList = roomList + object3.optString("room_id") + ",";
                            }

                            String cameraList = "";
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                JSONObject object3 = jsonArray2.optJSONObject(j);

                                cameraList = cameraList + object3.optString("camera_id") + ",";
                            }

                            if (!TextUtils.isEmpty(roomList)) {
                                ArrayList<String> myList = new ArrayList<String>(Arrays.asList(roomList.split(",")));
                                user.setRoomList(myList);
                            }

                            if (!TextUtils.isEmpty(cameraList)) {
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
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().GetUserCHildList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();

                try {
                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());
                    if (userArrayList != null) {
                        userArrayList.clear();
                    }

                    int code = result.getInt("code");

                    if (code == 200) {

                        JSONObject object = new JSONObject(result.toString());

                        JSONObject object1 = object.optJSONObject("data");
                        JSONArray jsonArray = object1.getJSONArray("child_list");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object2 = jsonArray.optJSONObject(i);

                            User user = new User();
                            user.setUser_id(object2.optString("user_id"));
                            user.setFirstname(object2.optString("first_name")); //  first name == firstname  as per api
                            user.setLastname(object2.optString("user_name"));  // last name  == user name as per api
                            user.setAdmin("0");
                            if (userIdDown.equals(object2.optString("user_id"))) {
                                user.setIsopen(true);
                            } else {
                                user.setIsopen(false);
                            }


                            JSONArray jsonArray1 = object2.optJSONArray("roomList");
                            JSONArray jsonArray2 = object2.optJSONArray("cameraList");

                            String roomList = "";
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject object3 = jsonArray1.optJSONObject(j);

                                roomList = roomList + object3.optString("room_id") + ",";
                            }

                            String cameraList = "";
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                JSONObject object3 = jsonArray2.optJSONObject(j);

                                cameraList = cameraList + object3.optString("camera_id") + ",";
                            }

                            if (!TextUtils.isEmpty(roomList)) {
                                ArrayList<String> myList = new ArrayList<String>(Arrays.asList(roomList.split(",")));
                                user.setRoomList(myList);
                            }

                            if (!TextUtils.isEmpty(cameraList)) {
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });


    }

    private void setAdapter() {
        if (userArrayList.size() > 0) {
            childUserAdapter = new ChildUserAdapter(getActivity(), userArrayList, this, this);
            recyclerUserList.setAdapter(childUserAdapter);
        }
    }

    /*confim dialog */
    private void showDeleteDialog(final int position, final User user) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete child user ? " + user.getFirstname(), new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {

                deleteUserChild(position, user);
            }

            @Override
            public void onConfirmDialogNoClick() {
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    /*child user delete */
    private void deleteUserChild(final int position, User user) {
        ActivityHelper.showProgressDialog(getActivity(), "Please Wait...", false);
       /* String url = ChatApplication.url + Constants.DeleteChildUser;

        JSONObject object = new JSONObject();
        try {
            object.put("user_id", Common.getPrefValue(getActivity(), Constants.USER_ID));
            object.put("child_user_id", user.getUser_id());
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + object.toString());
        new GetJsonTask2(getActivity(), url, "POST", object.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getActivity(), message);

                        userArrayList.remove(position);
                        childUserAdapter.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(getActivity(), message);
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
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().DeleteUserChild(user.getUser_id(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {

                try {

                    JSONObject result = new JSONObject(stringResponse);

                    ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.showToast(getActivity(), message);

                        userArrayList.remove(position);
                        childUserAdapter.notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(getActivity(), message);
                    }
                } catch (JSONException e) {
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
                ActivityHelper.dismissProgressDialog();
            }
        });



    }

    public void BackgroundBlurred() {
        recyclerUserList.setBackgroundResource(R.color.automation_transparent);
    }

    public void Backgroundwhite() {
        recyclerUserList.setBackgroundResource(R.color.automation_white);
    }

    @Override
    public void actionCHild(String type, int position, User user, boolean ispopupshow) {
        if (type.equalsIgnoreCase("update")) {
            showBottomSheetDialog(type, position, user);
        }
    }


    public void showBottomSheetDialog(String type, int position, User user) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);
        txt_edit.setText("Edit");

        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + user.getFirstname() + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                userIdDown = user.getUser_id();
                Intent intent = new Intent(getActivity(), UserChildActivity.class);
                intent.putExtra("modeType", "update");
                intent.putExtra("arraylist", user);
                startActivityForResult(intent, 1001);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDeleteDialog(position, user);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ChatApplication.logDisplay("activity is 11 " + requestCode + " " + requestCode);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setUiId();
        }
    }
}
