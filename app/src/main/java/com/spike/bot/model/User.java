package com.spike.bot.model;

/**
 * Created by Sagar on 17/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class User {
    String user_id;
    String firstname;
    String lastname;
    String cloudIP;
    String password;
    boolean isActive;

    public User(){
        user_id = "";
        firstname = "";
        lastname = "";
        cloudIP = "";
        isActive = false;
        password = "";
    }

    public User(String user_id, String firstname, String lastname, String cloudIP, boolean isActive,String password) {
        this.user_id = user_id;
        this.lastname = lastname;
        this.cloudIP = cloudIP;
        this.firstname = firstname;
        this.isActive = isActive;
        this.password = password;
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
