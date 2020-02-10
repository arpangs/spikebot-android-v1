package com.spike.bot.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
    String[] strDateOfTimeTemp,actionList;;


    public DoorAlertAdapter(Context context, List<RemoteDetailsRes.Data.UnseenLog> arrayListLog1) {
        this.mContext = context;
        this.arrayListLog = arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_log_new, parent, false);
        return new SensorViewHolder(view,viewType);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        try {
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

                strDateOfTimeTemp = dateString.split(" ");

                if (strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())) {
                    dateTime = strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2];
                } else {
                    dateTime = strDateOfTimeTemp[1] + strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
                }


                holder.tv_device_log_date.setText(Constants.formatcurrentdate(strDateOfTimeTemp[0]));
                holder.tv_device_log_time.setText(strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2]);
            }
            holder.tv_device_description.setText( arrayListLog.get(position).getMessage().trim() + " " + "@");
            actionList = arrayListLog.get(position).getActivityDescription().split("\\|");

            if (actionList[0] != null) {
                holder.tv_device_name.setVisibility(View.VISIBLE);
                holder.tv_device_name.setText(actionList[0].trim() + " " + " " + Html.fromHtml("<span style='font-size:20px;'>&#x2022;</span>" + " "));
            } else {
                holder.tv_device_name.setVisibility(View.GONE);
            }

            if (actionList.length > 1 && actionList[1] != null) {
                holder.tv_panel_name.setVisibility(View.VISIBLE);
                holder.tv_panel_name.setText(actionList[1].trim() + " "/* + " " + " " + "\u2022" + " "*/);
            } else {
                holder.tv_device_name.setText(actionList[0].trim());
                holder.tv_panel_name.setVisibility(View.GONE);
            }

            if (actionList.length > 2 && actionList[2] != null) {
                holder.tv_room_name.setVisibility(View.VISIBLE);
                holder.tv_room_name.setText(actionList[2]);
                holder.tv_panel_name.setText(actionList[1].trim() + " " + " " + Html.fromHtml("<span style='font-size:20px;'>&#x2022;</span>" + " "));
            } else {
                holder.tv_room_name.setVisibility(View.GONE);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

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

        //common
        View view, view_header;
        int viewType;
        TextView tv_device_log_date, tv_device_description, tv_device_log_time, tv_device_name, tv_panel_name, tv_room_name, txt_empty_view;

        public SensorViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            view_header = view.findViewById(R.id.view_header);
            tv_device_log_date = view.findViewById(R.id.txt_log_date);
            tv_device_description = view.findViewById(R.id.txt_log_description);
            tv_device_log_time = view.findViewById(R.id.txt_log_time);
            tv_device_name = view.findViewById(R.id.txt_device_name);
            tv_panel_name = view.findViewById(R.id.txt_panel_name);
            tv_room_name = view.findViewById(R.id.txt_room_name);
            txt_empty_view = view.findViewById(R.id.txt_empty_view);
        }

        public void clearAnimation() {
            view.clearAnimation();
        }
    }
}
