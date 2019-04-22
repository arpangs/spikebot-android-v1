package com.spike.bot.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 3/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddRemoteReq {

    /*
     * "brand_name":"Carrier",	"remote_codeset_id":"13",
     * "device_type":"AC",	"model_number":"42KGR-024R",
     * "remote_name":"IOT",    "ir_blaster_id":"1548399013334_7fG1X6xy-",
     * "ir_blaster_module_id":"SpikeBot1348709",
     * "room_id":"1548332287674_xR0nf1J0K",    "mode":"LOW",
     * "temperature": 25,    "ir_code":""
     * */

    @SerializedName("brand_name")
    private String brand_name;

    @SerializedName("remote_codeset_id")
    private String remote_codeset_id;

    @SerializedName("device_type")
    private String device_type;

    @SerializedName("device_id")
    private String device_id;
    @SerializedName("model_number")
    private String model_number;
    @SerializedName("remote_name")
    private String remote_name;
    @SerializedName("ir_blaster_id")
    private String ir_blaster_id;
    @SerializedName("ir_blaster_module_id")
    private String ir_blaster_moudle_id;
    @SerializedName("room_id")
    private String room_id;
    @SerializedName("mode")
    private String mode;
    @SerializedName("temperature")
    private String temperatature;
    @SerializedName("ir_code")
    private String ir_code;
    @SerializedName("user_id")
    private String user_id;


    @SerializedName("brand_id")
    private String brand_id;
    @SerializedName("brand_type")
    private String brand_type;
    @SerializedName("codeset_id")
    private String codeset_id;
    @SerializedName("phone_id")
    private String phone_id;
    @SerializedName("phone_type")
    private String phone_type;
    @SerializedName("remote_id")
    private String remote_id;
    @SerializedName("update_type")
    private int update_type;
    @SerializedName("room_device_id")
    private String roomDeviceId;

    public String getIr_blaster_name() {
        return ir_blaster_name;
    }

    public void setIr_blaster_name(String ir_blaster_name) {
        this.ir_blaster_name = ir_blaster_name;
    }

    @SerializedName("ir_blaster_name")
    private String ir_blaster_name;

    public AddRemoteReq(){}

    public AddRemoteReq(String user_id,String remote_id, String remote_name, String room_id,String ir_blaster_id, String mode, String temperatature, String phone_id, String phone_type,String ir_blaster_name){
        this.remote_id  = remote_id;
        this.remote_name = remote_name;
        this.ir_blaster_id = ir_blaster_id;
        this.room_id = room_id;
        this.mode = mode;
        this.temperatature = temperatature;
        this.phone_id = phone_id;
        this.phone_type = phone_type;
        this.ir_blaster_name = ir_blaster_name;
        this.user_id = user_id;
    }

    public AddRemoteReq(String user_id,String device_id, String device_type, String brand_id, String brand_type, String codeset_id, String remote_name, String ir_blaster_id, String ir_blaster_moudle_id, String room_id, String mode, String temperatature, String phone_id, String phone_type,
                        String brand_name,String remote_codeset_id,String model_number,String ir_code) {
        this.device_id = device_id;
        this.device_type = device_type;
//        this.brand_id = brand_id;
//        this.brand_type = brand_type;
//        this.codeset_id = codeset_id;
        this.remote_name = remote_name;
        this.ir_blaster_id = ir_blaster_id;
        this.ir_blaster_moudle_id = ir_blaster_moudle_id;
        this.room_id = room_id;
        this.mode = mode;
        this.temperatature = temperatature;
        this.brand_name = brand_name;
        this.remote_codeset_id = remote_codeset_id;
        this.model_number = model_number;
        this.ir_code = ir_code;
        this.user_id = user_id;
//        this.phone_id = phone_id;
//        this.phone_type = phone_type;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
    }

    public String getRoomDeviceId() {
        return roomDeviceId;
    }

    public void setRoomDeviceId(String roomDeviceId) {
        this.roomDeviceId = roomDeviceId;
    }

    public int getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(int update_type) {
        this.update_type = update_type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTemperatature() {
        return temperatature;
    }

    public void setTemperatature(String temperatature) {
        this.temperatature = temperatature;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_type() {
        return brand_type;
    }

    public void setBrand_type(String brand_type) {
        this.brand_type = brand_type;
    }

    public String getCodeset_id() {
        return codeset_id;
    }

    public void setCodeset_id(String codeset_id) {
        this.codeset_id = codeset_id;
    }

    public String getRemote_name() {
        return remote_name;
    }

    public void setRemote_name(String remote_name) {
        this.remote_name = remote_name;
    }

    public String getIr_blaster_id() {
        return ir_blaster_id;
    }

    public void setIr_blaster_id(String ir_blaster_id) {
        this.ir_blaster_id = ir_blaster_id;
    }

    public String getIr_blaster_moudle_id() {
        return ir_blaster_moudle_id;
    }

    public void setIr_blaster_moudle_id(String ir_blaster_moudle_id) {
        this.ir_blaster_moudle_id = ir_blaster_moudle_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getPhone_id() {
        return phone_id;
    }

    public void setPhone_id(String phone_id) {
        this.phone_id = phone_id;
    }

    public String getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(String phone_type) {
        this.phone_type = phone_type;
    }
}
