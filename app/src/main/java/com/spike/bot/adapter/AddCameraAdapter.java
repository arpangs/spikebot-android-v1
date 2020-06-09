package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 27/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddCameraAdapter extends RecyclerView.Adapter<AddCameraAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<CameraVO> arrayListLog=new ArrayList<>();
    public SelectCamera selectCamera;


    public AddCameraAdapter(Context context, ArrayList<CameraVO> arrayListLog1,SelectCamera selectCamera){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
        this.selectCamera=selectCamera;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_room_camera_item,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.iv_icon.setImageResource(Common.getIcon(0,"camera"));

        if(arrayListLog.get(position).getIsSelect()){
            holder.iv_icon_active_camera.setVisibility(View.VISIBLE);
            holder.iv_icon_active_camera.setImageDrawable(mContext.getResources().getDrawable(R.drawable.switch_select_));
        }else {
            holder.iv_icon_active_camera.setVisibility(View.GONE);
        }

        holder.text_item.setText(""+arrayListLog.get(position).getCamera_name());
        holder.iv_icon_text.setVisibility(View.GONE);

        holder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayListLog.get(position).getIsSelect()){
                    arrayListLog.get(position).setIsSelect(false);
                }else {
                    arrayListLog.get(position).setIsSelect(true);
                }
                notifyDataSetChanged();
                selectCamera.selectcameraList(arrayListLog);

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

        public TextView text_item;
        public ImageView iv_icon_text,iv_icon_active_camera,iv_icon;

        public SensorViewHolder(View view) {
            super(view);
            text_item = (TextView) itemView.findViewById(R.id.text_item);
            iv_icon_text = (ImageView) itemView.findViewById(R.id.iv_icon_text);
            iv_icon_active_camera = (ImageView) itemView.findViewById(R.id.iv_icon_active_camera);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
