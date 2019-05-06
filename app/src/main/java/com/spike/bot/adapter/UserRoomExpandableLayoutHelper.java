package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.customview.recycle.MoodStateChangeListener;
import com.spike.bot.listener.NotifityData;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Sagar on 13/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserRoomExpandableLayoutHelper implements MoodStateChangeListener, NotifityData {

    //data list
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();

    //section map
    //TODO : look for a way to avoid this
    //  private HashMap<String, MoodVO> mSectionMap = new HashMap<String, MoodVO>();

    //adapter
    private UserSubRoomAdapter mSectionedExpandableGridAdapter;

    //recycler view
    RecyclerView mRecyclerView;

    public UserRoomExpandableLayoutHelper(Context context, RecyclerView recyclerView, ItemClickMoodListener itemClickListener, int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new UserSubRoomAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, this,this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);

        mRecyclerView = recyclerView;
    }

    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll

        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void setClickable(boolean isClick){
        mSectionedExpandableGridAdapter.setClickabl(isClick);
    }

    //reload the row item in recycle
    public void reloadDeviceList(Object obj){
        int position = mDataArrayList.indexOf(obj);

        if(position!=-1) {
            mDataArrayList.set(position,obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);
        }


    }
    /*
    public void addSection(MoodVO section, ArrayList<DeviceVO> items) {
        mSectionMap.put(section.getRoomName(), section);
        mSectionDataMap.put(section, items);
    }*/
    public void addSectionList(ArrayList<RoomVO> roomList){
        mDataArrayList.clear();
        mSectionDataMap.clear();
        for(int i=0;i<roomList.size();i++){
            addSection(roomList.get(i));
        }
    }
    public void addSection(RoomVO section) {
        if (this.section!=null && this.section.equals(section)){

        }

        for(RoomVO moodVO : ListUtils.arrayListMood){
            if(moodVO.isExpanded && section.getRoomId().equalsIgnoreCase(moodVO.getRoomId())){
                section.isExpanded = true;
                section.setExpanded(true);
            }
        }

        ArrayList<PanelVO> panelVOList = section.getPanelList();

        boolean isModeStatusOpen = false;

        for(PanelVO panelVO : panelVOList){
            ArrayList<DeviceVO> deviceList = panelVO.getDeviceList();

            for(DeviceVO deviceVO : deviceList){
                if(deviceVO.getDeviceStatus() == 1){
                    isModeStatusOpen = true;
                }
            }
        }

        section.setRoom_status(isModeStatusOpen ? 1 : 0);

        mSectionDataMap.put(section, section.getPanelList());
    }
    private void generateDataList () {
        mDataArrayList.clear();
        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key;

            //  mDataArrayList.add((key = entry.getKey()));
            mDataArrayList.add((key = entry.getKey()));
            if (key.isExpanded ) {
                //key.isExpanded = true;
                //mDataArrayList.add(new PanelVO("Panel1"));
                //mDataArrayList.addAll(entry.getValue());
                ArrayList<PanelVO> panelList = entry.getValue();

                for(int i=0;i<panelList.size();i++){
                    //add panel
                    mDataArrayList.add(panelList.get(i));
                    //add all device switch
                    mDataArrayList.addAll(panelList.get(i).getDeviceList());
                }
            }
        }
    }
    public static RoomVO section;
    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        this.section = section;
        section.isExpanded = isOpen;

        if(!isOpen){

            if(ListUtils.arrayListMood.size()>0){

                for(int i=0;i<ListUtils.arrayListMood.size();i++){
                    if(section.getRoomId().equalsIgnoreCase(ListUtils.arrayListMood.get(i).getRoomId())){
                        section.isExpanded = false;
                        section.setExpanded(false);
                        ListUtils.arrayListMood.set(i,section);
                    }
                }
            }

        }else{
            ListUtils.arrayListMood.add(section);
        }

        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;
            ListUtils.arrayListMood.add(section);
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyData() {
        notifyDataSetChanged();
    }

}
