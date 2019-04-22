package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by kaushal on 22/12/17.
 */

public class CameraVO implements Serializable,Cloneable {
    private String camera_id;
    private String userId;
    private String homeControllerDeviceId;
    private String camera_name;
    private String camera_ip;
    private String camera_videopath;
    private String userName;
    private String password;
    private String camera_icon;
    private int isActive;
    private String camera_vpn_port;

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    private boolean isDisable;

    public String getIs_unread() {
        return is_unread;
    }

    public void setIs_unread(String is_unread) {
        this.is_unread = is_unread;
    }

    private String is_unread;

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    private boolean isSelect=false;

    public String getCamera_url() {
        return camera_url;
    }

    public void setCamera_url(String camera_url) {
        this.camera_url = camera_url;
    }

    private String camera_url;



    public String getCamera_vpn_port() {
        return camera_vpn_port;
    }

    public void setCamera_vpn_port(String camera_vpn_port) {
        this.camera_vpn_port = camera_vpn_port;
    }

    /*
     "camera_id": "1516175101672_34WWW56",
             "user_id": "1516175101672_SkUb1YhVf",
             "home_controller_device_id": "b8:27:eb:f7:45:b3",
             "camera_name": "R&D Camera",
             "camera_ip": "192.168.175.101",
             "camera_videopath": "/video/mjpg.cgi",
             "camera_icon": "camera",
             "userName": "vatsal",
             "password": "123",
             "is_active": 1,
             "camera_sync": "to_sync",
             "created_by": null,
             "created_date": null,
             "modified_by": null,
             "modified_date": null*/

    public CameraVO(){
        camera_id="";
        userId="";
        homeControllerDeviceId="";
        camera_name="";
        camera_ip="";
        camera_videopath="";
        userName="";
        password="";
        camera_icon="";
        isActive = 1;
    }

    public String getCamera_icon() {
        return camera_icon;
    }

    public void setCamera_icon(String camera_icon) {
        this.camera_icon = camera_icon;
    }

    public String getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(String camera_id) {
        this.camera_id = camera_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHomeControllerDeviceId() {
        return homeControllerDeviceId;
    }

    public void setHomeControllerDeviceId(String homeControllerDeviceId) {
        this.homeControllerDeviceId = homeControllerDeviceId;
    }

    public String getCamera_name() {
        return camera_name;
    }

    public void setCamera_name(String camera_name) {
        this.camera_name = camera_name;
    }

    public String getCamera_ip() {
        return camera_ip;
    }

    public void setCamera_ip(String camera_ip) {
        this.camera_ip = camera_ip;
    }

    public String getCamera_videopath() {
        return camera_videopath;
    }

    public void setCamera_videopath(String camera_videopath) {
        this.camera_videopath = camera_videopath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CameraVO)) {
            return false;
        }
        final CameraVO other = (CameraVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.camera_id == other.camera_id )  {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.camera_id != null ? this.camera_id.hashCode() : 0);
        return hash;
    }
    @Override public CameraVO clone() {
        try {
            final CameraVO result = (CameraVO) super.clone();
            // copy fields that need to be copied here!
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}