package com.spike.bot.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.UnassignedListRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddUnassignedPanelAdapter extends RecyclerView.Adapter<AddUnassignedPanelAdapter.UnassignedHolder> {

    private List<UnassignedListRes.Data> roomdeviceList;
    private UnassignedClickEvent unassignedClickEvent;

    public AddUnassignedPanelAdapter(ArrayList<UnassignedListRes.Data> roomdeviceList, UnassignedClickEvent unassignedClickEvent) {
        this.roomdeviceList = roomdeviceList;
        this.unassignedClickEvent = unassignedClickEvent;
    }

    @Override
    public UnassignedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unassigned_list, parent, false);
        return new UnassignedHolder(view);
    }

    @Override
    public void onBindViewHolder(UnassignedHolder holder, final int position) {

        holder.mModuleId.setText("[" + roomdeviceList.get(position).getModuleId() + "]");

        if(roomdeviceList.get(position).getModuleType().equalsIgnoreCase("door_sensor")){
            holder.mImageIcon.setImageResource(Common.getIcon(1, roomdeviceList.get(position).getModuleType()));
        }else {
            holder.mImageIcon.setImageResource(Common.getIcon(0, roomdeviceList.get(position).getModuleType()));
        }


        holder.mDeviceName.setText(roomdeviceList.get(position).getModuleType());

        holder.mImgAdd.setId(position);
        holder.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unassignedClickEvent.onClick(holder.mImgAdd.getId(), roomdeviceList.get(holder.mImgAdd.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomdeviceList.size();
    }

    class UnassignedHolder extends RecyclerView.ViewHolder {

        private ImageView mImageIcon, mImgAdd;
        private TextView mDeviceName, mModuleId;

        UnassignedHolder(View itemView) {
            super(itemView);
            mImageIcon =  itemView.findViewById(R.id.up_device_icon);
            mDeviceName = itemView.findViewById(R.id.up_device_name);
            mModuleId = itemView.findViewById(R.id.up_device_module);
            mImgAdd =  itemView.findViewById(R.id.up_save);
        }
    }

    public interface UnassignedClickEvent {
        void onClick(int position, UnassignedListRes.Data roomdeviceList);
    }
}
