package com.spike.bot.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.SmartRemoteModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SmartRemoteAdapter extends RecyclerView.Adapter<SmartRemoteAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Activity mContext;
    ArrayList<SmartRemoteModel> arrayListLog=new ArrayList<>();
    public SelectCamera selectCamera;


    public SmartRemoteAdapter(Activity context, ArrayList<SmartRemoteModel> arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_smart_remote,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtNameRemote.setText(arrayListLog.get(position).getSmart_remote_name());

        holder.imgEditRemote.setId(position);
        holder.imgEditRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRemoteshow(v.getId());
            }
        });


        holder.imgDeleteRemote.setId(position);
        holder.imgDeleteRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?" ,new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteRemote(arrayListLog.get(v.getId()).getSmart_remote_module_id(),v.getId(),arrayListLog.get(v.getId()).getSmart_remote_name());

                    }
                    @Override
                    public void onConfirmDialogNoClick() {

                    }
                });
                newFragment.show(mContext.getFragmentManager(), "dialog");
            }
        });

        holder.ll_sensor_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext,SubSmartRemoteActivity.class);
//                mContext.startActivity(intent);

            }
        });
    }


    private void dialogRemoteshow(final int b) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_remote_key);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        final AppCompatEditText editKeyValue =  dialog.findViewById(R.id.editKeyValue);
        Button btnSubmit =  dialog.findViewById(R.id.btnSubmit);
        Button btnCancel =  dialog.findViewById(R.id.btnCancel);
        TextView txtTitalMood =  dialog.findViewById(R.id.txtTitalMood);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);

        btnCancel.setVisibility(View.GONE);

        txtTitalMood.setText("Please enter Smart Remote name");
        editKeyValue.setFilters(new InputFilter[]{ChatApplication.filter,new InputFilter.LengthFilter(30)});

        editKeyValue.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(mContext);
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editKeyValue.getText().toString().length()==0){
                    ChatApplication.showToast(mContext,"Please enter smart remote name");
                }else {
                    ChatApplication.keyBoardHideForce(mContext);
                    saveSensor(dialog,arrayListLog.get(b),editKeyValue.getText().toString(),b);
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void saveSensor(final Dialog dialog, SmartRemoteModel smartRemoteModel, final String name, final int position){

        if(!ActivityHelper.isConnectingToInternet(mContext)){
            Toast.makeText(mContext.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(mContext,"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("smart_remote_module_id", smartRemoteModel.getSmart_remote_module_id());
            obj.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID) );
            obj.put("smart_remote_name",name);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.addSmartRemote;

        new GetJsonTask(mContext,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        dialog.dismiss();
                        arrayListLog.get(position).setSmart_remote_name(name);
                        notifyDataSetChanged();
                    }else{
                        ChatApplication.showToast(mContext,message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    public void  deleteRemote(String module_id, final int id, String smart_remote_name){

        if(!ActivityHelper.isConnectingToInternet(mContext)){
            Toast.makeText(mContext, R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(mContext,"Please wait...",false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("smart_remote_module_id", module_id);
            obj.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID) );
            obj.put("smart_remote_name",smart_remote_name);
            obj.put("is_update",1);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url =  ChatApplication.url + Constants.deleteSmartRemote;
        new GetJsonTask(mContext,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();

                try {
                    int code = result.getInt("code");
                    if(code==200){
                        arrayListLog.remove(id);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
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

        public TextView txtNameRemote;
        public ImageView imgDeleteRemote,imgEditRemote;
        public LinearLayout ll_sensor_view;

        public SensorViewHolder(View view) {
            super(view);
            txtNameRemote =  itemView.findViewById(R.id.txtNameRemote);
            imgEditRemote =  itemView.findViewById(R.id.imgEditRemote);
            imgDeleteRemote =  itemView.findViewById(R.id.imgDeleteRemote);
            ll_sensor_view =  itemView.findViewById(R.id.ll_sensor_view);
        }
    }
}
