package com.spike.bot.adapter.beacon;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.adapter.mood.MoodDeviceListExpandableGridAdapter;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeaconDeviceListLayoutHelper implements SectionStateChangeListener {

    //data list
    ArrayList<Object> mDataArrayList = new ArrayList<Object>();
    private LinkedHashMap<RoomVO, ArrayList<DeviceVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<DeviceVO>>();
    private BeaconDeviceListExpandableGridAdapter mSectionedExpandableGridAdapter;
    private Context context;
    RecyclerView mRecyclerView;

    public BeaconDeviceListLayoutHelper(Context ctx, RecyclerView recyclerView,  int gridSpanCount, boolean isBeaconAdapter) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new BeaconDeviceListExpandableGridAdapter(context, mDataArrayList, gridLayoutManager, this, isBeaconAdapter);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
        this.context = ctx;
        mRecyclerView = recyclerView;
    }

    public String getSelectedItemIds() {
        String roomIds = mSectionedExpandableGridAdapter.getSelectedItemIds();
        return roomIds;
    }

    public ArrayList<DeviceVO> getSelectedItemList() {
        ArrayList<DeviceVO> roomIds = mSectionedExpandableGridAdapter.getSelectedItemList();
        return roomIds;
    }

    String room_device_id = "";

    /*adapter refresh */
    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void addSectionList(ArrayList<RoomVO> roomList) {
        mDataArrayList.clear();
        mSectionDataMap.clear();
        for (int i = 0; i < roomList.size(); i++) {
            addSection(roomList.get(i));
        }
    }
    public void addSection(RoomVO section) {
        if (this.section!=null && this.section.equals(section)){
            section.isExpanded = true;
        }
        mSectionDataMap.put(section, section.getDeviceList());
        generateDataList();
    }

    ArrayList<DeviceVO> roomDevices = new ArrayList<>();

    private void generateDataList() {
        roomDevices = getSelectedItemList();
        mDataArrayList.clear();
        for (Map.Entry<RoomVO, ArrayList<DeviceVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key;
            mDataArrayList.add((key = entry.getKey()));
            if (key.isExpanded) {
                ArrayList<DeviceVO> panelList = entry.getValue();

                for (int i = 0; i < panelList.size(); i++) {
                    //add all device switch
                    mDataArrayList.add(panelList.get(i));

                }
            }
        }
        mSectionedExpandableGridAdapter.setSelection(room_device_id);
    }

    public static RoomVO section;

    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        this.section = section;
        ListUtils.sectionRoom = section;
        ListUtils.sectionRoom.setExpanded(isOpen);

        section.isExpanded = isOpen;


        if(!isOpen){

            if(ListUtils.arrayListRoom.size()>0){

                for(int i=0;i<ListUtils.arrayListRoom.size();i++){
                    if(section.getRoomId().equalsIgnoreCase(ListUtils.arrayListRoom.get(i).getRoomId())){
                        section.isExpanded = false;
                        section.setExpanded(false);
                        ListUtils.arrayListRoom.set(i,section);
                    }
                }
            }

        }else{
            ListUtils.arrayListRoom.add(section);
        }



        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;

            for (Map.Entry<RoomVO, ArrayList<DeviceVO>> entry : mSectionDataMap.entrySet()) {
                RoomVO key = entry.getKey();
                if(key.equals(section)) {
                    key.setExpanded(isOpen);
                }else{
                    key.setExpanded(false);
                }
            }
            notifyDataSetChanged();
        }
    }
}
