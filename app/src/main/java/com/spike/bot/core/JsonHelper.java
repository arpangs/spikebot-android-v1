package com.spike.bot.core;

import android.text.TextUtils;
import android.util.Log;

import com.spike.bot.model.AutoModeVO;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DevicePanelVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kaushal on 1/1/18.
 */


public class JsonHelper {


    public static ArrayList<RoomVO> parseRoomArray(JSONArray roomArray,boolean isParseOriginal){
        ArrayList<RoomVO> roomList = new ArrayList<>();

        for(int i=0;i<roomArray.length();i++){
            try {
                JSONObject objRoom = roomArray.getJSONObject(i);
                JSONObject roomObj = objRoom.getJSONObject("roomList");

                //roomDeviceId
                JSONArray jsonArrayRoomList = null;
                ArrayList<String> roomDeviceList = new ArrayList<>();
                if(roomObj.has("roomDeviceId")){

                    jsonArrayRoomList = roomObj.getJSONArray("roomDeviceId");

                    for(int r=0; r < jsonArrayRoomList.length(); r ++){
                        roomDeviceList.add(""+jsonArrayRoomList.get(r).toString());
                    }
                }
                //roomRemoteId
                JSONArray roomRemoteArray = null;
                ArrayList<String> roomRemoteList = new ArrayList<>();
                if(roomObj.has("roomRemoteId")){
                    roomRemoteArray = roomObj.getJSONArray("roomRemoteId");
                    for(int s = 0; s < roomRemoteArray.length(); s ++){
                        roomRemoteList.add(""+roomRemoteArray.get(s).toString());
                    }
                }


                RoomVO room = new RoomVO();
                room.setSmart_remote_number(roomObj.optString("smart_remote_number"));
                room.setRoomName(roomObj.getString("room_name"));
                room.setRoom_order(roomObj.getInt("room_order"));
                room.setRoomId(roomObj.getString("room_id"));
                room.setRoom_status(roomObj.getInt("room_status"));
                room.setSensor_panel(roomObj.optString("sensor_panel"));
                if(roomObj.has("device_count")){
                    room.setDevice_count(roomObj.getString("device_count"));
                }else{
                    room.setDevice_count("0");
                }

                if(roomObj.has("is_original")){
                    room.setIs_original(roomObj.getInt("is_original"));
                }

                if(roomObj.has("room_icon")){
                    room.setRoom_icon(roomObj.getString("room_icon"));
                }

                String isunread = "",temp_sensor_value = "",door_sensor_value = "";
                if(roomObj.has("is_unread")){
                    isunread = roomObj.getString("is_unread");
                }
                if(roomObj.has("temp_sensor_value")){
                    temp_sensor_value = roomObj.getString("temp_sensor_value");
                }
                if(roomObj.has("door_sensor_value")) {
                    door_sensor_value = roomObj.getString("door_sensor_value");
                }

                room.setIs_unread(isunread);
                room.setTemp_sensor_value(temp_sensor_value);
                room.setDoor_sensor_value(door_sensor_value);

                try{
                    if((jsonArrayRoomList != null ? jsonArrayRoomList.length() : 0) == 0){
                        room.setRoomDeviceId(null);
                    }else{
                        room.setRoomDeviceId(roomDeviceList);
                    }
                }catch (Exception ex){ ex.printStackTrace(); }

                if((roomRemoteArray != null ? roomRemoteArray.length() : 0) == 0){
                    room.setRemoteDeviceList(null);
                }else{
                    room.setRemoteDeviceList(roomRemoteList);
                }


                JSONArray panelArray = roomObj.getJSONArray("panelList");

                ArrayList<PanelVO> panelList = JsonHelper.parsePanelArray(panelArray,room,room.getRoomName(),room.getRoomId(),isParseOriginal); //new ArrayList<PanelVO>();

                roomList.add(room);

                room.setPanelList(panelList);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  roomList;
    }
    public static ArrayList<RoomVO> parseExistPanelArray(JSONArray roomArray){
        ArrayList<RoomVO> roomDeviceList = new ArrayList<>();

        for(int i=0;i<roomArray.length();i++){
            try {
                JSONObject objRoom = roomArray.getJSONObject(i);

                RoomVO room = new RoomVO();
                room.setRoomId(objRoom.optString("room_id"));
                room.setPanel_id(objRoom.getString("module_id"));
                room.setModule_name(objRoom.getString("module_name"));

                JSONObject roomArrayList = objRoom.getJSONObject("roomList");

                room.setRoomName(roomArrayList.getString("room_name"));

                if(roomArrayList.has("panel_id")){
                    room.setPanel_id(roomArrayList.getString("panel_id"));
                }
                if(roomArrayList.has("panel_name")){
                    room.setPanel_name(roomArrayList.getString("panel_name"));
                }

                if(roomArrayList.has("module_id")){
                    room.setModule_id(roomArrayList.getString("module_id"));
                }

                ArrayList<DevicePanelVO> deviceList = new ArrayList<DevicePanelVO>();

                JSONArray jsonArrayRoomList = null;
                if(objRoom.has("deviceList")){
                    jsonArrayRoomList = objRoom.getJSONArray("deviceList");

                    for(int d=0;d<jsonArrayRoomList.length();d++){
                        JSONObject deviceObject = jsonArrayRoomList.getJSONObject(d);
                        DevicePanelVO deviceVO = new DevicePanelVO();
                        deviceVO.setModuleId(deviceObject.getString("module_id"));
                        deviceVO.setDeviceId(deviceObject.getInt("device_id"));
                        deviceVO.setDeviceName(deviceObject.getString("device_name"));
                        deviceVO.setDevice_icon(deviceObject.getString("device_icon"));
                        deviceVO.setRoom_panel_id(""+deviceObject.optString("room_panel_id"));

                        if(deviceObject.has("original_room_device_id")){
                            deviceVO.setOriginal_room_device_id(deviceObject.getString("original_room_device_id"));
                        }

                      //  deviceVO.setIsActive(deviceObject.getInt("is_active"));
                     //   deviceVO.setIsAlive(deviceObject.getInt("is_alive"));
                     //   deviceVO.setIs_original(deviceObject.getInt("is_original"));

                        if(deviceObject.has("room_device_id")){
                            deviceVO.setRoomDeviceId(deviceObject.getString("room_device_id"));
                        }

                        if(deviceObject.has("panel_id")){
                            deviceVO.setPanel_id(deviceObject.getString("panel_id"));
                        }else{
                            if(roomArrayList.has("panel_id")){
                                deviceVO.setPanel_id(roomArrayList.getString("panel_id"));
                            }
                        }

                        if(deviceObject.has("device_status")){
                                deviceVO.setDeviceStatus(deviceObject.getString("device_status"));
                        }

                        if(deviceObject.has("device_specific_value")){
                            deviceVO.setDeviceSpecificValue(deviceObject.getString("device_specific_value"));
                        }
                        if(deviceObject.has("device_type")){
                            deviceVO.setDeviceType(deviceObject.getString("device_type"));
                        }

                        deviceList.add(deviceVO);
                    }
                }

                sortDevice(deviceList);

                room.setDevicePanelList(deviceList);
                roomDeviceList.add(room);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return roomDeviceList;
    }

    /**
     * check JsonObject has value or not
     * @param jsonObject
     * @param jsonString
     * @return
     */
    public static boolean hasObject(JSONObject jsonObject,String jsonString){
        return jsonObject.has(jsonString);
    }


    public static ArrayList<PanelVO> parsePanelArray(JSONArray panelArray,RoomVO roomVO, String roomName,String roomId, boolean isParseOriginal){
        ArrayList<PanelVO> panelList = new ArrayList<PanelVO>();

        boolean isDevicePanel = false;

        for (int j = 0; j < panelArray.length(); j++) {
            JSONObject objPanel = null;
            try {
                objPanel = panelArray.getJSONObject(j);

                String panel_id = hasObject(objPanel,"panel_id") ? objPanel.getString("panel_id") : "";
                String panel_name = hasObject(objPanel,"panel_name") ? objPanel.getString("panel_name") : "";
                String panel_name_sub = hasObject(objPanel,"panel_name") ? objPanel.getString("panel_name") : "";
                String room_panel_id = hasObject(objPanel,"room_panel_id") ? objPanel.getString("room_panel_id") : "";
                int panel_type1 = hasObject(objPanel,"panel_type") ? objPanel.getInt("panel_type") : 0;
                int panel_order = hasObject(objPanel,"panel_order") ? objPanel.getInt("panel_order") : 0;

                PanelVO panelVO = new PanelVO();


                if(hasObject(objPanel,"panel_status")){
                    int panel_status =  objPanel.getInt("panel_status");
                    panelVO.setPanel_status(panel_status);
                }

                String module_id = hasObject(objPanel,"module_id") ? objPanel.getString("module_id") : "";

                panelVO.setPanelId(panel_id);
                panelVO.setPanelName(panel_name);
                panelVO.setModule_id(module_id);
                panelVO.setPanel_type(panel_type1);
                panelVO.setRoom_panel_id(room_panel_id);
                panelVO.setPanel_name_sub(panel_name_sub);

                if(objPanel.has("deviceList")){

                    if(hasObject(objPanel,"is_original")){
                        int panel_type = objPanel.getInt("is_original");
                        panelVO.setIs_original(panel_type);
                    }
                    panelVO.setActivePanel(true);

                    JSONArray deviceArray = objPanel.getJSONArray("deviceList");
                    panelVO.setSensorPanel(false);
                    ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                    deviceList = JsonHelper.parseDeviceArray(deviceArray,roomName,panel_name,roomId,isParseOriginal,panel_id);
                    roomVO.setRoomOnOff(true);
                    panelVO.setDeviceList(deviceList);
                    isDevicePanel = true;

                }

                /*IR blaster remote array add */
                if(objPanel.has("remoteList")){
                    panelVO.setSensorPanel(true);

                    JSONArray sensorArray = objPanel.getJSONArray("remoteList");
                    ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                    deviceList = JsonHelper.parseIrRemoteArray(roomVO,panelVO,sensorArray,roomName,panel_name,roomId, isParseOriginal,panel_id);
                    panelVO.setDeviceList(deviceList);
                    boolean isRemote=true;
//                    for (int i=0; i<deviceList.size(); i++){
//                        if(deviceList.get(i).getDeviceType().equals("2")){
//                            isRemote=true;
//                            break;
//                        }
//                    }
                    panelVO.setRemoteAvabile(isRemote);
                    if(isDevicePanel){
                        isDevicePanel = true;
                    }
                }

                if(objPanel.has("sensorList")){
                    panelVO.setSensorPanel(true);

                    JSONArray sensorArray = objPanel.getJSONArray("sensorList");
                    ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                    deviceList = JsonHelper.parseSensorArray(roomVO,panelVO,sensorArray,roomName,panel_name,roomId, isParseOriginal,panel_id);
                    panelVO.setDeviceList(deviceList);
                    boolean isRemote=false;
                    for (int i=0; i<deviceList.size(); i++){
                        if(deviceList.get(i).getDeviceType().equals("2")){
                            isRemote=true;
                            break;
                        }
                    }
                    panelVO.setRemoteAvabile(isRemote);
                    if(isDevicePanel){
                        isDevicePanel = true;
                    }
                }


//                if(roomVO.getRoomName().equalsIgnoreCase("Iot")){
//                    Log.d("isDevicePanel","Found : " + isDevicePanel + " Panel name : " + panelVO.getPanelName());
//                }
                roomVO.setDevicePanel(isDevicePanel);

                if(!TextUtils.isEmpty(panel_id)){
                    Log.d("isPanelActive","Panel isActivePanel :" + panelVO.isActivePanel());
                    panelList.add(panelVO);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return panelList;
    }

    public static ArrayList<DeviceVO> parseIrRemoteArray(RoomVO roomVO, PanelVO panelVO, JSONArray sensorArray, String roomName, String panel_name, String roomId, boolean isParseOriginal, String panel_id){

        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        int countInActive = 0;
        boolean flagRemote=false;

        for(int j=0 ; j < sensorArray.length() ; j++){

            try {

                JSONObject deviceObj = sensorArray.getJSONObject(j);

                String sensor_voltage = hasObject(deviceObj,"sensor_voltage") ? deviceObj.getString("sensor_voltage") : "";

                String sensor_icon = deviceObj.getString("sensor_icon");

                String door_sensor_status = hasObject(deviceObj,"door_sensor_status") ? deviceObj.getString("door_sensor_status") : "";
                String sensor_type = deviceObj.getString("sensor_type");
                String is_unread = hasObject(deviceObj,"is_unread") ? deviceObj.getString("is_unread") : "";
                String created_date = hasObject(deviceObj,"created_date") ? deviceObj.getString("created_date") : "";
                String temp_in_C = hasObject(deviceObj,"temp_in_C") ? deviceObj.getString("temp_in_C") : "";
                String temp_in_F = hasObject(deviceObj,"temp_in_F") ? deviceObj.getString("temp_in_F") : "";
                String is_in_C = hasObject(deviceObj,"is_in_C") ? deviceObj.getString("is_in_C") : "";
                String to_use = hasObject(deviceObj,"to_use") ? deviceObj.getString("to_use") : "";
                int is_active = hasObject(deviceObj,"is_active") ? deviceObj.getInt("is_active") : 0;

                String remote_status = hasObject(deviceObj,"remote_status") ? deviceObj.getString("remote_status") : "";
                String remote_device_id = hasObject(deviceObj,"remote_device_id") ? deviceObj.getString("remote_device_id") : "";

                String speed = hasObject(deviceObj,"mode") ? deviceObj.getString("mode") : "";
                int temperature = hasObject(deviceObj,"temperature") ? deviceObj.getInt("temperature") : 0;

                if(is_active == 0){
                    Log.d("isPanelActive","Active : " + is_active);
                    countInActive ++;
                }

                String module_id = "";
                String sensor_id = "";
                String sensor_name = "";
                String ir_blaster_id = "";

                String room_panel_id = hasObject(deviceObj,"room_panel_id") ? deviceObj.getString("room_panel_id") : "";

                if(TextUtils.isEmpty(room_panel_id)){
                    room_panel_id=hasObject(deviceObj,"panel_id") ? deviceObj.getString("panel_id") : "";
                }

                DeviceVO d1 = new DeviceVO();
                d1.setSpeed(speed);
                d1.setTemprature(temperature);
                d1.setRoom_panel_id(room_panel_id);
                d1.setRemote_device_id(remote_device_id);
                d1.setRemote_status(remote_status);
                sensor_name = deviceObj.getString("sensor_name");
                Log.d("deviceObj","deviceObj : " + sensor_name);

                if(sensor_type.equalsIgnoreCase("remote")){
                    ir_blaster_id = deviceObj.getString("ir_blaster_id");
                    sensor_id = deviceObj.getString("sensor_id");
                    d1.setIr_blaster_id(ir_blaster_id);
                    module_id = deviceObj.has("module_id") ? deviceObj.getString("module_id") : "";

                }else{
                    sensor_id = deviceObj.getString("sensor_id");
                    module_id = deviceObj.has("module_id") ? deviceObj.getString("module_id") : "";
                }

                String device_id = deviceObj.has("device_id") ? deviceObj.getString("device_id") : "";
                int device_status = deviceObj.has("device_status") ? deviceObj.getInt("device_status") : 0;
                String device_type = deviceObj.has("device_type") ? deviceObj.getString("device_type"):"";
                String device_specific_value = deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"";
                String device_icon = deviceObj.has("device_icon")  ? deviceObj.getString("device_icon") : "";
                int auto_on_off_value =  deviceObj.has("auto_on_off_value")? deviceObj.getInt("auto_on_off_value"):0;
                int schedule_value =  deviceObj.has("schedule_value")? deviceObj.getInt("schedule_value"):0;
                int is_alive =  deviceObj.has("is_alive")? deviceObj.getInt("is_alive"):0;

                String room_name =  deviceObj.has("room_name")? deviceObj.getString("room_name"):"";

                String original_room_device_id = deviceObj.has("original_room_device_id") ? deviceObj.getString("original_room_device_id") : "";
                String room_device_id = deviceObj.has("room_device_id") ? deviceObj.getString("room_device_id") : "";
                String is_original = deviceObj.has("is_original") ? deviceObj.getString("is_original") : "";

                d1.setPanel_id(panel_id);
                d1.setDeviceId(device_id);
                d1.setDeviceStatus(device_status);
                d1.setDeviceType(device_type);
                d1.setDeviceSpecificValue(device_specific_value);
                d1.setAuto_on_off_value(auto_on_off_value);
                d1.setSchedule_value(schedule_value);
                d1.setDevice_icon(device_icon);
                d1.setIsAlive(is_alive);
                d1.setOriginal_room_device_id(original_room_device_id);
                d1.setRoomDeviceId(room_device_id);
                d1.setRoomName(room_name);

                d1.setSensor(true);
                d1.setSensor_id(sensor_id);
                d1.setModuleId(module_id);
                d1.setSensor_name(sensor_name);
                d1.setSensor_voltage(sensor_voltage);
                d1.setSensor_icon(sensor_icon);
                d1.setDoor_sensor_status(door_sensor_status);
                d1.setSensor_type(sensor_type);
                d1.setIsActive(is_active);

                d1.setIs_unread(is_unread);
                d1.setCreated_date(created_date);
                d1.setTemp_in_c(temp_in_C);
                d1.setTemp_in_f(temp_in_F);
                d1.setIs_in_c(is_in_C);
                d1.setTo_use(to_use);
                if(!TextUtils.isEmpty(is_original)){
                    d1.setIs_original(Integer.parseInt(is_original));
                }


                d1.setRoomName(roomName);
                d1.setRoomId(roomId);

                if(sensor_type.equalsIgnoreCase("remote")){
                    flagRemote=true;

                }

                deviceList.add(d1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        panelVO.setActivePanel(flagRemote);
        if(sensorArray.length() == countInActive){
            //    panelVO.setActivePanel(false);
        }else{
            //  panelVO.setActivePanel(true);
        }
        Log.d("sensorArraylength","Lengtj : "+ sensorArray.length() + " : Room Name : " + roomVO.getRoomName());

        return deviceList;
    }

    public static ArrayList<DeviceVO> parseSensorArray(RoomVO roomVO, PanelVO panelVO, JSONArray sensorArray, String roomName, String panel_name, String roomId, boolean isParseOriginal, String panel_id){

        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        int countInActive = 0;
        boolean flagRemote=false;

        for(int j=0 ; j < sensorArray.length() ; j++){

            try {

                JSONObject deviceObj = sensorArray.getJSONObject(j);

                String sensor_voltage = hasObject(deviceObj,"sensor_voltage") ? deviceObj.getString("sensor_voltage") : "";

                String sensor_icon = deviceObj.getString("sensor_icon");

                String door_sensor_status = hasObject(deviceObj,"door_sensor_status") ? deviceObj.getString("door_sensor_status") : "";
                String sensor_type = deviceObj.getString("sensor_type");
                String is_unread = hasObject(deviceObj,"is_unread") ? deviceObj.getString("is_unread") : "";
                String created_date = hasObject(deviceObj,"created_date") ? deviceObj.getString("created_date") : "";
                String temp_in_C = hasObject(deviceObj,"temp_in_C") ? deviceObj.getString("temp_in_C") : "";
                String temp_in_F = hasObject(deviceObj,"temp_in_F") ? deviceObj.getString("temp_in_F") : "";
                String humidity = deviceObj.optString("humidity");
                String is_in_C = hasObject(deviceObj,"is_in_C") ? deviceObj.getString("is_in_C") : "";
                String to_use = hasObject(deviceObj,"to_use") ? deviceObj.getString("to_use") : "";
                int is_active = hasObject(deviceObj,"is_active") ? deviceObj.getInt("is_active") : 0;

                String remote_status = hasObject(deviceObj,"remote_status") ? deviceObj.getString("remote_status") : "";
                String remote_device_id = hasObject(deviceObj,"remote_device_id") ? deviceObj.getString("remote_device_id") : "";

                String speed = hasObject(deviceObj,"mode") ? deviceObj.getString("mode") : "";
                int temperature = hasObject(deviceObj,"temperature") ? deviceObj.getInt("temperature") : 0;

                if(is_active == 0){
                    Log.d("isPanelActive","Active : " + is_active);
                    countInActive ++;
                }

                String module_id = "";
                String sensor_id = "";
                String sensor_name = "";
                String ir_blaster_id = "";

                String room_panel_id = hasObject(deviceObj,"room_panel_id") ? deviceObj.getString("room_panel_id") : "";

                if(TextUtils.isEmpty(room_panel_id)){
                    room_panel_id=hasObject(deviceObj,"panel_id") ? deviceObj.getString("panel_id") : "";
                }

                DeviceVO d1 = new DeviceVO();
                d1.setSpeed(speed);
                d1.setTemprature(temperature);
                d1.setRoom_panel_id(room_panel_id);
                d1.setRemote_device_id(remote_device_id);
                d1.setRemote_status(remote_status);
                sensor_name = deviceObj.getString("sensor_name");
                Log.d("deviceObj","deviceObj : " + sensor_name);

                if(sensor_type.equalsIgnoreCase("remote")){
                    ir_blaster_id = deviceObj.getString("ir_blaster_id");
                    sensor_id = deviceObj.getString("sensor_id");
                    d1.setIr_blaster_id(ir_blaster_id);
                    module_id = deviceObj.has("module_id") ? deviceObj.getString("module_id") : "";

                }else{
                    sensor_id = deviceObj.getString("sensor_id");
                    module_id = deviceObj.has("module_id") ? deviceObj.getString("module_id") : "";
                }

                String device_id = deviceObj.has("device_id") ? deviceObj.getString("device_id") : "2";
                int device_status = deviceObj.has("device_status") ? deviceObj.getInt("device_status") : 0;
                String device_type = deviceObj.has("device_type") ? deviceObj.getString("device_type"):"";
                String device_specific_value = deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"";
                String device_icon = deviceObj.has("device_icon")  ? deviceObj.getString("device_icon") : "";
                int auto_on_off_value =  deviceObj.has("auto_on_off_value")? deviceObj.getInt("auto_on_off_value"):0;
                int schedule_value =  deviceObj.has("schedule_value")? deviceObj.getInt("schedule_value"):0;
                int is_alive =  deviceObj.has("is_alive")? deviceObj.getInt("is_alive"):0;

                String room_name =  deviceObj.has("room_name")? deviceObj.getString("room_name"):"";

                String original_room_device_id = deviceObj.has("original_room_device_id") ? deviceObj.getString("original_room_device_id") : "";
                String room_device_id = deviceObj.has("room_device_id") ? deviceObj.getString("room_device_id") : "";
                String is_original = deviceObj.has("is_original") ? deviceObj.getString("is_original") : "";

                d1.setPanel_id(panel_id);
                d1.setDeviceId(device_id);
                d1.setDeviceStatus(device_status);
                d1.setDeviceType(device_type);
                d1.setDeviceSpecificValue(device_specific_value);
                d1.setAuto_on_off_value(auto_on_off_value);
                d1.setSchedule_value(schedule_value);
                d1.setDevice_icon(device_icon);
                d1.setIsAlive(is_alive);
                d1.setOriginal_room_device_id(original_room_device_id);
                d1.setRoomDeviceId(room_device_id);
                d1.setRoomName(room_name);

                d1.setSensor(true);
                d1.setSensor_id(sensor_id);
                d1.setModuleId(module_id);
                d1.setSensor_name(sensor_name);
                d1.setSensor_voltage(sensor_voltage);
                d1.setSensor_icon(sensor_icon);
                d1.setDoor_sensor_status(door_sensor_status);
                d1.setSensor_type(sensor_type);
                d1.setIsActive(is_active);

                d1.setIs_unread(is_unread);
                d1.setCreated_date(created_date);
                d1.setTemp_in_c(temp_in_C);
                d1.setTemp_in_f(temp_in_F);
                d1.setIs_in_c(is_in_C);
                d1.setHumidity(humidity);
                d1.setTo_use(to_use);
                if(!TextUtils.isEmpty(is_original)){
                    d1.setIs_original(Integer.parseInt(is_original));
                }


                d1.setRoomName(roomName);
                d1.setRoomId(roomId);

                if(sensor_type.equalsIgnoreCase("remote")){
                    flagRemote=true;

                }

                deviceList.add(d1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        panelVO.setActivePanel(flagRemote);
        if(sensorArray.length() == countInActive){
        //    panelVO.setActivePanel(false);
        }else{
          //  panelVO.setActivePanel(true);
        }
        Log.d("sensorArraylength","Lengtj : "+ sensorArray.length() + " : Room Name : " + roomVO.getRoomName());

        return deviceList;
    }

    public static ArrayList<DeviceVO> parseDeviceArray(JSONArray deviceArray, String roomName, String panelName, String roomId, boolean isParseOriginal, String panel_id){
        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        for(int j=0 ; j < deviceArray.length() ; j++){

            try {

                JSONObject deviceObj = deviceArray.getJSONObject(j);
                String device_name = deviceObj.getString("device_name");
                String device_nameTemp = deviceObj.getString("device_name");

                int is_original =  deviceObj.has("is_alive")? deviceObj.getInt("is_original"):0;

                String module_id = deviceObj.getString("module_id");
                String room_device_id = deviceObj.getString("room_device_id");
                int mood_id = 0;
                if(deviceObj.has("id")){
                    mood_id = deviceObj.getInt("id");
                }
                String device_id = deviceObj.getString("device_id");
                int device_status = deviceObj.getInt("device_status");
                String device_type = deviceObj.has("device_type")?deviceObj.getString("device_type"):"";
                String device_specific_value = deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"";
                String device_icon = deviceObj.getString("device_icon");
                int auto_on_off_value =  deviceObj.has("auto_on_off_value")? deviceObj.getInt("auto_on_off_value"):0;
                int schedule_value =  deviceObj.has("schedule_value")? deviceObj.getInt("schedule_value"):0;
                int is_active =  deviceObj.has("is_active")? deviceObj.getInt("is_active"):0;
                int is_alive =  deviceObj.has("is_alive")? deviceObj.getInt("is_alive"):0;
                int temperature =  deviceObj.has("temperature")? deviceObj.optInt("temperature"):0;
                int is_locked =  deviceObj.has("is_locked")? deviceObj.optInt("is_locked"):0;
                String mode =  deviceObj.has("mode")? deviceObj.optString("mode"):"";
                String power =  deviceObj.has("power")? deviceObj.optString("power"):"";

                String room_name =  deviceObj.has("room_name")? deviceObj.getString("room_name"):"";
                String panel_name =  deviceObj.has("panel_name")? deviceObj.getString("panel_name"):"";

                String original_room_device_id = deviceObj.has("original_room_device_id") ? deviceObj.getString("original_room_device_id") : "";

               // Log.d("OrignalDevice","id : " + original_room_device_id + " dname : " + device_name + " mood_name :" + mood_id);

                String  auto_off_timer = "";//deviceObj.getString("auto_off_timer").equalsIgnoreCase("null")?"":deviceObj.getString("auto_off_timer");
                String auto_off_id = "";//deviceObj.getString("auto_off_id").equalsIgnoreCase("null")?"":deviceObj.getString("auto_off_id");
                int auto_off_active  = 0;//deviceObj.getString("auto_off_active").equalsIgnoreCase("null")?0:Integer.parseInt(deviceObj.getString("auto_off_active"));
                String schedule_id = "";//deviceObj.getString("schedule_id").equalsIgnoreCase("null")?"":deviceObj.getString("schedule_id");
                String schedule_device_time = "";//deviceObj.getString("schedule_device_time").equalsIgnoreCase("null")?"":deviceObj.getString("schedule_device_time");
                int schedule_active  = 0;//deviceObj.getString("schedule_active").equalsIgnoreCase("null")?0:Integer.parseInt(deviceObj.getString("schedule_active"));

                //module_order
//                            obj.put("room_device_id", deviceVO.getRoomDeviceId());
//                            obj.put("module_id",deviceVO.getModuleId());
//                            obj.put("device_id",deviceVO.getDeviceId());


                String room_panel_id = hasObject(deviceObj,"room_panel_id") ? deviceObj.getString("room_panel_id") : "";

                DeviceVO d1 = new DeviceVO();
                d1.setRoom_panel_id(room_panel_id);
                Log.d("RoomIDSCH","id : " + roomId);
                d1.setRoomId(roomId);
                d1.setPanel_id(panel_id);
                d1.setMood_id(mood_id);
                d1.setDeviceName(device_name);
                d1.setModuleId(module_id);
                d1.setSensor(false);
                d1.setRoomDeviceId(room_device_id);
                d1.setDeviceId(device_id);
                d1.setDeviceStatus(device_status);
                d1.setDevice_icon(device_icon);
                d1.setDeviceSpecificValue(device_specific_value);
                d1.setIsActive(is_active);
                d1.setIsAlive(is_alive);
                d1.setAuto_on_off_value(auto_on_off_value);
                d1.setSchedule_value(schedule_value);
                d1.setDeviceType(device_type);
                d1.setOriginal_room_device_id(original_room_device_id);
                d1.setTemperature(""+temperature) ;
                d1.setMode(""+mode); ;
                d1.setPower(""+power);
                d1.setDevice_nameTemp(""+device_nameTemp);
                d1.setIs_locked(is_locked);
                if(!TextUtils.isEmpty(room_name)){
                    d1.setRoomName(room_name);
                }else{
                    d1.setRoomName(roomName);
                }
                if(!TextUtils.isEmpty(panel_name)){
                    d1.setPanel_name(panel_name);
                }else{
                    d1.setPanel_name(panelName);
                }
                d1.setIs_original(is_original);

              /*  if(isParseOriginal){
                    if(d1.getIs_original()==1){
                        deviceList.add(d1);
                    }
                }else{
                    deviceList.add(d1);
                }*/
                deviceList.add(d1);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("", "Exception parseDeviceArray " + e.getMessage());
            }
        }
        return  deviceList;
    }
    public static ArrayList<AutoModeVO> parseAutoModeArray(JSONArray deviceArray){
        ArrayList<AutoModeVO> deviceList = new ArrayList<AutoModeVO>();

        for(int j=0 ; j < deviceArray.length() ; j++){

            try {

                JSONObject deviceObj = deviceArray.getJSONObject(j);

                String auto_on_off_id = deviceObj.getString("auto_on_off_id");
                String auto_on_off_timer = deviceObj.getString("auto_on_off_timer");
                int auto_on_off_status = deviceObj.getInt("auto_on_off_status");
                int is_active = deviceObj.getInt("is_active");

                AutoModeVO d1 = new AutoModeVO();
                d1.setAuto_on_off_id(auto_on_off_id);
                d1.setAuto_on_off_timer(auto_on_off_timer);
                d1.setAuto_on_off_status(auto_on_off_status);
                d1.setIs_active(is_active);

                deviceList.add(d1);

            } catch (Exception e) {
                Log.d("", "Exception parseAutoModeArray " + e.getMessage());
            }
        }
        return  deviceList;
    }
    public static ArrayList<ScheduleVO> parseRoomScheduleArray(JSONArray deviceArray){
        ArrayList<ScheduleVO> deviceList = new ArrayList<ScheduleVO>();

        for(int j=0 ; j < deviceArray.length() ; j++){

            try {


                JSONObject deviceObj = deviceArray.getJSONObject(j);

                int id = j;
                if(deviceObj.has("id")){
                    id = deviceObj.getInt("id");
                }
                String schedule_id = deviceObj.getString("schedule_id");
                int schedule_type  = deviceObj.getInt("schedule_type");
                String schedule_name = deviceObj.getString("schedule_name");
                String room_id = deviceObj.getString("room_id");
                String user_id = deviceObj.getString("user_id");

                String schedule_device_on_time = deviceObj.getString("schedule_device_on_time");
                String schedule_device_off_time = deviceObj.getString("schedule_device_off_time");
                String room_name = "";
                if(deviceObj.has("room_name")){
                    room_name = deviceObj.getString("room_name");
                }
             /*   if(deviceObj.has("room_name")){
                    room_name = deviceObj.getString("room_name");
                }
                else{
                    room_name = deviceObj.getString("mood_name");
                }*/

                String schedule_device_day = deviceObj.getString("schedule_device_day");
                //int schedule_active = deviceObj.getInt("schedule_active");
                int schedule_status = deviceObj.getInt("schedule_status");

                String room_device_id = "";
                if(deviceObj.has("room_device_id")){
                    room_device_id = deviceObj.getString("room_device_id");
                }
//                JSONArray roomDeviceList = deviceObj.getJSONArray("scheduleDeviceList");
//                roomDeviceList.getJSONObject(0).getString("room_device_id");


                int is_timer = deviceObj.getInt("is_timer");
                String timer_on_after = deviceObj.getString("timer_on_after");
                String timer_on_date = deviceObj.getString("timer_on_date");
                String timer_off_after = deviceObj.getString("timer_off_after");
                String timer_off_date = deviceObj.getString("timer_off_date");

                ScheduleVO d1 = new ScheduleVO();
                d1.setSchedule_id(schedule_id);
                d1.setSchedule_type(schedule_type);
                d1.setSchedule_name(schedule_name);
                d1.setSchedule_device_on_time(schedule_device_on_time);
                d1.setSchedule_device_off_time(schedule_device_off_time);
                d1.setSchedule_device_day(schedule_device_day);
                d1.setSchedule_status(schedule_status);
                d1.setRoom_id(room_id);
                d1.setUser_id(user_id);
               // d1.setMood_id(mood_id);
                d1.setRoom_name(room_name);
                d1.setRoom_device_id(room_device_id);
                d1.setId(id);

                d1.setIs_timer(is_timer);
                d1.setTimer_on_after(timer_on_after);
                d1.setTimer_on_date(timer_on_date);
                d1.setTimer_off_after(timer_off_after);
                d1.setTimer_off_date(timer_off_date);

                //d1.setIs_active(is_active);

                deviceList.add(d1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
     //   Log.d("RoomParse","size inner : " + deviceList.size());
        return  deviceList;
    }
    public static ArrayList<CameraVO> parseCameraArray(JSONArray cameraArray){
        ArrayList<CameraVO> cameraList = new ArrayList<CameraVO>();

        for(int j=0 ; j < cameraArray.length() ; j++){

            try {

                JSONObject deviceObj = cameraArray.getJSONObject(j);

                String camera_id = hasObject(deviceObj,"camera_id") ? deviceObj.getString("camera_id") : "";
                String user_id = hasObject(deviceObj,"user_id") ? deviceObj.getString("user_id") : "";
                String home_controller_device_id = hasObject(deviceObj,"home_controller_device_id") ? deviceObj.getString("home_controller_device_id") : "";
                String camera_name = hasObject(deviceObj,"camera_name") ? deviceObj.getString("camera_name") : "";

                String camera_ip = hasObject(deviceObj,"camera_ip") ? deviceObj.getString("camera_ip") : "";
                String camera_videopath = hasObject(deviceObj,"camera_videopath") ? deviceObj.getString("camera_videopath") : "";
                String camera_icon = hasObject(deviceObj,"camera_icon") ? deviceObj.getString("camera_icon") : "";

                String userName = hasObject(deviceObj,"user_name") ? deviceObj.getString("user_name") : "";
                String camera_url = hasObject(deviceObj,"camera_url") ? deviceObj.getString("camera_url") : "";
                String password = hasObject(deviceObj,"password") ? deviceObj.getString("password") : "";
                int is_active  = hasObject(deviceObj,"is_active") ? deviceObj.getInt("is_active") : 1; //default 0
                String camera_vpn_port = hasObject(deviceObj,"camera_vpn_port") ? deviceObj.getString("camera_vpn_port") : "";
                String is_unread = hasObject(deviceObj,"is_unread") ? deviceObj.getString("is_unread") : "";

                CameraVO cameraVO = new CameraVO();
                cameraVO.setCamera_id(camera_id);
                cameraVO.setUserId(user_id);
                cameraVO.setHomeControllerDeviceId(home_controller_device_id);
                cameraVO.setCamera_name(camera_name);
                cameraVO.setCamera_ip(camera_ip);
                cameraVO.setCamera_videopath(camera_videopath);
                cameraVO.setUserName(userName);
                cameraVO.setPassword(password);
                cameraVO.setIsActive(is_active);
                cameraVO.setCamera_icon(camera_icon);
                cameraVO.setCamera_vpn_port(camera_vpn_port);
                cameraVO.setCamera_url(camera_url);
                cameraVO.setIs_unread(is_unread);

                cameraList.add(cameraVO);
               // cameraList.add(cameraVO);
               // cameraList.add(cameraVO);
               // cameraList.add(cameraVO);

            } catch (Exception e) {
                Log.d("", "Exception parseScheduleArray " + e.getMessage());
            }
        }
        return  cameraList;
    }
   /* {
        "auto_on_off_id": "1515493307456_6t6WER234",
            "auto_on_off_timer": "18:00",
            "auto_on_off_status": 1,
            "is_active": 1
    }
    ],
            "schedule_details": [
    {
        "schedule_id": "1235493307236_e4rpwfzNM",
            "schedule_name": "Test - 1",
            "schedule_device_time": "11:00",
            "schedule_device_month": null,
            "schedule_device_date": null,
            "schedule_device_day": "1,2,3",
            "schedule_device_status": 1,
            "is_active": 1
    },*/

    public static ArrayList<MoodVO> parseMoodArray(JSONArray moodArray){
        ArrayList<MoodVO> moodList = new ArrayList<>();

        for(int i=0;i<moodArray.length();i++){
            try {
                JSONObject objMood = moodArray.getJSONObject(i);
                MoodVO room = new MoodVO();

                if(objMood.has("id")){
                    room.setId(objMood.getInt("id"));
                }
                room.setMood_id(objMood.getString("mood_id"));
                room.setMood_name(objMood.getString("mood_name"));
                room.setMood_icon(objMood.getString("mood_icon"));
                room.setMood_status(objMood.getInt("mood_status"));
                room.setIs_schedule(objMood.getInt("is_schedule"));

                JSONArray moodDeviceArray = objMood.getJSONArray("moodDeviceList");
                if(moodDeviceArray.length()>0){

                }
                JSONObject moodDeviceObj = moodDeviceArray.getJSONObject(0);

                room.setMood_device_id(moodDeviceObj.getString("mood_device_id"));
                room.setRoom_device_id(moodDeviceObj.getString("room_device_id"));

               /* mood_id": "1516432650844_S1QMTwxBz",
                "mood_name": "Work",
                        "mood_icon": "work",
                        "mood_status": 0,*/
                moodList.add(room);
                Log.d("","objRoom " + objMood.toString() );

//                JSONArray panelArray = roomObj.getJSONArray("panelList");
//                ArrayList<PanelVO> panelList = JsonHelper.parsePanelArray(panelArray); //new ArrayList<PanelVO>();
//                room.setPanelList(panelList);
                ArrayList<DeviceVO>  deviceList= new ArrayList<>();
                JSONArray deviceInfoArray = moodDeviceObj.getJSONArray("deviceInfo");
                /*for(int j =0 ;j<deviceInfoArray.length();j++){
                    JSONObject deviceObj = moodDeviceArray.getJSONObject(j).getJSONArray("deviceInfo").getJSONObject(0) ;//objRoom.getJSONObject("roomList");
                    ArrayList<DeviceVO>  deviceListTemp = parseDeviceArray(deviceInfoArray);
                    deviceList.addAll(deviceListTemp);
                }*/
               // ArrayList<DeviceVO>  deviceListTemp = parseDeviceArray(deviceInfoArray,"","","",false, "");
              //  sortDeviceV0(deviceListTemp);
              //  deviceList.addAll(deviceListTemp);

                ArrayList<PanelVO> panelList = new ArrayList<>();
                PanelVO panel = new PanelVO();
                panel.setPanelName("Devices");
                panel.setPanelId(room.getMood_id());

               // sortMoodDevice(deviceList);
                sortDeviceV0(deviceList);

                panel.setDeviceList(deviceList);
                panelList.add(panel);
                room.setPanelList(panelList);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(""," JSonException e " + e.getMessage() );
            }
        }
        return  moodList;
    }

    /*
    * Parse orignal room device array
    * */


    //ArrayList<DeviceVO> moodList = new ArrayList<>();
    private static void sortMoodDevice(ArrayList<DeviceVO> list){
        Collections.sort(list, new Comparator<DeviceVO>() {
            @Override
            public int compare(DeviceVO o1, DeviceVO o2) {
                return o1.getMood_id() - o2.getMood_id();
            }
        });
    }
    private static void sortDevice(ArrayList<DevicePanelVO> list){
        Collections.sort(list, new Comparator<DevicePanelVO>() {
            @Override
            public int compare(DevicePanelVO o1, DevicePanelVO o2) {
                return o1.getDeviceId() - o2.getDeviceId();
            }
        });
    }

    private static void sortDeviceV0(ArrayList<DeviceVO> list){
        Collections.sort(list, new Comparator<DeviceVO>() {
            @Override
            public int compare(DeviceVO o1, DeviceVO o2) {
                return o1.getDeviceId().compareTo(o2.getDeviceId());
            }
        });
    }

}
