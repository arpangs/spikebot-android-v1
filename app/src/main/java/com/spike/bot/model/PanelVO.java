package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaushal on 22/12/17.
 */

public class PanelVO implements Serializable {
    private String panelId;
    private String panelName;

    public String getPanelnameTemp() {
        return panelnameTemp;
    }

    public void setPanelnameTemp(String panelnameTemp) {
        this.panelnameTemp = panelnameTemp;
    }

    private String panelnameTemp;
    private String homeControllerDeviceId;
    private String type;
    private int panel_status;
    private int is_original;
    public String module_id;
    private int oldStatus;
    private boolean isSensorPanel;
    private boolean isActivePanel;
    private String roomId;
    public boolean isExpanded=false;
    private boolean isSensorPanelOnly;
    String paneltype;

    public String getPaneltype() {
        return paneltype;
    }

    public void setPaneltype(String paneltype) {
        this.paneltype = paneltype;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setPanel_type(int panel_type) {
        this.panel_type = panel_type;
    }

    public boolean isDevicePanel() {
        return isSensorPanelOnly;
    }

    public void setDevicePanel(boolean sensorPanelOnly) {
        isSensorPanelOnly = sensorPanelOnly;
    }

    public String getPanel_name_sub() {
        return panel_name_sub;
    }

    public void setPanel_name_sub(String panel_name_sub) {
        this.panel_name_sub = panel_name_sub;
    }

    private String panel_name_sub;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String user_id;

    public String getRoom_panel_id() {
        return room_panel_id;
    }

    public void setRoom_panel_id(String room_panel_id) {
        this.room_panel_id = room_panel_id;
    }

    private String room_panel_id;

    public Integer getPanel_type() {
        return panel_type;
    }

    public void setPanel_type(Integer panel_type) {
        this.panel_type = panel_type;
    }

    private int panel_type;

    public ArrayList<DeviceVO> getRemoteDeviceList() {
        return remoteDeviceList;
    }

    public void setRemoteDeviceList(ArrayList<DeviceVO> remoteDeviceList) {
        this.remoteDeviceList = remoteDeviceList;
    }

    private ArrayList<DeviceVO> remoteDeviceList = new ArrayList<DeviceVO>();

    public boolean isRemoteAvabile() {
        return isRemoteAvabile;
    }

    public void setRemoteAvabile(boolean remoteAvabile) {
        isRemoteAvabile = remoteAvabile;
    }

    public boolean isRemoteAvabile;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isActivePanel() {
        return isActivePanel;
    }

    public void setActivePanel(boolean activePanel) {
        isActivePanel = activePanel;
    }

    public void setSensorPanel(boolean sensorPanel) {
        isSensorPanel = sensorPanel;
    }

    public boolean isSensorPanel() {
        return isSensorPanel;
    }

    public int getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(int oldStatus) {
        this.oldStatus = oldStatus;
    }

    private ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
    private ArrayList<CameraVO> cameraList= new ArrayList<CameraVO>();

    public PanelVO(String panelName,String panelId){
        this.panelName = panelName;
        this.panelId = panelId;
    }

    public PanelVO(String panelName){
        this.panelName = panelName;
    }

    public PanelVO() {
        panelId="";
        panelName="";
        homeControllerDeviceId="";
        type="";
        panel_status=0;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public int getIs_original() {
        return is_original;
    }

    public void setIs_original(int is_original) {
        this.is_original = is_original;
    }

    public int getPanel_status() {
        return panel_status;
    }

    public void setPanel_status(int panel_status) {
        this.panel_status = panel_status;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public String getHomeControllerDeviceId() {
        return homeControllerDeviceId;
    }

    public void setHomeControllerDeviceId(String homeControllerDeviceId) {
        this.homeControllerDeviceId = homeControllerDeviceId;
    }

    public ArrayList<DeviceVO> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<DeviceVO> deviceList) {
        this.deviceList = deviceList;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<CameraVO> getCameraList() {
        return cameraList;
    }

    public void setCameraList(ArrayList<CameraVO> cameraList) {
        this.cameraList = cameraList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PanelVO)) {
            return false;
        }
        final PanelVO other = (PanelVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.panelId.equalsIgnoreCase(other.panelId ) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.panelId != null ? this.panelId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return panelName;
    }
}
