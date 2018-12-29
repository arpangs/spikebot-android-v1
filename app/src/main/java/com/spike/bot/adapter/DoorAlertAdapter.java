package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.model.CameraPushLog;
import com.spike.bot.model.DoorSensorResModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 28/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class DoorAlertAdapter extends RecyclerView.Adapter<DoorAlertAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    DoorSensorResModel.DATA.DoorList.UnreadLogs[] arrayListLog;


    public DoorAlertAdapter(Context context, DoorSensorResModel.DATA.DoorList.UnreadLogs[] arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_door_alert,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        if(position==0){
            holder.viewLine.setVisibility(View.GONE);
        }else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
        final DoorSensorResModel.DATA.DoorList.UnreadLogs unreadLogs=arrayListLog[position];

        if(!TextUtils.isEmpty(unreadLogs.getActivityTime())){
            Date today = null;//2018-01-12 19:40:07
            try {
                today = DateHelper.parseDateSimple(unreadLogs.getActivityTime(),DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String strDateOfTime=DateHelper.getDayString(today);
            String []strDateOfTimeTemp=strDateOfTime.split(" ");

            String dateTime="";
            if(strDateOfTimeTemp.length>2){
                //dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2];
                dateTime = strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2]+ System.getProperty("line.separator") + strDateOfTimeTemp[0];
            }else {
                dateTime = strDateOfTimeTemp[0]+" "+strDateOfTimeTemp[1];
            }

            holder.tv_device_log_date.setText(dateTime);
        }

        holder.tv_device_description.setText(unreadLogs.getActivityDescription());
        holder.tv_device_log_type.setText(unreadLogs.getActivityAction());


    }


    @Override
    public int getItemCount() {
        return arrayListLog.length;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        View viewLine;
        public TextView tv_device_log_date,tv_device_log_type,tv_device_description;

        public SensorViewHolder(View view) {
            super(view);
            tv_device_description = (TextView) itemView.findViewById(R.id.txtDetails);
            tv_device_log_type = (TextView) itemView.findViewById(R.id.txtAction);
            tv_device_log_date = (TextView) itemView.findViewById(R.id.txtDateTime);
            viewLine = (View) itemView.findViewById(R.id.viewLine);
        }
    }
}
