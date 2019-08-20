package com.spike.bot.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.model.NotificationListRes;
import com.spike.bot.model.NotificationSettingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public class TempMultiSensorAlertAdapter extends RecyclerView.Adapter<TempMultiSensorAlertAdapter.SensorViewHolder> {

    private List<NotificationSettingList> onNotificationContextMenu = new ArrayList<>();
    private Context mContext;
    private String multiSensorId="";


    public TempMultiSensorAlertAdapter(Context context, List<NotificationSettingList> arrayListLog1, String multiSensorId) {
        this.mContext = context;
        this.onNotificationContextMenu = arrayListLog1;
        this.multiSensorId = multiSensorId;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_multisensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {


        holder.notification_title.setText(onNotificationContextMenu.get(position).getTitle());

        holder.switch_temp.setChecked((onNotificationContextMenu.get(position).getValue() == 1));


        holder.switch_temp.setOnTouchListener(new OnSwipeTouchListener(mContext){
            @Override
            public void onClick() {
                super.onClick();
                holder.switch_temp.setChecked(holder.switch_temp.isChecked());
                callDialogShow(position,mContext,holder.switch_temp);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switch_temp.setChecked(holder.switch_temp.isChecked());
                callDialogShow(position,mContext,holder.switch_temp);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switch_temp.setChecked(holder.switch_temp.isChecked());
                callDialogShow(position,mContext,holder.switch_temp);
            }

        });
//
//        holder.switch_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//                if (buttonView.isPressed()) {
//
//                    String message="";
//                    if(holder.switch_temp.isChecked()){
//                        message="Are you sure you want to Disable";
//                    }else {
//                        message="Are you sure you want to Enable";
//                    }
//
//
//                    final AlertDialog alertDialog =  new AlertDialog.Builder(mContext).setTitle("Confirm").setMessage(message)
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    onNotificationContextMenu.get(position).setValue(isChecked ? 1 : 0);
//                                    notifyItemChanged(position);
//
//                                    callONOff(position,mContext);
//                                }
//                            }).create();
//                    alertDialog.show();
//
//
//                }
//            }
//        });

        if (position == onNotificationContextMenu.size() - 1) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
//        if(position==0){
//            holder.viewLine.setVisibility(View.GONE);
//        }else {
//            holder.viewLine.setVisibility(View.VISIBLE);
//        }

//        if(!TextUtils.isEmpty(onNotificationContextMenu.get(position).getActivityTime())){
//            Date today = null;//2018-01-12 19:40:07
//            try {
//                today = DateHelper.parseDateSimple(onNotificationContextMenu.get(position).getActivityTime(),DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            String strDateOfTime=DateHelper.getDayString(today);
//            String []strDateOfTimeTemp=strDateOfTime.split(" ");
//
//            String dateTime="";
//            if(strDateOfTimeTemp.length>2){
//                //  dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2];
//                dateTime = strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
//            }else {
//                dateTime = strDateOfTimeTemp[0]+" "+strDateOfTimeTemp[1];
//            }
//
//            holder.tv_device_log_date.setText(dateTime);
//        }
//
//        holder.tv_device_description.setText(onNotificationContextMenu.get(position).getActivityDescription());
//        holder.tv_device_log_type.setText(onNotificationContextMenu.get(position).getActivityAction());


    }

    private void callDialogShow(final int position, final Context mContext, final SwitchCompat switch_temp) {
        String message="";
        if(switch_temp.isChecked()){
            message="Are you sure you want to Disable";
        }else {
            message="Are you sure you want to Enable";
        }

        final AlertDialog alertDialog =  new AlertDialog.Builder(mContext).setTitle("Confirm").setMessage(message)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callONOff(position,mContext);
                    }
                }).create();
        alertDialog.show();
    }

    private void callONOff(final int position, Context mContext) {

        if (!ActivityHelper.isConnectingToInternet(mContext)) {
            Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject dataObject = new JSONObject();

        try {

            dataObject.put("multi_sensor_id", multiSensorId);
            dataObject.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID));
            if(onNotificationContextMenu.get(position).getValue()==1){
                dataObject.put("is_push_enable", 0);
            }else {
                dataObject.put("is_push_enable", 1);
            }

            dataObject.put("sensor_type", onNotificationContextMenu.get(position).getSensor_type());
            dataObject.put("phone_id", APIConst.PHONE_ID_VALUE);
            dataObject.put("phone_type", APIConst.PHONE_TYPE_VALUE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String webUrl = ChatApplication.url + Constants.changeMultiSensorStatus;
        ActivityHelper.showProgressDialog(mContext, "Please wait.", false);
        ChatApplication.logDisplay("json is "+dataObject+" "+webUrl);


        new GetJsonTask(mContext, webUrl, "POST", dataObject.toString(), new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                ActivityHelper.dismissProgressDialog();
                try {

                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if(onNotificationContextMenu.get(position).getValue()==1){
                            onNotificationContextMenu.get(position).setValue(0);
                        }else {
                            onNotificationContextMenu.get(position).setValue(1);
                        }
                        //onNotificationContextMenu.get(position).setValue(switch_temp.isChecked() ? 1 : 0);
                        notifyItemChanged(position);

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


    @Override
    public int getItemCount() {
        return onNotificationContextMenu.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        View viewLine;
        SwitchCompat switch_temp;
        public TextView notification_title;

        public SensorViewHolder(View view) {
            super(view);
            notification_title = (TextView) itemView.findViewById(R.id.notification_title);
            switch_temp = itemView.findViewById(R.id.switch_temp);
            viewLine = itemView.findViewById(R.id.viewLine);
        }
    }

}
