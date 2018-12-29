package com.spike.bot.adapter.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.WrapContentGriLayoutManager;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 27/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomExpandbleParentAdapter extends RecyclerView.Adapter<RoomExpandbleParentAdapter.RoomParentHolder> implements ItemClickListener{

    private List<RoomVO> roomVOList;
    private Context context;
    private RoomPanelAdapter roomPanelAdapter;
    private ItemClickListener mItemClickListener;

    public RoomExpandbleParentAdapter(List<RoomVO> roomVOs,ItemClickListener itemClick){
        this.roomVOList = roomVOs;
        this.mItemClickListener = itemClick;
    }

    @Override
    public RoomParentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new RoomParentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_home_dashboard,parent,false));
    }


    @Override
    public void itemClicked(RoomVO item, String action) {
        mItemClickListener.itemClicked(item,action);
    }

    @Override
    public void itemClicked(PanelVO panelVO, String action) {
        mItemClickListener.itemClicked(panelVO,action);
    }

    @Override
    public void itemClicked(DeviceVO section, String action,int position) {
        mItemClickListener.itemClicked(section,action,position);

    }

    @Override
    public void itemClicked(CameraVO cameraVO, String action) {
        mItemClickListener.itemClicked(cameraVO,action);
    }

    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();

    public void updateItem(String moduleId,String deviceId,String deviceStatus) {

        DeviceVO item = new DeviceVO();
        item.setModuleId(moduleId);
        item.setDeviceId(deviceId);

        for (int i = 0; i < roomVOList.size(); i++) {
            RoomVO roomVO = roomVOList.get(i);
            ArrayList<PanelVO> panelVOArrayList = roomVO.getPanelList();
            for (PanelVO panelVO : panelVOArrayList) {
                ArrayList<DeviceVO> arrayList = panelVO.getDeviceList();
                for (DeviceVO deviceVO : arrayList) {
                    if (Integer.parseInt(deviceVO.getDeviceId()) == Integer.parseInt(deviceId) && deviceVO.getModuleId().equalsIgnoreCase(moduleId)) {
                        deviceVO.setDeviceStatus(Integer.parseInt(deviceStatus));
                        notifyItemChanged(i,deviceVO);
                    }
                }
            }
        }

    }

    public void updatePanel(String id, String deviceStatus) {

        RoomVO item = new RoomVO();
        item.setRoomId(id);

        for (int i = 0; i < roomVOList.size(); i++) {
            RoomVO roomVO = roomVOList.get(i);
            ArrayList<PanelVO> panelVOArrayList = roomVO.getPanelList();
            for (PanelVO panelVO : panelVOArrayList) {
                if(panelVO.getPanelId().equalsIgnoreCase(id)){
                    panelVO.setPanel_status(Integer.parseInt(deviceStatus));
                    notifyItemChanged(i,panelVO);
                }
            }
        }

    }
    public void updateRoom(String id,String deviceStatus) {
        //  Log.d("roomStatus "  , id + " roomStatus updatePanel " + deviceStatus);
        RoomVO item = new RoomVO();
        item.setRoomId(id);
        //  item.setRoom_status(Integer.parseInt(deviceStatus));
        for (int i = 0; i < roomVOList.size(); i++) {
            RoomVO roomVO = roomVOList.get(i);
            if(roomVO.getRoomId().equalsIgnoreCase(id)){
                roomVO.setRoom_status(Integer.parseInt(deviceStatus));
                notifyItemChanged(i,roomVO);
            }
        }

    }

    //for room section has expanded or not
    public void addSectionList(ArrayList<RoomVO> roomList){

        try{
            for (Map.Entry<RoomVO, Integer> entry : ListUtils.hashMapRoom.entrySet()) {
                RoomVO key = entry.getKey();
                int position = entry.getValue();
                roomList.set(position,key).setExpanded(true);
            }
        }catch (Exception ex){ ex.printStackTrace(); }
        this.roomVOList = roomList;
    }



    @Override
    public void onBindViewHolder(RoomParentHolder holder, final int position) {
        final RoomVO roomVO = roomVOList.get(holder.getAdapterPosition());
        holder.text_section.setText(roomVO.getRoomName());

        if(roomVO.getRoom_status()==1 ){ //|| position%2 ==1
            // holder.text_section_on_off.setText("ON");
            holder.text_section_on_off.setBackground(context.getResources().getDrawable(R.drawable.room_on));
            holder.text_section_on_off.setTextColor(context.getResources().getColor(R.color.automation_white));
        }
        else{
            //  holder.text_section_on_off.setText("OFF");
            holder.text_section_on_off.setBackground(context.getResources().getDrawable(R.drawable.room_off));
            holder.text_section_on_off.setTextColor(context.getResources().getColor(R.color.sky_blue));
        }


        holder.ll_root_view_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomVO.setExpanded(!roomVO.isExpanded());
                notifyItemChanged(position,roomVO);
                //  ListUtils.hashMapRoom.put()
                ListUtils.hashMapRoom.put(roomVO,position);
                mItemClickListener.itemClicked(roomVO,"expandclick");
            }
        });

        holder.text_section_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.itemClicked(roomVO,"onoffclick");
            }
        });

        holder.text_section_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.itemClicked(roomVO,"editclick");
            }
        });

        holder.text_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomVO.setExpanded(!roomVO.isExpanded());
                notifyItemChanged(position,roomVO);
              //  ListUtils.hashMapRoom.put()
                ListUtils.hashMapRoom.put(roomVO,position);
                mItemClickListener.itemClicked(roomVO,"expandclick");
            }
        });

        if(roomVO.isExpanded()){
            holder.sectionToggleButton.setImageResource(R.drawable.arrow_up2);
        }else{
            holder.sectionToggleButton.setImageResource(R.drawable.arrow_down2);
        }
        //holder.sectionToggleButton.setChecked(roomVO.isExpanded());

        if(roomVO.getRoomId().equalsIgnoreCase("camera")){
            holder.text_section_edit.setVisibility(View.GONE);
            holder.text_section_on_off.setVisibility(View.GONE);
        }
        else{
            holder.text_section_on_off.setVisibility(View.VISIBLE);

            if(roomVO.isExpanded()){
                holder.text_section_edit.setVisibility(View.VISIBLE);
            }
            else{
                holder.text_section_edit.setVisibility(View.GONE);
            }
        }

        if(roomVO.isExpanded()){
            ArrayList<PanelVO> panelVOList = roomVO.getPanelList();
            if(panelVOList!=null){

                holder.recycler_panel_listview.setVisibility(View.VISIBLE);
                roomPanelAdapter = new RoomPanelAdapter(panelVOList,this);
                holder.recycler_panel_listview.setAdapter(roomPanelAdapter);
            }
        }else{
            holder.recycler_panel_listview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return roomVOList.size();
    }


    class RoomParentHolder extends RecyclerView.ViewHolder{

        TextView text_section;
        TextView text_section_edit;
        TextView text_section_on_off;
        ImageView sectionToggleButton;
        LinearLayout ll_root_view_section;
        RecyclerView recycler_panel_listview;

        RoomParentHolder(View itemView) {
            super(itemView);
            text_section = (TextView)itemView.findViewById(R.id.text_section);
            text_section_edit = (TextView)itemView.findViewById(R.id.text_section_edit);
            text_section_on_off = (TextView)itemView.findViewById(R.id.text_section_on_off);
            sectionToggleButton = (ImageView)itemView.findViewById(R.id.toggle_button_section);
            ll_root_view_section = (LinearLayout)itemView.findViewById(R.id.ll_root_view_section);

            recycler_panel_listview = (RecyclerView)itemView.findViewById(R.id.recycler_panel_listview);
            recycler_panel_listview.setLayoutManager(new WrapContentGriLayoutManager(context,1));

        }
    }
}
