package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DoorSensorResModel;
import com.spike.bot.model.RemoteDetailsRes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 28/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class DoorAlertAdapter extends RecyclerView.Adapter<DoorAlertAdapter.SensorViewHolder> {

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    List<RemoteDetailsRes.Data.UnseenLog>  arrayListLog =new ArrayList<>();

    String strDateOfTime, dateTime = "";
    String[] strDateOfTimeTemp;


    public DoorAlertAdapter(Context context, List<RemoteDetailsRes.Data.UnseenLog> arrayListLog1) {
        this.mContext = context;
        this.arrayListLog = arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_door_alert, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        if (position == 0) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(arrayListLog.get(position).getCreatedAt())) {
//            Date today = null;//2018-01-12 19:40:07
//            try {
//                today = DateHelper.parseDateSimple(arrayListLog.get(position).getCreatedAt(), DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            strDateOfTime = DateHelper.getDayString(today);
//            strDateOfTimeTemp = strDateOfTime.split(" ");
//            if (strDateOfTimeTemp.length > 2) {
//                dateTime = strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
//            } else {
//                dateTime = strDateOfTimeTemp[0] + " " + strDateOfTimeTemp[1];
//            }

            String dateString = Constants.logConverterDate(Long.parseLong(arrayListLog.get(position).getCreatedAt()));

            strDateOfTimeTemp  =dateString.split(" ");

            if(strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())){
                dateTime = strDateOfTimeTemp[1]+" "+strDateOfTimeTemp[2];
            }else {
                dateTime = strDateOfTimeTemp[1]+ strDateOfTimeTemp[2]+ System.getProperty("line.separator") + strDateOfTimeTemp[0] ;
            }


            holder.tv_device_log_date.setText(dateTime);
        }

        holder.tv_device_description.setText(arrayListLog.get(position).getActivityDescription());
        holder.tv_device_log_type.setText(arrayListLog.get(position).getActivityAction());


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

        View viewLine;
        public TextView tv_device_log_date, tv_device_log_type, tv_device_description;

        public SensorViewHolder(View view) {
            super(view);
            tv_device_description =  itemView.findViewById(R.id.txtDetails);
            tv_device_log_type =  itemView.findViewById(R.id.txtAction);
            tv_device_log_date =  itemView.findViewById(R.id.txtDateTime);
            viewLine =  itemView.findViewById(R.id.viewLine);
        }
    }
}
