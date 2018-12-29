package com.spike.bot.adapter.mood;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kaushal on 2/23/2016.
 */
public class MoodSectionedExpandableLayoutHelper implements SectionStateChangeListener {

    //data list
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();

    //section map
    //TODO : look for a way to avoid this
  //  private HashMap<String, RoomVO> mSectionMap = new HashMap<String, RoomVO>();

    //adapter
    private MoodSectionedExpandableGridAdapter mSectionedExpandableGridAdapter;

    //recycler view
    RecyclerView mRecyclerView;

    public MoodSectionedExpandableLayoutHelper(Context context, RecyclerView recyclerView, ItemClickListener itemClickListener,
                                               int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new MoodSectionedExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);

        mRecyclerView = recyclerView;
    }
    public void setCameraClick(MoodSectionedExpandableGridAdapter.CameraClickListener mCameraClickListener){

        mSectionedExpandableGridAdapter.setCameraClickListener(mCameraClickListener);
    }


    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }
    public void updateItem(String moduleId,String deviceId,String deviceStatus) {

        DeviceVO item = new DeviceVO();
        item.setModuleId(moduleId);
        item.setDeviceId(deviceId);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey() ;
            //mDataArrayList.add((key = entry.getKey()));
            //if (key.isExpanded)
            ArrayList<PanelVO> panelsList = entry.getValue();

            for(PanelVO panel : panelsList){

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                if(devicesList.contains(item)){
                //    Log.d("","devicesList.contains " + key + "    size =  " + devicesList.size() );
                    for(int i=0;i<devicesList.size();i++) {
                        if (devicesList.get(i).equals(item)) {
                            devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));
                            /*int position = mDataArrayList.indexOf(devicesList.get(i));
                            Log.d("","devicesList  notifyItemChanged  position =  " + position);

                            if(position!=-1) {
                                mDataArrayList.set(position,devicesList.get(i));
                                mSectionedExpandableGridAdapter.notifyItemChanged(position);
                            }*/
                            reloadDeviceList(devicesList.get(i));
                            // break;
                        }
                    }
                }
            }
                //mDataArrayList.addAll(entry.getValue());
        }
/*        ArrayList<DeviceVO> devicesList = mSectionDataMap.get(mSectionMap.get(sectionName));
        for(int i=0;i<devicesList.size();i++){

        }
        mSectionDataMap.get(mSectionMap.get(sectionName)).add(item);
        */
    }
    public void updatePanel(String id,String deviceStatus) {

        PanelVO item = new PanelVO();
        item.setPanelId(id);
        item.setPanel_status(Integer.parseInt(deviceStatus));

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey() ;
           // Log.d("","panelsList contains " + key + "    size =  "  );
            //mDataArrayList.add((key = entry.getKey()));
            //if (key.isExpanded)
            ArrayList<PanelVO> panelsList = entry.getValue();
            if(panelsList.contains(item)){
                Log.d("onOffPanelEvent","panelsList contains " + key + "    status =  " + deviceStatus );

                for(int i=0;i<panelsList.size();i++) {
                    if (panelsList.get(i).equals(item)) {
                        panelsList.get(i).setPanel_status(Integer.parseInt(deviceStatus));
                        // break;
                        reloadDeviceList(panelsList.get(i));
                    }
                }
            }
        }
    }
    public void updateRoom(String id,String deviceStatus) {
      //  Log.d("roomStatus "  , id + " roomStatus updatePanel " + deviceStatus);
        RoomVO item = new RoomVO();
        item.setRoomId(id);
      //  item.setRoom_status(Integer.parseInt(deviceStatus));

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey() ;
            //mDataArrayList.add((key = entry.getKey()));
            //if (key.isExpanded)
            //if(key.equals(item)){
            if(id.equals( key.getRoomId() )){
             //   Log.d("roomStatus "  , id + " id updatePanel roomid " + key.getRoomId() );
                key.setRoom_status(Integer.parseInt(deviceStatus));
                reloadDeviceList(key);
            }
        }
    }
    //reload the row item in recycle
    public void reloadDeviceList(Object obj){
        int position = mDataArrayList.indexOf(obj);
       // Log.d("","reloadDeviceList  notifyItemChanged  position =  " + position);

        if(position!=-1) {
            mDataArrayList.set(position,obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);

            //added by sagar update panel list status issue
            /*for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
                RoomVO key = entry.getKey() ;
                ArrayList<PanelVO> panelsList = entry.getValue();
                for(PanelVO panelVO : panelsList){
                    boolean isActivePanel = false;
                    ArrayList<DeviceVO>  deviceVOArrayList = panelVO.getDeviceList();
                    for(DeviceVO deviceVO : deviceVOArrayList){
                        if(deviceVO.getDeviceStatus() == 1){
                            isActivePanel = true;
                        }
                    }
                    panelVO.setPanel_status(isActivePanel ? 1 : 0 );
                    synchronized (mSectionDataMap) {
                        mSectionDataMap.notify();
                    }
                }
            }*/
        }
    }
    /*
    public void addSection(RoomVO section, ArrayList<DeviceVO> items) {
        mSectionMap.put(section.getRoomName(), section);
        mSectionDataMap.put(section, items);
    }*/


    public void addSectionList(ArrayList<RoomVO> roomList){
       // mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
       // mDataArrayList = new ArrayList<Object>();
        mSectionDataMap.clear();
        mDataArrayList.clear();

        for(int i=0;i<roomList.size();i++){
            addSection(roomList.get(i));
        }
    }

    public static RoomVO section;
    public void addSection(RoomVO section) {
        //mSectionMap.put(section.getRoomName(), section);
        if (this.section!=null && this.section.equals(section)){


        }

        for(RoomVO roomVO : ListUtils.arrayListRoom){
            if(roomVO.isExpanded && roomVO.getRoomId().equalsIgnoreCase(section.getRoomId())){
                section.isExpanded = true;
            }
        }

        mSectionDataMap.put(section, section.getPanelList());
    }
    public void addCameraList(ArrayList<CameraVO> cameraList) {
        //mSectionMap.put(section.getRoomName(), section);
        RoomVO section = new RoomVO();
        section.setRoomName("Camera");
        section.setRoomId("Camera");

        ArrayList<PanelVO> panelList = new ArrayList<>();
        PanelVO panel = new PanelVO();
        panel.setPanelName("Devices");
        panel.setType("camera");
        panel.setCameraList(cameraList);

        panelList.add(panel);

        section.setPanelList(panelList);
        mSectionDataMap.put(section, section.getPanelList());
    }
    public void addPanel(RoomVO section) {
        //mSectionMap.put(section.getRoomName(), section);
        mSectionDataMap.put(section, section.getPanelList());
    }
  /*  public void addItem(RoomVO section, DeviceVO item) {
        mSectionDataMap.get(mSectionMap.get(section.getRoomName())).add(item);
    }

    public void removeItem(RoomVO section, DeviceVO item) {
        mSectionDataMap.get(mSectionMap.get(section.getRoomName())).remove(item);
    }

    public void removeSection(RoomVO section) {
        mSectionDataMap.remove(mSectionMap.get(section.getRoomName()));
        mSectionMap.remove(section);
    }*/

    private void generateDataList () {
     //   Log.d("CallAPI"," generateDataList =  " + mSectionDataMap.size());
        mDataArrayList.clear();

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey();
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
                    if(panelList.get(i).getType().equalsIgnoreCase("")){
                        //Log.d("isExpanded","generateDataList if isKey : " + key.isExpanded());
                        mDataArrayList.addAll(panelList.get(i).getDeviceList());
                    }else{
                        //Log.d("isExpanded","generateDataList else isKey : " + key.isExpanded());
                        mDataArrayList.addAll(panelList.get(i).getCameraList());
                    }
                }
            }
        }
    }

    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
       //  Log.d("isOpen","Map Entry isKey : " +isOpen);
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

            for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
                RoomVO key = entry.getKey();
                if(key.equals(section)) {
                    key.setExpanded(isOpen);
                }
            }
            notifyDataSetChanged();
        }
    }
}
