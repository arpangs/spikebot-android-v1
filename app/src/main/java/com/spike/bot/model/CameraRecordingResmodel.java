package com.spike.bot.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CameraRecordingResmodel {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data_ data;

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

    public Data_ getData() {
        return data;
    }

    public void setData(Data_ data) {
        this.data = data;
    }


    public class Data_ {

        @SerializedName("skip_time")
        @Expose
        private Integer skipTime;
        @SerializedName("camera_id")
        @Expose
        private String cameraId;
        @SerializedName("camera_files")
        @Expose
        private List<String> cameraFiles = null;

        public Integer getSkipTime() {
            return skipTime;
        }

        public void setSkipTime(Integer skipTime) {
            this.skipTime = skipTime;
        }

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public List<String> getCameraFiles() {
            return cameraFiles;
        }

        public void setCameraFiles(List<String> cameraFiles) {
            this.cameraFiles = cameraFiles;
        }
    }
}
