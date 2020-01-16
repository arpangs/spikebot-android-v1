package com.spike.bot.adapter.panel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.DevicePanelVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 21/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class ExistPanelGridAdapter extends RecyclerView.Adapter<ExistPanelGridAdapter.DeviceHolder>{

    private ArrayList<DevicePanelVO> deviceVOs;
    private Context context;
    private boolean isSync;

    public ExistPanelGridAdapter(ArrayList<DevicePanelVO> roomVOList, boolean isSync){
        this.deviceVOs = roomVOList;
        this.isSync = isSync;
    }


    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_exist_panel_devices, parent, false);
        return new DeviceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, final int position) {
        final DevicePanelVO deviceVO = deviceVOs.get(position);
        holder.device_header.setText(deviceVO.getDeviceName());

        if(deviceVO.getDeviceType().equalsIgnoreCase("-1")){
            holder.device_icon.setImageResource(R.drawable.off);
        }else {
            holder.device_icon.setImageResource(Common.getIcon(0, deviceVO.getDevice_icon()));
        }

        if(deviceVO.isSelected()){
            holder.iv_icon_select.setVisibility(View.VISIBLE);
        }else{
            holder.iv_icon_select.setVisibility(View.GONE);
        }

        if(!isSync){
            holder.device_icon.setOnClickListener(null);
        }

        holder.device_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSync){
                    deviceVO.setSelected(!deviceVO.isSelected());
                    notifyItemChanged(position,deviceVO);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceVOs.size();
    }

    public class DeviceHolder extends RecyclerView.ViewHolder{

        ImageView device_icon,iv_icon_select;
        TextView device_header;

        public DeviceHolder(View itemView) {
            super(itemView);
            device_icon = itemView.findViewById(R.id.device_icon);
            device_header = itemView.findViewById(R.id.device_title);
            iv_icon_select = itemView.findViewById(R.id.iv_icon_select);
        }
    }
}

