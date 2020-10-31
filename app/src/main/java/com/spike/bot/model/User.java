package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sagar on 17/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class User implements Serializable {
    String user_id;
    String firstname;
    String lastname;
    String cloudIP;
    String password;
    String auth_key;



    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    String mac_address;

    public String getGateway_ip() {
        return gateway_ip;
    }

    public void setGateway_ip(String gateway_ip) {
        this.gateway_ip = gateway_ip;
    }

    String gateway_ip;

    public String getLocal_ip() {
        return local_ip;
    }

    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    String local_ip;

    public boolean getIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    boolean isopen;


    public ArrayList<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<String> roomList) {
        this.roomList = roomList;
    }

    ArrayList<String> roomList=new ArrayList<>();


    public ArrayList<String> getCameralist() {
        return cameralist;
    }

    public void setCameralist(ArrayList<String> cameralist) {
        this.cameralist = cameralist;
    }

    ArrayList<String> cameralist=new ArrayList<>();
    boolean isActive;



    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    String admin;

    public User(){
        user_id = "";
        firstname = "";
        lastname = "";
        cloudIP = "";
        isActive = false;
        password = "";
        admin = "";
        auth_key ="";
    }

    public User(String user_id, String firstname, String lastname, String cloudIP, boolean isActive,String password,String admin,String local_ip,String mac_address,String auth_key) {
        this.user_id = user_id;
        this.lastname = lastname;
        this.cloudIP = cloudIP;
        this.firstname = firstname;
        this.isActive = isActive;
        this.password = password;
        this.admin = admin;
        this.local_ip = local_ip;
        this.mac_address = mac_address;
        this.auth_key = auth_key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCloudIP() {
        return cloudIP;
    }

    public void setCloudIP(String cloudIP) {
        this.cloudIP = cloudIP;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
