package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.AddCameraAdapter;
import com.spike.bot.adapter.RoomeListAdapter;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.fragments.UserChildListFragment;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.User;
import com.spike.bot.model.UserRoomModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sagar on 4/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserChildActivity extends AppCompatActivity implements View.OnClickListener {

    public android.support.v7.widget.Toolbar toolbar;
    public EditText edtUsername,edDisplayName,et_new_password_confirm,et_new_password;
    public RadioGroup radioGroup;
    public RadioButton radioBtnAdmin, radioBtnChild;
    public TextView spinnerRoomList, spinnerCameraList,txtEmptyRoom,txtEmptyCamera;
    public Button btnSave;
    public RecyclerView recyclerRoom,recyclerCamera;
    public LinearLayout linearChangePassword,ll_password_view_expand,ll_pass_edittext_view;
    public ImageView img_pass_arrow;
    public String selectRoomList="",strRoomList="",modeType="";
    public int isChildType=0;
    public boolean isClickFlag=false;

    public User user;
    private ArrayList<RoomVO> roomList = new ArrayList<>();
    private ArrayList<String> roomListString = new ArrayList<>();
    private ArrayList<String> roomListNameString = new ArrayList<>();
    public ArrayList<CameraVO> cameraarrayList=new ArrayList<>();

    RoomListAdapter roomListAdapter;
    CameraListAdapter cameraListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_users_child);

        modeType=getIntent().getStringExtra("modeType");

        if(modeType.equalsIgnoreCase("update")){
            user=(User)getIntent().getSerializableExtra("arraylist");
        }

        setUiId();

    }

    private void setUiId() {
        roomList.clear();
        cameraarrayList.clear();
        roomList= UserChildListFragment.roomList;
        cameraarrayList= UserChildListFragment.cameraList;
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
      //  linearPassword = findViewById(R.id.linearPassword);
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password_confirm = findViewById(R.id.et_new_password_confirm);
        btnSave.setVisibility(View.GONE);
        btnSave.setOnClickListener(this);
        spinnerRoomList.setOnClickListener(this);
        ll_password_view_expand.setOnClickListener(this);

        if(modeType.equalsIgnoreCase("update")){
//            linearChangePassword.setVisibility(View.VISIBLE);
            toolbar.setTitle("Update Child User");
            edDisplayName.setText(user.getFirstname());
            edtUsername.setText(user.getFirstname());
      //      edtPassword.setText(user.getPassword());

//            if(user.get().equalsIgnoreCase("0")){
//                radioBtnChild.setChecked(true);
//            }else {
//                radioBtnAdmin.setChecked(true);
//            }
            edDisplayName.setSelection(edDisplayName.getText().length());

            for(int i=0; i<roomList.size(); i++){
                for (int j=0; j<user.getRoomList().size(); j++){
                    if(user.getRoomList().get(j).equalsIgnoreCase(roomList.get(i).getRoomId())){
                        roomList.get(i).setDisable(true);
                    }
                }
            }


            for(int i=0; i<cameraarrayList.size(); i++){
                for (int j=0; j<user.getCameralist().size(); j++){
                    if(user.getCameralist().get(j).equalsIgnoreCase(cameraarrayList.get(i).getCamera_id())){
                        cameraarrayList.get(i).setDisable(true);
                    }
                }
            }

            edtUsername.setClickable(false);
            edtUsername.setEnabled(false);
            edDisplayName.setClickable(false);
            edDisplayName.setEnabled(false);

//            UserRoomListActivity.removeDuplicates(roomVO.getDeviceList());
//            UserRoomListActivity.strDeviceId=UserRoomListActivity.countRoomLIst(UserRoomListActivity.strDeviceId);

//            spinnerRoomList.setText("Room : "+UserRoomListActivity.strDeviceId.size()+" & device : "+roomVO.getDeviceList().size());
            setSelectValue();
        }
        setRoomAdapter();

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
                if(et_new_password.getText().toString().length()>0){
                    isClickFlag=true;
                }else {
                    isClickFlag=false;
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

            selectRoomList="";
            strRoomList="";
            roomListString.clear();
            roomListNameString.clear();

            setSelectValue();
            if(modeType.equalsIgnoreCase("update")){
                    if (isClickFlag) {
                        if (et_new_password.getText().toString().length()>0) {
                            if(et_new_password_confirm.getText().toString().length()==0){
                                ChatApplication.showToast(this, "Please enter confrim password");
                            }else if(!et_new_password.getText().toString().equalsIgnoreCase(et_new_password_confirm.getText().toString())){
                                ChatApplication.showToast(this, "Password not match...");
                            }else {
                                addUserChild();
                            }

                        }else {
                            addUserChild();
                        }
                    }else {
                        this.finish();
                    }
            }else {
                if (edDisplayName.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter display name");
                } else if (edtUsername.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter username");
                } else if (et_new_password.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter password");
                } else if (et_new_password_confirm.getText().toString().length() == 0) {
                    ChatApplication.showToast(this, "Please enter confirm password");
                }else if(!et_new_password.getText().toString().equalsIgnoreCase(et_new_password_confirm.getText().toString())){
                    ChatApplication.showToast(this, "Please do not match...");
                }else if (edtUsername.getText().toString().trim().matches(".*([ \t]).*")){
                    edtUsername.requestFocus();
                    edtUsername.setError("Invalid Username");
                    //Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
                } else {
                    addUserChild();
                }
                /*else if (selectRoomList.length() == 0) {
                    ChatApplication.showToast(this, "Please select at least one room");
                }
                * */
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {

        }else if (v == spinnerRoomList) {
      //      dialogRoomeList();
//            ActivityHelper.hideKeyboard(UserChildActivity.this);
//            Intent intent = new Intent(this, UserRoomListActivity.class);
//            intent.putExtra("isMoodAdapter",false);
//            if(modeType.equalsIgnoreCase("update")){
//                intent.putExtra("editMode",true);
//                intent.putExtra("moodVO",roomVO);
//                intent.putExtra("panel_id",roomVO.getPanel_id());
//                intent.putExtra("panel_name",roomVO.getPanel_name());
//                intent.putExtra("isMap",true);
//                intent.putExtra("isMoodAdapter",true);
//            }else {
//                intent.putExtra("editMode",false);
//            }
//
//            startActivityForResult(intent , 1001);
        }else if(v==ll_password_view_expand){
            if(ll_pass_edittext_view.getVisibility() == View.VISIBLE){
                //   ll_pass_edittext_view.setAnimation(slideDownAnimation);
                ll_pass_edittext_view.setVisibility(View.GONE);
                img_pass_arrow.setImageResource(R.drawable.icn_arrow_right);

            }else{
                // ll_pass_edittext_view.setAnimation(slideUpAnimation);
                img_pass_arrow.setImageResource(R.drawable.icn_arrow_down);
                ll_pass_edittext_view.setVisibility(View.VISIBLE);

            }

        }
    }

    private void dialogRoomeList() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_room_list);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RecyclerView recyclerRoomList=dialog.findViewById(R.id.recyclerRoomList);
        Button btnSumbit=dialog.findViewById(R.id.btnSumbit);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerRoomList.setLayoutManager(linearLayoutManager);

//        if(modeType.equalsIgnoreCase("update")){
//
//            for(int i=0; i<roomList.size(); i++){
//                for (int j=0; j<user.getRoomList().size(); j++){
//                    if(user.getRoomList().get(j).equalsIgnoreCase(roomList.get(i).getRoomId())){
//                        roomList.get(i).setDisable(true);
//                    }
//                }
//            }
//        }

        RoomeListAdapter roomeListAdapter =new RoomeListAdapter(this,roomList);
        recyclerRoomList.setAdapter(roomeListAdapter);

        btnSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectRoomList="";
                strRoomList="";
                roomListString.clear();
                roomListNameString.clear();

                setSelectValue();
            }
        });

        dialog.show();
    }

    private void setSelectValue() {
        for(int i=0; i<roomList.size(); i++){
            if(roomList.get(i).isDisable()){
                roomListString.add(roomList.get(i).getRoomId());
                roomListNameString.add(roomList.get(i).getRoomName());
            }
        }

        for(int i=0; i<roomListString.size(); i++){
            if(i==roomListString.size()-1){
                selectRoomList= selectRoomList+roomListString.get(i);
                strRoomList= strRoomList+roomListNameString.get(i);
            }else {
                selectRoomList= selectRoomList+roomListString.get(i)+",";
                strRoomList= strRoomList+roomListNameString.get(i)+" , ";
            }
        }

        setRoomAdapter();
//        if(!TextUtils.isEmpty(strRoomList)){
//            spinnerRoomList.setText(strRoomList);
//        }else {
//            spinnerRoomList.setText("");
//        }
    }

    private void setRoomAdapter() {
        if(roomList.size()==0){
            txtEmptyRoom.setVisibility(View.VISIBLE);
            recyclerRoom.setVisibility(View.GONE);
        }else {
            txtEmptyRoom.setVisibility(View.GONE);
            recyclerRoom.setVisibility(View.VISIBLE);
        }
        if(cameraarrayList.size()==0){
            txtEmptyCamera.setVisibility(View.VISIBLE);
            recyclerCamera.setVisibility(View.GONE);
        }else {
            txtEmptyCamera.setVisibility(View.GONE);
            recyclerCamera.setVisibility(View.VISIBLE);
        }
        if(roomListAdapter==null){
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,4);
            recyclerRoom.setLayoutManager(gridLayoutManager);
            roomListAdapter=new RoomListAdapter(this,roomList);
            recyclerRoom.setAdapter(roomListAdapter);
        }
        roomListAdapter.notifyDataSetChanged();

        if(cameraListAdapter==null){
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,4);
            recyclerCamera.setLayoutManager(gridLayoutManager);
            cameraListAdapter=new CameraListAdapter(this,cameraarrayList);
            recyclerCamera.setAdapter(cameraListAdapter);
        }
        cameraListAdapter.notifyDataSetChanged();


    }

    @SuppressLint("NewApi")
    public void addUserChild() {

        //admin=1
        //admin child =-1
        //child = 0



        String url="";
        if(modeType.equalsIgnoreCase("update")){
            url= ChatApplication.url + Constants.updateChildUser;
        }else {
            url= ChatApplication.url + Constants.AddChildUser;
        }

        JSONObject jsonObject = new JSONObject();

        int selectedRadioButtonID = radioGroup.getCheckedRadioButtonId();

        if(selectedRadioButtonID==R.id.radioBtnAdmin){
            isChildType=-1;
        }else {
            isChildType=0;
        }

        try {

            ArrayList<UserRoomModel> roomListTemp = new ArrayList<>();

            for(int i=0; i<roomListString.size(); i++){
                UserRoomModel userRoomModel=new UserRoomModel();
                userRoomModel.setRoom_id(roomListString.get(i));
                userRoomModel.setRoom_name(roomListNameString.get(i));
                roomListTemp.add(userRoomModel);
            }

            ArrayList<String> cameraId=new ArrayList<>();
            for(int i=0; i<cameraarrayList.size(); i++){
                if(cameraarrayList.get(i).isDisable()){
                    cameraId.add(cameraarrayList.get(i).getCamera_id());
                }
            }

//            String id="";
//            for(int i=0; i<cameraId.size(); i++){
//                if(i==cameraId.size()-1){
//                    id=id+cameraId.get(i);
//                }else {
//                    id=id+cameraId.get(i)+",";
////                }
//            else if (selectRoomList.length() == 0) {
//                ChatApplication.showToast(this, "Please select at least one room");
//            }
//            }

            Gson gson = new Gson();
            String personString = gson.toJson(roomListTemp);
            String toJson = gson.toJson(cameraId);
            String listIdString = gson.toJson(roomListString);

            JSONArray jsonArray=new JSONArray(personString);
            JSONArray jsonArray1=new JSONArray(toJson);
            JSONArray jsonArray2=new JSONArray(listIdString);

            if (cameraId.size()==0 && selectRoomList.length()==0) {
                ChatApplication.showToast(this, "Please select at least one room or camera");
                return;
            }

            ActivityHelper.showProgressDialog(this, "Please Wait...", false);

            if(modeType.equalsIgnoreCase("update")){
                jsonObject.put("child_user_id", user.getUser_id());
                jsonObject.put("roomList", jsonArray2);
                jsonObject.put("cameraList", jsonArray1);
                jsonObject.put("user_password", ""+et_new_password.getText().toString());
            }else {
                jsonObject.put("admin_user_id", Common.getPrefValue(this, Constants.USER_ID));
                jsonObject.put("user_name", edtUsername.getText().toString());
                jsonObject.put("user_password", et_new_password.getText().toString());
                jsonObject.put("display_name", edDisplayName.getText().toString());
                jsonObject.put("roomList", jsonArray);
                jsonObject.put("cameraList", jsonArray1);
                jsonObject.put("admin", isChildType);

            }

            jsonObject.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            jsonObject.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("json is " + jsonObject.toString());
        new GetJsonTask2(this, url, "POST", jsonObject.toString(), new ICallBack2() { //Constants.CHAT_SERVER_URL
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
                        ChatApplication.showToast(UserChildActivity.this,""+message);

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK,returnIntent);
                        UserChildActivity.this.finish();
                    }else {
                        ChatApplication.showToast(UserChildActivity.this,""+message);
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
    protected void onStop() {
        for(int i=0; i<UserChildListFragment.roomList.size(); i++){
            UserChildListFragment.roomList.get(i).setDisable(false);
        }

        for(int i=0; i<UserChildListFragment.cameraList.size(); i++){
            UserChildListFragment.cameraList.get(i).setDisable(false);
        }
        super.onStop();
    }

    private void setdefult() {
        edDisplayName.setText("");
        edtUsername.setText("");
        et_new_password.setText("");
        et_new_password_confirm.setText("");
        radioBtnChild.setChecked(true);
        selectRoomList="";
        strRoomList="";
        roomListString.clear();
        roomListNameString.clear();
        spinnerRoomList.setText("");
        spinnerCameraList.setText("");
        edDisplayName.requestFocus();
    }


    public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.SensorViewHolder>{

        private Context mContext;
        ArrayList<RoomVO> arrayListLog=new ArrayList<>();
        public SelectCamera selectCamera;

        public RoomListAdapter(Context context, ArrayList<RoomVO> roomListString) {
            this.mContext=context;
            this.arrayListLog=roomListString;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_roomlist,parent,false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            if(arrayListLog.get(position).isDisable()){
                holder.iv_icon_select.setVisibility(View.VISIBLE);
            }else {
                holder.iv_icon_select.setVisibility(View.GONE);
            }

            holder.text_item.setText(arrayListLog.get(position).getRoomName());
            holder.iv_icon.setId(position);
            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickFlag=true;
                    if(arrayListLog.get(v.getId()).isDisable()){
                        arrayListLog.get(v.getId()).setDisable(false);
                    }else {
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

        public class SensorViewHolder extends RecyclerView.ViewHolder{

            public TextView text_item;
            public ImageView iv_icon,iv_icon_select;

            public SensorViewHolder(View view) {
                super(view);
                text_item=view.findViewById(R.id.text_item);
                iv_icon_select=view.findViewById(R.id.iv_icon_select);
                iv_icon=view.findViewById(R.id.iv_icon);
            }
        }
    }



    public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.SensorViewHolder>{

        private Context mContext;
        ArrayList<CameraVO> arrayListLog=new ArrayList<>();
        public SelectCamera selectCamera;

        public CameraListAdapter(Context context, ArrayList<CameraVO> roomListString) {
            this.mContext=context;
            this.arrayListLog=roomListString;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_roomlist,parent,false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            holder.iv_icon.setImageResource(Common.getIcon(0,"camera"));

            if(arrayListLog.get(position).isDisable()){
                holder.iv_icon_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.switch_select_));
                holder.iv_icon_select.setVisibility(View.VISIBLE);
            }else {
                holder.iv_icon_select.setVisibility(View.GONE);
            }

            holder.text_item.setText(arrayListLog.get(position).getCamera_name());
            holder.iv_icon.setId(position);
            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickFlag=true;
                    if(arrayListLog.get(v.getId()).isDisable()){
                        arrayListLog.get(v.getId()).setDisable(false);
                    }else {
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

        public class SensorViewHolder extends RecyclerView.ViewHolder{

            public TextView text_item;
            public ImageView iv_icon,iv_icon_select;

            public SensorViewHolder(View view) {
                super(view);
                text_item=view.findViewById(R.id.text_item);
                iv_icon_select=view.findViewById(R.id.iv_icon_select);
                iv_icon=view.findViewById(R.id.iv_icon);
            }
        }
    }

}
