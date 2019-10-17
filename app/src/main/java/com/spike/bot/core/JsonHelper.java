package com.spike.bot.core;

import android.text.TextUtils;
import android.util.Log;

import com.spike.bot.ChatApplication;
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
                JSONObject roomObj = roomArray.getJSONObject(i);

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

                /*room list */
                RoomVO room = new RoomVO();
                room.setSmart_remote_number(roomObj.optString("smart_remote_number"));
                room.setRoomName(roomObj.optString("room_name"));
                room.setRoom_order(roomObj.optInt("room_order"));
                room.setRoomId(roomObj.optString("room_id"));
                room.setRoom_status(roomObj.optString("room_status").equals("y") ? 0:1);
                room.setSensor_panel(roomObj.optString("sensor_panel"));
                if(roomObj.has("device_count")){
                    room.setDevice_count(roomObj.optString("device_count"));
                }else{
                    room.setDevice_count("0");
                }

                if(roomObj.has("is_original")){
                    room.setIs_original(roomObj.optInt("is_original"));
                }

                if(roomObj.has("room_icon")){
                    room.setRoom_icon(roomObj.optString("room_icon"));
                }

                room.setIs_unread(""+roomObj.optString("is_unread"));
                room.setTemp_sensor_value(""+roomObj.optString("temp_sensor_value"));
                room.setDoor_sensor_value(""+roomObj.optString("door_sensor_value"));

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

                for(int j=0; j<panelList.size(); j++){
                    for(int k=0; k<panelList.get(j).getDeviceList().size(); k++){
                        if(panelList.get(j).getDeviceList().get(k).getDeviceType().equalsIgnoreCase("-1")){
                            room.setIsheavyload(true);
                            break;
                        }

                    }

                }
                roomList.add(room);

                room.setPanelList(panelList);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  roomList;
    }

    /*exting panel */
    public static ArrayList<RoomVO> parseExistPanelArray(JSONArray roomArray){
        ArrayList<RoomVO> roomDeviceList = new ArrayList<>();

        for(int i=0;i<roomArray.length();i++){
            try {
                JSONObject objRoom = roomArray.getJSONObject(i);

                RoomVO room = new RoomVO();
                room.setRoomId(objRoom.optString("room_id"));
                room.setPanel_id(objRoom.optString("module_id"));
                room.setModule_name(objRoom.optString("module_name"));

                JSONObject roomArrayList = objRoom.getJSONObject("roomList");

                room.setRoomName(roomArrayList.optString("room_name"));

                if(roomArrayList.has("panel_id")){
                    room.setPanel_id(roomArrayList.optString("panel_id"));
                }
                if(roomArrayList.has("panel_name")){
                    room.setPanel_name(roomArrayList.optString("panel_name"));
                }

                if(roomArrayList.has("module_id")){
                    room.setModule_id(roomArrayList.optString("module_id"));
                }

                ArrayList<DevicePanelVO> deviceList = new ArrayList<DevicePanelVO>();

                JSONArray jsonArrayRoomList = null;
                if(objRoom.has("deviceList")){
                    jsonArrayRoomList = objRoom.getJSONArray("deviceList");

                    for(int d=0;d<jsonArrayRoomList.length();d++){
                        JSONObject deviceObject = jsonArrayRoomList.getJSONObject(d);
                        DevicePanelVO deviceVO = new DevicePanelVO();
                        deviceVO.setModuleId(deviceObject.optString("module_id"));
                        deviceVO.setDeviceId(deviceObject.optInt("device_id"));
                        deviceVO.setDeviceName(deviceObject.optString("device_name"));
                        deviceVO.setDevice_icon(deviceObject.optString("device_icon"));
                        deviceVO.setRoom_panel_id(""+deviceObject.optString("room_panel_id"));

                        if(deviceObject.has("original_room_device_id")){
                            deviceVO.setOriginal_room_device_id(deviceObject.optString("original_room_device_id"));
                        }

                        if(deviceObject.has("room_device_id")){
                            deviceVO.setRoomDeviceId(deviceObject.optString("room_device_id"));
                        }

                        if(deviceObject.has("panel_id")){
                            deviceVO.setPanel_id(deviceObject.optString("panel_id"));
                        }else{
                            if(roomArrayList.has("panel_id")){
                                deviceVO.setPanel_id(roomArrayList.optString("panel_id"));
                            }
                        }

                        if(deviceObject.has("device_status")){
                            deviceVO.setDeviceStatus(deviceObject.optString("device_status"));
                        }

                        if(deviceObject.has("device_specific_value")){
                            deviceVO.setDeviceSpecificValue(deviceObject.optString("device_specific_value"));
                        }
                        if(deviceObject.has("device_type")){
                            deviceVO.setDeviceType(deviceObject.optString("device_type"));
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

                PanelVO panelVO = new PanelVO();
                panelVO.setPanel_status(objPanel.optString("panel_status").equals("y")?0:1);
                panelVO.setPanelId(hasObject(objPanel,"panel_id") ? objPanel.optString("panel_id") : "");
                panelVO.setPanelName(hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "");
                panelVO.setModule_id(hasObject(objPanel,"module_id") ? objPanel.optString("module_id") : "");
                panelVO.setPanel_type(hasObject(objPanel,"panel_type") ? objPanel.optInt("panel_type") : 0);
                panelVO.setRoom_panel_id(hasObject(objPanel,"room_panel_id") ? objPanel.optString("room_panel_id") : "");
                panelVO.setPanel_name_sub(hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "");

                /*device panel list*/
                if(objPanel.has("deviceList")){

                    if(hasObject(objPanel,"is_original")){
                        panelVO.setIs_original(objPanel.optInt("is_original"));
                    }
                    panelVO.setActivePanel(true);

                    JSONArray deviceArray = objPanel.getJSONArray("deviceList");
                    panelVO.setSensorPanel(false);
                    ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                    deviceList = JsonHelper.parseDeviceArray(deviceArray,roomName,hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "",roomId,isParseOriginal,hasObject(objPanel,"panel_id") ? objPanel.optString("panel_id") : "");
                    roomVO.setRoomOnOff(true);
                    panelVO.setDeviceList(deviceList);
                    isDevicePanel = true;

                }

                /*IR blaster remote array add */
                if(objPanel.has("remoteList")){
                    panelVO.setSensorPanel(true);

                    JSONArray sensorArray = objPanel.getJSONArray("remoteList");
                    ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                    deviceList = JsonHelper.parseIrRemoteArray(roomVO,panelVO,sensorArray,roomName,hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "",roomId, isParseOriginal,hasObject(objPanel,"panel_id") ? objPanel.optString("panel_id") : "");
                    panelVO.setDeviceList(deviceList);
                    panelVO.setRemoteAvabile(true);
                    if(isDevicePanel){
                        isDevicePanel = true;
                    }
                }

                /*all type sensor like gas, temp, door,blaster*/
                if(objPanel.has("sensorList")){
                    JSONArray sensorArray = objPanel.getJSONArray("sensorList");
                    if(sensorArray!=null && sensorArray.length()>0) {
                        panelVO.setSensorPanel(true);

                        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                        deviceList = JsonHelper.parseSensorArray(roomVO, panelVO, sensorArray, roomName, hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "", roomId, isParseOriginal, hasObject(objPanel,"panel_id") ? objPanel.optString("panel_id") : "");
                        panelVO.setDeviceList(deviceList);
                        boolean isRemote = false;
                        for (int i = 0; i < deviceList.size(); i++) {
                            if (deviceList.get(i).getDeviceType().equals("2")) {
                                isRemote = true;
                                break;
                            }
                        }
                        panelVO.setRemoteAvabile(isRemote);
                        if (isDevicePanel) {
                            isDevicePanel = true;
                        }
                    }
                }


                if(objPanel.has("curtainList")){
                    JSONArray deviceArray = objPanel.getJSONArray("curtainList");
                    if(deviceArray!=null && deviceArray.length()>0){
                        panelVO.setSensorPanel(false);

                        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();
                        deviceList = JsonHelper.curtainList(deviceArray,roomName,hasObject(objPanel,"panel_name") ? objPanel.optString("panel_name") : "",roomId,isParseOriginal,hasObject(objPanel,"panel_id") ? objPanel.optString("panel_id") : "");
                        panelVO.setDeviceList(deviceList);
                        panelVO.setRemoteAvabile(false);
                        panelVO.setPanel_type(5);
                        if(isDevicePanel){
                            isDevicePanel = true;
                        }
                    }

                }

                roomVO.setDevicePanel(isDevicePanel);

                if(!TextUtils.isEmpty(objPanel.optString("panel_id"))){
                    Log.d("isPanelActive","Panel isActivePanel :" + panelVO.isActivePanel());
                    panelList.add(panelVO);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return panelList;
    }

    private static ArrayList<DeviceVO>  curtainList(JSONArray deviceArray, String roomName, String panel_name, String roomId, boolean isParseOriginal, String panel_id) {
        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        for(int j=0 ; j < deviceArray.length() ; j++){

            try {

                //                   "panel_device_id": "1571306325982_cFx-xCwId-",
                //                "device_id": "1571306319165_vkbMfYd3o4",
                //                "device_name": "curtain1",
                //                "device_icon": "curtain",
                //                "device_status": "0",
                //                "device_sub_status": "0",
                //                "device_type": "curtain",
                //                "device_identifier": "1571306319132_cgEmC6Ua6",
                //                "device_meta": {
                //                  "alert_min_temp": "",
                //                  "alert_max_temp": "",
                //                  "alert_min_humidity": "",
                //                  "alert_max_humidity": ""
                //                },
                //                "module_id": "1571306319132_cgEmC6Ua6",
                //                "is_active": "y",
                //                "module_type": "curtain",
                //

                JSONObject deviceObj = deviceArray.getJSONObject(j);

                DeviceVO d1 = new DeviceVO();
                d1.setPanel_device_id(deviceObj.optString("panel_device_id"));
                d1.setRoom_panel_id(hasObject(deviceObj,"room_panel_id") ? deviceObj.optString("room_panel_id") : "");
                d1.setRoomId(roomId);
                d1.setPanel_id(panel_id);
                d1.setDeviceName(deviceObj.optString("device_name"));
                d1.setModuleId(deviceObj.optString("module_id"));
                d1.setSensor(false);
                d1.setRoomDeviceId(deviceObj.optString("room_id"));
                d1.setDeviceId(deviceObj.optString("device_id"));
                d1.setDeviceStatus(deviceObj.optInt("device_status"));
                d1.setDeviceType(deviceObj.optString("device_icon"));
                d1.setDevice_icon(deviceObj.optString("device_icon"));
                d1.setIsActive(deviceObj.has("is_active")? deviceObj.optInt("is_active"):0);
                d1.setAuto_on_off_value(deviceObj.has("auto_on_off_value")? deviceObj.optInt("auto_on_off_value"):0);
                d1.setOriginal_room_device_id(deviceObj.has("device_id") ? deviceObj.optString("device_id") : "");
                d1.setRoomName(roomName);
                d1.setPanel_name(panel_name);
                d1.setIs_original(deviceObj.optInt("is_original"));
                deviceList.add(d1);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("", "Exception parseDeviceArray " + e.getMessage());
            }
        }
        return deviceList;
    }

    public static ArrayList<DeviceVO> parseIrRemoteArray(RoomVO roomVO, PanelVO panelVO, JSONArray sensorArray, String roomName, String panel_name, String roomId, boolean isParseOriginal, String panel_id){

        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        int countInActive = 0;
        boolean flagRemote=false;

        for(int j=0 ; j < sensorArray.length() ; j++){

            try {

                JSONObject deviceObj = sensorArray.getJSONObject(j);

                int is_active = hasObject(deviceObj,"is_active") ? deviceObj.optInt("is_active") : 0;
                if(is_active == 0){
                    Log.d("isPanelActive","Active : " + is_active);
                    countInActive ++;
                }

                String module_id = "";
                String sensor_id = "";
                String sensor_name = "";
                String ir_blaster_id = "";

                String room_panel_id = hasObject(deviceObj,"room_panel_id") ? deviceObj.optString("room_panel_id") : "";

                if(TextUtils.isEmpty(room_panel_id)){
                    room_panel_id=hasObject(deviceObj,"panel_id") ? deviceObj.optString("panel_id") : "";
                }

                DeviceVO d1 = new DeviceVO();
                d1.setSpeed(hasObject(deviceObj,"mode") ? deviceObj.optString("mode") : "");
                d1.setTemprature(hasObject(deviceObj,"temperature") ? deviceObj.optInt("temperature") : 0);
                d1.setRoom_panel_id(room_panel_id);
                d1.setRemote_device_id(hasObject(deviceObj,"remote_device_id") ? deviceObj.optString("remote_device_id") : "");
                d1.setRemote_status(hasObject(deviceObj,"remote_status") ? deviceObj.optString("remote_status") : "");
                sensor_name = deviceObj.optString("sensor_name");
                Log.d("deviceObj","deviceObj : " + sensor_name);

//                if(sensor_type.equalsIgnoreCase("remote")){
                if(deviceObj.optString("sensor_type").equalsIgnoreCase("irblaster")){
                    ir_blaster_id = deviceObj.optString("ir_blaster_id");
                    sensor_id = deviceObj.optString("sensor_id");
                    d1.setIr_blaster_id(ir_blaster_id);
                    module_id = deviceObj.has("module_id") ? deviceObj.optString("module_id") : "";

                }else{
                    sensor_id = deviceObj.optString("sensor_id");
                    module_id = deviceObj.has("module_id") ? deviceObj.optString("module_id") : "";
                }


                String is_original = deviceObj.has("is_original") ? deviceObj.optString("is_original") : "";

                d1.setPanel_id(panel_id);
                d1.setDeviceId(deviceObj.has("device_id") ? deviceObj.optString("device_id") : "");
                d1.setDeviceStatus(deviceObj.has("device_status") ? deviceObj.optInt("device_status") : 0);
                d1.setDeviceType(deviceObj.has("device_type") ? deviceObj.optString("device_type"):"");
                d1.setDeviceSpecificValue(deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"");
                d1.setAuto_on_off_value(deviceObj.has("auto_on_off_value")? deviceObj.optInt("auto_on_off_value"):0);
                d1.setSchedule_value(deviceObj.has("schedule_value")? deviceObj.optInt("schedule_value"):0);
                d1.setDevice_icon(deviceObj.has("device_icon")  ? deviceObj.optString("device_icon") : "");
                d1.setIsAlive(deviceObj.has("is_alive")? deviceObj.optInt("is_alive"):0);
                d1.setOriginal_room_device_id(deviceObj.has("original_room_device_id") ? deviceObj.optString("original_room_device_id") : "");
                d1.setRoomDeviceId(deviceObj.has("room_device_id") ? deviceObj.optString("room_device_id") : "");
                d1.setRoomName(deviceObj.has("room_name")? deviceObj.optString("room_name"):"");

                d1.setSensor(true);
                d1.setSensor_id(sensor_id);
                d1.setModuleId(module_id);
                d1.setSensor_name(sensor_name);
                d1.setSensor_voltage(hasObject(deviceObj,"sensor_voltage") ? deviceObj.optString("sensor_voltage") : "");
                d1.setSensor_icon(deviceObj.optString("sensor_icon"));
                d1.setDoor_sensor_status(hasObject(deviceObj,"door_sensor_status") ? deviceObj.optString("door_sensor_status") : "");
                d1.setSensor_type(deviceObj.optString("sensor_type"));
                d1.setIsActive(is_active);

                d1.setIs_unread(hasObject(deviceObj,"is_unread") ? deviceObj.optString("is_unread") : "");
                d1.setCreated_date(hasObject(deviceObj,"created_date") ? deviceObj.optString("created_date") : "");
                d1.setTemp_in_c(hasObject(deviceObj,"temp_in_C") ? deviceObj.optString("temp_in_C") : "");
                d1.setTemp_in_f(hasObject(deviceObj,"temp_in_F") ? deviceObj.optString("temp_in_F") : "");
                d1.setIs_in_c(hasObject(deviceObj,"is_in_C") ? deviceObj.optString("is_in_C") : "");
                d1.setTo_use(hasObject(deviceObj,"to_use") ? deviceObj.optString("to_use") : "");
                if(!TextUtils.isEmpty(is_original)){
                    d1.setIs_original(Integer.parseInt(is_original));
                }


                d1.setRoomName(roomName);
                d1.setRoomId(roomId);

                if(deviceObj.optString("sensor_type").equalsIgnoreCase("irblaster")){
                    flagRemote=true;
                }

                deviceList.add(d1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        panelVO.setActivePanel(flagRemote);

        return deviceList;
    }



    public static ArrayList<DeviceVO> parseSensorArray(RoomVO roomVO, PanelVO panelVO, JSONArray sensorArray, String roomName, String panel_name, String roomId, boolean isParseOriginal, String panel_id){

        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        int countInActive = 0;
        boolean flagRemote=false;

        for(int j=0 ; j < sensorArray.length() ; j++){

            try {

                //"panel_device_id": "1571296767890_YQaLl0WaD-",
                //                "device_id": "1571296762190_Vj-6HmIAx_",
                //                "device_name": "door_sensor1",
                //                "device_icon": "door_sensor",
                //                "device_status": "0",
                //                "device_sub_status": "0",
                //                "device_type": "door_sensor",
                //                "device_identifier": "1571296762157_Y5bWTJsfs",
                //                "device_meta": {
                //                  "alert_min_temp": "",
                //                  "alert_max_temp": "",
                //                  "alert_min_humidity": "",
                //                  "alert_max_humidity": ""
                //                },
                //                "module_id": "1571296762157_Y5bWTJsfs",
                //                "is_active": "y",
                //                "module_type": "door_sensor",
                JSONObject deviceObj = sensorArray.getJSONObject(j);

                String sensor_type = deviceObj.optString("device_type");

                int is_active = hasObject(deviceObj,"is_active") ? deviceObj.optInt("is_active") : 0;

                int door_subtype=0;
                if(TextUtils.isEmpty(deviceObj.optString("device_type")) || deviceObj.optString("device_type").equals("null")){
                    door_subtype = 0;
                }else {
                    door_subtype =  deviceObj.optInt("door_subtype");
                }


                int lock_id=-1;
                if(TextUtils.isEmpty(deviceObj.optString("lock_id"))){
                    lock_id =-1;
                }else {
                    lock_id = deviceObj.optInt("lock_id");
                }


                int door_lock_status=0;

                if(deviceObj.optString("door_lock_status").equals("null") ||TextUtils.isEmpty(deviceObj.optString("door_lock_status"))){
                    door_lock_status = 0;
                }else {
                    door_lock_status = hasObject(deviceObj,"door_lock_status") ? deviceObj.optInt("door_lock_status") : 0;
                }

                int is_door_active=0,is_lock_active=0 ;
                if(deviceObj.optString("is_door_active").equals("null") ||TextUtils.isEmpty(deviceObj.optString("is_door_active"))){
                    is_door_active = 0;
                }else {
                    is_door_active = hasObject(deviceObj,"is_door_active") ? deviceObj.optInt("is_door_active") : 0;
                }


                if(deviceObj.optString("is_lock_active").equals("null") ||TextUtils.isEmpty(deviceObj.optString("is_lock_active"))){
                    is_lock_active = 0;
                }else {
                    is_lock_active = hasObject(deviceObj,"is_lock_active") ? deviceObj.optInt("is_lock_active") : 0;
                }


                if(is_active == 0){
                    countInActive ++;
                }

                String module_id = "";
                String sensor_id = "";
                String sensor_name = "";
                String ir_blaster_id = "";

                String room_panel_id = hasObject(deviceObj,"room_panel_id") ? deviceObj.optString("room_panel_id") : "";

                if(TextUtils.isEmpty(room_panel_id)){
                    room_panel_id=hasObject(deviceObj,"panel_id") ? deviceObj.optString("panel_id") : "";
                }

                DeviceVO d1 = new DeviceVO();
                d1.setPanel_device_id(deviceObj.optString("panel_device_id"));
                d1.setSpeed(hasObject(deviceObj,"mode") ? deviceObj.optString("mode") : "");
                d1.setIs_door_active(is_door_active);
                d1.setIs_lock_active(is_lock_active);
                d1.setTemprature(hasObject(deviceObj,"temperature") ? deviceObj.optInt("temperature") : 0);
                d1.setRoom_panel_id(room_panel_id);
                d1.setRemote_device_id(hasObject(deviceObj,"remote_device_id") ? deviceObj.optString("remote_device_id") : "");
                d1.setRemote_status(hasObject(deviceObj,"remote_status") ? deviceObj.optString("remote_status") : "");
                d1.setDoor_subtype(door_subtype);
                d1.setLock_id(lock_id);
                d1.setDoor_lock_status(door_lock_status);
                sensor_name = deviceObj.optString("device_name");

                if(sensor_type.equalsIgnoreCase("irblaster")){
                    ir_blaster_id = deviceObj.optString("ir_blaster_id");
                    sensor_id = deviceObj.optString("sensor_id");
                    d1.setIr_blaster_id(ir_blaster_id);
                    module_id = deviceObj.has("module_id") ? deviceObj.optString("module_id") : "";

                }else{
                    sensor_id = deviceObj.optString("sensor_id");
                    module_id = deviceObj.has("module_id") ? deviceObj.optString("module_id") : "";
                }

                String is_original = deviceObj.has("is_original") ? deviceObj.optString("is_original") : "";


                d1.setPanel_id(panel_id);
                d1.setDeviceId(deviceObj.has("device_id") ? deviceObj.optString("device_id") : "2");
                d1.setDeviceStatus(deviceObj.has("device_status") ? deviceObj.optInt("device_status") : 0);
                d1.setDeviceType(deviceObj.has("device_type") ? deviceObj.optString("device_type"):"");
                d1.setDeviceSpecificValue(deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"");
                d1.setAuto_on_off_value(deviceObj.has("auto_on_off_value")? deviceObj.optInt("auto_on_off_value"):0);
                d1.setSchedule_value(deviceObj.has("schedule_value")? deviceObj.optInt("schedule_value"):0);
                d1.setDevice_icon(deviceObj.has("device_icon")  ? deviceObj.optString("device_icon") : "");
                d1.setIsAlive(deviceObj.has("is_alive")? deviceObj.optInt("is_alive"):0);
                d1.setOriginal_room_device_id(deviceObj.has("original_room_device_id") ? deviceObj.optString("original_room_device_id") : "");
                d1.setRoomDeviceId(deviceObj.has("room_device_id") ? deviceObj.optString("room_device_id") : "");
                d1.setRoomName(deviceObj.has("room_name")? deviceObj.optString("room_name"):"");

                d1.setSensor(true);
                d1.setSensor_id(sensor_id);
                d1.setModuleId(module_id);
                d1.setSensor_name(sensor_name);
                d1.setSensor_voltage(hasObject(deviceObj,"sensor_voltage") ? deviceObj.optString("sensor_voltage") : "");
                d1.setSensor_icon(deviceObj.optString("device_icon"));
                d1.setDoor_sensor_status(hasObject(deviceObj,"device_sub_status") ? deviceObj.optString("device_sub_status") : "");
                d1.setSensor_type(sensor_type);
                d1.setIsActive(is_active);

                d1.setIs_unread(hasObject(deviceObj,"is_unread") ? deviceObj.optString("is_unread") : "");
                d1.setCreated_date(hasObject(deviceObj,"created_date") ? deviceObj.optString("created_date") : "");
                d1.setTemp_in_c(hasObject(deviceObj,"temp_in_C") ? deviceObj.optString("temp_in_C") : "");
                d1.setTemp_in_f(hasObject(deviceObj,"temp_in_F") ? deviceObj.optString("temp_in_F") : "");
                d1.setIs_in_c(hasObject(deviceObj,"is_in_C") ? deviceObj.optString("is_in_C") : "");
                d1.setHumidity(deviceObj.optString("humidity"));
                d1.setTo_use(hasObject(deviceObj,"to_use") ? deviceObj.optString("to_use") : "");

                if(!TextUtils.isEmpty(is_original)){
                    d1.setIs_original(Integer.parseInt(is_original));
                }


                d1.setRoomName(roomName);
                d1.setRoomId(roomId);

                if(sensor_type.equalsIgnoreCase("irblaster")){
                    flagRemote=true;
                }
                deviceList.add(d1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        panelVO.setActivePanel(flagRemote);

        return deviceList;
    }

    public static ArrayList<DeviceVO> parseDeviceArray(JSONArray deviceArray, String roomName, String panelName, String roomId, boolean isParseOriginal, String panel_id){
        ArrayList<DeviceVO> deviceList = new ArrayList<DeviceVO>();

        for(int j=0 ; j < deviceArray.length() ; j++){

            try {

                JSONObject deviceObj = deviceArray.getJSONObject(j);

                int mood_id = 0;
                if(deviceObj.has("id")){
                    mood_id = deviceObj.optInt("id");
                }
                int temperature =  deviceObj.has("temperature")? deviceObj.optInt("temperature"):0;
                String mode =  deviceObj.has("mode")? deviceObj.optString("mode"):"";
                String power =  deviceObj.has("power")? deviceObj.optString("power"):"";

                DeviceVO d1 = new DeviceVO();
                d1.setPanel_device_id(deviceObj.optString("panel_device_id"));
                d1.setRoom_panel_id(hasObject(deviceObj,"room_panel_id") ? deviceObj.optString("room_panel_id") : "");
                d1.setRoomId(roomId);
                d1.setPanel_id(panel_id);
                d1.setMood_id(mood_id);
                d1.setDeviceName(deviceObj.optString("device_name"));
                d1.setModuleId(deviceObj.optString("module_id"));
                d1.setSensor(false);
                d1.setRoomDeviceId(deviceObj.optString("room_device_id"));
                d1.setDeviceId(deviceObj.optString("device_id"));
                d1.setDeviceStatus(deviceObj.optInt("device_status"));
                d1.setDevice_icon(deviceObj.optString("device_icon"));
                d1.setDeviceSpecificValue(deviceObj.has("device_specific_value")?deviceObj.optString("device_specific_value"):"");
                d1.setIsActive(deviceObj.has("is_active")? deviceObj.optInt("is_active"):0);
                d1.setIsAlive(deviceObj.has("is_alive")? deviceObj.optInt("is_alive"):0);
                d1.setAuto_on_off_value(deviceObj.has("auto_on_off_value")? deviceObj.optInt("auto_on_off_value"):0);
                d1.setSchedule_value(deviceObj.has("schedule_value")? deviceObj.optInt("schedule_value"):0);
                d1.setDeviceType(deviceObj.has("device_type")?deviceObj.optString("device_type"):"");
                d1.setOriginal_room_device_id(deviceObj.has("original_room_device_id") ? deviceObj.optString("original_room_device_id") : "");
                d1.setTemperature(""+temperature) ;
                d1.setMode(""+mode); ;
                d1.setPower(""+power);
                d1.setDevice_nameTemp(""+deviceObj.optString("device_name"));
                d1.setIs_locked(deviceObj.has("is_locked")? deviceObj.optInt("is_locked"):0);
                d1.setDevice_identifier(deviceObj.optString("device_identifier"));
                if(!TextUtils.isEmpty(deviceObj.has("room_name")? deviceObj.optString("room_name"):"")){
                    d1.setRoomName(deviceObj.has("room_name")? deviceObj.optString("room_name"):"");
                }else{
                    d1.setRoomName(roomName);
                }
                if(!TextUtils.isEmpty(deviceObj.has("panel_name")? deviceObj.optString("panel_name"):"")){
                    d1.setPanel_name(deviceObj.has("panel_name")? deviceObj.optString("panel_name"):"");
                }else{
                    d1.setPanel_name(panelName);
                }
                d1.setIs_original(deviceObj.has("is_alive")? deviceObj.optInt("is_original"):0);


                deviceList.add(d1);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("", "Exception parseDeviceArray " + e.getMessage());
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
                    id = deviceObj.optInt("id");
                }

                int is_timer = deviceObj.optInt("is_timer");
                String timer_on_after = deviceObj.optString("timer_on_after");
                String timer_on_date = deviceObj.optString("timer_on_date");
                String timer_off_after = deviceObj.optString("timer_off_after");
                String timer_off_date = deviceObj.optString("timer_off_date");

                ScheduleVO d1 = new ScheduleVO();
                d1.setSchedule_id(deviceObj.optString("schedule_id"));
                d1.setSchedule_type(deviceObj.optInt("schedule_type"));
                d1.setSchedule_name(deviceObj.optString("schedule_name"));
                d1.setSchedule_device_on_time(deviceObj.optString("schedule_device_on_time"));
                d1.setSchedule_device_off_time(deviceObj.optString("schedule_device_off_time"));
                d1.setSchedule_device_day(deviceObj.optString("schedule_device_day"));
                d1.setSchedule_status(deviceObj.optInt("schedule_status"));
                d1.setRoom_id(deviceObj.optString("room_id"));
                d1.setUser_id(deviceObj.optString("user_id"));
                d1.setRoom_name(deviceObj.optString("room_name"));
                d1.setRoom_device_id(deviceObj.optString("room_device_id"));
                d1.setId(id);

                d1.setIs_timer(is_timer);
                d1.setTimer_on_after(timer_on_after);
                d1.setTimer_on_date(timer_on_date);
                d1.setTimer_off_after(timer_off_after);
                d1.setTimer_off_date(timer_off_date);


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

                CameraVO cameraVO = new CameraVO();
                cameraVO.setCamera_id(hasObject(deviceObj,"camera_id") ? deviceObj.optString("camera_id") : "");
                cameraVO.setUserId(hasObject(deviceObj,"user_id") ? deviceObj.optString("user_id") : "");
                cameraVO.setHomeControllerDeviceId(hasObject(deviceObj,"home_controller_device_id") ? deviceObj.optString("home_controller_device_id") : "");
                cameraVO.setCamera_name(hasObject(deviceObj,"camera_name") ? deviceObj.optString("camera_name") : "");
                cameraVO.setCamera_ip(hasObject(deviceObj,"camera_ip") ? deviceObj.optString("camera_ip") : "");
                cameraVO.setCamera_videopath(hasObject(deviceObj,"camera_videopath") ? deviceObj.optString("camera_videopath") : "");
                cameraVO.setUserName(hasObject(deviceObj,"user_name") ? deviceObj.optString("user_name") : "");
                cameraVO.setPassword(hasObject(deviceObj,"password") ? deviceObj.optString("password") : "");
                cameraVO.setIsActive(hasObject(deviceObj,"is_active") ? deviceObj.optInt("is_active") : 1);
                cameraVO.setCamera_icon(hasObject(deviceObj,"camera_icon") ? deviceObj.optString("camera_icon") : "");
                cameraVO.setCamera_vpn_port(hasObject(deviceObj,"camera_vpn_port") ? deviceObj.optString("camera_vpn_port") : "");
                cameraVO.setCamera_url(hasObject(deviceObj,"camera_url") ? deviceObj.optString("camera_url") : "");
                cameraVO.setTotal_unread(hasObject(deviceObj,"total_unread") ? deviceObj.optString("total_unread") : "");
                cameraVO.setIs_unread(hasObject(deviceObj,"is_unread") ? deviceObj.optString("is_unread") : "");

                cameraList.add(cameraVO);

            } catch (Exception e) {
                Log.d("", "Exception parseScheduleArray " + e.getMessage());
            }
        }
        return  cameraList;
    }


    private static void sortDevice(ArrayList<DevicePanelVO> list){
        Collections.sort(list, new Comparator<DevicePanelVO>() {
            @Override
            public int compare(DevicePanelVO o1, DevicePanelVO o2) {
                return o1.getDeviceId() - o2.getDeviceId();
            }
        });
    }


}
