package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteOnOffRes {

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

        @SerializedName("deviceId")
        @Expose
        private String deviceId;
        @SerializedName("brandId")
        @Expose
        private String brandId;
        @SerializedName("onoffList")
        @Expose
        private List<OnoffList> onoffList = null;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getBrandId() {
            return brandId;
        }

        public void setBrandId(String brandId) {
            this.brandId = brandId;
        }

        public List<OnoffList> getOnoffList() {
            return onoffList;
        }

        public void setOnoffList(List<OnoffList> onoffList) {
            this.onoffList = onoffList;
        }

        public class OnoffList {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("ON")
            @Expose
            private String oN;
            @SerializedName("OFF")
            @Expose
            private String oFF;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getON() {
                return oN;
            }

            public void setON(String oN) {
                this.oN = oN;
            }

            public String getOFF() {
                return oFF;
            }

            public void setOFF(String oFF) {
                this.oFF = oFF;
            }

        }

    }
}
