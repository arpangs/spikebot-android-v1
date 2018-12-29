package com.spike.bot.core;

import android.content.Context;
import android.util.Log;

import com.spike.bot.R;
import com.spike.bot.model.RoomVO;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kaushal on 28/12/17.
 */

public class WebHelper {
    public static  final String TAG ="WebHelper";

    public static void getDeviceList(final Context context,String url,final IWebCallBack webCallBack){
        Log.d(TAG, "getDeviceList " + url);

        if(!ActivityHelper.isConnectingToInternet(context)){
            //Toast.makeText(context.getApplicationContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            webCallBack.onFailure(context.getString(R.string.disconnect));
            return;
        }

        ArrayList<RoomVO> roomList = new ArrayList<>();
        /*ChatApplication app = (ChatApplication) context.getApplication();
        String webUrl = app.url;

        String url =  webUrl + Constants.GET_DEVICES_LIST +"/0";*/

        new GetJsonTask(context,url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
                try {
                    webCallBack.onSuccess(result);
                    /*JSONObject dataObject = result.getJSONObject("data");
                    JSONArray roomArray = dataObject.getJSONArray("roomdeviceList");
                    roomList = JsonHelper.parseRoomArray(roomArray);*/
                    //setData();
                } catch (Exception e) {
                    e.printStackTrace();
                    webCallBack.onFailure(e.getMessage());
                }
                finally {
                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getDeviceList onFailure " + error );
                //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
                webCallBack.onFailure(context.getString(R.string.disconnect));
            }
        }).execute();
    }
    public static void saveRoom(final Context context,String url,JSONObject obj) {
        Log.d(TAG, "getDeviceList");
        new GetJsonTask(context,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "getDeviceList onSuccess " + result.toString());
              /*  try {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                }*/
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                Log.d(TAG, "getDeviceList onFailure " + error );
            }
        }).execute();
    }
}
