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


    /*
     *
     * Post apis
     *
     * */
    @POST("http://52.24.23.7:3000" + Constants.APP_LOGIN)
    Call<JsonElement> postLoginWebserviceCall(@Body HashMap<String, Object> body);


    @POST("http://52.24.23.7:3000" + Constants.GET_CAMERA_NOTIFICATION_COUNTER)
    Call<JsonElement> postGetCameraBadgeCount(@Body HashMap<String, Object> body);

    @POST("http://52.24.23.7:3000" + Constants.APP_LOGOUT)
    Call<JsonElement> LogoutCloudUser(@Body HashMap<String, Object> body);

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

    @POST("http://{url}" + Constants.scheduleedit)
    Call<JsonElement> ChangeScheduleStatus(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.DeleteChildUser)
    Call<JsonElement> DeleteUserChild(@Path("url") String url, @Body HashMap<String, Object> body);

    @POST("http://{url}" + Constants.SAVE_USER_PROFILE_DETAILS)
    Call<JsonElement> SaveProfile(@Path("url") String url, @Body HashMap<String, Object> body);

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

    @POST("http://{url}" + Constants.SIGNUP_API)
    Call<JsonElement> Signup(@Path("url") String url, @Body HashMap<String, Object> body);

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





}