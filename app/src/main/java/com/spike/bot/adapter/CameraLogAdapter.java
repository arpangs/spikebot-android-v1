package com.spike.bot.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.activity.CameraDeviceLogActivity;
import com.spike.bot.activity.ImageZoomActivity;
import com.spike.bot.customview.CustomEditText;
import com.spike.bot.model.CameraPushLog;
import com.spike.bot.model.NotificationList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.activation.DataSource;

/**
 * Created by Sagar on 28/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraLogAdapter extends RecyclerView.Adapter<CameraLogAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<NotificationList> arrayListLog=new ArrayList<>();


    public CameraLogAdapter(Context context, ArrayList<NotificationList> arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_alert_adapter,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

//        if(position==0){
//            holder.view_header.setVisibility(View.GONE);
//        }else {
//            holder.view_header.setVisibility(View.VISIBLE);
//        }
//        holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
//        holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
//        holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));

        if(!TextUtils.isEmpty(arrayListLog.get(position).getActivityTime())){
            Date today = null;//2018-01-12 19:40:07
            try {
                today = DateHelper.parseDateSimple(arrayListLog.get(position).getActivityTime(),DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String strDateOfTime=DateHelper.getDayString(today);
            String []strDateOfTimeTemp=strDateOfTime.split(" ");

            String dateTime="";
            if(strDateOfTimeTemp.length>2){
               // dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2];
                dateTime = strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2]+ System.getProperty("line.separator") + strDateOfTimeTemp[0];
            }else {
                dateTime = strDateOfTimeTemp[0]+" "+strDateOfTimeTemp[1];
            }

            holder.tv_device_log_date.setText(dateTime);
            holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
        }

        holder.tv_device_description.setText(arrayListLog.get(position).getActivityDescription());
        holder.tv_device_log_type.setText(arrayListLog.get(position).getActivityAction());


        if(arrayListLog.get(position).getIs_unread().equalsIgnoreCase("1")){
            holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.automation_red));
        }else {
            holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
        }
        Glide.with(mContext)
                .load(arrayListLog.get(position).getImage_url())
                .fitCenter()
                .error(R.drawable.cam_defult)
                .skipMemoryCache(true)
                .into(holder.txtImage);

        holder.linearAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext,ImageZoomActivity.class);
                intent.putExtra("imgUrl",""+arrayListLog.get(position).getImage_url());
                intent.putExtra("imgName",""+arrayListLog.get(position).getActivityDescription());
                intent.putExtra("imgDate",""+arrayListLog.get(position).getActivityTime());
                mContext.startActivity(intent);
            }
        });



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

        public View view_header;
        public TextView tv_device_log_date,tv_device_log_type,tv_device_description;
        public ImageView txtImage;
        LinearLayout linearAlert;

        public SensorViewHolder(View view) {
            super(view);
            tv_device_description = (TextView) itemView.findViewById(R.id.tv_device_description);
            tv_device_log_type = (TextView) itemView.findViewById(R.id.tv_device_log_type);
            txtImage = (ImageView) itemView.findViewById(R.id.txtImage);
            tv_device_log_date = (TextView) itemView.findViewById(R.id.tv_device_log_date);
            view_header = (View) itemView.findViewById(R.id.view_header);
            linearAlert = (LinearLayout) itemView.findViewById(R.id.linearAlert);
        }
    }
}
