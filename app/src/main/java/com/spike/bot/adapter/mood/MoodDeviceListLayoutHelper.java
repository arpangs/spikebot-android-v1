package com.spike.bot.adapter.mood;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kaushal on 2/23/2016.
 */
public class MoodDeviceListLayoutHelper implements SectionStateChangeListener {

    //data list
    ArrayList<Object> mDataArrayList  = new ArrayList<Object>();
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private MoodDeviceListExpandableGridAdapter mSectionedExpandableGridAdapter;
    private Context context;
    RecyclerView mRecyclerView;


    public MoodDeviceListLayoutHelper(Context ctx, RecyclerView recyclerView, ItemClickListener itemClickListener, int gridSpanCount, boolean isMoodAdapter) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new MoodDeviceListExpandableGridAdapter(context, mDataArrayList, gridLayoutManager, itemClickListener, this,isMoodAdapter);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
        this.context = ctx;
        mRecyclerView = recyclerView;
    }
    public String getSelectedItemIds(){
        String roomIds = mSectionedExpandableGridAdapter.getSelectedItemIds();
        return roomIds;
    }
    public ArrayList<DeviceVO> getSelectedItemList(){
        ArrayList<DeviceVO> roomIds = mSectionedExpandableGridAdapter.getSelectedItemList();
        return roomIds;
    }


    String room_device_id="";

    /*adapter refresh */
    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }
    public void addSectionList(ArrayList<RoomVO> roomList){
        mDataArrayList.clear();
        mSectionDataMap.clear();
        for(int i=0;i<roomList.size();i++){
            addSection(roomList.get(i));
        }
    }
    public void addSection(RoomVO section) {
        if (this.section!=null && this.section.equals(section)){
            section.isExpanded = true;
        }
        mSectionDataMap.put(section, section.getPanelList());
        generateDataList();
    }
    ArrayList<DeviceVO> roomDevices = new ArrayList<>();

    private void generateDataList () {
        roomDevices = getSelectedItemList();
        mDataArrayList.clear();
        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key;
            mDataArrayList.add((key = entry.getKey()));
            if (key.isExpanded) {
                ArrayList<PanelVO> panelList = entry.getValue();

                for(int i=0;i<panelList.size();i++){
                    //add panel
                    mDataArrayList.add(panelList.get(i));
                    //add all device switch

                    if(panelList.get(i).isSensorPanel()){
                        /*for only remote add*/
                        ArrayList<DeviceVO> deviceVOArrayList=new ArrayList<>();
                        for(int k=0; k<panelList.get(i).getDeviceList().size(); k++){
                            if(panelList.get(i).getDeviceList().get(k).getDeviceType().equalsIgnoreCase("remote")){
                                deviceVOArrayList.add(panelList.get(i).getDeviceList().get(k));
                            }
                        }
                        mDataArrayList.addAll(deviceVOArrayList);
                    }else {
                        mDataArrayList.addAll(panelList.get(i).getDeviceList());
                    }
                }
            }
        }
        mSectionedExpandableGridAdapter.setSelection(room_device_id);
    }

    // MoodVO section;

    public static RoomVO section;

   /* @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;
            notifyDataSetChanged();
        }
    }*/

    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        this.section = section;
        ListUtils.sectionRoom = section;
        ListUtils.sectionRoom.setExpanded(isOpen);

        section.isExpanded = isOpen;


        /*if(!isOpen){

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
        }*/



        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;

            for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
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

    @Override
    public void onSectionStateChanged(PanelVO section, boolean isOpen) {

    }
}
