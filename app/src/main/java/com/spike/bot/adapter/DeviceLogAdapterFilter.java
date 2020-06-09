package com.spike.bot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.model.DeviceLog;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 19/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DeviceLogAdapterFilter extends RecyclerView.Adapter<DeviceLogAdapterFilter.LogHolder> {

    private List<DeviceLog> deviceLogsList;

    public DeviceLogAdapterFilter(List<DeviceLog> deviceLogs) {
        this.deviceLogsList = deviceLogs;
    }

    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_device_log_filter, parent, false);
        return new LogHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {
        DeviceLog deviceLog = deviceLogsList.get(position);

        Date today = null;//2018-01-12 19:40:07
        try {
            today = DateHelper.parseDateSimple(deviceLog.getActivity_time(), DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txt_date_time.setText("" + DateHelper.getDayString(today));
        holder.txt_type.setText(deviceLog.getActivity_type());
        holder.txt_details.setText(deviceLog.getActivity_description());
        holder.txt_action.setText(deviceLog.getActivity_action());
    }

    @Override
    public int getItemCount() {
        return deviceLogsList.size();
    }

    public static class LogHolder extends RecyclerView.ViewHolder {

        private TextView txt_date_time, txt_type, txt_details, txt_action;

        public LogHolder(View itemView) {
            super(itemView);

            txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_time_value);
            txt_type = (TextView) itemView.findViewById(R.id.txt_type_value);
            txt_details = (TextView) itemView.findViewById(R.id.txt_details_value);
            txt_action = (TextView) itemView.findViewById(R.id.txt_action_value);

        }
    }

}
