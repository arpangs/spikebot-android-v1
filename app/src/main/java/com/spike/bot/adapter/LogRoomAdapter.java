package com.spike.bot.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.model.DeviceLog;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 25/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class LogRoomAdapter extends RecyclerView.Adapter<LogRoomAdapter.ViewHolder> {

    Activity mContext;
    List<DeviceLog> deviceLogs;
    String isNotification = "", strDateOfTime, dateTime;
    String[] strDateOfTimeTemp, actionList;

    public LogRoomAdapter(Activity context, List<DeviceLog> deviceLogList, String isNotification) {

        this.mContext = context;
        this.deviceLogs = deviceLogList;
        this.isNotification = isNotification;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.row_device_log;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        DeviceLog deviceLog = deviceLogs.get(listPosition);
        try {

            if (isNotification.equals("roomSensorUnreadLogs")) {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description2.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description3.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            } else {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description2.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description3.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            }
            if (listPosition == 0) {
                holder.view_header.setVisibility(View.GONE);
            } else {
                holder.view_header.setVisibility(View.VISIBLE);
            }

            if (deviceLog.getActivity_action().equalsIgnoreCase("End of Record")) {
                holder.txt_empty_view.setVisibility(View.GONE);
                holder.txt_empty_view.setText(deviceLog.getActivity_type());
                holder.tv_device_log_date.setVisibility(View.GONE);
                holder.tv_device_log_type.setVisibility(View.GONE);
                holder.tv_device_description.setVisibility(View.GONE);
            } else {
                holder.txt_empty_view.setVisibility(View.GONE);
                holder.tv_device_log_date.setVisibility(View.VISIBLE);
                holder.tv_device_log_type.setVisibility(View.VISIBLE);
                holder.tv_device_description.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(deviceLog.getActivity_time())) {
                Date today = DateHelper.parseDateSimple(deviceLog.getActivity_time(), DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);//2018-01-12 19:40:07

                strDateOfTime = DateHelper.getDayString(today);
                strDateOfTimeTemp = strDateOfTime.split(" ");

                if (strDateOfTimeTemp.length > 2) {
                    dateTime = strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
                } else {
                    dateTime = strDateOfTimeTemp[0] + " " + strDateOfTimeTemp[1];
                }

                holder.tv_device_log_date.setText(dateTime);
            }

            holder.tv_device_log_type.setText(deviceLog.getActivity_action());

            actionList = deviceLog.getActivity_description().split("\\|");

            if (actionList[0] != null) {
                holder.tv_device_description.setVisibility(View.VISIBLE);
                holder.tv_device_description.setText(actionList[0].trim());
            } else {
                holder.tv_device_description.setVisibility(View.GONE);
            }
            if (actionList.length > 1 && actionList[1] != null) {
                holder.tv_device_description2.setVisibility(View.VISIBLE);
                holder.tv_device_description2.setText(actionList[1].trim());
            } else {
                holder.tv_device_description2.setVisibility(View.GONE);
            }

            if (actionList.length > 2 && actionList[2] != null) {
                holder.tv_device_description3.setVisibility(View.VISIBLE);
                holder.tv_device_description3.setText(actionList[2]);
            } else {
                holder.tv_device_description3.setVisibility(View.GONE);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((ViewHolder) holder).clearAnimation();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return deviceLogs.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view, view_header;
        int viewType;
        TextView tv_device_log_type, tv_device_log_date, tv_device_description, tv_device_description2, tv_device_description3, txt_empty_view;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            view_header = (View) view.findViewById(R.id.view_header);
            tv_device_log_type = (TextView) view.findViewById(R.id.tv_device_log_type);
            tv_device_log_date = (TextView) view.findViewById(R.id.tv_device_log_date);
            tv_device_description = (TextView) view.findViewById(R.id.tv_device_description);
            tv_device_description2 = (TextView) view.findViewById(R.id.tv_device_description2);
            tv_device_description3 = (TextView) view.findViewById(R.id.tv_device_description3);
            txt_empty_view = (TextView) view.findViewById(R.id.txt_empty_view);
        }

        public void clearAnimation() {
            view.clearAnimation();
        }
    }
}
