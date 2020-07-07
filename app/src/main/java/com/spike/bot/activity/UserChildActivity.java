package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.UnassignedListRes;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.spike.bot.core.Common.showToast;

/**
 * Created by Sagar on 4/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserChildActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    public EditText edtUsername, edDisplayName, et_new_password_confirm, et_new_password;
    public RadioGroup radioGroup;
    public RadioButton radioBtnAdmin, radioBtnChild;
    public TextView txtPasswordChange, spinnerRoomList, spinnerCameraList, txtEmptyRoom, txtEmptyCamera;
    public Button btnSave, btnChangePassword;
    public RecyclerView recyclerRoom, recyclerCamera;
    public LinearLayout linearChangePassword, ll_password_view_expand, ll_pass_edittext_view;
    public ImageView img_pass_arrow;
    public String selectRoomList = "", strRoomList = "", modeType = "", strPassword = "";
    public int isChildType = 0;
    public boolean isClickFlag = false;

    public User user;
    public ArrayList<CameraVO> cameraarrayList = new ArrayList<>();
    RoomListAdapter roomListAdapter;
    CameraListAdapter cameraListAdapter;
    private ArrayList<UnassignedListRes.Data.RoomList> roomList = new ArrayList<>();
    private ArrayList<String> roomListString = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_users_child);

        modeType = getIntent().getStringExtra("modeType");

        if (modeType.equalsIgnoreCase("update")) {
            user = (User) getIntent().getSerializableExtra("arraylist");
        }

        setUiId();

    }

    private void setUiId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Add Child user");
        edtUsername = findViewById(R.id.edtUsername);
        radioGroup = findViewById(R.id.radioGroup);
        radioBtnAdmin = findViewById(R.id.radioBtnAdmin);
        radioBtnChild = findViewById(R.id.radioBtnChild);
        spinnerRoomList = findViewById(R.id.spinnerRoomList);
        spinnerCameraList = findViewById(R.id.spinnerCameraList);
        txtEmptyCamera = findViewById(R.id.txtEmptyCamera);
        txtEmptyRoom = findViewById(R.id.txtEmptyRoom);
        spinnerCameraList = findViewById(R.id.spinnerCameraList);
        edDisplayName = findViewById(R.id.edDisplayName);
        recyclerCamera = findViewById(R.id.recyclerCamera);
        recyclerRoom = findViewById(R.id.recyclerRoom);
        ll_password_view_expand = findViewById(R.id.ll_password_view);
        ll_pass_edittext_view = findViewById(R.id.ll_pass_edittext_view);
        linearChangePassword = findViewById(R.id.linearChangePassword);
        img_pass_arrow = findViewById(R.id.img_pass_arrow);
        btnSave = findViewById(R.id.btnSave);
        txtPasswordChange = findViewById(R.id.txtPasswordChange);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password_confirm = findViewById(R.id.et_new_password_confirm);
        btnSave.setVisibility(View.GONE);
        btnSave.setOnClickListener(this);
        spinnerRoomList.setOnClickListener(this);
        ll_password_view_expand.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);

        getRoomList();

        if (modeType.equalsIgnoreCase("update")) {
            btnChangePassword.setText("Change password");
            toolbar.setTitle("Edit Child User");
            txtPasswordChange.setText("Change Password");
            edDisplayName.setText(user.getFirstname());
            edtUsername.setText(user.getFirstname());
            edDisplayName.setSelection(edDisplayName.getText().length());

            edtUsername.setClickable(false);
            edtUsername.setEnabled(false);
            edDisplayName.setClickable(false);
            edDisplayName.setEnabled(false);
        } else {
            btnChangePassword.setText("Enter password");
            txtPasswordChange.setText("Enter Password");
        }

        edtUsername.setFilters(new InputFilter[]{Constants.ignoreFirstWhiteSpace()});
        et_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_new_password.getText().toString().length() > 0) {
                    isClickFlag = true;
                } else {
                    isClickFlag = false;
                }
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {

            selectRoomList = "";
            strRoomList = "";
            roomListString.clear();
            if (modeType.equalsIgnoreCase("update")) {
                if (isClickFlag) {
                    addUserChild();
                } else {
                    this.finish();
                }
            } else {
                if (edDisplayName.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter display name");
                } else if (edtUsername.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter username");
                } else if (strPassword.length() == 0) {
                    ChatApplication.showToast(this, "Please enter password");
                } else if (edtUsername.getText().toString().trim().matches(".*([ \t]).*")) {
                    edtUsername.requestFocus();
                    edtUsername.setError("Invalid Username");
                } else {
                    addUserChild();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {

        } else if (v == ll_password_view_expand) {
            if (ll_pass_edittext_view.getVisibility() == View.VISIBLE) {
                ll_pass_edittext_view.setVisibility(View.GONE);
                img_pass_arrow.setImageResource(R.drawable.icn_arrow_right);
            } else {
                img_pass_arrow.setImageResource(R.drawable.icn_arrow_down);
                ll_pass_edittext_view.setVisibility(View.VISIBLE);
            }

        } else if (v == btnChangePassword) {
            addCustomRoom();
        }
    }

    public void addCustomRoom() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);
        dialog.setCanceledOnTouchOutside(true);

        final TextInputLayout inputRoom = dialog.findViewById(R.id.inputRoom);
        final TextInputLayout inputPassword = dialog.findViewById(R.id.inputPassword);
        final TextInputLayout inputnewPassword = dialog.findViewById(R.id.inputnewPassword);
        final TextInputLayout inputconfirmPassword = dialog.findViewById(R.id.inputconfirmPassword);

        final TextInputEditText edtPasswordChild = dialog.findViewById(R.id.edtOldPasswordChild);
        final TextInputEditText edtnewPasswordChild = dialog.findViewById(R.id.edtnewPasswordChild);
        final TextInputEditText edtconfirmPasswordChild = dialog.findViewById(R.id.edtconfirmPasswordChild);

        inputRoom.setVisibility(View.GONE);
        inputPassword.setVisibility(View.VISIBLE);
        inputnewPassword.setVisibility(View.VISIBLE);
        inputconfirmPassword.setVisibility(View.VISIBLE);

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (modeType.equalsIgnoreCase("update")) {
            tv_title.setText("Change Password");
        } else {
            tv_title.setText("Change Password");
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPasswordChild.getText().length() > 0) {
                    isClickFlag = true;
                    ChatApplication.keyBoardHideForce(UserChildActivity.this);
                    dialog.cancel();
                    strPassword = edtPasswordChild.getText().toString();
                } else {
                    ChatApplication.showToast(UserChildActivity.this, "Please enter password");
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /*set value selection*/
    private void setSelectValue() {
        if (modeType.equalsIgnoreCase("update")) {
            for (int i = 0; i < roomList.size(); i++) {
                for (int j = 0; j < user.getRoomList().size(); j++) {
                    if (user.getRoomList().get(j).equalsIgnoreCase(roomList.get(i).getRoomId())) {
                        roomList.get(i).setDisable(true);
                    }
                }
            }

            for (int i = 0; i < cameraarrayList.size(); i++) {
                for (int j = 0; j < user.getCameralist().size(); j++) {
                    if (user.getCameralist().get(j).equalsIgnoreCase(cameraarrayList.get(i).getCamera_id())) {
                        cameraarrayList.get(i).setDisable(true);
                    }
                }
            }
        }

        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).isDisable()) {
                roomListString.add(roomList.get(i).getRoomId());
            }
        }

        for (int i = 0; i < roomListString.size(); i++) {
            if (i == roomListString.size() - 1) {
                selectRoomList = selectRoomList + roomListString.get(i);
            } else {
                selectRoomList = selectRoomList + roomListString.get(i) + ",";
            }
        }

        setRoomAdapter();
    }

    private void setRoomAdapter() {
        if (roomList.size() == 0) {
            txtEmptyRoom.setVisibility(View.VISIBLE);
            recyclerRoom.setVisibility(View.GONE);
        } else {
            txtEmptyRoom.setVisibility(View.GONE);
            recyclerRoom.setVisibility(View.VISIBLE);
        }
        if (cameraarrayList.size() == 0) {
            txtEmptyCamera.setVisibility(View.VISIBLE);
            recyclerCamera.setVisibility(View.GONE);
        } else {
            txtEmptyCamera.setVisibility(View.GONE);
            recyclerCamera.setVisibility(View.VISIBLE);
        }
        if (roomListAdapter == null) {
            LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
            recyclerRoom.setLayoutManager(gridLayoutManager);
            roomListAdapter = new RoomListAdapter(this, roomList);
            recyclerRoom.setAdapter(roomListAdapter);
        }
        roomListAdapter.notifyDataSetChanged();

        if (cameraListAdapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recyclerCamera.setLayoutManager(gridLayoutManager);
            cameraListAdapter = new CameraListAdapter(this, cameraarrayList);
            recyclerCamera.setAdapter(cameraListAdapter);
        }
        cameraListAdapter.notifyDataSetChanged();
    }

    private void getRoomList() {

        if (!ActivityHelper.isConnectingToInternet(this)) {
            showToast("" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(this, "Loading...", false);

       /* JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_type", "room");
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.getRoomCameraList;

        ChatApplication.logDisplay("un assign is " + url + " " + jsonObject);

        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    ChatApplication.logDisplay("un assign is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {
                        }.getType();
                        roomList = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optJSONObject("data").optJSONArray("roomList").toString(), type);
//                        cameraarrayList = (ArrayList<CameraVO>) Constants.fromJson(result.optJSONObject("data").optJSONArray("cameradeviceList").toString(), type);

                        for (int i = 0; i < result.optJSONObject("data").optJSONArray("cameradeviceList").length(); i++) {
                            JSONObject object = result.optJSONObject("data").optJSONArray("cameradeviceList").optJSONObject(i);
                            CameraVO cameraVO = new CameraVO();
                            cameraVO.setCamera_id(object.optString("camera_id"));
                            cameraVO.setCamera_name(object.optString("camera_name"));
                            cameraarrayList.add(cameraVO);
                        }
                        setSelectValue();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");


        SpikeBotApi.getInstance().GetRoomList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("un assign is " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        Type type = new TypeToken<ArrayList<UnassignedListRes.Data.RoomList>>() {
                        }.getType();
                        roomList = (ArrayList<UnassignedListRes.Data.RoomList>) Constants.fromJson(result.optJSONObject("data").optJSONArray("roomList").toString(), type);

                        for (int i = 0; i < result.optJSONObject("data").optJSONArray("cameradeviceList").length(); i++) {
                            JSONObject object = result.optJSONObject("data").optJSONArray("cameradeviceList").optJSONObject(i);
                            CameraVO cameraVO = new CameraVO();
                            cameraVO.setCamera_id(object.optString("camera_id"));
                            cameraVO.setCamera_name(object.optString("camera_name"));
                            cameraarrayList.add(cameraVO);
                        }
                        setSelectValue();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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


    @SuppressLint("NewApi")
    public void addUserChild() {

        //admin=1
        //admin child =-1
        //child = 0

     /*   String url = "";
        if (modeType.equalsIgnoreCase("update")) {
            url = ChatApplication.url + Constants.updateChildUser;
        } else {
            url = ChatApplication.url + Constants.AddChildUser;
        }*/

        /*JSONObject jsonObject = new JSONObject();*/

        int selectedRadioButtonID = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonID == R.id.radioBtnAdmin) {
            isChildType = -1;
        } else {
            isChildType = 0;
        }

        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).isDisable()) {
                roomListString.add(roomList.get(i).getRoomId());
            }
        }

        ArrayList<String> cameraList = new ArrayList<>();

        for (int i = 0; i < cameraarrayList.size(); i++) {
            if (cameraarrayList.get(i).isDisable()) {
                cameraList.add(cameraarrayList.get(i).getCamera_id());
            }
        }


        if (roomListString.size() == 0 && cameraarrayList.size() == 0) {
            ChatApplication.showToast(this, "Please select at least one room or camera");
            return;
        }

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);


            /*if (modeType.equalsIgnoreCase("update")) {
                jsonObject.put("child_user_id", user.getUser_id());
                jsonObject.put("roomList", jsonArray3);
                jsonObject.put("cameraList", jsonArray4);

            } else {
                jsonObject.put("user_name", edtUsername.getText().toString());
                jsonObject.put("display_name", edDisplayName.getText().toString());
                jsonObject.put("roomList", jsonArray3);
                jsonObject.put("cameraList", jsonArray4);
            }
            if (!TextUtils.isEmpty(strPassword) && strPassword.length() > 0) {
                jsonObject.put("user_password", "" + strPassword);
            }
            jsonObject.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
*/


//        ChatApplication.logDisplay("json is " + jsonObject.toString());
       /* new GetJsonTask2(this, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());

                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        setdefult();
                        ActivityHelper.hideKeyboard(UserChildActivity.this);
                        ChatApplication.showToast(UserChildActivity.this, "" + message);

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        UserChildActivity.this.finish();
                    } else {
                        ChatApplication.showToast(UserChildActivity.this, "" + message);
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


        JSONArray jsonArray3 = new JSONArray(roomListString);
        JSONArray jsonArray4 = new JSONArray(cameraList);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");



        SpikeBotApi.getInstance().AddUserChild(modeType, user.getUser_id(), jsonArray3, jsonArray4,
                edtUsername.getText().toString(), edDisplayName.getText().toString(), strPassword,new DataResponseListener() {
                    @Override
                    public void onData_SuccessfulResponse(String stringResponse) {

                        ActivityHelper.dismissProgressDialog();

                        try {

                            JSONObject result = new JSONObject(stringResponse);

                            ChatApplication.logDisplay("getDeviceList onSuccess " + result.toString());
                            int code = result.getInt("code");
                            String message = result.getString("message");
                            if (code == 200) {
                                setdefult();
                                ActivityHelper.hideKeyboard(UserChildActivity.this);
                                ChatApplication.showToast(UserChildActivity.this, "" + message);
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                UserChildActivity.this.finish();
                            } else {
                                ChatApplication.showToast(UserChildActivity.this, "" + message);
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

    private void setdefult() {
        edDisplayName.setText("");
        edtUsername.setText("");
        et_new_password.setText("");
        et_new_password_confirm.setText("");
        radioBtnChild.setChecked(true);
        selectRoomList = "";
        strRoomList = "";
        roomListString.clear();
        spinnerRoomList.setText("");
        spinnerCameraList.setText("");
        edDisplayName.requestFocus();
    }

    public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.SensorViewHolder> {

        ArrayList<UnassignedListRes.Data.RoomList> arrayListLog = new ArrayList<>();
        private Context mContext;

        public RoomListAdapter(Context context, ArrayList<UnassignedListRes.Data.RoomList> roomListString) {
            this.mContext = context;
            this.arrayListLog = roomListString;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_roomlist_new, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            if (arrayListLog.get(position).isDisable()) {
                holder.iv_icon_select.setVisibility(View.VISIBLE);
            } else {
                holder.iv_icon_select.setVisibility(View.GONE);
            }

            holder.text_item.setText(arrayListLog.get(position).getRoomName());
            holder.iv_icon.setId(position);
            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickFlag = true;
                    if (arrayListLog.get(v.getId()).isDisable()) {
                        arrayListLog.get(v.getId()).setDisable(false);
                    } else {
                        arrayListLog.get(v.getId()).setDisable(true);
                    }
                    notifyDataSetChanged();
                }
            });


            holder.text_item.setId(position);
            holder.text_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickFlag = true;
                    if (arrayListLog.get(v.getId()).isDisable()) {
                        arrayListLog.get(v.getId()).setDisable(false);
                    } else {
                        arrayListLog.get(v.getId()).setDisable(true);
                    }
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayListLog.size();
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public TextView text_item;
            public ImageView iv_icon_select, iv_icon;

            public SensorViewHolder(View view) {
                super(view);
                text_item = view.findViewById(R.id.text_item);
                iv_icon_select = view.findViewById(R.id.iv_icon_select);
                iv_icon = view.findViewById(R.id.iv_icon);
            }
        }
    }


    public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.SensorViewHolder> {

        ArrayList<CameraVO> arrayListLog = new ArrayList<>();
        private Context mContext;

        public CameraListAdapter(Context context, ArrayList<CameraVO> roomListString) {
            this.mContext = context;
            this.arrayListLog = roomListString;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_roomlist, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            holder.iv_icon.setImageResource(Common.getIcon(0, "camera"));

            if (arrayListLog.get(position).isDisable()) {
                holder.iv_icon_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.switch_select_));
                holder.iv_icon_select.setVisibility(View.VISIBLE);
            } else {
                holder.iv_icon_select.setVisibility(View.GONE);
            }

            holder.text_item.setText(arrayListLog.get(position).getCamera_name());
            holder.iv_icon.setId(position);
            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickFlag = true;
                    if (arrayListLog.get(v.getId()).isDisable()) {
                        arrayListLog.get(v.getId()).setDisable(false);
                    } else {
                        arrayListLog.get(v.getId()).setDisable(true);
                    }
                    notifyDataSetChanged();
                }
            });

        }


        @Override
        public int getItemCount() {
            return arrayListLog.size();
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public TextView text_item;
            public ImageView iv_icon, iv_icon_select;

            public SensorViewHolder(View view) {
                super(view);
                text_item = view.findViewById(R.id.text_item);
                iv_icon_select = view.findViewById(R.id.iv_icon_select);
                iv_icon = view.findViewById(R.id.iv_icon);
            }
        }
    }

}
