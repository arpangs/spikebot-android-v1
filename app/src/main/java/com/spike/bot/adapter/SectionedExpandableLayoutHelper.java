package com.spike.bot.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

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

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by Kaushal on 2/23/2016.
 */
public class SectionedExpandableLayoutHelper implements SectionStateChangeListener ,OnSmoothScrollList{

    //data list
    private LinkedHashMap<RoomVO, ArrayList<PanelVO>> mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
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

        this.context=context;
        this.onSmoothScrollList = onSmoothScrollList;
        //setting the recycler view
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, onSmoothScrollList,tempClickListener,this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
        recyclerView.setHasFixedSize(true);
        mRecyclerView = recyclerView;


    }
    public void setCameraClick(SectionedExpandableGridAdapter.CameraClickListener mCameraClickListener){

        mSectionedExpandableGridAdapter.setCameraClickListener(mCameraClickListener);
    }

    public void setClickable(boolean clickable){
        mSectionedExpandableGridAdapter.setClickable(clickable);
    }


    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

        if(mDataArrayList.size()>0){
//            int badge=0;
//            for(int i=0; i<mDataArrayList.size(); i++){
//                try {
//                    RoomVO section= (RoomVO)mDataArrayList.get(i);
//                    if(section.getIs_unread()!=null){
//                        badge=badge +Integer.parseInt(section.getIs_unread());
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            if(badge>0){
           //     ShortcutBadger.applyCount(context, 10); //for 1.1.4+
//            }else {
//
//            }
        }
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public void updateItem(String moduleId,String deviceId,String deviceStatus ,int is_locked) {

        Log.d("updateDITEM","init....");

        /*DeviceVO item = new DeviceVO();
        item.setModuleId(moduleId);
        item.setDeviceId(""+deviceId);*/

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey();

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                //Device Loop

                //if (devicesList.contains(item)) {
                    for (int i = 0; i < devicesList.size(); i++) {

                        devicesList.get(i).setIs_locked(is_locked);
                        if(devicesList.get(i).getDeviceType().equalsIgnoreCase("2")){

                                if(devicesList.get(i).getRemote_device_id().equalsIgnoreCase(deviceId) &&
                                        devicesList.get(i).getModuleId().equalsIgnoreCase(moduleId)){

                                    devicesList.get(i).setRemote_status(Integer.parseInt(deviceStatus) == 1 ? "ON" : "OFF");
                                }

                        }else{
                            if (devicesList.get(i).getModuleId().equalsIgnoreCase(moduleId) && devicesList.get(i)
                                    .getDeviceId().equalsIgnoreCase(deviceId)) {
                                if(devicesList.get(i).getSensor_type()!=null &&
                                        (devicesList.get(i).getSensor_type().equalsIgnoreCase("remote"))){
                                    devicesList.get(i).setRemote_status(Integer.parseInt(deviceStatus) == 1 ? "ON" : "OFF");
                                }else{
                                    devicesList.get(i).setDeviceStatus(Integer.parseInt(deviceStatus));
                                }

                                //  reloadDeviceList(devicesList.get(i));

                                //   mSectionedExpandableGridAdapter.notifyItemChanged(i1,devicesList.get(i));

                            }
                        }
                    }
               // }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }
    public void updatePanel(String id,String deviceStatus) {
        PanelVO item = new PanelVO();
        item.setPanelId(id);
        item.setPanel_status(Integer.parseInt(deviceStatus));

        Log.d("PanelSocket","ID : " + deviceStatus);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {
            RoomVO key = entry.getKey() ;

            ArrayList<PanelVO> panelsList = entry.getValue();
            if(panelsList.contains(item)){

                for(int i=0;i<panelsList.size();i++) {

                    if (panelsList.get(i).equals(item)) {
                        panelsList.get(i).setPanel_status(Integer.parseInt(deviceStatus));

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
            RoomVO key = entry.getKey();
            //mDataArrayList.add((key = entry.getKey()));
            //if (key.isExpanded)
            //if(key.equals(item)){
            if(id.equals( key.getRoomId() )){
             //   Log.d("roomStatus "  , id + " id updatePanel roomid " + key.getRoomId() );
                key.setRoom_status(Integer.parseInt(deviceStatus));
                reloadDeviceList(key);
            }
        }
    //    mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param room_order
     * @param door_sensor_id
     * @param door_sensor_status
     */
    public void updateDoorStatus(String room_order, String door_sensor_id, String door_sensor_status){

        DeviceVO item = new DeviceVO();
        item.setSensor_id(door_sensor_id);

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey() ;

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                    for (int i = 0; i < devicesList.size(); i++) {

                        if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("door")){

                            if(devicesList.get(i).getSensor_id().equalsIgnoreCase(door_sensor_id)){
                                devicesList.get(i).setDoor_sensor_status(door_sensor_status);
                            }
                        }
                    }

            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }


    /**
     *
     * @param room_id
     * @param room_order
     * @param temp_sensor_id
     * @param temp_celsius
     * @param temp_fahrenheit
     * @param is_in_C
     */
    public void updateTempSensor(String room_id, String room_order, String temp_sensor_id, String temp_celsius, String temp_fahrenheit, String is_in_C){


        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey() ;

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                    for (int i = 0; i < devicesList.size(); i++) {

                        if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("temp")){

                            if(devicesList.get(i).getSensor_id().equalsIgnoreCase(temp_sensor_id)){

                                devicesList.get(i).setTemp_in_c(temp_celsius);
                                devicesList.get(i).setTemp_in_f(temp_fahrenheit);
                                devicesList.get(i).setIs_in_c(is_in_C); //last updated
                            }
                        }
                    }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }

    /**
     *
     * @param sensor_type
     * @param sensor_unread
     * @param module_id
     * @param room_id
     * @param room_unread
     */
    public void updateBadgeCount(String sensor_type, String sensor_unread, String module_id, String room_id, String room_unread){


        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey() ;

            if(key.getRoomId().equalsIgnoreCase(room_id)){
                key.setIs_unread(room_unread);
            }

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                for (int i = 0; i < devicesList.size(); i++) {

                    if(sensor_type.equalsIgnoreCase("temp")){

                        if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("temp")){
                            if(module_id.equalsIgnoreCase(devicesList.get(i).getModuleId())){
                                devicesList.get(i).setIs_unread(sensor_unread);
                            }
                        }
                    }else if(sensor_type.equalsIgnoreCase("door")){

                        if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("door")){
                            if(module_id.equalsIgnoreCase(devicesList.get(i).getModuleId())){
                                devicesList.get(i).setIs_unread(sensor_unread);
                            }
                        }
                    }
                }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }

    /**
     * update dead sensor
     * @param sensor_id
     * @param is_active
     */
    public void updateDeadSensor(String sensor_id, String is_active){

        for (Map.Entry<RoomVO, ArrayList<PanelVO>> entry : mSectionDataMap.entrySet()) {

            RoomVO key = entry.getKey() ;

            ArrayList<PanelVO> panelsList = entry.getValue();

            for (int i1 = 0; i1 < panelsList.size(); i1++) {
                PanelVO panel = panelsList.get(i1);

                ArrayList<DeviceVO> devicesList = panel.getDeviceList();

                for (int i = 0; i < devicesList.size(); i++) {

                    if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("temp")){

                        if(devicesList.get(i).getSensor_id().equalsIgnoreCase(sensor_id)){

                            devicesList.get(i).setIsActive(Integer.parseInt(is_active));

                        }
                    }else if(!TextUtils.isEmpty(devicesList.get(i).getSensor_type()) && devicesList.get(i).getSensor_type().equalsIgnoreCase("door")){

                        if(devicesList.get(i).getSensor_id().equalsIgnoreCase(sensor_id)){

                            devicesList.get(i).setIsActive(Integer.parseInt(is_active));

                        }
                    }
                }
            }
        }
        mSectionedExpandableGridAdapter.notifyDataSetChanged();

    }



    //reload the row item in recycle
    public  void reloadDeviceList(Object obj){
        int position = mDataArrayList.indexOf(obj);
        Log.d("updateDITEM","notifyItemChanged  position =  " + position);

        if(position!=-1) {
            Log.d("updateDITEM","notify....");
            mDataArrayList.set(position,obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);

        }
    }


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
       // Log.d("CallAPI"," generateDataList =  " + mSectionDataMap.size());
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

        Log.d("RefreshList","notifydata changes api");

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

    @Override
    public void onPoisitionClick(int position) {
        onSmoothScrollList.onPoisitionClick(position);
    }
}
