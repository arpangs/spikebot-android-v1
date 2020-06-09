package com.spike.bot.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.model.SensorLogNotificationRes;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 16/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorLogNotificationAdapter extends RecyclerView.Adapter<SensorLogNotificationAdapter.LogViewHolder> {

    List<SensorLogNotificationRes.Data.NotificationList> notificationLists;
    SensorLogNotificationRes.Data.NotificationList notificationList;
    int redColor;

    public SensorLogNotificationAdapter(List<SensorLogNotificationRes.Data.NotificationList> notificationList) {
        this.notificationLists = notificationList;
    }


    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sensor_log_notification, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {

        notificationList = notificationLists.get(position);
        if (notificationList.getActivityAction().equalsIgnoreCase("End Of Record")) {
            holder.txt_empty_view.setVisibility(View.VISIBLE);
            holder.tv_sensor_action.setVisibility(View.GONE);
            holder.tv_sensor_desc.setVisibility(View.GONE);
            holder.tv_sensor_date.setVisibility(View.GONE);

        } else {

            if (notificationList.getIsUnread() > 0) {
                redColor = Color.RED;
            } else {
                redColor = Color.parseColor("#111111");
            }

            holder.tv_sensor_action.setTextColor(redColor);
            holder.tv_sensor_action.setTextColor(redColor);
            holder.tv_sensor_desc.setTextColor(redColor);
            holder.tv_sensor_date.setTextColor(redColor);

            holder.txt_empty_view.setVisibility(View.GONE);
            holder.tv_sensor_action.setVisibility(View.VISIBLE);
            holder.tv_sensor_desc.setVisibility(View.VISIBLE);
            holder.tv_sensor_date.setVisibility(View.VISIBLE);

            holder.tv_sensor_action.setText(notificationList.getActivityAction() + " - " + notificationList.getActivityType());

            Date today = null;//2018-01-12 19:40:07
            try {
                today = DateHelper.parseDateSimple(notificationList.getActivityTime(), DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_sensor_date.setText("" + DateHelper.getDayString(today));

            holder.tv_sensor_desc.setText(notificationList.getActivityDescription());
        }


    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_sensor_action, tv_sensor_date, tv_sensor_desc, txt_empty_view;

        public LogViewHolder(View itemView) {
            super(itemView);
            tv_sensor_action = (TextView) itemView.findViewById(R.id.tv_sensor_log_type);
            tv_sensor_date = (TextView) itemView.findViewById(R.id.tv_sensor_log_date);
            tv_sensor_desc = (TextView) itemView.findViewById(R.id.tv_sensor_log_desc);
            txt_empty_view = (TextView) itemView.findViewById(R.id.txt_empty_view);
        }
    }

}
