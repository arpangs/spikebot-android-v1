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

import java.util.List;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddUnassignedPanelAdapter extends RecyclerView.Adapter<AddUnassignedPanelAdapter.UnassignedHolder>{

    private List<UnassignedListRes.Data.RoomdeviceList> roomdeviceList;
    private UnassignedClickEvent unassignedClickEvent;

    public AddUnassignedPanelAdapter(List<UnassignedListRes.Data.RoomdeviceList> roomdeviceList,UnassignedClickEvent unassignedClickEvent){
        this.roomdeviceList = roomdeviceList;
        this.unassignedClickEvent = unassignedClickEvent;
    }

    @Override
    public UnassignedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_unassigned_list,parent,false);
        return new UnassignedHolder(view);
    }

    @Override
    public void onBindViewHolder(UnassignedHolder holder, final int position) {

        final UnassignedListRes.Data.RoomdeviceList roomDevice = roomdeviceList.get(position);

        String mModuleName = "";
        if(roomDevice.getIsModule() == 1){
            holder.mImageIcon.setImageResource(R.drawable.bulb_off);
            mModuleName = roomDevice.getModuleName();

        }else{
            mModuleName = roomDevice.getSensorName();
            holder.mImageIcon.setImageResource(Common.getIcon(0,roomDevice.getSensorIcon()));
        }

        holder.mDeviceName.setText(mModuleName);
        holder.mModuleId.setText("["+roomDevice.getModuleId()+"]");

        holder.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unassignedClickEvent.onClick(position,roomDevice);
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomdeviceList.size();
    }

    class UnassignedHolder extends RecyclerView.ViewHolder{

        private ImageView mImageIcon;
        private TextView mDeviceName;
        private TextView mModuleId;
        private ImageView mImgAdd;

        UnassignedHolder(View itemView) {
            super(itemView);
            mImageIcon = (ImageView) itemView.findViewById(R.id.up_device_icon);
            mDeviceName = (TextView) itemView.findViewById(R.id.up_device_name);
            mModuleId = (TextView)itemView.findViewById(R.id.up_device_module);
            mImgAdd = (ImageView) itemView.findViewById(R.id.up_save);
        }
    }
    public interface UnassignedClickEvent{
        void onClick(int position, UnassignedListRes.Data.RoomdeviceList roomdeviceList);
    }
}
