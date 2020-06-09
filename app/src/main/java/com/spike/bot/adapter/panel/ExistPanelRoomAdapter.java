package com.spike.bot.adapter.panel;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DevicePanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 21/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class ExistPanelRoomAdapter extends RecyclerView.Adapter<ExistPanelRoomAdapter.ExistPanelHolder> {

    private ArrayList<RoomVO> roomVOList;
    private Context context;
    private boolean isSync;
    String header = "";

    public ExistPanelRoomAdapter(ArrayList<RoomVO> roomVOList, boolean isSync) {
        this.roomVOList = roomVOList;
        this.isSync = isSync;
    }

    @Override
    public ExistPanelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_panel, parent, false);

        return new ExistPanelHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ExistPanelHolder holder, final int position) {
        final RoomVO roomVO = roomVOList.get(position);

        if (isSync) {
            holder.img_check_panel.setVisibility(View.VISIBLE);
        } else {
            holder.img_check_panel.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(roomVO.getPanel_name())) {
            header = roomVO.getRoomName();
        } else {
            if (isSync) {
                header = roomVO.getRoomName() + " \n" + roomVO.getPanel_name();
            } else {
                header = roomVO.getRoomName() + " - " + roomVO.getPanel_name();
            }
        }
        holder.txt_room_panel_name.setText(header);

        if (isSync && roomVO.isSelectAllDevices()) {
            holder.img_check_panel.setImageResource(R.drawable.icn_check);
        } else {
            if (isSync) {
                holder.img_check_panel.setImageResource(R.drawable.icn_uncheck);
            }
        }

        holder.img_check_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (roomVO.isSelectAllDevices()) {
                    holder.img_check_panel.setImageResource(R.drawable.icn_uncheck);
                } else {
                    holder.img_check_panel.setImageResource(R.drawable.icn_check);
                }

                roomVO.setSelectAllDevices(!roomVO.isSelectAllDevices());

                for (int i = 0; i < roomVOList.size(); i++) {
                    RoomVO roomVO1 = roomVOList.get(i);
                    if (i == position) {
                        ArrayList<DevicePanelVO> devicePanelVOs = roomVO1.getDevicePanelList();
                        for (DevicePanelVO devicePanelVO : devicePanelVOs) {
                            devicePanelVO.setSelected(!devicePanelVO.isSelected());
                        }
                    } else {
                        ArrayList<DevicePanelVO> devicePanelVOs = roomVO1.getDevicePanelList();
                        roomVO1.setSelectAllDevices(false);
                        for (DevicePanelVO devicePanelVO : devicePanelVOs) {
                            devicePanelVO.setSelected(false);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        ExistPanelGridAdapter existPanelGridAdapter = new ExistPanelGridAdapter(roomVO.getDevicePanelList(), isSync);
        holder.devicesList.setAdapter(existPanelGridAdapter);
    }

    @Override
    public int getItemCount() {
        return roomVOList.size();
    }

    class ExistPanelHolder extends RecyclerView.ViewHolder {

        TextView txt_room_panel_name;
        private ImageView img_check_panel;
        RecyclerView devicesList;

        ExistPanelHolder(View itemView) {
            super(itemView);
            txt_room_panel_name = itemView.findViewById(R.id.txt_room_panel_name);
            devicesList = itemView.findViewById(R.id.list_panel_devices);
            img_check_panel = itemView.findViewById(R.id.img_check_panel);
            devicesList.setLayoutManager(new GridLayoutManager(context, Constants.SWITCH_NUMBER_EXIST_PANEL));
        }
    }
}
