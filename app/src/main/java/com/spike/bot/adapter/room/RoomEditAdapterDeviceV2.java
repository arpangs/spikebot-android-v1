package com.spike.bot.adapter.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.model.DeviceVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditAdapterDeviceV2 extends RecyclerView.Adapter<RoomEditAdapterDeviceV2.EditDeviceHolder> {

    Context context;
    DeviceVO item;
    ArrayList<DeviceVO> deviceVOs;
    String itemDeviceName = "", clickAction = "1";
    int itemIcon = 0;
    private ItemClickRoomEditListener mItemClickListener;


    public RoomEditAdapterDeviceV2(Context context, ArrayList<DeviceVO> deviceVOs1, ItemClickRoomEditListener itemClickRoomEditListener) {
        this.context = context;
        this.deviceVOs = deviceVOs1;
        this.mItemClickListener = itemClickRoomEditListener;
    }

    @Override
    public EditDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_edit_device_v2, parent, false);
        return new EditDeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(EditDeviceHolder holder, int position) {

        item = (DeviceVO) deviceVOs.get(position);

        if (!item.isSensor()) {
            itemDeviceName = item.getDeviceName();
            if (item.getDevice_icon().equals(context.getResources().getString(R.string.curtain))) {
                itemIcon = Common.getIcon(0, item.getDevice_icon());
            } else {
                if (item.getDevice_icon().equalsIgnoreCase("ac")) {
                    itemIcon = R.drawable.ac_off;
                } else {
                    itemIcon = Common.getIcon(0, item.getDevice_icon());
                }
            }

            clickAction = "1";
        } else {

            itemDeviceName = item.getSensor_name();
            if (item.getDevice_icon().equalsIgnoreCase("ac")) {
                itemIcon = R.drawable.ac_remote_off;
            } else {
                itemIcon = Common.getIcon(0, item.getDevice_icon());
            }
            clickAction = "isSensorClick";

            if (item.getSensor_icon().equals(context.getResources().getString(R.string.door_sensor))) {
                itemIcon = Common.getDoorIcon(1);
            }
        }

        if (item.getDevice_icon().equalsIgnoreCase(context.getResources().getString(R.string.heavyload))) {
            itemIcon = R.drawable.high_wolt_off;
        }

        if (item.getDevice_icon().equalsIgnoreCase("lock")) {
            itemIcon = R.drawable.lock_only;
        }


        if (item.getDeviceType().toLowerCase().equalsIgnoreCase("remote")) {
            if (item.getDevice_sub_type().toLowerCase().equalsIgnoreCase("ac")) {
                itemIcon = R.drawable.ac_remote_off;

            } else if (item.getDevice_sub_type().toLowerCase().equalsIgnoreCase("tv")) {

                itemIcon = R.drawable.tv_off;
            } else if (item.getDevice_sub_type().toLowerCase().equalsIgnoreCase("dth")) {

                itemIcon = R.drawable.dth_off;
            } else if (item.getDevice_sub_type().toLowerCase().equalsIgnoreCase("tv_dth")) {

                itemIcon = R.drawable.dth_tv_off;
            } else {
                itemIcon = R.drawable.remote_ac_off;
            }
        }


        holder.itemTextView.setText(itemDeviceName);
        holder.iv_icon.setImageResource(itemIcon); //all icon grey
        final String finalClickAction = clickAction;

        holder.view.setId(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(deviceVOs.get(holder.view.getId()), finalClickAction, view);
            }
        });

        holder.iv_icon_text.setVisibility(View.GONE);

        holder.iv_icon.setId(position);
        holder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(deviceVOs.get(holder.iv_icon.getId()), finalClickAction, view);
            }
        });

        holder.itemTextView.setId(position);
        holder.itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(deviceVOs.get(holder.itemTextView.getId()), finalClickAction, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceVOs.size();
    }

    class EditDeviceHolder extends RecyclerView.ViewHolder {

        View view;
        TextView itemTextView;
        ImageView iv_icon, iv_icon_text;
        LinearLayout ll_room_item;

        public EditDeviceHolder(View view) {
            super(view);
            this.view = view;
            itemTextView = view.findViewById(R.id.text_item);
            iv_icon = view.findViewById(R.id.iv_icon);
            iv_icon_text = view.findViewById(R.id.iv_icon_text);
            ll_room_item = view.findViewById(R.id.ll_room_item);

        }
    }

}
