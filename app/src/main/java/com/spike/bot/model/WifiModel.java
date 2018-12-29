package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 4/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class WifiModel implements Serializable{

    @SerializedName("WiFi_List")
    @Expose
    private List<WiFiList> wiFiList = null;

    public List<WiFiList> getWiFiList() {
        return wiFiList;
    }

    public void setWiFiList(List<WiFiList> wiFiList) {
        this.wiFiList = wiFiList;
    }

    public class WiFiList implements Serializable{

        @SerializedName("Network_Name")
        @Expose
        private String networkName;
        @SerializedName("Network_Strength")
        @Expose
        private String networkStrength;

        public String getNetworkName() {
            return networkName;
        }

        public void setNetworkName(String networkName) {
            this.networkName = networkName;
        }

        public String getNetworkStrength() {
            return networkStrength;
        }

        public void setNetworkStrength(String networkStrength) {
            this.networkStrength = networkStrength;
        }

    }
}
