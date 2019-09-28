package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Sagar on 2/9/19.
 * Gmail : vipul patel
 */
public class GatewayListModel implements Serializable {

    @SerializedName("gatewayMac")
    @Expose
    private String gatewayMac;
    @SerializedName("lockNum")
    @Expose
    private Integer lockNum;
    @SerializedName("gatewayName")
    @Expose
    private String gatewayName;
    @SerializedName("isOnline")
    @Expose
    private Integer isOnline;
    @SerializedName("gatewayVersion")
    @Expose
    private Integer gatewayVersion;
    @SerializedName("gatewayId")
    @Expose
    private Integer gatewayId;

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public Integer getLockNum() {
        return lockNum;
    }

    public void setLockNum(Integer lockNum) {
        this.lockNum = lockNum;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }

    public Integer getGatewayVersion() {
        return gatewayVersion;
    }

    public void setGatewayVersion(Integer gatewayVersion) {
        this.gatewayVersion = gatewayVersion;
    }

    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }
}
