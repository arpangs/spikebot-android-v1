package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteListRes {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("brandList")
        @Expose
        private List<BrandList> brandList = null;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public List<BrandList> getBrandList() {
            return brandList;
        }

        public void setBrandList(List<BrandList> brandList) {
            this.brandList = brandList;
        }

        public class BrandList {

            @SerializedName("brand_id")
            @Expose
            private Integer brandId;
            @SerializedName("brand_type")
            @Expose
            private String brandType;

            public Integer getBrandId() {
                return brandId;
            }

            public void setBrandId(Integer brandId) {
                this.brandId = brandId;
            }

            public String getBrandType() {
                return brandType;
            }

            public void setBrandType(String brandType) {
                this.brandType = brandType;
            }

        }

    }
}
