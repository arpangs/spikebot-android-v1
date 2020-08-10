package com.spike.bot.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.ListUtils;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraCounterModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.Device;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by akhil on 4/8/2020.
 */
public class PanelExpandableLayoutHelper implements SectionStateChangeListener, OnSmoothScrollList {

    //data list
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();
    private ArrayList<CameraCounterModel.Data> mCounterArrayList;


    private LinkedHashMap<PanelVO, ArrayList<DeviceVO>> mPanelSectionDataMap = new LinkedHashMap<PanelVO, ArrayList<DeviceVO>>();
    private LinkedHashMap<PanelVO, ArrayList<DeviceVO>> mPanelSectionDataMapTemp = new LinkedHashMap<PanelVO, ArrayList<DeviceVO>>();

    //section map
    //TODO : look for a way to avoid this1
    //  private HashMap<String, RoomVO> mSectionMap = new HashMap<String, RoomVO>();

    //adapter
    private PanelExpandableGridAdapter mSectionedExpandableGridAdapter;
    private OnSmoothScrollList onSmoothScrollList;

    public Context context;
    //recycler view`
    RecyclerView mRecyclerView;

    public PanelExpandableLayoutHelper(final Context context, RecyclerView recyclerView, ItemClickListener itemClickListener, OnSmoothScrollList onSmoothScrollList, TempClickListener tempClickListener,
                                           int gridSpanCount) {

        this.context = context;
        this.onSmoothScrollList = onSmoothScrollList;
        //setting the recycler view
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new PanelExpandableGridAdapter(context, mDataArrayList, mCounterArrayList,
                gridLayoutManager, itemClickListener, onSmoothScrollList, tempClickListener, this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);
      //  recyclerView.setHasFixedSize(false);
        mRecyclerView = recyclerView;


    }

    public void setCameraClick(PanelExpandableGridAdapter.CameraClickListener mCameraClickListener) {
        mSectionedExpandableGridAdapter.setCameraClickListener(mCameraClickListener);
    }

    public void setjetsonClick(PanelExpandableGridAdapter.JetsonClickListener jestonClickListener) {
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
        generatePanelDataList();
        getCameracounter();
        gettotalnotification();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void updateFanDevice(String deviceId, int deviceStatus) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof DeviceVO) {
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDevice_sub_status(String.valueOf(deviceStatus));
                        reloadDeviceList(((DeviceVO) mDataArrayList.get(k)));
//                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
//                        mSectionedExpandableGridAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        }
    }


    public void updateDeviceItem(String device_type, String deviceId, String deviceStatus, String device_sub_status) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof DeviceVO) {
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {

                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("status update panel adapter match");
                        ((DeviceVO) mDataArrayList.get(k)).setDeviceStatus(Integer.parseInt(deviceStatus));
                        ((DeviceVO) mDataArrayList.get(k)).setDevice_sub_status(device_sub_status);


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

            if (mDataArrayList.get(k) instanceof DeviceVO) {
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

            if (mDataArrayList.get(k) instanceof DeviceVO) {
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getModuleId().equals(module_id)) {
                        ((DeviceVO) mDataArrayList.get(k)).setIsActive(module_status.equals("y") ? 1 : -1);
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

            if (mDataArrayList.get(k) instanceof PanelVO) {
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

    List<CameraCounterModel.Data.CameraCounterList> counterlist = new ArrayList<>();
    CameraCounterModel.Data counterres = new CameraCounterModel.Data();

    public List<CameraCounterModel.Data.CameraCounterList> getCounterlist() {
        return counterlist;
    }

    public void setCounterlist(List<CameraCounterModel.Data.CameraCounterList> counterlist) {
        this.counterlist = counterlist;
    }

    public CameraCounterModel.Data getCounterres() {
        return counterres;
    }

    public void setCounterres(CameraCounterModel.Data counterres) {
        this.counterres = counterres;
    }

    public void gettotalnotification() {
       /* for (int l = 0; l < mDataArrayList.size(); l++) {
            if (mDataArrayList.get(l) instanceof RoomVO) {
                RoomVO roomVO1 = (RoomVO) mDataArrayList.get(l);
                if (roomVO1 != null) {
                    if(roomVO1.getRoomId().equalsIgnoreCase("Camera"))
                        ChatApplication.logDisplay("getCameracounter total notification" + " " + counterres.getTotalCameraNotification());
                        roomVO1.setIs_unread(String.valueOf(counterres.getTotalCameraNotification()));
                    // mSectionedExpandableGridAdapter.notifyItemChanged(l);
                    break;
                    //  mSectionedExpandableGridAdapter.notifyItemChanged(k);
                    //   break;


                }
            }
        }*/
        for (int i = 0; i < counterlist.size(); i++) {
            String jetson_device_id = counterlist.get(i).getJetsonDeviceId();
            ChatApplication.logDisplay("getCameracounter jetson_device_id " + " " + jetson_device_id);
            for (int k = 0; k < mDataArrayList.size(); k++) {
                if (mDataArrayList.get(k) instanceof RoomVO) {
                    RoomVO counterVO = (RoomVO) mDataArrayList.get(k);
                    if (counterVO != null) {
                        if (counterVO.getRoomId().equalsIgnoreCase("Camera")) {
                            ((RoomVO) mDataArrayList.get(k)).setIs_unread(String.valueOf(counterres.getTotalCameraNotification()));
                        } else if (counterVO.getRoomId().startsWith("JETSON-")) {
                            for (int j = 0; j < counterres.getTotalCounterList().size(); j++) {

                                String counter_jetson_device_id = counterres.getTotalCounterList().get(j).getJetsonDeviceId();
                                String total_counter_list = String.valueOf(counterres.getTotalCounterList().get(j).getTotalUnread());
                                //  counterVO.setIs_unread(total_counter_list);

                                if (jetson_device_id != null && !TextUtils.isEmpty(jetson_device_id)) {
                                    if (counterVO.getRoomId().equals(counter_jetson_device_id)) {

                                        ((RoomVO) mDataArrayList.get(k)).setIs_unread(total_counter_list);
                                        ChatApplication.logDisplay("getCameracounter jetson__id  " + " " + counter_jetson_device_id);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }


    public void getCameracounter() {

        for (int i = 0; i < counterlist.size(); i++) {
            String cameras_id = counterlist.get(i).getCameraId();
            String jetson_device_id = counterlist.get(i).getJetsonDeviceId();
            int total_unread = counterlist.get(i).getTotalUnread();

        /*    ChatApplication.logDisplay("getCameracounter camera_id" + " " + cameras_id);
            ChatApplication.logDisplay("getCameracounter jetson_device_id " + " " + jetson_device_id);
            ChatApplication.logDisplay("getCameracounter total_unread " + " " + total_unread);*/


            for (int l = 0; l < mDataArrayList.size(); l++) {
                if (mDataArrayList.get(l) instanceof CameraVO) {
                    CameraVO cameraVO1 = (CameraVO) mDataArrayList.get(l);
                    ChatApplication.logDisplay("getCameracounter total unread id" + " " + cameraVO1.getCamera_id());

                    if (cameraVO1 != null && cameraVO1.getJetson_device_id().startsWith("JETSON-")) {
                        if (cameraVO1.getCamera_id().equalsIgnoreCase(cameras_id) && cameraVO1.getJetson_device_id().equalsIgnoreCase(jetson_device_id)) {
                            cameraVO1.setIs_unread(String.valueOf(total_unread));
                            break;
                        }
                    }

                    if (cameraVO1 != null && cameraVO1.getCamera_id().equalsIgnoreCase(cameras_id)) {
                        cameraVO1.setIs_unread(String.valueOf(total_unread));
                        // mSectionedExpandableGridAdapter.notifyItemChanged(l);
                        break;
                        //  mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        //   break;
                    }
                }
            }


        }

        //mSectionedExpandableGridAdapter.notifyDataSetChanged();


        /*for (int i = 0; i < counterlist.size(); i++){
           String cameras_id = counterlist.get(i).getCameraId();
            String jetson_device_id = counterlist.get(i).getJetsonDeviceId();
            int total_unread = counterlist.get(i).getTotalUnread();

            ChatApplication.logDisplay("getCameracounter camera_id" + " " + cameras_id);
            ChatApplication.logDisplay("getCameracounter jetson_device_id " + " " + jetson_device_id);
            ChatApplication.logDisplay("getCameracounter total_unread " + " " + total_unread);

            ChatApplication.logDisplay("getCameracounter room list size " + " " + roomList.size());
            boolean found = false;
            *//*camera device counting*//*
            for (int j = 0; j < roomList.size(); j++) {
                if (roomList.get(j).getRoomId().equalsIgnoreCase("Camera"))
                {
                    roomcameralist = roomList.get(j).getPanelList().get(0).getCameraList();
                    for (int k = 0; k < roomcameralist.size(); k++) {
                        if(counterlist.get(i).getCameraId().equals(roomcameralist.get(k).getCamera_id()))
                        {
                            found = true;

                            ChatApplication.logDisplay("getCameracounter camera name " + " " + roomcameralist.get(k).getCamera_name());
                            ChatApplication.logDisplay("getCameracounter camera total unread" + " " + counterlist.get(i).getTotalUnread());

                            for (int l = 0; l < mDataArrayList.size(); l++) {
                                if (mDataArrayList.get(l) instanceof CameraVO) {
                                    CameraVO cameraVO1 = (CameraVO) mDataArrayList.get(l);
                                    ChatApplication.logDisplay("getCameracounter total unread id" + " " + cameraVO1.getCamera_id());
                                    if (cameraVO1 != null) {
                                        ((CameraVO) mDataArrayList.get(l)).setIs_unread(String.valueOf(counterlist.get(i).getTotalUnread()));
                                        //  mSectionedExpandableGridAdapter.notifyItemChanged(k);
                                        //   break;
                                        mSectionedExpandableGridAdapter.notifyItemChanged(l);

                                    }
                                }
                            }
                        }
                    }
                }
            }

        }*/
    }

    public void updateRoom(String id, String deviceStatus) {
        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof RoomVO) {
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



    public void updateBadgeCount(String deviceId, String counter) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof DeviceVO) {
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getDeviceId().equals(deviceId)) {
                        ChatApplication.logDisplay("device refresh is ");
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

            if (mDataArrayList.get(k) instanceof RoomVO) {
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

    public void updateBeaconBadgeCount(String roomid, String counter) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof RoomVO) {
                RoomVO roomVO1 = (RoomVO) mDataArrayList.get(k);
                if (roomVO1 != null) {
                    if (roomVO1.getRoomId().equals(roomid)) {
                        ((RoomVO) mDataArrayList.get(k)).setTotalbeacons(counter);
                        mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        break;
                    }

                }
            }
        }
    }

    public void updateTempCount(String blaster_id, String counter) {

        for (int k = 0; k < mDataArrayList.size(); k++) {

            if (mDataArrayList.get(k) instanceof DeviceVO) {
                DeviceVO deviceVO1 = (DeviceVO) mDataArrayList.get(k);
                if (deviceVO1 != null) {
                    if (deviceVO1.getDeviceType().equals("remote")) {
                        if (deviceVO1.getMeta_ir_blaster_id().equals(blaster_id)) {
                            ((DeviceVO) mDataArrayList.get(k)).setTemprature(counter);
                            mSectionedExpandableGridAdapter.notifyItemChanged(k);
                            break;
                        }
                    }

                }
            }
        }
    }



    //reload the row item in recycle
    public void reloadDeviceList(Object obj) {
        int position = mDataArrayList.indexOf(obj);

        if (position != -1) {
            mDataArrayList.set(position, obj);
            mSectionedExpandableGridAdapter.notifyItemChanged(position);
//               mSectionedExpandableGridAdapter.notifyDataSetChanged();
            getCameracounter();
            gettotalnotification();
            mSectionedExpandableGridAdapter.notifyDataSetChanged();
            ChatApplication.logDisplay("Relaod Device list ");
        }
    }

    public static PanelVO section;

    public void addPanelSectionList(ArrayList<PanelVO> panelList) {
        // mSectionDataMap = new LinkedHashMap<RoomVO, ArrayList<PanelVO>>();
        // mDataArrayList = new ArrayList<Object>();
        mPanelSectionDataMap.clear();
        mPanelSectionDataMapTemp.clear();
        mDataArrayList.clear();

        for (int i = 0; i < panelList.size(); i++) {
            addPanelSection(panelList.get(i));
        }
    }



    public void addPanelSection(PanelVO section) {
        //mSectionMap.put(section.getRoomName(), section);
        if (this.section != null && this.section.equals(section)) {

        }

        for (PanelVO PanelVO : ListUtils.arrayListPanel) {
            if (PanelVO.isExpanded() == true && PanelVO.getRoomId().equalsIgnoreCase(section.getRoomId())) {
                section.setExpanded(true);
            }
        }

        mPanelSectionDataMap.put(section, section.getDeviceList());
        mPanelSectionDataMapTemp.put(section, section.getDeviceList());
    }

    private void generatePanelDataList() {
        mDataArrayList.clear();

        for (Map.Entry<PanelVO, ArrayList<DeviceVO>> entry : mPanelSectionDataMap.entrySet()) {
//            RoomVO key = entry.getKey();
            PanelVO key;

            mDataArrayList.add((key = entry.getKey()));


            //mDataArrayList.add(new PanelVO("Panel1"));
            //mDataArrayList.addAll(entry.getValue());
            ArrayList<DeviceVO> deviceList = entry.getValue();

            for (int i = 0; i < deviceList.size(); i++) {
                //add panel
                mDataArrayList.add(deviceList.get(i));
                //add all device switch
                ChatApplication.logDisplay("panel name is " + deviceList.get(i).getPanel_name());

            }
        }
    }


    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    public void onSectionStateChanged(RoomVO section, boolean isOpen) {

    }

    @Override
    public void onSectionStateChanged(PanelVO section, boolean isOpen) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.section = section;
        ListUtils.sectionPanel = section;
        ListUtils.sectionPanel.setExpanded(isOpen);
        section.setExpanded(isOpen);

        if (!isOpen) {
            if (ListUtils.arrayListPanel.size() > 0) {

                for (int i = 0; i < ListUtils.arrayListPanel.size(); i++) {
                    if (section.getRoomId().equals(ListUtils.arrayListPanel.get(i).getRoomId())) {
                        ChatApplication.logDisplay("room id is same " + section.getRoomId() + " " + ListUtils.arrayListPanel.get(i).getRoomId());
//                        section.isExpanded = false;
                        section.setExpanded(false);
                        ListUtils.arrayListPanel.set(i, section);
                    }
                }
            }

        } else {
            ListUtils.arrayListPanel.add(section);
        }

        if (!mRecyclerView.isComputingLayout()) {

            section.setExpanded(isOpen);

            for (Map.Entry<PanelVO, ArrayList<DeviceVO>> entry : mPanelSectionDataMap.entrySet()) {
                PanelVO key = entry.getKey();
                if (key.equals(section)) {
                    key.setExpanded(isOpen);
                } else {
                    key.setExpanded(false);
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
