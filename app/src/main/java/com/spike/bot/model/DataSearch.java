package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataSearch implements Serializable{

        @SerializedName("device_brand_id")
        @Expose
        private String deviceBrandId;
        @SerializedName("device_brand_remote_list")
        @Expose
        private List<DeviceBrandRemoteList> deviceBrandRemoteList = null;

        public String getDeviceBrandId() {
            return deviceBrandId;
        }

        public void setDeviceBrandId(String deviceBrandId) {
            this.deviceBrandId = deviceBrandId;
        }

        public List<DeviceBrandRemoteList> getDeviceBrandRemoteList() {
            return deviceBrandRemoteList;
        }

        public void setDeviceBrandRemoteList(List<DeviceBrandRemoteList> deviceBrandRemoteList) {
            this.deviceBrandRemoteList = deviceBrandRemoteList;
        }

    }