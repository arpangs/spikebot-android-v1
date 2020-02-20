package com.spike.bot.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Kaushal on 2/23/2016.
 */
public class SectionedExpandableLayoutHelper implements SectionStateChangeListener, OnSmoothScrollList {

    //data list
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMapTemp = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();

    //section map
    //TODO : look for a way to avoid this
    //  private HashMap<String, RoomVO> mSectionMap = new HashMap<String, RoomVO>();

    //adapter
    private SectionedExpandableGridAdapter mSectionedExpandableGridAdapter;
    private OnSmoothScrollList onSmoothScrollList;

    public Context context;
    //recycler view
    RecyclerView mRecyclerView;

    public SectionedExpandableLayoutHelper(final Context context, RecyclerView recyclerView, ItemClickListener itemClickListener, OnSmoothScrollList onSmoothScrollList, TempClickListener tempClickListener,
                                           int gridSpanCount) {

        this.context = context;
        this.onSmoothScrollList = onSmoothScrollList;
        //setting the recycler view
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, onSmoothScrollList, tempClickListener, this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
        recyclerView.setHasFixedSize(true);
        mRecyclerView = recyclerView;


    }

    public void setCameraClick(SectionedExpandableGridAdapter.CameraClickListener mCameraClickListener) {

        mSectionedExpandableGridAdapter.setCameraClickListener(mCameraClickListener);
    }

    public void setjetsonClick(SectionedExpandableGridAdapter.JetsonClickListener jestonClickListener){
        mSectionedExpandableGridAdapter.setJetsonClickListener(jestonClickListener);
    }

    public void setClickable(boolean clickable) {
        mSectionedExpandableGridAdapter.setClickable(clickable);
    }

    public void clearData() {
        mDataArrayList.clear();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void updateFanDevice(String deviceId, String deviceStatus) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDevice_sub_status(deviceStatus);
                        reloadDeviceList(((DeviceVO) mDataArrayList.get(k)));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        }
    }


    public void updateDeviceItem(String device_type, String deviceId, String deviceStatus, int device_sub_status) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDeviceStatus(Integer.parseInt(deviceStatus));
                        ((DeviceVO) mDataArrayList.get(k)).setDevice_sub_status(String.valueOf(device_sub_status));

                        reloadDeviceList(((DeviceVO) mDataArrayList.get(k)));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        break;
                    }

                }
            }
        }
//        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void updateDeviceBlaster(String deviceId, String deviceStatus) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDeviceStatus(Integer.parseInt(deviceStatus));
                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        break;
                    }

                }
            }
        }
//        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void updateModuleActiveItem(String module_id, String module_status) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getModuleId().equals(module_id)) {
                        ((DeviceVO) mDataArrayList.get(k)).setIsActive(module_status.equals("y")?1:-1);
                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        }
//        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }

    public void updatePanel(String id, String deviceStatus, String room_panel_id) {
        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof PanelVO){
                PanelVO deviceVO1 = (PanelVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getPanelId().equals(id)) {
                        ((PanelVO) mDataArrayList.get(k)).setPanel_status(Integer.parseInt(deviceStatus));
                        reloadDeviceList(((PanelVO) mDataArrayList.get(k)));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
//                        break;
                    }

                }
            }
        }
    }

    public String getPanelName(String roomPanelId) {

        String strName = "";
        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMapTemp.entrySet()) {
            RoomVO key = entry.getKey();

            ArrayList<PanelVO> panelsList = entry.getValue();
            for (int i = 0; i < panelsList.size(); i++) {
                if (panelsList.get(i).getRoom_panel_id().equals(roomPanelId)) {
                    //ChatApplication.logDisplay("panel status panelStatus update "+panelId);
                    strName = panelsList.get(i).getPanelName();
                    break;
                }
            }
        }

        return strName;
    }

    public void updateRoom(String id, String deviceStatus) {
        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof RoomVO){
                RoomVO deviceVO1 = (RoomVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getRoomId().equals(id)) {
                        ChatApplication.logDisplay("room refreesh is ");
                        ((RoomVO) mDataArrayList.get(k)).setRoom_status(Integer.parseInt(deviceStatus));
                        reloadDeviceList(((RoomVO) mDataArrayList.get(k)));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
//                        break;
                    }

                }
            }
        }
    }

    /**
     * @param room_order
     * @param door_sensor_id
     * @param door_sensor_status
     */
    public void updateDoorStatus(String room_order, String door_sensor_id, String door_sensor_status, String door_lock_status) {

        DeviceVO item = new DeviceVO();
        item.setSensor_id(door_sensor_id);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                for (int i = 0; i < devicesList.size(); i++) {

                    if (!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("door")) {

                        if (devicesList.get(i).getSensor_id().equalsIgnoreCase(door_sensor_id)) {
                            if (!TextUtils.isEmpty(door_sensor_status) && !door_sensor_status.equals("null")) {
                                devicesList.get(i).setDoor_sensor_status(door_sensor_status);
                            }

                            ChatApplication.logDisplay("door_lock_status is " + door_lock_status);
                            if (!TextUtils.isEmpty(door_lock_status) && !door_lock_status.equals("null")) {
                                devicesList.get(i).setDoor_lock_status(Integer.parseInt(door_lock_status));
                            }
                        }
                    }
                }

            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }



    public void updateBadgeCount(String deviceId, String counter) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof DeviceVO){
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("room refreesh is ");
                        ((DeviceVO) mDataArrayList.get(k)).setIs_unread(counter);
                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        break;
                    }

                }
            }
        }
    }

    public void updateBadgeCountnew(String roomid, String counter) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if(mDataArrayList.get(k) instanceof RoomVO){
                RoomVO roomVO1 = (RoomVO) mDataArrayList.get(k);
                if (roomVO1 != null) {
                    if (roomVO1.getRoomId().equals(roomid)) {
                        ChatApplication.logDisplay("room refreesh is ");
                        ((RoomVO) mDataArrayList.get(k)).setIs_unread(counter);
                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        break;
                    }

                }
            }
        }
    }


    public void updateBadgeCount1(String sensor_type, String sensor_unread, String module_id, String room_id, String room_unread) {

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey();

            if (key.getRoomId().equalsIgnoreCase(room_id)) {
                key.setIs_unread(room_unread);
            }

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                for (int i = 0; i < devicesList.size(); i++) {

                    if (sensor_type.equalsIgnoreCase("tempsensor")) {

                        if (!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("tempsensor")) {
                            if (module_id.equalsIgnoreCase(devicesList.get(i).getModuleId())) {
                                devicesList.get(i).setIs_unread(sensor_unread);
                            }
                        }
                    } else if (sensor_type.equalsIgnoreCase("door")) {

                        if (!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("door")) {
                            if (module_id.equalsIgnoreCase(devicesList.get(i).getModuleId())) {
                                devicesList.get(i).setIs_unread(sensor_unread);
                            }
                        }
                    }
                }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    //reload the row item in recycle
    public void reloadDeviceList(Object obj) {
        int position = mDataArrayList.indexOf(obj);

        if (position != -1) {
            mDataArrayList.set(position, obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);
//               mSectionedExpandableGridAdapter.notifyDataSetChanged();

        }
    }


    public void addSectionList(ArrayList<RoomVO> roomList) {
        // mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
        // mDataArrayList = new ArrayList<Object>();
        mSectionDataMap.clear();
        mSectionDataMapTemp.clear();
        mDataArrayList.clear();

        for (int i = 0; i < roomList.size(); i++) {
            addSection(roomList.get(i));
        }
    }

    public static RoomVO section;

    public void addSection(RoomVO section) {
        //mSectionMap.put(section.getRoomName(), section);
        if (this.section != null && this.section.equals(section)) {

        }
        for (RoomVO roomVO : ListUtils.arrayListRoom) {
//            ChatApplication.logDisplay("room id is same check "+roomVO.getRoomName()+"  "+roomVO.isExpanded());
            if (roomVO.isExpanded()==true && roomVO.getRoomId().equalsIgnoreCase(section.getRoomId())) {
//                section.isExpanded = true;
                section.setExpanded(true);
            }
        }

        mSectionDataMap.put(section, section.getPanelList());
        mSectionDataMapTemp.put(section, section.getPanelList());
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


    private void generateDataList() {

        mDataArrayList.clear();

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
//            RoomVO key = entry.getKey();
            RoomVO key ;

            mDataArrayList.add((key = entry.getKey()));

            ChatApplication.logDisplay("room id is same key is  "+key.getRoomName()+" "+key.isExpanded());
            if (key.isExpanded()) {

                //mDataArrayList.add(new PanelVO("Panel1"));
                //mDataArrayList.addAll(entry.getValue());
                ArrayList<PanelVO> panelList = entry.getValue();

                for (int i = 0; i < panelList.size(); i++) {
                    //add panel
                    mDataArrayList.add(panelList.get(i));
                    //add all device switch
                    ChatApplication.logDisplay("panel name is "+panelList.get(i).getType());
                    if (panelList.get(i).getType().equalsIgnoreCase("camera") || panelList.get(i).getType().equals("JETSON-")) {
                        mDataArrayList.addAll(panelList.get(i).getCameraList());
                    } else {
                        mDataArrayList.addAll(panelList.get(i).getDeviceList());
                    }
                }
            }
        }
    }

    // variable to track event time
    private long mLastClickTime = 0;
    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500 ) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.section = section;
        ListUtils.sectionRoom = section;
        ListUtils.sectionRoom.setExpanded(isOpen);
        section.setExpanded(isOpen);

        ChatApplication.logDisplay("room id is callingcheck!! "+section.getRoomName()+"  " +isOpen);

        if (!isOpen) {
            ChatApplication.logDisplay("room id is calling!! "+section.getRoomName()+"  " +isOpen);
            if (ListUtils.arrayListRoom.size() > 0) {

                for (int i = 0; i < ListUtils.arrayListRoom.size(); i++) {
                    if (section.getRoomId().equals(ListUtils.arrayListRoom.get(i).getRoomId())) {
                        ChatApplication.logDisplay("room id is same "+section.getRoomId()+" "+ListUtils.arrayListRoom.get(i).getRoomId());
//                        section.isExpanded = false;
                        section.setExpanded(false);
                        ListUtils.arrayListRoom.set(i, section);
                    }
                }
            }

        } else {
            ChatApplication.logDisplay("room id is calling "+section.getRoomName()+"  " +isOpen);
            ListUtils.arrayListRoom.add(section);
        }


        if (!mRecyclerView.isComputingLayout()) {
//            section.isExpanded = isOpen;
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
    public void onPoisitionClick(int position) {
        onSmoothScrollList.onPoisitionClick(position);
    }
}
