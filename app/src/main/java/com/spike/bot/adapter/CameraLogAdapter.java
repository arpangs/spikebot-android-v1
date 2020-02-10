package com.spike.bot.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.ImageZoomActivity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.NotificationList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 28/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraLogAdapter extends RecyclerView.Adapter<CameraLogAdapter.SensorViewHolder> {

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<NotificationList> arrayListLog = new ArrayList<>();
    String strDateOfTime, dateTime = "";
    String[] strDateOfTimeTemp;

    public CameraLogAdapter(Context context, ArrayList<NotificationList> arrayListLog1) {
        this.mContext = context;
        this.arrayListLog = arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_alert_adapter, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        try {
            if (!TextUtils.isEmpty(arrayListLog.get(position).getActivityTime())) {
                String dateString = Constants.logConverterDate(Long.parseLong(arrayListLog.get(position).getActivityTime()));
                strDateOfTimeTemp = dateString.split(" ");

                if (strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())) {
                    dateTime = strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2];
                } else {
                    dateTime = strDateOfTimeTemp[1] + strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
                }

                holder.tv_device_log_date.setText(Constants.formatcurrentdate(strDateOfTimeTemp[0]));
                holder.tv_device_log_time.setText(strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2]);

                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            }

            holder.tv_device_description.setText(arrayListLog.get(position).getMessage() + " " + "@");
            holder.tv_device_camera_name.setText(arrayListLog.get(position).getActivityDescription());
            // holder.tv_device_log_type.setText(arrayListLog.get(position).getActivityAction());

            if (arrayListLog.get(position).getSeen_by() != null)
            {

                if (!arrayListLog.get(position).getSeen_by().replace("|", "").contains(Common.getPrefValue(mContext, Constants.USER_ID))) {
                    holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                    holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                    holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                    holder.tv_device_camera_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                } else {
                    holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                    holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                    holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                    holder.tv_device_camera_name.setTextColor(mContext.getResources().getColor(R.color.txtPanal));

                }
            }

            if (arrayListLog.get(position).getLogType().contains("camera_persor_detected")) {
                Glide.with(mContext)
                        .load(arrayListLog.get(position).getImageUrl())
                        .fitCenter()
                        .error(R.drawable.cam_defult)
                        .skipMemoryCache(true)
                        .into(holder.txtImage);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.camera_on)
                        .fitCenter()
                        .error(R.drawable.cam_defult)
                        .skipMemoryCache(true)
                        .into(holder.txtImage);
            }

            holder.linearAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, ImageZoomActivity.class);
                    intent.putExtra("imgUrl", "" + arrayListLog.get(position).getImageUrl());
                    intent.putExtra("imgName", "" + arrayListLog.get(position).getActivityDescription());
                    intent.putExtra("imgDate", "" + arrayListLog.get(position).getActivityTime());
                    mContext.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

            if (arrayListLog.get(position).getLogType().contains("camera_persor_detected")) {
                Glide.with(mContext)
                        .load(arrayListLog.get(position).getImageUrl())
                        .fitCenter()
                        .error(R.drawable.cam_defult)
                        .skipMemoryCache(true)
                        .into(holder.txtImage);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.camera_on)
                        .fitCenter()
                        .error(R.drawable.cam_defult)
                        .skipMemoryCache(true)
                        .into(holder.txtImage);
            }
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

        public View view_header;
        public TextView tv_device_log_date, tv_device_camera_name, tv_device_description, tv_device_log_time;
        public ImageView txtImage;
        LinearLayout linearAlert;

        public SensorViewHolder(View view) {
            super(view);
            tv_device_description = itemView.findViewById(R.id.tv_device_description);
            // tv_device_log_type =  itemView.findViewById(R.id.tv_device_log_type);
            txtImage = itemView.findViewById(R.id.txtImage);
            tv_device_log_date = itemView.findViewById(R.id.tv_device_log_date);
            tv_device_log_time = itemView.findViewById(R.id.tv_device_log_time);
            tv_device_camera_name = itemView.findViewById(R.id.tv_device_camera_name);
            view_header = itemView.findViewById(R.id.view_header);
            linearAlert = itemView.findViewById(R.id.linearAlert);
        }
    }
}
