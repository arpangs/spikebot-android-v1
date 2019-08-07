package com.spike.bot.activity.ir.blaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.GetJsonTaskRemote;
import com.kp.core.ICallBack;
import com.kp.core.ICallBack2;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.MoodRemoteAdapter;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.MoodEvent;
import com.spike.bot.model.AddRemoteReq;
import com.spike.bot.model.IRBlasterAddRes;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.model.RemoteMoodModel;
import com.spike.bot.model.SendRemoteCommandReq;
import com.spike.bot.model.SendRemoteCommandRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 31/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterRemote extends AppCompatActivity implements View.OnClickListener,MoodEvent {

    private LinearLayout mPowerButton;
    private TextView mTemp,mImageAutoText;
    private Button mBrandBottom, mTempMinus,mTempPlus;
    private ImageView mImageAuto,mToolbarBack,imgRemoteStatus, mImageEdit,mImageDelete;
    private TextView mRemoteName,txtRemoteState,txtAcState;

    private String mRemoteId,moodName="",mRoomDeviceId;
    private boolean isPowerOn = false;
    private int mSpeedCurrentPos,isRemoteActive,tempMinus,tempPlus,tempCurrent;

    private RemoteDetailsRes mRemoteList;
    private RemoteDetailsRes.Data.RemoteCommandList mRemoteCommandList;
    private RemoteDetailsRes.Data.RemoteCurrentStatusDetails mRemoteCurrentStatusList;

    private List<String> speedList;
    private String mIrBlasterId,mIrBlasterModuleId;
    public RecyclerView recyclerMode;
    public MoodRemoteAdapter moodRemoteAdapter;
    private ArrayList<RemoteMoodModel> arrayListMood=new ArrayList<>();

    private static String TURN_ON = "TURN ON",TURN_OFF = "TURN OFF";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_blaster_remote);

        syncIntent();
        bindView();
        //initView();

        getRemoteInfo();
    }

    private void syncIntent(){
        mRemoteId = getIntent().getStringExtra("REMOTE_ID");
        isRemoteActive = getIntent().getIntExtra("REMOTE_IS_ACTIVE",0);
        mIrBlasterId = getIntent().getStringExtra("IR_BLASTER_ID");
        mRoomDeviceId = getIntent().getStringExtra("ROOM_DEVICE_ID");
       // mIrBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_MODULE_ID");

        //irBlasterList = (IRBlasterInfoRes.Data.IrBlasterList.RemoteList) getIntent().getSerializableExtra("IR_REMOTE_STATUS");
      //  irBlasterCommandList = irBlasterList.getRemoteCommandList().get(0);
      //  irBlasterCurrentStatusList = irBlasterList.getRemoteCurrentStatusDetails();


    }

    private void bindView(){

        mImageEdit = (ImageView) findViewById(R.id.action_edit);
        mImageDelete = (ImageView) findViewById(R.id.action_delete);
        mBrandBottom = (Button) findViewById(R.id.brand_name_bottom);
        mToolbarBack  = (ImageView) findViewById(R.id.remote_toolbar_back);
        mRemoteName   = (TextView) findViewById(R.id.remote_name);
        txtAcState   = (TextView) findViewById(R.id.txtAcState);
        mTemp = (TextView) findViewById(R.id.remote_temp);
        mImageAuto = (ImageView) findViewById(R.id.remote_speed_img);
        imgRemoteStatus = (ImageView) findViewById(R.id.imgRemoteStatus);
        mImageAutoText = (TextView) findViewById(R.id.remote_speed_text);
        txtRemoteState = (TextView) findViewById(R.id.txtRemoteState);
        mPowerButton = (LinearLayout) findViewById(R.id.remote_power_button);
        mTempMinus = (Button)findViewById(R.id.remote_temp_minus);
        mTempPlus = (Button) findViewById(R.id.remote_temp_plus);
        recyclerMode = (RecyclerView) findViewById(R.id.recyclerMode);

        mToolbarBack.setOnClickListener(this);
        mPowerButton.setOnClickListener(this);
        mTempMinus.setOnClickListener(this);
        mTempPlus.setOnClickListener(this);

        mImageEdit.setOnClickListener(this);
        mImageDelete.setOnClickListener(this);
    }

    private void setMoodAdapter() {
        arrayListMood.clear();
        for(int i=0; i<mRemoteCommandList.getMODE().size(); i++){
            RemoteMoodModel remoteMoodModel=new RemoteMoodModel();
            remoteMoodModel.setMoodName(mRemoteCommandList.getMODE().get(i));
            if(mRemoteList.getData().getRemoteCurrentStatusDetails().getMode().equalsIgnoreCase(mRemoteCommandList.getMODE().get(i))){
                remoteMoodModel.setSelect(true);
            }else {
                remoteMoodModel.setSelect(false);
            }
            arrayListMood.add(remoteMoodModel);
        }
        recyclerMode.setLayoutManager(new GridLayoutManager(this,4));
        moodRemoteAdapter=new MoodRemoteAdapter(IRBlasterRemote.this,arrayListMood,this);
        recyclerMode.setAdapter(moodRemoteAdapter);
    }

    private void initView(){
        setMoodAdapter();
        mTemp.setText("" + mRemoteCurrentStatusList.getTemperature()+Common.getC());
        txtAcState.setText("Ac state : " + mRemoteCurrentStatusList.getPower());
        mImageAutoText.setText(String.format("Speed %s", mRemoteCurrentStatusList.getMode()));
        moodName= mRemoteCurrentStatusList.getMode();
        setPowerOnOff(mRemoteCurrentStatusList.getPower());
        mRemoteName.setText(mRemoteCurrentStatusList.getRemoteName());
        mBrandBottom.setText(mRemoteCurrentStatusList.getBrand_name()+"{"+mRemoteCurrentStatusList.getModel_number()+"}");

        speedList = mRemoteCommandList.getMODE();
        tempMinus = mRemoteCommandList.getTEMPERATURE().get(0);
        tempPlus  = mRemoteCommandList.getTEMPERATURE().get(1);
        tempCurrent = mRemoteCurrentStatusList.getTemperature();
        mSpeedCurrentPos = getCurrentPos(mRemoteCurrentStatusList.getMode());
    }

    private void getRemoteInfo(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ChatApplication.url + Constants.GET_REMOTE_INFO + "/"+mRemoteId;
        //  String url = ChatApplication.url + Constants.GET_REMOTE_INFO + "/"+mIRBlasterId;
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        mRemoteList = Common.jsonToPojo(result.toString(),RemoteDetailsRes.class);

                        mRemoteCommandList = mRemoteList.getData().getRemoteCommandList().get(0);
                        mRemoteCurrentStatusList = mRemoteList.getData().getRemoteCurrentStatusDetails();

                        ChatApplication.logDisplay("Res : " + mRemoteCommandList.getPOWER().size());

                        initView();

                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int getCurrentPos(String speed){
        int cPos = mSpeedCurrentPos;
        for (int i = 0; i < speedList.size(); i++) {
            String speedText = speedList.get(i);
            if (speedText.contains(speed)) {
                setSpeedText(speedText);
                cPos = i;
            }
        }
        return  cPos;
    }

    private void setSpeedText(String speedResult){

        if(speedResult.equalsIgnoreCase("AUTO")){
            mImageAuto.setImageResource(R.drawable.auto_new);
            setNormalText("AUTO");
        }else if(speedResult.equalsIgnoreCase("LOW")){
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity_low);
            setNormalText("LOW");
        }else if(speedResult.equalsIgnoreCase("MEDIUM")){
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity_middle);
            setNormalText("MEDIUM");
        }else if(speedResult.equalsIgnoreCase("HIGH")){
            mImageAuto.setImageResource(R.drawable.ic_conditionor_windcapacity);
            setNormalText("HIGH");
        }
    }
    private void setNormalText(String normalText){
        mImageAutoText.setText("Speed "+normalText);
    }

    private void setPowerOnOff(String powerText){
        isPowerOn = powerText.contains("ON") ||  powerText.contains(TURN_ON) ;
        txtRemoteState.setText(isPowerOn ? TURN_OFF : TURN_ON);
        txtAcState.setText(isPowerOn ? "Ac state : ON" : "Ac state : OFF");
        txtRemoteState.setTextColor(isPowerOn ? getResources().getColor(R.color.automation_red) : getResources().getColor(R.color.username4) );
        mPowerButton.setBackground(isPowerOn ?getResources().getDrawable(R.drawable.drawable_turn_on_btn) :getResources().getDrawable(R.drawable.drawable_turn_off_btn));
        imgRemoteStatus.setImageDrawable(isPowerOn ?getResources().getDrawable(R.drawable.power_red) :getResources().getDrawable(R.drawable.power_green));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.remote_power_button:
//                if(isRemoteActive == 1){
//                    if(isPowerOn) //TODO code here : if power button caption text is empty or nulll then user can't able to perform any action
//                        setPowerOnOff(TURN_OFF);
//                    else
//                        setPowerOnOff(TURN_ON);
                    sendRemoteCommand();
//                }else{
//                    Common.showToast("Remote is Not Active");
//                }
                break;

            case R.id.remote_temp_minus:
                ChatApplication.logDisplay("isOn : " + isPowerOn);
                if(isRemoteActive == 1 && !isPowerOn){

                }else{
                    if(isRemoteActive == 1){
                        if(tempCurrent != tempMinus){
                            tempCurrent --;
                        }
                        if(tempCurrent == tempMinus){
                            tempCurrent = tempMinus;
                        }
                      //  setTempText(tempCurrent);
                        sendRemoteCommand();
                    }else{
                        Common.showToast("Remote is Not Active");
                    }
                }

                break;
            case R.id.remote_temp_plus:
                ChatApplication.logDisplay("isOn : " + isPowerOn);
                if(isRemoteActive == 1 && !isPowerOn){

                }else{
                    if(isRemoteActive == 1){
                        if(tempCurrent != tempPlus){
                            tempCurrent ++;
                        }
                        if(tempCurrent == tempPlus){
                            tempCurrent = tempPlus;
                        }
                       // setTempText(tempCurrent);
                        sendRemoteCommand();
                    }else{
                        Common.showToast("Remote is Not Active");
                    }
                }

                break;
            case R.id.remote_toolbar_back:
                onBackPressed();
                break;
          /*  case R.id.remote_action:
                openAddPopup(mRemoteActionOption);
                break;*/
            case R.id.action_edit:
                showRemoteSaveDialog(true);
                break;
            case R.id.action_delete:
                ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to delete remote?",new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteRemote();
                    }
                    @Override
                    public void onConfirmDialogNoClick() {
                    }

                });
                newFragment.show(getFragmentManager(), "dialog");
                break;
        }
    }

    /**
     * Edit AC Remote
     */
    private Spinner mSpinnerBlaster,mSpinnerMode;
    private TextView remote_room_txt;
    private EditText mRemoteDefaultTemp;
    private TextView mTxtBlasterName;
    private EditText mEdtRemoteName;

    private Dialog mDialog;
    private void showRemoteSaveDialog(final boolean isEdit){
        if(mDialog == null){
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_save_remote_edit);
        }

        mEdtRemoteName = mDialog.findViewById(R.id.edt_remote_name);
        mEdtRemoteName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        TextView mDialogTitle = mDialog.findViewById(R.id.tv_title);
        mDialogTitle.setText("Remote Details");

        ImageView iv_close = (ImageView) mDialog.findViewById(R.id.iv_close);
        Button mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
        Button mBtnSave = mDialog.findViewById(R.id.btn_save);

        mSpinnerBlaster = (Spinner) mDialog.findViewById(R.id.remote_blaster_spinner);
        remote_room_txt = (TextView) mDialog.findViewById(R.id.remote_room_txt);
        mSpinnerMode = (Spinner) mDialog.findViewById(R.id.remote_mode_spinner);

        mRemoteDefaultTemp = (EditText) mDialog.findViewById(R.id.edt_remote_tmp);
        mTxtBlasterName = (TextView) mDialog.findViewById(R.id.txt_blastername);

        try{
            mEdtRemoteName.setText(""+mRemoteCurrentStatusList.getRemoteName());
            mEdtRemoteName.setSelection(mEdtRemoteName.getText().toString().length());
            mRemoteDefaultTemp.setText(""+mRemoteCurrentStatusList.getScheduleTemperature());
        }catch (Exception ex){ ex.printStackTrace(); }

        getIRBlasterList(isEdit);
      //  remote_room_txt.setText(""+mRemoteCurrentStatusList.getRoomName());
       // mTxtBlasterName.setText(""+mRemoteCurrentStatusList.getIrBlasterName());

        String[] moodList = new String[]{"AUTO","LOW","MEDIUM","HIGH"};
        final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, moodList);
        mSpinnerMode.setAdapter(roomAdapter1);

        for (int i = 0; i < moodList.length; i++) {
            String str = moodList[i];
            if (mRemoteCurrentStatusList.getScheduleMode()!=null && str.equalsIgnoreCase(mRemoteCurrentStatusList.getScheduleMode())) {
                mSpinnerMode.setSelection(i);
            }
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(mEdtRemoteName.getText().toString().trim())){
                    mEdtRemoteName.requestFocus();
                    mEdtRemoteName.setError("Enter Remote name");
                    return;
                }

                if(TextUtils.isEmpty(mRemoteDefaultTemp.getText().toString().trim())){
                    mRemoteDefaultTemp.requestFocus();
                    mRemoteDefaultTemp.setError("Enter Remote Default Temp");
                    return;
                }

                int mDefaultTemp = Integer.parseInt(mRemoteDefaultTemp.getText().toString());
                if(mDefaultTemp >=17 && mDefaultTemp <=30){

                }else{
                    mRemoteDefaultTemp.requestFocus();
                    mRemoteDefaultTemp.setError("Default Temp range between 17 to 30");
                    return;
                }

                saveRemote(mEdtRemoteName.getText().toString().trim(),isEdit);

            }
        });

        if(!mDialog.isShowing()){
            mDialog.show();
        }
    }
    private void hideSaveDialog(){
        if(mEdtRemoteName!=null){
            hideSoftKeyboard(mEdtRemoteName);
        }
        if(mRemoteDefaultTemp!=null){
            hideSoftKeyboard(mRemoteDefaultTemp);
        }
        if(mDialog != null){
            mDialog.dismiss();
        }
    }
    public void hideSoftKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    /**
     * save Remote
     * @param remoteName remotename
     * @param isEdit
     */
    private void saveRemote(String remoteName, boolean isEdit){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        IRBlasterAddRes.Data.IrList ir = (IRBlasterAddRes.Data.IrList) mSpinnerBlaster.getSelectedItem();

        if(!isEdit){
            if(ir.getIrBlasterName().equalsIgnoreCase("Select Blaster")){
                Common.showToast("Select Blaster");
                return;
            }
        }

        AddRemoteReq addRemoteReq = new AddRemoteReq(Common.getPrefValue(this, Constants.USER_ID),mRemoteCurrentStatusList.getRemoteId(),remoteName,
                ir.getRoomId(),ir.getIrBlasterId(),mSpinnerMode.getSelectedItem().toString(),
                mRemoteDefaultTemp.getText().toString().trim(),
                APIConst.PHONE_ID_KEY,APIConst.PHONE_TYPE_VALUE,mSpinnerBlaster.getSelectedItem().toString());
        addRemoteReq.setUpdate_type(1); //if remote update command fire in mood then pass the update_type = 1
        addRemoteReq.setRoomDeviceId(mRoomDeviceId);

        Gson gson = new Gson();
        String mStrOnOffReq = gson.toJson(addRemoteReq);

        ChatApplication.logDisplay("Request : " + mStrOnOffReq);

        String url = ChatApplication.url + Constants.UPDATE_REMOTE_DETAILS;
        new GetJsonTask(this, url, "POST", mStrOnOffReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    hideSaveDialog();
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
//                        ChatApplication.isMoodFragmentNeedResume = true;
//                        isRemoteActive = 1;
                        getRemoteInfo();
                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
            }
        }).execute();
    }



    private List<IRBlasterAddRes.Data.IrList> irList;
    List<IRBlasterAddRes.Data.RoomList> roomLists;

    /**
     * get IR Blaster list
     * @param isEdit
     */
    private void getIRBlasterList(final boolean isEdit){

        if(!ActivityHelper.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        irList = new ArrayList<>();
        irList.clear();

        String URL = ChatApplication.url + Constants.GET_IR_BLASTER_LIST;

        ActivityHelper.showProgressDialog(this,"Please wait.",false);

        new GetJsonTask(this, URL, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                android.util.Log.d("getIRBlasterList","result : " + result.toString());

                IRBlasterAddRes irBlasterAddRes = Common.jsonToPojo(result.toString(),IRBlasterAddRes.class);
                if(irBlasterAddRes.getCode() == 200){
                    irList  = irBlasterAddRes.getData().getIrList();
                    roomLists = irBlasterAddRes.getData().getRoomList();

                    if(!isEdit){
                        IRBlasterAddRes.Data.IrList ir0 = new IRBlasterAddRes.Data.IrList();
                        ir0.setIrBlasterName("Select Blaster");
                        ir0.setIrBlasterId("-1");
                        irList.add(0,ir0);
                    }else {
                       // remote_room_txt.setText(""+mRemoteCurrentStatusList.getRoomName());
                        remote_room_txt.setText(""+irList.get(0).getRoomName());
                    }

                    final ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, irList);
                    mSpinnerBlaster.setAdapter(roomAdapter1);

                    for (int i = 0; i < irList.size(); i++) {
                        IRBlasterAddRes.Data.IrList ir = irList.get(i);
                        if (ir.getIrBlasterId().equalsIgnoreCase(mRemoteCurrentStatusList.getIrBlasterId())) {
                            mSpinnerBlaster.setSelection(i);
                        }
                    }

                    if(!mSpinnerBlaster.getSelectedItem().toString().equalsIgnoreCase("Select Blaster")){
                      //  remote_room_txt.setText(""+mSpinnerBlaster.getSelectedItem().toString());
                    }

                    mSpinnerBlaster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                            if(position!=0){
                            //    IRBlasterAddRes.Data.IrList irList = (IRBlasterAddRes.Data.IrList) mSpinnerBlaster.getSelectedItem();
                                remote_room_txt.setText(""+irList.get(position).getRoomName());
//                            }else{
                               // remote_room_txt.setText("");
//                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }else{
                  //  Common.showToast(irBlasterAddRes.getMessage());
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                Toast.makeText(ChatApplication.getInstance(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();

        /**
         * if there are no any ir blaster then display error message
         */
        if(irList.size() == 0){
            IRBlasterAddRes.Data.IrList ir0 = new IRBlasterAddRes.Data.IrList();
            ir0.setIrBlasterName("No IR Blaster Found");
            ir0.setIrBlasterId("-1");
            irList.add(0,ir0);
            ArrayAdapter roomAdapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spinner, irList);
            mSpinnerBlaster.setAdapter(roomAdapter1);
        }

    }


    public void openAddPopup(final View v) {//,final ICallBackAction actionCallBack
        PopupMenu popup = new PopupMenu(this, v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_room_edit_option_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        //showRemoteSaveDialog();
                        break;
                    case R.id.action_delete:
                        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure ?",new ConfirmDialog.IDialogCallback() {
                            @Override
                            public void onConfirmDialogYesClick() {
                                deleteRemote();
                            }
                            @Override
                            public void onConfirmDialogNoClick() {
                            }

                        });
                        newFragment.show(getFragmentManager(), "dialog");

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void deleteRemote(){
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("remote_id",mRemoteCurrentStatusList.getRemoteId());
            object.put(APIConst.PHONE_ID_KEY,APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.DELETE_REMOTE;

        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                android.util.Log.d("RequestSch","onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){
                        ChatApplication.isMoodFragmentNeedResume = true;
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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


    private void sendRemoteCommand(){

        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        SendRemoteCommandReq sendRemoteCommandReq = new SendRemoteCommandReq();

        if(mRemoteCurrentStatusList!=null && mRemoteCurrentStatusList.getRemoteId()!=null){
            sendRemoteCommandReq.setRemoteid(mRemoteCurrentStatusList.getRemoteId());
        }


       // sendRemoteCommandReq.setCodesetid(String.valueOf(irBlasterCurrentStatusList.getCodesetId()));
     //   sendRemoteCommandReq.setIrblasterid(mIrBlasterId);
     //   sendRemoteCommandReq.setIrblasterModuleid(mIrBlasterModuleId);
        ChatApplication.logDisplay("Found : " + mRoomDeviceId);

        sendRemoteCommandReq.setPower(isPowerOn ? "ON" : "OFF");
        sendRemoteCommandReq.setSpeed(moodName);
        sendRemoteCommandReq.setTemperature(tempCurrent);
        sendRemoteCommandReq.setRoomDeviceId(mRoomDeviceId);
        sendRemoteCommandReq.setPhoneId(APIConst.PHONE_ID_VALUE);
        sendRemoteCommandReq.setPhoneType(APIConst.PHONE_TYPE_VALUE);

        Gson gson = new Gson();
        String mRemoteCommandReq = gson.toJson(sendRemoteCommandReq);

        ChatApplication.logDisplay("" + mRemoteCommandReq);

        String url = ChatApplication.url + Constants.SEND_REMOTE_COMMAND;
        new GetJsonTaskRemote(this, url, "POST", mRemoteCommandReq, new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("onSuccess result : " + result.toString());
                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code == 200){

                        if(isPowerOn) //TODO code here : if power button caption text is empty or nulll then user can't able to perform any action
                        setPowerOnOff(TURN_OFF);
                    else
                        setPowerOnOff(TURN_ON);

                        ChatApplication.isMoodFragmentNeedResume = true;
                        SendRemoteCommandRes tmpIrBlasterCurrentStatusList = Common.jsonToPojo(result.toString(),
                                SendRemoteCommandRes.class);

                        updateRemoteUI(tmpIrBlasterCurrentStatusList.getData().getRemoteCurrentStatusDetails());
                        if(isPowerOn){
                            isRemoteActive=1;
                        }else {
                            isRemoteActive=0;
                        }

                    }else{
                        ChatApplication.showToast(IRBlasterRemote.this,mRemoteName.getText()+" "+getString(R.string.ir_error));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(IRBlasterRemote.this,mRemoteName.getText()+" "+getString(R.string.ir_error));
            }
        }).execute();
    }

    /**
     *
     * @param tmpIrBlasterCurrentStatusList
     */
    private void updateRemoteUI(SendRemoteCommandRes.Data.RemoteCurrentStatusDetails tmpIrBlasterCurrentStatusList){
        mTemp.setText("" + tmpIrBlasterCurrentStatusList.getTemperature()+Common.getC());
        mImageAutoText.setText(""+String.format("Speed %s", tmpIrBlasterCurrentStatusList.getSpeed()));

        setSpeedText(""+tmpIrBlasterCurrentStatusList.getSpeed());
        mRemoteName.setText(""+tmpIrBlasterCurrentStatusList.getRemoteName());

        ChatApplication.logDisplay("Text : " + tmpIrBlasterCurrentStatusList.getTemperature());

    }

    public void remoteSpeed(String modeName){
        if(isRemoteActive == 1 && !isPowerOn){
            Common.showToast("Remote is Not Active");
        }else{
            if(isRemoteActive == 1){
//                if(mSpeedCurrentPos != speedList.size()){
//                    mSpeedCurrentPos ++;
//                }
//                if(mSpeedCurrentPos == speedList.size()){
//                    mSpeedCurrentPos =  0;
//                }
                setSpeedText(modeName);
                sendRemoteCommand();
            }else{
                Common.showToast("Remote is Not Active");
            }
        }
    }

    @Override
    public void selectMood(RemoteMoodModel remoteMoodModel) {
        moodName=remoteMoodModel.getMoodName();
        remoteSpeed(remoteMoodModel.getMoodName());
        moodRemoteAdapter.notifyDataSetChanged();
    }
}
