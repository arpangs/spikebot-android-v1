package com.spike.bot.api_retrofit;


import android.content.Context;
import android.text.TextUtils;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kp.core.DateHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.Filter;
import com.spike.bot.model.RemoteDetailsRes;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.ScheduleVO;
import com.spike.bot.model.UnassignedListRes;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SpikeBotApi {


    /*dev arp add this class on 23 june 2020*/

    private static SpikeBotApi SpikeBotApi = null;
    private static ApiInterface apiService = RestClient.getInstance().getApiInterface();
    private Context mContext;

    public static SpikeBotApi getInstance() {
        if (SpikeBotApi == null)
            return new SpikeBotApi();
        return SpikeBotApi;
    }

    /*Login screen */ // dev arpan on 23 june 2020
    public void loginUser(String userName, String password, String imei, DataResponseListener dataResponseListener) {
        // Constants.isNotStaticURL = false;
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
        } else if (deviceVO.getDeviceType().equalsIgnoreCase("pir_device")) {
            params.put("device_id", deviceVO.getDeviceId());
            params.put("panel_id", deviceVO.getPanel_id());
            params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
            params.put("device_sub_status", deviceVO.getDevice_sub_status());
            params.put("device_sub_type", deviceVO.getDevice_sub_type());

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
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE != null ? APIConst.PHONE_ID_VALUE : "");
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

    public void SaveProfile(String firstname, String lastname, String username, String userphone, String useremail, String strnewPassword, DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        params.put("first_name", firstname);
        params.put("last_name", lastname);
        params.put("user_name", username);
        params.put("user_phone", userphone);
        params.put("user_email", useremail);
        if (strnewPassword.length() > 0) {
            params.put("user_password", "" + strnewPassword);
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

    public void DeleteDeviceDailog(String original_room_device_id, DataResponseListener dataResponseListener) {

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

    public void AddUserChild(String modeType, String child_user_id, ArrayList<String> roomlist, ArrayList<String> cameraList, String user_name, String display_name, String strPassword, DataResponseListener dataResponseListener) {

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


        new GeneralRetrofit(apiService.Signup(params), params, dataResponseListener).call();

    }


    /*ScheduleActivity*/  // dev arpan add on 29 june 2020


    public void AddSchedule(boolean isEdit, HashMap<String, Object> deviceObj, DataResponseListener dataResponseListener) {
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
    public void SaveRoom(String room_id, String room_name, JsonArray panelArray, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        if (panelArray != null && panelArray.size() > 0) {
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


    public void SaveNotiSettingList(JsonArray dataArray, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (dataArray != null && dataArray.size() > 0) {

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
        params.put("auth_key", Common.getPrefValue(ChatApplication.getContext(), Constants.AUTHORIZATION_TOKEN)); // dev arpan add this new param as discussed with dev parth


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


    public void CallreadCountApi(JsonArray jsonArray, DataResponseListener dataResponseListener) {

        if (jsonArray != null && jsonArray.size() > 0) {
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
            case "room":
                params.put("room_type", "room");
                new GeneralRetrofit(apiService.RoomLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "mood":
                params.put("room_type", "mood");
                new GeneralRetrofit(apiService.MoodLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "schedule":
                new GeneralRetrofit(apiService.ScheduleLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "gas sensor":
                params.put("device_type", "gas_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "temp sensor":
                params.put("device_type", "temp_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "door sensor":
                params.put("device_type", "door_sensor");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "water detector":
                params.put("device_type", "water_detector");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            case "lock":
                params.put("device_type", "lock");
                new GeneralRetrofit(apiService.SensorLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;
            default:
                new GeneralRetrofit(apiService.RoomLogListApi(ChatApplication.url, params), params, dataResponseListener).call();
                break;

        }

    }


    /*public void CallFilterData(boolean isFilterActive, int position, String mRoomId, String start_date, String end_date, Spinner mSpinnerRoomList, Spinner mSpinnerRoomMood,
                               boolean isFilterType, ArrayList<Filter> filterArrayList, String actionType, String isCheckActivity, String strDeviceId, String strpanelId, String typeofFilter, boolean userChange,
                               DataResponseListener dataResponseListener) {*/
    public void CallFilterData(HashMap<String, Object> params,DataResponseListener dataResponseListener) {

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.CallFilterData(ChatApplication.url, params), params, dataResponseListener).call();
    }

    public void GetDeviceLog(boolean isFilterActive, int position, String mRoomId, String start_date, String end_date, Spinner mSpinnerRoomList, Spinner mSpinnerRoomMood,
                             boolean isFilterType, ArrayList<Filter> filterArrayList, String actionType, String isCheckActivity, String strDeviceId, String strpanelId, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (isFilterActive) {
            params.put("notification_number", position);
            params.put("room_id", "" + mRoomId);
            params.put("start_datetime", start_date);
            params.put("end_datetime", end_date);


            RoomVO roomVO = (RoomVO) mSpinnerRoomList.getSelectedItem();
            JsonArray array = new JsonArray();
            if (mSpinnerRoomMood.getSelectedItem().toString().equals("All")) {
                if (isFilterType) {
                    params.put("sensor_type", "all");

                    String actionname = "";
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    if (filterArrayList.size() > 0) {
                        for (int i = 0; i < filterArrayList.size(); i++) {
                            if (filterArrayList.get(i).isChecked()) {
                                stringArrayList.add(filterArrayList.get(i).getName());
                            }
                        }

                        for (int i = 0; i < stringArrayList.size(); i++) {
                            if (i == stringArrayList.size() - 1) {
                                actionname = actionname + "'" + stringArrayList.get(i) + "'";
                            } else {
                                actionname = actionname + "'" + stringArrayList.get(i) + "',";
                            }
                        }
                        JsonObject subObject = new JsonObject();
                        subObject.addProperty("activity_action", actionname);
                        subObject.addProperty("activity_type", "All");
                        array.add(subObject);
                        params.put("filter_data", array);
                    }

                } else {
                    String actionname = "";
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    if (filterArrayList.size() > 0) {
                        for (int i = 0; i < filterArrayList.size(); i++) {
                            if (filterArrayList.get(i).isChecked()) {
                                stringArrayList.add(filterArrayList.get(i).getName());
                            }
                        }

                        for (int i = 0; i < stringArrayList.size(); i++) {
                            if (i == stringArrayList.size() - 1) {
                                actionname = actionname + "'" + stringArrayList.get(i) + "'";
                            } else {
                                actionname = actionname + "'" + stringArrayList.get(i) + "',";
                            }
                        }
                        JsonObject subObject = new JsonObject();
                        subObject.addProperty("activity_action", actionname);

                        subObject.addProperty("activity_type", "All");
                        array.add(subObject);
                        params.put("filter_data", array);
                    }
                }

            } else {

                if (isFilterType) {

                    if (actionType.equalsIgnoreCase("Door Sensor")) {
                        params.put("sensor_type", "door");
                    } else {
                        params.put("sensor_type", "temp");
                    }

                    params.put("room_id", "");
                    params.put("panel_id", "");
                    if (roomVO != null) {
                        params.put("module_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));
                    } else {
                        params.put("module_id", "");
                    }

                } else {
                    if (roomVO != null) {
                        params.put("room_id", "" + (roomVO.getRoomId().equalsIgnoreCase("0") ? "" : roomVO.getRoomId()));
                    } else if (isCheckActivity.equals("room")) {
                        params.put("room_id", "" + mRoomId);
                    } else {
                        params.put("room_id", "");
                    }

                    if (actionType.equals("Room")) {
                        params.put("module_id", "" + strDeviceId);
                        params.put("panel_id", "" + strpanelId);
                    } else {
                        params.put("panel_id", "");
                        params.put("module_id", "");
                    }
                }
                String actionname = "";
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; i < filterArrayList.size(); i++) {

                    if (filterArrayList.get(i).isChecked()) {
                        stringArrayList.add(filterArrayList.get(i).getName());
                    }
                }

                for (int i = 0; i < stringArrayList.size(); i++) {
                    if (i == stringArrayList.size() - 1) {
                        actionname = actionname + "'" + stringArrayList.get(i) + "'";
                    } else {
                        actionname = actionname + "'" + stringArrayList.get(i) + "',";
                    }
                }
                JsonObject subObject = new JsonObject();
                subObject.addProperty("activity_action", actionname);
                subObject.addProperty("activity_type", "" + actionType);

                array.add(subObject);
                params.put("filter_data", array);
            }


        } else {
            params.put("notification_number", position);
            params.put("start_datetime", "");
            params.put("end_datetime", "");
            params.put("filter_data", "");
            if (TextUtils.isEmpty(mRoomId)) {
                params.put("room_id", "");
            } else {
                params.put("room_id", "" + mRoomId);
            }

            params.put("panel_id", "");
            if (isCheckActivity.equals("mode")) {
                params.put("filter_type", "mood");
            } else if (isCheckActivity.equalsIgnoreCase("mood")) {
                params.put("filter_type", "room");
            } else if (isCheckActivity.equalsIgnoreCase("schedule")) {
                params.put("filter_type", "schedule");
            } else {
                params.put("filter_type", "");
            }
        }
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.GetDeviceLog(ChatApplication.url, params), params, dataResponseListener).call();
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


    public void SaveMood(boolean editMode, HashMap<String, Object> moodObj, DataResponseListener dataResponseListener) {

        if (editMode) {
            new GeneralRetrofit(apiService.EditMood(ChatApplication.url, moodObj), moodObj, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.AddMood(ChatApplication.url, moodObj), moodObj, dataResponseListener).call();
        }

    }


    // AllUnassignedpanel - room listing
    public void getRoomList(String room_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_type", room_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.roomslist(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AllUnassignedpanel - unassign panel list
    public void getUnAssignedList(String module_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("module_type", module_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GetUnAssignedList(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AllUnassignedpanel - delete device from unassignlist
    public void DeleteDevice(String module_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("module_id", module_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.devicemoduledelete(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // get device List
    public void getDeviceList(String device_type, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_type", device_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.devicefind(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // update device
    public void updateDevice(String device_id, String device_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("device_name", device_name);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.addunAssignRepater(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.savePanel(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // Adddevice - save room
    public void saveCustomRoom(String room_name, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_name", room_name);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.saveCustomRoom(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // adddevice - get config device data
    public void getConfigData(int type, DataResponseListener dataResponseListener) {

        switch (type) {

            case 1:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "door_sensor"), null, dataResponseListener).call();
                break;
            case 2:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "temp_sensor"), null, dataResponseListener).call();
                break;
            case 5:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "gas_sensor"), null, dataResponseListener).call();
                break;
            case 6:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "curtain"), null, dataResponseListener).call();
                break;
            case 7:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "water_detector"), null, dataResponseListener).call();
                break;
            case 8:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "pir_detector"), null, dataResponseListener).call();
                break;
            default:
                new GeneralRetrofit(apiService.getConfigData(ChatApplication.url, "5"), null, dataResponseListener).call();
                break;
        }

    }

    // add device - save camera key
    public void saveCameraKey(String key, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("key", key);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.saveCameraKey(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.addCamera(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.addDevice(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // add existing panel - get room camera list
    public void getroomcameralist(String room_type, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("room_type", room_type);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.getroomcameralist(ChatApplication.url), params, dataResponseListener).call();
    }

    // addexisting panel - get custompanel detail
    public void getCustomPanelDetail(DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.getCustomPanelDetail(ChatApplication.url, params), params, dataResponseListener).call();

    }

    // addexisting panel - save existing panel
    public void saveExistPanel(String room_id, String panel_name, String panel_id, boolean isDeviceAdd, ArrayList<String> listDevice, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        if (!isDeviceAdd) {
            params.put("room_id", room_id);
            params.put("panel_name", panel_name);
        } else {
            params.put("panel_id", panel_id);
        }

        params.put("devices", listDevice);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        if (!isDeviceAdd) {
            new GeneralRetrofit(apiService.ADD_CUSTOM_PANEL(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.ADD_CUSTOME_DEVICE(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }

    // Cameradevicelog - call smart camera
    public void callGetSmarCamera(String jetson_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_device_id", jetson_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.cameralistbyjetson(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.getCameraLogs(params), params, dataResponseListener).call();
    }

    // Cameradevicelog - camera unread count clear
    public void callupdateUnReadCameraLogs(String log_type, String camera_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("log_type", log_type);
        params.put("camera_id", "" + camera_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.callupdateUnReadCameraLogs(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.updateCamera(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.getAllCameraList(ChatApplication.url, params), params, dataResponseListener).call();

    }

    // CameraNotification - Add camera
    public void callAddCamera(String start_time, String end_time, int edIntervalTime, ArrayList<CameraVO> getCameraList, DataResponseListener dataResponseListener) {

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

        JsonArray roomDeviceArray = new JsonArray();
        for (CameraVO dPanel : getCameraList) {

            if (dPanel.getIsSelect()) {
                JsonObject object = new JsonObject();
                object.addProperty("camera_id", dPanel.getCamera_id());
                object.addProperty("camera_name", "" + dPanel.getCamera_name());
                object.addProperty("camera_url", "" + dPanel.getCamera_url());
                object.addProperty("camera_ip", "" + dPanel.getCamera_ip());
                object.addProperty("camera_videopath", "" + dPanel.getCamera_videopath());
                object.addProperty("camera_icon", "" + dPanel.getCamera_icon());
                object.addProperty("camera_vpn_port", "" + dPanel.getCamera_vpn_port());
                object.addProperty("user_name", "" + dPanel.getUserName());
                object.addProperty("password", "" + dPanel.getPassword());

                roomDeviceArray.add(object);

            }
        }

        params.put("cameraList", roomDeviceArray);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.addCameraNotification(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // CameraNotification - Update camera
    public void callUpdateCamera(String start_time, String end_time, CameraAlertList cameraAlertList, String jetson_id, int edIntervalTime,
                                 ArrayList<CameraVO> getCameraList, JsonArray array, JsonArray roomDeviceArray, DataResponseListener dataResponseListener) {


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

        params.put("camera_ids", array);
        params.put("cameraList", roomDeviceArray);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.updateCameraNotification(ChatApplication.url, params), params, dataResponseListener).call();

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

        new GeneralRetrofit(apiService.getCameraNotificationAlertList(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.getUnseenCameraLog(params), params, dataResponseListener).call();

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
            new GeneralRetrofit(apiService.changeCameraAlertStatus(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.deleteCameraNotification(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    // CameraNotification - callupdateUnReadCameraLogs
    public void callupdateUnReadCameraLogs(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("log_type", "camera");
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.logsfind(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // CameraPlayBack - searchCameraList
    public void searchCameraList(String start_date, String end_date, ArrayList<CameraVO> cameraVOArrayList, List<String> selectedCamera, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_start_date", start_date);
        params.put("camera_end_date", end_date);

        JsonArray jsonArray = new JsonArray();
        for (CameraVO cameraVO : cameraVOArrayList) {
            for (String ss : selectedCamera) {
                if (cameraVO.getCamera_name().equalsIgnoreCase(ss)) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("camera_id", cameraVO.getCamera_id());
                    ob.addProperty("camera_name", cameraVO.getCamera_name());
                    jsonArray.add(ob);
                }
            }
        }
        params.put("camera_details", jsonArray);

        new GeneralRetrofit(apiService.GET_CAMERA_RECORDING_BY_DATE(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // ImageZoom - callReport false image
    public void callReport(String image_url, String home_controller_device_id, DataResponseListener dataResponseListener) {
        // Constants.isNotStaticURL = false;
        HashMap<String, Object> params = new HashMap<>();
        params.put("image_url", image_url);
        params.put("home_controller_device_id", home_controller_device_id);

        new GeneralRetrofit(apiService.reportFalseImage(params), params, dataResponseListener).call();
    }

    // ImageZoom - CameraRecordingPlay
    public void CameraRecordingPlay(String camera_id, String timestamp, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_id", camera_id);
        params.put("timestamp", timestamp);

        new GeneralRetrofit(apiService.camerarecording(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.CHANGE_DEVICE_STATUS(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // curtain - deletecurtainPanel
    public void deleteDevice(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.DELETE_MODULE(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // heavyloaddetail - getHeavyLoadValue
    public void getHeavyLoadValue(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.deviceheavyloadping(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // heavyloaddetail - getHeavyLoaddetail
    public void getHeavyloadDetails(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.getHeavyloadDetails(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.getHeavyLoadDetails(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.ADD_IR_BLASTER(ChatApplication.url, params), params, dataResponseListener).call();

    }

    // IRBlasterRemote -  getdeviceino - get all type of device info call this service
    public void deviceInfo(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.deviceinfo(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.CHANGE_DEVICE_STATUS(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //IRRemoteBrandList - getIRDetailsList
    public void getIRDetailsList(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getIRDeviceTypeBrands(ChatApplication.url, "1"), null, dataResponseListener).call();
    }

    //IRRemoteBrandList - getIRRemoteDetails
    public void getIRRemoteDetails(int device_brand_id, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getDeviceBrandRemoteList(ChatApplication.url, device_brand_id), null, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.SEND_ON_OFF_COMMAND(ChatApplication.url, params), params, dataResponseListener).call();
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

        new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //Repeater - saveRepeater
    public void saveRepeater(String device_name, String door_module_id, String module_type, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", "");
        params.put("device_name", device_name);
        params.put("module_id", door_module_id);
        params.put("module_type", module_type);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //DoorSensorInfo  - getDoorSensorDetails
    public void getDoorSensorDetails(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.deviceinfo(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //DoorSensorInfo  - doorSensor Notification Status
    public void doorSensorNotificationStatus(String doorSensorNotificationId, boolean isActive, boolean isNotification, DataResponseListener dataResponseListener) {
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
            new GeneralRetrofit(apiService.CHANGE_DOOR_SENSOR_STATUS(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.UPDATE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    //DoorSensorInfo - add Notification
    public void addNotification(HashMap<String, Object> params, boolean isEdit, DataResponseListener dataResponseListener) {

        if (isEdit) {
            new GeneralRetrofit(apiService.UPDATE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.ADD_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    //DoorSensorInfo - deleteDoorSensorNotification
    public void deleteDoorSensorNotification(String alert_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("alert_id", alert_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.DELETE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //DoorSensorInfo - unread notification
    public void unreadNotification(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("log_type", "device");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.MARK_SEEN(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // MultiSensor - tempSensorNotificationStatus
    public void tempSensorNotificationStatus(String alert_id, boolean is_active, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("alert_id", alert_id);
//        params.put("is_active", is_active);
        params.put("is_active", is_active ? "y" : "n");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.UPDATE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //MultiSensor - updateTempSensor
    public void updateTempSensor(String temp_module_id, String device_name, int isCFSelected, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", temp_module_id);
        params.put("device_name", device_name);
        params.put("unit", isCFSelected == 1 ? "C" : "F");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //MultiSensor - addHumity
    public void addHumity(int min_humidity, int max_humidity, String days, String alert_id, String device_id, boolean isEdit, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("min_humidity", min_humidity);
        params.put("max_humidity", max_humidity);
        params.put("alert_type", "humidity");
        params.put("days", days);
        if (isEdit) {
            params.put("alert_id", alert_id);

        } else {
            params.put("device_id", device_id);
        }
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (!isEdit) {
            new GeneralRetrofit(apiService.ADD_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.UPDATE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    //MultiSensor - addNotification
    public void addTempNotification(int min_temp, int max_temp, String days, String alert_id, String device_id, boolean isEdit, int isCFSelected, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        if (isCFSelected == 1) {
            params.put("min_temp", min_temp);
            params.put("max_temp", max_temp);
        } else {
           /* params.put("min_temp", min_temp);
            params.put("max_temp", max_temp);*/

            params.put("min_temp", Constants.getCTemp(String.valueOf(min_temp)));
            params.put("max_temp", Constants.getCTemp(String.valueOf(max_temp)));
        }

        params.put("alert_type", "temperature");
        params.put("days", days);
        if (isEdit) {
            params.put("alert_id", alert_id);

        } else {
            params.put("device_id", device_id);
        }


        if (isEdit) {
            new GeneralRetrofit(apiService.UPDATE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.ADD_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    //MultiSensor -  deleteTempSensorNotification
    public void deleteTempSensorNotification(RemoteDetailsRes.Data.Alert notificationList, RemoteDetailsRes.Data.Alert notification,
                                             DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        if (notificationList != null) {
            params.put("alert_id", notificationList.getAlertId());

        } else {
            params.put("alert_id", notification.getAlertId());
        }

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.DELETE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //SensorUnassigned - get repeaterlist
    public void callReptorList(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getUnassignedRepeaterList(ChatApplication.url), null, dataResponseListener).call();
    }

    //SensorUnassigned - get sensor unassinged list
    public void getSensorUnAssignedDetails(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.deviceunassigned(ChatApplication.url), null, dataResponseListener).call();
    }

    //SensorUnassigned - saveCurtain
    public void saveCurtain(String curtain_module_id, String curtain_name, String room_id, String room_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("curtain_module_id", curtain_module_id);
        params.put("curtain_name", curtain_name);
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //SensorUnassigned - saveRepeator
    public void saveRepeaters(String repeator_module_id, String repeator_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("repeator_module_id", repeator_module_id);
        params.put("repeator_name", repeator_name);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.reassignRepeater(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //SensorUnassigned - save sensor
    public void saveSensorUnassigned(String room_id, String room_name, String sensor_id, String module_id, String sensor_type, String sensor_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_id", room_id);
        params.put("room_name", room_name);
        params.put("sensor_id", sensor_id);
        params.put("module_id", module_id);
        params.put("sensor_type", sensor_type);
        params.put("sensor_name", sensor_name);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.SAVE_UNCONFIGURED_SENSOR(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //WaterSensor - add alert
    public void addalert(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.ADD_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //WaterSensor - deletealert
    public void deletealert(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.DELETE_TEMP_SENSOR_NOTIFICATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddJetSon - GetJetson
    public void callGetJetson(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_name", "jetson");
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.jetsonlist(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddJetSon - AddJetson
    public void callAddJetson(String jetson_id, String jetson_name, String jetson_ip, boolean isFlag, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        if (isFlag) {
            params.put("jetson_id", jetson_id);
        }

        params.put("jetson_name", jetson_name);
        params.put("jetson_ip", jetson_ip);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (isFlag) {
            new GeneralRetrofit(apiService.jetsonupdate(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.jetsonadd(ChatApplication.url, params), params, dataResponseListener).call();
        }
    }

    // AddJetSon - deleteJetson
    public void deleteJetson(String jetson_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_id", jetson_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.jetsondelete(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // SmartCameraActivity - saveJetsonCameraKey
    public void saveJetsonCameraKey(String camerakey, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("key", camerakey);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.validatecamerakey(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // SmartCameraActivity - addCameraCall
    public void addCameraCall(String camera_name, String camera_ip, String user_name, String password, String confidence_score_day,
                              String confidence_score_night, String video_path, String jetson_id, boolean isflag, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("camera_name", camera_name);
        params.put("camera_ip", camera_ip);

        params.put("user_name", user_name);
        params.put("password", password);
        params.put("confidence_score_day", confidence_score_day);
        params.put("confidence_score_night", confidence_score_night);

        params.put("video_path", video_path);
        params.put("jetson_device_id", jetson_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        if (isflag) {
            new GeneralRetrofit(apiService.SAVE_EDIT_CAMERA(ChatApplication.url, params), params, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.ADD_CAMERA(ChatApplication.url, params), params, dataResponseListener).call();
        }

    }

    //SmartCameraActivity - GetSmartCamera
    public void callGetSmartCamera(String jetson_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("jetson_device_id", jetson_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.cameralistbyjetson(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //SmartCameraActivity - deleteJetsoncamera
    public void deleteJetsoncamera(String camera_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("camera_id", camera_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.DELETE_CAMERA(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //AddDeviceconfirm - saveCustomRoom
    public void saveCustomRoomDevice(String room_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("room_name", room_name);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.ADD_CUSTOME_ROOM(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //AddDeviceconfirm - AddTTlock
    public void callAddTTlock(String lock_id, String room_id, String lock_data, String door_name, DataResponseListener dataResponseListener) {
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

        new GeneralRetrofit(apiService.addTTLock(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddDeviceconfirm - saveSensor
    public void saveSensor(String room_id, String room_name, String door_sensor_name, String door_sensor_module_id, DataResponseListener dataResponseListener) {
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

        new GeneralRetrofit(apiService.ADD_DOOR_SENSOR(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddGateWay  -  bridge list
    public void callAddBridge(int gatewayId, String gatewayName, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("module_id", gatewayId);
        params.put("device_name", gatewayName);
        params.put("module_type ", "tt_lock_bridge");

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddGateWay - deleteGateway
    public void deleteGateway(int bridge_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("bridge_id", bridge_id);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.deleteTTLockBridge(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //TTLockList - AllLockList
    public void getAllLockList(DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.getLockLists(ChatApplication.url), null, dataResponseListener).call();
    }

    //Yalelockinfo - update yale lock
    public void updateyalelock(String device_id, String enable_lock_unlock_from_app, String pass_code, String onetime_code, String device_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("enable_lock_unlock_from_app", enable_lock_unlock_from_app);
        params.put("pass_code", pass_code);
        params.put("onetime_code", onetime_code);
        params.put("device_name", device_name);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // AddBeacon - beacon device details
    public void getbeaconDeviceDetails(String device_id, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.beaconscannerscan(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // Beaconconfig - get beacon room list
    public void getBeaconRoomList(DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("room_type", "room");
        params.put("only_on_off_device", 1);
        params.put("only_rooms_of_beacon_scanner", 1);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GET_DEVICES_LIST(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // Beaconconfig - saveBeacon
    public void saveBeacon(HashMap<String, Object> deviceObj, boolean editBeacon, DataResponseListener dataResponseListener) {
        if (editBeacon) {
            new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, deviceObj), deviceObj, dataResponseListener).call();
        } else {
            new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, deviceObj), deviceObj, dataResponseListener).call();
        }
    }

    // Beaconconfig - saveBeacon
    public void getDeviceDetails(String original_room_device_id, DataResponseListener dataResponseListener) {
        new GeneralRetrofit(apiService.GET_MOOD_DEVICE_DETAILS(ChatApplication.url, "/" + original_room_device_id), null, dataResponseListener).call();
    }

    //BeaconDetail - get beacon location list
    public void getBeaconLocationList(String room_id, ArrayList<String> roomListString, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        if (roomListString.equals("all")) {
            params.put("room_id", "");
        } else {
            params.put("room_id", room_id);
        }
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.GET_BEACON_LOCATION(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //BeaconScannerAdd - Add Beacon Scanner
    public void addBeaconScanner(String ir_blaster_name, String ir_blaster_module_id, String room_id, String room_name, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("ir_blaster_name", ir_blaster_name);
        params.put("ir_blaster_module_id", ir_blaster_module_id);
        params.put("room_id", room_id);
        params.put("room_name", room_name);

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.ADD_IR_BLASTER(ChatApplication.url, params), params, dataResponseListener).call();
    }

    //BeaconScannerAdd - update Beacon Scanner
    public void updateBeaconScanner(String device_id, String device_name, String on_time, String off_time, String rangevalue, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("device_id", device_id);
        params.put("device_name", device_name);
        params.put("on_time", on_time);
        params.put("off_time", off_time);
        params.put("range", rangevalue);

        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
    }

    // ScannerWifiList - add scanner
    public void addScanner(String room_id, String device_name, String module_id, String module_type, String on_time, String off_time, String range, DataResponseListener dataResponseListener) {

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

        new GeneralRetrofit(apiService.deviceadd(ChatApplication.url, params), params, dataResponseListener).call();
    }


    /*forgot password*/  // dev arpan add on july 06

    public void forgetPassword(String mobile_no, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_phone", mobile_no);

        new GeneralRetrofit(apiService.forgetpassword(params), params, dataResponseListener).call();

    }


    /*forgot password*/  // dev arpan add on july 06
    public void RetryPassword(String mobile_no, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_phone", mobile_no);

        new GeneralRetrofit(apiService.RetryPassword(params), params, dataResponseListener).call();

    }


    /*forgot password*/  // dev arpan add on july 06

    public void OTPVerify(String OTP, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("otp", OTP);
        params.put("user_phone", Common.getPrefValue(ChatApplication.getContext(), Constants.FORGETPASSWORD_MOBILENUMBER));

        new GeneralRetrofit(apiService.verifyOTP(params), params, dataResponseListener).call();

    }

    /*forgot password*/  // dev arpan add on july 06

    public void SetNewPassword(String password, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_password", password);
        params.put("user_phone", Common.getPrefValue(ChatApplication.getContext(), Constants.FORGETPASSWORD_MOBILENUMBER));
        params.put("otp", Common.getPrefValue(ChatApplication.getContext(), Constants.CURRENT_OPT));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("fcm_token", Common.getPrefValue(ChatApplication.getContext(), Constants.DEVICE_PUSH_TOKEN));

        new GeneralRetrofit(apiService.SetNewPassword(params), params, dataResponseListener).call();

    }

    /*UserProfileFragment*/  //  akhil add on 8th july 2020
    public void ChangePassword(String old_password, String newpassword, DataResponseListener dataResponseListener) {


        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        params.put("password", newpassword);
        params.put("old_password", old_password);

        new GeneralRetrofit(apiService.ChangePassword(ChatApplication.url, params), params, dataResponseListener).call();

    }

    /*Mood fragment - smart remote digits*/

    public void CallSmartRemote(String module_id, String Smart_remote_number, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("mood_id", module_id);
        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put("smart_remote_no", "" + Smart_remote_number);
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);


        new GeneralRetrofit(apiService.CallSmartRemote(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /*Smart remote save*/

    public void SaveSmartRemote(String device_id, String device_name, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("device_id", device_id);
        params.put("device_name", device_name);


        new GeneralRetrofit(apiService.SaveSmartRemote(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /* Delete Smart remote*/

    public void DeleteRemote(String module_id, DataResponseListener dataResponseListener) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
        params.put("device_id", module_id);

        new GeneralRetrofit(apiService.DeleteRemote(ChatApplication.url, params), params, dataResponseListener).call();
    }

    /*Update PIR device*/
    public void updatePIRDevice(DeviceVO deviceVO, String timer, DataResponseListener dataResponseListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", deviceVO.getDeviceId());
        params.put("panel_id", deviceVO.getPanel_id());
        params.put("device_status", deviceVO.getOldStatus() == 0 ? "1" : "0");
        params.put("device_sub_status", deviceVO.getDevice_sub_status());
        params.put("device_sub_type", deviceVO.getDevice_sub_type());
        params.put("pir_timer", timer);
        params.put("device_name", deviceVO.getDeviceName());


        params.put("user_id", Common.getPrefValue(ChatApplication.getContext(), Constants.USER_ID));
        params.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
        params.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);

        new GeneralRetrofit(apiService.SAVE_EDIT_SWITCH(ChatApplication.url, params), params, dataResponseListener).call();
    }
}
