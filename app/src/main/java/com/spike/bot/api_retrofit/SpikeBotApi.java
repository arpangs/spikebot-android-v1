package com.spike.bot.api_retrofit;


import android.text.TextUtils;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SpikeBotApi {


    /*dev arp add this class on 23 june 2020*/

    private static SpikeBotApi SpikeBotApi = null;

    private ApiInterface apiService = RestClient.getInstance().getApiInterface();

    public static SpikeBotApi getInstance() {
        if (SpikeBotApi == null)
            return new SpikeBotApi();
        return SpikeBotApi;
    }

    /*Login screen */ // dev arpan on 23 june 2020
    public void loginUser(String userName, String password, String imei, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_name", userName);
        params.put("user_password", password);
        params.put("device_id", imei);
        params.put("device_type", "android");
        params.put("device_push_token", Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN));
        params.put("uuid", ChatApplication.getUuid());
        params.put("fcm_token", Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN)); // dev arpan add FCM token here - on 24 june 2020
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postLoginWebserviceCall(params), params, dataResponseListener).call();

    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void GetCameraBadgeCount(String homecontrollerid, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("home_controller_device_id", homecontrollerid);

        new GeneralRetrofit(apiService.postGetCameraBadgeCount(params), params, dataResponseListener).call();

    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void CallCameraToken(String CameraID, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.CallCameraToken(ChatApplication.url, CameraID), null, dataResponseListener).call();
    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void SaveCustomRoom(String Roomname, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("room_name", Roomname);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

//        new GeneralRetrofit(apiService.postWebserviceCall(Constants.ADD_CUSTOME_ROOM, params), params, dataResponseListener).call();
        new GeneralRetrofit(apiService.postCustomRoom(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void DeleteCamera(String camera_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("camera_id", camera_id);

//        new GeneralRetrofit(apiService.postWebserviceCall(Constants.DELETE_CAMERA, params), params, dataResponseListener).call();
        new GeneralRetrofit(apiService.postDeletCamera(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void GetBadgeClear(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.GetBadgeClear(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /*Dash board */  // dev arpan on 24 june 2020
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
            new GeneralRetrofit(apiService.DeviceOnOff_Type3(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.DeviceOnOff_Not_Type3(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }


    /*Dash board */  // dev arpan on 24 june 2020
    public void CallPanelOnOffApi(String roomId, String panelId, int panel_status, int type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        if (type == 1) {
            params.put("room_id", roomId);
            params.put("room_status", panel_status);
            new GeneralRetrofit(apiService.CallPanelOnOffApi_Type1(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            params.put("panel_id", panelId);
            params.put("panel_status", panel_status);

            new GeneralRetrofit(apiService.CallPanelOnOffApi_Not_Type1(ChatApplication.url, params), params, dataResponseListener).call();

        }
    }

    /*Dash board */  // dev arpan on 24 june 2020
    public void DeleteRoom(String room_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_id", room_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.DeleteRoom(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*Dashboard Fragment*/  // dev arpan on 25 june 2020
    /*Mood fragment */  // dev arpan add on 26 june 2020 // also calling from mood fragment too

    public void SendRemoteCommand(DeviceVO deviceVO, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("device_id", deviceVO.getDeviceId());
        params.put("device_status", deviceVO.getDeviceStatus());

        new GeneralRetrofit(apiService.DeviceOnOff_Not_Type3(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*Dashboard Fragement*/ // dev arpan add on 25 june 2020

    public void GetDeviceLocal(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_type", "room");
        params.put("device_push_token", Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postDeviceLocal_Cloud(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*DashBoard Fragment*/  /*ScheduleActivity*/  // from both site call same API

    public void GetDeviceClould(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_type", "room");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.postDeviceLocal_Cloud(ChatApplication.url, params), params, dataResponseListener).call(); // for device cloud and local both use same method
    }

    /*Dashboard Fragment*/

    public void GetMacAddress(String localip, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.GetMacAddressCall(localip), null, dataResponseListener).call();

    }


    /*Mood fragment */  // dev arpan add on 24 june 2020
    public void DeviceOnOff(DeviceVO deviceVO, DataResponseListener dataResponseListener) {

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
            params.put("device_id", deviceVO.getDeviceId());
            params.put("panel_id", deviceVO.getPanel_id());
            params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
        }

        if (deviceVO.getDeviceType().equalsIgnoreCase("3")) {
            new GeneralRetrofit(apiService.DeviceOnOff_Type3(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.DeviceOnOff_Not_Type3(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    /*Mood Fragment*/  // dev arpan add on 24 june 2020
    public void DeleteMood(String moodID, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("mood_id", moodID);

        new GeneralRetrofit(apiService.DeleteMood(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*Mood fragment*/  // dev arpan add on 24 june 2020

    public void ChangeMoodStatus(RoomVO moodVO, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("mood_id", moodVO.getRoomId());
        params.put("mood_status", moodVO.getRoom_status());

        new GeneralRetrofit(apiService.ChangeMoodStatus(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*Mood Fragment */  // dev arpan add on 27 june 2020

    public void GetDeviceDetails(String original_room_device_id, DataResponseListener dataResponseListener) {

        /* dev arp ignore the token id in url from old api due to black value pass*/
        new GeneralRetrofit(apiService.GetDeviceDetails(ChatApplication.url, original_room_device_id), null, dataResponseListener).call();
    }



    /*Mood fragment */ /*ScheduleActivity*/       // dev arpan add on 27 june 2020

    public void GetMoodList(DataResponseListener dataResponseListener) {

        /* dev arp ignore the token id in url from old api due to black value pass*/
        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_type", "mood");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GetMoodList(ChatApplication.url, params), params, dataResponseListener).call();


    }



    /*ScheduleFragment */  // dev arpan add on 27 june 2020


    public void GetScheduleList(String isActivityType, boolean isFilterType, String moodId, String moodId3, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();


        if (TextUtils.isEmpty(isActivityType)) {
            params.put("schedule_device_type", isFilterType ? "mood" : "room");
        } else {
            if (TextUtils.isEmpty(moodId)) {
                params.put("schedule_device_type", "room");
                params.put("room_id", "" + moodId3);
            } else {
                params.put("schedule_device_type", "mood");
                params.put("room_id", "" + moodId); //moodId1
            }
        }

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.GetScheduleList(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*Schedule Fragment */  // deva rpan add 27 june 2020

    public void DeleteSchedule(String schedule_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("schedule_id", schedule_id);


        new GeneralRetrofit(apiService.DeleteSchedule(ChatApplication.url, params), params, dataResponseListener).call();

    }

    public void ChangeScheduleStatus(ScheduleVO scheduleVO, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        String timer_on_date = "";
        String timer_off_date = "";

        try {
            ChatApplication.logDisplay("is ative is " + scheduleVO.getIs_active());
            params.put("schedule_id", scheduleVO.getSchedule_id());
            params.put("is_active", scheduleVO.getIs_active() == 1 ? "y" : "n");

            if (scheduleVO.getIs_active() == 1 && scheduleVO.getSchedule_type() == 1) {

                String sch_on_after = scheduleVO.getSchedule_device_on_time().trim();
                String sch_off_date = scheduleVO.getSchedule_device_off_time().trim();

                if (!TextUtils.isEmpty(sch_on_after) && !TextUtils.isEmpty(sch_off_date) && !sch_off_date.equalsIgnoreCase("null")) {
                    ChatApplication.logDisplay("timer is " + sch_on_after);
                    try {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        sch_on_after = Common.getHH(sch_on_after);
                        ChatApplication.logDisplay("timer is " + sch_on_after);
                        calendar.add(Calendar.HOUR, Integer.parseInt(sch_on_after.split(":")[0]));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(sch_on_after.split(":")[1]));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                        String formattedDate = dateFormat.format(calendar.getTime()).toString();

                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                        Date sendDate = null;
                        try {
                            sendDate = format.parse(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar sendCalendar = Calendar.getInstance();
                        sendCalendar.setTime(sendDate);


                        SimpleDateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        timer_on_date = sendDateFormat.format(calendar.getTime()).toString();


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(sch_off_date) && !sch_off_date.equalsIgnoreCase("null")) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            sch_off_date = Common.getHH(sch_off_date);
                            calendar.add(Calendar.HOUR, Integer.parseInt(sch_off_date.split(":")[0]));
                            calendar.add(Calendar.MINUTE, Integer.parseInt(sch_off_date.split(":")[1]));

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                            String formattedDate = dateFormat.format(calendar.getTime()).toString();

                            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                            Date sendDate = null;
                            try {
                                sendDate = format.parse(formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar sendCalendar = Calendar.getInstance();
                            sendCalendar.setTime(sendDate);


                            SimpleDateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            timer_off_date = sendDateFormat.format(calendar.getTime()).toString();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    scheduleVO.setSchedule_device_on_time(timer_on_date + " " + sch_on_after);
                    scheduleVO.setSchedule_device_off_time(timer_off_date + " " + sch_off_date);
                    params.put("on_time", timer_on_date + " " + sch_on_after + ":00");
                    params.put("off_time", timer_off_date + " " + sch_off_date + ":00");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.ChangeScheduleStatus(ChatApplication.url, params), params, dataResponseListener).call();
    }



    /*User Child List Fragment*/  // dev apran add 27 june 2020

    public void GetUserCHildList(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.GetUserCHildList(ChatApplication.url), null, dataResponseListener).call();
    }


    /*User Child List Fragment*/  // dev apran add 27 june 2020
    public void DeleteUserChild(String userID, DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("child_user_id", userID);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.DeleteUserChild(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*UserProfileFragment*/  // dev arpan add on 27 june 2020

    public void GetProfile(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.GetProfile(ChatApplication.url), null, dataResponseListener).call();
    }

    /*UserProfileFragment*/  // dev arpan add on 27 june 2020

    public void SaveProfile(String firstname, String lastname, String username, String userphone, String useremail, String strPassword, DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        params.put("first_name", firstname);
        params.put("last_name", lastname);
        params.put("user_name", username);
        params.put("user_phone", userphone);
        params.put("user_email", useremail);
        if (strPassword.length() > 0) {
            params.put("user_password", "" + strPassword);
        } else {
            params.put("user_password", "");
        }


        new GeneralRetrofit(apiService.SaveProfile(ChatApplication.url, params), params, dataResponseListener).call();

    }



    /*AddRoomDialog */  // dev arpan add on 27 june 2020

    public void ConfigureNewRoom(String room_id, String panelname, String module_id, String module_type, boolean isRoom, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_id", room_id);
        params.put("panel_name", panelname);
        params.put("module_id", module_id);
        params.put("module_type", module_type);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (!isRoom) {
            params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
            params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        }

        new GeneralRetrofit(apiService.ConfigureNewRoom(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*DeviceEditDialog*/  // dev arpan add on 27 june 2020

    public void DeleteDevice(String original_room_device_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("original_room_device_id", original_room_device_id);

        new GeneralRetrofit(apiService.DeleteDevice(ChatApplication.url, params), params, dataResponseListener).call();


    }


    /*DeviceEditDialog*/  // dev arpan add on 27 june 2020

    public void SaveSwithcDetails(String device_id, String device_name, String devicetype, String deviceicon, String subtype, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("device_id", device_id);
        params.put("device_name", device_name);
        if (devicetype.equalsIgnoreCase("curtain")) {
            params.put("device_icon", "");
        } else {
            params.put("device_icon", deviceicon);
        }

        if (devicetype.equals("fan")) {
            params.put("device_sub_type", subtype);
        }

        new GeneralRetrofit(apiService.SaveSwithcDetails(ChatApplication.url, params), params, dataResponseListener).call();


    }


    /*FanDialog*/ // dev arpan add on 27 june 2020

    public void ChangeFanSpeed(String room_device_id, int fanSpeed, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();


        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("device_id", room_device_id);
        params.put("device_status", "1");
        params.put("device_sub_status", fanSpeed);

        new GeneralRetrofit(apiService.ChangeFanSpeed(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*UserChildActivity*/ // dev arpan add on 29 june 2020

    public void GetRoomList(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_type", "room");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GetRoomList(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*UserChildActivity*/ // dev arpan add on 29 june 2020

    public void AddUserChild(String modeType, String child_user_id, JSONArray roomlist, JSONArray cameraList, String user_name, String display_name, String strPassword, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();


        if (!TextUtils.isEmpty(strPassword) && strPassword.length() > 0) {
            params.put("user_password", "" + strPassword);
        }

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        if (modeType.equalsIgnoreCase("update")) {
            params.put("child_user_id", child_user_id);
            params.put("roomList", roomlist);
            params.put("cameraList", cameraList);

            new GeneralRetrofit(apiService.UpdateUserChild(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            params.put("user_name", user_name);
            params.put("display_name", display_name);
            params.put("roomList", roomlist);
            params.put("cameraList", cameraList);

            new GeneralRetrofit(apiService.AddUserChild(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }


    /*SmartRemoteActivity*/ // dev arpan add on 29 june 2020

    public void SaveSensor(String door_name, String door_module_id, String module_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("module_id", door_module_id);
        params.put("device_name", door_name);
        params.put("module_type", module_type);


        new GeneralRetrofit(apiService.ConfigureNewRoom(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*SmartRemoteActivity*/ // dev arpan add on 29 june 2020
    public void GetRemoteLIst(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("device_type", "smart_remote");

        new GeneralRetrofit(apiService.GetRemoteLIst(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*SmartColorPickerActivity*/ // dev arpan add on 29 june 2020
    public void CallHueLightState(int progresbar, JSONArray jsonArray, String roomDeviceId, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        params.put("status", 1);
        params.put("bright", progresbar);
        params.put("is_rgb", 1);//1;
        params.put("rgb_array", jsonArray);
        params.put("room_device_id", roomDeviceId);

        new GeneralRetrofit(apiService.CallHueLightState(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*SmartColorPickerActivity*/ // dev arpan add on 29 june 2020
    public void GetPhilipsHueParams(String original_room_device_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("original_room_device_id", original_room_device_id);

        new GeneralRetrofit(apiService.GetPhilipsHueParams(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*SignUp*/ // dev arpan add on 29 june 2020

    public void Signup(String url, String first_name, String last_name, String user_name, String user_email, String user_password, String user_phone, String imei, String token, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();


        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("user_name", user_name);
        params.put("user_email", user_email);
        params.put("user_password", user_password);
        params.put("user_phone", user_phone);
        params.put("phone_id", imei);
        params.put("phone_type", "android");
        params.put("device_push_token", token);


        new GeneralRetrofit(apiService.Signup(url, params), params, dataResponseListener).call();

    }


    /*ScheduleActivity*/  // dev arpan add on 29 june 2020


    public void AddSchedule(boolean isEdit, Object deviceObj, DataResponseListener dataResponseListener) {
        if (isEdit) {
            new GeneralRetrofit(apiService.EditSchedule(ChatApplication.url, deviceObj), deviceObj, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.AddSchedule(ChatApplication.url, deviceObj), deviceObj, dataResponseListener).call();
        }
    }

    /*ScheduleActivity*/  // dev arpan add on 29 june 2020

    public void GetDeviceDetails_Schedule(String url, String original_room_device_id, DataResponseListener dataResponseListener) {

        new GeneralRetrofit(apiService.GetDeviceDetails(url, original_room_device_id), null, dataResponseListener).call();
    }


    /*RoomEditActivity2*/  // dev arpan add on 29 june 2020
    public void SaveRoom(String room_id, String room_name, JSONArray panelArray, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        if (panelArray != null && panelArray.length() > 0) {
            params.put("panel_data", panelArray);
        }

        new GeneralRetrofit(apiService.SaveRoom(ChatApplication.url, params), params, dataResponseListener).call();

    }


    /*RoomEditActivity2*/  // dev arpan add on 29 june 2020

    public void GetDeviceList(String room_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_type", "room");
        params.put("room_id", room_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GetRooms(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*RoomEditActivity2*/  // dev arpan add on 29 june 2020

    public void ShowDeviceEditDialog(DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.ShowDeviceEditDialog(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /*RoomEditActivity2*/  // dev arpan add on 29 june 2020

    public void DeletePanel(String panel_id, DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("panel_id", panel_id);

        new GeneralRetrofit(apiService.DeletePanel(ChatApplication.url, params), params, dataResponseListener).call();
    }


    public void SaveNotiSettingList(JSONArray dataArray, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (dataArray != null && dataArray.length() > 0) {

            params.put("data", dataArray);
            params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

            new GeneralRetrofit(apiService.SaveNotiSettingList(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }


    public void GetNotificationList(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GetNotificationList(ChatApplication.url, params), params, dataResponseListener).call();
    }

    public void LogoutCloudUser(String user_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();


        String device_push_token = Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN);
        params.put("user_id", "" + user_id);
        params.put("device_push_token", "" + device_push_token);
        params.put("phone_id", APIConst.PHONE_ID_VALUE);
        params.put("phone_type", APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.LogoutCloudUser(params), params, dataResponseListener).call();

    }

    public void GetLogFind(String ROOM_ID, int position, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("filter_action", "door_open,door_close,temp_alert,gas_detected,water_detected,door_lock,door_unlock");
        params.put("filter_type", "room");
        params.put("room_id", "" + ROOM_ID);
        params.put("unseen", 1);
        params.put("notification_number", "" + position);


        new GeneralRetrofit(apiService.GetLogFind(ChatApplication.url, params), params, dataResponseListener).call();

    }


    public void CallreadCountApi(JSONArray jsonArray, DataResponseListener dataResponseListener) {

        if (jsonArray != null && jsonArray.length() > 0) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("update_logs", jsonArray);

            new GeneralRetrofit(apiService.CallreadCountApi(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }


    public void GetDeviceLogList(String room, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        switch (room.toLowerCase()) {
            case "Room":
                params.put("room_type", "room");
                new GeneralRetrofit(apiService.RoomLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Mood":
                params.put("room_type", "mood");
                new GeneralRetrofit(apiService.MoodLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Schedule":
                new GeneralRetrofit(apiService.ScheduleLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Gas sensor":
                params.put("device_type", "gas_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Temp sensor":
                params.put("device_type", "temp_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Door sensor":
                params.put("device_type", "door_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Water detector":
                params.put("device_type", "water_detector");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "Lock":
                params.put("device_type", "lock");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            default:
                new GeneralRetrofit(apiService.RoomLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;

        }

    }


    public void CallFilterData(JSONObject jsonObject, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.CallFilterData(ChatApplication.url, jsonObject), jsonObject, dataResponseListener).call();
    }

    public void GetDeviceLog(JSONObject jsonObject, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.GetDeviceLog(ChatApplication.url, jsonObject), jsonObject, dataResponseListener).call();
    }

    public void GetMoodNameList(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        new GeneralRetrofit(apiService.GetMoodNameList(ChatApplication.url, params), params, dataResponseListener).call();
    }


  public void GetDeviceList(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        new GeneralRetrofit(apiService.GetDeviceList(ChatApplication.url, params), params, dataResponseListener).call();
    }


  public void  SaveMood(boolean editMode,JSONObject jsonObject,DataResponseListener dataResponseListener){

      if (editMode) {
          new GeneralRetrofit(apiService.EditMood(ChatApplication.url, jsonObject), jsonObject, dataResponseListener).call();
      } else {
          new GeneralRetrofit(apiService.AddMood(ChatApplication.url, jsonObject), jsonObject, dataResponseListener).call();
      }

  }



}