package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeviceBrandRemoteList implements Serializable{

        @SerializedName("remote_codeset_id")
        @Expose
        private Integer remote_codeset_id;
        @SerializedName("ir_code")
        @Expose
        private String irCode;
        @SerializedName("model_number")
        @Expose
        private String modelNumber;
        @SerializedName("brand_name")
        @Expose
        private String brandName;

        public Integer getremote_codeset_id() {
            return remote_codeset_id;
        }

        public void setremote_codeset_id(Integer id) {
            this.remote_codeset_id = id;
        }

        public String getIrCode() {
            return irCode;
        }

        public void setIrCode(String irCode) {
            this.irCode = irCode;
        }

        public String getModelNumber() {
            return modelNumber;
        }

        public void setModelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

    }