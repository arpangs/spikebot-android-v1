package com.spike.cameraapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by kaushal on 16/1/18.
 */

public class Common {
    public static void saveCamera(Context context, JSONArray array){

            //To save
            SharedPreferences settings = context.getSharedPreferences("App", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("array",array.toString());
            editor.commit();

    }
    public static JSONArray getCameraList(Context context){

         //To retrieve
    SharedPreferences settings = context.getSharedPreferences("App", Context.MODE_PRIVATE);
    String arrayString = settings.getString("array", "[]"); //0 is the default value
        JSONArray array = new JSONArray();
        try {
            array = new JSONArray( arrayString);
        } catch (JSONException e) {
            Log.d("","context context " + e.getMessage());
        }
        return array;
    }

}
