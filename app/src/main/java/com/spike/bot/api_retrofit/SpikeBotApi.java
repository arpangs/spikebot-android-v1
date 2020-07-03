package com.spike.bot.api_retrofit;


import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.kp.core.DateHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.IRRemoteOnOffReq;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpikeBotApi {


    /*dev arp add this class on 23 june 2020*/

    private static SpikeBotApi SpikeBotApi = null;
    private Context mContext;

    private static ApiInterface apiService = RestClient.getInstance().getApiInterface();

    public static SpikeBotApi getInstance() {
        if (SpikeBotApi == null)
            return new SpikeBotApi();
        return SpikeBotApi;
    }

    public void loginUser(String userName, String password, String imei, DataResponseListener dataResponseListener) {
        // Constants.isNotStaticURL = false;
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_name", userName);
        params.put("user_password", password);
        params.put("device_id", imei);
        params.put("device_type", "android");
        params.put("device_push_token", Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN));
        params.put("uuid", ChatApplication.getUuid());
        params.put("fcm_token", "Android_test");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.APP_LOGIN, params), params, dataResponseListener).call();


    }

    // AllUnassignedpanel - room listing
    public void getRoomList(String room_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_type", room_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.roomslist, params), params, dataResponseListener).call();
    }

    // AllUnassignedpanel - unassign panel list
    public void getUnAssignedList(String module_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("module_type", module_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceunassigned, params), params, dataResponseListener).call();
    }

    // AllUnassignedpanel - delete device from unassignlist
    public void DeleteDevice(String module_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("module_id", module_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.devicemoduledelete, params), params, dataResponseListener).call();
    }

    // get device List
    public void getDeviceList(String device_type,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_type", device_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.devicefind, params), params, dataResponseListener).call();
    }

    // update device
    public void updateDevice(String device_id, String device_name, DataResponseListener dataResponseListener){
            HashMap<String, Object> params = new HashMap<>();

            params.put("device_id", device_id);
            params.put("device_name", device_name);
            params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

            new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_SWITCH, params), params, dataResponseListener).call();
    }

    //AllUnassignedpanel - add unAssign Repeater
    public void addunAssignRepater(String room_id, String device_name, String module_id, String module_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("device_name", device_name);
        params.put("module_id", module_id);
        params.put("module_type", module_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    // AllUnassignedpanel - save panel
    public void savePanel(UnassignedListRes.Data roomdeviceList, String roomId, String panelName, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        if (roomdeviceList.getModuleType().equals("5") || roomdeviceList.getModuleType().equals("5f")
                || roomdeviceList.getModuleType().equals("heavy_load") || roomdeviceList.getModuleType().equals("double_heavy_load")) {
            params.put("panel_name", panelName);
        } else {
            params.put("device_name", panelName);
        }
        params.put("room_id", roomId);
        params.put("module_type", roomdeviceList.getModuleType());
        params.put("module_id", roomdeviceList.getModuleId());
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    // Adddevice - save room
    public void saveCustomRoom(String room_name, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_name", room_name);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOME_ROOM, params), params, dataResponseListener).call();
    }

    // adddevice - get config device data
    public void getConfigData(String url, DataResponseListener dataResponseListener) {

        new GeneralRetrofit(apiService.getWebserviceCall(url), null, dataResponseListener).call();
    }

    // add device - save camera key
    public void saveCameraKey(String key, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("key", key);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.validatecamerakey, params), params, dataResponseListener).call();
    }

    // add device - camera add
    public void addCamera(String camera_name, String camera_ip, String video_path, String user_name, String password, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("camera_name", camera_name);
        params.put("camera_ip", camera_ip);
        params.put("video_path", video_path);
        params.put("user_name", user_name);
        params.put("password", password);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CAMERA, params), params, dataResponseListener).call();
    }

    // add device - device add common service
    public void addDevice(String room_id, String device_name, String module_id, String module_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_id", room_id);
        params.put("device_name", device_name);
        params.put("module_id", module_id);
        params.put("module_type", module_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    // add existing panel - get room camera list
    public void getroomcameralist(String room_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_type", room_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.getWebserviceCall(Constants.getRoomCameraList), params, dataResponseListener).call();
    }

    // addexisting panel - get custompanel detail
    public void getCustomPanelDetail(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.postWebserviceCall(Constants.GET_ORIGINAL_DEVICES, params), params, dataResponseListener).call();

    }

    // addexisting panel - save existing panel
    public void saveExistPanel(String room_id, String panel_name, String panel_id, boolean isDeviceAdd, JSONArray jsonArrayDevice, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (!isDeviceAdd) {
            params.put("room_id", room_id);
            params.put("panel_name", panel_name);
        } else {
            params.put("panel_id", panel_id);
        }

        params.put("devices", jsonArrayDevice);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        if (!isDeviceAdd) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOM_PANEL, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOME_DEVICE, params), params, dataResponseListener).call();
        }

    }

    // Cameradevicelog - call smart camera
    public void callGetSmarCamera(String jetson_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_device_id", jetson_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.cameralistbyjetson, params), params, dataResponseListener).call();
    }

    // Cameradevicelog - Get camera log
    public void callCameraLog(String start_date, String end_date, String camera_id, String home_controller_device_id, int notification_number, boolean isJetsonCameralog,
                              boolean isjetsonnotification, String jetson_id, String cameralog, String jetsoncameralog, DataResponseListener dataResponseListener) {
        // Constants.isNotStaticURL = false;
        HashMap<String, Object> params = new HashMap<>();

        params.put("start_date", "" + start_date);
        params.put("end_date", "" + end_date);
        params.put("camera_id", "" + camera_id);
        params.put("home_controller_device_id", home_controller_device_id);
        params.put("notification_number", "" + notification_number);
        if (isJetsonCameralog || isjetsonnotification) {
            params.put("jetson_id", "" + jetson_id);
        } else {
            if (!TextUtils.isEmpty(cameralog)) {
                params.put("camera_id", "" + camera_id);
            } else if (!TextUtils.isEmpty(jetsoncameralog)) {
                params.put("jetson_id", "" + jetson_id);
                params.put("camera_id", "" + camera_id);
            } else {
                params.put("camera_id", "" + "");
                params.put("jetson_id", "" + "");
            }

        }

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("admin", Integer.parseInt(Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE)));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.getCameraLogs, params), params, dataResponseListener).call();
    }

    // Cameradevicelog - camera unread count clear
    public void callupdateUnReadCameraLogs(String log_type, String camera_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("log_type", log_type);
        params.put("camera_id", "" + camera_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.logsfind, params), params, dataResponseListener).call();
    }

    // CameraEdit - camera update
    public void updateCamera(String camera_id, String jetson_id, String camera_name, String camera_ip, String confidence_score_day, String confidence_score_night,
                             String camera_videopath, String user_name, String password, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("camera_id", camera_id);
        params.put("jetson_id", jetson_id);
        params.put("camera_name", camera_name);
        params.put("camera_ip", camera_ip);
        params.put("camera_icon", "camera");
        params.put("confidence_score_day", confidence_score_day);
        params.put("confidence_score_night", confidence_score_night);
        params.put("camera_videopath", camera_videopath);
        params.put("user_name", user_name);
        params.put("password", password);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_CAMERA, params), params, dataResponseListener).call();
    }

    // CameraGrid -  Get all camera list
    public void getAllCameraList(String jetson_id, boolean isCamera, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (isCamera) {
            params.put("jetson_id", "");
        } else {
            params.put("jetson_id", jetson_id);
        }
        params.put("admin", Integer.parseInt(Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE)));
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.getAllCameraToken, params), params, dataResponseListener).call();

    }

    // CameraNotification - Add camera
    public void callAddCamera(String start_time, String end_time, int edIntervalTime, ArrayList<CameraVO> getCameraList
            , DataResponseListener dataResponseListener) {

        String ontime = "", offTime = "";
        try {
            ontime = DateHelper.formateDate(DateHelper.parseTimeSimple(start_time, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
            offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(end_time, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> params = new HashMap<>();

        params.put("start_time", ontime);
        params.put("end_time", offTime);
        params.put("alert_interval", edIntervalTime);

        JSONArray roomDeviceArray = new JSONArray();
        for (CameraVO dPanel : getCameraList) {

            if (dPanel.getIsSelect()) {
                JSONObject object = new JSONObject();
                try {
                    object.put("camera_id", dPanel.getCamera_id());
                    object.put("camera_name", "" + dPanel.getCamera_name());
                    object.put("camera_url", "" + dPanel.getCamera_url());
                    object.put("camera_ip", "" + dPanel.getCamera_ip());
                    object.put("camera_videopath", "" + dPanel.getCamera_videopath());
                    object.put("camera_icon", "" + dPanel.getCamera_icon());
                    object.put("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                    object.put("user_name", "" + dPanel.getUserName());
                    object.put("password", "" + dPanel.getPassword());

                    roomDeviceArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        params.put("cameraList", roomDeviceArray);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.addCameraNotification, params), params, dataResponseListener).call();
    }

    // CameraNotification - Update camera
    public void callUpdateCamera(String start_time, String end_time, CameraAlertList cameraAlertList, String jetson_id, int edIntervalTime,
                                 ArrayList<CameraVO> getCameraList, DataResponseListener dataResponseListener) {

        String onTime = "", offTime = "";

        try {
            onTime = DateHelper.formateDate(DateHelper.parseTimeSimple(start_time, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);
            offTime = DateHelper.formateDate(DateHelper.parseTimeSimple(end_time, DateHelper.DATE_FROMATE_H_M_AMPM), DateHelper.DATE_FROMATE_HH_MM);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> params = new HashMap<>();

        params.put("start_time", onTime);
        params.put("end_time", offTime);
        params.put("alert_interval", edIntervalTime);
        params.put("jetson_id", jetson_id);
        params.put("camera_notification_id", cameraAlertList.getCameraNotificationId());

        JSONArray roomDeviceArray = new JSONArray();
        JSONArray array = new JSONArray();
        for (CameraVO dPanel : getCameraList) {

            if (dPanel.getIsSelect()) {
                JSONObject object = new JSONObject();
                try {
                    object.put("camera_id", dPanel.getCamera_id());
                    object.put("camera_name", "" + dPanel.getCamera_name());
                    object.put("camera_url", "" + dPanel.getCamera_url());
                    object.put("camera_ip", "" + dPanel.getCamera_ip());
                    object.put("camera_videopath", "" + dPanel.getCamera_videopath());
                    object.put("camera_icon", "" + dPanel.getCamera_icon());
                    object.put("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                    object.put("user_name", "" + dPanel.getUserName());
                    object.put("password", "" + dPanel.getPassword());

                    roomDeviceArray.put(object);

                    array.put(dPanel.getCamera_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        params.put("camera_ids", array);
        params.put("cameraList", roomDeviceArray);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.updateCameraNotification, params), params, dataResponseListener).call();

    }

    // CameraNotification - getconfigureData
    public void getconfigureData(boolean jetsoncameranotification, String jetson_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        if (jetsoncameranotification) {
            params.put("jetson_id", jetson_id);
            params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
            params.put("admin", Integer.parseInt(Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE)));
        } else {
            params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
            params.put("admin", Integer.parseInt(Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE)));
        }

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.getCameraNotificationAlertList, params), params, dataResponseListener).call();
    }

    // CameraNotification - callCameraUnseenLog
    public void callCameraUnseenLog(String home_controller_device_id, boolean jetsoncameranotification, String jetson_id, DataResponseListener dataResponseListener) {

        // Constants.isNotStaticURL = false;
        HashMap<String, Object> params = new HashMap<>();

        params.put("home_controller_device_id", home_controller_device_id);
        params.put("notification_number", "" + 0);
        if (jetsoncameranotification) {
            params.put("jetson_id", "" + jetson_id);
        } else {
            params.put("camera_id", "" + "");
            params.put("jetson_id", "" + "");
        }
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("admin", Integer.parseInt(Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ADMIN_TYPE)));
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.getUnseenCameraLog, params), params, dataResponseListener).call();

    }

    // CameraNotification - deleteCamera
    public void deleteCamera(String jetson_id, String camera_notification_id, boolean jetsoncameranotification, String strflag, CameraAlertList cameraAlertList,
                             DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        if (jetsoncameranotification) {
            params.put("jetson_id", jetson_id);
            params.put("camera_notification_id", cameraAlertList.getCameraNotificationId());
            if (strflag.equalsIgnoreCase("switch")) {
                if (cameraAlertList.getIsActive() == 1) {
                    params.put("is_active", "0");
                } else {
                    params.put("is_active", "1");
                }

            }
        } else {
            params.put("camera_notification_id", cameraAlertList.getCameraNotificationId());
            if (strflag.equalsIgnoreCase("switch")) {
                if (cameraAlertList.getIsActive() == 1) {
                    params.put("is_active", "0");
                } else {
                    params.put("is_active", "1");
                }

            }
        }
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        if (strflag.equalsIgnoreCase("switch")) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.changeCameraAlertStatus, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.deleteCameraNotification, params), params, dataResponseListener).call();
        }
    }

    // CameraNotification - callupdateUnReadCameraLogs
    public void callupdateUnReadCameraLogs(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("log_type", "camera");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.logsfind, params), params, dataResponseListener).call();
    }

    // CameraPlayBack - searchCameraList
    public void searchCameraList(String start_date, String end_date, ArrayList<CameraVO> cameraVOArrayList, List<String> selectedCamera, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_start_date", start_date);
        params.put("camera_end_date", end_date);

        try {
            JSONArray jsonArray = new JSONArray();
            for (CameraVO cameraVO : cameraVOArrayList) {
                for (String ss : selectedCamera) {
                    if (cameraVO.getCamera_name().equalsIgnoreCase(ss)) {
                        JSONObject ob = new JSONObject();
                        ob.put("camera_id", cameraVO.getCamera_id());
                        ob.put("camera_name", cameraVO.getCamera_name());
                        jsonArray.put(ob);
                    }
                }
            }
            params.put("camera_details", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.GET_CAMERA_RECORDING_BY_DATE, params), params, dataResponseListener).call();
    }

    // ImageZoom - callReport false image
    public void callReport(String image_url, String home_controller_device_id, DataResponseListener dataResponseListener) {
        // Constants.isNotStaticURL = false;
        HashMap<String, Object> params = new HashMap<>();
        params.put("image_url", image_url);
        params.put("home_controller_device_id", home_controller_device_id);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.reportFalseImage, params), params, dataResponseListener).call();
    }

    // ImageZoom - CameraRecordingPlay
    public void CameraRecordingPlay(String camera_id, String timestamp, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_id", camera_id);
        params.put("timestamp", timestamp);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.camerarecording, params), params, dataResponseListener).call();
    }

    // curtain - update curtain status
    public void updateCutainStatus(String curtain_id, String panel_id, String curtain_status, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", curtain_id);
        params.put("panel_id", panel_id);
        params.put("device_status", Integer.parseInt(curtain_status));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_DEVICE_STATUS, params), params, dataResponseListener).call();
    }

    // curtain - deletecurtainPanel
    public void deleteDevice(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_MODULE, params), params, dataResponseListener).call();
    }

    // heavyloaddetail - getHeavyLoadValue
    public void getHeavyLoadValue(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceheavyloadping, params), params, dataResponseListener).call();
    }

    // heavyloaddetail - getHeavyLoaddetail
    public void getHeavyloadDetails(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceheavyloadping, params), params, dataResponseListener).call();
    }

    // heavyloaddetail - heavyloadfilter
    public void HeavyloadFilter(String device_id, String strLess, int value, int stryear, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("filter_type", "month");
        params.put("filter_value", strLess + value + "," + stryear);
        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.getHeavyLoadDetails, params), params, dataResponseListener).call();
    }

    // IRBlasterAdd - saveIRBlaster
    public void saveIRBlaster(String ir_blaster_name, String ir_blaster_module_id, String room_id, String room_name, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("ir_blaster_name", ir_blaster_name);
        params.put("ir_blaster_module_id", ir_blaster_module_id);

        params.put("room_id", room_id);
        params.put("room_name", room_name);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_IR_BLASTER, params), params, dataResponseListener).call();

    }

    // IRBlasterRemote -  getdeviceino - get all type of device info call this service
    public void deviceInfo(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceinfo, params), params, dataResponseListener).call();
    }

    // IRBlasterRemote - saveRemote
    public void saveRemote(String device_id, String device_name, String device_default_status, String ir_blaster_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("device_name", device_name);
        params.put("device_default_status", device_default_status);
        params.put("ir_blaster_id", ir_blaster_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_SWITCH, params), params, dataResponseListener).call();
    }

    // IRBlasterRemote - sendRemoteCommand
    public void sendRemoteCommand(String device_id, String device_status, String device_sub_status, int counting, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        if (counting == 0) {
            params.put("device_status", device_status);
        } else {
            params.put("device_status", "1");
        }

        params.put("device_sub_status", device_sub_status);//1;
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_DEVICE_STATUS, params), params, dataResponseListener).call();
    }

    //IRRemoteBrandList - getIRDetailsList
    public void getIRDetailsList(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getWebserviceCall(Constants.getIRDeviceTypeBrands + "/1"), null, dataResponseListener).call();
    }

    //IRRemoteBrandList - getIRRemoteDetails
    public void getIRRemoteDetails(int device_brand_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_brand_id", device_brand_id);//1;
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.getWebserviceCall(Constants.getDeviceBrandRemoteList + "/" + device_brand_id), params, dataResponseListener).call();
    }

    //IRRemoteConfig - sendOnOfOffRequest
    public void sendOnOfOffRequest(String ir_blaster_module_id, String mode, String ir_code, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ir_blaster_module_id", ir_blaster_module_id);
        params.put("mode", mode);
        params.put("ir_code", ir_code);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SEND_ON_OFF_COMMAND, params), params, dataResponseListener).call();
    }

    //IRRemoteConfig - saveremote
    public void saveremote(String device_id, String module_type, String device_name, String ir_blaster_id, String ir_blaster_module_id, String room_id, String device_default_status,
                           String device_brand, String device_codeset_id, String device_model, String device_codes, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("module_type", module_type);
        params.put("device_name", device_name);
        params.put("ir_blaster_id", ir_blaster_id);
        params.put("ir_blaster_module_id", ir_blaster_module_id);
        params.put("room_id", room_id);
        params.put("device_default_status", device_default_status);
        params.put("device_brand", device_brand);
        params.put("device_codeset_id", device_codeset_id);
        params.put("device_model", device_model);
        params.put("device_codes", device_codes);
        params.put("update_type", 0);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    //Repeater - saveRepeater
    public void saveRepeater(String device_name,String door_module_id,String module_type,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", "");
        params.put("device_name", device_name);
        params.put("module_id", door_module_id);
        params.put("module_type", module_type);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

   //DoorSensorInfo  - getDoorSensorDetails
    public void getDoorSensorDetails(String device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceinfo, params), params, dataResponseListener).call();
    }

    //DoorSensorInfo  - doorSensor Notification Status
    public void doorSensorNotificationStatus(String doorSensorNotificationId, boolean isActive, boolean isNotification,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("alert_id", doorSensorNotificationId);
        if (isNotification) {
            params.put("alert_id", doorSensorNotificationId);
            params.put("is_active", isActive ? "y" : "n");
        } else {
            params.put("is_push_enable", isActive ? 1 : 0);
        }

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (!isNotification) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_DOOR_SENSOR_STATUS, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.UPDATE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        }
    }


    //DoorSensorInfo - add Notification
    public void addNotification(String start_time,String end_time,String alert_id,String device_id,boolean isEdit, DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("start_time",start_time);
        params.put("end_time",end_time);
        params.put("alert_type", "door_open_close");
        params.put("days", "0,1,2,3,4,5,6");
        if (isEdit) {
            params.put("alert_id", alert_id);

        } else {
            params.put("device_id", device_id);
        }

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (isEdit) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.UPDATE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        }
    }

    //DoorSensorInfo - deleteDoorSensorNotification
    public void deleteDoorSensorNotification(String alert_id,DataResponseListener dataResponseListener){

        HashMap<String, Object> params = new HashMap<>();
        params.put("alert_id", alert_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
    }

    //DoorSensorInfo - unread notification
    public void unreadNotification(String device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("log_type", "device");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.MARK_SEEN, params), params, dataResponseListener).call();
    }

    // MultiSensor - tempSensorNotificationStatus
    public void tempSensorNotificationStatus(String alert_id,boolean is_active,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("alert_id", alert_id);
        params.put("is_active", is_active);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.UPDATE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
    }

    //MultiSensor - updateTempSensor
    public void updateTempSensor(String temp_module_id,String device_name,int isCFSelected,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", temp_module_id);
        params.put("device_name", device_name);
        params.put("unit", isCFSelected == 1 ? "C" : "F");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_SWITCH, params), params, dataResponseListener).call();
    }

    //MultiSensor - addHumity
    public void addHumity(int min_humidity,int max_humidity,String days,String alert_id,String device_id,boolean isEdit,DataResponseListener dataResponseListener){

        HashMap<String, Object> params = new HashMap<>();
        params.put("min_humidity", min_humidity);
        params.put("max_humidity", max_humidity);
        params.put("alert_type", "humidity");
        params.put("days", days);
        if (isEdit) {
            params.put("alert_id", alert_id);

        } else {
            params.put("device_id",device_id);
        }
        params.put("days", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (!isEdit) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.UPDATE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        }
    }

    //MultiSensor - addNotification
    public void addTempNotification(int min_temp,int max_temp,String days,String alert_id,String device_id,boolean isEdit,int isCFSelected,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        if (isCFSelected == 1) {
            params.put("min_temp", min_temp);
            params.put("max_temp", max_temp);
        } else {
            params.put("min_temp", min_temp);
            params.put("max_temp", max_temp);
        }

        params.put("alert_type", "temperature");
        params.put("days", days);
        if (isEdit) {
            params.put("alert_id", alert_id);

        } else {
            params.put("device_id", device_id);
        }


        if (isEdit) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.UPDATE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
        }
    }

    //MultiSensor -  deleteTempSensorNotification
    public void deleteTempSensorNotification(RemoteDetailsRes.Data.Alert notificationList, RemoteDetailsRes.Data.Alert notification,
                                             DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        if (notificationList != null) {
            params.put("alert_id", notificationList.getAlertId());

        } else {
            params.put("alert_id", notification.getAlertId());
        }

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_TEMP_SENSOR_NOTIFICATION, params), params, dataResponseListener).call();
    }

    //SensorUnassigned - get repeaterlist
    public void callReptorList( DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getWebserviceCall(Constants.getUnassignedRepeaterList), null, dataResponseListener).call();
    }

    //SensorUnassigned - get sensor unassinged list
    public void getSensorUnAssignedDetails(DataResponseListener dataResponseListener){
        new GeneralRetrofit(apiService.getWebserviceCall(Constants.deviceunassigned), null, dataResponseListener).call();
    }

    //SensorUnassigned - saveCurtain
    public void saveCurtain(String curtain_module_id,String curtain_name,String room_id,String room_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("curtain_module_id", curtain_module_id);
        params.put("curtain_name", curtain_name);
        params.put("room_id", room_id);
        params.put("room_name",room_name);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd,params), params, dataResponseListener).call();
    }

    //SensorUnassigned - saveRepeator
    public void saveRepeaters(String repeator_module_id,String repeator_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("repeator_module_id", repeator_module_id);
        params.put("repeator_name", repeator_name);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.reassignRepeater,params), params, dataResponseListener).call();
    }

    //SensorUnassigned - save sensor
    public void saveSensorUnassigned(String room_id,String room_name,String sensor_id,String module_id,String sensor_type,String sensor_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        params.put("sensor_id",sensor_id);
        params.put("module_id", module_id);
        params.put("sensor_type", sensor_type);
        params.put("sensor_name", sensor_name);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_UNCONFIGURED_SENSOR,params), params, dataResponseListener).call();
    }

    //WaterSensor - add alert
    public void addalert(String device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_TEMP_SENSOR_NOTIFICATION,params), params, dataResponseListener).call();
    }

    //WaterSensor - deletealert
    public void deletealert(String device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_TEMP_SENSOR_NOTIFICATION,params), params, dataResponseListener).call();
    }

    // AddJetSon - GetJetson
    public void callGetJetson(DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_name", "jetson");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.jetsonlist, params), params, dataResponseListener).call();
    }

    // AddJetSon - AddJetson
    public void callAddJetson(String jetson_id,String jetson_name,String jetson_ip,boolean isFlag,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        if(isFlag){
            params.put("jetson_id", jetson_id);
        }

        params.put("jetson_name", jetson_name);
        params.put("jetson_ip",jetson_ip);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if(isFlag){
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.jetsonupdate, params), params, dataResponseListener).call();
        }else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.jetsonadd, params), params, dataResponseListener).call();
        }
    }

    // AddJetSon - deleteJetson
    public void deleteJetson(String jetson_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_id", jetson_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.jetsondelete, params), params, dataResponseListener).call();
    }

    // SmartCameraActivity - saveJetsonCameraKey
    public void saveJetsonCameraKey(String camerakey,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("key", camerakey);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.validatecamerakey, params), params, dataResponseListener).call();
    }

    // SmartCameraActivity - addCameraCall
    public void addCameraCall(String camera_name,String camera_ip,String user_name,String password,String confidence_score_day,
                              String confidence_score_night,String video_path,String jetson_id,boolean isflag,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_name", camera_name);
        params.put("camera_ip", camera_ip);

        params.put("user_name", user_name);
        params.put("password", password);
        params.put("confidence_score_day",confidence_score_day);
        params.put("confidence_score_night",confidence_score_night);

        params.put("video_path", video_path);
        params.put("jetson_device_id", jetson_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if(isflag){
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_CAMERA, params), params, dataResponseListener).call();
        }else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CAMERA, params), params, dataResponseListener).call();
        }

    }

    //SmartCameraActivity - GetSmartCamera
    public void callGetSmartCamera(String jetson_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_device_id", jetson_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.cameralistbyjetson, params), params, dataResponseListener).call();
    }

    //SmartCameraActivity - deleteJetsoncamera
    public void deleteJetsoncamera(String camera_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("camera_id", camera_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_CAMERA, params), params, dataResponseListener).call();
    }

    //AddDeviceconfirm - saveCustomRoom
    public void saveCustomRoomDevice(String room_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("room_name", room_name);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOME_ROOM, params), params, dataResponseListener).call();
    }

    //AddDeviceconfirm - AddTTlock
    public void callAddTTlock(String lock_id,String room_id,String lock_data,String door_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("panel_id", "");
        params.put("door_sensor_id", "");
        params.put("is_new", 1);

        params.put("lock_id", lock_id);
        params.put("room_id", room_id);
        params.put("lock_data", lock_data);

        params.put("lock_name", door_name);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.addTTLock, params), params, dataResponseListener).call();
    }

    // AddDeviceconfirm - saveSensor
    public void saveSensor(String room_id,String room_name,String door_sensor_name,String door_sensor_module_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        params.put("door_sensor_name", door_sensor_name);
        params.put("door_sensor_module_id", door_sensor_module_id);
        params.put("is_new", 1);
        params.put("panel_id", "");
        params.put("door_sensor_id", "");

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_DOOR_SENSOR, params), params, dataResponseListener).call();
    }

    // AddGateWay  -  bridge list
    public void callAddBridge(int gatewayId,String gatewayName,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("module_id", gatewayId);
        params.put("device_name", gatewayName);
        params.put("module_type ","tt_lock_bridge");

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    // AddGateWay - deleteGateway
    public void deleteGateway(int bridge_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("bridge_id", bridge_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deleteTTLockBridge, params), params, dataResponseListener).call();
    }

    //TTLockList - AllLockList
    public void getAllLockList(DataResponseListener dataResponseListener){
        new GeneralRetrofit(apiService.getWebserviceCall(Constants.getLockLists), null, dataResponseListener).call();
    }

    //Yalelockinfo - update yale lock
    public void updateyalelock(String device_id,String enable_lock_unlock_from_app,String pass_code,String onetime_code,String device_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("enable_lock_unlock_from_app", enable_lock_unlock_from_app);
        params.put("pass_code", pass_code);
        params.put("onetime_code",onetime_code);
        params.put("device_name", device_name);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_SWITCH, params), params, dataResponseListener).call();
    }

    // AddBeacon - beacon device details
    public void getbeaconDeviceDetails(String device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.beaconscannerscan, params), params, dataResponseListener).call();
    }

    // Beaconconfig - get beacon room list
    public void getBeaconRoomList(DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_type", "room");
        params.put("only_on_off_device", 1);
        params.put("only_rooms_of_beacon_scanner", 1);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.GET_DEVICES_LIST, params), params, dataResponseListener).call();
    }

    // Beaconconfig - saveBeacon
    public void saveBeacon(String device_name,String device_id,String module_id,JSONArray array,boolean editBeacon,DataResponseListener dataResponseListener){

        HashMap<String, Object> params = new HashMap<>();
        params.put("device_name", device_name);

        if (editBeacon) {
            params.put("device_id", device_id);
        } else {
            params.put("module_id", module_id);
        }
        params.put("module_type", "beacon");
        params.put("related_devices", array);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        if (editBeacon) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.SAVE_EDIT_SWITCH, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
        }
    }

    // Beaconconfig - saveBeacon
    public void getDeviceDetails(String original_room_device_id,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();
        params.put("original_room_device_id", original_room_device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.getWebserviceCall(Constants.GET_MOOD_DEVICE_DETAILS + "/" + original_room_device_id), params, dataResponseListener).call();
    }

    //BeaconDetail - get beacon location list
    public void getBeaconLocationList(String room_id,ArrayList<String> roomListString,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        if (roomListString.equals("all")) {
            params.put("room_id", "");
        } else {
            params.put("room_id", room_id);
        }
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.GET_BEACON_LOCATION, params), params, dataResponseListener).call();
    }

    //BeaconScannerAdd - Add Beacon Scanner
    public void addBeaconScanner(String ir_blaster_name,String ir_blaster_module_id,String room_id,String room_name,DataResponseListener dataResponseListener){
        HashMap<String, Object> params = new HashMap<>();

        params.put("ir_blaster_name", ir_blaster_name);
        params.put("ir_blaster_module_id", ir_blaster_module_id);
        params.put("room_id", room_id);
        params.put("room_name", room_name);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_IR_BLASTER, params), params, dataResponseListener).call();
    }

    // ScannerWifiList - add scanner
    public void addScanner(String room_id, String device_name, String module_id, String module_type,String on_time,String off_time,String range, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_id", room_id);
        params.put("device_name", device_name);
        params.put("module_id", module_id);
        params.put("module_type", module_type);
        params.put("on_time", on_time);
        params.put("off_time", off_time);
        params.put("range", range);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.deviceadd, params), params, dataResponseListener).call();
    }

    /*Dash board - Home listing*/  // dev arpan on 24 june 2020
    public void GetCameraBadgeCount(String homecontrollerid, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("home_controller_device_id", homecontrollerid);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.GET_CAMERA_NOTIFICATION_COUNTER, params), params, dataResponseListener).call();

    }

    public void CallCameraToken(String CameraID, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getWebserviceCall("MCommon/GetShortCutFields/" + CameraID), null, dataResponseListener).call();
    }

    public void SaveCustomRoom(String Roomname, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_name", Roomname);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOME_ROOM, params), params, dataResponseListener).call();
    }

    public void DeleteCamera(String camera_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("camera_id", camera_id);

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_CAMERA, params), params, dataResponseListener).call();

    }

    public void GetBadgeClear(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.updateBadgeCount, params), params, dataResponseListener).call();
    }

    public void CallDeviceOnOffApi(DeviceVO deviceVO, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            params.put("status", deviceVO.getDeviceStatus());
            params.put("bright", "");
            params.put("is_rgb", "0");//1;
            params.put("rgb_array", "");
            params.put("room_device_id", deviceVO.getRoomDeviceId());
        } else {
            try {
                if (deviceVO.getDevice_sub_type() != null) {
                    if (deviceVO.getDeviceType().equalsIgnoreCase("fan") && deviceVO.getDevice_sub_type().equalsIgnoreCase("normal")) {
                        params.put("device_id", deviceVO.getDeviceId());
                        params.put("panel_id", deviceVO.getPanel_id());
                        params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
                        params.put("device_sub_status", "5");
                    } else if (deviceVO.getDeviceType().equalsIgnoreCase("fan") && deviceVO.getDevice_sub_type().equalsIgnoreCase("dimmer")) {
                        params.put("device_id", deviceVO.getDeviceId());
                        params.put("panel_id", deviceVO.getPanel_id());
                        params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
                        params.put("device_sub_status", deviceVO.getDevice_sub_status());
                    } else {
                        params.put("device_id", deviceVO.getDeviceId());
                        params.put("panel_id", deviceVO.getPanel_id());
                        params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
                    }
                }

                params.put("device_id", deviceVO.getDeviceId());
                params.put("panel_id", deviceVO.getPanel_id());
                params.put("device_sub_status", deviceVO.getDevice_sub_status());
                params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.changeHueLightState, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_DEVICE_STATUS, params), params, dataResponseListener).call();
        }

    }


    public void CallPanelOnOffApi(String roomId, String panelId, int panel_status, int type, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        if (type == 1) {
            params.put("room_id", roomId);
            params.put("room_status", panel_status);
            new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW, params), params, dataResponseListener).call();
        } else {
            params.put("panel_id", panelId);
            params.put("panel_status", panel_status);

            new GeneralRetrofit(apiService.postWebserviceCall(Constants.CHANGE_PANELSTATUS, params), params, dataResponseListener).call();

        }
    }

    public void DeleteRoom(String room_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_id", room_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_ROOM, params), params, dataResponseListener).call();
    }


}