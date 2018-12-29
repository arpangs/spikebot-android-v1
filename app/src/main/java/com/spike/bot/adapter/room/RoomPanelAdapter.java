package com.spike.bot.adapter.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.customview.WrapContentGriLayoutManager;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 27/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomPanelAdapter extends RecyclerView.Adapter<RoomPanelAdapter.RoomPanelViewHolder> implements ItemClickListener{

    private List<PanelVO> panelVOList;
    private Context context;
    private RoomPanelDeviceAdapter roomPanelDeviceAdapter;
    private ItemClickListener itemClickListener;

    public RoomPanelAdapter(List<PanelVO> panelVOs,ItemClickListener itemClickListener){
        this.panelVOList = panelVOs;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RoomPanelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new RoomPanelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_panel_dashboard,parent,false));
    }

    @Override
    public void itemClicked(RoomVO item, String action) {
        itemClickListener.itemClicked(item,action);
    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {
        itemClickListener.itemClicked(panelVO,action);

    }

    @Override
    public void itemClicked(DeviceVO section, String action,int position) {
        itemClickListener.itemClicked(section,action,position);

    }

    @Override
    public void itemClicked(CameraVO cameraVO, String action) {
        itemClickListener.itemClicked(cameraVO,action);
    }

    @Override
    public void onBindViewHolder(RoomPanelViewHolder holder, int position) {

        final PanelVO panelVO = panelVOList.get(position);
        holder.heading.setText(panelVO.getPanelName());

        if(panelVO.getPanel_status()==1 ){
            holder.iv_room_panel_onoff.setImageResource(R.drawable.room_on);
        }
        else{
            holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
        }

        holder.iv_room_panel_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(panelVO,"onOffclick");
            }
        });
        if(panelVO.getType().equalsIgnoreCase("camera")){
            holder.iv_room_panel_onoff.setVisibility(View.INVISIBLE);
            List<CameraVO> cameraVOs = panelVO.getCameraList();

            if(cameraVOs!=null){
                holder.recycler_device_listview.setVisibility(View.VISIBLE);
                roomPanelDeviceAdapter = new RoomPanelDeviceAdapter(new ArrayList<DeviceVO>(),cameraVOs,itemClickListener,true);
                holder.recycler_device_listview.setAdapter(roomPanelDeviceAdapter);
            }
        }
        else{
            holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);

            List<DeviceVO> deviceVOList = panelVO.getDeviceList();
            if(deviceVOList!=null){
                holder.recycler_device_listview.setVisibility(View.VISIBLE);
                roomPanelDeviceAdapter = new RoomPanelDeviceAdapter(deviceVOList,new ArrayList<CameraVO>(),itemClickListener,false);
                holder.recycler_device_listview.setAdapter(roomPanelDeviceAdapter);
            }
        }

    }

    @Override
    public int getItemCount() {
        return panelVOList.size();
    }

    class RoomPanelViewHolder extends RecyclerView.ViewHolder{

        TextView heading;
        ImageView iv_room_panel_onoff;
        RecyclerView recycler_device_listview;

        public RoomPanelViewHolder(View itemView) {
            super(itemView);
            heading = (TextView)itemView.findViewById(R.id.heading);
            iv_room_panel_onoff = (ImageView)itemView.findViewById(R.id.iv_room_panel_onoff);
            recycler_device_listview = (RecyclerView)itemView.findViewById(R.id.recycler_device_listview);
            recycler_device_listview.setLayoutManager(new WrapContentGriLayoutManager(context,4));
        }
    }
}
