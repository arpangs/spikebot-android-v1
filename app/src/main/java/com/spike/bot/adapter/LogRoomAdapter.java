package com.spike.bot.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;

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
        return R.layout.row_device_log_new;
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

            if (!TextUtils.isEmpty(deviceLog.getActivity_time())) {

                String dateString = Constants.logConverterDate(Long.parseLong(deviceLog.getActivity_time()));

                strDateOfTimeTemp = dateString.split(" ");
                holder.tv_device_log_date.setText(Constants.formatcurrentdate(strDateOfTimeTemp[0]));
                holder.tv_device_log_time.setText(strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2]);
            }

            if (isNotification.equals("roomSensorUnreadLogs")) {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            } else if (isNotification.equals("All Notification") && deviceLog.seen_by == null) {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            } else if (isNotification.equals("DoorSensor") && deviceLog.seen_by == null) {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            } else if (isNotification.equals("YaleLock") && deviceLog.seen_by == null) {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            } else {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
            }

            if (deviceLog.getActivity_type().contains("No Record Found")) {
                holder.tv_device_description.setVisibility(View.GONE);
                holder.tv_device_log_date.setVisibility(View.GONE);
                holder.tv_device_log_time.setVisibility(View.GONE);
                holder.tv_device_name.setVisibility(View.GONE);
                holder.tv_panel_name.setVisibility(View.GONE);
                holder.tv_room_name.setVisibility(View.GONE);
                holder.view_header.setVisibility(View.GONE);
            } else {
                holder.tv_device_description.setVisibility(View.VISIBLE);
                holder.tv_device_log_date.setVisibility(View.VISIBLE);
                holder.tv_device_log_time.setVisibility(View.VISIBLE);
                holder.tv_device_name.setVisibility(View.VISIBLE);
                holder.tv_panel_name.setVisibility(View.VISIBLE);
                holder.tv_room_name.setVisibility(View.VISIBLE);
                holder.view_header.setVisibility(View.VISIBLE);

                holder.tv_device_description.setText(deviceLog.getMessage().trim());
                actionList = deviceLog.getActivity_description().split("\\|");

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
            }

        } catch (Exception e) {
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
        TextView tv_device_log_date, tv_device_description, tv_device_log_time, tv_device_name, tv_panel_name, tv_room_name, txt_empty_view;

        public ViewHolder(View view, int viewType) {
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
