package com.spike.bot.adapter.room;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.model.DeviceVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditAdapterDeviceV2 extends RecyclerView.Adapter<RoomEditAdapterDeviceV2.EditDeviceHolder>{

    ArrayList<DeviceVO> deviceVOs;
    private ItemClickRoomEditListener mItemClickListener;

    public RoomEditAdapterDeviceV2(ArrayList<DeviceVO> deviceVOs1 , ItemClickRoomEditListener itemClickRoomEditListener){
        this.deviceVOs = deviceVOs1;
        this.mItemClickListener = itemClickRoomEditListener;
    }

    @Override
    public EditDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_edit_device_v2,parent,false);
        return new EditDeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(EditDeviceHolder holder, int position) {

        final DeviceVO item = (DeviceVO) deviceVOs.get(position);

        String itemDeviceName = "";
        int itemIcon  = 0;

        String clickAction = "1";
        if(!item.isSensor()){
            itemDeviceName = item.getDeviceName();
            itemIcon = Common.getIcon(0,item.getDevice_icon());
            clickAction = "1";
        }else{

            itemDeviceName = item.getSensor_name();
            itemIcon = Common.getIcon(0,item.getSensor_icon());
            clickAction = "isSensorClick";

            if(item.getSensor_icon().equals("doorsensor")){
                if(item.getDoor_subtype()==1){
                    itemIcon=R.drawable.off_door;
                }else if(item.getDoor_subtype()==2){
                    itemIcon=R.drawable.lock_only_grey;
                }else {
                    itemIcon=R.drawable.door_locked;
                }
            }
        }

        if(item.getDevice_icon().equalsIgnoreCase("heavyload")){
            itemIcon=R.drawable.off;
        }

        holder.itemTextView.setText(itemDeviceName);

        holder.iv_icon.setImageResource(itemIcon); //all icon grey


            //holder.iv_icon.setVisibility(View.GONE);
        final String finalClickAction = clickAction;
        holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClicked(item, finalClickAction,view);
                }
            });

            holder.iv_icon_text.setVisibility(View.GONE);

            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClicked(item,finalClickAction,view);
                }
            });

            holder.ll_room_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.itemClicked(item,finalClickAction,view);
                }
            });
    }

    @Override
    public int getItemCount() {
        return deviceVOs.size();
    }

    class EditDeviceHolder extends RecyclerView.ViewHolder{

        View view;
        TextView itemTextView;
        ImageView iv_icon;
        LinearLayout ll_room_item;
        ImageView iv_icon_text;

        public EditDeviceHolder(View view) {
            super(view);
            this.view = view;
            itemTextView = (TextView) view.findViewById(R.id.text_item);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            iv_icon_text = (ImageView) view.findViewById(R.id.iv_icon_text );
            ll_room_item = (LinearLayout) view.findViewById(R.id.ll_room_item );

        }
    }

}
