package com.spike.bot.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.customview.recycle.MoodStateChangeListener;
import com.spike.bot.listener.NotifityData;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.NotificationListRes;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kaushal on 2/23/2016.
 */
public class MoodExpandableLayoutHelper implements MoodStateChangeListener , NotifityData {

    //data list
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();
    //adapter
    private MoodExpandableGridAdapter mSectionedExpandableGridAdapter;

    //recycler view
    RecyclerView mRecyclerView;

    public MoodExpandableLayoutHelper(Context context, RecyclerView recyclerView, ItemClickMoodListener itemClickListener, int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new MoodExpandableGridAdapter(context, mDataArrayList, gridLayoutManager, itemClickListener, this,this);
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

    public void updateDeviceItem(String device_type,String deviceId,String deviceStatus ,int device_sub_status) {

        DeviceVO item = new DeviceVO();
        item.setDeviceId(deviceId);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            ArrayList<PanelVO> panelsList = entry.getValue();

            for(PanelVO panel : panelsList){
                ArrayList<DeviceVO> devicesList = panel.getDeviceList();
                for(int i=0;i<devicesList.size();i++) {
//                    devicesList.get(i).setIs_locked(is_locked);
//                    if(devicesList.get(i).getDeviceType().equalsIgnoreCase("2")){
//                        ChatApplication.logDisplay("mood is tt"+devicesList.get(i).getDeviceId()+" "+deviceId);
//                        ChatApplication.logDisplay("mood is "+devicesList.get(i).getModuleId()+" "+moduleId);
//
//                        if(devicesList.get(i).getDeviceId().equalsIgnoreCase(deviceId) &&
//                                devicesList.get(i).getModuleId().equalsIgnoreCase(moduleId)){
//                            ChatApplication.logDisplay("mood is tt treue");
//
//                            devicesList.get(i).setRemote_status(Integer.parseInt(deviceStatus) == 1 ? "ON" : "OFF");
//                            devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));
//                        }
//
//                    }else {

                        if (devicesList.get(i).getDeviceId().equalsIgnoreCase(deviceId)) {
                            ChatApplication.logDisplay("device is same "+deviceId);
                            devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));
                            reloadDeviceList(devicesList.get(i));
//                            break;
                        }
//                    }
                }
            }
        }
//        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }

    public void updateFanDevice(String deviceId, String deviceStatus) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDevice_sub_status(deviceStatus);
                        reloadDeviceList(mDataArrayList.get(k));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        }
    }



    public void updateDeviceBlaster(String deviceId, String deviceStatus) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDeviceStatus(Integer.parseInt(deviceStatus));
                        reloadDeviceList(mDataArrayList.get(k));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
//                        break;
                    }

                }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }


    public void updateItem(String moduleId,String deviceId,String deviceStatus,int is_locked) {

        DeviceVO item = new DeviceVO();
        item.setModuleId(moduleId);
        item.setDeviceId(deviceId);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            ArrayList<PanelVO> panelsList = entry.getValue();

            for(PanelVO panel : panelsList){
                ArrayList<DeviceVO> devicesList = panel.getDeviceList();
                    for(int i=0;i<devicesList.size();i++) {
                        devicesList.get(i).setIs_locked(is_locked);
                        if(devicesList.get(i).getDeviceType().equalsIgnoreCase("2")){
                            ChatApplication.logDisplay("mood is tt"+devicesList.get(i).getDeviceId()+" "+deviceId);
                            ChatApplication.logDisplay("mood is "+devicesList.get(i).getModuleId()+" "+moduleId);

                            if(devicesList.get(i).getDeviceId().equalsIgnoreCase(deviceId) &&
                                    devicesList.get(i).getModuleId().equalsIgnoreCase(moduleId)){
                                ChatApplication.logDisplay("mood is tt treue");

                                devicesList.get(i).setRemote_status(Integer.parseInt(deviceStatus) == 1 ? "ON" : "OFF");
                                devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));
                            }

                        }else {

                            if (devicesList.get(i).getModuleId().equalsIgnoreCase(moduleId)
                                    && devicesList.get(i).getDeviceId().equalsIgnoreCase(deviceId)) {

                                devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));

                                reloadDeviceList(devicesList.get(i));
                            }
                        }
                    }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }

    /*--update panel--*/
    public void updatePanel(String id,String deviceStatus) {

        PanelVO item = new PanelVO();
        item.setPanelId(id);
        item.setPanel_status(Integer.parseInt(deviceStatus));

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            ArrayList<PanelVO> panelsList = entry.getValue();
            if(panelsList.contains(item)){

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

    /*--updateMood--*/
    public void updateMood(String room_id,String mood_status) {
//        MoodVO item = new MoodVO();
//        item.setMood_id(room_id);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey() ;
            ChatApplication.logDisplay("status update room mood id " + key.getRoomId()+" , "+room_id);
            if(room_id.equals(key.getRoomId())){
                key.setRoom_status(Integer.parseInt(mood_status));
                reloadDeviceList(key);
//                break;
            }
        }
    }
    //reload the row item in recycle
    public void reloadDeviceList(Object obj){
        int position = mDataArrayList.indexOf(obj);

        if(position!=-1) {
            mDataArrayList.set(position,obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);
        }
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

            mDataArrayList.add((key = entry.getKey()));
            if (key.isExpanded ) {
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

    // variable to track event time
    private long mLastClickTime = 0;
    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        this.section = section;
        section.setExpanded(isOpen);
        if(!isOpen){
            if(ListUtils.arrayListMood.size()>0){
                for(int i=0;i<ListUtils.arrayListMood.size();i++){
                    if(section.getRoomId().equalsIgnoreCase(ListUtils.arrayListMood.get(i).getRoomId())){
//                        section.isExpanded = false;
                        section.setExpanded(false);
                        ListUtils.arrayListMood.set(i,section);
                    }
                }
            }

        }else{
            ListUtils.arrayListMood.add(section);
        }

        if (!mRecyclerView.isComputingLayout()) {
//            section.isExpanded = isOpen;
//            ListUtils.arrayListMood.add(section);
//            notifyDataSetChanged();
            section.setExpanded(isOpen);

            for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
                RoomVO key = entry.getKey();
                if(key.equals(section)) {
                    key.setExpanded(isOpen);
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyData() {
        notifyDataSetChanged();
    }
}
