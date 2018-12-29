package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.SelectDevicesListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kaushal on 2/23/2016.
 */
public class DeviceListLayoutHelper implements SectionStateChangeListener {

    //data list
    ArrayList<Object> mDataArrayList  = new ArrayList<Object>();
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    //section map
    //TODO : look for a way to avoid this
  //  private HashMap<String, MoodVO> mSectionMap = new HashMap<String, MoodVO>();

    //adapter
    private DeviceListExpandableGridAdapter mSectionedExpandableGridAdapter;

    private Context context;
    //recycler view
    RecyclerView mRecyclerView;
    SelectDevicesListener selectDevicesListener;


    public DeviceListLayoutHelper(Context ctx, RecyclerView recyclerView, ItemClickListener itemClickListener, int gridSpanCount,boolean isMoodAdapter,SelectDevicesListener selectDevicesListener) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new DeviceListExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, this,isMoodAdapter,selectDevicesListener);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
        this.context = ctx;
        mRecyclerView = recyclerView;
        this.selectDevicesListener = selectDevicesListener;
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
    public void setSelection(String room_device_id){
        this.room_device_id = room_device_id;

        mSectionedExpandableGridAdapter.setSelection(room_device_id);
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }
    public void setSelectionAll(){
        mSectionedExpandableGridAdapter.setSelectionAllDevices();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }


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
    private void generateDataList () {
        mDataArrayList.clear();
        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key;
            //  mDataArrayList.add((key = entry.getKey()));
            mDataArrayList.add((key = entry.getKey()));
            if (key.isExpanded) {
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
        mSectionedExpandableGridAdapter.setSelection(room_device_id);
    }
    MoodVO section;
    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
       // Log.d("","reloadDeviceList  onSectionStateChanged  onSectionStateChanged =  " + isOpen + "  computing "+ !mRecyclerView.isComputingLayout());
        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;
            notifyDataSetChanged();
        }
    }
}
