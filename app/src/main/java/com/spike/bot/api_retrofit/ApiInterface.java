package com.spike.bot.api_retrofit;

import com.google.gson.JsonElement;
import com.spike.bot.core.Constants;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/*dev arp add this interface on 23 june 2020*/

public interface ApiInterface {


    @GET
    Call<JsonElement> getWebserviceCall(@Url String url);

    @DELETE
    Call<JsonElement> deleteWebserviceCall(@Url String url);

    @GET
    Call<JsonElement> getWebserviceCall(@Url String url, @QueryMap HashMap<String, Object> body);

    @POST
    Call<JsonElement> postWebserviceCall(@Url String url, @Body HashMap<String, Object> body);



    /*
     *
     * Get api
     *
     * */


    @GET("http://{localip}" + Constants.getLocalMacAddress)
    Call<JsonElement> GetMacAddressCall(@Path("localip") String localip);


    @GET("http://{localip}" + Constants.getCameraToken + "{CameraID}")
    Call<JsonElement> CallCameraToken(@Path("localip") String localip, @Path("CameraID") String CameraID);

    @GET("http://{localip}" + Constants.getChildUsers)
    Call<JsonElement> GetUserCHildList(@Path("localip") String localip);


    @GET("http://{localip}" + Constants.GET_USER_PROFILE_INFO)
    Call<JsonElement> GetProfile(@Path("localip") String localip);

    @GET("http://{localip}" + Constants.GET_MOOD_DEVICE_DETAILS + "{original_room_device_id}")
    Call<JsonElement> GetDeviceDetails(@Path("localip") String localip, @Path("original_room_device_id") String original_room_device_id);

    @GET("http://{localip}" + Constants.deviceconfigure + "{SensorType}")
    Call<JsonElement> getConfigData(@Path("localip") String localip, @Path("SensorType") String SensorType);

    @GET("http://{localip}" + Constants.getIRDeviceTypeBrands + "{original_room_device_id}")
    Call<JsonElement> getIRDeviceTypeBrands(@Path("localip") String localip, @Path("original_room_device_id") String original_room_device_id);

    @GET("http://{localip}" + Constants.getDeviceBrandRemoteList + "{original_room_device_id}")
    Call<JsonElement> getDeviceBrandRemoteList(@Path("localip") String localip, @Path("original_room_device_id") int original_room_device_id);

    @GET("http://{localip}" + Constants.getUnassignedRepeaterList)
    Call<JsonElement> getUnassignedRepeaterList(@Path("localip") String localip);

    @GET("http://{localip}" + Constants.getUnassignedRepeaterList)
    Call<JsonElement> deviceunassigned(@Path("localip") String localip);

    @GET("http://{localip}" + Constants.getLockLists)
    Call<JsonElement> getLockLists(@Path("localip") String localip);

    @GET("http://{localip}" + Constants.GET_MOOD_DEVICE_DETAILS + "{original_room_device_id}")
    Call<JsonElement> GET_MOOD_DEVICE_DETAILS(@Path("localip") String localip, @Path("original_room_device_id") String original_room_device_id);

    @GET("http://{localip}" + Constants.getRoomCameraList)
    Call<JsonElement> getroomcameralist(@Path("localip") String localip);


    /*
     *
     * Post apis
     *
     * */
    @POST("https://live.spikebot.io:8443" + Constants.APP_LOGIN)
    // updated on july 07 2020
    Call<JsonElement> postLoginWebserviceCall(@Body HashMap<String, Object> body);

    @POST("https://live.spikebot.io:8443" + Constants.GET_CAMERA_NOTIFICATION_COUNTER)
        // updated on july 07 2020
    Call<JsonElement> postGetCameraBadgeCount(@Body HashMap<String, Object> body);

    @POST("https://live.spikebot.io:8443" + Constants.APP_LOGOUT)
        // updated on july 07 2020
    Call<JsonElement> LogoutCloudUser(@Body HashMap<String, Object> body);

    @POST("https://live.spikebot.io:8443" + Constants.reportFalseImage)
        // updated on july 07 2020
    Call<JsonElement> reportFalseImage(@Body HashMap<String, Object> body);

    @POST("https://live.spikebot.io:8443" + Constants.getUnseenCameraLog)
        // updated on july 07 2020
    Call<JsonElement> getUnseenCameraLog(@Body HashMap<String, Object> body);

    @POST("https://live.spikebot.io:8443" + Constants.getCameraLogs)
        // updated on july 07 2020
    Call<JsonElement> getCameraLogs(@Body HashMap<String, Object> body);


    @POST("https://live.spikebot.io:8443" + Constants.FORGET_PASSWORD)
    Call<JsonElement> forgetpassword(@Body Object body);

    @POST("https://live.spikebot.io:8443" + Constants.RETRY_OTP)
    Call<JsonElement> RetryPassword(@Body Object body);

    @POST("https://live.spikebot.io:8443" + Constants.OTP_VERIFY)
    Call<JsonElement> verifyOTP(@Body Object body);

    @POST("https://live.spikebot.io:8443" + Constants.SET_NEW_PASSWORD)
    Call<JsonElement> SetNewPassword(@Body Object body);


    @POST("http://{url}" + Constants.ADD_CUSTOME_ROOM)
    Call<JsonElement> postCustomRoom(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.DELETE_CAMERA)
    Call<JsonElement> postDeletCamera(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.GET_DEVICES_LIST)
    Call<JsonElement> postDeviceLocal_Cloud(@Path("url") String url, @Body HashMap<String, Object> body);


    @POST("http://{url}" + Constants.changeHueLightState)
    Call<JsonElement> DeviceOnOff_Type3(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.CHANGE_DEVICE_STATUS)
    Call<JsonElement> DeviceOnOff_Not_Type3(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.mooddelete)
    Call<JsonElement> DeleteMood(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.mood_status)
    Call<JsonElement> ChangeMoodStatus(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.updateBadgeCount)
    Call<JsonElement> GetBadgeClear(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.CHANGE_ROOM_PANELMOOD_STATUS_NEW)
    Call<JsonElement> CallPanelOnOffApi_Type1(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.CHANGE_PANELSTATUS)
    Call<JsonElement> CallPanelOnOffApi_Not_Type1(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.DELETE_ROOM)
    Call<JsonElement> DeleteRoom(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.GET_SCHEDULE_LIST)
    Call<JsonElement> GetScheduleList(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.DELETE_SCHEDULE)
    Call<JsonElement> DeleteSchedule(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.schedulestatus)
    Call<JsonElement> ChangeScheduleStatus(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.DeleteChildUser)
    Call<JsonElement> DeleteUserChild(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.SAVE_USER_PROFILE_DETAILS)
    Call<JsonElement> SaveProfile(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.CHANGE_PASSWORD)
    Call<JsonElement> ChangePassword(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.deviceadd)
    Call<JsonElement> ConfigureNewRoom(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.deletePhilipsHue)
    Call<JsonElement> DeleteDevice(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.SAVE_EDIT_SWITCH)
    Call<JsonElement> SaveSwithcDetails(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.CHANGE_DEVICE_STATUS)
    Call<JsonElement> ChangeFanSpeed(@Path("url") String url, @Body HashMap<String, Object> body);


    @POST("http://{url}" + Constants.moodList)
    Call<JsonElement> GetMoodList(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.getRoomCameraList)
    Call<JsonElement> GetRoomList(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.AddChildUser)
    Call<JsonElement> AddUserChild(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.updateChildUser)
    Call<JsonElement> UpdateUserChild(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.devicefind)
    Call<JsonElement> GetRemoteLIst(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.changeHueLightState)
    Call<JsonElement> CallHueLightState(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.getPhilipsHueParams)
    Call<JsonElement> GetPhilipsHueParams(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://192.168.175.119" + Constants.SIGNUP_API)
    Call<JsonElement> Signup(@Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.ADD_NEW_SCHEDULE)
    Call<JsonElement> AddSchedule(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.scheduleedit)
    Call<JsonElement> EditSchedule(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_ROOM_AND_PANEL_NAME)
    Call<JsonElement> SaveRoom(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.roomsget)
    Call<JsonElement> GetRooms(@Path("url") String url, @Body Object body);


    @POST("http://{url}" + Constants.UPDATE_UNREAD_LOGS)
    Call<JsonElement> CallreadCountApi(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.CHECK_INDIVIDUAL_SWITCH_DETAILS)
    Call<JsonElement> ShowDeviceEditDialog(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.panelDelete)
    Call<JsonElement> DeletePanel(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_NOTIFICATION_LIST)
    Call<JsonElement> SaveNotiSettingList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_NOTIFICATION_LIST)
    Call<JsonElement> GetNotificationList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.logsfind)
    Call<JsonElement> GetLogFind(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_DEVICES_LIST)
    Call<JsonElement> RoomLogListApi(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.moodList)
    Call<JsonElement> MoodLogListApi(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_SCHEDULE_LIST)
    Call<JsonElement> ScheduleLogListApi(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.devicefind)
    Call<JsonElement> SensorLogListApi(@Path("url") String url, @Body Object body);


    @POST("http://{url}" + Constants.logsfind)
    Call<JsonElement> CallFilterData(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_FILTER_NOTIFICATION_INFO)
    Call<JsonElement> GetDeviceLog(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.getMoodName)
    Call<JsonElement> GetMoodNameList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_DEVICES_LIST)
    Call<JsonElement> GetDeviceList(@Path("url") String url, @Body Object body);


    @POST("http://{url}" + Constants.ADD_NEW_MOOD_NEW)
    Call<JsonElement> AddMood(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.EDITMOOD)
    Call<JsonElement> EditMood(@Path("url") String url, @Body Object body);


    @POST("http://{url}" + Constants.roomslist)
    Call<JsonElement> roomslist(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceunassigned)
    Call<JsonElement> GetUnAssignedList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.devicemoduledelete)
    Call<JsonElement> devicemoduledelete(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.devicefind)
    Call<JsonElement> devicefind(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceadd)
    Call<JsonElement> addunAssignRepater(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceadd)
    Call<JsonElement> savePanel(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CUSTOME_ROOM)
    Call<JsonElement> saveCustomRoom(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.validatecamerakey)
    Call<JsonElement> saveCameraKey(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CAMERA)
    Call<JsonElement> addCamera(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceadd)
    Call<JsonElement> addDevice(@Path("url") String url, @Body Object body);


    @POST("http://{url}" + Constants.GET_ORIGINAL_DEVICES)
    Call<JsonElement> getCustomPanelDetail(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CUSTOM_PANEL)
    Call<JsonElement> ADD_CUSTOM_PANEL(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CUSTOME_DEVICE)
    Call<JsonElement> ADD_CUSTOME_DEVICE(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.cameralistbyjetson)
    Call<JsonElement> cameralistbyjetson(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.logsfind)
    Call<JsonElement> callupdateUnReadCameraLogs(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_EDIT_CAMERA)
    Call<JsonElement> updateCamera(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.getAllCameraToken)
    Call<JsonElement> getAllCameraList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.addCameraNotification)
    Call<JsonElement> addCameraNotification(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.updateCameraNotification)
    Call<JsonElement> updateCameraNotification(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.getCameraNotificationAlertList)
    Call<JsonElement> getCameraNotificationAlertList(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.changeCameraAlertStatus)
    Call<JsonElement> changeCameraAlertStatus(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deleteCameraNotification)
    Call<JsonElement> deleteCameraNotification(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.logsfind)
    Call<JsonElement> logsfind(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_CAMERA_RECORDING_BY_DATE)
    Call<JsonElement> GET_CAMERA_RECORDING_BY_DATE(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.camerarecording)
    Call<JsonElement> camerarecording(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.CHANGE_DEVICE_STATUS)
    Call<JsonElement> CHANGE_DEVICE_STATUS(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_EDIT_SWITCH)
    Call<JsonElement> SAVE_EDIT_SWITCH(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceheavyloadping)
    Call<JsonElement> deviceheavyloadping(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.DELETE_MODULE)
    Call<JsonElement> DELETE_MODULE(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.getHeavyLoadDetails)
    Call<JsonElement> getHeavyloadDetails(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.getHeavyLoadDetails)
    Call<JsonElement> getHeavyLoadDetails(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_IR_BLASTER)
    Call<JsonElement> ADD_IR_BLASTER(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceinfo)
    Call<JsonElement> deviceinfo(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SEND_ON_OFF_COMMAND)
    Call<JsonElement> SEND_ON_OFF_COMMAND(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deviceadd)
    Call<JsonElement> deviceadd(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.CHANGE_DOOR_SENSOR_STATUS)
    Call<JsonElement> CHANGE_DOOR_SENSOR_STATUS(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.UPDATE_TEMP_SENSOR_NOTIFICATION)
    Call<JsonElement> UPDATE_TEMP_SENSOR_NOTIFICATION(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_TEMP_SENSOR_NOTIFICATION)
    Call<JsonElement> ADD_TEMP_SENSOR_NOTIFICATION(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.DELETE_TEMP_SENSOR_NOTIFICATION)
    Call<JsonElement> DELETE_TEMP_SENSOR_NOTIFICATION(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.MARK_SEEN)
    Call<JsonElement> MARK_SEEN(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.reassignRepeater)
    Call<JsonElement> reassignRepeater(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_UNCONFIGURED_SENSOR)
    Call<JsonElement> SAVE_UNCONFIGURED_SENSOR(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.jetsonlist)
    Call<JsonElement> jetsonlist(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.jetsonupdate)
    Call<JsonElement> jetsonupdate(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.jetsonadd)
    Call<JsonElement> jetsonadd(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.jetsondelete)
    Call<JsonElement> jetsondelete(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.validatecamerakey)
    Call<JsonElement> validatecamerakey(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_EDIT_CAMERA)
    Call<JsonElement> SAVE_EDIT_CAMERA(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CAMERA)
    Call<JsonElement> ADD_CAMERA(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.DELETE_CAMERA)
    Call<JsonElement> DELETE_CAMERA(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_CUSTOME_ROOM)
    Call<JsonElement> ADD_CUSTOME_ROOM(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.addTTLock)
    Call<JsonElement> addTTLock(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.ADD_DOOR_SENSOR)
    Call<JsonElement> ADD_DOOR_SENSOR(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.deleteTTLockBridge)
    Call<JsonElement> deleteTTLockBridge(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.beaconscannerscan)
    Call<JsonElement> beaconscannerscan(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_DEVICES_LIST)
    Call<JsonElement> GET_DEVICES_LIST(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_BEACON_LOCATION)
    Call<JsonElement> GET_BEACON_LOCATION(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.moodsmartremote)
    Call<JsonElement> CallSmartRemote(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.SAVE_EDIT_SWITCH)
    Call<JsonElement> SaveSmartRemote(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.DELETE_MODULE)
    Call<JsonElement> DeleteRemote(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_ROOM_DETAILS)
    Call<JsonElement> GET_ROOM_DETAILS(@Path("url") String url, @Body Object body);

    @POST("http://{url}" + Constants.GET_CAMERA_DETAILS)
    Call<JsonElement> GET_CAMERA_DETAILS(@Path("url") String url, @Body Object body);
}
