package com.spike.bot.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 29/7/19.
 * Gmail : vipul patel
 */
public class CameraSearchModel  {

    String camera_name;


    public CameraSearchModel(Boolean isOpen,String camera_id,String camera_name ) {
        this.camera_name = camera_name;
        this.camera_id=camera_id;
        this.isOpen = isOpen;
    }

    public CameraSearchModel(Parcel in) {
        camera_name = in.readString();
        camera_id = in.readString();
        isOpen = in.readByte() != 0;
    }

//    public static final Creator<CameraSearchModel> CREATOR = new Creator<CameraSearchModel>() {
//        @Override
//        public CameraSearchModel createFromParcel(Parcel in) {
//            return new CameraSearchModel(in);
//        }
//
//        @Override
//        public CameraSearchModel[] newArray(int size) {
//            return new CameraSearchModel[size];
//        }
//    };

    public String getCamera_name() {
        return camera_name;
    }

    public void setCamera_name(String camera_name) {
        this.camera_name = camera_name;
    }

    public String getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(String camera_id) {
        this.camera_id = camera_id;
    }

    String camera_id;

    public List<CameraVO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(List<CameraVO> arrayList) {
        this.arrayList = arrayList;
    }

    List<CameraVO> arrayList;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    boolean isOpen=false;

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(camera_name);
//        dest.writeString(camera_id);
//        dest.writeByte((byte) (isOpen ? 1 : 0));
//    }
}
